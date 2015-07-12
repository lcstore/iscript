package com.lezo.iscript.service.crawler.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import com.lezo.iscript.common.UnifyValueUtils;
import com.lezo.iscript.service.crawler.DaoBaseTest;
import com.lezo.iscript.service.crawler.dao.SimilarDao;
import com.lezo.iscript.service.crawler.dao.SkuRankDao;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.dto.SkuRankDto;

public class SkuRankDaoTest extends DaoBaseTest {

    @Test
    public void testCopyData() throws Exception {
        SimilarDao similarDao = getBean(SimilarDao.class);
        SkuRankDao skuRankDao = getBean(SkuRankDao.class);
        List<String> lineList =
                FileUtils.readLines(new File("src/test/resources/nav.txt"), "UTF-8");
        List<Long> idList = new ArrayList<Long>();
        for (String line : lineList) {
            idList.add(Long.valueOf(line));
        }
        // idList.add(1L);
        List<SimilarDto> dtoList = similarDao.getDtoByIds(idList);
        List<SkuRankDto> rankList = new ArrayList<SkuRankDto>();

        for (SimilarDto dto : dtoList) {
            SkuRankDto rankDto = new SkuRankDto();
            BeanUtils.copyProperties(dto, rankDto);
            rankDto.setCreateTime(new Date());
            rankDto.setUpdateTime(rankDto.getCreateTime());
            rankDto.setMatchCode(System.currentTimeMillis() + new Random().nextInt());
            rankList.add(rankDto);
        }
        UnifyValueUtils.unifyQuietly(rankList);
        skuRankDao.batchInsert(rankList);
    }
}
