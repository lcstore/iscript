package com.lezo.iscript.yeam.resultmgr;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileBuffer {
	private static final FileBuffer INSTANCE = new FileBuffer();
	private BufferedWriter bufferedWriter;

	private FileBuffer() {
		try {
			this.bufferedWriter = new BufferedWriter(new FileWriter("src/main/resources/result.track.log", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static FileBuffer getInstance() {
		return INSTANCE;
	}

	public BufferedWriter getBufferedWriter() {
		return bufferedWriter;
	}

}
