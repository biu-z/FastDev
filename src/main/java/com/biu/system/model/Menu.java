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

@Table(name = "blade_menu")
@BindID(name = "id")
@Data
//菜单表
public class Menu extends BaseModel {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; //主键
	private String code; //菜单编号
	private String alias; //菜单别名
	private String icon; //图标
	private String isopen; //是否打开tab
	private String istemplate; //是否模板
	private Integer levels; //菜单层级
	private String name; //菜单名称
	private Integer num; //排序号
	private String path; //地址
	private String pcode; //父编号
	private String source; //数据源
	private Integer status; //状态
	private String tips; //备注
	private String url; //接口地址
	private Integer version; //版本号
}
