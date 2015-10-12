package com.lezo.iscript.match.algorithm;

import java.util.List;

import com.lezo.iscript.match.pojo.CellToken;

/**
 * 过滤器,标准化
 * 
 * @author lezo
 * @since 2015年10月11日
 */
public interface IStrainer {

    List<CellToken> strain(List<CellToken> tokens);

}
