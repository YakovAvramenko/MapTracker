package com.mdvit.exports;

import ij.ImagePlus;

import java.util.List;
import java.util.Map;

import com.mdvit.Configuration;
import com.mdvit.surf.IntegralImage;
import com.mdvit.surf.InterestPoint;
import com.mdvit.surf.Matcher;
import com.mdvit.util.IJFacade;

public abstract class AbstractMapRefresh implements IMapRefresh {
	// root data for algoritm
	private IntegralImage root;
	private ImagePlus rootPlus;
	private List<InterestPoint> points;
	
	public AbstractMapRefresh(ImagePlus img) {
		rootPlus = img;
		root = new IntegralImage(rootPlus.getProcessor(), true);
		points = IJFacade.detectAndDescribeInterestPoints(root,
				Configuration.newParams());
	}

	public Statictics refresh(ImagePlus src) {
		IntegralImage intSrc = new IntegralImage(src.getProcessor(), true);
		List<InterestPoint> ipts = IJFacade.detectAndDescribeInterestPoints(intSrc, Configuration.newParams());

		Map<InterestPoint, InterestPoint> matchedPoints = Matcher.findMathes(points, ipts);
	    System.out.println("got matchedPoints = " + matchedPoints.size());
	    
		return refresh(matchedPoints, rootPlus, src);
	}

	public abstract Statictics refresh(Map<InterestPoint, InterestPoint> matchedPoints,
			ImagePlus root, ImagePlus from);
	
	
	public ImagePlus getRoot() {
		return rootPlus;
	};
}
