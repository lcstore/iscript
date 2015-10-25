package com.lezo.iscript.match.algorithm;

import java.util.List;

import com.lezo.iscript.match.pojo.SimilarCenter;
import com.lezo.iscript.match.pojo.SimilarFact;
import com.lezo.iscript.match.pojo.SimilarIn;

public interface ICluster {
    List<SimilarCenter> doCluster(List<SimilarIn> similarIns, List<SimilarFact> facts);
}
