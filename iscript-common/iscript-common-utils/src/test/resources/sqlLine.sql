  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `AGENT_NAME` varchar(20) NOT NULL COMMENT '名称',
  `AGENT_ADDR` varchar(100) NOT NULL DEFAULT '' COMMENT '地址',
  `STATUS` int NOT NULL default '0' COMMENT '状态，1:在线,-1：下线,0:中断',
  `PROXY_CODE` varchar(20) NOT NULL default '0' COMMENT '代理编码=IP+PORT',
  `LAST_DWON_TIME` timestamp NULL COMMENT '上次下线时间',
  `LAST_UP_TIME` timestamp NULL COMMENT '上次上线时间',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp