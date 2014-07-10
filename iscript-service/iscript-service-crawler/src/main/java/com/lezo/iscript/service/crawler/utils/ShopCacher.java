package com.lezo.iscript.service.crawler.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.service.ShopService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ShopCacher {
	private static final ConcurrentHashMap<Integer, ShopDto> shopIdMap = new ConcurrentHashMap<Integer, ShopDto>();
	private static final ConcurrentHashMap<String, ShopDto> shopKeyMap = new ConcurrentHashMap<String, ShopDto>();
	private static final ConcurrentHashMap<String, ShopDto> domainMap = new ConcurrentHashMap<String, ShopDto>();
	private static Logger logger = LoggerFactory.getLogger(ShopCacher.class);
	private static ShopCacher instance;

	public ShopCacher() {
	}

	public static ShopCacher getInstance() {
		if (instance == null) {
			synchronized (ShopCacher.class) {
				if (instance == null) {
					instance = new ShopCacher();
					loadShopMap();
				}
			}
		}
		return instance;
	}

	private static void loadShopMap() {
		ShopService shopService = SpringBeanUtils.getBean(ShopService.class);
		List<ShopDto> dtoList = shopService.getShopDtos(null);
		for (ShopDto dto : dtoList) {
			shopIdMap.put(dto.getId(), dto);
			shopKeyMap.put(getSiteCodeNameKey(dto.getSiteCode(), dto.getShopName()), dto);
			if (dto.getIsSelf().equals(1)) {
				domainMap.put(dto.getSiteCode(), dto);
			}
		}
	}

	private static String getSiteCodeNameKey(String siteCode, String shopName) {
		shopName = shopName == null ? "" : shopName;
		siteCode = siteCode == null ? "" : siteCode;
		return siteCode + "." + shopName;
	}

	public ShopDto getDomainShopDto(String domainUrl) {
		String siteCode = getSiteCodeFromUrl(domainUrl);
		ShopDto dto = domainMap.get(siteCode);
		if (dto == null) {
			logger.warn("can not found domain from Url:" + domainUrl);
		}
		return dto;
	}

	public ShopDto getShopDto(Integer shopId) {
		if (shopId == null) {
			return null;
		}
		ShopDto curShopDto = shopIdMap.get(shopId);
		if (curShopDto == null) {
			logger.warn("can not found shop[" + shopId + "].reload shop info..");
			synchronized (this) {
				loadShopMap();
			}
			curShopDto = shopIdMap.get(shopId);
			if (curShopDto == null) {
				logger.error("can not found shop[" + shopId + "].after reload..");
			}
		}
		return curShopDto;
	}

	public ShopDto getShopDto(String name, String shopUrl) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		String siteCode = getSiteCodeFromUrl(shopUrl);
		String key = getSiteCodeNameKey(siteCode, name);
		ShopDto curShopDto = shopKeyMap.get(key);
		if (curShopDto == null) {
			logger.warn("can not found shop[" + name + "].reload shop info..");
			synchronized (this) {
				loadShopMap();
			}
			curShopDto = shopKeyMap.get(key);
			if (curShopDto == null) {
				logger.error("can not found shop[" + name + "].after reload..");
			}
		}
		return curShopDto;
	}

	public ShopDto insertIfAbsent(String shopName, String shopUrl, String shopCode) {
		if (StringUtils.isEmpty(shopName)) {
			logger.warn("empty shopName.shopUrl:" + shopUrl + ",shopCode:" + shopCode);
			return null;
		}
		String siteCode = getSiteCodeFromUrl(shopUrl);
		String key = getSiteCodeNameKey(siteCode, shopName);
		ShopDto curShopDto = shopKeyMap.get(key);
		if (curShopDto == null) {
			logger.warn("can not found shop[" + shopName + "].insert and reload..");
			synchronized (this) {
				insertDto(shopName, shopUrl, shopCode);
				loadShopMap();
			}
			curShopDto = shopKeyMap.get(key);
			if (curShopDto == null) {
				logger.error("can not found shop[" + shopName + "].after reload..");
			}
		}
		return curShopDto;
	}

	private void insertDto(String shopName, String shopUrl, String shopCode) {
		ShopDto dto = new ShopDto();
		dto.setShopCode(shopCode);
		dto.setShopName(shopName);
		dto.setShopUrl(shopUrl);
		dto.setSiteCode(getSiteCodeFromUrl(shopUrl));
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getCreateTime());
		ShopService shopService = SpringBeanUtils.getBean(ShopService.class);
		List<ShopDto> dtoList = new ArrayList<ShopDto>();
		dtoList.add(dto);
		shopService.batchInsertShopDtos(dtoList);
	}

	private String getSiteCodeFromUrl(String shopUrl) {
		try {
			// http://www.gome.com.cn/product/A0004331780.html&version=3.2.1&vendor=chrome
			URL newUrl = new URL(shopUrl);
			String host = newUrl.getHost();
			int fromIndex = host.indexOf('.');
			int index = fromIndex < 0 ? 0 : fromIndex + 1;
			return host.substring(index);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}