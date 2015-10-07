package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteDto {
	private Integer id;
	private String siteCode;
	private String siteName;
	private String siteUrl;
	private Integer siteLevel;
	private Integer isDelete;
	private Date createTime;
	private Date updateTime;

}