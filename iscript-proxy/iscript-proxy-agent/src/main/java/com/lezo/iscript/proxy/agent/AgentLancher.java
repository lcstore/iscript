package com.lezo.iscript.proxy.agent;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.ClientCookieEncoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;

import com.lezo.iscript.proxy.agent.http.HttpSnoopClientInitializer;

public class AgentLancher {

	public static void main(String[] args) throws Exception {
		String url = "";
		URI uri = new URI(url);
		String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
		String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
		int port = uri.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("https".equalsIgnoreCase(scheme)) {
				port = 443;
			}
		}

		if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
			System.err.println("Only HTTP(S) is supported.");
			return;
		}

		// Configure SSL context if necessary.
		final boolean ssl = "https".equalsIgnoreCase(scheme);
		final SslContext sslCtx;
		if (ssl) {
			sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		} else {
			sslCtx = null;
		}

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new HttpSnoopClientInitializer(sslCtx));

			// Make the connection attempt.
			Channel ch = b.connect(host, port).sync().channel();

			// Prepare the HTTP request.
			HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
			request.headers().set(Names.HOST, host);
			request.headers().set(Names.CONNECTION, Values.CLOSE);
			request.headers().set(Names.ACCEPT_ENCODING, Values.GZIP);

			// Set some example cookies.
			request.headers().set(Names.COOKIE, ClientCookieEncoder.encode(new DefaultCookie("my-cookie", "foo"), new DefaultCookie("another-cookie", "bar")));

			// Send the HTTP request.
			ch.writeAndFlush(request);

			// Wait for the server to close the connection.
			ch.closeFuture().sync();
		} finally {
			// Shut down executor threads to exit.
			group.shutdownGracefully();
		}
	}
}
