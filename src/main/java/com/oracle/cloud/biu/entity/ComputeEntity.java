package com.oracle.cloud.biu.entity;

import java.util.ArrayList;
import java.util.List;

import com.oracle.cloud.biu.api.BasicAuthenticationAPI;
import com.oracle.cloud.biu.utils.BiuUtils;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class ComputeEntity {

	private List<ComputeInstanceEntity> instances = new ArrayList<>();
	private List<String> tags = new ArrayList<>();
	
	public static String build(String label, String name, String shape, String sshkeys, String imagelist) {
		ComputeEntity ce = new ComputeEntity();
		ComputeInstanceEntity cie = new ComputeInstanceEntity();
		cie.setLabel(label);
		cie.setName(name);
		cie.setShape(shape);
		cie.setImagelist(imagelist);
		cie.sshkeys.add(sshkeys);
		ce.instances.add(cie);
		ce.tags.add(BasicAuthenticationAPI.CLOUD_TENANT);
		String result = BiuUtils.toJson(ce);
		return result;
	}
	
	public static void test() {
		ComputeEntity ce = new ComputeEntity();
		ComputeInstanceEntity cie = new ComputeInstanceEntity();
		cie.setLabel("test1");
		cie.setName("/Compute-acme/jack.jones@example.com/dev-vm");
		cie.setShape("oc3");
		cie.setImagelist("/oracle/public/oel_6.4_2GB_v1");
		cie.sshkeys.add("/Compute-acme/jack.jones@example.com/dev-key1");
		ce.instances.add(cie);
		String result = BiuUtils.toJson(ce);
		log.debug("===========lanchplan==========");
		log.debug(result);
	}
	
	public static void main(String[] args) {
		ComputeEntity.test();
	}
}
