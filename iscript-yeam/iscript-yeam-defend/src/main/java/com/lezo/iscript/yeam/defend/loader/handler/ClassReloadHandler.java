package com.lezo.iscript.yeam.defend.loader.handler;

import java.io.File;

import com.lezo.iscript.yeam.defend.loader.ObjectReloader;
import com.lezo.iscript.yeam.defend.loader.ReloadHandler;

public class ClassReloadHandler implements ReloadHandler {
	private static final String HANDLER_FILE_SUFFIX = ".class";
	private ClassLoader loader;

	public ClassReloadHandler(ClassLoader loader) {
		super();
		this.loader = loader;
	}

	@Override
	public void handle(String parent, String name, ObjectReloader clientReloader) throws Exception {
		File file = new File(parent, name);
		if (!isAccept(file)) {
			return;
		}
		Class<?> newClass = loader.loadClass(file.getAbsolutePath());
		clientReloader.getLoadMap().put(newClass.getName(), newClass);
	}

	public boolean isAccept(File file) {
		return file.isFile() && file.getName().endsWith(HANDLER_FILE_SUFFIX);
	}

	public String getFolder(String name, String clsName) {
		String clsPath = clsName.replace(".", File.separator);
		String newName = name.replace("/", File.separator);
		int index = newName.indexOf(clsPath);
		String folder = (index > -1) ? newName.substring(0, index) : "";
		folder = folder.startsWith(File.separator) ? folder : File.separator + folder;
		return folder;
	}

	public String getQualifyName(File file) {
		String sMark = "com";
		char pMark = '.';
		StringBuilder sb = new StringBuilder();
		int offset = 0;
		sb.insert(offset, file.getName().replace(HANDLER_FILE_SUFFIX, ""));
		File parent = file.getParentFile();
		while (parent != null) {
			String name = parent.getName();
			if (name == null) {
				break;
			} else if (sMark.equals(name)) {
				sb.insert(offset, pMark);
				sb.insert(offset, name);
				break;
			} else {
				sb.insert(offset, pMark);
				sb.insert(offset, name);
				parent = parent.getParentFile();
			}
		}
		return sb.toString();
	}

}
