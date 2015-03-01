package com.yihaodian.pis.config.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

public class LoginSimulator {
	List<RequestGetable> requestLink = new ArrayList<RequestGetable>();
	public static final Map<String, Object> globalMap = new HashMap<String, Object>();
	public void addRequest(RequestGetable e) {
		requestLink.add(e);
	}

	public HttpUriRequest doLogin(HttpClient client, HttpUriRequest request) throws Exception {
		HttpUriRequest next = request;
		for (RequestGetable get : requestLink) {
			next = get.nextRequest(client, next);
		}
		return next;
	}
}
