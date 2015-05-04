package com.lezo.iscript.yeam.tasker.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("taskmgr")
public class TaskmgrAction {

	@RequestMapping("build")
	@ResponseBody
	public String build(Model model, String type) throws Exception {
		return "OK";
	}
}
