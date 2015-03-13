package com.lezo.rest.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
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

public class KanBoxRester implements DataRestable {

	private static final String DEFAULT_CHASET_NAME = "UTF-8";
	private static final String KEY_BY = "by";
	private static final String KEY_ORDER = "order";
	private static final String KEY_LIMIT_STRING = "limit";
	private String bucket;
	private String domain;
	private String accessToken;
	private HttpClient client;

	@Override
	public boolean upload(String targetPath, byte[] dataBytes) throws Exception {
//		targetPath = convertPath(targetPath);
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("path", "/idocs/11.xml"));
		paramList.add(new BasicNameValuePair("bearer_token", getAccessToken()));

		String url = "https://api-upload.kanbox.com/0/upload?" + buildParams(paramList);

		int index = targetPath.lastIndexOf("/");
		String suffix = targetPath.substring(index + 1, targetPath.length());
		HttpPost post = new HttpPost(url);
		MultipartEntity entity = new MultipartEntity();
		ContentBody bsData = new ByteArrayBody(dataBytes, suffix);
		entity.addPart("file", bsData);
		post.setEntity(entity);
		RestRespone customRespone = RestRequestUtils.doRequest(client, post);
		if (customRespone.getException() != null) {
			throw customRespone.getException();
		}
		String rsString = EntityUtils.toString(customRespone.getResponse().getEntity());
		EntityUtils.consumeQuietly(customRespone.getResponse().getEntity());
		StatusLine statusLine = customRespone.getResponse().getStatusLine();
		int status = statusLine.getStatusCode();
		return status / 100 == 2;
	}

	private String convertPath(String targetPath) {
		if (StringUtils.isNotEmpty(getRootPath()) && !targetPath.startsWith(getRootPath().substring(0, getRootPath().length() - 1))) {
			targetPath = targetPath.startsWith("/") ? (getRootPath() + targetPath.substring(1)) : (getRootPath() + targetPath);
		}
		targetPath = targetPath.replace("\\", "/");
		return targetPath;
	}

	@Override
	public String download(String soucePath) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("path", soucePath));
		params.add(new BasicNameValuePair("bearer_token", getAccessToken()));

		String url = "https://api.kanbox.com/0/download?" + buildParams(params);
		HttpGet request = new HttpGet(url);
		RestRespone customRespone = RestRequestUtils.doRequest(client, request);
		if (customRespone.getException() != null) {
			throw customRespone.getException();
		}
		byte[] byteArray = EntityUtils.toByteArray(customRespone.getResponse().getEntity());
		byteArray = convertBytes(byteArray);
		return new String(byteArray, DEFAULT_CHASET_NAME);
	}

	private byte[] convertBytes(byte[] dataBytes) throws Exception {
		if (isGzip(dataBytes)) {
			GZIPInputStream gis = null;
			ByteArrayOutputStream bos = null;
			try {
				InputStream inStream = new ByteArrayInputStream(dataBytes);
				gis = new GZIPInputStream(inStream);
				bos = new ByteArrayOutputStream();
				byte[] tmp = new byte[1024]; // Rough estimate
				int len = -1;
				while ((len = gis.read(tmp)) > -1) {
					bos.write(tmp, 0, len);
				}
				bos.flush();
				dataBytes = bos.toByteArray();
			} catch (Exception e) {
				throw e;
			} finally {
				IOUtils.closeQuietly(bos);
				IOUtils.closeQuietly(gis);
			}
		}
		return dataBytes;
	}

	private boolean isGzip(byte[] header) {
		if (header == null || header.length < 2) {
			return false;
		}
		int headCode = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
		return headCode == GZIPInputStream.GZIP_MAGIC;
	}

	@Override
	public RestList listFiles(String sourcePath, Map<String, String> paramMap) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("bearer_token", getAccessToken()));
		params.add(new BasicNameValuePair("path", sourcePath));

		String url = "https://api.kanbox.com/0/list?" + buildParams(params);
		HttpPost request = new HttpPost(url);
		RestRespone customRespone = RestRequestUtils.doRequest(client, request);
		if (customRespone.getException() != null) {
			throw customRespone.getException();
		}
		RestList restFileList = new RestList();
		String result = EntityUtils.toString(customRespone.getResponse().getEntity(), DEFAULT_CHASET_NAME);
		JSONObject rsObject = JSONUtils.getJSONObject(result);
		JSONArray listArray = (JSONArray) (rsObject == null ? null : JSONUtils.get(rsObject, "contents"));
		if (listArray != null) {
			int len = listArray.length();
			List<RestFile> fileList = new ArrayList<RestFile>(len);
			for (int i = 0; i < len; i++) {
				JSONObject itemObject = listArray.getJSONObject(i);
				RestFile restFile = new RestFile();
//				restFile.setId(JSONUtils.getString(itemObject, "fs_id"));
				restFile.setPath(JSONUtils.getString(itemObject, "fullPath"));
				restFile.setDirectory("true" == JSONUtils.getString(itemObject, "isFolder"));
				restFile.setSize(JSONUtils.getLong(itemObject, "fileSize"));
				restFile.setCreateTime(JSONUtils.getLong(itemObject, "creationDate") * 1000);
				restFile.setUpdateTime(restFile.getCreateTime());
				restFile.setSource(getBucket() + "." + getDomain());
				fileList.add(restFile);
			}
			restFileList.setDataList(fileList);
			String limit = paramMap == null ? null : paramMap.get(KEY_LIMIT_STRING);
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
