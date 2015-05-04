package com.lezo.iscript.yeam.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.SessionHisDto;
import com.lezo.iscript.yeam.io.IoOrder;
import com.lezo.iscript.yeam.server.IoAcceptorHolder;
import com.lezo.iscript.yeam.server.session.ProxySessionCacher;

public class ClientToServerProxyIoHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(ClientToServerProxyIoHandler.class);
	public static final String HTTP_NEW_LINE = "\r\n";
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private IoAcceptor acceptor;

	public ClientToServerProxyIoHandler(int port) throws IOException {
		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		if (logger.isDebugEnabled()) {
			acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		}
		acceptor.setHandler(this);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 5);
		acceptor.bind(new InetSocketAddress(port));
		logger.info("listen on proxy port:" + port);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		IoBuffer ioBuffer = (IoBuffer) message;
		int limit = ioBuffer.limit();
		byte[] destBytes = new byte[limit];
		ioBuffer.get(destBytes);
		String sMessge = new String(destBytes);
		int index = sMessge.indexOf(HTTP_NEW_LINE);
		String requestLine = sMessge.substring(0, index);
		IoOrder ioOrder = new IoOrder(IoOrder.ORDER_REQUEST_PROXY, requestLine, sMessge);
		IoSession proxySession = getProxySession();
		if (proxySession == null) {
			doRespone(session);
		} else {
			ProxySessionCacher.getInstance().putIfAbsent(ioOrder.getId(), session);
			proxySession.write(ioOrder);
		}
	}

	private void doRespone(IoSession session) throws Exception {
		IoBuffer returnBuffer = IoBuffer.allocate(10);
		returnBuffer.setAutoExpand(true);
		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

		String result = "<html><body><h1>wellcome to use mina.</h1></body></html>";

		returnBuffer.putString("HTTP/1.0 200 OK\r\n", encoder);
		returnBuffer.putString("Date: Thu, 30 Apr 2015 18:04:03 GMT\r\n", encoder);
		returnBuffer.putString("Server: Microsoft-IIS/6.0\r\n", encoder);
		returnBuffer.putString("X-Powered-By: ASP.NET\r\n", encoder);
		returnBuffer.putString("Content-Length: " + result.length() + "\r\n", encoder);
		returnBuffer.putString("Content-Type: text/html;charset=UTF-8\r\n", encoder);
		returnBuffer.putString("\r\n", encoder);
		returnBuffer.putString(result, encoder);
		returnBuffer.flip();

		session.write(returnBuffer).addListener(IoFutureListener.CLOSE);
	}

	private IoSession getProxySession() {
		Map<Long, IoSession> sessionMap = IoAcceptorHolder.getIoAcceptor().getManagedSessions();
		for (Entry<Long, IoSession> entry : sessionMap.entrySet()) {
			IoSession ioSession = entry.getValue();
			if (ioSession.containsAttribute(SessionHisDto.SESSION_ID)) {
				return ioSession;
			}
		}
		return null;
	}

//	@Override
//	public void messageSent(IoSession session, Object message) throws Exception {
//		if (!(message instanceof IoBuffer)) {
//			return;
//		}
//		IoBuffer ioBuffer = (IoBuffer) message;
//		CharsetDecoder decoder = DEFAULT_CHARSET.newDecoder();
//		String ioString = ioBuffer.getString(decoder);
//		JSONObject orderObject = JSONUtils.getJSONObject(ioString);
//		String idString = JSONUtils.getString(orderObject, "id");
//		String dataString = JSONUtils.getString(orderObject, "data");
//		JSONObject rsObject = JSONUtils.getJSONObject(dataString);
//		StringBuffer sb = new StringBuffer();
//		sb.append(JSONUtils.getString(rsObject, "status"));
//		sb.append(HTTP_NEW_LINE);
//		JSONArray headers = JSONUtils.get(rsObject, "headers");
//		if (headers != null) {
//			for (int i = 0; i < headers.length(); i++) {
//				String hString = headers.getString(i);
//				if (hString.contains("Transfer-Encoding")) {
//					continue;
//				}
//				sb.append(hString);
//				sb.append(HTTP_NEW_LINE);
//			}
//		}
//		String html = JSONUtils.getString(rsObject, "html");
//		// sb.append(html);
//		logger.info(idString);
//		logger.info(sb.toString());
//		IoBuffer returnBuffer = IoBuffer.allocate(sb.length());
//		returnBuffer.setAutoExpand(true);
//
//		CharsetEncoder encoder = DEFAULT_CHARSET.newEncoder();
//		returnBuffer.putString(sb.toString(), encoder);
//		if (StringUtils.isNotBlank(html)) {
//			sb.append(HTTP_NEW_LINE);
//			returnBuffer.put(toGzipByteArray(html.getBytes(DEFAULT_CHARSET)));
//		}
//		returnBuffer.flip();
//		session.write(returnBuffer);
//	}

	private byte[] toGzipByteArray(byte[] bytes) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(bos);
			gzos.write(bytes);
			gzos.close();
			return bos.toByteArray();
		} finally {
			IOUtils.closeQuietly(bos);
		}
	}
}
