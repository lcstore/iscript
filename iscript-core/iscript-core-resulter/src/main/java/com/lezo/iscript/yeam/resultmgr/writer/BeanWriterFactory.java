package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.HashMap;
import java.util.Map;

import com.lezo.iscript.common.ObjectWriter;
import com.lezo.iscript.service.crawler.dto.ProductDto;

public class BeanWriterFactory {

	private static final Map<String, Class<? extends ObjectWriter<?>>> BEAN_WRITER_MAP = new HashMap<String, Class<? extends ObjectWriter<?>>>();
	static {
		BEAN_WRITER_MAP.put(ProductDto.class.getName(), ProductWriter.class);
	}
}
