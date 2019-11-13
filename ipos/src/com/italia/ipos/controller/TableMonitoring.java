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
import com.italia.ipos.room.controller.Rooms;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 05/28/2017
 * @version 1.0
 */
public class TableMonitoring {

	private long id;
	private String dateTrans;
	private String tableNumber;
	private String tableName;
	private String customerName;
	private int status;
	private int isActive;
	private String remarks;
	private Timestamp timestamp;
	
	private Customer customer;
	private ProductRunning productRunning;
	
	public TableMonitoring(){}
	
	public TableMonitoring(
			long id,
			String dateTrans,
			String tableNumber,
			String tableName,
			String customerName,
			int status,
			int isActive,
			String remarks,
			Customer customer,
			ProductRunning productRunning
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.tableNumber = tableNumber;
		this.tableName = tableName;
		this.customerName = customerName;
		this.status = status;
		this.isActive = isActive;
		this.remarks = remarks;
		this.customer = customer;
		this.productRunning = productRunning;
	}
	
	public static String tableSQL(String tablename,TableMonitoring tble){
		String sql = " tIsActive=" + tble.getIsActive();
		if(tble!=null){
			if(tble.getId()!=0){
				sql += " AND "+ tablename +".tId=" + tble.getId();
			}
			if(tble.getDateTrans()!=null){
				sql += " AND "+ tablename +".tDateTrans='" + tble.getDateTrans()+"'";
			}
			if(tble.getTableNumber()!=null){
				sql += " AND "+ tablename +".tNo like '%" + tble.getTableNumber() +"%'";
			}
			if(tble.getTableName()!=null){
				sql += " AND "+ tablename +".tName like '%" + tble.getTableName() +"%'";
			}
			if(tble.getStatus()!=0){
				sql += " AND "+ tablename +".tStatus=" + tble.getStatus();
			}
		}
		return sql;
	}
	
	public static List<TableMonitoring> retrieve(Object... obj){
		List<TableMonitoring> mons = Collections.synchronizedList(new ArrayList<TableMonitoring>());
		
		String monTable = "mon";
		String sql = "SELECT * FROM tablemonitoring "+ monTable + 
				" WHERE "; 
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof TableMonitoring){
				sql += tableSQL(monTable,(TableMonitoring)obj[i]);
			}
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			TableMonitoring mon = new TableMonitoring();
			try{mon.setId(rs.getLong("tId"));}catch(NullPointerException e){}
			try{mon.setDateTrans(rs.getString("tDateTrans"));}catch(NullPointerException e){}
			try{mon.setTableNumber(rs.getString("tNo"));}catch(NullPointerException e){}
			try{mon.setTableName(rs.getString("tName"));}catch(NullPointerException e){}
			try{mon.setCustomerName(rs.getString("tCustomerName"));}catch(NullPointerException e){}
			try{mon.setStatus(rs.getInt("tStatus"));}catch(NullPointerException e){}
			try{mon.setIsActive(rs.getInt("tIsActive"));}catch(NullPointerException e){}
			try{mon.setRemarks(rs.getString("tRemarks"));}catch(NullPointerException e){}
			try{mon.setTimestamp(rs.getTimestamp("ttimestamp"));}catch(NullPointerException e){}
			
			Customer customer = new Customer();
			try{customer.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			mon.setCustomer(customer);
			
			ProductRunning run = new ProductRunning();
			try{run.setRunid(rs.getLong("runid"));}catch(NullPointerException e){}
			mon.setProductRunning(run);
			
			mons.add(mon);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mons;
	}
	
	public static void save(TableMonitoring mon){
		if(mon!=null){
			
			long id = TableMonitoring.getInfo(mon.getId() ==0? TableMonitoring.getLatestId()+1 : mon.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				TableMonitoring.insertData(mon, "1");
			}else if(id==2){
				LogU.add("update Data ");
				TableMonitoring.updateData(mon);
			}else if(id==3){
				LogU.add("added new Data ");
				TableMonitoring.insertData(mon, "3");
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
	
	public static TableMonitoring insertData(TableMonitoring mon, String type){
		String sql = "INSERT INTO tablemonitoring ("
				+ "tId,"
				+ "tDateTrans,"
				+ "tNo,"
				+ "tName,"
				+ "tCustomerName,"
				+ "tStatus,"
				+ "tIsActive,"
				+ "tRemarks,"
				+ "customerid,"
				+ "runid) " 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table tablemonitoring");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			mon.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			mon.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, mon.getDateTrans());
		ps.setString(cnt++, mon.getTableNumber());
		ps.setString(cnt++, mon.getTableName());
		ps.setString(cnt++, mon.getCustomerName());
		ps.setInt(cnt++, mon.getStatus());
		ps.setInt(cnt++, mon.getIsActive());
		ps.setString(cnt++, mon.getRemarks());
		ps.setLong(cnt++, mon.getCustomer()==null? 0 : mon.getCustomer().getCustomerid());
		ps.setLong(cnt++, mon.getProductRunning()==null? 0 : mon.getProductRunning().getRunid());
		
		LogU.add(mon.getDateTrans());
		LogU.add(mon.getTableNumber());
		LogU.add(mon.getTableName());
		LogU.add(mon.getCustomerName());
		LogU.add(mon.getStatus());
		LogU.add(mon.getIsActive());
		LogU.add(mon.getRemarks());
		LogU.add(mon.getCustomer()==null? 0 : mon.getCustomer().getCustomerid());
		LogU.add(mon.getProductRunning()==null? 0 : mon.getProductRunning().getRunid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to tablemonitoring : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mon;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO tablemonitoring ("
				+ "tId,"
				+ "tDateTrans,"
				+ "tNo,"
				+ "tName,"
				+ "tCustomerName,"
				+ "tStatus,"
				+ "tIsActive,"
				+ "tRemarks,"
				+ "customerid,"
				+ "runid) " 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table tablemonitoring");
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
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getTableNumber());
		ps.setString(cnt++, getTableName());
		ps.setString(cnt++, getCustomerName());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getIsActive());
		ps.setString(cnt++, getRemarks());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(cnt++, getProductRunning()==null? 0 : getProductRunning().getRunid());
		
		LogU.add(getDateTrans());
		LogU.add(getTableNumber());
		LogU.add(getTableName());
		LogU.add(getCustomerName());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getRemarks());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getProductRunning()==null? 0 : getProductRunning().getRunid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to tablemonitoring : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static TableMonitoring updateData(TableMonitoring mon){
		String sql = "UPDATE tablemonitoring SET "
				+ "tDateTrans=?,"
				+ "tNo=?,"
				+ "tName=?,"
				+ "tCustomerName=?,"
				+ "tStatus=?,"
				+ "tRemarks=?,"
				+ "customerid=?,"
				+ "runid=? " 
				+ " WHERE tId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table tablemonitoring");
		
		ps.setString(cnt++, mon.getDateTrans());
		ps.setString(cnt++, mon.getTableNumber());
		ps.setString(cnt++, mon.getTableName());
		ps.setString(cnt++, mon.getCustomerName());
		ps.setInt(cnt++, mon.getStatus());
		ps.setString(cnt++, mon.getRemarks());
		ps.setLong(cnt++, mon.getCustomer()==null? 0 : mon.getCustomer().getCustomerid());
		ps.setLong(cnt++, mon.getProductRunning()==null? 0 : mon.getProductRunning().getRunid());
		ps.setLong(cnt++, mon.getId());
		
		LogU.add(mon.getDateTrans());
		LogU.add(mon.getTableNumber());
		LogU.add(mon.getTableName());
		LogU.add(mon.getCustomerName());
		LogU.add(mon.getStatus());
		LogU.add(mon.getRemarks());
		LogU.add(mon.getCustomer()==null? 0 : mon.getCustomer().getCustomerid());
		LogU.add(mon.getProductRunning()==null? 0 : mon.getProductRunning().getRunid());
		LogU.add(mon.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to tablemonitoring : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mon;
	}
	
	public void updateData(){
		String sql = "UPDATE tablemonitoring SET "
				+ "tDateTrans=?,"
				+ "tNo=?,"
				+ "tName=?,"
				+ "tCustomerName=?,"
				+ "tStatus=?,"
				+ "tRemarks=?,"
				+ "customerid=?,"
				+ "runid=? " 
				+ " WHERE tId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table tablemonitoring");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getTableNumber());
		ps.setString(cnt++, getTableName());
		ps.setString(cnt++, getCustomerName());
		ps.setInt(cnt++, getStatus());
		ps.setString(cnt++, getRemarks());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(cnt++, getProductRunning()==null? 0 : getProductRunning().getRunid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getTableNumber());
		LogU.add(getTableName());
		LogU.add(getCustomerName());
		LogU.add(getStatus());
		LogU.add(getRemarks());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getProductRunning()==null? 0 : getProductRunning().getRunid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to tablemonitoring : " + s.getMessage());
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
		sql="SELECT tId FROM tablemonitoring  ORDER BY tId DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("tId");
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
		ps = conn.prepareStatement("SELECT tId FROM tablemonitoring WHERE tId=?");
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
		String sql = "UPDATE tablemonitoring set tIsActive=0 WHERE tId=?";
		
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
	public String getTableNumber() {
		return tableNumber;
	}
	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
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
	public ProductRunning getProductRunning() {
		return productRunning;
	}
	public void setProductRunning(ProductRunning productRunning) {
		this.productRunning = productRunning;
	}
	
	public static void main(String[] args) {
		
		TableMonitoring mon = new TableMonitoring();
		//mon.setId(1);
		mon.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		mon.setTableNumber("0012");
		mon.setTableName("Testing ");
		mon.setCustomerName("Mark ");
		mon.setStatus(1);
		mon.setIsActive(1);
		mon.setRemarks("Ok again");
		
		Customer cus = new Customer();
		cus.setCustomerid(1l);
		mon.setCustomer(cus);
		
		ProductRunning run = new ProductRunning();
		run.setRunid(1);
		mon.setProductRunning(run);
		
		mon.save();
		
		
		
	}
}
