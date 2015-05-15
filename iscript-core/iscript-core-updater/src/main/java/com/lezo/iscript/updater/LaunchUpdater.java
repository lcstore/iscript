package com.lezo.iscript.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.updater.service.IAgentManager;
import com.lezo.iscript.updater.service.IUpdateManager;
import com.lezo.iscript.updater.service.IVersionManager;
import com.lezo.iscript.updater.service.impl.AgentManager;
import com.lezo.iscript.updater.service.impl.UpdateManager;
import com.lezo.iscript.updater.service.impl.VersionManager;
import com.lezo.iscript.updater.utils.PropertiesUtils;

public class LaunchUpdater {
	private static final int MAX_RETRY_COUNT = 5;
	private static Logger logger = LoggerFactory.getLogger(LaunchUpdater.class);
	private IVersionManager versionManager = new VersionManager();
	private IUpdateManager updateManager = new UpdateManager();
	private IAgentManager agentManager = new AgentManager();

	public static void main(String[] args) throws Exception {
		PropertiesUtils.loadQuietly(PropertiesUtils.class.getClassLoader().getResourceAsStream("updater.properties"));

		final LaunchUpdater launcher = new LaunchUpdater();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				launcher.getAgentManager().stop();
			}
		}));
		String name = ManagementFactory.getRuntimeMXBean().getName();
		logger.info("start updater:" + name);
		while (true) {
			try {
				launcher.execute(args);
				TimeUnit.MILLISECONDS.sleep(60000);
			} catch (Exception ex) {
				logger.error("cause:", ex);
				TimeUnit.MILLISECONDS.sleep(10000);
			}
		}

	}

	private void execute(String[] args) {
		long startMills = System.currentTimeMillis();
		String oldVerion = versionManager.getVersion();
		String newVersion = updateManager.getCurrentVersion();
		if (StringUtils.isBlank(newVersion)) {
			logger.warn("get an empty version,check the server.");
			return;
		}
		if (oldVerion.equals(newVersion)) {
			if (IAgentManager.STATUS_RUNNING != agentManager.getStatus()) {
				logger.info("try to start agent by current version:" + oldVerion);
				turnTo(IAgentManager.STATUS_RUNNING, MAX_RETRY_COUNT);
				if (IAgentManager.STATUS_RUNNING != agentManager.getStatus()) {
					versionManager.changeTo(IVersionManager.TO_UPDATE_VERSION);
					logger.warn("fail to start agent by current version:" + oldVerion + ",try to update new agent");
				} else {
					logger.info("success to start agent by current version:" + oldVerion);
				}
			}
			return;
		}
		logger.info("update agent version,from:" + oldVerion + ",to:" + newVersion);
		File newAgentFile = new File("temp", IAgentManager.AGENT_NAME);
		if (!updateManager.extractTo(newAgentFile)) {
			logger.warn("fail to extract new version:" + newVersion + ",To:" + newAgentFile);
			return;
		}
		turnTo(IAgentManager.STATUS_DOWN, MAX_RETRY_COUNT);
		File workAgentFile = new File("agent", IAgentManager.AGENT_NAME);
		try {
			FileUtils.forceDeleteOnExit(workAgentFile);
			FileUtils.copyFile(newAgentFile, workAgentFile);
			FileUtils.deleteDirectory(newAgentFile.getParentFile());
		} catch (IOException e) {
			versionManager.changeTo(IVersionManager.TO_UPDATE_VERSION);
			logger.warn("fail to apply new agent file", e);
			return;
		}
		turnTo(IAgentManager.STATUS_RUNNING, MAX_RETRY_COUNT);
		long cost = System.currentTimeMillis() - startMills;
		if (agentManager.getStatus() != IAgentManager.STATUS_RUNNING) {
			newVersion = IVersionManager.TO_UPDATE_VERSION;
			logger.warn("fail to start new agent.changeTo:" + newVersion + ",to update again..");
			versionManager.changeTo(newVersion);
		} else {
			versionManager.changeTo(newVersion);
			logger.info("success to start new agent.changeTo:" + newVersion + ",cost:" + cost);
		}
	}

	private void turnTo(int toStatus, int maxRetryCount) {
		int maxRetry = maxRetryCount;
		while (maxRetry-- > 0 && agentManager.getStatus() != toStatus) {
			try {
				if (IAgentManager.STATUS_DOWN == toStatus) {
					agentManager.stop();
				} else if (IAgentManager.STATUS_RUNNING == toStatus) {
					agentManager.start();
				}
			} catch (FileNotFoundException e) {
				logger.warn("turn agent to status:" + toStatus + ",cause:", e);
				break;
			} catch (Exception e) {
				logger.warn("turn agent to status:" + toStatus + ".leave retry time:" + maxRetry + ",cause:", e);
			}
		}
	}

	public IAgentManager getAgentManager() {
		return agentManager;
	}
}
