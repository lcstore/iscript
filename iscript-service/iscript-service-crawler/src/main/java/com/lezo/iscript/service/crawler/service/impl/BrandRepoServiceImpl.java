package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.service.crawler.dao.BrandRepoDao;
import com.lezo.iscript.service.crawler.dto.BrandRepoDto;
import com.lezo.iscript.service.crawler.service.BrandRepoService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class BrandRepoServiceImpl implements BrandRepoService {
    private static final String WORD_SPLITOR = ",";
    @Autowired
    private BrandRepoDao brandRepoDao;

    @Override
    public int batchInsertDtos(List<BrandRepoDto> dtoList) {
        int affect = 0;
        BatchIterator<BrandRepoDto> it = new BatchIterator<BrandRepoDto>(dtoList);
        while (it.hasNext()) {
            affect += brandRepoDao.batchInsert(it.next());
        }
        return affect;
    }

    @Override
    public int batchUpdateDtos(List<BrandRepoDto> dtoList) {
        BatchIterator<BrandRepoDto> it = new BatchIterator<BrandRepoDto>(dtoList);
        while (it.hasNext()) {
            brandRepoDao.batchUpdate(it.next());
        }
        return -1;
    }

    @Override
    public int batchSaveDtos(List<BrandRepoDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return 0;
        }
        int affect = 0;
        List<BrandRepoDto> updateList = new ArrayList<BrandRepoDto>();
        List<BrandRepoDto> insertList = new ArrayList<BrandRepoDto>();
        doAssort(dtoList, updateList, insertList);
        affect += batchInsertDtos(insertList);
        affect += batchUpdateDtos(updateList);
        return affect;
    }

    private void doAssort(List<BrandRepoDto> dtoList, List<BrandRepoDto> updateList, List<BrandRepoDto> insertList) {
        Map<String, List<BrandRepoDto>> sortMap = toSameSortMap(dtoList);
        for (Entry<String, List<BrandRepoDto>> entry : sortMap.entrySet()) {
            Map<String, BrandRepoDto> sameCoreMap = toSameCoreMap(entry.getValue());
            List<String> coreList = Lists.newArrayList(sameCoreMap.keySet());
            List<BrandRepoDto> hasList = getDtoByCoreOrSort(coreList, entry.getKey());
            Set<String> hasSet = new HashSet<String>();
            for (BrandRepoDto oldDto : hasList) {
                String key = oldDto.getCoreName();
                if (hasSet.contains(key)) {
                    continue;
                }
                BrandRepoDto newDto = sameCoreMap.get(key);
                if (newDto != null) {
                    newDto.setId(oldDto.getId());
                    mergeNames(newDto, oldDto);
                    if (StringUtils.isBlank(newDto.getRegionName())) {
                        newDto.setRegionName(oldDto.getRegionName());
                    }
                    if (StringUtils.isBlank(newDto.getCrumbNav())) {
                        newDto.setCrumbNav(oldDto.getCrumbNav());
                    }
                    if (StringUtils.isBlank(newDto.getSortName())) {
                        newDto.setSortName(oldDto.getSortName());
                    }
                    hasSet.add(key);
                    updateList.add(newDto);
                }
            }
            for (Entry<String, BrandRepoDto> cnEntry : sameCoreMap.entrySet()) {
                if (hasSet.contains(cnEntry.getKey())) {
                    continue;
                }
                insertList.add(cnEntry.getValue());
            }
        }

    }

    private void mergeNames(BrandRepoDto newDto, BrandRepoDto oldDto) {
        Set<String> excludeSet = getMergeSet(oldDto.getExcludes(), newDto.getExcludes());
        Set<String> includeSet = getMergeSet(oldDto.getIncludes(), newDto.getIncludes());
        includeSet.removeAll(excludeSet);
        newDto.setExcludes(getMergeChars(excludeSet));
        newDto.setIncludes(getMergeChars(includeSet));
    }

    private String getMergeChars(Set<String> mergetSet) {
        if (CollectionUtils.isEmpty(mergetSet)) {
            return StringUtils.EMPTY;
        }
        StringBuffer sb = new StringBuffer();
        for (String newString : mergetSet) {
            if (sb.length() > 0) {
                sb.append(WORD_SPLITOR);
            }
            sb.append(newString);
        }
        return sb.toString();
    }

    private Set<String> getMergeSet(String oldChars, String newChars) {
        Set<String> mergetSet = Sets.newHashSet();
        if (StringUtils.isNotBlank(oldChars)) {
            String[] oldStrings = oldChars.split(WORD_SPLITOR);
            for (String oldVal : oldStrings) {
                if (StringUtils.isNotBlank(oldVal)) {
                    mergetSet.add(oldVal);
                }
            }
        }
        if (StringUtils.isNotBlank(newChars)) {
            String[] newStrings = newChars.split(WORD_SPLITOR);
            for (String newVal : newStrings) {
                if (StringUtils.isNotBlank(newVal)) {
                    mergetSet.add(newVal);
                }
            }
        }
        return mergetSet;
    }

    private Map<String, BrandRepoDto> toSameCoreMap(List<BrandRepoDto> dtoList) {
        Map<String, BrandRepoDto> sameCoreMap = Maps.newHashMap();
        Map<String, BrandRepoDto> brand2DtoMap = Maps.newHashMap();
        for (BrandRepoDto dto : dtoList) {
            BrandRepoDto repoDto = sameCoreMap.get(dto.getCoreName());
            repoDto = repoDto == null ? getSameBrandDto(brand2DtoMap, dto) : repoDto;
            if (repoDto == null) {
                repoDto = dto;
                sameCoreMap.put(dto.getCoreName(), repoDto);
            } else {
                mergeNames(repoDto, dto);
            }
            for (String brand : dto.getIncludes().split(",")) {
                brand2DtoMap.put(brand, repoDto);
            }
        }
        return sameCoreMap;
    }

    private BrandRepoDto getSameBrandDto(Map<String, BrandRepoDto> brand2DtoMap, BrandRepoDto dto) {
        for (String brand : dto.getIncludes().split(",")) {
            BrandRepoDto hitDto = brand2DtoMap.get(brand);
            if (hitDto != null) {
                return hitDto;
            }
        }
        return null;
    }

    private Map<String, List<BrandRepoDto>> toSameSortMap(List<BrandRepoDto> dtoList) {
        Map<String, List<BrandRepoDto>> sameSortMap = Maps.newHashMap();
        Set<String> hasSet = new HashSet<String>();
        for (BrandRepoDto dto : dtoList) {
            String key = dto.getSortName() + "_" + dto.getCoreName();
            if (hasSet.contains(key)) {
                continue;
            }
            List<BrandRepoDto> sameSortList = sameSortMap.get(dto.getSortName());
            if (sameSortList == null) {
                sameSortList = Lists.newArrayList();
                sameSortMap.put(dto.getSortName(), sameSortList);
            }
            sameSortList.add(dto);
            hasSet.add(key);
        }
        return sameSortMap;
    }

    @Override
    public List<BrandRepoDto> getDtoByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return brandRepoDao.getDtoByIds(idList);
    }

    @Override
    public List<BrandRepoDto> getDtoByCoreOrSort(List<String> coreList, String sortName) {
        if (CollectionUtils.isEmpty(coreList) && sortName == null) {
            return Collections.emptyList();
        }
        return brandRepoDao.getDtoByCoreOrSort(coreList, sortName);
    }

    @Override
    public List<BrandRepoDto> getDtoByIdWithLimit(long fromId, int limit) {
        if (limit < 1) {
            return Collections.emptyList();
        }
        return brandRepoDao.getDtoByIdWithLimit(fromId, limit);
    }

}
