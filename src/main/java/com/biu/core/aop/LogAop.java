package com.biu.core.aop;

import com.biu.common.vo.ShiroUser;
import com.biu.core.annotation.DoLog;
import com.biu.core.shiro.ShiroKit;
import com.biu.core.toolbox.Func;
import com.biu.core.toolbox.kit.HttpKit;
import com.biu.core.toolbox.kit.ObjectKit;
import com.biu.core.toolbox.kit.StrKit;
import com.biu.core.toolbox.log.BladeLogManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * AOP 日志
 */
@Aspect
@Component
public class LogAop {
	private static Logger log = LoggerFactory.getLogger(LogAop.class);

	//@Pointcut("within(@org.springframework.stereotype.Controller *)")
	@Pointcut("execution(* com.biu.*..service.*.*(..))")
	public void cutService() {
	}

	@Around("cutService()")
	public Object recordSysLog(ProceedingJoinPoint point) throws Throwable {
		if(!BladeLogManager.isDoLog()){
			return point.proceed();
		}
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method method = ms.getMethod();
		String methodName = ms.getName();
		DoLog doLog = method.getAnnotation(DoLog.class);
		if (!isWriteLog(methodName) && null == doLog) {
			return point.proceed();
		}
		ShiroUser user = ShiroKit.getUser();
		if(null == user){
			return point.proceed();
		}
		String className = point.getTarget().getClass().getName();
		Object[] params = point.getArgs();
		StringBuilder sb = new StringBuilder();
		Enumeration<String> paraNames = null;
		HttpServletRequest request = HttpKit.getRequest();
		if (params != null && params.length > 0) {
			paraNames = request.getParameterNames();
			String key;
			String value;
			while (paraNames.hasMoreElements()) {
				key = paraNames.nextElement();
				value = request.getParameter(key);
				Func.builder(sb, key, "=", value, "&");
			}
			if (Func.isEmpty(sb)) {
				Func.builder(sb, request.getQueryString());
			}
		}
		try {
			String msg = Func.format("[类名]:{}  [方法]:{}  [参数]:{}", className, methodName, StrKit.removeSuffix(sb.toString(), "&"));
			BladeLogManager.doLog((ObjectKit.isNull(doLog) ? getLogName(methodName) : doLog.name()), msg, true);
			log.info(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return point.proceed();
	}

	private boolean isWriteLog(String method) {
		String[] pattern = BladeLogManager.logPatten();
		for (String s : pattern) {
			if (method.indexOf(s) > -1) {
				return true;
			}
		}
		return false;
	}
	
	private String getLogName(String method){
		String[] pattern = BladeLogManager.logPatten();
		for (String s : pattern) {
			if (method.indexOf(s) > -1) {
				return BladeLogManager.logMaps().getStr(s);
			}
		}
		return "";
	}
	
}
