package com.lezo.iscript.proxy.driver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import org.littleshoot.proxy.HttpFiltersAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomHttpFilters extends HttpFiltersAdapter {
	private static Logger logger = LoggerFactory.getLogger(LaunchListener.class);
	public CustomHttpFilters(HttpRequest originalRequest, ChannelHandlerContext ctx) {
		super(originalRequest, ctx);
	}

	@Override
	public HttpResponse requestPre(HttpObject httpObject) {
		if (httpObject instanceof DefaultHttpRequest) {
			logger.info("doRequest:" + httpObject);
		}
		return super.requestPre(httpObject);
	}

	@Override
	public HttpObject responsePre(HttpObject httpObject) {
		if (httpObject instanceof DefaultHttpResponse) {
			logger.info("doResponse:" + httpObject);
		}
		return super.responsePre(httpObject);
	}

}
