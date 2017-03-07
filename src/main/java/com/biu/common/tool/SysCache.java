package com.biu.common.tool;

import com.biu.core.constant.ConstCache;
import com.biu.core.constant.ConstCacheKey;
import com.biu.core.plugins.dao.Blade;
import com.biu.core.plugins.dao.Db;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.cache.ILoader;
import com.biu.core.toolbox.kit.StrKit;
import com.biu.core.toolbox.support.Convert;
import com.biu.system.model.*;

import java.util.List;
import java.util.Map;

public class SysCache implements ConstCache, ConstCacheKey{
	
	/**
	 * 获取字典表对应中文
	 * @param code 字典编号
	 * @param num  字典序号
	 * @return
	 */
	public static String getDictName(final Object code, final Object num) {
		Dict dict = CacheKit.get(SYS_CACHE, GET_DICT_NAME + code + "_" + num, new ILoader() {
			@Override
			public Object load() {
				return Blade.create(Dict.class).findFirstBy("code = #{code} and num = #{num}", CMap.init().set("code", Convert.toStr(code)).set("num", num));
			}
		});
		if(null == dict){
			return "";
		}
		return dict.getName();
	}
	
	/**
	 * 获取字典表
	 * @param code 字典编号
	 * @return
	 */
	public static List<Dict> getDict(final Object code) {
		List<Dict> list = CacheKit.get(SYS_CACHE, GET_DICT + code, new ILoader() {
			@Override
			public Object load() {
				return Blade.create(Dict.class).findBy("code = #{code} and num > 0", CMap.init().set("code", Convert.toStr(code)));
			}
		});
		return list;
	}
	
	/**
	 * 获取字典表
	 * @param code 字典编号
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map> getSimpleDict(final Object code) {
		List<Map> list = CacheKit.get(SYS_CACHE, GET_DICT + "simple_" + code, new ILoader() {
			@Override
			public Object load() {
				return Db.selectList("select num, name, tips from blade_dict where code = #{code} and num > 0", CMap.init().set("code", Convert.toStr(code))); 
			}
		});
		return list;
	}

	/**
	 * 获取对应角色名
	 * @param roleIds 角色id
	 * @return
	 */
	public static String getRoleName(final Object roleIds) {
		if(Func.isEmpty(roleIds)){
			return "";
		}
		final Integer[] roleIdArr = Convert.toIntArray(roleIds.toString());
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < roleIdArr.length; i++){
			final Integer roleId = roleIdArr[i];
			Role role = CacheKit.get(SYS_CACHE, GET_ROLE_NAME + roleId, new ILoader() {
				@Override
				public Object load() {
					return Blade.create(Role.class).findById(roleId);
				}
			});
			if (null != role)
				sb.append(role.getName()).append(",");
		}
		return StrKit.removeSuffix(sb.toString(), ",");
	}

	/**
	 * 获取对应角色别名
	 * @param roleIds 角色id
	 * @return
	 */
	public static String getRoleAlias(final Object roleIds) {
		if(Func.isEmpty(roleIds)){
			return "";
		}
		final Integer[] roleIdArr = Convert.toIntArray(roleIds.toString());
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < roleIdArr.length; i++){
			final Integer roleId = roleIdArr[i];
			Role role = CacheKit.get(SYS_CACHE, GET_ROLE_ALIAS + roleId, new ILoader() {
				@Override
				public Object load() {
					return Blade.create(Role.class).findById(roleId);
				}
			});
			if (null != role)
				sb.append(role.getTips()).append(",");
		}
		return StrKit.removeSuffix(sb.toString(), ",");
	}

	/**
	 * 获取对应用户名
	 * @param userId 用户id
	 * @return
	 */
	public static String getUserName(final Object userId) {
		User user = CacheKit.get(SYS_CACHE, GET_USER_NAME + userId, new ILoader() {
			@Override
			public Object load() {
				return Blade.create(User.class).findById(Convert.toInt(userId));
			}
		});
		if(null == user){
			return "";
		}
		return user.getName();
	}

	/**
	 * 获取对应部门名
	 * @param deptIds 部门id集合
	 * @return
	 */
	public static String getDeptName(final Object deptIds) {
		if(Func.isEmpty(deptIds)){
			return "";
		}
		final Integer[] deptIdArr = Convert.toIntArray(deptIds.toString());
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < deptIdArr.length; i++){
			final Integer deptId = deptIdArr[i];
			Dept dept = CacheKit.get(SYS_CACHE, GET_DEPT_NAME + deptId, new ILoader() {
				@Override
				public Object load() {
					return Blade.create(Dept.class).findById(deptId);
				}
			});
			if (null != dept)
				sb.append(dept.getSimplename()).append(",");
		}
		return StrKit.removeSuffix(sb.toString(), ",");
	}
	
	/**   
	 * 获取参数表参数值
	 * @param code 参数编号
	 * @return String
	*/
	public static String getParamByCode(Object code){
		Parameter param = Blade.create(Parameter.class).findFirstBy("code = #{code} and status = 1", CMap.init().set("code", Convert.toInt(code)));
		return param.getPara();
	}
}
