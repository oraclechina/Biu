package com.oracle.cloud.biu.api.ccs;

import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.utils.BiuUtils;

public class OracleComputeCloudServiceAPI {
	
	public static JsonNode listSSHKeys(String path) throws Exception {
		JsonNode json = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE_DIR, path);
		return json;
	}
	
	public static void main(String[] args) {
		try {
			Map<String, String> pathMap = new HashMap<String, String>();
			pathMap.put("login", "/authenticate/");
			pathMap.put("sshkey", "/sshkey/");
			BasicAuthenticationAPI.login(pathMap.get("login"));
			JsonNode json = OracleComputeCloudServiceAPI.listSSHKeys(pathMap.get("sshkey"));
			System.out.println(json.getObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
