package com.oracle.cloud.biu.test;

public class Test2 {

	public static void main(String[] args) {
		String source = "sbt_catalog/c-393314600-20180109-01/metadata.xml";
		String t = ".{0,100}/.{0,100}";
		String a = "file_chunk/%/backuppiece";
		a = a.replaceAll("%", t);
		String b = "";
		b = source.replaceAll(a, "");
		System.out.println(b);
		
//		String source = "file_chunk/393393369/ORCLBIU/backuppiece/Koma、黄伯、黑仔、四眼仔";
//		String a = "file_chunk\\/.{0,100}/.{0,100}/backuppiece";
//		String b = "";
//		b = source.replaceAll(a, "");
//		System.out.println(b);
	}
	
}
