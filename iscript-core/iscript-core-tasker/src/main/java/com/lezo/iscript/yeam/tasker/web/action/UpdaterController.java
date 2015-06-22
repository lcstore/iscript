package com.lezo.iscript.yeam.tasker.web.action;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lezo.iscript.yeam.property.GlobalProperties;

@Controller
@RequestMapping("updater")
public class UpdaterController {
	private static Logger logger = LoggerFactory.getLogger(UpdaterController.class);
	private String agentPath = GlobalProperties.getInstance().getAgentPath();

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

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

	public String getAgentPath() {
		return agentPath;
	}

}
