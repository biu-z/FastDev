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
import com.biu.core.plugins.dao.Blade;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.system.meta.intercept.DeptIntercept;
import com.biu.system.model.Dept;
import com.biu.system.service.DeptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/dept")
public class DeptController extends BaseController {
	private static String LIST_SOURCE = "blade_dept";
	private static String BASE_PATH = "/system/dept/";
	private static String CODE = "dept";
	private static String PREFIX = "blade_dept";

	@Resource
	private DeptService deptService;

	@RequestMapping("/")
	public String index(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "dept.html";
	}


	@ResponseBody
	@RequestMapping(KEY_LIST)
	public Object list() {
		String sql = "select d.*,(select simpleName from blade_dept  where id=d.pId) pname from blade_dept d ";
		Object gird = paginate(sql, new DeptIntercept());
		return gird;
	}

	@RequestMapping(KEY_ADD)
	public String add(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "dept_add.html";
	}

	@RequestMapping(KEY_ADD + "/{id}")
	public String add(@PathVariable Integer id, ModelMap mm) {
		if (null != id) {
			mm.put("pId", id);
			mm.put("num", findLastNum(id));
		}
		mm.put("code", CODE);
		return BASE_PATH + "dept_add.html";
	}

	@RequestMapping(KEY_EDIT + "/{id}")
	public String edit(@PathVariable Integer id, ModelMap mm) {
		Dept Dept = Blade.create(Dept.class).findById(id);
		mm.put("model", JsonKit.toJson(Dept));
		mm.put("code", CODE);
		return BASE_PATH + "dept_edit.html";
	}

	@RequestMapping(KEY_VIEW + "/{id}")
	public String view(@PathVariable Integer id, ModelMap mm) {
		Blade blade = Blade.create(Dept.class);
		Dept Dept = blade.findById(id);
		Dept parent = blade.findById(Dept.getPid());
		String pname = (null == parent) ? "" : parent.getSimplename();
		CMap cmap = CMap.parse(Dept);
		cmap.set("pname", pname);
		mm.put("model", JsonKit.toJson(cmap));
		mm.put("code", CODE);
		return BASE_PATH + "dept_view.html";
	}

	@ResponseBody
	@RequestMapping(KEY_SAVE)
	public AjaxResult save() {
		Dept dept = mapping(PREFIX, Dept.class);
//		boolean temp = Blade.create(Dept.class).save(dept);
		int temp = deptService.save(dept);
		if (temp > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success("新增成功");
		} else {
			return error("新增失败");
		}
	}

	@ResponseBody
	@RequestMapping(KEY_UPDATE)
	public AjaxResult update() {
		Dept dept = mapping(PREFIX, Dept.class);
//		boolean temp =  Blade.create(Dept.class).update(dept);
		int temp = deptService.updateNotEmpty(dept);
		if (temp > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success("修改成功");
		} else {
			return error("修改失败");
		}
	}

	@ResponseBody
	@RequestMapping(KEY_REMOVE)
	public AjaxResult remove() {
		int cnt = Blade.create(Dept.class).deleteByIds(getParameter("ids"));
		if (cnt > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success("删除成功!");
		} else {
			return error("删除失败!");
		}
	}







	private int findLastNum(Integer id){
		try{
			Blade blade = Blade.create(Dept.class);
			Dept dept = blade.findFirstBy("pId = #{pId} order by num desc", CMap.init().set("pId", id));
			return dept.getNum() + 1;
		}
		catch(Exception ex){
			return 1;
		}
	}
	
	
}
