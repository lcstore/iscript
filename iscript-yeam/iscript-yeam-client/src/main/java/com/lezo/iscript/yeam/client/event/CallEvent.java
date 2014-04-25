package com.lezo.iscript.yeam.client.event;

import java.util.EventObject;

public class CallEvent extends EventObject {
	private static final long serialVersionUID = -930817307671196467L;
	public static final int FETCH_TASK_EVENT = 0;
	public static final int SUBMIT_TASK_EVENT = 1;
	public static final int DOING_TASK_EVENT = 2;
	public static final int DONE_TASK_EVENT = 3;
	private int type;

	public CallEvent(Object source, int type) {
		super(source);
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
