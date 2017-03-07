package com.biu.system.controller;

import com.biu.common.base.controller.BaseController;
import com.biu.core.beetl.BeetlMaker;
import com.biu.core.constant.Cst;
import com.biu.core.plugins.dao.Blade;
import com.biu.core.plugins.dao.Db;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.kit.DateKit;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.core.toolbox.kit.LogKit;
import com.biu.core.toolbox.kit.StrKit;
import com.biu.system.model.Dept;
import com.biu.system.model.Generate;
import com.biu.system.service.GenerateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/generate")
public class GenerateController extends BaseController {

	private static String BASE_PATH = "/system/generate/";
	private static String CODE = "generate";
	private static String PREFIX = "blade_generate";

	@Resource
	private GenerateService generateService;

	@RequestMapping("/")
	public String index(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "generate.html";
	}

	@ResponseBody
	@RequestMapping(KEY_LIST)
	public Object list() {
		Object gird = paginate(PREFIX);
		return gird;
	}

	@RequestMapping(KEY_ADD)
	public String add(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "generate_add.html";
	}

	@RequestMapping(KEY_EDIT + "/{id}")
	public String edit(@PathVariable Integer id, ModelMap mm) {
		Generate generate = Blade.create(Generate.class).findById(id);
		mm.put("model", JsonKit.toJson(generate));
		mm.put("code", CODE);
		return BASE_PATH + "generate_edit.html";
	}

	@RequestMapping(KEY_VIEW + "/{id}")
	public String view(@PathVariable Integer id, ModelMap mm) {
		Generate generate = Blade.create(Generate.class).findById(id);
		mm.put("model", JsonKit.toJson(generate));
		mm.put("code", CODE);
		return BASE_PATH + "generate_view.html";
	}

	@ResponseBody
	@RequestMapping(KEY_SAVE)
	public AjaxResult save() {
		Generate generate = mapping(PREFIX, Generate.class);
		int temp = generateService.save(generate);
		if (temp > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success("新增成功");
		} else {
			return error("新增失败");
		}
	}

	@ResponseBody
	@RequestMapping(KEY_UPDATE)
	public AjaxResult update() {
		Generate generate = mapping(PREFIX, Generate.class);
		int temp =  generateService.updateNotEmpty(generate);
		if (temp > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success("修改成功");
		} else {
			return error("修改失败");
		}
	}

	@ResponseBody
	@RequestMapping(KEY_REMOVE)
	public AjaxResult remove() {
		int cnt = Blade.create(Dept.class).deleteByIds(getParameter("ids"));
		if (cnt > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success("删除成功!");
		} else {
			return error("删除失败!");
		}
	}


	@ResponseBody
	@RequestMapping("/pojo/{table}")
	public String createPojo(@PathVariable String table) {
		try {
//			Blade.dao().genPojoCodeToConsole(table);
			return "[ " + table + " ] pojo生成成功,请查看控制台";
		} catch (Exception e) {
			return "[ " + table + " ] pojo生成失败:" + e.getMessage();
		}
	}
	
	@ResponseBody
	@RequestMapping("/pojo/{slave}/{table}")
	public String createPojoSlave(@PathVariable String slave, @PathVariable String table) {
		try {
//			Blade.dao(slave).genPojoCodeToConsole(table);
			return "[ " + table + " ] pojo生成成功,请查看控制台";
		} catch (Exception e) {
			return "[ " + table + " ] pojo生成失败:" + e.getMessage();
		}
	}
	
	@ResponseBody
	@RequestMapping("/sql/{table:.+}")
	public String createBuiltInSql(@PathVariable String table) {
		try {
			LogKit.println("\n\n-------------------------------- gen by beetlsql {} --------------------------------\n", DateKit.getTime());
			LogKit.println("-----↓------- curd -------↓-----\n");
//			Blade.dao().genBuiltInSqlToConsole(ClassKit.newInstance(table).getClass());
//			LogKit.println("\n-----↓-- updateNotNull --↓-----\n");
//			LogKit.println(Blade.dao().getDbStyle().genUpdateTemplate(ClassKit.newInstance(table).getClass()).getTemplate());
//			LogKit.println("\n-----↓------- field -------↓-----\n");
//			Blade.dao().genSQLTemplateToConsole(ClassKit.newInstance(table).getClass().getAnnotation(Table.class).name());
			return "[ " + table + " ] sql生成成功,请查看控制台";
		} catch (Exception e) {
			return "[ " + table + " ] sql生成失败:" + e.getMessage();
		}
	}
	
	@ResponseBody
	@RequestMapping("/sql/{slave}/{table:.+}")
	public String createBuiltInSqlSlave(@PathVariable String slave, @PathVariable String table) {
		try {
			LogKit.println("\n\n-------------------------------- gen by beetlsql {} --------------------------------\n", DateKit.getTime());
			LogKit.println("-----↓------- curd --------↓-----\n");
//			Blade.dao(slave).genBuiltInSqlToConsole(ClassKit.newInstance(table).getClass());
//			LogKit.println("\n-----↓-- updateNotNull --↓-----\n");
//			LogKit.println(Blade.dao(slave).getDbStyle().genUpdateTemplate(ClassKit.newInstance(table).getClass()).getTemplate());
//			LogKit.println("\n-----↓------ field -------↓-----\n");
//			Blade.dao(slave).genSQLTemplateToConsole(ClassKit.newInstance(table).getClass().getAnnotation(Table.class).name());
			return "[ " + table + " ] sql生成成功,请查看控制台";
		} catch (Exception e) {
			return "[ " + table + " ] sql生成失败:" + e.getMessage();
		}
	}
	
	
	@ResponseBody
	@RequestMapping("/code")
	public AjaxResult gencode(){
		String ids = getParameter("ids");
		List<Generate> list = Blade.create(Generate.class).findBy("id in ("+ids+")", CMap.init());

		for (Generate gen : list) {
			
			String realPath = gen.getRealpath() + File.separator + "src" + File.separator + "main";
			String packageName = gen.getPackagename();
			String modelName = gen.getModelname();
			String upperModelName = StrKit.upperFirst(modelName);
			String lowerModelName = StrKit.lowerFirst(modelName);
			
			String tableName = gen.getTablename();
			String pkName = gen.getPkname();
			String path = File.separator + realPath + File.separator + "java" + File.separator + packageName.replace(StrKit.DOT, File.separator);
			String resourcesPath = File.separator + realPath + File.separator + "resources";
			String webappPath = File.separator + realPath + File.separator + "webapp" + File.separator + "WEB-INF" + File.separator + "view";
			
			//java
			String controllerPath = path + File.separator + "controller" + File.separator + upperModelName + "Controller.java";
			String modelPath = path + File.separator + "model" + File.separator + upperModelName + ".java";
			String servicePath = path + File.separator + "service" + File.separator + upperModelName + "Service.java";
			String mapperPath = path + File.separator + "mapper" + File.separator + upperModelName + "Mapper.java";
			String serviceimplPath = path + File.separator + "service" + File.separator + "impl" + File.separator + upperModelName + "ServiceImpl.java";
			
			//resources
			String xmlPath = resourcesPath + File.separator + "mappings" + File.separator + "generate" + File.separator + upperModelName + "Mapper.xml";
			
			//webapp
			String indexPath = webappPath + File.separator + "generate" + File.separator + lowerModelName + File.separator + lowerModelName + ".html";
			String addPath = webappPath + File.separator + "generate" + File.separator + lowerModelName + File.separator + lowerModelName + "_add.html";
			String editPath = webappPath + File.separator + "generate" + File.separator + lowerModelName + File.separator + lowerModelName + "_edit.html";
			String viewPath = webappPath + File.separator + "generate" + File.separator + lowerModelName + File.separator + lowerModelName + "_view.html";
			
			Map<String, String> pathMap = new HashMap<>();
			pathMap.put("controllerPath", controllerPath);
			pathMap.put("modelPath", modelPath);
			pathMap.put("mapperPath", mapperPath);
			pathMap.put("servicePath", servicePath);
			pathMap.put("serviceimplPath", serviceimplPath);
			pathMap.put("xmlPath", xmlPath);
			pathMap.put("indexPath", indexPath);
			pathMap.put("addPath", addPath);
			pathMap.put("editPath", editPath);
			pathMap.put("viewPath", viewPath);
			
			//mkdirs
			for (Map.Entry<String, String> entry : pathMap.entrySet()) {  
				File file = new File(entry.getValue());
				if (file.exists()) {
					continue;
				} else {
					file.getParentFile().mkdirs();
				}
			}
			
			//java
			String _templatePath = File.separator + Cst.me().getRealPath() + File.separator + "WEB-INF" + File.separator + "view" + File.separator + "common" + File.separator + "_template" + File.separator;
			String controllerTemplatePath = _templatePath + "_controller" + File.separator + "_controller.bld";
			String modelTemplatePath = _templatePath + "_model" + File.separator +  "_model.bld";
			String mapperTemplatePath = _templatePath + "_mapper" + File.separator +  "_mapper.bld";
			String serviceTemplatePath = _templatePath + "_service" + File.separator + "_service.bld";
			String serviceimplTemplatePath = _templatePath + "_service" + File.separator + "_impl" + File.separator + "_serviceimpl.bld";
			
			//resources
			String xmlTemplatePath = _templatePath + "_mappings" + File.separator + "_xml.bld";
			
			//webapp
			String indexTemplatePath = _templatePath + "_view" + File.separator + "_index.bld";
			String addTemplatePath = _templatePath + "_view" + File.separator + "_add.bld";
			String editTemplatePath = _templatePath + "_view" + File.separator + "_edit.bld";
			String viewTemplatePath = _templatePath + "_view" + File.separator + "_view.bld";
			
			Map<String, Object> ps = new HashMap<>();
			ps.put("realPath", realPath);
			ps.put("packageName", packageName);
			ps.put("modelName", upperModelName);
			ps.put("lowerModelName", lowerModelName);
			ps.put("tableName", tableName);
			ps.put("pkName", pkName);
			
			//java
			BeetlMaker.makeFile(controllerTemplatePath, ps, controllerPath);
			BeetlMaker.makeFile(serviceTemplatePath, ps, servicePath);
			BeetlMaker.makeFile(serviceimplTemplatePath, ps, serviceimplPath);
			BeetlMaker.makeFile(mapperTemplatePath, ps, mapperPath);
			setParasAttr(tableName, ps);
			BeetlMaker.makeFile(modelTemplatePath, ps, modelPath);
			
			//resources
			BeetlMaker.makeFile(xmlTemplatePath, ps, xmlPath);
			
			//webapp
			BeetlMaker.makeFile(indexTemplatePath, ps, indexPath);
			BeetlMaker.makeFile(addTemplatePath, ps, addPath);
			BeetlMaker.makeFile(editTemplatePath, ps, editPath);
			BeetlMaker.makeFile(viewTemplatePath, ps, viewPath);
		}
		
		return success("生成成功,已经存在的文件将会覆盖!");
	}
	

	private void setParasAttr(String tableName, Map<String, Object> ps) {

		//查询数据库表字段信息
		String sql = "SELECT * FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = upper(#{tableName})";
		List<Map> table = Db.selectList(sql,CMap.init().set("tableName",tableName));

		//转换数据库类型为Java属性
		List<Map<String, Object>> attrs = new ArrayList<>();
		boolean isBigInt = false;
		boolean isDecimal = false;
		boolean isDate = false;
		for(Map col : table){
			Map<String, Object> attr = CMap.createHashMap();
			attr.put("comment", col.get("COLUMN_COMMENT"));
			attr.put("name", col.get("COLUMN_NAME"));
			//JDBC数据类型
			String DATA_TYPE = col.get("DATA_TYPE").toString();
			String javaType = "";

			switch (DATA_TYPE){
				case "datetime" :
					javaType = "Date";
					isDate = true;
					break;
				case "timestamp" :
					javaType = "Date";
					isDate = true;
					break;
				case "time" :
					javaType = "Date";
					isDate = true;
					break;
				case "date" :
					javaType = "Date";
					isDate = true;
					break;
				case "char" :
					javaType = "String";
					break;
				case "varchar" :
					javaType = "String";
					break;
				case "text" :
					javaType = "String";
					break;
				case "longtext" :
					javaType = "String";
					break;
				case "float" :
					javaType = "Float";
					break;
				case "int" :
					javaType = "Integer";
					break;
				case "double" :
					javaType = "Double";
					break;
				case "bigint" :
					javaType = "java.math.BigInteger";
					isBigInt = true;
					break;
				case "decimal" :
					javaType = "java.math.BigDecimal";
					isDecimal = true;
					break;
			}

			attr.put("type", javaType);
			attrs.add(attr);
		}

		String srcHead = "";
		String CR = System.getProperty("line.separator");
		if(isDate) {
			srcHead += "import java.util.Date;" + CR;
		}
		if(isBigInt) {
			srcHead += "import java.math.BigInteger;" + CR;
		}
		if(isDecimal) {
			srcHead += "import java.math.BigDecimal;" + CR;
		}

		ps.put("attrs", attrs);
		ps.put("imports", srcHead);
	}
}
