package com.lezo.iscript.yeam;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.mina.ClientMain;

public class ClientListener implements ServletContextListener {
	private static Logger logger = LoggerFactory.getLogger(ClientListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("start to init client..");
//		String[] args = new String[1];
		// YeamClient.main(args);
		new ClientMain().start();
		logger.info("client is running..");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("destroy client..");
	}
}
