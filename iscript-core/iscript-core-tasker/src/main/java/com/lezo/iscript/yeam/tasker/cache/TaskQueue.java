package com.lezo.iscript.yeam.tasker.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.lezo.iscript.yeam.writable.TaskWritable;

public class TaskQueue {
	private String type;
	private ConcurrentHashMap<Integer, Queue<TaskWritable>> levelQueueMap = new ConcurrentHashMap<Integer, Queue<TaskWritable>>();
	private List<Integer> levelDescList = new ArrayList<Integer>();

	public TaskQueue(String type) {
		super();
	}

	public Queue<TaskWritable> getQueue(int level) {
		Queue<TaskWritable> queue = levelQueueMap.get(level);
		if (queue == null) {
			synchronized (levelQueueMap) {
				queue = levelQueueMap.get(level);
				if (queue == null) {
					queue = new LinkedBlockingQueue<TaskWritable>();
					levelQueueMap.put(level, queue);
					// add new level and sort leveList desc
					levelDescList.add(level);
					Collections.sort(levelDescList, new Comparator<Integer>() {
						@Override
						public int compare(Integer o1, Integer o2) {
							return o2.compareTo(o1);
						}
					});
				}
			}
		}
		return queue;
	}

	public boolean offer(TaskWritable task, int level) {
		Queue<TaskWritable> queue = getQueue(level);
		return queue.offer(task);
	}

	public boolean offer(List<TaskWritable> taskList, int level) {
		Queue<TaskWritable> queue = getQueue(level);
		for (TaskWritable task : taskList) {
			if (!queue.offer(task)) {
				return false;
			}
		}
		return true;
	}

	public TaskWritable poll(int level) {
		Queue<TaskWritable> queue = getQueue(level);
		return queue.poll();
	}

	public List<TaskWritable> poll(int level, int limit) {
		Queue<TaskWritable> queue = getQueue(level);
		List<TaskWritable> taskList = new ArrayList<TaskWritable>(limit);
		while (limit-- > 0) {
			TaskWritable task = queue.poll();
			if (task == null) {
				break;
			}
			taskList.add(task);
		}
		return taskList;
	}

	/**
	 * poll task by leve desc
	 * 
	 * @param limit
	 * @return
	 */
	public List<TaskWritable> pollDecsLevel(int limit) {
		List<TaskWritable> taskList = new ArrayList<TaskWritable>(limit);
		int remain = limit;
		for (Integer level : levelDescList) {
			if (remain < 1) {
				break;
			}
			List<TaskWritable> curLevelTasks = poll(level, remain);
			taskList.addAll(curLevelTasks);
			remain = limit - taskList.size();
		}
		return taskList;
	}

	public int size(int level) {
		return getQueue(level).size();
	}

	public int size() {
		int total = 0;
		for (Entry<Integer, Queue<TaskWritable>> entry : levelQueueMap.entrySet()) {
			total += entry.getValue().size();
		}
		return total;
	}

	public String getType() {
		return type;
	}

	public List<Integer> getLeveList() {
		return new ArrayList<Integer>(levelQueueMap.keySet());
	}

}
