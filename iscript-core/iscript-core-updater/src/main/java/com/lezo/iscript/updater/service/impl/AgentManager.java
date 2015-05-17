package com.lezo.iscript.updater.service.impl;

import java.io.File;
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
	public synchronized void start() throws AgentManagerExcepton {
		File workAgentFile = new File(IAgentManager.AGENT_NAME);
		if (!workAgentFile.exists()) {
			throw new AgentManagerExcepton("Not found file:" + workAgentFile.getAbsolutePath());
		}
		workAgentFile.setWritable(true, true);
		StringBuilder sb = new StringBuilder();
		sb.append("java ");
		sb.append("-Dclient_name=");
		sb.append(NameUtils.APP_NAME);
		sb.append(" -jar ");
		sb.append(workAgentFile.getAbsolutePath());
		String execString = sb.toString();
		logger.info("ready to execute cmd:" + execString);
		String[] cmdArr = new String[] { "/bin/sh", "-c", execString };
		int index = 0;
		logger.info("start agent[" + (++index) + "],setup agent jar:" + workAgentFile);
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdArr);
		} catch (IOException e) {
			throw new AgentManagerExcepton("exec cmd", e);
		}
		setAgentProcess(process);
		setIoChecher(new Timer("ioChecker"));
		getIoChecher().schedule(new TimerTask() {

			@Override
			public void run() {
				if (getAgentProcess() != null && IAgentManager.STATUS_UP != getStatus()) {
					return;
				}
				try {
					byte[] buffer = new byte[1024];
					while (getAgentProcess().getInputStream().available() > 0) {
						setLastActive(System.currentTimeMillis());
						int len = getAgentProcess().getInputStream().read(buffer);
						// print the log when debug is enable.
						logger.info(new String(buffer, 0, len, "UTF-8"));
						if (logger.isDebugEnabled()) {
						}
					}
					while (getAgentProcess().getErrorStream().available() > 0) {
						setLastActive(System.currentTimeMillis());
						int len = getAgentProcess().getErrorStream().read(buffer);
						logger.warn(new String(buffer, 0, len, "UTF-8"));
						if (logger.isDebugEnabled()) {
						}
					}
				} catch (IOException ex) {
					logger.error("checking agent io.cause:", ex);
				}
				if (STATUS_UP == getStatus() && System.currentTimeMillis() - lastActive > 120000) {
					logger.warn("agent dead.stop agent to restart");
					stop();
					setLastActive(System.currentTimeMillis());
				}
			}
		}, 0, 1000);
		logger.info("start agent[" + (++index) + "],start io checher");
		setStatus(STATUS_UP);
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
