package com.italia.ipos.application;

import com.italia.ipos.enm.Ipos;
import com.italia.ipos.reader.ReadConfig;
/**
 * 
 * @author mark italia
 * @since created 10/05/2016
 * @version 1.0
 *
 */
public class Company {
	
	/**
	 * This method use for lock and unlock specific pages/menu/tag/component and alike
	 * @return
	 */
	public static boolean validateCompanyType(){
		String businessType = ReadConfig.value(Ipos.APP_BUSINESS_TYPE);
		if("0".equalsIgnoreCase(businessType)){
			return false;
		}else if("1".equalsIgnoreCase(businessType)){
			return true;
		}
		return false;
	}
	
	
}
