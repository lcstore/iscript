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
public class UnitMapper {
    private static final UnitMapper INSTANCE = new UnitMapper();
    private static final String encoding = "UTF-8";
    private static final String PATH_UNIT_DIC = "dic/unit.dic";
    private Map<String, Set<String>> unitMap;
    private int minLen = Integer.MAX_VALUE;
    private int maxLen = Integer.MIN_VALUE;

    private Map<String, Set<String>> getMap() {
        if (unitMap != null) {
            return unitMap;
        }
        synchronized (INSTANCE) {
            if (unitMap == null) {
                InputStream in = UnitMapper.class.getClassLoader().getResourceAsStream(PATH_UNIT_DIC);
                try {
                    List<String> lines = IOUtils.readLines(in, encoding);
                    Map<String, Set<String>> temMap = Maps.newHashMap();
                    for (String line : lines) {
                        StringTokenizer tokenizer = new StringTokenizer(line, "=");
                        Set<String> newSet = Sets.newHashSet();
                        while (tokenizer.hasMoreTokens()) {
                            String token = tokenizer.nextToken().trim();
                            if (StringUtils.isNotBlank(token)) {
                                token = toUnify(token);
                                newSet.add(token);
                            }
                        }
                        for (String value : newSet) {
                            Set<String> hasSet = temMap.get(value);
                            if (hasSet != null) {
                                log.warn("duplicat unit:" + value);
                            } else {
                                int len = value.length();
                                if (minLen > len) {
                                    minLen = len;
                                }
                                if (maxLen < len) {
                                    maxLen = len;
                                }
                                temMap.put(value, newSet);
                            }
                        }
                    }
                    unitMap = temMap;
                } catch (IOException e) {
                    log.error("load unit dic:" + PATH_UNIT_DIC + ",cause:", e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        }
        return unitMap;
    }

    public Set<String> getUnitSet(String token) {
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
}
