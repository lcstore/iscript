package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarJobTrackDto {
	private Long id;
	private String jobId;
	private String inputs;
	private String outputs;
	private String tasker;
	private String caller;
	private Integer status;
	private Date createTime;
	private Date updateTime;

}