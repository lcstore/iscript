package com.lezo.iscript.service.crawler.intercept;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

public class CustomSqlSessionTemplate extends SqlSessionTemplate {

	public CustomSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator) {
		super(sqlSessionFactory, executorType, exceptionTranslator);
	}

	@Override
	public int update(String statement, Object parameter) {
		return super.update(statement, parameter);
	}

}
