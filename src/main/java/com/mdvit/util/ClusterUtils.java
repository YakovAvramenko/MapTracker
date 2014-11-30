package com.mdvit.util;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import com.mdvit.surf.InterestPoint;

public class ClusterUtils {

	public static List<Cluster<InterestPoint>> cluster(List<InterestPoint> clusterInput, double eps) {
		Clusterer dbscan = new DBSCANClusterer(eps, 0);
		List<Cluster<InterestPoint>> cluster = dbscan.cluster(clusterInput);
		return cluster;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		Clusterer dbscan = new DBSCANClusterer(13.105, 0);
		List<Cluster<DoublePoint>> cluster = dbscan.cluster(getGPS());

		for (Cluster<DoublePoint> c : cluster) {
			System.out.println(c.getPoints());
			System.out.println(c.getPoints().size());
			System.out.println("---");
		}
		System.out.println("total size " + cluster.size());
	}

	private static List<DoublePoint> getGPS() throws FileNotFoundException,
			IOException {
		Random rand = new Random();
		List<DoublePoint> points = new ArrayList<DoublePoint>();
		for (int i = 0; i < 3000; i++) {
			double[] d = new double[2];
			d[0] = rand.nextDouble() * 1000;
			d[1] = rand.nextDouble() * 1000;
			points.add(new DoublePoint(d));
		}
		return points;
	}

	public static void main1(String[] args) throws Exception {
		List<InterestPoint> clusterInput = new ArrayList<InterestPoint>();
		Random rand = new Random();
		for (int i = 0; i < 300; i++) {
			clusterInput.add(new InterestPoint(rand.nextInt(), rand.nextInt(),
					0, 0, 0));
		}

		DBSCANClusterer<InterestPoint> clusterer = new DBSCANClusterer<InterestPoint>(
				99991211, 12);

		List<Cluster<InterestPoint>> clusterResults = clusterer
				.cluster(clusterInput);

		// output the clusters
		System.out.println("size " + clusterResults.size());
		for (int i = 0; i < clusterResults.size(); i++) {
			System.out.println("Cluster " + i);
			Color color = new Color(new Random().nextInt() % 150);

			for (InterestPoint point : clusterResults.get(i).getPoints()) {
				System.out.println(point.toLiteString());
			}
		}
	}
}
