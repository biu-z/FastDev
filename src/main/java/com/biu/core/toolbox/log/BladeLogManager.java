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
package com.biu.core.toolbox.log;

import com.biu.core.constant.Cst;
import com.biu.core.toolbox.CMap;

/**
 * 日志工厂
 */
public class BladeLogManager {
	private final static BladeLogManager me = new BladeLogManager();

	private ILog defaultLogFactory = Cst.me().getDefaultLogFactory();

	public static BladeLogManager me() {
		return me;
	}

	private BladeLogManager() {
	}

	public BladeLogManager(ILog checkFactory) {
		this.defaultLogFactory = checkFactory;
	}

	public ILog getDefaultLogFactory() {
		return defaultLogFactory;
	}

	public void setDefaultLogFactory(ILog defaultLogFactory) {
		this.defaultLogFactory = defaultLogFactory;
	}

	public static String[] logPatten() {
		return me.defaultLogFactory.logPatten();
	}

	public static CMap logMaps() {
		return me.defaultLogFactory.logMaps();
	}

	public static boolean isDoLog() {
		return me.defaultLogFactory.isDoLog();
	}

	public static void doLog(String logName, String msg, boolean succeed) {
		if (isDoLog())
			me.defaultLogFactory.doLog(logName, msg, succeed);
	}
}
