package com.lezo.iscript.yeam;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;

import com.lezo.iscript.yeam.config.ConfigAnccBarCode;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

@Log4j
public class BarCodeFetcher {

    public static void main(String[] args) throws Exception {
        List<String> brandList = FileUtils.readLines(new File("src/test/resources/data/brand.txt"));
        ConfigParser configParser = new ConfigAnccBarCode();
        Set<String> hasSet = new HashSet<String>(brandList);
        File leaveFile = new File("src/test/resources/data/brand.leave.txt");
        long start = System.currentTimeMillis();
        for (String brand : brandList) {
            TaskWritable task = new TaskWritable();
            task.put("searchKey", brand);
            String rs = configParser.doParse(task);
            log.info(rs);
            hasSet.remove(brand);
            FileUtils.writeLines(leaveFile, hasSet);
        }
        long cost = System.currentTimeMillis() - start;
        long minute = cost / 1000 / 60;
        log.info("done.brand:" + brandList.size() + ",cost:" + cost + ",minute:" + minute);
    }
}
