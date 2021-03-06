package com.lezo.iscript.service.crawler.service;

import java.util.Date;
import java.util.List;

import com.lezo.iscript.service.crawler.dto.ProductDto;

public interface ProductService {
    void batchInsertProductDtos(List<ProductDto> dtoList);

    void batchUpdateProductDtos(List<ProductDto> dtoList);

    void batchSaveProductDtos(List<ProductDto> dtoList);

    List<ProductDto> getProductDtos(List<String> codeList, Integer siteId);

    List<ProductDto> getProductDtosFromId(Long fromId, int limit, Integer siteId);

    List<ProductDto> getProductDtosByDateCateSiteId(Date fromCreateDate, Date toCreateDate, String sCategory,
            Integer siteId, Long fromId, int limit);

    void batchUpdateBarCodeBySkuCode(List<ProductDto> dtoList);
}
