package com.lezo.iscript.proxy.driver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.PropertiesUtils;

public class LaunchListener implements ServletContextListener {
	private static Logger logger = LoggerFactory.getLogger(LaunchListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		int port = Integer.valueOf(PropertiesUtils.getOrDefault("port", "8080"));
		logger.info("About to start server on port: " + port);
		HttpProxyServerBootstrap bootstrap = DefaultHttpProxyServer.bootstrap().withPort(port)
				.withAllowLocalOnly(false);
		bootstrap.withFiltersSource(new CustomHttpFiltersSource());
		bootstrap.start();
		logger.info("start on port:" + port);
	}

}
