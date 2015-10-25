package com.lezo.iscript.service.crawler.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandRepoDto {
    private Long id;
    private String sortName;
    private String regionName;
    private String crumbNav;
    private String coreName;
    private String includes;
    private String excludes;
    private Integer isDelete = 0;
    private Date createTime;
    private Date updateTime;

}