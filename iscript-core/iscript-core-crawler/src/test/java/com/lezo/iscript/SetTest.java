package com.lezo.iscript;

import java.util.TreeSet;

import org.junit.Test;

public class SetTest {

	@Test
	public void testTreeSet() {
		TreeSet<Integer> treeSet = new TreeSet<Integer>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				treeSet.add(j);
			}
		}
		treeSet.add(1);
		System.err.println("size:" + treeSet.size());
		for (Integer num : treeSet) {
			System.err.println(num);
		}
	}
	@Test
	public void testCookie() {
		TreeSet<Integer> treeSet = new TreeSet<Integer>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				treeSet.add(j);
			}
		}
		treeSet.add(1);
		System.err.println("size:" + treeSet.size());
		for (Integer num : treeSet) {
			System.err.println(num);
		}
	}
}
