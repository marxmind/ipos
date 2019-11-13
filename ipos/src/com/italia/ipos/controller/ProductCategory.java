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
 * @since 10/02/2016
 * @version 1.0
 *
 */
public class ProductCategory {

	private int prodcatid;
	private String catname;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private int isactive;
	
	public ProductCategory(){}
	
	public ProductCategory(
			int prodcatid,
			String catname,
			UserDtls userDtls,
			int isactive
			){
		this.prodcatid = prodcatid;
		this.catname = catname;
		this.userDtls = userDtls;
		this.isactive = isactive;
	}
	
	public static String prodCategorySQL(String tablename,ProductCategory cat){
		String sql="";
		if(cat!=null){
			if(cat.getProdcatid()!=0){
				sql += " AND "+ tablename +".prodcatid=" + cat.getProdcatid();
			}
			
			if(cat.getCatname()!=null){
				sql += " AND "+ tablename +".catname like '%" + cat.getCatname()+"%'";
			}
			
			if(cat.getIsactive()!=0){
				sql += " AND "+ tablename +".isactive=" + cat.getIsactive();
			}
			
			if(cat.getUserDtls()!=null){
				if(cat.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + cat.getUserDtls().getUserdtlsid() ;
				}
			}
		}
		return sql;
	}
	
	public static List<ProductCategory> retrieve(Object...obj){
		List<ProductCategory> cats = Collections.synchronizedList(new ArrayList<ProductCategory>());
		String catTable = "cat";
		String userTable = "usr";
		String sql = "SELECT * FROM productcat "+ catTable +", userdtls "+ userTable +" WHERE "+ catTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			
			if(obj[i] instanceof ProductCategory){
				sql += ProductCategory.prodCategorySQL(catTable,(ProductCategory)obj[i]);
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
			ProductCategory cat = new ProductCategory();
			try{cat.setProdcatid(rs.getInt("prodcatid"));}catch(NullPointerException e){}
			try{cat.setCatname(rs.getString("catname"));}catch(NullPointerException e){}
			try{cat.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
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
			try{cat.setUserDtls(user);}catch(NullPointerException e){}
			
			cats.add(cat);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cats;
	}
	
	public static List<ProductCategory> retrieve(String sql, String[] params){
		List<ProductCategory> cats = Collections.synchronizedList(new ArrayList<ProductCategory>());
		
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
			ProductCategory cat = new ProductCategory();
			try{cat.setProdcatid(rs.getInt("prodcatid"));}catch(NullPointerException e){}
			try{cat.setCatname(rs.getString("catname"));}catch(NullPointerException e){}
			try{cat.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{cat.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));}catch(NullPointerException e){}
			cats.add(cat);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cats;
	}
	
	public static ProductCategory prodcategory(String productCatgeryId){
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "SELECT * FROM productcat WHERE prodcatid=?";
		String[] params = new String[1];
		params[0] = productCatgeryId;
		ProductCategory cat = new ProductCategory();
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
			try{cat.setProdcatid(rs.getInt("prodcatid"));}catch(NullPointerException e){}
			try{cat.setCatname(rs.getString("catname"));}catch(NullPointerException e){}
			try{cat.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{cat.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));}catch(NullPointerException e){}
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cat;
	}
	
	public static void save(ProductCategory cat){
		if(cat!=null){
			
			long id = ProductCategory.getInfo(cat.getProdcatid()==0? ProductCategory.getLatestId()+1 : cat.getProdcatid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ProductCategory.insertData(cat, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ProductCategory.updateData(cat);
			}else if(id==3){
				LogU.add("added new Data ");
				ProductCategory.insertData(cat, "3");
			}
			
		}
	}
	
	public void save(){
					
			long id = getInfo(getProdcatid()==0? getLatestId()+1 : getProdcatid());
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
	
	public static ProductCategory insertData(ProductCategory cat, String type){
		String sql = "INSERT INTO productcat ("
				+ "prodcatid,"
				+ "catname,"
				+ "userdtlsid,"
				+ "isactive) " 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productcat");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(1, id);
			cat.setProdcatid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(1, id);
			cat.setProdcatid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, cat.getCatname());
		ps.setLong(3, cat.getUserDtls()==null? 0 : (cat.getUserDtls().getUserdtlsid()==null? 0 : cat.getUserDtls().getUserdtlsid()));
		ps.setInt(4, cat.getIsactive());
		
		LogU.add(cat.getCatname());
		LogU.add(cat.getUserDtls()==null? 0 : (cat.getUserDtls().getUserdtlsid()==null? 0 : cat.getUserDtls().getUserdtlsid()));
		LogU.add(cat.getIsactive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productcat : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cat;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO productcat ("
				+ "prodcatid,"
				+ "catname,"
				+ "userdtlsid,"
				+ "isactive) " 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productcat");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(1, id);
			setProdcatid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(1, id);
			setProdcatid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, getCatname());
		ps.setLong(3, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(4, getIsactive());
		
		LogU.add(getCatname());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getIsactive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productcat : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ProductCategory updateData(ProductCategory cat){
		String sql = "UPDATE productcat SET "
				+ "catname=?,"
				+ "userdtlsid=?,"
				+ "isactive=? " 
				+ " WHERE prodcatid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productcat");
		
		ps.setString(1, cat.getCatname());
		ps.setLong(2, cat.getUserDtls()==null? 0 : (cat.getUserDtls().getUserdtlsid()==null? 0 : cat.getUserDtls().getUserdtlsid()));
		ps.setInt(3, cat.getIsactive());
		ps.setInt(4, cat.getProdcatid());
		
		
		LogU.add(cat.getCatname());
		LogU.add(cat.getUserDtls()==null? 0 : (cat.getUserDtls().getUserdtlsid()==null? 0 : cat.getUserDtls().getUserdtlsid()));
		LogU.add(cat.getIsactive());
		LogU.add(cat.getProdcatid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productcat : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cat;
	}
	
	public void updateData(){
		String sql = "UPDATE productcat SET "
				+ "catname=?,"
				+ "userdtlsid=?,"
				+ "isactive=? " 
				+ " WHERE prodcatid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productcat");
		
		ps.setString(1,getCatname());
		ps.setLong(2,getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(3, getIsactive());
		ps.setInt(4,getProdcatid());
		
		
		LogU.add(getCatname());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getIsactive());
		LogU.add(getProdcatid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productcat : " + s.getMessage());
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
		sql="SELECT prodcatid FROM productcat  ORDER BY prodcatid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("prodcatid");
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
		ps = conn.prepareStatement("SELECT prodcatid FROM productcat WHERE prodcatid=?");
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
		String sql = "UPDATE productcat set isactive=0 WHERE prodcatid=?";
		
		String[] params = new String[1];
		params[0] = getProdcatid()+"";
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
	
	public int getProdcatid() {
		return prodcatid;
	}
	public void setProdcatid(int prodcatid) {
		this.prodcatid = prodcatid;
	}
	public String getCatname() {
		return catname;
	}
	public void setCatname(String catname) {
		this.catname = catname;
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
	
}
