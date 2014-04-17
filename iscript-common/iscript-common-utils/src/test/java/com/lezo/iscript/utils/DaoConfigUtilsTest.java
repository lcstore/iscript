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
		String tableName = "T_TASK_CONFIG";
		String qualifyName = "com.lezo.iscript.yeam.tasker.dto.TaskConfigDto";
		int index = qualifyName.lastIndexOf('.');
		String clsPackage = qualifyName.substring(0, index);
		String clsName = qualifyName.substring(index + 1);
		System.out.println("tableName:" + tableName);
		System.out.println(qualifyName);
		System.out.println(clsPackage);
		System.out.println(clsName);
		List<String> columnList = DBFieldUtils.sql2Field(sqlLines);
		DaoConfigUtils.createDBConfig(path, tableName, clsPackage, clsName, columnList);
	}
}
