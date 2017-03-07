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
import com.biu.core.meta.IQuery;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class LigerGridFactory extends BaseGridFactory {

	public LigerGrid<Map<String, Object>> paginate(String dbName, Integer page, Integer rows,
			String source, String para, String sort, String order,
			IQuery intercept, BladeController ctrl) {
		BladePage<Map<String, Object>> list = (BladePage<Map<String, Object>>) super.basePaginate(dbName, page, rows, source, para, sort, order, intercept, ctrl);
		
		int _pageNum = (int) list.getPage();
		int _pageSize = (int) list.getPageSize();
		int _totalPage = (int) list.getTotal();
		long _totalSize = list.getRecords();
		List<Map<String, Object>> _rows = list.getRows();
		
		LigerGrid<Map<String, Object>> grid = new LigerGrid<>(_pageNum, _pageSize, _totalPage, _totalSize, _rows);
		return grid;
	}

}
