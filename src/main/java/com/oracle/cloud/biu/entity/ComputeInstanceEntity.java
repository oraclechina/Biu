package com.oracle.cloud.biu.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class ComputeInstanceEntity {

	String shape;
	String imagelist;
	String name;
	String label;
	List<String> sshkeys = new ArrayList<>();
	
}
