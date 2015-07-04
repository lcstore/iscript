package com.lezo.iscript.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class DaoConfigUtils {

	private static final Object NEW_LINE = "\n";
	private static final String encoding = "utf-8";
	private static final String TEMPLATE_FILE_PATH = "mybatis-mapper-template.xml";
	private static final String LINE_FORMAT = "\t\t";

	public static String getDtoParams(List<String> fileds) throws IOException {
		List<String> paramList = DBFieldUtils.field2Param(fileds);
		StringBuilder sb = new StringBuilder();
		for (String col : paramList) {
			String colString = "private String " + col + ";";
			sb.append(colString);
			sb.append("\n");
		}
		return sb.toString();
	}

	public static void createDBConfig(String path, String tableName, String daoClassPackage, String dtoClassName,
			List<String> fileds) throws IOException {
		List<String> paramList = DBFieldUtils.field2Param(fileds);
		BufferedWriter bw = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String templatConfig = IOUtils.toString(loader.getResourceAsStream(TEMPLATE_FILE_PATH), encoding);
		String daoClassName = dtoClassName.replace("Dto", "Dao");
		try {
			Writer out = new FileWriter(path);
			bw = new BufferedWriter(out);
			templatConfig = templatConfig.replace("${dtoSimpleName}", dtoClassName);
			templatConfig = templatConfig.replace("${daoPackageName}", daoClassPackage + "." + daoClassName);
			templatConfig = templatConfig.replace("${tableName}", tableName);

			String rmConfig = getResultMapConfig(fileds, paramList);
			templatConfig = templatConfig.replace("${resultMap-results}", rmConfig);

			String coloumList = getColoumListConfig(fileds);
			templatConfig = templatConfig.replace("${coloumn-list}", coloumList);

			String insertValues = getInsertValuesConfig(paramList);
			templatConfig = templatConfig.replace("${insert-values}", insertValues);

			String updateValues = getUpdateValuesConfig(fileds, paramList);
			templatConfig = templatConfig.replace("${update-values}", updateValues);
			bw.append(templatConfig);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(bw);
		}
	}

	private static String getColoumListConfig(List<String> columnList) {
		StringBuilder sb = new StringBuilder();
		int size = columnList.size();
		for (int i = 0; i < size; i++) {
			String colName = columnList.get(i);
			// if ("ID".equals(colName)) {
			// continue;
			// }
			if (sb.length() > 0) {
				sb.append(LINE_FORMAT);
				sb.append(",");
			}
			sb.append(colName);
			if (i + 1 < size) {
				sb.append(NEW_LINE);
			}
		}
		return sb.toString();
	}

	private static String getUpdateValuesConfig(List<String> columnList, List<String> paramList) {
		StringBuilder sb = new StringBuilder();
		int size = paramList.size();
		for (int i = 0; i < size; i++) {
			String colName = columnList.get(i);
			if ("ID".equals(colName.toUpperCase())) {
				continue;
			}
			String updateString = colName + "=#{" + paramList.get(i) + "}";
			if (sb.length() > 0) {
				sb.append(LINE_FORMAT);
				sb.append(",");
				sb.append(updateString);
			} else {
				sb.append(updateString);
			}
			if (i + 1 < size) {
				sb.append(NEW_LINE);
			}
		}
		return sb.toString();
	}

	private static String getInsertValuesConfig(List<String> paramList) {
		StringBuilder sb = new StringBuilder();
		int size = paramList.size();
		for (int i = 0; i < size; i++) {
			String param = paramList.get(i);
			// if ("id".equals(param)) {
			// continue;
			// }
			String insertString = "#{item." + param + "}";
			sb.append(insertString);
			if (i + 1 < size) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	private static String getResultMapConfig(List<String> columnList, List<String> paramList) {
		StringBuilder sb = new StringBuilder();
		int size = columnList.size();
		for (int i = 0; i < size; i++) {
			String resultMap = "<result column=\"" + columnList.get(i) + "\" property=\"" + paramList.get(i) + "\"/>";
			if (sb.length() > 0) {
				sb.append(LINE_FORMAT);
			}
			sb.append(resultMap);
			if (i + 1 < size) {
				sb.append(NEW_LINE);
			}
		}
		return sb.toString();
	}
}
