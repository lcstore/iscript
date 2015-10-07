package com.lezo.iscript.service.crawler.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dao.SiteDao;
import com.lezo.iscript.service.crawler.dto.SiteDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.URLUtils;

public class SiteCacher {
    private static Logger logger = LoggerFactory.getLogger(SiteCacher.class);
    private static final SiteCacher INSTANCE = new SiteCacher();
    private static final Integer SITE_LEVEL_V2 = 2;
    private static final Integer SITE_LEVEL_V3 = 3;
    private static Map<Integer, SiteDto> id2DtoMap = null;
    private static Map<Integer, Map<String, SiteDto>> level2SiteMap = null;

    public SiteCacher() {
    }

    public static SiteCacher getInstance() {
        return INSTANCE;
    }

    private synchronized void ensureLoaded() {
        if (id2DtoMap != null) {
            return;
        }
        SiteDao siteDao = SpringBeanUtils.getBean(SiteDao.class);
        List<SiteDto> dtoList = siteDao.getSiteDtoByLevel(null);
        Map<Integer, SiteDto> id2DtoMap = new HashMap<Integer, SiteDto>();
        Map<Integer, Map<String, SiteDto>> level2SiteMap = new HashMap<Integer, Map<String, SiteDto>>();
        for (SiteDto dto : dtoList) {
            Map<String, SiteDto> siteMap = level2SiteMap.get(dto.getSiteLevel());
            if (siteMap == null) {
                siteMap = new HashMap<String, SiteDto>();
                level2SiteMap.put(dto.getSiteLevel(), siteMap);
            }
            id2DtoMap.put(dto.getId(), dto);
            siteMap.put(dto.getSiteCode(), dto);
        }
        SiteCacher.id2DtoMap = id2DtoMap;
        SiteCacher.level2SiteMap = level2SiteMap;
    }

    public SiteDto getDomainSiteDto(String domainUrl) {
        ensureLoaded();
        String domainV2 = URLUtils.getRootHost(domainUrl);
        if (domainV2.equals("tmall.com") && domainUrl.indexOf("chaoshi") > 0) {
            String domainV3 = "chaoshi.tmall.com";
            return SiteCacher.level2SiteMap.get(SITE_LEVEL_V3).get(domainV3);
        }
        Pattern oReg = Pattern.compile("[a-zA-Z0-9]\\." + domainV2);
        Matcher matcher = oReg.matcher(domainUrl);
        SiteDto siteDto = SiteCacher.level2SiteMap.get(SITE_LEVEL_V2).get(domainV2);
        if (siteDto == null) {
            if (matcher.find()) {
                domainV2 = matcher.group();
                siteDto = SiteCacher.level2SiteMap.get(SITE_LEVEL_V3).get(domainV2);
            }
        }
        if (siteDto == null) {
            logger.warn("can not found domain from Url:" + domainUrl);
        }
        return siteDto;
    }

    public SiteDto getSiteDto(Integer siteId) {
        if (siteId == null) {
            return null;
        }
        ensureLoaded();
        SiteDto curSiteDto = id2DtoMap.get(siteId);
        if (curSiteDto == null) {
            logger.warn("can not found site[" + siteId + "].reload site info..");
        }
        return curSiteDto;
    }
}
