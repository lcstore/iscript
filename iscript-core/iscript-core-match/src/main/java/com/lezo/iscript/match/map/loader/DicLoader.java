package com.lezo.iscript.match.map.loader;

import java.io.InputStream;
import java.util.Map;

import com.lezo.iscript.match.map.SameChars;

public interface DicLoader {
    Map<String, SameChars> loadDic(String dicPath) throws Exception;

    Map<String, SameChars> loadDic(InputStream in) throws Exception;
}
