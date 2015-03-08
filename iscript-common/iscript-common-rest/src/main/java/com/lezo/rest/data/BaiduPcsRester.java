package com.lezo.rest.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lezo.iscript.utils.JSONUtils;

public class BaiduPcsRester implements DataRestable {

	private static final String DEFAULT_CHASET_NAME = "UTF-8";
	private static final String KEY_BY = "by";
	private static final String KEY_ORDER = "order";
	private static final String KEY_LIMIT = "limit";
	private String bucket;
	private String domain;
	private String accessToken;
	private HttpClient client;

	@Override
	public boolean upload(String targetPath, byte[] dataBytes) throws Exception {
		targetPath = convertPath(targetPath);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("method", "upload"));
		paramList.add(new BasicNameValuePair("access_token", getAccessToken()));
		paramList.add(new BasicNameValuePair("path", targetPath));
		paramList.add(new BasicNameValuePair("ondup", "overwrite"));

		String url = "https://pcs.baidu.com/rest/2.0/pcs/file?" + buildParams(paramList);

		int index = targetPath.lastIndexOf("/");
		String suffix = targetPath.substring(index + 1, targetPath.length());

		HttpPost post = new HttpPost(url);
		MultipartEntity entity = new MultipartEntity();
		ContentBody bsData = new ByteArrayBody(dataBytes, suffix);
		entity.addPart("uploadedfile", bsData);
		post.setEntity(entity);

		RestRespone customRespone = RestRequestUtils.doRequest(client, post);
		if (customRespone.getException() != null) {
			throw customRespone.getException();
		}
		StatusLine statusLine = customRespone.getResponse().getStatusLine();
		int status = statusLine.getStatusCode();
		if (status / 100 != 2) {
			throw new HttpResponseException(statusLine.getStatusCode(), "upload " + targetPath + ",but[" + statusLine.getReasonPhrase() + "]");
		}
		return true;
	}

	private String convertPath(String targetPath) {
		if (StringUtils.isNotEmpty(getRootPath()) && !targetPath.startsWith(getRootPath().substring(0, getRootPath().length() - 1))) {
			targetPath = targetPath.startsWith("/") ? (getRootPath() + targetPath.substring(1)) : (getRootPath() + targetPath);
		}
		return targetPath;
	}

	@Override
	public String download(String soucePath) throws Exception {
		soucePath = convertPath(soucePath);
		String method = "download";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("method", method));
		params.add(new BasicNameValuePair("access_token", getAccessToken()));
		params.add(new BasicNameValuePair("path", soucePath));

		String url = "https://pcs.baidu.com/rest/2.0/pcs/file?" + buildParams(params);
		HttpGet request = new HttpGet(url);
		RestRespone customRespone = RestRequestUtils.doRequest(client, request);
		if (customRespone.getException() != null) {
			throw customRespone.getException();
		}
		return EntityUtils.toString(customRespone.getResponse().getEntity(), DEFAULT_CHASET_NAME);
	}

	@Override
	public RestFileList listFiles(String sourcePath, Map<String, String> paramMap) throws Exception {
		sourcePath = convertPath(sourcePath);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("method", "list"));
		params.add(new BasicNameValuePair("access_token", getAccessToken()));
		params.add(new BasicNameValuePair("path", sourcePath));

		addNameValuePair(params, KEY_BY, paramMap, "time");
		addNameValuePair(params, KEY_ORDER, paramMap, "desc");
		addNameValuePair(params, KEY_LIMIT, paramMap, null);

		String url = "https://pcs.baidu.com/rest/2.0/pcs/file?" + buildParams(params);
		HttpGet request = new HttpGet(url);
		RestRespone customRespone = RestRequestUtils.doRequest(client, request);
		if (customRespone.getException() != null) {
			throw customRespone.getException();
		}
		RestFileList restFileList = new RestFileList();
		String result = EntityUtils.toString(customRespone.getResponse().getEntity(), DEFAULT_CHASET_NAME);
		JSONObject rsObject = JSONUtils.getJSONObject(result);
		JSONArray listArray = (JSONArray) (rsObject == null ? null : JSONUtils.get(rsObject, "list"));
		if (listArray != null) {
			int len = listArray.length();
			List<RestFile> fileList = new ArrayList<RestFile>(len);
			for (int i = 0; i < len; i++) {
				JSONObject itemObject = listArray.getJSONObject(i);
				RestFile restFile = new RestFile();
				restFile.setId(JSONUtils.getString(itemObject, "fs_id"));
				restFile.setPath(JSONUtils.getString(itemObject, "path"));
				restFile.setDirectory(1 == JSONUtils.getInteger(itemObject, "isdir"));
				restFile.setSize(JSONUtils.getLong(itemObject, "size"));
				restFile.setCreateTime(JSONUtils.getLong(itemObject, "ctime"));
				restFile.setUpdateTime(JSONUtils.getLong(itemObject, "mtime"));
				restFile.setSource(getBucket() + "." + getDomain());
				fileList.add(restFile);
			}
			restFileList.setDataList(fileList);
			String limit = paramMap == null ? null : paramMap.get(KEY_LIMIT);
			if (StringUtils.isNotEmpty(limit) && limit.indexOf("-") > 0) {
				String[] limitArr = limit.split("-");
				int count = Integer.valueOf(limitArr[1]) - Integer.valueOf(limitArr[0]);
				restFileList.setEOF(count > fileList.size());
				restFileList.setMarker(limit);
			} else if (fileList.isEmpty()) {
				restFileList.setEOF(true);
			}
			return restFileList;
		} else {
			restFileList.setEOF(true);
		}
		return restFileList;
	}

	private void addNameValuePair(List<NameValuePair> params, String key, Map<String, String> paramMap, String defaultValue) {
		String destValue = paramMap == null ? null : paramMap.get(key);
		if (StringUtils.isEmpty(destValue)) {
			destValue = defaultValue;
		}
		if (StringUtils.isNotEmpty(destValue)) {
			params.add(new BasicNameValuePair(key, destValue));
		}
	}

	private String buildParams(List<NameValuePair> paramList) {
		String params = null;
		try {
			HttpEntity paramsEntity = new UrlEncodedFormEntity(paramList, DEFAULT_CHASET_NAME);
			params = EntityUtils.toString(paramsEntity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return params;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

	public String getRootPath() {
		return "/apps/" + getBucket() + "/";
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
