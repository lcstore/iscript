package com.lezo.iscript.yeam.tasker.handle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.tasker.buffer.TaskBuffer;
import com.lezo.iscript.yeam.tasker.buffer.TaskBuffer;
import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class GetTaskHandler implements GetMoreHandle {

	@Override
	public boolean isAccept(ClientWritable client) {
		return true;
	}

	@Override
	public RemoteWritable<?> createWritable(ClientWritable client) throws IOException {
		RemoteWritable<TaskWritable> remoteWritable = new RemoteWritable<TaskWritable>();
		if (!isAccept(client)) {
			remoteWritable.setStatus(ClientConstant.GET_NONE);
			return remoteWritable;
		}
		remoteWritable.setStatus(getStatus());
		List<TaskWritable> taskWritables = TaskBuffer.getInstance().poll();
		if (taskWritables == null) {
			remoteWritable.setStorageList(new ArrayList<TaskWritable>(0));
		} else {
			remoteWritable.setStorageList(taskWritables);
		}
		return remoteWritable;
	}

	@Override
	public Integer getStatus() {
		return ClientConstant.GET_TASK;
	}

}
