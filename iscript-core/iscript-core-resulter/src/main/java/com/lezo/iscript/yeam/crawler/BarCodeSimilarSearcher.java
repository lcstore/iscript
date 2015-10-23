package com.lezo.iscript.yeam.crawler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.BarCodeItemService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class BarCodeSimilarSearcher {
    private static Logger log = LoggerFactory.getLogger(BarCodeSimilarSearcher.class);
    private static volatile boolean running = false;
    private static final ConfigParser parser = new JdBarCodeSimilar();
    @Autowired
    private BarCodeItemService barCodeItemService;

    public void run() {
        if (running) {
            log.warn("HuihuiSimilarSearcher is working...");
            return;
        }
        try {
            running = true;
            doSimilarSearch();
        } catch (Exception ex) {
            log.warn("", ex);
            ex.fillInStackTrace();
        } finally {
            running = false;
        }
    }

    private void doSimilarSearch() {
        TaskWritable task = new TaskWritable();
        Long fromId = 0L;
        int limit = 200;
        List<Integer> shopIdList = new ArrayList<Integer>();
        shopIdList.add(1001);
        while (true) {
            List<BarCodeItemDto> barCodeItemDtos = barCodeItemService.getBarCodeItemDtoFromId(fromId, limit, null);
            for (BarCodeItemDto dto : barCodeItemDtos) {
                if (fromId < dto.getId()) {
                    fromId = dto.getId();
                }
                task.put("name", dto.getProductName());
                task.put("barCode", dto.getBarCode());
                for (Integer shopId : shopIdList) {
                    task.put("shopId", shopId);
                    String rs = doRetryCall(parser, task);
                    if (StringUtils.isEmpty(rs)) {
                        log.warn("can not get similar,barCode:" + dto.getBarCode() + ",name:" + dto.getProductName()
                                + ",name:" + dto.getProductName());
                        continue;
                    }
                    JSONObject rsObject = JSONUtils.getJSONObject(rs);
                    handleResult4Jd(rsObject, parser);
                }
            }
            if (barCodeItemDtos.size() < limit) {
                break;
            }
        }

    }

    private void handleResult4Jd(JSONObject rsObject, ConfigParser parser) {
        JSONObject argsObject = JSONUtils.get(rsObject, "args");
        rsObject = JSONUtils.getJSONObject(JSONUtils.get(rsObject, "rs"));
        if (rsObject == null) {
            log.warn("no similar to:" + argsObject);
            return;
        }
        JSONArray oList = JSONUtils.get(rsObject, "wareInfoList");
        if (oList == null || oList.length() < 1) {
            log.warn("no similar data list...");
            return;
        }
        Long similarCode = System.currentTimeMillis();
        List<SimilarDto> similarDtos = new ArrayList<SimilarDto>();

        for (int i = 0; i < oList.length(); i++) {
            try {
                JSONObject itemObject = oList.getJSONObject(i);
                handleItem4Jd(itemObject, similarDtos, parser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        addSimilarCode(similarDtos, similarCode);
        addBarCodeShopId(similarDtos, argsObject);
        // similarDtoStorageCaller.handleDtos(similarDtos);

    }

    private void addBarCodeShopId(List<SimilarDto> similarDtos, JSONObject argsObject) {
        if (similarDtos == null) {
            return;
        }
        Integer shopId = JSONUtils.getInteger(argsObject, "shopId");
        String barCode = JSONUtils.getString(argsObject, "barCode");
        for (SimilarDto dto : similarDtos) {
            dto.setSiteId(shopId);
            dto.setBarCode(barCode);
        }
    }

    private void addSimilarCode(List<SimilarDto> similarDtos, Long similarCode) {
        for (SimilarDto dto : similarDtos) {
            dto.setSimilarCode("" + similarCode);
        }
    }

    private void handleItem4Jd(JSONObject itemObject, List<SimilarDto> similarDtos, ConfigParser parser)
            throws Exception {
        if (itemObject == null) {
            return;
        }
        JSONObject mObject = itemObject;
        SimilarDto dto = new SimilarDto();
        dto.setProductName(JSONUtils.getString(mObject, "wname"));
        dto.setProductCode(JSONUtils.getString(mObject, "wareId"));
        dto.setProductUrl(getUrlFromCode(dto.getProductCode()));
        dto.setMarketPrice(JSONUtils.getFloat(mObject, "price"));
        String imgUrl = String.format("http://img10.360buyimg.com/n5/%s", JSONUtils.getString(mObject, "imageurl"));
        dto.setImgUrl(imgUrl);
        dto.setCreateTime(new Date());
        dto.setUpdateTime(dto.getCreateTime());
        similarDtos.add(dto);
    }

    private String getUrlFromCode(String productCode) {
        if (StringUtils.isEmpty(productCode)) {
            return null;
        }
        return String.format("http://item.jd.com/%s.html", productCode);
    }

    public boolean isChange(ProductStatDto oldDto, ProductStatDto newDto) {
        if (!isSameObject(oldDto.getProductPrice(), newDto.getProductPrice())) {
            return true;
        }
        if (!isSameObject(oldDto.getMarketPrice(), newDto.getMarketPrice())) {
            return true;
        }
        if (!isSameObject(oldDto.getSoldNum(), newDto.getSoldNum())) {
            return true;
        }
        if (!isSameObject(oldDto.getStockNum(), newDto.getStockNum())) {
            return true;
        }
        if (!isSameObject(oldDto.getCommentNum(), newDto.getCommentNum())) {
            return true;
        }
        return false;
    }

    public boolean isSameObject(Object lObject, Object rObject) {
        if (lObject == null && rObject == null) {
            return true;
        } else if (lObject == null && rObject != null) {
            return false;
        }
        return lObject.equals(rObject);
    }

    private String doRetryCall(ConfigParser parser, TaskWritable task) {
        int retry = 0;
        while (++retry < 3) {
            try {
                return parser.doParse(task);
            } catch (Exception e) {
                retry++;
                log.info("retry:" + retry + ",args:" + new JSONObject(task.getArgs()));
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // String[] configs = new String[] { "classpath:spring-config-ds.xml",
        // "classpath:spring/spring-bean-resulter.xml" };
        // ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        // BarCodeSimilarSearcher searcher = SpringBeanUtils.getBean(BarCodeSimilarSearcher.class);
        // searcher.run();

        String productUrl = "http://item.jd.com/856850.html";
        String productName = "中国台湾进口素手浣花黑糖棒棒糖140g";
        // ConfigParser similarParser = new HuihuiSimilar();
        // TaskWritable task = new TaskWritable();
        // task.put("url", productUrl);
        // task.put("name", productName);
        // String result = similarParser.doParse(task);
        // System.out.println(result);
        System.out.println(handleUrl("6911988013576", 1001));
    }

    public static String handleUrl(String barCode, Integer siteId) {
        String url = "";
        if (siteId == 1000) {
            // 处理新蛋URL
            url = "http://www.ows.newegg.com.cn/search?page=1&pagesize=20&sort=10&barcode=" + barCode;
        } else if (siteId == 1001) {
            // 处理京东URL
            url =
                    "http://gw.m.360buy.com/client.action?functionId=wareIdByBarCodeList&uuid="
                            + getRandomUid()
                            + "-002FE63DA839&clientVersion=3.2.2&client=android&d_brand=sprd&d_model=K-TouchT619&osVersion=2.3.5&screen=480*320&partner=jingdong&networkType=wifi&area=17_1381_0_0";
            String paramString = "{\"barcode\":\"" + barCode + "\"}";
            try {
                url += "&body=" + URLEncoder.encode(paramString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (siteId == 1002) {
            // 处理卓越URL
            url = "http://www.amazon.cn/s/ref=nb_sb_noss" + "?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99"
                    + "&url=search-alias%3Daps&field-keywords=" + barCode;
        } else if (siteId == 1003) {
            // 处理当当URL
            url = "http://mapi.dangdang.com/index.php?" + "user_client=android&client_version=3.3.0&result_format=2"
                    + "&action=list_isbn_product&img_size=b&isbn=" + barCode
                    + "&udid=5284047f4ffb4e04824a2fd1d1f0cd62&union_id=537-27&timestamp=&time_code=";
        } else if (siteId == 1004) {
            // 处理苏宁URL
            url = "http://www.suning.com/emall/snappprd_10052__" + barCode + "_9135__2_.html";
        } else if (siteId == 1008) {
            // 处理红孩子URL
            url = "http://mobile.binggo.com/product?pId=" + barCode;
        } else if (siteId == 1021) {
            url = "http://mobile.womai.com/wmapi/scancode?scancode=" + barCode;
        }
        try {
            // url = encodeUrl(url);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return url;
    }

    public static String getRandomUid() {
        Integer randomDataLong = new Random().nextInt(1000);
        String ranString = randomDataLong.toString();
        if (ranString.length() == 1) {
            ranString = "00000000000000" + ranString;
        } else if (ranString.length() == 2) {
            ranString = "0000000000000" + ranString;
        } else if (ranString.length() == 3) {
            ranString = "000000000000" + ranString;
        }
        return ranString;
    }
}
