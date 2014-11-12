package com.lezo.iscript.yeam.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

/**
 * {"data":[],"nexts":[]}
 * 
 * @param task
 * @return
 * @throws Exception
 */
public class ConfigJdPromotList implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject itemObject = getDataObject(task);
		doCollect(itemObject, task);
		return itemObject.toString();
	}

	private void doCollect(JSONObject dataObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);

		JSONUtils.put(gObject, "args", argsObject);

		JSONUtils.put(gObject, "rs", dataObject.toString());

		List<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(gObject);
		PersistentCollector.getInstance().getBufferWriter().write(dataList);
	}

	/**
	 * {"data":[],"nexts":[]}
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private JSONObject getDataObject(TaskWritable task) throws Exception {
		String url = task.get("url").toString();
		JSONObject itemObject = new JSONObject();
		HttpGet get = new HttpGet(url);
		get.addHeader("Cookie", "__jda=183698212.1642501015.1412589727.1415285934.1415794562.12; __jdv=183698212|kong|t_51698052_|tuiguang|d76cc5be82724528b3aac83fa9ee15d8; __jdu=1642501015; unpl=V2_ZzNtbRdRREcnDBUAchpaBmIHEVQRVRNFJwFEAysRCFFnB0dUclRCFXAURlRnGFsUZAAZWUpcQRFFCHZQex5UDGYHEW1yZ0MWRQF2VHwcXwFmAxNdRmc%3d; unionuuid=V2_ZgJGXhJeQkZ9W0VdfhBYBW4ARl1KAxEQcwBGVXwZX1cIABNdR1dCF3QIRVZ9Gl9qZAARQkVQXBFqCEFTbgxJ; mt_xid=52007|%7B%22unt%22%3A%222014-10-30T12%3A37%3A21%22%2C%22jd_pop%22%3Anull%2C%22ad_sku_type%22%3A%22%22%2C%22rf%22%3A0%2C%22stp%22%3A%221%22%2C%22ad_type%22%3A%22%22%2C%22xjdb%22%3A%221642501015%22%7D; mt_subsite=207944_197; mt_ext=%7B%22unt%22%3A%222014-10-30T12%3A37%3A21%22%2C%22jd_pop%22%3Anull%2C%22ad_sku_type%22%3A%22%22%2C%22rf%22%3A0%2C%22stp%22%3A%221%22%2C%22ad_type%22%3A%22%22%2C%22xjdb%22%3A%221642501015%22%7D; ipLocation=%u5317%u4EAC; ipLoc-djd=2-2813-2865-0; __jdb=183698212.1.1642501015|12.1415794562; __jdc=183698212");
		String html = HttpClientUtils.getContent(client, get);
		Document dom = Jsoup.parse(html, url);
		addProducts(dom, itemObject);
		JSONArray nextArray = new JSONArray();
		addActs(dom, nextArray);
		addNexts(dom, nextArray);
		JSONUtils.put(itemObject, "nextList", nextArray);
		System.err.println(itemObject);
		return itemObject;
	}

	private void addProducts(Document dom, JSONObject itemObject) {
		Elements productAs = dom.select("a[href*=item.jd.com][target]");
		if (productAs.isEmpty()) {
			return;
		}
		int size = productAs.size();
		JSONArray dataArray = new JSONArray();
		JSONUtils.put(itemObject, "dataList", dataArray);
		Set<String> urlSet = new HashSet<String>(size);
		for (int i = 0; i < size; i++) {
			String pUrl = productAs.get(i).absUrl("href");
			if (!urlSet.contains(pUrl)) {
				dataArray.put(pUrl);
				urlSet.add(pUrl);
			}
		}

	}

	private void addNexts(Document dom, JSONArray nextArray) {
		String url = dom.baseUri();
		Pattern oReg = Pattern.compile("(-0-0-[0-1]-)[0-9].html");
		Matcher matcher = oReg.matcher(url);
		if (!matcher.find()) {
			return;
		}
		Elements totalPageAs = dom.select("div.paging div.msdn.pagin span.text + a[onclick][href]");
		if (totalPageAs.isEmpty()) {
			return;
		}
		int total = Integer.valueOf(totalPageAs.first().ownText().trim());
		String curTxt = matcher.group(1);
		for (int i = 2; i <= total; i++) {
			String sNext = url.replace(curTxt, "-0-0-" + i + "-");
			nextArray.put(sNext);
		}
	}

	private void addActs(Document dom, JSONArray nextArray) {
		String url = dom.baseUri();
		if (url.indexOf("http://sale.jd.com/act/") > -1) {
			return;
		}
		System.err.println(dom);
		Elements actAs = dom.select("a[href*=sale.jd.com/act/]:not(a[href*=sale.jd.com/act/][rel=nofollow])");
		if (actAs.isEmpty()) {
			return;
		}
		int len = actAs.size();
		Set<String> urlSet = new HashSet<String>(len);
		urlSet.add(dom.baseUri());
		String head = "url=";
		for (int i = 0; i < len; i++) {
			String sActUrl = actAs.get(i).absUrl("href");
			int index = sActUrl.indexOf(head);
			sActUrl = index > 0 ? sActUrl.substring(index + head.length()) : sActUrl;
			if (!urlSet.contains(sActUrl)) {
				nextArray.put(sActUrl);
				urlSet.add(sActUrl);
			}
		}
	}
}