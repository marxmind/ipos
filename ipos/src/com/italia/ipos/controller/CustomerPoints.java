package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 09/28/2018
 *
 */
public class CustomerPoints {

	private long id;
	private int isActivated;
	private int isActive;
	private double latestAddedPoints;
	private double latestDeductedPoints;
	private double currentPoints;
	private Customer customer;
	
	private boolean checked;
	
	public static List<CustomerPoints> retrieve(String sqlAdd, String[] params){
		List<CustomerPoints> pts = Collections.synchronizedList(new ArrayList<CustomerPoints>());
		
		String tablePoints = "pts";
		String tableCustomer = "cuz";
		
		String sql = "SELECT * FROM customerpoints "+ tablePoints +", customer "+ tableCustomer +" WHERE  "+
				tablePoints + ".customerid=" +tableCustomer + ".customerid ";
		
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
			
			CustomerPoints pt = new CustomerPoints();
			try{pt.setId(rs.getLong("poid"));}catch(NullPointerException e){}
			try{pt.setIsActivated(rs.getInt("isactivated"));}catch(NullPointerException e){}
			try{pt.setLatestAddedPoints(rs.getDouble("latestaddedpoints"));}catch(NullPointerException e){}
			try{pt.setLatestDeductedPoints(rs.getDouble("latestdeductedpoints"));}catch(NullPointerException e){}
			try{pt.setCurrentPoints(rs.getDouble("currentpoints"));}catch(NullPointerException e){}
			try{pt.setIsActive(rs.getInt("isactivepo"));}catch(NullPointerException e){}
			try{
			if(pt.getIsActivated()==1) {
				pt.setChecked(true);
			}}catch(NullPointerException e){}
			
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
			
			pt.setCustomer(cus);
			
			pts.add(pt);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pts;
	}
	
	public static CustomerPoints retrieve(long id){
		CustomerPoints pt = new CustomerPoints();
		
		String tablePoints = "pts";
		String tableCustomer = "cuz";
		
		String sql = "SELECT * FROM customerpoints "+ tablePoints +", customer "+ tableCustomer +" WHERE  "+
				tablePoints + ".customerid=" +tableCustomer + ".customerid AND " + tablePoints +".isactivepo=1 AND " + tablePoints +".poid=" + id;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{pt.setId(rs.getLong("poid"));}catch(NullPointerException e){}
			try{pt.setIsActivated(rs.getInt("isactivated"));}catch(NullPointerException e){}
			try{pt.setLatestAddedPoints(rs.getDouble("latestaddedpoints"));}catch(NullPointerException e){}
			try{pt.setLatestDeductedPoints(rs.getDouble("latestdeductedpoints"));}catch(NullPointerException e){}
			try{pt.setCurrentPoints(rs.getDouble("currentpoints"));}catch(NullPointerException e){}
			try{pt.setIsActive(rs.getInt("isactivepo"));}catch(NullPointerException e){}
			
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
			
			pt.setCustomer(cus);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pt;
	}
	
	public static void save(CustomerPoints pt){
		if(pt!=null){
			
			long id = CustomerPoints.getInfo(pt.getId() ==0? CustomerPoints.getLatestId()+1 : pt.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				CustomerPoints.insertData(pt, "1");
			}else if(id==2){
				LogU.add("update Data ");
				CustomerPoints.updateData(pt);
			}else if(id==3){
				LogU.add("added new Data ");
				CustomerPoints.insertData(pt, "3");
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
	
	public static CustomerPoints insertData(CustomerPoints pt, String type){
		String sql = "INSERT INTO customerpoints ("
				+ "poid,"
				+ "isactivated,"
				+ "isactivepo,"
				+ "customerid,"
				+ "latestaddedpoints,"
				+ "latestdeductedpoints,"
				+ "currentpoints) " 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customerpoints");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			pt.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			pt.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setInt(cnt++, pt.getIsActivated());
		ps.setInt(cnt++, pt.getIsActive());
		ps.setLong(cnt++, pt.getCustomer()==null? 0 : pt.getCustomer().getCustomerid());
		ps.setDouble(cnt++, pt.getLatestAddedPoints());
		ps.setDouble(cnt++, pt.getLatestDeductedPoints());
		ps.setDouble(cnt++, pt.getCurrentPoints());
		
		LogU.add(pt.getIsActivated());
		LogU.add(pt.getIsActive());
		LogU.add(pt.getCustomer()==null? 0 : pt.getCustomer().getCustomerid());
		LogU.add(pt.getLatestAddedPoints());
		LogU.add(pt.getLatestDeductedPoints());
		LogU.add(pt.getCurrentPoints());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customerpoints : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return pt;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO customerpoints ("
				+ "poid,"
				+ "isactivated,"
				+ "isactivepo,"
				+ "customerid,"
				+ "latestaddedpoints,"
				+ "latestdeductedpoints,"
				+ "currentpoints) " 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customerpoints");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setInt(cnt++, getIsActivated());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setDouble(cnt++, getLatestAddedPoints());
		ps.setDouble(cnt++, getLatestDeductedPoints());
		ps.setDouble(cnt++, getCurrentPoints());
		
		LogU.add(getIsActivated());
		LogU.add(getIsActive());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getLatestAddedPoints());
		LogU.add(getLatestDeductedPoints());
		LogU.add(getCurrentPoints());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customerpoints : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static CustomerPoints updateData(CustomerPoints pt){
		String sql = "UPDATE customerpoints SET "
				+ "isactivated=?,"
				+ "customerid=?,"
				+ "latestaddedpoints=?,"
				+ "latestdeductedpoints=?,"
				+ "currentpoints=? " 
				+ " WHERE poid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customerpoints");
		
		
		ps.setInt(cnt++, pt.getIsActivated());
		ps.setLong(cnt++, pt.getCustomer()==null? 0 : pt.getCustomer().getCustomerid());
		ps.setDouble(cnt++, pt.getLatestAddedPoints());
		ps.setDouble(cnt++, pt.getLatestDeductedPoints());
		ps.setDouble(cnt++, pt.getCurrentPoints());
		ps.setLong(cnt++, pt.getId());
		
		LogU.add(pt.getIsActivated());
		LogU.add(pt.getCustomer()==null? 0 : pt.getCustomer().getCustomerid());
		LogU.add(pt.getLatestAddedPoints());
		LogU.add(pt.getLatestDeductedPoints());
		LogU.add(pt.getCurrentPoints());
		LogU.add(pt.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customerpoints : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return pt;
	}
	
	public void updateData(){
		String sql = "UPDATE customerpoints SET "
				+ "isactivated=?,"
				+ "customerid=?,"
				+ "latestaddedpoints=?,"
				+ "latestdeductedpoints=?,"
				+ "currentpoints=? " 
				+ " WHERE poid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customerpoints");
		
		
		ps.setInt(cnt++, getIsActivated());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setDouble(cnt++, getLatestAddedPoints());
		ps.setDouble(cnt++, getLatestDeductedPoints());
		ps.setDouble(cnt++, getCurrentPoints());
		ps.setLong(cnt++, getId());
		
		LogU.add(getIsActivated());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getLatestAddedPoints());
		LogU.add(getLatestDeductedPoints());
		LogU.add(getCurrentPoints());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customerpoints : " + s.getMessage());
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
		sql="SELECT poid FROM customerpoints  ORDER BY poid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("poid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static long getInfo(long id){
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
		ps = conn.prepareStatement("SELECT poid FROM customerpoints WHERE poid=?");
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
		String sql = "UPDATE customerpoints set isactivepo=0 WHERE poid=?";
		
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
	public int getIsActivated() {
		return isActivated;
	}
	public void setIsActivated(int isActivated) {
		this.isActivated = isActivated;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public double getLatestAddedPoints() {
		return latestAddedPoints;
	}
	public void setLatestAddedPoints(double latestAddedPoints) {
		this.latestAddedPoints = latestAddedPoints;
	}
	public double getLatestDeductedPoints() {
		return latestDeductedPoints;
	}
	public void setLatestDeductedPoints(double latestDeductedPoints) {
		this.latestDeductedPoints = latestDeductedPoints;
	}
	public double getCurrentPoints() {
		return currentPoints;
	}
	public void setCurrentPoints(double currentPoints) {
		this.currentPoints = currentPoints;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
