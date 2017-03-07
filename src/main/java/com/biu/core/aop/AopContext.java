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
package com.biu.core.aop;

import com.biu.common.base.controller.BladeController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 业务拦截器上下文
 */
public class AopContext {

	/**
	 * 当前控制器
	 */
	private BladeController ctrl;

	/**
	 * 视图
	 */
	private ModelAndView view;

	/**
	 * 当前对象id
	 */
	private Object id;
	
	/**
	 * 当前操作对象(model、list)
	 */
	private Object object;

	/**
	 * 当前定义SQL
	 */
	private String sql;

	/**
	 * 从前端grid传过来的参数
	 */
	private String sqlEx;

	/**
	 * 追加SQL条件(程序自动生成)
	 */
	private String condition;

	/**
	 * 自定义SQL覆盖默认查询条件
	 */
	private String where;

	/**
	 * 从前端grid传过来的排序方式
	 */
	private String orderBy;

	/**
	 * 自定义列表的sql
	 */
	private String sqlStatement;
	
	/**
	 * 自定义分页数的sql
	 */
	private String sqlCount;
	
	/**
	 * 自定义SQL参数(map形式)
	 */
	private Map<String, Object> param;

	/**
	 * 用于判断当前使用场景
	 */
	private String tips = "";

	public AopContext() {

	}

	public AopContext(String tips) {
		this.tips = tips;
	}

	public AopContext(BladeController ctrl) {
		this.ctrl = ctrl;
	}

	public AopContext(BladeController ctrl, Object object) {
		this(ctrl);
		this.object = object;
	}

	public AopContext(BladeController ctrl, ModelAndView view) {
		this(ctrl);
		this.view = view;
	}

	public AopContext(BladeController ctrl, Object object, ModelAndView view) {
		this(ctrl, object);
		this.view = view;
	}

	public BladeController getCtrl() {
		return ctrl;
	}

	public void setCtrl(BladeController ctrl) {
		this.ctrl = ctrl;
	}

	public ModelAndView getView() {
		return view;
	}

	public void setView(ModelAndView view) {
		this.view = view;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSqlEx() {
		return sqlEx;
	}

	public void setSqlEx(String sqlEx) {
		this.sqlEx = sqlEx;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getSqlStatement() {
		return sqlStatement;
	}

	public void setSqlStatement(String sqlStatement) {
		this.sqlStatement = sqlStatement;
	}

	public String getSqlCount() {
		return sqlCount;
	}

	public void setSqlCount(String sqlCount) {
		this.sqlCount = sqlCount;
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}


}
