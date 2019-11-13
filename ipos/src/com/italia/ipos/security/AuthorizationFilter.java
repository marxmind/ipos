package com.italia.ipos.security;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */

@WebFilter(filterName = "AuthFilter", urlPatterns={"*.xhtml"})
public class AuthorizationFilter implements Filter{

	private Serializable serializable = 80803131801l;
	
	public AuthorizationFilter(){}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException{}
	
	@Override
	public void doFilter(ServletRequest request, 
			ServletResponse response, 
			FilterChain chain) throws IOException, ServletException{
		try{
			HttpServletRequest reqt = (HttpServletRequest)request;
			HttpServletResponse resp = (HttpServletResponse) response;
			HttpSession session = reqt.getSession(false);
			
			String reqURI = reqt.getRequestURI();
			if(reqURI.indexOf("/login.xhtml")>=0 || reqURI.indexOf("/monitor.xhtml")>=0
					|| (session != null && session.getAttribute("username") !=null)
					|| reqURI.indexOf("/public/")>=0
					|| reqURI.contains("javax.faces.resource")){
				chain.doFilter(request, response);
			}else{
				resp.sendRedirect(reqt.getContextPath() + "/marxmind/login.xhtml");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void destroy(){}
}

