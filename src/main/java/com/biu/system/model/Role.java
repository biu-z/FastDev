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
package com.biu.system.model;

import com.biu.common.base.model.BaseModel;
import com.biu.core.annotation.BindID;
import lombok.Data;

import javax.persistence.*;

@Table(name = "blade_role")
@BindID(name = "id")
@Data
//角色表
public class Role extends BaseModel {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; //主键
	private Integer deptid; //部门id
	private String name; //角色名
	private Integer num; //排序号
	private Integer pid; //父角色
	private String tips; //角色别名(用于Permission注解权限检查)
	private Integer version; //版本号
}
