package com.lezo.iscript.resulter.cluster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class KmeansCluster {
	private int kCenter;

	public List<Cluster> cluster(List<Node> srcList) {
		List<Cluster> clusters = initClusters(srcList);
		if (kCenter >= srcList.size()) {
			return clusters;
		}
		doClustering(clusters, srcList);
		return clusters;
	}

	private void doClustering(List<Cluster> clusters, List<Node> srcList) {
		for (Node node : srcList) {
			Cluster destCluster = null;
			Double minDist = null;
			for (Cluster cluster : clusters) {
				Node center = cluster.getCenter();
				Double distance = center.getDistance(node);
				if (minDist == null || minDist > distance) {
					minDist = distance;
					destCluster = cluster;
				}
			}
			if (destCluster != null) {
				destCluster.getMembers().add(node);
			}
		}

	}

	private List<Cluster> initClusters(List<Node> srcList) {
		List<Cluster> clusters = new ArrayList<Cluster>(kCenter);
		Set<Integer> indexSet = new HashSet<Integer>();
		int kCount = kCenter > srcList.size() ? srcList.size() : kCenter;
		while (clusters.size() < kCount) {
			Random random = new Random();
			int index = random.nextInt(srcList.size());
			if (!indexSet.contains(index)) {
				indexSet.add(index);
				Cluster newCluster = new Cluster();
				newCluster.setId(index);
				newCluster.setCenter(srcList.get(index));
				clusters.add(newCluster);
			}
		}
		return clusters;
	}
}
