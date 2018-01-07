package com.oracle.cloud.biu.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.oracle.cloud.biu.Biu;
import com.oracle.cloud.biu.modules.biubiu.project.citic.ParamsVaildChecker;
import com.oracle.cloud.biu.modules.biubiu.project.citic.ServiceParams;

public class MyTest {
	
    public String hello(String param){  
        return "Hello " + param;  
    }
    
    public static void main(String[] args) {
		Biu.BIUPROFILE = "D:/project/Biu-orc/src/main/lib/biuaccount";
		Biu.main(new String[] { "_cn", "plugin" });
		
		List<String> a = new ArrayList<String>();
		a.add("oc1m");
		a.add("oc2m");
		a.add("oc3m");
		a.add("oc4m");
		String publickey = "";
		String privatekey = "";
		try {
			publickey = FileUtils.readFileToString(new File("D:\\job\\OTASK\\Oracle Cloud\\近期项目\\Others\\sshkeybundle\\publicKey.pub"));
			privatekey = FileUtils.readFileToString(new File("D:\\job\\OTASK\\Oracle Cloud\\近期项目\\Others\\sshkeybundle\\privateKey"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ServiceParams sp = ParamsVaildChecker.requestSingleCheck(1, "Oracle11.2", "oc1m", 200, "kw", "pdb1", "Welcome1!", publickey, privatekey, "utf8", a, 40);
		ServiceParams sp2 = ParamsVaildChecker.requestRACCheck(1, "Oracle11.2", "ecc1", 200, "ORCL", "pdbk", "sco", "Welcome1!#", "utf8");
		System.out.println(sp.getDescription());
		System.out.println(sp2.getDescription());
	}
}