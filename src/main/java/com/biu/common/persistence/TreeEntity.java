/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/Biu/jeesite">JeeSite</a> All rights reserved.
 */
package com.biu.common.persistence;

import com.biu.common.utils.Reflections;
import com.biu.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 数据Entity类
 * @author Biu
 * @version 2014-05-16
 */
public abstract class TreeEntity<T> extends DataEntity<T> {

	private static final long serialVersionUID = 1L;

	protected T parent;	// 父级编号
	protected String parentIds; // 所有父级编号
	protected String name; 	// 机构名称
	protected Integer sort;		// 排序
	
	public TreeEntity() {
		super();
		this.sort = 30;
	}
	
	public TreeEntity(String id) {
		super(id);
	}
	
	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 * @return
	 */
	@JsonBackReference
	public abstract T getParent();

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 * @return
	 */
	public abstract void setParent(T parent);

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public String getParentId() {
		String id = null;
		if (parent != null){
			id = (String) Reflections.getFieldValue(parent, "id");
		}
		return StringUtils.isNotBlank(id) ? id : "0";
	}
	
}
