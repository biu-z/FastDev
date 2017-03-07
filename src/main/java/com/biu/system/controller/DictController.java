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
import com.biu.core.annotation.Before;
import com.biu.core.plugins.dao.Blade;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.system.meta.intercept.DictValidator;
import com.biu.system.model.Dict;
import com.biu.system.service.DictService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/dict")
public class DictController extends BaseController {
	private static String LIST_SOURCE = "blade_dict";
	private static String BASE_PATH = "/system/dict/";
	private static String CODE = "dict";
	private static String PREFIX = "blade_dict";

	@Resource
	private DictService dictService;

	@RequestMapping("/")
	public String index(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "dict.html";
	}


	@ResponseBody
	@RequestMapping(KEY_LIST)
	public Object list() {
		String sql = "select d.*,(select name from blade_dict  where id=d.pId) pname from blade_dict d";
		Object gird = paginate(sql);
		return gird;
	}

	@RequestMapping(KEY_ADD)
	public String add(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "dict_add.html";
	}

	@RequestMapping(KEY_ADD + "/{id}")
	public String add(@PathVariable Integer id, ModelMap mm) {
		if (null != id) {
			Dict dict = Blade.create(Dict.class).findById(id);
			mm.put("dictcode", dict.getCode());
			mm.put("pId", id);
			mm.put("num", findLastNum(dict.getCode()));
		}
		mm.put("code", CODE);
		return BASE_PATH + "dict_add.html";
	}

	@RequestMapping(KEY_EDIT + "/{id}")
	public String edit(@PathVariable Integer id, ModelMap mm) {
		Dict dict = Blade.create(Dict.class).findById(id);
		mm.put("model", JsonKit.toJson(dict));
		mm.put("code", CODE);
		return BASE_PATH + "dict_edit.html";
	}

	@RequestMapping(KEY_VIEW + "/{id}")
	public String view(@PathVariable Integer id, ModelMap mm) {
		Blade blade = Blade.create(Dict.class);
		Dict dict = blade.findById(id);
		Dict parent = blade.findById(dict.getPid());
		String pname = (null == parent) ? "" : parent.getName();
		CMap cmap = CMap.parse(dict);
		cmap.set("pname", pname);
		mm.put("model", JsonKit.toJson(cmap));
		mm.put("code", CODE);
		return BASE_PATH + "dict_view.html";
	}

	@ResponseBody
	@Before(DictValidator.class)
	@RequestMapping(KEY_SAVE)
	public AjaxResult save() {
		Dict dict = mapping(PREFIX, Dict.class);
//		boolean temp = Blade.create(Dict.class).save(dict);
		int temp = dictService.save(dict);
		if (temp > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success(SAVE_SUCCESS_MSG);
		} else {
			return error(SAVE_FAIL_MSG);
		}
	}

	@ResponseBody
	@Before(DictValidator.class)
	@RequestMapping(KEY_UPDATE)
	public AjaxResult update() {
		Dict dict = mapping(PREFIX, Dict.class);
//		boolean temp =  Blade.create(Dict.class).update(dict);
		int temp = dictService.save(dict);
		if (temp > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success(UPDATE_SUCCESS_MSG);
		} else {
			return error(UPDATE_FAIL_MSG);
		}
	}

	@ResponseBody
	@RequestMapping(KEY_REMOVE)
	public AjaxResult remove() {
		int cnt = Blade.create(Dict.class).deleteByIds(getParameter("ids"));
		if (cnt > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success(DEL_SUCCESS_MSG);
		} else {
			return error(DEL_FAIL_MSG);
		}
	}


	private int findLastNum(String code){
		try{
			Blade blade = Blade.create(Dict.class);
			Dict dict = blade.findFirstBy("code = #{code} order by num desc", CMap.init().set("code", code));
			return dict.getNum() + 1;
		}
		catch(Exception ex){
			return 1;
		}
	}
	
	
}
