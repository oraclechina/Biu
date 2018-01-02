package com.oracle.cloud.biu.api;

import org.json.JSONObject;

import com.oracle.cloud.biu.modules.biubiu.shell.OracleDatabaseAutoDeploy;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class BiuBiuAPI {

	public static JSONObject createOracleDatabase12cR1AutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabase12cR1AutoDeploy");
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
		deploy.init();
//		deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, tenant, pk);
		deploy.deploy("zhiqiang", "", "biuins3", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "", "");
		return BiuUtils.getSucReturn("Biubiu-createOracleDatabase12cR1AutoDeploy");
	}
	
	public static JSONObject createOracleDatabase11gR2AutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabase11gR2AutoDeploy");
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
		deploy.init();
//		deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, tenant, pk);
		deploy.deploy("zhiqiang", "", "biuins4", "oc4", "/oracle/public/OL_6.8_UEKR4_x86_64", "zhiqiang", "", "");
		return BiuUtils.getSucReturn("Biubiu-createOracleDatabase11gR2AutoDeploy");
	}
	
	public static JSONObject createOracleDatabaseRACAutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabaseRACAutoDeploy");
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
		deploy.init();
//		deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, tenant, pk);
		deploy.deploy("zhiqiang", "", "biuins3", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "", "");
		return BiuUtils.getSucReturn("Biubiu-createOracleDatabaseAutoDeploy");
	}
	
	public static JSONObject createOracleWeblogicAutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabaseRACAutoDeploy");
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
		deploy.init();
//		deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, tenant, pk);
		deploy.deploy("zhiqiang", "", "biuins3", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "", "");
		return BiuUtils.getSucReturn("Biubiu-createOracleDatabaseAutoDeploy");
	}
	
	public static JSONObject createHadoopClusterAutoDeploy(String path, String sshkeyname, String sshkeycontent, String insname, String shape, String os, String tenant, String pk) throws Exception {
		log.info("BiuBiuUI - Loaded Plugin - createOracleDatabaseRACAutoDeploy");
		OracleDatabaseAutoDeploy deploy = new OracleDatabaseAutoDeploy();
		deploy.init();
//		deploy.deploy(sshkeyname, sshkeycontent, insname, shape, os, tenant, pk);
		deploy.deploy("zhiqiang", "", "biuins3", "oc4", "/oracle/public/OL_7.2_UEKR4_x86_64", "zhiqiang", "", "");
		return BiuUtils.getSucReturn("Biubiu-createOracleDatabaseAutoDeploy");
	}
	
}
