package com.lezo.iscript.yeam.ua;

public class ScriptBuilder {
	private StringBuilder builder = new StringBuilder();

	public ScriptBuilder append(String jsCode) {
		builder.append(jsCode);
		return this;
	}

	public ScriptBuilder clear() {
		builder = new StringBuilder();
		return this;
	}
}
