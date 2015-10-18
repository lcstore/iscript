package com.lezo.iscript.match.map;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lezo.iscript.match.map.loader.DicLoader;
import com.lezo.iscript.match.map.loader.LineDicLoader;

@Log4j
public class BrandMapper {
    private static final BrandMapper INSTANCE = new BrandMapper();
    private static final String PATH_DIC = "dic/brand.dic";
    private DicLoader loader = new LineDicLoader();
    private Map<String, SameEntity> dataMap;
    private Map<Character, Set<SameEntity>> firstCharMap;
    private int minLen = Integer.MAX_VALUE;
    private int maxLen = Integer.MIN_VALUE;

    private Map<String, SameEntity> getMap() {
        if (dataMap != null) {
            return dataMap;
        }
        synchronized (INSTANCE) {
            if (dataMap == null) {
                try {
                    Map<String, SameEntity> entityMap = createEntityMap();
                    calcKeyLength(entityMap);
                    firstCharMap = createCharMap(entityMap);
                    dataMap = entityMap;
                } catch (Exception e) {
                    log.error("load brand dic,cause:", e);
                }
            }
        }
        return dataMap;
    }

    protected Map<Character, Set<SameEntity>> createCharMap(Map<String, SameEntity> dataMap) {
        Map<Character, Set<SameEntity>> firstCharMap = Maps.newHashMap();
        for (Entry<String, SameEntity> entry : dataMap.entrySet()) {
            for (String sVal : entry.getValue().getSameSet()) {
                Character chKey = sVal.charAt(0);
                Set<SameEntity> sCharSet = firstCharMap.get(chKey);
                if (sCharSet == null) {
                    sCharSet = Sets.newHashSet();
                    firstCharMap.put(chKey, sCharSet);
                }
                sCharSet.add(entry.getValue());
            }
        }
        return firstCharMap;
    }

    private void calcKeyLength(Map<String, SameEntity> dataMap) {
        minLen = Integer.MAX_VALUE;
        maxLen = Integer.MIN_VALUE;
        for (String key : dataMap.keySet()) {
            int len = key.length();
            if (minLen > len) {
                minLen = len;
            }
            if (maxLen < len) {
                maxLen = len;
            }
        }
    }

    protected Map<String, SameEntity> createEntityMap() throws Exception {
        InputStream in = BrandMapper.class.getClassLoader().getResourceAsStream(PATH_DIC);
        return loader.loadDic(in);
    }

    public Set<SameEntity> getEntitySet(Character firstChar) {
        getMap();
        return firstCharMap.get(firstChar);
    }

    public SameEntity getSameEntity(String token) {
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

    public void setLoader(DicLoader loader) {
        dataMap = null;
        this.loader = loader;
    }
}
