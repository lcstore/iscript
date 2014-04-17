package com.lezo.iscript.yeam.defend.client;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.writable.ClientWritable;

public class ClientController {
	private static Logger log = Logger.getLogger(ClientController.class);
	private ClientLocker locker;
	private String name;
	private Process process;

	public ClientController(ClientLocker locker) {
		super();
		this.locker = locker;
	}

	public boolean start() {
		String name = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_NAME);
		if (this.process == null) {
			locker.unLock(name);
		}
		if (!locker.tryLock(name)) {
			return false;
		}
		try {
			this.process = newClient();
			locker.doLock(name);
			this.name = name;
		} catch (IOException e) {
			log.error("start[" + name + "]", e);
			return false;
		}
		return true;
	}

	public void close() {
		try {
			if (process == null) {
				return;
			}
			process.destroy();
			// IOUtils.closeQuietly(process.getErrorStream());
			// IOUtils.closeQuietly(process.getInputStream());
			// IOUtils.closeQuietly(process.getOutputStream());
			process = null;
		} finally {
			locker.unLock(this.name);
		}
	}

	private Process newClient() throws IOException {
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File workFile = new File(clientPath, ClientConstant.CLIENT_WORK_SPACE);
		ClientWritable clientWritable = (ClientWritable) ObjectBuilder.findObject(ClientWritable.class.getName());
		clientWritable.setName(ObjectBuilder.findObject(ClientConstant.CLIENT_NAME).toString());
		ProcessBuilder pBuilder = new ProcessBuilder();
		File clientFile = new File(workFile, "client" + File.separator + "client.jar");
		String[] cmdArray = new String[] { "java", "-Dyeam.name=" + clientWritable.getName(),
				"-Dyeam.version=" + clientWritable.getVersion(),
				"-Dyeam.client.path=" + ObjectBuilder.findObject(ClientConstant.CLIENT_PATH),
				"-Dyeam.tasker.host=" + ObjectBuilder.findObject(ClientConstant.CLIENT_TASKER_HOST), "-jar",
				clientFile.getAbsolutePath() };
		pBuilder = pBuilder.command(cmdArray);
		Process process = pBuilder.start();
		return process;
	}

	public Process getProcess() {
		return process;
	}

	public ClientLocker getLocker() {
		return locker;
	}

	public String getName() {
		return name;
	}
}
