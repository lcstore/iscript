package com.lezo.iscript.resulter.token;

import java.util.List;

public interface ClustTokenizer {
	public List<CharPart> doToken(List<CharPart> sourceList);
}
