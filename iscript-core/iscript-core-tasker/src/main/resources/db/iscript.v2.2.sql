ALTER TABLE T_TYPE_CONFIG
ADD COLUMN `LEVEL_LANG` varchar(100) NOT NULL default '' COMMENT '等级表达式,{exclude:{range:,list:},include:{range:,list:}}';
