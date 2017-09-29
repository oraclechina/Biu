package com.oracle.cloud.biu.entity;

import java.util.List;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class RestPoint {

	String name;
	String endpoint;
	String desc;
	String prompt;
	List<RestPointEntity> subcommand;
	
}
