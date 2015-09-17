package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.lezo.iscript.IoSeed;
import com.lezo.iscript.IoWatch;
import com.lezo.iscript.io.IoWatcher;
import com.lezo.iscript.service.crawler.dto.DataTransferDto;
import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.DataTransferService;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.utils.JSONUtils;

public class DataMessageHandler {
    private static Logger logger = LoggerFactory.getLogger(DataMessageHandler.class);
    private static AtomicBoolean running = new AtomicBoolean(false);
    @Autowired
    private MessageService messageService;
    @Autowired
    private DataTransferService dataTransferService;

    public void run() {
        if (running.get()) {
            logger.warn("DataMessageHandler is running..");
            return;
        }
        long start = System.currentTimeMillis();
        try {
            running.set(true);
            logger.info("start to do DataMessageHandler.");
            ConcurrentHashMap<String, IoWatch> watchMap = IoWatcher.getInstance().getWatchMap();
            Iterator<Entry<String, IoWatch>> it = watchMap.entrySet().iterator();
            List<IoWatch> doingList = new ArrayList<IoWatch>();
            while (it.hasNext()) {
                Entry<String, IoWatch> entry = it.next();
                IoWatch ioWatch = entry.getValue();
                Date beforeTime = new Date(ioWatch.getToMills());
                Calendar c = Calendar.getInstance();
                c.setTime(beforeTime);
                c.add(Calendar.MINUTE, 2);
                beforeTime = c.getTime();
                List<String> nameList = new ArrayList<String>();
                IoSeed ioSeed = ioWatch.getIoSeed();
                nameList.add(ioSeed.getType());
                messageService.updateStatusByCreateTime(nameList, ioSeed.getBucket(), ioSeed.getDomain(),
                        beforeTime, MessageDto.STATUS_NEW, MessageDto.STATUS_DONE);
                if (ioWatch.isDone() && ioWatch.getTotalCount() <= (ioWatch.getFetchCount() + ioWatch.getErrorCount())) {
                    logger.info("done.key:" + ioSeed.toKey() + ",total:" + ioWatch.getTotalCount() + ",fetch:"
                            + ioWatch.getFetchCount());
                    it.remove();
                } else {
                    doingList.add(entry.getValue());
                    logger.info("key:" + ioSeed.toKey() + ",total:" + ioWatch.getTotalCount() + ",fetch:"
                            + ioWatch.getFetchCount());
                }
            }
            saveDoingWatch(doingList);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            logger.warn("handle DirMeta,cost:" + cost + ",cause:", e);
        } finally {
            running.set(false);
        }
    }

    private void saveDoingWatch(List<IoWatch> doingList) {
        if (CollectionUtils.isEmpty(doingList)) {
            return;
        }
        Iterator<IoWatch> it = doingList.iterator();
        List<DataTransferDto> dtoList = new ArrayList<DataTransferDto>();
        Date currentDate = new Date();
        while (it.hasNext()) {
            IoWatch ioWatch = it.next();
            IoSeed ioSeed = ioWatch.getIoSeed();
            JSONObject pObject = JSONUtils.getJSONObject(ioSeed.getParams());
            DataTransferDto dto = new DataTransferDto();
            dto.setParams(pObject == null ? null : pObject.toString());
            dto.setDataBucket(ioSeed.getBucket());
            dto.setDataDomain(ioSeed.getDomain());
            dto.setDataPath(ioSeed.getDataPath());
            dto.setDataCount(ioWatch.getFetchCount());
            dto.setTotalCount(ioWatch.getTotalCount());
            dto.setDataCode(dto.toDataCode());
            dto.setCreateTime(currentDate);
            dto.setUpdateTime(dto.getUpdateTime());
            dtoList.add(dto);
        }
        // save data for transfer
        dataTransferService.batchInsertOrUpdateByKey(dtoList);
    }

}
