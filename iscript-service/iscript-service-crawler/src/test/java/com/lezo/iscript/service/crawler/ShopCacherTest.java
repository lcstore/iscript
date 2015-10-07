package com.lezo.iscript.service.crawler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lezo.iscript.service.crawler.dto.SiteDto;
import com.lezo.iscript.service.crawler.utils.SiteCacher;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-config-ds.xml" })
public class ShopCacherTest {

    @Test
    public void testSiteCode() {
        SiteCacher siteCacher = SiteCacher.getInstance();
        String domainUrl =
                "https://chaoshi.detail.tmall.com/item.htm?spm=a3204.7084713.2996785438.12.aa7cDy&acm=201506250.1003.1.399393&aldid=dyYC1nn4&scm=1003.1.201506250.13_43361139462_399393&pos=6&userBucket=5&id=43361139462";
        SiteDto siteDto = siteCacher.getDomainSiteDto(domainUrl);
        System.err.println(siteDto.getId() + "," + siteDto.getSiteCode());

	}
}
