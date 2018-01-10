package com.oracle.cloud.biu.modules.biubiu.shell;

import java.io.File;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.oracle.cloud.biu.Biu;
import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.api.CommonAPI;
import com.oracle.cloud.biu.api.ComputeAPI;
import com.oracle.cloud.biu.api.NetworkAPI;
import com.oracle.cloud.biu.api.SecurityAPI;
import com.oracle.cloud.biu.api.StorageAPI;
import com.oracle.cloud.biu.modules.biubiu.BaseModule;
import com.oracle.cloud.biu.modules.biubiu.RespEntity;
import com.oracle.cloud.biu.modules.biubiu.XEvent;
import com.oracle.cloud.biu.modules.biubiu.XTransaction;
import com.oracle.cloud.biu.modules.biubiu.project.citic.Biu_CITIC_SDK;
import com.oracle.cloud.biu.modules.biubiu.project.citic.Biu_CITIC_SDK_Desc;
import com.oracle.cloud.biu.modules.biubiu.shell.patch.OracleJDBCTools;
import com.oracle.cloud.biu.modules.biubiu.ssh.Shell;
import com.oracle.cloud.biu.modules.biubiu.ssh.Shell.Plain;
import com.oracle.cloud.biu.modules.biubiu.ssh.Ssh;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
//Deprecated
public class OracleDatabaseAutoDeploy extends BaseModule {

	private static final int RETRY = 10;

	String plugin = "false";
	String sshkeyname = "";
	String sshkeycontent = "";
	String insname = "";
	String shape = "";
	String os = "";
	String version = "";
	String tenant = "";
	String pk = "";
	String xid = "";
	String volumnsize = "";
	String dbpassword = "";
	String dbcharset = "";
	String dbsid = "";
	String oracledblisport = "";
	String emport = "";
	String price1hour = "";
	String autobackup = "false";
	String citicinstanceid = "";

	public OracleDatabaseAutoDeploy(String... params) {
		sshkeyname = params[0];
		sshkeycontent = params[1];
		insname = params[2];
		shape = params[3];
		os = params[4];
		version = params[5];
		tenant = params[6];
		pk = params[7];
		xid = params[8];
		volumnsize = params[9];
		dbpassword = params[10];
		dbcharset = params[11];
		dbsid = params[12];
		dbsid = dbsid.toUpperCase();
		oracledblisport = "1521";
		// emport = "5500";
		price1hour = params[15];
		autobackup = params[16];
		citicinstanceid = params[17];
	}

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
	 * 此方法实现虚拟机一键供应，必须按照规则指定参数表，方法调用前，参数必须经过非空检查，并且trim params: 1 = sshkey名称 2 =
	 * sshkey公钥字符串全文 3 = 实例名 4 = shape 5 = 操作系统定义串 6 = 数据库版本 7 = 租户名 8 = 私钥内容
	 */
	@SuppressWarnings("finally")
	@Override
	public OracleDatabaseAutoDeployRespEntity deploy() {
		if (!"true".equals(plugin))
			init();
		System.out.println(
				" ____  _       ____  _\n| __ )(_)_   _| __ )(_)_   _\n|  _ \\| | | | |  _ \\| | | | |\n| |_) | | |_| | |_) | | |_| |\n|____/|_|\\__,_|____/|_|\\__,_|");
		log.debug(
				"=================== Module[OracleDatabaseAutoDeployRespEntity] is received start event.========================");
		// 将获取到的参数传给临时变量，用户检测参数合法性

		XTransaction xtranc = new XTransaction();
		log.info("[Process] XID:" + xid);
		// 将程序是否可以继续执行做一个标记，标记为false的不允许继续执行
		OracleDatabaseAutoDeployRespEntity resp = new OracleDatabaseAutoDeployRespEntity();
		xtranc.setXid(xid);
		Biu_CITIC_SDK_Desc desc = Biu_CITIC_SDK.xmap.get(xid);
		try {
			//
			// 遍历已有的所有ssh列表，若已经存在的sshkey name不允许创建，模糊匹配原则，宁可错杀不可放过。
			log.info("[Process] Listing SSH Keys");
			desc.setXstatus("正在检查SSH Keys");
			desc.setXmessage("processing");
			desc.setTenant(tenant);
			desc.setOs(os);
			desc.setShape(shape);
			Biu_CITIC_SDK.xmap.put(xid, desc);

			boolean passable = true;
			String respMsg = "";
			boolean hasExistsKey = false;
			String finalinsname;
			String vcableid;
			String publicip = "";
			String privateip = "";
			String dbconsoleurl = "";
			int times = 0;

			// 检查系统资源配额
			log.info("[Process] Checking System Resources Quota");
			desc.setXstatus("正在检查系统资源配额");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			passable = checkingSystemQuota();
			if (!passable) {
				log.error("[ERROR] System Resources Quota is not enough, please contant to administrator.");
				respMsg = "[ERROR] System Resources Quota is not enough, please contant to administrator.";
				resp.setRespMsg(respMsg);
				desc.setXstatus("系统资源已达使用上限");
				desc.setXmessage("error");
				desc.setXlog(respMsg);
				Biu_CITIC_SDK.xmap.put(xid, desc);
				return resp;
			}

			// 检查SSH Key是否已经存在
			JSONObject obj = SecurityAPI.listSSHKeys(m.get("sshkeys"));
			JSONArray ary1 = obj.getJSONArray("result");
			for (Object object : ary1) {
				JSONObject t1 = (JSONObject) object;
				String tmp_name = t1.getString("name");
				// keep the field [tmp_enabled] current status
				boolean tmp_enabled = t1.getBoolean("enabled");

				tmp_name = BiuUtils.kvNULL(tmp_name,
						BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");

				if (tmp_name.trim().equals(sshkeyname)) {
					hasExistsKey = true;
				}
			}

			log.info("[Process] SSH Keys check result:" + hasExistsKey);
			if (!hasExistsKey) {
				// 没有存在的SSH Key需要创建
				log.warn("[WARN] The sshkey name " + sshkeyname
						+ " is not exists in system, will create a new sshkey pair in system.");
				if (StringUtils.isBlank(sshkeycontent)) {
					respMsg = "[ERROR] The sshkey name " + sshkeyname
							+ " is not exists in system, but the public sshkey content is NULL.";
					log.error(respMsg);
					passable = false;
				} else {
					log.info("[Process] Creating SSH Key:" + sshkeyname);
					SecurityAPI.createSSHKey(m.get("createsshkey"), sshkeyname, sshkeycontent);
					times = 0;
					do {
						times++;
						Thread.sleep(30000);
						JSONObject obj2 = SecurityAPI.listSSHKeys(m.get("sshkeys"));
						JSONArray ary2 = obj2.getJSONArray("result");
						boolean sechasExistsKey = false;
						for (Object object : ary2) {
							JSONObject t1 = (JSONObject) object;
							String tmp_name = t1.getString("name");
							// keep the field [tmp_enabled] current status
							boolean tmp_enabled = t1.getBoolean("enabled");
							tmp_name = BiuUtils.kvNULL(tmp_name, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/"
									+ BasicAuthenticationAPI.CLOUD_USERNAME + "/");
							if (tmp_name.trim().equals(sshkeyname)) {
								sechasExistsKey = true;
							}
						}
						if (!sechasExistsKey) {
							respMsg = "[ERROR] The sshkey name " + sshkeyname
									+ " can not be created, please check the ssh public key content.";
							log.error(respMsg);
							passable = false;
						}
					} while ((!passable) && (times < RETRY));
				}
			}
			if ((!passable) && (!StringUtils.isBlank(respMsg))) {
				resp.setRespMsg(respMsg);
				desc.setXstatus("SSH Keys检查失败");
				desc.setXmessage("error");
				desc.setXlog(respMsg);
				Biu_CITIC_SDK.xmap.put(xid, desc);
				return resp;
			}
			if (!hasExistsKey)
				xtranc.getEvents().add(new XEvent("key", sshkeyname));
			log.info("[Process] SSH Key loaded:" + sshkeyname);

			// 创建计算实例并验证实例已经运行
			log.info("[Process] Check Instance Name:" + insname);
			desc.setXstatus("正在检查计算实例");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			boolean hasInstance = verifyInstanceName(insname, tenant);
			if (hasInstance) {
				respMsg = "[ERROR] The instance name " + insname + " is exists, please change to another one.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				desc.setXstatus("计算实例名已经存在，请更换实例名");
				desc.setXmessage("error");
				rollback(xtranc);
				Biu_CITIC_SDK.xmap.put(xid, desc);
				return resp;
			}
			log.info("[Process] Passed Check Instance Name:" + insname);
			log.info("[Process] Creating Instance:" + insname);
			desc.setXstatus("正在创建计算实例");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);

			JSONObject obj2 = ComputeAPI.createComputeInstance(m.get("createcompute"), insname, shape, os, sshkeyname,
					tenant);
			finalinsname = getInstanceName(obj2);
			boolean insRunning = false;
			passable = false;
			do {
				times++;
				Thread.sleep(1000 * 90);
				JSONObject obj3 = ComputeAPI.listComputes(m.get("computeinstances"));
				JSONArray ary3 = obj3.getJSONArray("result");
				for (Object t_obj2 : ary3) {
					org.json.JSONObject t_jsonobj2 = (org.json.JSONObject) t_obj2;
					String tmp_name = t_jsonobj2.getString("name");
					String state = t_jsonobj2.getString("state");
					String tmp_tenant = "";
					JSONArray t2 = t_jsonobj2.optJSONArray("tags");
					if (t2.length() > 0)
						tmp_tenant = t2.optString(1);

					tmp_name = BiuUtils.kvNULL(tmp_name,
							BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
					if ((tmp_name.trim().startsWith(insname))
							&& (state.equals("running") && (tmp_tenant.trim().equals(tenant)))) {
						// 实例已经创建，并且运行正常
						passable = true;
					}
				}
			} while ((!passable) && (times < RETRY));

			if (!passable) {
				respMsg = "[ERROR] The instance created failed, please try it later or contact to the administrator.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				desc.setXstatus("计算实例创建失败，请联系管理员解决");
				desc.setXmessage("error");
				desc.setXlog(respMsg);
				rollback(xtranc);
				Biu_CITIC_SDK.xmap.put(xid, desc);
				return resp;
			}
			xtranc.getEvents().add(new XEvent("instance", finalinsname));
			vcableid = getInstanceVCable(finalinsname);
			log.info("[Process] Created Instance:" + insname + " and instance is running now");
			desc.setXstatus("计算实例已创建且正在运行中");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);

			log.info("[Process] Creating Volumns 1");
			// 计算实例已经成功创建，现在创建1个存储盘
			desc.setXstatus("正在创建存储卷");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			JSONObject stjson1 = StorageAPI.createStorageBlankVolumns(m.get("create_storage"), volumnsize, tenant);
			// JSONObject stjson2 =
			// StorageAPI.createStorageBlankVolumns(m.get("create_storage"),
			// "100");
			// JSONObject stjson3 =
			// StorageAPI.createStorageBlankVolumns(m.get("create_storage"),
			// "8");
			// JSONObject stjson4 =
			// StorageAPI.createStorageBlankVolumns(m.get("create_storage"),
			// "50");

			// 检查存储盘运行状态
			times = 0;
			do {
				times++;
				Thread.sleep(1000 * 10);
				boolean ret1 = verifyStorageVolumnStatus(stjson1);
				// boolean ret2 = verifyStorageVolumnStatus(stjson2);
				// boolean ret3 = verifyStorageVolumnStatus(stjson3);
				// boolean ret4 = verifyStorageVolumnStatus(stjson4);
				// if ((ret1) && (ret2) && (ret3) && (ret4)) {
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
				desc.setXstatus("存储卷创建失败");
				desc.setXmessage("error");
				desc.setXlog(respMsg);
				rollback(xtranc);
				Biu_CITIC_SDK.xmap.put(xid, desc);
				return resp;
			}
			log.info("[Process] Created Volumns 1");
			xtranc.getEvents().add(new XEvent("volumn", BiuUtils.kvNULL(stjson1.getString("name"),
					BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/")));

			log.info("[Process] Mount Volumns 1 to instance:" + insname);
			// 存储盘全部online，现在开始挂接存储盘到计算实例
			StorageAPI.createStorageAttachments(m.get("attach_storage"),
					BiuUtils.kvNULL(stjson1.getString("name"),
							BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"),
					BiuUtils.kvNULL(finalinsname,
							BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"),
					"1");
			// StorageAPI.createStorageAttachments(m.get("attach_storage"),
			// BiuUtils.kvNULL(stjson2.getString("name"),
			// BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" +
			// BasicAuthenticationAPI.CLOUD_USERNAME + "/"),
			// BiuUtils.kvNULL(finalinsname,
			// BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" +
			// BasicAuthenticationAPI.CLOUD_USERNAME + "/"), "2");
			// StorageAPI.createStorageAttachments(m.get("attach_storage"),
			// BiuUtils.kvNULL(stjson3.getString("name"),
			// BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" +
			// BasicAuthenticationAPI.CLOUD_USERNAME + "/"),
			// BiuUtils.kvNULL(finalinsname,
			// BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" +
			// BasicAuthenticationAPI.CLOUD_USERNAME + "/"), "3");
			// StorageAPI.createStorageAttachments(m.get("attach_storage"),
			// BiuUtils.kvNULL(stjson4.getString("name"),
			// BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" +
			// BasicAuthenticationAPI.CLOUD_USERNAME + "/"),
			// BiuUtils.kvNULL(finalinsname,
			// BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" +
			// BasicAuthenticationAPI.CLOUD_USERNAME + "/"), "4");

			// 检查存储盘挂接状态
			// 检查存储盘运行状态
			times = 0;
			do {
				times++;
				Thread.sleep(1000 * 5);
				boolean ret1 = verifyVolumnAttachment(stjson1);
				// boolean ret1 = verifyVolumnAttachment(stjson1, stjson2,
				// stjson3, stjson4);
				if (!ret1) {
					passable = false;
				} else {
					passable = true;
				}
			} while ((!passable) && (times < RETRY));
			// 存储盘全部已经挂载

			if (!passable) {
				respMsg = "[ERROR] The volumns 1 mounted failed, please try it later or contact to the administrator.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				desc.setXstatus("存储卷挂载失败，请联系管理员");
				desc.setXmessage("error");
				desc.setXlog(respMsg);
				Biu_CITIC_SDK.xmap.put(xid, desc);
				rollback(xtranc);
				return resp;
			}
			log.info("[Process] Mounted Volumns 1 to instance:" + insname);
			desc.setXstatus("存储卷已挂载");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);

			// 将计算实例分配固定IP
			log.info("[Process] Creating IP Reservation for instance:" + insname);
			JSONObject resp2 = NetworkAPI.createSharedRandomIP(m.get("create_randomip"), vcableid, tenant);
			publicip = getInstancePublicIP(resp2);
			Thread.sleep(1000 * 30);
			log.info("[Process] Created IP Reservation for instance:" + insname);
			desc.setXstatus("已分配公共IP");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);

			// 创建安全组
			log.info("[Process] Creating Security List:" + insname + "_seclist");
			JSONObject jsonseclist = SecurityAPI.createSecList(m.get("createseclist"), insname + "_seclist", "permit",
					"permit");
			Thread.sleep(1000 * 30);
			log.info("[Process] Created Security List:" + insname + "_seclist");
			desc.setXstatus("已创建安全组");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			xtranc.getEvents().add(new XEvent("secgroup", BiuUtils.kvNULL(jsonseclist.getString("name"),
					BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/")));

			// 创建安全规则
			log.info("[Process] Creating Security Rule:" + insname + "_secrule");
			JSONObject jsonsecrule = SecurityAPI.createSecRule(m.get("createsecrule"), insname + "_secrule",
					insname + "_seclist");
			Thread.sleep(1000 * 30);
			log.info("[Process] Creating Security Rule:" + insname + "_secrule");
			desc.setXstatus("已创建安全规则");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			xtranc.getEvents().add(new XEvent("secrule", BiuUtils.kvNULL(jsonsecrule.getString("name"),
					BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/")));

			// 绑定安全组到计算实例
			log.info("[Process] Binding Security List:" + insname + "_secrule to " + insname);
			SecurityAPI.createSecAssociation(m.get("bindseclist"), vcableid, insname + "_seclist");
			Thread.sleep(1000 * 30);
			log.info("[Process] Bounded Security List:" + insname + "_secrule to " + insname);
			desc.setXstatus("已绑定安全组到计算实例");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);

			// SSH 远程连接到VM
			pk = FileUtils
					.readFileToString(new File("D:\\job\\OTASK\\Oracle Cloud\\近期项目\\Others\\sshkeybundle\\privateKey"));

			if (StringUtils.isBlank(publicip))
				passable = false;

			if (!passable) {
				respMsg = "[ERROR] The instance have not public ip, please contact to administration.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				desc.setXstatus("计算实例没有分配到公有IP，实例创建失败");
				desc.setXmessage("error");
				desc.setXlog(respMsg);
				Biu_CITIC_SDK.xmap.put(xid, desc);
				rollback(xtranc);
				return resp;
			}
			desc.setXstatus("正在检查SSH计算实例连通性");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			log.info("[Process] SSH Check to instance:" + insname + "[" + publicip + "]");
			boolean _re = sshcheck(insname, publicip, pk);
			if (!_re)
				passable = false;
			if (!passable) {
				respMsg = "[ERROR] The instance ip [" + publicip
						+ "] can't connect by ssh, would you provide a correct private ssh key? Please contact to administration.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				desc.setXstatus("计算实例无法ssh连通");
				desc.setXmessage("error");
				Biu_CITIC_SDK.xmap.put(xid, desc);
				rollback(xtranc);
				return resp;
			}
			log.info("[Process] Passed check ssh to instance:" + insname + "[" + publicip + "]");
			desc.setXstatus("计算实例已通过ssh检测");
			desc.setXmessage("processing");
			privateip = getInstancePrivateIP(finalinsname);
			desc.setPrvip(privateip);
			desc.setPubip(publicip);
			desc.setInstancename(insname);
			desc.setShape(dbconsoleurl);
			Biu_CITIC_SDK.xmap.put(xid, desc);

			desc.setXstatus("正在安装Oracle数据库");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			log.info("[Process] Execute Oracle Database Installation Script");
			_re = initVM(insname, publicip, pk, dbpassword, oracledblisport, dbcharset, dbsid, version, emport);
			if (!_re)
				passable = false;
			if (!passable) {
				respMsg = "[ERROR] The VM instance init failed and oracle database can't install, Please contact to administration.";
				log.error(respMsg);
				passable = false;
				resp.setRespMsg(respMsg);
				desc.setXstatus("数据库安装失败");
				desc.setXmessage("error");
				desc.setXlog(respMsg);
				Biu_CITIC_SDK.xmap.put(xid, desc);
				rollback(xtranc);
				return resp;
			}
			log.info("[Process] Installation Done");
			desc.setXstatus("Oracle数据库安装成功");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);

			log.info("[Process] Checking Oracle Database whether is can connect");
			times = 0;
			do {
				times++;
				Thread.sleep(1000 * 30);
				log.debug("[Process] ========================================");
				log.debug("[Process] Public IP	：" + publicip);
				log.debug("[Process] Port		：" + oracledblisport);
				log.debug("[Process] SID		：" + dbsid);
				log.debug("[Process] Username	：" + "system");
				log.debug("[Process] Password	：" + dbpassword);
				log.debug("[Process] ========================================");
				desc.setXstatus("正在检查数据库健康状态");
				desc.setXmessage("processing");
				Biu_CITIC_SDK.xmap.put(xid, desc);
				boolean _isdbok = OracleJDBCTools.checkConnection(publicip, oracledblisport, dbsid, "system",
						dbpassword, true);
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
				desc.setXstatus("数据库无法通过JDBC检查");
				desc.setXmessage("error");
				Biu_CITIC_SDK.xmap.put(xid, desc);
				return resp;
			}
			desc.setXstatus("All Done");
			desc.setXmessage("All Done");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			log.info("[Process] Oracle Database passed check");
			buildResp(xid, desc, insname, publicip, privateip, oracledblisport, dbsid, dbpassword, dbcharset,
					dbconsoleurl, emport, version);
			System.out.println(
					"    _    _     _       ____\n   / \\  | |   | |     |  _ \\  ___  _ __   ___\n  / _ \\ | |   | |     | | | |/ _ \\| '_ \\ / _ \\\n / ___ \\| |___| |___  | |_| | (_) | | | |  __/\n/_/   \\_\\_____|_____| |____/ \\___/|_| |_|\\___|");
			log.info(
					"=================== Module[OracleDatabaseAutoDeployRespEntity] all done event.========================");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(
					"=================== Module[OracleDatabaseAutoDeployRespEntity] event has failed.========================");
			desc.setXstatus("执行过程出错");
			desc.setXmessage("error");
			desc.setXlog(e.getMessage());
			Biu_CITIC_SDK.xmap.put(xid, desc);
			rollback(xtranc);
			resp.setRespMsg(e.getMessage());
		} finally {
			return resp;
		}
	}

	// 检查资源使用情况
	private boolean checkingSystemQuota() throws Exception {
		JSONObject jsona = CommonAPI.summary(m.get("summary"), "all");
		JSONObject jsonb = jsona.optJSONObject("biureturn");
		int used_ocpu = jsonb.optInt("used_ocpu");
		int used_mem = jsonb.optInt("used_mem");
		int used_volumn = jsonb.optInt("used_blockvolumn");
		String res1 = Biu.SHAPEMAP.get(shape);

		int new_ocpu = 0;
		int new_mem = 0;
		if (null != res1) {
			String[] cpu_mem = res1.split("_");
			new_ocpu += Integer.parseInt(cpu_mem[0]);
			new_mem += Integer.parseInt(cpu_mem[1]);
		}
		boolean allow = true;
		log.info("[Resources Check] Start Resources Usage and Quota Check");
		if ((Biu.QUOTA_OCPU - used_ocpu) < new_ocpu) {
			allow = false;
		}
		log.info("[Resources Check] OCPU QUOTA[" + Biu.QUOTA_OCPU + "], Current Usage[" + used_ocpu + "], New Request["
				+ new_ocpu + "]");
		if ((Biu.QUOTA_MEM - used_mem) < new_mem) {
			allow = false;
		}
		log.info("[Resources Check] Memory QUOTA[" + Biu.QUOTA_MEM + "], Current Usage[" + used_mem + "], New Request["
				+ new_mem + "]");		
		if ((Biu.QUOTA_VOLUMN - used_volumn) < Integer.parseInt(volumnsize)) {
			allow = false;
		}
		log.info("[Resources Check] Volumn QUOTA[" + Biu.QUOTA_VOLUMN + "], Current Usage[" + used_volumn + "], New Request["
				+ volumnsize + "]");		
		return allow;
	}

	@Override
	public void run() {
		deploy();
	}

	private void buildResp(String xid, Biu_CITIC_SDK_Desc desc, String insname, String publicip, String privateip,
			String oracledblisport, String dbsid, String dbpassword, String dbcharset, String dbconsoleurl,
			String emport, String dbversion) {
		desc.setInstancename(insname);
		desc.setTenant(tenant);
		desc.setOracledblisport(oracledblisport);
		desc.setOracledbsid(dbsid);
		desc.setOracledbcharset(dbcharset);
		desc.setPrvip(privateip);
		desc.setPubip(publicip);
		if (dbversion.equals("OracleDatabase12cR1")) {
			dbconsoleurl = "https://<public_ip>:5500/em";
		} else {
			dbconsoleurl = "http://<public_ip>:1158/em";
		}
		dbconsoleurl = dbconsoleurl.replaceAll("<public_ip>", publicip);
		desc.setOracledbconsoleurl(dbconsoleurl);
		Biu_CITIC_SDK.xmap.put(xid, desc);
	}

	// AL32UTF8
	private boolean initVM(String insname, String publicip, String privatekey, String dbpassword, String dblistenport,
			String charset, String dbsid, String dbversion, String emport) throws Exception {
		Shell shell = new Ssh(publicip, 22, "opc", privatekey);
		Plain ap = new Shell.Plain(shell);
		ap.exec("sudo -s");
		String exitValue = "";
		int exitCode;
		if (dbversion.equals("OracleDatabase12cR1")) {
			ap.exec("curl -O https://em2.storage.oraclecloud.com/v1/Storage-gse00002004/shared/database/scripts/DB12cR1/vm_init_db12cR1_BiuBiu.sh");
			ap.exec("chmod +x ./vm_init_db12cR1_BiuBiu.sh");
			exitValue = ap.exec("sudo sh /home/opc/vm_init_db12cR1_BiuBiu.sh " + dbpassword + " 1521 "
					+ charset + " " + dbsid + " " + "5500");
			// exitCode = shell.exec("sudo sh
			// /home/opc/vm_init_db12cR1_BiuBiu.sh " + dbpassword + " " +
			// dblistenport + " " + charset + " " + dbsid + " " + emport,
			// System.in, System.out, System.err);
		} else {
			ap.exec("curl -O https://em2.storage.oraclecloud.com/v1/Storage-gse00002004/shared/database/scripts/DB11gR2/vm_init_db11gR2_BiuBiu.sh");
			ap.exec("chmod +x ./vm_init_db11gR2_BiuBiu.sh");
			exitValue = ap.exec("sudo sh /home/opc/vm_init_db11gR2_BiuBiu.sh " + dbpassword + " 1521 "
					+ charset + " " + dbsid + " " + "1521");
			// exitCode = shell.exec("sudo sh
			// /home/opc/vm_init_db11gR2_BiuBiu.sh " + dbpassword + " " +
			// dblistenport + " " + charset + " " + dbsid + " " + emport,
			// System.in, System.out, System.err);
		}
		if (exitValue.indexOf("Deploying Oracle...done.") > 0)
			return true;
		return false;
	}

	private boolean sshcheck(String insname, String publicip, String privatekey) throws Exception {
		try {
			Shell shell = new Ssh(publicip, 22, "opc", privatekey);
			String stdout = new Shell.Plain(shell).exec("echo passed");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean verifyVolumnAttachment(JSONObject stjson1, JSONObject stjson2, JSONObject stjson3,
			JSONObject stjson4) throws Exception {
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
		JSONObject p = ComputeAPI.viewComputes(m.get("viewcompute"), BiuUtils.kvNULL(finalname,
				BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"));
		String vcableid = p.optString("vcable_id");
		vcableid = BiuUtils.kvNULL(vcableid,
				BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
		return vcableid;
	}

	private String getInstancePrivateIP(String finalname) throws Exception {
		JSONObject p = ComputeAPI.viewComputes(m.get("viewcompute"), BiuUtils.kvNULL(finalname,
				BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"));
		String ip = p.optString("ip");
		return ip;
	}

	private String getInstancePublicIP(JSONObject obj2) {
		if (null != obj2)
			return obj2.optString("ip");
		return null;
	}

	private boolean verifyStorageVolumnStatus(JSONObject stjson1) throws Exception {
		JSONObject resp = StorageAPI.viewStorageVolumns(m.get("viewstoragevolumns"),
				BiuUtils.kvNULL(stjson1.getString("name"),
						BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"));
		if ("Online".equals(resp.getString("status")))
			return true;
		return false;
	}

	/**
	 * 检查计算实例是否已经创建
	 * 
	 * @param insname
	 * @return
	 * @throws Exception
	 */
	private boolean verifyInstanceName(String insname, String tenant) throws Exception {
		boolean hasInstance = false;
		JSONObject obj3 = ComputeAPI.listComputes(m.get("computeinstances"));
		JSONArray ary3 = obj3.getJSONArray("result");
		for (Object t_obj2 : ary3) {
			org.json.JSONObject t_jsonobj2 = (org.json.JSONObject) t_obj2;
			String tmp_name = t_jsonobj2.getString("name");
			String tmp_tenant = "";
			JSONArray t2 = t_jsonobj2.optJSONArray("tags");
			if (t2.length() > 0)
				tmp_tenant = t2.optString(1);
			tmp_name = BiuUtils.kv(tmp_name,
					BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/", "");
			System.out.println("tmp_name:" + tmp_name);
			System.out.println("tenant:" + tenant);
			if ((tmp_name.trim().startsWith(insname)) && (tmp_tenant.trim().equals(tenant))) {
				hasInstance = true;
			}
		}
		return hasInstance;
	}

	@Override
	public OracleDatabaseAutoDeployRespEntity query(String... params) {
		return null;
	}

	public static void main(String[] args) {
		// OracleDatabaseAutoDeploy deploy = new
		// OracleDatabaseAutoDeploy("zhiqiang", "", "biuins4", "oc1m",
		// "/oracle/public/OL_7.2_UEKR4_x86_64", "OracleDatabase12cR1",
		// "zhiqiang", "", "123456789", "100", "Welcome1#", "ZHS16GBK",
		// "orclbiu", "1521", "5500", "5");
		// deploy.init();
		// deploy.deploy("zhiqiang", "", "biuins3", "oc4",
		// "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "");
		try {
			String pk = FileUtils
					.readFileToString(new File("D:\\job\\OTASK\\Oracle Cloud\\近期项目\\Others\\sshkeybundle\\privateKey"));
			// deploy.initVM("biuins3", "140.86.12.56", pk);
			boolean _isdbok = OracleJDBCTools.checkConnection("140.86.12.20", "1521", "ORCLBIU", "system", "Welcome1#");
			System.out.println(_isdbok);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public RespEntity rollback(XTransaction xtranc) {
		return null;
	}

	@Override
	public boolean bomb(String orchid) {
		return false;
	}

	@Override
	public boolean bomb(String orchid, boolean isall) {
		return false;
	}

}
