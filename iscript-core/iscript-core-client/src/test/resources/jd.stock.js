var debug = {};
debug.log = function(msg) {
	if (msg) {
		java.lang.System.out.println(msg);
	} else {
		java.lang.System.out.println(\"NULL OR Undefine.\");
	}
}
function getCityId() {
	var sCid = '78'
	if (args.regionname == 'BJ') {
		sCid = '72';
	} else if (args.regionname == 'GZ') {
		sCid = '1601';
	} else if (args.regionname == 'WH') {
		sCid = '1381';
	} else if (args.regionname == 'CD') {
		sCid = '1930';
	}
	return sCid;
}
function getRegionCodeObj() {
	var oRegion = {};
	var sLocation = '&provinceid=2&cityid=78&areaid=79'
	if (args.regionname == 'BJ') {
		oRegion.provinceid = 1;
		oRegion.cityid = 72;
		oRegion.areaid = 4137;
		oRegion.townid = 0;
		sLocation = '&provinceid=1&cityid=72&areaid=4137&townid=0';
	} else if (args.regionname == 'GZ') {
		oRegion.provinceid = 19;
		oRegion.cityid = 1601;
		oRegion.areaid = 3633;
		oRegion.townid = 35726;
		sLocation = '&provinceid=19&cityid=1601&areaid=3633&townid=35726';
	} else if (args.regionname == 'WH') {
		oRegion.provinceid = 17;
		oRegion.cityid = 1381;
		oRegion.areaid = 3582;
		oRegion.townid = 50739;
		sLocation = '&provinceid=17&cityid=1381&areaid=3582&townid=50739';
	} else if (args.regionname == 'CD') {
		oRegion.provinceid = 22;
		oRegion.cityid = 1930;
		oRegion.areaid = 4284;
		oRegion.townid = 0;
		sLocation = '&provinceid=22&cityid=1930&areaid=4284&townid=0';
	} else {
		oRegion.provinceid = 2;
		oRegion.cityid = 2811;
		oRegion.areaid = 2860;
		oRegion.townid = 0;
		sLocation = '&provinceid=2&cityid=2811&areaid=2860&townid=0'
	}
	return oRegion;
}
function getCode() {
	var _url = args.url;
	var codeReg = new RegExp(\"[0-9]{5,}\", \"gm\");
	var match = _url.match(codeReg);
	if (match && match[0]) {
		return match[0];
	}
	return \"\";
}
function getJSONObj(sHtml) {
	var oJObjReg = new RegExp('{.*}', 'gm'), oMatch = sHtml.match(oJObjReg);
	var sJObj = (oMatch) ? oMatch[0] : \"{}\";
	return eval('(' + sJObj + ')');
}
function getSkuId_new(cid, aid, eleSkuIdKey) {
	if (eleSkuIdKey && eleSkuIdKey.length > 0) {
		var areas = null;
		for (var i = 0, j = eleSkuIdKey.length; i < j; i++) {
			if (eleSkuIdKey[i].area && eleSkuIdKey[i].area[cid + \"\"]) {
				areas = eleSkuIdKey[i].area[cid + \"\"];
				if (areas.length == 0 || areas[0] + \"\" == \"0\") {
					return eleSkuIdKey[i].SkuId;
				} else if (areas.length > 0) {
					for (var a = 0, b = areas.length; a < b; a++) {
						if (areas[a] + \"\" == aid + \"\") {
							return eleSkuIdKey[i].SkuId;
						}
					}
				}
			}
		}
	}
	return 0;
}
function chooseSkuToArea(provinceId, cityId, areaId, oConfig) {
	var pageConfig = oConfig.pageConfig;
	var eleSkuIdKey = oConfig.eleSkuIdKey;
	var currentSkuId = (pageConfig && pageConfig.product) ? pageConfig.product.skuid
			: '';
	var currentSkuKey = (pageConfig && pageConfig.product) ? pageConfig.product.skuidkey
			: '';
	var isAreaProduct = (eleSkuIdKey) ? true : false;
	if (isAreaProduct && provinceId > 0 && cityId > 0 && areaId > 0) {
		currentSkuId = 0;
		currentSkuKey = \"\";
		var eleRegion = '';
		if (eleRegion) {
			var provinceCitys = eleRegion[provinceId + \"\"];
			if (provinceCitys && provinceCitys.citys
					&& provinceCitys.citys.length > 0) {
				for (var i = 0, j = provinceCitys.citys.length; i < j; i++) {
					if (provinceCitys.citys[i].IdCity == cityId) {
						currentSkuId = provinceCitys.citys[i].SkuId;
						break;
					}
				}
			}
		} else {
			currentSkuId = getSkuId_new(cityId, areaId, eleSkuIdKey);
		}
		if (eleSkuIdKey && eleSkuIdKey.length > 0) {
			for (var i = 0, j = eleSkuIdKey.length; i < j; i++) {
				if (eleSkuIdKey[i].SkuId == currentSkuId) {
					currentSkuKey = eleSkuIdKey[i].Key;
					break;
				}
			}
		}
	}
	debug.log('currentSkuKey:' + currentSkuKey);
	return [ currentSkuKey, currentSkuId ];
}
function getRegionConfig(body, oConfig) {
	var oEleRegion_as = body.select('div#product-intro > script[type]');
	if (oEleRegion_as && oEleRegion_as.first()) {
		var sEleRegion = oEleRegion_as.first().html();
		if (!sEleRegion || '' == sEleRegion) {
			return oConfig;
		}
		eval(' ' + sEleRegion + ' ');
		if (typeof (warestatus) != 'undefined') {
			oConfig.warestatus = warestatus;
		}
		if (typeof (eleSkuIdKey) != 'undefined') {
			oConfig.eleSkuIdKey = eleSkuIdKey;
		}
	}
	return oConfig;
}
function getPageConfig(body, oConfig) {
	var head = body.previousElementSibling(), oScript = head
			.select('script');
	for (var i = 0; i < oScript.size(); i++) {
		var sHtml = oScript.get(i).html();
		if (sHtml.indexOf('pageConfig')>0) {
			sHtml = sHtml.replace('\\'\\',', '\"\",');
			sHtml = sHtml.replace('\\'\\'', '\"\"');
			sHtml = 'var window = {};' + sHtml;
			eval(' ' + sHtml + ' ');
			if (typeof (pageConfig) != 'undefined') {
				oConfig.pageConfig = pageConfig;
			}
			break;
		}
	}
	return oConfig;
}
function getWareinfo(body, oConfig) {
	var oWareinfo = body.select('#book-price li.sub > script[type]');
	if (oWareinfo.isEmpty()) {
		return oConfig;
	}
	var sWareinfo = oWareinfo.first().html();
	if (!sWareinfo || '' == sWareinfo) {
		return oConfig;
	}
	eval(' ' + sWareinfo + ' ');
	if (typeof (wareinfo) != 'undefined') {
		oConfig.wareinfo = wareinfo;
	}
	return oConfig;
}
function getSkuKey(body) {
	var cityId = getCityId(), skuId = '', sSkuKey = '';
	var oSkuidkey_as = body.select('#skuidkey');
	if (oSkuidkey_as && oSkuidkey_as.first()) {
		sSkuKey = oSkuidkey_as.first().ownText();
		return sSkuKey;
	}
	var oEleRegion_as = body.select('div#product-intro > script[type]');
	if (oEleRegion_as && oEleRegion_as.first()) {
		var sEleRegion = oEleRegion_as.first().html();
		eval(' ' + sEleRegion + ' ');
		var oRegionArr = sEleRegion.split('=');
		debug.log('sEleRegion:' + sEleRegion);
		if (oRegionArr.length > 3) {
			var sCitySkuId = oRegionArr[2], sSkuIdKey = oRegionArr[3], sCitySkuId = sCitySkuId
					.substring(0, sCitySkuId.lastIndexOf(';')), sSkuIdKey = sSkuIdKey
					.substring(0, sSkuIdKey.lastIndexOf(';'));
			var oCitySkuIdJSON = eval('(' + sCitySkuId + ')');
			var oSkuIdKeyJSON = eval('(' + sSkuIdKey + ')');
			for ( var n in oCitySkuIdJSON) {
				var citysArr = oCitySkuIdJSON[n].citys;
				for (var i = 0; i < citysArr.length; i++) {
					if (citysArr[i].IdCity == cityId) {
						skuId = citysArr[i].SkuId;
						break;
					}
				}
			}
			if (skuId) {
				for ( var index in oSkuIdKeyJSON) {
					var oSK = oSkuIdKeyJSON[index];
					if (oSK.SkuId == skuId) {
						sSkuKey = oSK.Key;
						break;
					}
				}
			}
			debug.log('090--skuId:' + skuId);
			debug.log('sSkuKey:' + sSkuKey);
		}
	}
	if (sSkuKey) {
		return sSkuKey;
	}
	var head = body.previousElementSibling(), oScript = head
			.select('script[type]');
	for (var i = 0; i < oScript.size(); i++) {
		var sHtml = oScript.get(i).html();
		if (sHtml.contains('pageConfig')) {
			var index = sHtml.indexOf('=');
			sHtml = sHtml.substring(index + 1);
			index = sHtml.indexOf(';');
			if (index > 0) {
				sHtml = sHtml.substring(0, index);
			}
			var oConfigJSON = eval('(' + sHtml + ')');
			debug.log('stock-sSkuKey:' + sHtml);
			if (oConfigJSON.product) {
				sSkuKey = oConfigJSON.product.skuidkey;
			}
			break;
		}
	}
	if (sSkuKey) {
		return sSkuKey;
	}
	var oWareinfo = body.select('#book-price li.sub > script[type]'); sWareinfo = oWareinfo
			.first().html(), oWareJSON = getJSONObj(sWareinfo);
	return oWareJSON.sid;
}
function getStockFlagByName(stockStateName) {
	debug.log('stockStateName2:'+stockStateName);
	var sStock = '0';
	if (-1 != stockStateName.indexOf(\"有货\")
			|| -1 != stockStateName.indexOf(\"现货\")
			|| -1 != stockStateName.indexOf(\"在途\")) {
		sStock = \"0\";
	} else if (-1 != stockStateName.indexOf(\"无货\")
			|| -1 != stockStateName.indexOf(\"售完\")) {
		sStock = \"-2\";
	} else if (-1 != stockStateName.indexOf(\"预订\")) {
		sStock = \"0\";
	}
	debug.log('stockStateName2:'+sStock);
	return sStock;
}
function getStockFlagByState(state) {
	var sStock = '0';
	if (state == 33) {
		sStock = '0';
	} else if (state == 34 || state == 0) {
		sStock = '-2';
	} else if (state == 39) {
		sStock = '0';
	} else if (state == 40) {
		sStock = '0';
	} else if (state == 36) {
		sStock = '0';
	}
	return sStock;
}
function SetNotifyByNoneStock(stockstatus, body) {
	var mvdMark = 'http://mvd.';
	if (args.url.indexOf(mvdMark) > -1) {
		return false;
	}
	var warestatus = '';
	var oEleRegion_as = body.select('div#product-intro > script[type]');
	if (oEleRegion_as && oEleRegion_as.first()) {
		var sEleRegion = oEleRegion_as.first().html();
		debug.log('sEleRegion:' + sEleRegion);
		var wsReg = new RegExp('(warestatus).*?[0-9]+', 'gm'), owsMatch = sEleRegion
				.match(wsReg)
		if (owsMatch && owsMatch[0]) {
			warestatus = owsMatch[0];
			warestatus = warestatus.replace(/[^0-9]+/gm, '');
		}
	}
	debug.log('warestatus:' + warestatus);
	debug.log('stockstatus:' + stockstatus);
	if (stockstatus && stockstatus != 34 && stockstatus != 0 && warestatus == 1) {
		return false;
	}
	return true;
}
function getProvinceStockCallback(result, body) {
	var sStock = '0';
	if (result.stock) {
		var stockstate = (result.stock.StockState) ? result.stock.StockState
				: result.stock.S;
		var stockStateName = result.stock.StockStateName;
		debug.log('stockStateName:' + stockStateName);
		sStock = (stockstate) ? getStockFlagByState(stockstate)
				: getStockFlagByName(stockStateName);
		var isNStock = SetNotifyByNoneStock(stockstate, body);
		debug.log(\"isNStock:\" + isNStock);
		//sStock = (isNStock) ? '-2' : sStock;
	}
	return sStock;
}
function getImgPrice2Num(pid) {
	var sPriceUrl = 'http://p.3.cn/prices/get?skuid=J_' + pid + '&type=1';
	debug.log('sPriceUrl:' + sPriceUrl);
	var sHtml = http.get(sPriceUrl, args);
	debug.log('sHtml:' + sHtml);
	var oPriceArr = eval('(' + sHtml + ')');
	var r = oPriceArr;
	var price = '-1';
	if (!!r && r.length > 0 && r[0].p && r[0].p > 0) {
		price = r[0].p;
	}
	return price;
}
function isEmptyVar(param) {
	return 'undefined'.equals(param) || '' == param;
}
function getStock(src) {
	var body = src;

	// var skuId = getSkuKey(body);
	var aimURL = \"\";
	var oRegion = getRegionCodeObj();
	var oConfig = {};
	oConfig = getRegionConfig(body, oConfig);
	oConfig = getPageConfig(body, oConfig);debug.log('@oConfig:'+JSON.stringify(oConfig));
	var skuId = '';
	var skuKey = '';

	if (oConfig.pageConfig) {
		var oSkuArr = chooseSkuToArea(oRegion.provinceid, oRegion.cityid,
				oRegion.areaid, oConfig);
		skuKey = oSkuArr[0];
		skuId = oSkuArr[1];
		debug.log(skuKey + \":\" + skuId);
	} else {
		getWareinfo(body, oConfig);
debug.log('oConfig:'+JSON.stringify(oConfig));		skuKey = oConfig.wareinfo.sid;
		skuId = oConfig.wareinfo.pid;
	}
	debug.log(\"skuKey:\" + skuKey + \",provinceid:\" + oRegion.provinceid
			+ \",warestatus:\" + oConfig.warestatus);
	if (!isEmptyVar(skuKey) && oRegion.provinceid != 84
			&& oConfig.warestatus == 1) {
	} else if (!isEmptyVar(oConfig.warestatus)) {
		return '-1';
	}
	var stockServiceDomain = 'http://st.3.cn';
	var pageConfig = oConfig.pageConfig;
	var sLocation = '&provinceid=' + oRegion.provinceid + '&cityid='
			+ oRegion.cityid + '&areaid=' + oRegion.areaid + \"&townid=\"
			+ oRegion.townid;
	var aimURL = \"\";
	if (pageConfig && pageConfig.product && pageConfig.product.cat.length >= 3) {
		aimURL = stockServiceDomain + \"/gds.html?skuid=\" + skuKey + sLocation
				+ \"&sortid1=\" + pageConfig.product.cat[0] + \"&sortid2=\"
				+ pageConfig.product.cat[1] + \"&sortid3=\"
				+ pageConfig.product.cat[2] + \"&cd=1_1_1\"
	} else {
		aimURL = \"http://st.3.cn/gds.html?skuid=\" + skuKey + sLocation + \"&t=\"
				+ (new Date()).getTime();
	}
	debug.log('aimURL:' + aimURL);
	var responseStock;
	try {
		responseStock = http.get(aimURL);
	} catch (er) {
		throw \"getstock:\" + aimURL + er;
	}

	debug.log(\"Multi:\" + responseStock);
	var provinceStockJson = eval(\"(\" + responseStock + \")\");
	var sStock = getProvinceStockCallback(provinceStockJson, body);
	var sPrice = getImgPrice2Num(skuId);
	debug.log('Vef:sStock:' + sStock + \",sPrice:\" + sPrice);
	sStock = (sPrice < 0) ? -1 : sStock;
	return sStock;
}

try {
	var body = src;
	var stockStatus = getStock(body);
} catch (ex) {
	throw 'From stock_status,Exception:' + ex;
}