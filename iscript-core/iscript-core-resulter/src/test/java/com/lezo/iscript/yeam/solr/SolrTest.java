package com.lezo.iscript.yeam.solr;

import java.io.StringWriter;
import java.lang.reflect.Field;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public class SolrTest {
    EmbeddedSolrServer server;

    @Before
    public void startup() throws Exception {
        System.setProperty("solr.solr.home", "/apps/src/istore/solr_home");
        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        CoreContainer coreContainer = initializer.initialize();
        server = new EmbeddedSolrServer(coreContainer, "collection1");
    }

    @Test
    public void testSolrAdd() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setProductName("OSCO 时尚牛皮保暖棉靴男靴子男士短靴军靴作战靴工装靴男户外靴2511 咖啡色 43");
        dto.setProductUrl("http://item.jd.com/1321297488.html");
        dto.setProductCode("1321297488");
        dto.setProductBrand("OSCO");
        dto.setMarketPrice(450F);
        dto.setShopId(100111);
        dto.setSiteId(1001);
        SolrInputDocument doc = new SolrInputDocument();
        for (Field field : dto.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            doc.addField(field.getName(), field.get(dto));
        }
        server.add(doc);
        server.commit();
    }

    @Test
    public void testSolrQuery() throws Exception {
        String queryStr = "*:*";
        queryStr = "id:1";
        queryStr = "productCode:1321297488";
        queryStr = "copyText:OSCO";
        queryStr = "copyText:雨伞/雨具";
        // queryStr = "copyText:Alfredo";
        // queryStr = "m_skuCode:1001_734860";
        queryStr = "*:*";
        // queryStr = "copyText:牛奶";
        SolrQuery solrQuery = new SolrQuery("{!frange l=0.4}query($qq)");
        solrQuery.set("qq", queryStr);
        solrQuery.setFields("*", "score");
        solrQuery.setStart(0);
        solrQuery.setRows(10);
        System.err.println("getQuery:" + solrQuery.getQuery());
        System.err.println("toString:" + solrQuery.toString());
        QueryResponse respone = server.query(solrQuery);
        SolrDocumentList resultList = respone.getResults();
        System.out.println(resultList.size());

        System.err.println(",result:" + resultList);
        SolrQueryResult sqr = new SolrQueryResult();
        SolrDocumentList docs = respone.getResults();
        sqr.setDocs(docs);
        sqr.setNumFound(docs.getNumFound());
        sqr.setStart(docs.getStart());
        sqr.setMaxScore(docs.getMaxScore());

        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, sqr);
        System.err.println(",result:" + writer.toString());
        // if (!resultList.isEmpty()) {
        // for (int i = 0; i < resultList.size(); i++) {
        // System.out.print(resultList.get(i).get("siteId") + " ");
        // System.out.print(resultList.get(i).get("productCode") + " ");
        // System.out.print(resultList.get(i).get("productName") + " ");
        // System.out.println(resultList.get(i).get("categoryNav") + " ");
        // }
        // }
    }

    @Test
    public void testSolrQueryStat() throws Exception {
        String queryStr = "*:*";
        queryStr = "*:*";
        SolrQuery solrQuery = new SolrQuery("{!frange l=0.4}query($qq)");
        solrQuery = new SolrQuery();
        solrQuery.set("q", queryStr);
        solrQuery.setGetFieldStatistics("itemCode");
        solrQuery.setGetFieldStatistics(true);
        // solrQuery.setFields("matchCode", "productName", "skuCode");
        // solrQuery.setFields("*", "score");
        // solrQuery.setStart(0);
        solrQuery.setRows(0);
        System.err.println("getQuery:" + solrQuery.getQuery());
        System.err.println("toString:" + solrQuery.toString());
        QueryResponse respone = server.query(solrQuery);

        SolrDocumentList resultList = respone.getResults();

        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, respone.getFieldStatsInfo());
        System.err.println("size:" + resultList.size());
        System.err.println("result:" + writer.toString());
    }

    /**
     * distinct
     * 
     * @throws Exception
     */
    @Test
    public void testSolrQueryFact() throws Exception {
        String queryStr = "*:*";
//        queryStr = "巧克力";
        // SolrQuery solrQuery = new SolrQuery("{!frange l=0.4}query($qq)");
        // SolrQuery solrQuery = new SolrQuery(queryStr);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setParam("q", queryStr);
        solrQuery.setFacet(true);
        solrQuery.setFacetMinCount(1);
        solrQuery.addFacetField("itemCode");
        solrQuery.setRows(0);
        System.err.println("toString:" + solrQuery.toString());
        QueryResponse respone = server.query(solrQuery);

        System.err.println("result:" + respone.getFacetField("itemCode"));
    }

    @Test
    public void testSolrQueryOR() throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        // solrQuery.setParam("q", "(skuCode:1001_734855)OR(skuCode:1001_734860)");
        solrQuery.setParam("q", "matchCode:172673569");
        System.err.println("toString:" + solrQuery.toString());
        QueryResponse respone = server.query(solrQuery);

        System.err.println("result:" + respone.getResponse());
    }

    @Test
    public void testSolrQueryRange() throws Exception {
        SolrQuery solrQuery = new SolrQuery("matchCode:172673569");
        // solrQuery.setParam("q", "productPrice:[900 TO * ]");
        System.err.println("toString:" + solrQuery.toString());
        QueryResponse respone = server.query(solrQuery);

        System.err.println("result:" + respone.getResponse());
    }

    @Test
    public void testSolrDelete() throws Exception {
        String queryStr = "*:*";
        server.deleteByQuery(queryStr);
        server.commit();
        server.optimize();
    }

    @Test
    public void testSolrConfig() {
        Field[] fields = ProductStatDto.class.getDeclaredFields();
        String source = "<field name=\"productCode\" type=\"string\" indexed=\"true\" stored=\"true\" />";
        for (Field f : fields) {
            String name = f.getName();
            String destString = source.replace("productCode", name);
            System.out.println(destString);
        }
    }
}
