package com.lezo.iscript.resulter.ident;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class IdentTarget {
	private Map<String, Set<IdentToken>> targets = new HashMap<String, Set<IdentToken>>();
}
