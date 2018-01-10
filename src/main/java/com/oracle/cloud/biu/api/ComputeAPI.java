package com.oracle.cloud.biu.api;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.entity.ComputeEntity;
import com.oracle.cloud.biu.entity.ComputeOrchestrationEntity;
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
	
	public static JSONObject createComputeInstance(String path, String label, String shape, String imagelist, String keyname, String tags) throws Exception {
		String name = BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + label;
		String jsonbody = ComputeEntity.build(label, name, shape, BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + keyname, imagelist, tags);
		log.debug("================== request body ======================");
		log.debug(jsonbody);
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject listOrchestrationComputes(String path) throws Exception {
		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject createComputeInstanceOrchestration(String path, String label, String shape, String imagelist, String bootstoragesize, String datastoragesize, String keyname, String tags)  throws Exception {
		ComputeOrchestrationEntity coEntity = new ComputeOrchestrationEntity(BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/");
		String publickey = "";
		publickey = FileUtils.readFileToString(new File("D:\\job\\OTASK\\Oracle Cloud\\近期项目\\Others\\sshkeybundle\\publicKey.pub"));
		String jsonbody = coEntity.build(tags, "", "Biu Generation", label, label, "BiuGeneration", tags + "_orch_" + label, shape, imagelist, tags + "_ssh_" + label, publickey, keyname, tags + "_vol_boot_1_" + label, tags + "_vol_boot_1_" + label, bootstoragesize, imagelist, tags + "_vol_data_2_" + label, datastoragesize, tags + "_vol_data_2_" + label, tags + "_ip_" + label, tags + "_ip_" + label);
		log.debug("================== request body ======================");
		log.debug(jsonbody);
		JsonNode node = BiuUtils.rest("post", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject viewOrchestrationComputes(String path, String name) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + name);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}	
	
	public static JSONObject deleteComputeInstanceOrchestration(String path, String instanceName) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + instanceName + "?terminate=true");
		log.debug(path);
		int retcode = BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, true);
		return BiuUtils.getSucReturn(String.valueOf(retcode));
	}
	
	public static JSONObject deleteComputeInstance(String path, String instanceName) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + instanceName);
		log.debug(path);
		BiuUtils.rest("delete", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		return BiuUtils.getSucReturn("Compute");
	}
	
	public static JSONObject changeComputeShape(String path, String instanceName, String newshape) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + instanceName);
		log.debug("================== [changeComputeShape] ======================");
		log.debug(path);
		String jsonbody = "{  \"shape\":\"" + newshape + "\"}";
		JsonNode node = BiuUtils.rest("put", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return node.getObject();
	}
	
	public static JSONObject startCompute(String path, String instanceName) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + instanceName);
		log.debug("================== [startCompute] ======================");
		log.debug(path);
		String jsonbody = "{  \"desired_state\":\"running\"}";
		JsonNode node = BiuUtils.rest("put", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return BiuUtils.getSucReturn("Compute");
	}
	
	public static JSONObject stopCompute(String path, String instanceName) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + instanceName);
		log.debug("================== [startCompute] ======================");
		log.debug(path);
		String jsonbody = "{  \"desired_state\":\"shutdown\"}";
		JsonNode node = BiuUtils.rest("put", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		return BiuUtils.getSucReturn("Compute");
	}
	
	public static JSONObject changeComputeTenant(String path, String instanceName, String newTenant) throws Exception {
		path = BiuUtils.kv(path, "name", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/" + instanceName);
		log.debug("================== [startCompute] ======================");
		log.debug(path);
		String jsonbody = "{\"tags\": [\"" + newTenant + "\"]}";
		JsonNode node = BiuUtils.rest("put", BasicAuthenticationAPI.ACCEPT_COMPUTE, path, jsonbody);
		log.debug(node.getObject());
		//TODO 没实现完整
		return BiuUtils.getSucReturn("Compute");
	}
}
