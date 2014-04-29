package com.lezo.rest.baidu.pcs;

import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcs.BaiduPCSClient;


public class ApiPcsClient {
	public static void main(String[] args) throws Exception {
		String access_token = "3.a1333cd5eebc4a402e706e06b060b60a.2592000.1389019338.4026763474-1552221";
		String url = "https://pcs.baidu.com/rest/2.0/pcs/file";
		String source = "src/main/resources/region.txt";
		String path = "/apps/emao_doc/region.txt";
		BaiduPCSClient client = new BaiduPCSClient(access_token);
		PCSFileInfoResponse res = client.uploadFile(source, path);
		System.out.println(res.status.message);
		System.out.println(res.commonFileInfo.fsId);
		System.out.println(res.commonFileInfo.path);
	}
}
