package com.lezo.iscript.yeam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class UrlTest {

    @Test
    public void testUrl() {
        String url =
                "http://www.yhd.com/ctg/searchPage/c21266-0/b/a-s2-v0-p1-price-d0-f0-m1-rt0-pid-mid0-k%25E7%25A7%2591%25E6%25B2%2583%25E6%2596%25AF|/?callback=jsonp1409631030727";
        try {
            URL oUrl = new URL(url);
            System.out.println(url.substring(130));
            System.out.println(url.replace("|/?", "?"));
            System.out.println(oUrl);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
