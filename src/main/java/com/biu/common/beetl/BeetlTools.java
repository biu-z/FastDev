package com.biu.common.beetl;

/**
 * beetl自定义函数注册
 */
public class BeetlTools {
	
	/**   
	 * 前端使用 : ${tool.hello()}
	 * @return String
	*/
	public String hello() {
		return "hello";
	}
	
}
