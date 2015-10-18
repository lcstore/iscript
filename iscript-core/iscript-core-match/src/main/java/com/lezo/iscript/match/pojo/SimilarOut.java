package com.lezo.iscript.match.pojo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarOut {
    private SimilarIn current;
    private SimilarIn refer;
    private int score;
    private List<SimilarPart> parts;

    @Override
    public String toString() {
        return "SimilarOut [current=" + current + ", score=" + score + "]";
    }
}
