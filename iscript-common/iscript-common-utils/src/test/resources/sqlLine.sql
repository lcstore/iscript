  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `SHOP_NAME` varchar(1000) NOT NULL COMMENT '店铺名称',
  `SHOP_CODE` varchar(200) DEFAULT '' COMMENT '店铺编码',
  `SHOP_URL` varchar(1000) NOT NULL COMMENT '店铺地址',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp