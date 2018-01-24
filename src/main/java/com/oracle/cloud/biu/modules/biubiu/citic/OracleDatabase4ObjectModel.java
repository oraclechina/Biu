package com.oracle.cloud.biu.modules.biubiu.citic;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class OracleDatabase4ObjectModel {

	//容器ID，可以不用关心
	String conid;
	//CDB名字，看需求是否需要
	String cdbname;
	//PDB名字，唯一，很重要，通过此名字找到对应的订单编号计费用
	String pdbname;
	//PDB块存储总量，这个为此CDB上所有的pdb容器之和，不作为计量使用
	String pdbblocktotalsize;
	//PDB块存储已使用用量，此为CDB上该PDB使用的块存储容量
	String pdbblockusedsize;
	//PDB块存储使用占比
	Integer pdbblockusedrate;
	//PDB对象存储总量，意思为该PDB所在的CDB使用的总对象存储容量，不作为计量使用
	String pdbobjecttotalsize;
	//PDB对象存储使用量，此作为对象存储用量引用值
	String pdbobjectusedsize;
	//统计日期
	Date currdate = new Date();
	
	public String toString() {
		JSONObject obj = new JSONObject();
		return obj.toJSONString(this);
	}
	
}
