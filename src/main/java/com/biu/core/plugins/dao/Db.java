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

import com.biu.core.aop.AopContext;
import com.biu.core.meta.IQuery;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.grid.BladePage;

import java.util.List;
import java.util.Map;

/**
 * beetlsql Dao工具
 */
public class Db {

	private static DbManager dbManager = null;

	public static DbManager init() {
		return DbManager.init();
	}

	private Db() {}

	private static DbManager getDbManager() {
		if (null == dbManager) {
			synchronized (Db.class) {
				dbManager = DbManager.init();
			}
		}
		return dbManager;
	}

	/************   ↓↓↓   ********     通用     *********   ↓↓↓   ****************/

	/**
	 * 直接执行sql语句，sql语句已经是准备好的，采用preparedstatment执行
	 * @param clazz
	 * @param p
	 * @return 返回查询结果
	 */
	public static <T> List<T> execute(String p, Class<T> clazz){
		return getDbManager().execute(p, clazz);
	}

	public <T> List<T> execute(String sqlTemplate, Class<T> clazz, Object paras, int pageNum, int pageSize) {
		return getDbManager().execute(sqlTemplate,clazz,paras,pageNum,pageSize);
	}

	/** 直接执行sql语句，sql语句已经是准备好的，采用preparedstatment执行
	 * @param p
	 * @return 返回更新条数
	 */
	public static int executeUpdate(String p){
		return getDbManager().executeUpdate(p);
	}

	/**
	 * 根据sql新增数据
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	public static int insert(String sqlTemplate, Object paras){
		return getDbManager().insert(sqlTemplate, paras);
	}

	/**
	 * 根据sql修改数据
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	public static int update(String sqlTemplate, Object paras){
		return getDbManager().update(sqlTemplate, paras);
	}

	/**
	 * 根据sql删除数据
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	public static int delete(String sqlTemplate, Object paras){
		return getDbManager().delete(sqlTemplate, paras);
	}

	/**
	 * 获取一条数据
	 * @param sqlTemplate	sql语句
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map selectOne(String sqlTemplate){
		return getDbManager().selectOne(sqlTemplate);
	}

	/**
	 * 获取一条数据
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map selectOne(String sqlTemplate, Object paras){
		return getDbManager().selectOne(sqlTemplate, paras);
	}

	/**
	 * 获取一条数据
	 * @param sqlTemplate	sql语句
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map> selectList(String sqlTemplate){
		return getDbManager().selectList(sqlTemplate);
	}

	/**
	 * 获取多条数据
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map> selectList(String sqlTemplate, Object paras){
		return getDbManager().selectList(sqlTemplate, paras);
	}

	/**
	 * 根据表名、主键获取一条数据
	 * @param tableName	表名
	 * @param pkValue	主键值
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map findById(String tableName, String pkValue) {
		return getDbManager().findById(tableName, pkValue);
	}

	/**
	 * 根据表名、主键名、主键值获取一条数据
	 * @param tableName	表名
	 * @param pk		主键名
	 * @param pkValue	主键值
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map findById(String tableName, String pk, String pkValue) {
		return getDbManager().findById(tableName, pk, pkValue);
	}

	/**
	 * 获取Integer
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	public static Integer queryInt(String sqlTemplate, Object paras){
		return getDbManager().queryInt(sqlTemplate, paras);
	}

	/**
	 * 获取Integer集合
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	public static List<Integer> queryListInt(String sqlTemplate, Object paras){
		return getDbManager().queryListInt(sqlTemplate, paras);
	}

	/**
	 * 获取字符串
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	public static String queryStr(String sqlTemplate, Object paras){
		return getDbManager().queryStr(sqlTemplate, paras);
	}

	/**
	 * 获取字符串集合
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或map
	 * @return
	 */
	public static List<String> queryListStr(String sqlTemplate, Object paras){
		return getDbManager().queryListStr(sqlTemplate, paras);
	}

	/** 查询aop返回单条数据
	 * @param sqlTemplate
	 * @param param
	 * @param ac
	 * @param intercept
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map selectOne(String sqlTemplate, Map<String, Object> param, AopContext ac, IQuery intercept) {
		return getDbManager().selectOne(sqlTemplate, param, ac, intercept);
	}

	/**查询aop返回多条数据
	 * @param sqlTemplate
	 * @param param
	 * @param ac
	 * @param intercept
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map> selectList(String sqlTemplate, Map<String, Object> param, AopContext ac, IQuery intercept) {
		return getDbManager().selectList(sqlTemplate, param, ac, intercept);
	}

	/**
	 * 根据缓存找一条数据
	 * @param cacheName
	 * @param key
	 * @param sqlTemplate
	 * @param paras
	 * @return Map
	*/
	@SuppressWarnings("rawtypes")
	public static Map selectOneByCache(String cacheName, String key, String sqlTemplate, Object paras){
		return getDbManager().selectOneByCache(cacheName, key, sqlTemplate, paras);
	}

	/**
	 * 根据缓存找多条数据
	 * @param cacheName
	 * @param key
	 * @param sqlTemplate
	 * @param paras
	 * @return List<Map>
	*/
	@SuppressWarnings("rawtypes")
	public static List<Map> selectListByCache(String cacheName, String key, String sqlTemplate, Object paras){
		return getDbManager().selectListByCache(cacheName, key, sqlTemplate, paras);
	}

	/************   ↑↑↑   ********     通用     *********   ↑↑↑   ****************/

	/**
	 * 新增一条数据
	 * @param tableName	表名
	 * @param pk		主键名
	 * @param paras		参数
	 * @return
	 */
	public static int save(String tableName, String pk, CMap paras) {
		return getDbManager().save(tableName, pk, paras);
	}

	/**
	 * 修改一条数据
	 * @param tableName	表名
	 * @param pk		主键名
	 * @param paras		参数
	 * @return
	 */
	public static int update(String tableName, String pk, CMap paras) {
		return getDbManager().update(tableName, pk, paras);
	}

	/**
	 * 根据表名、字段名、值删除数据
	 * @param table	表名
	 * @param col	字段名
	 * @param ids	字段值集合(1,2,3)
	 * @return
	 */
	public static int deleteByIds(String table, String col, String ids) {
		return getDbManager().deleteByIds(table, col, ids);
	}

	/**
	 * 根据表名、字段名、值删除数据
	 * @param table	表名
	 * @param col	字段名
	 * @param ids	字段值集合(1,2,3)
	 * @return
	 */
	public static int deleteByStrIds(String table, String col, String ids) {
		return getDbManager().deleteByStrIds(table, col, ids);
	}

	/**
	 * 获取list
	 * @param sqlTemplate sql语句
	 * @param clazz	返回类型
	 * @param paras	参数
	 * @param pageNum	页号
	 * @param pageSize	数量
	 * @return
	 */
	public static <T> List<T> getList(String sqlTemplate, Class<T> clazz, Object paras, int pageNum, int pageSize) {
		return getDbManager().getList(sqlTemplate, clazz, paras, pageNum, pageSize);
	}

	/**
	 * 分页
	 * @param sqlTemplate sql语句
	 * @param paras	参数
	 * @param pageNum	页号
	 * @param pageSize	数量
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static BladePage<Map> paginate(String sqlTemplate, Object paras, int pageNum, int pageSize){
		return getDbManager().paginate(sqlTemplate, paras, pageNum, pageSize);
	}

	/**
	 * 分页
	 * @param sqlTemplate sql语句
	 * @param clazz	返回类型
	 * @param paras	参数
	 * @param pageNum	页号
	 * @param pageSize	数量
	 * @return
	 */
	public static <T> BladePage<T> paginate(String sqlTemplate, Class<T> clazz, Object paras, int pageNum, int pageSize){
		return getDbManager().paginate(sqlTemplate, clazz, paras, pageNum, pageSize);
	}

	/**
	 * 分页
	 * @param sqlTemplate sql语句
	 * @param sqlCount count语句
	 * @param paras	参数
	 * @param pageNum	页号
	 * @param pageSize	数量
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static BladePage<Map> paginate(String sqlTemplate, String sqlCount, Object paras, int pageNum, int pageSize){
		return getDbManager().paginate(sqlTemplate, sqlCount, paras, pageNum, pageSize);
	}

	/**
	 * 分页
	 * @param sqlTemplate sql语句
	 * @param sqlCount count语句
	 * @param clazz	返回类型
	 * @param paras	参数
	 * @param pageNum	页号
	 * @param pageSize	数量
	 * @return
	 */
	public static <T> BladePage<T> paginate(String sqlTemplate, String sqlCount, Class<T> clazz, Object paras, int pageNum, int pageSize){
		return getDbManager().paginate(sqlTemplate, sqlCount, clazz, paras, pageNum, pageSize);
	}

	/**
	 * 是否存在
	 *
	 * @param sqlTemplate
	 * @param paras
	 * @return
	 */
	public static boolean isExist(String sqlTemplate, Object paras) {
		return getDbManager().isExist(sqlTemplate, paras);
	}
}
