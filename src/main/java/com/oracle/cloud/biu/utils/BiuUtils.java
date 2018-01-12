package com.oracle.cloud.biu.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jakewharton.fliptables.FlipTable;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.oracle.cloud.biu.Biu;
import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.api.NetworkAPI;
import com.oracle.cloud.biu.api.StorageAPI;
import com.oracle.cloud.biu.entity.AsciiTableEntity;
import com.oracle.cloud.biu.entity.KV;
import com.oracle.cloud.biu.entity.MyComparator;
import com.thoughtworks.xstream.XStream;

import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import lombok.extern.log4j.Log4j;

@Log4j
public class BiuUtils extends EncryptUtil {

	public static Map<String, String> propsMap = null;
	public static Map<String, KV> storageAttachmentRelationMap = null;
	public static Map<String, KV> networkAttachmentRelationMap = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> getProps() {
		if (null == propsMap) {
			Properties properties;
			properties = new Properties();
			try {
				properties.load(BiuUtils.class.getClassLoader().getResourceAsStream("opc_rest_path.properties"));
				propsMap = new HashMap<String, String>((Map) properties);
				properties.putAll(propsMap);
				String accountstr = FileUtils.readFileToString(new File(Biu.BIUPROFILE));
				String source = BiuUtils.decrypted(accountstr);
				Map<String, ? extends PromtResultItemIF> map1 = (Map<String, ? extends PromtResultItemIF>) JSON
						.parseObject(source);

				propsMap.put("endpoint", ((JSONObject) map1.get("endpoint")).getString("input"));
				propsMap.put("storageendpoint", ((JSONObject) map1.get("storageendpoint")).getString("input"));
				propsMap.put("cloud_username", ((JSONObject) map1.get("clouduser")).getString("input"));
				propsMap.put("cloud_domain", ((JSONObject) map1.get("clouddomain")).getString("input"));
				propsMap.put("cloud_password", ((JSONObject) map1.get("cloudpassword")).getString("input"));
				return propsMap;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else
			return propsMap;
	}

	public static String kv(String orgi, String key, String value) {
		String sdstr = orgi.replaceAll("\\{" + key + "\\}", value);
		sdstr = sdstr.replaceAll("//", "/");
		return sdstr;
	}

	public static String kvNULL(String orgi, String key) {
		String sdstr = orgi.replaceAll(key, "");
		return sdstr;
	}

	public static HttpResponse<JsonNode> rest(String path) throws Exception {
		HttpResponse<JsonNode> jsonresp = null;
		jsonresp = Unirest.get(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", BasicAuthenticationAPI.ACCEPT_COMPUTE)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		return jsonresp;
	}
	
	public static JsonNode rest(String method, String accept, String path) throws Exception {
		HttpResponse<JsonNode> jsonresp = null;
		if (method.equals("get")) {
			jsonresp = Unirest.get(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		} else if (method.equals("post")) {
			jsonresp = Unirest.post(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		} else if (method.equals("put")) {
			jsonresp = Unirest.put(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		} else if (method.equals("delete")) {
			jsonresp = Unirest.delete(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		}
		return jsonresp.getBody();
	}

	public static JsonNode rest(String method, String accept, String path, String jsonbody) throws Exception {
		HttpResponse<JsonNode> jsonresp = null;
		if (method.equals("post")) {
			jsonresp = Unirest.post(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).body(jsonbody).asJson();
		} else if (method.equals("put")) {
			jsonresp = Unirest.put(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).body(jsonbody).asJson();
		} else if (method.equals("delete")) {
			jsonresp = Unirest.delete(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).body(jsonbody).asJson();
		}
		return jsonresp.getBody();
	}

	public static int rest(String method, String accept, String path, boolean withhead) throws Exception {
		HttpResponse<JsonNode> jsonresp = null;
		if (method.equals("get")) {
			jsonresp = Unirest.get(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		} else if (method.equals("post")) {
			jsonresp = Unirest.post(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		} else if (method.equals("put")) {
			jsonresp = Unirest.put(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		} else if (method.equals("delete")) {
			jsonresp = Unirest.delete(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).asJson();
		}
		return jsonresp.getStatus();
	}

	public static int rest(String method, String accept, String path, String jsonbody, boolean withhead) throws Exception {
		HttpResponse<JsonNode> jsonresp = null;
		if (method.equals("post")) {
			jsonresp = Unirest.post(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).body(jsonbody).asJson();
		} else if (method.equals("put")) {
			jsonresp = Unirest.put(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).body(jsonbody).asJson();
		} else if (method.equals("delete")) {
			jsonresp = Unirest.delete(BasicAuthenticationAPI.ENDPOINT + path).header("Accept", accept)
					.header("Content-Type", BasicAuthenticationAPI.CONTENT_TYPE)
					.header("Cookie", BasicAuthenticationAPI.COOKIE).body(jsonbody).asJson();
		}
		return jsonresp.getStatus();
	}
	
	public static String ConvertStream2Json(InputStream inputStream) {
		String jsonStr = "";
		// ByteArrayOutputStream相当于内存输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		// 将输入流转移到内存输出流中
		try {
			while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, len);
			}
			// 将内存流转换为字符串
			jsonStr = new String(out.toByteArray(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static String toJson(Object obj) {
		return JSON.toJSONString(obj);
	}

	public static void print(AsciiTableEntity en) {
		System.out.println("\n" + FlipTable.of(en.getHeader(), en.getData()));
	}

	public static AsciiTableEntity toAsciiTable(String method, org.json.JSONObject jsonobj) {
		AsciiTableEntity en = new AsciiTableEntity();
		String[] headers = { "Result" };
		String[][] data = { { "ERROR" } };
		try {
			if (null == jsonobj) {
				en.setHeader(headers);
				en.setData(data);
				return en;
			}
			String flag = "";
			Object tobj = jsonobj.opt("name");
			if (null != tobj)
				flag = "hasName";
			tobj = jsonobj.opt("message");
			if (null != tobj)
				flag = "hasMessage";
			tobj = jsonobj.opt("result");
			if (null != tobj)
				flag = "hasResult";
			tobj = jsonobj.opt("instances");
			if (null != tobj)
				flag = "hasLunchplan";

			if (method.indexOf("view") > -1)
				flag = "hasView";

			if ("hasName".equals(flag))
				en = goHasName(jsonobj.getString("name"));
			else if ("hasMessage".equals(flag))
				en = goHasMessage(jsonobj.getString("message"));
			else if ("hasResult".equals(flag))
				en = goHasResult(method, jsonobj, jsonobj.getJSONArray("result"));
			else if ("hasLunchplan".equals(flag))
				en = goHasLunchplanResult(method, jsonobj, jsonobj.getJSONArray("instances"));
			else if ("hasView".equals(flag))
				en = goHasView(method, jsonobj);

		} catch (JSONException e) {
			en.setHeader(headers);
			en.setData(data);
			e.printStackTrace();
		}

		return en;
	}

	private static AsciiTableEntity goHasLunchplanResult(String method, org.json.JSONObject jsonobj,
			org.json.JSONArray jsonary) {
		String[] headers = { "Name" };
		String[][] data = new String[jsonary.length()][1];
		int i = 0;
		for (Object tempobj : jsonary) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			i++;
		}
		AsciiTableEntity en = new AsciiTableEntity();
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasView(String method, org.json.JSONObject jsonobj) {
		AsciiTableEntity en = new AsciiTableEntity();
		if ("com.oracle.cloud.biu.api.StorageAPI-viewStorageVolumns".equals(method))
			en = goHasStorageViewResult(jsonobj, en);
		else if ("com.oracle.cloud.biu.api.ComputeAPI-viewComputes".equals(method))
			en = goHasComputeViewResult(jsonobj, en);
		else
			en = goHasDefaultViewResult(jsonobj, en);
		return en;
	}

	private static AsciiTableEntity goHasComputeViewResult(org.json.JSONObject obj, AsciiTableEntity en) {
		String k = "Public IP";
		String k2 = "Public IP Association";
		String v = "ERROR";
		String v2 = "";
		if (null == networkAttachmentRelationMap) {
			v = "ERROR";
			v2 = "ERROR";
		} else {
			KV kv = networkAttachmentRelationMap.get(mask(obj.optString("vcable_id")));
			if (null != kv) {
				v = "Attached[" + kv.getValue() + "]";
				v2 = mask(kv.getKey());
			} else {
				v = "";
				v2 = "";
			}
		}
		obj.put(k, v);
		obj.put(k2, v2);
		List<KV> returnA = jsonToArray(obj);
		String[] headers = { "Key", "Value" };
		String[][] data = new String[obj.keySet().size()][2];
		int i = 0;
		for (KV tkey : returnA) {
			String inner = null;
			data[i][0] = tkey.getKey();
			data[i][1] = mask(tkey.getValue());
			inner = exceptComputeAttr(obj, tkey, inner, tkey.getKey());

			if (null != inner) {
				data[i][1] = inner;
			}
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	@SuppressWarnings("unchecked")
	private static String exceptComputeAttr(org.json.JSONObject obj, KV tkey, String inner, String except) {
		if ("attributes".equals(except)) {
			return "...";
		}
		if ("networking".equals(except)) {
			if (tkey.getKey().equals(except)) {
				List<KV> returnB = jsonToArray(obj.optJSONObject(except));
				String[] innerHeaders = { "Key", "Value" };
				String[][] innerData = new String[returnB.size()][2];
				int j = 0;
				for (KV t2key : returnB) {
					List<KV> returnC = jsonToArray(obj.optJSONObject(except).optJSONObject(t2key.getKey()));
					String[] inner2Headers = { "Key", "Value" };
					String[][] inner2Data = new String[returnC.size()][2];
					String inner2 = null;
					int k = 0;
					for (KV t3key : returnC) {
						inner2Data[k][0] = t3key.getKey();
						inner2Data[k][1] = mask(t3key.getValue());
						k++;
					}
					innerData[j][0] = t2key.getKey();
					inner2 = FlipTable.of(inner2Headers, inner2Data);
					innerData[j][1] = mask(inner2);
					j++;
				}
				inner = FlipTable.of(innerHeaders, innerData);
			}
		}
		if ("storage_attachments".equals(except)) {
			if (tkey.getKey().equals(except)) {
				String[] innerHeaders = { "Volumn", "Index" };
				String[][] innerData;
				List<KV> tlist = new ArrayList<KV>();
				Collection<KV> ckv = storageAttachmentRelationMap.values();
				int p = 0;
				if (null == storageAttachmentRelationMap) {
					innerData = new String[0][2];
				}
				for (KV kv : ckv) {
					// System.out.println("kv value:" + kv.getValue());
					// System.out.println("mask name:" +
					// mask(obj.optString("name")));
					if (kv.getValue().indexOf(obj.optString("name")) > -1) {
						p++;
						tlist.add(kv);
					}
				}
				Collections.sort(tlist, new MyComparator());
				if (p == 0)
					innerData = new String[0][2];
				else {
					innerData = new String[p][2];
					int j = 0;
					for (KV t2key : tlist) {
						innerData[j][0] = mask(t2key.getReserve1());
						innerData[j][1] = t2key.getReserve2();
						j++;
					}
				}
				inner = FlipTable.of(innerHeaders, innerData);
			}
		}
		return inner;
	}

	private static AsciiTableEntity goHasDefaultViewResult(org.json.JSONObject obj, AsciiTableEntity en) {
		List<KV> returnA = jsonToArray(obj);
		String[] headers = { "Key", "Value" };
		String[][] data = new String[obj.keySet().size()][2];
		int i = 0;
		for (KV tkey : returnA) {
			data[i][0] = tkey.getKey();
			data[i][1] = mask(tkey.getValue());
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasResult(String method, org.json.JSONObject obj, JSONArray jsonArray) {
		AsciiTableEntity en = new AsciiTableEntity();
		if ("com.oracle.cloud.biu.api.StorageAPI-listStorageVolumns".equals(method)) {
			log.debug("Go to Show Result");
			cacheStorageAttachementRelation(jsonArray);
			en = goHasStorageShowResult(jsonArray, en);
			log.debug("[Done] Cached Storage Attachment Relation");
		} else if ("com.oracle.cloud.biu.api.SecurityAPI-listSSHKeys".equals(method))
			en = goHasSecurityShowResult(jsonArray, en);
		else if ("com.oracle.cloud.biu.api.CommonAPI-listShape".equals(method))
			en = goHasShapeShowResult(jsonArray, en);
		else if ("com.oracle.cloud.biu.api.CommonAPI-listOSImages".equals(method))
			en = goHasOSShowResult(jsonArray, en);
		else if ("com.oracle.cloud.biu.api.NetworkAPI-listSharedFixIPs".equals(method))
			en = goHasSharedNetworkShowResult(jsonArray, en);
		else if ("com.oracle.cloud.biu.api.NetworkAPI-listIPNFixIPs".equals(method))
			en = goHasIPNetworkIPShowResult(jsonArray, en);
		else if ("com.oracle.cloud.biu.api.NetworkAPI-listIPN".equals(method))
			en = goHasIPNetworkShowResult(jsonArray, en);
		else if ("com.oracle.cloud.biu.api.ObjectStorageAPI-listContainer".equals(method))
			en = goObjectStorageCShowResult(jsonArray, en);
		else if ("com.oracle.cloud.biu.api.ObjectStorageAPI-listObjects".equals(method))
			en = goObjectStorageShowResult(jsonArray, en);		
		else if ("com.oracle.cloud.biu.api.ComputeAPI-listComputes".equals(method)) {
			cacheStorageAttachementRelation(jsonArray);
			cacheNetworkAttachementRelation(jsonArray);
			en = goHasComputeInstancesShowResult(jsonArray, en);
		} else
			en = goHasShowResult(jsonArray, en);
		return en;
	}

	private static AsciiTableEntity goObjectStorageCShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "Count", "Size" };
		String[][] data = new String[jsonArray.length()][3];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			data[i][1] = obj.getString("count");
			data[i][2] = obj.getString("size");
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}
	
	private static AsciiTableEntity goObjectStorageShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "Size" };
		String[][] data = new String[jsonArray.length()][2];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			data[i][1] = obj.getString("size");
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasComputeInstancesShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "State", "Public IP" };
		String[][] data = new String[jsonArray.length()][3];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = mask(obj.getString("name"));
			data[i][1] = obj.getString("state");
			if (null == networkAttachmentRelationMap) {
				data[i][3] = "ERROR";
			} else {
				KV kv = networkAttachmentRelationMap.get(mask(obj.optString("vcable_id")));
				if (null != kv)
					data[i][2] = kv.getValue();
				else
					data[i][2] = "";
			}
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasIPNetworkIPShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "ipAddress" };
		String[][] data = new String[jsonArray.length()][2];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			data[i][1] = obj.getString("ipAddress");
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasSharedNetworkShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "IP", "Permanent", "Used" };
		String[][] data = new String[jsonArray.length()][4];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			data[i][1] = obj.getString("ip");
			data[i][2] = String.valueOf(obj.getBoolean("permanent"));
			data[i][3] = String.valueOf(obj.getBoolean("used"));
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasIPNetworkShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "IP Prefix", "IP Exchange" };
		String[][] data = new String[jsonArray.length()][3];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			data[i][1] = obj.getString("ipAddressPrefix");
			if (obj.isNull("ipNetworkExchange"))
				data[i][2] = "否";
			else
				data[i][2] = "是";
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasOSShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "Available" };
		String[][] data = new String[jsonArray.length()][2];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			data[i][1] = obj.getString("state");
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasShapeShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "OCPUs", "vCPUs", "GPUs", "Memory" };
		String[][] data = new String[jsonArray.length()][5];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			data[i][1] = String.valueOf(obj.getInt("cpus") / 2);
			data[i][2] = String.valueOf(obj.getInt("cpus"));
			data[i][3] = String.valueOf(obj.getInt("gpus"));
			BigDecimal bigd = new BigDecimal(obj.getInt("ram"));
			BigDecimal unit = new BigDecimal("1024");
			bigd = bigd.divide(unit).setScale(1, BigDecimal.ROUND_UP);
			data[i][4] = bigd.toString().replaceAll("\\.0", "") + "G";
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	public static void cacheStorageAttachementRelation(JSONArray jsonArray) {
		try {
			org.json.JSONObject jsonobj = StorageAPI.listStorageAttachment(propsMap.get("list_storage_attachment"));
			if (null == jsonobj)
				return;
			storageAttachmentRelationMap = new HashMap<String, KV>();
			JSONArray ary = jsonobj.getJSONArray("result");
			for (Object object : ary) {
				org.json.JSONObject obj = (org.json.JSONObject) object;
				KV kv = new KV();
				kv.setKey(obj.optString("name"));
				kv.setValue(obj.optString("instance_name"));
				kv.setReserve1(obj.optString("storage_volume_name"));
				kv.setReserve2(String.valueOf(obj.optInt("index")));
				storageAttachmentRelationMap.put(mask(obj.optString("storage_volume_name")), kv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cacheNetworkAttachementRelation(JSONArray jsonArray) {
		try {
			org.json.JSONObject jsonobj = NetworkAPI.listNetworkAssociations(propsMap.get("list_network_associations"));
			if (null == jsonobj)
				return;
			networkAttachmentRelationMap = new HashMap<String, KV>();
			JSONArray ary = jsonobj.getJSONArray("result");
			for (Object object : ary) {
				org.json.JSONObject obj = (org.json.JSONObject) object;
				KV kv = new KV();
				kv.setKey(obj.optString("name"));
				kv.setValue(obj.optString("ip"));
				kv.setReserve1(obj.optString("enabled"));
				networkAttachmentRelationMap.put(mask(obj.optString("vcable")), kv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static AsciiTableEntity goHasStorageViewResult(org.json.JSONObject obj, AsciiTableEntity en) {
		String k = "Attachment";
		String k2 = "Attachment Name";
		String v = "ERROR";
		String v2 = "";
		if (null == storageAttachmentRelationMap) {
			v = "ERROR";
			v2 = "ERROR";
		} else {
			KV kv = storageAttachmentRelationMap.get(mask(obj.optString("name")));
			if (null != kv) {
				v = "Attached[" + kv.getValue() + "]";
				v2 = mask(kv.getKey());
			} else {
				v = "";
				v2 = "";
			}
		}
		obj.put(k, v);
		obj.put(k2, v2);
		List<KV> returnA = jsonToArray(obj);
		String[] headers = { "Key", "Value" };
		String[][] data = new String[obj.keySet().size()][2];
		int i = 0;
		for (KV tkey : returnA) {
			data[i][0] = tkey.getKey();
			if ("size".equals(tkey.getKey())) {
				BigDecimal bigd = new BigDecimal(tkey.getValue());
				BigDecimal unit = new BigDecimal("1073741824");
				bigd = bigd.divide(unit, 0);
				data[i][1] = bigd.toString() + "G";
			} else
				data[i][1] = tkey.getValue();
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	public static List<KV> jsonToArray(org.json.JSONObject obj) {
		List<KV> returnA = new ArrayList<KV>();
		if ((null != obj) && (obj.keySet().size() > 0)) {
			Iterator<?> keys = obj.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				KV kv = new KV();
				kv.setKey(key);
				kv.setValue(obj.optString(key));
				returnA.add(kv);
			}
		}
		return returnA;
	}

	public static List<KV> jsonaryToArray(org.json.JSONArray obj) {
		List<KV> returnA = new ArrayList<KV>();
		if ((null != obj) && (obj.length() > 0)) {
			for (int i = 0; i < obj.length(); i++) {
				org.json.JSONObject tk = obj.getJSONObject(i);
				if ((null != obj) && (tk.keySet().size() > 0)) {
					Iterator<?> keys = tk.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						KV kv = new KV();
						kv.setKey(key);
						kv.setValue(tk.optString(key));
						returnA.add(kv);
					}
				}
			}
		}
		return returnA;
	}

	public static AsciiTableEntity goHasSecurityShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "Enabled" };
		String[][] data = new String[jsonArray.length()][2];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = obj.getString("name");
			data[i][1] = String.valueOf(obj.getBoolean("enabled"));
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	public static AsciiTableEntity goHasShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name" };
		String[][] data = new String[jsonArray.length()][1];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			data[i][0] = mask(obj.getString("name"));
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	public static AsciiTableEntity goHasStorageShowResult(JSONArray jsonArray, AsciiTableEntity en) {
		String[] headers = { "Name", "Size", "Status", "Attachment" };
		String[][] data = new String[jsonArray.length()][4];
		int i = 0;
		for (Object tempobj : jsonArray) {
			org.json.JSONObject obj = (org.json.JSONObject) tempobj;
			String tempname = obj.getString("name");
			data[i][0] = mask(tempname);
			BigDecimal bigd = new BigDecimal(obj.getString("size"));
			BigDecimal unit = new BigDecimal("1073741824");
			bigd = bigd.divide(unit, 0);
			data[i][1] = bigd.toString() + "G";
			data[i][2] = obj.getString("status");
			if (null == storageAttachmentRelationMap) {
				data[i][3] = "ERROR";
			} else {
				KV kv = storageAttachmentRelationMap.get(mask(obj.optString("name")));
				if (null != kv)
					data[i][3] = "Attached";
				else
					data[i][3] = "";
			}
			i++;
		}
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static String mask(String tempname) {
		return tempname.replaceAll(
				"/" + BasicAuthenticationAPI.CLOUD_UNDOMAIN + "/" + BasicAuthenticationAPI.CLOUD_USERNAME + "/", "...");
	}

	private static AsciiTableEntity goHasMessage(String message) {
		AsciiTableEntity en = new AsciiTableEntity();
		String[] headers = { "Name" };
		String[][] data = { { message } };
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	private static AsciiTableEntity goHasName(String strname) {
		AsciiTableEntity en = new AsciiTableEntity();
		String[] headers = { "Name" };
		String[][] data = { { strname } };
		en.setHeader(headers);
		en.setData(data);
		return en;
	}

	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String encrypted(String input) throws Exception {
		return encrypt(input);
	}

	public static String decrypted(String input) throws Exception {
		return decrypt(input);
	}

	public static boolean checkAvailable(String accountstr) throws Exception {
		String source = decrypted(accountstr);
		// log.debug("source=" + source);
		Map<String, ? extends PromtResultItemIF> map1 = (Map<String, ? extends PromtResultItemIF>) JSON
				.parseObject(source);
		if (null != map1)
			return true;
		return false;
	}

	public static org.json.JSONObject getSucReturn(String string) {
		org.json.JSONObject j = new org.json.JSONObject();
		if (!StringUtils.isBlank(string)) {
			if (string.length() > 10)
				j.put("message", "操作完毕，请再次校验，输出内容：" + string.substring(1, 9));
			else
				j.put("message", "操作完毕，请再次校验，输出内容：" + string);
			j.put("biureturn", string);			
		} else
			j.put("message", "操作完毕，请再次校验，输出内容：" + string);
		return j;
	}
	
	public static org.json.JSONObject getSucReturn(String string, org.json.JSONObject biureturn) {
		org.json.JSONObject j = new org.json.JSONObject();
		if (!StringUtils.isBlank(string)) {
			if (string.length() > 10)
				j.put("message", "操作完毕，请再次校验，输出内容：" + string.substring(1, 9));
			else
				j.put("message", "操作完毕，请再次校验，输出内容：" + string);
			j.put("biureturn", biureturn);
		} else
			j.put("message", "操作完毕，请再次校验，输出内容：" + string);
		return j;
	}	

	public static org.json.JSONObject getErrReturn(String string, String message) {
		org.json.JSONObject j = new org.json.JSONObject();
		j.put("message", "操作失败原因[" + string + "]：" + message);
		return j;
	}
	
	public static org.json.JSONObject getErrReturn(String string, String message, boolean withhead) {
		org.json.JSONObject j = new org.json.JSONObject();
		j.put("message", "操作失败原因[" + string + "]：" + message);
		j.put("biureturn", message);
		return j;
	}

	/**
	 * 计算两个时间之间的差值，根据标志的不同而不同
	 * 
	 * @param flag
	 *            计算标志，表示按照年/月/日/时/分/秒等计算
	 * @param calSrc
	 *            减数
	 * @param calDes
	 *            被减数
	 * @return 两个日期之间的差值
	 */
	public static int dateDiff(Date date1, Date date2) {
		// 日期相减得到相差的日期
		long minu = (date1.getTime() - date2.getTime()) / (60 * 1000) > 0
				? (date1.getTime() - date2.getTime()) / (60 * 1000)
				: (date2.getTime() - date1.getTime()) / (60 * 1000);

		return (int) minu;
	}
}
