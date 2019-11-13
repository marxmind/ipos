package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import com.italia.ipos.application.Application;
import com.italia.ipos.controller.Barangay;
import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.Municipality;
import com.italia.ipos.controller.Province;
import com.italia.ipos.controller.Supplier;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 11/01/2016
 * @version 1.0
 */
@ManagedBean(name="customerBean", eager=true)
@ViewScoped
public class CustomerBean implements Serializable {

	private static final long serialVersionUID = 1094811425228384673L;
	
	private String firstname = "First Name";
	private String middlename = "Middle Name";
	private String lastname = "Last Name";
	private String gender = "Gender";
	private int age;
	private String address = "Address"; 
	private String contactno = "Contact No";
	private String dateregistered;
	private String cardnumber;
	private String genderId;
	private List genderList = Collections.synchronizedList(new ArrayList<>());
	
	private List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchCustomer;
	private Customer customer; 
	
	private List barangay;
	private int barangayId;
	private List municipality;
	private int municipalityId;
	private List province;
	private int provinceId;
	
	private Map<Integer, Barangay> barMap = Collections.synchronizedMap(new HashMap<Integer, Barangay>());
	private Map<Integer, Municipality> munMap = Collections.synchronizedMap(new HashMap<Integer, Municipality>());
	private Map<Integer, Province> provMap = Collections.synchronizedMap(new HashMap<Integer, Province>());
	
	@PostConstruct
	public void init(){
		customers = Collections.synchronizedList(new ArrayList<Customer>());
		
		Customer customer = new Customer();
		customer.setIsactive(1);
		if(getSearchCustomer()!=null && !getSearchCustomer().isEmpty()){
			customer.setFullname(Whitelist.remove(getSearchCustomer()));
		}
		for(Customer cus :  Customer.retrieve(customer)){
			customers.add(cus);
		}
		Collections.reverse(customers);
	}
	
	public void printAll(){
		
	}
	
	public String save(){
		
		if(Login.getUserLogin().checkUserStatus()){
			
			Customer cus = new Customer();
			if(getCustomer()!=null){
				cus = getCustomer();
			}else{
				cus.setDateregistered(DateUtils.getCurrentDateYYYYMMDD());
			}
			
			cus.setFirstname(getFirstname());
			cus.setMiddlename(getMiddlename());
			cus.setLastname(getLastname());
			cus.setFullname(getFirstname() + " " + getLastname());
			cus.setGender(getGenderId());
			cus.setAge(getAge());
			cus.setCardno(getCardnumber());
			cus.setContactno(getContactno());
			cus.setIsactive(1);
			cus.setUserDtls(Login.getUserLogin().getUserDtls());
			
			Barangay bg = getBarMap().get(getBarangayId());
			cus.setBarangay(bg);
			
			Municipality mun = getMunMap().get(getMunicipalityId());
			cus.setMunicipality(mun);
			
			Province prov = getProvMap().get(getProvinceId());
			cus.setProvince(prov);
			
			cus.setAddress(getAddress());
			
			cus.save();
			clearFields();
			init();
			Application.addMessage(1, "Success", "Successfully saved");
		}
		
		return "save";
	}
	
	public String close(){
		clearFields();
		return "close";
	}
	
	public void print(){
		
	}
	
	public void clickItem(Customer cus){
		setCustomer(cus);
		setCardnumber(cus.getCardno());
		setDateregistered(cus.getDateregistered());
		setFirstname(cus.getFirstname());
		setMiddlename(cus.getMiddlename());
		setLastname(cus.getLastname());
		setGenderId(cus.getGender());
		setAge(cus.getAge());
		setContactno(cus.getContactno());
		
		setBarangayId(cus.getBarangay().getId());
		setMunicipalityId(cus.getMunicipality().getId());
		setProvinceId(cus.getProvince().getId());
		setAddress(cus.getAddress());
		
	}
	
	public void clearFields(){
		setFirstname("Firstname");
		setMiddlename("Middlename");
		setLastname("Lastname");
		setGender("Gender");
		setAge(0);
		setAddress("Address");
		setContactno("Contact No");
		setDateregistered(null);
		setSearchCustomer(null);
		setCardnumber(null);
		setCustomer(null);
		setGenderId(null);
		setBarangayId(0);
		setMunicipalityId(0);
		setProvinceId(0);
	}
	
	public void deleteRow(Customer cus){
		if(Login.getUserLogin().checkUserStatus()){
			cus.setUserDtls(Login.getUserLogin().getUserDtls());
			cus.delete();
			init();
		}
	}
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getAddress() {
		if(address==null){
			address = " ";
		}
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
	public List<Customer> getCustomers() {
		return customers;
	}
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
	public String getSearchCustomer() {
		return searchCustomer;
	}
	public void setSearchCustomer(String searchCustomer) {
		this.searchCustomer = searchCustomer;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getDateregistered() {
		if(dateregistered==null){
			dateregistered = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateregistered;
	}

	public void setDateregistered(String dateregistered) {
		this.dateregistered = dateregistered;
	}

	public String getCardnumber() {
		if(cardnumber==null){
			cardnumber = Customer.cardNumber();
		}
		return cardnumber;
	}

	public void setCardnumber(String cardnumber) {
		this.cardnumber = cardnumber;
	}

	public String getGenderId() {
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
	}

	public List getGenderList() {
		
		genderList = Collections.synchronizedList(new ArrayList<>());
		genderList.add(new SelectItem("1","Male"));
		genderList.add(new SelectItem("2","Female"));
		
		return genderList;
	}

	public void setGenderList(List genderList) {
		this.genderList = genderList;
	}

	public List getBarangay() {
		
		barMap = Collections.synchronizedMap(new HashMap<Integer, Barangay>());
		
		
		barangay = new ArrayList<>();
		for(Barangay bg : Barangay.retrieve("SELECT * FROM barangay WHERE bgisactive=1", new String[0])){
			barangay.add(new SelectItem(bg.getId(), bg.getName()));
			barMap.put(bg.getId(), bg);
		}
		
		return barangay;
	}

	public void setBarangay(List barangay) {
		this.barangay = barangay;
	}

	public int getBarangayId() {
		return barangayId;
	}

	public void setBarangayId(int barangayId) {
		this.barangayId = barangayId;
	}

	public List getMunicipality() {
		
		munMap = Collections.synchronizedMap(new HashMap<Integer, Municipality>());
		
		municipality = new ArrayList<>();
		for(Municipality bg : Municipality.retrieve("SELECT * FROM municipality WHERE munisactive=1", new String[0])){
			municipality.add(new SelectItem(bg.getId(), bg.getName()));
			munMap.put(bg.getId(), bg);
		}
		
		return municipality;
	}

	public void setMunicipality(List municipality) {
		this.municipality = municipality;
	}

	public int getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(int municipalityId) {
		this.municipalityId = municipalityId;
	}

	public List getProvince() {
		
		provMap = Collections.synchronizedMap(new HashMap<Integer, Province>());
		
		province = new ArrayList<>();
		for(Province bg : Province.retrieve("SELECT * FROM province WHERE provisactive=1", new String[0])){
			province.add(new SelectItem(bg.getId(), bg.getName()));
			provMap.put(bg.getId(), bg);
		}
		
		return province;
	}

	public void setProvince(List province) {
		this.province = province;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public Map<Integer, Barangay> getBarMap() {
		return barMap;
	}

	public void setBarMap(Map<Integer, Barangay> barMap) {
		this.barMap = barMap;
	}

	public Map<Integer, Municipality> getMunMap() {
		return munMap;
	}

	public void setMunMap(Map<Integer, Municipality> munMap) {
		this.munMap = munMap;
	}

	public Map<Integer, Province> getProvMap() {
		return provMap;
	}

	public void setProvMap(Map<Integer, Province> provMap) {
		this.provMap = provMap;
	}
	
}
