  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CLIENT_TYPE` varchar(50) NOT NULL COMMENT '客户端类型',
  `CLIENT_ID` varchar(15) NOT NULL default '' COMMENT '客户端ID',
  `CLIENT_SECRET` varchar(20) NOT NULL default '' COMMENT '客户端密钥',
  `CLIENT_PARAMS` varchar(200) NOT NULL default '' COMMENT '其他参数',
  `REFRESH_TOKEN` varchar(100) NOT NULL default '' COMMENT '刷新的TOKEN',
  `ACCESS_TOKEN` varchar(100) NOT NULL default '' COMMENT '使用的TOKEN',
  `NEXT_REFRESH_TIME` datetime NULL  COMMENT '下次刷新时间',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL