package com.lezo.iscript.match.map.loader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Log4j
public class LineDicLoader implements DicLoader {
    private static final String encoding = "UTF-8";

    @Override
    public Map<String, Set<String>> loadDic(String dicPath) throws Exception {
        return loadDic(new FileInputStream(dicPath));
    }

    @Override
    public Map<String, Set<String>> loadDic(InputStream in) throws Exception {
        InputStreamReader isr = null;
        BufferedReader bReader = null;
        try {
            isr = new InputStreamReader(in, encoding);
            bReader = new BufferedReader(isr);
            Map<String, Set<String>> temMap = Maps.newHashMap();
            while (bReader.ready()) {
                String line = bReader.readLine();
                if (line == null) {
                    break;
                }
                Set<String> sameSet = toSameSet(line);
                for (String value : sameSet) {
                    Set<String> hasSet = temMap.get(value);
                    if (hasSet != null) {
                        log.warn("duplicat unit:" + value);
                    } else {
                        temMap.put(value, sameSet);
                    }
                }
            }
            return temMap;
        } catch (IOException e) {
            log.error("load dic,cause:", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(bReader);
        }
        return null;
    }

    private Set<String> toSameSet(String line) {
        if (StringUtils.isBlank(line)) {
            return Collections.emptySet();
        }
        StringTokenizer tokenizer = new StringTokenizer(line, "=");
        Set<String> newSet = Sets.newHashSet();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (StringUtils.isNotBlank(token)) {
                token = toUnify(token);
                newSet.add(token);
            }
        }
        return newSet;
    }

    private String toUnify(String token) {
        if (token == null) {
            return null;
        }
        return token.toLowerCase();
    }

}
