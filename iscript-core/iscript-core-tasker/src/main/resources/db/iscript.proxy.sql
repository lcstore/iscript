DROP TABLES IF EXISTS T_PROXY_ADDR;
CREATE TABLE `T_PROXY_ADDR` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `IP` int unsigned NOT NULL COMMENT 'IP地址值',
  `PORT` int NOT NULL COMMENT '端口',
  `SOURCE` varchar(100) COMMENT '来源',
  `REGION_NAME` varchar(50) COMMENT '地区名称',
  `ISP_NAME` varchar(50) COMMENT '服务商名称',
  `IS_DELETE` tinyint(4) DEFAULT '0' COMMENT '状态',
  `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY INDEX_T_PROXY_ADDR_IP_PORT ( `IP`,`PORT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '代理地址表';

DROP TABLES IF EXISTS T_PROXY_DETECT;
CREATE TABLE `T_PROXY_DETECT` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `IP` int unsigned NOT NULL COMMENT 'IP地址值',
  `PORT` int NOT NULL COMMENT '端口',
  `DOMAIN` varchar(50) COMMENT '监测',
  `URL` varchar(1000) COMMENT '监测URL',
  `DETECTOR` varchar(50) COMMENT '监测者',
  `CUR_COST`  int DEFAULT '0' COMMENT '耗时',
  `MIN_COST`  int DEFAULT '0' COMMENT '最短耗时',
  `MAX_COST`  int DEFAULT '0' COMMENT '最长耗时',
  `RETRY_TIMES` int DEFAULT '0' COMMENT '重试次数',
  `STATUS` tinyint(4) DEFAULT '0' COMMENT '状态,-2:禁用，-1:停用，0:重试，1:可用',
  `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_T_PROXY_DETECT_IP_HOST` (`IP`,`PORT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '代理检测表'