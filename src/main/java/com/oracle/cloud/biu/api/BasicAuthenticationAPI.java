package com.oracle.cloud.biu.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.oracle.cloud.biu.utils.BiuUtils;
import com.thoughtworks.xstream.XStream;

import lombok.extern.log4j.Log4j;

@Log4j
public class BasicAuthenticationAPI {

	
	public static String METRICENDPOINT = "https://monitoring.us.oraclecloud.com/";
	public static String ENDPOINT = "";
	public static String STORAGEENDPOINT = "";
	public static String ACCEPT_COMPUTE = "application/oracle-compute-v3+json";
	public static String ACCEPT_COMPUTE_DIR = "application/oracle-compute-v3+directory+json";
	public static String CONTENT_TYPE = "application/oracle-compute-v3+json";
	public static String COMMON_CONTENT_TYPE = "application/json";
	public static String CLOUD_USERNAME = "";
	public static String CLOUD_DOMAIN = "";
	public static String CLOUD_PASSWORD = "";
	public static String CLOUD_UNDOMAIN = "/Compute-" + CLOUD_DOMAIN;
	public static String COOKIE = "";

	public static void setUserInfoForLogin(String storageendpoint, String endpoint, String domain, String cloud_username, String cloud_password) {
		ENDPOINT = endpoint;
		STORAGEENDPOINT = storageendpoint;
		CLOUD_DOMAIN = domain;
		CLOUD_USERNAME = cloud_username;
		CLOUD_PASSWORD = cloud_password;
		CLOUD_UNDOMAIN = "/Compute-" + CLOUD_DOMAIN;
		log.debug("=============Login===========");
		log.debug("ENDPOINT:" + ENDPOINT);
		log.debug("STORAGE_ENDPOINT:" + STORAGEENDPOINT);
		log.debug("CLOUD_DOMAIN:" + CLOUD_DOMAIN);
		log.debug("CLOUD_USERNAME:" + CLOUD_USERNAME);
		log.debug("CLOUD_PASSWORD:" + CLOUD_PASSWORD);
	}
	
	public static void login(String path) throws Exception {
		log.debug("===========Rest Endpoint========");
		log.debug("[POST]--->" + ENDPOINT + path);
		log.debug("[HEAD]--->" + CONTENT_TYPE);
		log.debug("[---AUTH]--->" + CLOUD_UNDOMAIN + "/" + CLOUD_USERNAME);
		log.debug("[---PSWD]--->" + CLOUD_PASSWORD);
		log.debug("[BODY]--->" + "{ \"password\": \"" + CLOUD_PASSWORD + "\", \"user\": \"" + CLOUD_UNDOMAIN + "/" + CLOUD_USERNAME + "\"}");
		HttpResponse<JsonNode> hr = Unirest.post(ENDPOINT + path).header("Accept", ACCEPT_COMPUTE)
				.header("Content-Type", CONTENT_TYPE).basicAuth("/" + CLOUD_UNDOMAIN + "/" + CLOUD_USERNAME, CLOUD_PASSWORD)
				.body("{ \"password\": \"" + CLOUD_PASSWORD + "\", \"user\": \"" + CLOUD_UNDOMAIN + "/" + CLOUD_USERNAME + "\"}").asJson();
		log.debug("[RESP]<---" + hr);
//		log.debug("[DETL]<---" + new XStream().toXML(hr));
		COOKIE = hr.getHeaders().getFirst("Set-Cookie");
		log.debug("[COOK]<---" + COOKIE);
	}
	
	public static void loginMetric(String path) throws Exception {
		log.debug("===========Metric Rest Endpoint========");
		log.debug("[POST]--->" + METRICENDPOINT + path);
		HttpResponse<JsonNode> hr = Unirest.get(METRICENDPOINT + path).header("Content-Type", COMMON_CONTENT_TYPE).header("X-ID-TENANT-NAME", CLOUD_DOMAIN).basicAuth(CLOUD_USERNAME, CLOUD_PASSWORD).asJson();
		log.debug("[RESP]<---" + hr);
		System.out.println(hr.getBody());
//		log.debug("[DETL]<---" + new XStream().toXML(hr));
		log.debug("[COOK]<---" + COOKIE);
	}

	public static void refreshToken(String path) throws Exception {
		HttpResponse<JsonNode> hr = BiuUtils.rest(path);
		COOKIE = hr.getHeaders().getFirst("Set-Cookie");
		log.debug("[REFRESH] Token refreshed.");
	}
}
