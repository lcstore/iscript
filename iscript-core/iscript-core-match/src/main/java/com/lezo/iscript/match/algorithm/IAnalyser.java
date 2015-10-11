package com.lezo.iscript.match.algorithm;

import java.util.List;

import com.lezo.iscript.match.pojo.CellAnalyse;
import com.lezo.iscript.match.pojo.CellAssort;

public interface IAnalyser {

    CellAnalyse analyse(List<CellAssort> assorts);

}
