package com.lezo.iscript.service.crawler.dao;

import java.util.List;

public interface BatchInvoker {
	<E> void batchUpdate(List<E> dtoList);
}
