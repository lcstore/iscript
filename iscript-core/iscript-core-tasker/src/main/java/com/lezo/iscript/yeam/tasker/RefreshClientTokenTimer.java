package com.lezo.iscript.yeam.tasker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;
import com.lezo.iscript.service.crawler.service.ClientTokenService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.tasker.buffer.StampBufferHolder;

public class RefreshClientTokenTimer {
	private static final long INTEVAL_MILLS = 2 * 60 * 1000;
	private static final long AHEAD_MILLS = 5 * 60 * 1000;
	private static Logger log = Logger.getLogger(RefreshClientTokenTimer.class);
	private static volatile boolean running = false;
	private AtomicLong stamp = new AtomicLong(0);
	@Autowired
	private ClientTokenService clientTokenService;

	public void run() {
		if (running) {
			log.warn(this.getClass().getSimpleName() + " is working...");
			return;
		}
		try {
			running = true;
			long startFlush = System.currentTimeMillis();
			StampBeanBuffer<ClientTokenDto> tokenBuffer = StampBufferHolder.getClientTokenBuffer();
			Iterator<Entry<String, ClientTokenDto>> it = tokenBuffer.unmodifyIterator();
			Map<String, List<ClientTokenDto>> type2DtosMap = new HashMap<String, List<ClientTokenDto>>();
			int totalCount = 0;
			int refreshCount = 0;
			while (it.hasNext()) {
				totalCount++;
				Entry<String, ClientTokenDto> entry = it.next();
				ClientTokenDto dto = entry.getValue();
				if (!isTime2Refresh(dto)) {
					continue;
				}
				List<ClientTokenDto> sameList = type2DtosMap.get(dto.getClientType());
				if (sameList == null) {
					sameList = new ArrayList<ClientTokenDto>();
					type2DtosMap.put(dto.getClientType(), sameList);
				}
				sameList.add(dto);
				refreshCount++;
			}
			log.info("query totalCount:" + totalCount + ",refreshCount:" + refreshCount);
			List<ClientTokenDto> updateList = new ArrayList<ClientTokenDto>();
			for (Entry<String, List<ClientTokenDto>> entry : type2DtosMap.entrySet()) {
				refreshToken(entry.getKey(), entry.getValue());
				updateList.addAll(entry.getValue());
			}
			clientTokenService.batchUpdateDtos(updateList);
			long cost = System.currentTimeMillis() - startFlush;
			log.info("done.refresh token.size:" + updateList.size() + ",stamp:" + stamp.get() + ",cost:" + cost + "ms");
		} finally {
			running = false;
		}

	}

	private boolean isTime2Refresh(ClientTokenDto dto) {
		if (dto.getClientType().equals("qiniu.com")) {
			return false;
		}
		if (dto.getNextRefreshTime() == null) {
			return true;
		}
		if (System.currentTimeMillis() + INTEVAL_MILLS > dto.getNextRefreshTime().getTime()) {
			return true;
		}
		return false;
	}

	private void refreshToken(String clientType, List<ClientTokenDto> dtoList) {
		if (clientType.equals("baidu.com")) {
			refreshBaiduToken(dtoList);
		} else if (clientType.equals("qiniu.com")) {
			// no need to refresh token
		}
	}

	public static void main(String[] args) {
		List<ClientTokenDto> dtoList = new ArrayList<ClientTokenDto>();
		ClientTokenDto dto = new ClientTokenDto();
		dto.setClientType("pan.baidu.com");
		dto.setClientKey("UieQlNKz7omE7IvFQFLSWBIi");
		dto.setClientSecret("XF9kNymotK7x8qrhoZlFivAAjGL7qpQU");
		dto.setRefreshToken("22.eb4593d79722de761e081719fac93b2e.315360000.1741021772.4026763474-2920106");
		dtoList.add(dto);
		new RefreshClientTokenTimer().refreshBaiduToken(dtoList);
	}

	private void refreshBaiduToken(List<ClientTokenDto> dtoList) {
		CloseableHttpClient client = HttpClients.createDefault();
		for (ClientTokenDto dto : dtoList) {
			try {
				refreshBaiduToken(client, dto);
			} catch (Exception e) {
				log.warn("id:" + dto.getId() + ",cause:", e);
			}
		}
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void refreshBaiduToken(CloseableHttpClient client, ClientTokenDto dto) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("https://openapi.baidu.com/oauth/2.0/token?grant_type=refresh_token");
		sb.append("&refresh_token=");
		sb.append(dto.getRefreshToken());
		sb.append("&client_id=");
		sb.append(dto.getClientKey());
		sb.append("&client_secret=");
		sb.append(dto.getClientSecret());
		String url = sb.toString();
		HttpUriRequest post = new HttpPost(url);
		CloseableHttpResponse respone = client.execute(post);
		StatusLine statusLine = respone.getStatusLine();
		String content = EntityUtils.toString(respone.getEntity(), "UTF-8");
		dto.setLastMessge(content);
		if (statusLine.getStatusCode() == 200) {
			JSONObject jObject = JSONUtils.getJSONObject(content);
			dto.setAccessToken(JSONUtils.getString(jObject, "access_token"));
			dto.setRefreshToken(JSONUtils.getString(jObject, "refresh_token"));
			dto.setNextRefreshTime(new Date(System.currentTimeMillis() + JSONUtils.getLong(jObject, "expires_in") * 1000 - AHEAD_MILLS));
			dto.setSuccessCount(dto.getSuccessCount() + 1);
		} else {
			dto.setFailCount(dto.getFailCount() + 1);
			dto.setNextRefreshTime(new Date(System.currentTimeMillis() + AHEAD_MILLS));
		}
		respone.close();
	}
}
