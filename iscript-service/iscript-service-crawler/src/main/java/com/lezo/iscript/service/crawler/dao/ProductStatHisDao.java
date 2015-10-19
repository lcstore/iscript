package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public interface ProductStatHisDao {

    int batchInsert(List<ProductStatDto> dtoList);

    List<ProductStatDto> getDtoByIds(List<Long> idList);
}
