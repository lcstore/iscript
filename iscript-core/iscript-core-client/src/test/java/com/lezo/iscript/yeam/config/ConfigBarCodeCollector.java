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

import com.lezo.iscript.utils.BarCodeUtils;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.file.PersistentCollector;
import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigBarCodeCollector implements ConfigParser {
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private static final String EMTPY_RESULT = new JSONObject().toString();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		JSONObject itemObject = getDataObject(task);
		// doCollect(itemObject, task);
		return itemObject.toString();
	}

	private void doCollect(JSONObject dataObject, TaskWritable task) {
		JSONObject gObject = new JSONObject();
		JSONObject argsObject = new JSONObject(task.getArgs());
		JSONUtils.put(argsObject, "name@client", HeaderUtils.CLIENT_NAME);
		JSONUtils.put(argsObject, "target", "PromotionMapDto");

		JSONUtils.put(gObject, "args", argsObject);

		JSONUtils.put(gObject, "rs", dataObject.toString());
		System.err.println(dataObject);
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
		String url = (String) task.get("url");
		JSONObject dObject = new JSONObject();
		HttpGet pageGet = new HttpGet(url);
		pageGet.addHeader("Referer", url);
		String pHtml = HttpClientUtils.getContent(client, pageGet, "UTF-8");
		Pattern oReg = Pattern.compile("[0-9]{13}");
		Matcher matcher = oReg.matcher(pHtml);
		JSONArray dArray = new JSONArray();
		Set<String> hasCodeSet = new HashSet<String>();
		while (matcher.find()) {
			String sBarCode = matcher.group();
			if (BarCodeUtils.isBarCode(sBarCode)) {
				if (!hasCodeSet.contains(sBarCode)) {
					dArray.put(sBarCode);
					hasCodeSet.add(sBarCode);
				}
			}
		}
		JSONUtils.put(dObject, "data", dArray);
		return dObject;
	}
}