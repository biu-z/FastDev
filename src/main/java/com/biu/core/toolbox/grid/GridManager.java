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
package com.biu.core.toolbox.grid;

import com.biu.common.base.controller.BladeController;
import com.biu.core.constant.Cst;
import com.biu.core.meta.IQuery;

public class GridManager {
	private final static GridManager me = new GridManager();

	private IGrid defaultGridFactory = Cst.me().getDefaultGridFactory();

	public static GridManager me() {
		return me;
	}

	private GridManager() {}

	public void setDefaultGridFactory(IGrid defaultGridFactory) {
		this.defaultGridFactory = defaultGridFactory;
	}
	
	public static Object paginate(String dbName, Integer page, Integer rows, String sqlTemplate, String para, String sort, String order, IQuery intercept, BladeController ctrl) {
		return me.defaultGridFactory.paginate(dbName, page, rows, sqlTemplate, para, sort, order, intercept, ctrl);
	}

}
