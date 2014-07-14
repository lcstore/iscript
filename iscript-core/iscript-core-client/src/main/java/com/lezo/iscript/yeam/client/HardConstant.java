package com.lezo.iscript.yeam.client;

import com.lezo.iscript.common.HardUtils;
import com.lezo.iscript.common.MacAddress;

public class HardConstant {
	public static final String MAC_ADDR = MacAddress.getMacAddress();
	public static final String OS_UUID = HardUtils.getOSUUID();
}
