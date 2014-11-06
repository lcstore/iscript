  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `data_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '数据日期',
  `category_name` varchar(100) NOT NULL DEFAULT '' COMMENT '分类',
  `category_id` int(10) DEFAULT NULL COMMENT '分类ID',
  `brand_name` varchar(100) NOT NULL COMMENT '档期名称',
  `pt_brand_name` varchar(100) NOT NULL COMMENT '品牌',
  `sell_date_start` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '上档时间',
  `sell_date_end` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '下档时间',
  `sales_amount_per` decimal(15,2) DEFAULT NULL COMMENT '售卖比',
  `competitiveness_index` int(10) DEFAULT NULL COMMENT '竞争力指数',
  `site_id` int(10) NOT NULL DEFAULT '1111' COMMENT '对手站点编号',
  `brand_count` int(10) NOT NULL DEFAULT '0' COMMENT '参与的档期数',
  `vcode_count` int(10) NOT NULL DEFAULT '0' COMMENT '档期下所有商品数',
  `ocode_count` int(10) NOT NULL DEFAULT '0' COMMENT '对手商品数',
  `vmatch_count` int(10) NOT NULL DEFAULT '0' COMMENT 'VIP匹配SKU数',
  `calc_count` int(10) NOT NULL DEFAULT '0' COMMENT '参与计算指数的商品数',
  `vavg_price` float(10,2) NOT NULL DEFAULT '0.00' COMMENT '唯品会平均价格',
  `oavg_price` float(10,2) NOT NULL DEFAULT '0.00' COMMENT '对手平均平均价格',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDd
