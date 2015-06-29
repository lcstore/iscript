package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.common.UnifyValueUtils;
import com.lezo.iscript.resulter.ident.EntityToken;
import com.lezo.iscript.resulter.ident.SectionToken;
import com.lezo.iscript.resulter.similar.BrandUtils;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
@Log4j
public class ProductWriter implements ObjectWriter<ProductDto> {
	private ProductService productService = SpringBeanUtils.getBean(ProductService.class);

	@Override
	public void write(List<ProductDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		dataList = checkValue(dataList);
		doTokenizer(dataList);
		UnifyValueUtils.unifyQuietly(dataList);
		synchronized (this) {
			productService.batchSaveProductDtos(dataList);
		}
	}

	private void doTokenizer(List<ProductDto> dataList) {
		for (ProductDto dto : dataList) {
			EntityToken entity = new EntityToken(dto.getProductName());
			entity.addAssistToken(new SectionToken("productBrand", dto.getProductBrand()));
			List<SectionToken> brandList = BrandUtils.toBrandTokens(entity);
			if (!brandList.isEmpty()) {
				dto.setTokenBrand(brandList.get(0).getValue());
			}
			if (StringUtils.isNotBlank(dto.getCategoryNav()) && dto.getCategoryNav().contains("手机")) {
				String[] navArr = dto.getCategoryNav().split(";");
				for (String nav : navArr) {
					nav = nav.trim();
					if ("手机".equals(nav)) {
						dto.setTokenCategory(nav);
						break;
					}
				}
			}
		}

	}

	private List<ProductDto> checkValue(List<ProductDto> dataList) {
		List<ProductDto> dtoList = new ArrayList<ProductDto>(dataList.size());
		for (ProductDto data : dataList) {
			if (data.getSiteId() == null) {
				log.warn("siteId is null,url:" + data.getProductUrl());
				continue;
			}
			if (data.getShopId() == null) {
				log.warn("shopId is null,url:" + data.getProductUrl());
				continue;
			}
			dtoList.add(data);
		}
		return dtoList;
	}

}
