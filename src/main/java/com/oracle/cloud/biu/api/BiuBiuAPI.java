package com.oracle.cloud.biu.api;

import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

import com.oracle.cloud.biu.modules.biubiu.project.citic.Biu_CITIC_SDK;
import com.oracle.cloud.biu.modules.biubiu.project.citic.Biu_CITIC_SDK_Desc;
import com.oracle.cloud.biu.modules.biubiu.shell.OracleDatabaseAutoDeploy;
import com.oracle.cloud.biu.modules.biubiu.shell.OracleOrchDatabaseAutoDeploy;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class BiuBiuAPI {

	public static JSONObject createOracleDatabase12cR1AutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk, String containerid) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabase12cR1AutoDeploy");
		
		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		UUID uuid = UUID.randomUUID();
		String xid = uuid.toString().replaceAll("-", "");
		citic.setXid(xid);
		citic.setXmessage("ok");
		citic.setXestimatetime(24);
		citic.setXdate(new Date());
		Biu_CITIC_SDK.xmap.put(xid, citic);
		log.info("XID:" + xid);
//		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy("zhiqiang", "", "biuins4", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "OracleDatabase12cR1", "zhiqiang", "", xid, "100", "Welcome1#", "ZHS16GBK", "orclbiu", "1521", "5500", "5", "true");
		OracleOrchDatabaseAutoDeploy deploy = new OracleOrchDatabaseAutoDeploy("zhiqiang", "", "biuins4", "oc3", "/oracle/public/OL_7.2_UEKR4_x86_64", "OracleDatabase12cR1", "zhiqiang", "", xid, "100", "Welcome1#", "ZHS16GBK", "orclbiu", "1521", "5500", "5", "true", "biu_database_backup");
		deploy.start();
		
		return BiuUtils.getSucReturn("XID:" + xid);
	}
	
	public static JSONObject createOracleDatabase11gR2AutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk, String containerid) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabase11gR2AutoDeploy");

		Biu_CITIC_SDK_Desc citic = new Biu_CITIC_SDK_Desc();
		UUID uuid = UUID.randomUUID();
		String xid = uuid.toString().replaceAll("-", "");
		citic.setXid(xid);
		citic.setXmessage("ok");
		citic.setXestimatetime(24);
		citic.setXdate(new Date());
		Biu_CITIC_SDK.xmap.put(xid, citic);
		
//		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy("zhiqiang", "", "biuins5", "oc4", "/oracle/public/OL_6.8_UEKR4_x86_64", "OracleDatabase11gR2", "zhiqiang", "", xid, "100", "Welcome1#", "ZHS16GBK", "orclbiu", "1521", "5500", "5", "true");
		OracleOrchDatabaseAutoDeploy deploy = new OracleOrchDatabaseAutoDeploy("zhiqiang", "", "biuins9", "oc3", "/oracle/public/OL_6.8_UEKR4_x86_64", "OracleDatabase11gR2", "zhiqiang", "", xid, "100", "Welcome1#", "ZHS16GBK", "orclbiu", "1521", "1158", "5", "true", "biuins9");
		deploy.setRunnableMode("deploy");
		deploy.start();
		
		log.info("XID:" + xid);
		
		return BiuUtils.getSucReturn("XID:" + xid);
	}
	
	public static JSONObject queryTransactionStatus(String path, String xid) {
		Object obj = Biu_CITIC_SDK.xmap.get(xid);
		if (null == obj) {
			log.info("No Event Find, Do you input a correct xid?");
			return BiuUtils.getErrReturn("Wrong XID", "No Event Find, Do you input a correct xid?");
		} else {
			Biu_CITIC_SDK_Desc desc = (Biu_CITIC_SDK_Desc) obj;
			desc.setXremaintime((desc.getXestimatetime() - BiuUtils.dateDiff(new Date(), desc.getXdate())) > 0 ? (desc.getXestimatetime() - BiuUtils.dateDiff(new Date(), desc.getXdate())) : 0);
			log.info("========================= Transaction Event Status =====================");
			log.info("XMessage				:" + desc.getXmessage());
			log.info("XStatus				:" + desc.getXstatus());
			log.info("XLog					:" + desc.getXlog());
			log.info("Instance Name			:" + desc.getInstancename());
			log.info("Shape					:" + desc.getShape());
			log.info("Public IP				:" + desc.getPubip());
			log.info("Private IP			:" + desc.getPrvip());
			log.info("Tenant				:" + desc.getTenant());
			log.info("Operation System		:" + desc.getOs());
			log.info("DB Listener Port		:" + desc.getOracledblisport());
			log.info("DB SID				:" + desc.getOracledbsid());
			log.info("DB Charset			:" + desc.getOracledbcharset());
			log.info("DB EM Console			:" + desc.getOracledbconsoleurl());
			log.info("RemainTime			:" + desc.getXremaintime());
			return BiuUtils.getSucReturn("queryTransactionStatus");
		}
	}
	
	public static JSONObject bombOracleDatabase(String path, String orchname) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - bombOracleDatabase");
		OracleOrchDatabaseAutoDeploy deploy = new OracleOrchDatabaseAutoDeploy(orchname, null);
		deploy.setRunnableMode("bomb");
		deploy.start();
		return BiuUtils.getSucReturn("queryTransactionStatus");
	}
	
	public static JSONObject createOracleDatabaseRACAutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabaseRACAutoDeploy");
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
		deploy.init();
//		deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, tenant, pk);
//		deploy.deploy("zhiqiang", "", "biuins3", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "", "");
		return BiuUtils.getSucReturn("Biubiu-createOracleDatabaseAutoDeploy");
	}
	
	public static JSONObject createOracleWeblogicAutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabaseRACAutoDeploy");
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
		deploy.init();
//		deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, tenant, pk);
//		deploy.deploy("zhiqiang", "", "biuins3", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "", "");
		return BiuUtils.getSucReturn("Biubiu-createOracleDatabaseAutoDeploy");
	}
	
	public static JSONObject createHadoopClusterAutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabaseRACAutoDeploy");
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
		deploy.init();
//		deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, tenant, pk);
//		deploy.deploy("zhiqiang", "", "biuins3", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "", "");
		return BiuUtils.getSucReturn("Biubiu-createOracleDatabaseAutoDeploy");
	}
	
}
