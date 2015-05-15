package com.lezo.iscript.updater.service;

public interface IAgentManager {
	public static int STATUS_DOWN = 0;
	public static int STATUS_RUNNING = 1;
	public static final String AGENT_NAME = "client-runner.jar";

	void stop();

	void start() throws Exception;

	int getStatus();
}
