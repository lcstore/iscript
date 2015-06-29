package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProductDao;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ProductServiceImpl implements ProductService {
	private static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
	@Autowired
	private ProductDao productDao;

	@Override
	public void batchInsertProductDtos(List<ProductDto> dtoList) {
		BatchIterator<ProductDto> it = new BatchIterator<ProductDto>(dtoList);
		while (it.hasNext()) {
			productDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateProductDtos(List<ProductDto> dtoList) {
		BatchIterator<ProductDto> it = new BatchIterator<ProductDto>(dtoList);
		while (it.hasNext()) {
			List<ProductDto> subList = it.next();
			try {
				productDao.batchUpdate(subList);
			} catch (Exception e) {
				e.printStackTrace();
				for (ProductDto dto : subList) {
					logger.warn("code:{},url:{},onsail:{}", dto.getProductCode(), dto.getProductUrl(),
							dto.getOnsailTime());
				}
			}
		}
	}

	@Override
	public List<ProductDto> getProductDtos(List<String> codeList, Integer siteId) {
		List<ProductDto> dtoList = new ArrayList<ProductDto>();
		BatchIterator<String> it = new BatchIterator<String>(codeList);
		while (it.hasNext()) {
			List<ProductDto> subList = productDao.getProductDtos(it.next(), siteId);
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	@Override
	public List<ProductDto> getProductDtosFromId(Long fromId, int limit, Integer siteId) {
		return productDao.getProductDtosFromId(fromId, limit, siteId);
	}

	@Override
	public void batchSaveProductDtos(List<ProductDto> dtoList) {
		List<ProductDto> insertDtos = new ArrayList<ProductDto>();
		List<ProductDto> updateDtos = new ArrayList<ProductDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchInsertProductDtos(insertDtos);
		batchUpdateProductDtos(updateDtos);
		logger.info(String.format("save [%s],insert:%d,update:%d,cost:", "ProductDto", insertDtos.size(),
				updateDtos.size()));
	}

	private void doAssort(List<ProductDto> productDtos, List<ProductDto> insertDtos, List<ProductDto> updateDtos) {
		Map<Integer, Set<String>> shopMap = new HashMap<Integer, Set<String>>();
		Map<String, ProductDto> dtoMap = new HashMap<String, ProductDto>();
		for (ProductDto dto : productDtos) {
			String key = getDtoKey(dto);
			ProductDto hasDto = dtoMap.get(key);
			if (hasDto == null) {
				dtoMap.put(key, dto);
			} else if (hasDto.getUpdateTime().before(dto.getUpdateTime())) {
				dtoMap.put(key, dto);
			}
			Set<String> codeSet = shopMap.get(dto.getSiteId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				shopMap.put(dto.getSiteId(), codeSet);
			}
			codeSet.add(dto.getProductCode());
		}
		for (Entry<Integer, Set<String>> entry : shopMap.entrySet()) {
			List<ProductDto> hasDtos = getProductDtos(new ArrayList<String>(entry.getValue()), entry.getKey());
			Set<String> hasCodeSet = new HashSet<String>();
			for (ProductDto oldDto : hasDtos) {
				String key = getDtoKey(oldDto);
				ProductDto newDto = dtoMap.get(key);
				hasCodeSet.add(oldDto.getProductCode());
				convertFields(newDto, oldDto);
				updateDtos.add(newDto);
			}
			for (String code : entry.getValue()) {
				if (hasCodeSet.contains(code)) {
					continue;
				}
				String key = entry.getKey() + "-" + code;
				ProductDto newDto = dtoMap.get(key);
				insertDtos.add(newDto);
			}

		}

	}

	private String getDtoKey(ProductDto dto) {
		return dto.getSiteId() + "-" + dto.getProductCode();
	}

	private void convertFields(ProductDto newDto, ProductDto oldDto) {
		newDto.setId(oldDto.getId());
		if (BarCodeUtils.isBarCode(oldDto.getBarCode()) && !BarCodeUtils.isBarCode(newDto.getBarCode())) {
			newDto.setBarCode(oldDto.getBarCode());
		}
		if (newDto.getShopId() == null) {
			newDto.setShopId(oldDto.getShopId());
		}
	}

	@Override
	public List<ProductDto> getProductDtosByDateCateSiteId(Date fromCreateDate, Date toCreateDate, String sCategory,
			Integer siteId, Long fromId, int limit) {
		if (limit < 1 || fromCreateDate == null || toCreateDate == null) {
			return Collections.emptyList();
		}
		return productDao.getProductDtosByDateCateSiteId(fromCreateDate, toCreateDate, sCategory,
				siteId, fromId, limit);
	}
}
