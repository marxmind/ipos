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
 * @since 10/03/2016
 * @version 1.0
 */
public class ProductGroup {
	
	private long prodgroupid;
	private String productgroupname;
	private int isactive;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	public ProductGroup(){}
	
	public ProductGroup(
			long prodgroupid,
			String productgroupname,
			int isactive,
			UserDtls userDtls
	){
		this.prodgroupid = prodgroupid;
		this.productgroupname = productgroupname;
		this.isactive = isactive;
		this.userDtls = userDtls;
		
	}
	
	public static String productGroupSQL(String tablename,ProductGroup prod){
		String sql="";
		if(prod!=null){
			if(prod.getProdgroupid()!=0){
				sql += " AND "+ tablename +".prodgroupid=" + prod.getProdgroupid();
			}
			if(prod.getProductgroupname()!=null){
				sql += " AND "+ tablename +".productgroupname like '%" + prod.getProductgroupname() +"%'";
			}
			if(prod.getIsactive()!=0){
				sql += " AND "+ tablename +".isactive=" + prod.getIsactive();
			}
			if(prod.getUserDtls()!=null){
				if(prod.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + prod.getUserDtls().getUserdtlsid() ;
				}
			}
		}
		return sql;
	}
	
	public static List<ProductGroup> retrieve(Object... obj){
		List<ProductGroup> prods = Collections.synchronizedList(new ArrayList<ProductGroup>());
		String pgrpTable = "pgrp";
		String userTable = "usr";
		String sql = "SELECT * FROM productgroup "+ pgrpTable +", userdtls "+ userTable +" WHERE "+ pgrpTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof ProductGroup){
				sql += productGroupSQL(pgrpTable,(ProductGroup)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			ProductGroup prod = new ProductGroup();
			try{prod.setProdgroupid(rs.getLong("prodgroupid"));}catch(NullPointerException e){}
			try{prod.setProductgroupname(rs.getString("productgroupname"));}catch(NullPointerException e){}
			try{prod.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
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
			prod.setUserDtls(user);
			
			prods.add(prod);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return prods;
	}
	
	public static ProductGroup productGroup(String productGroupId){
		ProductGroup prod = new ProductGroup();
		String pgrpTable = "pgrp";
		String userTable = "usr";
		String sql = "SELECT * FROM productgroup "+ pgrpTable +", userdtls "+ userTable +
				" WHERE "+ pgrpTable +".userdtlsid = "+ userTable +".userdtlsid AND "+ pgrpTable + ".prodgroupid=" + productGroupId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{prod.setProdgroupid(rs.getLong("prodgroupid"));}catch(NullPointerException e){}
			try{prod.setProductgroupname(rs.getString("productgroupname"));}catch(NullPointerException e){}
			try{prod.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
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
			prod.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return prod;
	}
	
	public static void save(ProductGroup prod){
		if(prod!=null){
			
			long id = ProductGroup.getInfo(prod.getProdgroupid() ==0? ProductGroup.getLatestId()+1 : prod.getProdgroupid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ProductGroup.insertData(prod, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ProductGroup.updateData(prod);
			}else if(id==3){
				LogU.add("added new Data ");
				ProductGroup.insertData(prod, "3");
			}
			
		}
	}
	
	public void save(){
		
			
			long id = getInfo(getProdgroupid() ==0? getLatestId()+1 : getProdgroupid());
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
	
	public static ProductGroup insertData(ProductGroup prod, String type){
		String sql = "INSERT INTO productgroup ("
				+ "prodgroupid,"
				+ "productgroupname,"
				+ "isactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productgroup");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			prod.setProdgroupid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			prod.setProdgroupid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, prod.getProductgroupname());
		ps.setInt(3, prod.getIsactive());
		ps.setLong(4, prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		
		LogU.add( prod.getProductgroupname());
		LogU.add( prod.getIsactive());
		LogU.add( prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productgroup : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO productgroup ("
				+ "prodgroupid,"
				+ "productgroupname,"
				+ "isactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productgroup");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setProdgroupid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setProdgroupid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, getProductgroupname());
		ps.setInt(3, getIsactive());
		ps.setLong(4, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add(getProductgroupname());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productgroup : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ProductGroup updateData(ProductGroup prod){
		String sql = "UPDATE productgroup SET "
				+ "productgroupname=?,"
				+ "isactive=?,"
				+ "userdtlsid=? " 
				+ " WHERE prodgroupid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productgroup");
		
		
		ps.setString(1, prod.getProductgroupname());
		ps.setInt(2, prod.getIsactive());
		ps.setLong(3, prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		ps.setLong(4, prod.getProdgroupid());
		
		LogU.add(prod.getProductgroupname());
		LogU.add(prod.getIsactive());
		LogU.add(prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		LogU.add(prod.getProdgroupid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productgroup : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void updateData(){
		String sql = "UPDATE productgroup SET "
				+ "productgroupname=?,"
				+ "isactive=?,"
				+ "userdtlsid=? " 
				+ " WHERE prodgroupid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productgroup");
		
		
		ps.setString(1, getProductgroupname());
		ps.setInt(2, getIsactive());
		ps.setLong(3, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(4, getProdgroupid());
		
		LogU.add(getProductgroupname());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getProdgroupid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productgroup : " + s.getMessage());
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
		sql="SELECT prodgroupid FROM productgroup  ORDER BY prodgroupid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("prodgroupid");
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
		ps = conn.prepareStatement("SELECT prodgroupid FROM productgroup WHERE prodgroupid=?");
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
		String sql = "UPDATE productgroup set isactive=0 WHERE prodgroupid=?";
		
		String[] params = new String[1];
		params[0] = getProdgroupid()+"";
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
	
	public long getProdgroupid() {
		return prodgroupid;
	}
	public void setProdgroupid(long prodgroupid) {
		this.prodgroupid = prodgroupid;
	}
	public String getProductgroupname() {
		return productgroupname;
	}
	public void setProductgroupname(String productgroupname) {
		this.productgroupname = productgroupname;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public UserDtls getUserDtls() {
		return userDtls;
	}

	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}

	public int getIsactive() {
		return isactive;
	}

	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	
	public static void main(String[] args) {
		ProductGroup prod = new ProductGroup();
		//prod.setProdgroupid(1);
		prod.setProductgroupname("Water");
		prod.setUserDtls(UserDtls.addedby("1"));
		prod.setIsactive(1);
		prod.save();
		for(ProductGroup p : ProductGroup.retrieve(prod)){
			System.out.println(p.getProductgroupname());
		}
	}
	
}
