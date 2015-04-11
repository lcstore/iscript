package com.lezo.iscript.proxy.driver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomHttpFiltersSource extends HttpFiltersSourceAdapter {
	private static Logger logger = LoggerFactory.getLogger(LaunchListener.class);
	@Override
	public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
		return new CustomHttpFilters(originalRequest,ctx);
	}

}
