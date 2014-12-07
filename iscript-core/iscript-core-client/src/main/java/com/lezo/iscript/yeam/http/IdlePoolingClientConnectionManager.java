package com.lezo.iscript.yeam.http;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdlePoolingClientConnectionManager extends PoolingClientConnectionManager {
	private static Logger logger = LoggerFactory.getLogger(IdlePoolingClientConnectionManager.class);

	public IdlePoolingClientConnectionManager(SchemeRegistry schreg, DnsResolver dnsResolver) {
		super(schreg, dnsResolver);
	}

	@Override
	public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
		IdleConnectionMonitorThread.ensureRunning(this, 25, 30);
		return super.requestConnection(route, state);
	}

	private static class IdleConnectionMonitorThread extends Thread {
		private final PoolingClientConnectionManager manager;
		private final int idleTimeoutSeconds;
		private final int checkIntervalSeconds;
		private static IdleConnectionMonitorThread thread = null;

		public IdleConnectionMonitorThread(PoolingClientConnectionManager manager, int idleTimeoutSeconds, int checkIntervalSeconds) {
			this.manager = manager;
			this.idleTimeoutSeconds = idleTimeoutSeconds;

			this.checkIntervalSeconds = checkIntervalSeconds;
		}

		public static synchronized void ensureRunning(PoolingClientConnectionManager manager, int idleTimeoutSeconds, int checkIntervalSeconds) {
			if (thread == null) {
				thread = new IdleConnectionMonitorThread(manager, idleTimeoutSeconds, checkIntervalSeconds);
				thread.start();
			}
		}

		public void run() {
			try {
				synchronized (this) {
					super.wait(this.checkIntervalSeconds * 1000);
				}
				PoolStats poolStats = this.manager.getTotalStats();
				logger.info("close.expired,PoolStats:{}", poolStats.toString());
				this.manager.closeExpiredConnections();
				this.manager.closeIdleConnections(this.idleTimeoutSeconds, TimeUnit.SECONDS);
				synchronized (IdleConnectionMonitorThread.class) {
					if (this.manager.getTotalStats().getAvailable() == 0) {
						thread = null;
						return;
					}
				}
			} catch (InterruptedException e) {
				thread = null;
				logger.warn("close.idle", e);
			}
		}
	}
}
