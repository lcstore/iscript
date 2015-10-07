package com.lezo.iscript.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.iscript.resulter.ident.EntityToken;
import com.lezo.iscript.resulter.ident.SectionToken;
import com.lezo.iscript.resulter.similar.ModelUtils;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.SimilarJobDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.similar.SimilarParam;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.task.TaskConstant;

public class BaseSimilarJobHandler implements SimilarJobHandler {

    private static final String[] CONFIG_LIST = new String[] { "ConfigSimilarByBarCode", "ConfigSimilarBySearch",
            "ConfigSimilarByWare" };

    @Override
    public void handle(SimilarJobDto jobDto) {
        SimilarParam paramBean = JSON.parseObject(jobDto.getInputs(), SimilarParam.class);
        if (paramBean == null) {
            return;
        }
        List<ProductDto> srcList = getProducts(paramBean);
        List<ProductDto> waitList = srcList;
        if (!paramBean.getFlushGlobal()) {
        }
        List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
        Map<String, List<ProductDto>> brandModel2DtoMap = getBrandModel2DtoMap(waitList);
        String taskId = jobDto.getId().toString() + "-" + jobDto.getName().hashCode();

        for (String config : CONFIG_LIST) {
            if ("ConfigSimilarBySearch".equals(config)) {
                for (Entry<String, List<ProductDto>> entry : brandModel2DtoMap.entrySet()) {
                    TaskPriorityDto taskDto = toPriorityDto(config, taskId);
                    JSONObject argsObject = JSON.parseObject(entry.getKey());
                    List<String> skuCodes = Lists.newArrayList();
                    for (ProductDto dto : entry.getValue()) {
                        skuCodes.add(dto.getSkuCode());
                    }
                    argsObject.put("skuCodes", skuCodes);
                    taskDto.setParams(argsObject.toJSONString());
                    taskList.add(taskDto);
                }
            } else {
                JSONObject argsObject = new JSONObject();
                for (Entry<String, List<ProductDto>> entry : brandModel2DtoMap.entrySet()) {
                    for (ProductDto dto : entry.getValue()) {
                        TaskPriorityDto taskDto = toPriorityDto(config, taskId);
                        argsObject.put("name", dto.getProductName());
                        argsObject.put("skuCode", dto.getSkuCode());
                        argsObject.put("url", dto.getProductUrl());
                        argsObject.put("mprice", dto.getMarketPrice());
                        taskDto.setParams(argsObject.toJSONString());
                        taskList.add(taskDto);
                    }
                }
            }
        }
        SpringBeanUtils.getBean(TaskPriorityService.class).batchInsert(taskList);
    }

    private TaskPriorityDto toPriorityDto(String taskType, String taskId) {
        TaskPriorityDto taskDto = new TaskPriorityDto();
        taskDto.setBatchId(taskId);
        taskDto.setType(taskType);
        String sUrl = "";
        taskDto.setUrl(sUrl);
        taskDto.setLevel(1);
        taskDto.setSource("tasker");
        taskDto.setCreatTime(new Date());
        taskDto.setStatus(TaskConstant.TASK_NEW);
        return taskDto;
    }

    private Map<String, List<ProductDto>> getBrandModel2DtoMap(List<ProductDto> waitList) {
        Map<String, List<ProductDto>> map = Maps.newHashMap();
        com.alibaba.fastjson.JSONObject paramObject = new com.alibaba.fastjson.JSONObject();
        for (ProductDto dto : waitList) {
            String brand = StringUtils.isBlank(dto.getTokenBrand()) ? dto.getProductBrand() : dto.getTokenBrand();
            EntityToken entity = new EntityToken(dto.getProductName());
            entity.addAssistToken(new SectionToken("productModel", dto.getProductModel()));
            List<SectionToken> tokenList = ModelUtils.toModelTokens(entity);
            paramObject.put("brand", brand);
            if (CollectionUtils.isNotEmpty(tokenList)) {
                paramObject.put("model", tokenList.get(0).getValue());
            } else {
                paramObject.put("model", dto.getProductModel());
            }
            String key = paramObject.toString();
            List<ProductDto> hasList = map.get(key);
            if (hasList == null) {
                hasList = Lists.newArrayList();
                map.put(key, hasList);
            }
            hasList.add(dto);
        }
        return map;
    }

    private Set<String> getMatchSkuCodeSet(List<ProductDto> srcList) {
        // TODO Auto-generated method stub
        return Collections.emptySet();
    }

    private List<ProductDto> getProducts(SimilarParam param) {
        if (SimilarParam.TYPE_PRODUCT_CODE.equals(param.getIdType())) {
            Set<String> pCodeSet = param.getIdSet();
            if (param.getSiteId() == null) {
                return Collections.emptyList();
            }
            ProductService productService = SpringBeanUtils.getBean(ProductService.class);
            List<String> codeList = new ArrayList<String>(pCodeSet);
            return productService.getProductDtos(codeList, param.getSiteId());
        }
        return Collections.emptyList();
    }

}
