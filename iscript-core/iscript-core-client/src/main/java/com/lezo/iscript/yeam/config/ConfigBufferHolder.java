package com.lezo.iscript.yeam.config;

import com.lezo.iscript.common.buffer.StampBeanBuffer;

public class ConfigBufferHolder {

	private static final StampBeanBuffer<String> TOKEN_BUFFER = new StampBeanBuffer<String>();

	public static StampBeanBuffer<String> getTokenBuffer() {
		return TOKEN_BUFFER;
	}

}
