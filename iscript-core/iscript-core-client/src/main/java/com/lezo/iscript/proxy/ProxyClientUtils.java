package com.lezo.iscript.proxy;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;

import com.lezo.iscript.api.utils.TaskParamUtils;
import com.lezo.iscript.rest.http.HttpClientFactory;
import com.lezo.iscript.rest.http.ProxySocketFactory;
import com.lezo.iscript.utils.ProxyUtils;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ProxyClientUtils {
    public static final int PROXY_TYPE_HTTP = 1;
    public static final int PROXY_TYPE_SOCKS = 2;
    private static DefaultHttpClient proxyHttpClient;

    public static DefaultHttpClient getProxyHttpClient() {
        if (proxyHttpClient == null) {
            synchronized (ProxyClientUtils.class) {
                if (proxyHttpClient == null) {
                    proxyHttpClient = HttpClientFactory.createHttpClient();
                    SchemeRegistry schreg = proxyHttpClient.getConnectionManager().getSchemeRegistry();
                    ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(schreg,
                            new SimpleProxySelector());
                    proxyHttpClient.setRoutePlanner(routePlanner);
                }
            }
        }
        return proxyHttpClient;
    }

    public static HttpGet createHttpGet(String url, TaskWritable taskWritable) {
        String proxyHost = TaskParamUtils.getOrDefault(taskWritable, "proxyHost", null);
        Integer proxyPort = TaskParamUtils.getOrDefault(taskWritable, "proxyPort", null);
        Integer proxyType = TaskParamUtils.getOrDefault(taskWritable, "proxyType", null);
        return createHttpGet(url, proxyHost, proxyPort, proxyType);
    }

    public static HttpPost createHttpPost(String url, TaskWritable taskWritable) {
        String proxyHost = TaskParamUtils.getOrDefault(taskWritable, "proxyHost", null);
        Integer proxyPort = TaskParamUtils.getOrDefault(taskWritable, "proxyPort", null);
        Integer proxyType = TaskParamUtils.getOrDefault(taskWritable, "proxyType", null);
        return createHttpPost(url, proxyHost, proxyPort, proxyType);
    }

    public static HttpGet createHttpGet(String url, String proxyHost, Integer proxyPort, Integer proxyType) {
        HttpGet get = new HttpGet(url);
        convertToProxyRequest(get, proxyHost, proxyPort, proxyType);
        return get;
    }

    public static HttpPost createHttpPost(String url, String proxyHost, Integer proxyPort, Integer proxyType) {
        HttpPost post = new HttpPost(url);
        convertToProxyRequest(post, proxyHost, proxyPort, proxyType);
        return post;
    }

    public static HttpUriRequest convertToProxyRequest(HttpUriRequest request, String proxyHost, Integer proxyPort,
            Integer proxyType) {
        if (StringUtils.isBlank(proxyHost) || !ProxyUtils.isPort(proxyPort)) {
            return request;
        }
        proxyType = proxyType == null ? 0 : proxyType;
        switch (proxyType) {
            case PROXY_TYPE_HTTP: {
                request.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
                        new HttpHost(proxyHost, proxyPort, "http"));
                break;
            }
            case PROXY_TYPE_SOCKS: {
                request.getParams().setParameter(ProxySocketFactory.SOCKET_PROXY,
                        new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyHost, proxyPort)));
                break;
            }
            default:
                break;
        }
        return request;
    }
}
