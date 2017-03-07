package com.biu.system.meta.intercept;

import com.biu.core.aop.AopContext;
import com.biu.core.constant.ConstShiro;
import com.biu.core.meta.PageIntercept;
import com.biu.core.shiro.ShiroKit;

public class RoleIntercept extends PageIntercept {

	public void queryBefore(AopContext ac) {
		if (ShiroKit.lacksRole(ConstShiro.ADMINISTRATOR)) {
			String roles = ShiroKit.getUser().getRoles() + "," + ShiroKit.getUser().getSubRoles();
			String condition = "and id in ("+roles+")";
			ac.setCondition(condition);
//			ac.getParam().put("ids",roles);
		}
	}

}
