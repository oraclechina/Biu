package com.oracle.cloud.biu.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class KV implements Serializable {

	private static final long serialVersionUID = -1018662537671524460L;

	private String key;
	private String value;
	
}
