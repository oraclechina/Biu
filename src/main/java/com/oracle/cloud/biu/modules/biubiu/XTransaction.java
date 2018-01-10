package com.oracle.cloud.biu.modules.biubiu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.oracle.cloud.biu.modules.biubiu.citic.Biu_CITIC_SDK_Desc;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class XTransaction {

	String xid;
	
	Biu_CITIC_SDK_Desc desc;
	
	List<XEvent> events = new ArrayList<XEvent>();
	
}
