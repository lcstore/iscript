package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.TypeConfigDto;

public interface TypeConfigService {
	List<TypeConfigDto> getEnableTypeConfigDtos(String tasker);
}
