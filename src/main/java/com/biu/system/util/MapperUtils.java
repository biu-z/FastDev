package com.biu.system.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by Lyon on 2017/2/17.
 */
public interface MapperUtils<T> extends Mapper<T>,MySqlMapper<T>
{
}
