package com.lezo.iscript.common.loader;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceManager implements ResourceCacheable {
	private ConcurrentHashMap<String, ByteBuffer> resMap = new ConcurrentHashMap<String, ByteBuffer>();

	@Override
	public byte[] findResource(String name) {
		// com.lezo.iscript.yeam.compile.InClass$1
		ByteBuffer buffer = resMap.get(name);
		if (buffer == null) {
			int index = name.indexOf("$");
			if (index > 0) {
				String sName = name.substring(0, index);
				buffer = resMap.get(sName);
			}
		}
		return buffer == null ? null : buffer.array();
	}

	@Override
	public void addResource(String name, byte[] source) {
		ByteBuffer buffer = ByteBuffer.allocate(source.length);
		buffer.put(source);
		resMap.put(name, buffer);
	}

}
