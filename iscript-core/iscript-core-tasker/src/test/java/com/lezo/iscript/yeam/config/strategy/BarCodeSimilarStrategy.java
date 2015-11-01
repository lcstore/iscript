package com.lezo.iscript.yeam.config.strategy;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.ObjectUtils;
import com.lezo.iscript.yeam.strategy.ResultStrategy;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class BarCodeSimilarStrategy implements ResultStrategy, Closeable {
    private static Logger logger = LoggerFactory.getLogger(BarCodeSimilarStrategy.class);
    private Timer timer;

    public BarCodeSimilarStrategy() {
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
        JSONObject argsObject = JSONUtils.getJSONObject(gObject, "args");
        if (dArray == null) {
            return;
        }
        Date currentDate = new Date();
        List<SimilarDto> similarDtos = Lists.newArrayList();
        String barCode = JSONUtils.getString(argsObject, "barCode");
        String jobid = JSONUtils.getString(argsObject, "jobid");
        for (int i = 0; i < dArray.length(); i++) {
            JSONObject dObj = null;
            try {
                dObj = dArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dObj == null) {
                continue;
            }
            SimilarDto sDto = new SimilarDto();
            try {
                ObjectUtils.copyObject(dObj, sDto);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            try {
                addShopId(sDto, dObj, argsObject);
            } catch (Exception e) {
                e.printStackTrace();
                sDto.setShopId(0);
            }
            sDto.setSimilarCode(barCode);
            sDto.setJobId(jobid);
            sDto.setCreateTime(currentDate);
            sDto.setUpdateTime(sDto.getCreateTime());
            sDto.setBarCode(barCode);
            sDto.setSkuCode(sDto.getSiteId() + "_" + sDto.getProductCode());
            sDto.setArbiterId(SimilarDto.ARBITER_BARCODE);
            similarDtos.add(sDto);
        }
        for (Field field : SimilarDto.class.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(String.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                for (SimilarDto sDto : similarDtos) {
                    try {
                        if (field.get(sDto) == null) {
                            field.set(sDto, StringUtils.EMPTY);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        SpringBeanUtils.getBean(SimilarService.class).batchInsertSimilarDtos(similarDtos);
        logger.info("add data for type:" + rWritable.getType() + ",count:" + similarDtos.size());

    }

    private void addShopId(SimilarDto destObject, JSONObject dataObject, JSONObject argsObject) throws Exception {
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
