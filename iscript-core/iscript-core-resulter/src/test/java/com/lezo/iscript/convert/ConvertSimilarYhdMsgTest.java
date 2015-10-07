package com.lezo.iscript.convert;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-config-ds.xml" })
// @Transactional
// @TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Log4j
public class ConvertSimilarYhdMsgTest {
    private static final String encoding = "UTF-8";
    @Autowired
    private SimilarService similarService;

    @Test
    public void testWorker() {
        List<SimilarDto> similarDtos = Lists.newArrayList();
        SimilarDto dto = new SimilarDto();
        dto.setProductUrl("http://item.yhd.com/item/1056207");
        similarDtos.add(dto);
        Runnable worker = newWorker(similarDtos);
        worker.run();
    }

    @Test
    public void testConvertMsg() throws Exception {
        long start = System.currentTimeMillis();
        int siteId = 1002;
        String jobId = "20151003-one.match";
        List<SimilarDto> dtoList = similarService.getSimilarDtoByJobIdSiteId(jobId, siteId);
        Map<String, List<SimilarDto>> skuMap = Maps.newHashMap();
        for (SimilarDto dto : dtoList) {
            List<SimilarDto> hasList = skuMap.get(dto.getSkuCode());
            if (hasList == null) {
                hasList = Lists.newArrayList();
                skuMap.put(dto.getSkuCode(), hasList);
            }
            hasList.add(dto);
        }
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        for (Entry<String, List<SimilarDto>> entry : skuMap.entrySet()) {
            final List<SimilarDto> similarDtos = entry.getValue();
            Runnable worker = newWorker(similarDtos);
            executor.execute(worker);
        }
        executor.shutdown();
        waitForDone(executor);
        for (int i = 0; i < 3; i++) {
            try {
                similarService.batchUpdateSimilarDtos(dtoList);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long cost = System.currentTimeMillis() - start;
        log.info("done...,cost:" + cost);
    }

    private static void waitForDone(ThreadPoolExecutor exec) throws Exception {
        exec.shutdown();
        while (!exec.isTerminated()) {
            log.info("active:" + exec.getActiveCount() + ",done:"
                    + exec.getCompletedTaskCount() + ",queue:" + exec.getQueue().size());
            TimeUnit.SECONDS.sleep(1);
        }

    }

    private Runnable newWorker(final List<SimilarDto> similarDtos) {
        return (new Runnable() {

            @Override
            public void run() {
                String sUrl = similarDtos.get(0).getProductUrl();
                int retry = 0;
                while (true) {
                    try {
                        Document dom =
                                Jsoup.connect(sUrl)
                                        .userAgent(
                                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:41.0) Gecko/20100101 Firefox/41.0")
                                        .cookie("detail_yhdareas",
                                                "1_1_3_%E4%B8%8A%E6%B5%B7%3Ci%3E%3C%2Fi%3E_%E4%B8%8A%E6%B5%B7%E5%B8%82%3Ci%3E%3C%2Fi%3E_%E9%BB%84%E6%B5%A6%E5%8C%BA%3Ci%3E%3C%2Fi%3E")
                                        .cookie("provinceId", "1")
                                        .cookie("grouponAreaId", "1")
                                        .get();
                        Elements crumbELs = dom.select("div.detail_wrap div.mod_detail_crumb div.crumb");
                        if (crumbELs.isEmpty()) {
                            for (SimilarDto sDto : similarDtos) {
                                sDto.setProductCode(StringUtils.EMPTY);
                            }
                        } else {
                            Elements pmIdEls = dom.select("#productMercantId[value]");
                            if (!pmIdEls.isEmpty()) {
                                for (SimilarDto sDto : similarDtos) {
                                    sDto.setProductCode(pmIdEls.first().val().trim());
                                    sDto.setSkuCode("1002_" + sDto.getProductCode());
                                }
                            }
                            Elements shareEls = dom.select("span.sharelist a[href].ico_sina");
                            if (!shareEls.isEmpty()) {
                                String jsCode = shareEls.first().attr("href");
                                Pattern priceReg = Pattern.compile("价格.*￥([0-9.]+)");
                                Matcher matcher = priceReg.matcher(jsCode);
                                if (matcher.find()) {
                                    Float fPrice = Float.valueOf(matcher.group(1));
                                    for (SimilarDto sDto : similarDtos) {
                                        sDto.setMarketPrice(fPrice);
                                    }
                                }
                            }

                        }
                        break;
                    } catch (IOException e) {
                        retry++;
                        if (retry == 3) {
                            log.error("connet fail.sUrl:" + sUrl, e);
                            break;
                        }
                        log.warn("connet retry:" + retry + ".sUrl:" + sUrl);
                    }

                }

            }
        });
    }
}
