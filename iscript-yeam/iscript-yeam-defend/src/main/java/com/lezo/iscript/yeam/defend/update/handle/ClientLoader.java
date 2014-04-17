package com.lezo.iscript.yeam.defend.update.handle;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import sun.misc.ClassLoaderUtil;

import com.lezo.iscript.yeam.ClassBuilder;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.Clientable;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.defend.DefendClient;
import com.lezo.iscript.yeam.defend.dirs.DirsUtils;
import com.lezo.iscript.yeam.defend.loader.CustomReloadClassLoader;
import com.lezo.iscript.yeam.writable.ClientWritable;

public class ClientLoader extends AbtractClientHandler {
	private static Logger log = Logger.getLogger(ClientLoader.class);
	private ClientHandle nextHandler;

	public ClientLoader() {
	}

	@Override
	public boolean doHandle(DefendClient client) throws Exception {
		if (isLoadable()) {
			String path = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
			String loadPath = path + File.separator + ClientConstant.CLIENT_UPDATE_SPACE;
			String workPath = path + File.separator + ClientConstant.CLIENT_WORK_SPACE;
			closeLoadFile();
			Object clientObject = ObjectBuilder.findObject(ClientWritable.class.getName());
			if (clientObject != null) {
				DirsUtils.clearDirs(new File(workPath));
			}
			log.info("move new client to workspace.");
			moveToWorkspace(loadPath, workPath);
			URL[] urls = new URL[] {};
			CustomReloadClassLoader newLoader = new CustomReloadClassLoader(urls);
			addURLs(newLoader, workPath);
			StringBuilder sb = new StringBuilder();
			urls = newLoader.getURLs();
			for (int i = 0; i < urls.length; i++) {
				sb.append(urls[i]);
				if (i + 1 < urls.length) {
					sb.append(",");
				}
			}
			log.info("Current ClassLoader URLs:" + sb.toString());
			loadNewClient(newLoader);
			log.info("finish to load:" + workPath);
			return true;
		} else {
			return false;
		}
	}

	private void closeLoadFile() {
		ClassLoader loader = ClassBuilder.getClassLoader(ClassBuilder.YEAM_CLIENT_NAME);
		if (loader == null) {
			return;
		}
		try {
			CustomReloadClassLoader newLoader = (CustomReloadClassLoader) loader;
			List<IOException> excepitons = ClassLoaderUtil.releaseLoader(newLoader,new ArrayList<String>());
			int size = 0;
			if(excepitons != null){
				for(IOException ex:excepitons){
					log.warn("close loader.",ex);
				}
				size = excepitons.size();
			}
			log.info("close class loader,cause exection:" + size);
		} catch (Exception e) {
			log.warn("", e);
		}
	}

	private void loadNewClient(CustomReloadClassLoader newLoader) throws Exception {
		Object workingClientObject = ObjectBuilder.findObject(ClassBuilder.YEAM_CLIENT_NAME);
		if (workingClientObject != null) {
			long timeout = 60 * 1000;
			int leave = ((Clientable) workingClientObject).shutdown(timeout);
			log.info("<client>.shutdown old client.give up:" + leave);
		}
		Object clientObj = ObjectBuilder.findObject(ClientWritable.class.getName());
		ClassBuilder.clear();
		ObjectBuilder.clear();
		initArgs();
		if (clientObj != null) {
			ObjectBuilder.newObject(ClientWritable.class.getName(), clientObj);
		}
		ClassBuilder.newClass(ClassBuilder.YEAM_CLIENT_NAME, newLoader, true);
	}

	protected void initArgs() throws Exception {
		String envKeyMark = "yeam.";
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

	private boolean isLoadable() {
		Object clientObject = ObjectBuilder.findObject(ClientWritable.class.getName());
		// first load
		if (clientObject == null) {
			return true;
		}
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File hasVersion = getVersion(new File(clientPath, ClientConstant.CLIENT_WORK_SPACE));
		return hasVersion != null;
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

	private void addURLs(CustomReloadClassLoader loader, String workPath) throws ClassNotFoundException {
		File workFile = new File(workPath);
		if (!workFile.exists()) {
			return;
		}
		addURLs(loader, workFile);
	}

	private void addURLs(CustomReloadClassLoader loader, File file) {
		if (file.isFile() && (file.getName().endsWith(".class") || file.getName().endsWith(".jar"))) {
			try {
				loader.addNewURL(file.toURI().toURL());
			} catch (MalformedURLException e) {
				log.warn("", e);
			}
		} else {
			File[] files = file.listFiles();
			if (files != null) {
				for (File child : files) {
					addURLs(loader, child);
				}
			}
		}

	}

	private void moveToWorkspace(String loadPath, String workPath) {
		if (loadPath.equals(workPath)) {
			return;
		}
		copyTo(loadPath, workPath, new File(loadPath));
		File loadFile = new File(loadPath);
		DirsUtils.clearDirs(loadFile);
		loadFile.delete();
	}

	private void copyTo(String loadPath, String workPath, File copyFile) {
		if (copyFile.isFile()) {
			String destPath = copyFile.getAbsolutePath().replace(loadPath, workPath);
			File destFile = new File(destPath);
			if (!destFile.exists()) {
				File parent = destFile.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}
			}
			destFile.delete();
			copyFile.renameTo(destFile);
		} else {
			File[] childArr = copyFile.listFiles();
			if (childArr != null) {
				for (File child : childArr) {
					copyTo(loadPath, workPath, child);
				}
			}
		}
	}

	public void setNextHandler(ClientHandle nextHandler) {
		this.nextHandler = nextHandler;
	}

	public ClientHandle getNextHandler() {
		return nextHandler;
	}

}
