package com.oracle.cloud.biu.modules.biubiu.project.citic;

public class SSHkeyChecker {

	public static boolean checkPublicKey(String sshpublickey) {
		if (sshpublickey.trim().startsWith("ssh-rsa"))
			return true;
		return false;
	}

	public static boolean checkPrivateKey(String sshprivatekey) {
		boolean re1 = false;
		boolean re2 = false;
		if (sshprivatekey.trim().startsWith("-----BEGIN RSA PRIVATE KEY-----"))
			re1 = true;
		if (sshprivatekey.trim().indexOf("-----END RSA PRIVATE KEY-----") > 0)
			re2 = true;
		if ((re1) && (re2))
			return true;
		return false;
	}
	
}
