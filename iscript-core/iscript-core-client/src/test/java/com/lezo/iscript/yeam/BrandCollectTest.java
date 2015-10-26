package com.lezo.iscript.yeam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.google.common.collect.Lists;

public class BrandCollectTest {

    @Test
    public void testGetJdBrands() throws Exception {

        List<String> urlList = Lists.newArrayList();
        for (int i = 0; i <= 4; i++) {
            urlList.add("http://list.jd.com/1320-5019-502" + i + ".html");
        }

        List<String> destList = Lists.newArrayList();
        for (String sUrl : urlList) {
            Document dom = Jsoup.connect(sUrl)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:41.0) Gecko/20100101 Firefox/41.0")
                    .get();

            Elements brandEls = dom.select("li[id^=brand-] a[href][title]");
            for (Element brandEle : brandEls) {
                String sBrand = brandEle.attr("title");
                sBrand = sBrand.replaceAll("（", "(");
                sBrand = sBrand.replaceAll("）", "");
                sBrand = sBrand.replaceAll("\\)", "");
                String[] brandStrings = sBrand.split("\\(");
                StringBuilder sb = new StringBuilder();
                for (String brand : brandStrings) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(brand);
                }
                destList.add(sb.toString());
            }
        }
        FileUtils.writeLines(new File("src/test/resources/data/brand.txt"), destList);
    }

    @Test
    public void testDns() throws Exception {
        InetAddress[] intAddrs = InetAddress.getAllByName("www.ip138.com");
        for (InetAddress addr : intAddrs) {
            System.err.println(addr);
        }
    }

    @Test
    public void testPort() throws Exception {
        System.err.println(0xFFFF);
    }

    @Test
    public void testReadFile() throws Exception {
        Reader in = new FileReader("/Users/lezo/Downloads/T_MATCH.sql");
        BufferedReader br = new BufferedReader(in);
        Writer out = new FileWriter("/Users/lezo/Downloads/T_MATCH-insert.sql");
        BufferedWriter bWriter = new BufferedWriter(out);
        int count = 0;
        while (br.ready()) {
            System.err.println(br.readLine());
            String line = br.readLine();
            if (line.startsWith("INSERT INTO")) {
                bWriter.append(line);
                bWriter.append("\n");
                count++;
                System.err.println("count:" + count);
            }
        }
        bWriter.flush();
        IOUtils.closeQuietly(bWriter);
        IOUtils.closeQuietly(br);
        System.err.println("done....");
    }
}
