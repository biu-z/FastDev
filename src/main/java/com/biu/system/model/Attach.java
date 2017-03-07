package com.biu.system.model;

import com.biu.common.base.model.BaseModel;
import com.biu.core.annotation.BindID;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "blade_attach")
@BindID(name = "id")
//附件表
@Data
public class Attach extends BaseModel {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; //主键
	private String code; //编码
	private Integer creater; //创建人
	private String name; //附件名
	private Integer status; //状态
	private String url; //附件地址
	private Date createtime; //上传时间
}
