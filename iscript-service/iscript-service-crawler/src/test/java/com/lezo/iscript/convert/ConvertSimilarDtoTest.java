package com.lezo.iscript.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.BatchIterator;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.utils.ObjectUtils;

@Log4j
public class ConvertSimilarDtoTest {

    @Test
    public void testUpdateBarCodeSimilarDtos() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        SimilarService similarService = SpringBeanUtils.getBean(SimilarService.class);
        List<String> dataList = FileUtils.readLines(new File("src/test/resources/data/code.txt"), "UTF-8");
        BatchIterator<String> it = new BatchIterator<String>(dataList);
        int total = 0;
        while (it.hasNext()) {
            List<SimilarDto> dtoList = similarService.getSimilarDtoBySkuCodes(it.next());
            Map<String, List<SimilarDto>> sCodeMap = Maps.newHashMap();
            for (SimilarDto dto : dtoList) {
                List<SimilarDto> sameList = sCodeMap.get(dto.getSkuCode());
                if (sameList == null) {
                    sameList = Lists.newArrayList();
                    sCodeMap.put(dto.getSkuCode(), sameList);
                }
                sameList.add(dto);
            }
            for (Entry<String, List<SimilarDto>> entry : sCodeMap.entrySet()) {
                for (SimilarDto referDto : entry.getValue()) {
                    if (StringUtils.isBlank(referDto.getBarCode())) {
                        continue;
                    }
                    for (SimilarDto curDto : entry.getValue()) {
                        if (curDto == referDto) {
                            continue;
                        }
                        if (StringUtils.isBlank(referDto.getTokenCategory())
                                && StringUtils.isNotBlank(curDto.getTokenCategory())) {
                            referDto.setTokenCategory(curDto.getTokenCategory());
                        }
                        if (referDto.getShopId() < 1 && curDto.getShopId() > 1) {
                            referDto.setShopId(curDto.getShopId());
                        }
                    }
                }
            }
            similarService.batchUpdateSimilarDtos(dtoList);
            total += dtoList.size();
            System.err.println("update....size:" + dtoList.size() + ",total:" + total);
        }
        cx.close();
        System.err.println("done....total:" + total);
    }

    @Test
    public void testInsertSimilarDtos() throws Exception {
        String[] configs = new String[] { "classpath:spring-config-ds.xml" };
        ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(configs);
        File dirFile =
                new File("/apps/src/codes/lezo/iscript/iscript-service/iscript-service-crawler/data/barcode/suning");
        File[] fileArr = dirFile.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".txt");
            }
        });
        int total = 0;
        List<SimilarDto> similarDtos = Lists.newArrayList();
        for (File f : fileArr) {
            Reader in = new FileReader(f);
            BufferedReader bReader = new BufferedReader(in);
            while (bReader.ready()) {
                final String line = bReader.readLine();
                if (line == null) {
                    break;
                }
                String source = line;
                addSimilars(source, similarDtos);
                if (similarDtos.size() >= 190) {
                    total += similarDtos.size();
                    SpringBeanUtils.getBean(SimilarService.class).batchInsertSimilarDtos(similarDtos);
                    log.info("add data for ,count:" + similarDtos.size() + ",total:" + total);
                    similarDtos.clear();
                }
            }
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(bReader);
        }
        total += similarDtos.size();
        SpringBeanUtils.getBean(SimilarService.class).batchInsertSimilarDtos(similarDtos);
        log.info("add data for ,count:" + similarDtos.size() + ",total:" + total + ",fileCount:" + fileArr.length);
        cx.close();
        System.err.println("done....total:" + total + ",fileCount:" + fileArr.length);
    }

    private void addSimilars(String source, List<SimilarDto> similarDtos) {
        JSONObject rsObject = JSONUtils.getJSONObject(source);
        JSONArray dArray = JSONUtils.get(rsObject, "dataList");
        JSONObject argsObject = new JSONObject();
        if (dArray == null) {
            return;
        }
        Date currentDate = new Date();
        String jobid = "11111";
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
                // e.printStackTrace();
                sDto.setShopId(0);
            }
            if (StringUtils.isNotBlank(sDto.getBarCode())) {
                sDto.setSimilarCode(sDto.getBarCode());
            }
            sDto.setMarketPrice(JSONUtils.getLong(dObj, "productPrice"));
            if (sDto.getMarketPrice() == null) {
                sDto.setMarketPrice(0L);
            }
            sDto.setJobId(jobid);
            sDto.setCreateTime(currentDate);
            sDto.setUpdateTime(sDto.getCreateTime());
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

}
