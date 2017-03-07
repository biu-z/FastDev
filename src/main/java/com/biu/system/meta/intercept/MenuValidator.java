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

import com.biu.core.aop.Invocation;
import com.biu.core.intercept.BladeValidator;
import com.biu.core.plugins.dao.Blade;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.kit.StrKit;
import com.biu.system.model.Menu;

public class MenuValidator extends BladeValidator {

	@Override
	protected void doValidate(Invocation inv) {
		
		if (inv.getMethod().toString().indexOf("update") == -1) {
			validateRequired("blade_menu.pcode", "请输入菜单父编号");
			validateCode("blade_menu.code", "菜单编号已存在!");
		}
		validateSql("blade_menu.source", "含有非法字符,请仔细检查!");
		
	}

	protected void validateCode(String field, String errorMessage) {
		String code = request.getParameter(field);
		if (StrKit.isBlank(code)) {
			addError("请输入菜单编号!");
		}
		Blade blade = Blade.create(Menu.class);
		String sql = "select * from blade_menu where code = #{code}";
		boolean temp = blade.isExist(sql, CMap.init().set("code", code));
		if (temp) {
			addError(errorMessage);
		}
	}

}
