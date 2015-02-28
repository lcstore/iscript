package com.lezo.iscript.yeam.server;

import org.apache.mina.core.service.IoAcceptor;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年2月28日
 */
public class IoAcceptorHolder {
	private static IoAcceptor ioAcceptor = null;

	public static IoAcceptor getIoAcceptor() {
		return ioAcceptor;
	}

	public static void setIoAcceptor(IoAcceptor ioAcceptor) {
		IoAcceptorHolder.ioAcceptor = ioAcceptor;
	}

}
