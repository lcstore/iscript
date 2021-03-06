package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.ScriptableObject;

import com.lezo.iscript.crawler.dom.ScriptDocument;
import com.lezo.iscript.crawler.dom.ScriptHtmlParser;
import com.lezo.iscript.crawler.dom.browser.ScriptWindow;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigYhdProduct implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private static Map<String, String> hostIpMap = new HashMap<String, String>();
    private static final Integer SITE_ID = 1002;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String doParse(TaskWritable task) throws Exception {
        addCookie();
        DataBean dataBean = getDataObject(task);
        return convert2TaskCallBack(dataBean, task);
    }

    private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
        dataBean.getTargetList().add("ProductDto");
        dataBean.getTargetList().add("ProductStatDto");

        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, dataBean);
        String dataString = writer.toString();

        JSONObject returnObject = new JSONObject();
        JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
        JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
        Object fromUrlObject = task.get("fromUrl");
        if (fromUrlObject != null && fromUrlObject.toString().contains("list.yhd.com")) {
            ProductBean pBean = (ProductBean) dataBean.getDataList().get(0);
            String sCodes = pBean.getSpuCodes();
            if (StringUtils.isNotEmpty(sCodes)) {
                String[] sArray = sCodes.split(",");
                for (String sCode : sArray) {
                    String sUrl = "http://item.yhd.com/item/" + sCode;
                    dataBean.getNextList().add(sUrl);
                }
            }
            if (CollectionUtils.isNotEmpty(dataBean.getNextList())) {
                dataBean.setDataList(null);
                mapper = new ObjectMapper();
                writer = new StringWriter();
                mapper.writeValue(writer, dataBean);
                String newData = writer.toString();
                JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, newData);
            }
        }
        JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, dataString);
        return returnObject.toString();
    }

    private DataBean getDataObject(TaskWritable task) throws Exception {
        String url = (String) task.get("url");
        String refer = url;
        int index = url.indexOf("?");
        url = index > 0 ? url.substring(0, index) : url;
        HttpGet get = createHttpGetWithIp(url);
        get.addHeader("Refer", refer);
        get.addHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:41.0) Gecko/20100101 Firefox/41.0");
        String html = HttpClientUtils.getContent(client, get, "UTF-8");
        Document dom = Jsoup.parse(html, url);
        DataBean dataBean = new DataBean();
        ProductBean productBean = new ProductBean();
        dataBean.getDataList().add(productBean);
        String barCode = (String) task.get("barCode");
        if (BarCodeUtils.isBarCode(barCode)) {
            productBean.setBarCode(barCode);
        }
        productBean.setProductUrl(url);
        Elements oElements = dom.select("div.main_info_con div[class^=pd] h2,#productMainName");
        if (!oElements.isEmpty()) {
            productBean.setProductName(oElements.first().text());
        }
        oElements = dom.select("#productMercantId[value]");
        if (!oElements.isEmpty()) {
            productBean.setProductCode(oElements.first().attr("value").trim());
        }

        //
        Elements oHomeAs = dom.select("div.layout_wrap.crumbbox div.crumb,div.mod_detail_crumb div.crumb");
        if (oHomeAs.isEmpty()) {
            if (StringUtils.isEmpty(productBean.getProductCode())) {
                Pattern oReg = Pattern.compile("item/([0-9]{5,})");
                Matcher matcher = oReg.matcher(url);
                if (matcher.find()) {
                    productBean.setProductCode(matcher.group(1));
                }
            }
            productBean.setStockNum(-1);
            return dataBean;
        }
        String detailUrl = String.format(
                "http://gps.yihaodian.com/restful/detail?mcsite=1&provinceId=1&pmId=%s&callback=jsonp%s",
                productBean.getProductCode(), System.currentTimeMillis());
        HttpGet dGet = createHttpGetWithIp(detailUrl);
        dGet.addHeader("Refer", url);
        html = HttpClientUtils.getContent(client, dGet, "UTF-8");
        int fromIndex = html.indexOf("(");
        int toIndex = html.indexOf(")");
        fromIndex = fromIndex < 0 ? 0 : fromIndex;
        toIndex = toIndex < 0 ? 0 : html.length();
        html = html.substring(fromIndex + 1, toIndex);
        JSONObject dObject = new JSONObject(html);
        productBean.setProductPrice(JSONUtils.getFloat(dObject, "currentPrice"));
        productBean.setMarketPrice(JSONUtils.getFloat(dObject, "marketPrice"));
        productBean.setStockNum(JSONUtils.getInteger(dObject, "currentStockNum"));
        oElements = dom.select("#companyName[value]");
        if (!oElements.isEmpty()) {
            productBean.setShopName(oElements.first().attr("value"));
        }
        oElements = dom.select("#mod_salesvolume p strong");
        if (!oElements.isEmpty()) {
            Integer soldNum = JSONUtils.get(dObject, "soldNum");
            try {
                soldNum = Integer.valueOf(oElements.first().ownText().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            productBean.setSoldNum(soldNum);
        } else {
            oElements = dom.select("#mod_salesvolume[saleNumber]");
            if (!oElements.isEmpty()) {
                productBean.setSoldNum(Integer.valueOf(oElements.first().attr("saleNumber")));
            }
        }
        oElements = dom.select("div.crumb a[href^=http://www.yhd.com/ctg/],div.crumb a[href^=http://list.yhd.com/]");
        if (!oElements.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Element oEle : oElements) {
                if (sb.length() < 1) {
                    sb.append(oEle.ownText());
                } else {
                    sb.append(";");
                    sb.append(oEle.ownText());
                }
            }
            String sCat = sb.toString();
            productBean.setCategoryNav(sCat);
        }
        oElements = dom.select("#prodDetailCotentDiv.desitem dl.des_info dd[title]");
        if (!oElements.isEmpty()) {
            JSONArray descArray = new JSONArray();
            for (Element oEle : oElements) {
                descArray.put(oEle.attr("title"));
            }
            JSONObject attrObject = new JSONObject();
            JSONUtils.put(attrObject, "desc", descArray);
            productBean.setProductAttr(attrObject.toString());
        }
        oElements = dom.select("#merchantId[value]");
        if (!oElements.isEmpty()) {
            String merchantId = oElements.first().attr("value");
            String shopUrl = "1".equals(merchantId) ? "http://www.yhd.com/" : String.format(
                    "http://shop.yhd.com/m-%s.html", merchantId);
            productBean.setShopUrl(shopUrl);
            productBean.setShopCode(merchantId);
        }
        oElements = dom.select("#brandName[value]");
        if (!oElements.isEmpty()) {
            productBean.setProductBrand(oElements.first().attr("value"));
        } else {
            oElements = dom.select("#brand_relevance");
            if (!oElements.isEmpty()) {
                productBean.setProductBrand(oElements.first().ownText());
            }
        }
        oElements = dom.select("#J_tabSlider ul.imgtab_con li a img[id][src],img#J_prodImg[src]");
        if (!oElements.isEmpty()) {
            String imgUrl = oElements.first().attr("src");
            imgUrl = imgUrl.replace("_60x60.jpg", "_200x200.jpg");
            productBean.setImgUrl(imgUrl);
        }
        addSpuData(dom, productBean);
        // String mUrl =
        // String.format("http://e.yhd.com/front-pe/queryNumsByPm.do?pmInfoId=%s&callback=detailSkuPeComment.countCallback",
        // productBean.getProductCode());
        Integer siteType = 2;
        if ("1".equals(productBean.getShopCode())) {
            siteType = 1;
        }
        Elements pidAs = dom.select("#productId[value]");
        String productId = pidAs.first().attr("value");
        String mUrl =
                String
                        .format("http://e.yhd.com/front-pe/productExperience/proExperienceAction!ajaxView_pe.do?product.id=%s&merchantId=%s&pagenationVO.currentPage=1&pagenationVO.rownumperpage=5&currSiteId=1&f=1&currSiteType=%s&callback=flightHtmlHandler",
                                productId, productBean.getShopCode(), siteType);
        HttpGet mGet = createHttpGetWithIp(mUrl);
        dGet.addHeader("Refer", url);
        html = HttpClientUtils.getContent(client, mGet, "UTF-8");
        try {
            fromIndex = html.indexOf("(");
            toIndex = html.indexOf(")");
            fromIndex = fromIndex < 0 ? 0 : fromIndex;
            toIndex = toIndex < 0 ? 0 : html.length();
            html = html.substring(fromIndex + 1, toIndex);
            JSONObject mObject = new JSONObject(html);
            String source = mObject.getString("value");
            source = source == null ? "" : source;
            Document cmmDom = Jsoup.parse(source);
            Elements cmmAs = cmmDom.select("#all-comment_num");
            if (!cmmAs.isEmpty()) {
                Pattern oReg = Pattern.compile("[0-9]+");
                Matcher matcher = oReg.matcher(cmmAs.first().ownText());
                if (matcher.find()) {
                    productBean.setCommentNum(Integer.valueOf(matcher.group()));
                }
                Elements gCmmAs = cmmDom.select("div.comment_type ul li[tag=good-comment] span");
                if (!gCmmAs.isEmpty()) {
                    matcher = oReg.matcher(gCmmAs.first().ownText());
                    if (matcher.find()) {
                        productBean.setGoodComment(Integer.valueOf(matcher.group()));
                    }
                }
                Elements pCmmAs = cmmDom.select("div.comment_type ul li[tag=bad-comment] span");
                if (!pCmmAs.isEmpty()) {
                    matcher = oReg.matcher(pCmmAs.first().ownText());
                    if (matcher.find()) {
                        productBean.setPoorComment(Integer.valueOf(matcher.group()));
                    }
                }
                cmmAs = null;
                gCmmAs = null;
                pCmmAs = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataBean;
    }

    private void addSpuData(Document dom, ProductBean productBean) throws Exception {
        Elements scriptEls = dom.select("input[id][value] ~ script");
        if (scriptEls.isEmpty()) {
            return;
        }
        ScriptDocument scriptDocument = ScriptHtmlParser.parser(dom);
        ScriptWindow window = new ScriptWindow();
        window.setDocument(scriptDocument);
        String script = scriptEls.first().html();
        window.eval(script);
        Object jsObject = ScriptableObject.getProperty(window.getScope(), "subPmIdList");
        Object subPmIdList = NativeJSON.stringify(Context.getCurrentContext(), window.getScope(), jsObject, null, null);
        String sPmids = subPmIdList.toString();
        sPmids = sPmids.length() > 2 ? sPmids.substring(1, sPmids.length() - 1) : "";
        productBean.setSpuCodes(sPmids);
        Elements attrEls = dom.select("[id^=attribute]");
        JSONObject attrObject = new JSONObject();
        for (Element attrEle : attrEls) {
            String key = attrEle.select("dt").first().ownText();
            String value = attrEle.select("li[attrId][attrValueName].selected").first().attr("attrValueName");
            JSONUtils.put(attrObject, key, value);
        }
        productBean.setSpuVary(attrObject.toString());
    }

    private HttpGet createHttpGetWithIp(String url) throws Exception {
        URI oUri = new URI(url);
        String host = oUri.getHost();
        String oldUrl = oUri.toString();
        String ip = hostIpMap.get(host);
        if (ip != null) {
            url = oldUrl.replace(host, ip);
        }
        HttpGet get = new HttpGet(url);
        get.addHeader("Host", oUri.getHost());
        return get;
    }

    private void addCookie() {
        BasicClientCookie cookie = new BasicClientCookie("__utma",
                "40580330.1541470702.1396602044.1406527175.1406603327.18");
        client.getCookieStore().addCookie(cookie);
        cookie = new BasicClientCookie("__utmc", "193324902");
        client.getCookieStore().addCookie(cookie);
        cookie = new BasicClientCookie("__utmz",
                "193324902.1401026096.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
        client.getCookieStore().addCookie(cookie);
        cookie = new BasicClientCookie("provinceId", "1");
        client.getCookieStore().addCookie(cookie);
        String[] uArr = UUID.randomUUID().toString().split("-");
        cookie = new BasicClientCookie("uname", uArr[0]);
        client.getCookieStore().addCookie(cookie);
        cookie = new BasicClientCookie("yihaodian_uid", "" + Math.abs(uArr[0].hashCode()));
        client.getCookieStore().addCookie(cookie);
        cookie = new BasicClientCookie("i2042", "_");
        client.getCookieStore().addCookie(cookie);
        cookie = new BasicClientCookie("newUserFlag", "1");
        client.getCookieStore().addCookie(cookie);
        cookie = new BasicClientCookie("test_cookie", "1");
        client.getCookieStore().addCookie(cookie);
        cookie = new BasicClientCookie("msessionid", "VEEWSM5ZGU974XPBHBK49YEREC45U6ENG6UT");
        client.getCookieStore().addCookie(cookie);
    }

    private class ProductBean {
        // productStat
        private String productCode;
        private String productName;
        private String productUrl;
        private Long productPrice;
        private Long marketPrice;
        private Integer soldNum;
        private Integer commentNum;
        private Integer stockNum;
        private String categoryNav;
        // product
        private String productBrand;
        private String productModel;
        private String productAttr;
        private String barCode;
        private String imgUrl;
        private Date onsailTime;

        private Integer siteId = SITE_ID;
        private Integer goodComment;
        private Integer poorComment;

        private String spuCodes;
        private String spuVary;

        // shopDto
        private Integer shopId;
        private String shopName;
        private String shopCode;
        private String shopUrl;

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductUrl() {
            return productUrl;
        }

        public void setProductUrl(String productUrl) {
            this.productUrl = productUrl;
        }

        public Long getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(Float productPrice) {
            Long destValue = productPrice == null ? null : (long) (100 * productPrice);
            this.productPrice = destValue;
        }

        public Long getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(Float marketPrice) {
            Long destValue = marketPrice == null ? null : (long) (100 * marketPrice);
            this.marketPrice = destValue;
        }

        public Integer getSoldNum() {
            return soldNum;
        }

        public void setSoldNum(Integer soldNum) {
            this.soldNum = soldNum;
        }

        public Integer getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(Integer commentNum) {
            this.commentNum = commentNum;
        }

        public Integer getStockNum() {
            return stockNum;
        }

        public void setStockNum(Integer stockNum) {
            this.stockNum = stockNum;
        }

        public String getCategoryNav() {
            return categoryNav;
        }

        public void setCategoryNav(String categoryNav) {
            this.categoryNav = categoryNav;
        }

        public String getProductBrand() {
            return productBrand;
        }

        public void setProductBrand(String productBrand) {
            this.productBrand = productBrand;
        }

        public String getProductModel() {
            return productModel;
        }

        public void setProductModel(String productModel) {
            this.productModel = productModel;
        }

        public String getProductAttr() {
            return productAttr;
        }

        public void setProductAttr(String productAttr) {
            this.productAttr = productAttr;
        }

        public String getBarCode() {
            return barCode;
        }

        public void setBarCode(String barCode) {
            this.barCode = barCode;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public Date getOnsailTime() {
            return onsailTime;
        }

        public void setOnsailTime(Date onsailTime) {
            this.onsailTime = onsailTime;
        }

        public Integer getSiteId() {
            return siteId;
        }

        public void setSiteId(Integer siteId) {
            this.siteId = siteId;
        }

        public Integer getGoodComment() {
            return goodComment;
        }

        public void setGoodComment(Integer goodComment) {
            this.goodComment = goodComment;
        }

        public Integer getPoorComment() {
            return poorComment;
        }

        public void setPoorComment(Integer poorComment) {
            this.poorComment = poorComment;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getShopCode() {
            return shopCode;
        }

        public void setShopCode(String shopCode) {
            this.shopCode = shopCode;
        }

        public String getShopUrl() {
            return shopUrl;
        }

        public void setShopUrl(String shopUrl) {
            this.shopUrl = shopUrl;
        }

        public Integer getShopId() {
            return shopId;
        }

        public void setShopId(Integer shopId) {
            this.shopId = shopId;
        }

        public String getSpuCodes() {
            return spuCodes;
        }

        public void setSpuCodes(String spuCodes) {
            this.spuCodes = spuCodes;
        }

        public String getSpuVary() {
            return spuVary;
        }

        public void setSpuVary(String spuVary) {
            this.spuVary = spuVary;
        }

    }
}
