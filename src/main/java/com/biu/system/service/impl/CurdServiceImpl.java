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
package com.biu.system.service.impl;

import com.biu.common.base.controller.BladeController;
import com.biu.core.aop.AopContext;
import com.biu.core.meta.MetaIntercept;
import com.biu.core.plugins.dao.Blade;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.grid.GridManager;
import com.biu.core.toolbox.support.Convert;
import com.biu.system.service.CurdService;

//@Service
public class CurdServiceImpl implements CurdService {

	public boolean save(BladeController ctrl, Object model, Class<?> modelClass,
						MetaIntercept intercept) {
//		AopContext ac = null;
//		if (null != intercept) {
//			ac = new AopContext(ctrl, model);
//			intercept.saveBefore(ac);
//		}
//		String rtid = Blade.create(modelClass).saveRtStrId(model);
//		boolean tempAfter = true;
//		if (null != intercept && rtid.length() > 0) {
//			ac.setId(rtid);
//			tempAfter = intercept.saveAfter(ac);
//		}
//		return (rtid.length() > 0 && tempAfter);
		return true;
	}

	public boolean update(BladeController ctrl, Object model,
			Class<?> modelClass, MetaIntercept intercept) {
		AopContext ac = null;
		if (null != intercept) {
			ac = new AopContext(ctrl, model);
			intercept.updateBefore(ac);
		}
//		boolean temp = Blade.create(modelClass).update(model);
//		boolean tempAfter = true;
//		if (null != intercept && temp) {
//			tempAfter = intercept.updateAfter(ac);
//		}
//		return (temp && tempAfter);
		return true;
	}

	public boolean deleteByIds(BladeController ctrl, String ids,
			Class<?> modelClass, MetaIntercept intercept) {
		AopContext ac = null;
		if (null != intercept) {
			ac = new AopContext(ctrl);
			ac.setId(ids);
			intercept.removeBefore(ac);
		}
		int n = Blade.create(modelClass).deleteByIds(ids);
		boolean tempAfter = true;
		if (null != intercept && n > 0) {
			tempAfter = intercept.removeAfter(ac);
		}
		return (n > 0 && tempAfter);
	}

	public boolean delByIds(BladeController ctrl, String ids,
			Class<?> modelClass, MetaIntercept intercept) {
		AopContext ac = null;
		if (null != intercept) {
			ac = new AopContext(ctrl);
			ac.setId(ids);
			intercept.delBefore(ac);
		}
		boolean temp = Blade.create(modelClass).updateBy("status = #{status}", "id in (#{join(ids)})", CMap.init().set("status", 5).set("ids", Convert.toIntArray(ids)));
		boolean tempAfter = true;
		if (null != intercept && temp) {
			tempAfter = intercept.delAfter(ac);
		}
		return (temp && tempAfter);
	}

	public boolean restoreByIds(BladeController ctrl, String ids,
			Class<?> modelClass, MetaIntercept intercept) {
		AopContext ac = null;
		if (null != intercept) {
			ac = new AopContext(ctrl);
			ac.setId(ids);
			intercept.restoreBefore(ac);
		}
		boolean temp = Blade.create(modelClass).updateBy("status = #{status}", "id in (#{join(ids)})", CMap.init().set("status", 1).set("ids", Convert.toIntArray(ids)));
		boolean tempAfter = true;
		if (null != intercept && temp) {
			tempAfter = intercept.restoreAfter(ac);
		}
		return (temp && tempAfter);
	}

	public Object paginate(Integer page, Integer rows, String source,
			String para, String sort, String order, MetaIntercept intercept,
			BladeController ctrl) {
		Object list = GridManager.paginate(null, page, rows, source, para, sort, order, intercept, ctrl);
		return list;
	}

}
