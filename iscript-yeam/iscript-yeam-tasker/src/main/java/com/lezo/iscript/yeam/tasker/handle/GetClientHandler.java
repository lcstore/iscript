package com.lezo.iscript.yeam.tasker.handle;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;
import com.lezo.iscript.yeam.writable.StorageHeaderWritable;

public class GetClientHandler implements GetMoreHandle {
	private static Logger log = Logger.getLogger(GetClientHandler.class);
	private String clientPath;

	public GetClientHandler(String clientPath) {
		super();
		this.clientPath = clientPath;
	}

	@Override
	public boolean isAccept(ClientWritable client) {
		if (client == null || client.getVersion() == null) {
			return true;
		}
		return !new File(clientPath, client.getVersion()).exists();
	}

	@Override
	public RemoteWritable<?> createWritable(ClientWritable client) throws IOException {
		RemoteWritable<StorageHeaderWritable> remoteWritable = new RemoteWritable<StorageHeaderWritable>();
		if (!isAccept(client)) {
			remoteWritable.setStatus(ClientConstant.GET_NONE);
			return remoteWritable;
		}
		File versionFile = new File(clientPath);
		File[] vFiles = versionFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches("^[0-9]+.version$");
			}
		});
		if (vFiles == null) {
			return remoteWritable;
		}
		List<StorageHeaderWritable> storageHeaderWritables = new ArrayList<StorageHeaderWritable>();
		remoteWritable.setStorageList(storageHeaderWritables);
		remoteWritable.setStatus(ClientConstant.GET_CLIENT);
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
		if (newVersion == null) {
			throw new IOException("Not found version on tasker.clientPath:"+clientPath);
		}
		try {
			int limit = 1024 * 1024;
			StorageHeaderWritable vHeaderWritable = new StorageHeaderWritable();
			vHeaderWritable.setLimit(limit);
			vHeaderWritable.setOffset(0);
			vHeaderWritable.setName(newVersion.getName());
			vHeaderWritable.setLength(newVersion.length());
			storageHeaderWritables.add(vHeaderWritable);

			String vContent = FileUtils.readFileToString(newVersion, ClientConstant.CLIENT_CHARSET);
			StringTokenizer lineTokenizer = new StringTokenizer(vContent, "\n");
			while (lineTokenizer.hasMoreTokens()) {
				String name = lineTokenizer.nextToken().trim();
				if (name.isEmpty()) {
					continue;
				}
				File newFile = new File(clientPath, name);
				if (newFile.isFile() && newFile.exists()) {
					StorageHeaderWritable headerWritable = new StorageHeaderWritable();
					headerWritable.setLimit(limit);
					headerWritable.setOffset(0);
					headerWritable.setName(name);
					headerWritable.setLength(newFile.length());
					storageHeaderWritables.add(headerWritable);
				}
			}
		} catch (IOException e) {
			log.warn("", e);
		}
		return remoteWritable;
	}

	@Override
	public Integer getStatus() {
		return ClientConstant.GET_CLIENT;
	}

}
