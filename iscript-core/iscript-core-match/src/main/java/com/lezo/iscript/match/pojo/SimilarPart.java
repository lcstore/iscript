package com.lezo.iscript.match.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarPart {
    private CellAssort current;
    private CellAssort refer;
    private int score;
}
