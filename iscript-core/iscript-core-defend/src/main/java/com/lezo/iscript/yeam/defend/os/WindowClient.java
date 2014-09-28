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

public class WindowClient implements Clientable {
	private static Logger log = Logger.getLogger(WindowClient.class);

	@Override
	public void closeClient(List<String> clientIds) throws IOException {
		if (CollectionUtils.isEmpty(clientIds)) {
			return;
		}
		// String taskkill /F /T /PID 5488 /PID 1480
		int argsNum = 3 + 2 * clientIds.size();
		String[] cmdArray = new String[argsNum];
		int index = -1;
		cmdArray[++index] = "taskkill";
		cmdArray[++index] = "/F";
		cmdArray[++index] = "/T";
		for (String pid : clientIds) {
			cmdArray[++index] = "/PID";
			cmdArray[++index] = pid;
		}
		ProcessBuilder pBuilder = new ProcessBuilder();
		pBuilder = pBuilder.command(cmdArray);
		try {
			Process process = pBuilder.start();
			IOUtils.readLines(process.getInputStream(), ClientConstant.CMD_CHARSET);
			process.destroy();
		} catch (IOException e) {
			log.warn("Client" + clientIds + " fail to close,cause:", e);
		}

	}

	@Override
	public boolean hasClient(String clientId) throws IOException {
		if (StringUtils.isEmpty(clientId)) {
			return false;
		}
		// tasklist /v /FI "PID eq 5156"
		String[] cmdArray = new String[] { "cmd.exe", "/C", "tasklist", "/v", "/FI", "\"PID eq " + clientId + "\"" };
		ProcessBuilder pBuilder = new ProcessBuilder();
		pBuilder = pBuilder.command(cmdArray);
		try {
			Process process = pBuilder.start();
			List<?> taskList = IOUtils.readLines(process.getInputStream(), ClientConstant.CMD_CHARSET);
			if (taskList != null) {
				for (Object taskLine : taskList) {
					if (taskLine.toString().contains(clientId)) {
						return true;
					}
				}
			}
			process.destroy();
		} catch (IOException e) {
			log.warn("find client[" + clientId + "],cause:", e);
		}
		return false;
	}

	@Override
	public Process newClient() throws IOException {
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File workFile = new File(clientPath, ClientConstant.CLIENT_WORK_SPACE);
		ProcessBuilder pBuilder = new ProcessBuilder();
		File clientFile = new File(workFile, "client" + File.separator + "client.jar");
		int size = 8;
		String[] cmdArray = new String[size];
		int index = -1;
		cmdArray[++index] = "cmd.exe";
		cmdArray[++index] = "/C";
		cmdArray[++index] = "java";
		cmdArray[++index] = "-Dyeam.name=" + ObjectBuilder.findObject(ClientConstant.CLIENT_NAME);
		cmdArray[++index] = "-Dyeam.client.path=" + ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		cmdArray[++index] = "-Dyeam.tasker.host=" + ObjectBuilder.findObject(ClientConstant.CLIENT_TASKER_HOST);
		cmdArray[++index] = "-jar";
		cmdArray[++index] = clientFile.getAbsolutePath();
		pBuilder = pBuilder.command(cmdArray);
		return pBuilder.start();
	}

	@Override
	public List<String> findClients() throws IOException {
		List<String> sClientList = new ArrayList<String>();
		ProcessBuilder pBuilder = new ProcessBuilder();
		String[] cmdArray = new String[] { "cmd.exe", "/C", "jps -v" };
		pBuilder = pBuilder.command(cmdArray);
		Process process = pBuilder.start();
		String regex = "(^[0-9]+).*?-Dyeam.name=(.*?)(-Dyeam.client.path=).*?(-Dyeam.tasker.host=).*";
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
		return sClientList;
	}

}
