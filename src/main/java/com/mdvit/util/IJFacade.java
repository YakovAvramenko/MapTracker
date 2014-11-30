package com.mdvit.util;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import com.mdvit.surf.Descriptor;
import com.mdvit.surf.Detector;
import com.mdvit.surf.IntegralImage;
import com.mdvit.surf.InterestPoint;
import com.mdvit.surf.Params;

public class IJFacade {

	public static int COPY_BLOCK_SIZE = 180;

	private IJFacade() {
	}

	/** Cached last result. */
	private static List<InterestPoint> lastResult = null;

	synchronized public static void setLastResult(List<InterestPoint> ipts) {
		lastResult = ipts;
	}

	synchronized public static List<InterestPoint> getLastResult() {
		return lastResult;
	}

	/** Finds interest points using the default parameter. */
	public static List<InterestPoint> detectAndDescribeInterestPoints(
			IntegralImage intImg) {
		return detectAndDescribeInterestPoints(intImg, new Params());
	}

	/** Finds interest points using the provided parameter. */
	public static List<InterestPoint> detectAndDescribeInterestPoints(
			IntegralImage intImg, Params p) {

		long begin, end;

		// Detect interest points with Fast-Hessian
		begin = System.currentTimeMillis();
		List<InterestPoint> ipts = Detector.fastHessian(intImg, p);
		end = System.currentTimeMillis();
		p.getStatistics().timeSURFDetector = end - begin;

		p.getStatistics().detectedIPs = ipts.size();
		float[] strengthOfIPs = new float[ipts.size()];
		for (int i = 0; i < ipts.size(); i++) {
			strengthOfIPs[i] = ipts.get(i).strength;
		}
		Arrays.sort(strengthOfIPs);
		p.getStatistics().strengthOfIPs = strengthOfIPs;

		// Describe interest points with SURF-descriptor
		begin = System.currentTimeMillis();
		if (!p.isUpright())
			for (InterestPoint ipt : ipts)
				Descriptor.computeAndSetOrientation(ipt, intImg);
		for (InterestPoint ipt : ipts)
			Descriptor.computeAndSetDescriptor(ipt, intImg, p);
		end = System.currentTimeMillis();
		p.getStatistics().timeSURFDescriptor = end - begin;

		setLastResult(ipts);
		return ipts;
	}

	/** Draws interest points onto suplied <code>ImageProcessor</code>. */
	public static void drawInterestPoints(ImageProcessor img,
			List<InterestPoint> ipts, Params params) {

		for (InterestPoint ipt : ipts)
			drawSingleInterestPoint(img, params, ipt);
	}

	public static void borderPoint(int bottomY, int upperY, int leftX,
			int rightX, ImagePlus root, ImagePlus rootBackup) {

		for (int coordY = bottomY; coordY < upperY; coordY++) {
			// need only border
			noisePoint(leftX, coordY, root, rootBackup);
		}

		for (int coordY = bottomY; coordY < upperY; coordY++) {
			// need only border
			noisePoint(rightX, coordY, root, rootBackup);
		}

		for (int coordX = leftX; coordX < rightX; coordX++) {
			noisePoint(coordX, bottomY, root, rootBackup);
		}

		for (int coordX = leftX; coordX < rightX; coordX++) {
			noisePoint(coordX, upperY, root, rootBackup);
		}
	}

	private static void noisePoint(int coordX, int coordY, ImagePlus root,
			ImagePlus fromImg) {
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				noisePointImpl(coordX + i, coordY + j, root, fromImg);
			}
		}
	}

	private static void noisePointImpl(int coordX, int coordY, ImagePlus root,
			ImagePlus fromImg) {
		root.getProcessor().putPixelValue(coordX, coordY,
				fromImg.getProcessor().getPixelValue(coordX, coordY));

	}

	public static void mergePoints(ImagePlus root, ImagePlus src,
			InterestPoint rootPoint, InterestPoint srcPoint) throws Exception {

		// convert
		int resizeCoef = round(rootPoint.scale / srcPoint.scale);
		float resizeCoefFloat = rootPoint.scale / srcPoint.scale;
		int widthCopy = COPY_BLOCK_SIZE;
		int heightCopy = COPY_BLOCK_SIZE;
		boolean ok = false;

		while (!ok) {
			try {
				float rotateAngle = rootPoint.orientation
						- srcPoint.orientation;

				BufferedImage rootImg = root.getBufferedImage();
				BufferedImage part = src.getBufferedImage().getSubimage(
						round(srcPoint.x - widthCopy / 2),
						round(srcPoint.y - heightCopy / 2), widthCopy,
						heightCopy);
				// resize
				ImagePlus help = new ImagePlus("", part);
				ImageProcessor proc = help.getProcessor().resize(
						round(resizeCoefFloat * part.getWidth()));
				
				// 1) rotate
				//proc.rotate(Math.toDegrees(rotateAngle)); part = proc.getBufferedImage();
				
				//2) try another rotate
				part = ImageUtils.tilt(proc.getBufferedImage(), rotateAngle);
				//try another rotate

				int widthToCopy = round(rootPoint.x - resizeCoefFloat
						* widthCopy / 2);
				int heightToCopy = round(rootPoint.y - resizeCoefFloat
						* heightCopy / 2);
				rootImg.getGraphics().drawImage(part, widthToCopy,
						heightToCopy, null);

				root.setImage(rootImg);

				// noise
				ImagePlus rootBackup = (ImagePlus) root.clone();
				int leftX = widthToCopy, rightX = round(rootPoint.x)
						+ resizeCoef * widthCopy / 2;
				int bottomY = heightToCopy, upperY = round(rootPoint.y)
						+ resizeCoef * heightCopy / 2;
				borderPoint(bottomY, upperY, leftX, rightX, root, rootBackup);

				ok = true;
			} catch (java.awt.image.RasterFormatException ex) {
				ok = false;

				widthCopy /= 1.5;
				heightCopy /= 1.5;
			} catch (IllegalArgumentException e) {
				System.out.println("IllegalArgumentException = " + e);
				return;
			}
		}
	}

	public static void mergePointsImpl(ImagePlus root, ImagePlus src,
			InterestPoint rootPoint, InterestPoint srcPoint, int widthCopy,
			int heightCopy, float resizeCoefFloat, float rotateAngle)
			throws Exception {

		System.out.printf("%3d %3d %12f %12f", widthCopy, heightCopy, resizeCoefFloat, rotateAngle);
		
		// convert
		int resizeCoef = round(resizeCoefFloat);
		boolean ok = false;

		while (!ok) {
			try {

				BufferedImage rootImg = root.getBufferedImage();
				BufferedImage part = src.getBufferedImage().getSubimage(
						round(srcPoint.x), round(srcPoint.y),
						round(widthCopy / resizeCoefFloat),
						round(heightCopy / resizeCoefFloat));
				// resize
				ImagePlus help = new ImagePlus("", part);
				ImageProcessor proc = help.getProcessor().resize(round(resizeCoefFloat * part.getWidth()));
				
				//rotate
				part = ImageUtils.tilt(proc.getBufferedImage(), rotateAngle);

				int widthToCopy = round(rootPoint.x);
				int heightToCopy = round(rootPoint.y);
				rootImg.getGraphics().drawImage(part, widthToCopy, heightToCopy, null);

				root.setImage(rootImg);

				ok = true;
			} catch (java.awt.image.RasterFormatException ex) {
				ok = false;

				widthCopy /= 1.1;
				heightCopy /= 1.1;
			} catch (IllegalArgumentException e) {
				System.out.println("IllegalArgumentException = " + e);
				return;
			}
		}
	}

	public static void drawSingleInterestPoint(ImageProcessor img, Params p,
			InterestPoint ipt) {
		drawSingleInterestPoint(img, p, ipt, null);
	}

	public static void drawSingleInterestPoint(ImageProcessor img, Params p,
			InterestPoint ipt, Color color) {
		int x = round(ipt.x);
		int y = round(ipt.y);
		float w = ipt.scale * 10; // for descriptor window
		float ori = ipt.orientation;
		float co = (float) cos(ori);
		float si = (float) sin(ori);
		float s = ipt.strength * 10000;
		// Draw descriptor window around the interest point
		if (p.isDisplayDescriptorWindows()) {
			img.setLineWidth(p.getLineWidth());
			img.setColor(p.getDescriptorWindowColor());

			float x0 = w * (si + co) + ipt.x;
			float y0 = w * (-co + si) + ipt.y;
			float x1 = w * (si - co) + ipt.x;
			float y1 = w * (-co - si) + ipt.y;
			float x2 = w * (-si - co) + ipt.x;
			float y2 = w * (co - si) + ipt.y;
			float x3 = w * (-si + co) + ipt.x;
			float y3 = w * (co + si) + ipt.y;

			img.moveTo(x, y);
			img.lineTo(round(x0), round(y0));
			img.lineTo(round(x1), round(y1));
			img.lineTo(round(x2), round(y2));
			img.lineTo(round(x3), round(y3));
			img.lineTo(x, y);
		}

		// Draw orientation vector
		if (p.isDisplayOrientationVectors()) {
			img.setLineWidth(p.getLineWidth());
			img.setColor(p.getOrientationVectorColor());
			img.drawLine(x, y, round(20 * co + x), round(20 * si + y));
		}

		// Draw interest point
		img.setLineWidth(p.getLineWidth() * 2);

		if (color == null) {
			if (ipt.sign) {
				img.setColor(Color.RED);
			} else {
				img.setColor(Color.BLUE);
			}
		} else {
			img.setColor(color);
		}
		
		// img.drawDot(x, y);
		img.drawRect(x, y, 2, 2);

	}

	
}
