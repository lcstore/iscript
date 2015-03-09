package com.lezo.rest.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.URLUtils;
import com.qiniu.api.rsf.ListItem;
import com.qiniu.api.rsf.ListPrefixRet;
import com.qiniu.api.rsf.RSFClient;
import com.qiniu.api.rsf.RSFEofException;

public class QiniuRester implements DataRestable {

	private static final String DEFAULT_CHASET_NAME = "UTF-8";
	private static final String KEY_LIMIT_NUM = "limit";
	private static final String KEY_MARKER = "marker";
	private String bucket;
	private String domain;
	private Mac mac;
	private HttpClient client;

	@Override
	public boolean upload(String targetPath, byte[] dataBytes) throws Exception {
		String key = toQiniuKey(targetPath);
		PutExtra extra = new PutExtra();
		PutPolicy putPolicy = new PutPolicy(getBucket());
		String uptoken = putPolicy.token(mac);
		InputStream in = new ByteArrayInputStream(dataBytes);

		PutRet ret = IoApi.Put(uptoken, key, in, extra);
		if (ret.exception != null) {
			throw ret.exception;
		}
		return true;
	}

	private String toQiniuKey(String key) {
		String newKey = key.replace("\\", "/");
		try {
			newKey = new String(newKey.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newKey;
	}

	@Override
	public String download(String soucePath) throws Exception {
		String baseUrl = URLUtils.makeBaseUrl(getDomain(), soucePath);
		GetPolicy getPolicy = new GetPolicy();
		String downloadUrl = getPolicy.makeRequest(baseUrl, getMac());
		HttpGet request = new HttpGet(downloadUrl);
		RestRespone customRespone = RestRequestUtils.doRequest(client, request);
		if (customRespone.getException() != null) {
			throw customRespone.getException();
		}
		return EntityUtils.toString(new GzipDecompressingEntity(customRespone.getResponse().getEntity()), DEFAULT_CHASET_NAME);
	}

	@Override
	public RestList listFiles(String targetPath, Map<String, String> paramMap) throws Exception {
		RSFClient rsfClient = new RSFClient(mac);
		String marker = getValue(paramMap, KEY_MARKER, "");
		int limit = Integer.valueOf(getValue(paramMap, KEY_LIMIT_NUM, "100"));
		ListPrefixRet ret = rsfClient.listPrifix(getBucket(), targetPath, marker, limit);
		if (ret.results != null) {
			List<ListItem> itemList = ret.results;
			int len = itemList.size();
			List<RestFile> fileList = new ArrayList<RestFile>(len);
			for (int i = 0; i < len; i++) {
				ListItem itemObject = itemList.get(i);
				RestFile restFile = new RestFile();
				restFile.setId(itemObject.hash);
				restFile.setPath(itemObject.key);
				restFile.setDirectory(false);
				restFile.setSize(itemObject.fsize);
				restFile.setCreateTime(itemObject.putTime);
				// restFile.setUpdateTime(itemObject.putTime);
				restFile.setSource(getBucket() + "." + getDomain());
				fileList.add(restFile);
			}
			RestList restFileList = new RestList();
			restFileList.setDataList(fileList);
			restFileList.setEOF(ret.exception instanceof RSFEofException);
			restFileList.setMarker(ret.marker);
			return restFileList;
		}
		return null;
	}

	private String getValue(Map<String, String> paramMap, String key, String defaultValue) {
		String value = paramMap == null ? null : paramMap.get(key);
		return value == null ? defaultValue : value;
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

	public Mac getMac() {
		return mac;
	}

	public void setMac(Mac mac) {
		this.mac = mac;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

}
