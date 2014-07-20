package com.lezo.iscript.yeam.tasker.web.action;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lezo.iscript.service.crawler.dto.TaskConfigDto;
import com.lezo.iscript.service.crawler.service.TaskConfigService;

@Controller
@RequestMapping("configmgr")
public class ConfigmgrAction {
	private Logger log = LoggerFactory.getLogger(ConfigmgrAction.class);
	@Autowired
	private TaskConfigService taskConfigService;

	@RequestMapping("listConfigs")
	public String listConfigs(Model model) throws Exception {
		Date afterStamp = new Date(0);
		List<TaskConfigDto> configList = taskConfigService.getTaskConfigDtos(afterStamp, null);
		model.addAttribute("siteCount", 1);
		int configSize = configList.size();
		model.addAttribute("configList", configList);
		model.addAttribute("configSize", configSize);
		return "/listConfigs";
	}

	@RequestMapping("deleteConfig")
	@ResponseBody
	public String deleteConfig(Model model, String type) throws Exception {
		taskConfigService.deleteConfig(type);
		return "OK";
	}

	@RequestMapping("addConfig")
	public ModelAndView addConfig(@RequestParam("configType") String configType,
			@RequestParam("destType") int destType, @RequestParam("configFile") MultipartFile file, Model model)
			throws Exception {
		ModelAndView mav = new ModelAndView("redirect:/configmgr/listConfigs");
		Integer siteId = 1;
		if (!isValidate(siteId, configType, file)) {
			return mav;
		}
		String content = "";
		InputStream in = null;
		BufferedReader bReader = null;
		try {
			in = file.getInputStream();
			bReader = new BufferedReader(new InputStreamReader(in));
			StringBuffer configBuffer = new StringBuffer();
			while (bReader.ready()) {
				String line = bReader.readLine();
				configBuffer.append(line);
				configBuffer.append("\n");
			}
			content = configBuffer.toString();
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(bReader);
		}
		TaskConfigDto confDto = new TaskConfigDto();
		confDto.setSource(file.getOriginalFilename());
		confDto.setConfig(content);
		confDto.setType(configType);
		confDto.setDestType(destType);
		confDto.setCreateTime(new Date());
		confDto.setUpdateTime(confDto.getCreateTime());
		confDto.setStatus(TaskConfigDto.STATUS_ENABLE);
		TaskConfigDto hasConfDto = taskConfigService.getTaskConfig(configType);
		if (hasConfDto != null) {
			confDto.setId(hasConfDto.getId());
			confDto.setCreateTime(hasConfDto.getCreateTime());
			taskConfigService.updateOne(confDto);
		} else {
			List<TaskConfigDto> dtoList = new ArrayList<TaskConfigDto>(1);
			dtoList.add(confDto);
			taskConfigService.batchInsert(dtoList);
		}
		return mav;
	}

	private boolean isValidate(Integer siteId, String type, MultipartFile file) {
		if (siteId == null) {
			log.warn("Can not found siteId for type:" + type);
			return false;
		}
		if (file == null || file.getName() == null) {
			return false;
		}
		return true;
	}

}
