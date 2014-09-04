 `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TYPE` varchar(50) COMMENT '消息类型',
  `MESSAGE` varchar(1000)  COMMENT '消息内容',
  `SOURCE` varchar(50) COMMENT '消息来源',
  `HANDLER` varchar(100) COMMENT '处理者',
  `STATUS` tinyint NOT NULL DEfault '0' COMMENT '状态',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp