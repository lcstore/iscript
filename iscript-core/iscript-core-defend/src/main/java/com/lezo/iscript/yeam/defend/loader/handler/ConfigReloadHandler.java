package com.lezo.iscript.yeam.defend.loader.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.lezo.iscript.yeam.defend.loader.ObjectReloader;
import com.lezo.iscript.yeam.defend.loader.ReloadHandler;
import com.lezo.iscript.yeam.defend.loader.ReloaderConstant;
import com.lezo.iscript.yeam.io.IOUtils;

public class ConfigReloadHandler implements ReloadHandler {
	@Override
	public void handle(String parent, String name, ObjectReloader clientReloader) throws Exception {
		File file = new File(parent, name);
		if (!isAccept(file)) {
			return;
		}
		String content = getContent(file);
		clientReloader.getLoadMap().put(name, content);
	}

	public String getContent(File file) throws Exception {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file), ReloaderConstant.CLIENT_CHARSET);
			br = new BufferedReader(isr);
			String line = null;
			String newLine = "\n";
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(newLine);
			}
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(isr);
		}
		return sb.toString();
	}

	public boolean isAccept(File file) {
		String name = file.getName();
		return file.isFile() && (name.endsWith(".js") || name.endsWith(".xml"));
	}
}
