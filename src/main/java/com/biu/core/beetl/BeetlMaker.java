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
package com.biu.core.beetl;

import com.biu.core.toolbox.kit.CharsetKit;
import com.biu.core.toolbox.kit.FileKit;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Beetl静态化生成工具类
 */
public class BeetlMaker {

	/**
	 * 生成文件
	 * 
	 * @param tlPath 模板路径
	 * @param paras 参数
	 * @param filePath  文件保存路径
	 */
	public static void makeFile(String tlPath, Map<String, Object> paras, String filePath) {
		makeFile(tlPath, paras, filePath, CharsetKit.UTF_8);
	}
	
	/**
	 * 生成文件
	 * 
	 * @param tlPath 模板路径
	 * @param paras 参数
	 * @param filePath  文件保存路径
	 * @param charsetName  编码
	 */
	public static void makeFile(String tlPath, Map<String, Object> paras, String filePath, String charsetName) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), charsetName));
			BeetlTemplate.buildTo(FileKit.readString(tlPath, charsetName), paras, pw);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

}
