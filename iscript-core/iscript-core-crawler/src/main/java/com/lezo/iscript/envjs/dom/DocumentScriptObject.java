package com.lezo.iscript.envjs.dom;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

public class DocumentScriptObject extends NativeJavaObject {
	private static final long serialVersionUID = 8442225004015613579L;
	private LocationScriptObject location;

	public DocumentScriptObject(LocationScriptObject location, Scriptable scope, Object object) {
		super(scope, object, object.getClass());
		this.location = location;
	}

	public LocationScriptObject getLocation() {
		return location;
	}

	public void setLocation(LocationScriptObject location) {
		this.location = location;
	}

	@Override
	protected void initMembers() {
		// TODO Auto-generated method stub
		super.initMembers();
	}

	@Override
	public boolean has(String name, Scriptable start) {
		if ("location".equals(name)) {
			return true;
		}
		return super.has(name, start);
	}
}
