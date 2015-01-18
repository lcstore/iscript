package com.lezo.iscript.yeam.resultmgr;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.http.HttpClientUtils;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.URLUtils;

public class DataFileConsumer implements Runnable {
	private static final DefaultHttpClient CLIENT = HttpClientUtils.createHttpClient();
	private static final String CHARSET_NAME = "UTF-8";
	private String type;
	private DataFileWrapper dataFileWrapper;

	public DataFileConsumer(String type, DataFileWrapper dataFileWrapper) {
		super();
		this.type = type;
		this.dataFileWrapper = dataFileWrapper;
	}

	@Override
	public void run() {
		List<String> dataLineList = null;
		try {
			dataLineList = downData();
		} catch (EncoderException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		}
		if (CollectionUtils.isNotEmpty(dataLineList)) {
			ThreadPoolExecutor dataConsumeExecutor = (ThreadPoolExecutor) SpringBeanUtils.getBean("dataConsumeExecutor");
			for (String dataLine : dataLineList) {
				dataConsumeExecutor.execute(new DataLineConsumer(type, dataLine));
			}
		}
	}

	private List<String> downData() throws EncoderException, AuthException {
		String bucketDomain = dataFileWrapper.getBucketName() + "." + dataFileWrapper.getDomain();
		String baseUrl = URLUtils.makeBaseUrl(bucketDomain, dataFileWrapper.getItem().key);
		GetPolicy getPolicy = new GetPolicy();
		String downloadUrl = getPolicy.makeRequest(baseUrl, dataFileWrapper.getMac());
		HttpGet fileGet = new HttpGet(downloadUrl);
		InputStream inStream = null;
		try {
			HttpResponse res = CLIENT.execute(fileGet);
			inStream = res.getEntity().getContent();
			return toDataList(inStream);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		} finally {
			IOUtils.closeQuietly(inStream);
			if (fileGet != null && !fileGet.isAborted()) {
				fileGet.abort();
			}
		}
	}

	private List<String> toDataList(InputStream inStream) throws Exception {
		if (inStream == null) {
			return Collections.emptyList();
		}
		GZIPInputStream gis = new GZIPInputStream(inStream);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] tmp = new byte[1024]; // Rough estimate
		int len = -1;
		while ((len = gis.read(tmp)) > -1) {
			bos.write(tmp, 0, len);
		}
		bos.flush();
		byte[] byteArray = bos.toByteArray();
		bos.close();
		return toStringList(byteArray);
	}

	private List<String> toStringList(byte[] byteArray) throws Exception {
		String fileData = new String(byteArray, CHARSET_NAME);
		StringTokenizer tokenizer = new StringTokenizer(fileData, "\n");
		List<String> stringList = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			stringList.add(tokenizer.nextToken());
		}
		return stringList;
	}
}
