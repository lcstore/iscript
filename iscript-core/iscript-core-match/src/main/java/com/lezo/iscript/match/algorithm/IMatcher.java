package com.lezo.iscript.match.algorithm;

import java.util.List;

import com.lezo.iscript.match.pojo.SimilarCenter;
import com.lezo.iscript.match.pojo.SimilarIn;

public interface IMatcher {
    List<SimilarCenter> doMatcher(List<SimilarIn> similarIns);
}
