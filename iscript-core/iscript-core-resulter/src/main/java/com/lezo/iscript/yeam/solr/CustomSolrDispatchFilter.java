package com.lezo.iscript.yeam.solr;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.servlet.SolrDispatchFilter;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年11月12日
 */
public class CustomSolrDispatchFilter extends org.apache.solr.servlet.SolrDispatchFilter {

	@Override
	public void init(FilterConfig config) throws ServletException {
		// 设置solr home目录，默认是tomcat的根目录下solr
		String solrHome = System.getProperty("solr.solr.home");
		String solrData = System.getProperty("solr.data.dir");
//		File dataDir = new File(solrHome,);
//		System.setProperty("solr.data.dir", dataDir);
//		try {
//			String dataDir = ResourceUtils.getFile(getDataDir("file:///opt/any/home/searcher/data")).getAbsolutePath();
//			LOG.info("Use solr.data.dir:[{}]", dataDir);
//			// 索引数据存放目录，默认是solr home下data
//			
//		} catch (FileNotFoundException ignored) {
//			throw new ServletException("solr data dir not found");
//		}
		super.init(config); // 然后调用父类的init
		/**
		 * solr是作为一个内嵌的服务，并把它保存到servletContext里面，后面取很方便
		 */
		EmbeddedSolrServer solrServer = new EmbeddedSolrServer(cores, cores.getDefaultCoreName());
		config.getServletContext().setAttribute(SolrDispatchFilter.class.getName(), solrServer);

		// spring context loader
//		ContextLoader loader = new ContextLoaderListener();
//		loader.initWebApplicationContext(config.getServletContext());
	}
}
