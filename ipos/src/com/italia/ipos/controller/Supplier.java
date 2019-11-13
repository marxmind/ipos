package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.ipos.bean.SessionBean;
import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 11/01/2016
 *@version 1.0
 */
public class Supplier {

	private long supid;
	private String suppliername;
	private String address;
	private String contactno;
	private String ownername;
	private int isactive;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private double balance;
	
	public Supplier(){}
	
	public Supplier(
			long supid,
			String suppliername,
			String address,
			String contactno,
			String ownername,
			int isactive,
			UserDtls userDtls
			){
		
		this.supid = supid;
		this.suppliername = suppliername;
		this.address = address;
		this.contactno = contactno;
		this.ownername = ownername;
		this.isactive = isactive;
		this.userDtls = userDtls;
		
	}
	
	public static String supplierSQL(String tablename,Supplier sup){
		String sql= " AND "+ tablename +".isactive=" + sup.getIsactive();
		if(sup!=null){
			if(sup.getSupid()!=0){
				sql += " AND "+ tablename +".supid=" + sup.getSupid();
			}
			if(sup.getSuppliername()!=null){
				sql += " AND "+ tablename +".suppliername like '%" + sup.getSuppliername() +"%'";
			}
			if(sup.getAddress()!=null){
				sql += " AND "+ tablename +".address like '%" + sup.getAddress() +"%'";
			}
			if(sup.getContactno()!=null){
				sql += " AND "+ tablename +".contactno like '%" + sup.getContactno() +"%'";
			}
			if(sup.getOwnername()!=null){
				sql += " AND "+ tablename +".ownername like '%" + sup.getOwnername() +"%'";
			}
			
			if(sup.getUserDtls()!=null){
				if(sup.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + sup.getUserDtls().getUserdtlsid() ;
				}
			}
		}
		return sql;
	}
	
	public static List<Supplier> retrieve(Object... obj){
		List<Supplier> sups = Collections.synchronizedList(new ArrayList<Supplier>());
		String supTable = "sup";
		String userTable = "usr";
		String sql = "SELECT * FROM supplier "+ supTable +", userdtls "+ userTable +" WHERE "+ supTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof Supplier){
				sql += supplierSQL(supTable,(Supplier)obj[i]);
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
			Supplier sup = new Supplier();
			try{sup.setSupid(rs.getLong("supid"));}catch(NullPointerException e){}
			try{sup.setSuppliername(rs.getString("suppliername"));}catch(NullPointerException e){}
			try{sup.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{sup.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{sup.setOwnername(rs.getString("ownername"));}catch(NullPointerException e){}
			try{sup.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{sup.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
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
			sup.setUserDtls(user);
			
			sups.add(sup);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return sups;
	}
	
	public static Supplier supplier(String supplierId){
		Supplier sup = new Supplier();
		String supTable = "sup";
		String userTable = "usr";
		String sql = "SELECT * FROM supplier "+ supTable +", userdtls "+ userTable +" WHERE "+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".supid=" + supplierId + " AND " + supTable + ".isactive=1";
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("Supplier SQL : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{sup.setSupid(rs.getLong("supid"));}catch(NullPointerException e){}
			try{sup.setSuppliername(rs.getString("suppliername"));}catch(NullPointerException e){}
			try{sup.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{sup.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{sup.setOwnername(rs.getString("ownername"));}catch(NullPointerException e){}
			try{sup.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{sup.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
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
			sup.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return sup;
	}
	
	public static void save(Supplier sup){
		if(sup!=null){
			
			long id = Supplier.getInfo(sup.getSupid() ==0? Supplier.getLatestId()+1 : sup.getSupid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				Supplier.insertData(sup, "1");
			}else if(id==2){
				LogU.add("update Data ");
				Supplier.updateData(sup);
			}else if(id==3){
				LogU.add("added new Data ");
				Supplier.insertData(sup, "3");
			}
			
		}
	}
	
	public void save(){
			long id = getInfo(getSupid() ==0? getLatestId()+1 : getSupid());
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
	
	public static Supplier insertData(Supplier sup, String type){
		String sql = "INSERT INTO supplier ("
				+ "supid,"
				+ "suppliername,"
				+ "address,"
				+ "contactno,"
				+ "ownername,"
				+ "isactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table supplier");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			sup.setSupid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			sup.setSupid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, sup.getSuppliername());
		ps.setString(3, sup.getAddress());
		ps.setString(4, sup.getContactno());
		ps.setString(5, sup.getOwnername());
		ps.setInt(6, sup.getIsactive());
		ps.setLong(7, sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		
		LogU.add(sup.getSuppliername());
		LogU.add(sup.getAddress());
		LogU.add(sup.getContactno());
		LogU.add(sup.getOwnername());
		LogU.add(sup.getIsactive());
		LogU.add(sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to supplier : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sup;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO supplier ("
				+ "supid,"
				+ "suppliername,"
				+ "address,"
				+ "contactno,"
				+ "ownername,"
				+ "isactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table supplier");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setSupid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setSupid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, getSuppliername());
		ps.setString(3, getAddress());
		ps.setString(4, getContactno());
		ps.setString(5, getOwnername());
		ps.setInt(6, getIsactive());
		ps.setLong(7, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add(getSuppliername());
		LogU.add(getAddress());
		LogU.add(getContactno());
		LogU.add(getOwnername());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to supplier : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static Supplier updateData(Supplier sup){
		String sql = "UPDATE supplier SET "
				+ "suppliername=?,"
				+ "address=?,"
				+ "contactno=?,"
				+ "ownername=?,"
				+ "isactive=?,"
				+ "userdtlsid=?" 
				+ " WHERE supid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table supplier");
		
		ps.setString(1, sup.getSuppliername());
		ps.setString(2, sup.getAddress());
		ps.setString(3, sup.getContactno());
		ps.setString(4, sup.getOwnername());
		ps.setInt(5, sup.getIsactive());
		ps.setLong(6, sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		ps.setLong(7, sup.getSupid());
		
		LogU.add(sup.getSuppliername());
		LogU.add(sup.getAddress());
		LogU.add(sup.getContactno());
		LogU.add(sup.getOwnername());
		LogU.add(sup.getIsactive());
		LogU.add(sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		LogU.add(sup.getSupid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to supplier : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sup;
	}
	
	public void updateData(){
		String sql = "UPDATE supplier SET "
				+ "suppliername=?,"
				+ "address=?,"
				+ "contactno=?,"
				+ "ownername=?,"
				+ "isactive=?,"
				+ "userdtlsid=?" 
				+ " WHERE supid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table supplier");
		
		ps.setString(1, getSuppliername());
		ps.setString(2, getAddress());
		ps.setString(3, getContactno());
		ps.setString(4, getOwnername());
		ps.setInt(5, getIsactive());
		ps.setLong(6, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(7, getSupid());
		
		LogU.add(getSuppliername());
		LogU.add(getAddress());
		LogU.add(getContactno());
		LogU.add(getOwnername());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getSupid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to supplier : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT supid FROM supplier  ORDER BY supid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("supid");
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
		ps = conn.prepareStatement("SELECT supid FROM supplier WHERE supid=?");
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
		String sql = "UPDATE supplier set isactive=0 WHERE supid=?";
		
		String[] params = new String[1];
		params[0] = getSupid()+"";
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
	
	public long getSupid() {
		return supid;
	}
	public String getSuppliername() {
		return suppliername;
	}

	public void setSuppliername(String suppliername) {
		this.suppliername = suppliername;
	}

	public void setSupid(long supid) {
		this.supid = supid;
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
	public UserDtls getUserDtls() {
		return userDtls;
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

	public int getIsactive() {
		return isactive;
	}

	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}

	
	public static void main(String[] args) {
		
		Supplier s = new Supplier();
		s.setSupid(1);
		s.setSuppliername("EEE");
		s.setAddress("aaa");
		s.setContactno("11111");
		s.setOwnername("ako");
		s.setIsactive(1);
		
		UserDtls u = new UserDtls();
		u.setUserdtlsid(1l);
		s.setUserDtls(u);
		s.save();
		
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
}
