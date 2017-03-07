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
package com.biu.core.meta;

import com.biu.core.aop.AopContext;

/**
 * 分页aop
 */
public interface IQuery {

	/**
	 * 查询前操作
	 * 
	 * @param ac
	 */
	void queryBefore(AopContext ac);

	/**
	 * 查询后操作
	 * 
	 * @param ac
	 */
	void queryAfter(AopContext ac);
	
}
