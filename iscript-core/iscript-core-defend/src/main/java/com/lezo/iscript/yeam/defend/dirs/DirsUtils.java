package com.lezo.iscript.yeam.defend.dirs;

import java.io.File;
import java.util.Stack;

public class DirsUtils {

	/**
	 * delete dir
	 * 
	 * @param dir
	 */
	public static void clearDirs(File dir) {
		if (!dir.isDirectory()) {
			return;
		}
		Stack<File> fileStack = new Stack<File>();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File dirFile : files) {
				if (dirFile.isDirectory()) {
					fileStack.push(dirFile);
				} else {
					dirFile.delete();
				}
			}
		}
		if (!fileStack.isEmpty()) {
			clearDirs(fileStack);
		}
	}

	/**
	 * delete files of dir,then delete the dir
	 * 
	 * @param fileStack
	 */
	public static void clearDirs(Stack<File> fileStack) {
		if (fileStack == null) {
			return;
		}
		while (!fileStack.isEmpty()) {
			File top = fileStack.peek();
			File[] files = top.listFiles();
			if (files != null && files.length > 0) {
				for (File dirFile : files) {
					if (dirFile.isDirectory()) {
						fileStack.push(dirFile);
					} else {
						dirFile.setExecutable(true);
						dirFile.delete();
					}
				}
			} else {
				fileStack.pop().delete();
			}
		}
	}

	public static void moveTo(File src, File dest) {
		if (src.equals(dest)) {
			return;
		}
		moveTo(src, dest, src);
		DirsUtils.clearDirs(src);
		src.deleteOnExit();
	}

	private static void moveTo(File srcParent, File destParent, File copyFile) {
		if (copyFile.isFile()) {
			String destPath = copyFile.getAbsolutePath().replace(srcParent.getAbsolutePath(),
					destParent.getAbsolutePath());
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
					moveTo(srcParent, destParent, child);
				}
			}
		}
	}
}
