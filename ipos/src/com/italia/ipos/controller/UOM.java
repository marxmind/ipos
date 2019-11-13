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
 * @since 10/01/2016
 *@version 1.0
 */
public class UOM {

	private int uomid;
	private String uomname;
	private UserDtls userDtls;
	private String symbol;
	private Timestamp timestamp;
	private int isactive;
	
	public UOM(){}
	
	public UOM(
			int uomid,
			String uomname,
			String symbol,
			UserDtls userDtls,
			int isactive
			){
		this.uomid = uomid;
		this.uomname = uomname;
		this.symbol = symbol;
		this.userDtls = userDtls;
		this.isactive = isactive;
	}
	
	public static String uomSQL(String tablename,UOM uom){
		String sql="";
		if(uom!=null){
			if(uom.getUomid()!=0){
				sql += " AND "+ tablename +".uomid=" + uom.getUomid();
			}
			if(uom.getUomname()!=null){
				sql += " AND "+ tablename +".uomname like '%" + uom.getUomname()+"%'";
			}
			if(uom.getSymbol()!=null){
				sql += " AND "+ tablename +".symbol like '%" + uom.getSymbol()+"%'";
			}
			if(uom.getIsactive()!=0){
				sql += " AND "+ tablename +".isactive=" + uom.getIsactive();
			}
			if(uom.getUserDtls()!=null){
				if(uom.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + uom.getUserDtls().getUserdtlsid() ;
				}
			}
		}
		return sql;
	}
	
	public static List<UOM> retrieve(Object... obj){
		List<UOM> uoms = Collections.synchronizedList(new ArrayList<UOM>());
		String uomTable = "uom";
		String userTable = "usr";
		String sql = "SELECT * FROM uom "+ uomTable +", userdtls "+ userTable +" WHERE "+ uomTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof UOM){
				sql += UOM.uomSQL(uomTable,(UOM)obj[i]);
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
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
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
			uom.setUserDtls(user);
			
			uoms.add(uom);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return uoms;
	}
	
	public static List<UOM> retrieve(String sql, String[] params){
		List<UOM> uoms = Collections.synchronizedList(new ArrayList<UOM>());
		
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
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			uoms.add(uom);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return uoms;
	}
	
	public static UOM uom(String uomid){
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "SELECT * FROM uom WHERE uomid=?";
		String[] params = new String[1];
		params[0] = uomid;
		UOM uom = new UOM(); 
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
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		return uom;
	}
	
	public static void save(UOM uom){
		if(uom!=null){
			
			long id = UOM.getInfo(uom.getUomid() ==0? UOM.getLatestId()+1 : uom.getUomid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				UOM.insertData(uom, "1");
			}else if(id==2){
				LogU.add("update Data ");
				UOM.updateData(uom);
			}else if(id==3){
				LogU.add("added new Data ");
				UOM.insertData(uom, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getInfo(getUomid() ==0? getLatestId()+1 : getUomid());
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
	
	public static UOM insertData(UOM uom, String type){
		String sql = "INSERT INTO uom ("
				+ "uomid,"
				+ "uomname,"
				+ "symbol,"
				+ "userdtlsid,"
				+ "isactive) " 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table uom");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(1, id);
			uom.setUomid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(1, id);
			uom.setUomid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, uom.getUomname());
		ps.setString(3, uom.getSymbol());
		ps.setLong(4, uom.getUserDtls()==null? 0 : (uom.getUserDtls().getUserdtlsid()==null? 0 : uom.getUserDtls().getUserdtlsid()));
		ps.setInt(5, uom.getIsactive());
		
		LogU.add(uom.getUomname());
		LogU.add(uom.getSymbol());
		LogU.add(uom.getUserDtls()==null? 0 : (uom.getUserDtls().getUserdtlsid()==null? 0 : uom.getUserDtls().getUserdtlsid()));
		LogU.add(uom.getIsactive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to uom : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return uom;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO uom ("
				+ "uomid,"
				+ "uomname,"
				+ "symbol,"
				+ "userdtlsid,"
				+ "isactive) " 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table uom");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(1, id);
			setUomid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(1, id);
			setUomid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, getUomname());
		ps.setString(3, getSymbol());
		ps.setLong(4, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(5, getIsactive());
		
		LogU.add(getUomname());
		LogU.add(getSymbol());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getIsactive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to uom : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static UOM updateData(UOM uom){
		String sql = "UPDATE uom SET "
				+ "uomname=?,"
				+ "symbol=?,"
				+ "userdtlsid=?,"
				+ "isactive=? " 
				+ " WHERE uomid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table uom");
		
		ps.setString(1, uom.getUomname());
		ps.setString(2, uom.getSymbol());
		ps.setLong(3, uom.getUserDtls()==null? 0 : (uom.getUserDtls().getUserdtlsid()==null? 0 : uom.getUserDtls().getUserdtlsid()));
		ps.setInt(4, uom.getIsactive());
		ps.setInt(5, uom.getUomid());
		
		
		LogU.add( uom.getUomname());
		LogU.add(uom.getSymbol());
		LogU.add( uom.getUserDtls()==null? 0 : (uom.getUserDtls().getUserdtlsid()==null? 0 : uom.getUserDtls().getUserdtlsid()));
		LogU.add(uom.getIsactive());
		LogU.add( uom.getUomid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to uom : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return uom;
	}
	
	public void updateData(){
		String sql = "UPDATE uom SET "
				+ "uomname=?,"
				+ "symbol=?,"
				+ "userdtlsid=?,"
				+ "isactive=? " 
				+ " WHERE uomid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table uom");
		
		ps.setString(1, getUomname());
		ps.setString(2, getSymbol());
		ps.setLong(3, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(4, getIsactive());
		ps.setInt(5, getUomid());
		
		
		LogU.add(getUomname());
		LogU.add(getSymbol());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getIsactive());
		LogU.add(getUomid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to uom : " + s.getMessage());
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
		sql="SELECT uomid FROM uom  ORDER BY uomid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("uomid");
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
		ps = conn.prepareStatement("SELECT uomid FROM uom WHERE uomid=?");
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
		String sql = "UPDATE uom set isactive=0 WHERE uomid=?";
		
		String[] params = new String[1];
		params[0] = getUomid()+"";
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
	
	public int getUomid() {
		return uomid;
	}
	public void setUomid(int uomid) {
		this.uomid = uomid;
	}
	public String getUomname() {
		return uomname;
	}
	public void setUomname(String uomname) {
		this.uomname = uomname;
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
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getIsactive() {
		return isactive;
	}

	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	
}
