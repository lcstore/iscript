package com.lezo.iscript.yeam.solr;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.servlet.SolrDispatchFilter;

import com.lezo.iscript.spring.context.SpringBeanUtils;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年11月12日
 */
public class CustomSolrDispatchFilter extends org.apache.solr.servlet.SolrDispatchFilter {

	@Override
	public void init(FilterConfig config) throws ServletException {
		super.init(config); // 然后调用父类的init
		/**
		 * solr是作为一个内嵌的服务，并把它保存到servletContext里面，后面取很方便
		 */
		EmbeddedSolrServer solrServer = new EmbeddedSolrServer(cores, cores.getDefaultCoreName());
		config.getServletContext().setAttribute(SolrDispatchFilter.class.getName(), solrServer);
		EmbeddedSolrServerHolder.getInstance().setEmbeddedSolrServer(solrServer);
		// spring context loader
		// ContextLoader loader = new ContextLoaderListener();
		// loader.initWebApplicationContext(config.getServletContext());
	}
}
