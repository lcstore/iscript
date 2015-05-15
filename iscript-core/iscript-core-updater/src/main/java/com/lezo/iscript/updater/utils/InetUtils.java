package com.lezo.iscript.updater.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.updater.http.HttpUtils;

public class InetUtils {
	private static Logger logger = LoggerFactory.getLogger(InetUtils.class);
	private static final String[] CHECK_URLS = new String[] { "http://www.icanhazip.com/", "http://curlmyip.com/",
			"http://www.ip.cn/index.php", "http://www.net.cn/static/customercare/yourip.asp" };

	public static InetAddress getLANLocalHost() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// Iterate all NICs (network interface cards)...
			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
			while (ifaces.hasMoreElements()) {
				NetworkInterface iface = ifaces.nextElement();
				// Iterate all IP addresses assigned to each card...
				Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();
				while (inetAddrs.hasMoreElements()) {
					InetAddress inetAddr = inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {
						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it
							// immediately...
							return inetAddr;
						} else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily
							// site-local.
							// Store it as a candidate to be returned if
							// site-local address is not subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback
							// non-site-local addresses as candidates,
							// only the first. For subsequent iterations,
							// candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other
				// non-loopback address.
				// Server might have a non-site-local address assigned to its
				// NIC (or it might be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				return candidateAddress;
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost()
			// returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: "
					+ e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

	public static String getWANHost() throws UnknownHostException {
		DefaultHttpClient client = HttpUtils.getDefaultClient();
		Pattern oReg = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
		for (int i = 0; i < CHECK_URLS.length; i++) {
			String url = CHECK_URLS[i];
			HttpGet get = new HttpGet(url);
			try {
				HttpResponse resp = client.execute(get);
				if (resp.getStatusLine().getStatusCode() == 200) {
					Header contentType = resp.getEntity().getContentType();
					byte[] byteArray = EntityUtils.toByteArray(resp.getEntity());
					String charset = HttpUtils.getCharsetOrDefault(contentType, byteArray, "UTF-8");
					String source = new String(byteArray, charset);
					Matcher matcher = oReg.matcher(source);
					if (matcher.find()) {
						String wanHost = matcher.group();
						logger.info("get wan host:" + wanHost + ",from:" + url);
						return wanHost;
					}
				} else {
					EntityUtils.consumeQuietly(resp.getEntity());
				}
			} catch (Exception e) {
				logger.warn("can not get wan host from:" + url, e);
			} finally {
				if (!get.isAborted()) {
					get.abort();
				}
			}

		}
		UnknownHostException unknownHostException = new UnknownHostException("can not get wan host,has try site:"
				+ CHECK_URLS.length);
		throw unknownHostException;
	}
}
