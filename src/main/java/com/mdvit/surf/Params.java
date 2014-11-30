package com.mdvit.surf;

import java.awt.Color;

import ij.gui.GenericDialog;

/** Parameter for SURF Detector, SURF Descriptor and for displaying the results.  */
public class Params {

	public Params() {
	}


	public Params(int octaves, int layers, float threshold, int initStep, boolean upright, boolean displayOrientationVectors, boolean displayDescriptorWindows, int lineWidth, boolean displayStatistics) {
		this.octaves = validate(octaves, 3, 4);
		this.layers = validate(layers, 3, 4);
		this.threshold = validate(threshold, 0, 1);
		this.initStep = validate(initStep, 1, 6);
		this.upright = upright;
		this.displayOrientationVectors = displayOrientationVectors;
		this.displayDescriptorWindows = displayDescriptorWindows;
		this.lineWidth = validate(lineWidth, 1, 5);
		this.displayStatistics = displayStatistics;
	}

	public Params(Params p) {
		this.octaves = p.octaves;
		this.layers = p.layers;
		this.threshold = p.threshold;
		this.initStep = p.initStep;
		this.upright = p.upright;
		this.displayOrientationVectors = p.displayOrientationVectors;
		this.displayDescriptorWindows = p.displayDescriptorWindows;
		this.lineWidth = p.lineWidth;
		this.displayStatistics = p.displayStatistics;
	}


	private float validate(float val, float lowerBound, float upperBound) {
		if (val < lowerBound) return lowerBound;
		if (val > upperBound) return upperBound;
		return val;
	}

	/** Validates parameter values for the constructor. 
	 * @param lowerBound The lowest valid value.
	 * @param upperBound The highest valid value. 
	 * @param val The value to validate. */
	int validate(int val, int lowerBound, int upperBound) {
		if (val < lowerBound) return lowerBound;
		if (val > upperBound) return upperBound;
		return val;
	}

	private int[][] filterSizes = { { 9, 15, 21, 27 }, { 15, 27, 39, 51 }, { 27, 51, 75, 99 }, { 51, 99, 147, 195 } };
	private int[] maxFilterSizes = {27, 51, 99, 195};

	/** Number of analysed octaves. Default is 4. */
	private int octaves = 4;
	private int layers = 4;
	public int getOctaves() {return octaves;}
	public int getLayers() {return layers;}

	public int getFilterSize(int octave, int layer) {
		return filterSizes[octave][layer];
	}

	/** Returns the biggest filter size in the octave. */
	public int getMaxFilterSize(int octave) {
		return maxFilterSizes[octave];
	}

	// Set this flag "true" to double the image size
	boolean doubleImageSize = false; 

	/**
	 * The responses are thresholded such that all values below the
	 * <code>threshold</code> are removed. Increasing the threshold lowers the
	 * number of detected interest points, and vice versa. Must be >= 0 and <= 1.
	 */
	private float threshold = 0.001f; 
	public float getThreshold() {return threshold;}

	/** The initial sampling step (1..6). Default is 2. <br>
	 * Will be doubled for each next octave (see stepIncFactor). */
	private int initStep = 2;
	public int getInitStep() {return initStep;}

	private int stepIncFactor = 2;
	public int getStepIncFactor() {return stepIncFactor;}

	// Descriptor params

	/** Extract upright (i.e. not rotation invariant) descriptors. Default is <code>false</code>. */
	private boolean upright;
	public boolean isUpright() {return upright;}

	private int descSize = 64;
	public int getDescSize() {return descSize;}

	// Display params

	private boolean displayOrientationVectors = true;
	public boolean isDisplayOrientationVectors() {return displayOrientationVectors;}

	private boolean displayDescriptorWindows = false;
	public boolean isDisplayDescriptorWindows() {return displayDescriptorWindows;}

	private int lineWidth = 1; // 1..5
	public int getLineWidth() {return lineWidth;}

	boolean displayStatistics = false;
	public boolean isDisplayStatistics() {return displayStatistics;}

	private Statistics stat = new Statistics();
	public Statistics getStatistics() {return stat;}




	/**  Reads SURF parameter from the ImageJ <code>GenericDialog</code>
	 * and returns a <code>SurfParams</code> object. 
	 * Depends on the order and types of fields in the method {@link Params#addSurfParamsToDialog(GenericDialog)}. */
	public static Params getSurfParamsFromDialog(GenericDialog gd) {
		int octaves = (int) gd.getNextNumber();
		int layers = (int) gd.getNextNumber();
		float threshold = (float) gd.getNextNumber();
		int initStep = (int) gd.getNextNumber();
		boolean upright = gd.getNextBoolean();
		boolean displayOrientationVectors = gd.getNextBoolean();
		boolean displayDescriptorWindows = gd.getNextBoolean();
		int lineWidth = (int) gd.getNextNumber();
		boolean displayStatistics = gd.getNextBoolean();
		return new Params(octaves, layers, threshold, initStep, upright, displayOrientationVectors, displayDescriptorWindows, lineWidth, displayStatistics);
	}


	public Color getDescriptorWindowColor() { return Color.PINK; }
	public Color getOrientationVectorColor() { return Color.CYAN; }

	/** Drawing color for dark blobs on light background */   
	public Color getDarkPointColor() { return Color.BLUE; }

	/** Drawing color for light blobs on dark background */   
	public Color getLightPointColor() { return Color.RED; }


	public void setFilterSizes(int[][] filterSizes) {
		this.filterSizes = filterSizes;
	}


	public int[] getMaxFilterSizes() {
		return maxFilterSizes;
	}


	public void setMaxFilterSizes(int[] maxFilterSizes) {
		this.maxFilterSizes = maxFilterSizes;
	}


	public Statistics getStat() {
		return stat;
	}


	public void setStat(Statistics stat) {
		this.stat = stat;
	}


	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}


	public void setLayers(int layers) {
		this.layers = layers;
	}


	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}


	public void setInitStep(int initStep) {
		this.initStep = initStep;
	}


	public void setStepIncFactor(int stepIncFactor) {
		this.stepIncFactor = stepIncFactor;
	}


	public void setUpright(boolean upright) {
		this.upright = upright;
	}


	public void setDescSize(int descSize) {
		this.descSize = descSize;
	}


	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}
}

