/**
 * Copyright (c) 2015-2017, Chill Zhuang 庄骞 (smallchill@163.com).
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
package com.biu.system.service.impl;

import com.biu.common.base.service.impl.BaseServiceImpl;
import com.biu.core.plugins.dao.Blade;
import com.biu.core.plugins.dao.Db;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.system.mapper.RoleMapper;
import com.biu.system.mapper.RoleMenuMapper;
import com.biu.system.model.Role;
import com.biu.system.model.RoleMenu;
import com.biu.system.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("roleService")
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

	@Resource
	private RoleMapper roleMapper;
	@Resource
	private RoleMenuMapper roleMenuMapper;

	@Override
	public int findLastNum(Integer id) {
		try{
			Blade blade = Blade.create(Role.class);
			Role role = blade.findFirstBy("pId = #{pId} order by num desc", CMap.init().set("pId", id));
			return role.getNum() + 1;
		}
		catch(Exception ex){
			return 1;
		}
	}

	@Override
	public boolean grant(String ids, Integer roleId) {
//		Blade dao = Blade.create(RoleMenu.class);
		RoleMenu roleMenu = new RoleMenu();
		roleMenu.setRoleid(roleId);
//		RoleMenu rm = dao.findFirstBy("roleid = #{roleId}",CMap.init().set("roleId",roleId));
		RoleMenu rm = roleMenuMapper.selectOne(roleMenu);
		if(rm != null){
//			dao.updateBy("menus = #{ids}","roleid = #{roleId}", CMap.init().set("roleId", roleId).set("ids",ids));
			rm.setMenus(ids);
			roleMenuMapper.updateByPrimaryKeySelective(rm);
		}else {
			roleMenu.setMenus(ids);
			roleMenuMapper.insert(roleMenu);
		}

//		dao.deleteBy("ROLEID = #{roleId}", CMap.init().set("roleId", roleId));
//		List<Relation> relations = new ArrayList<>();
//		for (Integer menuId : Convert.toIntArray(ids)) {
//			Relation relation = new Relation();
//			relation.setMenuid(menuId);
//			relation.setRoleid(roleId);
//			relations.add(relation);
//		}
//		dao.saveBatch(relations);
		return true;
	}

	@Override
	public int getParentCnt(Integer id) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT");
		sb.append("(CASE WHEN ");
		sb.append("	(select (CASE when (PID=0 or PID is null) then ID else 0 end) as ID from BLADE_ROLE where ID=#{id})>0 ");
		sb.append("THEN 1 ");
		sb.append("ELSE");
		sb.append("	(select count(*) from BLADE_RELATION where ROLEID=(select (CASE when (PID=0 or PID is null) then ID else PID end) as ID from BLADE_ROLE where ID=#{id})) ");
		sb.append("END) CNT");
		Object cnt = Db.selectOne(sb.toString(), CMap.init().set("id", id)).get("CNT");
		return Func.toInt(cnt, 0);
	}

//	@Override
//	public int deleteByIds(Integer[] ids) {
//		return roleMapper.deleteByIds(ids);
//	}
}
