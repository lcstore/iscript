package com.lezo.iscript.service.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseDao;
import com.lezo.iscript.service.crawler.dto.ItemDto;

public interface ItemDao extends BaseDao<ItemDto> {

    List<ItemDto> getDtoByMatchCodes(@Param("mCodes") List<String> mCodes);

    List<ItemDto> getDtoByCategory(@Param("category") String category, @Param("offset") int offset,
            @Param("limit") int limit);

}
