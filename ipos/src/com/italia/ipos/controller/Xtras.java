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
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 01/22/2017
 * @version 1.0
 *
 */
public class Xtras {

	private long id;
	private String dateTrans;
	private String description;
	private int transType;
	private int isActive;
	private int status;
	private double amount;
	private String remarks;
	private Timestamp timestamp;
	private Customer customer;
	private UserDtls userDtls;
	
	private boolean between;
	private String dateFrom;
	private String dateTo;
	
	private String transactionName;
	
	public Xtras(){}
	public Xtras(
			long id,
			String dateTrans,
			String description,
			int transType,
			int isActive,
			int status,
			double amount,
			String remarks,
			Customer customer,
			UserDtls userDtls
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.description = description;
		this.transType = transType;
		this.isActive = isActive;
		this.status = status;
		this.amount = amount;
		this.remarks = remarks;
		this.customer = customer;
		this.userDtls = userDtls;
	}
	
	public static String xtrasSQL(String tablename,Xtras xtras){
		String sql = " AND " + tablename + ".xIsActive=" + xtras.getIsActive();
		if(xtras!=null){
			if(xtras.getId()!=0){
				sql += " AND "+ tablename +".xid=" + xtras.getId();
			}
			if(xtras.getStatus()!=0){
				sql += " AND "+ tablename +".xStatus=" + xtras.getStatus();
			}
			if(xtras.getDescription()!=null){
				sql += " AND "+ tablename +".xdescription like '%" + xtras.getDescription() +"%'";
			}
			if(xtras.isBetween()){
				
				sql += " AND ( "+ tablename +".xDateTrans>='" + xtras.getDateFrom() +"' AND " + tablename +".xDateTrans<='" + xtras.getDateTo() + "')";
								
			}else{
				if(xtras.getDateTrans()!=null){
					sql += " AND "+ tablename +".xDateTrans='" + xtras.getDateTrans() +"'";
				}
			}
		}	
			
		return sql;
	}
	
	public static List<Xtras> retrieve(Object...obj){
		List<Xtras> xsx = Collections.synchronizedList(new ArrayList<Xtras>());
		
		String xTable = "xt";
		String userTable = "usr";
		String sql = "SELECT * FROM xtras " + xTable +
				", userdtls " + userTable +
				" WHERE "  + xTable + ".userdtlsid=" + userTable + ".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof Xtras){
				sql += xtrasSQL(xTable,(Xtras)obj[i]);
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
		
		System.out.println("Xtras SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Xtras xs = new Xtras();
			try{xs.setId(rs.getLong("xid"));}catch(NullPointerException e){}
			try{xs.setDescription(rs.getString("xdescription"));}catch(NullPointerException e){}
			try{xs.setDateTrans(rs.getString("xDateTrans"));}catch(NullPointerException e){}
			try{xs.setStatus(rs.getInt("xStatus"));}catch(NullPointerException e){}
			try{xs.setIsActive(rs.getInt("xIsActive"));}catch(NullPointerException e){}
			try{xs.setTransType(rs.getInt("xTransType"));}catch(NullPointerException e){}
			try{xs.setAmount(rs.getDouble("xamount"));}catch(NullPointerException e){}
			try{xs.setRemarks(rs.getString("xRemarks"));}catch(NullPointerException e){}
			try{xs.setTimestamp(rs.getTimestamp("xtimestamp"));}catch(NullPointerException e){}
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			xs.setCustomer(cus);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			xs.setUserDtls(user);
			
			xsx.add(xs);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return xsx;
		
	}	
	
	public static List<Xtras> retrieve(String sql, String[] params){
		List<Xtras> xsx = Collections.synchronizedList(new ArrayList<Xtras>());
		
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
			
			Xtras xs = new Xtras();
			try{xs.setId(rs.getLong("xid"));}catch(NullPointerException e){}
			try{xs.setDescription(rs.getString("xdescription"));}catch(NullPointerException e){}
			try{xs.setDateTrans(rs.getString("xDateTrans"));}catch(NullPointerException e){}
			try{xs.setStatus(rs.getInt("xStatus"));}catch(NullPointerException e){}
			try{xs.setIsActive(rs.getInt("xIsActive"));}catch(NullPointerException e){}
			try{xs.setTransType(rs.getInt("xTransType"));}catch(NullPointerException e){}
			try{xs.setAmount(rs.getDouble("xamount"));}catch(NullPointerException e){}
			try{xs.setRemarks(rs.getString("xRemarks"));}catch(NullPointerException e){}
			try{xs.setTimestamp(rs.getTimestamp("xtimestamp"));}catch(NullPointerException e){}
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			xs.setCustomer(cus);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			xs.setUserDtls(user);
			
			xsx.add(xs);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return xsx;
		
	}	
	
	public Xtras retrieve(String xtrasId){
		Xtras xs = new Xtras();
		String xTable = "xt";
		String userTable = "usr";
		String sql = "SELECT * FROM xtras " + xTable +
				", userdtls " + userTable +
				" WHERE "  + xTable + ".userdtlsid=" + userTable + ".userdtlsid AND " + xTable + ".xtrasId=" + xtrasId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("Xtras SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{xs.setId(rs.getLong("xid"));}catch(NullPointerException e){}
			try{xs.setDescription(rs.getString("xdescription"));}catch(NullPointerException e){}
			try{xs.setDateTrans(rs.getString("xDateTrans"));}catch(NullPointerException e){}
			try{xs.setStatus(rs.getInt("xStatus"));}catch(NullPointerException e){}
			try{xs.setIsActive(rs.getInt("xIsActive"));}catch(NullPointerException e){}
			try{xs.setTransType(rs.getInt("xTransType"));}catch(NullPointerException e){}
			try{xs.setAmount(rs.getDouble("xamount"));}catch(NullPointerException e){}
			try{xs.setRemarks(rs.getString("xRemarks"));}catch(NullPointerException e){}
			try{xs.setTimestamp(rs.getTimestamp("xtimestamp"));}catch(NullPointerException e){}
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			xs.setCustomer(cus);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			xs.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return xs;
		
	}	
	
	public static void save(Xtras xt){
		if(xt!=null){
			
			long id = Xtras.getInfo(xt.getId() ==0? Xtras.getLatestId()+1 : xt.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				Xtras.insertData(xt, "1");
			}else if(id==2){
				LogU.add("update Data ");
				Xtras.updateData(xt);
			}else if(id==3){
				LogU.add("added new Data ");
				Xtras.insertData(xt, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getInfo(getId() ==0? getLatestId()+1 : getId());
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
	
	public static Xtras insertData(Xtras xt, String type){
		String sql = "INSERT INTO xtras ("
				+ "xid,"
				+ "xdescription,"
				+ "xDateTrans,"
				+ "xStatus,"
				+ "xIsActive,"
				+ "xTransType,"
				+ "xamount,"
				+ "xRemarks,"
				+ "customerid,"
				+ "userdtlsid)" 
				+ " values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		long id =1;
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table xtras");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(i++, id);
			xt.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(i++, id);
			xt.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(i++, xt.getDescription());
		ps.setString(i++, xt.getDateTrans());
		ps.setInt(i++, xt.getStatus());
		ps.setInt(i++, xt.getIsActive());
		ps.setInt(i++, xt.getTransType());
		ps.setDouble(i++, xt.getAmount());
		ps.setString(i++, xt.getRemarks());
		ps.setLong(i++, xt.getCustomer()==null? 0 : xt.getCustomer().getCustomerid());
		ps.setLong(i++, xt.getUserDtls()==null? 0l : (xt.getUserDtls().getUserdtlsid()==null? 0l : xt.getUserDtls().getUserdtlsid()));
		
		LogU.add(xt.getDescription());
		LogU.add(xt.getDateTrans());
		LogU.add(xt.getStatus());
		LogU.add(xt.getIsActive());
		LogU.add(xt.getTransType());
		LogU.add(xt.getAmount());
		LogU.add(xt.getRemarks());
		LogU.add(xt.getCustomer()==null? 0 : xt.getCustomer().getCustomerid());
		LogU.add(xt.getUserDtls()==null? 0l : (xt.getUserDtls().getUserdtlsid()==null? 0l : xt.getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to xtras : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return xt;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO xtras ("
				+ "xid,"
				+ "xdescription,"
				+ "xDateTrans,"
				+ "xStatus,"
				+ "xIsActive,"
				+ "xTransType,"
				+ "xamount,"
				+ "xRemarks,"
				+ "customerid,"
				+ "userdtlsid)" 
				+ " values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		long id =1;
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table xtras");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(i++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(i++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(i++, getDescription());
		ps.setString(i++, getDateTrans());
		ps.setInt(i++, getStatus());
		ps.setInt(i++, getIsActive());
		ps.setInt(i++, getTransType());
		ps.setDouble(i++, getAmount());
		ps.setString(i++, getRemarks());
		ps.setLong(i++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(i++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		
		LogU.add(getDescription());
		LogU.add(getDateTrans());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getTransType());
		LogU.add(getAmount());
		LogU.add(getRemarks());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to xtras : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static Xtras updateData(Xtras xt){
		String sql = "UPDATE xtras SET "
				+ "xdescription=?,"
				+ "xDateTrans=?,"
				+ "xStatus=?,"
				+ "xTransType=?,"
				+ "xamount=?,"
				+ "xRemarks=?,"
				+ "customerid=?,"
				+ "userdtlsid=? " 
				+ " WHERE xid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table xtras");
		
		ps.setString(i++, xt.getDescription());
		ps.setString(i++, xt.getDateTrans());
		ps.setInt(i++, xt.getStatus());
		ps.setInt(i++, xt.getTransType());
		ps.setDouble(i++, xt.getAmount());
		ps.setString(i++, xt.getRemarks());
		ps.setLong(i++, xt.getCustomer()==null? 0 : xt.getCustomer().getCustomerid());
		ps.setLong(i++, xt.getUserDtls()==null? 0l : (xt.getUserDtls().getUserdtlsid()==null? 0l : xt.getUserDtls().getUserdtlsid()));
		ps.setLong(i++, xt.getId());
		
		LogU.add(xt.getDescription());
		LogU.add(xt.getDateTrans());
		LogU.add(xt.getStatus());
		LogU.add(xt.getTransType());
		LogU.add(xt.getAmount());
		LogU.add(xt.getRemarks());
		LogU.add(xt.getCustomer()==null? 0 : xt.getCustomer().getCustomerid());
		LogU.add(xt.getUserDtls()==null? 0l : (xt.getUserDtls().getUserdtlsid()==null? 0l : xt.getUserDtls().getUserdtlsid()));
		LogU.add(xt.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to xtras : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return xt;
	}
	
	public void updateData(){
		String sql = "UPDATE xtras SET "
				+ "xdescription=?,"
				+ "xDateTrans=?,"
				+ "xStatus=?,"
				+ "xTransType=?,"
				+ "xamount=?,"
				+ "xRemarks=?,"
				+ "customerid=?,"
				+ "userdtlsid=? " 
				+ " WHERE xid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table xtras");
		
		ps.setString(i++, getDescription());
		ps.setString(i++, getDateTrans());
		ps.setInt(i++, getStatus());
		ps.setInt(i++, getTransType());
		ps.setDouble(i++, getAmount());
		ps.setString(i++, getRemarks());
		ps.setLong(i++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(i++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setLong(i++, getId());
		
		LogU.add(getDescription());
		LogU.add(getDateTrans());
		LogU.add(getStatus());
		LogU.add(getTransType());
		LogU.add(getAmount());
		LogU.add(getRemarks());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to xtras : " + s.getMessage());
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
		sql="SELECT xid FROM xtras  ORDER BY xid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("xid");
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
		ps = conn.prepareStatement("SELECT xid FROM xtras WHERE xid=?");
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
		String sql = "UPDATE xtras set xIsActive=0, userdtlsid="+ getUserDtls().getUserdtlsid()	  +"  WHERE xid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
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
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDateTrans() {
		return dateTrans;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getTransType() {
		return transType;
	}
	public void setTransType(int transType) {
		this.transType = transType;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public boolean isBetween() {
		return between;
	}
	public void setBetween(boolean between) {
		this.between = between;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
	public String getTransactionName() {
		return transactionName;
	}
	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	} 
	
}
