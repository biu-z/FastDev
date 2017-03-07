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
package com.biu.core.config;


import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * blade配置
 */
public class BladeConfig {

//	private static Map<String, SQLManager> sqlManagerPool = new ConcurrentHashMap<String, SQLManager>();
	private static Map<String, JedisPool> jedisPool = new ConcurrentHashMap<String, JedisPool>();
	private static Map<String, JedisCluster> jedisCluster = new ConcurrentHashMap<String, JedisCluster>();
	
	private BladeConfig(){}

	private static IConfig conf;

	public static IConfig getConf() {
		if(null == conf){
			throw new RuntimeException("BladeConfig未注入,请在applicationContext.xml中定义bladeConfig!");
		}
		return conf;
	}

//	public static Map<String, SQLManager> getSqlManagerPool(){
//		if(null == sqlManagerPool){
//			throw new RuntimeException("sqlManagerMap未注入,请在applicationContext.xml中定义sqlManagerMap!");
//		}
//		return sqlManagerPool;
//	}
	
	public static Map<String, JedisPool> getJedisPool(){
		return jedisPool;
	} 
	
	public static Map<String, JedisCluster> getJedisCluster(){
		return jedisCluster;
	} 
	
	/**
	 * 注入自定义config
	 * @param config
	 */
	public void setConf(IConfig config) {
		conf = config;
	}
	
	/**
	 * 注入sqlManagerMap
	 * @param map
	 */
//	public void setSqlManager(Map<String, SQLManager> map){
//		sqlManagerPool.putAll(map);
//	}
	
	/**
	 * 注入jedisPoolMap
	 * @param map
	 */
	public void setJedisPool(Map<String, JedisPool> map){
		jedisPool.putAll(map);
	}
	
	/**
	 * 注入jedisClusterMap
	 * @param map
	 */
	public void setJedisCluster(Map<String, JedisCluster> map){
		jedisCluster.putAll(map);
	}
	
}
