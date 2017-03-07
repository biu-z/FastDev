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
package com.biu.core.constant;

import com.biu.core.listener.ConfigListener;

import java.util.Map;

public interface ConstConfig {

	Map<String, String> pool = ConfigListener.getConf();

	String DBTYPE = pool.get("master.dbType");
	String DRIVER = pool.get("master.driver");
	String URL = pool.get("master.url");
	String USERNAME = pool.get("master.username");
	String PASSWORD = pool.get("master.password");
	String INITIALSIZE = pool.get("druid.initialSize");
	String MAXACTIVE = pool.get("druid.maxActive");
	String MINIDLE = pool.get("druid.minIdle");
	String MAXWAIT = pool.get("druid.maxWait");

	String REAL_PATH = pool.get("realPath");
	String CONTEXT_PATH = pool.get("contextPath");
	String DOMAIN = pool.get("config.domain");
	String REMOTE_MODE = pool.get("config.remoteMode");
	String REMOTE_PATH = pool.get("config.remotePath");
	String UPLOAD_PATH = pool.get("config.uploadPath");
	String DOWNLOAD_PATH = pool.get("config.downloadPath");
	String COMPRESS = pool.get("config.compress");
	String COMPRESS_SCALE = pool.get("config.compressScale");
	String COMPRESS_FLAG = pool.get("config.compressFlag");
	
}
