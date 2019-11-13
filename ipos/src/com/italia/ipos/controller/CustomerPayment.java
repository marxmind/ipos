package com.italia.ipos.controller;

import java.math.BigDecimal;
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
 * @since 11/05/2016
 * @version 1.0
 */
public class CustomerPayment {

	private long cpayid;
	private Customer customer;
	private BigDecimal amountpaid;
	private String amountpaiddate;
	private BigDecimal amountprevpaid;
	private String amountprevpaiddate;
	private BigDecimal amountbalance;
	private BigDecimal amountprevbalance;
	private UserDtls userDtls;
	private int payisactive;
	private Timestamp timestamp;
	
	private double inputAmount;
	
	public CustomerPayment(){}
	
	public CustomerPayment(
			long cpayid,
			Customer customer,
			BigDecimal amountpaid,
			String amountpaiddate,
			BigDecimal amountprevpaid,
			String amountprevpaiddate,
			BigDecimal amountbalance,
			BigDecimal amountprevbalance,
			UserDtls userDtls,
			int payisactive
			){
		this.cpayid = cpayid;
		this.customer = customer;
		this.amountpaid = amountpaid;
		this.amountpaiddate = amountpaiddate;
		this.amountprevpaid = amountprevpaid;
		this.amountbalance = amountbalance;
		this.amountprevbalance = amountprevbalance;
		this.userDtls = userDtls;
		this.payisactive = payisactive;
	}
	
	public static String customerPaymentSQL(String tablename,CustomerPayment cus){
		String sql= " AND "+ tablename +".payisactive=" + cus.getPayisactive();
		if(cus!=null){
			
			if(cus.getCpayid()!=0){
				sql += " AND "+ tablename +".cpayid=" + cus.getCpayid();
			}
			
			if(cus.getCustomer()!=null){
				if(cus.getCustomer().getCustomerid()!=0){
					sql += " AND "+ tablename +".customerid=" + cus.getCustomer().getCustomerid();
				}
			}
			
			if(cus.getAmountpaid()!=null){
				sql += " AND "+ tablename +".amountpaid=" + cus.getAmountpaid();
			}
			if(cus.getAmountpaiddate()!=null){
				sql += " AND "+ tablename +".amountpaiddate like '%" + cus.getAmountpaiddate()+"%'";
			}
			if(cus.getAmountprevpaid()!=null){
				sql += " AND "+ tablename +".amountprevpaid=" + cus.getAmountprevpaid();
			}
			if(cus.getAmountprevpaiddate()!=null){
				sql += " AND "+ tablename +".amountprevpaiddate like '%" + cus.getAmountprevpaiddate()+"%'";
			}
			if(cus.getAmountbalance()!=null){
				sql += " AND "+ tablename +".amountbalance>=" + cus.getAmountbalance();
			}
			if(cus.getAmountprevbalance()!=null){
				sql += " AND "+ tablename +".amountprevbalance=" + cus.getAmountprevbalance();
			}
			
			if(cus.getUserDtls()!=null){
				if(cus.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + cus.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		
		return sql;
	}
	
	public static List<CustomerPayment> retrieve(Object... obj){
		List<CustomerPayment> pays = Collections.synchronizedList(new ArrayList<CustomerPayment>());
		String payTable = "pay";
		String supTable = "cus";
		String userTable = "usr";
		String sql = "SELECT * FROM customerpayment "+ payTable +" ,customer "+ 
		supTable +", userdtls "+ userTable +" WHERE " + payTable + ".customerid = " + supTable + ".customerid AND "+ 
				supTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof CustomerPayment){
				sql += customerPaymentSQL(payTable,(CustomerPayment)obj[i]);
			}
			if(obj[i] instanceof Customer){
				sql += Customer.customerSQL(supTable,(Customer)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
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
			
			CustomerPayment pay = new CustomerPayment();
			try{pay.setCpayid(rs.getLong("cpayid"));}catch(NullPointerException e){}
			try{pay.setAmountpaid(rs.getBigDecimal("amountpaid"));}catch(NullPointerException e){}
			try{pay.setAmountpaiddate(rs.getString("amountpaiddate"));}catch(NullPointerException e){}
			try{pay.setAmountprevpaid(rs.getBigDecimal("amountprevpaid"));}catch(NullPointerException e){}
			try{pay.setAmountprevpaiddate(rs.getString("amountprevpaiddate"));}catch(NullPointerException e){}
			try{pay.setAmountbalance(rs.getBigDecimal("amountbalance"));}catch(NullPointerException e){}
			try{pay.setAmountprevbalance(rs.getBigDecimal("amountprevbalance"));}catch(NullPointerException e){}
			try{pay.setPayisactive(rs.getInt("payisactive"));}catch(NullPointerException e){}
			
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
			pay.setCustomer(cus);
			
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
			pay.setUserDtls(user);
			
			pays.add(pay);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pays;
	}
	
	public static CustomerPayment retrieve(String customerpayId){
		CustomerPayment pay = new CustomerPayment();
		String payTable = "pay";
		String supTable = "cus";
		String userTable = "usr";
		String sql = "SELECT * FROM customerpayment "+ payTable +" ,customer "+ 
		supTable +", userdtls "+ userTable +" WHERE " + payTable + ".customerid = " + supTable + ".customerid AND "+ 
				supTable +".userdtlsid = "+ userTable +".userdtlsid AND " + payTable + ".cpayid = " + customerpayId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{pay.setCpayid(rs.getLong("cpayid"));}catch(NullPointerException e){}
			try{pay.setAmountpaid(rs.getBigDecimal("amountpaid"));}catch(NullPointerException e){}
			try{pay.setAmountpaiddate(rs.getString("amountpaiddate"));}catch(NullPointerException e){}
			try{pay.setAmountprevpaid(rs.getBigDecimal("amountprevpaid"));}catch(NullPointerException e){}
			try{pay.setAmountprevpaiddate(rs.getString("amountprevpaiddate"));}catch(NullPointerException e){}
			try{pay.setAmountbalance(rs.getBigDecimal("amountbalance"));}catch(NullPointerException e){}
			try{pay.setAmountprevbalance(rs.getBigDecimal("amountprevbalance"));}catch(NullPointerException e){}
			try{pay.setPayisactive(rs.getInt("payisactive"));}catch(NullPointerException e){}
			
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
			pay.setCustomer(cus);
			
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
			pay.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pay;
	}
	
	public static CustomerPayment save(CustomerPayment cus){
		if(cus!=null){
			
			long id = CustomerPayment.getInfo(cus.getCpayid() ==0? CustomerPayment.getLatestId()+1 : cus.getCpayid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				cus = CustomerPayment.insertData(cus, "1");
			}else if(id==2){
				LogU.add("update Data ");
				cus = CustomerPayment.updateData(cus);
			}else if(id==3){
				LogU.add("added new Data ");
				cus = CustomerPayment.insertData(cus, "3");
			}
			
		}
		return cus;
	}
	
	public void save(){
		
			long id = getInfo(getCpayid() ==0? getLatestId()+1 : getCpayid());
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
	
	public static CustomerPayment insertData(CustomerPayment cus, String type){
		String sql = "INSERT INTO customerpayment ("
				+ "cpayid,"
				+ "customerid,"
				+ "amountpaid,"
				+ "amountpaiddate,"
				+ "amountprevpaid,"
				+ "amountprevpaiddate,"
				+ "amountbalance,"
				+ "amountprevbalance,"
				+ "userdtlsid,"
				+ "payisactive)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customerpayment");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			cus.setCpayid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			cus.setCpayid(id);
			LogU.add("id: " + id);
		}
		ps.setLong(2, cus.getCustomer()==null? 0 : cus.getCustomer().getCustomerid());
		ps.setBigDecimal(3, cus.getAmountpaid());
		ps.setString(4, cus.getAmountpaiddate());
		ps.setBigDecimal(5, cus.getAmountprevpaid());
		ps.setString(6, cus.getAmountprevpaiddate());
		ps.setBigDecimal(7, cus.getAmountbalance());
		ps.setBigDecimal(8, cus.getAmountprevbalance());
		ps.setLong(9, cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		ps.setInt(10, cus.getPayisactive());
		
		LogU.add(cus.getCustomer()==null? 0 : cus.getCustomer().getCustomerid());
		LogU.add(cus.getAmountpaid());
		LogU.add(cus.getAmountpaiddate());
		LogU.add(cus.getAmountprevpaid());
		LogU.add(cus.getAmountprevpaiddate());
		LogU.add(cus.getAmountbalance());
		LogU.add(cus.getAmountprevbalance());
		LogU.add(cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		LogU.add(cus.getPayisactive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customerpayment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO customerpayment ("
				+ "cpayid,"
				+ "customerid,"
				+ "amountpaid,"
				+ "amountpaiddate,"
				+ "amountprevpaid,"
				+ "amountprevpaiddate,"
				+ "amountbalance,"
				+ "amountprevbalance,"
				+ "userdtlsid,"
				+ "payisactive)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customerpayment");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setCpayid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setCpayid(id);
			LogU.add("id: " + id);
		}
		ps.setLong(2, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setBigDecimal(3, getAmountpaid());
		ps.setString(4, getAmountpaiddate());
		ps.setBigDecimal(5, getAmountprevpaid());
		ps.setString(6, getAmountprevpaiddate());
		ps.setBigDecimal(7, getAmountbalance());
		ps.setBigDecimal(8, getAmountprevbalance());
		ps.setLong(9, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(10, getPayisactive());
		
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getAmountpaid());
		LogU.add(getAmountpaiddate());
		LogU.add(getAmountprevpaid());
		LogU.add(getAmountprevpaiddate());
		LogU.add(getAmountbalance());
		LogU.add(getAmountprevbalance());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getPayisactive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customerpayment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static CustomerPayment updateData(CustomerPayment cus){
		String sql = "UPDATE customerpayment SET "
				+ "amountpaid=?,"
				+ "amountpaiddate=?,"
				+ "amountprevpaid=?,"
				+ "amountprevpaiddate=?,"
				+ "amountbalance=?,"
				+ "amountprevbalance=? "
				+ " WHERE cpayid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("upadting data into table customerpayment");
		
		
		ps.setBigDecimal(1, cus.getAmountpaid());
		ps.setString(2, cus.getAmountpaiddate());
		ps.setBigDecimal(3, cus.getAmountprevpaid());
		ps.setString(4, cus.getAmountprevpaiddate());
		ps.setBigDecimal(5, cus.getAmountbalance());
		ps.setBigDecimal(6, cus.getAmountprevbalance());
		ps.setLong(7, cus.getCpayid());
		
		
		LogU.add(cus.getAmountpaid());
		LogU.add(cus.getAmountpaiddate());
		LogU.add(cus.getAmountprevpaid());
		LogU.add(cus.getAmountprevpaiddate());
		LogU.add(cus.getAmountbalance());
		LogU.add(cus.getAmountprevbalance());
		LogU.add(cus.getCpayid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customerpayment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void updateData(){
		String sql = "UPDATE customerpayment SET "
				+ "amountpaid=?,"
				+ "amountpaiddate=?,"
				+ "amountprevpaid=?,"
				+ "amountprevpaiddate=?,"
				+ "amountbalance=?,"
				+ "amountprevbalance=? "
				+ " WHERE cpayid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("upadting data into table customerpayment");
		
		
		ps.setBigDecimal(1, getAmountpaid());
		ps.setString(2, getAmountpaiddate());
		ps.setBigDecimal(3, getAmountprevpaid());
		ps.setString(4, getAmountprevpaiddate());
		ps.setBigDecimal(5, getAmountbalance());
		ps.setBigDecimal(6, getAmountprevbalance());
		ps.setLong(7, getCpayid());
		
		LogU.add(getAmountpaid());
		LogU.add(getAmountpaiddate());
		LogU.add(getAmountprevpaid());
		LogU.add(getAmountprevpaiddate());
		LogU.add(getAmountbalance());
		LogU.add(getAmountprevbalance());
		LogU.add(getCpayid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customerpayment : " + s.getMessage());
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
		sql="SELECT cpayid FROM customerpayment  ORDER BY cpayid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("cpayid");
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
		ps = conn.prepareStatement("SELECT cpayid FROM customerpayment WHERE cpayid=?");
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
		String sql = "UPDATE customerpayment set payisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE cpayid=?";
		
		String[] params = new String[1];
		params[0] = getCpayid()+"";
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
	
	public long getCpayid() {
		return cpayid;
	}
	public void setCpayid(long cpayid) {
		this.cpayid = cpayid;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public BigDecimal getAmountpaid() {
		return amountpaid;
	}
	public void setAmountpaid(BigDecimal amountpaid) {
		this.amountpaid = amountpaid;
	}
	public String getAmountpaiddate() {
		return amountpaiddate;
	}
	public void setAmountpaiddate(String amountpaiddate) {
		this.amountpaiddate = amountpaiddate;
	}
	public BigDecimal getAmountprevpaid() {
		return amountprevpaid;
	}
	public void setAmountprevpaid(BigDecimal amountprevpaid) {
		this.amountprevpaid = amountprevpaid;
	}
	public String getAmountprevpaiddate() {
		return amountprevpaiddate;
	}
	public void setAmountprevpaiddate(String amountprevpaiddate) {
		this.amountprevpaiddate = amountprevpaiddate;
	}
	public BigDecimal getAmountbalance() {
		return amountbalance;
	}
	public void setAmountbalance(BigDecimal amountbalance) {
		this.amountbalance = amountbalance;
	}
	public BigDecimal getAmountprevbalance() {
		return amountprevbalance;
	}
	public void setAmountprevbalance(BigDecimal amountprevbalance) {
		this.amountprevbalance = amountprevbalance;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public int getPayisactive() {
		return payisactive;
	}

	public void setPayisactive(int payisactive) {
		this.payisactive = payisactive;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public double getInputAmount() {
		return inputAmount;
	}

	public void setInputAmount(double inputAmount) {
		this.inputAmount = inputAmount;
	}

	public static void main(String[] args) {
		
		CustomerPayment pay = new CustomerPayment();
		/*pay.setCpayid(2);
		pay.setAmountpaid(new BigDecimal("1000"));
		pay.setAmountpaiddate(DateUtils.getCurrentDateMMDDYYYY());
		pay.setAmountprevpaid(new BigDecimal("50"));
		pay.setAmountprevpaiddate(DateUtils.getCurrentDateMMDDYYYY());
		pay.setAmountbalance(new BigDecimal("200"));
		pay.setAmountprevbalance(new BigDecimal("300"));
		
		UserDtls userDtls = new UserDtls();
		userDtls.setUserdtlsid(1l);
		pay.setUserDtls(userDtls);*/
		
		Customer customer = new Customer();
		/*customer.setCustomerid(1);
		pay.setCustomer(customer);*/
		customer.setFullname("mark");
		customer.setIsactive(1);
		//pay.save();
		
		pay = new CustomerPayment();
		pay.setPayisactive(1);
		pay.setAmountbalance(new BigDecimal("1"));
		for(CustomerPayment p : CustomerPayment.retrieve(pay,customer)){
			System.out.println("id "+ p.getCpayid() + " amount " + p.getAmountpaid());
		}
		
	}
	
}
