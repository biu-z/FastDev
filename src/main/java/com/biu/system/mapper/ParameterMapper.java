package com.biu.system.mapper;

import com.biu.common.persistence.annotation.MyBatisRepository;
import com.biu.system.model.Parameter;
import com.biu.system.util.MapperUtils;

/**
 * Created by Lyon on 2017/2/27.
 */
@MyBatisRepository
public interface ParameterMapper extends MapperUtils<Parameter> {

    boolean getPara();

}
