package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public interface ProductStatHisService {
    int batchInsertDtos(List<ProductStatDto> dtoList);

    List<ProductStatDto> getDtoByIds(List<Long> idList);
}

