package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.crawler.dom.ScriptDocument;
import com.lezo.iscript.crawler.dom.ScriptHtmlParser;
import com.lezo.iscript.crawler.dom.browser.ScriptWindow;
import com.lezo.iscript.rest.http.HttpClientUtils;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class VipListBarCodeStrategy implements ResultStrategy, Closeable {
    private static Logger logger = LoggerFactory.getLogger(VipListBarCodeStrategy.class);
    // private static final String DEFAULT_DETECT_URL = "http://www.baidu.com/";
    private static volatile boolean running = false;
    private Timer timer;

    public VipListBarCodeStrategy() {
        // ProxyDetectTimer task = new ProxyDetectTimer();
        // this.timer = new Timer(getName());
        // this.timer.schedule(task, 1 * 60 * 1000, 100 * 24 * 60 * 60 * 1000);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void handleResult(ResultWritable rWritable) {

    }

    private class ProxyDetectTimer extends TimerTask {
        private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);

        public ProxyDetectTimer() {
        }

        public void run() {
            if (running) {
                logger.warn("ProxyDetectTimer is working...");
                return;
            }
            long start = System.currentTimeMillis();
            int total = 0;
            String taskType = "ConfigVipBarCode";
            try {
                logger.info("add taskType:" + taskType + " is start...");
                List<String> listUrls = new ArrayList<String>();
                listUrls.add("http://www.vip.com/902");
                for (String listUrl : listUrls) {
                    List<TaskPriorityDto> taskList = getTaskList(listUrl, taskType);
                    taskPriorityService.batchInsert(taskList);
                    total += taskList.size();
                    logger.info("add task count:" + taskList.size() + ",url:" + listUrl);
                }
                int maxPage = 34;
                String taskId = UUID.randomUUID().toString();
                List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(maxPage);
                JSONObject argsObject = new JSONObject();
                JSONUtils.put(argsObject, "strategy", getName());
                for (int i = 1; i < maxPage; i++) {
                    String url = "http://category.vip.com/search-5-0-" + i + ".html?q=1|8399|&rp=8399|0#catPerPos";
                    TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                    newDto.setParams(argsObject.toString());
                    taskList.add(newDto);
                }
                taskPriorityService.batchInsert(taskList);
                total += taskList.size();
                logger.info("add task count:" + taskList.size()
                        + ",url:http://category.vip.com/search-5-0-1.html?q=1|8399|&rp=8399|0#catPerPos");
                running = true;
            } catch (Exception ex) {
                logger.warn(ExceptionUtils.getStackTrace(ex));
            } finally {
                long cost = System.currentTimeMillis() - start;
                String msg = String.format("add task.taskType:%s,total:%s,cost:%s", taskType, total, cost);
                logger.info(msg);
                running = false;
            }

        }

    }

    @Override
    public void close() throws IOException {
        this.timer.cancel();
        this.timer = null;
        logger.info("close " + getName() + " strategy..");
    }

    public List<TaskPriorityDto> getTaskList(String listUrl, String taskType) throws Exception {
        DefaultHttpClient client = HttpClientUtils.createHttpClient();
        HttpGet get = new HttpGet(listUrl);
        HttpResponse resp = client.execute(get);
        String html = EntityUtils.toString(resp.getEntity());
        Document dom = Jsoup.parse(html, get.getURI().toURL().toString());
        ScriptDocument sDom = ScriptHtmlParser.parser(dom);
        ScriptWindow window = new ScriptWindow();
        window.setDocument(sDom);
        Elements scriptEls = dom.select("#J-sp-foverseas-wrap + script[type=text/javascript]");
        String sScript = scriptEls.first().html();
        window.eval(sScript);
        Object jsObject = ScriptableObject.getProperty(window.getScope(), "floorBrandData");
        Object floorBrandData = NativeJSON.stringify(Context.getCurrentContext(), window.getScope(), jsObject,
                null, null);
        JSONObject brandObject = JSONUtils.getJSONObject(floorBrandData.toString());
        JSONArray bArray = JSONUtils.get(brandObject, "foverseas");
        List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(bArray.length());
        String taskId = UUID.randomUUID().toString();
        for (int i = 0; i < bArray.length(); i++) {
            JSONObject bObj = bArray.getJSONObject(i);
            String link = JSONUtils.getString(bObj, "link");
            String name = JSONUtils.getString(bObj, "name");
            String mark = "http:";
            int index = link.indexOf(mark);
            String url = link.substring(index, link.length() - 1);
            TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "name", name);
            newDto.setParams(argsObject.toString());
            taskList.add(newDto);
        }
        return taskList;
    }

    private TaskPriorityDto newPriorityDto(String sUrl, String taskType, String taskId) {
        TaskPriorityDto taskDto = new TaskPriorityDto();
        taskDto.setBatchId(taskId);
        taskDto.setType(taskType);
        taskDto.setUrl(sUrl);
        taskDto.setLevel(1);
        taskDto.setSource("tasker");
        taskDto.setCreatTime(new Date());
        taskDto.setStatus(TaskConstant.TASK_NEW);
        return taskDto;
    }

}
