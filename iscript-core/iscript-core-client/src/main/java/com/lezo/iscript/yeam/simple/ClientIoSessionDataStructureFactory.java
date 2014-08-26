package com.lezo.iscript.yeam.simple;

import org.apache.mina.core.session.DefaultIoSessionDataStructureFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionAttributeMap;

public class ClientIoSessionDataStructureFactory extends DefaultIoSessionDataStructureFactory {

	@Override
	public IoSessionAttributeMap getAttributeMap(IoSession session) throws Exception {
		IoSessionAttributeMap attributeMap = super.getAttributeMap(session);
		attributeMap.setAttribute(session, "nkey", "mClient");
		return attributeMap;
	}

}
