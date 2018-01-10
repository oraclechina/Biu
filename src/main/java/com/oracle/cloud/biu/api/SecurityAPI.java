package com.oracle.cloud.biu.api;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class SecurityAPI {
	
	public static JSONObject listSecList(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createSecList(String path, String name, String outbound, String inbound) throws Exception {
		String jsonbody = "{  \"policy\": \"" + inbound + "\",  \"outbound_cidr_policy\": \"" + outbound + "\",  \"name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name + "\"}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject deleteSecList(String path, String keyname) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + keyname);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Security");
	}
	
	public static JSONObject listSecRule(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createSecRule(String path, String name, String to) throws Exception {
		String secapp = "/oracle/public/all";
		String src = "/oracle/public/public-internet";
		String jsonbody = "{  \"dst_list\": \"seclist:" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + to + "\",  \"name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name + "\",  \"src_list\": \"seciplist:" + src + "\",  \"application\": \"" + secapp + "\",  \"action\": \"PERMIT\"}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject listSecAssociation(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}	
	
	public static JSONObject createSecAssociation(String path, String vcableid, String seclist) throws Exception {
		String jsonbody = "{  \"vcable\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + vcableid + "\",  \"seclist\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + seclist + "\"}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject deleteSecAssociation(String path, String vcableid, String seclistname) throws Exception {
		JSONObject obj1 = listSecAssociation(BiuUtils.propsMap.get("security_association"));
		JSONArray jsonary1 = obj1.getJSONArray("result");
		String f_name = "";
		for (int i = 0; i < jsonary1.length(); i++) {
			JSONObject temp1 = jsonary1.getJSONObject(i);
			String tvcableid = temp1.optString("vcable");
			String tseclist = temp1.optString("seclist");
			if ((tvcableid.indexOf(vcableid) > -1) && (tseclist.indexOf(seclistname) > -1)) {
				f_name = temp1.optString("name");
			}
		}
		if (StringUtils.isBlank(f_name))
			return BiuUtils.getErrReturn("Security", "没有找到关联关系");
		
		path = BiuUtils.kv(path, "name", f_name);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Security");
	}
	
	public static JSONObject deleteSecRule(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Security");
	}
	
	public static JSONObject listSSHKeys(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createSSHKey(String path, String keyname, String keystring) throws Exception {
		String jsonbody = "{ \"enabled\": true, \"key\": \"" + keystring + "\", \"name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + keyname + "\"}";
		log.debug("============================== json body ============================");
		log.debug(jsonbody);
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
