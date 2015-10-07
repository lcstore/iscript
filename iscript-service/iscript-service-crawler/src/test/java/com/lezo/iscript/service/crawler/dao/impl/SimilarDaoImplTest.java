package com.lezo.iscript.service.crawler.dao.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.SimilarDao;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;

public class SimilarDaoImplTest {

    @Test
    public void testUpdateBarCode() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        SimilarDao similarDao = SpringBeanUtils.getBean(SimilarDao.class);
        List<SimilarDto> dtoList = new ArrayList<SimilarDto>(100);
        Reader reader = new FileReader("/Users/lezo/Downloads/jd.all.UPC");
        BufferedReader bReader = new BufferedReader(reader);
        int count = 0;
        while (bReader.ready()) {
            String line = bReader.readLine();
            String[] uArr = line.split("\t");
            SimilarDto dto = new SimilarDto();
            dto.setBarCode(uArr[0]);
            dto.setSkuCode("1001_" + uArr[1]);
            dtoList.add(dto);
            count++;
            if (dtoList.size() == 100) {
                System.err.println("update barCode.total:" + count);
                similarDao.batchUpdateBarCodeBySkuCode(dtoList);
                dtoList.clear();
            }
        }
        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(bReader);
        if (!dtoList.isEmpty()) {
            similarDao.batchUpdateBarCodeBySkuCode(dtoList);
        }
        System.err.println("done.batch update BarCode");
    }

}
