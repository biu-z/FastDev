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
package com.biu.common.base.controller;

import com.biu.core.constant.Const;
import com.biu.core.constant.ConstShiro;
import com.biu.core.exception.NoPermissionException;
import com.biu.core.exception.NoUserException;
import com.biu.core.meta.IQuery;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.captcha.CaptchaMaker;
import com.biu.core.toolbox.file.BladeFile;
import com.biu.core.toolbox.file.BladeFileKit;
import com.biu.core.toolbox.file.FileMaker;
import com.biu.core.toolbox.grid.GridManager;
import com.biu.core.toolbox.kit.*;
import com.biu.core.toolbox.log.BladeLogManager;
import com.biu.core.toolbox.qrcode.QRCodeMaker;
import com.biu.core.toolbox.support.BeanInjector;
import com.biu.core.toolbox.support.Convert;
import com.biu.core.toolbox.support.WafRequestWrapper;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Blade控制器封装类
 */
public class BladeController {

	protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	/** ============================     requset    =================================================  */

	@Resource
	private HttpServletRequest request;
	
	/**   
	 * 获取request
	*/
	public HttpServletRequest getRequest() {
		return new WafRequestWrapper(this.request);
	}
	
	/**   
	 * 是否ajax
	*/
	public boolean isAjax() {
		String header = getRequest().getHeader("X-Requested-With");
		boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(header);
		return isAjax;
	}
	
	/**   
	 * 是否post
	*/
	public boolean isPost() {
		String method = getRequest().getMethod();
		return StrKit.equalsIgnoreCase("POST", method);
	}

	/**   
	 * 获取String参数
	*/
	public String getParameter(String name) {
		return getRequest().getParameter(name);
	}

	/**   
	 * 获取String参数
	*/
	public String getParameter(String name, String defaultValue) {
		return Convert.toStr(getRequest().getParameter(name), defaultValue);
	}

	/**   
	 * 获取Integer参数
	*/
	public Integer getParameterToInt(String name) {
		return Convert.toInt(getRequest().getParameter(name));
	}

	/**   
	 * 获取Integer参数
	*/
	public Integer getParameterToInt(String name, Integer defaultValue) {
		return Convert.toInt(getRequest().getParameter(name), defaultValue);
	}

	/**   
	 * 获取Long参数
	*/
	public Long getParameterToLong(String name) {
		return Convert.toLong(getRequest().getParameter(name));
	}

	/**   
	 * 获取Long参数
	*/
	public Long getParameterToLong(String name, Long defaultValue) {
		return Convert.toLong(getRequest().getParameter(name), defaultValue);
	}

	/**   
	 * 获取Float参数
	*/
	public Float getParameterToFloat(String name) {
		return Convert.toFloat(getRequest().getParameter(name));
	}

	/**   
	 * 获取Float参数
	*/
	public Float getParameterToFloat(String name, Float defaultValue) {
		return Convert.toFloat(getRequest().getParameter(name), defaultValue);
	}

	/**   
	 * 获取BigDecimal参数
	*/
	public BigDecimal getParameterToBigDecimal(String name) {
		return Convert.toBigDecimal(getRequest().getParameter(name));
	}

	/**   
	 * 获取BigDecimal参数
	*/
	public BigDecimal getParameterToBigDecimal(String name, BigDecimal defaultValue) {
		return Convert.toBigDecimal(getRequest().getParameter(name), defaultValue);
	}
	
	/**   
	 * 获取Encode参数
	*/
	public String getParameterToEncode(String para) {
		return URLKit.encode(getRequest().getParameter(para), CharsetKit.UTF_8);
	}

	/**   
	 * 获取Decode参数
	*/
	public String getParameterToDecode(String para) {
		return URLKit.decode(getRequest().getParameter(para), CharsetKit.UTF_8);
	}

	/**   
	 * 获取ContextPath
	*/
	public String getContextPath() {
		return getRequest().getContextPath();
	}

	/**   
	 * 页面跳转
	 * @param url 路径
	*/
	public String redirect(String url) {
		return StrKit.format("redirect:{}", url);
	}
	
	/**
	 * 对象是否为空
	 * 
	 * @param o String,List,Map,Object[],int[],long[]
	 * @return
	 */
	public boolean isEmpty(Object o) {
		return Func.isEmpty(o);
	}
	
	/**
	 * 对象是否不为空
	 * 
	 * @param o String,List,Map,Object[],int[],long[]
	 * @return
	 */
	public boolean notEmpty(Object o) {
		return !isEmpty(o);
	}
	
	/**
	 * 字符串是否为空白 空白的定义如下： <br>
	 * 1、为null <br>
	 * 2、为不可见字符（如空格）<br>
	 * 3、""<br>
	 * 
	 * @param str 被检测的字符串
	 * @return 是否为空
	 */
	public boolean isBlank(String str) {
		return StrKit.isBlank(str);
	}
	
	/**
	 * 字符串是否为非空白 空白的定义如下： <br>
	 * 1、不为null <br>
	 * 2、不为不可见字符（如空格）<br>
	 * 3、不为""<br>
	 * 
	 * @param str 被检测的字符串
	 * @return 是否为非空
	 */
	public boolean notBlank(String str) {
		return StrKit.notBlank(str);
	}
	
	/**
	 * 格式化文本, {} 表示占位符<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 		通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
	 * 		转义{}： 	format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
	 * 		转义\：		format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
	 * 
	 * @param template 文本模板，被替换的部分用 {} 表示
	 * @param params 参数值
	 * @return 格式化后的文本
	 */
	public String format(String template, Object... params) {
		return StrKit.format(template, params);
	}
	
	/**
	 * 格式化文本，使用 {varName} 占位<br>
	 * map = {a: "aValue", b: "bValue"}
	 * format("{a} and {b}", map)    ---->    aValue and bValue
	 * 
	 * @param template 文本模板，被替换的部分用 {key} 表示
	 * @param map 参数值对
	 * @return 格式化后的文本
	 */
	public String format(String template, Map<?, ?> map) {
		return StrKit.format(template, map);
	}
	
	
	/**
	 * 创建StringBuilder对象
	 */
	public StringBuilder builder() {
		return StrKit.builder();
	}
	
	/**
	 * 创建StringBuilder对象
	 */
	public StringBuilder builder(int capacity) {
		return StrKit.builder(capacity);
	}
	
	/**
	 * 创建StringBuilder对象
	 */
	public StringBuilder build(String... strs) {
		return StrKit.builder(strs);
	}
	
	/**
	 * 克隆对象<br>
	 * 对象必须实现Serializable接口
	 * 
	 * @param obj 被克隆对象
	 * @return 克隆后的对象
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public <T extends Cloneable> T clone(T obj) {
		return ObjectKit.clone(obj);
	}
	
	/**
	 * 克隆对象<br>
	 * 对象必须实现Serializable接口
	 * 
	 * @param obj 被克隆对象
	 * @return 克隆后的对象
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public <T> T clone(T obj) {
		return ObjectKit.clone(obj);
	}
	
	/** ============================     mapping    =================================================  */
	
	/**
	 * 表单值映射为javabean
	 * 
	 * @param beanClass javabean.class
	 * @return T
	 */
	public <T> T mapping(Class<T> beanClass) {
		return (T) BeanInjector.inject(beanClass, getRequest());
	}

	/**
	 * 表单值映射为javabean
	 * 
	 * @param prefix name前缀
	 * @param beanClass javabean.class
	 * @return T
	 */
	public <T> T mapping(String prefix, Class<T> beanClass) {
		return (T) BeanInjector.inject(beanClass, prefix, getRequest());
	}

	/**
	 * 表单值映射为CMap
	 * 
	 * @return CMap
	 */
	public CMap getCMap() {
		return BeanInjector.injectMaps(getRequest());
	}

	/**
	 * 表单值映射为CMap
	 * 
	 * @param prefix  name前缀
	 * @return CMap
	 */
	public CMap getCMap(String prefix) {
		return BeanInjector.injectMaps(prefix, getRequest());
	}
	
	/**===========================     convert    ===============================================  */
	
	/**   
	 * 强转String
	*/
	public String toStr(Object value) {
		return Convert.toStr(value);
	}

	/**   
	 * 强转String
	*/
	public String toStr(Object value, String defaultValue) {
		return Convert.toStr(value, defaultValue);
	}

	/**   
	 * 强转Integer
	*/
	public Integer toInt(Object value) {
		return Convert.toInt(value);
	}

	/**   
	 * 强转Integer
	*/
	public Integer toInt(Object value, Integer defaultValue) {
		return Convert.toInt(value, defaultValue);
	}

	/**   
	 * 强转Long
	*/
	public Long toLong(Object value) {
		return Convert.toLong(value);
	}

	/**   
	 * 强转Long
	*/
	public Long toLong(Object value, Long defaultValue) {
		return Convert.toLong(value, defaultValue);
	}

	/**   
	 * 强转Float
	*/
	public Float toFloat(Object value) {
		return Convert.toFloat(value);
	}

	/**   
	 * 强转Float
	*/
	public Float toFloat(Object value, Float defaultValue) {
		return Convert.toFloat(value, defaultValue);
	}

	/**   
	 * 强转BigDecimal
	*/
	public BigDecimal toBigDecimal(Object value) {
		return Convert.toBigDecimal(value);
	}

	/**   
	 * 强转BigDecimal
	*/
	public BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
		return Convert.toBigDecimal(value, defaultValue);
	}
	
	/**   
	 * 强转String数组
	*/
	public String[] toArray(String str) {
		return Convert.toStrArray(str);
	}
	
	/**   
	 * 强转String数组
	*/
	public String[] toArray(String split, String str) {
		return Convert.toStrArray(split, str);
	}
	
	/**   
	 * 强转Integer数组
	*/
	public Integer[] toIntArray(String str) {
		return Convert.toIntArray(str);
	}
	
	/**   
	 * 强转Integer数组
	*/
	public Integer[] toIntArray(String split, String str) {
		return Convert.toIntArray(split, str);
	}
	
	/**   
	 * encode
	*/
	public String encode(String value) {
		return URLKit.encode(value, CharsetKit.UTF_8);
	}

	/**   
	 * decode
	*/
	public String decode(String value) {
		return URLKit.decode(value, CharsetKit.UTF_8);
	}
	
	/**============================     file    =================================================  */
	
	/**
	 * 获取BladeFile封装类
	 * @param file
	 * @return
	 */
	public BladeFile getFile(MultipartFile file){
		return BladeFileKit.getFile(file);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param file
	 * @param dir
	 * @return
	 */
	public BladeFile getFile(MultipartFile file, String dir){
		return BladeFileKit.getFile(file, dir);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param file
	 * @param dir
	 * @param path
	 * @param virtualPath
	 * @return
	 */
	public BladeFile getFile(MultipartFile file, String dir, String path, String virtualPath){
		return BladeFileKit.getFile(file, dir, path, virtualPath);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param files
	 * @return
	 */
	public List<BladeFile> getFiles(List<MultipartFile> files){
		return BladeFileKit.getFiles(files);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param files
	 * @param dir
	 * @return
	 */
	public List<BladeFile> getFiles(List<MultipartFile> files, String dir){
		return BladeFileKit.getFiles(files, dir);
	}
	
	/**
	 * 获取BladeFile封装类
	 * @param files
	 * @param path
	 * @param virtualPath
	 * @return
	 */
	public List<BladeFile> getFiles(List<MultipartFile> files, String dir, String path, String virtualPath){
		return BladeFileKit.getFiles(files, dir, path, virtualPath);
	}


	/** ============================     ajax    =================================================  */
	
	/**   
	 * 返回ajaxresult
	 * @param data
	 * @return AjaxResult
	*/
	public AjaxResult json(Object data) {
		return new AjaxResult().success(data);
	}
	
	/**   
	 * 返回ajaxresult
	 * @param data
	 * @param message
	 * @return AjaxResult
	*/
	public AjaxResult json(Object data, String message) {
		return json(data).setMessage(message);
	}
	
	/**   
	 * 返回ajaxresult
	 * @param data
	 * @param message
	 * @param code
	 * @return AjaxResult
	*/
	public AjaxResult json(Object data, String message, int code) {
		return json(data, message).setCode(code);
	}
	
	/**   
	 * 返回ajaxresult
	 * @param message
	 * @return AjaxResult
	*/
	public AjaxResult success(String message) {
		return new AjaxResult().addSuccess(message);
	}
	
	/**   
	 * 返回ajaxresult
	 * @param message
	 * @return AjaxResult
	*/
	public AjaxResult error(String message) {
		return new AjaxResult().addError(message);
	}
	
	/**   
	 * 返回ajaxresult
	 * @param message
	 * @return AjaxResult
	*/
	public AjaxResult warn(String message) {
		return new AjaxResult().addWarn(message);
	}
	
	/**   
	 * 返回ajaxresult
	 * @param message
	 * @return AjaxResult
	*/
	public AjaxResult fail(String message) {
		return new AjaxResult().addFail(message);
	}
	
	
	/** ============================     paginate    =================================================  */
	
	/**   
	 * 分页
	 * @param dbName 数据库别名
	 * @param source 数据源
	 * @param intercept 分页拦截器
	 * @return Object
	*/
	private Object basepage(String dbName, String source, IQuery intercept){
		Integer page = getParameterToInt("page", 1);
		Integer rows = getParameterToInt("rows", 10);
		String where = getParameter("where", StrKit.EMPTY);
		String sidx =  getParameter("sidx", StrKit.EMPTY);
		String sord =  getParameter("sord", StrKit.EMPTY);
		String sort =  getParameter("sort", StrKit.EMPTY);
		String order =  getParameter("order", StrKit.EMPTY);
		if (StrKit.notBlank(sidx)) {
			sort = sidx + " " + sord + (StrKit.notBlank(sort) ? ("," + sort) : StrKit.EMPTY);
		}
		Object grid = GridManager.paginate(dbName, page, rows, source, where, sort, order, intercept, this);
		return grid;
	}
	

	/**   
	 * 分页
	 * @param source 数据源
	 * @return Object
	*/
	public Object paginate(String source){
		return basepage(null, source, null);
	}
	

	/**   
	 * 分页
	 * @param source 数据源
	 * @param intercept 分页拦截器
	 * @return Object
	*/
	public Object paginate(String source, IQuery intercept){
		return basepage(null, source, intercept);
	}
	

	/**   
	 * 分页
	 * @param dbName 数据库别名
	 * @param source 数据源
	 * @return Object
	*/
	public Object paginate(String dbName, String source){
		return basepage(dbName, source, null);
	}
	

	/**   
	 * 分页
	 * @param dbName 数据库别名
	 * @param source 数据源
	 * @param intercept 分页拦截器
	 * @return Object
	*/
	public Object paginate(String dbName, String source, IQuery intercept){
		return basepage(dbName, source, intercept);
	}
	
	/** ============================      maker    ===================================================  */
	
	/**   
	 * 返回验证码
	*/
	public void makeCaptcha(HttpServletResponse response) {
		CaptchaMaker.init(response).start();
	}
	
	/**   
	 * 校验验证码
	*/
	public boolean validateCaptcha(HttpServletResponse response, String value) {
		return CaptchaMaker.validate(getRequest(), response, value);
	}
	
	/**   
	 * 返回二维码
	 * 
	 * @param content 二维码携带内容
	 * @param width 二维码宽度
	 * @param height 二维码高度
	*/
	public void makeQRCode(HttpServletResponse response, String content, int width, int height) {
		QRCodeMaker.init(response, content, width, height).start();
	}
	
	/**   
	 * 返回二维码
	 * 
	 * @param content 二维码携带内容
	 * @param width 二维码宽度
	 * @param height 二维码高度
	 * @param errorCorrectionLevel 带有纠错级别参数的构造方法，纠错能力从高到低共有四个级别：'H'、'Q'、'M'、'L'
	*/
	public void makeQRCode(HttpServletResponse response, String content, int width, int height, ErrorCorrectionLevel errorCorrectionLevel) {
		QRCodeMaker.init(response, content, width, height, errorCorrectionLevel).start();
	}
	
	/**   
	 * 返回二维码
	 * 
	 * @param content 二维码携带内容
	 * @param width 二维码宽度
	 * @param height 二维码高度
	 * @param errorCorrectionLevel 带有纠错级别参数的构造方法，纠错能力从高到低共有四个级别：'H'、'Q'、'M'、'L'
	*/
	public void makeQRCode(HttpServletResponse response, String content, int width, int height, char errorCorrectionLevel) {
		QRCodeMaker.init(response, content, width, height, errorCorrectionLevel).start();
	}
	
	/**   
	 * 返回文件
	*/
	public void makeFile(HttpServletResponse response, File file) {
		FileMaker.init(getRequest(), response, file).start();
	}
	
	/** ============================     exception    =================================================  */

	@ResponseBody
	@ExceptionHandler(Exception.class)
	public Object exceptionHandler(Exception ex, HttpServletResponse response, HttpServletRequest request) throws IOException {
		AjaxResult result = new AjaxResult();
		String url = Const.ERROR_500;
		String msg = ex.getMessage();
		Object resultModel = null;
		try {
			if (ex.getClass() == HttpRequestMethodNotSupportedException.class) {
				url = Const.ERROR_500;// 请求方式不允许抛出的异常,后面可自定义页面
			} else if (ex.getClass() == NoPermissionException.class) {
				url = Const.NOPERMISSION_PATH;// 无权限抛出的异常
				msg = ConstShiro.NO_PERMISSION;
			} else if (ex.getClass() == NoUserException.class) {
				url = Const.LOGIN_REALPATH;// session过期抛出的异常
				msg = ConstShiro.NO_USER;
			}
			if (isAjax() || isPost()) {
				result.addFail(msg);
				resultModel = result;
			} else {
				ModelAndView view = new ModelAndView(url);
				view.addObject("error", msg);
				view.addObject("class", ex.getClass());
				view.addObject("method", request.getRequestURI());
				resultModel = view;
			}
			try {
				if(StrKit.notBlank(msg)){
					BladeLogManager.doLog("异常日志", msg, false);
				}
			} catch (Exception logex) {
				LogKit.logNothing(logex);
			}
			return resultModel;
		} catch (Exception exception) {
//			LOGGER.error(exception.getMessage(), exception);
			return resultModel;
		} finally {
//			LOGGER.error(msg, ex);
		}
	}

}
