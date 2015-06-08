  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `market_info_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '促销活动ID',
  `market_template_item_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '模板项ID',
  `market_item_value` varchar(2048) NOT NULL DEFAULT '' COMMENT '模板项值',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否,1.是',
  `update_time` timestamp