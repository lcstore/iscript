package com.lezo.iscript.match.job;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.log4j.Log4j;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.BaseSimilarJobHandler;
import com.lezo.iscript.match.SimilarJobHandler;
import com.lezo.iscript.service.crawler.dto.SimilarJobDto;
import com.lezo.iscript.service.crawler.service.SimilarJobService;
import com.lezo.iscript.similar.SimilarParam;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Log4j
public class SimilarJobScanJob implements Runnable {
    private static AtomicBoolean running = new AtomicBoolean(false);

    public static void main(String[] args) {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        SimilarJobHandler handler = new BaseSimilarJobHandler();
        SimilarJobDto jobDto = new SimilarJobDto();
        SimilarParam param = new SimilarParam();
        Set<String> idSet = Sets.newHashSet("997951");
        param.setIdSet(idSet);
        param.setIdType(SimilarParam.TYPE_PRODUCT_CODE);
        param.setSiteId(1001);
        jobDto.setInputs(JSON.toJSONString(param));
        jobDto.setId(1000L);
        jobDto.setName("similar");
        handler.handle(jobDto);

    }

    @Override
    public void run() {
        if (running.get()) {
            log.warn("ClusterSimilarJob is running..");
            return;
        }
        long start = System.currentTimeMillis();
        try {
            running.set(true);
            SimilarJobService similarJobService = SpringBeanUtils.getBean(SimilarJobService.class);
        } catch (Exception e) {
            log.warn("", e);
        } finally {
            running.set(false);
        }
    }
}
