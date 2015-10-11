package com.lezo.iscript.match.pojo;

import lombok.Data;

@Data
public class CellToken {
    private int index;
    private String origin;
    private String token;
    private String creator;

    @Override
    public String toString() {
        return "CellToken [index=" + index + ", token=" + token + ", creator=" + creator + "]";
    }
}
