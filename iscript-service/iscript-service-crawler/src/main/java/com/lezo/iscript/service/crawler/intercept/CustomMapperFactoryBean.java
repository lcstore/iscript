package com.lezo.iscript.service.crawler.intercept;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;

public class CustomMapperFactoryBean<T> extends MapperFactoryBean<T> {
	private List<String> batchUpdateMethods;
	private List<String> batchInsertMethods;

	@Override
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		SqlSession customSqlSession = null;
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public List<String> getBatchInsertMethods() {
		return batchInsertMethods;
	}

	public void setBatchInsertMethods(List<String> batchInsertMethods) {
		this.batchInsertMethods = batchInsertMethods;
	}

	public List<String> getBatchUpdateMethods() {
		return batchUpdateMethods;
	}

	public void setBatchUpdateMethods(List<String> batchUpdateMethods) {
		this.batchUpdateMethods = batchUpdateMethods;
	}

}
