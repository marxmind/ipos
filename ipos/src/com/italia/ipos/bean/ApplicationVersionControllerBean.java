package com.italia.ipos.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

import com.italia.ipos.application.ApplicationFixes;
import com.italia.ipos.application.ApplicationVersionController;
import com.italia.ipos.security.Copyright;
import com.italia.ipos.security.License;

/**
 * 
 * @author mark italia
 * @since 09/28/2016
 * @version 1.0
 */

@ManagedBean(name="versionBean", eager=true)
public class ApplicationVersionControllerBean {

	private static final long serialVersionUID = 1394801825228386363L;
	
	private ApplicationVersionController versionController;
	private ApplicationFixes applicationFixes;
	private List<ApplicationFixes> fixes = Collections.synchronizedList(new ArrayList<ApplicationFixes>());
	private Copyright copyright;
	private List<License> licenses = Collections.synchronizedList(new ArrayList<License>());
	
	@PostConstruct
	public void init(){
		 
		String sql = "SELECT * FROM app_version_control ORDER BY timestamp DESC LIMIT 1";
		String[] params = new String[0];
		versionController = ApplicationVersionController.retrieve(sql, params).get(0);
		
		sql = "SELECT * FROM copyright ORDER BY id desc limit 1";
		params = new String[0];
		copyright = Copyright.retrieve(sql, params).get(0);
		
		try{fixes = Collections.synchronizedList(new ArrayList<ApplicationFixes>());}catch(Exception e){}
		sql = "SELECT * FROM buildfixes WHERE buildid=?";
		params = new String[1];
		params[0] = versionController.getBuildid()+"";
		try{fixes = ApplicationFixes.retrieve(sql, params);}catch(Exception e){}
		
		sql = "SELECT * FROM license";
		licenses = Collections.synchronizedList(new ArrayList<License>());
		licenses = License.retrieve(sql, new String[0]);
		
	}
	
	
	
	public ApplicationVersionController getVersionController() {
		return versionController;
	}
	public void setVersionController(ApplicationVersionController versionController) {
		this.versionController = versionController;
	}
	public ApplicationFixes getApplicationFixes() {
		return applicationFixes;
	}
	public void setApplicationFixes(ApplicationFixes applicationFixes) {
		this.applicationFixes = applicationFixes;
	}
	public List<ApplicationFixes> getFixes() {
		return fixes;
	}
	public void setFixes(List<ApplicationFixes> fixes) {
		this.fixes = fixes;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Copyright getCopyright() {
		return copyright;
	}

	public void setCopyright(Copyright copyright) {
		this.copyright = copyright;
	}



	public List<License> getLicenses() {
		return licenses;
	}



	public void setLicenses(List<License> licenses) {
		this.licenses = licenses;
	}
	
}
