package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.lezo.iscript.IoSeed;
import com.lezo.iscript.IoWatch;
import com.lezo.iscript.cache.SeedCacher;
import com.lezo.iscript.io.IFetcher;
import com.lezo.iscript.io.IoConstants;
import com.lezo.iscript.io.IoWatcher;
import com.lezo.iscript.io.PathFetcher;
import com.lezo.iscript.service.crawler.dto.DataTransferDto;
import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.DataTransferService;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.directory.DirMeta;

public class EarliestMessageHandler {
    private static Logger logger = LoggerFactory.getLogger(EarliestMessageHandler.class);
    private static AtomicBoolean running = new AtomicBoolean(false);
    @Value("#{settings['msg_handle_names'].split(',')}")
    private List<String> nameList;
    @Autowired
    private MessageService messageService;
    @Autowired
    private DataTransferService dataTransferService;

    public void run() {
        if (running.get()) {
            logger.warn("EarliestMessageHandler is running..");
            return;
        }
        long start = System.currentTimeMillis();
        try {
            running.set(true);
            if (CollectionUtils.isEmpty(nameList)) {
                logger.warn("nameList is empty.check the config..");
                return;
            }
            logger.info("start to do EarliestMessageHandler,name size:" + nameList.size());
            List<MessageDto> dtoList = messageService.getEarlyMessageDtoByNameList(nameList, 0);
            List<DirMeta> dirBeans = new ArrayList<DirMeta>();
            for (MessageDto dto : dtoList) {
                if (StringUtils.isEmpty(dto.getDataBucket()) || StringUtils.isEmpty(dto.getDataDomain())) {
                    continue;
                }
                JSONObject mObject = JSONUtils.getJSONObject(dto.getMessage());
                if (mObject == null) {
                    continue;
                }
                Iterator<?> it = mObject.keys();
                while (it.hasNext()) {
                    DirMeta dirBean = new DirMeta();
                    dirBean.setBucket(dto.getDataBucket());
                    dirBean.setCreateTime(dto.getCreateTime());
                    dirBean.setDomain(dto.getDataDomain());
                    dirBean.setType(dto.getName());
                    dirBean.setPid(it.next().toString());
                    dirBeans.add(dirBean);
                }
            }
            List<IoSeed> ioSeeds = new ArrayList<IoSeed>(dirBeans.size());
            IFetcher fetcher = new PathFetcher();
            IoWatcher ioWatcher = IoWatcher.getInstance();
            for (DirMeta dirBean : dirBeans) {
                // cacher.fireEvent(dirBean);
                IoSeed element = new IoSeed();
                element.setBucket(dirBean.getBucket());
                element.setDomain(dirBean.getDomain());
                element.setDataPath(dirBean.toDirPath());
                element.setFetcher(fetcher);
                element.setLevel(IoConstants.LEVEL_PATH);
                IoWatch ioWatch = ioWatcher.getIoWatch(element);
                if (ioWatch != null) {
                    logger.info("doWatch:" + ioWatch.getIoSeed().toKey() + ",total:" + ioWatch.getTotalCount()
                            + ",fetch:"
                            + ioWatch.getFetchCount()
                            + ",toMills:" + ioWatch.getToMills());
                    continue;
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("limit", "0-100");
                element.setParams(params);
                ioSeeds.add(element);
            }
            loadAbort(ioSeeds);
            SeedCacher.getInstance().getQueue().offer(IoConstants.LEVEL_PATH, ioSeeds);
            long cost = System.currentTimeMillis() - start;
            logger.info("add earliest message:" + dirBeans.size() + ",nameCount:" + nameList.size() + ",seedCount:"
                    + ioSeeds.size() + ",cost:" + cost);
        } catch (Exception e) {
            logger.warn("", e);
        } finally {
            running.set(false);
        }
    }

    private void loadAbort(List<IoSeed> ioSeeds) {
        List<String> codeList = new ArrayList<String>();
        DataTransferDto dto = new DataTransferDto();
        Map<String, IoSeed> code2SeedMap = new HashMap<String, IoSeed>();
        for (IoSeed ioSeed : ioSeeds) {
            dto.setDataBucket(ioSeed.getBucket());
            dto.setDataDomain(ioSeed.getDomain());
            dto.setDataPath(ioSeed.getDataPath());
            String code = dto.toDataCode();
            codeList.add(code);
            code2SeedMap.put(code, ioSeed);
        }
        List<DataTransferDto> dtoList = dataTransferService.getDtoByCodeList(codeList);
        for (DataTransferDto hasDto : dtoList) {
            IoSeed ioSeed = code2SeedMap.get(hasDto.getDataCode());
            if (ioSeed != null) {
                logger.info("load abort.path:" + hasDto.getDataPath() + ",param:" + hasDto.getParams());
                JSONObject pObj = JSONUtils.getJSONObject(hasDto.getParams());
                if (pObj != null) {
                    Map<String, String> param = ioSeed.getParams();
                    Iterator<?> it = pObj.keys();
                    while (it.hasNext()) {
                        String key = it.next().toString();
                        param.put(key, JSONUtils.getString(pObj, key));
                    }
                }
            }
        }

    }
}
