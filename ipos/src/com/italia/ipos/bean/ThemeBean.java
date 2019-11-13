package com.italia.ipos.bean;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpSession;

import com.italia.ipos.enm.Ipos;
import com.italia.ipos.reader.ReadConfig;

/**
 * 
 * @author mark italia
 * @since 04/09/2017
 * @version 1.0
 *
 */
@ApplicationScoped
@ManagedBean(name="themeBean", eager=true)
public class ThemeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 147868854437557L;

	public String getApplicationTheme(){
		String theme = "nova-light";
		try {
			System.out.println("Applying theme...");
			/*ReadApplicationDetails db = new ReadApplicationDetails();
			theme = db.getThemeStyle();*/
			
				HttpSession session = SessionBean.getSession();
				theme = session.getAttribute("theme").toString();
			
			System.out.println("Theme " + theme + " has been applied...");}catch(Exception e){}
		return theme;
	}
	
	public String getmobileTheme(){
		String theme = "none";
		System.out.println("Applying theme...");
		try{theme = ReadConfig.value(Ipos.MOBILE_THEME);
		System.out.println("Theme " + theme + " has been applied...");}catch(Exception e){}
		return theme;
	}
	
}
