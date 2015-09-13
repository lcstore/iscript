package com.lezo.iscript.yeam.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Scriptable;

import com.lezo.iscript.proxy.ProxyClientUtils;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class Config1688List implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
    public static final Integer SITE_ID = 1001;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    private void ensureCookie() throws Exception {
        Set<String> checkSet = new HashSet<String>();
        checkSet.add("__jda");
        checkSet.add("__jdb");
        checkSet.add("__jdc");
        checkSet.add("__jdv");
        boolean hasAddCookie = false;
        for (Cookie ck : client.getCookieStore().getCookies()) {
            if (checkSet.contains(ck.getName())) {
                hasAddCookie = true;
                break;
            }
        }
        if (!hasAddCookie) {
            addCookie(client, null);
        }
    }

    private void addCookie(DefaultHttpClient client, Scriptable scope) throws Exception {
        Map<String, String> cookieMap = new HashMap<String, String>();
        cookieMap.put("__jda", "95931165.580577879.1416135846.1416135846.1416135846.1");
        cookieMap.put("__jdb", "95931165.1.580577879|1.1416135846");
        cookieMap.put("__jdc", "95931165");
        cookieMap.put("__jdv", "95931165|direct|-|none|-");
        for (String key : cookieMap.keySet()) {
            String cookieValue = cookieMap.get(key);
            BasicClientCookie cookie = new BasicClientCookie(key, cookieValue);
            cookie.setDomain(".jd.com");
            client.getCookieStore().addCookie(cookie);
        }
    }

    @Override
    public String doParse(TaskWritable task) throws Exception {
        // ensureCookie();
        DataBean dataBean = getDataObject(task);
        return convert2TaskCallBack(dataBean, task);
    }

    private String convert2TaskCallBack(DataBean dataBean, TaskWritable task) throws Exception {
        JSONObject returnObject = new JSONObject();
        if (dataBean != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Inclusion.NON_NULL);
            // 不序列化null
            String dataString = mapper.writeValueAsString(dataBean);

            JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, dataString);
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
        url = doUnify(url);
        HttpGet get = ProxyClientUtils.createHttpGet(url, task);
        String html = HttpClientUtils.getContent(client, get);
        html = getHtml(html);
        Document dom = Jsoup.parse(html, url);
        DataBean dataBean = new DataBean();
        addByDom(dom, dataBean);
        addByScript(dom, dataBean);
        addNexts(dataBean, dom);
        return dataBean;
    }

    private String getHtml(String html) {
        if (!html.startsWith("jQuery")) {
            return html;
        }
        int startIndex = html.indexOf("{");
        int endIndex = html.lastIndexOf("}");
        if (startIndex < 0 || endIndex < 0) {
            return html;
        }
        String source = html.substring(startIndex, endIndex + 1);
        JSONObject dObject = JSONUtils.getJSONObject(source);
        dObject = JSONUtils.getJSONObject(dObject, "content");
        dObject = JSONUtils.getJSONObject(dObject, "offerResult");
        if (dObject == null) {
            return html;
        }
        return JSONUtils.getString(dObject, "html");
    }

    private void addByScript(Document dom, DataBean dataBean) throws Exception {
        Elements dataEls = dom.select("div.sm-offer-trigger[data-mod-config*=rpc_async_render.jsonp]");
        if (dataEls.isEmpty()) {
            return;
        }
        String sConfig = dataEls.first().attr("data-mod-config");
        JSONObject jObject = JSONUtils.getJSONObject(sConfig);
        if (jObject == null) {
            return;
        }
        String sUrl = JSONUtils.getString(jObject, "url");
        System.err.println(sUrl);
        if (StringUtils.isBlank(sUrl)) {
            return;
        }
        sUrl = doUnify(sUrl);
        HttpGet get = new HttpGet(sUrl);
        String html = HttpClientUtils.getContent(client, get);
        html = getHtml(html);
        dom = Jsoup.parse(html, dom.baseUri());
        addByDom(dom, dataBean);
        addByScript(dom, dataBean);
        Set<Object> urlSet = new HashSet<Object>(dataBean.getDataList());
        dataBean.getDataList().clear();
        dataBean.getDataList().addAll(urlSet);

    }

    private String doUnify(String sUrl) {
        if (sUrl.indexOf("rpc_async_render") > 0 && sUrl.indexOf("callback") < 0) {
            long curMills = System.currentTimeMillis();
            long stamp = curMills + new Random().nextInt(1000);
            sUrl += "&callback=jQuery183016101696020531775_" + curMills + "&_=" + stamp;
        }
        return sUrl;
    }

    private void addByDom(Document dom, DataBean dataBean) {
        Elements urlEls = dom.select("a[href^=http://detail.1688.com/offer/]");
        List<Object> dataList = dataBean.getDataList();
        for (Element urlEle : urlEls) {
            String sUrl = urlEle.absUrl("href");
            dataList.add(sUrl);
        }
    }

    private void addNexts(DataBean dataBean, Document dom) {
        if (dom.baseUri().indexOf("rpc_async_render.jsonp") > 0) {
            return;
        }
        Elements curELs = dom.select("div.sm-pagination[data-async-url][data-total-page]");
        if (curELs.isEmpty()) {
            return;
        }
        Integer total = Integer.valueOf(curELs.first().attr("data-total-page"));
        String curPageUrl = curELs.first().absUrl("data-async-url");
        System.err.println(curPageUrl);
        List<Object> nextList = dataBean.getNextList();
        for (int i = 2; i <= total; i++) {
            nextList.add(curPageUrl.replace("{page}", "" + i));
        }
    }

}