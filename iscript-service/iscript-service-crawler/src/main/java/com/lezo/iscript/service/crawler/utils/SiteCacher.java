package com.lezo.iscript.service.crawler.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.service.crawler.dao.SiteDao;
import com.lezo.iscript.service.crawler.dto.SiteDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.URLUtils;

public class SiteCacher {
    private static Logger logger = LoggerFactory.getLogger(SiteCacher.class);
    private static final SiteCacher INSTANCE = new SiteCacher();
    private static final Integer SITE_LEVEL_V2 = 2;
    private static Map<Integer, SiteDto> id2DtoMap = null;
    private static Map<String, SiteDto> code2DtoMap;
    private static Map<String, Set<String>> codeSetMap;

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
        Map<String, SiteDto> codeDtoMap = Maps.newHashMap();
        Map<String, Set<String>> v2CodeSetMap = Maps.newHashMap();
        for (SiteDto dto : dtoList) {
            id2DtoMap.put(dto.getId(), dto);
            codeDtoMap.put(dto.getSiteCode(), dto);
            if (dto.getSiteLevel() > SITE_LEVEL_V2) {
                String root = URLUtils.getRootHost(dto.getSiteUrl());
                Set<String> codeSet = v2CodeSetMap.get(root);
                if (codeSet == null) {
                    codeSet = Sets.newHashSet();
                    v2CodeSetMap.put(root, codeSet);
                }
                codeSet.add(dto.getSiteCode());
            }
        }
        SiteCacher.id2DtoMap = id2DtoMap;
        SiteCacher.code2DtoMap = codeDtoMap;
        SiteCacher.codeSetMap = v2CodeSetMap;
    }

    public SiteDto getDomainSiteDto(String url) {
        ensureLoaded();
        String domainV2 = URLUtils.getRootHost(url);
        Set<String> codeSet = codeSetMap.get(domainV2);
        SiteDto siteDto = null;
        if (codeSet != null) {
            String host = URLUtils.getHost(url);
            for (String code : codeSet) {
                String sRemainCode = code.replace(domainV2, "");
                if (host.contains(sRemainCode)) {
                    siteDto = code2DtoMap.get(code);
                    break;
                }
            }
        }
        if (siteDto == null) {
            siteDto = code2DtoMap.get(domainV2);
        }
        if (siteDto == null) {
            logger.warn("can not found domain from Url:" + url);
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
