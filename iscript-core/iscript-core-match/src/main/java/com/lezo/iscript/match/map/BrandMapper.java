package com.lezo.iscript.match.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Log4j
public class BrandMapper {
    private static final BrandMapper INSTANCE = new BrandMapper();
    private static final String encoding = "UTF-8";
    private static final String PATH_BRAND_DIC = "dic/brand.dic";
    private Map<String, Set<String>> brandMap;

    private Map<String, Set<String>> getMap() {
        if (brandMap != null) {
            return brandMap;
        }
        synchronized (INSTANCE) {
            if (brandMap == null) {
                InputStream in = BrandMapper.class.getClassLoader().getResourceAsStream(PATH_BRAND_DIC);
                try {
                    List<String> lines = IOUtils.readLines(in, encoding);
                    Map<String, Set<String>> temMap = Maps.newHashMap();
                    for (String line : lines) {
                        StringTokenizer tokenizer = new StringTokenizer(line, "=");
                        Set<String> newSet = Sets.newHashSet();
                        while (tokenizer.hasMoreTokens()) {
                            String token = tokenizer.nextToken().trim();
                            if (StringUtils.isBlank(token)) {
                                newSet.add(token);
                            }
                        }
                        for (String value : newSet) {
                            Set<String> hasSet = temMap.get(value);
                            if (hasSet != null) {
                                log.warn("duplicat unit:" + value);
                            } else {
                                temMap.put(value, newSet);
                            }
                        }
                    }
                    brandMap = temMap;
                } catch (IOException e) {
                    log.error("load unit dic:" + PATH_BRAND_DIC + ",cause:", e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        }
        return brandMap;
    }

    public Set<String> getBrandSet(String token) {
        return getMap().get(token);
    }

    public Iterator<String> iterator() {
        return getMap().keySet().iterator();
    }

    public static BrandMapper getInstance() {
        return INSTANCE;
    }
}
