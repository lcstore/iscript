package com.lezo.iscript.utils.exception;

public class ExceptionHandleUtils {

	public static void sendException(Class<?> cls, Throwable e, String msg, ExceptionHandler handler) {
		handler.execute();
	}
}
