package com.lezo.rest.jss;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.google.common.net.HttpHeaders;
import com.jcloud.jss.Credential;
import com.jcloud.jss.auth.RestSigner;
import com.jcloud.jss.client.ClientConfig;
import com.jcloud.jss.client.Request;
import com.jcloud.jss.client.Request.Builder;
import com.jcloud.jss.client.StorageHttpClient;
import com.jcloud.jss.http.Method;
import com.jcloud.jss.http.Scheme;
import com.jcloud.jss.http.StorageHttpResponse;
import com.lezo.encrypt.EncryptorFactory;

public class JssClientApiTest {

	private static final String charset = "utf-8";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ClientConfig config = new ClientConfig();
		String accessKeyId = "89561dd23bb0410992836508954062c7";
		String secretAccessKeyId = "eaf09f71e43546f7970c6e25398d2cfcSlW8Ut8Q";
		Credential credential = new Credential(accessKeyId, secretAccessKeyId);
		StorageHttpClient client = new StorageHttpClient(config, credential);

		Builder builder = null;
		
//		builder = buildGetBurketRequestBuilder();
		builder = buildPutBurketRequestBuilder();
		 builder = buildDelBurketRequestBuilder();
//		builder = buildAddBurketRequestBuilder();
		RestSigner signer = new RestSigner();
		String summary = signer.getStringToSign(builder.build());
		String signature = RestSigner.createToken(summary, secretAccessKeyId);
		builder.parameter("Signature", signature);
		Request request = builder.build();

		StorageHttpResponse rs = client.excute(request, StorageHttpResponse.class);
		System.out.println("status:" + rs.getStatus());
		System.out.println(EntityUtils.toString(rs.getEntity(), "utf-8"));

	}

	public static void test() {
		// Authorization = "jingdong" + " " + AccessKey + ":" + Signature;
	}

	public static Builder buildGetBurketRequestBuilder() throws Exception {
		Builder builder = Request.builder();
		builder.bucket("item.lezomao.com");
		builder.key("region.txt");
		builder.method(Method.GET);
		builder.scheme(Scheme.DEFAULT);
		builder.endpoint("storage.jcloud.com");
		long timeout = System.currentTimeMillis() + 5 * 60 * 1000;
		builder.parameter("Expires", "" + timeout);
		return builder;
	}

	public static Builder buildPutBurketRequestBuilder() throws Exception {
		Builder builder = Request.builder();
		builder.bucket("create2.bucket.com");
		builder.method(Method.PUT);
		builder.scheme(Scheme.DEFAULT);
		builder.endpoint("storage.jcloud.com");
		//不能有Expires
		return builder;
	}

	public static Builder buildDelBurketRequestBuilder() throws Exception {
		Builder builder = Request.builder();
		builder.bucket("create2.bucket.com");
		builder.method(Method.DELETE);
		builder.scheme(Scheme.DEFAULT);
		builder.endpoint("storage.jcloud.com");
		//不能有Expires
		// 204，content为空
		return builder;
	}

	public static Builder buildAddBurketRequestBuilder() throws Exception {
		Builder builder = Request.builder();
		builder.bucket("create2.bucket.com");
		builder.key("addt.txt");
		builder.method(Method.PUT);
		builder.scheme(Scheme.DEFAULT);
		builder.endpoint("storage.jcloud.com");
		long timeout = System.currentTimeMillis() + 5 * 60 * 1000;
		builder.parameter("Expires", "" + timeout);
		String source = "add text to bucket";
		StringEntity entity = new StringEntity(source, ContentType.APPLICATION_OCTET_STREAM);
		builder.entity(entity);
		builder.parameter(HttpHeaders.CONTENT_MD5,
				EncryptorFactory.getEncryptor(EncryptorFactory.KEY_MD5).encript(source.getBytes()));
		return builder;
	}
}
