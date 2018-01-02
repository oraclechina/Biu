package com.oracle.cloud.biu.modules.biubiu.project.citic;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.oracle.cloud.biu.modules.biubiu.shell.OracleDatabaseAutoDeploy;
import com.oracle.cloud.biu.modules.biubiu.shell.OracleDatabaseAutoDeployRespEntity;

import lombok.extern.log4j.Log4j;

@Log4j
public class Biu_CITIC_SDK {

	/**
	 * 全局状态表
	 */
	public static Map<String, Biu_CITIC_SDK_Desc> xmap;
	
	/**
	 * 一键供应一台拥有已运行Oracle数据库实例的VM计算服务
	 * @param params
	 * 		String xid 				= params[0];
		String sshkeyname 		= params[1];
		String sshkeycontent 	= params[2];
		String sshprivatekey 	= params[3];
		String insname 			= params[4];
		String shape 			= params[5];
		String os				= params[6];
		String tenant 			= params[7];
		String volumnsize		= params[8];
		String dbcharset		= params[9];
		String dbsid			= params[10];
		String oracledblisport	= params[11];
		String price1hour		= params[12];
	 * @return
	 */
	public Biu_CITIC_SDK_Desc biuCreateAndDeployOracleDatabaseOnOCC(String...params) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuCreateAndDeployOracleDatabaseOnOCC");
		if (null == params)
			return null;
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		String xid 				= params[0];
		String sshkeyname 		= params[1];
		String sshkeycontent 	= params[2];
		String sshprivatekey 	= params[3];
		String insname 			= params[4];
		String shape 			= params[5];
		String os				= params[6];
		String tenant 			= params[7];
		String volumnsize		= params[8];
		String dbversion		= params[9];
		String dbcharset		= params[10];
		String dbsid			= params[11];
		String dbpassword		= params[12];
		String oracledblisport	= params[13];
		String price1hour		= params[14];
		
		citic.setXid(xid);
		citic.setXmessage("ok");
		citic.setXestimatetime(13);
		citic.setXdate(new Date());
		xmap.put(xid, citic);
		
		new Thread() {
			OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
			OracleDatabaseAutoDeployRespEntity resp = deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, dbversion, tenant, sshprivatekey, xid, volumnsize, dbpassword, dbcharset, dbsid, oracledblisport, price1hour);
		};
		return citic;
	}
	
	/**
	 * 显示VM供应状态，主要指的是Biu在供应期间的状态，若已经All Done了之后这里只会返回All Done
	 * @param params
	 * @return
	 */
	public Biu_CITIC_SDK_Desc biuShowStatus(String xid) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuShowStatus");
		Biu_CITIC_SDK_Desc desc = xmap.get(xid);
		return desc;
	}
	
	/**
	 * 停止一个含有数据库服务的虚拟机实例
	 * @param params
	 * @return
	 */	
	public Biu_CITIC_SDK_Desc biuStop(String...params) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuStop");
		if (null == params)
			return null;
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		return citic;
	}
	
	/**
	 * 启动一个含有数据库服务的虚拟机实例
	 * @param params
	 * @return
	 */
	public Biu_CITIC_SDK_Desc biuStart(String...params) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuStart");
		if (null == params)
			return null;
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		return citic;
	}
	
	/**
	 * 扩容一个含有数据库服务的虚拟机实例，这里指的是CPU和内存，不包含存储
	 * @param params
	 * @return
	 */
	public Biu_CITIC_SDK_Desc biuUpsizeCPU(String...params) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuUpsizeCPU");
		if (null == params)
			return null;
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		return citic;
	}
	
	/**
	 * 扩容一个含有数据库服务的虚拟机实例，这里指的是存储，不包含CPU和内存
	 * @param params
	 * @return
	 */
	public Biu_CITIC_SDK_Desc biuUpsizeVolumn(String...params) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuUpsizeVolumn");
		if (null == params)
			return null;
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		return citic;
	}
	
	/**
	 * 列出我所有的计算实例
	 * @param params
	 * @return
	 */	
	public Biu_CITIC_SDK_Desc biuListAllComputeInstances(String...params) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuListAllComputeInstances");
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		return citic;
	}
	
	/**
	 * 查看我的计算实例的状态
	 * @param params
	 * @return
	 */	
	public Biu_CITIC_SDK_Desc biuViewStatus(String...params) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuListAllComputeInstances");
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		return citic;
	}
	
	/**
	 * 查看我的数据库状态，通过JDBC检测
	 * @param params
	 * @return
	 */	
	public Biu_CITIC_SDK_Desc biuDBCheck(String...params) {
		log.info("Biu_CITIC_SDK - Loaded Plugin BiuBiu - biuListAllComputeInstances");
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		return citic;
	}
	
	public static void main(String[] args) {
		Biu_CITIC_SDK citic = new Biu_CITIC_SDK();
		UUID uuid = UUID.randomUUID();
		String xid = uuid.toString().replaceAll("-", "");
		
		/**
		 * 使用Example
		 */
		//用于创建并部署Oracle数据库,参数列表，1：事件名，2：事件ID，3：SSH名，若此名字在系统内不存在，参数4和5必须提供，4：若参数3指定的ssh名不存在，必须传递公钥内容，详情见参数3说明，5：若参数3指定的ssh名不存在，必须传递私钥内容，详情见参数3说明，6：实例名或实例label，必须不重复且唯一，若重复会返回错误，7：机型内存低配oc3，高配oc1m，8：操作系统版本分别是/oracle/public/OL_6.8_UEKR4_x86_64和/oracle/public/OL_7.2_UEKR4_x86_64，9：数据库版本，10：租户A，中信云portal客户登录用户名
		Biu_CITIC_SDK_Desc desc = citic.biuCreateAndDeployOracleDatabaseOnOCC(xid, "zhiqiang", "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCcucK...", "-----BEGIN RSA PRIVATE KEY-----...", "BiuTestInstance1", "oc3", "/oracle/public/OL_6.8_UEKR4_x86_64", "12.1", "TenantUserA");
		String ret = desc.getXmessage();
		if ("ok".equals(ret)) {
			//操作已提交，通过状态查询处理进度
			log.debug("申请成功,预计" + desc.getXestimatetime() + "分钟后完成，当前状态：" + desc.getXstatus());
		} else {
			log.debug("请求出错，详情如下");
			log.debug(ret);
		}
		
		//用于查询Biu执行事务中的状态,参数列表，1：事件名，2：事件ID，3：实例名
		Biu_CITIC_SDK_Desc desc2 = citic.biuShowStatus(xid);
		String ret2 = desc2.getXmessage();
		if ("ok".equals(ret2)) {
			//操作已提交，通过状态查询处理进度
			log.debug("执行查询成功，当前返回进度如下：" + desc2.getXstatus() + "，预计完成所需" + desc2.getXestimatetime() + "分钟");
		} else {
			log.debug("查询请求出错，详情如下");
			log.debug(ret);
		}
		
		//用于启动Oracle数据库计算实例,参数列表，1：事件名，2：事件ID，3：实例名
		Biu_CITIC_SDK_Desc desc3 = citic.biuStart(xid, "BiuTestInstance1");
		String ret3 = desc3.getXmessage();
		if ("ok".equals(ret3)) {
			//操作已提交，通过状态查询处理进度
			log.debug("执行启动成功，当前返回进度如下：" + desc3.getXstatus());
		} else {
			log.debug("查询请求出错，详情如下");
			log.debug(ret);
		}
		
		//用于停止Oracle数据库计算实例,参数列表，1：事件名，2：事件ID，3：实例名
		Biu_CITIC_SDK_Desc desc4 = citic.biuStop(xid, "BiuTestInstance1");
		String ret4 = desc4.getXmessage();
		if ("ok".equals(ret4)) {
			//操作已提交，通过状态查询处理进度
			log.debug("执行停止成功，当前返回进度如下：" + desc4.getXstatus());
		} else {
			log.debug("查询请求出错，详情如下");
			log.debug(ret);
		}
		
		//用于扩容虚拟机CPU，内存资源,参数列表，1：事件名，2：事件ID，3：实例名，4：新的机型
		Biu_CITIC_SDK_Desc desc5 = citic.biuUpsizeCPU(xid, "BiuTestInstance1", "oc4");
		String ret5 = desc5.getXmessage();
		if ("ok".equals(ret5)) {
			//操作已提交，通过状态查询处理进度
			log.debug("执行CPU扩容成功，当前返回进度如下：" + desc5.getXstatus());
		} else {
			log.debug("查询请求出错，详情如下");
			log.debug(ret);
		}
		
		//用于扩容虚拟机存储资源,参数列表，1：事件名，2：事件ID，3：实例名，4：新创建的存储大小
		Biu_CITIC_SDK_Desc desc6 = citic.biuUpsizeVolumn(xid, "BiuTestInstance1", "300");
		String ret6 = desc6.getXmessage();
		if ("ok".equals(ret6)) {
			//操作已提交，通过状态查询处理进度
			log.debug("执行存储扩容成功，当前返回进度如下：" + desc6.getXstatus());
		} else {
			log.debug("查询请求出错，详情如下");
			log.debug(ret);
		}
		
		//用于列出我的所有虚拟计算实例，参数列表，1：事件名，2：租户名
		Biu_CITIC_SDK_Desc desc7 = citic.biuListAllComputeInstances("TenantUserA");
		String ret7 = desc7.getXmessage();
		if ("ok".equals(ret7)) {
			//操作已提交，通过状态查询处理进度
			log.debug("查询成功，详情如下");
			List<Biu_CITIC_SDK_Desc> dd = desc7.getMyvmlist();
			for (Biu_CITIC_SDK_Desc biu_CITIC_SDK_Desc : dd) {
				log.debug(biu_CITIC_SDK_Desc.getInstancename());
			}
		} else {
			log.debug("查询请求出错，详情如下");
			log.debug(ret);
		}
		
		//用于查看我的虚拟计算实例运行状态（注：此方法和biuShowStatus有区别），参数列表，1：事件名，2：租户名，3：实例名
		Biu_CITIC_SDK_Desc desc8 = citic.biuViewStatus("TenantUserA", "BiuTestInstance1");
		String ret8 = desc8.getXmessage();
		if ("ok".equals(ret8)) {
			//操作已提交，通过状态查询处理进度
			log.debug("查询成功，详情如下");
			log.debug("虚拟机运行状态：" + desc8.getXstatus());
		} else {
			log.debug("查询请求出错，详情如下");
			log.debug(ret);
		}
	}
}
