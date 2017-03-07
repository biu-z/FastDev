package com.biu.system.meta.intercept;

import com.biu.core.aop.AopContext;
import com.biu.core.constant.ConstShiro;
import com.biu.core.meta.PageIntercept;
import com.biu.core.shiro.ShiroKit;

public class DeptIntercept extends PageIntercept {

	public void queryBefore(AopContext ac) {
		if (ShiroKit.lacksRole(ConstShiro.ADMINISTRATOR)) {
			String depts = ShiroKit.getUser().getDeptId() + "," + ShiroKit.getUser().getSubDepts();
			String condition = "and id in (#{ids})";
			ac.setCondition(condition);
			ac.getParam().put("ids", depts);
		}
	}

}
