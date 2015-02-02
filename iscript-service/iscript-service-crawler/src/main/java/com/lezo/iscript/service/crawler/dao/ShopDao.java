package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ShopDto;

public interface ShopDao {
	void batchInsert(List<ShopDto> dtoList);

	ShopDto getShopDto(Integer shopId);

	List<ShopDto> getShopDtos(String siteCode);
}
