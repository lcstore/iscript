package com.lezo.iscript.resulter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.io.IoDispatcher;

public class StartedListener implements ServletContextListener {
	private static Logger logger = LoggerFactory.getLogger(StartedListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("start to init IoDispatcher..");
		IoDispatcher.getInstance().start();
		logger.info("IoDispatcher is running..");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		IoDispatcher.getInstance().stop();
		logger.info("stop IoDispatcher..");
	}
}