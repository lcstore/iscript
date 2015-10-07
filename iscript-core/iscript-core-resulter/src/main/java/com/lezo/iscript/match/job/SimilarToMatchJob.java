package com.lezo.iscript.match.job;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.log4j.Log4j;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Log4j
public class SimilarToMatchJob implements Runnable {
    private static final int MIN_SCORE = 70;
    private static AtomicBoolean running = new AtomicBoolean(false);

    public static void main(String[] args) {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        new SimilarToMatchJob().run();
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
            SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
            MatchService matchService = SpringBeanUtils.getBean(MatchService.class);
            List<String> matchCodes = matchService.getMatchCodeWithNullItemCode();
            long cost = System.currentTimeMillis() - start;

        } catch (Exception e) {
            log.warn("", e);
        } finally {
            running.set(false);
        }
    }

}
