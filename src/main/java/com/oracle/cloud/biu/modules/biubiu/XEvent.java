package com.oracle.cloud.biu.modules.biubiu;

import java.util.List;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class XEvent {

	String type;
	String sid;
	
	public XEvent(String type, String sid) {
		this.type = type;
		this.sid = sid;
	}
}
