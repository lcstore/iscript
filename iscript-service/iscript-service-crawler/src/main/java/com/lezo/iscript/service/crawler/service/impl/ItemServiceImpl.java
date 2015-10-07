package com.lezo.iscript.service.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lezo.iscript.service.crawler.dao.ItemDao;
import com.lezo.iscript.service.crawler.dto.ItemDto;
import com.lezo.iscript.service.crawler.service.ItemService;
import com.lezo.iscript.utils.BatchIterator;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;

    @Override
    public int batchInsertDtos(List<ItemDto> dtoList) {
        int affect = 0;
        BatchIterator<ItemDto> it = new BatchIterator<ItemDto>(dtoList);
        while (it.hasNext()) {
            affect += itemDao.batchInsert(it.next());
        }
        return affect;
    }

    @Override
    public int batchUpdateDtos(List<ItemDto> dtoList) {
        BatchIterator<ItemDto> it = new BatchIterator<ItemDto>(dtoList);
        while (it.hasNext()) {
            itemDao.batchUpdate(it.next());
        }
        return -1;
    }

    @Override
    public int batchSaveDtos(List<ItemDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return 0;
        }
        List<ItemDto> insertDtos = new ArrayList<ItemDto>();
        List<ItemDto> updateDtos = new ArrayList<ItemDto>();
        doAssort(dtoList, insertDtos, updateDtos);
        batchUpdateDtos(updateDtos);
        return batchInsertDtos(insertDtos);
    }

    private void doAssort(List<ItemDto> dtoList, List<ItemDto> insertDtos, List<ItemDto> updateDtos) {
        Map<String, ItemDto> mCodeMap = new HashMap<String, ItemDto>();
        for (ItemDto newDto : dtoList) {
            mCodeMap.put(newDto.getMatchCode(), newDto);
        }
        List<String> mCodes = new ArrayList<String>(mCodeMap.keySet());
        List<ItemDto> hasList = getDtoByMatchCodes(mCodes);
        Set<String> hasSet = new HashSet<String>();
        for (ItemDto oldDto : hasList) {
            ItemDto newDto = mCodeMap.get(oldDto.getMatchCode());
            if (newDto == null) {
                continue;
            }
            newDto.setId(oldDto.getId());
            convertNullToOld(newDto, oldDto);
            updateDtos.add(newDto);
            hasSet.add(oldDto.getMatchCode());
        }
        for (Entry<String, ItemDto> entry : mCodeMap.entrySet()) {
            String mCode = entry.getKey();
            if (hasSet.contains(mCode)) {
                continue;
            }
            insertDtos.add(entry.getValue());
        }

    }

    private void convertNullToOld(ItemDto newDto, ItemDto oldDto) {
        if (newDto.getMinPrice() == null) {
            newDto.setMinPrice(oldDto.getMinPrice());
        }
        if (newDto.getMaxPrice() == null) {
            newDto.setMaxPrice(oldDto.getMaxPrice());
        }
    }

    @Override
    public List<ItemDto> getDtoByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return itemDao.getDtoByIds(idList);
    }

    @Override
    public List<ItemDto> getDtoByMatchCodes(List<String> mCodes) {
        if (CollectionUtils.isEmpty(mCodes)) {
            return Collections.emptyList();
        }
        return itemDao.getDtoByMatchCodes(mCodes);
    }

    @Override
    public List<ItemDto> getDtoByCategory(String category, int offset, int limit) {
        if (StringUtils.isBlank(category)) {
            return Collections.emptyList();
        }
        return itemDao.getDtoByCategory(category, offset, limit);
    }

}
