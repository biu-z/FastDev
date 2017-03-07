package com.biu.system.model;

import com.biu.common.base.model.BaseModel;
import com.biu.core.annotation.BindID;
import lombok.Data;

import javax.persistence.*;

@Table(name = "blade_generate")
@BindID(name = "id")
@Data
//在线开发
public class Generate extends BaseModel {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; //主键
	private String modelname; //实体类名
	private String name; //模块名称
	private String realpath; //物理地址
	private String packagename; //package包名
	private String pkname; //主键名
	private String tablename; //表名
	private String tips; //备注
}
