package com.lezo.iscript.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ConvertBarCodeTest {

    @Test
    public void testGetBarCode() throws Exception {
        Reader reader = new FileReader("/Users/lezo/Downloads/jd.all.UPC");
        BufferedReader bReader = new BufferedReader(reader);
        Set<String> barCodeSet = Sets.newHashSet();
        while (bReader.ready()) {
            String line = bReader.readLine();
            if (line == null) {
                break;
            }
            String[] uArr = line.split("\t");
            String sBarCode = uArr[0];
            barCodeSet.add(sBarCode);
        }
        IOUtils.closeQuietly(bReader);
        FileUtils.writeLines(new File("data/jd.bc.txt"), barCodeSet);
        System.err.println("done,size:" + barCodeSet.size());

    }

    @Test
    public void testDistinctBarCode() throws Exception {
        List<String> bcPaths = Lists.newArrayList();
        bcPaths.add("/Users/lezo/Downloads/bCode536333.log");
        bcPaths.add("/Users/lezo/Downloads/jd.all.UPC");
        bcPaths.add("/Users/lezo/Downloads/dangdang.all.UPC");
        bcPaths.add("/Users/lezo/Downloads/suning.all.UPC");
        Set<String> barCodeSet = Sets.newHashSet();
        for (String path : bcPaths) {
            Reader reader = new FileReader(path);
            BufferedReader bReader = new BufferedReader(reader);
            int count = 0;
            while (bReader.ready()) {
                String line = bReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("[")) {
                    JSONArray dArray = new JSONArray(line);
                    for (int i = 0; i < dArray.length(); i++) {
                        barCodeSet.add(dArray.getString(i));
                        count++;
                    }
                } else {
                    String[] uArr = line.split("\t");
                    String sBarCode = uArr[0];
                    barCodeSet.add(sBarCode);
                    count++;
                }
            }
            IOUtils.closeQuietly(bReader);
            System.err.println("path:" + path + ",count:" + count + ",total:" + barCodeSet.size());
        }
        FileUtils.writeLines(new File("data/all.bc.txt"), barCodeSet);
        System.err.println("done,size:" + barCodeSet.size());

    }
}
