package com.lezo.iscript.updater.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.updater.service.IAgentManager;
import com.lezo.iscript.updater.utils.NameUtils;

public class AgentManager implements IAgentManager {
	private static Logger logger = LoggerFactory.getLogger(AgentManager.class);
	private Process agentProcess;
	private Timer ioChecher;
	private Long lastActive = System.currentTimeMillis();
	private int status = STATUS_DOWN;

	@Override
	public synchronized void stop() {
		int index = 0;
		logger.info("stop agent.step[" + (++index) + "],stop io checher ....");
		if (getIoChecher() != null) {
			getIoChecher().cancel();
			setIoChecher(null);
		}
		logger.info("stop agent.step[" + (++index) + "],destroy agent process ....");
		if (getAgentProcess() != null) {
			getAgentProcess().destroy();
			setAgentProcess(null);
		}
		setStatus(STATUS_DOWN);
		logger.info("stop agent success. done all step(" + index + ") ....");
	}

	@Override
	public synchronized void start() throws Exception {
		File workAgentFile = new File("agent", IAgentManager.AGENT_NAME);
		if (!workAgentFile.exists()) {
			throw new FileNotFoundException(workAgentFile.getAbsolutePath());
		}
		// String[] cmdArr = new String[] { "/bin/sh", "-c",
		// "java -Dclient_name=" + NameUtils.APP_NAME + " -jar " +
		// workAgentFile.getAbsolutePath() };
		String[] cmdArr = new String[] { "java", "-Dclient_name=" + NameUtils.APP_NAME, "-jar",
				workAgentFile.getAbsolutePath() };
		int index = 0;
		logger.info("start agent[" + (++index) + "],setup agent jar:" + workAgentFile);
		Process process = Runtime.getRuntime().exec(cmdArr);
		setAgentProcess(process);
		// int code = this.process.waitFor();
		setIoChecher(new Timer("ioChecker"));
		getIoChecher().schedule(new TimerTask() {

			@Override
			public void run() {
				if (getAgentProcess() != null) {
					try {
						byte[] buffer = new byte[1024];
						while (getAgentProcess().getInputStream().available() > 0) {
							getAgentProcess().getInputStream().read(buffer);
							setLastActive(System.currentTimeMillis());
							// print the log when debug is enable.
							if (logger.isDebugEnabled()) {
								logger.info(new String(buffer, "UTF-8"));
							}
						}
						while (getAgentProcess().getErrorStream().available() > 0) {
							getAgentProcess().getErrorStream().read(buffer);
							setLastActive(System.currentTimeMillis());
							logger.warn(new String(buffer, "UTF-8"));
							if (logger.isDebugEnabled()) {
							}
						}
					} catch (IOException ex) {
						logger.error("checking agent io.cause:", ex);
					}
					if (STATUS_RUNNING == getStatus() && System.currentTimeMillis() - lastActive > 120000) {
						logger.warn("agent dead.stop agent to restart");
						stop();
					}
				}
			}
		}, 0, 1000);
		logger.info("start agent[" + (++index) + "],start io checher");
		setStatus(STATUS_RUNNING);
		logger.info("start agent success. done all step(" + index + ") ....");
	}

	@Override
	public int getStatus() {
		return this.status;
	}

	public Process getAgentProcess() {
		return agentProcess;
	}

	public synchronized void setAgentProcess(Process agentProcess) {
		this.agentProcess = agentProcess;
	}

	public Timer getIoChecher() {
		return ioChecher;
	}

	public synchronized void setIoChecher(Timer ioChecher) {
		this.ioChecher = ioChecher;
	}

	public Long getLastActive() {
		return lastActive;
	}

	public synchronized void setLastActive(Long lastActive) {
		this.lastActive = lastActive;
	}

	private synchronized void setStatus(int status) {
		this.status = status;
	}

}
