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
package com.biu.system.controller;

import com.biu.common.base.controller.BaseController;
import com.biu.core.aop.AopContext;
import com.biu.core.constant.Cst;
import com.biu.core.meta.IQuery;
import com.biu.core.plugins.dao.Db;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.cache.ILoader;
import com.biu.core.toolbox.kit.ClassKit;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.core.toolbox.kit.StrKit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/combotree")
public class ComboTreeController extends BaseController {

	@ResponseBody
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/getTreeList")
	public AjaxResult getTreeList(@RequestParam String type, @RequestParam String source, @RequestParam String where, @RequestParam String intercept, @RequestParam String ext, @RequestParam String val, @RequestParam String treeId) {
		type = getTypeName(type, source);
		String sqlSource = getSql(type, source);
		
		Map<String, Object> params = CMap.createHashMap();
		if(!where.equals("0") && StrKit.notBlank(where)){
			params = JsonKit.parse(where, Map.class);
		}
		
		Map<String, Object> modelOrMap = params;
		
		IQuery _intercept = getIntercept(type);
		if(StrKit.notBlank(intercept) && !Func.equals(intercept, "0")){
			_intercept = ClassKit.newInstance(intercept);
		}
		
		final AopContext ac = new AopContext();
		ac.setObject(ext);
		ac.setTips("ztree");
		
		String search = getParameter("search", "");
		if (StrKit.notBlank(search)) {
			sqlSource = "select * from (" + sqlSource + ") search where name like CONCAT(CONCAT('%', #{search}),'%')";
			modelOrMap.put("search", search);
		}
		
		final String f_sqlSource = sqlSource;
		final Map<String, Object> f_modelOrMap = modelOrMap;
		final IQuery f_intercept = _intercept;
		List<Map> list = CacheKit.get(SYS_CACHE, GET_COMBOTREE + type + val + source + where + intercept + ext + treeId + search, new ILoader() {
			public Object load() {
				return Db.selectList(f_sqlSource, f_modelOrMap, ac, f_intercept);
			}
		});

		String key = (StrKit.notBlank(treeId) && !Func.equals(treeId, "0")) ? treeId : ((type.indexOf("dict") >= 0) ? "num" : "id");

		String [] arr = val.split(",");
		for(Map<String, Object> map : list){
			for(String v : arr){
				if(Func.toStr(map.get(key)).equals(v)){
					map.put("checked", "true");
				}
			}
		}
		
		return json(list);
	}
	
	@ResponseBody
	@RequestMapping("/getTreeListName")
	@SuppressWarnings("unchecked")
	public AjaxResult getTreeListName(@RequestParam String type, @RequestParam String source, @RequestParam String where, @RequestParam String val, @RequestParam String treeId){
		
		type = getTypeName(type, source);
		
		final String sqlSource = getSql(type, source);
		
		Map<String, Object> params = CMap.createHashMap();
		if(StrKit.notBlank(where)){
			params = JsonKit.parse(where, Map.class);
		}
		
		final Map<String, Object> modelOrMap = params;
		
		List<Map<String, Object>> list = CacheKit.get(SYS_CACHE, DICT_ZTREE_LIST + type,
				new ILoader() {
					public Object load() {
						return Db.selectList(sqlSource, modelOrMap);
					}
				});
		
		String name = "";
		
		String key = (StrKit.notBlank(treeId) && !Func.equals(treeId, "0")) ? treeId : ((type.indexOf("dict") >= 0) ? "num" : "id");

		String [] arr = val.split(",");
		for(Map<String, Object> map : list){
			for(String v : arr){
				if(Func.toStr(map.get(key)).equals(v)){
					name += Func.toStr(map.get("name")) + ",";
				}
			}
		}

		name = StrKit.removeSuffix(name, ",");
		
		return json(name);
	}
	
	private String getTypeName(String type, String source){
		if(type.indexOf("combotreeUser") >= 0){
			type = "user";
		} else if(type.indexOf("combotreeDept") >= 0){
			type = "dept";
		} else if(type.indexOf("combotreeRole") >= 0){
			type = "role";
		} else if(type.indexOf("combotree_") >= 0 || type.indexOf("combotreeDict") >= 0){
			type = "dict_" + type.replace("combotree_", "").replace("combotreeDict", "");
		} else {
			type = "diy_" + source;
		}
		return type;
	}
	
	private String getSql(String type, String source){
		System.out.println("type >>>> " + type);
		System.out.println("source >>>> " + source);

		String sql = "";
		if (type.indexOf("dict_") >= 0) {
			String code = type.replace("dict_", "");
			sql = "select NUM as \"num\",ID as \"id\",PID as \"pId\",NAME as \"name\",(case when (pId=0 or pId is null) then 'true' else 'false' end) \"open\" from  BLADE_DICT where code= '" + code + "'";;
		} else if (type.equals("user")) {
			sql = "select ID as \"id\",0 as \"pId\",NAME as \"name\",'true' as \"open\" from  BLADE_USER where status=1";
		} else if (type.equals("dept")) {
			sql = "select ID as \"id\",PID as \"pId\",SIMPLENAME as \"name\",(case when (pId=0 or pId is null) then 'true' else 'false' end) \"open\" from  BLADE_DEPT";
		} else if (type.equals("role")) {
			sql = "select ID as \"id\",PID as \"pId\",NAME as \"name\",(case when (pId=0 or pId is null) then 'true' else 'false' end) \"open\" from  BLADE_ROLE";
		}else if (type.contains("diy_role")) {
			sql = "select 0 as \"id\", 0 as \"pId\",'顶级' as \"name\",'true' as \"open\" from  dual \n" +
					"union\n" +
					"select ID as \"id\", pId as \"pId\",name as \"name\",(case when (pId=0 or pId is null) then 'true' else 'false' end) as \"open\" from  blade_role";
		}else if (type.contains("diy_dict")) {
			sql = "select 0 as \"id\", 0 as \"pId\",'顶级' as \"name\",'true' as \"open\" from  dual \n" +
					"union\n" +
					"select ID as \"id\", pId as \"pId\",name as \"name\",'false' as \"open\" from  blade_dict";
		}else if (type.contains("diy_dept")) {
			sql = "select 0 as \"id\", 0 as \"pId\",'顶级' as \"name\",'true' as \"open\" from  dual \n" +
					"union\n" +
					"select ID as \"id\", pId as \"pId\",simplename as \"name\",'false' as \"open\" from  blade_dept";
		}
		else {
//			sql = Md.getSql(source);
		}
		return sql;
	}
	
	private IQuery getIntercept(String type) {
		IQuery intercept = Cst.me().getDefaultQueryFactory();
		if (type.indexOf("dict") >= 0) {
			intercept = Cst.me().getDefaultSelectFactory().dictIntercept();
		} else if (type.equals("user")) {
			intercept = Cst.me().getDefaultSelectFactory().userIntercept();
		} else if (type.equals("dept")) {
			intercept = Cst.me().getDefaultSelectFactory().deptIntercept();
		} else if (type.equals("role")) {
			intercept = Cst.me().getDefaultSelectFactory().roleIntercept();
		}  
		return intercept;
	}
	
}
