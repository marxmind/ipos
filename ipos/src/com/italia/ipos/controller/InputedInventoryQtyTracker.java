package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 10/07/2016
 * @version 1.0
 */
public class InputedInventoryQtyTracker {

	
	private long qtyIdtrack;
	private double inputqty;
	private ProductInventory productInventory;
	private long invid;
	private long userdtlsid;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	
	public static void saveQty(ProductInventory inv, String remarks){
		
			String sql = "INSERT INTO qtyaddedtracker ("
					+ "qtyIdtrack,"
					+ "inputqty,"
					+ "invid,"
					+ "userdtlsid,"
					+ "qtyRemarks)" 
					+ "values(?,?,?,?,?)";
			
			PreparedStatement ps = null;
			Connection conn = null;
			
			try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			long id =1;
			LogU.add("===========================START=========================");
			LogU.add("inserting data into table qtyaddedtracker");
			
			id=getLatestId()+1;
			ps.setLong(1, id);
			ps.setDouble(2, inv.getAddqty());
			ps.setLong(3, inv.getInvid());
			ps.setLong(4, Login.getUserLogin().getUserDtls().getUserdtlsid());
			ps.setString(5, remarks);
			
			LogU.add(id);
			LogU.add(inv.getAddqty());
			LogU.add(inv.getInvid());
			LogU.add(Login.getUserLogin().getUserDtls().getUserdtlsid());
			LogU.add(remarks);
			
			LogU.add("executing for saving...");
			ps.execute();
			LogU.add("closing...");
			ps.close();
			ConnectDB.close(conn);
			LogU.add("data has been successfully saved...");
			}catch(SQLException s){
				LogU.add("error inserting data to qtyaddedtracker : " + s.getMessage());
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
	sql="SELECT qtyIdtrack FROM qtyaddedtracker  ORDER BY qtyIdtrack DESC LIMIT 1";	
	conn = ConnectDB.getConnection();
	prep = conn.prepareStatement(sql);	
	rs = prep.executeQuery();
	
	while(rs.next()){
		id = rs.getLong("qtyIdtrack");
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
	ps = conn.prepareStatement("SELECT qtyIdtrack FROM qtyaddedtracker WHERE qtyIdtrack=?");
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

	public long getQtyIdtrack() {
		return qtyIdtrack;
	}
	public void setQtyIdtrack(long qtyIdtrack) {
		this.qtyIdtrack = qtyIdtrack;
	}
	public double getInputqty() {
		return inputqty;
	}
	public void setInputqty(double inputqty) {
		this.inputqty = inputqty;
	}
	public ProductInventory getProductInventory() {
		return productInventory;
	}
	public void setProductInventory(ProductInventory productInventory) {
		this.productInventory = productInventory;
	}
	public long getInvid() {
		return invid;
	}
	public void setInvid(long invid) {
		this.invid = invid;
	}
	public long getUserdtlsid() {
		return userdtlsid;
	}
	public void setUserdtlsid(long userdtlsid) {
		this.userdtlsid = userdtlsid;
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
	
}
