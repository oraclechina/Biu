package com.oracle.cloud.biu.api;

import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class CommonAPI {
	
	public static JSONObject listShape(String path) throws Exception {
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static String listOSContainers(String path) throws Exception {
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE_DIR, path);
		log.debug(node.getObject());
		return node.getObject().getJSONArray("result").get(1).toString();
	}
	
	public static JSONObject listOSImages(String path) throws Exception {
//		String container = listOSContainers("/machineimage/");
		path = BiuUtils.kv(path, "container", "/oracle/public/");
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
//		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject listMyOSImages(String path) throws Exception {
//		String container = listOSContainers("/machineimage/");
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static void listMyImages(String path) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE_DIR, path);
		log.debug(node.getObject());
	}
	
	public static void listOracleImages(String path) throws Exception {
		path = BiuUtils.kv(path, "name", "oracle/public/");
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE_DIR, path);
		log.debug(node.getObject());
	}
	
	public static void listMyConImages(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
	}	
	
}
