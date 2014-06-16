package com.lezo.iscript.yeam.mina;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.AbstractIoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;

public abstract class ConnectBuilder extends IoHandlerAdapter {
	public abstract AbstractIoConnector newConnector();

	protected void addFilter(AbstractIoConnector connector, IoFilter filter) {
		connector.getFilterChain().addLast(filter.getClass().getSimpleName(), filter);
	}
}
