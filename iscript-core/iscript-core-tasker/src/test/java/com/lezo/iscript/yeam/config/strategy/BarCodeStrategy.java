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
            String taskType = "ConfigBarCodeCollector";
            try {
                logger.info("add taskType:" + taskType + " is start...");
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
                total += taskList.size();
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
