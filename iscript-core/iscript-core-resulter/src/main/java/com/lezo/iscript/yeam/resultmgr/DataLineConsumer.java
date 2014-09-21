package com.lezo.iscript.yeam.resultmgr;


public class DataLineConsumer implements Runnable {
	private String type;
	private String data;

	public DataLineConsumer(String type, String data) {
		super();
		this.type = type;
		this.data = data;
	}

	@Override
	public void run() {
		System.out.println(type+"@@ "+data);
	}

}
