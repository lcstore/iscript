package com.lezo.iscript.yeam.io;

import java.io.Serializable;

public class IoOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int ORDER_REQUEST_PROXY = 1;
	public static final int ORDER_RESPONE_PROXY = -ORDER_REQUEST_PROXY;
	private int order;
	private String id;
	private Object data;

	public IoOrder(int order, String id) {
		this(order, id, null);
	}

	public IoOrder(int order, String id, Object data) {
		super();
		this.order = order;
		this.id = id;
		this.data = data;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
