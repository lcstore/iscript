package com.lezo.iscript.utils;

public class ScriptArgsBean {

	private String name;
	private Integer age;
	private Boolean bMan;

	public ScriptArgsBean(String name, Integer age, Boolean bMan) {
		super();
		this.name = name;
		this.age = age;
		this.bMan = bMan;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Boolean getbMan() {
		return bMan;
	}

	public void setbMan(Boolean bMan) {
		this.bMan = bMan;
	}

}
