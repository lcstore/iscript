package com.lezo.iscript.yeam.client.result;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

public class SubmitFuture {
	private int submitSize;
	private Date fromDate;
	private Date toDate;
	private Future<List<Long>> future;

	public int getSubmitSize() {
		return submitSize;
	}

	public void setSubmitSize(int submitSize) {
		this.submitSize = submitSize;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Future<List<Long>> getFuture() {
		return future;
	}

	public void setFuture(Future<List<Long>> future) {
		this.future = future;
	}
}
