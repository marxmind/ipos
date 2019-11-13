package com.italia.ipos.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 09/27/2016
 * @version 1.0
 *
 */
public class UserTrans {

	private static final long serialVersionUID = 1094801825228386365L;
	
	private Long usertransid;
	private String modulename;
	private String actiondetails;
	private String datetrans;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	public UserTrans(){}
	
	public UserTrans(
			Long usertransid,
			String modulename,
			String actiondetails,
			String datetrans,
			UserDtls userDtls
			){
		this.usertransid = usertransid;
		this.modulename = modulename;
		this.actiondetails = actiondetails;
		this.datetrans = datetrans;
		this.userDtls = userDtls;
	}
	
	public List<UserTrans> retrieve(String sql, String[] params){
		List<UserTrans> users = Collections.synchronizedList(new ArrayList<UserTrans>());
		
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
			UserTrans user = new UserTrans();
			try{user.setUsertransid(rs.getLong("usertransid"));}catch(NullPointerException e){}
			try{user.setModulename(rs.getString("modulename"));}catch(NullPointerException e){}
			try{user.setActiondetails(rs.getString("actiondetails"));}catch(NullPointerException e){}
			try{user.setDatetrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{user.setUserDtls(UserDtls.user(rs.getString("userdtlsId")));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			users.add(user);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return users;
	}
	
	public static void save(UserTrans user){
		if(user!=null){
			
			long id = UserTrans.getInfo(user.getUsertransid() ==null? UserTrans.getLatestId()+1 : user.getUsertransid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				UserTrans.insertData(user, "1");
			}else if(id==2){
				LogU.add("update Data ");
				UserTrans.updateData(user);
			}else if(id==3){
				LogU.add("added new Data ");
				UserTrans.insertData(user, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getInfo(getUsertransid() ==null? getLatestId()+1 : getUsertransid());
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
	
	public static UserTrans insertData(UserTrans user, String type){
		String sql = "INSERT INTO usertrans ("
				+ "usertransid,"
				+ "modulename,"
				+ "actiondetails,"
				+ "datetrans,"
				+ "userdtlsId)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table usertrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			user.setUsertransid(Long.valueOf(id));
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			user.setUsertransid(Long.valueOf(id));
			LogU.add("id: " + id);
		}
		
		ps.setString(2, user.getModulename());
		ps.setString(3, user.getActiondetails());
		ps.setString(4, user.getDatetrans());
		ps.setLong(5, user.getUserDtls()==null? 0l : (user.getUserDtls().getUserdtlsid()==null? 0l : user.getUserDtls().getUserdtlsid()));
		
		LogU.add(user.getModulename());
		LogU.add(user.getActiondetails());
		LogU.add(user.getDatetrans());
		LogU.add(user.getUserDtls()==null? 0l : (user.getUserDtls().getUserdtlsid()==null? 0l : user.getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to usertrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return user;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO usertrans ("
				+ "usertransid,"
				+ "modulename,"
				+ "actiondetails,"
				+ "datetrans,"
				+ "userdtlsId)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table usertrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setUsertransid(Long.valueOf(id));
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setUsertransid(Long.valueOf(id));
			LogU.add("id: " + id);
		}
		
		ps.setString(2, getModulename());
		ps.setString(3, getActiondetails());
		ps.setString(4, getDatetrans());
		ps.setLong(5, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		
		LogU.add(getModulename());
		LogU.add(getActiondetails());
		LogU.add(getDatetrans());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to usertrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static UserTrans updateData(UserTrans user){
		String sql = "UPDATE usertrans SET "
				+ "modulename=?,"
				+ "actiondetails=?,"
				+ "datetrans=?,"
				+ "userdtlsId=? " 
				+ " WHERE usertransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table usertrans");
		
		ps.setString(1, user.getModulename());
		ps.setString(2, user.getActiondetails());
		ps.setString(3, user.getDatetrans());
		ps.setLong(4, user.getUserDtls()==null? 0l : (user.getUserDtls().getUserdtlsid()==null? 0l : user.getUserDtls().getUserdtlsid()));
		ps.setLong(5, user.getUsertransid());
		
		LogU.add(user.getModulename());
		LogU.add(user.getActiondetails());
		LogU.add(user.getDatetrans());
		LogU.add(user.getUserDtls()==null? 0l : (user.getUserDtls().getUserdtlsid()==null? 0l : user.getUserDtls().getUserdtlsid()));
		LogU.add(user.getUsertransid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to usertrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return user;
	}
	
	public void updateData(){
		String sql = "UPDATE usertrans SET "
				+ "modulename=?,"
				+ "actiondetails=?,"
				+ "datetrans=?,"
				+ "userdtlsId=? " 
				+ " WHERE usertransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table usertrans");
		
		ps.setString(1, getModulename());
		ps.setString(2, getActiondetails());
		ps.setString(3, getDatetrans());
		ps.setLong(4, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setLong(5, getUsertransid());
		
		LogU.add(getModulename());
		LogU.add(getActiondetails());
		LogU.add(getDatetrans());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getUsertransid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to usertrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT usertransid FROM usertrans  ORDER BY usertransid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("usertransid");
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
		ps = conn.prepareStatement("SELECT usertransid FROM usertrans WHERE usertransid=?");
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
	
	public Long getUsertransid() {
		return usertransid;
	}
	public void setUsertransid(Long usertransid) {
		this.usertransid = usertransid;
	}
	public String getModulename() {
		return modulename;
	}
	public void setModulename(String modulename) {
		this.modulename = modulename;
	}
	public String getActiondetails() {
		return actiondetails;
	}
	public void setActiondetails(String actiondetails) {
		this.actiondetails = actiondetails;
	}
	public String getDatetrans() {
		return datetrans;
	}
	public void setDatetrans(String datetrans) {
		this.datetrans = datetrans;
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

	public static void main(String[] args) {
		UserTrans u = new UserTrans();
		u.setUsertransid(1l);
		u.setModulename("User");
		u.setActiondetails("testing again");
		u.setDatetrans(DateUtils.getCurrentDateMMDDYYYY());
		u.save(u);
	}
	
}
