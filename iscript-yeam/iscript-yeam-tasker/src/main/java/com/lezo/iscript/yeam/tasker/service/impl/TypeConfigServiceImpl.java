package com.lezo.iscript.yeam.tasker.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.yeam.tasker.dao.TypeConfigDao;
import com.lezo.iscript.yeam.tasker.dto.TypeConfigDto;
import com.lezo.iscript.yeam.tasker.service.TypeConfigService;

@Service
public class TypeConfigServiceImpl implements TypeConfigService {
	@Autowired
	private TypeConfigDao typeConfigDao;

	@Override
	public List<TypeConfigDto> getEnableTypeConfigDtos(String tasker) {
		int status = TypeConfigDto.TYPE_ENABLE;
		List<TypeConfigDto> commonList = typeConfigDao.getTypeConfigDtos(TypeConfigDto.TASKER_COMMON, status);
		List<TypeConfigDto> typeList = typeConfigDao.getTypeConfigDtos(tasker, status);
		Map<String, TypeConfigDto> typeMap = new HashMap<String, TypeConfigDto>();
		for (TypeConfigDto dto : typeList) {
			typeMap.put(dto.getType(), dto);
		}
		for (TypeConfigDto dto : commonList) {
			if (!typeMap.containsKey(dto.getType())) {
				typeList.add(dto);
			}
		}
		return typeList;
	}

}
