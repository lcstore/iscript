package com.lezo.iscript.yeam.server.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.server.HeadCacher;
import com.lezo.iscript.yeam.server.SendUtils;
import com.lezo.iscript.yeam.tasker.buffer.StampBufferHolder;

public class IoTokenHandler implements MessageHandler {
	private static final String KEY_CAPACITY = "capacity";
	private Logger logger = org.slf4j.LoggerFactory.getLogger(IoTokenHandler.class);

	public void handleMessage(IoSession session, Object message) {
		IoRequest ioRequest = (IoRequest) message;
		if (ioRequest == null) {
			return;
		}
		ensureTokenLoaded();
		String header = ioRequest.getHeader();
		pushTokens(JSONUtils.getJSONObject(header), session);
	}

	private void ensureTokenLoaded() {
		StampBeanBuffer<ClientTokenDto> tokenBuffer = StampBufferHolder.getClientTokenBuffer();
		Long stamp = tokenBuffer.getBufferStamp();
		long timeout = 1000;
		while (stamp == 0) {
			logger.warn("wait to buffer token...");
			try {
				TimeUnit.MILLISECONDS.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stamp = tokenBuffer.getBufferStamp();
		}
	}

	private void pushTokens(JSONObject hObject, IoSession ioSession) {
		Long tokenStamp = JSONUtils.getLong(hObject, "tokenStamp");
		tokenStamp = tokenStamp == null ? 0 : tokenStamp;
		StampBeanBuffer<ClientTokenDto> tokenBuffer = StampBufferHolder.getClientTokenBuffer();
		Long bufferStamp = tokenBuffer.getBufferStamp();
		if (bufferStamp.equals(tokenStamp)) {
			return;
		}
		if (!HeadCacher.getInstace().putIfVary(IoConstant.EVENT_TYPE_TOKEN, hObject)) {
			return;
		}
		List<String> tokenList = new ArrayList<String>();
		Iterator<Entry<String, ClientTokenDto>> it = tokenBuffer.unmodifyIterator();
		while (it.hasNext()) {
			ClientTokenDto config = it.next().getValue();
			if (config.getUpdateTime().getTime() > tokenStamp) {
				JSONObject tokenObject = new JSONObject();
				JSONUtils.put(tokenObject, "type", config.getClientType());
				JSONUtils.put(tokenObject, "bucket", config.getClientBucket());
				JSONUtils.put(tokenObject, "domain", config.getClientDomain());
				if (1 != config.getIsDelete()) {
					JSONUtils.put(tokenObject, "key", config.getClientKey());
					JSONUtils.put(tokenObject, "secret", config.getClientSecret());
					JSONUtils.put(tokenObject, "token", config.getAccessToken());
					JSONObject paramObject = JSONUtils.getJSONObject(config.getClientParams());
					Integer capacity = 1;
					if (paramObject != null && paramObject.has("capacity")) {
						capacity = JSONUtils.getInteger(paramObject, KEY_CAPACITY);
						capacity = (capacity == null || capacity < 1) ? 1 : capacity;
					}
					JSONUtils.put(tokenObject, KEY_CAPACITY, capacity);
				}
				JSONUtils.put(tokenObject, "isDelete", config.getIsDelete());
				JSONUtils.put(tokenObject, "stamp", config.getUpdateTime().getTime());
				tokenList.add(tokenObject.toString());
			}
		}
		IoRespone ioRespone = new IoRespone();
		ioRespone.setType(IoConstant.EVENT_TYPE_TOKEN);
		ioRespone.setData(tokenList);
		SendUtils.doSend(hObject, ioRespone, ioSession);
	}
}
