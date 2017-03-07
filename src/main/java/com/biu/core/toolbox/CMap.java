package com.biu.core.toolbox;

import com.biu.core.toolbox.kit.BeanKit;
import com.biu.core.toolbox.kit.CollectionKit;
import com.biu.core.toolbox.support.BasicTypeGetter;
import com.biu.core.toolbox.support.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 扩充了HashMap中的方法
 *
 * @author Biu
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class CMap extends CaseInsensitiveHashMap<String, Object> implements BasicTypeGetter<String> {

	/**
	 * 创建CMap
	 * @return CMap
	 */
	public static CMap init() {
		return new CMap();
	}

	private CMap(){

	}

	/**
	 * 创建HashMap
	 *
	 * @return HashMap
	 */
	public static HashMap<String, Object> createHashMap() {
		return new HashMap<>();
	}

	/**
	 * 将PO对象转为Maps
	 * @param <T>
	 * @param bean Bean对象
	 * @return Vo
	 */
	public static <T> CMap parse(T bean) {
		return init().parseBean(bean);
	}

	/**
	 * 将map对象转为Maps
	 *
	 * @param <T>
	 *            值对象
	 * @return Vo
	 */
	public static <T> CMap parse(Map<String, Object> map) {
		return init().parseMap(map);
	}


	/**
	 * 转换为Bean对象
	 * @param <T>
	 * @param bean Bean
	 * @return Bean
	 */
	public <T> T toBean(T bean) {
		return toBean(bean, false);
	}

	/**
	 * 转换为Bean对象
	 * @param <T>
	 * @param bean Bean
	 * @param isToCamelCase 是否转换为驼峰模式
	 * @return Bean
	 */
	public <T> T toBean(T bean, boolean isToCamelCase) {
		BeanKit.fillBeanWithMap(this, bean, isToCamelCase);
		return bean;
	}

	/**
	 * 填充Value Object对象
	 * @param clazz Value Object（或者POJO）的类
	 * @return vo
	 */
	public <T> T toBean(Class<T> clazz) {
		return BeanKit.mapToBean(this, clazz);
	}

	/**
	 * 填充Value Object对象，忽略大小写
	 * @param clazz Value Object（或者POJO）的类
	 * @return vo
	 */
	public <T> T toBeanIgnoreCase(Class<T> clazz) {
		return BeanKit.mapToBeanIgnoreCase(this, clazz);
	}

	/**
	 * 将值对象转换为Maps<br>
	 * 类名会被当作表名，小写第一个字母
	 * @param <T>
	 * @param bean 值对象
	 * @return 自己
	 */
	public <T> CMap parseBean(T bean) {
		if (null != bean) {
			this.putAll(BeanKit.beanToMap(bean));
		}
		return this;
	}

	/**
	 * 将值对象转换为Maps<br>
	 * 类名会被当作表名，小写第一个字母
	 *
	 * @param <T>
	 * @param map
	 *            值对象
	 * @return 自己
	 */
	public <T> CMap parseMap(Map<String, Object> map) {
		if (null != map) {
			this.putAll(map);
		}
		return this;
	}

	/**
	 * 与给定实体对比并去除相同的部分<br>
	 * 此方法用于在更新操作时避免所有字段被更新，跳过不需要更新的字段
	 * version from 2.0.0
	 * @param withoutNames 不需要去除的字段名
	 */
	public <T extends CMap> void removeEqual(T paras, String... withoutNames) {
		HashSet<String> withoutSet = CollectionKit.newHashSet(withoutNames);
		for(Entry<String, Object> entry : paras.entrySet()) {
			if(withoutSet.contains(entry.getKey())) {
				continue;
			}

			final Object value = this.get(entry.getKey());
			if(null != value && value.equals(entry.getValue())) {
				this.remove(entry.getKey());
			}
		}
	}

	//-------------------------------------------------------------------- 特定类型值
	/**
	 * 设置列
	 * @param attr 属性
	 * @param value 值
	 * @return 本身
	 */
	public CMap set(String attr, Object value) {
		return this.put(attr, value);
	}


	/**
	 * 设置列，当键或值为null时忽略
	 * @param attr 属性
	 * @param value 值
	 * @return 本身
	 */
	public CMap setIgnoreNull(String attr, Object value) {
		if(null != attr && null != value) {
			set(attr, value);
		}
		return this;
	}

	/**
	 * 设置列
	 * @param attr 属性
	 * @param value 值
	 * @return 本身
	 */
	@Override
	public CMap put(String attr, Object value) {
		super.put(attr, value);
		return this;
	}

	/**
	 * 获得特定类型值
	 * @param attr 字段名
	 * @param defaultValue 默认值
	 * @return 字段值
	 */
	public <T> T get(String attr, T defaultValue) {
		final Object result = get(attr);
		return (T)(result != null ? result : defaultValue);
	}

	@Override
	public Object getObj(String key) {
		return super.get(key);
	}

	@Override
	public String getStr(String attr) {
		return Convert.toStr(get(attr), "");
	}

	@Override
	public Integer getInt(String attr) {
		return Convert.toInt(get(attr), null);
	}

	@Override
	public Long getLong(String attr) {
		return Convert.toLong(get(attr), null);
	}

	@Override
	public Float getFloat(String attr) {
		return Convert.toFloat(get(attr), null);
	}

	@Override
	public Short getShort(String attr) {
		return Convert.toShort(get(attr), null);
	}

	@Override
	public Character getChar(String attr) {
		return Convert.toChar(get(attr), null);
	}

	@Override
	public Double getDouble(String attr) {
		return Convert.toDouble(get(attr), null);
	}

	@Override
	public Byte getByte(String attr) {
		return Convert.toByte(get(attr), null);
	}

	@Override
	public Boolean getBool(String attr) {
		return Convert.toBool(get(attr), null);
	}

	@Override
	public BigDecimal getBigDecimal(String attr) {
		return Convert.toBigDecimal(get(attr));
	}

	@Override
	public BigInteger getBigInteger(String attr) {
		return Convert.toBigInteger(get(attr));
	}

	@Override
	public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) {
		return Convert.toEnum(clazz, get(key));
	}

	public byte[] getBytes(String attr) {
		return get(attr, null);
	}

	public Date getDate(String attr) {
		return get(attr, null);
	}

	public Time getTime(String attr) {
		return get(attr, null);
	}

	public Timestamp getTimestamp(String attr) {
		return get(attr, null);
	}

	public Number getNumber(String attr) {
		return get(attr, null);
	}


	//-------------------------------------------------------------------- 特定类型值

	@Override
	public CMap clone() {
		return (CMap) super.clone();
	}

	//-------------------------------------------------------------------- 特定类型值
}
