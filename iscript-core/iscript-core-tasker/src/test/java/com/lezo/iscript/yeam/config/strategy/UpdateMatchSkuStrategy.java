package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.iscript.service.crawler.dto.MatchDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.dto.SiteDto;
import com.lezo.iscript.service.crawler.dto.TaskPriorityDto;
import com.lezo.iscript.service.crawler.service.MatchService;
import com.lezo.iscript.service.crawler.service.ProductStatService;
import com.lezo.iscript.service.crawler.service.TaskPriorityService;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.service.crawler.utils.SiteCacher;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.ObjectUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.task.TaskConstant;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class UpdateMatchSkuStrategy implements ResultStrategy, Closeable {
    private static Logger logger = LoggerFactory.getLogger(UpdateMatchSkuStrategy.class);
    private static volatile boolean running = false;
    private Timer timer;

    public UpdateMatchSkuStrategy() {
        CreateTaskTimer task = new CreateTaskTimer();
        // this.timer = new Timer(getName());
        // this.timer.schedule(task, 60 * 1000, 24 * 60 * 60 * 1000);
        task.run();
    }

    private class CreateTaskTimer extends TimerTask {
        private TaskPriorityService taskPriorityService = SpringBeanUtils.getBean(TaskPriorityService.class);
        private MatchService matchService = SpringBeanUtils.getBean(MatchService.class);

        public CreateTaskTimer() {
        }

        @Override
        public void run() {
            if (running) {
                logger.warn("CreateTaskTimer is working...");
                return;
            }
            long start = System.currentTimeMillis();
            try {
                logger.info("CreateTaskTimer is start...");
                running = true;
                int total = 0;
                int siteId = 1002;
                Long fromId = 0L;
                Date fromCreateDate = null;
                Date toCreateDate = null;
                int limit = 500;
                while (true) {
                    List<MatchDto> hasList =
                            matchService.getDtoBySiteIdWithCreateDate(siteId, fromCreateDate, toCreateDate, fromId,
                                    limit);
                    List<TaskPriorityDto> taskList = convertToTasks(hasList);
                    for (MatchDto dto : hasList) {
                        if (fromId < dto.getId()) {
                            fromId = dto.getId();
                        }
                    }
                    total += hasList.size();
                    taskPriorityService.batchInsert(taskList);
                    if (hasList.size() < limit) {
                        break;
                    }
                }
                logger.info("Offer task,siteId:{},total:{}", siteId, total);
            } catch (Exception ex) {
                logger.warn(ExceptionUtils.getStackTrace(ex));
            } finally {
                long cost = System.currentTimeMillis() - start;
                logger.info("CreateTaskTimer is done.cost:{}", cost);
                running = false;
            }
        }

        private List<TaskPriorityDto> convertToTasks(List<MatchDto> hasList) {
            JSONObject argsObject = new JSONObject();
            JSONUtils.put(argsObject, "strategy", getName());
            JSONUtils.put(argsObject, "retry", 0);
            String taskId = UUID.randomUUID().toString();
            JSONUtils.put(argsObject, "bid", taskId);
            Map<Integer, String> siteConfigMap = Maps.newHashMap();
            siteConfigMap.put(1001, "ConfigJdProduct");
            siteConfigMap.put(1002, "ConfigYhdProduct");
            List<TaskPriorityDto> taskList = Lists.newArrayList();
            for (MatchDto hasDto : hasList) {
                String sConfig = siteConfigMap.get(hasDto.getSiteId());
                if (sConfig == null) {
                    continue;
                }
                JSONUtils.put(argsObject, "skuCode", hasDto.getSkuCode());
                String url = hasDto.getProductUrl();
                String type = sConfig;
                TaskPriorityDto newDto = createPriorityDto(url, type, argsObject);
                taskList.add(newDto);
            }
            return taskList;
        }
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
        if (rWritable.getType().endsWith("Product")) {
            JSONObject gObject = JSONUtils.getJSONObject(rWritable.getResult());
            JSONObject rsObject = JSONUtils.getJSONObject(gObject, "rs");
            JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
            try {
                argsObject.remove("name@client");
                argsObject.remove("target");
                argsObject.remove("fromUrl");
                handleResult(rWritable, rsObject, argsObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleResult(ResultWritable rWritable, JSONObject rsObject, JSONObject argsObject) throws Exception {
        JSONArray dataArray = JSONUtils.get(rsObject, "dataList");
        if (dataArray == null) {
            return;
        }
        int len = dataArray.length();
        List<ProductStatDto> dtoList = new ArrayList<ProductStatDto>();
        List<MatchDto> matchDtos = Lists.newArrayList();
        for (int i = 0; i < len; i++) {
            JSONObject dObject = dataArray.getJSONObject(i);
            ProductStatDto statDto = new ProductStatDto();
            dtoList.add(statDto);
            ObjectUtils.copyObject(dObject, statDto);
            addProperties(statDto, dObject, argsObject);

            String skuCode = JSONUtils.getString(argsObject, "skuCode");
            statDto.setSkuCode(skuCode);
            if (StringUtils.isBlank(statDto.getProductCode())) {
                statDto.setProductCode(skuCode.split("_")[1]);
            }
            if (StringUtils.isBlank(skuCode)) {
                continue;
            }
            String sImgUrl = JSONUtils.getString(dObject, "imgUrl");
            MatchDto mDto = new MatchDto();
            mDto.setSkuCode(skuCode);
            mDto.setImgUrl(sImgUrl);
            mDto.setBarCode(JSONUtils.getString(dObject, "barCode"));
            if (StringUtils.isNotBlank(mDto.getImgUrl()) || StringUtils.isNotBlank(mDto.getBarCode())) {
                if (mDto.getImgUrl() == null) {
                    mDto.setImgUrl(StringUtils.EMPTY);
                }
                if (mDto.getBarCode() == null) {
                    mDto.setBarCode(StringUtils.EMPTY);
                }
                matchDtos.add(mDto);
            }
        }
        SpringBeanUtils.getBean(ProductStatService.class).batchSaveProductStatDtos(dtoList);
        SpringBeanUtils.getBean(MatchService.class).batchUpdateDtoBySkuCode(matchDtos);
        logger.info("save statDto,count:" + dtoList.size());

    }

    private void addProperties(ProductStatDto destObject, JSONObject dataObject,
            JSONObject argsObject) throws Exception {
        if (destObject.getSiteId() == null) {
            SiteDto siteDto = SiteCacher.getInstance().getDomainSiteDto(destObject.getProductUrl());
            if (siteDto != null) {
                destObject.setSiteId(siteDto.getId());
            } else {
                destObject.setSiteId(0);
            }
        }
        addShopId(destObject, dataObject, argsObject);
        if (destObject.getShopId() == null) {
            destObject.setShopId(destObject.getSiteId());
        }
        Date newDate = new Date();
        destObject.setCreateTime(newDate);
        destObject.setUpdateTime(newDate);
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
            taskPriorityDto.setLevel(1);
        }
        taskPriorityDto.setParams(paramObject.toString());
        return taskPriorityDto;
    }

    private void addShopId(ProductStatDto destObject, JSONObject dataObject, JSONObject argsObject) throws Exception {
        Integer stockNum = JSONUtils.getInteger(dataObject, "stockNum");
        if (stockNum != null && stockNum < 0) {
            return;
        }
        if (destObject.getShopId() != null) {
            return;
        }
        Integer shopId = JSONUtils.getInteger(argsObject, "shopId");
        if (shopId != null) {
            destObject.setShopId(shopId);
            return;
        }
        String shopUrl = JSONUtils.getString(dataObject, "shopUrl");
        String shopCode = JSONUtils.getString(dataObject, "shopCode");
        String shopName = JSONUtils.getString(dataObject, "shopName");
        if (!StringUtils.isEmpty(shopUrl) && !StringUtils.isEmpty(shopName)) {
            ShopDto shopDto = ShopCacher.getInstance().insertIfAbsent(shopName, shopUrl, shopCode);
            if (shopDto != null) {
                destObject.setShopId(shopDto.getId());
                return;
            }
        }
        String msg = String.format("can not set shopId.args:%s,data:%s", argsObject, dataObject);
        throw new IllegalAccessException(msg);
    }

    @Override
    public void close() throws IOException {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        logger.info("close " + getName() + " strategy..");
    }
}