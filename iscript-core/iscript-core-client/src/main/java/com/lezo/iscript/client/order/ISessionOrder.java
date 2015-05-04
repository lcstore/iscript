package com.lezo.iscript.client.order;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.io.IoOrder;

public interface ISessionOrder {
	void execute(IoOrder ioOrder, IoSession ioSession);
}
