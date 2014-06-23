 `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `IP` int NOT NULL COMMENT 'IP地址值',
  `PORT` int NOT NULL COMMENT '端口',
  `SOURCE` varchar(100) COMMENT '来源',
  `IS_DELETE` tinyint(4) DEFAULT '0' COMMENT '状态',
  `CREATE_TIME` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp N