package com.italia.ipos.utils;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */

import java.text.DecimalFormat;

public class Numbers {

	public Numbers(){}
	
	/**
	 * utils to return two digits e.g 87.69999694824219 become 87.70
	 * @param value
	 * @return
	 */
	public static double formatDouble(double value){
		try{
		DecimalFormat df = new DecimalFormat("####0.00");
		value = Double.valueOf(df.format(value));
		}catch(Exception e){System.out.println("Error in formatDouble for value : " + value + " error : " + e.getMessage());}
		return value;
	}
	
}
