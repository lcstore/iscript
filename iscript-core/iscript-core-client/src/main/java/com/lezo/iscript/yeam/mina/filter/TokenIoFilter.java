package com.lezo.iscript.yeam.mina.filter;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.rest.data.BaiduPcsRester;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;
import com.lezo.rest.data.QiniuRester;
import com.qiniu.api.auth.digest.Mac;

public class TokenIoFilter extends IoFilterAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(TokenIoFilter.class);

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		IoRespone ioRespone = (IoRespone) message;
		if (IoConstant.EVENT_TYPE_TOKEN == ioRespone.getType()) {
			updateToken(ioRespone);
		} else {
			nextFilter.messageReceived(session, message);
		}
	}

	private void updateToken(IoRespone ioRespone) {
		List<String> tokenList = getDataList(ioRespone);
		if (CollectionUtils.isEmpty(tokenList)) {
			return;
		}
		ClientRestFactory factory = ClientRestFactory.getInstance();
		int size = tokenList.size();
		long maxStamp = 0;
		int removeCount = 0;
		int putCount = 0;
		for (int i = 0; i < size; i++) {
			JSONObject tokenObject = JSONUtils.getJSONObject(tokenList.get(i));
			Integer isDelete = JSONUtils.getInteger(tokenObject, "isDelete");
			Long stamp = JSONUtils.getLong(tokenObject, "stamp");
			maxStamp = stamp < maxStamp ? maxStamp : stamp;
			if (1 == isDelete) {
				String bucket = JSONUtils.getString(tokenObject, "bucket");
				String domain = JSONUtils.getString(tokenObject, "domain");
				factory.remove(bucket, domain);
				removeCount++;
			} else {
				ClientRest rest = convertToClientRest(tokenObject);
				if (rest != null) {
					factory.put(rest);
					putCount++;
				}
			}
		}
		JSONUtils.put(HeaderUtils.getHeader(), "tokenStamp", maxStamp);
		logger.info("finish to update token. tokenStamp:" + maxStamp + ",updateCount:" + tokenList.size() + ",removeCount:" + removeCount + ",putCount:" + putCount);

	}

	private ClientRest convertToClientRest(JSONObject tokenObject) {
		String bucket = JSONUtils.getString(tokenObject, "bucket");
		String domain = JSONUtils.getString(tokenObject, "domain");
		Integer capacity = JSONUtils.getInteger(tokenObject, "capacity");
		ClientRest clientRest = new ClientRest();
		clientRest.setBucket(bucket);
		clientRest.setDomain(domain);
		clientRest.setCapacity(capacity);
		String type = JSONUtils.getString(tokenObject, "type");
		if ("baidu.com".equals(type)) {
			BaiduPcsRester rester = new BaiduPcsRester();
			rester.setAccessToken(JSONUtils.getString(tokenObject, "token"));
			rester.setBucket(bucket);
			rester.setDomain(domain);
			rester.setClient(HttpClientManager.getDefaultHttpClient());
			clientRest.setRester(rester);

		} else if ("qiniu.com".equals(type)) {
			QiniuRester rester = new QiniuRester();
			rester.setBucket(bucket);
			rester.setDomain(domain);
			rester.setClient(HttpClientManager.getDefaultHttpClient());
			rester.setMac(new Mac(JSONUtils.getString(tokenObject, "key"), JSONUtils.getString(tokenObject, "secret")));
			clientRest.setRester(rester);
		} else {
			logger.warn("unknown.tokenObject:" + tokenObject);
			return null;
		}
		return clientRest;
	}

	@SuppressWarnings("unchecked")
	private List<String> getDataList(IoRespone ioRespone) {
		try {
			return (List<String>) ioRespone.getData();
		} catch (Exception e) {
			logger.warn("can not cast data to config.", e);
		}
		return Collections.emptyList();
	}

}
