package com.lezo.iscript.common.buffer;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月6日
 */
public interface StampGetable<T> {
	String getName(T bean);

	long getStamp(T bean);
}
