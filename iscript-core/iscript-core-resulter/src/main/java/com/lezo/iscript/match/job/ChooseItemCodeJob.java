package com.lezo.iscript.match.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dto.ItemDto;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.service.ItemService;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Log4j
public class ChooseItemCodeJob implements Runnable {
    private static final Integer SITE_JD = 1001;
    private static AtomicBoolean running = new AtomicBoolean(false);

    public static void main(String[] args) {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        new ChooseItemCodeJob().run();
    }

    @Override
    public void run() {
        if (running.get()) {
            log.warn("ClusterSimilarJob is running..");
            return;
        }
        long start = System.currentTimeMillis();
        try {
            running.set(true);
            MatchService matchService = SpringBeanUtils.getBean(MatchService.class);
            ItemService itemService = SpringBeanUtils.getBean(ItemService.class);
            List<String> matchCodes = matchService.getMatchCodeWithNullItemCode();
            List<String> mCodes = new ArrayList<String>();
            mCodes.add(StringUtils.EMPTY);
            for (String mCode : matchCodes) {
                mCodes.set(0, mCode);
                List<MatchDto> dtoList = matchService.getDtoByMatchCodes(mCodes, 0);
                if (CollectionUtils.isEmpty(dtoList) || dtoList.size() < 2) {
                    continue;
                }
                String itemCode = chooseItemCode(dtoList);
                ItemDto itemDto = getItemDto(dtoList, itemCode);
                if (itemDto != null) {
                    log.info("choose item:" + itemCode + ",mCode:" + mCode);
                    // update itemCode to MatchDto
                    matchService.updateItemCodeByMatchCode(itemDto.getMatchCode(), itemCode);
                    // save itemDto
                    List<ItemDto> itemList = new ArrayList<ItemDto>(1);
                    itemList.add(itemDto);
                    itemService.batchSaveDtos(itemList);
                }

            }
            long cost = System.currentTimeMillis() - start;
            log.info("done.choose itemCode,count:" + matchCodes.size() + ",cost:" + cost);
        } catch (Exception e) {
            log.warn("", e);
        } finally {
            running.set(false);
        }
    }

    private ItemDto getItemDto(List<MatchDto> dtoList, String itemCode) {
        MatchDto matchDto = null;
        for (MatchDto dto : dtoList) {
            if (dto.getSkuCode().equals(itemCode)) {
                matchDto = dto;
                break;
            }
        }
        if (matchDto == null) {
            return null;
        }
        ItemDto itemDto = new ItemDto();
        BeanUtils.copyProperties(matchDto, itemDto);
        itemDto.setId(null);
        itemDto.setCreateTime(new Date());
        itemDto.setUpdateTime(itemDto.getCreateTime());
        return itemDto;
    }

    private String chooseItemCode(List<MatchDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return null;
        }
        String hadItemCode = getItemCodeIfExist(dtoList);
        if (StringUtils.isNotBlank(hadItemCode)) {
            return hadItemCode;
        }
        Map<String, Integer> skuScoreMap = getSkuScoreMap(dtoList);
        List<Map.Entry<String, Integer>> skuScoreEntityList =
                new ArrayList<Map.Entry<String, Integer>>(skuScoreMap.entrySet());
        Collections.sort(skuScoreEntityList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return skuScoreEntityList.get(0).getKey();
    }

    private String getItemCodeIfExist(List<MatchDto> dtoList) {
        String itemCode = null;
        for (MatchDto dto : dtoList) {
            if (StringUtils.isNotBlank(dto.getItemCode()) && dto.getItemCode().equals(dto.getSkuCode())) {
                itemCode = dto.getItemCode();
            }
        }
        return itemCode;
    }

    private Map<String, Integer> getSkuScoreMap(List<MatchDto> dtoList) {
        Map<String, Integer> wareCountMap = getWareCountMap(dtoList);
        Map<String, Integer> barCodeCountMap = getBarCodeCountMap(dtoList);
        Map<String, Integer> skuScoreMap = new HashMap<String, Integer>();
        for (MatchDto dto : dtoList) {
            int score = getSiteScore(dto);
            Integer wScore = wareCountMap.get(dto.getWareCode());
            if (wScore != null) {
                score += wScore;
            }
            Integer bScore = barCodeCountMap.get(dto.getBarCode());
            if (bScore != null) {
                score += bScore;
            }
            skuScoreMap.put(dto.getSkuCode(), score);
        }
        return skuScoreMap;
    }

    private int getSiteScore(MatchDto dto) {
        int unitCount = 5;
        if (SITE_JD.equals(dto.getSiteId())) {
            return unitCount;
        }
        return 0;
    }

    private Map<String, Integer> getBarCodeCountMap(List<MatchDto> dtoList) {
        Map<String, Integer> barCodeCountMap = new HashMap<String, Integer>();
        int unitCount = 10;
        for (MatchDto dto : dtoList) {
            String barCode = dto.getBarCode();
            if (StringUtils.isBlank(barCode)) {
                continue;
            }
            Integer count = barCodeCountMap.get(barCode);
            if (count == null) {
                count = unitCount;
            } else {
                count += unitCount;
            }
            barCodeCountMap.put(barCode, count);
        }
        return barCodeCountMap;
    }

    private Map<String, Integer> getWareCountMap(List<MatchDto> dtoList) {
        Map<String, Integer> wareCountMap = new HashMap<String, Integer>();
        int unitCount = 5;
        for (MatchDto dto : dtoList) {
            String wareCode = dto.getWareCode();
            if (StringUtils.isBlank(wareCode)) {
                continue;
            }
            Integer count = wareCountMap.get(wareCode);
            if (count == null) {
                count = unitCount;
            } else {
                count += unitCount;
            }
            wareCountMap.put(wareCode, count);
        }
        return wareCountMap;
    }
}
