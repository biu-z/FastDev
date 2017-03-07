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


public interface Const {

	/**
	 * 当前版本号
	 */
	String FRAMEWORK_VERSION = "1.0";

	/**
	 * 配置文件
	 */
	String PROPERTY_FILE = "config.properties";

	/**
	 * SQL配置文件
	 */
	String SQL_PROPERTIY = "sql.properties";

	/**
	 * 字符编码
	 */
	String ENCODING = "UTF-8";

	/**
	 * view层根目录
	 */
	String BASE_VIEWPATH = "/WEB-INF/view/";
	
	/**
	 * 登陆地址(接口)
	 */
	String LOGIN_PATH = "/login/";
	
	/**
	 * 登陆地址(路径)
	 */
	String LOGIN_REALPATH = "/login.html";
	
	/**
	 * 首页面地址(路径)
	 */
	String INDEX_REALPATH = "/index.html";
	
	/**
	 * 主页面地址(路径)
	 */
	String INDEX_MAIN_REALPATH = "/main.html";
	
	/**
	 * 400页面地址
	 */
	String ERROR_400 = "/error/400.html";
	
	/**
	 * 401页面地址
	 */
	String ERROR_401 = "/error/401.html";
	
	/**
	 * 404页面地址
	 */
	String ERROR_404 = "/error/404.html";
	
	/**
	 * 403页面地址
	 */
	String ERROR_403 = "/error/403.html";
	
	/**
	 * 500页面地址
	 */
	String ERROR_500 = "/error/500.html";
	
	/**
	 * 无权限地址
	 */
	String NOPERMISSION_PATH = "/error/permission.html";
	
	/**
	 * 下载地址
	 */
	String DOWNLOAD_PATH = "/download";

	/**
	 * 定义用户sessionkey
	 */
	String USER_SESSION_KEY = "user";

	/**
	 * 定义日志参数
	 */
	String PARA_LOG_CODE = "101";

	/**
	 * 定义乐观锁字段
	 */
	String OPTIMISTIC_LOCK = "VERSION";

	/**
	 * 定义分页插件的排序字段
	 */
	String ORDER_BY_STR = "orderBy";

	/**
	 * 定义条件封装的值
	 */
	String SQL_EX_STR = "sqlEx";
	
	/**
	 * 定义跳转常量
	 */
	String REDIRECT = "redirect:";

}
