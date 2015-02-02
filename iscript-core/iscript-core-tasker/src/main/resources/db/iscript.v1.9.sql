ALTER TABLE T_MESSAGE
ADD COLUMN `DATA_BUCKET` varchar(10) NOT NULL DEFAULT '' COMMENT '数据桶',
ADD COLUMN `DATA_DOMAIN` varchar(20) NOT NULL DEFAULT '' COMMENT '数据域名',
ADD COLUMN `DATA_COUNT` int NOT NULL DEFAULT '0' COMMENT '数据量';

ALTER TABLE T_PROXY_DETECT
ADD COLUMN `TYPE` tinyint(4) NOT NULL default '0' COMMENT '代理类型,0-未知,1-HTTP代理,2-SOCKET代理',
ADD INDEX IDX_T_PROXY_DETECT_CODE_DOMAIN USING BTREE ( `ADDR_CODE`, `DOMAIN` );