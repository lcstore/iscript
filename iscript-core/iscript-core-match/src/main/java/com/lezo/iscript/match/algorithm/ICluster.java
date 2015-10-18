package com.lezo.iscript.match.algorithm;

import java.util.List;
import java.util.Map;

import com.lezo.iscript.match.pojo.SimilarCenter;
import com.lezo.iscript.match.pojo.SimilarIn;

public interface ICluster {
    List<SimilarCenter> doCluster(List<SimilarIn> similarIns, Map<String, ISimilar> similarMap);
}
