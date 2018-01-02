package com.oracle.cloud.biu.modules.biubiu.project.citic;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class Biu_CITIC_SDK_Desc {

	//事务ID，每一个非查询申请都会形成一个独立的事务存储在内存中，服务器如遇重启则信息丢失，没有持久化，需调用方实现持久化
	private String xid = "728f7703860049cf8003b134ed553eea";
	//事务类型，每一个非查询申请都会有一个事务类型，响应实例将包含请求的事务类型
	private String xtype = "biuCreateAndDeployOracleDatabaseOnOCC";
	//事务消息，当非查询申请的返回值为ok则为成功操作，否则为任何可能的报错信息
	private String xmessage = "ok";
	private String xlog = "";
	//事务返回状态
	private String xstatus = "申请中";
	//事务接受时间
	private Date xdate = new Date();
	//事务执行完预估时间（分钟）
	private Integer xestimatetime = 13;
	//主机公共IP
	private String pubip = "140.86.14.121";
	//主机私有IP
	private String prvip = "10.15.38.172";
	//sshkey名称
	private String sshkeyname = "zhiqiang";
	//sshkey 公钥内容(仅适用系统内无SSH Key时，新建SSH Key关系时传递，若使用已有ssh key则不需要传递)
	private String sshkeycontent = "";
	//实例名
	private String instancename = "BiuTestIns1";
	//实例型号
	private String shape = "oc4";
	//操作系统
	private String os = "/oracle/public/OL_6.8_UEKR4_x86_64";
	//存储数量
	private Integer volumns = 1;
	//总存储空间
	private Integer volumnssize = 300;
	//租户名
	private String tenant = "UserA";
	//私钥内容，必传参数
	private String pk = "pkcontent";
	//Oracle 数据库 base目录
	private String oracledbbase = "/u01/app/oracle";
	//Oracle 数据库 home目录
	private String oracledbhome = "/u01/app/oracle/product/11.2.0/dbhome_1";
	//Oracle 数据库 SID
	private String oracledbsid = "orcl11g";
	//Oracle 数据库 服务监听端口
	private String oracledblisport = "1521";
	//Oracle 数据库 库字符集
	private String oracledbcharset = "AL32UTF8";
	//Oracle 数据库 Web控制台URL
	private String oracledbconsoleurl = "http://www.baidu.com";
	//资费，运行中每小时多少元
	private Integer price1hour = 5;
	//我的租户下所有虚拟机列表
	private List<Biu_CITIC_SDK_Desc> myvmlist = null;
}
