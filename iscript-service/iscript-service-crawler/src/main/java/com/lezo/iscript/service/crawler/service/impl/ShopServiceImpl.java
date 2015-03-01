package com.lezo.iscript.service.crawler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ShopDao;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.service.ShopService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ShopServiceImpl implements ShopService {
	@Autowired
	private ShopDao shopDao;

	@Override
	public void batchInsertShopDtos(List<ShopDto> dtoList) {
		BatchIterator<ShopDto> it = new BatchIterator<ShopDto>(dtoList);
		while (it.hasNext()) {
			shopDao.batchInsert(it.next());
		}
	}

	@Override
	public ShopDto getShopDto(Integer shopId) {
		if (shopId == null) {
			return null;
		}
		return shopDao.getShopDto(shopId);
	}

	@Override
	public List<ShopDto> getShopDtos(String siteCode) {
		return shopDao.getShopDtos(siteCode);
	}

	public void setShopDao(ShopDao shopDao) {
		this.shopDao = shopDao;
	}

}
