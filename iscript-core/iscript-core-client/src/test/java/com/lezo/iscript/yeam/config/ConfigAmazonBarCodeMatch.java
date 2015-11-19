package com.lezo.iscript.yeam.config;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.PriceUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigAmazonBarCodeMatch implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    private static final Integer SITE_ID = 1003;

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
        String sUrl = getSearchUrl(barCode);
        HttpGet get = new HttpGet(sUrl);
        get.addHeader("Referer", "http://www.amazon.cn/");
        get.addHeader(
                "Cookie",
                "x-wl-uid=1E/IFQvX+aIzvlwU1d80AptMhqL4h2PbVDTq+OwEine+L1R1ZT7LCLWxuAR+KPPoyPZFwnwk2EGSikygeJht793gUsJFzPPG30j8J5LUkGye9hL5MCitXS21C5gfcdQnKZpKZaRWUznk=; session-id-time=2082729601l; session-id=475-7069679-7614531; ubid-acbcn=475-1985592-6987469; 5SnMamzvowels.pos=4; 5SnMamzvowels.time.0=1440158032866; 5SnMamzvowels.time.1=1440158293822; 5SnMamzvowels.time.2=1440158437442; 5SnMamzvowels.time.3=1440158725639; 5SnMamzvowels.time.4=1440157943223; __utma=164006624.1171682384.1443781472.1443781472.1445213798.2; __utmz=164006624.1445213798.2.2.utmccn=(referral)|utmcsr=mail.126.com|utmcct=/js6/read/readhtml.jsp|utmcmd=referral; __utmv=164006624.lcstore-23; session-token=\"T2EuZ5UiKJsrXTSqJgEe+nLVWvORtFTDcsQr12y0SF66bodY+/ZUuwxGrmJIhcAanpNGBd0FZbhLj89KRkiMxAHCTdfvUyD4Jk47qpUL9dScvFHT9SuKRSeqP46kSyuEFSp5hIp/TW0RKfOf8SNf5KUvq3LFa3hxz7m8C1xUgTi7LYyeN9YN5jZeQ0PL5klCsz96CPF+MBNlbaL8htagGg==\"; csm-hit=0N7S3EAFYBDFSJHN54ZP+s-0N7S3EAFYBDFSJHN54ZP|1447562773145");
        get.addHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:42.0) Gecko/20100101 Firefox/42.0");
        try {
            String html = HttpClientUtils.getContent(client, get);
            Document dom = Jsoup.parse(html);
            Elements rsList = dom.select("[id^=result_][data-asin]");
            if (!rsList.isEmpty()) {
                for (Element rsEle : rsList) {
                    ProductBean tBean = new ProductBean();
                    tBean.setSiteId(SITE_ID);
                    tBean.setBarCode(barCode);
                    tBean.setProductCode(rsEle.attr("data-asin").trim());
                    tBean.setProductUrl(String.format("http://www.amazon.cn/dp/%s", tBean.getProductCode()));
                    Elements nameEls = rsEle.select("div.s-item-container a.a-link-normal[title][href][target]");
                    if (!nameEls.isEmpty()) {
                        tBean.setProductName(nameEls.first().attr("title"));
                    }
                    Elements imgEls =
                            rsEle.select("div.s-item-container div.a-row.a-spacing-base img.s-access-image[src]");
                    if (!imgEls.isEmpty()) {
                        String sImgUrl = imgEls.first().attr("src");
                        sImgUrl = sImgUrl.replaceFirst("\\._AA[0-9]+_\\.", "._AA220_.");
                        tBean.setImgUrl(sImgUrl);
                    }
                    Elements brandEls =
                            rsEle.select("div.s-item-container div.a-row.a-spacing-mini:has(div.a-spacing-none) div.a-row.a-spacing-mini > span.a-size-small.a-color-secondary");
                    if (!brandEls.isEmpty()) {
                        tBean.setProductBrand(brandEls.last().ownText().trim());
                    }
                    Elements priceEls =
                            rsEle.select("div.s-item-container span.a-color-price.s-price");
                    if (!priceEls.isEmpty()) {
                        Pattern oReg = Pattern.compile("[0-9.]+");
                        Matcher matcher = oReg.matcher(priceEls.first().ownText());
                        if (matcher.find()) {
                            String sPrice = matcher.group();
                            tBean.setProductPrice(PriceUtils.toCentPrice(Float.valueOf(sPrice)));
                        }
                    }
                    tBean.setStockNum(1);
                    dataBean.getDataList().add(tBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataBean;
    }

    private String getSearchUrl(String keywords) {
        return String.format(
                "http://www.amazon.cn/s/ref=nb_sb_noss?__mk_zh_CN=亚马逊网站&url=search-alias=aps&field-keywords=%s",
                keywords);
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
