package com.oracle.cloud.biu.modules.biubiu.shell;

import java.io.File;
import java.math.BigDecimal;
import java.net.UnknownHostException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.api.ComputeAPI;
import com.oracle.cloud.biu.api.NetworkAPI;
import com.oracle.cloud.biu.api.SecurityAPI;
import com.oracle.cloud.biu.api.StorageAPI;
import com.oracle.cloud.biu.modules.biubiu.BaseModule;
import com.oracle.cloud.biu.modules.biubiu.shell.patch.OracleJDBCTools;
import com.oracle.cloud.biu.modules.biubiu.ssh.Shell;
import com.oracle.cloud.biu.modules.biubiu.ssh.Shell.Plain;
import com.oracle.cloud.biu.modules.biubiu.ssh.Ssh;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class OracleDatabaseAutoDeploy extends BaseModule {

	private static final int RETRY = 5;
	
	@Override
	public void init() {
		try {
			authoritied();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 此方法只针对单实例用户，多实例环境下需要另外改造，KV
	 * 此方法实现虚拟机一键供应，必须按照规则指定参数表，方法调用前，参数必须经过非空检查，并且trim
	 * params:
	 * 1 = sshkey名称
	 * 2 = sshkey公钥字符串全文
	 * 3 = 实例名
	 * 4 = shape
	 * 5 = 操作系统定义串
	 * 6 = 数据库版本
	 * 7 = 租户名
	 * 8 = 私钥内容
	 */
	@SuppressWarnings("finally")
	@Override
	public OracleDatabaseAutoDeployRespEntity deploy(String...params) {
		System.out.println(" ____  _       ____  _\n| __ )(_)_   _| __ )(_)_   _\n|  _ \\| | | | |  _ \\| | | | |\n| |_) | | |_| | |_) | | |_| |\n|____/|_|\\__,_|____/|_|\\__,_|");
		log.debug("=================== Module[OracleDatabaseAutoDeployRespEntity] is received start event.========================");
		//将获取到的参数传给临时变量，用户检测参数合法性
		String sshkeyname       = params[0];
		String sshkeycontent    = params[1];
		String insname          = params[2];
		String shape			= params[3];
		String os				= params[4];
		String version			= params[5];
		String tenant			= params[6];
		String pk				= params[7];
		
		//将程序是否可以继续执行做一个标记，标记为false的不允许继续执行
		OracleDatabaseAutoDeployRespEntity resp = new OracleDatabaseAutoDeployRespEntity();
		try {
			//
			//遍历已有的所有ssh列表，若已经存在的sshkey name不允许创建，模糊匹配原则，宁可错杀不可放过。
			log.info("[Process] Listing SSH Keys");
			boolean passable = true;
			String respMsg = "";
			boolean hasExistsKey = false;
			String finalinsname;
			String vcableid;
			String publicip = "";
			int times = 0;
			JSONObject obj = SecurityAPI.listSSHKeys(m.get("sshkeys"));
			JSONArray ary1 = obj.getJSONArray("result");
			for (Object object : ary1) {
				JSONObject t1 = (JSONObject) object;
				String tmp_name = t1.getString("name");
				// keep the field [tmp_enabled] current status
				boolean tmp_enabled = t1.getBoolean("enabled");
				
				tmp_name = BiuUtils.kvNULL(tmp_name, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
				
				if (tmp_name.trim().equals(sshkeyname)) {
					hasExistsKey = true;
				}
			}
			log.info("[Process] SSH Keys check result:" + hasExistsKey);
			if (!hasExistsKey) {
				//没有存在的SSH Key需要创建
				log.warn("[WARN] The sshkey name " + sshkeyname + " is not exists in system, will create a new sshkey pair in system.");
				if (StringUtils.isBlank(sshkeycontent)) {
					respMsg = "[ERROR] The sshkey name " + sshkeyname + " is not exists in system, but the public sshkey content is NULL.";
					log.error(respMsg);
					passable = false;
				} else {
					log.info("[Process] Creating SSH Key:" + sshkeyname);
					SecurityAPI.createSSHKey(m.get("createsshkey"), sshkeyname, sshkeycontent);
					times = 0;
					do {
						times ++;
						Thread.sleep(30000);
						JSONObject obj2 = SecurityAPI.listSSHKeys(m.get("sshkeys"));
						JSONArray ary2 = obj2.getJSONArray("result");
						boolean sechasExistsKey = false;
						for (Object object : ary2) {
							JSONObject t1 = (JSONObject) object;
							String tmp_name = t1.getString("name");
							// keep the field [tmp_enabled] current status
							boolean tmp_enabled = t1.getBoolean("enabled");
							tmp_name = BiuUtils.kvNULL(tmp_name, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
							if (tmp_name.trim().equals(sshkeyname)) {
								sechasExistsKey = true;
							}
						}
						if (!sechasExistsKey) {
							respMsg = "[ERROR] The sshkey name " + sshkeyname + " can not be created, please check the ssh public key content.";
							log.error(respMsg);
							passable = false;
						}	
					} while ((!passable) && (times < RETRY));
				}
			}
			if ((!passable) && (!StringUtils.isBlank(respMsg))) {
				resp.setRespMsg(respMsg);
				return resp;
			}
			log.info("[Process] SSH Key loaded:" + sshkeyname);
			
			//创建计算实例并验证实例已经运行
			log.info("[Process] Check Instance Name:" + insname);
			boolean hasInstance = verifyInstanceName(insname);
			if (hasInstance) {
				respMsg = "[ERROR] The instance name " + insname + " is exists, please change to another one.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				return resp;
			}
			log.info("[Process] Passed Check Instance Name:" + insname);
			log.info("[Process] Creating Instance:" + insname);
			JSONObject obj2 = ComputeAPI.createComputeInstance(m.get("createcompute"), insname, shape, os, sshkeyname);
			finalinsname = getInstanceName(obj2);
			boolean insRunning = false;
			passable = false;
			do {
				times ++;
				Thread.sleep(1000 * 90);
				JSONObject obj3 = ComputeAPI.listComputes(m.get("computeinstances"));
				JSONArray ary3 = obj3.getJSONArray("result");
				for (Object t_obj2 : ary3) {
					org.json.JSONObject t_jsonobj2 = (org.json.JSONObject) t_obj2;
					String tmp_name = t_jsonobj2.getString("name");
					String state = t_jsonobj2.getString("state");
					tmp_name = BiuUtils.kvNULL(tmp_name, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
					if ((tmp_name.trim().startsWith(insname)) && (state.equals("running"))) {
						//实例已经创建，并且运行良好
						passable = true;
					}
				}
			} while ((!passable) && (times < RETRY));
			
			if (!passable) {
				respMsg = "[ERROR] The instance created failed, please try it later or contact to the administrator.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				return resp;
			}
			vcableid = getInstanceVCable(finalinsname);
			log.info("[Process] Created Instance:" + insname + " and instance is running now");
			
			log.info("[Process] Creating Volumns 1");
			//计算实例已经成功创建，现在创建1个存储盘
			
			JSONObject stjson1 = StorageAPI.createStorageBlankVolumns(m.get("create_storage"), "100");
//			JSONObject stjson2 = StorageAPI.createStorageBlankVolumns(m.get("create_storage"), "100");
//			JSONObject stjson3 = StorageAPI.createStorageBlankVolumns(m.get("create_storage"), "8");
//			JSONObject stjson4 = StorageAPI.createStorageBlankVolumns(m.get("create_storage"), "50");
			
			//检查存储盘运行状态
			times = 0;
			do {
				times ++;
				Thread.sleep(1000 * 10);
				boolean ret1 = verifyStorageVolumnStatus(stjson1);
//				boolean ret2 = verifyStorageVolumnStatus(stjson2);
//				boolean ret3 = verifyStorageVolumnStatus(stjson3);
//				boolean ret4 = verifyStorageVolumnStatus(stjson4);
//				if ((ret1) && (ret2) && (ret3) && (ret4)) {
				if (ret1) {
					passable = true;
				} else {
					passable = false;
				}
			} while ((!passable) && (times < RETRY));
			if (!passable) {
				respMsg = "[ERROR] The volumns 1 created failed, please try it later or contact to the administrator.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				return resp;
			}
			log.info("[Process] Created Volumns 1");
			
			log.info("[Process] Mount Volumns 1 to instance:" + insname);
			//存储盘全部online，现在开始挂接存储盘到计算实例
			StorageAPI.createStorageAttachments(m.get("attach_storage"), BiuUtils.kvNULL(stjson1.getString("name"), BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"), BiuUtils.kvNULL(finalinsname, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"), "1");
//			StorageAPI.createStorageAttachments(m.get("attach_storage"), BiuUtils.kvNULL(stjson2.getString("name"), BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"), BiuUtils.kvNULL(finalinsname, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"), "2");
//			StorageAPI.createStorageAttachments(m.get("attach_storage"), BiuUtils.kvNULL(stjson3.getString("name"), BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"), BiuUtils.kvNULL(finalinsname, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"), "3");
//			StorageAPI.createStorageAttachments(m.get("attach_storage"), BiuUtils.kvNULL(stjson4.getString("name"), BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"), BiuUtils.kvNULL(finalinsname, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"), "4");
		
			//检查存储盘挂接状态
			//检查存储盘运行状态
			times = 0;
			do {
				times ++;
				Thread.sleep(1000 * 5);
				boolean ret1 = verifyVolumnAttachment(stjson1);
//				boolean ret1 = verifyVolumnAttachment(stjson1, stjson2, stjson3, stjson4);
				if (!ret1) {
					passable = false;
				} else {
					passable = true;
				}
			} while ((!passable) && (times < RETRY));
			//存储盘全部已经挂载
			
			if (!passable) {
				respMsg = "[ERROR] The volumns 1 mounted failed, please try it later or contact to the administrator.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				return resp;
			}
			log.info("[Process] Mounted Volumns 1 to instance:" + insname);
			
			//将计算实例分配固定IP
			log.info("[Process] Creating IP Reservation for instance:" + insname);
			JSONObject resp2 = NetworkAPI.createSharedRandomIP(m.get("create_randomip"), vcableid);
			publicip = getInstancePublicIP(resp2);
			Thread.sleep(1000 * 30);
			log.info("[Process] Created IP Reservation for instance:" + insname);			
			
			//创建安全组
			log.info("[Process] Creating Security List:" + insname + "_seclist");
			SecurityAPI.createSecList(m.get("createseclist"), insname + "_seclist", "permit", "permit");
			Thread.sleep(1000 * 30);
			log.info("[Process] Created Security List:" + insname + "_seclist");
			
			//创建安全规则
			log.info("[Process] Creating Security Rule:" + insname + "_secrule");
			SecurityAPI.createSecRule(m.get("createsecrule"), insname + "_secrule", insname + "_seclist");
			Thread.sleep(1000 * 30);
			log.info("[Process] Creating Security Rule:" + insname + "_secrule");
			
			//绑定安全组到计算实例
			log.info("[Process] Binding Security List:" + insname + "_secrule to " + insname);
			SecurityAPI.createSecAssociation(m.get("bindseclist"), vcableid, insname + "_seclist");
			Thread.sleep(1000 * 30);
			log.info("[Process] Bounded Security List:" + insname + "_secrule to " + insname);
			
			//SSH 远程连接到VM
			pk = FileUtils.readFileToString(new File("D:\\job\\OTASK\\Oracle Cloud\\近期项目\\Others\\sshkeybundle\\privateKey"));
			
			if (StringUtils.isBlank(publicip))
				passable = false;
				
			if (!passable) {
				respMsg = "[ERROR] The instance have not public ip, please contact to administration.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				return resp;
			}
			log.info("[Process] SSH Check to instance:" + insname + "[" + publicip + "]");
			boolean _re = sshcheck(insname, publicip, pk);
			if (!_re)
				passable = false;
			if (!passable) {
				respMsg = "[ERROR] The instance ip [" + publicip + "] can't connect by ssh, would you provide a correct private ssh key? Please contact to administration.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				return resp;
			}
			log.info("[Process] Pass checked ssh to instance:" + insname + "[" + publicip + "]");
			
			log.info("[Process] Execute Oracle Database Installation Script");
			_re = initVM(insname, publicip, pk);
			if (!_re)
				passable = false;
			if (!passable) {
				respMsg = "[ERROR] The VM instance init failed and oracle database can't install, Please contact to administration.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				return resp;
			}			
			log.info("[Process] Installation Done");
			
			log.info("[Process] Checking Oracle Database whether is can connect");
			times = 0;
			do {
				times ++;
				Thread.sleep(1000 * 30);
				log.debug("[Process] ========================================");
				log.debug("[Process] Public IP	：" + publicip);
				log.debug("[Process] Port		：" + "1521");
				log.debug("[Process] SID		：" + "orcl11g");
				log.debug("[Process] Username	：" + "system");
				log.debug("[Process] Password	：" + "passW0RD");
				log.debug("[Process] ========================================");
				boolean _isdbok = OracleJDBCTools.checkConnection(publicip, "1521", "orcl11g", "system", "passW0RD");
				if (!_isdbok) {
					passable = false;
				} else {
					passable = true;
				}
			} while ((!passable) && (times < RETRY));
			
			if (!passable) {
				respMsg = "[ERROR] The database can't connect by jdbc, would you check by ssh connection and run sqlplus / as sysdba? Please contact to administration.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				return resp;
			}
			log.info("[Process] Oracle Database passed check");
			System.out.println("    _    _     _       ____\n   / \\  | |   | |     |  _ \\  ___  _ __   ___\n  / _ \\ | |   | |     | | | |/ _ \\| '_ \\ / _ \\\n / ___ \\| |___| |___  | |_| | (_) | | | |  __/\n/_/   \\_\\_____|_____| |____/ \\___/|_| |_|\\___|");
			log.info("=================== Module[OracleDatabaseAutoDeployRespEntity] all done event.========================");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("=================== Module[OracleDatabaseAutoDeployRespEntity] event has failed.========================");
			resp.setRespMsg(e.getMessage());
		} finally {
			return resp;
		}
	}

	private boolean initVM(String insname, String publicip, String privatekey) throws Exception {
		Shell shell = new Ssh(publicip, 22, "opc", privatekey);
		Plain ap = new Shell.Plain(shell);
		ap.exec("sudo -s");
//		ap.exec("curl -O https://em2.storage.oraclecloud.com/v1/Storage-gse00002004/shared/scripts/DB12cR1/vm_init_db12cR1_BiuBiu.sh");
		ap.exec("curl -O https://em2.storage.oraclecloud.com/v1/Storage-gse00002004/shared/scripts/DB11gR2/vm_init_db11gR2_BiuBiu.sh");
		ap.exec("chmod +x ./vm_init_db11gR2_BiuBiu.sh");
//		int exitValue = shell.exec("sudo sh /home/opc/vm_init_db11gR2_BiuBiu.sh", System.in, System.out, System.err);
		String exitValue = ap.exec("sudo sh /home/opc/vm_init_db11gR2_BiuBiu.sh");
		if (exitValue.indexOf("Deploying Oracle...done.") > 0)
			return true;
		return false;
	}

	private boolean sshcheck(String insname, String publicip, String privatekey) throws Exception {
		Shell shell = new Ssh(publicip, 22, "opc", privatekey);
		String stdout = new Shell.Plain(shell).exec("echo passed");
		if ("passed".equals(stdout.trim()))
			return true;
		return false;
	}

	private boolean verifyVolumnAttachment(JSONObject stjson1, JSONObject stjson2, JSONObject stjson3, JSONObject stjson4) throws Exception {
		JSONObject tt1 = StorageAPI.listStorageAttachment(m.get("list_storage_attachment"));
		JSONArray ary = tt1.getJSONArray("result");
		boolean yes1 = false;
		boolean yes2 = false;
		boolean yes3 = false;
		boolean yes4 = false;
		for (Object object : ary) {
			org.json.JSONObject obj666 = (org.json.JSONObject) object;
			String stoname = obj666.optString("storage_volume_name");
			String stostate = obj666.optString("state");
			if ((stoname.equals(stjson1.optString("name"))) && (stostate.equals("attached"))) {
				yes1 = true;
			}
			if ((stoname.equals(stjson2.optString("name"))) && (stostate.equals("attached"))) {
				yes2 = true;
			}
			if ((stoname.equals(stjson3.optString("name"))) && (stostate.equals("attached"))) {
				yes3 = true;
			}
			if ((stoname.equals(stjson4.optString("name"))) && (stostate.equals("attached"))) {
				yes4 = true;
			}
		}
		if ((yes1) && (yes2) && (yes3) && (yes4))
			return true;
		return false;
	}
	
	private boolean verifyVolumnAttachment(JSONObject stjson1) throws Exception {
		JSONObject tt1 = StorageAPI.listStorageAttachment(m.get("list_storage_attachment"));
		JSONArray ary = tt1.getJSONArray("result");
		boolean yes1 = false;
		for (Object object : ary) {
			org.json.JSONObject obj666 = (org.json.JSONObject) object;
			String stoname = obj666.optString("storage_volume_name");
			String stostate = obj666.optString("state");
			if ((stoname.equals(stjson1.optString("name"))) && (stostate.equals("attached"))) {
				yes1 = true;
			}
		}
		if (yes1)
			return true;
		return false;
	}	

	private String getInstanceName(JSONObject obj2) {
		JSONArray ary = obj2.getJSONArray("instances");
		for (Object object : ary) {
			JSONObject t1 = (JSONObject) object;
			return t1.getString("name");
		}
		return null;
	}
	
	private String getInstanceVCable(String finalname) throws Exception {
		JSONObject p = ComputeAPI.viewComputes(m.get("viewcompute"), BiuUtils.kvNULL(finalname, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"));
		String vcableid = p.optString("vcable_id");
		vcableid = BiuUtils.kvNULL(vcableid, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
		return vcableid;
	}

	private String getInstancePublicIP(JSONObject obj2) {
		if (null != obj2)
			return obj2.optString("ip");
		return null;
	}	

	private boolean verifyStorageVolumnStatus(JSONObject stjson1) throws Exception {
		JSONObject resp = StorageAPI.viewStorageVolumns(m.get("viewstoragevolumns"), BiuUtils.kvNULL(stjson1.getString("name"), BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"));
		if ("Online".equals(resp.getString("status")))
			return true;
		return false;
	}

	/**
	 * 检查计算实例是否已经创建
	 * @param insname
	 * @return
	 * @throws Exception
	 */
	private boolean verifyInstanceName(String insname) throws Exception {
		boolean hasInstance = false;
		JSONObject obj3 = ComputeAPI.listComputes(m.get("computeinstances"));
		JSONArray ary3 = obj3.getJSONArray("result");
		for (Object t_obj2 : ary3) {
			org.json.JSONObject t_jsonobj2 = (org.json.JSONObject) t_obj2;
			String tmp_name = t_jsonobj2.getString("name");
			tmp_name = BiuUtils.kv(tmp_name, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/", "");
			if (tmp_name.trim().startsWith(insname)) {
				hasInstance = true;
			}
		}
		return hasInstance;
	}

	@Override
	public OracleDatabaseAutoDeployRespEntity query(String...params) {
		return null;
	}
	
	public static void main(String[] args) {
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
//		deploy.init();
//		deploy.deploy("zhiqiang", "", "biuins3", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "");
		try {
			String pk = FileUtils.readFileToString(new File("D:\\job\\OTASK\\Oracle Cloud\\近期项目\\Others\\sshkeybundle\\privateKey"));
//			deploy.initVM("biuins3", "140.86.12.56", pk);
			boolean _isdbok = OracleJDBCTools.checkConnection("140.86.39.9", "1521", "orcl12c", "system", "passW0RD");
			System.out.println(_isdbok);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
