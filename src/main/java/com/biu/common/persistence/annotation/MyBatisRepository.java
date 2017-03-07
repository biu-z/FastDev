/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/Biu/jeesite">JeeSite</a> All rights reserved.
 */
package com.biu.common.persistence.annotation;

import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * 标识MyBatis的DAO,方便{@link org.mybatis.spring.mapper.MapperScannerConfigurer}的扫描。 
 * @author Biu
 * @version 2013-8-28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repository
public @interface MyBatisRepository {
	
	/**
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * @return the suggested component name, if any
	 */
	String value() default "";

}