package com.lezo.iscript.envjs;

public class SimpleObject {
	private String name = "lezo";
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNameAndAge(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public int getAge() {
		return age;
	}
}
