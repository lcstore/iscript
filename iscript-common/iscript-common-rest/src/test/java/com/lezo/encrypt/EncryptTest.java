package com.lezo.encrypt;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import com.lezo.iscript.utils.encrypt.Base64Decryptor;
import com.lezo.iscript.utils.encrypt.Base64Encryptor;
import com.lezo.iscript.utils.encrypt.Encryptor;
import com.lezo.iscript.utils.encrypt.EncryptorFactory;

public class EncryptTest {

	@Test
	public void testMe() throws Exception {
		String inputStr = "简单加密";
		System.err.println("原文:/n" + inputStr);

		byte[] inputData = inputStr.getBytes();
		String code = new Base64Encryptor().encript(inputData);
		code = "zzE8CC741E398BE86F261070A7B491241430713222ECA16B97C2005E0CB490484AB822472F34F086C912815685A4D1854F185E1F8218310D557C606108E550CCCE186700AB5D83339336E801279512F990DE7C020189B1E70F38037707A872A00614731EABF8102ACB284270032071ED0FA061778544F0CDC236122A0FCC51CF8BBC71678D24D1A9C62A523A2B48918F0D9C704904EC209BD7069A562AD0E0CF01B8357B08407060C42A5231A2B092A516A633678500D0814E9E030EA9F8800F99B4723D0B58B1E0C98E020F084C018A90AA2231217880EAD5368367A634500C15A46335297DC0A3D7CAEF2028DDC2BF82";
		System.err.println("BASE64加密后:/n" + code);

		String output = new Base64Decryptor().decript(code.getBytes());

		String outputStr = output;

		System.err.println("BASE64解密后:/n" + new String(output.getBytes(), "US-ASCII"));

		// 验证BASE64加密解密一致性
		assertEquals(inputStr, outputStr);

		// 验证MD5对于同一内容加密是否一致
		Encryptor md5Encryptor = EncryptorFactory.getEncryptor(EncryptorFactory.KEY_MD5);
		assertEquals(md5Encryptor.encript(inputData), md5Encryptor.encript(inputData));

		// 验证SHA对于同一内容加密是否一致
		Encryptor shaEncryptor = EncryptorFactory.getEncryptor(EncryptorFactory.KEY_SHA);
		assertEquals(shaEncryptor.encript(inputData), shaEncryptor.encript(inputData));

		String key = "124124";
		System.err.println("Mac密钥:/n" + key);

		// 验证HMAC对于同一内容，同一密钥加密是否一致
		Encryptor hmacEncryptor = EncryptorFactory.getEncryptor(EncryptorFactory.KEY_HMAC_MD5);
		assertEquals(hmacEncryptor.encript(inputData, key), hmacEncryptor.encript(inputData, key));

		BigInteger md5 = new BigInteger(md5Encryptor.encript(inputData).getBytes());
		System.err.println("MD5:/n" + md5.toString(16));

		BigInteger sha = new BigInteger(shaEncryptor.encript(inputData).getBytes());
		System.err.println("SHA:/n" + sha.toString(32));

		BigInteger mac = new BigInteger(hmacEncryptor.encript(inputData, key).getBytes());
		System.err.println("HMAC:/n" + mac.toString(16));
	}

	@Test
	public void testwcaca() {
		String a = "0EE325247233010C01C0C7300240C41AF520450662B7463699D30734E8E24704D8902D25D2698E08C08A8277B08406060D4BA332C239C020C17787204016430C9C39C43102214806953A620670680F0E546A0131E8DD8504B03083052A2B42009550A025D87F451CC54B8C1090309901319CAEF2028DDC2BF82";
		String b = "0FE325247233010C01C0C7300240C41AF520450662B7463699D30734E8E24704D8902D25D2698E08C08A8277B08406060D4BA332C239C020C17787204016430C9C39C43102214806953A620670680F0E546A0131E8DD8504B03003052AAB42001550A025D87F451CC54B8C1090309901319CAEF2028DDC2BF82";
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				System.out.println(a.charAt(i) + "=" + b.charAt(i) + ":"+i);
			}
		}
		a =rep(a, "@", 1);
		a =rep(a, "@", 180);
		a =rep(a, "@", 186);
		a =rep(a, "@", 192);
		System.out.println(a);
		System.out.println(b);
	}
	
	public String rep(String a,String dest,int index){
		return 	a.substring(0,index)+"#"+a.substring(index+1);
	}
	
	@Test
	public void testUrl(){
		String productUrl="6944908237158";
		String productName="6944908237158";
		System.out.println(getJsonUrl(productUrl, productName));
	}
	
	private String getJsonUrl(String productUrl, String productName) {
		String urlHead = "http://zhushou.huihui.cn/productSense?av=2.5";
		String m = "m=" + getM(productUrl);
		String k = "k=" + getK(productName);
		return urlHead + '&' + m + '&' + k;
	}

	private StringBuilder getM(String url) {
		Character charUrl;
		StringBuilder encodeParam = new StringBuilder();
		for (int i = 0; i < url.length(); i++) {
			charUrl = url.charAt(i);
			encodeParam.append(enCode(charUrl));
		}
		return encodeParam.reverse();
	}

	private String getK(String title) {
		String param = "t=" + title + "^&k=lxsx^&d=ls";
		Character chrYhdId;
		StringBuilder encodeParam = new StringBuilder();
		for (int i = 0; i < param.length(); i++) {
			chrYhdId = param.charAt(i);
			if (enCode(chrYhdId).length() == 2) {
				encodeParam.append("00" + enCode(chrYhdId));
			} else {
				encodeParam.append(enCode(chrYhdId));
			}
		}
		return encodeParam.toString();
	}

	public String enCode(char a) {
		return Integer.toHexString((int) a + 88);
	}
}
