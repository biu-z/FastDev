/**
 * Copyright (c) 2015-2017, Chill Zhuang 庄骞 (biu@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biu.core.constant;

public interface ConstShiro {
	/**
	 * 超级管理员
	 */
	String ADMINISTRATOR = "administrator";
	/**
	 * 普通管理员
	 */
	String ADMIN = "admin";
	/**
	 * 用户
	 */
	String USER = "user";
	/**
	 * 定义无权操作信息
	 */
	String NO_PERMISSION = "当前用户无权操作!";
	
	/**
	 * 定义session过期信息
	 */
	String NO_USER = "无法获取当前用户,session已过期,请重新登录!";
	
	/**
	 * 定义无请求方法信息
	 */
	String NO_METHOD = "请求方法错误!";	
	
	/**
	 * 定义无权限转向
	 */
	String REDIRECT_UNAUTH = "redirect:/unauth";
}
