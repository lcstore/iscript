package com.lezo.iscript.common;

import java.util.List;

public interface BaseService<T> {
	void batchInsertDtos(List<T> dtoList);

	void batchUpdateDtos(List<T> dtoList);

	void batchSaveDtos(List<T> dtoList);
}
