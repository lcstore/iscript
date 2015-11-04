package com.lezo.iscript.convert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class ConvertMatchDtoTest {

    @Test
    public void testBarCode2Match() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
        String jobId = "1446351659066";
        int siteId = 1002;
        Long fromId = 0L;
        int limit = 200;
        List<SimilarDto> dtoList = similarService.getSimilarDtoByJobIdSiteId(jobId, siteId, fromId, limit);
        while (true) {
            List<SimilarDto> subList = similarService.getSimilarDtoByJobIdSiteId(jobId, siteId, fromId, limit);
            for (SimilarDto sDto : subList) {
                if (fromId < sDto.getId()) {
                    fromId = sDto.getId();
                }
            }
            dtoList.addAll(subList);
            System.err.println("query dto:" + subList.size() + ",dtoList:" + dtoList.size());
            if (subList.size() < limit) {
                break;
            }
        }
        Date curDate = new Date();
        Set<String> sCodeSet = Sets.newHashSet();
        Map<String, List<MatchDto>> barCodeMap = Maps.newHashMap();
        for (SimilarDto dto : dtoList) {
            MatchDto mDto = new MatchDto();
            BeanUtils.copyProperties(dto, mDto);
            mDto.setId(null);
            mDto.setConfirmModel(MatchDto.CONFIRM_MODEL_SEMI);
            mDto.setCreateTime(curDate);
            mDto.setUpdateTime(mDto.getCreateTime());
            List<MatchDto> bcList = barCodeMap.get(mDto.getBarCode());
            if (bcList == null) {
                bcList = Lists.newArrayList();
                barCodeMap.put(mDto.getBarCode(), bcList);
            }
            if (!sCodeSet.contains(mDto.getSkuCode())) {
                bcList.add(mDto);
                sCodeSet.add(mDto.getSkuCode());
            }
        }
        List<MatchDto> matchDtos = Lists.newArrayList();
        for (Entry<String, List<MatchDto>> entry : barCodeMap.entrySet()) {
            String mCode = MatchDto.newMatchCode();
            for (MatchDto valDto : entry.getValue()) {
                valDto.setMatchCode(mCode);
            }
            matchDtos.addAll(entry.getValue());
        }
        System.err.println("done....size:" + dtoList.size() + ",match count:" + matchDtos.size());
        SpringBeanUtils.getBean(MatchService.class).batchSaveDtos(matchDtos);
        cx.close();
        System.err.println("done....size:" + dtoList.size());
    }
}
