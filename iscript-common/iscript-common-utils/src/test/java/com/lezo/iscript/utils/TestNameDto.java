package com.lezo.iscript.utils;

import com.lezo.iscript.common.UnifyValueAnnotation;

public class TestNameDto {
	@UnifyValueAnnotation(value = "lezo")
	private String name;
	private Integer age;
	@UnifyValueAnnotation(value = "1.2")
	private Float height;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "TestNameDto [name=" + name + ", age=" + age + ", height=" + height + "]";
	}

}