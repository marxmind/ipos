package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 11/01/2016
 * @version 1.0
 */


public class Customer {

	private long customerid;
	private String firstname;
	private String middlename;
	private String lastname;
	private String fullname;
	private String gender;
	private int age;
	private String address; 
	private String contactno;
	private String dateregistered;
	private String cardno; 
	private int isactive;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private Barangay barangay;
	private Municipality municipality;
	private Province province;
	
	private List<RentedBottle> rentedBottle = Collections.synchronizedList(new ArrayList<RentedBottle>());
	private List<DeliveryItemReceipt> receipts = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
	
	public Customer(){}
	
	public Customer(
			long customerid,
			String firstname,
			String middlename,
			String lastname,
			String fullname,
			String gender,
			int age,
			String address, 
			String contactno,
			String dateregistered,
			String cardno, 
			int isactive,
			UserDtls userDtls,
			Barangay barangay,
			Municipality municipality,
			Province province
			){
		
		this.customerid = customerid;
		this.firstname = firstname;
		this.middlename = middlename;
		this.lastname = lastname;
		this.fullname = fullname;
		this.gender = gender;
		this.age = age;
		this.address = address;
		this.contactno = contactno;
		this.dateregistered = dateregistered;
		this.cardno = cardno;
		this.isactive = isactive;
		this.userDtls = userDtls;
		this.barangay = barangay;
		this.municipality = municipality;
		this.province = province;
		
	}
	
	public static String cardNumber(){
		String cardNum = "1-010101-010101";
		
		cardNum = (getLatestId()+1) + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + "-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
		
		return cardNum;
	}
	
	public static String customerSQL(String tablename,Customer cus){
		String sql= " AND "+ tablename +".cusisactive=" + cus.getIsactive();
		if(cus!=null){
			
			if(cus.getCustomerid()!=0){
				sql += " AND "+ tablename +".customerid=" + cus.getCustomerid();
			}/*else{
				sql += " AND "+ tablename +".customerid=" + cus.getCustomerid();
			}*/
			if(cus.getDateregistered()!=null){
				sql += " AND "+ tablename +".cusdateregistered like '%" + cus.getDateregistered()+"%'";
			}
			if(cus.getFirstname()!=null){
				sql += " AND "+ tablename +".cusfirstname like '%" + cus.getFirstname()+"%'";
			}
			if(cus.getMiddlename()!=null){
				sql += " AND "+ tablename +".cusmiddlename like '%" + cus.getMiddlename()+"%'";
			}
			if(cus.getLastname()!=null){
				sql += " AND "+ tablename +".cuslastname like '%" + cus.getLastname()+"%'";
			}
			if(cus.getFullname()!=null){
				sql += " AND "+ tablename +".fullname like '%" + cus.getFullname()+"%'";
			}
			if(cus.getAddress()!=null){
				sql += " AND "+ tablename +".cusaddress like '%" + cus.getAddress()+"%'";
			}
			if(cus.getContactno()!=null){
				sql += " AND "+ tablename +".cuscontactno like'%" + cus.getContactno()+"%'";
			}
			if(cus.getAge()!=0){
				sql += " AND "+ tablename +".cusage=" + cus.getAge();
			}
			if(cus.getGender()!=null){
				sql += " AND "+ tablename +".cusgender like'%" + cus.getGender()+"%'";
			}
			
			if(cus.getUserDtls()!=null){
				if(cus.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + cus.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		
		return sql;
	}
	
	public static List<Customer> retrieve(Object... obj){
		List<Customer> cuss = Collections.synchronizedList(new ArrayList<Customer>());
		String supTable = "cus";
		String userTable = "usr";
		String barTable = "bar";
		String munTable = "mun";
		String provTable = "prov";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable +
				                 ", barangay "+ barTable +
				                 ", municipality "+ munTable +
				                 ", province "+ provTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".bgid = "+ barTable +".bgid AND "
				+ supTable +".munid = "+ munTable +".munid AND "
				+ supTable +".provid = "+ provTable +".provid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof Customer){
				sql += customerSQL(supTable,(Customer)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
			if(obj[i] instanceof Barangay){
				sql += Barangay.barangaySQL(barTable,(Barangay)obj[i]);
			}
			if(obj[i] instanceof Municipality){
				sql += Municipality.municipalitySQL(munTable,(Municipality)obj[i]);
			}
			if(obj[i] instanceof Province){
				sql += Province.provinceSQL(provTable,(Province)obj[i]);
			}
		}
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			cuss.add(cus);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cuss;
	}
	
	public static List<Customer> retrieve(String sqlAdd, String[] params){
		List<Customer> cuss = Collections.synchronizedList(new ArrayList<Customer>());
		String supTable = "cus";
		String userTable = "usr";
		String barTable = "bar";
		String munTable = "mun";
		String provTable = "prov";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable +
				                 ", barangay "+ barTable +
				                 ", municipality "+ munTable +
				                 ", province "+ provTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".bgid = "+ barTable +".bgid AND "
				+ supTable +".munid = "+ munTable +".munid AND "
				+ supTable +".provid = "+ provTable +".provid ";
		
		sql += sqlAdd;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			cuss.add(cus);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cuss;
	}
	
	public static Customer retrieve(String customerid){
		Customer cus = new Customer();
		String supTable = "cus";
		String userTable = "usr";
		/*String sql = "SELECT * FROM customer "+ supTable +", userdtls "+ userTable +" WHERE "+ supTable +".userdtlsid = "
		+ userTable +".userdtlsid AND " + supTable + ".customerid="+customerid;
		*/
		String barTable = "bar";
		String munTable = "mun";
		String provTable = "prov";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable +
				                 ", barangay "+ barTable +
				                 ", municipality "+ munTable +
				                 ", province "+ provTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".bgid = "+ barTable +".bgid AND "
				+ supTable +".munid = "+ munTable +".munid AND "
				+ supTable +".provid = "+ provTable +".provid AND " +
				supTable + ".customerid="+customerid;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
		
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cus;
	}
	
	public static Customer addUnknownCustomer(){
		Customer customer = new Customer();
		String unknowname = "unknown buyer" + DateUtils.getCurrentDateMMDDYYYYTIME();
		customer.setFirstname(unknowname);
		customer.setMiddlename(unknowname);
		customer.setLastname(unknowname);
		customer.setFullname(unknowname);
		customer.setGender("1");
		customer.setAge(0);
		customer.setAddress("unknown address");
		customer.setContactno("00000000000");
		customer.setDateregistered(DateUtils.getCurrentDateYYYYMMDD());
		customer.setIsactive(1);
		customer.setUserDtls(Login.getUserLogin().getUserDtls());
		return customer;
	}
	
	public static Customer customer(String customerId){
		Customer cus = new Customer();
		String supTable = "cus";
		String userTable = "usr";
		/*String sql = "SELECT * FROM customer "+ supTable +", userdtls "+ userTable +" WHERE "+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".customerid="+customerId;
		
        System.out.println("SQL "+sql);*/
		
		String barTable = "bar";
		String munTable = "mun";
		String provTable = "prov";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable +
				                 ", barangay "+ barTable +
				                 ", municipality "+ munTable +
				                 ", province "+ provTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".bgid = "+ barTable +".bgid AND "
				+ supTable +".munid = "+ munTable +".munid AND "
				+ supTable +".provid = "+ provTable +".provid AND " +
				supTable + ".customerid="+customerId;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cus;
	}
	
	public static Customer save(Customer cus){
		if(cus!=null){
			
			long id = Customer.getInfo(cus.getCustomerid() ==0? Customer.getLatestId()+1 : cus.getCustomerid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				cus = Customer.insertData(cus, "1");
			}else if(id==2){
				LogU.add("update Data ");
				cus = Customer.updateData(cus);
			}else if(id==3){
				LogU.add("added new Data ");
				cus = Customer.insertData(cus, "3");
			}
			
		}
		return cus;
	}
	
	public void save(){
			
			long id = getInfo(getCustomerid() ==0? getLatestId()+1 : getCustomerid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				insertData("1");
			}else if(id==2){
				LogU.add("update Data ");
				updateData();
			}else if(id==3){
				LogU.add("added new Data ");
				insertData("3");
			}
			
	}
	
	public static Customer insertData(Customer cus, String type){
		String sql = "INSERT INTO customer ("
				+ "customerid,"
				+ "cusfirstname,"
				+ "cusmiddlename,"
				+ "cuslastname,"
				+ "cusgender,"
				+ "cusage,"
				+ "cusaddress,"
				+ "cuscontactno,"
				+ "cusdateregistered,"
				+ "cuscardno,"
				+ "cusisactive,"
				+ "userdtlsid,"
				+ "fullname,"
				+ "bgid,"
				+ "munid,"
				+ "provid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customer");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			cus.setCustomerid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			cus.setCustomerid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, cus.getFirstname());
		ps.setString(3, cus.getMiddlename());
		ps.setString(4, cus.getLastname());
		ps.setString(5, cus.getGender());
		ps.setInt(6, cus.getAge());
		ps.setString(7, cus.getAddress());
		ps.setString(8, cus.getContactno());
		ps.setString(9, cus.getDateregistered());
		ps.setString(10, cus.getCardno()==null? cardNumber() : cus.getCardno());
		ps.setInt(11, cus.getIsactive());
		ps.setLong(12, cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		ps.setString(13, cus.getFullname());
		ps.setInt(14, cus.getBarangay()==null? 0 : cus.getBarangay().getId());
		ps.setInt(15, cus.getMunicipality()==null? 0 : cus.getMunicipality().getId());
		ps.setInt(16, cus.getProvince()==null? 0 : cus.getProvince().getId());
		
		LogU.add(cus.getFirstname());
		LogU.add(cus.getMiddlename());
		LogU.add(cus.getLastname());
		LogU.add(cus.getGender());
		LogU.add(cus.getAge());
		LogU.add(cus.getAddress());
		LogU.add(cus.getContactno());
		LogU.add(cus.getDateregistered());
		LogU.add(cus.getCardno()==null? cardNumber() : cus.getCardno());
		LogU.add(cus.getIsactive());
		LogU.add(cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		LogU.add(cus.getFullname());
		LogU.add(cus.getBarangay()==null? 0 : cus.getBarangay().getId());
		LogU.add(cus.getMunicipality()==null? 0 : cus.getMunicipality().getId());
		LogU.add(cus.getProvince()==null? 0 : cus.getProvince().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO customer ("
				+ "customerid,"
				+ "cusfirstname,"
				+ "cusmiddlename,"
				+ "cuslastname,"
				+ "cusgender,"
				+ "cusage,"
				+ "cusaddress,"
				+ "cuscontactno,"
				+ "cusdateregistered,"
				+ "cuscardno,"
				+ "cusisactive,"
				+ "userdtlsid,"
				+ "fullname,"
				+ "bgid,"
				+ "munid,"
				+ "provid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customer");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setCustomerid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setCustomerid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, getFirstname());
		ps.setString(3, getMiddlename());
		ps.setString(4, getLastname());
		ps.setString(5, getGender());
		ps.setInt(6, getAge());
		ps.setString(7, getAddress());
		ps.setString(8, getContactno());
		ps.setString(9, getDateregistered());
		ps.setString(10, getCardno()==null? cardNumber() : getCardno());
		ps.setInt(11, getIsactive());
		ps.setLong(12, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(13, getFullname());
		ps.setInt(14, getBarangay()==null? 0 : getBarangay().getId());
		ps.setInt(15, getMunicipality()==null? 0 : getMunicipality().getId());
		ps.setInt(16, getProvince()==null? 0 : getProvince().getId());
		
		LogU.add(getFirstname());
		LogU.add(getMiddlename());
		LogU.add(getLastname());
		LogU.add(getGender());
		LogU.add(getAge());
		LogU.add(getAddress());
		LogU.add(getContactno());
		LogU.add(getDateregistered());
		LogU.add(getCardno());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getFullname());
		LogU.add(getBarangay()==null? 0 : getBarangay().getId());
		LogU.add(getMunicipality()==null? 0 : getMunicipality().getId());
		LogU.add(getProvince()==null? 0 : getProvince().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Customer updateData(Customer cus){
		String sql = "UPDATE customer SET "
				+ "cusfirstname=?,"
				+ "cusmiddlename=?,"
				+ "cuslastname=?,"
				+ "cusgender=?,"
				+ "cusage=?,"
				+ "cusaddress=?,"
				+ "cuscontactno=?,"
				+ "cusdateregistered=?,"
				+ "cusisactive=?,"
				+ "userdtlsid=?,"
				+ "fullname=?,"
				+ "bgid=?,"
				+ "munid=?,"
				+ "provid=?,"
				+ "cuscardno=? " 
				+ " WHERE customerid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customer");
		
		ps.setString(1, cus.getFirstname());
		ps.setString(2, cus.getMiddlename());
		ps.setString(3, cus.getLastname());
		ps.setString(4, cus.getGender());
		ps.setInt(5, cus.getAge());
		ps.setString(6, cus.getAddress());
		ps.setString(7, cus.getContactno());
		ps.setString(8, cus.getDateregistered());
		ps.setInt(9, cus.getIsactive());
		ps.setLong(10, cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		ps.setString(11, cus.getFullname());
		ps.setInt(12, cus.getBarangay()==null? 0 : cus.getBarangay().getId());
		ps.setInt(13, cus.getMunicipality()==null? 0 : cus.getMunicipality().getId());
		ps.setInt(14, cus.getProvince()==null? 0 : cus.getProvince().getId());
		ps.setString(15, cus.getCardno()==null? cardNumber() : cus.getCardno());
		ps.setLong(16, cus.getCustomerid());
		
		LogU.add(cus.getFirstname());
		LogU.add(cus.getMiddlename());
		LogU.add(cus.getLastname());
		LogU.add(cus.getGender());
		LogU.add(cus.getAge());
		LogU.add(cus.getAddress());
		LogU.add(cus.getContactno());
		LogU.add(cus.getDateregistered());
		LogU.add(cus.getIsactive());
		LogU.add(cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		LogU.add(cus.getFullname());
		LogU.add(cus.getBarangay()==null? 0 : cus.getBarangay().getId());
		LogU.add(cus.getMunicipality()==null? 0 : cus.getMunicipality().getId());
		LogU.add(cus.getProvince()==null? 0 : cus.getProvince().getId());
		LogU.add(cus.getCardno()==null? cardNumber() : cus.getCardno());
		LogU.add(cus.getCustomerid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void updateData(){
		String sql = "UPDATE customer SET "
				+ "cusfirstname=?,"
				+ "cusmiddlename=?,"
				+ "cuslastname=?,"
				+ "cusgender=?,"
				+ "cusage=?,"
				+ "cusaddress=?,"
				+ "cuscontactno=?,"
				+ "cusdateregistered=?,"
				+ "cusisactive=?,"
				+ "userdtlsid=?,"
				+ "fullname=?,"
				+ "bgid=?,"
				+ "munid=?,"
				+ "provid=?,"
				+ "cuscardno=? " 
				+ " WHERE customerid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customer");
		
		ps.setString(1, getFirstname());
		ps.setString(2, getMiddlename());
		ps.setString(3, getLastname());
		ps.setString(4, getGender());
		ps.setInt(5, getAge());
		ps.setString(6, getAddress());
		ps.setString(7, getContactno());
		ps.setString(8, getDateregistered());
		ps.setInt(9, getIsactive());
		ps.setLong(10, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(11, getFullname());
		ps.setInt(12, getBarangay()==null? 0 : getBarangay().getId());
		ps.setInt(13, getMunicipality()==null? 0 : getMunicipality().getId());
		ps.setInt(14, getProvince()==null? 0 : getProvince().getId());
		ps.setString(15, getCardno()==null? cardNumber() : getCardno());
		ps.setLong(16, getCustomerid());
		
		
		LogU.add(getFirstname());
		LogU.add(getMiddlename());
		LogU.add(getLastname());
		LogU.add(getGender());
		LogU.add(getAge());
		LogU.add(getAddress());
		LogU.add(getContactno());
		LogU.add(getDateregistered());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getFullname());
		LogU.add(getBarangay()==null? 0 : getBarangay().getId());
		LogU.add(getMunicipality()==null? 0 : getMunicipality().getId());
		LogU.add(getProvince()==null? 0 : getProvince().getId());
		LogU.add(getCardno()==null? cardNumber() : getCardno());
		LogU.add(getCustomerid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT customerid FROM customer  ORDER BY customerid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("customerid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT customerid FROM customer WHERE customerid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE customer set cusisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE customerid=?";
		
		String[] params = new String[1];
		params[0] = getCustomerid()+"";
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public long getCustomerid() {
		return customerid;
	}
	public void setCustomerid(long customerid) {
		this.customerid = customerid;
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
	public String getDateregistered() {
		return dateregistered;
	}
	public void setDateregistered(String dateregistered) {
		this.dateregistered = dateregistered;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public int getIsactive() {
		return isactive;
	}
	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	public UserDtls getUserDtls() {
		return userDtls;
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

	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public List<RentedBottle> getRentedBottle() {
		return rentedBottle;
	}

	public void setRentedBottle(List<RentedBottle> rentedBottle) {
		this.rentedBottle = rentedBottle;
	}

	public List<DeliveryItemReceipt> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<DeliveryItemReceipt> receipts) {
		this.receipts = receipts;
	}

	public Barangay getBarangay() {
		return barangay;
	}

	public void setBarangay(Barangay barangay) {
		this.barangay = barangay;
	}

	public Municipality getMunicipality() {
		return municipality;
	}

	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public static void main(String[] args) {
		Customer c = new Customer();
		//c.setCustomerid(1);
		c.setFirstname("Markos");
		c.setMiddlename("M");
		c.setLastname("L");
		c.setGender("Male");
		c.setAge(1);
		c.setAddress("Add");
		c.setContactno("1211");
		c.setDateregistered(DateUtils.getCurrentDateMMDDYYYY());
		c.setCardno("45247");
		c.setIsactive(1);
		UserDtls u = new UserDtls();
		u.setUserdtlsid(1l);
		c.setUserDtls(u);
		c.save();
		c.setIsactive(1);
		for(Customer cx :  Customer.retrieve(c)){
			System.out.println("name : " + cx.getFirstname());
		}
	}
	
}










