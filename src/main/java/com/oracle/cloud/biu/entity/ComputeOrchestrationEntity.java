package com.oracle.cloud.biu.entity;

import java.util.ArrayList;
import java.util.List;

import com.oracle.cloud.biu.Biu;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
public class ComputeOrchestrationEntity {

	String _prefix = "";
	
	public ComputeOrchestrationEntity(String prefix) {
		this._prefix = prefix;
	}
	
	String computetemplate = "{\"description\":\"%description%\",\"name\":\"%finalorchname%\",\"desired_state\":\"active\",\"tags\":[\"%tenant%\"],\"objects\":[%buildssh%,%buildbootvolumn%,%builddatavolumn%,%buildipreservation%,%buildinstance%]}";
	String bootstoragevolumntemplate = "{\"type\":\"StorageVolume\",\"label\":\"%bootstoragelabel%\",\"description\":\"Boot disk for your instance\",\"bootable\":true,\"persistent\":true,\"template\":{\"properties\":[\"/oracle/public/storage/latency\"],\"size\":\"%storagesize%G\",\"imagelist\":\"%bootos%\",\"name\":\"%storagename%\"}}";
	String datastoragevolumntemplate = "{\"type\":\"StorageVolume\",\"label\":\"%datastoragelabel%\",\"description\":\"Data disk for your instance\",\"persistent\":true,\"template\":{\"properties\":[\"/oracle/public/storage/latency\"],\"size\":\"%storagesize%G\",\"name\":\"%storagename%\"}}";
	String sshkeytemplate = "{\"type\":\"SSHKey\",\"label\":\"%sshlabel%\",\"template\":{\"enabled\":true,\"key\":\"%sshpublickey%\",\"name\":\"%sshkeyname%\"}}";
	String ipreservationtemplate = "{\"type\":\"IPReservation\",\"label\":\"%ipreservation%\",\"description\":\"IP reservation for your instance\",\"persistent\":true,\"template\":{\"parentpool\":\"%public_ip_pool%\",\"name\":\"%ipreservationname%\",\"permanent\":true}}";
	String secapptemplate = "";
	String secruletemplate = "";
	String seclisttemplate = "";
	String instancetemplate = "{\"type\":\"Instance\",\"description\":\"%insdesc%\",\"label\":\"%inslabel%\",\"template\":{\"label\":\"%inslabel%\",\"name\":\"%insname%\",\"shape\":\"%insshape%\",\"imagelist\":\"%insos%\",\"networking\":{\"eth0\":{\"nat\":\"ipreservation:{{%ipreservation%:name}}\"}},\"storage_attachments\":[{\"index\":1,\"volume\":\"{{%bootstoragelabel%:name}}\"},{\"index\":2,\"volume\":\"{{%datastoragelabel%:name}}\"}],\"boot_order\":[1],\"sshkeys\":[\"{{%sshlabel%:name}}\"]}}";
	
	public String build(String tenant, String subtenant, String insdesc, String inslabel, String insname, String description, String finalorchname, String insshape, String insos, String sshlabel, String sshpublickey, String sshkeyname, String bootstoragelabel, String bootstoragename, String bootstoragesize, String bootos, String datastoragelabel, String datastoragesize, String datastoragename, String ipreservation, String ipreservationname) {
		buildSSH(sshlabel, sshpublickey, sshkeyname);
		buildBootStorageVolumn(bootstoragelabel, bootstoragename, bootstoragesize, bootos);
		buildDataStorageVolumn(datastoragelabel, datastoragesize, datastoragename);
		buildIPReservation(ipreservation, ipreservationname);
		buildInstance(insdesc, inslabel, insname, insshape, insos, ipreservation, bootstoragelabel, datastoragelabel, sshlabel);
		computetemplate = computetemplate.replaceAll("%finalorchname%", _prefix + finalorchname);
		computetemplate = computetemplate.replaceAll("%description%", description);
		computetemplate = computetemplate.replaceAll("%tenant%", tenant);
		computetemplate = computetemplate.replaceAll("%buildssh%", sshkeytemplate);
		computetemplate = computetemplate.replaceAll("%buildbootvolumn%", bootstoragevolumntemplate);
		computetemplate = computetemplate.replaceAll("%builddatavolumn%", datastoragevolumntemplate);
		computetemplate = computetemplate.replaceAll("%buildipreservation%", ipreservationtemplate);
		computetemplate = computetemplate.replaceAll("%buildinstance%", instancetemplate);
//		computetemplate = computetemplate.replaceAll("%buildsecapp%", secapptemplate);
//		computetemplate = computetemplate.replaceAll("%buildsecrule%", secruletemplate);
//		computetemplate = computetemplate.replaceAll("%buildseclist%", seclisttemplate);
		log.debug("================ Build Compute Orchestration Play Book  ===============");
		log.debug(computetemplate);
		return computetemplate;
	}

	private void buildInstance(String insdesc, String inslabel, String insname, String insshape, String insos, String ipreservation, String bootstoragelabel, String datastoragelabel, String sshlabel) {
		instancetemplate = instancetemplate.replaceAll("%insdesc%", insdesc);
		instancetemplate = instancetemplate.replaceAll("%inslabel%", inslabel);
		instancetemplate = instancetemplate.replaceAll("%insname%", _prefix + insname);
		instancetemplate = instancetemplate.replaceAll("%insshape%", insshape);
		instancetemplate = instancetemplate.replaceAll("%insos%", insos);
		instancetemplate = instancetemplate.replaceAll("%ipreservation%", ipreservation);
		instancetemplate = instancetemplate.replaceAll("%bootstoragelabel%", bootstoragelabel);
		instancetemplate = instancetemplate.replaceAll("%datastoragelabel%", datastoragelabel);
		instancetemplate = instancetemplate.replaceAll("%sshlabel%", sshlabel);
	}

	private void buildIPReservation(String ipreservation, String ipreservationname) {
		ipreservationtemplate = ipreservationtemplate.replaceAll("%ipreservation%", ipreservation);
		ipreservationtemplate = ipreservationtemplate.replaceAll("%ipreservationname%", _prefix + ipreservationname);
		ipreservationtemplate = ipreservationtemplate.replaceAll("%public_ip_pool%", Biu.PUBLIC_IP_POOL);		
	}

	private void buildDataStorageVolumn(String datastoragelabel, String datastoragesize, String datastoragename) {
		datastoragevolumntemplate = datastoragevolumntemplate.replaceAll("%datastoragelabel%", datastoragelabel);
		datastoragevolumntemplate = datastoragevolumntemplate.replaceAll("%storagesize%", datastoragesize);
		datastoragevolumntemplate = datastoragevolumntemplate.replaceAll("%storagename%", _prefix + datastoragename);
	}

	private void buildBootStorageVolumn(String bootstoragelabel, String bootstoragename, String bootstoragesize, String bootos) {
		bootstoragevolumntemplate = bootstoragevolumntemplate.replaceAll("%bootstoragelabel%", bootstoragelabel);
		bootstoragevolumntemplate = bootstoragevolumntemplate.replaceAll("%storagesize%", bootstoragesize);
		bootstoragevolumntemplate = bootstoragevolumntemplate.replaceAll("%bootos%", bootos);
		bootstoragevolumntemplate = bootstoragevolumntemplate.replaceAll("%storagename%", _prefix + bootstoragename);
	}

	private void buildSSH(String sshlabel, String sshpublickey, String sshkeyname) {
		sshkeytemplate = sshkeytemplate.replaceAll("%sshlabel%", sshlabel);
		sshkeytemplate = sshkeytemplate.replaceAll("%sshpublickey%", sshpublickey);
		sshkeytemplate = sshkeytemplate.replaceAll("%sshkeyname%", _prefix + sshkeyname);
	}
	
}
