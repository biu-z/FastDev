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

import com.biu.core.toolbox.redis.serializer.ISerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.Map.Entry;

/**
 * Redis单机工具类
 * Redis 命令参考: http://redisdoc.com/
 */
public class RedisSingle implements IJedis {
	
	protected String name;
	protected JedisPool jedisPool;
	protected ISerializer serializer;
	protected IKeyNamingPolicy keyNamingPolicy;
	
	protected final ThreadLocal<Jedis> threadLocalJedis = new ThreadLocal<Jedis>();
	
	protected RedisSingle() {
		
	}
	
	public RedisSingle(String name, JedisPool jedisPool, ISerializer serializer, IKeyNamingPolicy keyNamingPolicy) {
		this.name = name;
		this.jedisPool = jedisPool;
		this.serializer = serializer;
		this.keyNamingPolicy = keyNamingPolicy;
	}

	public String set(Object key, Object value) {
		Jedis jedis = getJedis();
		try {
			return jedis.set(keyToBytes(key), valueToBytes(value));
		}
		finally {close(jedis);}
	}

	public String setex(Object key, int seconds, Object value) {
		Jedis jedis = getJedis();
		try {
			return jedis.setex(keyToBytes(key), seconds, valueToBytes(value));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object key) {
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.get(keyToBytes(key)));
		}
		finally {close(jedis);}
	}

	public Long del(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.del(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long del(Object... keys) {
		Jedis jedis = getJedis();
		try {
			return jedis.del(keysToBytesArray(keys));
		}
		finally {close(jedis);}
	}

	public Set<String> keys(String pattern) {
		Jedis jedis = getJedis();
		try {
			return jedis.keys(pattern);
		}
		finally {close(jedis);}
	}

	public Set<byte[]> keys(byte[] pattern) {
		Jedis jedis = getJedis();
		try {
			return jedis.keys(pattern);
		}
		finally {close(jedis);}
	}

	public String mset(Object... keysValues) {
		if (keysValues.length % 2 != 0)
			throw new IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd");
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
		try {
			byte[][] keysBytesArray = keysToBytesArray(keys);
			List<byte[]> data = jedis.mget(keysBytesArray);
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	public Long decr(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.decr(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long decrBy(Object key, long longValue) {
		Jedis jedis = getJedis();
		try {
			return jedis.decrBy(keyToBytes(key), longValue);
		}
		finally {close(jedis);}
	}

	public Long incr(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.incr(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long incrBy(Object key, long longValue) {
		Jedis jedis = getJedis();
		try {
			return jedis.incrBy(keyToBytes(key), longValue);
		}
		finally {close(jedis);}
	}

	public boolean exists(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.exists(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public String randomKey() {
		/*Jedis jedis = getJedis();
		try {
			return jedis.randomKey();
		}
		finally {close(jedis);}*/
		return null;
	}

	public String rename(Object oldkey, Object newkey) {
		Jedis jedis = getJedis();
		try {
			return jedis.rename(keyToBytes(oldkey), keyToBytes(newkey));
		}
		finally {close(jedis);}
	}

	public Long move(Object key, int dbIndex) {
		/*Jedis jedis = getJedis();
		try {
			return jedis.move(keyToBytes(key), dbIndex);
		}
		finally {close(jedis);}*/
		return null;
	}

	public String migrate(String host, int port, Object key, int destinationDb, int timeout) {
		/*Jedis jedis = getJedis();
		try {
			return jedis.migrate(valueToBytes(host), port, keyToBytes(key), destinationDb, timeout);
		}
		finally {close(jedis);}*/
		return null;
	}

	public String select(int databaseIndex) {
		Jedis jedis = getJedis();
		try {
			return jedis.select(databaseIndex);
		}
		finally {close(jedis);}
	}

	public Long expire(Object key, int seconds) {
		Jedis jedis = getJedis();
		try {
			return jedis.expire(keyToBytes(key), seconds);
		}
		finally {close(jedis);}
	}

	public Long expireAt(Object key, long unixTime) {
		Jedis jedis = getJedis();
		try {
			return jedis.expireAt(keyToBytes(key), unixTime);
		}
		finally {close(jedis);}
	}

	public Long pexpire(Object key, long milliseconds) {
		Jedis jedis = getJedis();
		try {
			return jedis.pexpire(keyToBytes(key), milliseconds);
		}
		finally {close(jedis);}
	}

	public Long pexpireAt(Object key, long millisecondsTimestamp) {
		Jedis jedis = getJedis();
		try {
			return jedis.pexpireAt(keyToBytes(key), millisecondsTimestamp);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T getSet(Object key, Object value) {
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.getSet(keyToBytes(key), valueToBytes(value)));
		}
		finally {close(jedis);}
	}

	public Long persist(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.persist(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public String type(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.type(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long ttl(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.ttl(keyToBytes(key));
		}
		finally {close(jedis);}
	}
	
	public Long pttl(Object key) {
		/*Jedis jedis = getJedis();
		try {
			return jedis.pttl(keyToBytes(key));
		}
		finally {close(jedis);}*/
		return null;
	}

	public Long objectRefcount(Object key) {
		/*Jedis jedis = getJedis();
		try {
			return jedis.objectRefcount(keyToBytes(key));
		}
		finally {close(jedis);}*/
		return null;
	}

	public Long objectIdletime(Object key) {
		/*Jedis jedis = getJedis();
		try {
			return jedis.objectIdletime(keyToBytes(key));
		}
		finally {close(jedis);}*/
		return null;
	}

	public Long hset(Object key, Object field, Object value) {
		Jedis jedis = getJedis();
		try {
			return jedis.hset(keyToBytes(key), fieldToBytes(field), valueToBytes(value));
		}
		finally {close(jedis);}
	}

	public String hmset(Object key, Map<Object, Object> hash) {
		Jedis jedis = getJedis();
		try {
			Map<byte[], byte[]> para = new HashMap<byte[], byte[]>();
			for (Entry<Object, Object> e : hash.entrySet())
				para.put(fieldToBytes(e.getKey()), valueToBytes(e.getValue()));
			return jedis.hmset(keyToBytes(key), para);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T hget(Object key, Object field) {
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.hget(keyToBytes(key), fieldToBytes(field)));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List hmget(Object key, Object... fields) {
		Jedis jedis = getJedis();
		try {
			List<byte[]> data = jedis.hmget(keyToBytes(key), fieldsToBytesArray(fields));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	public Long hdel(Object key, Object... fields) {
		Jedis jedis = getJedis();
		try {
			return jedis.hdel(keyToBytes(key), fieldsToBytesArray(fields));
		}
		finally {close(jedis);}
	}

	public boolean hexists(Object key, Object field) {
		Jedis jedis = getJedis();
		try {
			return jedis.hexists(keyToBytes(key), fieldToBytes(field));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Map hgetAll(Object key) {
		Jedis jedis = getJedis();
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
		/*Jedis jedis = getJedis();
		try {
			List<byte[]> data = jedis.hvals(keyToBytes(key));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}*/
		return null;
	}

	public Set<Object> hkeys(Object key) {
		Jedis jedis = getJedis();
		try {
			Set<byte[]> fieldSet = jedis.hkeys(keyToBytes(key));
			Set<Object> result = new HashSet<Object>();
			fieldSetFromBytesSet(fieldSet, result);
			return result;
		}
		finally {close(jedis);}
	}
	
	public Long hlen(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.hlen(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long hincrBy(Object key, Object field, long value) {
		Jedis jedis = getJedis();
		try {
			return jedis.hincrBy(keyToBytes(key), fieldToBytes(field), value);
		}
		finally {close(jedis);}
	}

	public Double hincrByFloat(Object key, Object field, double value) {
		Jedis jedis = getJedis();
		try {
			return jedis.hincrByFloat(keyToBytes(key), fieldToBytes(field), value);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T lindex(Object key, long index) {
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.lindex(keyToBytes(key), index));
		}
		finally {close(jedis);}
	}

	public Long getCounter(Object key) {
		Jedis jedis = getJedis();
		try {
			return Long.parseLong((String)jedis.get(keyNamingPolicy.getKeyName(key)));
		}
		finally {close(jedis);}
	}

	public Long llen(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.llen(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T lpop(Object key) {
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.lpop(keyToBytes(key)));
		}
		finally {close(jedis);}
	}

	public Long lpush(Object key, Object... values) {
		Jedis jedis = getJedis();
		try {
			return jedis.lpush(keyToBytes(key), valuesToBytesArray(values));
		}
		finally {close(jedis);}
	}

	public String lset(Object key, long index, Object value) {
		Jedis jedis = getJedis();
		try {
			return jedis.lset(keyToBytes(key), index, valueToBytes(value));
		}
		finally {close(jedis);}
	}

	public Long lrem(Object key, long count, Object value) {
		Jedis jedis = getJedis();
		try {
			return jedis.lrem(keyToBytes(key), count, valueToBytes(value));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List lrange(Object key, long start, long end) {
		Jedis jedis = getJedis();
		try {
			List<byte[]> data = jedis.lrange(keyToBytes(key), start, end);
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	public String ltrim(Object key, long start, long end) {
		Jedis jedis = getJedis();
		try {
			return jedis.ltrim(keyToBytes(key), start, end);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T rpop(Object key) {
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.rpop(keyToBytes(key)));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T rpoplpush(Object srcKey, Object dstKey) {
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.rpoplpush(keyToBytes(srcKey), keyToBytes(dstKey)));
		}
		finally {close(jedis);}
	}

	public Long rpush(Object key, Object... values) {
		Jedis jedis = getJedis();
		try {
			return jedis.rpush(keyToBytes(key), valuesToBytesArray(values));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List blpop(Object... keys) {
		/*Jedis jedis = getJedis();
		try {
			List<byte[]> data = jedis.blpop(keysToBytesArray(keys));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}*/
		return null;
	}

	@SuppressWarnings("rawtypes")
	public List blpop(int timeout, Object... keys) {
		Jedis jedis = getJedis();
		try {
			List<byte[]> data = jedis.blpop(timeout, keysToBytesArray(keys));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List brpop(Object... keys) {
		/*Jedis jedis = getJedis();
		try {
			List<byte[]> data = jedis.brpop(keysToBytesArray(keys));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}*/
		return null;
	}

	@SuppressWarnings("rawtypes")
	public List brpop(int timeout, Object... keys) {
		Jedis jedis = getJedis();
		try {
			List<byte[]> data = jedis.brpop(timeout, keysToBytesArray(keys));
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	public String ping() {
		Jedis jedis = getJedis();
		try {
			return jedis.ping();
		}
		finally {close(jedis);}
	}

	public Long sadd(Object key, Object... members) {
		Jedis jedis = getJedis();
		try {
			return jedis.sadd(keyToBytes(key), valuesToBytesArray(members));
		}
		finally {close(jedis);}
	}

	public Long scard(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.scard(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("unchecked")
	public <T> T spop(Object key) {
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.spop(keyToBytes(key)));
		}
		finally {close(jedis);}
	}
	
	@SuppressWarnings("rawtypes")
	public Set smembers(Object key) {
		Jedis jedis = getJedis();
		try {
			Set<byte[]> data = jedis.smembers(keyToBytes(key));
			Set<Object> result = new HashSet<Object>();
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	public boolean sismember(Object key, Object member) {
		Jedis jedis = getJedis();
		try {
			return jedis.sismember(keyToBytes(key), valueToBytes(member));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set sinter(Object... keys) {
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
		try {
			return (T)valueFromBytes(jedis.srandmember(keyToBytes(key)));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public List srandmember(Object key, int count) {
		Jedis jedis = getJedis();
		try {
			List<byte[]> data = jedis.srandmember(keyToBytes(key), count);
			return valueListFromBytesList(data);
		}
		finally {close(jedis);}
	}

	public Long srem(Object key, Object... members) {
		Jedis jedis = getJedis();
		try {
			return jedis.srem(keyToBytes(key), valuesToBytesArray(members));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set sunion(Object... keys) {
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
		try {
			Set<byte[]> data = jedis.sdiff(keysToBytesArray(keys));
			Set<Object> result = new HashSet<Object>();
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	public Long zadd(Object key, double score, Object member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zadd(keyToBytes(key), score, valueToBytes(member));
		}
		finally {close(jedis);}
	}
	
	public Long zadd(Object key, Map<Object, Double> scoreMembers) {
		Jedis jedis = getJedis();
		try {
			Map<byte[], Double> para = new HashMap<byte[], Double>();
			for (Entry<Object, Double> e : scoreMembers.entrySet())
				para.put(valueToBytes(e.getKey()), e.getValue());	// valueToBytes is important
			return jedis.zadd(keyToBytes(key), para);
		}
		finally {close(jedis);}
	}

	public Long zcard(Object key) {
		Jedis jedis = getJedis();
		try {
			return jedis.zcard(keyToBytes(key));
		}
		finally {close(jedis);}
	}

	public Long zcount(Object key, double min, double max) {
		Jedis jedis = getJedis();
		try {
			return jedis.zcount(keyToBytes(key), min, max);
		}
		finally {close(jedis);}
	}

	public Double zincrby(Object key, double score, Object member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zincrby(keyToBytes(key), score, valueToBytes(member));
		}
		finally {close(jedis);}
	}

	@SuppressWarnings("rawtypes")
	public Set zrange(Object key, long start, long end) {
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
		try {
			Set<byte[]> data = jedis.zrangeByScore(keyToBytes(key), min, max);
			Set<Object> result = new LinkedHashSet<Object>();	// 有序集合必须 LinkedHashSet
			valueSetFromBytesSet(data, result);
			return result;
		}
		finally {close(jedis);}
	}

	public Long zrank(Object key, Object member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zrank(keyToBytes(key), valueToBytes(member));
		}
		finally {close(jedis);}
	}

	public Long zrevrank(Object key, Object member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zrevrank(keyToBytes(key), valueToBytes(member));
		}
		finally {close(jedis);}
	}

	public Long zrem(Object key, Object... members) {
		Jedis jedis = getJedis();
		try {
			return jedis.zrem(keyToBytes(key), valuesToBytesArray(members));
		}
		finally {close(jedis);}
	}

	public Double zscore(Object key, Object member) {
		Jedis jedis = getJedis();
		try {
			return jedis.zscore(keyToBytes(key), valueToBytes(member));
		}
		finally {close(jedis);}
	}

	public String flushDB() {
		Jedis jedis = getJedis();
		try {
			return jedis.flushDB();
		}
		finally {close(jedis);}
	}

	public Long getDB() {
		Jedis jedis = getJedis();
		try {
			return jedis.getDB();
		}
		finally {close(jedis);}
	}

	public Long dbSize() {
		Jedis jedis = getJedis();
		try {
			return jedis.dbSize();
		}
		finally {close(jedis);}
	}
	
	public void close() {
		jedisPool.close();
		jedisPool.destroy();
	}

	public <T> T call(ICallBack call) {
		Jedis jedis = getJedis();
		T val = call.call(jedis);
		close(jedis);
		return val;
	}

	public void setKeyNamingPolicy(IKeyNamingPolicy keyNamingPolicy) {
		this.keyNamingPolicy = keyNamingPolicy;	
	}
	
	// ---------
	
	protected byte[] keyToBytes(Object key) {
		if (key instanceof byte[]) {
			return (byte[]) key;
		}
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
	
	public Jedis getJedis() {
		Jedis jedis = getThreadLocalJedis();
		if (null == jedis) 
			jedis = jedisPool.getResource(); setThreadLocalJedis(jedis);
		return jedis;
	}
	
	public void close(Jedis jedis) {
		if (jedis != null)
			jedis.close(); removeThreadLocalJedis();
	}
	
	public Jedis getThreadLocalJedis() {
		return threadLocalJedis.get();
	}
	
	public void setThreadLocalJedis(Jedis jedis) {
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






