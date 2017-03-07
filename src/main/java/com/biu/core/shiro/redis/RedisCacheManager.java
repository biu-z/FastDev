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
package com.biu.core.shiro.redis;

import org.apache.shiro.ShiroException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存接口管理类
 */
public class RedisCacheManager implements CacheManager, Initializable, Destroyable {

	private String keyPrefix = "shiro_cache:";

	@SuppressWarnings("rawtypes")
	private Map<String, Cache> caches;
	
	private String redisName;

	public RedisCacheManager() {

	}
	
	public String getRedisName() {
		return redisName;
	}

	public void setRedisName(String redisName) {
		this.redisName = redisName;
	}
	
	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	@Override
	public final void init() throws ShiroException {
		if(null == caches) {
			caches = new ConcurrentHashMap<>();
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        Cache cache = caches.get(name);  
        if (null == cache) {  
        	synchronized (RedisCacheManager.class) {
        		if (null == cache) {  
                	cache = new RedisCache<K, V>(getRedisName(), getKeyPrefix(), name);  
                    caches.put(name, cache);  
        		}
        	}
        }  
        return cache; 
	}

	@Override
	public void destroy() throws Exception {
		caches.clear();
	}

}
