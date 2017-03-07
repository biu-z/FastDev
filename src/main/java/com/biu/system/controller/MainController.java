package com.biu.system.controller;

import com.biu.common.base.controller.BaseController;
import com.biu.core.constant.Const;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class MainController extends BaseController {

	@GetMapping
	public String index() {
		return Const.INDEX_MAIN_REALPATH;
	}

}
