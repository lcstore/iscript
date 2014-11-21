package com.lezo.iscript.yeam.solr;

import java.lang.reflect.Field;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.junit.Test;

import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;

public class SolrTest {

	@Test
	public void testSolrAdd() throws Exception {
		System.setProperty("solr.solr.home", "E:/lezo/codes/solr_home/");
		System.setProperty("solr.solr.home", "D:/codes/lezo/solr_home/");

		CoreContainer.Initializer initializer = new CoreContainer.Initializer();
		CoreContainer coreContainer = initializer.initialize();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");

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
		System.setProperty("solr.solr.home", "E:/lezo/codes/solr_home/");
		System.setProperty("solr.solr.home", "D:/codes/lezo/solr_home/");

		CoreContainer.Initializer initializer = new CoreContainer.Initializer();
		CoreContainer coreContainer = initializer.initialize();
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "collection1");

		String queryStr = "*:*";
		queryStr = "id:1";
		queryStr = "productCode:1321297488";
		queryStr = "copyText:OSCO";
		SolrQuery solrQuery = new SolrQuery(queryStr);
		solrQuery.setStart(0);
		solrQuery.setRows(1000);
		QueryResponse respone = server.query(solrQuery);
		SolrDocumentList resultList = respone.getResults();
		System.out.println(resultList.size());
		if (!resultList.isEmpty()) {
			System.out.println(resultList.get(0).get("productCode"));
			System.out.println(resultList.get(0).get("productName"));
		}
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
