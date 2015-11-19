package com.lezo.iscript.yeam.config;

import java.security.MessageDigest;
import java.util.Date;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.PriceUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigDangBarCodeMatch implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private static final Integer SITE_ID = 1015;

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
        String barCode = (String) task.getArgs().get("barCode");
        DataBean dataBean = new DataBean();
        if (!BarCodeUtils.isBarCode(barCode)) {
            return dataBean;
        }
        String sUrl = getSimilarUrl(barCode);
        HttpGet get = new HttpGet(sUrl);
        get.addHeader("Accept-Encoding", "gzip");
        get.addHeader("User-Agent", "4.1.1,MI 2,720*1280");
        get.addHeader("X-Online-Host", "mapi.dangdang.com");
        get.addHeader("Host", "mapi.dangdang.com");
        try {
            String html = HttpClientUtils.getContent(client, get);
            JSONObject dObject = JSONUtils.getJSONObject(html);
            JSONArray productArray = JSONUtils.get(dObject, "product");
            if (productArray != null && productArray.length() > 0) {
                for (int i = 0; i < productArray.length(); i++) {
                    Object pObj = productArray.get(i);
                    JSONObject dataObj = null;
                    try {
                        if (pObj instanceof JSONObject) {
                            dataObj = (JSONObject) pObj;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        System.err.println(dObject);
                        throw e;
                    }
                    ProductBean tBean = new ProductBean();
                    tBean.setSiteId(SITE_ID);
                    tBean.setBarCode(barCode);
                    tBean.setProductName(JSONUtils.getString(dataObj, "product_name"));
                    tBean.setProductCode(JSONUtils.getString(dataObj, "product_id"));
                    // tBean.setWareCode(JSONUtils.getString(dataObj, "itemId"));
                    tBean.setProductUrl(String.format("http://product.dangdang.com/%s.html", tBean.getProductCode()));
                    String shopId = JSONUtils.getString(dataObj, "shop_id");
                    if ("0".equals(shopId)) {
                        tBean.setShopId(tBean.getSiteId());
                    } else {
                        tBean.setShopCode(shopId);
                        tBean.setShopUrl(String.format("http://shop.dangdang.com/%s", tBean.getShopCode()));
                    }
                    tBean.setProductPrice(PriceUtils.toCentPrice(JSONUtils.getFloat(dataObj, "sale_price")));
                    if (tBean.getProductPrice() == null || tBean.getProductPrice() <= 0) {
                        tBean.setStockNum(0);
                    } else {
                        tBean.setStockNum(1);
                    }
                    String imgUrl = JSONUtils.getString(dataObj, "image_url");
                    imgUrl = imgUrl.replaceFirst("-[0-9_a-zA-Z]+\\.", "-1_b_1\\.");
                    tBean.setImgUrl(imgUrl);
                    dataBean.getDataList().add(tBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataBean;
    }

    private String getSimilarUrl(String barCode) {
        long stamp = System.currentTimeMillis() / 1000;
        StringBuilder sb = new StringBuilder();
        String timestamp = "" + stamp;
        // timestamp = "1447576198";
        sb.append("list_isbn_product").append(",").append(timestamp).append(",XinXF,")
                .append("f6cb965617ddf6a2cc605e63986cbe76");
        String encode = encode(sb.toString());

        return "http://mapi.dangdang.com/index.php?permanent_id=20151115155913994749351552620346617&timestamp="
                + timestamp
                + "&img_size=b&union_id=537-100475&user_client=android&isbn=" + barCode
                + "&action=list_isbn_product&time_code="
                + encode + "&udid=f6cb965617ddf6a2cc605e63986cbe76&client_version=6.0.2";
    }

    private static final char[] chars = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };

    public static String encode(String paramString) {
        int j = 0;
        try
        {
            Object localObject = MessageDigest.getInstance("MD5");
            ((MessageDigest) localObject).update(paramString.getBytes("UTF-8"));
            byte[] mBytes = ((MessageDigest) localObject).digest();
            int k = mBytes.length;
            char[] destChars = new char[k << 1];
            int i = 0;
            while (i < k)
            {
                int m = j + 1;
                destChars[j] = chars[((mBytes[i] & 0xF0) >>> 4)];
                j = m + 1;
                destChars[m] = chars[(mBytes[i] & 0xF)];
                i += 1;
            }
            return new String(destChars);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
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
