package com.lezo.iscript.yeam.defend.update;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.io.IOUtils;
import com.lezo.iscript.yeam.service.TaskerService;
import com.lezo.iscript.yeam.writable.StorageBufferWritable;
import com.lezo.iscript.yeam.writable.StorageHeaderWritable;

public class StorageUpdater {
	private static Logger log = Logger.getLogger(StorageUpdater.class);

	public static void doStorage(String localFolder, StorageHeaderWritable header, TaskerService taskerService)
			throws Exception {
		int limit = 1024 * 1024;
		File localFile = new File(localFolder, header.getName());
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		header.setOffset(0);
		try {
			mkdirs(localFile);
			fos = new FileOutputStream(localFile);
			bos = new BufferedOutputStream(fos, limit);
			int times = 0;
			while (true) {
				StorageBufferWritable buffer = getStorageBufferRetyable(header, taskerService,
						new StorageBufferRetryHandler());
				log.info("get storage buffer for[" + header.getName() + "],times:" + (++times));
				if (buffer == null || buffer.getBuffer() == null) {
					break;
				} else {
					bos.write(buffer.getBuffer());
				}
				if (buffer.getBuffer().length < limit) {
					break;
				} else {
					header.setOffset(header.getOffset() + buffer.getBuffer().length);
				}
			}
			bos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(bos);
		}

	}

	public static void doWrite(File dest, StorageBufferWritable buffer) throws Exception {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			mkdirs(dest);
			fos = new FileOutputStream(dest);
			bos = new BufferedOutputStream(fos);
			bos.write(buffer.getBuffer());
			bos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(bos);
		}
	}

	private static void mkdirs(File curFile) {
		if (curFile == null || curFile.exists()) {
			return;
		}
		File parent = curFile.getParentFile();
		if (parent == null || parent.exists()) {
			return;
		}
		parent.mkdirs();
	}

	public static StorageBufferWritable getStorageBufferRetyable(StorageHeaderWritable header,
			TaskerService taskerService, StorageBufferRetryHandler retryHandler) throws Exception {
		int count = 0;
		while (true) {
			try {
				return taskerService.getStorageBuffer(header);
			} catch (Exception e) {
				if (!retryHandler.doRetry(e, ++count)) {
					throw e;
				}
			}
		}

	}

	private static final class StorageBufferRetryHandler {
		private static final int total = 3;

		public boolean doRetry(Exception ex, int count) throws Exception {
			if (total > count) {
				return true;
			}
			return false;
		}
	}

}
