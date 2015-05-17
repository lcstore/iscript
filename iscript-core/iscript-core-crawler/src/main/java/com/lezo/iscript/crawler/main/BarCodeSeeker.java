package com.lezo.iscript.crawler.main;

import java.awt.image.BufferedImage;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.lezo.iscript.crawler.config.ConfigHolder;
import com.lezo.iscript.ocr.ANCCOCRParser;
import com.lezo.iscript.rest.http.HttpBrowserManager;
import com.lezo.iscript.rest.http.ImageResponseHandler;
import com.lezo.iscript.rest.http.SimpleHttpBrowser;

public class BarCodeSeeker {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String tessPath = "D:/lezo/iscript/v0.01/iscript-core/iscript-core-ocr/src/main/resources/tesseract";
		tessPath = "C:/Tesseract-ocr";
		ConfigHolder holder = new ConfigHolder();
		holder.setCofigFolder("src/main/resources");
		holder.initConfig();
		// ContextFactory cf = new ContextFactory();
		// Context cx = cf.enterContext();
		// String sourceName = "<test>";
		// String source = holder.getConfig("test");
		// source = "var args={};" + source;
		// Scriptable scope = cx.initStandardObjects();
		// Object result = cx.evaluateString(scope, source, sourceName, 1,
		// null);
		// System.out.println(Context.toString(result));
		int index = 20;
		while (index > 0) {

			SimpleHttpBrowser browser = HttpBrowserManager.buildBrowser("");
			String uri = "http://search.anccnet.com/comm/select_CheckCodeImg.aspx?id=0.44784616563643065";
			HttpUriRequest request = new HttpGet(uri);
			BufferedImage image = browser.execute(request, new ImageResponseHandler());
			// File imageFile = new File("r42l.jpg");
			// imageFile = new File("src/main/resources/R42L.jpg");
			// BufferedImage bi = ImageIO.read(imageFile);
			BufferedImage bi = image;
			String result = ANCCOCRParser.doParse(tessPath, bi);
			result = result.trim();
			System.out.println("result:" + result);
			System.out.println("isVerifyCode:" + ANCCOCRParser.isVerifyCode(result));
			if (ANCCOCRParser.isVerifyCode(result)) {
				index--;
			}
		}
	}
}
