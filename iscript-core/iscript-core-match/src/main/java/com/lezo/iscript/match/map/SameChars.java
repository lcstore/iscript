package com.lezo.iscript.match.map;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SameChars {
    /**
     * 标准值
     */
    private String value;
    /**
     * 同义词组
     */
    private Set<String> sameSet;
}
