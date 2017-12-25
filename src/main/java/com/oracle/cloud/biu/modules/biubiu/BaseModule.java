package com.oracle.cloud.biu.modules.biubiu;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.oracle.cloud.biu.Biu;
import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.modules.biubiu.shell.OracleDatabaseAutoDeployRespEntity;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public abstract class BaseModule {

	protected Map<String, String> m = null;
	
	public void authoritied() throws Exception {
		File file = new File(Biu.BIUPROFILE);
		if (!file.exists()) {
			log.warn("biuaccount not exists");
			FileUtils.writeStringToFile(file, "");
		}
		String accountstr = FileUtils.readFileToString(new File(Biu.BIUPROFILE));
		Biu.initBiu(accountstr);
		Biu.loadPrompt();

		m = BiuUtils.getProps();
		
		//Login to Biu
		BasicAuthenticationAPI.setUserInfoForLogin(m.get("cloud_tenant"), m.get("endpoint"), m.get("cloud_domain"),
				m.get("cloud_username"), m.get("cloud_password"));
		BasicAuthenticationAPI.login(m.get("login"));
	}
	public abstract void init();
	
	public abstract RespEntity deploy(String...params);
	
	public abstract RespEntity query(String...params);
}
