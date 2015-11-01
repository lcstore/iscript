package com.lezo.iscript.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

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
}
