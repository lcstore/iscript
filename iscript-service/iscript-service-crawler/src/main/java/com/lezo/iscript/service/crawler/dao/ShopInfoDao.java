package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ShopInfoDto;

public interface ShopInfoDao {
	void batchInsert(List<ShopInfoDto> dtoList);

	ShopInfoDto getShopInfo(Integer shopId);
}
