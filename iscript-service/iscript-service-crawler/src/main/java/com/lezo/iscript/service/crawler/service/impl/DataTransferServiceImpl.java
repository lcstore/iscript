package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.DataTransferDao;
import com.lezo.iscript.service.crawler.dto.DataTransferDto;
import com.lezo.iscript.service.crawler.service.DataTransferService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class DataTransferServiceImpl implements DataTransferService {
	@Autowired
	private DataTransferDao dtoDao;

	@Override
	public int batchInsertDtos(List<DataTransferDto> dtoList) {
		int affect = 0;
		BatchIterator<DataTransferDto> it = new BatchIterator<DataTransferDto>(dtoList);
		while (it.hasNext()) {
			affect += dtoDao.batchInsert(it.next());
		}
		return affect;
	}

	@Override
	public int batchUpdateDtos(List<DataTransferDto> dtoList) {
		int affect = 0;
		BatchIterator<DataTransferDto> it = new BatchIterator<DataTransferDto>(dtoList);
		while (it.hasNext()) {
			affect += dtoDao.batchUpdate(it.next());
		}
		return affect;
	}

	@Override
	public int batchSaveDtos(List<DataTransferDto> dtoList) {
		return 0;
	}

	@Override
	public int batchInsertOrUpdateByKey(List<DataTransferDto> dtoList) {
		int affect = 0;
		BatchIterator<DataTransferDto> it = new BatchIterator<DataTransferDto>(dtoList);
		while (it.hasNext()) {
			affect += dtoDao.batchInsertOrUpdateByKey(it.next());
		}
		return affect;
	}

	@Override
	public List<DataTransferDto> getDtoByCodeList(List<String> codeList) {
		List<DataTransferDto> dtoList = new ArrayList<DataTransferDto>();
		BatchIterator<String> it = new BatchIterator<String>(codeList);
		while (it.hasNext()) {
			List<DataTransferDto> hasList = dtoDao.getDtoByCodeList(it.next());
			if (CollectionUtils.isNotEmpty(hasList)) {
				dtoList.addAll(hasList);
			}
		}
		return dtoList;
	}

}
