package com.lezo.iscript.spring.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class SpringBeanFactory implements BeanFactoryAware {

	private static BeanFactory beanFactory = null;
	private static SpringBeanFactory serviceLocator = null;

	@SuppressWarnings("static-access")
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public static SpringBeanFactory getInstance() {
		if (serviceLocator == null) {
			serviceLocator = (SpringBeanFactory) beanFactory
					.getBean("serviceLocator");
		}
		return serviceLocator;
	}

	/*
	 * 根据提供的bean名称获得相应的类 beanName - bean名称
	 */
	public static Object getBean(String beanName) {
		return beanFactory.getBean(beanName);
	}

	/*
	 * 根据提供的bean名称得到对应指定类型的服务类 beanName - bean名称 cla - 返回的bean类型，若类型不匹配，跑出异常
	 */
	@SuppressWarnings("unchecked")
	public static Object getBean(String beanName, Class cla) {
		return beanFactory.getBean(beanName, cla);
	}

}