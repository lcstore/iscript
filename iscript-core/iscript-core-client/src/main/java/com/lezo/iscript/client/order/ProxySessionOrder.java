package com.lezo.iscript.client.order;

import java.util.StringTokenizer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoOrder;
import com.lezo.iscript.yeam.mina.SessionSender;

public class ProxySessionOrder extends AbstractSessionOrder {
	private static final String HEAD_SPLIT = ": ";
	private static final String DEFAULT_CHARSET = "UTF-8";
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	@Override
	protected void doOrder(IoOrder ioOrder, IoSession ioSession) {
		StringTokenizer tokenizer = new StringTokenizer(ioOrder.getData().toString(), "\r\n");
		String urlString = tokenizer.nextToken();
		HttpUriRequest request = createRequest(urlString);
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();
			int index = line.indexOf(HEAD_SPLIT);
			if (index > 0) {
				String name = line.substring(0, index).trim();
				String value = line.substring(index + HEAD_SPLIT.length()).trim();
				request.addHeader(name, value);
			}
		}
		JSONObject rsObject = new JSONObject();
		try {
			HttpResponse resp = client.execute(request);
			StatusLine statusLine = resp.getStatusLine();
			JSONUtils.put(rsObject, "status", statusLine);
			JSONArray hArray = new JSONArray();
			for (Header header : resp.getAllHeaders()) {
				StringBuilder sb = new StringBuilder();
				sb.append(header.getName());
				sb.append(HEAD_SPLIT);
				sb.append(header.getValue());
				hArray.put(sb.toString());
			}
			JSONUtils.put(rsObject, "headers", hArray);

			HttpEntity entity = resp.getEntity();
			byte[] dataBytes = EntityUtils.toByteArray(entity);
			String charsetName = HttpClientUtils.getCharsetOrDefault(entity.getContentType(), dataBytes,
					DEFAULT_CHARSET);
			String html = new String(dataBytes, charsetName);
			JSONUtils.put(rsObject, "html", html);
		} catch (Exception e) {
			String errString = ExceptionUtils.getStackTrace(e);
			JSONUtils.put(rsObject, "cause", errString);
		}
		ioOrder.setOrder(-ioOrder.getOrder());
		ioOrder.setData(rsObject.toString());
		SessionSender.getInstance().send(ioOrder);
	}

	private HttpUriRequest createRequest(String urlString) {
		String[] unitArr = urlString.split("\\s");
		int index = -1;
		HttpUriRequest request = null;
		if ("GET".equals(unitArr[++index])) {
			String sUrl = unitArr[++index];
			request = new HttpGet(sUrl);
		} else if ("POST".equals(unitArr[++index])) {
			String sUrl = unitArr[++index];
			request = new HttpPost(sUrl);
		}
		return request;
	}

	@Override
	protected int getOrder() {
		return IoOrder.ORDER_REQUEST_PROXY;
	}

}
