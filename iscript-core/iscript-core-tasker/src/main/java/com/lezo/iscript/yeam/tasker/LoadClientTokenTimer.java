package com.lezo.iscript.yeam.tasker;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.common.buffer.StampGetable;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;
import com.lezo.iscript.service.crawler.service.ClientTokenService;
import com.lezo.iscript.yeam.tasker.buffer.StampBufferHolder;

public class LoadClientTokenTimer {
	private static Logger log = Logger.getLogger(LoadClientTokenTimer.class);
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
			Date afterTime = new Date(stamp.get());
			List<ClientTokenDto> dtoList = clientTokenService.getClientTokenDtoByUpdateTime(afterTime);
			log.info("query stamp:" + stamp.get() + ",get size:" + dtoList.size());
			StampBeanBuffer<ClientTokenDto> tokenBuffer = StampBufferHolder.getClientTokenBuffer();
			tokenBuffer.addAll(dtoList, new StampGetable<ClientTokenDto>() {
				@Override
				public long getStamp(ClientTokenDto bean) {
					return bean.getUpdateTime().getTime();
				}

				@Override
				public String getName(ClientTokenDto bean) {
					return bean.getClientBucket() + "." + bean.getClientDomain();
				}
			});
			Date maxDate = getMaxDate(dtoList);
			if (maxDate != null) {
				stamp.set(maxDate.getTime());
			}
			long cost = System.currentTimeMillis() - startFlush;
			log.info("done.load client token.size:" + dtoList.size() + ",stamp:" + stamp.get() + ",cost:" + cost + "ms");
		} finally {
			running = false;
		}

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
