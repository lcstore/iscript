package com.lezo.iscript.updater;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchListener implements ServletContextListener {
	private static Logger logger = LoggerFactory.getLogger(LaunchListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("start to init updater..");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String[] args = new String[1];
					LaunchUpdater.main(args);
				} catch (Exception e) {
					RuntimeException ex = new RuntimeException("try to launch updater");
					ex.initCause(e);
					throw ex;
				}
			}
		}, "updater").start();
		logger.info("updater is running..");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("destroy client..");
	}
}
