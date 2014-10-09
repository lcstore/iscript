package com.lezo.iscript.yeam.resultmgr.writer;

import com.lezo.iscript.common.ObjectWriter;

public class BufferWriteFactor {
	private Class<?> beanClass;
	private ObjectWriter<?> objectWriter;
	private int bufferSize = 200;

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public ObjectWriter<?> getObjectWriter() {
		return objectWriter;
	}

	public void setObjectWriter(ObjectWriter<?> objectWriter) {
		this.objectWriter = objectWriter;
	}

}
