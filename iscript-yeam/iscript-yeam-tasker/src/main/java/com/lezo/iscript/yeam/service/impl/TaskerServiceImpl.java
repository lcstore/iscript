package com.lezo.iscript.yeam.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.io.IOUtils;
import com.lezo.iscript.yeam.service.TaskerService;
import com.lezo.iscript.yeam.tasker.handle.GetClientHandler;
import com.lezo.iscript.yeam.tasker.handle.GetConfigHandler;
import com.lezo.iscript.yeam.tasker.handle.GetMoreHandle;
import com.lezo.iscript.yeam.tasker.handle.GetTaskHandler;
import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;
import com.lezo.iscript.yeam.writable.StorageBufferWritable;
import com.lezo.iscript.yeam.writable.StorageHeaderWritable;

public class TaskerServiceImpl implements TaskerService {
	private static Logger log = Logger.getLogger(TaskerServiceImpl.class);
	private String clientPath;
	private GetMoreHandle clientHandler;
	private List<GetMoreHandle> moreHandles;

	protected void initHandler() {
		clientHandler = new GetClientHandler(clientPath);
		moreHandles = new ArrayList<GetMoreHandle>();
		GetMoreHandle configHandler = new GetConfigHandler();
		GetMoreHandle taskHandler = new GetTaskHandler();
		moreHandles.add(configHandler);
		moreHandles.add(taskHandler);
	}

	@Override
	public StorageBufferWritable getStorageBuffer(StorageHeaderWritable header) throws IOException {
		StorageBufferWritable sbWritable = new StorageBufferWritable();
		sbWritable.setName(header.getName());
		File srcFile = getSource(header, clientPath);
		FileInputStream fis = null;
		long offset = header.getOffset();
		long limit = srcFile.length() - offset;
		limit = limit > header.getLimit() ? header.getLimit() : limit;
		int times = (int) ((offset / header.getLimit()) + 1);
		try {
			fis = new FileInputStream(srcFile);
			int len = (int) (limit);
			byte[] newBytes = new byte[len];
			fis.skip(offset);
			int length = fis.read(newBytes);
			if (length != len) {
				throw new IllegalArgumentException("len and read len not match.file:" + srcFile);
			}
			log.info("get storage buffer for[" + header.getName() + "],times:" + (++times));
			sbWritable.setBuffer(newBytes);
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return sbWritable;
	}

	@Override
	public RemoteWritable<?> getClient(String version) throws IOException {
		ClientWritable client = new ClientWritable();
		client.setVersion(version);
		return clientHandler.createWritable(client);
	}

	private File getSource(StorageHeaderWritable header, String clientPath) {
		if (header.getPath() != null) {
			File srcFile = new File(header.getPath(), header.getName());
			if (srcFile.exists()) {
				return srcFile;
			}
			File parent = new File(clientPath, header.getPath());
			srcFile = new File(parent, header.getName());
			if (srcFile.exists()) {
				return srcFile;
			}
		}
		File srcFile = new File(clientPath, header.getName());
		if (srcFile.exists()) {
			return srcFile;
		}
		return null;
	}

	@Override
	public RemoteWritable<?> getMore(ClientWritable client) throws IOException {
		// TODO:has a new client version?
		// TODO:has a new config version?
		// TODO:get a new task?
		for (GetMoreHandle handler : moreHandles) {
			if (handler.isAccept(client)) {
				return handler.createWritable(client);
			}
		}
		RemoteWritable<?> noneWritable = new RemoteWritable<Serializable>();
		noneWritable.setStatus(ClientConstant.GET_NONE);
		return noneWritable;
	}

	public void setClientPath(String clientPath) {
		this.clientPath = clientPath;
	}

}
