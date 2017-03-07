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
package com.biu.system.meta.intercept;

import com.biu.common.base.controller.BladeController;
import com.biu.common.tool.SysCache;
import com.biu.core.aop.AopContext;
import com.biu.core.constant.ConstCache;
import com.biu.core.constant.ConstShiro;
import com.biu.core.meta.MetaIntercept;
import com.biu.core.plugins.dao.Blade;
import com.biu.core.shiro.ShiroKit;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.grid.BladePage;
import com.biu.system.model.Parameter;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

public class ParameterIntercept extends MetaIntercept {

	/**
	 * 列表转向前操作 只有超管才能访问参数管理
	 * 
	 * @param ac
	 */
	public void renderIndexBefore(AopContext ac) {
		if(ShiroKit.lacksRole(ConstShiro.ADMINISTRATOR)){
			ModelAndView view = ac.getView();
			view.setViewName("redirect:/unauth");
		}
	}
	
	/**
	 * 查询后操作
	 * 
	 * @param ac
	 */
	@SuppressWarnings("unchecked")
	public void queryAfter(AopContext ac) {
		BladePage<Map<String, Object>> page = (BladePage<Map<String, Object>>) ac.getObject();
		List<Map<String, Object>> list = page.getRows();
		for (Map<String, Object> map : list) {
			map.put("statusname", SysCache.getDictName(901, map.get("status")));
		}
	}
	
	/**
	 * 主表新增前操作
	 * 
	 * @param ac
	 */
	public void saveBefore(AopContext ac) {
		BladeController ctrl = ac.getCtrl();
		String code = ctrl.getParameter("blade_parameter.code");
		int cnt = Blade.create(Parameter.class).count("code = #{code}", CMap.init().set("code", code));
		if(cnt > 0){
			throw new RuntimeException("参数编号已存在!");
		}
	}
	
	/**
	 * 主表新增后操作(事务内)
	 * 
	 * @param ac
	 */
	public boolean saveAfter(AopContext ac) {
		CacheKit.remove(ConstCache.SYS_CACHE, "parameter_log");
		return super.saveAfter(ac);
	}
	
	/**
	 * 主表修改后操作(事务内)
	 * 
	 * @param ac
	 */
	public boolean updateAfter(AopContext ac) {
		CacheKit.remove(ConstCache.SYS_CACHE, "parameter_log");
		return super.updateAfter(ac);
	}
	
	/**
	 * 删除全部完毕后操作(事务外)
	 * 
	 * @param ac
	 */
	public AjaxResult removeSucceed(AopContext ac) {
		CacheKit.remove(ConstCache.SYS_CACHE, "parameter_log");
		return super.removeSucceed(ac);
	}
	
	/**
	 * 逻辑删除后操作(事务外)
	 * 
	 * @param ac
	 */
	public AjaxResult delSucceed(AopContext ac) {
		CacheKit.remove(ConstCache.SYS_CACHE, "parameter_log");
		return super.delSucceed(ac);
	}
	
	/**
	 * 还原全部完毕后操作(事务外)
	 * 
	 * @param ac
	 */
	public AjaxResult restoreSucceed(AopContext ac) {
		CacheKit.remove(ConstCache.SYS_CACHE, "parameter_log");
		return super.restoreSucceed(ac);
	}
	
}
