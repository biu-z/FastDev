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

import com.biu.common.tool.SysCache;
import com.biu.core.aop.AopContext;
import com.biu.core.meta.PageIntercept;
import com.biu.core.toolbox.grid.BladePage;

import java.util.List;
import java.util.Map;

public class UserIntercept extends PageIntercept {

	/**
	 * 查询后操作 字典项、部门不通过数据库查询,通过缓存附加,减轻数据库压力,提高分页效率
	 * 
	 * @param ac
	 */
	@SuppressWarnings("unchecked")
	public void queryAfter(AopContext ac) {
		BladePage<Map<String, Object>> page = (BladePage<Map<String, Object>>) ac.getObject();
		List<Map<String, Object>> list = page.getRows();
		for (Map<String, Object> map : list) {
			map.put("rolename", SysCache.getRoleName(map.get("roleid")));
			map.put("statusname", SysCache.getDictName(901, map.get("status")));
			map.put("sexname", SysCache.getDictName(101, map.get("sex")));
			map.put("deptname", SysCache.getDeptName(map.get("deptid")));
		}
	}
}
