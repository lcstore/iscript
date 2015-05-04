package com.lezo.iscript.yeam.resultmgr;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.spring.context.SpringBeanUtils;
import com.lezo.iscript.yeam.buffer.HttpClientFactory;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryDescriptor;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryLockUtils;
import com.lezo.iscript.yeam.resultmgr.directory.DirectoryTracker;
import com.lezo.rest.data.BaiduPcsRester;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;

public class DataFileProducerMainTest {
	private static Logger logger = LoggerFactory.getLogger(DataFileProducerMainTest.class);

	public static void main(String[] args) throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		String dataPath = "/apps/idocs/iscript/20150427/ConfigProxyDetector/b0624cf5-29b9-4422-8789-da37f1eda7d8";
		String bucketName = "idocs";
		DirectoryDescriptor event = new DirectoryDescriptor(dataPath, bucketName,"baidu.com");
		Date date = DateUtils.parseDate("2015-04-27", "yyyy-MM-dd");
		event.setCreateTime(date);
		Calendar c = Calendar.getInstance();
		c.setTime(event.getCreateTime());
		c.add(Calendar.MINUTE, -10);
		DirectoryTracker tracker = new DirectoryTracker(event);
		tracker.setStamp(c.getTimeInMillis());
		DirectoryLockUtils.addLock(event.getDirectoryKey(), new ReentrantLock());
		
		ClientRest clientRest = new ClientRest();
		clientRest.setBucket(bucketName);
		clientRest.setDomain(event.getDomain());
		clientRest.setCapacity(10);
		
		BaiduPcsRester rester = new BaiduPcsRester();
		rester.setAccessToken("21.451868998d44920386990b9f5e6624b4.2592000.1431009003.4026763474-2920106");
		rester.setBucket(clientRest.getBucket());
		rester.setDomain(clientRest.getDomain());
		rester.setClient(HttpClientFactory.createHttpClient());
		clientRest.setRester(rester);
		ClientRestFactory.getInstance().put(clientRest);
		while (true) {
			ThreadPoolExecutor executor = (ThreadPoolExecutor) SpringBeanUtils.getBean("fileProduceExecutor");
			executor.execute(new DataFileProducerTest(tracker));
			TimeUnit.SECONDS.sleep(60000);
			c.add(Calendar.MINUTE, 60);
			tracker.getDescriptor().setCreateTime(c.getTime());
		}
	}
}