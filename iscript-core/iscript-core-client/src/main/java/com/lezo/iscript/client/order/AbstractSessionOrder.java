package com.lezo.iscript.client.order;

import org.apache.mina.core.session.IoSession;

import com.lezo.iscript.yeam.io.IoOrder;

public abstract class AbstractSessionOrder implements ISessionOrder {

	@Override
	public void execute(IoOrder ioOrder, IoSession ioSession) {
		if (ioOrder.getOrder() != getOrder()) {
			return;
		}
		doOrder(ioOrder, ioSession);
	}

	protected abstract void doOrder(IoOrder ioOrder, IoSession ioSession);

	protected abstract int getOrder();

}
