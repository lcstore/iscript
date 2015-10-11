package com.lezo.iscript.match.algorithm;

import java.util.List;

import com.lezo.iscript.match.pojo.CellAssort;
import com.lezo.iscript.match.pojo.CellToken;

/**
 * 过滤器,标准化
 * 
 * @author lezo
 * @since 2015年10月11日
 */
public interface IStrainer {
    static final String VALUE_IGNORE = "ignore";
    static final String VALUE_CLEAN = "clean";

    CellAssort strain(List<CellToken> tokens);

}
