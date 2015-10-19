package com.lezo.iscript.service.crawler.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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

    public void setProductStatHisDao(ProductStatHisDao productStatHisDao) {
        this.productStatHisDao = productStatHisDao;
    }

    @Override
    public int batchInsertDtos(List<ProductStatDto> dtoList) {
        int affect = 0;
        BatchIterator<ProductStatDto> it = new BatchIterator<ProductStatDto>(dtoList);
        while (it.hasNext()) {
            affect += productStatHisDao.batchInsert(it.next());
        }
        return affect;
    }

    @Override
    public List<ProductStatDto> getDtoByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return productStatHisDao.getDtoByIds(idList);
    }

}
