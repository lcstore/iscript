package com.lezo.iscript.match.map.loader;

import java.io.InputStream;
import java.util.Map;

import com.lezo.iscript.match.map.SameEntity;

public interface DicLoader {
    Map<String, SameEntity> loadDic(InputStream in) throws Exception;
}
