package com.lezo.iscript.yeam.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.http.HttpClientManager;
import com.lezo.iscript.yeam.mina.utils.ClientPropertiesUtils;
import com.lezo.iscript.yeam.service.ConfigParser;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class ConfigOpenShiftClientValidator implements ConfigParser {
	private static Logger logger = LoggerFactory.getLogger(ConfigOpenShiftClientValidator.class);
	private DefaultHttpClient client = HttpClientManager.getDefaultHttpClient();
	private Timer timer;

	public ConfigOpenShiftClientValidator() {
		final String clientName = ClientPropertiesUtils.getProperty("name");
		if (clientName.indexOf("openshift") >= 0) {
			final Map<String, String> name2UrlMap = new HashMap<String, String>();
			name2UrlMap.put("openshift", "http://osclient-iscript.rhcloud.com/");
			name2UrlMap.put("openshift.d1001", "http://d1001-dlink.rhcloud.com/");
			name2UrlMap.put("openshift.p1001", "http://p1001-p1001.rhcloud.com/");
			name2UrlMap.put("openshift.p1001", "http://p1002-p1002.rhcloud.com/");
			this.timer = new Timer();
			long delay = 60 * 1000L;
			long period = 5 * 60 * 1000L;
			this.timer.schedule(new TimerTask() {

				@Override
				public void run() {
					for (Entry<String, String> entry : name2UrlMap.entrySet()) {
						try {
							if (clientName.equals(entry.getKey())) {
								continue;
							}
							HttpGet get = new HttpGet(entry.getValue());
							HttpResponse respone = client.execute(get);
							StatusLine statueLine = respone.getStatusLine();
							String html = EntityUtils.toString(respone.getEntity());
							logger.info("validate,url:" + entry.getValue() + ",status:" + statueLine.getStatusCode() + ",msg:" + statueLine.getReasonPhrase() + ",html:" + html);
						} catch (Exception e) {
							logger.warn("url:" + entry.getValue() + ",cause:", e);
						}
					}

				}
			}, delay, period);
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String doParse(TaskWritable task) throws Exception {
		return "";
	}

	@Override
	protected void finalize() throws Throwable {
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
		super.finalize();
	}

}
