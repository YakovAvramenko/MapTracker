package com.mdvit.surf;

import static java.lang.Math.round;
import ij.ImagePlus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Matcher {

	public static class Point2D {
		public int x, y;

		public Point2D() {}

		public Point2D(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static class Point2Df {
		public float x, y;

		public Point2Df() {}

		public Point2Df(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	public static Map<InterestPoint, InterestPoint> findMathes(List<InterestPoint> ipts1, List<InterestPoint> ipts2, boolean doReverseComparisonToo) {

		Map<InterestPoint, InterestPoint> matchedPoints = Matcher.findMathes(ipts1, ipts2);

		if (doReverseComparisonToo) {
			Map<InterestPoint, InterestPoint> matchedPointsReverse = Matcher.findMathes(ipts2, ipts1);

			// take only those points that matched in the reverse comparison too
			Map<InterestPoint, InterestPoint> matchedPointsBoth = new HashMap<InterestPoint, InterestPoint>();
			for (InterestPoint ipt1 : matchedPoints.keySet()) {
				InterestPoint ipt2 = matchedPoints.get(ipt1);
				if (ipt1 == matchedPointsReverse.get(ipt2))
					matchedPointsBoth.put(ipt1, ipt2);
			}
			matchedPoints = matchedPointsBoth;
		}
		return matchedPoints;
	}

	/**
	 * Finds matching points using the sign of laplacian and a linear nearest
	 * neighbor search.
	 */
	public static Map<InterestPoint, InterestPoint> findMathes(List<InterestPoint> ipts1, List<InterestPoint> ipts2) {
		Map<InterestPoint, InterestPoint> res = new HashMap<InterestPoint, InterestPoint>();
		float distance, bestDistance, secondBest;
		InterestPoint bestMatch;
		int descSize = 64;
		float delta;
		float[] v1, v2;

		for (InterestPoint p1 : ipts1) {
			bestDistance = secondBest = Float.MAX_VALUE;
			bestMatch = null;

			ipts2Loop: for (InterestPoint p2 : ipts2) {

				if (p1.sign != p2.sign)
					continue;

				// Compare descriptors (based on calculating of squared distance between two vectors)
				distance = 0;
				v1 = p1.descriptor;
				v2 = p2.descriptor;
				for (int i = 0; i < descSize; i++) {
					delta = v1[i] - v2[i];
					distance += delta * delta;
					if (distance >= secondBest)
						continue ipts2Loop;
				}
				if (distance < bestDistance) {
					secondBest = bestDistance;
					bestDistance = distance;
					bestMatch = p2;
				} else { // distance < secondBest
					secondBest = distance;
				}

			}
			
			if (bestDistance < 0.5f * secondBest) {
				
				// Matching point found.
				res.put(p1, bestMatch);
				// Store the change in position (p1 -> p2) into the
				// matchingPoint:
				bestMatch.dx = bestMatch.x - p1.x;
				bestMatch.dy = bestMatch.y - p1.y;
			}

		}

		return res;
	}


	/** @param h 3x3 homography matrix. */
	public static Point2Df getTargetPointByHomography(Point2Df p1, float[][] h) {
		float p1_z = 1.0f;
		float Z = (h[2][0] * p1.x + h[2][1] * p1.y + h[2][2] * p1_z);
		float X = (h[0][0] * p1.x + h[0][1] * p1.y + h[0][2] * p1_z) / Z;
		float Y = (h[1][0] * p1.x + h[1][1] * p1.y + h[1][2] * p1_z) / Z;
		return new Point2Df(X, Y);
	}

	public static int countMatchesUsingHomography(Map<InterestPoint, InterestPoint> matches, ImagePlus imp1, int margin, float[][] h, ImagePlus imp2, float tolerance) {
		int count = 0;
		float x1, y1, x2, y2, x2H, y2H, z2H, dx, dy;
		for (Entry<InterestPoint, InterestPoint> pair : matches.entrySet()) {
			// Point 1:
			x1 = pair.getKey().x;
			y1 = pair.getKey().y;

			// Point 2:
			x2 = pair.getValue().x;
			y2 = pair.getValue().y;

			z2H = (h[2][0] * x1 + h[2][1] * y1 + h[2][2]);
			x2H = (h[0][0] * x1 + h[0][1] * y1 + h[0][2]) / z2H;
			y2H = (h[1][0] * x1 + h[1][1] * y1 + h[1][2]) / z2H;

			dx = Math.abs(x2H-x2);
			dy = Math.abs(y2H-y2);
			
			if (dx <= tolerance && dy <= tolerance)
				count++;
		}
		return count;
	}

	static int translateCorners(Map<InterestPoint, InterestPoint> matches, Point2D[] src_corners, Point2D[] dst_corners) {
		int n = matches.size();
		if (n < 4)
			return 0;

		Point2Df[] pt1 = new Point2Df[n];
		Point2Df[] pt2 = new Point2Df[n];
		int i = 0;
		for (Entry<InterestPoint, InterestPoint> pair : matches.entrySet()) {
			pt1[i] = new Point2Df(pair.getKey().x, pair.getKey().y);
			pt2[i] = new Point2Df(pair.getValue().x, pair.getValue().y);
			i++;
		}

		final int CV_RANSAC = 8;
		double[] h = cvFindHomography(pt1, pt2, CV_RANSAC, 5);
		if (h == null)
			return 0;

		// Translate src_corners to dst_corners using homography
		for (i = 0; i < 4; i++) {
			double x = src_corners[i].x, y = src_corners[i].y;
			double Z = 1. / (h[6] * x + h[7] * y + h[8]);
			double X = (h[0] * x + h[1] * y + h[2]) * Z;
			double Y = (h[3] * x + h[4] * y + h[5]) * Z;
			dst_corners[i] = new Point2D((int) round(X), (int) round(Y));
		}
		return 1;
	}

	/** It's a stub. There is no implementation yet. */
	static double[] cvFindHomography(Point2Df[] objectPoints, Point2Df[] imagePoints, int method, double ransacReprojThreshold) {
		double[] homography = null;
		return homography;
	}


}
