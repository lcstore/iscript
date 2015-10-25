package com.lezo.iscript.match.utils;

public class SimilarUtils {
    public static final int SCORE_MIN = 0;
    public static final int SCORE_MAX = 100;

    public static int clamp(int score, int min, int max) {
        if (score < 0) {
            return min;
        } else if (score > 100) {
            return max;
        }
        return score;
    }

    public static int clamp(int score) {
        return clamp(score, SCORE_MIN, SCORE_MAX);
    }
}
