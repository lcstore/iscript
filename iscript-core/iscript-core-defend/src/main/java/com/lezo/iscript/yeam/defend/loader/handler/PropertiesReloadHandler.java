package com.lezo.iscript.yeam.defend.loader.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Properties;

import com.lezo.iscript.yeam.defend.loader.ObjectReloader;
import com.lezo.iscript.yeam.defend.loader.ReloadHandler;
import com.lezo.iscript.yeam.defend.loader.ReloaderConstant;
import com.lezo.iscript.yeam.io.IOUtils;

public class PropertiesReloadHandler implements ReloadHandler {

	@Override
	public void handle(String parent, String name, ObjectReloader clientReloader) throws Exception {
		File file = new File(parent, name);
		if (!isAccept(file)) {
			return;
		}
		InputStream in = new FileInputStream(file);
		InputStreamReader ins = new InputStreamReader(in, ReloaderConstant.CLIENT_CHARSET);
		// 生成properties对象
		Properties pro = new Properties();
		try {
			pro.load(ins);
			for (Entry<Object, Object> entry : pro.entrySet()) {
				clientReloader.getLoadMap().put(entry.getKey().toString(), entry.getValue().toString());
			}
		} finally {
			IOUtils.closeQuietly(ins);
			IOUtils.closeQuietly(in);
		}

	}

	public boolean isAccept(File file) {
		return file.isFile() && file.getName().endsWith(".properties");
	}

}
