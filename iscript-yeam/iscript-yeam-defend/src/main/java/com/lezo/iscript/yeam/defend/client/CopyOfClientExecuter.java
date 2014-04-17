package com.lezo.iscript.yeam.defend.client;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.defend.os.Clientable;
import com.lezo.iscript.yeam.defend.os.LinuxClient;
import com.lezo.iscript.yeam.defend.os.WindowClient;

public class CopyOfClientExecuter implements Closeable {
	private static Logger log = Logger.getLogger(CopyOfClientExecuter.class);
	private final Clientable clientable;
	private long lastActive = System.currentTimeMillis();
	private Process clientProcess;
	private String clientId;
	private volatile boolean isClose = false;

	private CopyOfClientExecuter() {
		String osName = System.getProperty("os.name");
		osName = osName.toUpperCase();
		if (osName.startsWith("LINUX")) {
			clientable = new LinuxClient();
		} else if (osName.startsWith("WIN")) {
			clientable = new WindowClient();
		} else {
			clientable = new WindowClient();
		}
	}

	private static final class InstanceHolder {
		private static final CopyOfClientExecuter INSTANCE = new CopyOfClientExecuter();
	}

	public static CopyOfClientExecuter getClientExecuter() {
		return InstanceHolder.INSTANCE;
	}

	public void doExecute() {
		try {
			List<String> clientList = clientable.findClients();
			List<String> clientIdList = getMatchClients(clientList);
			if (!CollectionUtils.isEmpty(clientIdList)) {
				long start = System.currentTimeMillis();
				clientable.closeClient(clientIdList);
				long cost = System.currentTimeMillis() - start;
				log.info("Client" + clientIdList + " has closed,cost:" + cost + "ms");
				setClientId(clientIdList.get(clientIdList.size() - 1));
			}
			long delay = 0;
			long period = 30 * 1000;
			new Timer("Excute.Timer").schedule(new TimerTask() {
				private volatile boolean running = false;

				@Override
				public void run() {
					if (running) {
						return;
					}
					try {
						running = true;
						if (clientable.hasClient(getClientId())) {
							return;
						}
						if (!hasVersion()) {
							log.warn("Can not found version in worskspace.");
							return;
						}
						if (!hasProperties()) {
							log.warn("Can not found [client.properties].fail to start client.");
							return;
						}
						loadPropeties();
						assertPropeties();
						String newClientId = null;
						while (newClientId == null) {
							try {
								Process process = clientable.newClient();
								setClientProcess(process);
								List<String> clientList = clientable.findClients();
								List<String> clientIdList = getMatchClients(clientList);
								newClientId = clientIdList.isEmpty() ? null : clientIdList.get(0);
							} catch (IOException e) {
								log.warn("", e);
							}

						}
						log.info("Client[" + getClientId() + "] had been shutdown.Start up new Client[" + newClientId
								+ "]");
						setClientId(newClientId);
					} catch (Exception e) {
						log.warn("", e);
					} finally {
						running = false;
					}
				}
			}, delay, period);
			period = 1000;
			new Timer(true).schedule(new TimerTask() {
				private volatile boolean running = false;

				@Override
				public void run() {
					if (running || isClose) {
						return;
					}
					try {
						running = true;
						Process process = getClientProcess();
						if (process == null) {
							return;
						}
						InputStream in = getClientProcess().getInputStream();
						if (in.available() > 0) {
							IOUtils.readLines(in, ClientConstant.CLIENT_CHARSET);
						}
						InputStream err = getClientProcess().getErrorStream();
						if (err.available() > 0) {
							IOUtils.readLines(err, ClientConstant.CLIENT_CHARSET);
						}
					} catch (Exception e) {
						log.warn("", e);
					} finally {
						running = false;
					}
				}
			}, 0, period);
		} catch (IOException e) {
			log.warn("", e);
		}
	}

	protected void assertPropeties() {
		assertArgument(ClientConstant.CLIENT_NAME);
		assertArgument(ClientConstant.CLIENT_PATH);
		assertArgument(ClientConstant.CLIENT_TASKER_HOST);
	}

	private void assertArgument(String name) {
		if (ObjectBuilder.findObject(name) == null) {
			throw new IllegalArgumentException("Can not found[" + name + "],please set <-D" + name + "=something>");
		}
	}

	protected void loadPropeties() {
		String proName = "client.properties";
		InputStream in = null;
		BufferedInputStream inStream = null;
		try {
			Object pathObj = ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
			if (pathObj != null) {
				File workFile = new File(pathObj.toString(), ClientConstant.CLIENT_WORK_SPACE);
				File properity = new File(workFile, proName);
				if (properity.exists()) {
					in = new FileInputStream(properity);
				}
			}
			if (in == null) {
				throw new FileNotFoundException("Can not found [" + proName + "] in work space.");
			}
			Properties pro = new Properties();
			inStream = new BufferedInputStream(in);
			pro.load(inStream);
			for (Entry<Object, Object> entry : pro.entrySet()) {
				String key = entry.getKey().toString();
				if (ObjectBuilder.findObject(key) != null) {
					ObjectBuilder.deleteObject(key);
				}
				ObjectBuilder.newObject(key, entry.getValue().toString());
			}
		} catch (IOException e) {
			log.warn("", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(inStream);
		}

	}

	protected boolean hasProperties() {
		String proName = "client.properties";
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File clientProperity = new File(clientPath, ClientConstant.CLIENT_WORK_SPACE + File.separator + proName);
		return clientProperity != null;
	}

	protected boolean hasVersion() {
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File hasVersion = getVersion(new File(clientPath, ClientConstant.CLIENT_WORK_SPACE));
		return hasVersion != null;
	}

	private File getVersion(File file) {
		File[] vFiles = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches("^[0-9]+.version$");
			}
		});
		if (vFiles == null) {
			return null;
		}
		File newVersion = null;
		long maxVersion = 0;
		for (File vFile : vFiles) {
			if (newVersion == null) {
				newVersion = vFile;
				String version = newVersion.getName().replace(".version", "");
				maxVersion = Long.valueOf(version);
			} else {
				String version = vFile.getName().replace(".version", "");
				Long curVersion = Long.valueOf(version);
				if (maxVersion < curVersion) {
					newVersion = vFile;
					maxVersion = curVersion;
				}
			}
		}
		return newVersion;
	}

	private List<String> getMatchClients(List<String> clientList) {
		List<String> matchClents = new ArrayList<String>(clientList.size());
		String clientName = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_NAME);
		for (String client : clientList) {
			String[] clientArr = client.split(",");
			if (clientArr != null && clientArr.length == 2) {
				if (clientArr[1].trim().equals(clientName)) {
					matchClents.add(clientArr[0].trim());
				}
			}
		}
		return matchClents;
	}

	public long getLastActive() {
		return lastActive;
	}

	public void setLastActive(long lastActive) {
		this.lastActive = lastActive;
	}

	public Process getClientProcess() {
		return clientProcess;
	}

	public void setClientProcess(Process clientProcess) {
		this.clientProcess = clientProcess;
	}

	public String getClientId() {
		return clientId;
	}

	private void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public void close() throws IOException {
		if (getClientId() != null) {
			List<String> clientIds = new ArrayList<String>(1);
			clientIds.add(getClientId());
			clientable.closeClient(clientIds);
		}
		Process process = getClientProcess();
		if (process != null) {
			process.destroy();
		}
		isClose = true;
	}

	public void start() {
		isClose = false;
	}

}
