package com.lezo.iscript.service.crawler.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dao.TaskPriorityDao;
import com.lezo.iscript.service.crawler.dto.BrandDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.vo.BrandConfigVo;

@Log4j
public class TaskPriorityServiceImplTest {

    @Test
    public void testBatchInsert() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        TaskPriorityDao taskPriorityDao = SpringBeanUtils.getBean(TaskPriorityDao.class);
        String type = "ConfigJdProduct";
        File srcDir = new File("/apps/src/codes/lezo/iblade/data/jd/top/sku");
        File[] files = srcDir.listFiles();
        String jobId = System.currentTimeMillis() + "";
        String cate = "进口食品";
        JSONObject argsObject = new JSONObject();
        JSONUtils.put(argsObject, "strategy", "SkuWithSimilarStrategy");
        JSONUtils.put(argsObject, "cate", cate);
        JSONUtils.put(argsObject, "jobId", jobId);
        int total = 0;
        for (File f : files) {
            List<TaskPriorityDto> taskList = new ArrayList<TaskPriorityDto>();
            List<String> lineList = FileUtils.readLines(f, "UTF-8");
            for (String line : lineList) {
                JSONArray dArray = new JSONArray(line);
                if (dArray == null || dArray.length() < 1) {
                    continue;
                }
                for (int i = 0; i < dArray.length(); i++) {
                    JSONObject dObj = dArray.getJSONObject(i);
                    String url = JSONUtils.getString(dObj, "productUrl");
                    if (StringUtils.isBlank(url)) {
                        continue;
                    }
                    TaskPriorityDto taskDto = createPriorityDto(url, type, argsObject);
                    taskList.add(taskDto);
                }
            }
            taskPriorityDao.batchInsert(taskList);
            total += taskList.size();
            log.info("insert type:" + type + ",count:" + taskList.size() + ",total:" + total);

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

    private List<BrandDto> convertDto(BrandConfigVo brandVo) throws CloneNotSupportedException {
        if (StringUtils.isEmpty(brandVo.getSynonyms())) {
            return Collections.emptyList();
        }
        List<BrandDto> brandList = new ArrayList<BrandDto>();
        String sSynonyms = brandVo.getSynonyms();
        int fromIndex = sSynonyms.indexOf("[");
        int toIndex = sSynonyms.indexOf("]");
        fromIndex = fromIndex < 0 ? 0 : fromIndex + 1;
        toIndex = toIndex < 0 ? sSynonyms.length() : toIndex;
        sSynonyms = sSynonyms.substring(fromIndex, toIndex);
        String[] synStrings = sSynonyms.split(",");
        String synCode = BrandDto.randomSynonymCode();
        for (int i = 0; i < synStrings.length; i++) {
            BrandDto baseDto = new BrandDto();
            baseDto.setBrandCode(brandVo.getBrandCode());
            baseDto.setBrandUrl(brandVo.getBrandUrl());
            baseDto.setRegion(brandVo.getRegion());
            baseDto.setCreateTime(brandVo.getCreateTime());
            baseDto.setUpdateTime(brandVo.getUpdateTime());
            baseDto.setSiteId(brandVo.getSiteId());
            baseDto.setSynonymCode(synCode);
            baseDto.setBrandName(synStrings[i]);
            brandList.add(baseDto);
        }
        return brandList;
    }
}
