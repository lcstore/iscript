`ID` bigint(20) NOT NULL auto_increment,
  `SIMILAR_CODE` bigint(20) NOT NULL default '0' COMMENT '相似编码',
  `SITE_ID` int(11)  NOT NULL default '0' COMMENT '站点ID',
  `SHOP_ID` int(11) NOT NULL default '0' COMMENT '商店ID',
  `PRODUCT_CODE` varchar(50) NOT NULL default "" COMMENT '商品编号',
  `PRODUCT_NAME` varchar(1000) NOT NULL default "" COMMENT '商品名称',
  `PRODUCT_URL` varchar(1000)  COMMENT '商品链接',
  `BAR_CODE` varchar(13) COMMENT '商品条码',
  `IMG_URL` varchar(1000)  COMMENT '图片路径',
  `TOKEN_BRAND` varchar(20) NOT NULL default "" COMMENT '标准品牌',
  `TOKEN_GATEGORY` varchar(20) NOT NULL default "" COMMENT '标准类目',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default