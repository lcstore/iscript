package com.lezo.iscript.yeam.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
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

public class JinGouBarCodeGetter implements ConfigParser {
	public static final int BUFFER_SIZE = 200;
	private static Logger log = Logger.getLogger(JinGouBarCodeGetter.class);
	private HttpDriver httpDriver = HttpDriver.getInstance();
	private BarCodeItemService barCodeItemService;

	public JinGouBarCodeGetter() {
	}

	public String getName() {
		return "jingou-barCodeGetter";
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject rs = new JSONObject();
		JSONObject argsObject = JSONUtils.getJSONObject(task.getArgs());
		JSONUtils.put(rs, "args", argsObject);
		String url = JSONUtils.getString(argsObject, "url");
		String listUrl = url;
		while (true) {
			System.out.println("listUrl:" + listUrl);
			String html = doRetryCaller(listUrl);
			if (html == null) {
				break;
			}
			Document dom = Jsoup.parse(html, listUrl);
			List<BarCodeItemDto> dtoList = new ArrayList<BarCodeItemDto>();
			Elements oUrlAs = dom.select("#goodsList div.pro_list ul.piclist li div.pro_list_ad a[href]");
			for (Element urlEle : oUrlAs) {
				String pUrl = urlEle.absUrl("href");
				addProduct(pUrl, dtoList);
			}
			new DataSaver(dtoList).run();
			Elements oNextAs = dom.select("#pager a.next[href]:contains(下一页)");
			if (!oNextAs.isEmpty()) {
				listUrl = oNextAs.first().absUrl("href");
			} else {
				break;
			}
		}
		return rs.toString();
	}

	public String doRetryCaller(String url) {
		int maxRetry = 3;
		int retry = 0;
		while (true) {
			HttpGet get = new HttpGet(url);
			try {
				get.addHeader("refer", "http://www.200804.com");
				return HttpClientUtils.getContent(httpDriver.getClient(), get, "gbk");
			} catch (Exception e) {
				get.abort();
				e.printStackTrace();
				if (++retry <= maxRetry) {
					log.warn("retry url:" + url + ",because:" + e.getClass().getSimpleName());
				}
			}
		}
	}

	private void addProduct(String url, List<BarCodeItemDto> dtoList) {
		int maxRetry = 3;
		int retry = 0;
		while (true) {
			HttpGet get = new HttpGet(url);
			try {
				get.addHeader("refer", "http://www.200804.com");
				String html = HttpClientUtils.getContent(httpDriver.getClient(), get, "gbk");
				Document dom = Jsoup.parse(html, url);
				BarCodeItemDto dto = new BarCodeItemDto();
				addCtName(dom, dto);
				addName(dom, dto);
				addImgs(dom, dto);
				addAttrs(dom, dto);
				dto.setProductUrl(url);
				if (BarCodeUtils.isBarCode(dto.getBarCode())) {
					dtoList.add(dto);
				} else {
					log.warn("error barCode:" + dto.getBarCode() + ",url:" + url);
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
		Elements elements = dom.select("#goods_gallery li a img[src*=goods_img]");
		if (elements.isEmpty()) {
			return;
		}
		dto.setImgUrl(elements.first().absUrl("src"));
	}

	private void addAttrs(Document dom, BarCodeItemDto dto) {
		Elements elements = dom.select("div.promotionMiddleTop div.textInfo ul li");
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
		String barCode = JSONUtils.getString(attrObject, "商品货号");
		barCode = barCode == null ? JSONUtils.getString(attrObject, "商品条码") : barCode;
		barCode = barCode == null ? JSONUtils.getString(attrObject, "条形码") : barCode;
		String brandName = JSONUtils.getString(attrObject, "商品品牌");
		brandName = brandName == null ? JSONUtils.getString(attrObject, "品牌") : brandName;
		Pattern oReg = Pattern.compile("[0-9]{13,}");
		Matcher matcher = oReg.matcher(barCode);
		if (matcher.find()) {
			barCode = matcher.group();
			dto.setBarCode(barCode);
		}

		dto.setProductBrand(brandName);
		attrObject.remove("商品货号");
		attrObject.remove("商品条码");
		attrObject.remove("条形码");
		attrObject.remove("商品品牌");
		attrObject.remove("品牌");
		dto.setProductAttr(attrObject.toString());
		dto.setCreateTime(new Date());
		dto.setUpdateTime(dto.getUpdateTime());
	}

	private void addName(Document dom, BarCodeItemDto dto) {
		Elements elements = dom.select("#ECS_FORMBUY div.promotionMiddle div.promotionMiddleTop h1");
		if (!elements.isEmpty()) {
			dto.setProductName(elements.first().text());
		}
	}

	private void addCtName(Document dom, BarCodeItemDto dto) {
		Elements ctAs = dom.select("div.crumb a[href*=category-]");
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
				// XXX do not to update
				hasCodeSet.add(oldDto.getBarCode());
			}
			for (Entry<String, BarCodeItemDto> entry : dtoMap.entrySet()) {
				if (hasCodeSet.contains(entry.getKey())) {
					continue;
				}
				BarCodeItemDto newDto = entry.getValue();
				insertDtos.add(newDto);
			}
		}
	}

	public void setBarCodeItemService(BarCodeItemService barCodeItemService) {
		this.barCodeItemService = barCodeItemService;
	}
}
