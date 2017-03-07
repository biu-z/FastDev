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
package com.biu.core.beetl.tag;

import com.alibaba.fastjson.JSON;
import com.biu.common.utils.SpringContextHolder;
import com.biu.common.vo.TreeNode;
import com.biu.core.constant.ConstCache;
import com.biu.core.constant.ConstCacheKey;
import com.biu.core.constant.Cst;
import com.biu.core.plugins.dao.Db;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.kit.StrKit;
import com.biu.core.toolbox.support.Convert;
import com.biu.system.mapper.UserMapper;
import org.beetl.core.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SideBarTag extends Tag {

	private UserMapper userMapper = SpringContextHolder.getBean(UserMapper.class);
	
	private List<TreeNode> nodeList = new ArrayList<TreeNode>();
	
	@Override
	@SuppressWarnings("unchecked")
	public void render() {
		try {
			Map<String, String> param = (Map<String, String>) args[1];
			final Integer userId = Convert.toInt(param.get("userId"));
			final String roleId = param.get("roleId");
			String ctxPath = Cst.me().getContextPath();

			String roleMenuSql = "select * from blade_role_menus where roleid = #{roleid}";
			Map roleMenu = Db.selectOneByCache(ConstCache.SYS_CACHE,ConstCacheKey.ROLE_MENUS + userId,roleMenuSql,CMap.init().set("roleid",roleId));

			final StringBuilder sql = new StringBuilder();
			sql.append("select * from blade_menu  ");
			sql.append(" where ( (status=1)");
			sql.append(" and (icon is not null and icon not LIKE '%btn%' and icon not LIKE '%icon%') ");
			sql.append(" and (id in ( "+roleMenu.get("menus")+" )))");
			sql.append(" order by levels,pCode,num");

			@SuppressWarnings("rawtypes")
			List<Map> sideBar = Db.selectListByCache(
					ConstCache.SYS_CACHE,
					ConstCacheKey.SIDEBAR + userId, sql.toString(),
					CMap.init());

			for (Map<String, Object> side : sideBar) {
				TreeNode node = new TreeNode();
				CMap cmap = CMap.parse(side);
				node.setId(cmap.getStr("code"));
				node.setParentId(cmap.getStr("pcode"));
				node.setName(cmap.getStr("name"));
				node.setIcon(cmap.getStr("icon"));
				node.setIsParent(false);
				nodeList.add(node);
			}

			new TreeNode().buildNodes(nodeList);

			StringBuilder sb = new StringBuilder();

			for (Map<String, Object> side : sideBar) {
				if (Func.toInt(side.get("levels")) == 1) {
					String firstMenu = "";
					String subMenu = "";
					String href = StrKit.isBlank(Convert.toStr(side.get("url"), "").trim()) ? "#" : ctxPath + side.get("url") + "";
					String addtabs = StrKit.isBlank(Convert.toStr(side.get("url"), "").trim()) ? "" : "data-addtabs=\"" + side.get("code") + "\"";

					firstMenu += "<li >";
					firstMenu += "	<a data-url=\"" + href + "\" " + addtabs + " data-title=\"" + side.get("name") + "\" data-icon=\"fa " + side.get("icon") + "\" class=\"" + getDropDownClass(Func.toStr(side.get("code")),"dropdown-toggle") + " blade-pointer\">";
					firstMenu += "		<i class=\"menu-icon fa " + side.get("icon") + "\"></i>";
					firstMenu += "		<span class=\"menu-text\">" + side.get("name") + "</span>";
					firstMenu += "		<b class=\"arrow " + getDropDownClass(Func.toStr(side.get("code")),"fa fa-angle-down") + "\"></b>";
					firstMenu += "	</a>";
					firstMenu += "	<b class=\"arrow\"></b>";

					subMenu = this.reloadMenu(sideBar, Func.toStr(side.get("code")), firstMenu, 1, ctxPath);// 寻找子菜单

					sb.append(subMenu);
				}
			}

			System.out.print(JSON.toJSONString(sb));

			//版权校验
			sb.append("<script type=\"text/javascript\">");
			sb.append(" $(function(){");
			sb.append("  setTimeout(function(){");
			sb.append("  var $supporter = $(\"#support_tonbusoft\");");
			sb.append("  $supporter.addClass('bigger-110');");
			sb.append("  var name = $supporter.html();");
			sb.append("  var index = layer;");
			sb.append("  if(index == undefined){");
			sb.append("    alert(\"该产品版权归 " + FootTag.company + " 所有，请勿盗版！\");");
			sb.append("    return;");
			sb.append("  }");
			sb.append("  if(name == undefined){");
			sb.append("    layer.alert(\"该产品版权归 " + FootTag.company + " 所有，请勿盗版！\", {icon: 2,title:\"侵权警告\"});");
			sb.append("    return;");
			sb.append("  } else if(!(name.indexOf(\"" + FootTag.company + "\") >= 0 && $supporter.is(\"span\") && !$supporter.is(\":hidden\"))){");
			sb.append("    layer.alert(\"该产品版权归 " + FootTag.company + " 所有，请勿盗版！\", {icon: 2,title:\"侵权警告\"});");
			sb.append("    return;");
			sb.append("  }");
			sb.append("  }, 1800);");
			sb.append(" });");
			sb.append("</script>");

			ctx.byteWriter.writeString(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载子菜单
	 * 
	 * @param sideBar
	 *            菜单集合
	 * @param pCode
	 *            父编号
	 * @param pStr
	 *            父HTML
	 * @param levels
	 *            层级
	 * @param ctxPath
	 *            ctxPath
	 * @return String 返回子菜单HTML集
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String reloadMenu(List<Map> sideBar, String pCode, String pStr, int levels, String ctxPath) {
		String Str = "";
		String subStr = "";
		for (Map subside : sideBar) {
			CMap cmap = CMap.parse(subside);
			int _levels = cmap.getInt("LEVELS");
			String _code = cmap.getStr("CODE");
			String _pCode = cmap.getStr("PCODE");
			String _url = cmap.getStr("URL");
			String _icon = cmap.getStr("ICON");
			String _name = cmap.getStr("NAME");
			if ((_pCode.equals(pCode) && _levels > levels)) {
				String href = StrKit.isBlank(_url.trim()) ? "#" : ctxPath + _url + "";
				String addtabs = StrKit.isBlank(_url.trim()) ? "" : "data-addtabs=\"" + _code + "\"";

				Str += "<li>";
				Str += "	<a data-url=\"" + href + "\" " + addtabs + " data-title=\"" + _name + "\" data-icon=\"fa " + _icon + "\" class=\"" + getDropDownClass(_code, "dropdown-toggle") + " blade-pointer\">";
				Str += "		<i class=\"menu-icon fa " + _icon + "\"></i>";
				Str += _name;
				Str += "		<b class=\"arrow " + getDropDownClass(_code,"fa fa-angle-down") + "\"></b>";
				Str += "	</a>";
				Str += "	<b class=\"arrow\"></b>";

				subStr = this.reloadMenu(sideBar, _code, Str, _levels, ctxPath);// 递归寻找子菜单

				Str = Func.isEmpty(subStr) ? Str : subStr;
			}

		}
		if (Str.length() > 0) {
			pStr += (Func.isEmpty(pStr)) ? Str : "<ul class=\"submenu\">" + Str + "</ul>";
			pStr += "</li>";
			return pStr;
		} else {
			return "";
		}

	}
	
	public String getDropDownClass(String code,String dropdownclass){
		Iterator<TreeNode> it = nodeList.iterator();
		while (it.hasNext()) {
			TreeNode n = (TreeNode) it.next();
			if(n.getId().equals(code)&&n.isParent()){
				return dropdownclass;
			}
		}
		return "";
	}

}
