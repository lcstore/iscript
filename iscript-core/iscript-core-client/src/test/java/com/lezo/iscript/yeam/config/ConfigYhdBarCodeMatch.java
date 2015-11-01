package com.lezo.iscript.yeam.config;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.PriceUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigYhdBarCodeMatch implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private static final Integer SITE_ID = 1002;

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
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Inclusion.NON_NULL);
        String dataString = mapper.writeValueAsString(dataBean);
        JSONObject returnObject = new JSONObject();
        if (CollectionUtils.isNotEmpty(dataBean.getDataList())) {
            JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
        }
        JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, dataString);
        return returnObject.toString();
    }

    private DataBean getDataObject(TaskWritable task) throws Exception {
        String sBarCode = (String) task.get("barCode");
        String sUrl = getUrl(sBarCode);
        HttpPost post = new HttpPost(sUrl);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("userToken", "");
        paramMap.put("provinceId", "1");
        paramMap.put("Accept-Encoding", "gzip, deflate");
        paramMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        paramMap.put(
                "clientInfo",
                "{\"clientAppVersion\":\"4.1.2\",\"clientSystem\":\"android\",\"clientVersion\":\"4.1.1\",\"deviceCode\":\"ffffffff-8070-baad-ea14-430a62cce3ff\",\"iaddr\":\"1\",\"imei\":\"860308028232581\",\"latitude\":\"31.197161\",\"longitude\":\"121.430204\",\"nettype\":\"wifi\",\"phoneType\":\"MI 2,16,4.1.1\",\"traderName\":\"androidSystem\",\"unionKey\":\"8149186\"}");

        post.addHeader(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
        for (Entry<String, String> entry : paramMap.entrySet()) {
            post.addHeader(entry.getKey(), entry.getValue());
        }
        String html = HttpClientUtils.getContent(client, post);
        JSONObject dObject = JSONUtils.getJSONObject(html);
        DataBean dataBean = new DataBean();
        dObject = JSONUtils.getJSONObject(dObject, "data");
        if (dObject == null) {
            return dataBean;
        }
        JSONArray dArray = JSONUtils.get(dObject, "productList");
        if (dArray == null) {
            return dataBean;
        }
        for (int i = 0; i < dArray.length(); i++) {
            JSONObject dObj = dArray.getJSONObject(i);
            ProductBean tBean = new ProductBean();
            tBean.setProductName(JSONUtils.getString(dObj, "cnName"));
            tBean.setBarCode(sBarCode);
            String imgUrl = JSONUtils.getString(dObj, "midleDefaultProductUrl");
            imgUrl = imgUrl.replace("_200x200.jpg", "_360x360.jpg");
            tBean.setImgUrl(imgUrl);
            tBean.setMarketPrice(PriceUtils.toCentPrice(JSONUtils.getFloat(dObj, "maketPrice")));
            tBean.setProductCode(JSONUtils.getString(dObj, "pmId"));
            tBean.setSiteId(SITE_ID);
            Boolean canBuy = JSONUtils.get(dObj, "canBuy");
            if (canBuy != null && canBuy) {
                tBean.setStockNum(1);
            } else {
                tBean.setStockNum(0);
            }
            tBean.setShopName(JSONUtils.getString(dObj, "merchantName"));
            tBean.setShopCode(JSONUtils.getString(dObj, "merchantId"));
            if (StringUtils.isBlank(tBean.getShopName())) {
                tBean.setShopId(SITE_ID);
            }
            if (StringUtils.isBlank(tBean.getShopCode())) {
                tBean.setShopUrl("http://shop.yhd.com/m-" + tBean.getShopCode() + ".html");
            }
            tBean.setProductPrice(PriceUtils.toCentPrice(JSONUtils.getFloat(dObj, "yhdPrice")));
            tBean.setWareCode(JSONUtils.getString(dObj, "productId"));
            tBean.setProductUrl("http://item.yhd.com/item/" + tBean.getProductCode());
            dataBean.getDataList().add(tBean);
        }

        return dataBean;
    }

    public String getUrl(String bCode) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String timestamp = "" + System.currentTimeMillis() / 1000;
        map.put("timestamp", timestamp);
        map.put("guid", "0");
        map.put("methodBody", "");
        map.put("signature_method", "md5");
        map.put("barcode", bCode);
        map.put("trader", "androidSystem");
        map.put("methodName", "getProductByBarcodeWithPMS/v1.3.8");
        String sKey = "wwwdhsm6";
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(map);
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : treeMap.entrySet()) {
            sb.append(entry.getKey().toLowerCase(Locale.US)).append("=").append(entry.getValue());
        }
        sb.append(sKey);
        String newParams = sb.toString();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String signature = new String(doCharMap(md.digest(newParams.getBytes())));

        map.put("signature", signature);
        StringBuffer urlBuffer = new StringBuffer("http://mapi.yhd.com/search/getProductByBarcodeWithPMS/v1.3.8?");
        Iterator<Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            urlBuffer.append(entry.getKey()).append("=").append(entry.getValue());
            if (it.hasNext()) {
                urlBuffer.append("&");
            }
        }
        return urlBuffer.toString();
    }

    private static final char[] CHAR_MAP = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };

    public static char[] doCharMap(byte[] paramArrayOfByte)
    {
        int j = 0;
        int k = paramArrayOfByte.length;
        char[] arrayOfChar = new char[k << 1];
        int i = 0;
        for (;;)
        {
            if (i >= k) {
                return arrayOfChar;
            }
            int m = j + 1;
            arrayOfChar[j] = CHAR_MAP[((paramArrayOfByte[i] & 0xF0) >>> 4)];
            j = m + 1;
            arrayOfChar[m] = CHAR_MAP[(paramArrayOfByte[i] & 0xF)];
            i += 1;
        }
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
        cookie = new BasicClientCookie("msessionid", "1PJ241E6A15H8896SR8M7ANCZBRWJX14");
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
