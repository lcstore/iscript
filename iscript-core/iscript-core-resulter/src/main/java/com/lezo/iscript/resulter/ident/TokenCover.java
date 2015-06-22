package com.lezo.iscript.resulter.ident;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TokenCover {
	private String cover;
	private List<SectionToken> unCovers;
}
