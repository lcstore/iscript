package com.lezo.rest.jss;

import java.io.File;

import org.junit.Test;

import com.jcloud.jss.Credential;

public class JssServerClientTest {

	@Test
	public void testPut() throws Exception {
		String accessKey = "89561dd23bb0410992836508954062c7";
		String secretKey = "eaf09f71e43546f7970c6e25398d2cfcSlW8Ut8Q";
		JssServerClient client = new JssServerClient(accessKey, secretKey);

		String accessKeyId = "89561dd23bb0410992836508954062c7";
		String secretAccessKeyId = "eaf09f71e43546f7970c6e25398d2cfcSlW8Ut8Q";
		Credential credential = new Credential(accessKeyId, secretAccessKeyId);
		client = new JssServerClient(credential);

		String albumName = "item.lezomao.com";
		String uploadifyFileName = "jd.category.0.0";
		File uploadify = new File("src/main/resources/region.txt");
		client.storageObject(uploadify, uploadifyFileName, albumName);
//		client.deleteObject(albumName, uploadifyFileName);
	}
	
	public void testClear(){
		
	}
}
