package com.italia.ipos.session;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.italia.ipos.bean.SessionBean;

/**
 * 
 * @author mark italia
 * @since 10/01/2016
 * @version 1.0
 *
 */
public class IBean {

	/**
	 * Remove and invalidate user session
	 */
	public static void removeBean(){
		try{
		HttpSession session = SessionBean.getSession();
		String[] beans = {
				"auserBean",
				"auomBean",
				"aprodBean",
				"menuBean",
				"posBean",
				"productBean,"
				+ "featuresBean"
				};
		for(String bean : beans){
			FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(bean);
		}
		session.invalidate();
		}catch(Exception e){}
	}
	
}
