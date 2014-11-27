package com.lezo.iscript.service.crawler.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.TypeConfigDao;
import com.lezo.iscript.service.crawler.dto.TypeConfigDto;
import com.lezo.iscript.service.crawler.service.TypeConfigService;

@Service
public class TypeConfigServiceImpl implements TypeConfigService {
	@Autowired
	private TypeConfigDao typeConfigDao;

	@Override
	public List<TypeConfigDto> getTypeConfigDtos(String tasker, Integer status) {
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
