package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ProductStatDao;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.service.ProductStatHisService;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ProductStatServiceImpl implements ProductStatService {
    private static Logger logger = LoggerFactory.getLogger(ProductStatServiceImpl.class);
    @Autowired
    private ProductStatDao productStatDao;
    @Autowired
    private ProductStatHisService productStatHisService;

    @Override
    public void batchInsertProductStatDtos(List<ProductStatDto> dtoList) {
        BatchIterator<ProductStatDto> it = new BatchIterator<ProductStatDto>(dtoList);
        while (it.hasNext()) {
            productStatDao.batchInsert(it.next());
        }
    }

    @Override
    public void batchUpdateProductStatDtos(List<ProductStatDto> dtoList) {
        BatchIterator<ProductStatDto> it = new BatchIterator<ProductStatDto>(dtoList);
        while (it.hasNext()) {
            productStatDao.batchUpdate(it.next());
        }
    }

    @Override
    public List<ProductStatDto> getProductStatDtos(List<String> codeList, Integer siteId, Integer minStock) {
        List<ProductStatDto> dtoList = new ArrayList<ProductStatDto>();
        BatchIterator<String> it = new BatchIterator<String>(codeList);
        while (it.hasNext()) {
            List<ProductStatDto> subList = productStatDao.getProductStatDtos(it.next(), siteId, minStock);
            if (CollectionUtils.isNotEmpty(subList)) {
                dtoList.addAll(subList);
            }
        }
        return dtoList;
    }

    public void setProductStatDao(ProductStatDao productStatDao) {
        this.productStatDao = productStatDao;
    }

    @Override
    public void batchSaveProductStatDtos(List<ProductStatDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        long start = System.currentTimeMillis();
        List<ProductStatDto> insertStatDtos = new ArrayList<ProductStatDto>();
        List<ProductStatDto> insertStatHisDtos = new ArrayList<ProductStatDto>();
        List<ProductStatDto> updateStatDtos = new ArrayList<ProductStatDto>();
        doStatAssort(dtoList, insertStatDtos, updateStatDtos, insertStatHisDtos);
        batchInsertProductStatDtos(insertStatDtos);
        batchUpdateProductStatDtos(updateStatDtos);
        if (CollectionUtils.isNotEmpty(insertStatHisDtos)) {
            for (ProductStatDto statHis : insertStatHisDtos) {
                statHis.setId(null);
                statHis.setCreateTime(statHis.getUpdateTime());
            }
            productStatHisService.batchInsertDtos(insertStatHisDtos);
        }
        long cost = System.currentTimeMillis() - start;
        logger.info(String.format("save [%s]insert:%d,update:%d,[%s]insert:%d,cost:%s", "ProductStatDto",
                insertStatDtos.size(), updateStatDtos.size(), "ProductStatHisDto", insertStatHisDtos.size(), cost));
    }

    private void doStatAssort(List<ProductStatDto> productDtos, List<ProductStatDto> insertDtos,
            List<ProductStatDto> updateDtos, List<ProductStatDto> insertStatHisDtos) {
        Map<Integer, Set<String>> shopMap = new HashMap<Integer, Set<String>>();
        Map<String, ProductStatDto> dtoMap = new HashMap<String, ProductStatDto>();
        for (ProductStatDto dto : productDtos) {
            String key = getDtoKey(dto);
            dtoMap.put(key, dto);
            Set<String> codeSet = shopMap.get(dto.getSiteId());
            if (codeSet == null) {
                codeSet = new HashSet<String>();
                shopMap.put(dto.getSiteId(), codeSet);
            }
            codeSet.add(dto.getProductCode());
        }
        for (Entry<Integer, Set<String>> entry : shopMap.entrySet()) {
            List<ProductStatDto> hasDtos =
                    getProductStatDtos(new ArrayList<String>(entry.getValue()), entry.getKey(), null);
            Set<String> hasCodeSet = new HashSet<String>();
            for (ProductStatDto oldDto : hasDtos) {
                String key = getDtoKey(oldDto);
                ProductStatDto newDto = dtoMap.get(key);
                if (newDto == null) {
                    continue;
                }
                hasCodeSet.add(oldDto.getProductCode());
                keepProperty(newDto, oldDto);
                doPriceStatistic(newDto, oldDto);
                updateDtos.add(newDto);
                if (isChange(oldDto, newDto)) {
                    insertStatHisDtos.add(newDto);
                }
            }
            for (String code : entry.getValue()) {
                if (hasCodeSet.contains(code)) {
                    continue;
                }
                String key = entry.getKey() + "-" + code;
                ProductStatDto newDto = dtoMap.get(key);
                if (newDto.getProductPrice() >= 0) {
                    newDto.setMinPrice(newDto.getProductPrice());
                    newDto.setMaxPrice(newDto.getProductPrice());
                }
                insertDtos.add(newDto);
                insertStatHisDtos.add(newDto);
            }

        }

    }

    private void keepProperty(ProductStatDto newDto, ProductStatDto oldDto) {
        newDto.setId(oldDto.getId());
        if (newDto.getShopId() == null) {
            newDto.setShopId(oldDto.getShopId());
        }
        if (newDto.getStockNum() != null && newDto.getStockNum() < 0
                && (newDto.getProductPrice() == null || newDto.getProductPrice() < 0)) {
            newDto.setProductPrice(oldDto.getProductPrice());
        } else if (newDto.getStockNum() == null) {
            logger.warn("error StockNum.url:" + newDto.getProductUrl());
        }
    }

    private String getDtoKey(ProductStatDto dto) {
        return dto.getSiteId() + "-" + dto.getProductCode();
    }

    private void doPriceStatistic(ProductStatDto newDto, ProductStatDto oldDto) {
        if (newDto.getProductPrice() == null || newDto.getProductPrice() < 0) {
            return;
        }
        if (oldDto.getMinPrice() == null) {
            oldDto.setMinPrice(newDto.getProductPrice());
        }
        if (oldDto.getMaxPrice() == null) {
            oldDto.setMaxPrice(newDto.getProductPrice());
        }
        if (newDto.getProductPrice() < oldDto.getMinPrice()) {
            newDto.setMinPrice(newDto.getProductPrice());
        } else {
            newDto.setMinPrice(oldDto.getMinPrice());
        }
        if (newDto.getProductPrice() > oldDto.getMaxPrice()) {
            newDto.setMaxPrice(newDto.getProductPrice());
        } else {
            newDto.setMaxPrice(oldDto.getMinPrice());
        }
    }

    public boolean isChange(ProductStatDto oldDto, ProductStatDto newDto) {
        if (!isSameObject(oldDto.getProductPrice(), newDto.getProductPrice())) {
            return true;
        }
        if (!isSameObject(oldDto.getStockNum(), newDto.getStockNum())) {
            return true;
        }
        // if (!isSameObject(oldDto.getMarketPrice(), newDto.getMarketPrice()))
        // {
        // return true;
        // }
        // if (!isSameObject(oldDto.getSoldNum(), newDto.getSoldNum())) {
        // return true;
        // }
        //
        // if (!isSameObject(oldDto.getCommentNum(), newDto.getCommentNum())) {
        // return true;
        // }
        return false;
    }

    public boolean isSameObject(Object lObject, Object rObject) {
        if (lObject == null && rObject == null) {
            return true;
        } else if (lObject == null && rObject != null) {
            return false;
        }
        return lObject.equals(rObject);
    }

    @Override
    public List<ProductStatDto> getProductStatDtosByCommentDesc(Integer siteId, int limit) {
        return productStatDao.getProductStatDtosByCommentDesc(siteId, limit);
    }

    @Override
    public List<ProductStatDto> getProductStatDtosByPriceAsc(Integer siteId, int limit) {
        return productStatDao.getProductStatDtosByPriceAsc(siteId, limit);
    }

    @Override
    public List<ProductStatDto> getProductStatDtosBySoldDesc(Integer siteId, int limit) {
        return productStatDao.getProductStatDtosBySoldDesc(siteId, limit);
    }

    @Override
    public List<ProductStatDto> getProductStatDtosLowestPrice(Long fromId, Integer siteId, Date updateTime, int limit) {
        if (limit < 1) {
            return Collections.emptyList();
        }
        if (fromId == null) {
            fromId = 0L;
        }
        return productStatDao.getProductStatDtosLowestPrice(fromId, siteId, updateTime, limit);
    }
}
