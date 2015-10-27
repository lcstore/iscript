package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarService {
    void batchInsertSimilarDtos(List<SimilarDto> dtoList);

    void batchUpdateSimilarDtos(List<SimilarDto> dtoList);

    List<SimilarDto> getSimilarDtoByJobIds(List<Long> jobIds);

    List<String> getBrands();

    List<SimilarDto> getSimilarDtoByBrandAndId(String brand, Long fromId, int limit);

    List<SimilarDto> getSimilarDtoByJobIdSiteId(String jobId, int siteId, Long fromId, int limit);

    List<SimilarDto> getSimilarDtoByIds(List<Long> idList);

}
