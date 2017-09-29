package com.oracle.cloud.biu.entity;

import lombok.Data;

@Data
public class AsciiTableEntity {

	public String[] header;
	public String[][] data;
	
}
