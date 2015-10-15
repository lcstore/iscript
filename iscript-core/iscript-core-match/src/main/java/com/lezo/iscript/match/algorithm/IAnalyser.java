package com.lezo.iscript.match.algorithm;

import java.util.List;

import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;

public interface IAnalyser {
    static final String NAME_BRAND = "brand";
    static final String NAME_UNIT = "unit";

    CellAssort analyse(List<CellToken> tokens);

}
