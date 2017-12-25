package com.oracle.cloud.biu.api;

import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class StorageAPI {

	public static JSONObject viewStorageVolumns(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
//		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject listStorageAttachment(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject deleteStorageVolumns(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Storage");
	}
	
	public static JSONObject deleteStorageAttachment(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Storage");
	}
	
	public static JSONObject viewStorageAttachment(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
//		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject listStorageVolumns(String path) throws Exception {
		path = BiuUtils.kv(path, "container",
				BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
//		log.debug(node.getObject());
		return node.getObject();
	}

	public static JSONObject createStorageBootVolumns(String path, String size) throws Exception {
		String addition = "\"bootable\": \"true\", \"imagelist\": \"/oracle/public/OL_7.2_UEKR4_x86_64\",";
		String storagename = "vol_" + BiuUtils.getRandomString(3);
		String jsonbody = "{  \"size\": \"" + size
				+ "G\",  \"properties\": [\"/oracle/public/storage/latency\"], " + addition + "  \"name\": \""+BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + storagename + "\",  \"tags\": [\"" + BasicAuthenticationAPI.CLOUD_TENANT + "\"]}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}

	public static JSONObject createStorageBlankVolumns(String path, String size) throws Exception {
		String storagename = "vol_" + BiuUtils.getRandomString(3);
		String jsonbody = "{  \"size\": \"" + size
				+ "G\",  \"properties\": [\"/oracle/public/storage/latency\"], \"name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + storagename + "\",  \"tags\": [\"" + BasicAuthenticationAPI.CLOUD_TENANT + "\"]}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createStorageAttachments(String path, String volname, String instanceName, String index) throws Exception {
		String jsonbody = "{ \"index\": " + index + ", \"storage_volume_name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + volname + "\", \"instance_name\": \"" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + instanceName + "\"}";
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}

}
