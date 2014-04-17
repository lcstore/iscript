package com.lezo.iscript.yeam.defend.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lezo.iscript.yeam.defend.loader.handler.ClassReloadHandler;
import com.lezo.iscript.yeam.defend.loader.handler.ConfigReloadHandler;
import com.lezo.iscript.yeam.defend.loader.handler.JarReloadHandler;
import com.lezo.iscript.yeam.defend.loader.handler.PropertiesReloadHandler;

public class ObjectReloader {
	private Map<String, Object> loadMap = new HashMap<String, Object>();
	private List<ReloadHandler> handlers = new ArrayList<ReloadHandler>();

	public void attach(ReloadHandler handler) {
		handlers.add(handler);
	}

	public void dettach(ReloadHandler handler) {
		handlers.remove(handler);
	}

	public List<ReloadHandler> getDefaultHandlers() {
		List<ReloadHandler> defaultHandlers = new ArrayList<ReloadHandler>();
		ReloadClassLoader loader = new ReloadClassLoader();
		defaultHandlers.add(new ClassReloadHandler(loader));
		defaultHandlers.add(new JarReloadHandler(loader));
		defaultHandlers.add(new PropertiesReloadHandler());
		defaultHandlers.add(new ConfigReloadHandler());
		return defaultHandlers;
	}

	public void reload(String parent, String name) throws Exception {
		File file = new File(parent, name);
		if (!file.exists()) {
			// throw new FileNotFoundException(file + " missing..");
			return;
		}
		if (file.isFile()) {
			for (ReloadHandler handler : handlers) {
				if (handler.isAccept(file)) {
					handler.handle(parent, name, this);
					break;
				}
			}
		} else {
			File[] subFiles = file.listFiles();
			for (File sFile : subFiles) {
				reload(parent, name + File.separator + sFile.getName());
			}
		}
	}

	public List<ReloadHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<ReloadHandler> handlers) {
		this.handlers = handlers;
	}

	public Map<String, Object> getLoadMap() {
		return loadMap;
	}
}
