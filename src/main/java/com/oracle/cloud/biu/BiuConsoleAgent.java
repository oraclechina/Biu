package com.oracle.cloud.biu;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jakewharton.fliptables.FlipTable;
import com.oracle.cloud.biu.api.StorageAPI;
import com.oracle.cloud.biu.entity.AsciiTableEntity;
import com.oracle.cloud.biu.entity.KV;
import com.oracle.cloud.biu.utils.BiuUtils;
import com.oracle.cloud.biu.utils.InvokeUtils;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Data
@Log4j
public class BiuConsoleAgent {

	public static Object runCommand(JSONObject parentobj, JSONObject srcobj, String... args) {
		log.debug("================ Execute Debug =================");
		Object result = null;
		try {
		    Class[] paramTypes = new Class[1];
		    Object[] params = new Object[1];
		    paramTypes[0] = String.class;
		    params[0] = parentobj.getString("endpoint");

		    String method = "com.oracle.cloud.biu.api." + parentobj.getString("name");
		    log.debug("Invoke Method:" + method);
			result = InvokeUtils.exec(method, paramTypes, params);
//		    log.debug("[OUT]" + result);
		    AsciiTableEntity en = BiuUtils.toAsciiTable(method, (org.json.JSONObject) result);
		    BiuUtils.print(en);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}	
	
	public static Object runCommand(JSONObject parentobj, JSONObject srcobj, List<KV> kvlist, String... args) {
		log.debug("================ Execute Debug =================");
		Object result = null;
		try {
			if ((StringUtils.isEmpty(srcobj.getJSONObject(kvlist.get(0).getKey()).toJSONString())) || ("{}".equals(srcobj.getJSONObject(kvlist.get(0).getKey()).toJSONString())))
				return null;

		    Class[] paramTypes = new Class[kvlist.size() + 1];
		    Object[] params = new Object[kvlist.size() + 1];
		    paramTypes[0] = String.class;
		    params[0] = parentobj.getString("endpoint");
		    if ((null != kvlist) && (kvlist.size() > 0)) {
		    	for (int i = 0; i < kvlist.size(); i++) {
		    		KV kv = kvlist.get(i);
		    		paramTypes[i + 1] = String.class;
		    		params[i + 1] = srcobj.getJSONObject(kv.getKey()).getString(kv.getValue());
				}
		    }
		    
		    String method = "com.oracle.cloud.biu.api." + parentobj.getString("name");
		    log.debug("Invoke Method:" + method);
		    for (Object object : params) {
		    	log.debug("Invoke Params:" + object);
			}
			result = InvokeUtils.exec(method, paramTypes, params);
//		    log.debug("[OUT]" + result);
		    AsciiTableEntity en = BiuUtils.toAsciiTable(method, (org.json.JSONObject) result);
		    BiuUtils.print(en);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

//	public static void executeStorageCreateBoot(JSONObject parentobj, JSONObject selfobj, List<KV> kvlist) throws Exception {
//		log.debug(parentobj.getString("name"));
//		log.debug(parentobj.getString("endpoint"));
//		if ((null != kvlist) && (kvlist.size() > 0)) {
//			JSONObject pobj = selfobj.getJSONObject(kvlist.get(0).getKey());
//			log.debug("self name:" + kvlist.get(0).getKey());
//			log.debug("self value:" + pobj.getString(kvlist.get(0).getValue()));
//			StorageAPI.createStorageBootVolumns(pobj.getString(kvlist.get(0).getValue()), parentobj.getString("endpoint"));
//		}
//	}
//	
//	public static void executeStorageCreateBlank(JSONObject parentobj, JSONObject selfobj, List<KV> kvlist) throws Exception {
//		log.debug(parentobj.getString("name"));
//		log.debug(parentobj.getString("endpoint"));
//		if ((null != kvlist) && (kvlist.size() > 0)) {
//			JSONObject pobj = selfobj.getJSONObject(kvlist.get(0).getKey());
//			log.debug("self name:" + kvlist.get(0).getKey());
//			log.debug("self value:" + pobj.getString(kvlist.get(0).getValue()));
//			StorageAPI.createStorageBlankVolumns(pobj.getString(kvlist.get(0).getValue()), parentobj.getString("endpoint"));
//		}
//	}

}
