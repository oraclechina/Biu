package com.oracle.cloud.biu.test;

import com.jakewharton.fliptables.FlipTable;

public class MyTest {
	
    public String hello(String param){  
        return "Hello " + param;  
    }
    
    public static void main(String[] args) {
    	String[] headers = { "Test", "Header" };
    	String[][] data = {
    	    { "Foo", "Bar" },
    	    { "Kit", "Kat" },
    	};
    	System.out.println(FlipTable.of(headers, data));
	}
}