package com.lezo.iscript.yeam.buffer;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;
import com.lezo.iscript.service.crawler.service.ClientTokenService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.rest.data.BaiduPcsRester;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;
import com.lezo.rest.data.QiniuRester;
import com.qiniu.api.auth.digest.Mac;

public class TokenToRestTimer {
	private static Logger logger = Logger.getLogger(TokenToRestTimer.class);
	private static volatile boolean running = false;
	private AtomicLong stamp = new AtomicLong(0);
	@Autowired
	private ClientTokenService clientTokenService;

	public void run() {
		if (running) {
			logger.warn(this.getClass().getSimpleName() + " is working...");
			return;
		}
		try {
			running = true;
			long startFlush = System.currentTimeMillis();
			Date afterTime = new Date(stamp.get());
			List<ClientTokenDto> dtoList = clientTokenService.getClientTokenDtoByUpdateTime(afterTime);
			logger.info("query stamp:" + stamp.get() + ",get size:" + dtoList.size());
			ClientRestFactory factory = ClientRestFactory.getInstance();
			int removeCount = 0;
			int addCount = 0;
			for (ClientTokenDto dto : dtoList) {
				if (1 == dto.getIsDelete()) {
					factory.remove(dto.getClientBucket(), dto.getClientDomain());
					removeCount++;
				} else {
					ClientRest clientRest = convertToClientRest(dto);
					factory.put(clientRest);
					addCount++;
				}
			}
			Date maxDate = getMaxDate(dtoList);
			if (maxDate != null) {
				stamp.set(maxDate.getTime());
			}
			long cost = System.currentTimeMillis() - startFlush;
			logger.info("done.TokenToRest.size:" + dtoList.size() + ",removeCount:" + removeCount + ",addCount:" + addCount + ",stamp:" + stamp.get() + ",cost:" + cost + "ms");
		} finally {
			running = false;
		}

	}

	private ClientRest convertToClientRest(ClientTokenDto dto) {
		String bucket = dto.getClientBucket();
		String domain = dto.getClientDomain();
		JSONObject paramObject = StringUtils.isEmpty(dto.getClientParams()) ? null : JSONUtils.getJSONObject(dto.getClientParams());
		Integer capacity = paramObject == null ? 1 : JSONUtils.getInteger(paramObject, "capacity");
		capacity = capacity == null ? 1 : capacity;
		ClientRest clientRest = new ClientRest();
		clientRest.setBucket(bucket);
		clientRest.setDomain(domain);
		clientRest.setCapacity(capacity);
		String type = dto.getClientType();
		if ("baidu.com".equals(type)) {
			BaiduPcsRester rester = new BaiduPcsRester();
			rester.setAccessToken(dto.getAccessToken());
			rester.setBucket(bucket);
			rester.setDomain(domain);
			rester.setClient(HttpClientManager.getDefaultHttpClient());
			clientRest.setRester(rester);

		} else if ("qiniu.com".equals(type)) {
			QiniuRester rester = new QiniuRester();
			rester.setBucket(bucket);
			rester.setDomain(domain);
			rester.setClient(HttpClientManager.getDefaultHttpClient());
			rester.setMac(new Mac(dto.getClientKey(), dto.getClientSecret()));
			clientRest.setRester(rester);
		} else {
			logger.warn("unknown.id:" + dto.getId());
			return null;
		}
		return clientRest;
	}

	private Date getMaxDate(List<ClientTokenDto> dtoList) {
		if (CollectionUtils.isEmpty(dtoList)) {
			return null;
		}
		Date maxDate = null;
		for (ClientTokenDto dto : dtoList) {
			if (maxDate == null || maxDate.before(dto.getUpdateTime())) {
				maxDate = dto.getUpdateTime();
			}
		}
		return maxDate;
	}
}
