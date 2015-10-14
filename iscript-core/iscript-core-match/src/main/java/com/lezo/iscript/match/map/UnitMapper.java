package com.lezo.iscript.match.map;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;

import com.lezo.iscript.match.map.loader.DicLoader;
import com.lezo.iscript.match.map.loader.LineDicLoader;

@Log4j
public class UnitMapper {
    private static final UnitMapper INSTANCE = new UnitMapper();
    private static final String PATH_DIC = "dic/unit.dic";
    private DicLoader loader = new LineDicLoader();
    private Map<String, SameChars> dataMap;
    private int minLen = Integer.MAX_VALUE;
    private int maxLen = Integer.MIN_VALUE;

    private Map<String, SameChars> getMap() {
        if (dataMap != null) {
            return dataMap;
        }
        synchronized (INSTANCE) {
            if (dataMap == null) {
                InputStream in = UnitMapper.class.getClassLoader().getResourceAsStream(PATH_DIC);
                try {
                    dataMap = loader.loadDic(in);
                    for (String key : dataMap.keySet()) {
                        int len = key.length();
                        if (minLen > len) {
                            minLen = len;
                        }
                        if (maxLen < len) {
                            maxLen = len;
                        }
                    }
                } catch (Exception e) {
                    log.error("load unit dic,cause:", e);
                }
            }
        }
        return dataMap;
    }

    public SameChars getSameSet(String token) {
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

    public static UnitMapper getInstance() {
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
