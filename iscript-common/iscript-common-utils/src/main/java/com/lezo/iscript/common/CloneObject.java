package com.lezo.iscript.common;


/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年12月20日
 */
public class CloneObject<T> implements Cloneable {

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
