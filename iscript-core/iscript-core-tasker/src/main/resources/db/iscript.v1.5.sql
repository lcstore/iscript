DROP TABLES IF EXISTS T_MESSAGE;
CREATE TABLE `T_MESSAGE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) COMMENT '消息名称',
  `MESSAGE` varchar(2000)  COMMENT '消息内容',
  `SOURCE` varchar(50) COMMENT '消息来源',
  `REMARK` varchar(100) COMMENT '备注信息',
  `STATUS` tinyint NOT NULL DEfault '0' COMMENT '状态',
  `SORT_CODE` tinyint NOT NULL DEfault '0' COMMENT '类型编码',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_T_MESSAGE_NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '消息记录表';


DROP TABLES IF EXISTS T_PROMOTION_MAP;
CREATE TABLE `T_PROMOTION_MAP` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SITE_ID` varchar(50) COMMENT '站点ID',
  `PRODUCT_CODE` varchar(50)  COMMENT '商品编码',
  `PROMOTE_CODE` varchar(50)  COMMENT '促销编码',
  `PROMOTE_NAME` varchar(50) COMMENT '促销名称',
  `PROMOTE_DETAIL` varchar(1000) COMMENT '促销内容',
  `PROMOTE_URL` varchar(1000) COMMENT '促销链接',
  `PROMOTE_TYPE` tinyint NOT NULL default '0' COMMENT '促销类型，0-满减,1-满赠，2-满折',
  `PROMOTE_STATUS` tinyint NOT NULL default '0' COMMENT '状态，-1-促销未开始,0-促销中，1-促销结束',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_T_PROMOTION_MAP_CODE_SITE` (`SITE_ID`,`PRODUCT_CODE`),
  KEY `IDX_T_PROMOTION_MAP_MCODE_SITE` (`SITE_ID`,`PROMOTE_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '促销产品映射表';

DROP TABLES IF EXISTS T_PROMOTION_DETAIL;
CREATE TABLE `T_PROMOTION_DETAIL` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SITE_ID` varchar(50) COMMENT '站点ID',
  `PROMOTE_CODE` varchar(50)  COMMENT '促销编码',
  `PROMOTE_NAME` varchar(50) COMMENT '促销名称',
  `PROMOTE_DETAIL` varchar(1000) COMMENT '促销内容',
  `PROMOTE_URL` varchar(1000) COMMENT '促销链接',
  `PROMOTE_TYPE` tinyint NOT NULL default '0' COMMENT '促销类型，0-满减,1-满赠，2-满折',
  `PROMOTE_STATUS` tinyint NOT NULL default '0' COMMENT '状态，-1-促销未开始,0-促销中，1-促销结束',
  `CREATE_TIME` timestamp NULL  COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_T_PROMOTION_MAP_MCODE_SITE` (`SITE_ID`,`PROMOTE_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '促销详情表';