package com.lezo.sendcloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class SendEmailTest {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		String url = "https://sendcloud.sohu.com/webapi/mail.send.json";
		HttpClient httpclient = new DefaultHttpClient();
		// httpclient = wrapHttpClient(httpclient);
		HttpPost httpost = new HttpPost(url);

		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		// 不同于登录SendCloud站点的帐号，您需要登录后台创建发信子帐号，使用子帐号和密码才可以进行邮件的发送。
//		nvps.add(new BasicNameValuePair("api_user", "postmaster@noreply.baixing.com.cn"));
//		nvps.add(new BasicNameValuePair("api_key", "Coer2VSD"));
//		nvps.add(new BasicNameValuePair("from", "noreply@noreply.baixing.com.cn"));
		nvps.add(new BasicNameValuePair("api_user", "postmaster@lezomao.sendcloud.org"));
		nvps.add(new BasicNameValuePair("api_key", "aGHgoJgZ"));
		nvps.add(new BasicNameValuePair("from", "dlinked@126.com"));
		nvps.add(new BasicNameValuePair("to", "lcstore@126.com"));
		nvps.add(new BasicNameValuePair("subject", "php 调用WebAPI测试主题"));
		nvps.add(new BasicNameValuePair("html", "欢迎使用SendCloud"));
		nvps.add(new BasicNameValuePair("resp_email_id", "true"));

		httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		// 请求
		HttpResponse response = httpclient.execute(httpost);
		// 处理响应
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
			// 读取xml文档
			String result = EntityUtils.toString(response.getEntity());
			System.out.println(result);
		} else {
			System.err.println("error");
		}
	}
	
	public void testDecode(){
	}
}
