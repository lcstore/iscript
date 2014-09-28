package com.lezo.iscript.yeam.file;

import org.json.JSONObject;

import com.lezo.iscript.common.BufferObjectWriter;

public class PersistentCollector {
	private static final PersistentCollector INSTANCE = new PersistentCollector();
	private BufferObjectWriter<JSONObject> bufferWriter = new BufferObjectWriter<JSONObject>(new PersistentWriter(), 20);

	private PersistentCollector() {

	}

	public static PersistentCollector getInstance() {
		return INSTANCE;
	}

	public BufferObjectWriter<JSONObject> getBufferWriter() {
		return bufferWriter;
	}
}
