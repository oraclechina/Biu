package com.oracle.cloud.biu.api;

import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.entity.IP;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class NetworkAPI {
	
	public static JSONObject listSharedFixIPs(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createSharedRandomIP(String path, String computeName) throws Exception {
		String jsonbody = "{\"parentpool\": \"ippool:/oracle/public/ippool\",  \"vcable\": \"" + computeName + "\",  \"tags\": [\"" + BasicAuthenticationAPI.CLOUD_TENANT + "\"]}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createIPN(String path, String name, String networkrange) throws Exception {
		String jsonbody = "{ \"name\": \"/"+BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name + "\", \"ipAddressPrefix\": \"" + networkrange + "\",  \"tags\": [\"" + BasicAuthenticationAPI.CLOUD_TENANT + "\"]}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject deleteIPN(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Network");
	}
	
	public static JSONObject listIPN(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createSharedFixIP(String path, String name) throws Exception {
//		String name = BiuUtils.getProps().get("name");
		String body = "{\"parentpool\": \"/oracle/public/ippool\",  \"permanent\": true,  \"name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name + "\",  \"tags\": [\"" + BasicAuthenticationAPI.CLOUD_TENANT + "\"]}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, body);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject deleteSharedFixIP(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Network");
	}
	
	public static JSONObject listIPNFixIPs(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}	
	
	public static JSONObject createIPNFixIP(String path, String name) throws Exception {
//		String name = BiuUtils.getProps().get("name");
		String body = "{  \"name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name + "\",  \"ipAddressPool\": \"/oracle/public/public-ippool\",  \"tags\": [\"" + BasicAuthenticationAPI.CLOUD_TENANT + "\"]}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, body);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject deleteIPNFixIP(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Network");
	}
	
}
