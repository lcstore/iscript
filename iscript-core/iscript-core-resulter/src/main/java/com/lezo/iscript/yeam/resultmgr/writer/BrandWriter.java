package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.BrandDto;
import com.lezo.iscript.service.crawler.service.BrandService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.resultmgr.vo.BrandConfigVo;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class BrandWriter implements ObjectWriter<BrandConfigVo> {
	private BrandService brandService = SpringBeanUtils.getBean(BrandService.class);

	@Override
	public void write(List<BrandConfigVo> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		try {
			List<BrandDto> brandList = convertDto(dataList);
			synchronized (this) {
				brandService.batchSaveDtos(brandList);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private List<BrandDto> convertDto(List<BrandConfigVo> dataList) throws CloneNotSupportedException {
		List<BrandDto> brandList = new ArrayList<BrandDto>(dataList.size());
		Set<String> hasSet = new HashSet<String>();
		for (BrandConfigVo brandVo : dataList) {
			if (StringUtils.isEmpty(brandVo.getSynonyms())) {
				continue;
			}
			String key = brandVo.getSiteId() + "-" + brandVo.getBrandCode();
			if (hasSet.contains(key)) {
				continue;
			}
			hasSet.add(key);
			String sSynonyms = brandVo.getSynonyms();
			int fromIndex = sSynonyms.indexOf("[");
			int toIndex = sSynonyms.indexOf("]");
			fromIndex = fromIndex < 0 ? 0 : fromIndex + 1;
			toIndex = toIndex < 0 ? sSynonyms.length() : toIndex;
			sSynonyms = sSynonyms.substring(fromIndex, toIndex);
			String[] synStrings = sSynonyms.split(",");
			String synCode = BrandDto.randomSynonymCode();

			BrandDto baseDto = new BrandDto();
			baseDto.setBrandName(synStrings[0].trim());
			baseDto.setBrandCode(brandVo.getBrandCode());
			baseDto.setBrandUrl(brandVo.getBrandUrl());
			baseDto.setRegion(brandVo.getRegion());
			baseDto.setCreateTime(brandVo.getCreateTime());
			baseDto.setUpdateTime(brandVo.getUpdateTime());
			baseDto.setSiteId(brandVo.getSiteId());
			baseDto.setSynonymCode(synCode);
			brandList.add(baseDto);
			for (int i = 1; i < synStrings.length; i++) {
				BrandDto cloneDto = (BrandDto) baseDto.clone();
				cloneDto.setBrandName(synStrings[i].trim());
				brandList.add(cloneDto);
			}
		}
		return brandList;
	}

}
