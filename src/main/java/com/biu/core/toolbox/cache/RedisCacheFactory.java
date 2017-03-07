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
package com.biu.core.toolbox.cache;

import com.biu.core.plugins.dao.Redis;
import com.biu.core.toolbox.redis.IJedis;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis缓存工厂
 */
public class RedisCacheFactory extends BaseCacheFactory {
	
	private IJedis jedis;
	
	private String redisName;
	
	public String getRedisName() {
		return redisName;
	}

	public void setRedisName(String redisName) {
		this.redisName = redisName;
	}
	
	private IJedis getCacheManager() {
		if (jedis == null) {
			synchronized (RedisCacheFactory.class) {
				if (jedis == null) {
					jedis = Redis.init(getRedisName());
				}
			}
		}
		return jedis;
	}
	
	public RedisCacheFactory() {
		this.redisName = "cache";
	}
	
	public RedisCacheFactory(String redisName) {
		this.redisName = redisName;
	}
	
	public void put(String cacheName, Object key, Object value) {
		getCacheManager().hset(cacheName, key, value);
	}

	public <T> T get(String cacheName, Object key) {
		return getCacheManager().hget(cacheName, key);
	}

	@SuppressWarnings("rawtypes")
	public List getKeys(String cacheName) {
		return new ArrayList<>(getCacheManager().hkeys(cacheName));
	}

	public void remove(String cacheName, Object key) {
		getCacheManager().hdel(cacheName, key);
	}

	public void removeAll(String cacheName) {
		getCacheManager().del(cacheName);
	}

}
