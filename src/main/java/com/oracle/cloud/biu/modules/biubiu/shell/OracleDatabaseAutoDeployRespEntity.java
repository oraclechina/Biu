package com.oracle.cloud.biu.modules.biubiu.shell;

import com.oracle.cloud.biu.modules.biubiu.RespEntity;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class OracleDatabaseAutoDeployRespEntity extends RespEntity {

	private String publicIP;
	private String port;
	private String respMsg;
	
}
