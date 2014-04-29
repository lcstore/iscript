package com.lezo.iscript.yeam.tasker.service;

import java.util.List;

import com.lezo.iscript.yeam.tasker.dto.TypeConfigDto;

public interface TypeConfigService {
	List<TypeConfigDto> getEnableTypeConfigDtos(String tasker);
}
