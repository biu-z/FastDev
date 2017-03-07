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

import com.biu.common.base.controller.BladeController;
import com.biu.core.constant.ConstConfig;
import com.biu.core.constant.Cst;
import com.biu.core.plugins.dao.Db;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.ajax.AjaxResult;
import com.biu.core.toolbox.file.BladeFile;
import com.biu.core.toolbox.file.BladeFileKit;
import com.biu.core.toolbox.kit.PathKit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/kindeditor")
public class KindEditorController extends BladeController {
	
	@ResponseBody
	@RequestMapping("/upload_json")
	public CMap upload_json(@RequestParam("imgFile") MultipartFile file) {
		CMap cmap = CMap.init();
		if (null == file) {
			cmap.set("error", 1);
			cmap.set("message", "请选择要上传的图片");
			return cmap;
		}
		String originalFileName = file.getOriginalFilename();
		String dir = getParameter("dir", "image");
		// 测试后缀
		boolean ok = BladeFileKit.testExt(dir, originalFileName);
		if (!ok) {
			cmap.set("error", 1);
			cmap.set("message", "上传文件的类型不允许");
			return cmap;
		}
		BladeFile bf = getFile(file, dir);
		bf.transfer();
		Object fileId = bf.getFileId();	
		String url = ConstConfig.DOMAIN + bf.getUploadVirtualPath();
		cmap.set("error", 0);
		cmap.set("title", fileId);
		cmap.set("url", url);
		cmap.set("name", originalFileName);
		return cmap;	
	}
	
	@ResponseBody
	@RequestMapping("/file_manager_json")
	public Object file_manager_json() {
		String dir = getParameter("dir", "image");
		// 考虑安全问题
		String path = getParameter("path", "");
		// 不允许使用..移动到上一级目录
		if (path.indexOf("..") >= 0) {
			return "Access is not allowed.";
		}
		// 最后一个字符不是/
		if (!"".equals(path) && !path.endsWith("/")) {
			return "Parameter is not valid.";
		}
		String order = getParameter("order", "name");

		Map<String, Object> result = BladeFileKit.listFiles(dir, path, order);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/initimg")
	public AjaxResult initimg(@RequestParam String id) {
		Map<String, Object> img = Db.findById("BLADE_ATTACH", id);
		if (null != img) {
			String url = ConstConfig.DOMAIN + img.get("URL");
			img.put("URL", url);
			return json(img);
		} else {
			return fail("获取图片失败！");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/initfile")
	public AjaxResult initfile(@RequestParam String ids) {
		List<Map> file = Db.selectList("select ID as \"id\",NAME as \"name\",URL as \"url\" from BLADE_ATTACH where ID in (#{join(ids)})", CMap.init().set("ids", ids.split(",")));
		if (null != file) {
			for (Map m : file) {
				String url = ConstConfig.DOMAIN + m.get("url");
				m.put("url", url);
			}
			return json(file);
		} else {
			return fail("获取附件失败！");
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/renderFile/{id}")
	public void renderFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
		Map<String, Object> file = Db.findById("BLADE_ATTACH", id);
		String url = file.get("URL").toString();
		File f = new File((Cst.me().isRemoteMode() ? "" : PathKit.getWebRootPath()) + url);
		makeFile(response, f);
	}
	
	
}
