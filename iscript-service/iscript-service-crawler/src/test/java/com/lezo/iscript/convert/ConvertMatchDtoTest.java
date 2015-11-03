package com.lezo.iscript.convert;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ConvertMatchDtoTest {

    @Test
    public void testAddNewBrands() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
        String jobId = "";
        int siteId = 1002;
        Long fromId = 0L;
        int limit = Integer.MAX_VALUE;
        List<SimilarDto> dtoList = similarService.getSimilarDtoByJobIdSiteId(jobId, siteId, fromId, limit);
        List<MatchDto> matchDtos = Lists.newArrayList();
        Date curDate = new Date();
        for (SimilarDto dto : dtoList) {
            MatchDto mDto = new MatchDto();
            BeanUtils.copyProperties(dto, mDto);
            matchDtos.add(mDto);
            mDto.setId(null);
            mDto.setConfirmModel(MatchDto.CONFIRM_MODEL_SEMI);
            mDto.setCreateTime(curDate);
            mDto.setUpdateTime(mDto.getCreateTime());
        }
        SpringBeanUtils.getBean(MatchService.class).batchSaveDtos(matchDtos);
        cx.close();
        System.err.println("done....size:" + dtoList.size());
    }
}
