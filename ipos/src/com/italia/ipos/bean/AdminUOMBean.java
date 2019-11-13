package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.UOM;
import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 10/01/2016
 *@version 1.0
 */
@ManagedBean(name="auomBean", eager=true)
@ViewScoped
public class AdminUOMBean implements Serializable {

	private static final long serialVersionUID = 1094801425228384363L;
	
	private List<UOM> uoms = Collections.synchronizedList(new ArrayList<UOM>());
	private String uomname = "Input Description";
	private String symbol = "Input Symbol";
	private UOM uomdata;
	
	public void save(){
		UOM uom = new UOM();
		
		if(Login.checkUserStatus()){
			
			if(getUomdata()!=null){
				uom = getUomdata();
			}
			uom.setUomname(getUomname());
			uom.setSymbol(getSymbol());
			uom.setUserDtls(Login.getUserLogin().getUserDtls());
			uom.save();
			clearFields();
			init();
			
		}
		
	}
	
	private void clearFields(){
		setUomdata(null);
		setUomname("Input Description");
		setSymbol("Input Symbol");
	}
	
	@PostConstruct
	public void init(){
		if(Login.checkUserStatus()){
			
			uoms = Collections.synchronizedList(new ArrayList<UOM>());
			uoms = UOM.retrieve("SELECT * FROM uom", new String[0]);
			Collections.reverse(uoms);
			
		}
		
	}
	
	public void print(){
		System.out.println("Print");
	}
	
	public void printAll(){
		System.out.println("Print All");
	}
	
	public void close(){
		System.out.println("close");
		clearFields();
	}
	
	public void clickItem(UOM uom){
		System.out.println("clickItem");
		setUomdata(uom);
		setUomname(uom.getUomname());
		setSymbol(uom.getSymbol());
	}
	
	public void deleteRow(UOM uom, boolean isValid){
		System.out.println("deleteRow");
		if(isValid){
			if(Login.checkUserStatus()){
				LogU.add("Delete UOM id " + uom.getUomid());
				uom.delete();
				init();
			}
		}
		
	}

	public List<UOM> getUoms() {
		return uoms;
	}

	public void setUoms(List<UOM> uoms) {
		this.uoms = uoms;
	}

	public String getUomname() {
		return uomname;
	}

	public void setUomname(String uomname) {
		this.uomname = uomname;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public UOM getUomdata() {
		return uomdata;
	}

	public void setUomdata(UOM uomdata) {
		this.uomdata = uomdata;
	}
	
}
