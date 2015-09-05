package com.lezo.iscript.rest.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static DefaultHttpClient createHttpClient() {
        return HttpClientFactory.createHttpClient();
    }

    public static String getContent(DefaultHttpClient client, HttpUriRequest request) throws Exception {
        HttpResponse response = null;
        try {
            response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.toString());
            }
            HttpEntity entity = response.getEntity();
            byte[] dataBytes = EntityUtils.toByteArray(entity);
            String charset = getCharsetOrDefault(entity.getContentType(), dataBytes, DEFAULT_CHARSET);
            return new String(dataBytes, charset);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
    }

    public static String getContent(DefaultHttpClient client, HttpUriRequest request, String charsetName)
            throws Exception {
        HttpResponse response = null;
        try {
            response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.toString());
            }
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, charsetName);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

    }

    public static String getCharsetOrDefault(Header contentType, byte[] dataBytes, String defaultCharset)
            throws Exception {
        String charset = getCharsetFromHead(contentType);
        if (charset != null) {
            return charset;
        }
        charset = getCharsetFromData(dataBytes);
        if (charset != null) {
            return charset;
        }
        return defaultCharset;
    }

    public static String getCharsetFromHead(Header contentType) throws Exception {
        Header header = contentType;
        if (header != null) {
            HeaderElement[] elements = header.getElements();
            if (elements.length > 0) {
                HeaderElement helem = elements[0];
                NameValuePair param = helem.getParameterByName("charset");
                if (param != null) {
                    return param.getValue();
                }
            }
        }
        return null;
    }

    public static String getCharsetFromData(byte[] dataBytes) throws Exception {
        if (dataBytes != null) {
            String souce = new String(dataBytes, "GBK");
            int index = souce.indexOf("Content-Type");
            if (index > 0) {
                int maxLen = 100;
                maxLen = maxLen < souce.length() ? maxLen : souce.length();
                souce = souce.substring(index, index + maxLen);
                Pattern oReg = Pattern.compile("charset.*?=([a-zA-Z0-9\\-]{3,})");
                Matcher matcher = oReg.matcher(souce);
                if (matcher.find()) {
                    return matcher.group(1).trim();
                }
            }
        }
        return null;
    }
}
