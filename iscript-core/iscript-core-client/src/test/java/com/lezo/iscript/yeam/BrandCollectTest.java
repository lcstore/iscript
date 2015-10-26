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
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lezo.iscript.utils.JSONUtils;

public class BrandCollectTest {

    @Test
    public void testGetJdBrands() throws Exception {
        String url = "http://list.jd.com/list.html?cat=1320,5019,5020";
        Document rootDoc = getDocument(url);
        List<String> urlList = Lists.newArrayList();
        Elements crumbEls = rootDoc.select("div.crumbs-nav-main div.crumbs-nav-item");
        Elements elements =
                crumbEls.get(crumbEls.size() - 1).select(
                        "div.menu-drop > div.menu-drop-main ul.menu-drop-list a[title][href]");
        for (Element ele : elements) {
            urlList.add(ele.absUrl("href"));
        }

        Set<String> destSet = Sets.newHashSet();
        for (String sUrl : urlList) {
            Document dom = getDocument(sUrl);
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
                destSet.add(sb.toString());
            }
        }
        System.err.println("list:" + elements.size() + ",brand:" + destSet.size());
        FileUtils.writeLines(new File("src/test/resources/data/brand.txt"), destSet);
    }

    @Test
    public void testGetYhdBrands() throws Exception {
        String url =
                "http://list.yhd.com/searchPage/c8644-0-81089/b/a-s1-v4-p1-price-d0-f0d-m1-rt0-pid-mid0-k/?callback=jQuery1113030866268260481766_1445879831626&&type=moreBrand&_=1445879831632";
        Document dom = null;
        String body = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:41.0) Gecko/20100101 Firefox/41.0")
                .maxBodySize(Integer.MAX_VALUE).method(Method.GET).execute().body();
        int index = body.indexOf("{");
        int toIndex = body.lastIndexOf("}");
        String content = body.substring(index, toIndex + 1);
        JSONObject jObject = JSONUtils.getJSONObject(content);
        content = JSONUtils.getString(jObject, "value");
        dom = Jsoup.parse(content, url);
        Elements brandEls = dom.select("li[id^=default_][title]");
        Set<String> destSet = Sets.newHashSet();
        for (Element brandEle : brandEls) {
            String sBrand = brandEle.attr("title");
            sBrand = sBrand.replaceAll("\\(.+?\\)", "");
            sBrand = sBrand.replaceAll("（.+?）", "");
            String[] brandStrings = sBrand.split("/");
            StringBuilder sb = new StringBuilder();
            for (String brand : brandStrings) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(brand.trim().toLowerCase());
            }
            destSet.add(sb.toString());
        }
        System.err.println("list:" + brandEls.size() + ",brand:" + destSet.size());
        FileUtils.writeLines(new File("src/test/resources/data/brand.txt"), destSet);
    }

    private Document getDocument(String sUrl) throws Exception {
        return Jsoup.connect(sUrl)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:41.0) Gecko/20100101 Firefox/41.0")
                .get();
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
