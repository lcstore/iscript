package com.lezo.iscript.yeam.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.ConfigSuningBarCodeMatch;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

@Log4j
public class ConvertBarCodeMatchTest {

    public static void main(String[] args) throws Exception {
        final String destDir =
                "/apps/src/codes/lezo/iscript/iscript-service/iscript-service-crawler/barcode/suning/";
        final ConfigParser configMatch = new ConfigSuningBarCodeMatch();
        final String jobId = System.currentTimeMillis() + "";

        Reader in =
                new FileReader(
                        "/apps/src/codes/lezo/iscript/iscript-service/iscript-service-crawler/data/all.bc.854218.txt");
        int capacity = 5;
        int core = 2;
        ThreadPoolExecutor exec = new ThreadPoolExecutor(core, core,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(capacity));
        BufferedReader bReader = new BufferedReader(in);
        int total = 0;
        while (bReader.ready()) {
            final String line = bReader.readLine();
            if (line == null) {
                break;
            }
            while (exec.getQueue().size() >= capacity) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            total++;
            // if (total < 90530) {
            // continue;
            // }
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    TaskWritable task = new TaskWritable();
                    Map<String, Object> argsMap = new HashMap<String, Object>();
                    argsMap.put("strategy", "BarCodeSimilarStrategy");
                    argsMap.put("jobid", jobId);
                    argsMap.put("bid", jobId);
                    argsMap.put("barCode", line);
                    task.setArgs(argsMap);
                    try {
                        String rsString = configMatch.doParse(task);
                        JSONObject gObject = JSONUtils.getJSONObject(rsString);
                        JSONObject storageObject = JSONUtils.getJSONObject(gObject, "storage");
                        if (storageObject != null) {
                            rsString = storageObject.toString();
                            String name = System.currentTimeMillis() + "_" + Math.abs(rsString.hashCode()) + ".txt";
                            File file = new File(destDir, name);
                            FileUtils.writeStringToFile(file, rsString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (exec.getQueue().size() == capacity) {
                log.info("active:" + exec.getActiveCount() + ",done:"
                        + exec.getCompletedTaskCount() + ",queue:" + exec.getQueue().size() + ",total:" + total);
            }
        }
        IOUtils.closeQuietly(bReader);
        exec.shutdown();
        while (!exec.isTerminated()) {
            log.info("active:" + exec.getActiveCount() + ",done:"
                    + exec.getCompletedTaskCount() + ",queue:" + exec.getQueue().size());
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
