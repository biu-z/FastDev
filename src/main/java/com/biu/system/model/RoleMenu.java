package com.biu.system.model;


import com.biu.common.base.model.BaseModel;

import javax.persistence.*;

/**
 * Generated by Blade.
 * 2017-02-15 18:09:11
 */
@Table(name = "blade_role_menus")
@SuppressWarnings("serial")
public class RoleMenu extends BaseModel {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Integer roleid;
	private String menus;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getRoleid() {
		return roleid;
	}
	
	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}
	
	public String getMenus() {
		return menus;
	}
	
	public void setMenus(String menus) {
		this.menus = menus;
	}
	

}
