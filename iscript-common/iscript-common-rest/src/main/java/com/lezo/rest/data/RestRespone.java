package com.lezo.rest.data;

import org.apache.http.HttpResponse;

public class RestRespone {
	private HttpResponse response;
	private Exception exception;

	public HttpResponse getResponse() {
		return response;
	}
	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
