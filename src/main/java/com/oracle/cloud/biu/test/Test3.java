package com.oracle.cloud.biu.test;

import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.api.MetricAPI;

public class Test3 {

	public static void main(String[] args) {
		try {
			BasicAuthenticationAPI.CLOUD_DOMAIN = "gdrcb";
			BasicAuthenticationAPI.CLOUD_USERNAME = "1974705@qq.com";
			BasicAuthenticationAPI.CLOUD_PASSWORD = "!QAZ2wsx";
			BasicAuthenticationAPI.loginMetric("monitoring/"+BasicAuthenticationAPI.CLOUD_DOMAIN+"/.customer/api/v1/metricTypes/compute.MEMORY.CURRENT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
