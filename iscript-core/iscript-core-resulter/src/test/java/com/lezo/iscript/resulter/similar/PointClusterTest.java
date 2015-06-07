package com.lezo.iscript.resulter.similar;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.junit.Test;

public class PointClusterTest {

	@Test
	public void testCluster() {
		KMeansPlusPlusClusterer<Clusterable> kmeans = new KMeansPlusPlusClusterer<Clusterable>(2);
		List<Clusterable> srcList = new ArrayList<Clusterable>();
		for (int i = 0; i <= 3; i++) {
			srcList.add(new PointCluaster(i, 1));
			srcList.add(new PointCluaster(i, 2));
			srcList.add(new PointCluaster(i, 3));
		}
		List<CentroidCluster<Clusterable>> clusters = kmeans.cluster(srcList);
		for (CentroidCluster<Clusterable> cl : clusters) {
			System.err.println("center:" + cl.getCenter());
			int hasCode = cl.getCenter().hashCode();
			for (Clusterable pt : cl.getPoints()) {
				System.err.println(hasCode + ":" + pt);
			}
		}
	}

	class PointCluaster implements Clusterable {
		private double x;
		private double y;
		private double[] points;

		public PointCluaster(double x, double y) {
			super();
			this.x = x;
			this.y = y;
			this.points = new double[] { this.x, this.y };
		}

		@Override
		public double[] getPoint() {
			return this.points;
		}

		@Override
		public String toString() {
			return "PointCluaster [x=" + x + ", y=" + y + "]";
		}

	}
}
