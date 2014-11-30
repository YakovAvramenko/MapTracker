package com.mdvit.exports;

import ij.ImagePlus;

import java.util.Map;

import com.mdvit.surf.InterestPoint;
import com.mdvit.util.IJFacade;

public class SimpleMapRefresh extends AbstractMapRefresh {

	public SimpleMapRefresh(ImagePlus img) {
		super(img);
	}
	
	@Override
	public Statictics refresh(Map<InterestPoint, InterestPoint> matchedPoints,
			ImagePlus root, ImagePlus from) {
		Statictics stat = new Statictics();
		stat.getData().put("method", "SimpleMapRefresh");
		if (matchedPoints.size() == 0) {
			stat.getData().put("result", "Got 0 matched points, sooo sad");
			return stat;
		}

		System.out.println("got " + matchedPoints.size());
		int handledCount = 0;

		for (InterestPoint key : matchedPoints.keySet()) {
			InterestPoint srcPoint = matchedPoints.get(key);
			try {
				IJFacade.mergePoints(root, from, key, srcPoint);
			} catch (Exception e) {
				e.printStackTrace();
			}

			handledCount++;
			// if (handledCount > 25) {break;}
			System.out.println("remains = "
					+ (matchedPoints.size() - handledCount));
		}
		
		stat.getData().put("result", "Got " + matchedPoints.size() + " matched points");
		return stat;
	}

}
