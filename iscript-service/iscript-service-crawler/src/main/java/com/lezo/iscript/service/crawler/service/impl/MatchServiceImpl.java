package com.lezo.iscript.service.crawler.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
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

import com.lezo.iscript.service.crawler.dao.MatchDao;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.utils.BatchIterator;

@Log4j
@Service
public class MatchServiceImpl implements MatchService {
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
        return matchDao.updateItemCodeByMatchCode(matchCode, itemCode);
    }

    @Override
    public List<MatchDto> getDtoByMatchCodesWithLimit(List<String> mCodes, int offset, int limit) {
        if (CollectionUtils.isEmpty(mCodes)) {
            return Collections.emptyList();
        }
        return matchDao.getDtoByMatchCodesWithLimit(mCodes, offset, limit);
    }

}
