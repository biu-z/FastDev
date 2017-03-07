package com.biu.system.controller;

import com.biu.common.base.controller.BladeController;
import com.biu.core.constant.Const;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorController extends BladeController {

	@RequestMapping("/error400")
	public ModelAndView error400(){
		ModelAndView view = new ModelAndView(Const.ERROR_400);
		return view;
	}
	
	@RequestMapping("/error401")
	public ModelAndView error401(){
		ModelAndView view = new ModelAndView(Const.ERROR_401);
		return view;
	}
	
	@RequestMapping("/error404")
	public ModelAndView error404(){
		ModelAndView view = new ModelAndView(Const.ERROR_404);
		return view;
	}
	
	@RequestMapping("/error500")
	public ModelAndView error500(){
		ModelAndView view = new ModelAndView(Const.ERROR_500);
		return view;
	}
}
