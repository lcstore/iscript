package com.lezo.iscript.common;

import java.io.Serializable;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年12月20日
 */
public class CloneSerializeObject<T> implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public T clone() {
		try {
			@SuppressWarnings("unchecked")
			T dest = (T) super.clone();
			return dest;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

}
