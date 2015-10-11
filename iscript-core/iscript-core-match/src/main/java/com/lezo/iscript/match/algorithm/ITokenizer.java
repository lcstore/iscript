package com.lezo.iscript.match.algorithm;

import java.util.List;

import com.lezo.iscript.match.pojo.CellToken;

public interface ITokenizer {

    List<CellToken> token(String origin);

}
