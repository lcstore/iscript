ALTER TABLE T_TYPE_CONFIG
ADD COLUMN `LEVEL_LANG` varchar(100) NOT NULL default '' COMMENT '等级表达式,{exclude:{range:,list:},include:{range:,list:}}';

ALTER TABLE T_TYPE_CONFIG
ADD COLUMN `ASSIGN_MAX_SIZE` int NOT NULL default '-1' COMMENT '分配给个爬虫的最大数量';



ALTER TABLE T_PRODUCT
ADD COLUMN `TOKEN_BRAND` varchar(20) NOT NULL default '' COMMENT '标准品牌',
ADD COLUMN `TOKEN_CATEGORY` varchar(50) NOT NULL default '' COMMENT '标准类目';

--匹配切词规则&相似度计算规则
DROP TABLE IF EXISTS `T_SIMILAR_STRATEGY`;
CREATE TABLE `T_SIMILAR_STRATEGY` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SIMILAR_STRATEGY_NAME` varchar(100) NOT NULL default '' COMMENT '相似策略名称',
  `TOKENIZER_STRATEGY_ID` bigint(20) NOT NULL default '0' COMMENT '切词策略ID',
  `SIMILAR_CLASS` varchar(1000) NOT NULL default '' COMMENT '相似度类名称',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '更新时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `T_TOKENIZER_STRATEGY`;
CREATE TABLE `T_TOKENIZER_STRATEGY` (
  `ID` bigint(20) NOT NULL auto_increment,
  `STRATEGY_ID` bigint(20) NOT NULL default '0' COMMENT '切词策略ID',
  `STRATEGY_NAME` varchar(20) NOT NULL default '' COMMENT '切词关键词',
  `TOKENIZER_ID` bigint(20) NOT NULL default '' COMMENT '切词关键词',
  `SEQUENCE` tinyint(4) NOT NULL default '0' COMMENT '顺序值',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '更新时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_TOKENIZER_STRATEGY_STRATEGY_ID` USING BTREE (`STRATEGY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `T_TOKENIZER`;
CREATE TABLE `T_TOKENIZER` (
  `ID` bigint(20) NOT NULL auto_increment,
  `TOKEN_KEY` varchar(20) NOT NULL default '' COMMENT '切词关键词',
  `TOKEN_NAME` varchar(20) NOT NULL default '' COMMENT '切词器名称',
  `TOKEN_CLASS` varchar(1000) NOT NULL default '' COMMENT '切词器类名称',
  `TOKEN_DESC` varchar(20) NOT NULL default '' COMMENT '切词器描述信息',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '更新时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

