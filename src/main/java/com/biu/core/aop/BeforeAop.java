/**
 * Copyright (c) 2015-2017, Chill Zhuang 庄骞 (biu@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biu.core.aop;

import com.biu.core.annotation.Before;
import com.biu.core.intercept.Interceptor;
import com.biu.core.toolbox.kit.HttpKit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * before拦截
 */
@Aspect
@Component
public class BeforeAop {

	@Pointcut(value = "@annotation(com.biu.core.annotation.Before)")
	private void cutBefore() {

	}

	@Around("cutBefore()")
	public Object doBefore(ProceedingJoinPoint point) throws Throwable {
		HttpServletRequest request = HttpKit.getRequest();
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method method = ms.getMethod();
		Object[] args = point.getArgs();
		Class<?> clazz = point.getTarget().getClass();
		Before before = method.getAnnotation(Before.class);
		Interceptor ic = before.value().newInstance();
		Object result = ic.intercept(new Invocation(clazz, method, args, request));
		if (null == result) {
			return point.proceed();
		} else {
			return result;
		}
	}
}
