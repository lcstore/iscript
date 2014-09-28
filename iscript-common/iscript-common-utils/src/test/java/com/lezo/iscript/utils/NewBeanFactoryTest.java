package com.lezo.iscript.utils;

import org.junit.Test;

public class NewBeanFactoryTest {

	@Test
	public void testNoArgsBean() throws Exception {
		Object[] args = new Object[0];
		String clsName = ScriptNoArgBean.class.getName();
		ScriptNoArgBean argsBean = (ScriptNoArgBean) NewBeanFactory.newObj(clsName, args);
		System.out.println(argsBean.getName());
		System.out.println(argsBean.getAge());
		argsBean.setAge(18);
		System.out.println(argsBean.getAge());
	}

	@Test
	public void testArgsBean() throws Exception {
		int index = 0;
		String name = "lezo";
		Integer age = 20;
		Boolean bMan = false;
		Object[] args = new Object[3];
		args[index++] = name;
		args[index++] = age;
		args[index++] = bMan;
		String clsName = ScriptArgsBean.class.getName();
		ScriptArgsBean argsBean = (ScriptArgsBean) NewBeanFactory.newObj(clsName, args);
		System.out.println(argsBean.getName());
		System.out.println(argsBean.getAge());
		argsBean.setAge(18);
		System.out.println(argsBean.getAge());
	}
}
