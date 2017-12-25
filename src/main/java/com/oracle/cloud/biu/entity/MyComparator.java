package com.oracle.cloud.biu.entity;

import java.util.Comparator;

public class MyComparator implements Comparator {
	 
	//接口，必须实现的方法
	public int compare(Object o1, Object o2) {
		KV kv1 = (KV) o1;
		KV kv2 = (KV) o2;
		if (Integer.parseInt(kv1.getReserve2()) > Integer.parseInt(kv2.getReserve2()))
			return 1;
		else
			return -1;
	}
}
