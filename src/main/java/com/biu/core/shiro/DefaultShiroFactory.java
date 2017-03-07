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
package com.biu.core.shiro;

import com.biu.common.utils.SpringContextHolder;
import com.biu.common.vo.ShiroUser;
import com.biu.core.constant.ConstCache;
import com.biu.core.constant.ConstCacheKey;
import com.biu.core.plugins.dao.Db;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.cache.ILoader;
import com.biu.core.toolbox.kit.CollectionKit;
import com.biu.core.toolbox.kit.StrKit;
import com.biu.core.toolbox.support.Convert;
import com.biu.system.mapper.UserMapper;
import com.biu.system.model.User;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultShiroFactory implements IShiro{

	private UserMapper userMapper = SpringContextHolder.getBean(UserMapper.class);

	public User user(String account) {
		User record = new User();
		record.setAccount(account);
		User user = userMapper.selectOne(record);
//		User user = Blade.create(User.class).findFirstBy("account = #{account}", CMap.init().set("account", account));
		// 账号不存在
		if (null == user) {
			throw new UnknownAccountException();
		}
		// 账号未审核
		if (user.getStatus() == 3 || user.getStatus() == 4) {
			throw new DisabledAccountException();
		}
		// 账号被冻结
		if (user.getStatus() == 2 || user.getStatus() == 5) {
			throw new DisabledAccountException();
		}
		return user;
	}

	public ShiroUser shiroUser(User user) {
		List<Integer> roleList = new ArrayList<>();
		Integer[] roles = Convert.toIntArray(user.getRoleid());
		for (int i = 0; i < roles.length; i++) {
			roleList.add(roles[i]);
		}

		ShiroUser shiroUser = new ShiroUser();
		shiroUser.setId(user.getId());
		shiroUser.setDeptId(user.getDeptid());
		shiroUser.setLoginName(user.getAccount());
		shiroUser.setName(user.getName());
		shiroUser.setRoleList(roleList);
		shiroUser.setRoles(CollectionKit.join(roleList.toArray(), ","));

		// 递归查找上级部门id集合
		String superDepts = null;
		if (Func.isMySql()) {
			String[] arr = user.getDeptid().toString().split(",");
			StringBuilder sb = new StringBuilder();
			for (String deptid : arr) {
				String str = userMapper.queryParent(deptid, "blade_dept");
				sb.append(str).append(",");
				superDepts = StrKit.removeSuffix(sb.toString(), ",");
			}
		}
		shiroUser.setSuperDepts(superDepts);

		// 递归查找子部门id集合
		String subDepts = null;
		if (Func.isMySql()) {
			String[] arr = user.getDeptid().toString().split(",");
			StringBuilder sb = new StringBuilder();
			for (String deptid : arr) {
//				subDeptSql = "select queryChildren(#{deptid},'blade_dept') as subdepts";
				String str = userMapper.queryChildren(deptid,"blade_dept");
				sb.append(str).append(",");
			}
			subDepts = StrKit.removeSuffix(sb.toString(), ",");
		}
		shiroUser.setSubDepts(subDepts);

		// 递归查找子角色id集合
		String subRoles = null;
		if (Func.isMySql()){
			StringBuilder sb = new StringBuilder();
			for (Integer roleid : roleList) {
//				roleSql = "SELECT queryChildren(#{deptid},'blade_role') as subroles";
				String str = userMapper.queryChildren(roleid,"blade_role");
				sb.append(str).append(",");
			}
			subRoles = StrKit.removeSuffix(sb.toString(), ",");
		}
		shiroUser.setSubRoles(subRoles);

		// 查找子角色对应账号id集合
		List<User> listUser = CacheKit.get(ConstCache.SYS_CACHE, ConstCacheKey.USER_ALL_LIST, new ILoader() {
			@Override
			public Object load() {
				User record = new User();
				record.setStatus(1);
				return userMapper.select(record);
			}
		});

		String[] subrolestr = Func.toStr(shiroUser.getSubRoles()).split(",");
		StringBuilder sbUser = new StringBuilder();
		for (User u : listUser) {
			for (String str : subrolestr) {
				if (Func.toStr(u.getRoleid()).indexOf(str) >= 0 && (("," + sbUser.toString() + ",").indexOf("," + Func.toStr(u.getId()) + ",") == -1)) {
					Func.builder(sbUser, Func.toStr(u.getId()) + ",");
				}
			}
		}
		shiroUser.setSubUsers(StrKit.removeSuffix(sbUser.toString(), ","));

		return shiroUser;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map> findPermissionsByRoleId(final Object userId, Integer roleId) {
		Map<String, Object> userRole =  Db.selectOneByCache(ConstCache.SYS_CACHE, 
															ConstCacheKey.ROLE_EXT + userId, 
															"select * from BLADE_ROLE_EXT where USERID=#{userId}", 
															CMap.init().set("userId", Convert.toInt(userId)));

		String roleIn = "0";
		String roleOut = "0";
		if (!Func.isEmpty(userRole)) {
			CMap cmap = CMap.parse(userRole);
			roleIn = cmap.getStr("ROLEIN");
			roleOut = cmap.getStr("ROLEOUT");
		}
		
		final StringBuilder sql = new StringBuilder();
		
		sql.append("select ID,CODE,URL from BLADE_MENU  ");
		sql.append(" where ( ");
		sql.append("	 (status=1)");
		sql.append("	 and (url is not null) ");
		sql.append("	 and (id in (select menuId from BLADE_RELATION where roleId = #{roleId}) or id in (#{join(roleIn)}))");
		sql.append("	 and id not in(#{join(roleOut)})");
		sql.append("	)");
		sql.append(" order by levels,pCode,num");

		List<Map> permissions = Db.selectListByCache(ConstCache.SYS_CACHE, ConstCacheKey.PERMISSIONS + userId, sql.toString(), CMap.init()
				.set("roleId", roleId).set("roleIn", Convert.toIntArray(roleIn)).set("roleOut", Convert.toIntArray(roleOut)));
		
		return permissions;
	}

	@SuppressWarnings("unchecked")
	public String findRoleNameByRoleId(final Integer roleId) {
		Map<String, Object> map = Db.selectOneByCache(ConstCache.SYS_CACHE, 
														ConstCacheKey.GET_ROLE_NAME_BY_ID + roleId, 
														"select TIPS from BLADE_ROLE where id = #{id}", 
														CMap.init().set("id", roleId));
		return Func.toStr(map.get("TIPS"));
	}

	public SimpleAuthenticationInfo info(ShiroUser shiroUser, User user, String realmName) {
		String credentials = user.getPassword();
		// 密码加盐处理
		String source = user.getSalt();
		ByteSource credentialsSalt = new Md5Hash(source);
		return new SimpleAuthenticationInfo(shiroUser, credentials, credentialsSalt, realmName);
	}

}
