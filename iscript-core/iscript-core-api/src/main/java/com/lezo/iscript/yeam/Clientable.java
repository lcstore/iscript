package com.lezo.iscript.yeam;


public interface Clientable {
	public int startup(String[] args) throws Exception;

	public int shutdown(long timeout) throws Exception;

}
