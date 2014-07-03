package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProductStatDao;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ProductStatServiceImpl implements ProductStatService {
	@Autowired
	private ProductStatDao productStatDao;

	@Override
	public void batchInsertProductStatDtos(List<ProductStatDto> dtoList) {
		BatchIterator<ProductStatDto> it = new BatchIterator<ProductStatDto>(dtoList);
		while (it.hasNext()) {
			productStatDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateProductStatDtos(List<ProductStatDto> dtoList) {
		BatchIterator<ProductStatDto> it = new BatchIterator<ProductStatDto>(dtoList);
		while (it.hasNext()) {
			productStatDao.batchUpdate(it.next());
		}
	}

	@Override
	public List<ProductStatDto> getProductStatDtos(List<String> codeList, Integer shopId) {
		List<ProductStatDto> dtoList = new ArrayList<ProductStatDto>();
		BatchIterator<String> it = new BatchIterator<String>(codeList);
		while (it.hasNext()) {
			List<ProductStatDto> subList = productStatDao.getProductStatDtos(it.next(), shopId);
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	public void setProductStatDao(ProductStatDao productStatDao) {
		this.productStatDao = productStatDao;
	}

}
