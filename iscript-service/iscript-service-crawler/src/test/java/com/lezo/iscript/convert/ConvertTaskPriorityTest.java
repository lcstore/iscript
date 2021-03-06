package com.lezo.iscript.convert;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.TaskPriorityDao;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.JSONUtils;

@Log4j
public class ConvertTaskPriorityTest {

    @Test
    public void testInsertBarCodeMatch() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        TaskPriorityDao taskPriorityDao = SpringBeanUtils.getBean(TaskPriorityDao.class);
        String type = "ConfigSuningBarCodeMatch";
        File bcFile =
                new File("/apps/src/codes/lezo/iscript/iscript-service/iscript-service-crawler/data/all.bc.854218.txt");
        String jobId = System.currentTimeMillis() + "";
        JSONObject argsObject = new JSONObject();
        JSONUtils.put(argsObject, "strategy", "BarCodeSimilarStrategy");
        JSONUtils.put(argsObject, "jobid", jobId);
        JSONUtils.put(argsObject, "bid", jobId);
        int total = 0;
        List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
        List<String> lineList = FileUtils.readLines(bcFile, "UTF-8");
        String url = "";
        for (String line : lineList) {
            JSONUtils.put(argsObject, "barCode", line.trim());
            TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
            taskList.add(taskDto);
        }
        BatchIterator<TaskPriorityDto> it = new BatchIterator<TaskPriorityDto>(taskList, 500);
        while (it.hasNext()) {
            taskPriorityDao.batchInsert(it.next());
        }
        total += taskList.size();
        log.info("insert type:" + type + ",count:" + taskList.size() + ",total:" + total);

    }

    @Test
    public void testInsertBarCodeSkuWithSimilarStrategy() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        TaskPriorityDao taskPriorityDao = SpringBeanUtils.getBean(TaskPriorityDao.class);
        String type = "ConfigYhdProduct";
        String jobId = System.currentTimeMillis() + ":sku";
        JSONObject argsObject = new JSONObject();
        JSONUtils.put(argsObject, "strategy", "SkuWithSimilarStrategy");
        JSONUtils.put(argsObject, "jobid", jobId);
        JSONUtils.put(argsObject, "bid", jobId);
        int total = 0;
        SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
        Long fromId = 0L;
        int limit = 500;
        int siteId = 1002;
        String srcJobId = "1446351659066";
        while (true) {
            List<SimilarDto> dtoList = similarService.getSimilarDtoByJobIdSiteId(srcJobId, siteId, fromId, limit);
            for (SimilarDto dto : dtoList) {
                if (fromId < dto.getId()) {
                    fromId = dto.getId();
                }
            }
            total += dtoList.size();
            log.info("siteId:" + siteId + ",fromId:" + fromId + ",size:" + dtoList.size() + ",total:" + total);
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
            for (SimilarDto sDto : dtoList) {
                if (StringUtils.isBlank(sDto.getBarCode())) {
                    continue;
                }
                JSONUtils.put(argsObject, "barCode", sDto.getBarCode());
                TaskPriorityDto taskDto = createPriorityDto(sDto.getProductUrl(), type, argsObject);
                taskList.add(taskDto);
            }
            BatchIterator<TaskPriorityDto> it = new BatchIterator<TaskPriorityDto>(taskList, 500);
            while (it.hasNext()) {
                taskPriorityDao.batchInsert(it.next());
            }
            log.info("insert type:" + type + ",count:" + taskList.size() + ",total:" + total);
            if (dtoList.size() < limit) {
                break;
            }

        }

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
        taskPriorityDto.setStatus(0);
        JSONObject paramObject = JSONUtils.getJSONObject(argsObject.toString());
        paramObject.remove("bid");
        paramObject.remove("type");
        paramObject.remove("url");
        paramObject.remove("level");
        paramObject.remove("src");
        paramObject.remove("ctime");
        if (taskPriorityDto.getLevel() == null) {
            taskPriorityDto.setLevel(1);
        }
        taskPriorityDto.setParams(paramObject.toString());
        return taskPriorityDto;
    }
}
