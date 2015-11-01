package com.lezo.iscript.match.algorithm.adorn;

import java.util.Locale;

import com.lezo.iscript.match.algorithm.IAdorner;

public class LowerCaseAdorner implements IAdorner {

    @Override
    public String doAdorn(String origin) {
        if (origin == null) {
            return null;
        }
        return origin.toLowerCase(Locale.US);
    }

}
