package com.lezo.iscript.yeam.crawler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.ProductStatDto;
import com.lezo.iscript.service.crawler.dto.ShopDto;
import com.lezo.iscript.service.crawler.dto.SimilarDto;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.utils.ShopCacher;
import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class HuihuiSimilarSearcher {
	private static Logger log = LoggerFactory.getLogger(HuihuiSimilarSearcher.class);
	private static volatile boolean running = false;
	private static final ConfigParser parser = new HuihuiSimilar();
	@Autowired
	private ProductService productService;
	@Autowired
	private SimilarDtoStorageCaller similarDtoStorageCaller;

	public void run() {
		if (running) {
			log.warn("HuihuiSimilarSearcher is working...");
			return;
		}
		try {
			running = true;
			doSimilarSearch();
		} catch (Exception ex) {
			log.warn("", ex);
			ex.fillInStackTrace();
		} finally {
			running = false;
		}
	}

	private void doSimilarSearch() {
		TaskWritable task = new TaskWritable();
		Integer shopId = 1001;
		Long fromId = 0L;
		int limit = 200;
		while (true) {
			List<ProductDto> productDtos = productService.getProductDtosFromId(fromId, limit, shopId);
			for (ProductDto dto : productDtos) {
				if (fromId < dto.getId()) {
					fromId = dto.getId();
				}
				task.put("shopId", dto.getShopId());
				task.put("url", dto.getProductUrl());
				task.put("name", dto.getProductName());
				task.put("code", dto.getProductCode());
				task.put("price", dto.getMarketPrice());
				task.put("barCode", dto.getBarCode());
				task.put("imgUrl", dto.getImgUrl());
				String rs = doRetryCall(task);
				if (StringUtils.isEmpty(rs)) {
					log.warn("can not get similar,code:" + dto.getProductCode() + ",url:" + dto.getProductUrl()
							+ ",name:" + dto.getProductName());
					continue;
				}
				JSONObject rsObject = JSONUtils.getJSONObject(rs);
				handleResult(rsObject);
			}
			if (productDtos.size() < limit) {
				break;
			}
		}

	}

	private void handleResult(JSONObject rsObject) {
		JSONObject argsObject = JSONUtils.get(rsObject, "args");
		rsObject = JSONUtils.getJSONObject(JSONUtils.get(rsObject, "rs"));
		if (rsObject == null) {
			log.warn("no similar to:" + argsObject);
			return;
		}
		JSONArray oList = JSONUtils.get(rsObject, "urlPriceList");
		if (oList == null || oList.length() < 1) {
			log.warn("no similar data list...");
			return;
		}
		Long similarCode = System.currentTimeMillis();
		List<SimilarDto> similarDtos = new ArrayList<SimilarDto>();

		SimilarDto dto = new SimilarDto();
		dto.setSiteId(JSONUtils.getInteger(argsObject, "shopId"));
		dto.setProductCode(JSONUtils.getString(argsObject, "code"));
		dto.setProductName(JSONUtils.getString(argsObject, "name"));
		dto.setProductUrl(JSONUtils.getString(argsObject, "url"));
		dto.setProductPrice(JSONUtils.getFloat(argsObject, "price"));
		dto.setImgUrl(JSONUtils.getString(argsObject, "imgUrl"));
		dto.setBarCode(JSONUtils.getString(argsObject, "barCode"));
		dto.setSource(parser.getName());
		similarDtos.add(dto);
		for (int i = 0; i < oList.length(); i++) {
			try {
				JSONObject itemObject = oList.getJSONObject(i);
				handleItem(itemObject, similarDtos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		addSimilarCode(similarDtos, similarCode);
		similarDtoStorageCaller.handleDtos(similarDtos);

	}

	private void addSimilarCode(List<SimilarDto> similarDtos, Long similarCode) {
		for (SimilarDto dto : similarDtos) {
			dto.setSimilarCode(similarCode);
		}
	}

	private void handleItem(JSONObject itemObject, List<SimilarDto> similarDtos) throws Exception {
		JSONArray itemsArray = JSONUtils.get(itemObject, "items");
		if (itemsArray.length() < 1) {
			return;
		}
		String domain = JSONUtils.getString(itemObject, "site");
		domain = domain.replace("360buy", "jd");
		domain = domain.replace("yihaodian", "yhd");
		String domainUrl = String.format("http://www.%s", domain);
		ShopDto shopDto = ShopCacher.getInstance().getDomainShopDto(domainUrl);
		Integer shopId = 0;
		if (shopDto != null) {
			shopId = shopDto.getId();
		}
		for (int i = 0; i < itemsArray.length(); i++) {
			JSONObject mObject = itemsArray.getJSONObject(i);
			SimilarDto dto = new SimilarDto();
			dto.setSiteId(shopId);
			dto.setProductName(JSONUtils.getString(mObject, "name"));
			dto.setProductUrl(toDestUrl(JSONUtils.getString(mObject, "url")));
			String code = CodeParser.getCodeFromUrl(dto.getProductUrl());
			if (StringUtils.isEmpty(code)) {
				log.warn("can not get code from:" + dto.getProductUrl());
			}
			dto.setProductCode(code);
			dto.setProductPrice(JSONUtils.getFloat(mObject, "price"));
			dto.setSource(parser.getName());
			dto.setCreateTime(new Date());
			dto.setUpdateTime(dto.getCreateTime());
			similarDtos.add(dto);
		}
	}

	private String toDestUrl(String url) {
		String mark = "http:";
		try {
			url = URLDecoder.decode(url, "UTF-8");
			int index = url.lastIndexOf(mark);
			index = index < 0 ? 0 : index;
			url = url.substring(index);
			url = url.replace("360buy", "jd");
			return url;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean isChange(ProductStatDto oldDto, ProductStatDto newDto) {
		if (!isSameObject(oldDto.getProductPrice(), newDto.getProductPrice())) {
			return true;
		}
		if (!isSameObject(oldDto.getMarketPrice(), newDto.getMarketPrice())) {
			return true;
		}
		if (!isSameObject(oldDto.getSoldNum(), newDto.getSoldNum())) {
			return true;
		}
		if (!isSameObject(oldDto.getStockNum(), newDto.getStockNum())) {
			return true;
		}
		if (!isSameObject(oldDto.getCommentNum(), newDto.getCommentNum())) {
			return true;
		}
		return false;
	}

	public boolean isSameObject(Object lObject, Object rObject) {
		if (lObject == null && rObject == null) {
			return true;
		} else if (lObject == null && rObject != null) {
			return false;
		}
		return lObject.equals(rObject);
	}

	private String doRetryCall(TaskWritable task) {
		int retry = 0;
		while (++retry < 3) {
			try {
				return parser.doParse(task);
			} catch (Exception e) {
				retry++;
				log.info("retry:" + retry + ",args:" + new JSONObject(task.getArgs()));
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public static void main(String[] args) throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml", "classpath:spring/spring-bean-resulter.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		HuihuiSimilarSearcher searcher = SpringBeanUtils.getBean(HuihuiSimilarSearcher.class);
		searcher.run();

		// String productUrl = "http://item.jd.com/856850.html";
		// String productName = "中国台湾进口素手浣花黑糖棒棒糖140g";
		// ConfigParser similarParser = new HuihuiSimilar();
		// TaskWritable task = new TaskWritable();
		// task.put("url", productUrl);
		// task.put("name", productName);
		// String result = similarParser.doParse(task);
		// System.out.println(result);
	}

	public void setSimilarDtoStorageCaller(SimilarDtoStorageCaller similarDtoStorageCaller) {
		this.similarDtoStorageCaller = similarDtoStorageCaller;
	}
}
