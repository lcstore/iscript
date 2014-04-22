package com.lezo.iscript.yeam.loader;

public interface ResourceCacheable {
	byte[] findResource(final String name);

	void addResource(final String name, byte[] source);
}
