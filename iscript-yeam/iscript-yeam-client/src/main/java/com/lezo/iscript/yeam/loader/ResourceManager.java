package com.lezo.iscript.yeam.loader;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceManager implements ResourceCacheable {
	private ConcurrentHashMap<String, ByteBuffer> resMap = new ConcurrentHashMap<String, ByteBuffer>();

	@Override
	public byte[] findResource(String name) {
		ByteBuffer buffer = resMap.get(name);
		if (buffer == null) {
			return null;
		}
		return buffer.array();
	}

	@Override
	public void addResource(String name, byte[] source) {
		ByteBuffer buffer = ByteBuffer.allocate(source.length);
		buffer.put(source);
		resMap.put(name, buffer);
	}

}
