 `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SITE_HOST` varchar(50) not null COMMENT '网站域名',
  `NEWS_TITLE` varchar(1000) COMMENT '新闻标题',
  `NEWS_URL` varchar(1000) COMMENT '新闻链接',
  `NEWS_SORT` varchar(100) COMMENT '分类名称',
  `NEWS_TYPE` tinyint default '0'  COMMENT '新闻类型',
  `PUBLISH_TIME` datetime COMMENT '发布时间',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT