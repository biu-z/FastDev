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

import com.biu.common.mybatis.util.SqlMapper;
import com.biu.core.annotation.BindID;
import com.biu.core.toolbox.CMap;
import com.biu.core.toolbox.grid.BladePage;

import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * beetlsql 自动API封装dao工具
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class Blade {
	private static Map<Class<?>, Blade> pool = new ConcurrentHashMap<Class<?>, Blade>();
	private volatile SqlMapper sql = null;
	private Class<?> modelClass;
	private String table;
	private String pk;
	private String dbName;

	private static DbManager getSqlManager() {
		return Db.init();
	}

	/**
	 * 返回针对实体封装的dao
	 * @param modelClass 实体类
	 * @return
	 */
	public static Blade create(Class<?> modelClass) {
		Blade blade = pool.get(modelClass);
		if (null == blade) {
			synchronized (Blade.class) {
				blade = pool.get(modelClass);
				if (null == blade) {
					blade = new Blade(modelClass);
					pool.put(modelClass, blade);
				}
			}
		}
		return blade;
	}

	private Blade() {

	}

	private Blade(Class<?> modelClass) {
		if(modelClass != Blade.class){
			setTable(modelClass);
		}
	}

	private void setTable(Class<?> modelClass) {
		this.modelClass = modelClass;
		Table Table = this.modelClass.getAnnotation(Table.class);
		if (null != Table) {
			this.table = Table.name();
		} else {
			throw new RuntimeException("未给 " + this.modelClass.getName() + " 绑定表名!");
		}
		BindID BindID = this.modelClass.getAnnotation(BindID.class);
		if (null != BindID) {
			this.pk = BindID.name();
		} else {
			this.pk = "id";
		}
	}

	/**
	 * 获取map
	 * @param columns		字段名
	 * @return
	 */
	public Map findOneColBy(String columns){
		List<Map> list = getSqlManager().execute(getSelectSql(columns) + getFromSql(), Map.class, CMap.init(), 1, 1);
		if(list.size() == 0){
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 获取map
	 * @param columns		字段名
	 * @param where			条件
	 * @param paras	实体类或map
	 * @return
	 */
	public Map findOneColBy(String columns, String where, Object paras){
		List<Map> list = getSqlManager().execute(getSelectSql(columns) + getFromSql() + getWhere(where), Map.class, paras, 1, 1);
		if (list.size() == 0){
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 获取map集合
	 * @param columns		字段名
	 * @return
	 */
	public List<Map> findColBy(String columns){
		List<Map> list = getSqlManager().execute(getSelectSql(columns) + getFromSql(), Map.class, CMap.init());
		return list;
	}

	/**
	 * 获取map集合
	 * @param columns		字段名
	 * @param where			条件
	 * @param paras	实体类或map
	 * @return
	 */
	public List<Map> findColBy(String columns, String where, Object paras){
		List<Map> list = getSqlManager().execute(getSelectSql(columns) + getFromSql() + getWhere(where), Map.class, paras);
		return list;
	}

	/**
	 * 根据主键查询一条数据
	 * @param id
	 * @return
	 */
	public <T> T findById(Object id) {
		try{
			return (T)getSqlManager().getOne(getUnique(id),CMap.init().set("id",id),this.modelClass);
		} catch (Exception ex){
			return null;
		}
	}

	/**
	 * 根据sql查询多条数据
	 * @param sqlTemplate sql语句
	 * @param paras  实体类或者Map(查询条件)
	 * @return
	 */
	public <T> List<T> find(String sqlTemplate, Object paras) {
		List<T> list = (List<T>) getSqlManager().execute(sqlTemplate, this.modelClass, paras);
		return list;
	}

	/**
	 * 查询第一条数据
	 * @param model
	 * @return
	 */
//	public <T> T findTopOne(Object model) {
//		List<T> list = (List<T>) getSqlManager().template(model, 1, 1);
//		if(list.size() == 0){
//			return null;
//		}
//		return list.get(0);
//	}
//
//	/**
//	 * 查询前几条数据
//	 * @param topNum
//	 * @param model
//	 * @return
//	 */
//	public <T> List<T> findTop(int topNum, Object model) {
//		List<T> list = (List<T>) getSqlManager().template(model, 1, topNum);
//		return list;
//	}

	/**
	 * 查询前几条数据
	 * @param topNum
	 * @param sqlTemplate
	 * @return
	 */
	public <T> List<T> findTop(int topNum, String sqlTemplate) {
		List<T> list = (List<T>) getSqlManager().execute(sqlTemplate, this.modelClass, CMap.init(), 1, topNum);
		return list;
	}

	/**
	 * 查询前几条数据
	 * @param topNum
	 * @param sqlTemplate
	 * @param paras
	 * @return
	 */
	public <T> List<T> findTop(int topNum, String sqlTemplate, Object paras) {
		List<T> list = (List<T>) getSqlManager().execute(sqlTemplate, this.modelClass, paras, 1, topNum);
		return list;
	}

	/**
	 * 查询所有数据
	 * @return
	 */
	public <T> List<T> findAll() {
		List<T> all = (List<T>) getSqlManager().execute(getSelectSql() + getFromSql(), this.modelClass);;
		return all;
	}

	/**
	 * 根据条件查询数据
	 * @param where			sql条件
	 * @param paras	实体类或者Map(查询条件)
	 * @return
	 */
	public <T> List<T> findBy(String where, Object paras) {
		List<T> models = (List<T>) getSqlManager().execute(getSelectSql() + getFromSql() + getWhere(where), this.modelClass, paras);
		return models;
	}

	/**
	 * 根据条件查询数据,指定返回字段
	 * @param columns		字段
	 * @param where			sql条件
	 * @param paras	实体类或者Map(查询条件)
	 * @return
	 */
	public <T> List<T> findBy(String columns, String where, Object paras) {
		List<T> models = (List<T>) getSqlManager().execute(getSelectSql(columns) + getFromSql() + getWhere(where), this.modelClass, paras);
		return models;
	}

	/**
	 * 根据实体类查询与之符合的数据
	 * @param model 实体类
	 * @return
	 */
//	public <T> List<T> findByTemplate(Object model) {
//		return (List<T>) getSqlManager().template(model);
//	}

	/**
	 * 查询第一条数据
	 * @param sqlTemplate	sql语句
	 * @param paras	实体类或者Map(查询条件)
	 * @return
	 */
	public <T> T findFirst(String sqlTemplate, Object paras) {
		List<T> list = this.findTop(1, sqlTemplate, paras);
		if (list.size() == 0){
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 根据条件查询第一条数据
	 * @param where		 sql条件
	 * @param paras 实体类或者Map(查询条件)
	 * @return
	 */
	public <T> T findFirstBy(String where, Object paras) {
		List<T> list = this.findTop(1, getSelectSql() + getFromSql() + getWhere(where), paras);
		if (list.size() == 0){
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 更新条件修改数据
	 * @param set		 set条件
	 * @param where		 where条件
	 * @param paras 实体类或者Map(查询条件)
	 * @return
	 */
	public boolean updateBy(String set, String where, Object paras) {
		int n = getSqlManager().executeUpdate(getUpdateSql() + getSet(set) + getWhere(where), paras);
		return n > 0;
	}

	/**
	 * 根据id删除数据
	 * @param id 主键值
	 * @return
	 */
	public int delete(Object id) {
		int cnt = getSqlManager().delete(getDeleteSql(),CMap.init().set("id",id));
		return cnt;
	}

	/**
	 * 根据sql语句删除数据
	 * @param sqlTemplate sql语句
	 * @return
	 */
	public int deleteBy(String sqlTemplate) {
		int result = getSqlManager().executeUpdate(sqlTemplate, null);
		return result;
	}

	/**
	 * 根据条件删除数据
	 * @param where where条件
	 * @param paras 实体类或者Map(查询条件)
	 * @return
	 */
	public int deleteBy(String where, Object paras) {
		int result = getSqlManager().executeUpdate(getDeleteSql(where), paras);
		return result;
	}

	/**
	 * 根据多个id集合删除数据
	 * @param ids id集合(1,2,3)
	 * @return
	 */
	public int deleteByIds(String ids) {
		String sqlTemplate = getDeleteByIdsSql(ids);
		int result = getSqlManager().executeUpdate(sqlTemplate);
		return result;
	}

	/**
	 * 查询sql语句查询结果的总数
	 * @param sqlTemplate sql语句
	 * @param paras  实体类或者Map(查询条件)
	 * @return
	 */
	public int countBy(String sqlTemplate, Object paras) {
		int n = getSqlManager().execute(sqlTemplate, this.modelClass, paras).size();
		return n;
	}

	/**
	 * 根据where条件查询总数
	 * @param where		 where条件
	 * @param paras 实体类或者Map(查询条件)
	 * @return
	 */
	public int count(String where, Object paras) {
		int n = getSqlManager().execute(getCountSql() + getWhere(where), this.modelClass, paras).size();
		return n;
	}

	/**
	 * 获取list
	 * @param start	页号
	 * @param size	每页数量
	 * @return
	 */
	public <T> List<T> getList(int start, int size) {
		List<T> all = (List<T>) getSqlManager().paginate(getSelectSql()+getFromSql(),null, (start - 1) * size + 1, size);
		return all;
	}

	/**
	 * 获取list
	 * @param sqlTemplate sql语句
	 * @param paras	参数
	 * @param start	页号
	 * @param size	数量
	 * @return
	 */
	public <T> List<T> getList(String sqlTemplate, Object paras, int start, int size) {
		List<T> all = (List<T>) getSqlManager().execute(sqlTemplate, this.modelClass, paras, (start - 1) * size + 1, size);
		return all;
	}


	/**
	 * 获取list
	 * @param sqlTemplate sql语句
	 * @param clazz	返回类型
	 * @param paras	参数
	 * @param start	页号
	 * @param size	数量
	 * @return
	 */
	public <T> List<T> getList(String sqlTemplate, Class<?> clazz, Object paras, int start, int size) {
		List<T> all = (List<T>) getSqlManager().execute(sqlTemplate, clazz, paras, (start - 1) * size + 1, size);
		return all;
	}

	/**
	 * 分页
	 * @param sqlTemplate sql语句
	 * @param paras	参数
	 * @param start	页号
	 * @param size	数量
	 * @return
	 */
	public <T> BladePage<T> paginate(String sqlTemplate, Object paras, int start, int size){
		List<T> rows = getList(sqlTemplate, paras, start, size);
		long count = Db.init().queryInt(getCountSql(sqlTemplate), paras).longValue();
		BladePage<T> page = new BladePage<>(rows, start, size, count);
		return page;
	}

	/**
	 * 分页
	 * @param sqlTemplate sql语句
	 * @param clazz	返回类型
	 * @param paras	参数
	 * @param start	页号
	 * @param size	数量
	 * @return
	 */
	public <T> BladePage<T> paginate(String sqlTemplate, Class<?> clazz, Object paras, int start, int size){
		List<T> rows = getList(sqlTemplate, clazz, paras, start, size);
		long count = Db.init().queryInt(getCountSql(sqlTemplate), paras).longValue();
		BladePage<T> page = new BladePage<>(rows, start, size, count);
		return page;
	}


	/**
	 * 是否存在
	 * @param sqlTemplate
	 * @param paras
	 * @return
	 */
	public boolean isExist(String sqlTemplate, Object paras) {
		int count = getSqlManager().execute(sqlTemplate, this.modelClass, paras).size();
		if (count != 0) {
			return true;
		}
		return false;
	}

//	/**
//	 * 获取model的主键值
//	 * @param model
//	 * @return Object
//	*/
//	public Object getIdValue(Object model){
//		SQLManager sql = getSqlManager();
//		String table = sql.getNc().getTableName(this.modelClass);
//		ClassDesc desc = sql.getMetaDataManager().getTable(table).getClassDesc(this.modelClass, sql.getNc());
//		Method getterMethod = (Method) desc.getIdMethods().get(desc.getIdCols().get(0));
//		Object idValue = null;
//		try {
//			idValue = getterMethod.invoke(model);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return idValue;
//	}

	/*************************************************************************************************/

	private String getUnique(Object id){
		return "SELECT * FROM " + this.table + " WHERE " + this.pk + " = #{id}";
	}

	private String getSet(String set) {
		if (set != null && !set.isEmpty() && !set.trim().toUpperCase().startsWith("SET")) {
			set = " SET " + set + " ";
		}
		return set;
	}

	private String getWhere(String where) {
		if (where != null && !where.isEmpty() && !where.trim().toUpperCase().startsWith("WHERE")) {
			where = " WHERE " + where + " ";
		}
		return where;
	}

	private String getSelectSql() {
		return " SELECT * ";
	}

	private String getSelectSql(String columns) {
		return " SELECT " + columns + " ";
	}

	private String getFromSql() {
		return " FROM " + this.table + " ";
	}

	private String getUpdateSql() {
		return " UPDATE " + this.table + " ";
	}

	private String getDeleteSql() {
		return " DELETE FROM " + this.table + " WHERE " + this.pk + " = #{id} ";
	}

	private String getDeleteSql(String where) {
		return " DELETE FROM " + this.table + " WHERE " + where + " ";
	}

	private String getDeleteByIdsSql(String ids) {
		return " DELETE FROM " + this.table + " WHERE " + this.pk + " in ( " + ids + " ) ";
	}

	private String getCountSql() {
		return " SELECT " + this.pk + " FROM " + this.table + " ";
	}

	private String getCountSql(String sqlTemplate) {
		return " SELECT COUNT(*) CNT FROM (" + sqlTemplate + ") a";
	}
}

