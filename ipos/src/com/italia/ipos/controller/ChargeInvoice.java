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
 * @since 05/27/2018
 *
 */
public class ChargeInvoice {

	private long id;
	private String receiptNo;
	private double balanceAmount;
	private int terms;
	private String dueDate;
	private int isActive;
	
	private Transactions transactions;
	private Customer customer;
	
	public static List<ChargeInvoice> retrieve(String sqlAdd, String[] params){
		List<ChargeInvoice> ins = Collections.synchronizedList(new ArrayList<ChargeInvoice>());
		
		String sql = "SELECT * FROM chargeinvoice WHERE isactivech=1 ";
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
			ChargeInvoice in = new ChargeInvoice();
			
			try{in.setId(rs.getLong("chid"));}catch(NullPointerException e) {}
			try{in.setReceiptNo(rs.getString("receiptno"));}catch(NullPointerException e) {}
			try{in.setBalanceAmount(rs.getDouble("balamount"));}catch(NullPointerException e) {}
			try{in.setTerms(rs.getInt("terms"));}catch(NullPointerException e) {}
			try{in.setDueDate(rs.getString("duedate"));}catch(NullPointerException e) {}
			try{in.setIsActive(rs.getInt("isactivech"));}catch(NullPointerException e) {}
			
			Transactions tran = new Transactions();
			try{tran.setTransid(rs.getLong("transid"));}catch(NullPointerException e) {}
			in.setTransactions(tran);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e) {}
			in.setCustomer(cus);
			
			ins.add(in);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return ins;
	}
	
	public static ChargeInvoice retrieve(String transId){
		ChargeInvoice in = new ChargeInvoice();
		
		String sql = "SELECT * FROM chargeinvoice WHERE isactivech=1 AND transid="+ transId;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{in.setId(rs.getLong("chid"));}catch(NullPointerException e) {}
			try{in.setReceiptNo(rs.getString("receiptno"));}catch(NullPointerException e) {}
			try{in.setBalanceAmount(rs.getDouble("balamount"));}catch(NullPointerException e) {}
			try{in.setTerms(rs.getInt("terms"));}catch(NullPointerException e) {}
			try{in.setDueDate(rs.getString("duedate"));}catch(NullPointerException e) {}
			try{in.setIsActive(rs.getInt("isactivech"));}catch(NullPointerException e) {}
			
			Transactions tran = new Transactions();
			try{tran.setTransid(rs.getLong("transid"));}catch(NullPointerException e) {}
			in.setTransactions(tran);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e) {}
			in.setCustomer(cus);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return in;
	}
	
	public static void save(ChargeInvoice rpt){
		if(rpt!=null){
			
			long id = ChargeInvoice.getInfo(rpt.getId() ==0? ChargeInvoice.getLatestId()+1 : rpt.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ChargeInvoice.insertData(rpt, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ChargeInvoice.updateData(rpt);
			}else if(id==3){
				LogU.add("added new Data ");
				ChargeInvoice.insertData(rpt, "3");
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
	
	public static ChargeInvoice insertData(ChargeInvoice rpt, String type){
		String sql = "INSERT INTO chargeinvoice ("
				+ "chid,"
				+ "receiptno,"
				+ "balamount,"
				+ "terms,"
				+ "duedate,"
				+ "isactivech,"
				+ "transid,"
				+ "customerid) " 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table chargeinvoice");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			rpt.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			rpt.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, rpt.getReceiptNo());
		ps.setDouble(cnt++, rpt.getBalanceAmount());
		ps.setInt(cnt++, rpt.getTerms());
		ps.setString(cnt++, rpt.getDueDate());
		ps.setInt(cnt++, rpt.getIsActive());
		ps.setLong(cnt++, rpt.getTransactions()==null? 0 : rpt.getTransactions().getTransid());
		ps.setLong(cnt++, rpt.getCustomer()==null? 0 : rpt.getCustomer().getCustomerid());
		
		
		LogU.add(rpt.getReceiptNo());
		LogU.add(rpt.getBalanceAmount());
		LogU.add(rpt.getTerms());
		LogU.add(rpt.getDueDate());
		LogU.add(rpt.getIsActive());
		LogU.add(rpt.getTransactions()==null? 0 : rpt.getTransactions().getTransid());
		LogU.add(rpt.getCustomer()==null? 0 : rpt.getCustomer().getCustomerid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to chargeinvoice : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rpt;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO chargeinvoice ("
				+ "chid,"
				+ "receiptno,"
				+ "balamount,"
				+ "terms,"
				+ "duedate,"
				+ "isactivech,"
				+ "transid,"
				+ "customerid) " 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table chargeinvoice");
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
		
		ps.setString(cnt++, getReceiptNo());
		ps.setDouble(cnt++, getBalanceAmount());
		ps.setInt(cnt++, getTerms());
		ps.setString(cnt++, getDueDate());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getTransactions()==null? 0 : getTransactions().getTransid());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		
		
		LogU.add(getReceiptNo());
		LogU.add(getBalanceAmount());
		LogU.add(getTerms());
		LogU.add(getDueDate());
		LogU.add(getIsActive());
		LogU.add(getTransactions()==null? 0 : getTransactions().getTransid());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to chargeinvoice : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ChargeInvoice updateData(ChargeInvoice rpt){
		String sql = "UPDATE chargeinvoice SET "
				+ "receiptno=?,"
				+ "balamount=?,"
				+ "terms=?,"
				+ "duedate=?,"
				+ "transid=?,"
				+ "customerid=? " 
				+ " WHERE chid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("updating data into table chargeinvoice");
		
		
		ps.setString(cnt++, rpt.getReceiptNo());
		ps.setDouble(cnt++, rpt.getBalanceAmount());
		ps.setInt(cnt++, rpt.getTerms());
		ps.setString(cnt++, rpt.getDueDate());
		ps.setLong(cnt++, rpt.getTransactions()==null? 0 : rpt.getTransactions().getTransid());
		ps.setLong(cnt++, rpt.getCustomer()==null? 0 : rpt.getCustomer().getCustomerid());
		ps.setLong(cnt++, rpt.getId());
		
		
		LogU.add(rpt.getReceiptNo());
		LogU.add(rpt.getBalanceAmount());
		LogU.add(rpt.getTerms());
		LogU.add(rpt.getDueDate());
		LogU.add(rpt.getTransactions()==null? 0 : rpt.getTransactions().getTransid());
		LogU.add(rpt.getCustomer()==null? 0 : rpt.getCustomer().getCustomerid());
		LogU.add(rpt.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to chargeinvoice : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rpt;
	}
	
	public void updateData(){
		String sql = "UPDATE chargeinvoice SET "
				+ "receiptno=?,"
				+ "balamount=?,"
				+ "terms=?,"
				+ "duedate=?,"
				+ "transid=?,"
				+ "customerid=? " 
				+ " WHERE chid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("updating data into table chargeinvoice");
		
		
		ps.setString(cnt++, getReceiptNo());
		ps.setDouble(cnt++, getBalanceAmount());
		ps.setInt(cnt++, getTerms());
		ps.setString(cnt++, getDueDate());
		ps.setLong(cnt++, getTransactions()==null? 0 : getTransactions().getTransid());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(cnt++, getId());
		
		
		LogU.add(getReceiptNo());
		LogU.add(getBalanceAmount());
		LogU.add(getTerms());
		LogU.add(getDueDate());
		LogU.add(getTransactions()==null? 0 : getTransactions().getTransid());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to chargeinvoice : " + s.getMessage());
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
		sql="SELECT chid FROM chargeinvoice  ORDER BY chid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("chid");
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
		ps = conn.prepareStatement("SELECT chid FROM chargeinvoice WHERE chid=?");
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
		String sql = "UPDATE chargeinvoice set isactivech=0 WHERE chid=?";
		
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
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public double getBalanceAmount() {
		return balanceAmount;
	}
	public void setBalanceAmount(double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	public int getTerms() {
		return terms;
	}
	public void setTerms(int terms) {
		this.terms = terms;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Transactions getTransactions() {
		return transactions;
	}
	public void setTransactions(Transactions transactions) {
		this.transactions = transactions;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}
