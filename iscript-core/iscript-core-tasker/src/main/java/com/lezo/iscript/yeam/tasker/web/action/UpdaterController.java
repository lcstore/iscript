package com.lezo.iscript.yeam.tasker.web.action;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("updater")
public class UpdaterController {
	private static Logger logger = LoggerFactory.getLogger(UpdaterController.class);
	@Value("#{settings['agent_path']}")
	private String agentPath;

	@ResponseBody
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "version")
	public String getVersion(@RequestParam("name") String name) {
		File agentFile = new File(getAgentPath());
		if (!agentFile.exists()) {
			logger.warn("not exist agent file:" + agentPath);
			return "";
		}
		String version = "" + agentFile.lastModified();
		return version;
	}

	@ResponseBody
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "entity")
	public byte[] getEntity(@RequestParam("name") String name, HttpServletResponse response) throws IOException {
		File agentFile = new File(getAgentPath());
		String version = "" + agentFile.lastModified();
		logger.info("getEntity from updater:" + name + ",current version:" + version);
		byte[] fileBytes = null;
		try {
			fileBytes = FileUtils.readFileToByteArray(agentFile);
			response.setStatus(HttpStatus.OK.value());
		} catch (IOException e) {
			logger.warn("read file:" + agentFile + ",cause:", e);
			response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), "can not read agent.jar");
		}
		return fileBytes;
	}

	// @Value("${agent_path}")
	public void setAgentPath(String agentPath) {
		this.agentPath = agentPath;
	}

	public String getAgentPath() {
		return agentPath;
	}

}
