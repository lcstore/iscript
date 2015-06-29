package com.lezo.iscript.resulter.ident;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EntitySimilar {
	private int similar;
	private TokenCover leftCover;
	private TokenCover rightCover;
}