package com.oracle.cloud.biu.modules.biubiu.shell;

import java.io.File;
import java.net.UnknownHostException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.oracle.cloud.biu.Biu;
import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.api.CommonAPI;
import com.oracle.cloud.biu.api.ComputeAPI;
import com.oracle.cloud.biu.api.NetworkAPI;
import com.oracle.cloud.biu.api.StorageAPI;
import com.oracle.cloud.biu.modules.biubiu.BaseModule;
import com.oracle.cloud.biu.modules.biubiu.RespEntity;
import com.oracle.cloud.biu.modules.biubiu.XTransaction;
import com.oracle.cloud.biu.modules.biubiu.citic.Biu_CITIC_SDK;
import com.oracle.cloud.biu.modules.biubiu.citic.Biu_CITIC_SDK_Desc;
import com.oracle.cloud.biu.modules.biubiu.shell.patch.OracleJDBCTools;
import com.oracle.cloud.biu.modules.biubiu.ssh.Shell;
import com.oracle.cloud.biu.modules.biubiu.ssh.Ssh;
import com.oracle.cloud.biu.modules.biubiu.ssh.Shell.Plain;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class OracleBlockVolumnUpscale extends BaseModule {

	private static final int RETRY = 10;
	
	String xid = "";
	String instancefullname = "";
	String privatekey = "";
	String volumnname = "";
	Integer newvolumnsize = -1;
	String oraclesid = "";
	String oraclepwd = "";
	
	public OracleBlockVolumnUpscale(String xid, String instancefullname, String volumnname, String privatekey, Integer newvolumnsize, String oraclesid, String oraclepwd) {
		this.xid = xid;
		this.instancefullname = instancefullname;
		this.volumnname = volumnname;
		this.newvolumnsize = newvolumnsize;
		this.privatekey = privatekey;
		this.oraclesid = oraclesid;
		this.oraclepwd = oraclepwd;
	}
	
	@Override
	public void init() {
		try {
			authoritied();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		deploy();
	}
	
	@Override
	public RespEntity deploy() {
		boolean passable = true;
		try {
			privatekey = FileUtils
					.readFileToString(new File("D:\\job\\OTASK\\Oracle Cloud\\近期项目\\Others\\sshkeybundle\\privateKey"));
			
			log.info("[Process] XID:" + xid);
			Biu_CITIC_SDK_Desc desc = Biu_CITIC_SDK.xmap.get(xid);
			desc.setXstatus("正在检查OCC总体用量");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			passable = checkingSystemQuota();
			if (!passable) {
				log.error("[ERROR] System Resources Quota is not enough, please contant to administrator.");
				desc.setXstatus("系统资源已达使用上限");
				desc.setXmessage("error");
				desc.setXlog("[ERROR] System Resources Quota is not enough, please contant to administrator.");
				Biu_CITIC_SDK.xmap.put(xid, desc);				
				return null;
			}
			desc.setXstatus("正在关闭计算实例");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			int times = 1;
			JSONObject json1 = ComputeAPI.stopCompute(m.get("stopCompute"), instancefullname);
			do {
				log.warn("[Processing] Start to try shutdown the compute instance, try times[" + times + "].");
				times++;
				passable = checkInstanceStatus(m.get("viewcompute"), instancefullname, "shutdown");
				if (!passable)
					Thread.sleep(1000 * 10);
			} while ((!passable) && (times < 300));
			log.info("[Processing] Compute instance, trid times[" + times + "], has been shutdown status");
			desc.setXstatus("主机已安全关闭");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			
			times = 1;
			json1 = StorageAPI.updateStorageVolumns(m.get("update_storage_volumn"), volumnname, String.valueOf(newvolumnsize), null);
			do {
				log.warn("[Processing] Start to upsize the storage volumn, try times[" + times + "].");
				times++;
				passable = checkVolumnStatus(m.get("viewstoragevolumns"), volumnname);
				if (!passable)
					Thread.sleep(1000 * 20);
			} while ((!passable) && (times < 300));
			log.info("[Processing] Storage Volumn Upsized, trid times[" + times + "], has been online status");
			desc.setXstatus("存储卷已经扩容完成");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			
			times = 1;
			json1 = ComputeAPI.startCompute(m.get("startCompute"), instancefullname);
			do {
				log.warn("[Processing] Start to compute instance, try times[" + times + "].");
				times++;
				passable = checkInstanceStatus(m.get("viewcompute"), instancefullname, "running");
				if (!passable)
					Thread.sleep(1000 * 10);
			} while ((!passable) && (times < 300));
			log.info("[Processing] Compute instance started, trid times[" + times + "], has been running status");
			desc.setXstatus("主机已经安全启动");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			
			times = 1;
			String publicip = "";
			do {
				publicip = getInstancePublicIP(instancefullname);
				log.warn("[Processing] Start connecting to instance, try times[" + times + "].");
				passable = checkWaitingForSSH(publicip, privatekey, times, false);
				times ++;
				if (!passable)
					Thread.sleep(1000 * 10);
			} while ((!passable) && (times < 300));
			log.info("[Processing] SSH checked, trid times[" + times + "], passed the check");
			desc.setXstatus("SSH连接检测通过，结果[正常]");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			
			times = 1;
			do {
				log.warn("[Processing] Startup Database in instance, try times[" + times + "].");
				passable = startupOracleDatabase(publicip);
				times ++;
				if (!passable)
					Thread.sleep(1000 * 30);
			} while ((!passable) && (times < 300));
			log.info("[Processing] Oracle database startup, trid times[" + times + "], passed the check");
			desc.setXstatus("Oracle数据库已经安全启动");
			desc.setXmessage("processing");
			Biu_CITIC_SDK.xmap.put(xid, desc);
			
			times = 1;
			do {
				log.debug("[Process] ========================================");
				desc.setXstatus("正在检查数据库健康状态");
				desc.setXmessage("processing");
				Biu_CITIC_SDK.xmap.put(xid, desc);
				passable = OracleJDBCTools.checkConnection(publicip, "1521", oraclesid, "system", oraclepwd, true);
				times++;
				if (!passable)
					Thread.sleep(1000 * 30);
			} while ((!passable) && (times < RETRY));

			if (!passable) {
				log.error("[ERROR] The database can't connect by jdbc, would you check by ssh connection and run sqlplus / as sysdba? Please contact to administration.");
				passable = false;
				desc.setXstatus("数据库无法通过JDBC检查");
				desc.setXmessage("error");
				Biu_CITIC_SDK.xmap.put(xid, desc);
				return null;
			}
			
			desc.setXstatus("数据库通过JDBC检测，扩容完毕");
			desc.setXmessage("All Done");
			Biu_CITIC_SDK.xmap.put(xid, desc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//shutdown instance
		//upsize storage volumn
		//startup instance
		//check available
		//startup database
		//check jdbc connection
		return null;
	}
	
	public boolean startupOracleDatabase(String publicip) throws Exception {
		Shell shell = new Ssh(publicip, 22, "opc", privatekey);
		Plain ap = new Shell.Plain(shell);
		ap.exec("sudo -s");
//		int exitCode = shell.exec("sudo sh /u01/install/startupdb.sh", System.in, System.out, System.err);
		String exitCode = ap.exec("sudo sh /u01/install/startupdb.sh");
		return true;
	}
	
	public static void main(String[] args) {
		OracleBlockVolumnUpscale up = new OracleBlockVolumnUpscale(null, null, null, null, null, null, null);
		try {
			up.startupOracleDatabase("140.86.12.145");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkWaitingForSSH(String publicip, String pk, int times, boolean _re) {
		try {
			do {
				log.debug("Start[" + times + "] to try publicip");
				times++;
				Thread.sleep(1000 * 120);
				_re = sshcheck(publicip, pk);
			} while ((!_re) && (times < RETRY));
		} catch (Exception e) {
			try {
				Thread.sleep(1000 * 120);
			} catch (InterruptedException e1) {
			}
			checkWaitingForSSH(publicip, pk, times, _re);
		}
		return _re;
	}
	
	private boolean sshcheck(String publicip, String privatekey) {
		try {
			Shell shell = new Ssh(publicip, 22, "opc", privatekey);
			String stdout = new Shell.Plain(shell).exec("echo passed");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private String getInstancePublicIP(String finalname) throws Exception {
		JSONObject p = ComputeAPI.viewComputes(m.get("viewcompute"), BiuUtils.kvNULL(finalname,
				BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/"));

		JSONObject jsonobj = NetworkAPI.listNetworkAssociations(m.get("list_network_associations"));
		String publicip = "";
		JSONArray ary = jsonobj.getJSONArray("result");
		for (Object object : ary) {
			org.json.JSONObject obj = (org.json.JSONObject) object;
			String vcableid = obj.optString("vcable");
			if (p.optString("vcable_id").equals(vcableid)) {
				publicip = obj.optString("ip");
			}
		}
		return publicip;
	}
	
	private boolean checkVolumnStatus(String string, String volumnname) throws Exception {
		if (null == m)
			loadM();
		JSONObject obj1 = StorageAPI.viewStorageVolumns(m.get("viewstoragevolumns"), volumnname);
		String desired_state = "";
		if (null != obj1) {
			desired_state = obj1.optString("status");
		}
		if ("Online".equals(desired_state))
			return true;
		return false;
	}

	private boolean checkInstanceStatus(String string, String instancefullname2, String state) throws Exception {
		JSONObject obj1 = ComputeAPI.viewComputes(string, instancefullname2);
		String desired_state = "";
		if (null != obj1) {
			desired_state = obj1.optString("state");
		}
		if (state.equals(desired_state))
			return true;
		return false;
	}

	private void loadM() {
		m = BiuUtils.getProps();
	}
	
	// 检查资源使用情况
	public boolean checkingSystemQuota() throws Exception {
		if (m == null)
			loadM();
		JSONObject jsona = CommonAPI.summary(m.get("summary"), "all");
		JSONObject jsonb = jsona.optJSONObject("biureturn");
		int used_volumn = jsonb.optInt("used_blockvolumn");
		boolean allow = true;
		log.info("[Resources Check] Start Resources Usage and Quota Check");
		if ((Biu.QUOTA_VOLUMN - used_volumn) < newvolumnsize) {
			allow = false;
		}
		log.info("[Resources Check] Volumn QUOTA[" + Biu.QUOTA_VOLUMN + "], Current Usage[" + used_volumn + "], New Request["
				+ newvolumnsize + "]");
		return allow;
	}	

	@Override
	public RespEntity query(String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RespEntity rollback(XTransaction xtranc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean bomb(String orchid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bomb(String orchid, boolean isall) {
		// TODO Auto-generated method stub
		return false;
	}

}
