package com.biu.system.mapper;

import com.biu.common.persistence.annotation.MyBatisRepository;
import com.biu.system.model.User;
import com.biu.system.util.MapperUtils;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Lyon on 2017/2/16.
 */
@MyBatisRepository
public interface UserMapper extends MapperUtils<User>{

    // 递归查找上级部门id集合
    String queryParent(@Param("param") Object param ,@Param("table") String table);

    // 递归查找子部门id集合
    String queryChildren(@Param("param") Object param ,@Param("table") String table);

}
