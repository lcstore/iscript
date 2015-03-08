package com.lezo.rest.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;

public class ClientRestFactory {
	private ConcurrentHashMap<String, ClientRester> clientRestMap = new ConcurrentHashMap<String, ClientRester>();

	private ClientRestFactory() {

	}

	private static final class InstanceHolder {
		private static final ClientRestFactory INSTANCE = new ClientRestFactory();
	}

	public static ClientRestFactory getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public synchronized void put(ClientRester clientRester) {
		String key = clientRester.getBucket() + "." + clientRester.getDomain();
		clientRestMap.put(key, clientRester);
	}

	public void get(String bucket, String domain) {
		String key = bucket + "." + domain;
		clientRestMap.get(key);
	}

	public synchronized void remove(String bucket, String domain) {
		String key = bucket + "." + domain;
		clientRestMap.remove(key);
	}

	public Iterator<Entry<String, ClientRester>> unmodifyIterator() {
		Collection<Entry<String, ClientRester>> unmodifyList = CollectionUtils.unmodifiableCollection(clientRestMap.entrySet());
		return unmodifyList.iterator();
	}
}
