package com.lezo.iscript.common.compile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class CacheJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	public OutputJavaFileObject getJavaClassObject() {
		return jclassObject;
	}

	private OutputJavaFileObject jclassObject;
	private Map<String, JavaFileObject> fileObjectMap = new HashMap<String, JavaFileObject>();

	public CacheJavaFileManager(StandardJavaFileManager standardManager) {
		super(standardManager);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
			FileObject sibling) throws IOException {
		String key = location.getName() + className + kind.name();
		JavaFileObject fileObject = fileObjectMap.get(key);
		if (fileObject != null) {
			return fileObject;
		}
		synchronized (fileObjectMap) {
			fileObject = fileObjectMap.get(key);
			if (fileObject != null) {
				return fileObject;
			} else {
				fileObject = new OutputJavaFileObject(className, kind);
				fileObjectMap.put(key, fileObject);
			}
		}
		return fileObject;
	}

	public Map<String, JavaFileObject> getFileObjectMap() {
		return fileObjectMap;
	}

}