	package com.lezo.iscript.rest.http;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class ImageResponseHandler implements ResponseHandler<BufferedImage> {

	@Override
	public BufferedImage handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		if (response == null) {
			return null;
		}
		HttpEntity entity = response.getEntity();
		if (entity.getContentLength() < 1) {
			EntityUtils.consume(entity);
			return null;
		}
		InputStream in = entity.getContent();
		BufferedImage image = ImageIO.read(in);
		EntityUtils.consume(entity);
		return image;
	}

}
