package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DataTransferDto {
	private static final String DIR_SEPARATOR = "/";
	private Long id;
	private String dataCode;
	private String dataPath;
	private String dataBucket;
	private String dataDomain;
	private Integer dataCount;
    private Integer totalCount;
	private String params;
	private Date createTime;
	private Date updateTime;

	public String toDataCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDataDomain());
		sb.append(DIR_SEPARATOR);
		sb.append(getDataBucket());
		sb.append(DIR_SEPARATOR);
		sb.append(getDataPath());
		String dirKey = sb.toString();
		String sCode = "" + dirKey.hashCode();
		return sCode.replace("-", "H");
	}
}
