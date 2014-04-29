package com.lezo.iscript.yeam.defend.os;

import java.io.IOException;
import java.util.List;

public interface Clientable {
	public void closeClient(List<String> clientIds) throws IOException;

	public boolean hasClient(String clientId) throws IOException;

	public Process newClient() throws IOException;

	public List<String> findClients() throws IOException;
}
