package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.BrandDto;
import com.lezo.iscript.service.crawler.service.BrandService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年9月26日
 */
public class BrandWriter implements ObjectWriter<BrandDto> {
	private BrandService brandService = SpringBeanUtils.getBean(BrandService.class);

	@Override
	public void write(List<BrandDto> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		synchronized (this) {
			brandService.batchSaveDtos(dataList);
		}
	}

}
