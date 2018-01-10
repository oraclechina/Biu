package com.oracle.cloud.biu.modules.biubiu.project.citic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class Biu_CITIC_SDK_Desc {

	//下单ID
	private String citicinstanceid = "";
	
	//事务ID，每一个非查询申请都会形成一个独立的事务存储在内存中，服务器如遇重启则信息丢失，没有持久化，需调用方实现持久化
	//728f7703860049cf8003b134ed553eea
	private String xid = "";
	//事务类型，每一个非查询申请都会有一个事务类型，响应实例将包含请求的事务类型
	//biuCreateAndDeployOracleDatabaseOnOCC Deprecated
	private String xtype = "";
	//事务消息，当非查询申请的返回值为ok则为成功操作，否则为任何可能的报错信息
	//ok
	private String xmessage = "";
	private String xlog = "";
	//事务返回状态
	//申请中
	private String xstatus = "";
	//事务接受时间
	private Date xdate = new Date();
	//事务执行完预估时间（分钟）
	private Integer xestimatetime = 24;
	//事务预估剩余执行时间(分钟)
	private Integer xremaintime = xestimatetime;
	//编排计划ID
	private String orchid = "";
	//主机公共IP
	//140.86.14.121
	private String pubip = "";
	//主机私有IP
	//10.15.38.172
	private String prvip = "";
	//sshkey名称
	//zhiqiang
	private String sshkeyname = "";
	//sshkey 公钥内容(仅适用系统内无SSH Key时，新建SSH Key关系时传递，若使用已有ssh key则不需要传递)
	private String sshkeycontent = "";
	//实例名
	//BiuTestIns1
	private String instancename = "";
	//实例型号
	//oc4
	private String shape = "";
	//操作系统
	///oracle/public/OL_6.8_UEKR4_x86_64
	private String os = "";
	//存储数量
	private Integer volumns = 1;
	//总存储空间
	private Integer volumnssize = 300;
	//租户名
	//UserA
	private String tenant = "";
	//私钥内容，必传参数
	//pkcontent
	private String pk = "";
	//Oracle 数据库 base目录
	private String oracledbbase = "/u01/app/oracle";
	//Oracle 数据库 home目录
	private String oracledbhome = "/u01/app/oracle/product/11.2.0/dbhome_1";
	//Oracle 数据库 SID
	//orcl11g
	private String oracledbsid = "";
	//Oracle 数据库 服务监听端口
	//1521
	private String oracledblisport = "";
	//Oracle 数据库 库字符集
	//AL32UTF8
	private String oracledbcharset = "";
	//Oracle 数据库 Web控制台URL
	//https://140.86.12.72:5500/em
	private String oracledbconsoleurl = "";
	//资费，运行中每小时多少元
	private Integer price1hour = 5;
	//我的租户下所有虚拟机列表
	private List<Biu_CITIC_SDK_Desc> myvmlist = null;

	//用于最终一致性回滚
	private List<String> desctroy_sshkeys = new ArrayList<>();
	//用于最终一致性回滚
	private List<String> desctroy_volumnstoragelist = new ArrayList();
	//用于最终一致性回滚
	private List<String> desctroy_securityrulelist = new ArrayList();
	//用于最终一致性回滚
	private List<String> desctroy_securitygrouplist = new ArrayList();
	//用于最终一致性回滚
	private String desctroy_finalinstanceid = "";

	private JSONObject instance = null;
}
