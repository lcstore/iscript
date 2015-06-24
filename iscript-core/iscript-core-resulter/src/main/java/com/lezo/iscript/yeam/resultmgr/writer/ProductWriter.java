package com.lezo.iscript.yeam.resultmgr.writer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.common.UnifyValueUtils;
import com.lezo.iscript.resulter.ident.BrandTokenizer;
import com.lezo.iscript.resulter.ident.EntityToken;
import com.lezo.iscript.resulter.ident.SectionToken;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.SynonymBrandService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
@Log4j
public class ProductWriter implements ObjectWriter<ProductDto> {
	private ProductService productService = SpringBeanUtils.getBean(ProductService.class);
	private SynonymBrandService synonymBrandService = SpringBeanUtils.getBean(SynonymBrandService.class);

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
		Comparator<SectionToken> comparator = new Comparator<SectionToken>() {
			@Override
			public int compare(SectionToken o1, SectionToken o2) {
				int sub = o2.getTrust() - o1.getTrust();
				if (sub == 0) {
					int len1 = getCharLength(o1.getValue());
					int len2 = getCharLength(o2.getValue());
					return len2 - len1;
				}
				return sub;
			}

			private int getCharLength(String o1) {
				if (o1 == null) {
					return -1;
				}
				try {
					return o1.getBytes("GBK").length;
				} catch (UnsupportedEncodingException e) {
					log.warn("Chars:" + o1 + ",cause:", e);
				}
				return -1;
			}
		};
		BrandTokenizer tokenizer = new BrandTokenizer(synonymBrandService);
		String sTokenizer = tokenizer.getClass().getName();
		for (ProductDto dto : dataList) {
			List<EntityToken> entityList = new ArrayList<EntityToken>();
			EntityToken entity = new EntityToken(dto.getProductName());
			entity.addAssistToken(new SectionToken("productBrand", dto.getProductBrand()));
			entityList.add(entity);
			tokenizer.identify(entityList);
			List<SectionToken> leaveList = new ArrayList<SectionToken>();
			EntityToken.getLeveChildren(leaveList, entity.getMaster());
			List<SectionToken> brandList = EntityToken.getTokensByTokenizer(leaveList, sTokenizer);
			if (!brandList.isEmpty()) {
				Collections.sort(brandList, comparator);
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
