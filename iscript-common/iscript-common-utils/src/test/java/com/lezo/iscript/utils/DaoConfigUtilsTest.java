package com.lezo.iscript.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DaoConfigUtilsTest {

	@Test
	public void testParams() throws IOException {
		File file = new File("src/test/resources/sqlLine.sql");
		List<String> sqlLines = FileUtils.readLines(file, "utf-8");
		List<String> columnList = DBFieldUtils.sql2Field(sqlLines);
		String params = DaoConfigUtils.getDtoParams(columnList);
		System.out.println(params);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws IOException {
		File file = new File("src/test/resources/sqlLine.sql");
		List<String> sqlLines = FileUtils.readLines(file, "utf-8");
		String path = "src/test/resources/mybatis-mapper-current.xml";
		String tableName = "T_PRODUCT_STAT";
		String daoQualifyName = "com.lezo.iscript.service.crawler.dao.ProductStatDao";
		int index = daoQualifyName.lastIndexOf('.');
		String daoClassPackage = daoQualifyName.substring(0, index);
		String daoClassName = daoQualifyName.substring(index + 1);
		String dtoClassName = daoClassName.replace("Dao", "Dto");
		System.out.println("tableName:" + tableName);
		System.out.println(daoQualifyName);
		System.out.println(daoClassPackage);
		System.out.println(dtoClassName);
		List<String> columnList = DBFieldUtils.sql2Field(sqlLines);
		DaoConfigUtils.createDBConfig(path, tableName, daoClassPackage, dtoClassName, columnList);
	}
}
