package com.lezo.iscript.envjs.dom;

import java.util.HashMap;
import java.util.Map;

public class SimpleClass {
	protected Map<String, Object> valueMap = new HashMap<String, Object>();
	private String name = "lezo";

	public String find() {
		return name + ".find";
	}
}
