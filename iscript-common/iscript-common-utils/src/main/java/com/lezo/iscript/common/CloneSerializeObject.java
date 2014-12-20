package com.lezo.iscript.common;

import java.io.Serializable;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年12月20日
 */
public class CloneSerializeObject implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
