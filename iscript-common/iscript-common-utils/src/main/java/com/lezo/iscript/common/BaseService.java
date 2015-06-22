package com.lezo.iscript.common;

import java.util.List;

public interface BaseService<T> {
	int batchInsertDtos(List<T> dtoList);

	int batchUpdateDtos(List<T> dtoList);

	int batchSaveDtos(List<T> dtoList);
}
