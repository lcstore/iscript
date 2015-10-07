package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarJobDto {
    /**
     * 0.就绪,1.进行中,2.完成,3.中断
     */
    public static final Integer STATUS_READY = 0;
    public static final Integer STATUS_DOING = 1;
    public static final Integer STATUS_DONE = 2;
    public static final Integer STATUS_ABORT = 3;
    private Long id;
    private String name;
    private String inputs;
    private String handler;
    private Integer status;
    private Integer taskDone;
    private Integer taskTotal;
    private Date createTime;
    private Date updateTime;

}