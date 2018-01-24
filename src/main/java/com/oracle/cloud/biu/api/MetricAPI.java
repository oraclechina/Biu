package com.oracle.cloud.biu.api;

import org.json.JSONObject;

import com.mashape.unirest.http.JsonNode;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class MetricAPI {

	public JSONObject generateMetricReport(String path, String isall) throws Exception {
		path = BiuUtils.kv(path, "idDomainName", BasicAuthenticationAPI.CLOUD_DOMAIN);
		log.debug(path);
		JsonNode node = BiuUtils.rest("get", BasicAuthenticationAPI.ACCEPT_COMPUTE, path);
		log.debug(node.getObject());
		return node.getObject();
	}
	
}
