package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class BarCodeStrategy implements ResultStrategy, Closeable {
    private static Logger logger = LoggerFactory.getLogger(BarCodeStrategy.class);
    private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);
    // private static final String DEFAULT_DETECT_URL = "http://www.baidu.com/";
    private static volatile boolean running = false;
    private Timer timer;

    public BarCodeStrategy() {
        ProxyDetectTimer task = new ProxyDetectTimer();
        // this.timer = new Timer(getName());
        // this.timer.schedule(task, 1 * 60 * 1000, 100 * 24 * 60 * 60 * 1000);
        task.run();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void handleResult(ResultWritable rWritable) {
        if (ResultWritable.RESULT_SUCCESS != rWritable.getStatus()) {
            return;
        }
        JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
        JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
        JSONArray dArray = JSONUtils.get(rsObject, "dataList");
        JSONArray nextArray = JSONUtils.get(rsObject, "nextList");
        JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
        addNexts(rWritable, nextArray, argsObject);
        addNewType(rWritable, dArray, argsObject);

    }

    private void addNexts(ResultWritable rWritable, JSONArray nextArray, JSONObject srcArgsObj) {
        if (nextArray == null || nextArray.length() < 1) {
            return;
        }
        List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>();
        for (int i = 0; i < nextArray.length(); i++) {
            try {
                String nextUrl = nextArray.getString(i);
                TaskPriorityDto taskPriorityDto = createPriorityDto(nextUrl, rWritable.getType(), srcArgsObj);
                dtoList.add(taskPriorityDto);
            } catch (JSONException e) {
                logger.warn("" + nextArray, e);
            }
        }
        taskPriorityService.batchInsert(dtoList);
        logger.info("addNexts from nextList,type:" + rWritable.getType() + ",count:" + dtoList.size());
    }

    private TaskPriorityDto createPriorityDto(String url, String type, JSONObject argsObject) {
        String taskId = JSONUtils.getString(argsObject, "bid");
        taskId = taskId == null ? UUID.randomUUID().toString() : taskId;
        TaskPriorityDto taskPriorityDto = new TaskPriorityDto();
        taskPriorityDto.setBatchId(taskId);
        taskPriorityDto.setType(type);
        taskPriorityDto.setUrl(url);
        taskPriorityDto.setLevel(JSONUtils.getInteger(argsObject, "level"));
        taskPriorityDto.setSource(JSONUtils.getString(argsObject, "src"));
        taskPriorityDto.setCreatTime(new Date());
        taskPriorityDto.setUpdateTime(taskPriorityDto.getCreatTime());
        taskPriorityDto.setStatus(TaskConstant.TASK_NEW);
        JSONObject paramObject = JSONUtils.getJSONObject(argsObject.toString());
        paramObject.remove("bid");
        paramObject.remove("type");
        paramObject.remove("url");
        paramObject.remove("level");
        paramObject.remove("src");
        paramObject.remove("ctime");
        if (taskPriorityDto.getLevel() == null) {
            taskPriorityDto.setLevel(0);
        }
        taskPriorityDto.setParams(paramObject.toString());
        return taskPriorityDto;
    }

    private void addNewType(ResultWritable rWritable, JSONArray dArray, JSONObject srcArgsObj) {
        if (dArray == null || dArray.length() < 1) {
            return;
        }
        JSONObject argsObject = new JSONObject();
        JSONUtils.put(argsObject, "strategy", getName());
        String taskType = "ConfigBarCodeCollector";
        String taskId = UUID.randomUUID().toString();
        int len = dArray.length();
        List<TaskPriorityDto> dtoList = new ArrayList<TaskPriorityDto>(len);
        for (int i = 0; i < len; i++) {
            try {
                Object dVal = dArray.get(i);
                if (dVal instanceof String) {
                    String sUrl = dVal.toString();
                    if (sUrl.indexOf("http") >= 0) {
                        TaskPriorityDto dto = newPriorityDto(dVal.toString(), taskType, taskId);
                        dto.setParams(argsObject.toString());
                        dtoList.add(dto);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        taskPriorityService.batchInsert(dtoList);
        logger.info("add task from dataList,type:" + taskType + ",count:" + dtoList.size());
    }

    private class ProxyDetectTimer extends TimerTask {

        public ProxyDetectTimer() {
        }

        public void run() {
            if (running) {
                logger.warn("ProxyDetectTimer is working...");
                return;
            }
            long start = System.currentTimeMillis();
            int total = 0;
            String taskType = "ConfigBarCodeCollector";
            try {
                logger.info("add taskType:" + taskType + " is start...");
                // total += addMeilis(taskType);
                // total += addLeebaa(taskType);
                // total += addAptamil(taskType);
                // total += addBnm("ConfigBnmList");
                // total += add1688("Config1688List");
                // total += addSoukai(taskType);
                // total += addMdmall(taskType);
                // total += addHaitao(taskType);
                // total += addKjt(taskType);
                total += addRakuten(taskType);
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

        private int addRakuten(String taskType) {
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            JSONUtils.put(argsObject, "retry", 0);
            Map<String, Integer> urlPageMap = new HashMap<String, Integer>();
            // urlPageMap.put("http://global.rakuten.com/zh-cn/category/100939/?p=1&h=3&l-id=rgm-top-cn-navi-beauty",
            // 9120);
            urlPageMap.put("http://global.rakuten.com/zh-cn/category/100938/?p=1&h=3&l-id=rgm-top-cn-navi-health",
                    6337);
            urlPageMap.put("http://global.rakuten.com/zh-cn/category/551169/?p=1&h=3&l-id=rgm-top-cn-navi-medicine",
                    6110);
            int total = 0;
            for (Entry<String, Integer> entry : urlPageMap.entrySet()) {
                List<String> urlList = new ArrayList<String>(entry.getValue());
                for (int i = 1; i <= entry.getValue(); i++) {
                    String sUrl = entry.getKey().replace("p=1", "p=" + i);
                    urlList.add(sUrl);
                }
                BatchIterator<String> it = new BatchIterator<String>(urlList, 1000);
                while (it.hasNext()) {
                    List<String> subList = it.next();
                    String taskId = UUID.randomUUID().toString();
                    List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(subList.size());
                    for (String url : subList) {
                        TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                        newDto.setParams(argsObject.toString());
                        taskList.add(newDto);
                    }
                    taskPriorityService.batchInsert(taskList);
                    total += taskList.size();
                }
            }
            logger.info("add task:" + taskType + ",global.rakuten.com,count:" + total);
            return total;
        }

        private int addKjt(String taskType) {
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            JSONUtils.put(argsObject, "retry", 0);
            List<String> urlList = new ArrayList<String>();
            for (int i = 10000; i <= 20000; i++) {
                urlList.add("http://www.kjt.com/product/detail/" + i);
            }
            BatchIterator<String> it = new BatchIterator<String>(urlList, 500);
            int total = 0;
            while (it.hasNext()) {
                List<String> subList = it.next();
                String taskId = UUID.randomUUID().toString();
                List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(subList.size());
                for (String url : subList) {
                    TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                    newDto.setParams(argsObject.toString());
                    taskList.add(newDto);
                }
                taskPriorityService.batchInsert(taskList);
                total += taskList.size();
            }
            logger.info("add task:" + taskType + ",http://tuan.haitao.com,count:" + total);
            return total;
        }

        private int addHaitao(String taskType) {
            String taskId = UUID.randomUUID().toString();
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            List<String> urlList = new ArrayList<String>();
            // 5000~ 后续
            for (int i = 5000; i <= 7770; i++) {
                urlList.add("http://tuan.haitao.com/" + i + ".html");
            }
            // urlList.add("");
            for (String url : urlList) {
                TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                newDto.setParams(argsObject.toString());
                taskList.add(newDto);
            }
            taskPriorityService.batchInsert(taskList);
            logger.info("add task:" + taskType + ",http://tuan.haitao.com,count:" + taskList.size());
            return taskList.size();
        }

        private int addMdmall(String taskType) {
            String taskId = UUID.randomUUID().toString();
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            List<String> urlList = new ArrayList<String>();
            for (int i = 10000; i <= 26200; i++) {
                urlList.add("http://www.mdmall.com.au/goods.php?id=" + i);
            }
            // urlList.add("");
            for (String url : urlList) {
                TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                newDto.setParams(argsObject.toString());
                taskList.add(newDto);
            }
            taskPriorityService.batchInsert(taskList);
            logger.info("add task:" + taskType + ",http://www.mdmall.com.au,count:" + taskList.size());
            return taskList.size();
        }

        private int addSoukai(String taskType) {
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            JSONUtils.put(argsObject, "retry", 0);
            Map<String, Integer> urlCountMap = new HashMap<String, Integer>();
            urlCountMap.put("http://www.soukai.com/G485/p1/li.html", 35);
            urlCountMap.put("http://www.soukai.com/G013/p1/li.html", 23);
            urlCountMap.put("http://www.soukai.com/G4520/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G650/p1/li.html", 20);
            urlCountMap.put("http://www.soukai.com/G020/p1/li.html", 14);
            urlCountMap.put("http://www.soukai.com/G660/p1/li.html", 13);
            urlCountMap.put("http://www.soukai.com/G3811/p1/li.html", 6);
            urlCountMap.put("http://www.soukai.com/G3885/p1/li.html", 7);
            urlCountMap.put("http://www.soukai.com/G655/p1/li.html", 14);
            urlCountMap.put("http://www.soukai.com/G030/p1/li.html", 18);
            urlCountMap.put("http://www.soukai.com/G3849/p1/li.html", 5);
            urlCountMap.put("http://www.soukai.com/G0150/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G5292/p1/li.html", 4);
            urlCountMap.put("http://www.soukai.com/G955/p1/li.html", 7);
            urlCountMap.put("http://www.soukai.com/G025/p1/li.html", 10);
            urlCountMap.put("http://www.soukai.com/G5300/p1/li.html", 5);
            urlCountMap.put("http://www.soukai.com/G670/p1/li.html", 7);
            urlCountMap.put("http://www.soukai.com/G5290/p1/li.html", 4);
            urlCountMap.put("http://www.soukai.com/G3856/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G665/p1/li.html", 4);
            urlCountMap.put("http://www.soukai.com/G3884/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G0220/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G0240/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G0190/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G956/p1/li.html", 12);
            urlCountMap.put("http://www.soukai.com/G5311/p1/li.html", 4);
            urlCountMap.put("http://www.soukai.com/G5312/p1/li.html", 5);
            urlCountMap.put("http://www.soukai.com/G957/p1/li.html", 36);
            urlCountMap.put("http://www.soukai.com/G5332/p1/li.html", 10);
            urlCountMap.put("http://www.soukai.com/G960/p1/li.html", 26);
            urlCountMap.put("http://www.soukai.com/G963/p1/li.html", 30);
            urlCountMap.put("http://www.soukai.com/G959/p1/li.html", 25);
            urlCountMap.put("http://www.soukai.com/G961/p1/li.html", 20);
            urlCountMap.put("http://www.soukai.com/G958/p1/li.html", 8);
            urlCountMap.put("http://www.soukai.com/G9032/p1/li.html", 4);
            urlCountMap.put("http://www.soukai.com/G5319/p1/li.html", 4);
            urlCountMap.put("http://www.soukai.com/G9385/p1/li.html", 2);
            urlCountMap.put("http://www.soukai.com/G5351/p1/li.html", 18);
            urlCountMap.put("http://www.soukai.com/G965/p1/li.html", 14);
            urlCountMap.put("http://www.soukai.com/G5344/p1/li.html", 8);
            urlCountMap.put("http://www.soukai.com/G9383/p1/li.html", 6);
            urlCountMap.put("http://www.soukai.com/G5315/p1/li.html", 1);
            urlCountMap.put("http://www.soukai.com/G5343/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G9387/p1/li.html", 2);
            urlCountMap.put("http://www.soukai.com/G969/p1/li.html", 3);
            urlCountMap.put("http://www.soukai.com/G990/p1/li.html", 4);
            urlCountMap.put("http://www.soukai.com/G9384/p1/li.html", 1);
            urlCountMap.put("http://www.soukai.com/G962/p1/li.html", 11);
            urlCountMap.clear();
            urlCountMap = getUrlMap();
            int total = 0;
            for (Entry<String, Integer> entry : urlCountMap.entrySet()) {
                String sUrl = entry.getKey();
                String taskId = UUID.randomUUID().toString();
                List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(entry.getValue());
                for (int i = 1; i <= entry.getValue(); i++) {
                    String sNewUrl = sUrl.replace("/li.html", "/p" + i + "/li.html");
                    TaskPriorityDto newDto = newPriorityDto(sNewUrl, taskType, taskId);
                    newDto.setParams(argsObject.toString());
                    taskList.add(newDto);
                }
                taskPriorityService.batchInsert(taskList);
                logger.info("add task:" + taskType + ",http://www.soukai.com,count:" + taskList.size());
                total += taskList.size();

            }
            return total;
        }

        private int add1688(String taskType) {
            String taskId = UUID.randomUUID().toString();
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            List<String> urlList = new ArrayList<String>();
            urlList.add("http://in.1688.com/import/offer_search.htm?spm=a26qs.7707710.1998522834.171.kMNzin&categoryID=10311");
            urlList.add("http://in.1688.com/import/offer_search.htm?spm=a26qs.7707710.1998522834.183.kMNzin&categoryID=82101");
            urlList.add("http://in.1688.com/import/offer_search.htm?spm=a26qs.7707710.1998522834.191.kMNzin&categoryID=52772006");
            urlList.add("http://in.1688.com/import/offer_search.htm?spm=a26qs.7707710.1998522834.202.kMNzin&categoryID=1043594");
            urlList.add("http://in.1688.com/import/-CEC0C9FABDED-97.html?spm=a26qs.7707710.1998522834.208.kMNzin");
            urlList.add("http://in.1688.com/import/offer_search.htm?spm=a26qs.7707710.1998522834.212.kMNzin&categoryID=97");
            // urlList.add("");
            for (String url : urlList) {
                TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                newDto.setParams(argsObject.toString());
                taskList.add(newDto);
            }
            taskPriorityService.batchInsert(taskList);
            logger.info("add task:" + taskType + ",in.1688.com,count:" + taskList.size());
            return taskList.size();
        }

        private int addBnm(String taskType) {
            int maxPage = 716;
            String taskId = UUID.randomUUID().toString();
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(maxPage);
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            for (int i = 1; i <= maxPage; i++) {
                String url =
                        "http://bnm.com.hk/index_eng.php?o=item&act=show&pricestart=&priceend=&keywd=&id=&category=&order=&page="
                                + i;
                TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                newDto.setParams(argsObject.toString());
                taskList.add(newDto);
            }
            taskPriorityService.batchInsert(taskList);
            logger.info("add task:" + taskType + ",bnm.com.hk,count:" + taskList.size());
            return taskList.size();
        }

        private int addAptamil(String taskType) {
            int maxPage = 716;
            String taskId = UUID.randomUUID().toString();
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(maxPage);
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            List<String> urlList = new ArrayList<String>();
            urlList.add("http://www.aptamil.com.hk/aptamil-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/hipp-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/beba-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/bebivita-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/alete-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/penaten-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/china-oel-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/alverde-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/babydream-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/babylove-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/balea-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/bübchen-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/das-gesunde-plus-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/dontodent-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/holle-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/humana-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/humaneo-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/lebenswert-bio-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/milasan-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/milupa-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/s-quito-free-worldwide-delivery?limit=100");
            urlList.add("http://www.aptamil.com.hk/topfer-lactana-worldwide-delivery?limit=100");
            for (String url : urlList) {
                TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                newDto.setParams(argsObject.toString());
                taskList.add(newDto);
            }
            taskPriorityService.batchInsert(taskList);
            logger.info("add task:" + taskType + ",bnm.com.hk,count:" + taskList.size());
            return taskList.size();
        }

        private int addMeilis(String taskType) {
            int maxPage = 57;
            String taskId = UUID.randomUUID().toString();
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>(maxPage);
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            for (int i = 1; i <= maxPage; i++) {
                String url = "http://www.meili51.com/huo.asp?page=" + 57;
                TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                newDto.setParams(argsObject.toString());
                taskList.add(newDto);
            }
            taskPriorityService.batchInsert(taskList);
            logger.info("add task:" + taskType + ",www.meili51.com,count:" + taskList.size());
            return taskList.size();
        }

        private int addLeebaa(String taskType) {
            String taskId = UUID.randomUUID().toString();
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            for (int i = 3000; i <= 5000; i++) {
                String url = "http://www.leebaa.com/archivers/p" + i + ".html";
                TaskPriorityDto newDto = newPriorityDto(url, taskType, taskId);
                newDto.setParams(argsObject.toString());
                taskList.add(newDto);
            }
            taskPriorityService.batchInsert(taskList);
            logger.info("add task:" + taskType + ",www.leebaa.com,count:" + taskList.size());
            return taskList.size();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        logger.info("close " + getName() + " strategy..");
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

    private Map<String, Integer> getUrlMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("http://www.soukai.com/G013/li.html?ref=mega", 22);
        map.put("http://www.soukai.com/G010/li.html?ref=mega", 20);
        map.put("http://www.soukai.com/G4520/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G650/li.html?ref=mega", 19);
        map.put("http://www.soukai.com/G020/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G660/li.html?ref=mega", 12);
        map.put("http://www.soukai.com/G3811/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G3885/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G655/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G030/li.html?ref=mega", 17);
        map.put("http://www.soukai.com/G3849/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G0150/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G5292/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G955/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G025/li.html?ref=mega", 9);
        map.put("http://www.soukai.com/G5300/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G670/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G5290/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G3856/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G665/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G3884/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G0220/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G0240/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G0190/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G956/li.html?ref=mega", 11);
        map.put("http://www.soukai.com/G5311/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G5312/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G957/li.html?ref=mega", 36);
        map.put("http://www.soukai.com/G5332/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G960/li.html?ref=mega", 25);
        map.put("http://www.soukai.com/G963/li.html?ref=mega", 30);
        map.put("http://www.soukai.com/G959/li.html?ref=mega", 24);
        map.put("http://www.soukai.com/G961/li.html?ref=mega", 19);
        map.put("http://www.soukai.com/G958/li.html?ref=mega", 7);
        map.put("http://www.soukai.com/G9032/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G5319/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G9385/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G5351/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/G965/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G5344/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G9383/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G5315/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G5343/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G9387/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G969/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G990/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G9384/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G962/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G778/li.html?ref=mega", 57);
        map.put("http://www.soukai.com/G645/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/G640/li.html?ref=mega", 155);
        map.put("http://www.soukai.com/G770/li.html?ref=mega", 76);
        map.put("http://www.soukai.com/G635/li.html?ref=mega", 72);
        map.put("http://www.soukai.com/G5677/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G776/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G780/li.html?ref=mega", 31);
        map.put("http://www.soukai.com/G983/li.html?ref=mega", 15);
        map.put("http://www.soukai.com/G9359/li.html?ref=mega", 22);
        map.put("http://www.soukai.com/G3710/li.html?ref=mega", 9);
        map.put("http://www.soukai.com/G9341/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G779/li.html?ref=mega", 26);
        map.put("http://www.soukai.com/G775/li.html?ref=mega", 40);
        map.put("http://www.soukai.com/G636/li.html?ref=mega", 14);
        map.put("http://www.soukai.com/G5991/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/G4917/li.html?ref=mega", 17);
        map.put("http://www.soukai.com/G9358/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G4916/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G9372/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G637/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G984/li.html?ref=mega", 14);
        map.put("http://www.soukai.com/G4924/li.html?ref=mega", 19);
        map.put("http://www.soukai.com/G4933/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G1333/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G1335/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G1295/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G1328/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G1299/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G1293/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G1287/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G1284/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G1310/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G1353/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G1352/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G1326/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G1288/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G1311/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G1312/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G1292/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G1324/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G1285/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G1306/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G1305/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G1308/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G148/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G1296/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G1318/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G039/li.html?ref=mega", 14);
        map.put("http://www.soukai.com/G9092/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G9094/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G9095/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G9098/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G9089/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G9090/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G316/li.html?ref=mega", 32);
        map.put("http://www.soukai.com/G9191/li.html?ref=mega", 11);
        map.put("http://www.soukai.com/G9192/li.html?ref=mega", 7);
        map.put("http://www.soukai.com/G318/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/G320/li.html?ref=mega", 14);
        map.put("http://www.soukai.com/G9188/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G9190/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G1505/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G4002/li.html?ref=mega", 12);
        map.put("http://www.soukai.com/G4004/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G1790/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G4587/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G051/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G5948/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G1673/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G1660/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G9236/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G485/li.html?ref=mega", 55);
        map.put("http://www.soukai.com/G4430/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G4426/li.html?ref=mega", 12);
        map.put("http://www.soukai.com/G4428/li.html?ref=mega", 11);
        map.put("http://www.soukai.com/G520/li.html?ref=mega", 9);
        map.put("http://www.soukai.com/G490/li.html?ref=mega", 19);
        map.put("http://www.soukai.com/G493/li.html?ref=mega", 24);
        map.put("http://www.soukai.com/G540/li.html?ref=mega", 25);
        map.put("http://www.soukai.com/G978/li.html?ref=mega", 103);
        map.put("http://www.soukai.com/G575/li.html?ref=mega", 61);
        map.put("http://www.soukai.com/G578/li.html?ref=mega", 40);
        map.put("http://www.soukai.com/G512/li.html?ref=mega", 42);
        map.put("http://www.soukai.com/G510/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/G500/li.html?ref=mega", 30);
        map.put("http://www.soukai.com/G495/li.html?ref=mega", 7);
        map.put("http://www.soukai.com/G505/li.html?ref=mega", 20);
        map.put("http://www.soukai.com/G537/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/G535/li.html?ref=mega", 22);
        map.put("http://www.soukai.com/G515/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G560/li.html?ref=mega", 33);
        map.put("http://www.soukai.com/G3023/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G550/li.html?ref=mega", 7);
        map.put("http://www.soukai.com/G557/li.html?ref=mega", 77);
        map.put("http://www.soukai.com/G340/li.html?ref=mega", 14);
        map.put("http://www.soukai.com/G325/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G971/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G5420/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G345/li.html?ref=mega", 26);
        map.put("http://www.soukai.com/G334/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G346/li.html?ref=mega", 28);
        map.put("http://www.soukai.com/G326/li.html?ref=mega", 7);
        map.put("http://www.soukai.com/G333/li.html?ref=mega", 34);
        map.put("http://www.soukai.com/G4099/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G972/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G4109/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G4118/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G4625/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G4092/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G355/li.html?ref=mega", 206);
        map.put("http://www.soukai.com/G947/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G949/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G951/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G954/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G335/li.html?ref=mega", 29);
        map.put("http://www.soukai.com/G337/li.html?ref=mega", 97);
        map.put("http://www.soukai.com/G347/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G007/li.html?ref=mega", 7);
        map.put("http://www.soukai.com/G027/li.html?ref=mega", 32);
        map.put("http://www.soukai.com/G009/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G5820/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G998/li.html?ref=mega", 31);
        map.put("http://www.soukai.com/G005/li.html?ref=mega", 20);
        map.put("http://www.soukai.com/G6211/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G060/li.html?ref=mega", 22);
        map.put("http://www.soukai.com/G002/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G003/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G006/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G004/li.html?ref=mega", 12);
        map.put("http://www.soukai.com/G5763/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G001/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G9230/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G5994/li.html?ref=mega", 11);
        map.put("http://www.soukai.com/G5806/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G5779/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G991/li.html?ref=mega", 21);
        map.put("http://www.soukai.com/G5799/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G5792/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G5770/li.html?ref=mega", 11);
        map.put("http://www.soukai.com/G5811/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G5775/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G414/li.html?ref=mega", 40);
        map.put("http://www.soukai.com/G4324/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G4325/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G380/li.html?ref=mega", 16);
        map.put("http://www.soukai.com/G2368/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G412/li.html?ref=mega", 443);
        map.put("http://www.soukai.com/G4296/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G064/li.html?ref=mega", 9);
        map.put("http://www.soukai.com/G4297/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G053/li.html?ref=mega", 150);
        map.put("http://www.soukai.com/G052/li.html?ref=mega", 117);
        map.put("http://www.soukai.com/G401/li.html?ref=mega", 180);
        map.put("http://www.soukai.com/G415/li.html?ref=mega", 69);
        map.put("http://www.soukai.com/G5574/li.html?ref=mega", 9);
        map.put("http://www.soukai.com/G420/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G480/li.html?ref=mega", 48);
        map.put("http://www.soukai.com/G5575/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G400/li.html?ref=mega", 45);
        map.put("http://www.soukai.com/G4363/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G750/li.html?ref=mega", 16);
        map.put("http://www.soukai.com/G4691/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G405/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G430/li.html?ref=mega", 17);
        map.put("http://www.soukai.com/G435/li.html?ref=mega", 17);
        map.put("http://www.soukai.com/G062/li.html?ref=mega", 87);
        map.put("http://www.soukai.com/G068/li.html?ref=mega", 55);
        map.put("http://www.soukai.com/G6050/li.html?ref=mega", 11);
        map.put("http://www.soukai.com/G6044/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/G6046/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G6047/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G069/li.html?ref=mega", 23);
        map.put("http://www.soukai.com/G070/li.html?ref=mega", 36);
        map.put("http://www.soukai.com/G6067/li.html?ref=mega", 17);
        map.put("http://www.soukai.com/G6126/li.html?ref=mega", 15);
        map.put("http://www.soukai.com/G6129/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G6128/li.html?ref=mega", 8);
        map.put("http://www.soukai.com/G072/li.html?ref=mega", 48);
        map.put("http://www.soukai.com/G074/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G6123/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G071/li.html?ref=mega", 32);
        map.put("http://www.soukai.com/G6076/li.html?ref=mega", 14);
        map.put("http://www.soukai.com/G076/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G079/li.html?ref=mega", 20);
        map.put("http://www.soukai.com/G078/li.html?ref=mega", 44);
        map.put("http://www.soukai.com/G081/li.html?ref=mega", 14);
        map.put("http://www.soukai.com/G080/li.html?ref=mega", 11);
        map.put("http://www.soukai.com/G067/li.html?ref=mega", 42);
        map.put("http://www.soukai.com/G077/li.html?ref=mega", 20);
        map.put("http://www.soukai.com/G6144/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G021/li.html?ref=mega", 31);
        map.put("http://www.soukai.com/G019/li.html?ref=mega", 32);
        map.put("http://www.soukai.com/G014/li.html?ref=mega", 19);
        map.put("http://www.soukai.com/G012/li.html?ref=mega", 73);
        map.put("http://www.soukai.com/G017/li.html?ref=mega", 23);
        map.put("http://www.soukai.com/G022/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G016/li.html?ref=mega", 16);
        map.put("http://www.soukai.com/G024/li.html?ref=mega", 28);
        map.put("http://www.soukai.com/G015/li.html?ref=mega", 22);
        map.put("http://www.soukai.com/G5885/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G993/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G5899/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G5972/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G5864/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G5862/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G5856/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G5832/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G018/li.html?ref=mega", 15);
        map.put("http://www.soukai.com/G890/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G900/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/G5163/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G5075/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G5077/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G046/li.html?ref=mega", 7);
        map.put("http://www.soukai.com/G9133/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G5063/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G5159/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G5158/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G905/li.html?ref=mega", 24);
        map.put("http://www.soukai.com/G925/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/G920/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G910/li.html?ref=mega", 25);
        map.put("http://www.soukai.com/G945/li.html?ref=mega", 53);
        map.put("http://www.soukai.com/G6224/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G941/li.html?ref=mega", 24);
        map.put("http://www.soukai.com/G930/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G5076/li.html?ref=mega", 1);
        map.put("http://www.soukai.com/G5121/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G5092/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G5130/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G5755/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G365/li.html?ref=mega", 35);
        map.put("http://www.soukai.com/Gp10/li.html?ref=mega", 277);
        map.put("http://www.soukai.com/Gp100/li.html?ref=mega", 52);
        map.put("http://www.soukai.com/Gp155/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/Gp110/li.html?ref=mega", 29);
        map.put("http://www.soukai.com/Gp120/li.html?ref=mega", 98);
        map.put("http://www.soukai.com/Gp130/li.html?ref=mega", 48);
        map.put("http://www.soukai.com/Gp140/li.html?ref=mega", 21);
        map.put("http://www.soukai.com/Gp150/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/Gp320/li.html?ref=mega", 92);
        map.put("http://www.soukai.com/Gp310/li.html?ref=mega", 31);
        map.put("http://www.soukai.com/Gp300/li.html?ref=mega", 101);
        map.put("http://www.soukai.com/Gp330/li.html?ref=mega", 15);
        map.put("http://www.soukai.com/Gp20/li.html?ref=mega", 164);
        map.put("http://www.soukai.com/Gp230/li.html?ref=mega", 18);
        map.put("http://www.soukai.com/Gp200/li.html?ref=mega", 28);
        map.put("http://www.soukai.com/Gp210/li.html?ref=mega", 71);
        map.put("http://www.soukai.com/Gp240/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/Gp220/li.html?ref=mega", 23);
        map.put("http://www.soukai.com/Gp50/li.html?ref=mega", 68);
        map.put("http://www.soukai.com/Gp510/li.html?ref=mega", 13);
        map.put("http://www.soukai.com/Gp575/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/Gp540/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/Gp40/li.html?ref=mega", 63);
        map.put("http://www.soukai.com/G623/li.html?ref=mega", 74);
        map.put("http://www.soukai.com/G620/li.html?ref=mega", 57);
        map.put("http://www.soukai.com/G622/li.html?ref=mega", 39);
        map.put("http://www.soukai.com/G621/li.html?ref=mega", 123);
        map.put("http://www.soukai.com/G6403/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G6443/li.html?ref=mega", 10);
        map.put("http://www.soukai.com/G995/li.html?ref=mega", 79);
        map.put("http://www.soukai.com/G6481/li.html?ref=mega", 3);
        map.put("http://www.soukai.com/G6445/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G6521/li.html?ref=mega", 0);
        map.put("http://www.soukai.com/G6460/li.html?ref=mega", 4);
        map.put("http://www.soukai.com/G6478/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G626/li.html?ref=mega", 176);
        map.put("http://www.soukai.com/G6480/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G6474/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G6466/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G6432/li.html?ref=mega", 7);
        map.put("http://www.soukai.com/G6430/li.html?ref=mega", 17);
        map.put("http://www.soukai.com/G6408/li.html?ref=mega", 6);
        map.put("http://www.soukai.com/G624/li.html?ref=mega", 42);
        map.put("http://www.soukai.com/G9461/li.html?ref=mega", 25);
        map.put("http://www.soukai.com/G6477/li.html?ref=mega", 5);
        map.put("http://www.soukai.com/G6433/li.html?ref=mega", 2);
        map.put("http://www.soukai.com/G6463/li.html?ref=mega", 5);
        return map;
    }

}
