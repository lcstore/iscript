package com.lezo.iscript.common;

import java.util.List;

public interface ObjectWriter<E> {
	void write(List<E> dataList);

	void flush();
}
