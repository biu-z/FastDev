package com.biu.system.mapper;

import com.biu.common.persistence.annotation.MyBatisRepository;
import com.biu.system.model.Role;
import com.biu.system.util.MapperUtils;

/**
 * Created by Lyon on 2017/2/28.
 */
@MyBatisRepository
public interface RoleMapper extends MapperUtils<Role> {

//    int deleteByIds(Integer[] ids);
}
