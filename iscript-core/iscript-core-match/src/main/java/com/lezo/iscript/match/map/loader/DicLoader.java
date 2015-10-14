package com.lezo.iscript.match.map.loader;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public interface DicLoader {
    Map<String, Set<String>> loadDic(String dicPath) throws Exception;

    Map<String, Set<String>> loadDic(InputStream in) throws Exception;
}
