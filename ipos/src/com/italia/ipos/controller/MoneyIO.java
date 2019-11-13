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
 * @author Mark Italia
 * @since 01/16/2017
 * @version 1.0
 *
 */
public class MoneyIO {

	private long id;
	private String dateTrans;
	private String descripion;
	private int transType;
	private double inAmount;
	private double outAmount;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private String receiptNo;
	private Customer customer;
	
	private String transactionName;
	
	public MoneyIO(){}
	
	public MoneyIO(
			long id,
			String dateTrans,
			String description,
			int transType,
			double inAmount,
			double outAmount,
			String receiptNo,
			UserDtls userDtls,
			Customer customer
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.descripion = description;
		this.transType = transType;
		this.inAmount = inAmount;
		this.outAmount = outAmount;
		this.userDtls = userDtls;
		this.customer = customer;
		this.receiptNo = receiptNo;
	}
	
	public static List<MoneyIO> retrieve(String sql, String[] params){
		List<MoneyIO> ios = Collections.synchronizedList(new ArrayList<MoneyIO>());
		
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
			MoneyIO io = new MoneyIO();
			io.setId(rs.getLong("ioid"));
			io.setDateTrans(rs.getString("iodatetrans"));
			io.setDescripion(rs.getString("iodescription"));
			io.setTransType(rs.getInt("iotype"));
			io.setInAmount(rs.getDouble("moneyin"));
			io.setOutAmount(rs.getDouble("moneyout"));
			io.setTimestamp(rs.getTimestamp("iotimestamp"));
			io.setReceiptNo(rs.getString("receiptno"));
			
			Customer customer = new Customer();
			try{customer.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			io.setCustomer(customer);
			
			ios.add(io);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return ios;
	}
	
	public static void save(MoneyIO io){
		if(io!=null){
			
			long id = MoneyIO.getInfo(io.getId() ==0? MoneyIO.getLatestId()+1 : io.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				MoneyIO.insertData(io, "1");
			}else if(id==2){
				LogU.add("update Data ");
				MoneyIO.updateData(io);
			}else if(id==3){
				LogU.add("added new Data ");
				MoneyIO.insertData(io, "3");
			}
			
		}
	}
	
	/*public void save(){
			
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
		
	}*/
	
	public static MoneyIO insertData(MoneyIO io, String type){
		String sql = "INSERT INTO iomoney ("
				+ "ioid,"
				+ "iodatetrans,"
				+ "iodescription,"
				+ "iotype,"
				+ "moneyin,"
				+ "moneyout,"
				+ "userdtlsid,"
				+ "customerid,"
				+ "receiptno) " 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table userdtls");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(i++, id);
			io.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(i++, id);
			io.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(i++, io.getDateTrans());
		ps.setString(i++, io.getDescripion());
		ps.setInt(i++, io.getTransType());
		ps.setDouble(i++, io.getInAmount());
		ps.setDouble(i++, io.getOutAmount());
		ps.setLong(i++, io.getUserDtls()==null? 0l : (io.getUserDtls().getUserdtlsid()==null? 0l : io.getUserDtls().getUserdtlsid()));
		ps.setLong(i++, io.getCustomer()==null? 0 : (io.getCustomer().getCustomerid()==0? 0 : io.getCustomer().getCustomerid()));
		ps.setString(i++, io.getReceiptNo());
		
		LogU.add(io.getDateTrans());
		LogU.add(io.getDescripion());
		LogU.add(io.getTransType());
		LogU.add(io.getInAmount());
		LogU.add(io.getOutAmount());
		LogU.add(io.getUserDtls()==null? 0l : (io.getUserDtls().getUserdtlsid()==null? 0l : io.getUserDtls().getUserdtlsid()));
		LogU.add(io.getCustomer()==null? 0 : (io.getCustomer().getCustomerid()==0? 0 : io.getCustomer().getCustomerid()));
		LogU.add(io.getReceiptNo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to userdtls : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return io;
	}
	
	public static MoneyIO updateData(MoneyIO io){
		String sql = "UPDATE iomoney SET "
				+ "iodatetrans=?,"
				+ "iodescription=?,"
				+ "iotype=?,"
				+ "moneyin=?,"
				+ "moneyout=?,"
				+ "userdtlsid=?,"
				+ "customerid=?,"
				+ "receiptno=?" 
				+ " WHERE ioid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table iomoney");
		int i=1;
		ps.setString(i++, io.getDateTrans());
		ps.setString(i++, io.getDescripion());
		ps.setInt(i++, io.getTransType());
		ps.setDouble(i++, io.getInAmount());
		ps.setDouble(i++, io.getOutAmount());
		ps.setLong(i++, io.getUserDtls()==null? 0l : (io.getUserDtls().getUserdtlsid()==null? 0l : io.getUserDtls().getUserdtlsid()));
		ps.setLong(i++, io.getCustomer()==null? 0 : (io.getCustomer().getCustomerid()==0? 0 : io.getCustomer().getCustomerid()));
		ps.setString(i++, io.getReceiptNo());
		ps.setLong(i++, io.getId());
		
		LogU.add(io.getDateTrans());
		LogU.add(io.getDescripion());
		LogU.add(io.getTransType());
		LogU.add(io.getInAmount());
		LogU.add(io.getOutAmount());
		LogU.add(io.getUserDtls()==null? 0l : (io.getUserDtls().getUserdtlsid()==null? 0l : io.getUserDtls().getUserdtlsid()));
		LogU.add(io.getCustomer()==null? 0 : (io.getCustomer().getCustomerid()==0? 0 : io.getCustomer().getCustomerid()));
		LogU.add(io.getReceiptNo());
		LogU.add(io.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to iomoney : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return io;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT ioid FROM iomoney  ORDER BY ioid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("ioid");
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
		ps = conn.prepareStatement("SELECT ioid FROM iomoney WHERE ioid=?");
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
	
	public void delete(boolean retain){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE userdtls set isactive=0 WHERE userdtlsid=?";
		
		if(!retain){
			sql = "DELETE FROM userdtls WHERE userdtlsid=?";
		}
		
		String[] params = new String[1];
		params[0] = Login.getUserLogin().getUserDtls().getUserdtlsid()+"";
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
	public String getDescripion() {
		return descripion;
	}
	public void setDescripion(String descripion) {
		this.descripion = descripion;
	}
	public int getTransType() {
		return transType;
	}
	public void setTransType(int transType) {
		this.transType = transType;
	}
	public double getInAmount() {
		return inAmount;
	}
	public void setInAmount(double inAmount) {
		this.inAmount = inAmount;
	}
	public double getOutAmount() {
		return outAmount;
	}
	public void setOutAmount(double outAmount) {
		this.outAmount = outAmount;
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

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	
}
