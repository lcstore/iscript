 `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SEED_ID` bigint(20) NOT NULL COMMENT '代理源ID',
  `TOTAL_PAGE` int DEFAULT '0' COMMENT '链接生成函数 ',
  `FETCH_PAGE` int DEFAULT '0' COMMENT '页面解码函数',
  `TOTAL_COUNT` int DEFAULT '0' COMMENT '总代理数',
  `NEW_COUNT` int DEFAULT '0' COMMENT '新代理数',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NU