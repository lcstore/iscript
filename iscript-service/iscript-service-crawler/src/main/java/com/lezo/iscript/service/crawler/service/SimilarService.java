package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarService {
    void batchInsertSimilarDtos(List<SimilarDto> dtoList);

    void batchUpdateSimilarDtos(List<SimilarDto> dtoList);

    List<SimilarDto> getSimilarDtoByJobIds(List<String> jobIds);

    List<String> getBrands();

    List<SimilarDto> getSimilarDtoByBrandAndId(String brand, Long fromId, int limit);

    List<SimilarDto> getSimilarDtoByJobIdSiteId(String jobId, int siteId, Long fromId, int limit);

    List<SimilarDto> getSimilarDtoByIds(List<Long> idList);

    List<SimilarDto> getSimilarDtoBySkuCodes(List<String> skuCodes);

    List<String> getBrandByJobIdsOrSiteIds(List<String> jobIds, List<Integer> siteIds);

}
