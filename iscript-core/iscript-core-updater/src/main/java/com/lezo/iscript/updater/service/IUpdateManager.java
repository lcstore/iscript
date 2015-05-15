package com.lezo.iscript.updater.service;

import java.io.File;

public interface IUpdateManager {

	String getCurrentVersion();

	boolean extractTo(File destFile);
}
