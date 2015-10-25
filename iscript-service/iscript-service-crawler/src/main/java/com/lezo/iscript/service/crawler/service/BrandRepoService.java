package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.BrandRepoDto;

public interface BrandRepoService extends BaseService<BrandRepoDto> {
    List<BrandRepoDto> getDtoByIds(List<Long> idList);

    List<BrandRepoDto> getDtoByCoreOrSort(List<String> coreList, String sortName);

    List<BrandRepoDto> getDtoByIdWithLimit(long fromId, int limit);
}
