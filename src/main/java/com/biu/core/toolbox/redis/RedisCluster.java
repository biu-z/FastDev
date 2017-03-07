/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
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

package com.biu.core.toolbox.redis;

import com.biu.core.toolbox.kit.LogKit;
import com.biu.core.toolbox.redis.serializer.ISerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Redis集群工具类
 * Redis 命令参考: http://redisdoc.com/
 */
public class RedisCluster implements IJedis {
	
	protected String name;
	protected JedisCluster jedisCluster;
	protected ISerializer serializer;
	protected IKeyNamingPolicy keyNamingPolicy;
	
	protected final ThreadLocal<JedisCluster> threadLocalJedis = new ThreadLocal<JedisCluster>();
	
	protected RedisCluster() {
		
	}
	
	public RedisCluster(String name, JedisCluster jedisCluster, ISerializer serializer, IKeyNamingPolicy keyNamingPolicy) {
		this.name = name;
		this.jedisCluster = jedisCluster;
		this.serializer = serializer;
		this.keyNamingPolicy = keyNamingPolicy;
	}
	
	public String set(Object key, Object value) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.set(keyToBytes(key), valueToBytes(value));
		}
		finally {close(jedis);}
	}
	
	public String setex(Object key, int seconds, Object value) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.setex(keyToBytes(key), seconds, valueToBytes(value));
		}
		finally {close(jedis);}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.get(keyToBytes(key)));
		}
		finally {close(jedis);}
	}
	
	public Long del(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.del(keyToBytes(key));
		}
		finally {close(jedis);}
	}
	
	public Long del(Object... keys) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.del(keysToBytesArray(keys));
		}
		finally {close(jedis);}
	}

	public Set<String> keys(String pattern) {
		Set<String> keys = new HashSet<>();
		JedisCluster jedis = getJedis();
		Map<String, JedisPool> clusterNodes = jedis.getClusterNodes();
		for (JedisPool jedisPool : clusterNodes.values()) {
			Jedis resource = jedisPool.getResource();
			Set<String> key = resource.keys(pattern);
			keys.addAll(key);
			resource.close();
		}
		return keys;
	}

	public Set<byte[]> keys(byte[] pattern) {
		Set<byte[]> keys = new HashSet<>();
		JedisCluster jedis = getJedis();
		Map<String, JedisPool> clusterNodes = jedis.getClusterNodes();
		for (JedisPool jedisPool : clusterNodes.values()) {
			Jedis resource = jedisPool.getResource();
			Set<byte[]> key = resource.keys(pattern);
			keys.addAll(key);
			resource.close();
		}
		return keys;
	}
	
	public String mset(Object... keysValues) {
		if (keysValues.length % 2 != 0)
			throw new IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd");
		JedisCluster jedis = getJedis();
		try {
			byte[][] kv = new byte[keysValues.length][];
			for (int i=0; i<keysValues.length; i++) {
				if (i % 2 == 0)
					kv[i] = keyToBytes(keysValues[i]);
				else
					kv[i] = valueToBytes(keysValues[i]);
			}
			return jedis.mset(kv);
		}
		finally {close(jedis);}
	}
	
	@SuppressWarnings("rawtypes")
	public List mget(Object... keys) {
		JedisCluster jedis = getJedis();
		try {
			byte[][] keysBytesArray = keysToBytesArray(keys);
			List<byte[]> data = jedis.mget(keysBytesArray);
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}
	
	public Long decr(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.decr(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long decrBy(Object key, long longValue) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.decrBy(keyToBytes(key), longValue);
		}
		finally {close(jedis);}
	}

	public Long incr(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.incr(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long incrBy(Object key, long longValue) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.incrBy(keyToBytes(key), longValue);
		}
		finally {close(jedis);}
	}

	public boolean exists(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.exists(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public String randomKey() {
		return null;
	}

	public String rename(Object oldkey, Object newkey) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.rename(keyToBytes(oldkey), keyToBytes(newkey));
		}
		finally {close(jedis);}
	}

	public Long move(Object key, int dbIndex) {
		return null;
	}

	public String migrate(String host, int port, Object key, int destinationDb, int timeout) {
		return null;
	}

	@SuppressWarnings("deprecation")
	public String select(int databaseIndex) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.select(databaseIndex);
		}
		finally {close(jedis);}
	}

	public Long expire(Object key, int seconds) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.expire(keyToBytes(key), seconds);
		}
		finally {close(jedis);}
	}
	
	public Long expireAt(Object key, long unixTime) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.expireAt(keyToBytes(key), unixTime);
		}
		finally {close(jedis);}
	}

	public Long pexpire(Object key, long milliseconds) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.pexpire(keyToBytes(key), milliseconds);
		}
		finally {close(jedis);}
	}

	public Long pexpireAt(Object key, long millisecondsTimestamp) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.pexpireAt(keyToBytes(key), millisecondsTimestamp);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T getSet(Object key, Object value) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.getSet(keyToBytes(key), valueToBytes(value)));
		}
		finally {close(jedis);}
	}

	public Long persist(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.persist(keyToBytes(key));
		}
		finally {close(jedis);}
	}


	public String type(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.type(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long ttl(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.ttl(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long pttl(Object key) {
		return null;
	}

	public Long objectRefcount(Object key) {
		return null;
	}

	public Long objectIdletime(Object key) {
		return null;
	}

	public Long hset(Object key, Object field, Object value) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.hset(keyToBytes(key), fieldToBytes(field), valueToBytes(value));
		}
		finally {close(jedis);}
	}

	public String hmset(Object key, Map<Object, Object> hash) {
		JedisCluster jedis = getJedis();
		try {
			Map<byte[], byte[]> para = new HashMap<byte[], byte[]>();
			for (Entry<Object, Object> e : hash.entrySet())
				para.put(fieldToBytes(e.getKey()), valueToBytes(e.getValue()));
			return jedis.hmset(keyToBytes(key), para);
		}
		finally {close(jedis);}
	}
	
	/**
	 * 返回哈希表 key 中给定域 field 的值。
	 */
	@SuppressWarnings("unchecked")
	public <T> T hget(Object key, Object field) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.hget(keyToBytes(key), fieldToBytes(field)));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List hmget(Object key, Object... fields) {
		JedisCluster jedis = getJedis();
		try {
			List<byte[]> data = jedis.hmget(keyToBytes(key), fieldsToBytesArray(fields));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	public Long hdel(Object key, Object... fields) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.hdel(keyToBytes(key), fieldsToBytesArray(fields));
		}
		finally {close(jedis);}
	}

	public boolean hexists(Object key, Object field) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.hexists(keyToBytes(key), fieldToBytes(field));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Map hgetAll(Object key) {
		JedisCluster jedis = getJedis();
		try {
			Map<byte[], byte[]> data = jedis.hgetAll(keyToBytes(key));
			Map<Object, Object> result = new HashMap<Object, Object>();
			for (Entry<byte[], byte[]> e : data.entrySet())
				result.put(fieldFromBytes(e.getKey()), valueFromBytes(e.getValue()));
			return result;
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List hvals(Object key) {
		return null;
	}

	public Set<Object> hkeys(Object key) {
		JedisCluster jedis = getJedis();
		try {
			Set<byte[]> fieldSet = jedis.hkeys(keyToBytes(key));
			Set<Object> result = new HashSet<Object>();
			fieldSetFromBytesSet(fieldSet, result);
			return result;
		}
		finally {close(jedis);}
	}

	public Long hlen(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.hlen(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long hincrBy(Object key, Object field, long value) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.hincrBy(keyToBytes(key), fieldToBytes(field), value);
		}
		finally {close(jedis);}
	}

	public Double hincrByFloat(Object key, Object field, double value) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.hincrByFloat(keyToBytes(key), fieldToBytes(field), value);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T lindex(Object key, long index) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.lindex(keyToBytes(key), index));
		}
		finally {close(jedis);}
	}

	public Long getCounter(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return Long.parseLong((String)jedis.get(keyNamingPolicy.getKeyName(key)));
		}
		finally {close(jedis);}
	}

	public Long llen(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.llen(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T lpop(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.lpop(keyToBytes(key)));
		}
		finally {close(jedis);}
	}

	public Long lpush(Object key, Object... values) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.lpush(keyToBytes(key), valuesToBytesArray(values));
		}
		finally {close(jedis);}
	}

	public String lset(Object key, long index, Object value) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.lset(keyToBytes(key), index, valueToBytes(value));
		}
		finally {close(jedis);}
	}

	public Long lrem(Object key, long count, Object value) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.lrem(keyToBytes(key), count, valueToBytes(value));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List lrange(Object key, long start, long end) {
		JedisCluster jedis = getJedis();
		try {
			List<byte[]> data = jedis.lrange(keyToBytes(key), start, end);
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	public String ltrim(Object key, long start, long end) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.ltrim(keyToBytes(key), start, end);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T rpop(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.rpop(keyToBytes(key)));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T rpoplpush(Object srcKey, Object dstKey) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.rpoplpush(keyToBytes(srcKey), keyToBytes(dstKey)));
		}
		finally {close(jedis);}
	}

	public Long rpush(Object key, Object... values) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.rpush(keyToBytes(key), valuesToBytesArray(values));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List blpop(Object... keys) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public List blpop(int timeout, Object... keys) {
		JedisCluster jedis = getJedis();
		try {
			List<byte[]> data = jedis.blpop(timeout, keysToBytesArray(keys));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List brpop(Object... keys) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public List brpop(int timeout, Object... keys) {
		JedisCluster jedis = getJedis();
		try {
			List<byte[]> data = jedis.brpop(timeout, keysToBytesArray(keys));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("deprecation")
	public String ping() {
		JedisCluster jedis = getJedis();
		try {
			return jedis.ping();
		}
		finally {close(jedis);}
	}

	public Long sadd(Object key, Object... members) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.sadd(keyToBytes(key), valuesToBytesArray(members));
		}
		finally {close(jedis);}
	}
	
	public Long scard(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.scard(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T spop(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.spop(keyToBytes(key)));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set smembers(Object key) {
		JedisCluster jedis = getJedis();
		try {
			Set<byte[]> data = jedis.smembers(keyToBytes(key));
			Set<Object> result = new HashSet<Object>();
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}
	
	public boolean sismember(Object key, Object member) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.sismember(keyToBytes(key), valueToBytes(member));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set sinter(Object... keys) {
		JedisCluster jedis = getJedis();
		try {
			Set<byte[]> data = jedis.sinter(keysToBytesArray(keys));
			Set<Object> result = new HashSet<Object>();
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T srandmember(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.srandmember(keyToBytes(key)));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List srandmember(Object key, int count) {
		JedisCluster jedis = getJedis();
		try {
			List<byte[]> data = jedis.srandmember(keyToBytes(key), count);
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	public Long srem(Object key, Object... members) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.srem(keyToBytes(key), valuesToBytesArray(members));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set sunion(Object... keys) {
		JedisCluster jedis = getJedis();
		try {
			Set<byte[]> data = jedis.sunion(keysToBytesArray(keys));
			Set<Object> result = new HashSet<Object>();
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set sdiff(Object... keys) {
		JedisCluster jedis = getJedis();
		try {
			Set<byte[]> data = jedis.sdiff(keysToBytesArray(keys));
			Set<Object> result = new HashSet<Object>();
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	public Long zadd(Object key, double score, Object member) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.zadd(keyToBytes(key), score, valueToBytes(member));
		}
		finally {close(jedis);}
	}
	
	public Long zadd(Object key, Map<Object, Double> scoreMembers) {
		JedisCluster jedis = getJedis();
		try {
			Map<byte[], Double> para = new HashMap<byte[], Double>();
			for (Entry<Object, Double> e : scoreMembers.entrySet())
				para.put(valueToBytes(e.getKey()), e.getValue());	// valueToBytes is important
			return jedis.zadd(keyToBytes(key), para);
		}
		finally {close(jedis);}
	}

	public Long zcard(Object key) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.zcard(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long zcount(Object key, double min, double max) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.zcount(keyToBytes(key), min, max);
		}
		finally {close(jedis);}
	}

	public Double zincrby(Object key, double score, Object member) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.zincrby(keyToBytes(key), score, valueToBytes(member));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set zrange(Object key, long start, long end) {
		JedisCluster jedis = getJedis();
		try {
			Set<byte[]> data = jedis.zrange(keyToBytes(key), start, end);
			Set<Object> result = new LinkedHashSet<Object>();	// 有序集合必须 LinkedHashSet
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set zrevrange(Object key, long start, long end) {
		JedisCluster jedis = getJedis();
		try {
			Set<byte[]> data = jedis.zrevrange(keyToBytes(key), start, end);
			Set<Object> result = new LinkedHashSet<Object>();	// 有序集合必须 LinkedHashSet
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set zrangeByScore(Object key, double min, double max) {
		JedisCluster jedis = getJedis();
		try {
			Set<byte[]> data = jedis.zrangeByScore(keyToBytes(key), min, max);
			Set<Object> result = new LinkedHashSet<Object>();	// 有序集合必须 LinkedHashSet
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	public Long zrank(Object key, Object member) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.zrank(keyToBytes(key), valueToBytes(member));
		}
		finally {close(jedis);}
	}

	public Long zrevrank(Object key, Object member) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.zrevrank(keyToBytes(key), valueToBytes(member));
		}
		finally {close(jedis);}
	}

	public Long zrem(Object key, Object... members) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.zrem(keyToBytes(key), valuesToBytesArray(members));
		}
		finally {close(jedis);}
	}

	public Double zscore(Object key, Object member) {
		JedisCluster jedis = getJedis();
		try {
			return jedis.zscore(keyToBytes(key), valueToBytes(member));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("deprecation")
	public String flushDB() {
		JedisCluster jedis = getJedis();
		try {
			return jedis.flushDB();
		}
		finally {close(jedis);}
	}	
	
	@SuppressWarnings("deprecation")
	public Long getDB() {
		JedisCluster jedis = getJedis();
		try {
			return jedis.getDB();
		}
		finally {close(jedis);}
	}


	@SuppressWarnings("deprecation")
	public Long dbSize() {
		JedisCluster jedis = getJedis();
		try {
			return jedis.dbSize();
		}
		finally {close(jedis);}
	}
	
	public void close() {
		try {
			jedisCluster.close();
		} catch (IOException e) {
			LogKit.error(e.getMessage(), e);
		}
	}

	public <T> T call(ICallBack call) {
		JedisCluster jedis = getJedis();
		T val = call.call(jedis);
		close(jedis);
		return val;
	}
	
	public void setKeyNamingPolicy(IKeyNamingPolicy keyNamingPolicy) {
		this.keyNamingPolicy = keyNamingPolicy;	
	}
	
	// ---------
	
	protected byte[] keyToBytes(Object key) {
		String keyStr = keyNamingPolicy.getKeyName(key);
		return serializer.keyToBytes(keyStr);
	}
	
	protected byte[][] keysToBytesArray(Object... keys) {
		byte[][] result = new byte[keys.length][];
		for (int i=0; i<result.length; i++)
			result[i] = keyToBytes(keys[i]);
		return result;
	}
	
	protected byte[] fieldToBytes(Object field) {
		return serializer.fieldToBytes(field);
	}
	
	protected Object fieldFromBytes(byte[] bytes) {
		return serializer.fieldFromBytes(bytes);
	}
	
	protected byte[][] fieldsToBytesArray(Object... fieldsArray) {
		byte[][] data = new byte[fieldsArray.length][];
		for (int i=0; i<data.length; i++)
			data[i] = fieldToBytes(fieldsArray[i]);
		return data;
	}
	
	protected void fieldSetFromBytesSet(Set<byte[]> data, Set<Object> result) {
		for (byte[] fieldBytes : data) {
			result.add(fieldFromBytes(fieldBytes));
		}
	}
	
	protected byte[] valueToBytes(Object value) {
		return serializer.valueToBytes(value);
	}
	
	protected Object valueFromBytes(byte[] bytes) {
		return serializer.valueFromBytes(bytes);
	}
	
	protected byte[][] valuesToBytesArray(Object... valuesArray) {
		byte[][] data = new byte[valuesArray.length][];
		for (int i=0; i<data.length; i++)
			data[i] = valueToBytes(valuesArray[i]);
		return data;
	}
	
	protected void valueSetFromBytesSet(Set<byte[]> data, Set<Object> result) {
		for (byte[] valueBytes : data) {
			result.add(valueFromBytes(valueBytes));
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected List valueListFromBytesList(List<byte[]> data) {
		List<Object> result = new ArrayList<Object>();
		for (byte[] d : data)
			result.add(valueFromBytes(d));
		return result;
	}
	
	// ---------
	
	public String getName() {
		return name;
	}
	
	public ISerializer getSerializer() {
		return serializer;
	}
	
	public IKeyNamingPolicy getKeyNamingPolicy() {
		return keyNamingPolicy;
	}
	
	// ---------
	
	public JedisCluster getJedis() {
		JedisCluster jedis = getThreadLocalJedis();
		if (null == jedis) 
			jedis = jedisCluster; setThreadLocalJedis(jedisCluster);
		return jedisCluster;
	}
	
	public void close(JedisCluster jedis) {
		if (jedis != null)
			removeThreadLocalJedis();
	}
	
	public JedisCluster getThreadLocalJedis() {
		return threadLocalJedis.get();
	}
	
	public void setThreadLocalJedis(JedisCluster jedis) {
		threadLocalJedis.set(jedis);
	}
	
	public void removeThreadLocalJedis() {
		try {
			threadLocalJedis.remove();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}






