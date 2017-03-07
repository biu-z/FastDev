package com.biu.common.intercept;

import com.biu.core.intercept.SelectInterceptor;
import com.biu.core.meta.IQuery;

public class DefaultSelectFactory extends SelectInterceptor {
	
	public IQuery deptIntercept() {
		return new SelectDeptIntercept();
	}
	
	public IQuery roleIntercept() {
		return new SelectRoleIntercept();
	}
	
}
