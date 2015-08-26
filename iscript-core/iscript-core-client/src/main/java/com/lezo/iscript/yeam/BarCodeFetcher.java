package com.lezo.iscript.yeam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;

import com.lezo.iscript.yeam.config.AnccBarCodeFetcher;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

@Log4j
public class BarCodeFetcher {

    public static void main(String[] args) throws Exception {
        String srcPath = System.getProperty("src", "src/test/resources/data/brand.txt");
        String remainPath = System.getProperty("remain", "src/test/resources/data/brand.leave.txt");
        List<String> brandList = FileUtils.readLines(new File(srcPath));
        ConfigParser configParser = new AnccBarCodeFetcher();
        List<String> remianList = new ArrayList<String>(brandList);
        File leaveFile = new File(remainPath);
        long start = System.currentTimeMillis();
        for (String brand : brandList) {
            TaskWritable task = new TaskWritable();
            task.put("searchKey", brand);
            String rs = configParser.doParse(task);
            log.info(rs);
            remianList.remove(brand);
            FileUtils.writeLines(leaveFile, remianList);
        }
        long cost = System.currentTimeMillis() - start;
        long minute = cost / 1000 / 60;
        log.info("done.brand:" + brandList.size() + ",cost:" + cost + ",minute:" + minute);
    }
}
