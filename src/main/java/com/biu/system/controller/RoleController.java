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
package com.biu.system.controller;

import com.biu.common.base.controller.BaseController;
import com.biu.common.tool.SysCache;
import com.biu.core.annotation.Before;
import com.biu.core.annotation.Permission;
import com.biu.core.constant.ConstShiro;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.system.meta.intercept.RoleIntercept;
import com.biu.system.meta.intercept.RoleValidator;
import com.biu.system.model.Role;
import com.biu.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController{
	private static String LIST_SOURCE = "blade_role";
	private static String BASE_PATH = "/system/role/";
	private static String CODE = "role";
	private static String PREFIX = "blade_role";
	
	@Autowired
	RoleService service;

	@RequestMapping("/")
	public String index(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "role.html";
	}

	@ResponseBody
	@RequestMapping(KEY_LIST)
	public Object list() {
		String sql = "select r.*,d.simpleName deptname,(select name from blade_role where id=r.pId) pname from blade_role r left join blade_dept d on r.deptId=d.id";
		Object gird = paginate(sql, new RoleIntercept());
		return gird;
	}

	@RequestMapping(KEY_ADD)
	public String add(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "role_add.html";
	}

	@RequestMapping(KEY_ADD + "/{id}")
	public String add(@PathVariable Integer id, ModelMap mm) {
		if (null != id) {
			mm.put("pId", id);
			mm.put("num", service.findLastNum(id));
		}
		mm.put("code", CODE);
		return BASE_PATH + "role_add.html";
	}

	@RequestMapping(KEY_EDIT + "/{id}")
	public String edit(@PathVariable Integer id, ModelMap mm) {
		Role role = service.getOneByKey(id);
		mm.put("model", JsonKit.toJson(role));
		mm.put("code", CODE);
		return BASE_PATH + "role_edit.html";
	}

	@RequestMapping(KEY_VIEW + "/{id}")
	public String view(@PathVariable Integer id, ModelMap mm) {
		Role role = service.getOneByKey(id);
		Role parent = service.getOneByKey(role.getPid());
		String pname = (null == parent) ? "" : parent.getName();
		CMap cmap = CMap.parse(role);
		cmap.set("deptName", SysCache.getDeptName(role.getDeptid())).set("pname", pname);
		mm.put("model", JsonKit.toJson(cmap));
		mm.put("code", CODE);
		return BASE_PATH + "role_view.html";
	}

	@Permission({ConstShiro.ADMINISTRATOR, ConstShiro.ADMIN})
	@RequestMapping("/authority")
	public String authority(ModelMap mm) {
		mm.put("roleId", getParameter("roleId"));
		mm.put("roleName", getParameterToDecode("roleName"));
		return BASE_PATH + "role_authority.html";
	}

	@ResponseBody
	@Before(RoleValidator.class)
	@RequestMapping("/saveAuthority")
	public AjaxResult saveAuthority() {
		String ids = getParameter("ids");
		Integer roleId = getParameterToInt("roleId");
		String[] id = ids.split(",");
		if (id.length <= 1) {
			CacheKit.removeAll(SYS_CACHE);
			return success("设置成功");
		}
		boolean temp = service.grant(ids, roleId);
		if (temp) {
			CacheKit.removeAll(SYS_CACHE);
			return success("设置成功");
		} else {
			return error("设置失败");
		}
	}

	@ResponseBody
	@RequestMapping(KEY_SAVE)
	public AjaxResult save() {
		Role role = mapping(PREFIX, Role.class);
		Object pid = role.getPid();
		if (null == pid) {
			role.setPid(0);
		}
		int temp = service.save(role);
		if (temp > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success(SAVE_SUCCESS_MSG);
		} else {
			return error(SAVE_FAIL_MSG);
		}
	}

	@ResponseBody
	@RequestMapping(KEY_UPDATE)
	public AjaxResult update() {
		Role role = mapping(PREFIX, Role.class);
		int temp = service.update(role);
		if (temp > 0 ) {
			CacheKit.removeAll(SYS_CACHE);
			return success(UPDATE_SUCCESS_MSG);
		} else {
			return error(UPDATE_FAIL_MSG);
		}
	}

	@ResponseBody
	@RequestMapping(KEY_REMOVE)
	public AjaxResult remove() {
		String ids = getParameter("ids");
		int cnt = 1;//service.deleteByIds(Convert.toIntArray(ids));
		if (cnt > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success(DEL_SUCCESS_MSG);
		} else {
			return error(DEL_FAIL_MSG);
		}
	}

	@ResponseBody
	@RequestMapping("/getPowerById")
	public AjaxResult getPowerById() {
		int cnt = service.getParentCnt(getParameterToInt("id"));
		if (cnt > 0) {
			return success("success");
		} else {
			return error("error");
		}
	}
	
}
