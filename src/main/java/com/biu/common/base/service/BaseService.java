package com.biu.common.base.service;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 通用Service接口
 * @param <T>
 * @author Lyon
 */
//@Service
public interface BaseService<T> {

    /**
     *  插入一条数据
     *  支持Oracle序列,UUID,类似Mysql的INDENTITY自动增长(自动回写)
     *  优先使用传入的参数值,参数值空时,才会使用序列、UUID,自动增长
     * @param record
     * @return
     */
    int save(T record);

    /**
     * 插入一条数据,只插入不为null的字段,不会影响有默认值的字段
     * 支持Oracle序列,UUID,类似Mysql的INDENTITY自动增长(自动回写)
     * 优先使用传入的参数值,参数值空时,才会使用序列、UUID,自动增长
     * @param record
     * @return
     */
    int saveNotEmpty(T record);

    /**
     * 根据实体类中字段不为null的条件进行删除,条件全部使用=号and条件
     * @param record
     * @return
     */
    int delete(T record);

    /**
     * 通过主键进行删除,这里最多只会删除一条数据
     单个字段做主键时,可以直接写主键的值
     联合主键时,key可以是实体类,也可以是Map
     * @param key
     * @return
     */
    int deleteByKey(Object key);

    /**
     * 根据主键集合删除
     * @param ids
     * @return
     */
    int deleteByIds(String ids);

    /**
     * 根据主键进行更新,这里最多只会更新一条数据
     参数为实体类
     * @param record
     * @return
     */
    int update(T record);

    /**
     * 根据主键进行更新
     只会更新不是null的数据
     * @param record
     * @return
     */
    int updateNotEmpty(T record);

    /**
     * 根据实体类不为null的字段进行查询,条件全部使用=号and条件
     * @param record
     * @return
     */
    T getOne(T record);

    /**
     *  根据主键进行查询,必须保证结果唯一
     单个字段做主键时,可以直接写主键的值
     联合主键时,key可以是实体类,也可以是Map
     * @param key
     * @return
     */
    T getOneByKey(Object key);

    /**
     * 查询全部
     * @return
     */
    List<T> getList();

    /**
     * 根据实体类不为null的字段进行查询,条件全部使用=号and条件
     * @param record
     * @return
     */
    List<T> getList(T record);

    /**
     * 计数全部
     * @return
     */
    int getCount();

    /**
     * 根据实体类不为null的字段查询总数,条件全部使用=号and条件
     * @param record
     * @return
     */
    int getCount(T record);

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    PageInfo<T> getPage(int pageNum, int pageSize, T record);
}
