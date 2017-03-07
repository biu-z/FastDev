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
package com.biu.core.plugins.connection;

import com.biu.core.config.BladeConfig;
import com.biu.core.plugins.IPlugin;
import com.biu.core.toolbox.kit.LogKit;
import com.biu.core.toolbox.redis.IJedis;
import com.biu.core.toolbox.redis.IKeyNamingPolicy;
import com.biu.core.toolbox.redis.RedisCluster;
import com.biu.core.toolbox.redis.RedisSingle;
import com.biu.core.toolbox.redis.serializer.JdkSerializer;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis插件
 */
public class RedisPlugin implements IPlugin {
	private static Map<String, IJedis> redisCachePool = new ConcurrentHashMap<String, IJedis>();
	
	public String MASTER = "master";
	
	public Map<String, IJedis> getRedisCachePool(){
		return redisCachePool;
	}
	
	private RedisPlugin() { }
	
	private static RedisPlugin me = new RedisPlugin();
	
	public static RedisPlugin init(){
		return me;
	}
	
	public void start() {
		try {
			//注入redisSingle
			for(String key : BladeConfig.getJedisPool().keySet()){
				JedisPool jedisPool = BladeConfig.getJedisPool().get(key);
				//创建redis单机操作类
				RedisSingle rs = new RedisSingle(key, jedisPool, JdkSerializer.me, IKeyNamingPolicy.defaultKeyNamingPolicy);
				redisCachePool.put(key, rs);
			}
			//注入redisCluster
			for(String key : BladeConfig.getJedisCluster().keySet()){
				JedisCluster jedisCluster = BladeConfig.getJedisCluster().get(key);
				//创建redis集群操作类
				RedisCluster rc = new RedisCluster(key, jedisCluster, JdkSerializer.me, IKeyNamingPolicy.defaultKeyNamingPolicy);
				redisCachePool.put(key, rc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		for (IJedis jedis : redisCachePool.values()) {
			jedis.close();
		}
		redisCachePool.clear();
		LogKit.println("RedisPlugin关闭成功");
	}

}
