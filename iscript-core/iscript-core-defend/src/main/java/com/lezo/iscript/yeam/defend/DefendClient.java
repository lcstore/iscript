package com.lezo.iscript.yeam.defend;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.defend.client.ClientController;
import com.lezo.iscript.yeam.defend.client.ClientExecuter;
import com.lezo.iscript.yeam.defend.client.ClientUpdater;

public class DefendClient {
	private static Logger log = Logger.getLogger(DefendClient.class);
	public static final int CLIENT_NORMAL = 0;
	public static final int CLIENT_UPDATE = 1;
	public static final int CLIENT_RESTART = 2;
	private int status = CLIENT_NORMAL;

	private DefendClient() {
	}

	private static final class InstanceHolder {
		public static final DefendClient INSTANCE = new DefendClient();
	}

	public static DefendClient getDefendClient() {
		return InstanceHolder.INSTANCE;
	}

	public static void main(String[] args) throws Exception {
		log.info("start to init defend..");
		long start = System.currentTimeMillis();
		DefendClient client = DefendClient.getDefendClient();
		client.initArgs();
		ClientUpdater.getClientUpdater().doExecute(client);
		ClientExecuter.getClientExecuter().doExecute();
		Thread hook = new Thread(new Runnable() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				ClientController controller = ClientExecuter.getClientExecuter().getController();
				controller.close();
				long cost = System.currentTimeMillis() - start;
				log.info("close client[" + controller.getName() + "].cost:" + cost + "ms");
			}
		});
		Runtime.getRuntime().addShutdownHook(hook);
		long cost = System.currentTimeMillis() - start;
		log.info("success to start defend.cost:" + cost + "ms");
	}

	protected void initArgs() {
		ObjectBuilder.newObject(ClientConstant.CLIENT_PATH, getArgument(ClientConstant.CLIENT_PATH));
		ObjectBuilder.newObject(ClientConstant.CLIENT_DEFEND_TASKER, getArgument(ClientConstant.CLIENT_DEFEND_TASKER));
	}

	private Object getArgument(String name) {
		Object argsObject = System.getProperty(name);
		if (argsObject == null) {
			argsObject = System.getenv(name);
		}
		if (argsObject == null) {
			throw new IllegalArgumentException("Can not found[" + name + "],please set <-D" + name + "=something>");
		}
		return argsObject;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
