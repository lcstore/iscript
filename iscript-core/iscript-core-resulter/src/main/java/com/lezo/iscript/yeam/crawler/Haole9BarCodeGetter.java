package com.lezo.iscript.yeam.crawler;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.http.HttpDriver;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.storage.BarCodeItemStorager;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class Haole9BarCodeGetter implements ConfigParser {
	private static Logger log = Logger.getLogger(Haole9BarCodeGetter.class);
	private HttpDriver httpDriver = HttpDriver.getInstance();
	private BarCodeItemStorager barCodeItemStorager;

	public String getName() {
		return "haole9-barCodeGetter";
	}

	public void init() throws Exception {
//		String url = "http://www.haole9.com/search.php?keywords=&category=0&brand=0&sort=last_update&order=DESC&min_price=0&max_price=0&action=&intro=&goods_type=0&sc_ds=0&outstock=0&page=93";
//		TaskWritable task = new TaskWritable();
//		task.put("url", url);
//		doParse(task);
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject rs = new JSONObject();
		JSONObject argsObject = JSONUtils.getJSONObject(task.getArgs());
		JSONUtils.put(rs, "args", argsObject);
		String url = JSONUtils.getString(argsObject, "url");
		url = StringUtils.isEmpty(url) ? "http://www.haole9.com/search.php?encode=YToxNTp7czo0OiJzb3J0IjtzOjExOiJsYXN0X3VwZGF0ZSI7czo1OiJvcmRlciI7czo0OiJERVNDIjtzOjQ6InBhZ2UiO3M6MToiMSI7czo3OiJkaXNwbGF5IjtzOjQ6InRleHQiO3M6ODoia2V5d29yZHMiO3M6MDoiIjtzOjg6ImNhdGVnb3J5IjtzOjE6IjAiO3M6NToiYnJhbmQiO3M6MToiMCI7czo5OiJtaW5fcHJpY2UiO3M6MToiMCI7czo5OiJtYXhfcHJpY2UiO3M6MToiMCI7czo2OiJhY3Rpb24iO3M6MDoiIjtzOjU6ImludHJvIjtzOjM6Im5ldyI7czoxMDoiZ29vZHNfdHlwZSI7czoxOiIwIjtzOjU6InNjX2RzIjtzOjE6IjAiO3M6ODoib3V0c3RvY2siO3M6MToiMCI7czoxODoic2VhcmNoX2VuY29kZV90aW1lIjtpOjE0MDUyMjA0NTM7fQ=="
				: url;
		httpDriver.execute(new ListGetter(url));
		return rs.toString();
	}

	class ProductGetter implements Runnable {
		private String url;

		public ProductGetter(String url) {
			super();
			this.url = url;
		}

		@Override
		public void run() {
			int maxRetry = 3;
			int retry = 0;
			while (true) {
				HttpGet get = new HttpGet(url);
				try {
					get.addHeader("refer", "http://www.lsd.hk");
					String html = HttpClientUtils.getContent(httpDriver.getClient(), get, "gbk");
					Document dom = Jsoup.parse(html, url);
					BarCodeItemDto dto = new BarCodeItemDto();
					addCtName(dom, dto);
					addName(dom, dto);
					addImgs(dom, dto);
					addAttrs(dom, dto);
					if (BarCodeUtils.isBarCode(dto.getBarCode())) {
						barCodeItemStorager.getStorageBuffer().add(dto);
						int size = barCodeItemStorager.getStorageBuffer().size();
						log.info("dtoList size:" + size);
					} else {
						log.warn("error barCode:" + dto.getBarCode() + ",url:" + this.url);
					}
					break;
				} catch (Exception e) {
					get.abort();
					e.printStackTrace();
					if (++retry <= maxRetry) {
						log.warn("retry url:" + url + ",because:" + e.getClass().getSimpleName());
					}
				}
			}
		}

		private void addImgs(Document dom, BarCodeItemDto dto) {
			Elements elements = dom.select("#focustab li a.act img[src*=goods_img]");
			if (elements.isEmpty()) {
				return;
			}
			dto.setImgUrl(elements.first().absUrl("src"));
		}

		private void addAttrs(Document dom, BarCodeItemDto dto) {
			Elements elements = dom.select("#ECS_FORMBUY");
			if (elements.isEmpty()) {
				return;
			}
			String attrString = elements.text();
			Pattern oReg = Pattern.compile("[0-9]{13,}");
			Matcher matcher = oReg.matcher(attrString);
			if (matcher.find()) {
				dto.setBarCode(matcher.group().trim());
			}
			dto.setProductUrl(this.url);
			dto.setCreateTime(new Date());
			dto.setUpdateTime(dto.getUpdateTime());
		}

		private void addName(Document dom, BarCodeItemDto dto) {
			Elements elements = dom.select("div.goodstxtbox h4.goodName");
			if (!elements.isEmpty()) {
				dto.setProductName(elements.first().text());
			}
		}

		private void addCtName(Document dom, BarCodeItemDto dto) {
			Elements ctAs = dom.select("div.ur_here a[href^=category.php?id=]");
			StringBuilder sb = new StringBuilder();
			for (Element ctEle : ctAs) {
				if (sb.length() < 1) {
					sb.append(ctEle.ownText());
				} else {
					sb.append(";");
					sb.append(ctEle.ownText());
				}
			}
			dto.setProductModel(sb.toString());
		}
	}

	class ListGetter implements Runnable {
		private String url;

		public ListGetter(String url) {
			super();
			this.url = url;
		}

		@Override
		public void run() {
			int maxRetry = 3;
			int retry = 0;
			while (true) {
				HttpGet get = new HttpGet(url);
				try {
					get.addHeader("refer", "http://www.haole9.com");
					String html = HttpClientUtils.getContent(httpDriver.getClient(), get, "UTF-8");
					Document dom = Jsoup.parse(html, url);
					addNextList(dom);
					addProducts(dom);
					break;
				} catch (Exception e) {
					get.abort();
					e.printStackTrace();
					if (++retry <= maxRetry) {
						log.warn("retry url:" + url + ",because:" + e.getClass().getSimpleName());
					}
				}
			}

		}

		private void addProducts(Document dom) {
			Elements oUrlAs = dom.select("a[href^=goods.php?id=]");
			log.info("Get " + oUrlAs.size() + " product from list:" + this.url);
			Set<String> urlSet = new HashSet<String>();
			if (!oUrlAs.isEmpty()) {
				for (Element oUrlEle : oUrlAs) {
					String pUrl = oUrlEle.absUrl("href").trim();
					if (urlSet.contains(pUrl)) {
						continue;
					}
					urlSet.add(pUrl);
					httpDriver.execute(new ProductGetter(pUrl));
				}
			}
			log.info("add " + urlSet.size() + " product,and Queue size:" + httpDriver.getTaskQueue().size());

		}

		private void addNextList(Document dom) {
			Elements oNextAs = dom.select("#pager a.next[href]:contains(下一页)");
			if (!oNextAs.isEmpty()) {
				String listUrl = oNextAs.first().absUrl("href");
				if (!StringUtils.isEmpty(listUrl)) {
					httpDriver.execute(new ListGetter(listUrl));
				}
			}
		}

	}

	public HttpDriver getHttpDriver() {
		return httpDriver;
	}

	public void setHttpDriver(HttpDriver httpDriver) {
		this.httpDriver = httpDriver;
	}

	public void setBarCodeItemStorager(BarCodeItemStorager barCodeItemStorager) {
		this.barCodeItemStorager = barCodeItemStorager;
	}

}
