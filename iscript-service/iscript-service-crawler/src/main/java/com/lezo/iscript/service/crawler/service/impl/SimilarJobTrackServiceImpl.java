package com.lezo.iscript.service.crawler.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.SimilarJobTrackDao;
import com.lezo.iscript.service.crawler.dto.SimilarJobTrackDto;
import com.lezo.iscript.service.crawler.service.SimilarJobTrackService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class SimilarJobTrackServiceImpl implements SimilarJobTrackService {
    @Autowired
    private SimilarJobTrackDao thisDao;
    @Override
    public int batchInsertDtos(List<SimilarJobTrackDto> dtoList) {
        int affect = 0;
        BatchIterator<SimilarJobTrackDto> it = new BatchIterator<SimilarJobTrackDto>(dtoList);
        while (it.hasNext()) {
            affect += thisDao.batchInsert(it.next());
        }
        return affect;
    }

    @Override
    public int batchUpdateDtos(List<SimilarJobTrackDto> dtoList) {
        BatchIterator<SimilarJobTrackDto> it = new BatchIterator<SimilarJobTrackDto>(dtoList);
        while (it.hasNext()) {
            thisDao.batchUpdate(it.next());
        }
        return -1;
    }

    @Override
    public int batchSaveDtos(List<SimilarJobTrackDto> dtoList) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<SimilarJobTrackDto> getDtoByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return thisDao.getDtoByIds(idList);
    }

}
