package com.lezo.iscript.common;

import java.util.List;

public interface BaseDao<T> {
	void batchInsert(List<T> dtoList);

	void batchUpdate(List<T> dtoList);
}
