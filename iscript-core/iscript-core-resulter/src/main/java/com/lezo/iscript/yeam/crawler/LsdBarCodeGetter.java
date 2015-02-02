package com.lezo.iscript.yeam.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lezo.iscript.service.crawler.dto.BarCodeItemDto;
import com.lezo.iscript.service.crawler.service.BarCodeItemService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.http.HttpDriver;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class LsdBarCodeGetter implements ConfigParser {
	public static final int BUFFER_SIZE = 200;
	private static Logger log = Logger.getLogger(LsdBarCodeGetter.class);
	private HttpDriver httpDriver = HttpDriver.getInstance();
	private List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>(200);
	private BarCodeItemService barCodeItemService;
	private ExecutorService dbCaller = Executors.newFixedThreadPool(1);
	private Timer saveTimer;

	public LsdBarCodeGetter() {
//		saveTimer = new Timer(true);
//		saveTimer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				log.info("@@start to save....");
//				addSaver();
//				log.info("@@end to save....");
//			}
//		}, 60 * 1000);
	}

	public String getName() {
		return "lsd-barCodeGetter";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject rs = new JSONObject();
		JSONObject argsObject = JSONUtils.getJSONObject(task.getArgs());
		JSONUtils.put(rs, "args", argsObject);
		String url = JSONUtils.getString(argsObject, "url");
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
						log.info("dtoList size:" + dtoList.size());
						synchronized (dtoList) {
							dtoList.add(dto);
							if (dtoList.size() >= BUFFER_SIZE) {
								addSaver();
							}
						}
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
			Elements elements = dom.select("#goods_gallery li div a img[src*=goods_img]");
			if (elements.isEmpty()) {
				return;
			}
			dto.setImgUrl(elements.first().absUrl("src"));
		}

		private void addAttrs(Document dom, BarCodeItemDto dto) {
			Elements elements = dom.select("#ECS_FORMBUY ul li");
			if (elements.isEmpty()) {
				return;
			}
			JSONObject attrObject = new JSONObject();
			for (Element ele : elements) {
				String content = ele.text();
				int index = content.indexOf("：");
				index = index < 0 ? content.indexOf(":") : index;
				index = index < 0 ? content.indexOf(" ") : index;
				if (index < 0) {
					continue;
				}
				String key = content.substring(0, index);
				String value = content.substring(index + 1);
				JSONUtils.put(attrObject, key.trim(), value.trim());
			}
			String barCode = JSONUtils.getString(attrObject, "商品条码");
			barCode = barCode == null ? JSONUtils.getString(attrObject, "条形码") : barCode;
			String brandName = JSONUtils.getString(attrObject, "商品品牌");
			brandName = brandName == null ? JSONUtils.getString(attrObject, "品牌") : brandName;
			dto.setBarCode(barCode);
			dto.setProductBrand(brandName);
			dto.setProductUrl(this.url);
			attrObject.remove("商品条码");
			attrObject.remove("条形码");
			attrObject.remove("商品品牌");
			attrObject.remove("品牌");
			dto.setProductAttr(attrObject.toString());
			dto.setCreateTime(new Date());
			dto.setUpdateTime(dto.getUpdateTime());
		}

		private void addName(Document dom, BarCodeItemDto dto) {
			Elements elements = dom.select("#my_info h2");
			if (!elements.isEmpty()) {
				dto.setProductName(elements.first().text());
			}
		}

		private void addCtName(Document dom, BarCodeItemDto dto) {
			Elements ctAs = dom.select("div.title span a[href*=category-]");
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
					get.addHeader("refer", "http://www.lsd.hk");
					String html = HttpClientUtils.getContent(httpDriver.getClient(), get, "gbk");
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
			Elements oUrlAs = dom.select("a[target][href*=goods-]");
			log.info("Get " + oUrlAs.size() + " product from list:" + this.url);
			Set<String> urlSet = new HashSet<String>();
			if (!oUrlAs.isEmpty()) {
				for (Element oUrlEle : oUrlAs) {
					String pUrl = oUrlEle.absUrl("href");
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

	public void addSaver() {
		if (CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		synchronized (dtoList) {
			if (CollectionUtils.isEmpty(dtoList)) {
				return;
			}
			// int len = dtoList.size();
			List<BarCodeItemDto> copyList = new ArrayList<BarCodeItemDto>(dtoList);
			// CollectionUtils.addAll(copyList, dtoList);
			dtoList.clear();
			dbCaller.execute(new DataSaver(copyList));
		}

	}

	class DataSaver implements Runnable {
		List<BarCodeItemDto> copyList;

		public DataSaver(List<BarCodeItemDto> copyList) {
			super();
			this.copyList = copyList;
		}

		@Override
		public void run() {
			if (CollectionUtils.isEmpty(copyList)) {
				return;
			}
			handleDtos(copyList);
		}

		public void handleDtos(List<BarCodeItemDto> BarCodeItemDtos) {
			List<BarCodeItemDto> insertDtos = new ArrayList<BarCodeItemDto>();
			List<BarCodeItemDto> updateDtos = new ArrayList<BarCodeItemDto>();
			doAssort(BarCodeItemDtos, insertDtos, updateDtos);
			barCodeItemService.batchInsertBarCodeItemDtos(insertDtos);
			barCodeItemService.batchUpdateBarCodeItemDtos(updateDtos);
			log.info("save BarCodeItemDto.insert:" + insertDtos.size() + ",update:" + updateDtos.size());
		}

		private void doAssort(List<BarCodeItemDto> barCodeItemDtos, List<BarCodeItemDto> insertDtos,
				List<BarCodeItemDto> updateDtos) {
			Map<String, BarCodeItemDto> dtoMap = new HashMap<String, BarCodeItemDto>();
			for (BarCodeItemDto dto : barCodeItemDtos) {
				String key = dto.getBarCode();
				dtoMap.put(key, dto);
			}
			List<String> barCodeList = new ArrayList<String>(dtoMap.keySet());
			List<BarCodeItemDto> hasDtos = barCodeItemService.getBarCodeItemDtos(barCodeList);
			Set<String> hasCodeSet = new HashSet<String>();
			for (BarCodeItemDto oldDto : hasDtos) {
				String key = oldDto.getBarCode();
				BarCodeItemDto newDto = dtoMap.get(key);
				hasCodeSet.add(oldDto.getBarCode());
				newDto.setId(oldDto.getId());
				handleEmptyFileds(newDto, oldDto);
				updateDtos.add(newDto);
			}
			for (Entry<String, BarCodeItemDto> entry : dtoMap.entrySet()) {
				if (hasCodeSet.contains(entry.getKey())) {
					continue;
				}
				BarCodeItemDto newDto = entry.getValue();
				insertDtos.add(newDto);
			}
		}

		private void handleEmptyFileds(BarCodeItemDto newDto, BarCodeItemDto oldDto) {
			if (StringUtils.isEmpty(newDto.getProductName())) {
				newDto.setBarCode(oldDto.getProductName());
			}
			if (StringUtils.isEmpty(newDto.getProductBrand())) {
				newDto.setImgUrl(oldDto.getProductBrand());
			}
		}
	}

	public void setBarCodeItemService(BarCodeItemService barCodeItemService) {
		this.barCodeItemService = barCodeItemService;
	}

	public HttpDriver getHttpDriver() {
		return httpDriver;
	}

	public void setHttpDriver(HttpDriver httpDriver) {
		this.httpDriver = httpDriver;
	}

}
