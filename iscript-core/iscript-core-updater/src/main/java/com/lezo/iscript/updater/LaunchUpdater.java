package com.lezo.iscript.updater;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

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
	private static Logger logger = LoggerFactory.getLogger(LaunchUpdater.class);
	private IVersionManager versionManager = new VersionManager();
	private IUpdateManager updateManager = new UpdateManager();
	private IAgentManager agentManager = new AgentManager();

	// TODO when updater stop[kill -15 pid],the client-runner.jar auto been
	// delete.
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
			} catch (IAgentManager.AgentManagerExcepton ex) {
				launcher.getVersionManager().changeTo(IVersionManager.TO_UPDATE_VERSION);
				logger.error("agent manager fail,try to update.cause:", ex);
				TimeUnit.MILLISECONDS.sleep(30000);
			} catch (Exception ex) {
				logger.error("cause:", ex);
				TimeUnit.MILLISECONDS.sleep(30000);
			}
		}

	}

	private void execute(String[] args) throws Exception {
		long startMills = System.currentTimeMillis();
		String oldVerion = versionManager.getVersion();
		String newVersion = updateManager.getCurrentVersion();
		if (StringUtils.isBlank(newVersion)) {
			logger.warn("get an empty version,check the server.");
			return;
		}
		if (oldVerion.equals(newVersion)) {
			if (IAgentManager.STATUS_UP != agentManager.getStatus()) {
				logger.info("start agent by current version:" + oldVerion);
				getAgentManager().start();
			}
			return;
		}
		logger.info("update agent version,from:" + oldVerion + ",to:" + newVersion);
		File tmpFile = new File(IAgentManager.AGENT_NAME + ".tmp");
		if (!updateManager.extractTo(tmpFile)) {
			logger.warn("fail to extract new version:" + newVersion + ",To:" + tmpFile);
			return;
		}
		getAgentManager().stop();
		File destFile = new File(IAgentManager.AGENT_NAME);
		if (tmpFile.exists()) {
			destFile.deleteOnExit();
			if (!tmpFile.renameTo(destFile)) {
				logger.warn("cannot rename from:" + tmpFile + ",to:" + destFile);
			}
		}
		getAgentManager().start();
		long cost = System.currentTimeMillis() - startMills;
		if (agentManager.getStatus() != IAgentManager.STATUS_UP) {
			newVersion = IVersionManager.TO_UPDATE_VERSION;
			logger.warn("fail to start new agent.changeTo:" + newVersion + ",to update again..");
			versionManager.changeTo(newVersion);
		} else {
			versionManager.changeTo(newVersion);
			logger.info("success to start new agent.changeTo:" + newVersion + ",cost:" + cost);
		}
	}

	public IAgentManager getAgentManager() {
		return agentManager;
	}

	public IVersionManager getVersionManager() {
		return versionManager;
	}
}
