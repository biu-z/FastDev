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
package com.biu.core.listener;

import com.biu.core.constant.Const;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.kit.ObjectKit;
import com.biu.core.toolbox.kit.PropKit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigListener implements ServletContextListener {

	private static Map<String, String> conf = new HashMap<String, String>();

	public static Map<String, String> getConf() {
		return ObjectKit.clone(conf);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		conf.clear();
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext sc = evt.getServletContext();
		// 项目路径
		conf.put("realPath", sc.getRealPath("/").replaceFirst("/", ""));
		conf.put("contextPath", sc.getContextPath());

		Properties prop = PropKit.use(Const.PROPERTY_FILE).getProperties();
		for (Object name : prop.keySet()) {
			conf.put(name.toString(), Func.toStr(prop.get(name)));
		}
	}

}
