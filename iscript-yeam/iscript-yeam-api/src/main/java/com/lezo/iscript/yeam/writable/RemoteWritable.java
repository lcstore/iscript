package com.lezo.iscript.yeam.writable;

import java.io.Serializable;
import java.util.List;

public class RemoteWritable<E extends Serializable> implements Serializable {
	private static final long serialVersionUID = 6521572348967065286L;
	private int status;
	private List<E> storageList;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<E> getStorageList() {
		return storageList;
	}

	public void setStorageList(List<E> storageList) {
		this.storageList = storageList;
	}

}
