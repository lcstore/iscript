package com.lezo.iscript.yeam.property;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lezo.iscript.spring.context.SpringBeanUtils;

@Data
@Component
public class GlobalProperties {
	private static GlobalProperties instance;
	@Value("${port}")
	private int port;
	@Value("${proxy_port}")
	private int proxyPort;
	@Value("${agent_path}")
	private String agentPath;
	@Value("${min_task_size}")
	private int minTaskSize;
	@Value("${per_offer_size}")
	private int perOfferSize;

	public static GlobalProperties getInstance() {
		if (instance == null) {
			synchronized (GlobalProperties.class) {
				if (instance == null) {
					instance = SpringBeanUtils.getBean(GlobalProperties.class);
				}
			}
		}
		return instance;
	}

	public int getMinTaskSize() {
		return minTaskSize;
	}
}
