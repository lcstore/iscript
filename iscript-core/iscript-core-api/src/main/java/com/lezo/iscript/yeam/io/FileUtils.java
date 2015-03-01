package com.lezo.iscript.yeam.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

	public static byte[] readBytes(File file) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int len = 0;
			// len != bytes.length
			while ((len = in.read(bytes)) > 0) {
				bos.write(bytes, 0, len);
			}
			bos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new ClassNotFoundException("class[" + file + "] not found.", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(bos);
		}
	}

	public static void writeBytes(File file, byte[] bytes) throws Exception {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(bytes);
			os.flush();
		} catch (IOException e) {
			throw new ClassNotFoundException("class[" + file + "] not found.", e);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
}
