package com.lezo.iscript.yeam.client.event;

import java.util.ArrayList;
import java.util.List;

import com.lezo.iscript.yeam.client.listener.CallEventListener;
import com.lezo.iscript.yeam.client.listener.DoingTaskListener;
import com.lezo.iscript.yeam.client.listener.DoneTaskListener;
import com.lezo.iscript.yeam.client.listener.FetchTaskListener;
import com.lezo.iscript.yeam.client.listener.SubmitTaskListener;

public class EventManager {
	private List<CallEventListener> listeners = new ArrayList<CallEventListener>();
	private static EventManager instance = new EventManager();

	private EventManager() {
		addListener(new FetchTaskListener());
		addListener(new SubmitTaskListener());
		addListener(new DoingTaskListener());
		addListener(new DoneTaskListener());
	}

	public static EventManager getInstance() {
		return instance;
	}

	public void addListener(CallEventListener listener) {
		listeners.add(listener);
	}

	public void notifyEvent(CallEvent event) {
		for (CallEventListener listener : listeners) {
			listener.handleEvent(event);
		}
	}

	private CallEvent newEvent(Object srcObject, int type) {
		return null;
	}
}
