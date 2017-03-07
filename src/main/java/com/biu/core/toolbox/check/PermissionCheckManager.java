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
package com.biu.core.toolbox.check;

import com.biu.core.constant.Cst;

/**
 * 权限检查工厂
 */
public class PermissionCheckManager {
	private final static PermissionCheckManager me = new PermissionCheckManager();

	private ICheck defaultCheckFactory = Cst.me().getDefaultCheckFactory();

	public static PermissionCheckManager me() {
		return me;
	}

	private PermissionCheckManager() {
	}

	public PermissionCheckManager(ICheck checkFactory) {
		this.defaultCheckFactory = checkFactory;
	}

	public void setDefaultCheckFactory(ICheck defaultCheckFactory) {
		this.defaultCheckFactory = defaultCheckFactory;
	}

	public static boolean check(Object[] permissions) {
		return me.defaultCheckFactory.check(permissions);
	}
	
	public static boolean checkAll() {
		return me.defaultCheckFactory.checkAll();
	}
}
