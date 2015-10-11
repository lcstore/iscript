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
public class CellAssort {
    private String name;
    private String value;
    private List<CellToken> tokens;
}
