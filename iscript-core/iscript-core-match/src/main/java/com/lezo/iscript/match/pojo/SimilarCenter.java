package com.lezo.iscript.match.pojo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarCenter {
    private SimilarIn value;
    private List<SimilarOut> outs;
}
