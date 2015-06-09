package com.lezo.iscript.resulter.ident;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class IdentTarget {
	private String origin;
	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, List<IdentToken>> targets = new HashMap<String, List<IdentToken>>();
}
