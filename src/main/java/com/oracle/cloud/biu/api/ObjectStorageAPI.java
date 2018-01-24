package com.oracle.cloud.biu.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.oracle.cloud.biu.Biu;
import com.oracle.cloud.biu.entity.KV;

import lombok.extern.log4j.Log4j;
import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.model.Container;
import oracle.cloud.storage.model.Key;
import oracle.cloud.storage.model.QueryOption;
import oracle.cloud.storage.model.QueryResult;

@Log4j
public class ObjectStorageAPI {
	
	public static CloudStorage connection = null;
	
	static {
		if (null == connection) {
			try {
				Biu.initObjectStorage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteContainer(String path, String conname) {
		connection.deleteContainer(conname);
	}
	
	public static void deleteObjects(String path, String conname, String fc, String blurtiercnt, String key) {
		if (StringUtils.isBlank(fc)) {
			connection.deleteObject(conname, key);
			return;
		}
		JSONObject jsonobj = listObjects(path, conname, fc, blurtiercnt, "G", "list");
		JSONObject t1 = null;
		JSONArray ary1 = jsonobj.getJSONArray("result");
		for (Object object : ary1) {
			t1 = (JSONObject) object;
			deleteObjects(path, conname, null, null, t1.optString("name"));
		}
	}
	
	public static JSONObject listContainer(String path, String fc) {
		List<Container> clist = new ArrayList<Container>();
		Map<String, String> blacklist = new HashMap<String, String>();

		if ("all".equals(fc)) {
			clist = connection.listContainers();
		} else {
//			Map<QueryOption, String> map = new HashMap<QueryOption, String>();
//			connection.listContainers(map);
			clist = connection.listContainers();
			String[] ary1 = fc.split(",");
			for (String string : ary1) {
				blacklist.put(string, string);
			}
		}

		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();
		JSONObject t1 = null;
		for (int i = 0; i < clist.size(); i++) {
			t1 = new JSONObject();
			Container con = clist.get(i);
			t1.put("name", con.getName());
			t1.put("count", String.valueOf(con.getCount()));
			BigDecimal bd = new BigDecimal(con.getSize());
			BigDecimal bd2 = new BigDecimal(1024 * 1024 * 1024);
			BigDecimal bd3 = bd.divide(bd2);
			bd3 = bd3.setScale(1, BigDecimal.ROUND_HALF_UP);
			if (null == blacklist.get(con.getName())) {
				t1.put("size", bd3.toString() + "G");
				ary.put(t1);
			}
		}
		json.put("result", ary);
		json.optJSONArray("result");
		System.out.println("Total Container Size: " + ary.length());
		return json;
	}
	
	public static JSONObject listObjects(String path, String containername, String fc, String blurtiercnt, String unit, String model) {
		Map<QueryOption, String> map = new HashMap<QueryOption, String>();
		Map<String, KV> keymap = new HashMap<String, KV>();
//		map.put(QueryOption.DELIMITER, value);
		QueryResult qr = null;
		boolean hasblur = false;
		if (StringUtils.isBlank(fc))
			fc = "";
		if (fc.indexOf("%") > -1) {
			qr = connection.listObjectsByPath(containername, ",", "", map);
			if (!StringUtils.isBlank(blurtiercnt)) {
				if (Integer.parseInt(blurtiercnt) == 1)
					fc = fc.replaceAll("%", ".{0,100}");
				else if (Integer.parseInt(blurtiercnt) == 2)
					fc = fc.replaceAll("%", ".{0,100}/.{0,100}");
				else if (Integer.parseInt(blurtiercnt) == 3)
					fc = fc.replaceAll("%", ".{0,100}/.{0,100}/.{0,100}");
				else if (Integer.parseInt(blurtiercnt) == 4)
					fc = fc.replaceAll("%", ".{0,100}/.{0,100}/.{0,100}/.{0,100}");
				else if (Integer.parseInt(blurtiercnt) == 5)
					fc = fc.replaceAll("%", ".{0,100}/.{0,100}/.{0,100}/.{0,100}/.{0,100}");
			}
			hasblur = true;
		} else {
			qr = connection.listObjectsByPath(containername, ",", fc, map);
		}
		if (StringUtils.isBlank(unit))
			unit = "M";
		if (StringUtils.isBlank(model))
			model = "list";
		List<Key> keys = qr.getKeys();
		
		JSONObject json = new JSONObject();
		JSONArray ary = new JSONArray();
		JSONObject t1 = null;
		
		//for cluster model
		int totalcount = 0;
		
		for (int i = 0; i < keys.size(); i++) {
			t1 = new JSONObject();
			Key con = keys.get(i);
			String keystr = con.getKey();
			if (model.toUpperCase().equals("CLUSTER")) {
				if (!StringUtils.isBlank(fc))
					keystr = keystr.replaceAll(fc, "");
				if (keystr.startsWith("/"))
					keystr = keystr.substring(1);
				String[] ary2 = keystr.split("/");
				if (ary2.length > 0) {
					keystr = ary2[0];
					KV k2 = keymap.get(keystr);
					if (null == k2) {
						k2 = new KV();
						k2.setKey(keystr);
						k2.setValue(String.valueOf(con.getSize()));
					} else {
						k2.setValue(String.valueOf(Long.parseLong(k2.getValue()) + con.getSize()));
					}
					if (!hasblur)
						keymap.put(keystr.trim(), k2);
					else {
						String[] temp1 = fc.split("/");
						String[] temp2 = con.getKey().split("/");
						if ((temp1[0].equals(temp2[0])) && (con.getKey().indexOf(temp1[temp1.length - 1]) > -1)) {
							if (temp1[0].equals(temp2[0]))
								keymap.put(keystr.trim(), k2);
						}
					}
				}
			} else {
				BigDecimal bd = new BigDecimal(con.getSize());
				BigDecimal bd2;
				
				if (unit.toUpperCase().equals("M")) {
					bd2 = new BigDecimal(1024 * 1024);
				} else if (unit.toUpperCase().equals("G")) {
					bd2 = new BigDecimal(1024 * 1024 * 1024);
				} else {
					bd2 = new BigDecimal(1024 * 1024);
				}
				BigDecimal bd3 = bd.divide(bd2);
				bd3 = bd3.setScale(2, BigDecimal.ROUND_HALF_UP);
				
				if (hasblur) {
					String[] temp1 = fc.split("/");
					String[] temp2 = con.getKey().split("/");
					if ((temp1.length > 0) && (temp2.length > 0)) {
						if ((temp1[0].equals(temp2[0])) && (con.getKey().indexOf(temp1[temp1.length - 1]) > -1)) {
							t1.put("name", keystr);
							t1.put("size", bd3.toString() + unit);
							ary.put(t1);
						}
					}
				} else {
					t1.put("name", keystr);
					t1.put("size", bd3.toString() + unit);
					ary.put(t1);
				}
				
//				if (null == blacklist.get(con.getName())) {
//				}				
			}
		}
		if (model.toUpperCase().equals("CLUSTER")) {
			for (String ky : keymap.keySet()) {
				t1 = new JSONObject();
				t1.put("name", ky);
				
				BigDecimal bd = new BigDecimal(Long.parseLong(keymap.get(ky).getValue()));
				BigDecimal bd2;
				
				if (unit.toUpperCase().equals("M")) {
					bd2 = new BigDecimal(1024 * 1024);
				} else if (unit.toUpperCase().equals("G")) {
					bd2 = new BigDecimal(1024 * 1024 * 1024);
				} else {
					bd2 = new BigDecimal(1024 * 1024);
				}
				BigDecimal bd3 = bd.divide(bd2);
				bd3 = bd3.setScale(2, BigDecimal.ROUND_HALF_UP);					
				t1.put("size", bd3.toString() + unit);
				ary.put(t1);
			}
		}
		json.put("result", ary);
		System.out.println("Total Objects Size: " + ary.length());		
		return json;
	}
	
	public static void main(String[] args) {
		
	}
}
