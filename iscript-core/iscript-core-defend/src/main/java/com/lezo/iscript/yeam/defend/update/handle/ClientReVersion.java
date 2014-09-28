package com.lezo.iscript.yeam.defend.update.handle;

import java.io.File;
import java.io.FileFilter;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.defend.DefendClient;
import com.lezo.iscript.yeam.writable.ClientWritable;

public class ClientReVersion extends AbtractClientHandler {
	private static Logger log = Logger.getLogger(ClientReVersion.class);
	private ClientHandle nextHandler;

	@Override
	public boolean doHandle(DefendClient client) throws Exception {
		// load yeam version
		log.info("start to do reversion..");
		// init client msg
		Object clientObject = ObjectBuilder.findObject(ClientWritable.class.getName());
		if (clientObject == null) {
			clientObject = new ClientWritable();
			ObjectBuilder.newObject(ClientWritable.class.getName(), clientObject);
		}
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File hasVersion = getVersion(clientPath);
		String version = null;
		if (hasVersion != null) {
			version = hasVersion.getName();
		}
		ClientWritable clientWritable = (ClientWritable) clientObject;
		String oldVersion = clientWritable.getVersion();
		String name = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_NAME);
		clientWritable.setName(name);
		clientWritable.setVersion(version);
		log.info("end to do reversion[" + clientWritable.getName() + "](" + oldVersion + " --> " + version + ")");
		return true;
	}

	private File getVersion(String clientPath) {
		File workFile = new File(clientPath, ClientConstant.CLIENT_WORK_SPACE);
		File[] vFiles = workFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches("^[0-9]+.version$");
			}
		});
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

	public ClientHandle getNextHandler() {
		return nextHandler;
	}

	public void setNextHandler(ClientHandle nextHandler) {
		this.nextHandler = nextHandler;
	}
}
