package com.lezo.iscript.yeam.defend.dirs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class FileZipUtils {

	private static final int BUFFER = 2048;

	public static void doZip(File src, ZipEntry parent, ZipOutputStream out) throws IOException {
		if (src.isDirectory()) {
			String pName = (parent == null) ? "" : parent.getName();
			ZipEntry entry = new ZipEntry(pName + src.getName() + File.separator);
			File[] fileArr = src.listFiles();
			for (File file : fileArr) {
				doZip(file, entry, out);
			}
		} else {
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			String pName = (parent == null) ? "" : parent.getName();
			ZipEntry entry = new ZipEntry(pName + src.getName());
			try {
				fis = new FileInputStream(src);
				bis = new BufferedInputStream(fis, BUFFER);
				out.putNextEntry(entry);
				int count = 0;
				byte data[] = new byte[BUFFER];
				while ((count = bis.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				out.closeEntry();
				out.flush();
			} catch (IOException e) {
				throw e;
			} finally {
				IOUtils.closeQuietly(bis);
			}
		}
	}

	public static void doZip(File src, File dest) throws IOException {
		OutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(dest);
			zos = new ZipOutputStream(fos);
			doZip(src, null, zos);
			zos.finish();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(zos);
		}
	}

	public static void doUnZip(File src, File dest) throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ZipInputStream zis = null;
		try {
			fis = new FileInputStream(src);
			zis = new ZipInputStream(fis);
			// bis = new BufferedInputStream(fis, BUFFER);
			// ZipInputStream zis = new ZipInputStream(bis);
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				String name = entry.getName();
				File curFile = new File(dest, name);
				mkdirs(curFile);
				if (entry.isDirectory()) {
					continue;
				}
				unZipFile(zis, curFile);
				zis.closeEntry();
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(zis);
		}
	}

	private static void unZipFile(ZipInputStream in, File dest) throws IOException {
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(dest));
			int count = -1;
			byte[] buffer = new byte[BUFFER];
			while ((count = in.read(buffer)) != -1) {
				out.write(buffer, 0, count);
			}
			out.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(out);
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
}
