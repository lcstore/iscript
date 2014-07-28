package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProductDao;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.service.ProductService;
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
			productDao.batchUpdate(it.next());
		}
	}

	@Override
	public List<ProductDto> getProductDtos(List<String> codeList, Integer shopId) {
		List<ProductDto> dtoList = new ArrayList<ProductDto>();
		BatchIterator<String> it = new BatchIterator<String>(codeList);
		while (it.hasNext()) {
			List<ProductDto> subList = productDao.getProductDtos(it.next(), shopId);
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
	public List<ProductDto> getProductDtosFromId(Long fromId, int limit, Integer shopId) {
		return productDao.getProductDtosFromId(fromId, limit, shopId);
	}

	@Override
	public void batchSaveProductDtos(List<ProductDto> dtoList) {
		List<ProductDto> insertDtos = new ArrayList<ProductDto>();
		List<ProductDto> updateDtos = new ArrayList<ProductDto>();
		doAssort(dtoList, insertDtos, updateDtos);
		batchInsertProductDtos(insertDtos);
		batchUpdateProductDtos(updateDtos);
		logger.info("save [%s],insert:%d,update:%d,cost:", "ProductDto", insertDtos.size(), updateDtos.size());
	}

	private void doAssort(List<ProductDto> productDtos, List<ProductDto> insertDtos, List<ProductDto> updateDtos) {
		Map<Integer, Set<String>> shopMap = new HashMap<Integer, Set<String>>();
		Map<String, ProductDto> dtoMap = new HashMap<String, ProductDto>();
		for (ProductDto dto : productDtos) {
			String key = dto.getShopId() + "-" + dto.getProductCode();
			dtoMap.put(key, dto);
			Set<String> codeSet = shopMap.get(dto.getShopId());
			if (codeSet == null) {
				codeSet = new HashSet<String>();
				shopMap.put(dto.getShopId(), codeSet);
			}
			codeSet.add(dto.getProductCode());
		}
		for (Entry<Integer, Set<String>> entry : shopMap.entrySet()) {
			List<ProductDto> hasDtos = getProductDtos(new ArrayList<String>(entry.getValue()), entry.getKey());
			Set<String> hasCodeSet = new HashSet<String>();
			for (ProductDto dto : hasDtos) {
				String key = dto.getShopId() + "-" + dto.getProductCode();
				ProductDto newDto = dtoMap.get(key);
				hasCodeSet.add(dto.getProductCode());
				newDto.setId(dto.getId());
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
}
