package com.lezo.iscript.yeam.writable;

import java.io.Serializable;

public class ResultWritable implements Serializable {
	private static final long serialVersionUID = 3628691351919899205L;
	private Long taskId;
	private Long batchId;
	private int status;
	private String result;

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

}
