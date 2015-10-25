package com.lezo.iscript.service.crawler.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.MapUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.service.crawler.dto.BrandRepoDto;
import com.lezo.iscript.service.crawler.service.BrandRepoService;
import com.lezo.iscript.spring.context.SpringBeanUtils;

@Log4j
public class BrandRepoCacher {
    private static final BrandRepoCacher INSTANCE = new BrandRepoCacher();
    private static final String BRAND_SPLITER = ",";
    private Map<String, Set<String>> coreMap;

    private void ensuerCache() {
        if (coreMap != null) {
            return;
        }
        synchronized (BrandRepoCacher.class) {
            if (coreMap != null) {
                return;
            }
            BrandRepoService brandRepoService = SpringBeanUtils.getBean(BrandRepoService.class);
            Map<String, Set<String>> temMap = Maps.newHashMap();
            int limit = 500;
            long fromId = 0L;
            int query = 0;
            int total = 0;
            while (true) {
                List<BrandRepoDto> hasList = brandRepoService.getDtoByIdWithLimit(fromId, limit);
                for (BrandRepoDto hasDto : hasList) {
                    temMap.put(hasDto.getCoreName(), Sets.newHashSet(hasDto.getIncludes().split(BRAND_SPLITER)));
                    if (fromId < hasDto.getId()) {
                        fromId = hasDto.getId();
                    }
                }
                query++;
                total += hasList.size();
                log.info("query:" + query + ",fromId:" + fromId + ",count:" + hasList.size() + ",total:" + total);
                if (hasList.size() < limit) {
                    break;
                }
            }
            coreMap = MapUtils.unmodifiableMap(temMap);
        }
    }

    public Map<String, Set<String>> getMap() {
        ensuerCache();
        return coreMap;
    }

    public static BrandRepoCacher getInstance() {
        return INSTANCE;
    }
}
