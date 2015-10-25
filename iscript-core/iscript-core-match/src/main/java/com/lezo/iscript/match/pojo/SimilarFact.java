package com.lezo.iscript.match.pojo;

import lombok.Getter;
import lombok.Setter;

import com.lezo.iscript.match.algorithm.ISimilar;

@Getter
@Setter
public class SimilarFact {
    private String name;
    private ISimilar similar;
    private float fact;

}
