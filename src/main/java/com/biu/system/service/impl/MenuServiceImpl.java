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
import com.biu.core.toolbox.CMap;
import com.biu.system.mapper.MenuMapper;
import com.biu.system.model.Menu;
import com.biu.system.service.MenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("menuService")
public class MenuServiceImpl extends BaseServiceImpl<Menu> implements MenuService {

	@Resource
	private MenuMapper menuMapper;

	@Override
	public int findLastNum(String code) {
		try{
			Blade blade = Blade.create(Menu.class);
			Menu menu = blade.findFirstBy("pCode = #{pCode} order by num desc", CMap.init().set("pCode", code));
			return menu.getNum() + 1;
		}
		catch(Exception ex){
			return 1;
		}
	}
	@Override
	public boolean isExistCode(String code) {
		Blade blade = Blade.create(Menu.class);
		String sql = "select * from blade_menu where code = #{code}";
		boolean temp = blade.isExist(sql, CMap.init().set("code", code));
		return temp;
	}
	@Override
	public boolean updateStatus(String ids, Integer status) {
		CMap paras = CMap.init().set("status", status);
		Blade blade = Blade.create(Menu.class);

		boolean temp = blade.updateBy("status=#{status}", "id in ("+ids+")", paras);
		return temp;
	}

	@Override
	public int deleteByIds(String ids) {
		return Blade.create(Menu.class).deleteByIds(ids);
	}
}
