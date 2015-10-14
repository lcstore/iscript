package com.lezo.iscript.match.map;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.map.loader.DicLoader;
import com.lezo.iscript.match.map.loader.LineDicLoader;

@Log4j
public class BrandMapper {
    private static final BrandMapper INSTANCE = new BrandMapper();
    private static final String PATH_UNIT_DIC = "dic/brand.dic";
    private DicLoader loader = new LineDicLoader();
    private Map<String, Set<String>> dataMap;
    private int minLen = Integer.MAX_VALUE;
    private int maxLen = Integer.MIN_VALUE;

    private Map<String, Set<String>> getMap() {
        if (dataMap != null) {
            return dataMap;
        }
        synchronized (INSTANCE) {
            if (dataMap == null) {
                InputStream in = BrandMapper.class.getClassLoader().getResourceAsStream(PATH_UNIT_DIC);
                try {
                    dataMap = loader.loadDic(in);
                } catch (Exception e) {
                    log.error("load brand dic,cause:", e);
                }
            }
        }
        return dataMap;
    }

    public Set<String> getSameSet(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        token = toUnify(token);
        return getMap().get(token);
    }

    private String toUnify(String token) {
        if (token == null) {
            return null;
        }
        return token.toLowerCase();
    }

    public Iterator<String> iterator() {
        return getMap().keySet().iterator();
    }

    public static BrandMapper getInstance() {
        return INSTANCE;
    }

    public int getMinLen() {
        getMap();
        return minLen;
    }

    public int getMaxLen() {
        getMap();
        return maxLen;
    }

    public DicLoader getLoader() {
        return loader;
    }
}
