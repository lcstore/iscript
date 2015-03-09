package com.lezo.rest.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections4.CollectionUtils;

public class ClientRestFactory {
	private ConcurrentHashMap<String, ClientRest> clientRestMap = new ConcurrentHashMap<String, ClientRest>();
	private List<ClientRest> clientRestList = new ArrayList<ClientRest>();
	private AtomicBoolean modify = new AtomicBoolean(false);

	private ClientRestFactory() {

	}

	private static final class InstanceHolder {
		private static final ClientRestFactory INSTANCE = new ClientRestFactory();
	}

	public static ClientRestFactory getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void put(ClientRest clientRest) {
		if (clientRest == null) {
			throw new IllegalArgumentException("ClientRest can not be null.");
		}
		String key = clientRest.getBucket() + "." + clientRest.getDomain();
		clientRestMap.put(key, clientRest);
		modify.set(true);
	}

	public ClientRest get(String bucket, String domain) {
		String key = bucket + "." + domain;
		return clientRestMap.get(key);
	}

	public ClientRest get(String bucket, String domain, int retryCount, long sleepMills) {
		String key = bucket + "." + domain;
		ClientRest rest = null;
		while (rest == null && retryCount-- > 0) {
			rest = clientRestMap.get(key);
			if (rest == null && retryCount > 0) {
				try {
					TimeUnit.MILLISECONDS.sleep(sleepMills);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return rest;
	}

	public void remove(String bucket, String domain) {
		String key = bucket + "." + domain;
		clientRestMap.remove(key);
		modify.set(true);
	}

	public Iterator<Entry<String, ClientRest>> unmodifyIterator() {
		Collection<Entry<String, ClientRest>> unmodifyList = CollectionUtils.unmodifiableCollection(clientRestMap.entrySet());
		return unmodifyList.iterator();
	}

	public ClientRest getRandom() {
		if (modify.get()) {
			synchronized (this) {
				if (modify.get()) {
					clientRestList.clear();
					for (Entry<String, ClientRest> entry : clientRestMap.entrySet()) {
						for (int i = 0; i < entry.getValue().getCapacity(); i++) {
							clientRestList.add(entry.getValue());
						}
					}
					Collections.shuffle(clientRestList);
					modify.set(false);
				}
			}
		}
		Random rand = new Random();
		int index = rand.nextInt(clientRestList.size());
		return clientRestList.get(index);
	}
}
