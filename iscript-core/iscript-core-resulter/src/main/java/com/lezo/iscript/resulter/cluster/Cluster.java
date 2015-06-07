package com.lezo.iscript.resulter.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Cluster {
	private int id;// 标识
	private Node center;// 中心
	private List<Node> members = new ArrayList<Node>();// 成员

	/**
	 * 选举新的中心
	 * 
	 * @return
	 */
	public List<Cluster> electCluster() {
		// 匹配信息聚类
		// 确定属性聚类
		// 标准商品聚类
		// 信息完整度聚类
		
		return null;
	}
}
