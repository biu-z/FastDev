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
package com.biu.core.beetl.tag;

import com.biu.core.aop.AopContext;
import com.biu.core.constant.ConstCache;
import com.biu.core.constant.ConstCacheKey;
import com.biu.core.constant.Cst;
import com.biu.core.meta.IQuery;
import com.biu.core.plugins.dao.Db;
import com.biu.core.shiro.ShiroKit;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.cache.ILoader;
import com.biu.core.toolbox.kit.ClassKit;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.core.toolbox.kit.StrKit;
import org.beetl.core.Tag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SelectTag extends Tag {

	@Override
	@SuppressWarnings("unchecked")
	public void render() {
		try {
			Map<String, String> param = (Map<String, String>) args[1];

			final String code = param.get("code");
			String name = param.get("name");
			String value = Func.toStr(param.get("value"));
			String token = (StrKit.notBlank(value)) ? "" : Func.toStr(param.get("token"));
			String type = param.get("type");
			String where = param.get("where");
			String required = param.get("required");
			String tail = param.get("tail");
			String inter = param.get("intercept");
			String sql = "";
			
			Map<String, Object> modelOrMap = CMap.createHashMap();
			
			IQuery intercept = Cst.me().getDefaultQueryFactory();
			
			String CACHE_NAME = ConstCache.SYS_CACHE;
			
			if (type.equals("dict")) {
				sql = "select num as ID,pId as PID,name as TEXT from  BLADE_DICT where code='" + code + "' and num > 0 order by num asc";
				intercept = Cst.me().getDefaultSelectFactory().dictIntercept();
			} else if (type.equals("user")) {
				CACHE_NAME = ConstCache.SYS_CACHE;
				sql = "select ID,name as TEXT from  BLADE_USER where status=1";
				intercept = Cst.me().getDefaultSelectFactory().userIntercept();
			} else if (type.equals("dept")) {
				CACHE_NAME = ConstCache.SYS_CACHE;
				sql = "select ID,PID,SIMPLENAME as TEXT from  BLADE_DEPT";
				intercept = Cst.me().getDefaultSelectFactory().deptIntercept();
			} else if (type.equals("role")) {
				CACHE_NAME = ConstCache.SYS_CACHE;
				sql = "select ID,name as TEXT from  BLADE_ROLE";
				intercept = Cst.me().getDefaultSelectFactory().roleIntercept();
			} else if (type.equals("diy")) {
				CACHE_NAME = ConstCache.SYS_CACHE;
				type = type + "_" + param.get("source");
				if(StrKit.notBlank(where)){
					modelOrMap = JsonKit.parse(where, Map.class);
				}
//				sql = Md.getSql(param.get("source"));
			}

			if(StrKit.notBlank(inter)) {
				intercept = ClassKit.newInstance(inter);
			}
			
			final String _sql = sql;
			final Map<String, Object> _modelOrMap = modelOrMap;
			final IQuery _intercept = intercept;
			
			List<Map<String, Object>> dict = CacheKit.get(CACHE_NAME, ConstCacheKey.DICT + type + "_" + code + "_" + ShiroKit.getUser().getId(), new ILoader() {
				@Override
				public Object load() {
					return Db.selectList(_sql, _modelOrMap, new AopContext(), _intercept);
				}
			}); 

			StringBuilder sb = new StringBuilder();
			String [] arr = name.split("\\.");
			String sid = arr[0];
			if (arr.length == 2) {
				sid = arr[1];
			}
			sid = "_" + sid;
			sb.append("<select onchange=\"" +sid + "_selectChanged('" + sid + "')\" " + required + " class=\"form-control\" id=\"" + sid + "\"  name=\"" + token + name + "\">");
			sb.append("<option value></option>");
			
			for (Map<String, Object> dic : dict) {
				String id =  Func.toStr(dic.get("ID"));
				String selected = "";
				if (Func.equals(id, value)) {
					selected = "selected";
				}
				sb.append("<option " + selected + " value=\"" + id + "\">" + dic.get("TEXT") + "</option>");
			}
			sb.append("</select>");
			
			
			sb.append("<script type=\"text/javascript\">");
			sb.append("		function " +sid + "_selectChanged(sid) {");
			sb.append("			$('#form_token').val(1);");
			sb.append("			$('#' + sid).attr('name','"+name+"');");
			if(StrKit.notBlank(tail)) {
				sb.append("			var options=$('#' + sid + ' option:selected');");
				sb.append("			$('#' + sid + '_ext').val(options.text());");
			}
			sb.append("		};");
			sb.append("</script>");
			if(StrKit.notBlank(tail)) {
				sb.append("<input type=\"hidden\" id=\"" + sid + "_ext\" name=\"" + name.split("\\.")[0] + "." + tail + "\">");
			}

			ctx.byteWriter.writeString(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
