package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ShopDto;

public interface ShopService {
	void batchInsertShopDtos(List<ShopDto> dtoList);

	ShopDto getShopDto(Integer shopId);

	/**
	 * 
	 * @param siteCode
	 * @return if siteCode==null,return all.
	 */
	List<ShopDto> getShopDtos(String siteCode);

}
