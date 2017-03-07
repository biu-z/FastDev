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
package com.biu.core.plugins.dao;

import com.biu.core.plugins.connection.RedisPlugin;
import com.biu.core.toolbox.redis.IJedis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis操作工具类
 */
public class RedisManager {
	private static Map<String, IJedis> pool = new ConcurrentHashMap<String, IJedis>();
	
	public static IJedis init() {
		return init(RedisPlugin.init().MASTER);
	}

	public static IJedis init(String name) {
		IJedis rc = pool.get(name);
		if (null == rc) {
			synchronized (RedisManager.class) {
				rc = pool.get(name);
				if (null == rc) {
					rc = RedisPlugin.init().getRedisCachePool().get(name);
					pool.put(name, rc);
				}
			}
		}
		return rc;
	}

	private RedisManager() {}

}
