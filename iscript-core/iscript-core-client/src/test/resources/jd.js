var Jsoup = org.jsoup.Jsoup;
var debug = {};
debug.log = function(msg) {
	if (msg) {
		java.lang.System.out.println(msg);
	} else {
		java.lang.System.out.println("NULL OR Undefine.");
	}
}
function getPageConfig(body) {
	var head = body.previousElementSibling(), oScript = head
			.select('script[type]');
	var pageConfig;
	for (var i = 0; i < oScript.size(); i++) {
		var sHtml = oScript.get(i).html();
		if (sHtml.contains('pageConfig')) {
			sHtml = 'var window = {};\n' + sHtml;
			eval(' ' + sHtml + ' ');
			if (typeof (window.pageConfig) != 'undefined') {
				pageConfig = window.pageConfig;
			}
			break;
		}
	}
	return pageConfig;
};
function customEach(objArray, funName) {
	for (var i = 0; i < objArray.length; i++) {
		funName(i, objArray[i]);
	}
};
var oPageConfig = {}, G = {};
var Promotions = {
	init : function(sku) {
		this.sku = sku || G.sku;
		this.get();
	},
	get : function() {
		function setPromotions(data) {
			return data;
		}
		var _this = this;
		var url = "http://jprice.360buy.com/pageadword/" + _this.sku
				+ "-1-1.html?callback=setPromotions"
		debug.log('url:' + url);
		var html = http.get(url);
		var oPromotJson = eval(' ' + html + ' ');
		this.set(oPromotJson);
	},

	set : function(result) {
		var promotionsItems = [], promotionsItemsExtra = [], giftsItems = [], tipsItems = [], titleItems = [];
		var infoList = result.promotionInfoList;
		var skuId = result.skuId;

		var str_len = (oPageConfig.wideVersion && oPageConfig.compatible) ? 35
				: 25;

		var gift_TPL = '';

		if (infoList !== null && infoList.length > 0) {

			for (var i = 0; i < infoList.length; i++) {

				var levelText = '';
				var coupon = infoList[i].adwordCouponList;

				// 单品促销
				if (infoList[i].promoType == 1) {

					if (infoList[i].minNum > 1 && infoList[i].price > 0) {
						promotionsItems.push('<em class="hl_red_bg" promoId="'
								+ infoList[i].promoId + '">多买优惠</em>'
								+ '<em class="hl_red">购买' + infoList[i].minNum
								+ '件：￥' + infoList[i].price + '</em>');
					} else if (infoList[i].minNum > 0
							&& infoList[i].minNum == infoList[i].maxNum
							&& infoList[i].price > 0) {
						promotionsItems.push('<em class="hl_red_bg" promoId="'
								+ infoList[i].promoId + '">多买优惠</em>'
								+ '<em class="hl_red">购买' + infoList[i].minNum
								+ '件及以上：￥' + infoList[i].price + '</em>');
					} else {
						if (infoList[i].userLevel <= 50
								&& infoList[i].discount > 0
								&& (skuId.length != 10 || (skuId.length == 10 && (infoList[i].extType == 4 || infoList[i].extType == 8)))) {
							var limitText = (infoList[i].limitUserType == 4 || infoList[i].maxNum >= 1) ? ('，购买超过'
									+ infoList[i].maxNum + '件时不享受该优惠')
									: '';
							var limitHTML = (infoList[i].limitUserType == 4 || infoList[i].maxNum >= 1) ? '限购'
									: '直降';
							var descText = infoList[i].extType == 8
									|| infoList[i].extType == 4 ? '为您节省'
									: '已优惠';

							promotionsItems
									.push('<em class="hl_red_bg" promoId="'
											+ infoList[i].promoId + '">'
											+ limitHTML
											+ '</em><em class="hl_red">'
											+ descText + '￥'
											+ infoList[i].discount + ' '
											+ limitText + '</em>');
						} else if (infoList[i].userLevel > 50
								&& infoList[i].price > 0
								&& infoList[i].price > 0) {
							promotionsItems
									.push('<em class="hl_red_bg" promoId="'
											+ infoList[i].promoId
											+ '">会员特享</em><em class="hl_red">'
											+ levelText + '及以上会员价：￥'
											+ infoList[i].price + '</em>');
						}
					}

					var tuanTag = '', orginServiceText = [];

					if (infoList[i].extType == 8) {
						tuanTag = '闪团';
					} else if (infoList[i].extType == 4) {
						tuanTag = '团购';
					}
					if (infoList[i].extType == 8 || infoList[i].extType == 4) {
						promotionsItems[0] = promotionsItems[0].replace(
								/hl_red_bg">[\u4e00-\u9fa5]+</, 'hl_red_bg">'
										+ tuanTag + '<');
					}

					var couponList = infoList[i].adwordCouponList;
					var score = infoList[i].score;
					var jq = 0;

					if (score > 0) {
						promotionsItems.push('<em class="hl_red_bg" promoId="'
								+ infoList[i].promoId
								+ '">赠积分</em><em class="hl_red">赠' + score
								+ '积分</em>');
					}
					if (coupon != null && coupon.length > 0) {
						customEach(
								coupon,
								function(name, value) {
									if (value.type == 1) {
										if (value.key != null
												&& value.key != "") {
											if (value.adWord == null) {
												value.adWord = "";
											}
											if (value.adWord != null
													&& value.adWord.length > 0) {
												promotionsItems
														.push('<em class="hl_red_bg" promoId="'
																+ infoList[i].promoId
																+ '">赠券</em><em class="hl_red">赠'
																+ value.couponQouta
																+ '元限品类京券（'
																+ value.adWord
																+ ')</em>');
											} else {
												promotionsItems
														.push('<em class="hl_red_bg" promoId="'
																+ infoList[i].promoId
																+ '">赠券</em><em class="hl_red">赠'
																+ value.couponQouta
																+ '元限品类京券</em>');
											}

										} else {
											promotionsItems
													.push('<em class="hl_red_bg" promoId="'
															+ infoList[i].promoId
															+ '">赠券</em><em class="hl_red">赠'
															+ value.couponQouta
															+ '元京券</em>');
										}
									}
								});
					}

				}
				// 买多赠多
				if (infoList[i].promoType == 2 && infoList[i].minNum > 1) {
					promotionsItems.push('<em class="hl_red_bg" promoId="'
							+ infoList[i].promoId + '">满赠</em>购买'
							+ infoList[i].minNum + '件即得下方赠品');

					var giftList = infoList[i].adwordGiftSkuList;

					if (giftList.length > 0 & giftList !== null) {
						var res = '';

						if (res !== '') {
							giftsItems.push(res);
						}

					}
				}

				// 封顶促销
				if (infoList[i].promoType == 15) {
					var adword = infoList[i].adword;
					var bookTopAdword = "参与图书“封顶折扣”活动";
					var unitInfor = '';

					if (adword != null && adword != "") {
						unitInfor += "<div><font color=\"#ef0000\">" + adword
								+ "</font></div>";
					} else {
						unitInfor += "<div><font color=\"#ef0000\">"
								+ bookTopAdword + "</font></div>";
					}

					promotionsItemsExtra.push(unitInfor);
				}

				// 赠品促销
				if (infoList[i].promoType == 4) {

					var giftList = infoList[i].adwordGiftSkuList;

					if (giftList.length > 0 & giftList !== null) {
						var res = '';

						if (res !== '') {
							giftsItems.push(res);
						}

					}

					var couponList = infoList[i].adwordCouponList;
					var score = infoList[i].score;
					var jq = 0;

					if (couponList != null && couponList.length > 0) {
						customEach(couponList, function(y, coupon) {
							if (coupon.type == 1) {
								jq = jq + coupon.couponQouta;
							}
						});
					}

					if (score > 0) {
						promotionsItems.push('<em class="hl_red_bg" promoId="'
								+ infoList[i].promoId
								+ '">赠积分</em><em class="hl_red">赠' + score
								+ '积分</em><br />');
					}
					if (jq > 0) {
						if (score > 0)
							promotionsItems
									.push('<em class="hl_red_bg" promoId="'
											+ infoList[i].promoId
											+ '">赠券</em><em class="hl_red">赠'
											+ jq + '元京券</em>');
						else
							promotionsItems
									.push('<em class="hl_red_bg" promoId="'
											+ infoList[i].promoId
											+ '">赠券</em><em class="hl_red">赠'
											+ jq + '元京券</em>');
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

					// 满返满赠促销子类型
					var fullRefundType = infoList[i].fullRefundType;
					var reward = infoList[i].reward;
					var needMoney = infoList[i].needMondey;
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

					// 拼接满返满赠信息
					var fullRefundInfo = "";
					if (couponList != null && couponList.length > 0) {
						customEach(couponList, function(z, couponValue) {
							if (couponValue.type == 1) {
								jq = jq + coupon.couponQouta;
							}
						});
					}

					if (fullRefundType == FULL_REFUND) {
						if (needMoney > 0 && reward > 0) {
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">满减</em><em class="hl_red">满'
									+ needMoney + '立减' + reward + '</em>';
						}

						if (haveGifts) {
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">满赠</em><em class="hl_red">满'
									+ needMoney + '元即赠热销商品，赠完即止</em>';
						}
						if (jq > 0 && needMoney > 0) {
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">满赠</em><em class="hl_red">满'
									+ needMoney + '元，赠' + jq + '元京券</em>';
						}
					} else if (fullRefundType == FULL_REFUND_PER) {
						if (needMoney > 0 && reward > 0) {
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">满减</em><em class="hl_red">每满'
									+ needMoney + '元，可减' + reward + '元现金</em>';
							if (topMoney > 0) {
								fullRefundInfo += '<em class="hl_red">，最多可减'
										+ topMoney + '元</em>';
							}
						} else {
							if (haveGifts) {
								fullRefundInfo = '<em class="hl_red_bg" promoId="'
										+ infoList[i].promoId
										+ '">满赠</em><em class="hl_red">每满'
										+ needMoney + '元即赠，赠完即止</em>';
							} else if (jq > 0) {
								fullRefundInfo = '<em class="hl_red_bg" promoId="'
										+ infoList[i].promoId
										+ '">满赠</em><em class="hl_red">每满'
										+ needMoney + '元，即赠' + jq + '元京券</em>';
							}
						}
					} else if (fullRefundType == EXTRA_PRICE) {
						if (needMoney > 0 && addMoney > 0) {
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">加价购</em><em class="hl_red">满'
									+ needMoney + '元另加' + addMoney
									+ '元即可购买热销商品</em>';
						}
					} else if (fullRefundType == PERCENT) {
						if (needMoney > 0 && percent > 0) {
							percent = percent * 100;
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">满减</em><em class="hl_red">满'
									+ needMoney + '元，可减' + percent + '%</em>';
						}
					} else if (fullRefundType == FULL_LADDER) {
						if (fullLadderList != null && fullLadderList.length > 0) {
							// fullRefundInfo = '<em
							// class="hl_red_bg">满减</em>该商品参加阶梯满减活动，购买活动商品<br/>';
							customEach(
									fullLadderList,
									function(z, fullLadderValue) {

										var tipsHtml = z == 0 ? '<em class="hl_red_bg" promoId="'
												+ infoList[i].promoId
												+ '">满减</em>'
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
						var tipsHTML = '<em class="hl_red"><em class="hl_red_bg" promoId="'
								+ infoList[i].promoId
								+ '">多买优惠</em>满'
								+ infoList[i].needNum
								+ '件，立减最低'
								+ infoList[i].deliverNum
								+ '件商品价格</em>'
								+ moreLink;
						promotionsItems.push(tipsHTML);
					} else if (fullRefundType == 20) {
						// 跨品类满减促销
						var list = infoList[i].fullLadderDiscountList, len = list.length, f, resText = [], resLink = infoList[i].adwordUrl;

						for (f = 0; f < len && len > 0; f++) {
							resText.push('满' + parseInt(list[f].needMoney)
									+ '减' + parseInt(list[f].rewardMoney));
						}
						if (len > 0) {
							promotionsItems
									.push('<em class="hl_red_bg" promoId="'
											+ infoList[i].promoId
											+ '">满减</em><em class="hl_red">购买至少'
											+ list[0].minPoolNum
											+ '类商品，'
											+ resText.join('、')
											+ ' <a href="'
											+ resLink
											+ '" target="_blank">详情&raquo;</a></em>');
						}
					} else if (fullRefundType == FULLREFUND_MPRICE_NNUM) {
						if (needMoney > 0 && deliverNum > 0) {
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">满减</em><em class="hl_red">满'
									+ needMoney + '元买' + deliverNum + '件</em>';
						}
					} else if (fullRefundType == FULLREFUND_MNUN_ZENG) {
						if (haveGifts && needNum > 0) {
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">满赠</em><em class="hl_red">满' + needNum
									+ '件即赠热销商品，赠完即止</em>';
						}
					} else if (fullRefundType == FULLNUM_MNUM_NREBATE) {
						if (needNum > 0 && rebate > 0) {
							fullRefundInfo = '<em class="hl_red_bg" promoId="'
									+ infoList[i].promoId
									+ '">多买优惠</em><em class="hl_red">满'
									+ needNum + '件，总价打' + rebate * 10
									+ '折</em>';
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
					promotionsItems.push(fullRefundTotalInfo);
				}

			}

			// 节能补贴
			if (!!result.subsidyMoney == true
					&& oPageConfig.product.cat[1] !== 794) {
				var isBr = tipsItems.length > 0 ? '<br/>' : '';

				tipsItems
						.push(isBr
								+ '<em class="hl_red_bg" promoId="'
								+ infoList[i].promoId
								+ '">节能补贴</em>参加节能补贴，下单立减￥'
								+ parseFloat(result.subsidyMoney).toFixed(2)
								+ '&nbsp;&nbsp;<a href="http://help.360buy.com/help/question-91.html" target="_blank">查看更多细则</a><br>');

			} else {
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

			// 奢侈品
			if (oPageConfig.product.tips == true) {
				var strPerfix = tipsItems.length > 0 ? '<br/>' : '';
				tipsItems.push(strPerfix + '此商品尊享7天无忧退换货服务');
			}

			// 附加[返券]促销信息
			if (!!oPageConfig.product) {
				var sExtra = this.getExtraPromotions(G.sku, G.cat[2], infoList,
						infoList.length - 1);
				(sExtra && '' != sExtra) ? promotionsItemsExtra.push(sExtra)
						: '';
			}
		}
		var sTitlePromot = this.getTitlePromotions(G.sku);
		(sTitlePromot && '' != sTitlePromot) ? titleItems.push(sTitlePromot)
				: '';
		Promotions.promotionsItems = promotionsItems;
		Promotions.promotionsItemsExtra = promotionsItemsExtra;
		Promotions.giftsItems = giftsItems;
		Promotions.tipsItems = tipsItems;
		Promotions.titleItems = titleItems;
	},
	getExtraPromotions : function(sku, catId, infoList, i) {
		function getExtraPromot(data) {
			return data;
		}
		var sku = sku, catId = catId, _url = 'http://bank.market.360buy.com/bank/show_index.action?sku='
				+ sku + "&csId=" + catId + "&callback=getExtraPromot";
		var sHtml = http.get(_url);
		if (!sHtml || ''.equals(sHtml.trim())) {
			return '';
		}
		var r = eval('(' + sHtml + ')');
		var text = '<a target="_blank" href="{href}">详情 <s class="s-arrow">&gt;&gt;</s></a>';
		if (!r && !r.title) {
			return '';
		}
		text = !!r.actUrl ? text.replace('{href}', r.actUrl) : '';
		var exProm = '<div id="extra-promotions"><em class="hl_red_bg"'
				+ (infoList[i].adwordUrl ? ' adwordUrl="'
						+ infoList[i].adwordUrl + '"' : "")
				+ (infoList[i].promoId ? ' promoId="' + infoList[i].promoId
						+ '"' : "") + '>满额返券</em><em class="hl_red">'
				+ unescape(r.title.replace(/\\/g, '%')) + '</em>' + text
				+ '</div>';
		return exProm;
	},
	getTitlePromotions : function(sku) {
		function setproductadwords(r) {
			if (r && r.AdWordList && r.AdWordList.length > 0 && r.AdWordList[0]) {
				var sPromot = (r.AdWordList[0].waretitle ? r.AdWordList[0].waretitle
						: "");
				return sPromot;
			}
			return '';
		}
		var sUrl = 'http://jprice.jd.com/adslogan/' + sku
				+ '-setproductadwords.ad';
		var sHtml = http.get(sUrl);
		var sPromot = '';
		if (sHtml && '' != sHtml) {
			sPromot = eval(' ' + sHtml + ' ');
		}
		return sPromot;
	}
};
function parseEm(oItmList) {
	var size = oItmList.size();
	var oExtraEms = new org.jsoup.select.Elements();
	for (var i = 0; i < size; i++) {
		var curEle = oItmList.get(i);
		if ('div' != curEle.tagName()) {
			oExtraEms.add(curEle);
		} else {
			oExtraEms.addAll(curEle.children());
		}
	}
	return oExtraEms;
};

function getPromotionInfo() {
	var body = src;
	oPageConfig = getPageConfig(body);
	G.sku = oPageConfig.product.skuid;
	G.cat = oPageConfig.product.cat;
	Promotions.init();
	(Promotions.promotionsItemsExtra.length > 0) ? Promotions.promotionsItems
			.push(Promotions.promotionsItemsExtra) : "";
	var sPromotHtml = Promotions.promotionsItems.join('<br/>');
	var oPromotBody = Jsoup.parse(sPromotHtml);
	var oItmList = oPromotBody.select('body > *');
	var oPmtItems = [];
	if (oItmList.isEmpty()) {
		return '';
	}
	oItmList = parseEm(oItmList);
	debug.log("oItmList:\n" + oItmList);
	var size = oItmList.size();
	var pmtItem = {};
	var pmtItem = {};
	pmtItem.pmt_code = '';
	pmtItem.pmt_title = '';
	pmtItem.pmt_cont = '';
	pmtItem.pmt_url = '';
	for (var i = 0; i < size; i++) {
		var curEle = oItmList.get(i);
		if ('br' == curEle.tagName()) {
			oPmtItems.push(pmtItem);
			pmtItem = {};
			pmtItem.pmt_code = '';
			pmtItem.pmt_title = '';
			pmtItem.pmt_cont = '';
			pmtItem.pmt_url = '';
		} else if ('em' == curEle.tagName()) {
			if (curEle.hasClass('hl_red_bg')) {
				pmtItem.pmt_title = curEle.ownText();
			} else if (curEle.hasClass('hl_red')) {
				pmtItem.pmt_cont += curEle.text();
			}
		} else if ('a' == curEle.tagName()) {
			pmtItem.pmt_url = curEle.absUrl('href');
		}
	}
	oPmtItems.push(pmtItem);
	var oPmtArr = [];
	for ( var index in oPmtItems) {
		var oItem = oPmtItems[index];
		var sPromot = oItem.pmt_title + oItem.pmt_cont;
		oPmtArr.push(sPromot);
	}
	(Promotions.titleItems.length > 0) ? oPmtArr.push(Promotions.titleItems)
			: "";
	for (var i = 0; i < oPmtArr.length; i++) {
		var curPmt = oPmtArr[i];
		var pmtDom = Jsoup.parse(curPmt);
		var oUrlAs = pmtDom.select('a[href]');
		curPmt = pmtDom.text();
		for (var j = 0; j < oUrlAs.size(); j++) {
			curPmt += ',' + oUrlAs.get(j).attr('href');
		}
		oPmtArr[i] = curPmt;
	}
	var sPmt = oPmtArr.join('|');
	debug.log('prompt_info:' + sPmt);
	return sPmt;
}
var promotionInfo = getPromotionInfo();
debug.log('prompt_info:' + promotionInfo);
