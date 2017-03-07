package com.biu.system.controller;

import com.biu.common.base.controller.BaseController;
import com.biu.core.plugins.dao.Blade;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.cache.CacheKit;
import com.biu.core.toolbox.kit.JsonKit;
import com.biu.system.meta.intercept.DeptIntercept;
import com.biu.system.model.Attach;
import com.biu.system.service.AttachService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/attach")
public class AttachController extends BaseController {

	private static String LIST_SOURCE = "blade_attach";
	private static String BASE_PATH = "/system/attach/";
	private static String CODE = "attach";
	private static String PREFIX = "blade_attach";

	@Resource
	private AttachService attachService;

	@RequestMapping("/")
	public String index(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "attach.html";
	}


	@ResponseBody
	@RequestMapping(KEY_LIST)
	public Object list() {
		Object gird = paginate(LIST_SOURCE, new DeptIntercept());
		return gird;
	}

	@RequestMapping(KEY_ADD)
	public String add(ModelMap mm) {
		mm.put("code", CODE);
		return BASE_PATH + "attach_add.html";
	}

	@RequestMapping(KEY_ADD + "/{id}")
	public String add(@PathVariable Integer id, ModelMap mm) {
		if (null != id) {
			mm.put("pId", id);
		}
		mm.put("code", CODE);
		return BASE_PATH + "attach_add.html";
	}

	@RequestMapping(KEY_EDIT + "/{id}")
	public String edit(@PathVariable Integer id, ModelMap mm) {
		Attach Dept = Blade.create(Attach.class).findById(id);
		mm.put("model", JsonKit.toJson(Dept));
		mm.put("code", CODE);
		return BASE_PATH + "attach_edit.html";
	}

	@RequestMapping(KEY_VIEW + "/{id}")
	public String view(@PathVariable Integer id, ModelMap mm) {
		Attach attach = attachService.getOneByKey(id);
		mm.put("model", JsonKit.toJson(attach));
		mm.put("code", CODE);
		return BASE_PATH + "attach_view.html";
	}

	@ResponseBody
	@RequestMapping(KEY_SAVE)
	public AjaxResult save() {
		Attach dept = mapping(PREFIX, Attach.class);
//		boolean temp = Blade.create(Dept.class).save(dept);
		int temp = attachService.save(dept);
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
		Attach dept = mapping(PREFIX, Attach.class);
//		boolean temp =  Blade.create(Dept.class).update(dept);
		int temp = attachService.updateNotEmpty(dept);
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
		int cnt = Blade.create(Attach.class).deleteByIds(getParameter("ids"));
		if (cnt > 0) {
			CacheKit.removeAll(SYS_CACHE);
			return success("删除成功!");
		} else {
			return error("删除失败!");
		}
	}

}
