package com.lezo.iscript.utils;

import java.lang.reflect.Constructor;

public class NewBeanFactory {
	/**
	 * if has @ThreadSafe,object cannot been create
	 * 
	 * @param clsName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static Object newObj(String clsName, Object[] args) throws Exception {
		if (args == null) {
			args = new Object[0];
		}
		Class<?> cls = Class.forName(clsName);// 想要反射clsName
		Class<?>[] argsCls = new Class[args.length];// 导入这个类所需要的参数，取所有参数的class
		int index = 0;
		for (Object arg : args) {
			argsCls[index++] = arg.getClass();
		}
		Constructor<?> con = cls.getConstructor(argsCls);// 指定这个对象的某一个带参数的构造器
		Object obj = con.newInstance(args);// 调用带参数的构造器进行实例化
		return obj;
	}
}
