package com.biu.common.intercept;

import com.biu.core.aop.AopContext;
import com.biu.core.constant.ConstShiro;
import com.biu.core.intercept.QueryInterceptor;
import com.biu.core.shiro.ShiroKit;

public class SelectRoleIntercept extends QueryInterceptor {

	public void queryBefore(AopContext ac) {
		if (ShiroKit.lacksRole(ConstShiro.ADMINISTRATOR)) {
			String roles = ShiroKit.getUser().getRoles() + "," + ShiroKit.getUser().getSubRoles();
			String condition = "where id in (#{join(ids)})";
			ac.setCondition(condition);
			ac.getParam().put("ids", roles.split(","));
		}
	}

}
