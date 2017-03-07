package com.biu.system.model;

import com.biu.common.base.model.BaseModel;
import com.biu.core.annotation.BindID;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "blade_user")
@BindID(name = "id")
@Data
@SuppressWarnings("serial")
//用户表
public class User extends BaseModel {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; //主键
	private String account; //账号
	private Integer deptid; //部门id
	private String email; //邮箱
	private String name; //姓名
	private String password; //密码
	private String salt; //密码盐
	private String phone; //手机号
	private String roleid; //角色id
	private Integer sex; //性别
	private Integer status; //状态
	private Integer version; //版本号
	private Date birthday; //生日
	private Date createtime; //创建时间
}
