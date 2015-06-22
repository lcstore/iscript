package com.lezo.iscript.common;

import java.util.List;

public interface BaseDao<T> {
	int batchInsert(List<T> dtoList);

	int batchUpdate(@Batch List<T> dtoList);

	int batchDeleteByIds(List<Long> idList);

	List<T> getDtoByIds(List<Long> idList);
}
