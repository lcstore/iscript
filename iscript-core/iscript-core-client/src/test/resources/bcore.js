var debug = {};
debug.log = function(msg) {
	if (msg) {
		java.lang.System.out.println(msg);
	} else {
		java.lang.System.out.println("NULL OR Undefine.");
	}
}
var Jsoup = org.jsoup.Jsoup;
var pageConfig = {
	compatible : true,
	product : {
		skuid : 1124365,
		name : '\u534e\u4e3a\u0020\u0041\u0073\u0063\u0065\u006e\u0064\u0020\u0050\u0037\u002d\u004c\u0030\u0039\u0020\u0034\u0047\u624b\u673a\uff08\u767d\u8272\uff09\u0054\u0044\u002d\u004c\u0054\u0045\u002f\u0043\u0044\u004d\u0041\u0032\u0030\u0030\u0030\u002f\u0047\u0053\u004d\u0020\u53cc\u5361\u53cc\u5f85\u53cc\u901a',
		skuidkey : '83E3F0A161EBAA6E0C1B7FF46CDD0FC9',
		href : 'http://item.jd.com/1124365.html',
		src : 'g16/M00/00/0E/rBEbRVNp5sMIAAAAAAErsXXbgr8AAAFmANdhqYAASvJ802.jpg',
		cat : [ 9987, 653, 655 ],
		brand : 8557,
		nBrand : 8557,
		tips : false,
		type : 1,
		venderId : 0,
		shopId : '0',
		TJ : '0',
		specialAttrs : [ "HYKHSP-0", "isHaveYB", "isSelfService-0",
				"isWeChatStock-0", "isCanUseJQ", "isOverseaPurchase-0",
				"packType", "IsNewGoods", "isCanUseDQ", "is7ToReturn-1",
				"isCanVAT" ],
		videoPath : '7170a1a199',
		HM : '0'
	}
};
var iQuery = function(selector, context) {
	if (!selector) {
		return this;
	}
	return new iQuery.fn.init(selector, context);
};
iQuery.fn = iQuery.prototype = {
	constructor : iQuery,
	init : function(selector, context) {
		context = context || src;
		this.elements = context.select(selector);
	}
};
iQuery.init = function(elements) {
	this.elements = elements;
}
iQuery.ajax = function(oParam) {
	var sUrl;
	for ( var key in oParam.data) {
		if (sUrl) {
			sUrl += '&' + key + '=' + oParam.data[key];
		} else {
			sUrl = key + '=' + oParam.data[key];
		}
	}
	sUrl = oParam.url + '?' + sUrl;
	debug.log(sUrl);
	var html = http.get(sUrl);
	debug.log(html);
	eval('' + html);
}

var $ = iQuery;

var G = {};
G.getNewUserLevel = function(t) {
	switch (t) {
	case 50:
		return "注册用户";
	case 56:
		return "铜牌用户";
	case 59:
		return "注册用户";
	case 60:
		return "银牌用户";
	case 61:
		return "银牌用户";
	case 62:
		return "金牌用户";
	case 63:
		return "钻石用户";
	case 64:
		return "经销商";
	case 110:
		return "VIP";
	case 66:
		return "京东员工";
	case -1:
		return "未注册";
	case 88:
		return "钻石用户";
	case 90:
		return "企业用户";
	case 103:
		return "钻石用户";
	case 104:
		return "钻石用户";
	case 105:
		return "钻石用户"
	}
	return "未知";
}
function readCookie(key) {
	return '1-72-4137-0';
}

try {
	String.prototype.process = function(oPromot, b) {
		var res = '';
		if (oPromot && oPromot.adwordGiftSkuList) {
			var adwordGiftSkuList = oPromot.adwordGiftSkuList;
			var len = adwordGiftSkuList.length;
			for (var i = 0; i < len; i++) {
				var item = adwordGiftSkuList[i];
				if (item.giftType == 2) {
					res += '<div class="li-img"><a target="_blank" href="http://item.jd.com/'
							+ item.skuId + '.html">';
					if (item.imagePath !== "") {
						res += '<img src="http://img11.360buyimg.com/n5/'
								+ item.imagePath + ' width="25" height="25" />';
					} else {
						res += '<img src="http://misc.360buyimg.com/product/skin/2012/i/gift.png" width="25" height="25" />';
					}
					if (item.name && item.name.length > 25) {
						res += item.name.substring(0, 24) + "...";
					} else {
						res += item.name;
					}
					res += '</a>\n';
				}
			}
		}
		debug.log(this);
		debug.log('ddd:' + res);
		return res;
	}
} catch (e) {
}

// -----------------------
var Promotions = {
	init : function(sku) {
		this.sku = sku || pageConfig.product.skuid;
		this.ipLoc = readCookie('ipLoc-djd') || '1_0';
		this.get();
	},
	get : function() {
		var _this = this;
		$.ajax({
			url : 'http://pi.3.cn/promoinfo/get',
			data : {
				id : this.sku,
				area : this.ipLoc.replace(/-/g, '_'),
				origin : 1,
				callback : 'Promotions.set'
			},
			// url: 'http://jprice.360buy.com/pageadword/' + _this.sku + '-1-1-'
			// + this.ipLoc.replace(/-/g, '_') +
			// '.html?callback=Promotions.set',
			dataType : "script",
			cache : true,
			scriptCharset : "utf-8"
		});
	},
	set : function(result) {
		var promotionsDiv = $('#summary-promotion .dd'), promotionsExtraDiv = $('#summary-promotion-extra .dd'), giftsDiv = $('#summary-gifts .dd'), tips = $('#summary-tips .dd');

		var promotionsItems = [], promotionsItemsExtra = [], promoType10 = [], giftsItems = [], tipsItems = [];

		var infoList = result.promotionInfoList;
		debug.log('infoList:' + infoList);
		var skuId = result.skuId;
		var str_len = (pageConfig.wideVersion && pageConfig.compatible) ? 35
				: 25;
		var gift_TPL = '{for item in adwordGiftSkuList}{if item.giftType==2}'
				+ '<div class="li-img">'
				+ '<a target="_blank" href="http://item.jd.com/${item.skuId}.html">{if item.imagePath !== ""}<img src="${pageConfig.FN_GetImageDomain(item.skuId)}n5/${item.imagePath}" width="25" height="25" />{else}<img src="http://misc.360buyimg.com/product/skin/2012/i/gift.png" width="25" height="25" />{/if}'
				+ '{if item.name.length > ' + str_len
				+ '}${item.name.substring(0,' + (str_len - 1)
				+ ')+"..."}{else}${item.name}{/if}</a>'
				+ '<em class="hl_red"> × ${item.number}</em>' + '</div>'
				+ '{/if}{/for}';

		// 京豆优惠购活动链接
		var jBeanDetailATag = '<a href="http://vip.jd.com/purchase.html" target="_blank">&nbsp;&nbsp;更多 <s class="s-arrow">&gt;&gt;</s></a>';
		// 会员特价活动链接
		var vipTeXiangDetailATag = '<a href="http://vip.jd.com/price.html" target="_blank">&nbsp;&nbsp;更多 <s class="s-arrow">&gt;&gt;</s></a>';
		// 企业会员活动链接
		var companyTeXiangDetailATag = '<a href="http://b.jd.com" target="_blank">&nbsp;&nbsp;更多 <s class="s-arrow">&gt;&gt;</s></a>';
		if (infoList !== null && infoList.length > 0) {
			for (var i = 0; i < infoList.length; i++) {
				var levelText = G.getNewUserLevel(infoList[i].userLevel);
				var coupon = infoList[i].adwordCouponList;
				var limText = '';
				var buyNum = '';
				if (infoList[i].minNum > 0 || infoList[i].maxNum > 0) {
					if (infoList[i].minNum > 0 && infoList[i].maxNum == 0) {
						limText = '购买至少' + infoList[i].minNum + '件时享受优惠';
						buyNum = '购买' + infoList[i].minNum + '件及以上';
					} else if (infoList[i].minNum == 0
							&& infoList[i].maxNum > 0) {
						limText = '购买最多' + infoList[i].maxNum + '件时享受优惠';
						buyNum = '购买' + infoList[i].maxNum + '件及以下';
					} else if (infoList[i].minNum < infoList[i].maxNum) {
						limText = '购买' + infoList[i].minNum + '-'
								+ infoList[i].maxNum + '件时享受优惠';
						buyNum = '购买' + infoList[i].minNum + '-'
								+ infoList[i].maxNum + '件';
					} else if (infoList[i].minNum == infoList[i].maxNum) {
						limText = '购买' + infoList[i].minNum + '件时享受优惠';
						buyNum = '购买' + infoList[i].minNum + '件';
					}
				}
				// 会员特享
				var huiyuantexiang = infoList[i].userLevel > 50
						&& infoList[i].minNum == 0 && infoList[i].maxNum == 0
						&& infoList[i].needJBeanNum <= 0;
				// 会员特享 限购
				var huiyuantexiang_xianguo = infoList[i].userLevel > 50
						&& (infoList[i].minNum > 0 || infoList[i].maxNum > 0)
						&& infoList[i].needJBeanNum <= 0;
				// 只有限购
				var xiangou = (infoList[i].minNum > 0 || infoList[i].maxNum > 0)
						&& infoList[i].userLevel <= 50
						&& infoList[i].needJBeanNum <= 0;
				// 非会员非限购
				var normal = infoList[i].userLevel <= 50
						&& infoList[i].minNum == 0 && infoList[i].maxNum == 0
						&& infoList[i].needJBeanNum <= 0;
				// 京豆优惠购
				var jBean = infoList[i].userLevel <= 50
						&& infoList[i].needJBeanNum > 0;
				// 单品是否和套装叠加
				var notOverlySuitTxt = '';
				var promoFlags = infoList[i].promoFlags;
				if (promoFlags != null && promoFlags.length > 0) {
					for (var k = 0; k < promoFlags.length; k++) {
						if (promoFlags[k] == 6) { // 单品不和套装叠加
							notOverlySuitTxt = '，且不与套装优惠同时享受';
						}
					}
				}
				// 获取券信息
				function setCoupon(coupon) {
					if (coupon != null && coupon.length > 0) {
						$
								.each(
										coupon,
										function(name, value) {
											if (value.type == 1) {
												var xianpinlei = value.key != null
														&& value.key != "";
												// var xianpinleiTxt =
												// xianpinlei ? '限品类' : '';
												var xianpinleiTxt = "";
												// 限品类券广告词
												var xianpinleiguanggao = value.adWord != null
														&& value.adWord.length > 0 ? '（'
														+ value.adWord + ')'
														: '';
												// 券会员特享
												if (huiyuantexiang) {
													promotionsItems
															.push('<em class="hl_red_bg">赠券</em><em class="hl_red">'
																	+ levelText
																	+ '及以上会员赠'
																	+ value.couponQouta
																	+ '元'
																	+ xianpinleiTxt
																	+ '京券'
																	+ xianpinleiguanggao
																	+ notOverlySuitTxt
																	+ '</em>');
												}
												// 券会员特享 限购
												if (huiyuantexiang_xianguo) {
													promotionsItems
															.push('<em class="hl_red_bg">赠券</em><em class="hl_red">'
																	+ levelText
																	+ '及以上会员赠'
																	+ value.couponQouta
																	+ '元'
																	+ xianpinleiTxt
																	+ '京券'
																	+ xianpinleiguanggao
																	+ '，且'
																	+ limText
																	+ notOverlySuitTxt
																	+ '</em>');
												}
												// 券限购
												if (xiangou) {
													promotionsItems
															.push('<em class="hl_red_bg">赠券</em><em class="hl_red">赠'
																	+ value.couponQouta
																	+ '元'
																	+ xianpinleiTxt
																	+ '京券'
																	+ xianpinleiguanggao
																	+ '，且'
																	+ limText
																	+ notOverlySuitTxt
																	+ '</em>');
												}
												// 普通赠券
												if (normal) {
													promotionsItems
															.push('<em class="hl_red_bg">赠券</em><em class="hl_red">赠'
																	+ value.couponQouta
																	+ '元'
																	+ xianpinleiTxt
																	+ '京券'
																	+ xianpinleiguanggao
																	+ notOverlySuitTxt
																	+ '</em>');
												}
											}
										});
					}
				}

				// 获取京豆
				function setScore(score) {
					if (score > 0) {
						// 京豆会员特享
						if (huiyuantexiang) {
							promotionsItems
									.push('<em class="hl_red_bg">赠京豆</em><em class="hl_red">'
											+ levelText
											+ '及以上会员赠'
											+ score
											+ '京豆' + notOverlySuitTxt + '</em>');
						}
						// 京豆会员特享 限购
						if (huiyuantexiang_xianguo) {
							promotionsItems
									.push('<em class="hl_red_bg">赠京豆</em><em class="hl_red">'
											+ levelText
											+ '及以上会员赠'
											+ score
											+ '京豆，且'
											+ limText
											+ notOverlySuitTxt + '</em>');
						}
						// 京豆限购
						if (xiangou) {
							promotionsItems
									.push('<em class="hl_red_bg">赠京豆</em><em class="hl_red">赠'
											+ score
											+ '京豆，且'
											+ limText
											+ notOverlySuitTxt + '</em>');
						}
						// 普通赠京豆
						if (normal) {
							promotionsItems
									.push('<em class="hl_red_bg">赠京豆</em><em class="hl_red">赠'
											+ score
											+ '京豆'
											+ notOverlySuitTxt
											+ '</em>');
						}
					}
				}

				// 单品促销
				if (infoList[i].promoType == 1) {
					// 会员特享
					if (infoList[i].price > 0) {
						var vipTeXiangItem = null;
						if (infoList[i].userLevel == 90) {
							vipTeXiangDetailATag = companyTeXiangDetailATag;
						}
						if (huiyuantexiang) {
							vipTeXiangItem = '<em class="hl_red_bg">会员特价</em><em class="hl_red">'
									+ levelText
									+ '及以上会员价：￥'
									+ infoList[i].price
									+ notOverlySuitTxt
									+ '</em>' + vipTeXiangDetailATag;
							promotionsItems.push(vipTeXiangItem);
						}
						if (huiyuantexiang_xianguo) {
							vipTeXiangItem = '<em class="hl_red_bg">会员特价</em><em class="hl_red">'
									+ levelText
									+ '及以上会员价：￥'
									+ infoList[i].price
									+ '，且'
									+ limText
									+ notOverlySuitTxt
									+ '</em>'
									+ vipTeXiangDetailATag;
							promotionsItems.push(vipTeXiangItem);
						}
					}
					// 普通直降
					if (infoList[i].discount > 0 && normal) {
						promotionsItems
								.push('<em class="hl_red_bg">直降</em><em class="hl_red">已优惠￥'
										+ infoList[i].discount
										+ notOverlySuitTxt + '</em>');
					}
					// 限购
					if (infoList[i].discount > 0 && xiangou) {
						if (infoList[i].minNum <= 1) {
							// promotionsItems.push('<em
							// class="hl_red_bg">限购</em><em class="hl_red">已优惠￥'
							// + infoList[i].discount + '，且' + limText +
							// '</em>');
							promotionsItems
									.push('<em class="hl_red_bg">限购</em><em class="hl_red">'
											+ limText
											+ notOverlySuitTxt
											+ '</em>');
						} else if (infoList[i].price > 0) {
							promotionsItems
									.push('<em class="hl_red_bg">限购</em><em class="hl_red">每件可享受优惠价￥'
											+ infoList[i].price
											+ '，且'
											+ limText
											+ notOverlySuitTxt
											+ '</em>');
						}
					}
					// 京豆优惠购
					if (jBean && infoList[i].price > 0) {
						var jBeanCondition = '';
						if (buyNum != '' && buyNum != null) {
							jBeanCondition = '（条件：' + buyNum + '）';
						}
						var jBeanItem = '<em class="hl_red_bg">京豆优惠购</em><em class="hl_red">使用'
								+ infoList[i].needJBeanNum
								+ '京豆可享受优惠价'
								+ infoList[i].price
								+ '元'
								+ jBeanCondition
								+ '</em>' + jBeanDetailATag;
						promotionsItems.push(jBeanItem);
					}
					setCoupon(infoList[i].adwordCouponList);
					setScore(infoList[i].score);
					var tuanTag = '', orginServiceText = $(
							'#summary-service .dd').html();
					if (infoList[i].extType == 8) {
						tuanTag = '闪团';
						$('#summary-service .dd').html(
								orginServiceText.replace('，支持货到付款', ''));
						if ($('#tuan-shouhou').length < 1) {
							$('#product-detail-5 .item-detail')
									.html(
											'<div id="tuan-shouhou"><p>如您购买闪团商品，即表明您认可下述约定并同意受其约束：</p> <ul> <li>1、闪团商品一经售出，如无质量问题，恕不退换。</li> <li>2、用户收到商品后十五日内（以快递公司送货单上的签收日期为准）如商品存在质量问题，可通过我的京东-返修/退换货页面申请办理退货手续；依据国家质量监 督检验检疫总局颁布的相关规定实施“三包”的商品，可依据相关规定通过本网站或联系京东商城客服人员办理退货手续；因闪团商品为限量商品，用户认可前述情形下仅办理退货手续。</li> <li>3、实施国家“三包”的商品，用户在收到商品后十五日内商品出现质量问题（以快递公司送货单上的签收日期为准）或三包有效期内商品经两次维修仍不能正常使用的，如销售方不能提供同型号同规格商品或不低于原商品性能的同品牌产品时，用户认可仅选择修理或者办理退货处理。</li> </ul> <p><strong>特别提示：</strong></p> <ul> <li>1）质量问题是指国家相关规定中列明的性能故障或不符合国家有关法规、强制性质量标准对产品适用、安全和其他特性的要求，因用户个人原因导致的性能故障或依据相关规定不能享受“三包”服务的情形除外。用户办理退货时须提供权威机构出具的产品存在质量问题的检测报告。</li> <li>2）用户办理退货手续时请务必将原装产品及配件、赠品、发票、三包凭证、附属资料等全部寄回，否则无法办理退货。</li> <li>3）用户在接收快递时，应查看包裹外包装是否有明显的破损、拆封等异常，如有异常用户可拒收，并由快递人员做异常处理；如果接收时包裹外包装完好，则用户 可与快递人员当场查验包裹内商品及配件是否与购买商品一致、是否有缺失、破损或与网站描述不一致等异常情况，如有异常则用户可拒收，并由快递人员做异常处 理。请用户将快递标注异常的证明及商品异常的情况拍照留存。闪团商品一经签收，即表明用户完全认可并接受商品。如无相反证据，用户不得以货物流损、缺件或 商品描述与网站不符等原因要求退货。</li> <li>用户接受上述售后服务的约定作为其参与购买闪团商品时向本网站发出要约不可分割的组成部分。上述约定与本网站公示的售后服务政策冲突的，以上述约定为准，未约定事项仍按照网站公示的内容执行。</li> </ul></div>');
						}
					} else if (infoList[i].extType == 4) {
						tuanTag = '团购';
					}
					if (infoList[i].extType == 8 || infoList[i].extType == 4) {
						if (promotionsItems.length) {
							promotionsItems[0] = promotionsItems[0].replace(
									/hl_red_bg">[\u4e00-\u9fa5]+</,
									'hl_red_bg">' + tuanTag + '<');
						}

						if ($('#tuan-tag').length > 0) {
							$('#tuan-tag').html('[' + tuanTag + '] ');
						} else {
							$('#name h1').prepend(
									'<span class="hl_red" id="tuan-tag">['
											+ tuanTag + '] </span>');
						}
					}
				}
				// 赠品条件
				var giftCondition = '';
				if (huiyuantexiang_xianguo || xiangou) {
					giftCondition = huiyuantexiang_xianguo ? '（条件：' + buyNum
							+ '、' + levelText + '及以上会员）' : '（条件：' + buyNum
							+ '）';
				}
				if (huiyuantexiang) {
					giftCondition = '（条件：' + levelText + '及以上会员）';
				}

				// 买多赠多
				if (infoList[i].promoType == 2 && infoList[i].minNum > 1) {
					// promotionsItems.push('<em class="hl_red_bg">满赠</em>购买' +
					// infoList[i].minNum + '件即得下方赠品');
					var giftList = infoList[i].adwordGiftSkuList;
					if (giftList.length > 0 & giftList !== null) {
						promotionsItems
								.push('<em class="hl_red_bg">赠品</em><em class="hl_red">赠下方的热销商品，赠完即止'
										+ giftCondition + '</em>');
						var res = gift_TPL.process(infoList[i]);
						if (res !== '') {
							giftsItems.push(res);
						}
					}
				}
				// 封顶促销
				if (infoList[i].promoType == 15 && infoList[i].rebate > 0) {
					var rebate = infoList[i].rebate;
					var bookTopAdword = '<em class="hl_red_bg">封顶</em><em class="hl_red">本商品参与'
							+ (rebate * 10).toFixed(1) + '折封顶活动</em>';
					if (xiangou) {
						bookTopAdword = '<em class="hl_red_bg">封顶</em><em class="hl_red">本商品参与'
								+ (rebate * 10).toFixed(1)
								+ '折封顶活动,且'
								+ limText + '</em>';
					}
					var adwordLink = infoList[i].adwordUrl;
					if (adwordLink != null && adwordLink.length > 0) {
						bookTopAdword += '<a href="'
								+ adwordLink
								+ '" target="_blank">&nbsp;&nbsp;详情 <s class="s-arrow">&gt;&gt;</s></a>';
					}
					promotionsItems.push(bookTopAdword);
				}
				// 赠品促销
				if (infoList[i].promoType == 4) {
					var giftList = infoList[i].adwordGiftSkuList;
					if (giftList.length > 0 & giftList !== null) {
						for (var k = 0; k < giftList.length; k++) {
							if (giftList[k].giftType == 2) {
								promotionsItems
										.push('<em class="hl_red_bg">赠品</em><em class="hl_red">赠下方的热销商品，赠完即止'
												+ giftCondition + '</em>');
								break;
							}
						}

						var res = gift_TPL.process(infoList[i]);
						if (res !== '') {
							giftsItems.push(res);
						}
					}
					setCoupon(infoList[i].adwordCouponList);
					setScore(infoList[i].score);
				}
				// 附件
				if (infoList[i].adwordGiftSkuList !== null
						&& infoList[i].adwordGiftSkuList.length > 0) {
					var gift_list = infoList[i].adwordGiftSkuList;
					if ($('#product-fj').length < 1) {
						$('#product-detail-3').append(
								'<div id="product-fj"></div>');
						for (var k = 0; k < gift_list.length; k++) {
							if (gift_list[k].giftType == 1) {
								$('#product-fj').append(
										'<div id="product-fj">'
												+ gift_list[k].name + ' × '
												+ gift_list[k].number
												+ '</div>')
							}
						}
					}
				}
				// 满返满赠促销
				if (infoList[i].promoType == 10) {
					// 满 赠、返
					var FULL_REFUND = 1;
					// 每满赠、返
					var FULL_REFUND_PER = 2;
					// 加价购
					var EXTRA_PRICE = 4;
					// 阶梯满减
					var FULL_LADDER = 6;
					// 满返百分比
					var PERCENT = 8;
					// M元买N件
					var FULLREFUND_MPRICE_NNUM = 13;
					// 满M件赠
					var FULLREFUND_MNUN_ZENG = 14;
					// 满M件N折
					var FULLNUM_MNUM_NREBATE = 15;
					// 满返满赠叠加促销
					var FULLPRICE_MFMZ = 16;
					// 满M件N折和满赠叠加促销
					var FULLNUM_REBATE_MFMZ = 17;
					// 满减池促销
					var FULL_POOL = 20;
					// 满返满赠促销子类型
					var fullRefundType = infoList[i].fullRefundType;
					var reward = infoList[i].reward;
					var needMoney = infoList[i].needMondey;
					var mzNeedMoney = infoList[i].mzNeedMoney;
					var mzNeedNum = infoList[i].mzNeedNum;
					var needNum = infoList[i].needNum;
					var addMoney = infoList[i].addMoney;
					var topMoney = infoList[i].topMoney;
					var percent = infoList[i].percent;
					var rebate = infoList[i].rebate;
					var deliverNum = infoList[i].deliverNum;
					var score = infoList[i].score;
					var couponList = infoList[i].adwordCouponList;
					var haveGifts = infoList[i].haveFullRefundGifts;
					var jq = 0;
					var fullLadderList = infoList[i].fullLadderDiscountList;
					var adwordLink = infoList[i].adwordUrl;
					var mfmzExtType = infoList[i].mfmzExtType;
					// 拼接满返满赠信息
					var fullRefundInfo = "";
					if (couponList != null && couponList.length > 0) {
						$.each(couponList, function(z, couponValue) {
							if (couponValue.type == 1) {
								jq = jq + coupon.couponQouta;
							}
						});
					}
					if (fullRefundType == FULL_REFUND) {
						if (fullLadderList != null && fullLadderList.length > 0) {
							$
									.each(
											fullLadderList,
											function(z, fullLadderValue) {
												var fNeedMoney = fullLadderValue.needMoney;
												var fRewardMoney = fullLadderValue.rewardMoney;
												var fAddMoney = fullLadderValue.addMoney;
												if (fNeedMoney > 0
														&& fRewardMoney > 0
														&& !haveGifts) {
													var isFirstSign = z == 0 ? ''
															: '，';
													var tipsHtml = z == 0 ? '<em class="hl_red_bg">满减</em>'
															: '';
													fullRefundInfo = (fullRefundInfo
															+ tipsHtml
															+ '<em class="hl_red">'
															+ isFirstSign
															+ '满'
															+ fNeedMoney
															+ '减'
															+ fRewardMoney + '</em>');
												}
												if (haveGifts) {
													var isFirstSign = z == 0 ? ''
															: '；';
													if (fNeedMoney > 0
															&& fRewardMoney > 0
															&& fAddMoney > 0) {
														var tipsHtml = z == 0 ? '<em class="hl_red_bg">满送</em>'
																: '';
														fullRefundInfo = (fullRefundInfo
																+ tipsHtml
																+ '<em class="hl_red">'
																+ isFirstSign
																+ '满'
																+ fNeedMoney
																+ '元减'
																+ fRewardMoney + '元以折扣价购买热销商品</em>');
													} else if (fNeedMoney > 0
															&& fRewardMoney > 0
															&& fAddMoney <= 0) { // 满减赠
														var tipsHtml = z == 0 ? '<em class="hl_red_bg">满送</em>'
																: '';
														fullRefundInfo = (fullRefundInfo
																+ tipsHtml
																+ '<em class="hl_red">'
																+ isFirstSign
																+ '满'
																+ fNeedMoney
																+ '元减'
																+ fRewardMoney + '元、得赠品（赠完即止）</em>');
													} else if (fNeedMoney > 0
															&& fRewardMoney <= 0
															&& fAddMoney > 0) {
														var tipsHtml = z == 0 ? '<em class="hl_red_bg">加价购</em>'
																: '';
														fullRefundInfo = (fullRefundInfo
																+ tipsHtml
																+ '<em class="hl_red">'
																+ isFirstSign
																+ '满'
																+ fNeedMoney + '元以折扣价购买热销商品</em>');
													} else {
														var tipsHtml = z == 0 ? '<em class="hl_red_bg">满赠</em>'
																: '';
														fullRefundInfo = (fullRefundInfo
																+ tipsHtml
																+ '<em class="hl_red">'
																+ isFirstSign
																+ '满'
																+ fNeedMoney + '元即赠热销商品，赠完即止</em>');
													}
												}
												if (jq > 0 && fNeedMoney > 0) {
													var isFirstSign = z == 0 ? ''
															: '；';
													var tipsHtml = z == 0 ? '<em class="hl_red_bg">满赠</em>'
															: '';
													fullRefundInfo = (fullRefundInfo
															+ tipsHtml
															+ '<em class="hl_red">'
															+ isFirstSign
															+ '满'
															+ fNeedMoney
															+ '元，赠' + jq + '元京券</em>');
												}
												if (fNeedMoney > 0
														&& percent > 0) {
													var isFirstSign = z == 0 ? ''
															: '；';
													var tipsHtml = z == 0 ? '<em class="hl_red_bg">满减</em>'
															: '';
													percent = percent * 100;
													fullRefundInfo = (fullRefundInfo
															+ tipsHtml
															+ '<em class="hl_red">'
															+ isFirstSign
															+ '满'
															+ fNeedMoney
															+ '元，可减' + percent + '%</em>');
												}
											});
						}
					} else if (fullRefundType == FULL_REFUND_PER) {
						if (needMoney > 0 && reward > 0) {
							fullRefundInfo = '<em class="hl_red_bg">满减</em><em class="hl_red">每满'
									+ needMoney + '元，可减' + reward + '元现金</em>';
							if (topMoney > 0) {
								fullRefundInfo += '<em class="hl_red">，最多可减'
										+ topMoney + '元</em>';
							}
						} else {
							if (haveGifts) {
								fullRefundInfo = '<em class="hl_red_bg">满赠</em><em class="hl_red">每满'
										+ needMoney + '元即赠，赠完即止</em>';
							} else if (jq > 0) {
								fullRefundInfo = '<em class="hl_red_bg">满赠</em><em class="hl_red">每满'
										+ needMoney + '元，即赠' + jq + '元京券</em>';
							}
						}
					} else if (fullRefundType == EXTRA_PRICE) {
						if (needMoney > 0 && addMoney > 0) {
							fullRefundInfo = '<em class="hl_red_bg">加价购</em><em class="hl_red">满'
									+ needMoney
									+ '元另加'
									+ addMoney
									+ '元即可购买热销商品</em>';
						}
					} else if (fullRefundType == PERCENT) {
						if (needMoney > 0 && percent > 0) {
							percent = percent * 100;
							fullRefundInfo = '<em class="hl_red_bg">满减</em><em class="hl_red">满'
									+ needMoney + '元，可减' + percent + '%</em>';
						}
					} else if (fullRefundType == FULL_LADDER) {
						if (fullLadderList != null && fullLadderList.length > 0) {
							// fullRefundInfo = '<em
							// class="hl_red_bg">满减</em>该商品参加阶梯满减活动，购买活动商品<br/>';
							$
									.each(
											fullLadderList,
											function(z, fullLadderValue) {
												var tipsHtml = z == 0 ? '<em class="hl_red_bg">满减</em>'
														: '', isFirstSign = z == 0 ? ''
														: '，';
												if (fullLadderValue.needMoney > 0
														&& fullLadderValue.rewardMoney > 0) {
													fullRefundInfo = (fullRefundInfo
															+ tipsHtml
															+ '<em class="hl_red">'
															+ isFirstSign
															+ '满'
															+ fullLadderValue.needMoney
															+ '减'
															+ fullLadderValue.rewardMoney + '</em>');
												}
											});
							// fullRefundInfo = fullRefundInfo.substring(0,
							// fullRefundInfo.length - 1)
						}
					} else if (fullRefundType == 11) {
						// 满返满赠促销大类型
						var moreLink = infoList[i].adwordUrl ? ' <a href="'
								+ infoList[i].adwordUrl
								+ '" target="_blank">详情&raquo;</a>' : '';
						var tipsHTML = '<em class="hl_red"><em class="hl_red_bg">多买优惠</em>满'
								+ infoList[i].needNum
								+ '件，立减最低'
								+ infoList[i].deliverNum
								+ '件商品价格</em>'
								+ moreLink;
						if (tipsHTML !== '') {
							promoType10.push(tipsHTML);
						}
					} else if (fullRefundType == FULL_POOL) {
						// 跨品类满减促销
						var list = infoList[i].fullLadderDiscountList, len = list.length, f, resText = [], resLink = infoList[i].adwordUrl;
						for (f = 0; f < len && len > 0; f++) {
							if (list[f].rebate > 0) {
								resText.push(list[f].minPoolNum + '类且满'
										+ parseInt(list[f].needMoney) + '元打'
										+ (list[f].rebate * 10).toFixed(1)
										+ '折');
							} else {
								resText.push(list[f].minPoolNum + '类且满'
										+ parseInt(list[f].needMoney) + '元减'
										+ parseInt(list[f].rewardMoney) + '元');
							}
						}
						if (len > 0) {
							var conditionTxt = '';
							if (list[0].maxSkuNumInPool > 0) {
								conditionTxt += '每类商品最多购买'
										+ list[0].maxSkuNumInPool + '件';
							}
							if (list[0].minTotalSkuNum > 1) {
								conditionTxt += '总件数至少'
										+ list[0].minTotalSkuNum + '件';
							}

							if (list[0].poolSkuUnique > 0) {
								if (list[0].maxSkuNumInPool > 0
										|| list[0].minTotalSkuNum > 1) {
									conditionTxt += '且型号不同';
								} else {
									conditionTxt += '须型号不同';
								}
							}

							if (conditionTxt.length > 0) {
								conditionTxt = '，' + conditionTxt;
							}

							promoType10
									.push('<em class="hl_red_bg">满减</em><em class="hl_red">'
											+ '购买'
											+ resText.join('，')
											+ conditionTxt
											+ '</em> <a href="'
											+ resLink
											+ '" target="_blank">详情<s class="s-arrow">&gt;&gt;</s></a>');
						}
					} else if (fullRefundType == FULLREFUND_MPRICE_NNUM) {
						if (needMoney > 0 && deliverNum > 0) {
							fullRefundInfo = '<em class="hl_red_bg">满减</em><em class="hl_red">满'
									+ needMoney + '元买' + deliverNum + '件</em>';
						}
					} else if (fullRefundType == FULLREFUND_MNUN_ZENG) {
						if (haveGifts && needNum > 0) {
							fullRefundInfo = '<em class="hl_red_bg">满赠</em><em class="hl_red">满'
									+ needNum + '件即赠热销商品，赠完即止</em>';
						}
						if (needNum > 0 && rebate > 0) {
							fullRefundInfo = '<em class="hl_red_bg">多买优惠</em><em class="hl_red">满'
									+ needNum
									+ '件，总价打'
									+ (rebate * 10).toFixed(1) + '折</em>';
						}

					} else if (fullRefundType == FULLNUM_MNUM_NREBATE) {
						if (fullLadderList != null && fullLadderList.length > 0) {
							$
									.each(
											fullLadderList,
											function(z, fullLadderValue) {
												var tipsHtml = z == 0 ? '<em class="hl_red_bg">多买优惠</em>'
														: '', isFirstSign = z == 0 ? ''
														: '；';
												if (fullLadderValue.needNum > 0) {
													fullRefundInfo = (fullRefundInfo
															+ tipsHtml
															+ '<em class="hl_red">'
															+ isFirstSign
															+ '满'
															+ fullLadderValue.needNum + '件');
													if (fullLadderValue.rebate > 0) {
														fullRefundInfo += '，总价打'
																+ (fullLadderValue.rebate * 10)
																		.toFixed(1)
																+ '折';
													}
													if (fullLadderValue.addMoney > 0) {
														fullRefundInfo += '，每加'
																+ fullLadderValue.addMoney
																+ '元即可购买热销商品';
													}
													fullRefundInfo += "</em>";
												}

											});
						} else {
							if (needNum > 0) {
								fullRefundInfo = '<em class="hl_red_bg">多买优惠</em><em class="hl_red">满'
										+ needNum + '件';
								if (rebate > 0) {
									fullRefundInfo += '，总价打'
											+ (rebate * 10).toFixed(1) + '折';
								}
								if (addMoney > 0) {
									fullRefundInfo += '，每加' + addMoney
											+ '元即可购买热销商品'
								}
								fullRefundInfo += "</em>";
							}

						}
					} else if (fullRefundType == FULLPRICE_MFMZ) {
						var mfmzExtTypeMF = 1;
						var mfmzExtTypeMZ = 2;
						var mfmzExtTypeMFJT = 3;
						var mfmzExtTypeMZJT = 4;
						var mfmzExtTypeMFMZ = 5;
						if (mfmzExtType === mfmzExtTypeMF) {
							$
									.each(
											fullLadderList,
											function(z, fullLadderValue) {
												if (fullLadderValue.needMoney > 0
														&& fullLadderValue.rewardMoney > 0) {
													fullRefundInfo = '<em class="hl_red_bg">满送</em><em class="hl_red">满'
															+ fullLadderValue.needMoney
															+ '立减'
															+ fullLadderValue.rewardMoney
															+ '</em>';
												}
											});
						}
						if (mfmzExtType === mfmzExtTypeMZ) {
							$
									.each(
											fullLadderList,
											function(z, fullLadderValue) {
												if (fullLadderValue.rewardMoney <= 0
														&& fullLadderValue.needMoney > 0
														&& fullLadderValue.addMoney <= 0) {
													fullRefundInfo = '<em class="hl_red_bg">满送</em><em class="hl_red">满'
															+ fullLadderValue.needMoney
															+ '元即赠热销商品，赠完即止</em>';
												}
												if (fullLadderValue.rewardMoney <= 0
														&& fullLadderValue.needMoney > 0
														&& fullLadderValue.addMoney > 0) {
													fullRefundInfo = '<em class="hl_red_bg">满送</em><em class="hl_red">满'
															+ fullLadderValue.needMoney
															+ '元另加'
															+ fullLadderValue.addMoney
															+ '元即赠热销商品，赠完即止</em>';
												}
											});
						}
						if (mfmzExtType === mfmzExtTypeMFJT) {
							$
									.each(
											fullLadderList,
											function(z, fullLadderValue) {
												var tipsHtml = z == 0 ? '<em class="hl_red_bg">满送</em>'
														: '', isFirstSign = z == 0 ? ''
														: '，';
												if (fullLadderValue.needMoney > 0
														&& fullLadderValue.rewardMoney > 0) {
													fullRefundInfo = (fullRefundInfo
															+ tipsHtml
															+ '<em class="hl_red">'
															+ isFirstSign
															+ '满'
															+ fullLadderValue.needMoney
															+ '减'
															+ fullLadderValue.rewardMoney + '</em>');
												}
											});
						}
						if (mfmzExtType === mfmzExtTypeMZJT) {
							if (addMoney <= 0) {
								$
										.each(
												fullLadderList,
												function(z, fullLadderValue) {
													var tipsHtml = z == 0 ? '<em class="hl_red_bg">满赠</em><em class="hl_red">'
															: '', isFirstSign = z == 0 ? ''
															: '，或';
													if (fullLadderValue.needMoney > 0) {
														fullRefundInfo = (fullRefundInfo
																+ tipsHtml
																+ isFirstSign
																+ '满'
																+ fullLadderValue.needMoney + '得赠品');
													}
												});
								fullRefundInfo += '，赠完即止</em>';
							}
							if (addMoney > 0) {
								$
										.each(
												fullLadderList,
												function(z, fullLadderValue) {
													var tipsHtml = z == 0 ? '<em class="hl_red_bg">加价购</em><em class="hl_red">'
															: '', isFirstSign = z == 0 ? ''
															: '，或';
													if (fullLadderValue.needMoney > 0) {
														fullRefundInfo = (fullRefundInfo
																+ tipsHtml
																+ isFirstSign
																+ '满'
																+ fullLadderValue.needMoney
																+ '另加'
																+ fullLadderValue.addMoney + '元');
													}
												});
								fullRefundInfo += '，即可购买热销商品</em>';
							}
						}
						if (mfmzExtType === mfmzExtTypeMFMZ) {
							$
									.each(
											fullLadderList,
											function(z, fullLadderValue) {
												var tipsHtml = z == 0 ? '<em class="hl_red_bg">满送</em>'
														: '';
												var fullRefundInfo1 = '';
												var fullRefundInfo2 = '';
												fullRefundInfo += tipsHtml;
												if (fullLadderValue.mfmzTag == 1) {
													fullRefundInfo1 += '<em class="hl_red">满'
															+ fullLadderValue.needMoney
															+ '立减'
															+ fullLadderValue.rewardMoney
															+ '</em>';
												}
												if (fullLadderValue.mfmzTag == 2
														&& fullLadderValue.addMoney <= 0) {
													fullRefundInfo2 += '，减后满'
															+ fullLadderValue.needMoney
															+ '元即可购买热销商品</em>';
												}
												if (fullLadderValue.mfmzTag == 2
														&& fullLadderValue.addMoney > 0) {
													fullRefundInfo2 += '，减后满'
															+ fullLadderValue.needMoney
															+ '元另加'
															+ fullLadderValue.addMoney
															+ '元即可购买热销商品</em>';
												}
												fullRefundInfo += fullRefundInfo1
														+ fullRefundInfo2;
											});
						}
						// if ( needMoney > 0 && reward > 0 && mzNeedMoney <=0 )
						// {
						// fullRefundInfo = '<em class="hl_red_bg">满送</em><em
						// class="hl_red">满' + needMoney + '立减' + reward +
						// '</em>';
						// }
						// if (reward <= 0 && mzNeedMoney > 0 && addMoney <= 0 )
						// {
						// fullRefundInfo = '<em class="hl_red_bg">满送</em><em
						// class="hl_red">满' + mzNeedMoney +
						// '元即赠热销商品，赠完即止</em>';
						// }
						// if (reward <= 0 && mzNeedMoney > 0 && addMoney > 0 )
						// {
						// fullRefundInfo = '<em class="hl_red_bg">满送</em><em
						// class="hl_red">满' + mzNeedMoney + '元另加' + addMoney +
						// '元即赠热销商品，赠完即止</em>';
						// }
						// if ( needMoney > 0 && reward > 0 && mzNeedMoney > 0
						// && addMoney <= 0 ) {
						// fullRefundInfo = '<em class="hl_red_bg">满送</em><em
						// class="hl_red">满' + needMoney + '立减' + reward +
						// '，减后满' + mzNeedMoney + '元即可购买热销商品</em>';
						// }
						// if ( needMoney > 0 && reward > 0 && mzNeedMoney > 0
						// && addMoney > 0 ) {
						// fullRefundInfo = '<em class="hl_red_bg">满送</em><em
						// class="hl_red">满' + needMoney + '立减' + reward +
						// '，减后满' + mzNeedMoney + '元另加' + addMoney +
						// '元即可购买热销商品</em>';
						// }
					} else if (fullRefundType == FULLNUM_REBATE_MFMZ) {
						if (needNum > 0 && rebate > 0 && addMoney <= 0
								&& mzNeedNum > 0) {
							fullRefundInfo = '<em class="hl_red_bg">满送</em><em class="hl_red">满'
									+ needNum
									+ '件，总价打'
									+ (rebate * 10).toFixed(1)
									+ '折，且赠热销商品，赠完即止</em>';
						}
						if (needNum > 0 && rebate > 0 && addMoney > 0
								&& mzNeedNum > 0) {
							fullRefundInfo = '<em class="hl_red_bg">满送</em><em class="hl_red">满'
									+ needNum
									+ '件，总价打'
									+ (rebate * 10).toFixed(1)
									+ '折，再加'
									+ addMoney + '元赠热销商品，赠完即止</em>';
						}
						if (mzNeedNum <= 0 && needNum > 0 && rebate > 0) {
							fullRefundInfo = '<em class="hl_red_bg">满送</em><em class="hl_red">满'
									+ needNum
									+ '件，总价打'
									+ (rebate * 10).toFixed(1) + '折</em>';
						}
						if (mzNeedNum > 0 && addMoney <= 0 && rebate <= 0) {
							fullRefundInfo = '<em class="hl_red_bg">满送</em><em class="hl_red">满'
									+ mzNeedNum + '件赠热销商品，赠完即止</em>';
						}
						if (mzNeedNum > 0 && addMoney > 0 && rebate <= 0) {
							fullRefundInfo = '<em class="hl_red_bg">满送</em><em class="hl_red">满'
									+ mzNeedNum
									+ '件，再加'
									+ addMoney
									+ '元赠热销商品，赠完即止</em>';
						}
					}
					var fullRefundTotalInfo = "";
					if (fullRefundInfo != "") {
						if (adwordLink != null && adwordLink.length > 0) {
							fullRefundInfo = fullRefundInfo
									+ '<a href="'
									+ adwordLink
									+ '" target="_blank">&nbsp;&nbsp;详情 <s class="s-arrow">&gt;&gt;</s></a>';
							// "<a target=\"_blank\" style='color:#CE0000'
							// xx='oo' href=\"" + adwordLink + "\">" + + "</a>";
						}
						fullRefundTotalInfo = fullRefundInfo;
					}
					if (fullRefundTotalInfo !== '') {
						promoType10.push(fullRefundTotalInfo);
					}
				}

				// 是否限时打折
				if (infoList[i].limitTimePromo == 1) {
					if ($('#a-tips').length < 1) {
						$('#summary-price strong').after(
								'<em id="a-tips">&nbsp;促销即将结束&nbsp;</em>');
					}
				}
			}
		}
		// 节能补贴
		// if ( !!result.subsidyMoney == true && pageConfig.product.cat[1] !==
		// 794 ) {
		if (false) {
			var isBr = tipsItems.length > 0 ? '<br/>' : '';
			$('#choose-btn-append').addClass('choose-btn-append-lite');
			tipsItems
					.push(isBr
							+ '<em class="hl_red_bg">节能补贴</em>参加节能补贴，下单立减￥'
							+ parseFloat(result.subsidyMoney).toFixed(2)
							+ '&nbsp;&nbsp;<a href="http://help.360buy.com/help/question-91.html" target="_blank">查看更多细则</a><br>');
			if ($('#choose-btn-subsidy').length <= 0) {
				$('#choose-btn-append')
						.before(
								'<div id="choose-btn-subsidy" class="btn"><a class="btn-subsidies" clstag="shangpin|keycount|product|jieneng" href="http://jd2008.360buy.com/purchase/orderinfo_elePow.aspx?pid='
										+ pageConfig.product.skuid
										+ '&pcount='
										+ $('#buy-num').val()
										+ '&ptype=1">参加节能补贴<b></b></a></div>');
				setAmount.targetLink = $('#choose-btn-subsidy .btn-subsidies,#choose-btn-append .btn-append');
			}
		} else {
			$('#choose-btn-subsidy').remove();
		}
		(function() {
			var txtPerfix = '本商品不能使用', txtDq = '', txtJq = '', txtTips = '', infoList = result.infoList;
			if (!infoList || infoList.length < 1) {
				return;
			}
			for (var m = 0; m < infoList.length; m++) {
				if (infoList[m] === 1) {
					txtDq += ' 东券';
				}
				if (infoList[m] === 2) {
					txtJq += ' 京券';
				}
				if (infoList[m] === 3) {
					txtTips += '<a class="hl_red" href="http://help.360buy.com/help/question-97.html" target="_blank" title="售后到家（仅针对京东指定商品）：自商品售出一年内，如出现质量问题，京东将提供免费上门取送及原厂授权维修服务。">赠送一年期京东售后到家服务（上门取送维修）</a><br/>'
				}
			}
			if (txtDq === '' && txtJq === '') {
				tipsItems.push(txtTips);
			} else {
				tipsItems.push('<em class="hl_red">' + txtPerfix + txtDq
						+ txtJq + '</em><br>');
				tipsItems.push(txtTips);
			}
		})();

		if (typeof MBuy !== 'undefined') {
			MBuy.setProm(result.mpt);
		}

		// 奢侈品
		if (pageConfig.product.tips == true) {
			var strPerfix = tipsItems.length > 0 ? '<br/>' : '';
			tipsItems.push(strPerfix + '此商品尊享7天无忧退换货服务');
		}
		// 赠品提示
		if (giftsItems.length > 0) {
			giftsDiv.parent().show();
			giftsDiv.html('<div id="product-gifts">' + giftsItems.join('')
					+ '</div>');
		} else {
			giftsDiv.parent().hide();
		}
		// 促销信息
		if (promotionsItems.length > 0 || promoType10.length > 0) {
			promotionsDiv.parent().show();
			var resPromoType10 = [];
			var tipsForCart = '以下促销，可在购物车任选其一';

			if (pageConfig.product.specialAttrs && pageConfig.product.isLOC) {
				tipsForCart = '以下促销，只可享受其中一种';
			}

			if (promoType10.length > 1) {
				if (promotionsItems.length > 0) {
					resPromoType10 = [ '<br />' + tipsForCart ]
							.concat(promoType10);
				} else {
					resPromoType10 = [ tipsForCart ].concat(promoType10);
				}
			} else {
				if (promotionsItems.length > 0 && promoType10[0]) {
					promoType10[0] = '<br />' + promoType10[0];
				}
				resPromoType10 = promoType10;
			}

			if ($('#product-promotions').length > 0) {
				$('#product-promotions').html(
						promotionsItems.join('<br />')
								+ resPromoType10.join('<br />'));
			} else {
				promotionsDiv.prepend('<div id="product-promotions">'
						+ promotionsItems.join('<br />')
						+ resPromoType10.join('<br />') + '</div>');
			}

		} else {

			$('#product-promotions').remove();
			if (promotionsDiv.html() == '') {
				promotionsDiv.parent().hide();
			}
		}

		// 促销信息 extra
		if (promotionsItemsExtra.length > 0) {
			promotionsExtraDiv.parent().show();
			promotionsExtraDiv.html('<div id="product-prom-ext">'
					+ promotionsItemsExtra.join('&nbsp;&nbsp;&nbsp;&nbsp;')
					+ '</div>');
		} else {
			promotionsExtraDiv.parent().hide();
		}
		// 温馨提示
		if (tipsItems.length > 0) {
			var productTipsEl = $('#product-tips');
			if (productTipsEl.length > 0) {
				productTipsEl.html(tipsItems.join('&nbsp;'));
			} else {
				tips.append('<div id="product-tips">'
						+ tipsItems.join('&nbsp;') + '</div>');
			}
			tips.parent().show();
		} else if (tips.html() == '') {
			tips.parent().hide();
		}
		// 附加[返券]促销信息
		if (!!pageConfig.product) {
			this.getExtraPromotions(pageConfig.product.skuid, G.cat[2]);
		}
	},
	clear : function() {
		$('#product-gifts,#product-promotions,#product-prom-ext,#product-tips')
				.remove();
		$(
				'#summary-promotion,#summary-promotion-extra,#summary-gifts,#summary-tips')
				.hide();
	},
	getExtraPromotions : function(sku, catId) {
		var sku = sku, catId = catId, holder = $('#summary-promotion .dd');

		$
				.ajax({
					url : 'http://bank.market.360buy.com/bank/show_index.action?',
					data : {
						sku : sku,
						csId : catId
					},
					dataType : 'jsonp',
					success : function(r) {
						var text = '&nbsp;<a href="{href}" target="_blank">详情 <s class="s-arrow">&gt;&gt;</s></a>';
						if (!r && !r.title) {
							return;
						}
						text = !!r.actUrl ? text.replace('{href}', r.actUrl)
								: '';

						if ($('#extra-promotions').length > 0) {
							$('#extra-promotions').html(
									'<em class="hl_red_bg">满额返券</em><em class="hl_red">'
											+ unescape(r.title.replace(/\\/g,
													'%')) + '</em>' + text);
						} else {
							holder
									.append('<div id="extra-promotions"><em class="hl_red_bg">满额返券</em><em class="hl_red">'
											+ unescape(r.title.replace(/\\/g,
													'%'))
											+ '</em>'
											+ text
											+ ' </div>');
						}
						$('#summary-promotion').show();
					}
				});
	}
};

if (typeof G !== 'undefined' && !pageConfig.promotionInited) {
	Promotions.init(G.sku);
}