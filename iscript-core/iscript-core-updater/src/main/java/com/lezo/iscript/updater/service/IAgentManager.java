package com.lezo.iscript.updater.service;

public interface IAgentManager {
	public static int STATUS_DOWN = 0;
	public static int STATUS_UP = 1;
	public static final String AGENT_NAME = "client-runner.jar";

	void stop();

	void start() throws AgentManagerExcepton;

	int getStatus();

	class AgentManagerExcepton extends Exception {
		private static final long serialVersionUID = 1L;

		public AgentManagerExcepton() {
			super();
		}

		public AgentManagerExcepton(String message, Throwable cause) {
			super(message, cause);
		}

		public AgentManagerExcepton(String message) {
			super(message);
		}

		public AgentManagerExcepton(Throwable cause) {
			super(cause);
		}
	}
}
