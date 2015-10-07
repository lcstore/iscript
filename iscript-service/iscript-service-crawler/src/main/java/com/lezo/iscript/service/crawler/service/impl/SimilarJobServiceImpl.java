package com.lezo.iscript.service.crawler.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.SimilarJobDao;
import com.lezo.iscript.service.crawler.dto.SimilarJobDto;
import com.lezo.iscript.service.crawler.service.SimilarJobService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class SimilarJobServiceImpl implements SimilarJobService {
    @Autowired
    private SimilarJobDao similarJobDao;

    @Override
    public int batchInsertDtos(List<SimilarJobDto> dtoList) {
        int affect = 0;
        BatchIterator<SimilarJobDto> it = new BatchIterator<SimilarJobDto>(dtoList);
        while (it.hasNext()) {
            affect += similarJobDao.batchInsert(it.next());
        }
        return affect;
    }

    @Override
    public int batchUpdateDtos(List<SimilarJobDto> dtoList) {
        BatchIterator<SimilarJobDto> it = new BatchIterator<SimilarJobDto>(dtoList);
        while (it.hasNext()) {
            similarJobDao.batchUpdate(it.next());
        }
        return -1;
    }

    @Override
    public int batchSaveDtos(List<SimilarJobDto> dtoList) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<SimilarJobDto> getDtoByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return similarJobDao.getDtoByIds(idList);
    }

    @Override
    public List<SimilarJobDto> getDtoByStatus(Long fromId, int status, int limit) {
        return similarJobDao.getDtoByStatus(fromId, status, limit);
    }

}
