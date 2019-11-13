package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.italia.ipos.security.License;
import com.italia.ipos.security.Module;


@ManagedBean(name= "expiredBean", eager=true)
@SessionScoped
public class ExpiredBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 14575677545475L;

	private List modules;
	private int moduleId;
	private String activationCode;
	private List<License> exes = Collections.synchronizedList(new ArrayList<>());
	
	@PostConstruct 
	public void init(){
		exes = Collections.synchronizedList(new ArrayList<>());
		String sql = "SELECT * FROM license";
		exes = License.retrieve(sql, new String[0]);
	}
	
	public String activate(){
		
		boolean isActivated = false;
		
		isActivated = License.activateLicenseCode(Module.selected(getModuleId()), getActivationCode());
		if(isActivated){
			return "login.xhtml";
		}
			
		
		return "expired.xhtml";
	}
	
	public List getModules() {
		
		modules = new ArrayList<>();
		
		for(Module m : Module.values()){
			modules.add(new SelectItem(m.getId(), m.getName()));
		}
		
		return modules;
	}
	public void setModules(List modules) {
		this.modules = modules;
	}
	public int getModuleId() {
		return moduleId;
	}
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public List<License> getExes() {
		return exes;
	}

	public void setExes(List<License> exes) {
		this.exes = exes;
	}
	
	
	
}
