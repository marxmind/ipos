package com.italia.ipos.bean;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
/**
 * 
 * @author mark italia
 * @since created 09/27/2016
 *
 */
public class SessionBean {

	private static final long serialVersionUID = 1094801825228386363L;
	
	public static HttpSession getSession(){
		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}
	public static HttpServletRequest getRequest(){
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	public static String getUserName(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		return session.getAttribute("username").toString();
	}
	public static String getUserId(){
		HttpSession session = getSession();
		if(session != null){
			return (String)session.getAttribute("userid");
		}else{
			return null;
		}
	}
	
}
