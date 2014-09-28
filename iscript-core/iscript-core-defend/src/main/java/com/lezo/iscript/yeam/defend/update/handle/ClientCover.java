package com.lezo.iscript.yeam.defend.update.handle;

import java.io.File;
import java.io.FileFilter;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.defend.DefendClient;
import com.lezo.iscript.yeam.defend.client.ClientExecuter;
import com.lezo.iscript.yeam.defend.dirs.DirsUtils;
import com.lezo.iscript.yeam.writable.ClientWritable;

public class ClientCover extends AbtractClientHandler {
	private static Logger log = Logger.getLogger(ClientCover.class);
	private ClientHandle nextHandler;

	public ClientCover() {
	}

	@Override
	public boolean doHandle(DefendClient client) throws Exception {
		if (isStartup()) {
			setNextHandler(new ClientReVersion());
			return true;
		} else if (!isCoverable()) {
			setNextHandler(null);
			return false;
		}
		log.info("start to do cover..");
		String path = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		String updatePath = path + File.separator + ClientConstant.CLIENT_UPDATE_SPACE;
		String workPath = path + File.separator + ClientConstant.CLIENT_WORK_SPACE;
		String lockSuffix = ".lock";
		File workFile = new File(workPath);
		File workLock = new File(workPath + lockSuffix);
		File updateFile = new File(updatePath);
		int index = 0;
		while (workFile.exists() && !workFile.renameTo(workLock)) {
			ClientExecuter.getClientExecuter().getController().close();
			log.info("try to close client,cover client sources.try[" + (++index) + "].");
		}
		updateFile.renameTo(workFile);
		DirsUtils.clearDirs(workLock);
		workLock.delete();
		setNextHandler(new ClientReVersion());
		log.info("end to do cover..");
		return true;
	}

	protected void initArgs() throws Exception {
		String envKeyMark = ClientConstant.CLIENT_ENV_HEAD;
		// load System.getenv
		for (Entry<String, String> entry : System.getenv().entrySet()) {
			if (entry.getKey().startsWith(envKeyMark)) {
				ObjectBuilder.newObject(entry.getKey(), entry.getValue());
			}
		}
		// load System.getProperties
		for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
			String key = entry.getKey().toString();
			if (key.startsWith(envKeyMark)) {
				ObjectBuilder.newObject(key, entry.getValue().toString());
			}
		}
	}

	private boolean isCoverable() {
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File hasVersion = getVersion(new File(clientPath, ClientConstant.CLIENT_UPDATE_SPACE));
		return hasVersion != null;
	}

	private boolean isStartup() {
		Object clientObject = ObjectBuilder.findObject(ClientWritable.class.getName());
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		// first load
		if (clientObject == null) {
			File hasVersion = getVersion(new File(clientPath, ClientConstant.CLIENT_WORK_SPACE));
			return hasVersion != null;
		}
		return false;
	}

	private File getVersion(File file) {
		File[] vFiles = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches("^[0-9]+.version$");
			}
		});
		if (vFiles == null) {
			return null;
		}
		File newVersion = null;
		long maxVersion = 0;
		for (File vFile : vFiles) {
			if (newVersion == null) {
				newVersion = vFile;
				String version = newVersion.getName().replace(".version", "");
				maxVersion = Long.valueOf(version);
			} else {
				String version = vFile.getName().replace(".version", "");
				Long curVersion = Long.valueOf(version);
				if (maxVersion < curVersion) {
					newVersion = vFile;
					maxVersion = curVersion;
				}
			}
		}
		return newVersion;
	}

	public void setNextHandler(ClientHandle nextHandler) {
		this.nextHandler = nextHandler;
	}

	public ClientHandle getNextHandler() {
		return nextHandler;
	}

}
