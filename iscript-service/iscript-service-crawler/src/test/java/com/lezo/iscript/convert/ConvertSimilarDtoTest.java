package com.lezo.iscript.convert;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BatchIterator;

public class ConvertSimilarDtoTest {

    @Test
    public void testUpdateBarCodeSimilarDtos() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
        List<String> dataList = FileUtils.readLines(new File("src/test/resources/data/code.txt"), "UTF-8");
        BatchIterator<String> it = new BatchIterator<String>(dataList);
        int total = 0;
        while (it.hasNext()) {
            List<SimilarDto> dtoList = similarService.getSimilarDtoBySkuCodes(it.next());
            Map<String, List<SimilarDto>> sCodeMap = Maps.newHashMap();
            for (SimilarDto dto : dtoList) {
                List<SimilarDto> sameList = sCodeMap.get(dto.getSkuCode());
                if (sameList == null) {
                    sameList = Lists.newArrayList();
                    sCodeMap.put(dto.getSkuCode(), sameList);
                }
                sameList.add(dto);
            }
            for (Entry<String, List<SimilarDto>> entry : sCodeMap.entrySet()) {
                for (SimilarDto referDto : entry.getValue()) {
                    if (StringUtils.isBlank(referDto.getBarCode())) {
                        continue;
                    }
                    for (SimilarDto curDto : entry.getValue()) {
                        if (curDto == referDto) {
                            continue;
                        }
                        if (StringUtils.isBlank(referDto.getTokenCategory())
                                && StringUtils.isNotBlank(curDto.getTokenCategory())) {
                            referDto.setTokenCategory(curDto.getTokenCategory());
                        }
                        if (referDto.getShopId() < 1 && curDto.getShopId() > 1) {
                            referDto.setShopId(curDto.getShopId());
                        }
                    }
                }
            }
            similarService.batchUpdateSimilarDtos(dtoList);
            total += dtoList.size();
            System.err.println("update....size:" + dtoList.size() + ",total:" + total);
        }
        cx.close();
        System.err.println("done....total:" + total);
    }
}
