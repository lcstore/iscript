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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
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
                total += addAptamil(taskType);
                total += addBnm("ConfigBnmList");
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

}
