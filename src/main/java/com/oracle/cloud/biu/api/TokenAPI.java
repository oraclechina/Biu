package com.oracle.cloud.biu.api;

import java.util.Map;

import com.oracle.cloud.biu.utils.BiuUtils;

public class TokenAPI extends Thread {

	final int TOKEN_REFRESH_DELAY = 20;
	
	@Override
	public void run() {
		Map<String, String> m = BiuUtils.getProps();
		
		while (true) {
			try {
				Thread.sleep(1000 * 60 * TOKEN_REFRESH_DELAY);
				BasicAuthenticationAPI.refreshToken(m.get("refresh"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
