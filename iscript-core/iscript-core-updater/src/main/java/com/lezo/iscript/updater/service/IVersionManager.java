package com.lezo.iscript.updater.service;

public interface IVersionManager {
	String TO_UPDATE_VERSION = "TO_UPDATE";

	String getVersion();

	void changeTo(String newVersion);
}
