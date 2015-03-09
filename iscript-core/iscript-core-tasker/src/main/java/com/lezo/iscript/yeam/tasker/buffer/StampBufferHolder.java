package com.lezo.iscript.yeam.tasker.buffer;

import com.lezo.iscript.common.buffer.StampBeanBuffer;
import com.lezo.iscript.service.crawler.dto.ClientTokenDto;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.writable.ConfigWritable;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月6日
 */
public class StampBufferHolder {

	private static final StampBeanBuffer<ClientTokenDto> CLIENT_TOKEN_BUFFER = new StampBeanBuffer<ClientTokenDto>();
	private static final StampBeanBuffer<ConfigWritable> CONFIG_BUFFER = new StampBeanBuffer<ConfigWritable>();
	private static final StampBeanBuffer<ResultStrategy> STRATEGY_BUFFER = new StampBeanBuffer<ResultStrategy>();

	public static StampBeanBuffer<ClientTokenDto> getClientTokenBuffer() {
		return CLIENT_TOKEN_BUFFER;
	}

	public static StampBeanBuffer<ConfigWritable> getConfigBuffer() {
		return CONFIG_BUFFER;
	}

	public static StampBeanBuffer<ResultStrategy> getStrategyBuffer() {
		return STRATEGY_BUFFER;
	}
}
