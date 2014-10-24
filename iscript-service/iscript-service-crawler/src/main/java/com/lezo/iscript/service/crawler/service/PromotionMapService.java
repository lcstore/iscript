package com.lezo.iscript.service.crawler.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lezo.iscript.common.BaseService;
import com.lezo.iscript.service.crawler.dto.PromotionMapDto;

public interface PromotionMapService extends BaseService<PromotionMapDto> {
	List<PromotionMapDto> getPromotionMapDtosByProductCodes(@Param(value = "siteId") Integer siteId, @Param(value = "productCodes") List<String> productCodes, @Param(value = "promoteType") Integer promoteType, @Param(value = "promoteStatus") Integer promoteStatus, @Param(value = "isDelete") Integer isDelete);

	List<PromotionMapDto> getPromotionMapDtosByPromotCodes(@Param(value = "siteId") Integer siteId, @Param(value = "promotCodes") List<String> promotCodes, @Param(value = "promoteType") Integer promoteType, @Param(value = "promoteStatus") Integer promoteStatus, @Param(value = "isDelete") Integer isDelete);

	void batchUpdateIsDelete(@Param(value = "idList") List<Long> idList, @Param(value = "isDelete") Integer isDelete);

	List<String> getProductCodeSetBySiteIdAndType(Integer siteId, Integer promoteType, Integer promoteStatus, Integer isDelete);
}
