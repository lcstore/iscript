package com.lezo.iscript.match.algorithm.adorn;

import com.lezo.iscript.match.algorithm.IAdorner;

public class ReplaceAdorner implements IAdorner {
    private String regex;
    private String replacement;

    public ReplaceAdorner(String regex, String replacement) {
        super();
        this.regex = regex;
        this.replacement = replacement;
    }

    @Override
    public String doAdorn(String origin) {
        if (origin == null) {
            return null;
        }
        return origin.replaceAll(regex, replacement);
    }

}
