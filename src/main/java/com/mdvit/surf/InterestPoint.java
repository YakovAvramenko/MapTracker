package com.mdvit.surf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.commons.math3.ml.clustering.Clusterable;

// TODO move IJ-related code into IJFacade

/** Interest Point class. */
public class InterestPoint implements Clusterable, Cloneable {

	/** Interpolated X-coordinate. */
	public float x;
	
	/** Interpolated Y-coordinate. */
	public float y;

	/** Value of the hessian determinant (blob response) means the strength of the interest point. */
	public float strength;
	
	/** Trace of the hessian determinant. */
	float trace;
	
	/** Sign of hessian traces (laplacian sign).<br>
	 * <code>true</code> means >= 0, <code>false</code> means < 0.
	 * (Signs are saved separately for better matching performance.) */
	public boolean sign;
	
	/** Detected scale. */
	public float scale;
	
	/** Orientation measured anti-clockwise from +ve x-axis. The default is 0 (i.e. upright SURF). */
	public float orientation;

	/** Vector of descriptor components. */
	float[] descriptor;

	/** Point motion (can be used for frame to frame motion analysis). */
	float dx;
	
	/** Point motion (can be used for frame to frame motion analysis). */
	float dy;

	public InterestPoint() {}
	
	public InterestPoint(float x, float y, float strength, float trace, float scale) {
		this.x = x;
		this.y = y;
		this.strength = strength;
		this.trace = trace;
		this.scale = scale;
		this.sign = (trace >= 0);
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb, Locale.US); // (all output will be sent to sb)

		f.format("%12f %12f %12f %12f %12f %12f ", x, y, strength, trace, scale, orientation);
		int descSize = (descriptor == null) ? 0 : descriptor.length;
		f.format("%12d ", descSize);
		
		if (descSize > 0) {
			for (int i = 0; i < descSize; i++) {
				if (i%8 == 0) f.format("\n");
				f.format("%12f ", descriptor[i]);
			}
			f.format("\n");
		}
		return f.toString();
	}
	
	public String toLiteString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb, Locale.US);
		f.format("%3f %3f", x, y);
		f.format("\n");
		return f.toString();
	}

	public static void saveToFile(List<InterestPoint> ipts, String fileName, boolean inclDescriptor) {
		try {
			PrintWriter out = new PrintWriter(fileName);
			if (ipts != null && ipts.size() > 0) {
				
				// Header
				out.println(ipts.size()+"\n"); // Number of Interest Points
				
				// Points
				for (InterestPoint ipt : ipts) {
					if (inclDescriptor) {
						out.println(ipt);
					} else {
						// temporarily unbound the descriptor
						float[] temp = ipt.descriptor;
						ipt.descriptor = null;
						out.println(ipt);
						ipt.descriptor = temp;
					}
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		}
		
	}

	public static List<InterestPoint> loadFromFile(String fileName) {
		try {
			Scanner in = new Scanner(new File(fileName));
			
			// Header
			int iptsSize = in.nextInt(); // Number of Interest Points
			
			// Points
			List<InterestPoint> ipts = new ArrayList<InterestPoint>(iptsSize);
			float x, y, strength, trace, scale, ori;
			int descSize;
			InterestPoint ipt;
			
			for (int i = 0; i < iptsSize; i++) {
				
				x     = in.nextFloat();
				y     = in.nextFloat();
				strength = in.nextFloat();
				trace = in.nextFloat();
				scale = in.nextFloat();
				ori   = in.nextFloat();
				descSize = in.nextInt();
				
				ipt = new InterestPoint(x, y, strength, trace, scale);
				ipt.orientation = ori;
				if (descSize > 0) {
					ipt.descriptor = new float[descSize];
					for (int j = 0; j < descSize; j++)
						ipt.descriptor[j] = in.nextFloat();
				}
				ipts.add(ipt);
			}
			
			in.close();
			return ipts;
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		}
	}

	public double[] getPoint() {
		return new double [] {x, y};
	}

	
	public InterestPoint clone() {
		return new InterestPoint(x, y, strength, trace, scale);
	}
	
	
}
