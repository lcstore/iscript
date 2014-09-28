 `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) COMMENT '消息名称',
  `MESSAGE` varchar(1000)  COMMENT '消息内容',
  `SOURCE` varchar(50) COMMENT '消息来源',
  `REMARK` varchar(100) COMMENT '备注信息',
  `STATUS` tinyint NOT NULL DEfault '0' COMMENT '状态',
  `SORT_CODE` tinyint NOT NULL DEfault '0' COMMENT '类型编码',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp