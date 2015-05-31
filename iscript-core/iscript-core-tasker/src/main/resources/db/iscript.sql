/*
 Navicat Premium Data Transfer

 Source Server         : iscript
 Source Server Type    : MySQL
 Source Server Version : 50051
 Source Host           : www.lezomao.com
 Source Database       : iscript

 Target Server Type    : MySQL
 Target Server Version : 50051
 File Encoding         : utf-8

 Date: 05/31/2015 16:58:04 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `T_BARCODE_ITEM`
-- ----------------------------
DROP TABLE IF EXISTS `T_BARCODE_ITEM`;
CREATE TABLE `T_BARCODE_ITEM` (
  `ID` bigint(20) NOT NULL auto_increment,
  `BAR_CODE` varchar(13) NOT NULL COMMENT '商品条码',
  `PRODUCT_NAME` varchar(1000) NOT NULL COMMENT '商品名称',
  `PRODUCT_URL` varchar(1000) default NULL COMMENT '商品链接',
  `PRODUCT_BRAND` varchar(200) default NULL COMMENT '品牌名称',
  `PRODUCT_MODEL` varchar(200) default NULL COMMENT '型号',
  `PRODUCT_ATTR` varchar(2000) default NULL COMMENT '商品属性',
  `IMG_URL` varchar(2000) default NULL COMMENT '图片路径',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `INDEX_T_BARCODE_ITEM_BAR_CODE` USING BTREE (`BAR_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=260858 DEFAULT CHARSET=utf8 COMMENT='条码商品表';

-- ----------------------------
--  Table structure for `T_BRAND`
-- ----------------------------
DROP TABLE IF EXISTS `T_BRAND`;
CREATE TABLE `T_BRAND` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SITE_ID` int(11) NOT NULL default '0' COMMENT '站点ID',
  `BRAND_CODE` varchar(20) NOT NULL default '' COMMENT '品牌编码',
  `BRAND_NAME` varchar(50) NOT NULL default '' COMMENT '品牌名称',
  `BRAND_URL` varchar(1000) NOT NULL default '' COMMENT '品牌链接',
  `SYNONYM_CODE` varchar(50) NOT NULL default '' COMMENT '同义词编码',
  `REGION` varchar(50) NOT NULL default '' COMMENT '地区',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_BRAND_SITE_CODE` USING BTREE (`SITE_ID`,`BRAND_CODE`),
  KEY `IDX_T_BRAND_SITE_SYNCODE` USING BTREE (`SITE_ID`,`SYNONYM_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=102843 DEFAULT CHARSET=utf8 COMMENT='品牌库';

-- ----------------------------
--  Table structure for `T_BRAND_SHOP`
-- ----------------------------
DROP TABLE IF EXISTS `T_BRAND_SHOP`;
CREATE TABLE `T_BRAND_SHOP` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SITE_ID` int(11) NOT NULL default '0' COMMENT '站点ID',
  `BRAND_CODE` varchar(20) NOT NULL default '' COMMENT '品牌编码',
  `BRAND_NAME` varchar(50) NOT NULL default '' COMMENT '品牌名称',
  `SHOP_NAME` varchar(50) NOT NULL default '' COMMENT '店铺名称',
  `SHOP_CODE` varchar(20) NOT NULL default '' COMMENT '店铺编码',
  `SHOP_URL` varchar(1000) default NULL COMMENT '店铺链接',
  `SHOP_TYPE` tinyint(4) NOT NULL default '3' COMMENT '0-旗舰店，1-专卖店，2-专营店，3-其他',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_BRAND_SHOP_NAME_BCODE` USING BTREE (`SHOP_NAME`,`BRAND_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=124152 DEFAULT CHARSET=utf8 COMMENT='品牌店铺表';

-- ----------------------------
--  Table structure for `T_CLIENT_TOKEN`
-- ----------------------------
DROP TABLE IF EXISTS `T_CLIENT_TOKEN`;
CREATE TABLE `T_CLIENT_TOKEN` (
  `ID` bigint(20) NOT NULL auto_increment,
  `CLIENT_TYPE` varchar(20) NOT NULL COMMENT '客户端类型',
  `CLIENT_BUCKET` varchar(20) NOT NULL COMMENT '桶名称',
  `CLIENT_DOMAIN` varchar(50) NOT NULL COMMENT '域名',
  `CLIENT_KEY` varchar(100) NOT NULL default '' COMMENT '客户端KEY',
  `CLIENT_SECRET` varchar(100) NOT NULL default '' COMMENT '客户端密钥',
  `CLIENT_PARAMS` varchar(200) NOT NULL default '' COMMENT '其他参数',
  `REFRESH_TOKEN` varchar(100) NOT NULL default '' COMMENT '刷新的TOKEN',
  `ACCESS_TOKEN` varchar(100) NOT NULL default '' COMMENT '使用的TOKEN',
  `SUCCESS_COUNT` int(11) NOT NULL default '0' COMMENT '成功数',
  `FAIL_COUNT` int(11) NOT NULL default '0' COMMENT '失败数',
  `LAST_MESSGE` varchar(1000) NOT NULL default '' COMMENT '上次信息',
  `NEXT_REFRESH_TIME` datetime default NULL COMMENT '下次刷新时间',
  `IS_DELETE` tinyint(4) NOT NULL default '0' COMMENT '0-未删除,1-删除',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_CLIENT_TOKEN_CLIENT_TYPE_BUCKET` (`CLIENT_TYPE`,`CLIENT_BUCKET`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='客户端授权表';

-- ----------------------------
--  Table structure for `T_CRAWLER_WARN_HIS`
-- ----------------------------
DROP TABLE IF EXISTS `T_CRAWLER_WARN_HIS`;
CREATE TABLE `T_CRAWLER_WARN_HIS` (
  `ID` bigint(20) NOT NULL auto_increment,
  `TYPE` varchar(50) NOT NULL COMMENT '任务类型',
  `CLIENT_NAME` varchar(50) NOT NULL default '0' COMMENT '客户端名称',
  `MAC_ADDR` varchar(100) NOT NULL default '0' COMMENT 'MAC地址',
  `TASK_ID` bigint(20) NOT NULL default '0' COMMENT '任务ID',
  `PROCESS_ID` varchar(50) NOT NULL default '0' COMMENT '进度ID',
  `PARAM` varchar(2000) NOT NULL default '0' COMMENT '任务参数',
  `RETRY` int(11) NOT NULL default '0' COMMENT '重试次数',
  `WARN_NAME` varchar(200) NOT NULL default '' COMMENT '异常名称',
  `MESSAGE` text COMMENT '异常信息',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_CRAWLER_WARN_HIS_CLIENT_NAME` USING BTREE (`CLIENT_NAME`),
  KEY `IDX_T_CRAWLER_WARN_HIS_TYPE` USING BTREE (`TYPE`)
) ENGINE=InnoDB AUTO_INCREMENT=8977 DEFAULT CHARSET=utf8 COMMENT='抓取异常历史表';

-- ----------------------------
--  Table structure for `T_LIST_RANK`
-- ----------------------------
DROP TABLE IF EXISTS `T_LIST_RANK`;
CREATE TABLE `T_LIST_RANK` (
  `ID` bigint(20) NOT NULL auto_increment,
  `CATEGORY_NAME` varchar(100) default NULL COMMENT '目录名称',
  `LIST_URL` varchar(1000) default NULL COMMENT '列表URL',
  `SHOP_ID` int(11) NOT NULL COMMENT '店铺ID',
  `PRODUCT_CODE` varchar(50) NOT NULL COMMENT '商品编号',
  `PRODUCT_NAME` varchar(1000) NOT NULL COMMENT '商品名称',
  `PRODUCT_PRICE` float(12,2) default NULL COMMENT '商品价格',
  `PRODUCT_URL` varchar(1000) default NULL COMMENT '商品链接',
  `SORT_TYPE` tinyint(4) NOT NULL default '0' COMMENT '排序类型',
  `SORT_RANK` int(11) NOT NULL default '0' COMMENT '商品链接',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_LIST_RANK_CATEGORY_NAME` USING BTREE (`CATEGORY_NAME`),
  KEY `IDX_T_LIST_RANK_PRODUCT_CODE` USING BTREE (`PRODUCT_CODE`),
  KEY `IDX_T_LIST_RANK_LIST_URL` USING BTREE (`LIST_URL`(50))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='列表产品排名表';

-- ----------------------------
--  Table structure for `T_LUCENE_INDEX`
-- ----------------------------
DROP TABLE IF EXISTS `T_LUCENE_INDEX`;
CREATE TABLE `T_LUCENE_INDEX` (
  `ID` bigint(20) NOT NULL auto_increment,
  `MESSAGE` varchar(1000) default NULL COMMENT '索引信息',
  `DATA_COUNT` int(11) default NULL COMMENT '数据量',
  `DATA_DAY` date default NULL COMMENT '数据日期',
  `RETRY` tinyint(4) NOT NULL default '0' COMMENT '重试次数',
  `STATUS` tinyint(4) NOT NULL default '0' COMMENT '0-创建中，1-创建结束，-1-创建失败',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_LUCENE_INDEX_CREATE_TIME` USING BTREE (`CREATE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8 COMMENT='LUCENE索引表';

-- ----------------------------
--  Table structure for `T_MESSAGE`
-- ----------------------------
DROP TABLE IF EXISTS `T_MESSAGE`;
CREATE TABLE `T_MESSAGE` (
  `ID` bigint(20) NOT NULL auto_increment,
  `NAME` varchar(50) default NULL COMMENT '消息名称',
  `MESSAGE` varchar(2000) default NULL COMMENT '消息内容',
  `SOURCE` varchar(50) default NULL COMMENT '消息来源',
  `REMARK` varchar(100) default NULL COMMENT '备注信息',
  `STATUS` tinyint(4) NOT NULL default '0' COMMENT '状态',
  `SORT_CODE` tinyint(4) NOT NULL default '0' COMMENT '类型编码',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  `DATA_BUCKET` varchar(10) NOT NULL default '' COMMENT '数据桶',
  `DATA_DOMAIN` varchar(50) NOT NULL default '' COMMENT '数据域名',
  `DATA_COUNT` int(11) NOT NULL default '0' COMMENT '数据量',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_MESSAGE_NAME` USING BTREE (`NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=236470 DEFAULT CHARSET=utf8 COMMENT='消息记录表';

-- ----------------------------
--  Table structure for `T_PRODUCT`
-- ----------------------------
DROP TABLE IF EXISTS `T_PRODUCT`;
CREATE TABLE `T_PRODUCT` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SITE_ID` int(11) NOT NULL default '0' COMMENT '站点ID',
  `SHOP_ID` int(11) NOT NULL COMMENT '商店ID',
  `PRODUCT_CODE` varchar(50) NOT NULL COMMENT '商品编号',
  `PRODUCT_NAME` varchar(1000) NOT NULL COMMENT '商品名称',
  `MARKET_PRICE` float(12,2) default NULL COMMENT '市场价',
  `PRODUCT_URL` varchar(1000) default NULL COMMENT '商品链接',
  `PRODUCT_BRAND` varchar(200) default NULL COMMENT '品牌名称',
  `PRODUCT_MODEL` varchar(200) default NULL COMMENT '型号',
  `PRODUCT_ATTR` varchar(2000) default NULL COMMENT '商品属性',
  `BAR_CODE` varchar(13) default NULL COMMENT '商品条码',
  `IMG_URL` varchar(2000) default NULL COMMENT '图片路径',
  `UNION_URL` varchar(1000) default NULL COMMENT '推广URL',
  `ONSAIL_TIME` timestamp NULL default NULL COMMENT '上架时间',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  `CATEGORY_NAV` varchar(1000) default NULL COMMENT '类目导航',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_PRODUCT_CODE` USING BTREE (`PRODUCT_CODE`,`SHOP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=74557 DEFAULT CHARSET=utf8 COMMENT='产品表';

-- ----------------------------
--  Table structure for `T_PRODUCT_STAT`
-- ----------------------------
DROP TABLE IF EXISTS `T_PRODUCT_STAT`;
CREATE TABLE `T_PRODUCT_STAT` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SITE_ID` int(11) NOT NULL default '0' COMMENT '站点ID',
  `SHOP_ID` int(11) NOT NULL COMMENT '商店ID',
  `PRODUCT_CODE` varchar(50) default NULL COMMENT '商品编码',
  `PRODUCT_NAME` varchar(1000) NOT NULL COMMENT '商品名称',
  `PRODUCT_URL` varchar(1000) default NULL COMMENT '商品链接',
  `CATEGORY_NAV` varchar(1000) default NULL COMMENT '目录导航',
  `MIN_PRICE` float(12,2) default NULL COMMENT '最低价',
  `MAX_PRICE` float(12,2) default NULL COMMENT '最高价',
  `PRODUCT_PRICE` float(12,2) default NULL COMMENT '当前价',
  `MARKET_PRICE` float(12,2) default NULL COMMENT '市场价',
  `SOLD_NUM` int(11) default NULL COMMENT '销量',
  `COMMENT_NUM` int(11) default NULL COMMENT '评论数',
  `GOOD_COMMENT` int(11) default NULL COMMENT '好评数',
  `POOR_COMMENT` int(11) default NULL COMMENT '差评数',
  `STOCK_NUM` int(11) default NULL COMMENT '创建时间,下架：<0，缺货：0，有货：>0',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_PRODUCT_STAT_CODE_SHOP_ID` USING BTREE (`PRODUCT_CODE`,`SHOP_ID`),
  KEY `IDX_T_PRODUCT_STAT_UPDATE_TIME` USING BTREE (`UPDATE_TIME`),
  KEY `IDX_T_PRODUCT_STAT_CATEGORY_NAV` USING BTREE (`CATEGORY_NAV`(50))
) ENGINE=InnoDB AUTO_INCREMENT=74157 DEFAULT CHARSET=utf8 COMMENT='产品状态表';

-- ----------------------------
--  Table structure for `T_PRODUCT_STAT_HIS`
-- ----------------------------
DROP TABLE IF EXISTS `T_PRODUCT_STAT_HIS`;
CREATE TABLE `T_PRODUCT_STAT_HIS` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SITE_ID` int(11) NOT NULL default '0' COMMENT '站点ID',
  `SHOP_ID` int(11) NOT NULL COMMENT '商店ID',
  `PRODUCT_CODE` varchar(50) default NULL COMMENT '商品编码',
  `PRODUCT_NAME` varchar(1000) NOT NULL COMMENT '商品名称',
  `PRODUCT_URL` varchar(1000) default NULL COMMENT '商品链接',
  `CATEGORY_NAV` varchar(1000) default NULL COMMENT '目录导航',
  `MIN_PRICE` float(12,2) default NULL COMMENT '最低价',
  `MAX_PRICE` float(12,2) default NULL COMMENT '最高价',
  `PRODUCT_PRICE` float(12,2) default NULL COMMENT '当前价',
  `MARKET_PRICE` float(12,2) default NULL COMMENT '市场价',
  `SOLD_NUM` int(11) default NULL COMMENT '销量',
  `COMMENT_NUM` int(11) default NULL COMMENT '评论数',
  `GOOD_COMMENT` int(11) default NULL COMMENT '好评数',
  `POOR_COMMENT` int(11) default NULL COMMENT '差评数',
  `STOCK_NUM` int(11) default NULL COMMENT '创建时间,下架：<0，缺货：0，有货：>0',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_PRODUCT_STAT_HIS_CODE` USING BTREE (`PRODUCT_CODE`),
  KEY `IDX_T_PRODUCT_STAT_HIS_CREATE_TIME` USING BTREE (`CREATE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=1431257 DEFAULT CHARSET=utf8 COMMENT='产品状态历史表';

-- ----------------------------
--  Table structure for `T_PROMOTION_MAP`
-- ----------------------------
DROP TABLE IF EXISTS `T_PROMOTION_MAP`;
CREATE TABLE `T_PROMOTION_MAP` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SITE_ID` varchar(50) default NULL COMMENT '站点ID',
  `PRODUCT_CODE` varchar(50) default NULL COMMENT '商品编码',
  `PROMOTE_CODE` varchar(50) default NULL COMMENT '促销编码',
  `PROMOTE_NAME` varchar(50) default NULL COMMENT '促销名称',
  `PROMOTE_DETAIL` varchar(1500) default NULL COMMENT '促销内容',
  `PROMOTE_NUMS` varchar(1000) default NULL COMMENT '促销数值',
  `PROMOTE_URL` varchar(1000) default NULL COMMENT '促销链接',
  `PROMOTE_EXTRA` varchar(1500) default NULL COMMENT '附加信息',
  `PROMOTE_TYPE` tinyint(4) NOT NULL default '0' COMMENT '促销类型，0-满减,1-满赠，2-满折',
  `PROMOTE_STATUS` tinyint(4) NOT NULL default '0' COMMENT '状态，-1-促销未开始,0-促销中，1-促销结束',
  `IS_DELETE` tinyint(4) NOT NULL default '0' COMMENT '0-未删除,1-删除',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_PROMOTION_MAP_CODE_SITE` USING BTREE (`SITE_ID`,`PRODUCT_CODE`),
  KEY `IDX_T_PROMOTION_MAP_MCODE_SITE` USING BTREE (`SITE_ID`,`PROMOTE_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=9639 DEFAULT CHARSET=utf8 COMMENT='促销产品映射表';

-- ----------------------------
--  Table structure for `T_PROMOTION_TRACK`
-- ----------------------------
DROP TABLE IF EXISTS `T_PROMOTION_TRACK`;
CREATE TABLE `T_PROMOTION_TRACK` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SITE_ID` int(11) NOT NULL default '0' COMMENT '站点ID',
  `PRODUCT_CODE` varchar(50) default NULL COMMENT '商品编码',
  `PRODUCT_NAME` varchar(1000) NOT NULL COMMENT '商品名称',
  `PRODUCT_URL` varchar(1000) default NULL COMMENT '商品链接',
  `FROM_PRICE` float(12,2) default NULL COMMENT '当前价',
  `TARGET_PRICE` float(12,2) default NULL COMMENT '目标促销价',
  `TO_PRICE` float(12,2) default NULL COMMENT '最终促销价',
  `PROMOTION_DETAIL` varchar(1000) NOT NULL COMMENT '促销信息',
  `FROM_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `TO_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_PROMOTION_TRACK_SITE_CODE` USING BTREE (`SITE_ID`,`PRODUCT_CODE`),
  KEY `IDX_T_PROMOTION_TRACK_FROM_TIME` USING BTREE (`FROM_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='诚信促销跟踪';

-- ----------------------------
--  Table structure for `T_PROXY_ADDR`
-- ----------------------------
DROP TABLE IF EXISTS `T_PROXY_ADDR`;
CREATE TABLE `T_PROXY_ADDR` (
  `ID` bigint(20) NOT NULL auto_increment,
  `IP` int(10) unsigned NOT NULL COMMENT 'IP地址值',
  `PORT` int(11) NOT NULL COMMENT '端口',
  `ADDR_CODE` varchar(20) NOT NULL default '0' COMMENT 'IP-PORT',
  `REGION_NAME` varchar(50) default NULL COMMENT '地区名称',
  `ISP_NAME` varchar(50) default NULL COMMENT '服务商名称',
  `IS_DELETE` tinyint(4) default '0' COMMENT '状态',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  `TYPE` tinyint(4) NOT NULL default '0' COMMENT '代理类型,0-未知,1-HTTP代理,2-SOCKET代理',
  `REMARK` varchar(100) NOT NULL default '' COMMENT '备注',
  `SEED_ID` bigint(20) NOT NULL default '0' COMMENT '代理源ID',
  `MAP_TYPE` tinyint(4) NOT NULL default '0' COMMENT '地图类型',
  `MAP_LAT` varchar(20) NOT NULL default '' COMMENT '纬度',
  `MAP_LNG` varchar(20) NOT NULL default '' COMMENT '经度',
  `FAIL_COUNT` int(11) NOT NULL default '0' COMMENT '失败数',
  `SUCCESS_COUNT` int(11) NOT NULL default '0' COMMENT '成功数',
  `LAST_SUCCESS_COUNT` int(11) NOT NULL default '0' COMMENT '连续成功数',
  `REGION_COUNTRY` varchar(50) NOT NULL default '' COMMENT '国家',
  `REGION_CITY` varchar(50) NOT NULL default '' COMMENT '城市',
  PRIMARY KEY  (`ID`),
  KEY `INDEX_T_PROXY_ADDR_CODE` USING BTREE (`ADDR_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=4318937 DEFAULT CHARSET=utf8 COMMENT='代理地址表';

-- ----------------------------
--  Table structure for `T_PROXY_COLLECT_HIS`
-- ----------------------------
DROP TABLE IF EXISTS `T_PROXY_COLLECT_HIS`;
CREATE TABLE `T_PROXY_COLLECT_HIS` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SEED_ID` bigint(20) NOT NULL default '0' COMMENT '代理源ID',
  `TOTAL_PAGE` int(11) default '0' COMMENT '链接生成函数 ',
  `FETCH_PAGE` int(11) default '0' COMMENT '页面解码函数',
  `TOTAL_COUNT` int(11) default '0' COMMENT '总代理数',
  `NEW_COUNT` int(11) default '0' COMMENT '新代理数',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  `TASK_ID` bigint(20) NOT NULL default '0' COMMENT '任务ID',
  PRIMARY KEY  (`ID`),
  KEY `T_PROXY_COLLECT_HIS_SEED_ID` (`SEED_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=21325 DEFAULT CHARSET=utf8 COMMENT='代理收集历史';

-- ----------------------------
--  Table structure for `T_PROXY_DETECT`
-- ----------------------------
DROP TABLE IF EXISTS `T_PROXY_DETECT`;
CREATE TABLE `T_PROXY_DETECT` (
  `ID` bigint(20) NOT NULL auto_increment,
  `ADDR_CODE` varchar(20) NOT NULL default '0' COMMENT 'IP-PORT',
  `IP` int(10) unsigned NOT NULL COMMENT 'IP地址值',
  `PORT` int(11) NOT NULL COMMENT '端口',
  `DOMAIN` varchar(50) default NULL COMMENT '监测',
  `URL` varchar(1000) default NULL COMMENT '监测URL',
  `DETECTOR` varchar(100) default NULL COMMENT '监测者',
  `CUR_COST` int(11) default '0' COMMENT '耗时',
  `MIN_COST` int(11) default '0' COMMENT '最短耗时',
  `MAX_COST` int(11) default '0' COMMENT '最长耗时',
  `RETRY_TIMES` int(11) default '0' COMMENT '重试次数',
  `STATUS` tinyint(4) default '0' COMMENT '状态,-2:禁用，-1:停用，0:重试，2:可用',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  `FAIL_COUNT` int(11) NOT NULL default '0' COMMENT '失败数',
  `SUCCESS_COUNT` int(11) NOT NULL default '0' COMMENT '成功数',
  `LAST_SUCCESS_COUNT` int(11) NOT NULL default '0' COMMENT '连续成功数',
  `VERIFY_STATUS` tinyint(4) default '0' COMMENT '验证状态,0:未知,1:成功,-1:失败',
  `REMARK` varchar(100) NOT NULL default '' COMMENT '备注',
  `TYPE` tinyint(4) NOT NULL default '0' COMMENT '代理类型,0-未知,1-HTTP代理,2-SOCKET代理',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_PROXY_DETECT_IP_HOST` USING BTREE (`IP`,`PORT`),
  KEY `IDX_T_PROXY_DETECT_CODE_DOMAIN` USING BTREE (`ADDR_CODE`,`DOMAIN`)
) ENGINE=InnoDB AUTO_INCREMENT=2834992 DEFAULT CHARSET=utf8 COMMENT='代理检测表';

-- ----------------------------
--  Table structure for `T_PROXY_HOME`
-- ----------------------------
DROP TABLE IF EXISTS `T_PROXY_HOME`;
CREATE TABLE `T_PROXY_HOME` (
  `ID` bigint(20) NOT NULL auto_increment,
  `HOME_URL` varchar(200) NOT NULL default '' COMMENT '代理源URL',
  `CONFIG_PARSER` varchar(50) default NULL COMMENT '抓取配置',
  `MAX_PAGE` int(11) NOT NULL default '1' COMMENT '最大页数',
  `IS_DELETE` tinyint(4) NOT NULL default '0' COMMENT '0-否，1-是',
  `STATUS` tinyint(4) NOT NULL default '0' COMMENT '0-等待，1-处理中，2-完成',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `T_PROXY_HOME_HOME_URL` USING BTREE (`HOME_URL`(50))
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COMMENT='代理源';

-- ----------------------------
--  Table structure for `T_PROXY_SEED`
-- ----------------------------
DROP TABLE IF EXISTS `T_PROXY_SEED`;
CREATE TABLE `T_PROXY_SEED` (
  `ID` bigint(20) NOT NULL auto_increment,
  `URL` varchar(200) NOT NULL default '' COMMENT '代理URL',
  `CREATE_URLS_FUN` text NOT NULL COMMENT '链接生成函数 ',
  `DECODE_PAGE_FUN` text NOT NULL COMMENT '页面解码函数',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='代理种子';

-- ----------------------------
--  Table structure for `T_SEARCH_HIS`
-- ----------------------------
DROP TABLE IF EXISTS `T_SEARCH_HIS`;
CREATE TABLE `T_SEARCH_HIS` (
  `ID` bigint(20) NOT NULL auto_increment,
  `QUERY_WORD` varchar(50) NOT NULL default '' COMMENT '关键词',
  `QUERY_SOLR` varchar(200) NOT NULL default '{}' COMMENT 'SOLR语句',
  `QUERY_RESULT` text COMMENT '查询结果',
  `QUERY_COST` int(11) default NULL COMMENT '查询耗时',
  `QUERY_HIT` int(11) NOT NULL default '0' COMMENT '命中次数',
  `STATUS` tinyint(4) NOT NULL default '0' COMMENT '0-新建，1-查询中，2-查询完成，-1-失败,-2-失效',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `T_SEARCH_HIS_QUERY_SOLR` USING BTREE (`QUERY_SOLR`(50))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='搜索历史';

-- ----------------------------
--  Table structure for `T_SESSION_HIS`
-- ----------------------------
DROP TABLE IF EXISTS `T_SESSION_HIS`;
CREATE TABLE `T_SESSION_HIS` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SESSION_ID` varchar(36) NOT NULL COMMENT '会话ID',
  `CLIEN_NAME` varchar(100) NOT NULL COMMENT '客户端名称',
  `REQUEST_SIZE` int(11) NOT NULL default '0' COMMENT '请求数',
  `RESPONE_SIZE` int(11) NOT NULL default '0' COMMENT '响应数',
  `ERROR_SIZE` int(11) NOT NULL default '0' COMMENT '异常数',
  `SUCCESS_NUM` int(11) NOT NULL default '0' COMMENT '成功数',
  `FAIL_NUM` int(11) NOT NULL default '0' COMMENT '失败数',
  `STATUS` tinyint(4) NOT NULL default '1' COMMENT '状态，0-下线，1-上线',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_SESSION_HIS_SESSION_ID` USING BTREE (`SESSION_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=745 DEFAULT CHARSET=utf8 COMMENT='会话历史表';

-- ----------------------------
--  Table structure for `T_SHOP`
-- ----------------------------
DROP TABLE IF EXISTS `T_SHOP`;
CREATE TABLE `T_SHOP` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `PARENT_ID` int(11) default NULL COMMENT '父店铺',
  `SITE_CODE` varchar(20) default NULL COMMENT '站点域名',
  `SHOP_NAME` varchar(200) NOT NULL COMMENT '店铺名称',
  `SHOP_CODE` varchar(20) default NULL COMMENT '店铺编码',
  `SHOP_URL` varchar(1000) default NULL COMMENT '店铺地址',
  `ON_LINE` tinyint(4) default '0' COMMENT '线上店铺，0-否，1-是',
  `IS_SELF` tinyint(4) default '0' COMMENT '自营店，0-否，1-是',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_SHOP_SHOP_NAME` USING BTREE (`SHOP_NAME`),
  KEY `IDX_T_SHOP_SITE_CODE` USING BTREE (`SITE_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=6165 DEFAULT CHARSET=utf8 COMMENT='店铺信息表';

-- ----------------------------
--  Table structure for `T_SIMILAR`
-- ----------------------------
DROP TABLE IF EXISTS `T_SIMILAR`;
CREATE TABLE `T_SIMILAR` (
  `ID` bigint(20) NOT NULL auto_increment,
  `SIMILAR_CODE` bigint(20) NOT NULL COMMENT '相似编码,第一批的stamp',
  `SITE_ID` int(11) default NULL,
  `PRODUCT_CODE` varchar(50) default NULL COMMENT '商品编号',
  `PRODUCT_NAME` varchar(1000) default NULL COMMENT '商品名称',
  `PRODUCT_URL` varchar(1000) default NULL COMMENT '商品链接',
  `PRODUCT_PRICE` float(12,2) default NULL COMMENT '当前价',
  `BAR_CODE` varchar(13) default NULL COMMENT '商品条码',
  `IMG_URL` varchar(1000) default NULL COMMENT '图片路径',
  `SOURCE` varchar(100) default NULL COMMENT '来源，JSON存多个来源',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_SIMILAR_PRODUCT_CODE` USING BTREE (`PRODUCT_CODE`),
  KEY `IDX_T_SIMILAR_SIMILAR_CODE` USING BTREE (`SIMILAR_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=30238 DEFAULT CHARSET=utf8 COMMENT='相似商品表';

-- ----------------------------
--  Table structure for `T_TASK_CONFIG`
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_CONFIG`;
CREATE TABLE `T_TASK_CONFIG` (
  `ID` bigint(20) NOT NULL auto_increment,
  `TYPE` varchar(100) NOT NULL COMMENT '配置类型',
  `CONFIG` mediumtext NOT NULL COMMENT '配置内容',
  `SOURCE` varchar(100) NOT NULL default '0' COMMENT '配置来源，如jd-product.xml',
  `STATUS` tinyint(4) default '1' COMMENT '状态，0-disable,1-enable',
  `DEST_TYPE` tinyint(4) default '0' COMMENT '目标，0-配置，1-策略',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `T_TASK_PRIORITY`
-- ----------------------------
DROP TABLE IF EXISTS `T_TASK_PRIORITY`;
CREATE TABLE `T_TASK_PRIORITY` (
  `TASK_ID` bigint(20) NOT NULL auto_increment,
  `BATCH_ID` varchar(1000) default NULL COMMENT '批次ID',
  `TYPE` varchar(100) NOT NULL COMMENT '类型',
  `URL` varchar(1000) default NULL COMMENT '链接',
  `PARAMS` varchar(2000) default NULL COMMENT '参数',
  `LEVEL` tinyint(4) default NULL COMMENT '优先级',
  `STATUS` tinyint(4) default NULL COMMENT '状态,0-new,1-cacher,2-ASSIGN,3-DONE,4-error',
  `SOURCE` varchar(200) default NULL COMMENT '来源',
  `CREAT_TIME` timestamp NULL default NULL COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`TASK_ID`),
  KEY `IDX_T_TASK_PRIORITY_TYPE` USING BTREE (`TYPE`)
) ENGINE=InnoDB AUTO_INCREMENT=5636280 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `T_TYPE_CONFIG`
-- ----------------------------
DROP TABLE IF EXISTS `T_TYPE_CONFIG`;
CREATE TABLE `T_TYPE_CONFIG` (
  `ID` bigint(20) NOT NULL auto_increment,
  `TYPE` varchar(100) NOT NULL COMMENT '任务TYPE',
  `TASKER` varchar(200) NOT NULL default 'common' COMMENT 'TYPE拥有者，common为针对所有TASKER',
  `MIN_SIZE` int(11) NOT NULL default '1' COMMENT '最小值',
  `MAX_SIZE` int(11) NOT NULL default '5' COMMENT '最大值',
  `STATUS` smallint(6) NOT NULL default '1' COMMENT '状态,0-disable,1-enable',
  `CREATE_TIME` timestamp NULL default NULL COMMENT '更新时间',
  `UPDATE_TIME` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP COMMENT '更新时间',
  `LEVEL_LANG` varchar(100) NOT NULL default '' COMMENT '等级表达式,{exclude:{range:,list:},include:{range:,list:}}',
  `ASSIGN_MAX_SIZE` int(11) NOT NULL default '-1' COMMENT '分配给个爬虫的最大数量',
  PRIMARY KEY  (`ID`),
  KEY `IDX_T_TYPE_CONFIG_TASKER_STATUS` USING BTREE (`TASKER`,`STATUS`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
