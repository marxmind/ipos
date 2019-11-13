package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.Supplier;
import com.italia.ipos.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 11/01/2016
 *@version 1.0
 */
@ManagedBean(name="supplierBean", eager=true)
@ViewScoped
public class SupplierBean implements Serializable {

	private static final long serialVersionUID = 1094811425228384663L;
	
	private String suppliername = "Supplier Name";
	private String address="Address";
	private String contactno="Contact No";
	private String ownername="Owner Name";
	private Supplier supplier;
	private List<Supplier> sups = Collections.synchronizedList(new ArrayList<Supplier>());
	private String searchSupplier;
	
	@PostConstruct
	public void init(){
		sups = Collections.synchronizedList(new ArrayList<Supplier>());
		Supplier sup = new Supplier();
		sup.setIsactive(1);
		
		if(getSearchSupplier()!=null){
			sup.setSuppliername(Whitelist.remove(getSearchSupplier()));
		}
		sups = Supplier.retrieve(sup);
		
		
		Collections.reverse(sups);
	}
	
	public void printAll(){
		
	}
	
	public String save(){
		
		if(Login.getUserLogin().checkUserStatus()){
			
			Supplier sup = new Supplier();
			if(getSupplier()!=null){
				sup = getSupplier();
			}
			
			sup.setSuppliername(getSuppliername());
			sup.setAddress(getAddress());
			sup.setContactno(getContactno());
			sup.setOwnername(getOwnername());
			sup.setIsactive(1);
			sup.setUserDtls(Login.getUserLogin().getUserDtls());
			
			sup.save();
			clearFields();
			init();
		}
		
		return "save";
	}
	
	public String close(){
		clearFields();
		return "close";
	}
	
	public void print(){
		
	}
	
	public void clickItem(Supplier sup){
		setSupplier(sup);
		setSuppliername(sup.getSuppliername());
		setAddress(sup.getAddress());
		setOwnername(sup.getOwnername());
		setContactno(sup.getContactno());
	}
	
	public void clearFields(){
		setSupplier(null);
		setSuppliername("Supplier Name");
		setAddress("Address");
		setOwnername("Owner Name");
		setContactno("Contact No");
		setSearchSupplier(null);
	}
	
	public void deleteRow(Supplier sup){
		if(Login.getUserLogin().checkUserStatus()){
			sup.delete();
			init();
		}
	}

	public String getSuppliername() {
		return suppliername;
	}

	public void setSuppliername(String suppliername) {
		this.suppliername = suppliername;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactno() {
		return contactno;
	}

	public void setContactno(String contactno) {
		this.contactno = contactno;
	}

	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<Supplier> getSups() {
		return sups;
	}

	public void setSups(List<Supplier> sups) {
		this.sups = sups;
	}

	public String getSearchSupplier() {
		return searchSupplier;
	}

	public void setSearchSupplier(String searchSupplier) {
		this.searchSupplier = searchSupplier;
	}
	
}
