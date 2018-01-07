package com.oracle.cloud.biu.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import lombok.extern.log4j.Log4j;

@Log4j
public class BasicAuthenticationAPI {

	public static String ENDPOINT = "";
	public static String ACCEPT_COMPUTE = "application/oracle-compute-v3+json";
	public static String ACCEPT_COMPUTE_DIR = "application/oracle-compute-v3+directory+json";
	public static String CONTENT_TYPE = "application/oracle-compute-v3+json";
	public static String CLOUD_USERNAME = "";
	public static String CLOUD_DOMAIN = "";
	public static String CLOUD_PASSWORD = "";
	public static String CLOUD_TENANT = "";
	public static String CLOUD_UNDOMAIN = "/Compute-" + CLOUD_DOMAIN;
	public static String COOKIE = "";

	public static void setUserInfoForLogin(String tenant, String endpoint, String domain, String cloud_username, String cloud_password) {
		ENDPOINT = endpoint;
		CLOUD_DOMAIN = domain;
		CLOUD_USERNAME = cloud_username;
		CLOUD_PASSWORD = cloud_password;
		CLOUD_TENANT = tenant + "_";
		CLOUD_UNDOMAIN = "/Compute-" + CLOUD_DOMAIN;
		log.debug("=============Login===========");
		log.debug("ENDPOINT:" + ENDPOINT);
		log.debug("CLOUD_DOMAIN:" + CLOUD_DOMAIN);
		log.debug("CLOUD_TENANT:" + CLOUD_TENANT);
		log.debug("CLOUD_USERNAME:" + CLOUD_USERNAME);
		log.debug("CLOUD_PASSWORD:" + CLOUD_PASSWORD);
	}
	
	public static void login(String path) throws Exception {
		HttpResponse<JsonNode> hr = Unirest.post(ENDPOINT + path).header("Accept", ACCEPT_COMPUTE)
				.header("Content-Type", CONTENT_TYPE).basicAuth("/" + CLOUD_UNDOMAIN + "/" + CLOUD_USERNAME, CLOUD_PASSWORD)
				.body("{ \"password\": \"" + CLOUD_PASSWORD + "\", \"user\": \"" + CLOUD_UNDOMAIN + "/" + CLOUD_USERNAME + "\"}").asJson();
		COOKIE = hr.getHeaders().getFirst("Set-Cookie");
	}

}
