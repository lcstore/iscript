package com.lezo.iscript.service.crawler.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.BeanCopyUtils;
import com.lezo.iscript.service.crawler.dao.BrandDao;
import com.lezo.iscript.service.crawler.dto.BrandDto;
import com.lezo.iscript.service.crawler.service.BrandService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.vo.BrandConfigVo;

public class BrandServiceImplTest {

	@Test
	public void testBatchInsert() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		BrandDao brandDao = SpringBeanUtils.getBean(BrandDao.class);
		BrandService brandService = new BrandServiceImpl();
		String type = "";
		List<String> lineList = FileUtils.readLines(new File("src/test/resources/nav.txt"), "UTF-8");
		List<BrandDto> dataList = new ArrayList<BrandDto>();
		for (String line : lineList) {
			Map<String, List<Object>> targetMap = BeanCopyUtils.doHanlde(type, JSONUtils.getJSONObject(line));
			List<Object> srcList = targetMap.get("BrandConfigVo");
			for (Object dataObject : srcList) {
				dataList.addAll(convertDto((BrandConfigVo) dataObject));
			}
		}
		brandService.batchSaveDtos(dataList);
	}

	private List<BrandDto> convertDto(BrandConfigVo brandVo) throws CloneNotSupportedException {
		if (StringUtils.isEmpty(brandVo.getSynonyms())) {
			return Collections.emptyList();
		}
		List<BrandDto> brandList = new ArrayList<BrandDto>();
		String sSynonyms = brandVo.getSynonyms();
		int fromIndex = sSynonyms.indexOf("[");
		int toIndex = sSynonyms.indexOf("]");
		fromIndex = fromIndex < 0 ? 0 : fromIndex + 1;
		toIndex = toIndex < 0 ? sSynonyms.length() : toIndex;
		sSynonyms = sSynonyms.substring(fromIndex, toIndex);
		String[] synStrings = sSynonyms.split(",");
		String synCode = "" + UUID.randomUUID().toString();

		for (int i = 0; i < synStrings.length; i++) {
			BrandDto baseDto = new BrandDto();
			baseDto.setBrandCode(brandVo.getBrandCode());
			baseDto.setBrandUrl(brandVo.getBrandUrl());
			baseDto.setRegion(brandVo.getRegion());
			baseDto.setCreateTime(brandVo.getCreateTime());
			baseDto.setUpdateTime(brandVo.getUpdateTime());
			baseDto.setSiteId(brandVo.getSiteId());
			baseDto.setSynonymCode(synCode);
			baseDto.setBrandName(synStrings[i]);
			brandList.add(baseDto);
		}
		return brandList;
	}
}
