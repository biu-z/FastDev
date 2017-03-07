package com.biu.common.base.service.impl;

import com.biu.common.base.service.BaseService;
import com.biu.core.plugins.dao.Blade;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Lyon on 2017/2/17.
 */
//@Service
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    private Class<T> getClazz() {
        Type t = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) t).getActualTypeArguments();
        return (Class<T>) params[0];
    }

//    @Lazy
    @Autowired
    private Mapper<T> mapper;

    @Override
    public int save(T record) {
        return mapper.insert(record);
    }

    @Override
    public int saveNotEmpty(T record) {
        return mapper.insertSelective(record);
    }

    @Override
    public int delete(T record) {
        return mapper.delete(record);
    }

    @Override
    public int deleteByKey(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int deleteByIds(String ids) {
        return Blade.create(getClazz()).deleteByIds(ids);
    }

    @Override
    public int update(T record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateNotEmpty(T record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public T getOne(T record) {
        return mapper.selectOne(record);
    }

    @Override
    public T getOneByKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public List<T> getList() {
        return mapper.selectAll();
    }

    @Override
    public List<T> getList(T record) {
        return mapper.select(record);
    }

    @Override
    public int getCount() {
        return mapper.selectCount(null);
    }

    @Override
    public int getCount(T record) {
        return mapper.selectCount(record);
    }

    @Override
    public PageInfo<T> getPage(int pageNum, int pageSize, T record) {
        PageHelper.startPage(pageNum,pageSize);
        List<T> list = mapper.select(record);
        PageInfo<T> page = new PageInfo<T>(list);
        return page;
    }
}
