package com.lezo.iscript.yeam.simple;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		int port = 1089;
		IoServer server = new IoServer(port);
		String host = "localhost";
		IoClient client = new IoClient(host, port);
		client.start();
	}

}
