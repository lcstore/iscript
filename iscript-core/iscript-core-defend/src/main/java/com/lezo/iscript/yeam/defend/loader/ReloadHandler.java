package com.lezo.iscript.yeam.defend.loader;

import java.io.File;

public interface ReloadHandler {
	void handle(String parent, String name, ObjectReloader clientReloader) throws Exception;

	boolean isAccept(File file);
}

