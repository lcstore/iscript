package com.lezo.iscript.match.pojo;

import java.util.Map;

import lombok.Data;

/**
 * token同义转化，聚类
 * 
 * @author lezo
 * @since 2015年10月11日
 */
@Data
public class CellAnalyse {
    private String origin;
    private Map<String, CellAssort> assortMap;
}
