package com.mdvit.exports;

import java.util.Map;

import com.mdvit.surf.InterestPoint;

import ij.ImagePlus;

public interface IMapRefresh {
	Statictics refresh(ImagePlus img);

	Statictics refresh(Map<InterestPoint, InterestPoint> matchedPoints,
			ImagePlus root, ImagePlus from);
	
	ImagePlus getRoot();
}
