package com.lezo.iscript.yeam.config.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class OutputJavaFileObject extends SimpleJavaFileObject {

	protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

	public OutputJavaFileObject(String name, JavaFileObject.Kind kind) {
		super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return bos;
	}
}
