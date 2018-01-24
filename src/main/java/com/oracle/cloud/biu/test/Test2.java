package com.oracle.cloud.biu.test;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;

public class Test2 {

	public static void main(String[] args) {
//		String source = "file_chunk/393638992/ORCLBIU/backuppiece/2018-01-12/01sofpeg_1_1/KTVs8w9V0wwD/0000000001";
//		String t = ".{0,100}/.{0,100}/.{0,100}";
//		String a = "file_chunk/%/2018-01-12/";
//		a = a.replaceAll("%", t);
//		String b = "";
//		b = source.replaceAll(a, "");
//		System.out.println(b);
		
//		String source = "file_chunk/393393369/ORCLBIU/backuppiece/Koma、黄伯、黑仔、四眼仔";
//		String a = "file_chunk\\/.{0,100}/.{0,100}/backuppiece";
//		String b = "";
//		b = source.replaceAll(a, "");
//		System.out.println(b);
		
//		String d = "2018-01-10";
//		try {
//			Date p = DateUtils.parseDate(d, "yyyy-MM-dd");
//			SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd");
//		    String datestr = sdf.format(p); 
//		    System.out.println(datestr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
//		UUID u = UUID.randomUUID();
//		System.out.println(u);
//		System.out.println(ac);
		
		List<String> a = new ArrayList<String>();
		a.add("2018-01-16");
		a.add("2018-01-10");
		a.add("2018-01-12");
		a.add("2018-01-23");
		a.add("2018-01-13");
		Collections.sort(a, Collator.getInstance(java.util.Locale.CHINA));
		for (String string : a) {
			System.out.println(string);
		}
	}
	
}
