package com.lezo.iscript.match.algorithm;

import java.util.List;

import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;

public interface IAnalyser {
    CellAssort analyse(List<CellToken> tokens);

}
