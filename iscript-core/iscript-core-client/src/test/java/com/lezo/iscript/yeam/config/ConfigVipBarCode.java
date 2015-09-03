package com.lezo.iscript.yeam.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
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
import com.lezo.iscript.proxy.ProxyClientUtils;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

/**
 * http://list.vip.com/508518.html
 * 
 * /104/3100165393496104251/1/3605532996837-5.jpg
 * 
 * @author lezo
 *
 */
public class ConfigVipBarCode implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private static Pattern oBarCodeReg = Pattern.compile("(/[0-9]/)([0-9]{13,})(-[0-9]\\.jpg)");
    private static Pattern oPriceReg = Pattern.compile("[.0-9]+");
    private static final int SITE_ID = 1003;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String doParse(TaskWritable task) throws Exception {
        DataBean dataBean = getDataObject(task);
        return convert2TaskCallBack(dataBean, task);
    }

    private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
        JSONObject returnObject = new JSONObject();
        JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, JSONUtils.EMPTY_JSONOBJECT);
        if (dataBean != null) {
            // dataBean.getTargetList().add("ProductDto");
            // dataBean.getTargetList().add("ProductStatDto");

            // 通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化
            // Inclusion.Include.ALWAYS 默认
            // Inclusion.NON_DEFAULT 属性为默认值不序列化
            // Inclusion.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
            // Inclusion.NON_NULL 属性为NULL 不序列化
            // mapper.setSerializationInclusion(Inclusion.NON_NULL);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Inclusion.NON_NULL);
            // 不序列化null
            String dataString = mapper.writeValueAsString(dataBean);

            JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
        }
        return returnObject.toString();
    }

    /**
     * {"dataList":[],"nextList":[]}
     * 
     * @param task
     * @return
     * @throws Exception
     */
    private DataBean getDataObject(TaskWritable task) throws Exception {
        String url = task.get("url").toString();
        DataBean dataBean = new DataBean();
        HttpGet get = ProxyClientUtils.createHttpGet(url, task);
        String html = HttpClientUtils.getContent(client, get);
        Document dom = Jsoup.parse(html, url);
        Map<String, ProductBean> code2BeanMap = new HashMap<String, ProductBean>();
        addDomSkus(dom, code2BeanMap);
        addScriptSkus(dom, code2BeanMap);
        parseBarCode(code2BeanMap);
        dataBean.getDataList().addAll(code2BeanMap.values());
        return dataBean;
    }

    private void parseBarCode(Map<String, ProductBean> code2BeanMap) {
        for (Entry<String, ProductBean> entry : code2BeanMap.entrySet()) {
            ProductBean tBean = entry.getValue();
            if (StringUtils.isNotBlank(tBean.getImgUrl())) {
                Matcher matcher = oBarCodeReg.matcher(tBean.getImgUrl());
                if (matcher.find()) {
                    String sBarCode = matcher.group(2);
                    if (BarCodeUtils.isBarCode(sBarCode)) {
                        tBean.setBarCode(sBarCode);
                    }
                }
            }
        }

    }

    private void addScriptSkus(Document dom, Map<String, ProductBean> code2BeanMap) throws Exception {
        ScriptDocument scriptDocument = ScriptHtmlParser.parser(dom);
        ScriptWindow window = new ScriptWindow();
        window.setDocument(scriptDocument);
        Elements scriptEls = dom.select("#J_sizeItem_tmp + script[type=text/javascript]");
        String source = scriptEls.first().html();
        window.eval("var $ ={}; $.Loader={}; $.Loader.advScript = function(){};");
        window.eval(source);

        Object jsObject = ScriptableObject.getProperty(window.getScope(), "merchandise");
        Object merchandise = NativeJSON.stringify(Context.getCurrentContext(), window.getScope(), jsObject,
                null, null);
        JSONArray mArray = new JSONArray(merchandise.toString());
        for (int i = 0; i < mArray.length(); i++) {
            JSONObject itemObj = mArray.getJSONObject(i);
            ProductBean tBean = new ProductBean();
            tBean.setProductCode(JSONUtils.getString(itemObj, "id"));
            tBean.setProductName(JSONUtils.getString(itemObj, "name"));
            tBean.setProductUrl(JSONUtils.getString(itemObj, "link"));
            tBean.setImgUrl(JSONUtils.getString(itemObj, "img"));
            tBean.setProductPrice(JSONUtils.getFloat(itemObj, "sell_price"));
            tBean.setMarketPrice(JSONUtils.getFloat(itemObj, "market_price"));
            tBean.setStockNum(JSONUtils.getInteger(itemObj, "itemNum"));
            code2BeanMap.put(tBean.getProductCode(), tBean);
        }
    }

    private void addDomSkus(Document dom, Map<String, ProductBean> code2BeanMap) {
        Elements elements = dom.select("[id^=J_pro_].J_pro_items");
        for (Element ele : elements) {
            Elements picEls = ele.select("dt.pro_list_pic a[href][title]");
            if (picEls.isEmpty()) {
                continue;
            }
            ProductBean tBean = new ProductBean();
            tBean.setProductUrl(picEls.first().absUrl("href"));
            tBean.setProductName(picEls.first().attr("title"));
            tBean.setProductCode(ele.id().replace("J_pro_", ""));
            code2BeanMap.put(tBean.getProductCode(), tBean);
            Elements imgEls = picEls.select("img.J_first_pic[src]");
            if (!imgEls.isEmpty()) {
                tBean.setImgUrl(imgEls.first().absUrl("src"));
            }
            Elements priceEls = ele.select("dd.pro_list_data span.deep_red em");
            if (!priceEls.isEmpty()) {
                String sPrice = priceEls.first().ownText();
                Matcher matcher = oPriceReg.matcher(sPrice);
                if (matcher.find()) {
                    tBean.setProductPrice(Float.valueOf(matcher.group()));
                }
            }
            Elements marketPriceEls = ele.select("dd.pro_list_data del.gray");
            if (!priceEls.isEmpty()) {
                String sPrice = marketPriceEls.first().ownText();
                Matcher matcher = oPriceReg.matcher(sPrice);
                if (matcher.find()) {
                    tBean.setMarketPrice(Float.valueOf(matcher.group()));
                }
            }

        }
    }

    @SuppressWarnings("unused")
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