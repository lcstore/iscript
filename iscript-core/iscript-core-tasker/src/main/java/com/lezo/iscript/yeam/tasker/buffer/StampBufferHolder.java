package com.lezo.iscript.yeam.tasker.buffer;

import com.lezo.iscript.service.crawler.dto.ClientTokenDto;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月6日
 */
public class StampBufferHolder {

	private static final StampBeanBuffer<ClientTokenDto> CLIENT_TOKEN_BUFFER = new StampBeanBuffer<ClientTokenDto>();

	public static StampBeanBuffer<ClientTokenDto> getClientTokenBuffer() {
		return CLIENT_TOKEN_BUFFER;
	}
}
