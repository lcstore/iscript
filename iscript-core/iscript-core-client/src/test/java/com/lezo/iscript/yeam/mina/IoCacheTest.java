package com.lezo.iscript.yeam.mina;

import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.junit.Test;

public class IoCacheTest {

	@Test
	public void test() throws Exception {
		String hostname = "localhost";
		int port = 18567;
		InetSocketAddress socketAddress = new InetSocketAddress(hostname, port);
		IoCache ioCache = IoCacheBuilder.newIoCache(socketAddress, IoCacheBuilder.TASK_BUILDER);
		IoSession session = ioCache.getSession();
		int index = 0;
		while (index < 1000) {
			IoBuffer buffer = IoBuffer.allocate(4);
			buffer.putInt(index);
			buffer.flip();
			WriteFuture future = session.write(buffer);
			session.setAttribute("cmdKey", getCmdValue(index));
			while (!future.isDone()) {
				Thread.sleep(1);
			}
			index++;
		}
		ioCache.getConnector().dispose();
	}

	private String getCmdValue(int index) {
		int mode = index % 3;
		String value = null;
		switch (mode) {
		case 0:
			value = "client";
			break;
		case 1:
			value = "config";
			break;
		case 2:
			value = "type";
			break;
		default:
			break;
		}
		return value;
	}
}
