/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.lezo.iscript.yeam.proxy.mina;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.proxy.AbstractProxyIoHandler;
import org.apache.mina.proxy.handlers.ProxyRequest;
import org.apache.mina.proxy.handlers.http.HttpProxyConstants;
import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClientSessionHandler.java - Client session handler for the mina proxy test
 * class.
 * 
 * @author Edouard De Oliveira <a
 *         href="mailto:doe_wanted@yahoo.fr">doe_wanted@yahoo.fr</a>
 * @version $Id: $
 */
public class ClientSessionHandler extends AbstractProxyIoHandler {
	private final static Logger logger = LoggerFactory.getLogger(ClientSessionHandler.class);

	private boolean finished;

	private File file;

	private FileChannel wChannel;

	private String cmd;

	public ClientSessionHandler(String cmd) {
		this.cmd = cmd;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.debug("CLIENT - Session created: " + session);
	}

	@Override
	public void proxySessionOpened(IoSession session) throws Exception {
		logger.debug("CLIENT - Session opened: " + session);
		ProxyIoSession proxyIoSession = (ProxyIoSession) session.getAttribute(ProxyIoSession.PROXY_SESSION);
		if (proxyIoSession != null) {
			ProxyRequest req = proxyIoSession.getRequest();

			if (req instanceof SocksProxyRequest && cmd != null) {
				logger.debug("Sending request to a SOCKS SESSION ...");
				IoBuffer buf = IoBuffer.allocate(64).setAutoExpand(true);
				buf.put(cmd.getBytes(proxyIoSession.getCharset()));
				buf.flip();
				session.write(buf);
			} else if (req instanceof HttpProxyRequest
					&& ((HttpProxyRequest) req).getHttpVerb() == HttpProxyConstants.CONNECT) {
				logger.debug("Sending request to a HTTP CONNECTED SESSION ...");
				IoBuffer buf = IoBuffer.allocate(64).setAutoExpand(true);
				buf.put((((HttpProxyRequest) req).toHttpString()).getBytes(proxyIoSession.getCharset()));
				buf.flip();
				session.write(buf);
			}
		}
	}

	public boolean isFinished() {
		return finished;
	}

	@Override
	public void messageReceived(IoSession session, Object message) {
		logger.debug("CLIENT - Message received: " + session);
		IoBuffer buf = (IoBuffer) message;

		try {
			if (file == null) {
				file = File.createTempFile("http", ".html");
				logger.info("Writing request result to " + file.getAbsolutePath());
				wChannel = new FileOutputStream(file, false).getChannel();
			}

			// Write the ByteBuffer contents; the bytes between the ByteBuffer's
			// position and the limit is written to the file
			wChannel.write(buf.buf());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.debug("CLIENT - Session closed - closing result file if open.");
		// Close the file
		if (wChannel != null) {
			wChannel.close();
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		if (session.isClosing()) {
			return;
		}

		logger.debug("CLIENT - Session idle");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		logger.debug("CLIENT - Exception caught");
		cause.printStackTrace();
		session.close();
	}
}