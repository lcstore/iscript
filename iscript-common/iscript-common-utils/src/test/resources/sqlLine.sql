  `ID` bigint(20) NOT NULL auto_increment,
  `DATA_CODE` varchar(50) NOT NULL default '' COMMENT '数据编号',
  `DATA_PATH` varchar(1000) NOT NULL default '' COMMENT '数据桶',
  `DATA_BUCKET` varchar(10) NOT NULL default '' COMMENT '数据桶',
  `DATA_DOMAIN` varchar(50) NOT NULL default '' COMMENT '数据域名',
  `DATA_COUNT` int(11) NOT NULL default '0' COMMENT '数据量',
  `PARAMS` varchar(1000) NOT NULL default '' COMMENT '获取参数',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT