  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '店铺主键Id',
  `shop_name` varchar(50) DEFAULT '' COMMENT '店铺名称',
  `shop_url` varchar(1000) NOT NULL DEFAULT '' COMMENT '店铺连接，Ps：唯一',
  `shop_type` int(2) DEFAULT '0' COMMENT '店铺类型：自营，专营等',
  `shop_code` varchar(50) DEFAULT '' COMMENT '对手店铺id',
  `site_id` int(11) NOT NULL COMMENT '店铺所在商城Id',
  `lastest_start_time` timestamp NULL DEFAULT '0000-00-00 00:00:00' COMMENT '最近一次任务开始时间',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` timestamp 