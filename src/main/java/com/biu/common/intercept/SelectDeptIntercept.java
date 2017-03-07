package com.biu.common.intercept;

import com.biu.core.aop.AopContext;
import com.biu.core.constant.ConstShiro;
import com.biu.core.intercept.QueryInterceptor;
import com.biu.core.shiro.ShiroKit;

public class SelectDeptIntercept extends QueryInterceptor {

	public void queryBefore(AopContext ac) {
		if (ShiroKit.lacksRole(ConstShiro.ADMINISTRATOR)) {
			String depts = ShiroKit.getUser().getSuperDepts() + "," + ShiroKit.getUser().getDeptId() + "," + ShiroKit.getUser().getSubDepts();
			String condition = "where id in (#{join(ids)})";
			ac.setCondition(condition);
			ac.getParam().put("ids", depts.split(","));
		}
	}

}
