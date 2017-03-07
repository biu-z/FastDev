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
import com.biu.system.model.User;

public class UserValidator extends BladeValidator {

	@Override
	protected void doValidate(Invocation inv) {
		
		if (inv.getMethod().toString().indexOf("update") == -1) {
			validateAccount("BLADE_USER.ACCOUNT", "账号已存在");
			validateStringExt("BLADE_USER.ACCOUNT",  "含有非法字符,请检查");
			validateRequired("BLADE_USER.ACCOUNT",  "请输入账号");
			validateString("BLADE_USER.ACCOUNT", 5, 11,  "请输入5~11位的账号");
		}
		validateStringExt("BLADE_USER.NAME",  "含有非法字符,请检查");
		validateRequired("BLADE_USER.NAME",  "请输入姓名");
		
		validateRequired("BLADE_USER.BIRTHDAY",  "请选择出生日期");
		validateDate("BLADE_USER.BIRTHDAY",  "请输入正确的日期格式");
		
		validateRequired("BLADE_USER.PASSWORD",  "请输入密码");
		
		validateRequired("password",  "请输入确认密码");
		validateTwoEqual("BLADE_USER.PASSWORD", "password",  "两次密码不相同");
	}

	protected void validateAccount(String field, String errorMessage) {
		String account = request.getParameter(field);
		if (StrKit.isBlank(account)) {
			addError("请输入账号!");
		}
		if (Blade.create(User.class).isExist("SELECT * FROM blade_user WHERE account = #{account} and status=1", CMap.init().set("account", account))) {
			addError(errorMessage);
		}
	}

}
