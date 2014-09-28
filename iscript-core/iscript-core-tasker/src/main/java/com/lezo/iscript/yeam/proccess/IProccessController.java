package com.lezo.iscript.yeam.proccess;

public interface IProccessController {
	String createKey();

	void add2Sum(String key, int count);

	void add2Done(String key, int count);
}
