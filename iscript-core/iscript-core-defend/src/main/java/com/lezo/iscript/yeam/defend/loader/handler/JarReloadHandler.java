package com.lezo.iscript.yeam.defend.loader.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.lezo.iscript.yeam.defend.loader.ClassByteUtils;
import com.lezo.iscript.yeam.defend.loader.ObjectReloader;
import com.lezo.iscript.yeam.defend.loader.ReloadClassLoader;
import com.lezo.iscript.yeam.defend.loader.ReloadHandler;
import com.lezo.iscript.yeam.defend.loader.ReloaderConstant;
import com.lezo.iscript.yeam.io.IOUtils;

public class JarReloadHandler implements ReloadHandler {
	private static final String HANDLER_FILE_SUFFIX = ".jar";
	private ReloadClassLoader loader;

	public JarReloadHandler(ReloadClassLoader loader) {
		super();
		this.loader = loader;
	}

	@Override
	public void handle(String parent, String name, ObjectReloader clientReloader) throws Exception {
		File file = new File(parent, name);
		if (!isAccept(file)) {
			return;
		}
		JarFile jarFile = new JarFile(file);
		Enumeration<JarEntry> jarEntries = jarFile.entries();

		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			if (jarEntry.isDirectory()) {
				continue;
			}
			String curName = jarEntry.getName();
//			 curName = "com/lezo/iscript/yeam/client/result/ResultsCaller.class";
//			 curName = "com/lezo/iscript/yeam/client/result/ResultsCaller$ResultsCallerHolder.class";
//			 curName = "com/lezo/iscript/yeam/client/config/ConfigCallable.class";
//			 curName = "com/lezo/iscript/yeam/client/result/ResultsCaller$1.class";
//			 curName = "com/lezo/iscript/yeam/client/service/impl/FetchServiceImpl.class";
			 curName = "com/lezo/iscript/yeam/client/strategy/SubmitStrategy.class";
			 curName = "com/lezo/iscript/yeam/client/strategy/impl/SubmitStrategyImpl.class";
			if (curName.endsWith(".class")) {
				InputStream in = jarFile.getInputStream(jarFile.getEntry(curName));
				byte[] bytes = ClassByteUtils.loadClassData(in);
				Class<?> newClass = loader.loadClass(curName, bytes);
				clientReloader.getLoadMap().put(newClass.getName(), newClass);
			} else if (curName.endsWith(".js") || curName.endsWith(".xml")) {
				InputStream in = jarFile.getInputStream(jarFile.getEntry(curName));
				byte[] bytes = ClassByteUtils.loadClassData(in);
				String content = new String(bytes, ReloaderConstant.CLIENT_CHARSET);
				clientReloader.getLoadMap().put(name, content);
			} else if (curName.endsWith(".properties")) {
				InputStream in = jarFile.getInputStream(jarFile.getEntry(curName));
				byte[] bytes = ClassByteUtils.loadClassData(in);
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				Properties pro = new Properties();
				try {
					pro.load(bis);
					for (Entry<Object, Object> entry : pro.entrySet()) {
						clientReloader.getLoadMap().put(entry.getKey().toString(), entry.getValue().toString());
					}
				} finally {
					IOUtils.closeQuietly(bis);
				}
			}
		}
	}

	public boolean isAccept(File file) {
		return file.isFile() && file.getName().endsWith(HANDLER_FILE_SUFFIX);
	}
}
