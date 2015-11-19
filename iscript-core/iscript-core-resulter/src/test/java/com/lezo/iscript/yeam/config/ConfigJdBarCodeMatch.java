package com.lezo.iscript.yeam.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.PriceUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigJdBarCodeMatch implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private static final Integer SITE_ID = 1001;

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
        if (CollectionUtils.isNotEmpty(dataBean.getDataList())) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Inclusion.NON_NULL);
            String dataString = mapper.writeValueAsString(dataBean);

            return dataString;
        }
        return null;
        // JSONObject returnObject = new JSONObject();
        // if (CollectionUtils.isNotEmpty(dataBean.getDataList())) {
        // JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
        // }
        // JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, dataString);
        // return returnObject.toString();
    }

    private DataBean getDataObject(TaskWritable task) throws Exception {
        String userAgent = "Dalvik/1.6.0 (Linux; U; Android 4.1.1; MI 2 MIUI/JLB34.0)";
        String oldAgent = HttpProtocolParams.getUserAgent(client.getParams());
        HttpProtocolParams.setUserAgent(client.getParams(), userAgent);
        String barCode = (String) task.getArgs().get("barCode");
        DataBean dataBean = new DataBean();
        if (!BarCodeUtils.isBarCode(barCode)) {
            return dataBean;
        }
        String sUrl = getSimilarUrl(barCode);
        HttpGet get = new HttpGet(sUrl);
        get.addHeader("refer", "http://gw.m.360buy.com");
        try {
            String html = HttpClientUtils.getContent(client, get);
            JSONObject dObject = JSONUtils.getJSONObject(html);
            JSONArray wareArray = JSONUtils.get(dObject, "wareInfoList");
            if (wareArray != null && wareArray.length() > 0) {
                for (int i = 0; i < wareArray.length(); i++) {
                    JSONObject dataObj = wareArray.getJSONObject(i);
                    ProductBean tBean = new ProductBean();
                    tBean.setSiteId(SITE_ID);
                    tBean.setBarCode(barCode);
                    tBean.setProductName(JSONUtils.getString(dataObj, "wname"));
                    tBean.setProductCode(JSONUtils.getString(dataObj, "wareId"));
                    tBean.setProductUrl(String.format("http://item.jd.com/%s.html", tBean.getProductCode()));
                    String isOnLine = JSONUtils.getString(dataObj, "onLine");
                    if ("false".equals(isOnLine)) {
                        tBean.setStockNum(0);
                    } else {
                        tBean.setStockNum(1);
                    }
                    tBean.setProductPrice(PriceUtils.toCentPrice(JSONUtils.getFloat(dataObj, "jdPrice")));
                    tBean.setShopCode(JSONUtils.getString(dataObj, "shopId"));
                    if (StringUtils.isNotBlank(tBean.getShopCode())) {
                        if ("0".equals(tBean.getShopCode())) {
                            tBean.setShopId(SITE_ID);
                        } else {
                            tBean.setShopUrl("http://mall.jd.com/index-" + tBean.getShopCode() + ".html");
                        }
                    }
                    String imgUrl = JSONUtils.getString(dataObj, "imageurl");
                    if (StringUtils.isNotBlank(imgUrl)) {
                        imgUrl = imgUrl.replaceFirst("/n[0-9]/", "/n1/");
                        tBean.setImgUrl(imgUrl);
                    }
                    dataBean.getDataList().add(tBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HttpProtocolParams.setUserAgent(client.getParams(), oldAgent);
        }
        return dataBean;
    }

    private String getSimilarUrl(String barCode) {
        String url =
                "http://gw.m.360buy.com/client.action?functionId=wareIdByBarCodeList&uuid="
                        + getRandomUid()
                        + "-acf7f34353f1&clientVersion=3.6.3&client=android&d_brand=Xiaomi&d_model=MI2&osVersion=4.1.1&screen=1280*720&partner=jingdong&networkType=wifi&area=2_2841_0_0&sv=1&st="
                        + System.currentTimeMillis();
        String paramString = "{\"barcode\":\"" + barCode + "\"}";
        try {
            url += "&body=" + URLEncoder.encode(paramString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getRandomUid() {
        Integer randomDataLong = new Random().nextInt(10000);
        String head = "860308028232581";
        String ranString = head + randomDataLong.toString();
        return ranString.substring(ranString.length() - head.length());
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

        private String wareCode;

        public void setSpuCodes(String spuCodes) {
            this.spuCodes = spuCodes;
        }

        public void setSpuVary(String spuVary) {
            this.spuVary = spuVary;
        }

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

        public void setProductPrice(Long productPrice) {
            this.productPrice = productPrice;
        }

        public Long getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(Long marketPrice) {
            this.marketPrice = marketPrice;
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

        public Integer getShopId() {
            return shopId;
        }

        public void setShopId(Integer shopId) {
            this.shopId = shopId;
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

        public String getSpuCodes() {
            return spuCodes;
        }

        public String getSpuVary() {
            return spuVary;
        }

        public String getWareCode() {
            return wareCode;
        }

        public void setWareCode(String wareCode) {
            this.wareCode = wareCode;
        }

    }
}
