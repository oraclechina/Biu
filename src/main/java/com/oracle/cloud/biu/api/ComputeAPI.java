package com.oracle.cloud.biu.api;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.entity.ComputeEntity;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class ComputeAPI {
	
	public static JSONObject listComputes(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject viewComputes(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}	
	
	public static String getVcable(String path, String instanceName) throws Exception {
		path = BiuUtils.kv(path, "name", instanceName);
		log.debug(path);
		String vcalbeid = null;
		do {
			JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
			log.debug(node.getObject().get("vcable_id"));
			vcalbeid = node.getObject().get("vcable_id").toString();
			if (StringUtils.isEmpty(vcalbeid))
				Thread.sleep(10000);
		}
		while (StringUtils.isEmpty(vcalbeid));
		return vcalbeid;
	}
	
	public static JSONObject createComputeInstance(String path, String label, String shape, String imagelist, String keyname) throws Exception {
		String name = BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + label;
		String jsonbody = ComputeEntity.build(label, name, shape, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + keyname, imagelist);
		log.debug("================== request body ======================");
		log.debug(jsonbody);
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject deleteComputeInstance(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Compute");
	}
	
}
