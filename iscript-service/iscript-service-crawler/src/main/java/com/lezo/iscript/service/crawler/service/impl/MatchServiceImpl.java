package com.lezo.iscript.service.crawler.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.lezo.iscript.service.crawler.dao.MatchDao;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.utils.BatchIterator;

@Log4j
@Service
public class MatchServiceImpl implements MatchService {
    private static final Map<Integer, Integer> SITE_SCRORE_MAP = Maps.newHashMap();
    private static final Comparator<Entry<String, Integer>> CMP_SKU_SCORE =
            new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            };

    static {
        SITE_SCRORE_MAP.put(1001, 5);
    }
    @Autowired
    private MatchDao matchDao;

    @Override
    public int batchInsertDtos(List<MatchDto> dtoList) {
        int affect = 0;
        BatchIterator<MatchDto> it = new BatchIterator<MatchDto>(dtoList);
        while (it.hasNext()) {
            affect += matchDao.batchInsert(it.next());
        }
        return affect;
    }

    @Override
    public int batchUpdateDtos(List<MatchDto> dtoList) {
        int affect = 0;
        BatchIterator<MatchDto> it = new BatchIterator<MatchDto>(dtoList);
        while (it.hasNext()) {
            affect += matchDao.batchUpdate(it.next());
        }
        return affect;
    }

    @Override
    public int batchSaveDtos(List<MatchDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return 0;
        }
        convertNullToDefault(dtoList);
        List<MatchDto> insertDtos = new ArrayList<MatchDto>();
        List<MatchDto> updateDtos = new ArrayList<MatchDto>();
        doAssort(dtoList, insertDtos, updateDtos);
        batchUpdateDtos(updateDtos);
        return batchInsertDtos(insertDtos);
    }

    private void convertNullToDefault(List<MatchDto> dtoList) {
        java.lang.reflect.Field[] fields = MatchDto.class.getDeclaredFields();
        Map<String, Field> keyMap = new HashMap<String, Field>();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(String.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                keyMap.put(field.getName(), field);
            }
        }
        for (Entry<String, Field> entry : keyMap.entrySet()) {
            Field field = entry.getValue();
            for (MatchDto dto : dtoList) {
                try {
                    Object oldValue = field.get(dto);
                    if (oldValue == null) {
                        field.set(dto, StringUtils.EMPTY);
                    }
                } catch (Exception e) {
                    log.warn("null to default.cause:", e);
                }
            }
        }
    }

    private void doAssort(List<MatchDto> dtoList, List<MatchDto> insertDtos, List<MatchDto> updateDtos) {
        Map<String, List<MatchDto>> mCode2DtoMap = new HashMap<String, List<MatchDto>>();
        Set<String> skuCodeSet = new HashSet<String>();
        for (MatchDto dto : dtoList) {
            List<MatchDto> matchDtos = mCode2DtoMap.get(dto.getMatchCode());
            if (matchDtos == null) {
                matchDtos = new ArrayList<MatchDto>();
                mCode2DtoMap.put(dto.getMatchCode(), matchDtos);
            }
            if (!skuCodeSet.contains(dto.getSkuCode())) {
                matchDtos.add(dto);
                skuCodeSet.add(dto.getSkuCode());
            }
        }
        for (Entry<String, List<MatchDto>> entry : mCode2DtoMap.entrySet()) {
            Map<String, MatchDto> skuCodeMap = new HashMap<String, MatchDto>();
            for (MatchDto dto : entry.getValue()) {
                String key = dto.getSkuCode();
                skuCodeMap.put(key, dto);
            }
            List<String> skuCodes = new ArrayList<String>(skuCodeMap.keySet());
            List<MatchDto> oldDtos = getDtoBySkuCodes(skuCodes, 0);
            String mostMatchCode = getMostMatchCode(oldDtos);
            String currentItemCode = getItemCodeIfExist(dtoList);
            if (StringUtils.isBlank(currentItemCode)) {
                currentItemCode = newItemCode(entry.getValue());
                // itemSku is delete, update to new itemCode
                updateItemCodeByMatchCode(mostMatchCode, currentItemCode);
            }
            // set new itemCode
            for (MatchDto dto : entry.getValue()) {
                dto.setItemCode(currentItemCode);
            }
            mostMatchCode = mostMatchCode == null ? entry.getKey() : mostMatchCode;
            Set<String> hasSet = new HashSet<String>();
            // TODO 是否重新计算分值
            for (MatchDto oldDto : oldDtos) {
                MatchDto newDto = skuCodeMap.get(oldDto.getSkuCode());
                if (newDto == null) {
                    continue;
                }
                newDto.setId(oldDto.getId());
                newDto.setCreateTime(oldDto.getCreateTime());
                newDto.setMatchCode(mostMatchCode);
                updateDtos.add(newDto);
                hasSet.add(oldDto.getSkuCode());
            }
            for (Entry<String, MatchDto> skuEntry : skuCodeMap.entrySet()) {
                if (hasSet.contains(skuEntry.getKey())) {
                    continue;
                }
                MatchDto newDto = skuEntry.getValue();
                newDto.setMatchCode(mostMatchCode);
                insertDtos.add(newDto);
            }
        }

    }

    private String newItemCode(List<MatchDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return null;
        }
        Map<String, Integer> skuScoreMap = getSkuScoreMap(dtoList);
        List<Map.Entry<String, Integer>> skuScoreEntityList =
                new ArrayList<Map.Entry<String, Integer>>(skuScoreMap.entrySet());
        Collections.sort(skuScoreEntityList, CMP_SKU_SCORE);
        return skuScoreEntityList.get(0).getKey();
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
        if (SITE_SCRORE_MAP.containsKey(dto.getSiteId())) {
            return SITE_SCRORE_MAP.get(dto.getSiteId());
        }
        return 0;
    }

    private String getItemCodeIfExist(List<MatchDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return null;
        }
        String itemCode = null;
        for (MatchDto dto : dtoList) {
            if (StringUtils.isNotBlank(dto.getItemCode()) && dto.getItemCode().equals(dto.getSkuCode())) {
                itemCode = dto.getItemCode();
            }
        }
        return itemCode;
    }

    private String getMostMatchCode(List<MatchDto> oldDtos) {
        Map<String, Integer> mCodeCountMap = new HashMap<String, Integer>();
        for (MatchDto oldDto : oldDtos) {
            Integer count = mCodeCountMap.get(oldDto.getMatchCode());
            if (count == null) {
                count = 1;
            } else {
                count += 1;
            }
            mCodeCountMap.put(oldDto.getMatchCode(), count);
        }
        int maxCount = 0;
        String mostMatchCode = null;
        for (Entry<String, Integer> entry : mCodeCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostMatchCode = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mostMatchCode;
    }

    @Override
    public List<MatchDto> getDtoByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return matchDao.getDtoByIds(idList);
    }

    @Override
    public List<MatchDto> getDtoBySkuCodes(List<String> skuCodes, Integer isDelete) {
        if (CollectionUtils.isEmpty(skuCodes)) {
            return Collections.emptyList();
        }
        return matchDao.getDtoBySkuCodes(skuCodes, isDelete);
    }

    @Override
    public List<String> getMatchCodeWithNullItemCode() {
        return matchDao.getMatchCodeWithBlankItemCode();
    }

    @Override
    public List<MatchDto> getDtoByMatchCodes(List<String> mCodes, Integer isDelete) {
        if (CollectionUtils.isEmpty(mCodes)) {
            return Collections.emptyList();
        }
        return matchDao.getDtoByMatchCodes(mCodes, isDelete);
    }

    @Override
    public int updateItemCodeByMatchCode(String matchCode, String itemCode) {
        if (StringUtils.isBlank(matchCode) || StringUtils.isBlank(itemCode)) {
            return 0;
        }
        return matchDao.updateItemCodeByMatchCode(matchCode, itemCode);
    }

    @Override
    public List<MatchDto> getDtoByMatchCodesWithLimit(List<String> mCodes, int offset, int limit) {
        if (CollectionUtils.isEmpty(mCodes)) {
            return Collections.emptyList();
        }
        return matchDao.getDtoByMatchCodesWithLimit(mCodes, offset, limit);
    }

    @Override
    public List<MatchDto> getDtoBySiteIdWithCreateDate(int siteId, Date fromCreateDate, Date toCreateDate, Long fromId,
            int limit) {
        return matchDao.getDtoBySiteIdWithCreateDate(siteId, fromCreateDate, toCreateDate, fromId, limit);
    }

    @Override
    public void batchUpdateDtoBySkuCode(List<MatchDto> dtoList) {
        BatchIterator<MatchDto> it = new BatchIterator<MatchDto>(dtoList);
        while (it.hasNext()) {
            matchDao.batchUpdateDtoBySkuCode(it.next());
        }
    }

}
