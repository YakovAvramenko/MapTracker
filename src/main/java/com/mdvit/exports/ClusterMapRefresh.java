package com.mdvit.exports;

import ij.ImagePlus;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.math3.ml.clustering.Cluster;

import com.mdvit.Configuration;
import com.mdvit.surf.InterestPoint;
import com.mdvit.util.ClusterUtils;
import com.mdvit.util.IJFacade;

public class ClusterMapRefresh extends AbstractMapRefresh {
	
	public double eps;// = 90;
	public boolean debug = false;

	public ClusterMapRefresh(ImagePlus img) {
		super(img);
		this.eps = 30;
	}
	
	public ClusterMapRefresh(ImagePlus img, double eps) {
		super(img);
		this.eps = eps;
	}

	@Override
	public Statictics refresh(Map<InterestPoint, InterestPoint> matchedPoints,
			ImagePlus root, ImagePlus from) {
		Statictics stat = new Statictics();
		stat.getData().put("method", "ClusterMapRefresh");
		if (matchedPoints.size() == 0) {
			stat.getData().put("result", "Got 0 matched points, sooo sad");
			return stat;
		}

		try {
			List<InterestPoint> clusterInput = new ArrayList<InterestPoint>(matchedPoints.size());
			for (Entry<InterestPoint, InterestPoint> pair : matchedPoints.entrySet()) {
				clusterInput.add(pair.getKey());
			}

			// clustered root keys
			List<Cluster<InterestPoint>> clusters = ClusterUtils
					.cluster(clusterInput, eps);

			System.out.println("clusters.size() = " + clusters.size());
			stat.getData().put("clusters info", "Got " + clusters.size() + " clusters");
			
			int i = 0;
			for (Cluster<InterestPoint> cluster : clusters) {
				stat.getData().put("cluster " + ++i, "Size = " + cluster.getPoints().size());
				mergeCluster(cluster, matchedPoints, root, from);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			stat.setException(e);
		}
		
		stat.getData().put("result", "Got " + matchedPoints.size() + " matched points");
		return stat;
	}

	private void mergeCluster(Cluster<InterestPoint> cluster,
			Map<InterestPoint, InterestPoint> matchedPoints, ImagePlus root,
			ImagePlus from) throws Exception {

		if (cluster.getPoints().size() == 1) {
			InterestPoint rootPoint = cluster.getPoints().get(0);
			IJFacade.mergePoints(root, from, rootPoint,
					matchedPoints.get(rootPoint));

			return;
		}
		
		//merge block
		if (debug ) {
			print(cluster, root);
		}
		List<InterestPoint> rootPoints = cluster.getPoints();
		System.out.println("next cluster size() = " + rootPoints.size());

		InterestPoint left = cluster.getPoints().get(0);
		InterestPoint right = cluster.getPoints().get(0);
		
		InterestPoint upper = cluster.getPoints().get(0);
		InterestPoint bottom = cluster.getPoints().get(0);
		
		float upperY = 0, leftX = 0;

		for (InterestPoint point : rootPoints) {
			if (point.x >= right.x) {
				right = point;
			}
			if (point.x <= left.x) {
				left = point;
				leftX = left.x;
			}
			if (point.y >= upper.y) {
				upper = point;
				upperY = point.y;
				System.out.println("upper = " + upper.y);
			}
			if (point.y <= bottom.y) {
				bottom = point;
				System.out.println("bottom = " + bottom.y);
			}

		}
		
		///upper.y = bottom.y;
		
		InterestPoint rootUpperLeft = new InterestPoint();
		rootUpperLeft.y = bottom.y;
		rootUpperLeft.x = left.x;
		
		InterestPoint srcUpperLeft = new InterestPoint();
		srcUpperLeft.y = matchedPoints.get(bottom).y;
		srcUpperLeft.x = matchedPoints.get(left).x;
		
		float resizeCoefFloat = bottom.scale / matchedPoints.get(bottom).scale;
		float rotateAngle = bottom.orientation - matchedPoints.get(bottom).orientation;
		
		int widthCopy = Math.round(Math.abs(right.x - left.x));
		int heightCopy = Math.round(Math.abs(upper.y - bottom.y));
		
		//dirty hack
		widthCopy = widthCopy == 0 ? 1 : widthCopy;
		heightCopy = heightCopy == 0 ? 1 : heightCopy;
		
		if (debug ) {
			print(rootUpperLeft, root, widthCopy, heightCopy);
			
//			root.getProcessor().setLineWidth(3);
//			
//			root.getProcessor().setColor(Color.RED);
//			root.getProcessor().drawRect(Math.round(bottom.x), Math.round(bottom.y), 3, 3);
//			
//			root.getProcessor().setColor(Color.YELLOW);
//			root.getProcessor().drawRect(Math.round(upper.x), Math.round(upper.y), 3, 3);
//			
//			root.getProcessor().setColor(Color.ORANGE);
//			root.getProcessor().drawRect(300, 400, 4, 4);
//			
//			root.getProcessor().setColor(Color.WHITE);
//			root.getProcessor().drawRect(300, 500, 4, 4);
//			
//			root.getProcessor().setColor(Color.GREEN);
//			root.getProcessor().drawRect(300, 600, 4, 4);
//			
//			System.out.println("bottom y= " + bottom.y);
//			System.out.println("upper y= " + upper.y);
			
			return;
		}
		
		IJFacade.mergePointsImpl(root, from, rootUpperLeft, srcUpperLeft, widthCopy,
				heightCopy, resizeCoefFloat, rotateAngle);
	}
	
	private void print(InterestPoint rootUpperLeft, ImagePlus root, int widthCopy, int heightCopy) {
		IJFacade.drawSingleInterestPoint(root.getProcessor(), Configuration.newParams(), rootUpperLeft, Color.CYAN);

		root.getProcessor().drawRect(Math.round(rootUpperLeft.x), Math.round(rootUpperLeft.y), widthCopy, heightCopy);
		
		root.show();
	}

	void print(Cluster<InterestPoint> cluster, ImagePlus root) {
		// output the clusters
		Color color = new Color(new Random().nextInt() % 150);

		for (InterestPoint point : cluster.getPoints()) {
			IJFacade.drawSingleInterestPoint(root.getProcessor(), Configuration.newParams(), point, color);
		}

		//root.show();
	}

}
