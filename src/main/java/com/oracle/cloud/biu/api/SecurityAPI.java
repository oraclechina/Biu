package com.oracle.cloud.biu.api;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class SecurityAPI {
	
	public static JSONObject listSSHKeys(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createSSHKey(String path, String keyname, String keystring) throws Exception {
		String jsonbody = "{ \"enabled\": true, \"key\": \"" + keystring + "\", \"name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + keyname + "\"}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject deleteSSHKey(String path, String keyname) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + keyname);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Security");
	}
	
	public static JSONObject createSSHKeyForUI(String path, String keyname, String filepath) throws Exception {
		String keystring = "";
		keystring = FileUtils.readFileToString(new File(filepath), "UTF-8");
		JSONObject obj = createSSHKey(path, keyname, keystring);
		return obj;
	}
	
}
