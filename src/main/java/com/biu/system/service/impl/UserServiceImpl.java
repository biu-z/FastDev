package com.biu.system.service.impl;

import com.biu.common.base.service.impl.BaseServiceImpl;
import com.biu.system.mapper.UserMapper;
import com.biu.system.model.User;
import com.biu.system.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Lyon on 2017/2/17.
 */
@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    @Resource
    private UserMapper userMapper;

}
