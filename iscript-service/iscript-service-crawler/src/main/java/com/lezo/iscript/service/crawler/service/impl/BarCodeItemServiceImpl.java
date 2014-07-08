package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.BarCodeItemDao;
import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.service.BarCodeItemService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class BarCodeItemServiceImpl implements BarCodeItemService {
	@Autowired
	private BarCodeItemDao barCodeItemDao;

	@Override
	public void batchInsertBarCodeItemDtos(List<BarCodeItemDto> dtoList) {
		BatchIterator<BarCodeItemDto> it = new BatchIterator<BarCodeItemDto>(dtoList);
		while (it.hasNext()) {
			barCodeItemDao.batchInsert(it.next());
		}
	}

	@Override
	public void batchUpdateBarCodeItemDtos(List<BarCodeItemDto> dtoList) {
		BatchIterator<BarCodeItemDto> it = new BatchIterator<BarCodeItemDto>(dtoList);
		while (it.hasNext()) {
			barCodeItemDao.batchUpdate(it.next());
		}
	}

	@Override
	public List<BarCodeItemDto> getBarCodeItemDtos(List<String> barCodeList) {
		List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
		BatchIterator<String> it = new BatchIterator<String>(barCodeList);
		while (it.hasNext()) {
			List<BarCodeItemDto> subList = barCodeItemDao.getBarCodeItemDtos(it.next());
			if (CollectionUtils.isNotEmpty(subList)) {
				dtoList.addAll(subList);
			}
		}
		return dtoList;
	}

	public void setBarCodeItemDao(BarCodeItemDao barCodeItemDao) {
		this.barCodeItemDao = barCodeItemDao;
	}

}
