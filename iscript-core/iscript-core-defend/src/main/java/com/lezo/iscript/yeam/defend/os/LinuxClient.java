package com.lezo.iscript.yeam.defend.os;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;

public class LinuxClient implements Clientable {
	private static Logger log = Logger.getLogger(LinuxClient.class);

	public void closeClient(List<String> clientIds) {
		log.info("start to close:" + clientIds);
		if (CollectionUtils.isEmpty(clientIds)) {
			return;
		}
		// String taskkill /F /T /PID 5488 /PID 1480
		int argsNum = 2 + clientIds.size();
		String[] cmdArray = new String[argsNum];
		int index = -1;
		cmdArray[++index] = "kill";
		cmdArray[++index] = "-9";
		for (String pid : clientIds) {
			cmdArray[++index] = pid;
		}
		ProcessBuilder pBuilder = new ProcessBuilder();
		pBuilder = pBuilder.command(cmdArray);
		try {
			Process process = pBuilder.start();
			List<?> inList = IOUtils.readLines(process.getInputStream(), ClientConstant.CMD_CHARSET);
			for (Object o : inList) {
				log.info("closeClient:" + o);
			}
			process.destroy();
		} catch (IOException e) {
			log.warn("Client" + clientIds + " fail to close,cause:", e);
		}
		log.info("end to close:" + clientIds);
	}

	public boolean hasClient(String clientId) {
		log.info("start to hasClient:" + clientId);
		if (StringUtils.isEmpty(clientId)) {
			return false;
		}
		String[] cmdArray = new String[] { "ps", "-p", clientId };
		ProcessBuilder pBuilder = new ProcessBuilder();
		pBuilder = pBuilder.command(cmdArray);
		try {
			Process process = pBuilder.start();
			List<?> taskList = IOUtils.readLines(process.getInputStream(), ClientConstant.CMD_CHARSET);
			for (Object o : taskList) {
				log.info("hasClient:" + o);
			}
			if (taskList != null) {
				for (Object taskLine : taskList) {
					if (taskLine.toString().contains(clientId)) {
						log.info("end to hasClient:true");
						return true;
					}
				}
			}
			process.destroy();
		} catch (IOException e) {
			log.warn("find client[" + clientId + "],cause:", e);
		}
		log.info("end to hasClient:false");
		return false;
	}

	public Process newClient() throws IOException {
		log.info("start to newClient");
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File workFile = new File(clientPath, ClientConstant.CLIENT_WORK_SPACE);
		ProcessBuilder pBuilder = new ProcessBuilder();
		File clientFile = new File(workFile, "client" + File.separator + "client.jar");
		String[] cmdArray = new String[] { "java",
				"-Dyeam.name=" + ObjectBuilder.findObject(ClientConstant.CLIENT_NAME),
				"-Dyeam.client.path=" + ObjectBuilder.findObject(ClientConstant.CLIENT_PATH),
				"-Dyeam.tasker.host=" + ObjectBuilder.findObject(ClientConstant.CLIENT_TASKER_HOST), "-jar",
				clientFile.getAbsolutePath() };
		pBuilder = pBuilder.command(cmdArray);
		Process process = pBuilder.start();
		log.info("end to newClient:" + (process == null) == null ? "null" : "sdfs");
		return process;
	}

	public List<String> findClients() throws IOException {
		log.info("start to findClients");
		List<String> sClientList = new ArrayList<String>();
		ProcessBuilder pBuilder = new ProcessBuilder();
		String[] cmdArray = new String[] { "ps", "-ef|grep ", "'\\-Dyeam\\.name='" };
		pBuilder = pBuilder.command(cmdArray);
		Process process = pBuilder.start();
		String regex = "^.*?([0-9]+).*?-Dyeam.name=(.*?)(-Dyeam.client.path=).*?(-Dyeam.tasker.host=).*";
		InputStream in = null;
		Pattern oPattern = Pattern.compile(regex);
		try {
			in = process.getInputStream();
			List<?> lineList = IOUtils.readLines(in, ClientConstant.CMD_CHARSET);
			for (Object line : lineList) {
				String sLine = line.toString();
				Matcher matcher = oPattern.matcher(sLine);
				if (!matcher.find()) {
					continue;
				}
				String sClient = matcher.group(1).trim() + "," + matcher.group(2).trim();
				sClientList.add(sClient);
			}
			process.destroy();
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		log.info("end to findClients:" + sClientList);
		return sClientList;
	}
}
