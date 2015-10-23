package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.dto.SiteDto;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.service.crawler.utils.SiteCacher;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.ObjectUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class SkuWithSimilarStrategy implements ResultStrategy, Closeable {
    private static Logger logger = LoggerFactory.getLogger(SkuWithSimilarStrategy.class);

    public SkuWithSimilarStrategy() {
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void handleResult(ResultWritable rWritable) {
        if (ResultWritable.RESULT_SUCCESS != rWritable.getStatus()) {
            return;
        }
        if (rWritable.getType().endsWith("Product")) {
            JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
            JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
            JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
            try {
                argsObject.remove("name@client");
                argsObject.remove("target");
                argsObject.remove("fromUrl");
                handleResult(rWritable, rsObject, argsObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleResult(ResultWritable rWritable, JSONObject rsObject, JSONObject argsObject) throws Exception {
        JSONArray dataArray = JSONUtils.get(rsObject, "dataList");
        if (dataArray == null) {
            return;
        }
        int len = dataArray.length();
        List<ProductStatDto> dtoList = new ArrayList<ProductStatDto>();
        List<SimilarDto> similarDto = Lists.newArrayList();
        String jobId = JSONUtils.getString(argsObject, "jid");
        String tokenCategory = JSONUtils.getString(argsObject, "cate");
        for (int i = 0; i < len; i++) {
            JSONObject dObject = dataArray.getJSONObject(i);
            ProductStatDto statDto = new ProductStatDto();
            dtoList.add(statDto);
            ObjectUtils.copyObject(dObject, statDto);
            addProperties(statDto, dObject, argsObject);
            statDto.setSkuCode(statDto.getSiteId() + "_" + statDto.getProductCode());
            if (statDto.getStockNum() == null || statDto.getStockNum() < 1) {
                continue;
            }
            String sImgUrl = JSONUtils.getString(dObject, "imgUrl");
            SimilarDto sDto = new SimilarDto();
            sDto.setSkuCode(statDto.getSkuCode());
            sDto.setProductCode(statDto.getProductCode());
            sDto.setProductName(statDto.getProductName());
            sDto.setProductUrl(statDto.getProductUrl());
            sDto.setMarketPrice(statDto.getMarketPrice());
            sDto.setSiteId(statDto.getSiteId());
            sDto.setShopId(statDto.getShopId());
            sDto.setImgUrl(sImgUrl);
            sDto.setBarCode(JSONUtils.getString(dObject, "barCode"));
            sDto.setJobId(jobId);
            sDto.setTokenCategory(tokenCategory);
            sDto.setTokenVary(JSONUtils.getString(dObject, "spuVary"));
            sDto.setArbiterId(SimilarDto.ARBITER_NAME);
            similarDto.add(sDto);
        }
        for (Field field : SimilarDto.class.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(String.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                for (SimilarDto sDto : similarDto) {
                    if (field.get(sDto) == null) {
                        field.set(sDto, StringUtils.EMPTY);
                    }
                }
            }
        }
        for (Field field : ProductStatDto.class.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(String.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                for (ProductStatDto sDto : dtoList) {
                    if (field.get(sDto) == null) {
                        field.set(sDto, StringUtils.EMPTY);
                    }
                }
            }
        }
        SpringBeanUtils.getBean(ProductStatService.class).batchSaveProductStatDtos(dtoList);
        SpringBeanUtils.getBean(SimilarService.class).batchInsertSimilarDtos(similarDto);
        logger.info("save statDto,count:" + dtoList.size() + ",similar,count:" + similarDto.size());

    }

    private void addProperties(ProductStatDto destObject, JSONObject dataObject,
            JSONObject argsObject) throws Exception {
        if (destObject.getSiteId() == null) {
            SiteDto siteDto = SiteCacher.getInstance().getDomainSiteDto(destObject.getProductUrl());
            if (siteDto != null) {
                destObject.setSiteId(siteDto.getId());
            } else {
                destObject.setSiteId(0);
            }
        }
        addShopId(destObject, dataObject, argsObject);
        if (destObject.getShopId() == null) {
            destObject.setShopId(destObject.getSiteId());
        }
        Date newDate = new Date();
        destObject.setCreateTime(newDate);
        destObject.setUpdateTime(newDate);
    }

    private void addShopId(ProductStatDto destObject, JSONObject dataObject, JSONObject argsObject) throws Exception {
        Integer stockNum = JSONUtils.getInteger(dataObject, "stockNum");
        if (stockNum != null && stockNum < 0) {
            return;
        }
        if (destObject.getShopId() != null) {
            return;
        }
        Integer shopId = JSONUtils.getInteger(argsObject, "shopId");
        if (shopId != null) {
            destObject.setShopId(shopId);
            return;
        }
        String shopUrl = JSONUtils.getString(dataObject, "shopUrl");
        String shopCode = JSONUtils.getString(dataObject, "shopCode");
        String shopName = JSONUtils.getString(dataObject, "shopName");
        if (!StringUtils.isEmpty(shopUrl) && !StringUtils.isEmpty(shopName)) {
            ShopDto shopDto = ShopCacher.getInstance().insertIfAbsent(shopName, shopUrl, shopCode);
            if (shopDto != null) {
                destObject.setShopId(shopDto.getId());
                return;
            }
        }
        String msg = String.format("can not set shopId.args:%s,data:%s", argsObject, dataObject);
        throw new IllegalAccessException(msg);
    }

    @Override
    public void close() throws IOException {
        logger.info("close " + getName() + " strategy..");
    }
}