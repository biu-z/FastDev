package com.biu.system.controller;

import com.biu.system.mapper.DeptMapper;
import com.biu.system.mapper.GenerateMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/biu")
public class BiuController{

	private static String BASE_PATH = "/system/biu/";
	private static String CODE = "attach";
	private static String PREFIX = "blade_biu";

	@Resource
	private DeptMapper deptMapper;
	@Resource
	private GenerateMapper generateMapper;

	/**
	 * 跳转列表页
	 * @param mm
	 * @return
	 */
	@RequestMapping("/")
	@ResponseBody
	public Object index(ModelMap mm) {
		return deptMapper.selectAll();
	}

	@RequestMapping("/1")
	@ResponseBody
	public Object index1(ModelMap mm) {
		return generateMapper.selectAll();
	}

}
