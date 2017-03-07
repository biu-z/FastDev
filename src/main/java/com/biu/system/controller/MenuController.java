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
import com.biu.core.annotation.Permission;
import com.biu.core.constant.ConstShiro;
import com.biu.core.plugins.dao.Db;
import com.biu.core.shiro.ShiroKit;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.cache.ILoader;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.core.toolbox.support.Convert;
import com.biu.system.meta.intercept.MenuValidator;
import com.biu.system.model.Menu;
import com.biu.system.service.MenuService;
import com.biu.core.constant.ConstCurd;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/menu")
public class MenuController extends BaseController implements ConstShiro {
	private static String LIST_SOURCE = "blade_menu";
	private static String BASE_PATH = "/system/menu/";
	private static String CODE = "menu";
	private static String PREFIX = "blade_menu";

	@Resource
	private MenuService menuService;

	@RequestMapping("/")
	@Permission(ADMINISTRATOR)
	public String index(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "menu.html";
	}

	@RequestMapping(ConstCurd.KEY_ADD)
	public String add(ModelMap mm) {
		if(ShiroKit.lacksRole(ADMINISTRATOR)){
			return REDIRECT_UNAUTH;
		}
		mm.put("code", CODE);
		return BASE_PATH + "menu_add.html";
	}

	@RequestMapping(ConstCurd.KEY_ADD + "/{id}")
	@Permission(ADMINISTRATOR)
	public String add(@PathVariable Integer id, ModelMap mm) {
		if (null != id) {
			Menu menu = menuService.getOneByKey(id);
			mm.put("PCODE", menu.getCode());
			mm.put("LEVELS", menu.getLevels() + 1);
			mm.put("NUM", menuService.findLastNum(menu.getCode()));
		}
		mm.put("code", CODE);
		return BASE_PATH + "menu_add.html";
	}

	@RequestMapping(ConstCurd.KEY_EDIT + "/{id}")
	@Permission(ADMINISTRATOR)
	public String edit(@PathVariable Integer id, ModelMap mm) {
		Menu menu = menuService.getOneByKey(id);
		mm.put("model", JsonKit.toJson(menu));
		mm.put("code", CODE);
		return BASE_PATH + "menu_edit.html";
	}

	@RequestMapping(ConstCurd.KEY_VIEW + "/{id}")
	@Permission(ADMINISTRATOR)
	public String view(@PathVariable Integer id, ModelMap mm) {
		Menu menu = menuService.getOneByKey(id);
		mm.put("model", JsonKit.toJson(menu));
		mm.put("code", CODE);
		return BASE_PATH + "menu_view.html";
	}

	@ResponseBody
	@RequestMapping(ConstCurd.KEY_LIST)
	@Permission(ADMINISTRATOR)
	public Object list() {
		String sql = "select \n" +
				"\tm.*,d.name as dic_status \n" +
				"from blade_menu m \n" +
				"\tleft join (select num,name from blade_dict where code=902) d on m.status=d.num";
		Object gird = paginate(sql);
		return gird;
	}

	@ResponseBody
	@Before(MenuValidator.class)
	@RequestMapping(ConstCurd.KEY_SAVE)
	@Permission(ADMINISTRATOR)
	public AjaxResult save() {
		Menu menu = mapping(PREFIX, Menu.class);
		menu.setStatus(1);
		int temp = menuService.save(menu);
		if (temp > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success(ConstCurd.SAVE_SUCCESS_MSG);
		} else {
			return error(ConstCurd.SAVE_FAIL_MSG);
		}
	}

	@ResponseBody
	@Before(MenuValidator.class)
	@RequestMapping(ConstCurd.KEY_UPDATE)
	@Permission(ADMINISTRATOR)
	public AjaxResult update() {
		Menu menu = mapping(PREFIX, Menu.class);
		int temp = menuService.update(menu);
		if (temp > 0 ) {
			CacheKit.removeAll(SYS_CACHE);
			return success(ConstCurd.UPDATE_SUCCESS_MSG);
		} else {
			return error(ConstCurd.UPDATE_FAIL_MSG);
		}
	}

	@ResponseBody
	@RequestMapping(ConstCurd.KEY_DEL)
	@Permission(ADMINISTRATOR)
	public AjaxResult del() {
		boolean temp = menuService.updateStatus(getParameter("ids"), 2);
		if (temp) {
			CacheKit.removeAll(SYS_CACHE);
			return success(ConstCurd.DEL_SUCCESS_MSG);
		} else {
			return error(ConstCurd.DEL_FAIL_MSG);
		}
	}

	@ResponseBody
	@RequestMapping(ConstCurd.KEY_RESTORE)
	@Permission(ADMINISTRATOR)
	public AjaxResult restore(@RequestParam String ids) {
		boolean temp = menuService.updateStatus(ids, 1);
		if (temp) {
			CacheKit.removeAll(SYS_CACHE);
			return success(ConstCurd.RESTORE_SUCCESS_MSG);
		} else {
			return error(ConstCurd.RESTORE_FAIL_MSG);
		}

	}

	@ResponseBody
	@RequestMapping(ConstCurd.KEY_REMOVE)
	@Permission(ADMINISTRATOR)
	public AjaxResult remove(@RequestParam String ids) {
		int cnt = menuService.deleteByIds(ids);
		if (cnt > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success(ConstCurd.DEL_SUCCESS_MSG);
		} else {
			return error(ConstCurd.DEL_FAIL_MSG);
		}
	}



	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/getMenu")
	public List<Map> getMenu(){
		final Integer userId = getParameterToInt("userId");
		final Integer roleId = getParameterToInt("roleId");

		Map<String, Object> userRole = CacheKit.get(SYS_CACHE, ROLE_EXT + userId, new ILoader() {
			@Override
			public Object load() {
				return Db.selectOne("select * from BLADE_ROLE_EXT where USERID = #{userId}", CMap.init().set("userId", userId));
			}
		});


		String roleIn = "0";
		String roleOut = "0";
		if (!Func.isEmpty(userRole)) {
			CMap cmap = CMap.parse(userRole);
			roleIn = cmap.getStr("ROLEIN");
			roleOut = cmap.getStr("ROLEOUT");
		}
		final StringBuilder sql = new StringBuilder();
		sql.append("select * from BLADE_MENU ");
		sql.append(" where ( ");
		sql.append("	 (status=1)");
		sql.append("	 and (icon is not null and icon not LIKE '%btn%' and icon not LIKE '%icon%' ) ");
		sql.append("	 and (id in (select menuId from BLADE_RELATION where roleId in (#{join(roleId)})) or id in (#{join(roleIn)}))");
		sql.append("	 and id not in(#{join(roleOut)})");
		sql.append("	)");
		sql.append(" order by levels,pCode,num");

		List<Map> sideBar = Db.selectListByCache(SYS_CACHE, SIDEBAR + userId, sql.toString(),
				CMap.init()
				.set("roleId", Convert.toIntArray(roleId.toString()))
				.set("roleIn", Convert.toIntArray(roleIn))
				.set("roleOut", Convert.toIntArray(roleOut)));
		return sideBar;
	}

}
