package com.lezo.iscript.service.crawler.service;

import java.util.List;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.ItemDto;

public interface ItemService extends BaseService<ItemDto> {
    List<ItemDto> getDtoByIds(List<Long> idList);

    List<ItemDto> getDtoByMatchCodes(List<String> mCodes);

    List<ItemDto> getDtoByCategory(String category, int offset, int limit);
}
