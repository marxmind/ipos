package com.italia.ipos.utils;
/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */
public class Whitelist {

	public static String remove(String val){
		if(val==null) return null;
		val = val.replace("--", "");
		return val;
	}
	
}
