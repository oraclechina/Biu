package com.oracle.cloud.biu.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.Biu;
import com.oracle.cloud.biu.entity.KV;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class CommonAPI {
	
	public static JSONObject summary(String path, String tenant) throws Exception {
//		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		String computepath = path.split("_")[0];
		String storagepath = path.split("_")[1];
		int used_ocpu = 0;
		int used_mem = 0;
		long used_blockvolumn = 0l;
		String param = "";
		if (!"all".equals(tenant))
			param = "/?tags=" + tenant;
		JSONObject jsonobj = ComputeAPI.listComputes(computepath + param);
		JSONObject jsonobj2 = StorageAPI.listStorageVolumns(storagepath + param);
		JSONArray ary = jsonobj.optJSONArray("result");
		JSONArray ary2 = jsonobj2.optJSONArray("result");
		int user = 0;
		log.info("====================== Biu Summary Report =================");
		if ((null != ary) && (ary.length() > 0)) {
			user = ary.length();
			Set<String> unset = new HashSet<String>();
			for (Object object : ary) {
				JSONObject t1 = (JSONObject) object;
				String shape = t1.optString("shape");
				JSONArray t2 = t1.optJSONArray("tags");
				if ((null != t2) && (t2.length() > 0)) {
					unset.add(t2.getString(0));
				}
				String res1 = Biu.SHAPEMAP.get(shape);
				if (null != res1) {
					String[] cpu_mem = res1.split("_");
					used_ocpu += Integer.parseInt(cpu_mem[0]);
					used_mem += Integer.parseInt(cpu_mem[1]);
				}
			}
			for (String string : unset) {
				log.info("Tenant User Name	:	" + string);
			}
			if ((null != ary2) && (ary2.length() > 0)) {
				for (Object object : ary2) {
					JSONObject t1 = (JSONObject) object;
					String size = t1.optString("size");
					long sotsize = (Long.parseLong(size) / 1024 / 1024 / 1024);
					used_blockvolumn += sotsize;
				}
			}
			user = unset.size();
			log.info("No. of Tenant User	:	" + user);
			log.info("Used OCPU Resources	:	" + used_ocpu + " / " + Biu.QUOTA_OCPU + " (Unit OCPU)");
			log.info("Used Memory Resources	:	" + used_mem + " / " + Biu.QUOTA_MEM + " (Unit G)");
			log.info("Used Volumn Resources  :	" + used_blockvolumn + " / " + Biu.QUOTA_VOLUMN + " (Unit G)");
		}
		org.json.JSONObject json = new org.json.JSONObject();
		json.put("tenantusers", user);
		json.put("used_ocpu", used_ocpu);
		json.put("used_mem", used_mem);
		json.put("used_blockvolumn", used_blockvolumn);
		return BiuUtils.getSucReturn("summary", json);
	}
	
	public static JSONObject summary(String path, String tenant, List<String> shapes) throws Exception {
//		path = BiuUtils.kv(path, "container", BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME);
		String computepath = path.split("_")[0];
		String storagepath = path.split("_")[1];
		int used_ocpu = 0;
		int used_mem = 0;
		long used_blockvolumn = 0l;
		String param = "";
		if (!"all".equals(tenant))
			param = "/?tags=" + tenant;
		JSONObject jsonobj = ComputeAPI.listComputes(computepath + param);
		JSONObject jsonobj2 = StorageAPI.listStorageVolumns(storagepath + param);
		JSONArray ary = jsonobj.optJSONArray("result");
		JSONArray ary2 = jsonobj2.optJSONArray("result");
		Map<String, String> aids = new HashMap<String, String>();
		log.info("====================== Biu Summary Report =================");
		if ((null != ary) && (ary.length() > 0)) {
			for (Object object : ary) {
				JSONObject t1 = (JSONObject) object;
				String shape = t1.optString("shape");
				String res1 = Biu.SHAPEMAP.get(shape);
				if (null != res1) {
					String[] cpu_mem = res1.split("_");
					for (String tshape : shapes) {
						if (shape.equals(tshape)) {
							String[] p1 = new String[2];
							aids.put(t1.optString("name"), t1.optString("shape"));
							used_ocpu += Integer.parseInt(cpu_mem[0]);
							used_mem += Integer.parseInt(cpu_mem[1]);
						}
					}
				}
			}
			if ((null != ary2) && (ary2.length() > 0)) {
				for (Object object : ary2) {
					JSONObject t1 = (JSONObject) object;
					String volname = t1.optString("name");
					KV kv = BiuUtils.storageAttachmentRelationMap.get(volname);
					String insname = kv.getValue();
					String t5 = aids.get(insname);
					if (!StringUtils.isBlank(t5)) {
						String size = t1.optString("size");
						long sotsize = (Long.parseLong(size) / 1024 / 1024 / 1024);
						used_blockvolumn += sotsize;
					}
				}
			}
			log.info("Used OCPU Resources	:	" + used_ocpu + " / " + Biu.QUOTA_OCPU + " (Unit OCPU)");
			log.info("Used Memory Resources	:	" + used_mem + " / " + Biu.QUOTA_MEM + " (Unit G)");
			log.info("Used Volumn Resources  :	" + used_blockvolumn + " / " + Biu.QUOTA_VOLUMN + " (Unit G)");
		}
		org.json.JSONObject json = new org.json.JSONObject();
		json.put("used_ocpu", used_ocpu);
		json.put("used_mem", used_mem);
		json.put("used_blockvolumn", used_blockvolumn);
		return BiuUtils.getSucReturn("summary", json);
	}
	
	public static JSONObject subtenantlist(String path, String roottenant) throws Exception {
		String computepath = path.split("_")[0];
		JSONArray retAry = new JSONArray();
		JSONObject retObj = new JSONObject();
		String param = "/?tags=" + roottenant;
		JSONObject jsonobj = ComputeAPI.listComputes(computepath + param);
		JSONArray ary = jsonobj.optJSONArray("result");
		Set<String> unset = new HashSet<String>();
		for (Object object : ary) {
			JSONObject t1 = (JSONObject) object;
			JSONArray t2 = t1.optJSONArray("tags");
			if ((null != t2) && (t2.length() > 0)) {
				unset.add(t2.getString(0));
			}
		}
		for (String tenantName : unset) {
			JSONObject jsonb = summary(path, tenantName);
			retAry.put(jsonb);
		}
		retObj.put("xresult", retAry);
		return retObj;
	}
	
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
