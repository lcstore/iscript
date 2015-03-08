DROP TABLES IF EXISTS T_CLIENT_TOKEN;
CREATE TABLE `T_CLIENT_TOKEN` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CLIENT_TYPE` varchar(20) NOT NULL COMMENT '客户端类型',
  `CLIENT_BUCKET` varchar(20) NOT NULL COMMENT '桶名称',
  `CLIENT_DOMAIN` varchar(50) NOT NULL COMMENT '域名',
  `CLIENT_KEY` varchar(100) NOT NULL default '' COMMENT '客户端KEY',
  `CLIENT_SECRET` varchar(100) NOT NULL default '' COMMENT '客户端密钥',
  `CLIENT_PARAMS` varchar(200) NOT NULL default '' COMMENT '其他参数',
  `REFRESH_TOKEN` varchar(100) NOT NULL default '' COMMENT '刷新的TOKEN',
  `ACCESS_TOKEN` varchar(100) NOT NULL default '' COMMENT '使用的TOKEN',
  `SUCCESS_COUNT` int NOT NULL default '0' COMMENT '成功数',
  `FAIL_COUNT` int NOT NULL default '0' COMMENT '失败数',
  `LAST_MESSGE` varchar(1000) NOT NULL default '' COMMENT '上次信息',
  `NEXT_REFRESH_TIME` datetime NULL  COMMENT '下次刷新时间',
  `IS_DELETE` tinyint(4) NOT NULL default '0' COMMENT '0-未删除,1-删除',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_T_CLIENT_TOKEN_CLIENT_TYPE_BUCKET` (`CLIENT_TYPE`,`CLIENT_BUCKET`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '客户端授权表';