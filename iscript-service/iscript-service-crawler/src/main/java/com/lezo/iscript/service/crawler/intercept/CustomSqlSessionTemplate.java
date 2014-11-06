package com.lezo.iscript.service.crawler.intercept;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

public class CustomSqlSessionTemplate extends SqlSessionTemplate {
	private List<String> batchUpdateMethods;
	private List<String> batchInsertMethods;

	public CustomSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Override
	public int update(String statement, Object parameter) {
		return super.update(statement, parameter);
	}

}
