package com.lezo.iscript.yeam.config;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.proxy.ProxyClientUtils;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.service.DataBean;
import com.lezo.iscript.yeam.writable.TaskWritable;

//http://bnm.com.hk/index_eng.php?o=item&act=show&pricestart=&priceend=&keywd=&id=&category=&order=&page=5
public class ConfigBnmList implements ConfigParser {
    private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

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

            ObjectMapper mapper = new ObjectMapper();
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, dataBean);
            String dataString = writer.toString();

            JSONUtils.put(returnObject, ClientConstant.KEY_STORAGE_RESULT, dataString);
            JSONUtils.put(returnObject, ClientConstant.KEY_CALLBACK_RESULT, dataString);
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
        get.addHeader("Referer", url);
        String html = HttpClientUtils.getContent(client, get);
        Set<String> itemUrlSet = new HashSet<String>();
        Document dom = Jsoup.parse(html, url);
        Elements urlEls = dom.select("a[href*=?o=item&act=view&id=]");
        for (Element ele : urlEls) {
            String sUrl = ele.absUrl("href");
            itemUrlSet.add(sUrl);
        }
        dataBean.getDataList().addAll(itemUrlSet);
        return dataBean;
    }

}