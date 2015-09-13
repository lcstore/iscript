package com.lezo.iscript.io;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

import com.lezo.iscript.IoSeed;
import com.lezo.iscript.IoWatch;

@Getter
public class IoWatcher {
    private static final IoWatcher INSTANCE = new IoWatcher();
    public ConcurrentHashMap<String, IoWatch> watchMap = new ConcurrentHashMap<String, IoWatch>();

    private IoWatcher() {
    }

    public static IoWatcher getInstance() {
        return INSTANCE;
    }

    public IoWatch addDir(IoSeed ioSeed, int newCount, boolean isDone) {
        String key = convertPath(ioSeed, ioSeed.getDataPath());
        IoWatch ioWatch = getOrNewValue(key);
        synchronized (ioWatch) {
            if (ioWatch.getIoSeed() == null) {
                ioWatch.setIoSeed(ioSeed);
            }
            ioWatch.setTotalCount(ioWatch.getTotalCount() + newCount);
            ioWatch.setDone(isDone);
        }
        return ioWatch;
    }

    private String convertPath(IoSeed ioSeed, String dirPath) {
        if (ioSeed.getDomain().equals("baidu.com") && !dirPath.startsWith("/apps/")) {
            dirPath = "/apps/" + ioSeed.getBucket() + "/" + dirPath;
        }
        return dirPath;
    }

    public IoWatch getIoWatch(IoSeed ioSeed) {
        String key = convertPath(ioSeed, ioSeed.getDataPath());
        return watchMap.get(key);
    }

    private IoWatch getOrNewValue(String key) {
        IoWatch ioWatch = watchMap.get(key);
        if (ioWatch == null) {
            synchronized (INSTANCE) {
                ioWatch = watchMap.get(key);
                if (ioWatch == null) {
                    ioWatch = new IoWatch();
                    watchMap.put(key, ioWatch);
                }
            }

        }
        return ioWatch;
    }

    public IoWatch addFile(IoSeed ioSeed) {
        File file = new File(ioSeed.getDataPath());
        String key = convertPath(ioSeed, file.getParent());
        IoWatch ioWatch = getOrNewValue(key);
        synchronized (ioWatch) {
            if (ioWatch.getIoSeed() == null) {
                ioSeed.setDataPath(key);
                ioWatch.setIoSeed(ioSeed);
            }
            ioWatch.setFetchCount(ioWatch.getFetchCount() + 1);
            ioWatch.setToMills(getStampByName(ioSeed.getDataPath()));
        }
        return ioWatch;
    }

    private long getStampByName(String path) {
        char dotChar = '.';
        int toIndex = path.lastIndexOf(dotChar);
        int fromIndex = path.lastIndexOf(dotChar, toIndex - 1);
        String strStamp = path.substring(fromIndex + 1, toIndex);
        Long fileStamp = Long.valueOf(strStamp);
        return fileStamp;
    }

    public IoWatch delWatch(String key) {
        return watchMap.remove(key);
    }
}
