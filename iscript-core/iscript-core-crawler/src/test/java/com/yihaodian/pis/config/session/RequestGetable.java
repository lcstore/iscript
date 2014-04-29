package com.yihaodian.pis.config.session;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

public interface RequestGetable {
	HttpUriRequest nextRequest(HttpClient client, HttpUriRequest request) throws Exception;
}
