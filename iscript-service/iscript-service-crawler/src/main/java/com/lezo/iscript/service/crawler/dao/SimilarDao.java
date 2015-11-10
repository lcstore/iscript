package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.common.Batch;
import com.lezo.iscript.service.crawler.dto.SimilarDto;

public interface SimilarDao extends BaseDao<SimilarDto> {
    List<SimilarDto> getSimilarDtoByJobIds(@Param("jobIds") List<String> jobIds);

    void batchUpdateBarCodeBySkuCode(@Batch List<SimilarDto> dtoList);

    List<SimilarDto> getDtoWithId(@Param("fromId") Long fromId, @Param("limit") int limit);

    List<String> getBrands();

    List<SimilarDto> getSimilarDtoByBrandAndId(@Param("brand") String brand, @Param("fromId") Long fromId,
            @Param("limit") int limit);

    List<SimilarDto> getSimilarDtoByJobIdSiteId(@Param("jobId") String jobId, @Param("siteId") int siteId,
            @Param("fromId") Long fromId, @Param("limit") int limit);

    List<SimilarDto> getSimilarDtoBySkuCodes(@Param("skuCodes") List<String> skuCodes);

    /**
     * 根据jobIds 或 siteIds查询品牌,若都为空查询全部品牌
     * 
     * @param jobIds
     * @param siteIds
     * @return
     */
    List<String> getBrandByJobIdsOrSiteIds(@Param("jobIds") List<String> jobIds,
            @Param("siteIds") List<Integer> siteIds);
}
