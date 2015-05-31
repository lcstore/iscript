ALTER TABLE T_TYPE_CONFIG
ADD COLUMN `LEVEL_LANG` varchar(100) NOT NULL default '' COMMENT '等级表达式,{exclude:{range:,list:},include:{range:,list:}}';

ALTER TABLE T_TYPE_CONFIG
ADD COLUMN `ASSIGN_MAX_SIZE` int NOT NULL default '-1' COMMENT '分配给个爬虫的最大数量';


