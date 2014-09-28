package com.lezo.iscript.service.crawler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProductStatHisDao;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.service.ProductStatHisService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ProductStatHisServiceImpl implements ProductStatHisService {
	@Autowired
	private ProductStatHisDao productStatHisDao;

	@Override
	public void batchInsertProductStatHisDtos(List<ProductStatDto> dtoList) {
		BatchIterator<ProductStatDto> it = new BatchIterator<ProductStatDto>(dtoList);
		while (it.hasNext()) {
			productStatHisDao.batchInsert(it.next());
		}
	}

	public void setProductStatHisDao(ProductStatHisDao productStatHisDao) {
		this.productStatHisDao = productStatHisDao;
	}

}
