package com.lezo.iscript.yeam.io;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

	public static void close(Closeable closeable) throws IOException {
		if (closeable != null) {
			closeable.close();
		}
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			close(closeable);
		} catch (IOException e) {
		}
	}
}
