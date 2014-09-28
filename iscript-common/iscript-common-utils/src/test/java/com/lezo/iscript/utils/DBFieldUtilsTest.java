package com.lezo.iscript.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
//import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DBFieldUtilsTest {
	@Test
	public void testSql2Field() throws IOException {
		File file = new File("src/test/resources/sqlLine.sql");
		List<String> sqlLines = FileUtils.readLines(file, "utf-8");
		List<String> columnList = DBFieldUtils.sql2Field(sqlLines);
		for (String col : columnList) {
			System.out.println("," + col);
		}
	}

	@Test
	public void testField2Param() throws IOException {
		File file = new File("src/test/resources/sqlLine.sql");
		List<String> sqlLines = FileUtils.readLines(file, "utf-8");
		List<String> columnList = DBFieldUtils.sql2Field(sqlLines);
		List<String> paramList = DBFieldUtils.field2Param(columnList);
		for (String col : paramList) {
			String dtoFiled = "private String " + col + ";";
			// System.out.println("<parameter property=\""+col+"\" javaType=\"java.lang.String\"/>");
			String derbyInsert = "#{item." + col + ",typeHandler=DerbyTypeHandler},";
			// System.out.print("#" + col + "#,");
			String insertString = "#{item." + col + "},";
//			 insertString = "#" + col + "#,";
//			System.out.print(insertString);
			 System.out.println(dtoFiled);
		}
	}

	@Test
	public void toResultMap() throws IOException {
		File file = new File("src/test/resources/sqlLine.sql");
		List<String> sqlLines = FileUtils.readLines(file, "utf-8");
		List<String> columnList = DBFieldUtils.sql2Field(sqlLines);
		List<String> paramList = DBFieldUtils.field2Param(columnList);
		int size = paramList.size();
		for (int i = 0; i < size; i++) {

			// System.out.print( columnList.get(i) + "=#{item." +
			// paramList.get(i) + "},");
			String updateString = columnList.get(i) + "=#{item." + paramList.get(i) + "},";
			String resultMap = "<result column=\"" + columnList.get(i) + "\" property=\"" + paramList.get(i) + "\"/>";
			String derbyUpdate = (columnList.get(i) + "=#{item." + paramList.get(i) + ",typeHandler=DerbyTypeHandler},");

			String updateSet = "<if test=\"" + paramList.get(i) + " != null\">" + columnList.get(i) + "=#{"
					+ paramList.get(i) + "},</if>";
//			 System.out.println(resultMap);
			// System.out.println(updateSet);
			// System.out.println( columnList.get(i) + "=#" +
			// paramList.get(i) + "#,");
			String batchUpdate = ("<foreach collection=\"list\" item=\"item\"  open=\"," + columnList.get(i)
					+ "= CASE ID \" close=\" END\"> WHEN #{item.id} THEN #{item." + paramList.get(i) + "}</foreach>");
			String derbyBatchUpdate = ("<foreach collection=\"list\" item=\"item\"  open=\"," + columnList.get(i)
					+ "= CASE \" close=\" END\"> WHEN ID=#{item.id} THEN ${item." + paramList.get(i) + "}</foreach>");
			String derbyBatchUpdateNotNull = ("<trim prefix=\"," + columnList.get(i)
					+ "= CASE \" suffix=\"END\"><foreach collection=\"list\" item=\"item\">  <if test=\"item."
					+ paramList.get(i) + " != null\">    WHEN ID=#{item.id} THEN #{item." + paramList.get(i) + "} </if></foreach></trim>");
			System.out.println(updateString);
		}

	}
}
