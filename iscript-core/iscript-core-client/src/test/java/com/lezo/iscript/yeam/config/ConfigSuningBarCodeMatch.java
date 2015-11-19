package com.lezo.iscript.yeam.config;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
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

public class ConfigSuningBarCodeMatch implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private static final Integer SITE_ID = 1005;

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
        get.addHeader(
                "User-Agent",
                "Mozilla/5.0(Linux; U;SNEBUY-APP; Android 4.1.1; zh; MI 2) AppleWebKit/533.0 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        String sCookie =
                "_customId=6r8nc0633526; _customId=6r8nc0633526; mtisAbTest=A; _device_session_id=6037080528471447682264522_37EFA83B721A5C5540B4497C839AD2060C43739F5DA295BC54CCF67CA72DC7C5C4CCF83A5C1296EA5BB00069905E98AD; cityId=9264; newCity=025_1000173_9264; districtId=12113";
        if (new Random().nextBoolean()) {
            sCookie = sCookie.replace("districtId=12113", "districtId=11365");
        }
        get.addHeader("Cookie", sCookie);
        try {
            String html = null;
            try {
                html = HttpClientUtils.getContent(client, get);
            } catch (Exception e) {
                TimeUnit.MILLISECONDS.sleep(200 + new Random().nextInt(500));
                html = HttpClientUtils.getContent(client, get);
            }
            JSONObject dObject = JSONUtils.getJSONObject(html);
            dObject = JSONUtils.getJSONObject(dObject, "data");
            JSONObject dataObj = JSONUtils.getJSONObject(dObject, "itemInfoVo");
            if (dataObj != null) {
                ProductBean tBean = new ProductBean();
                tBean.setSiteId(SITE_ID);
                tBean.setBarCode(barCode);
                tBean.setProductName(JSONUtils.getString(dataObj, "itemName"));
                if (StringUtils.isBlank(tBean.getProductName())) {
                    return dataBean;
                }
                String partNumber = JSONUtils.getString(dataObj, "partNumber");
                String passPartNumber = JSONUtils.getString(dObject, "passPartNumber");
                String pCode = partNumber.substring(passPartNumber.length());
                tBean.setProductCode(pCode);
                tBean.setWareCode(JSONUtils.getString(dataObj, "itemId"));
                tBean.setProductUrl(String.format("http://product.suning.com/%s.html", tBean.getProductCode()));
                if (isPlatform(passPartNumber)) {
                    tBean.setShopId(tBean.getSiteId());
                } else {
                    tBean.setShopId(0);
                }
                tBean.setProductPrice(PriceUtils.toCentPrice(JSONUtils.getFloat(dataObj, "itemPrice")));
                if (tBean.getProductPrice() == null || tBean.getProductPrice() <= 0) {
                    tBean.setStockNum(0);
                } else {
                    tBean.setStockNum(1);
                }
                tBean.setProductBrand(JSONUtils.getString(dataObj, "brandName"));
                tBean.setShopCode(passPartNumber);
                String imgUrl = "http://image5.suning.cn/b2c/catentries/" + partNumber + "_1_200x200.jpg";
                tBean.setImgUrl(imgUrl);
                dataBean.getDataList().add(tBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataBean;
    }

    private boolean isPlatform(String passPartNumber) {
        if (StringUtils.isBlank(passPartNumber)) {
            return false;
        }
        char sMark = '0';
        for (int i = 0; i < passPartNumber.length(); i++) {
            if (sMark != (passPartNumber.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private String getSimilarUrl(String barCode) {
        return "http://product.m.suning.com/pds-web/app/itemBigInfo__" + barCode + ".html";
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
