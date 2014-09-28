package com.lezo.iscript.yeam.defend.update.handle;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

import com.lezo.iscript.spring.remote.ProxyFactoryBeanUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.defend.DefendClient;
import com.lezo.iscript.yeam.defend.dirs.DirsUtils;
import com.lezo.iscript.yeam.defend.dirs.FileZipUtils;
import com.lezo.iscript.yeam.defend.update.StorageUpdater;
import com.lezo.iscript.yeam.service.TaskerService;
import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;
import com.lezo.iscript.yeam.writable.StorageHeaderWritable;

public class ClientPuller extends AbtractClientHandler {
	private static Logger log = Logger.getLogger(ClientPuller.class);
	private ClientHandle nextHandler;

	@Override
	public boolean doHandle(final DefendClient client) throws Exception {
		return doPullClient();
	}

	protected boolean doPullClient() throws Exception {
		String serviceUrl = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_DEFEND_TASKER);
		HttpInvokerRequestExecutor excutor = null;
		TaskerService taskerService = (TaskerService) ProxyFactoryBeanUtils.createHttpInvokerProxyFactoryBean(
				serviceUrl, TaskerService.class, excutor);
		ClientWritable clientWritable = null;
		Object clientObject = ObjectBuilder.findObject(ClientWritable.class.getName());
		if (clientObject != null) {
			clientWritable = (ClientWritable) clientObject;
		} else {
			// command to load old client
			if (existClient()) {
				setNextHandler(new ClientCover());
				return true;
			}
		}
		String version = clientWritable == null ? null : clientWritable.getVersion();
		log.info("start to find new version,current[" + version + "]");
		RemoteWritable<?> remoteWritable = taskerService.getClient(version);
		if (ClientConstant.GET_CLIENT != remoteWritable.getStatus() || remoteWritable.getStorageList() == null) {
			setNextHandler(null);
			return false;
		}
		List<?> storages = remoteWritable.getStorageList();
		List<StorageHeaderWritable> vHeaders = new ArrayList<StorageHeaderWritable>(storages.size());
		for (Object storage : storages) {
			if (storage instanceof StorageHeaderWritable) {
				vHeaders.add((StorageHeaderWritable) storage);
			}
		}
		if (vHeaders.isEmpty()) {
			return false;
		}
		log.info("start to pull new version client..");
		handleNewVersionFiles(vHeaders, taskerService);
		handleVersionFile();
		setNextHandler(new ClientCover());
		log.info("end to pull new version client..");
		return true;
	}

	private boolean existClient() {
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File hasVersion = getVersion(new File(clientPath, ClientConstant.CLIENT_WORK_SPACE));
		return hasVersion != null;
	}

	private void handleNewVersionFiles(List<StorageHeaderWritable> headers, TaskerService taskerService)
			throws Exception {
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File updateSpace = new File(clientPath, ClientConstant.CLIENT_UPDATE_SPACE);
		DirsUtils.clearDirs(updateSpace);
		if (!updateSpace.exists()) {
			updateSpace.mkdirs();
		}
		for (StorageHeaderWritable shWritable : headers) {
			String name = shWritable.getName();
			if (name == null || name.isEmpty()) {
				continue;
			}
			StorageUpdater.doStorage(updateSpace.getAbsolutePath(), shWritable, taskerService);
			if (name.endsWith(".zip")) {
				File src = new File(updateSpace, name);
				File dest = updateSpace;
				FileZipUtils.doUnZip(src, dest);
				src.delete();
			}
		}
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

	private void handleVersionFile() {
		// get old version files
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File updateSpace = new File(clientPath, ClientConstant.CLIENT_UPDATE_SPACE);
		File localFile = updateSpace;
		File[] vFiles = localFile.listFiles(getVersionFilter());
		if (vFiles == null) {
			return;
		}
		File newVersion = getVersion(updateSpace);
		// delete old version
		for (File vFile : vFiles) {
			if (newVersion.equals(vFile)) {
				continue;
			} else {
				vFile.delete();
			}
		}
	}

	private FileFilter getVersionFilter() {
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches("^[0-9]+\\.version$");
			}
		};
		return filter;
	}

	public ClientHandle getNextHandler() {
		return nextHandler;
	}

	public void setNextHandler(ClientHandle nextHandler) {
		this.nextHandler = nextHandler;
	}

}
