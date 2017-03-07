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
import com.biu.common.vo.ShiroUser;
import com.biu.core.aop.AopContext;
import com.biu.core.constant.Const;
import com.biu.core.constant.ConstCache;
import com.biu.core.constant.ConstCacheKey;
import com.biu.core.constant.Cst;
import com.biu.core.plugins.dao.Db;
import com.biu.core.shiro.ShiroKit;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.cache.ILoader;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.core.toolbox.kit.PropKit;
import com.biu.core.toolbox.kit.StrKit;
import com.biu.core.toolbox.support.Convert;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/cache")
public class CacheController extends BaseController {

	public void index() {

	}

	/**
	 * 获取按钮组
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/getBtn")
	public AjaxResult getBtn() {
		final String code = getParameter("code");
		ShiroUser user = ShiroKit.getUser();
		final Integer userId = Convert.toInt(user.getId());
		final String roleId = Convert.toStr(user.getRoles());

		String roleMenuSql = "select * from blade_role_menus where roleid = #{roleid}";
		Map roleMenu = Db.selectOneByCache(ConstCache.SYS_CACHE, ConstCacheKey.ROLE_MENUS + userId,roleMenuSql,CMap.init().set("roleid",roleId));

		final StringBuilder sql = new StringBuilder();
		sql.append("select blade_menu.id as \"id\", blade_menu.code as \"code\", blade_menu.pcode as \"pcode\", blade_menu.alias as \"alias\", blade_menu.name as \"name\", blade_menu.icon as \"icon\", blade_menu.url as \"url\", blade_menu.path as \"path\", blade_menu.tips as \"tips\", blade_menu.isopen as \"isopen\", (select name from blade_menu where code = #{code}) as \"pname\"  from blade_menu");
		sql.append(" where ((status=1) ");
		sql.append(" and (icon is not null and (icon like '%btn%' or icon like '%icon%' ) ) ");
		sql.append(" and (pCode=#{code})");
		sql.append(" and (id in ("+roleMenu.get("menus")+"))) ");
		sql.append(" order by num");


		List<Map> btnList = Db.selectListByCache(SYS_CACHE, BTN_LIST + code + "_" + userId, sql.toString(),
				CMap.init()
				.set("code", code));
//				.set("roleId", roleId));
//				.set("roleIn", Convert.toIntArray(roleIn))
//				.set("roleOut", Convert.toIntArray(roleOut)));
		
		
		
		return json(btnList);
	}

	/**
	 * 获取按钮组
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/getChildBtn")
	public AjaxResult getChildBtn() {
		final String code = getParameter("code");
		ShiroUser user = ShiroKit.getUser();
		final Integer userId = Convert.toInt(user.getId());
		final String roleId = Convert.toStr(user.getRoles());

		String roleMenuSql = "select * from blade_role_menus where roleid = #{roleid}";
		Map roleMenu = Db.selectOneByCache(ConstCache.SYS_CACHE, ConstCacheKey.ROLE_MENUS + userId,roleMenuSql,CMap.init().set("roleid",roleId));

		final StringBuilder sql = new StringBuilder();
		sql.append("select blade_menu.id as \"id\", blade_menu.code as \"code\", blade_menu.pcode as \"pcode\", blade_menu.alias as \"alias\", blade_menu.name as \"name\", blade_menu.icon as \"icon\", blade_menu.url as \"url\", blade_menu.path as \"path\", blade_menu.tips as \"tips\", blade_menu.isopen as \"isopen\", (select name from blade_menu where code=#{code}) as \"pname\"  from blade_menu");
		sql.append(" where ((status=1)");
		sql.append(" and (icon is not null and (icon like '%btn%' or icon like '%icon%' ) ) ");
		sql.append(" and (pCode=#{code})");
		sql.append(" and (id in ("+roleMenu.get("menus")+")))");
		sql.append(" order by num");

		List<Map> btnList = Db.selectListByCache(SYS_CACHE, BTN_CHILD_LIST + code + "_" + userId, sql.toString(),
				CMap.init()
				.set("code", code));
//				.set("roleId", roleId));

		return json(btnList);
	}

	/**
	 * 根据字典编号获取下拉框
	 *
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/getSelect")
	public AjaxResult getSelect() {
		final String code = getParameter("code");
		final String num = getParameter("num");

		List<Map> dict = Db.selectListByCache(SYS_CACHE, DICT_SELECT + code, "select num as ID,pId as PID,name as TEXT from  blade_dict where code=#{code} and num>0", CMap.init().set("code", code));
		
		StringBuilder sb = new StringBuilder();
		sb.append("<select class=\"form-control\" style=\"margin-left:-3px;cursor:pointer;\" id=\"inputs"
				+ num + "\">");
		sb.append("<option value></option>");
		for (Map dic : dict) {
			sb.append("<option value=\"" + dic.get("ID") + "\">" + dic.get("TEXT") + "</option>");
		}
		sb.append("</select>");
		return json(sb.toString());
	}

	@ResponseBody
	@RequestMapping("/getCombo")
	public AjaxResult getCombo() {
		String type = getParameter("type");
		if (StrKit.equalsIgnoreCase(type, "diy")) {
			final String source = getParameter("source");
			String where = getParameter("where");
			Map<String, Object> param = CMap.createHashMap();
			if (StrKit.notBlank(where)) {
				param = JsonKit.parse(where);
			}
			final Map<String, Object> map = param;
			List<Map<String, Object>> diy = CacheKit.get(SYS_CACHE, DICT_COMBO + type + source,
					new ILoader() {
						public Object load() {
							Properties prop = PropKit.use(Const.SQL_PROPERTIY).getProperties();
							String md_source = Func.toStr(prop.get(source));
							return Db.selectList(md_source, map);
						}
					});
			return json(diy);
		} else {
			final String code = getParameter("code");
			List<Map<String, Object>> dict = CacheKit.get(SYS_CACHE, DICT_COMBO + type + code,
					new ILoader() {
						public Object load() {
							return Db.selectList("select num as \"id\",name as \"text\" from  blade_dict where code=#{code} and num>0", CMap.init().set("code", code));
						}
					});
			return json(dict);
		}
	}

	@ResponseBody
	@RequestMapping("/getDeptSelect")
	public AjaxResult getDeptSelect() {
		final String num = getParameter("num");
		List<Map<String, Object>> dept = CacheKit.get(SYS_CACHE, DEPT_ALL_LIST,
				new ILoader() {
					public Object load() {
						return Db.selectList("select ID,PID,simpleName as TEXT from  blade_dept order by pId,num asc", CMap.init(), new AopContext(), Cst.me().getDefaultSelectFactory().deptIntercept());
					}
				});
		StringBuilder sb = new StringBuilder();
		sb.append("<select class=\"form-control\" style=\"margin-left:-3px;cursor:pointer;\" id=\"inputs"
				+ num + "\">");
		sb.append("<option value></option>");
		for (Map<String, Object> _dept : dept) {
			sb.append("<option value=\"" + _dept.get("ID") + "\">" + _dept.get("TEXT") + "</option>");
		}
		sb.append("</select>");
		return json(sb.toString());
	}

	@ResponseBody
	@RequestMapping("/getUserSelect")
	public AjaxResult getUserSelect() {
		final String num = getParameter("num");
		List<Map<String, Object>> user = CacheKit.get(SYS_CACHE, USER_SELECT_ALL,
				new ILoader() {
					public Object load() {
						return Db.selectList("select ID,name as TEXT from blade_user where status=1 and name is not null order by name ", CMap.init(), new AopContext(), Cst.me().getDefaultSelectFactory().userIntercept());
					}
				});
		StringBuilder sb = new StringBuilder();
		sb.append("<select class=\"form-control\" style=\"margin-left:-3px;cursor:pointer;\" id=\"inputs"
				+ num + "\">");
		sb.append("<option value></option>");
		for (Map<String, Object> _user : user) {
			sb.append("<option value=\"" + _user.get("ID") + "\">" + _user.get("TEXT") + "</option>");
		}
		sb.append("</select>");
		return json(sb.toString());
	}

	@ResponseBody
	@RequestMapping("/getRoleSelect")
	public AjaxResult getRoleSelect() {
		final String num = getParameter("num");
		List<Map<String, Object>> role = CacheKit.get(SYS_CACHE, ROLE_ALL_LIST,
				new ILoader() {
					public Object load() {
						return Db.selectList("select ID,name as TEXT from blade_role where  name is not null order by name ", CMap.init(), new AopContext(), Cst.me().getDefaultSelectFactory().roleIntercept());
					}
				});
		StringBuilder sb = new StringBuilder();
		sb.append("<select class=\"form-control\" style=\"margin-left:-3px;cursor:pointer;\" id=\"inputs"
				+ num + "\">");
		sb.append("<option value></option>");
		for (Map<String, Object> _role : role) {
			sb.append("<option value=\"" + _role.get("ID") + "\">" + _role.get("TEXT") + "</option>");
		}
		sb.append("</select>");
		return json(sb.toString());
	}
	
	@ResponseBody
	@RequestMapping("/getDiySelect")
	public AjaxResult getDiySelect() {
		final String num = getParameter("num");
		final String source = getParameter("source");
		String where = getParameter("where");
		Map<String, Object> param = CMap.createHashMap();
		if (StrKit.notBlank(where)) {
			param = JsonKit.parse(where);
		}
		final Map<String, Object> map = param;
		List<Map<String, Object>> diy = CacheKit.get(SYS_CACHE, DIY_SELECT + source,
				new ILoader() {
					public Object load() {
						Properties prop = PropKit.use(Const.SQL_PROPERTIY).getProperties();
						String md_source = Func.toStr(prop.get(source));
						return Db.selectList(md_source, map);
					}
				});
		StringBuilder sb = new StringBuilder();
		sb.append("<select class=\"form-control\" style=\"margin-left:-3px;cursor:pointer;\" id=\"inputs" + num + "\">");
		sb.append("<option value></option>");
		for (Map<String, Object> _diy : diy) {
			sb.append("<option value=\"" + _diy.get("ID") + "\">" + _diy.get("TEXT") + "</option>");
		}
		sb.append("</select>");
		return json(sb.toString());
	}

	@ResponseBody
	@RequestMapping("/dicTreeList")
	public AjaxResult dicTreeList() {
		List<Map<String, Object>> dic = CacheKit.get(SYS_CACHE, DICT_TREE_ALL,new ILoader() {
					public Object load() {
						return Db.selectList("select code ,id ,pId ,name ,num ,'false' open from blade_dict order by code asc,num asc", CMap.init());
					}
				});

		return json(dic);
	}

	@ResponseBody
	@RequestMapping("/deptTreeList")
	public AjaxResult deptTreeList() {
		List<Map<String, Object>> dept = CacheKit.get(SYS_CACHE, DEPT_TREE_ALL + "_" + ShiroKit.getUser().getId(),
				new ILoader() {
					public Object load() {
						return Db.selectList("select id \"id\",pId \"pId\",simpleName as \"name\",(case when (pId=0 or pId is null) then 'true' else 'false' end) \"open\" from  blade_dept ", CMap.init(), new AopContext("ztree"), Cst.me().getDefaultSelectFactory().deptIntercept());
					}
				});

		return json(dept);
	}

	@ResponseBody
	@RequestMapping("/roleTreeList")
	public AjaxResult roleTreeList() {
		List<Map<String, Object>> dept = CacheKit.get(SYS_CACHE, ROLE_TREE_ALL + "_" + ShiroKit.getUser().getId(),
				new ILoader() {
					public Object load() {
						return Db.selectList("select id \"id\",pId \"pId\",name as \"name\",(case when (pId=0 or pId is null) then 'true' else 'false' end) \"open\" from  blade_role ", CMap.init(), new AopContext("ztree"), Cst.me().getDefaultSelectFactory().roleIntercept());
					}
				});

		return json(dept);
	}

	@ResponseBody
	@RequestMapping("/getDicById")
	public AjaxResult getDicById() {
		final int id = getParameterToInt("id");
		List<Map<String, Object>> dict = CacheKit.get(SYS_CACHE, DICT_CODE + id,
				new ILoader() {
					public Object load() {
						return Db.selectList("select code from blade_dict where id=#{id}", CMap.init().set("id", id));
					}
				});
		return json(dict);
	}

	@ResponseBody
	@RequestMapping("/menuTreeList")
	public AjaxResult menuTreeList() {
		List<Map<String, Object>> menu = CacheKit.get(SYS_CACHE, MENU_TREE_ALL,
				new ILoader() {
					public Object load() {
						return Db.selectList("select code \"id\",pCode \"pId\",name \"name\",(case when levels=1 then 'true' else 'false' end) \"open\" from BLADE_MENU where status=1 order by levels asc,num asc");
					}
				});

		return json(menu);
	}

	@ResponseBody
	@RequestMapping("/menuTreeListByRoleId")
	public AjaxResult menuTreeListByRoleId() {
		final String roleId = getParameter("roleId", "0");
		List<Map<String, Object>> menu = CacheKit.get(SYS_CACHE, MENU_TREE + roleId,
				new ILoader() {
					@SuppressWarnings("rawtypes")
					public Object load() {
						String table = "BLADE_MENU";
						String pid = "";
						List<Map> pids = Db.selectList("select PID from BLADE_ROLE where id in (#{roleId})", CMap.init().set("roleId", roleId));
						for (Map p : pids) {
							if (!Func.isEmpty(p.get("PID")) && Func.toInt(p.get("PID")) > 0) {
								pid += p.get("PID").toString() + ",";
							}
						}
						if (!Func.isEmpty(pid)) {
							pid = StrKit.removeSuffix(pid, ",");
							table = "(select * from BLADE_MENU where id in( select MENUID from BLADE_RELATION where roleId in (#{pid}) ))";
						}
						StringBuilder sb = new StringBuilder();
						sb.append("select m.id \"id\",(select id from BLADE_MENU  where code=m.pCode) \"pId\",name \"name\",(case when m.levels=1 then 'true' else 'false' end) \"open\",(case when r.menuId is not null then 'true' else 'false' end) \"checked\"");
						sb.append(" from ");
						sb.append(table);
						sb.append(" m left join (select MENUID from BLADE_RELATION where roleId in (#{roleId}) GROUP BY MENUID) r on m.id=r.menuId where m.status=1 order by m.levels,m.num asc");
						return Db.selectList(sb.toString(), CMap.init().set("roleId", roleId).set("pid", pid));
					}
				});

		return json(menu);
	}

	@ResponseBody
	@RequestMapping("/roleTreeListById")
	public AjaxResult roleTreeListById() {
		final String Id = getParameter("id");
		final String roleId = getParameter("roleId", "0");
		List<Map<String, Object>> menu = CacheKit.get(SYS_CACHE, ROLE_TREE + Id,
				new ILoader() {
					public Object load() {
						String sql = "select id \"id\",pId \"pId\",name as \"name\",(case when (pId=0 or pId is null) then 'true' else 'false' end) \"open\" from  BLADE_ROLE order by pId,num asc";
						if (Id.indexOf(",") == -1) {
							sql = "select r.id \"id\",pId \"pId\",name as \"name\",(case when (pId=0 or pId is null) then 'true' else 'false' end) \"open\",(case when (r1.ID=0 or r1.ID is null) then 'false' else 'true' end) \"checked\" from  BLADE_ROLE r left join (select ID  from BLADE_ROLE where ID in ("
									+ "#{roleId}"
									+ ")) r1 on r.ID=r1.ID order by pId,num asc";
						}
						return Db.selectList(sql, CMap.init().set("roleId",roleId));
					}
				});

		return json(menu);
	}
	
	@ResponseBody
	@RequestMapping("/theme")
	public AjaxResult theme() {
		if (null == ShiroKit.getUser()) {
			return error("error");
		}
		Map<String, String> theme = CacheKit.get(SYS_CACHE, ACE_THEME + ShiroKit.getUser().getId() , new ILoader() {
			public Object load() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ace", "ace-dark.css");
				return map;
			}
		});
		String currentTheme = theme.get("ace");
		theme.put("ace", (StrKit.equals(currentTheme, "ace-dark.css") ? "ace-white.css" : "ace-dark.css"));
		return success("success");
	}
	
}
