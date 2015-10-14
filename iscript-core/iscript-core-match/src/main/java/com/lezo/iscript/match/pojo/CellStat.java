package com.lezo.iscript.match.pojo;

import java.util.List;

import lombok.Data;

/**
 * token归类,分裂
 * 
 * @author lezo
 * @since 2015年10月11日
 */
@Data
public class CellStat {
    private CellToken value;
    private List<CellToken> tokens;
}
