package com.italia.ipos.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 11/05/2016
 * @version 1.0
 */
public class CustomerPaymentTrans {

	private long paytransid;
	private CustomerPayment customerPayment;
	private String paymentdate;
	private BigDecimal amountpay;
	private int paymentType;
	private String remarks;
	private int ispaid;
	private int paytransisactive;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private String receiptNo;
	private boolean between;
	private String dateFrom;
	private String dateTo;
	private boolean enableEdit;
	
	private String description;
	
	public CustomerPaymentTrans(){}
	
	public CustomerPaymentTrans(
			long paytransid,
			CustomerPayment customerPayment,
			String paymentdate,
			BigDecimal amountpay,
			int ispaid,
			int paytransisactive,
			UserDtls userDtls,
			int paymentType,
			String remarks,
			String receiptNo
			){
		this.paytransid = paytransid;
		this.customerPayment = customerPayment;
		this.paymentdate = paymentdate;
		this.amountpay = amountpay;
		this.ispaid = ispaid;
		this.paytransisactive = paytransisactive;
		this.userDtls = userDtls;
		this.paymentType = paymentType;
		this.remarks = remarks;
		this.receiptNo = receiptNo;
	}
	
	public static String customerPaymentTransSQL(String tablename,CustomerPaymentTrans cus){
		String sql= " AND "+ tablename +".paytransisactive=" + cus.getPaytransisactive();
		if(cus!=null){
			
			if(cus.getPaytransid()!=0){
				sql += " AND "+ tablename +".paytransid=" + cus.getPaytransid();
			}
			
			if(cus.getPaymentType()!=0){
				sql += " AND "+ tablename +".paymentType=" + cus.getPaymentType();
			}
			
			if(cus.getCustomerPayment()!=null){
				if(cus.getCustomerPayment().getCpayid()!=0){
					sql += " AND "+ tablename +".cpayid=" + cus.getCustomerPayment().getCpayid();
				}
			}
			
			
			if(cus.getReceiptNo()!=null){
					sql += " AND "+ tablename +".receiptno='" + cus.getReceiptNo() +"'";
			}
			
			
			if(cus.isBetween()){
				sql += " AND ( "+ tablename +".paymentdate>='" + cus.getDateFrom()+"' AND " + tablename +".paymentdate<='" + cus.getDateTo() + "')";
			}else{
			
				if(cus.getPaymentdate()!=null){
					sql += " AND "+ tablename +".paymentdate='" + cus.getPaymentdate()+"'";
				}
			
			}
			if(cus.getAmountpay()!=null){
				sql += " AND "+ tablename +".amountpay=" + cus.getAmountpay();
			}
			
			if(cus.getIspaid()==1){
				sql += " AND "+ tablename +".ispaid=" + cus.getIspaid();
			}else{
				sql += " AND "+ tablename +".ispaid=0";
			}
			
			if(cus.getUserDtls()!=null){
				if(cus.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + cus.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		
		return sql;
	}

	public static List<CustomerPaymentTrans> retrieve(Object... obj){
		List<CustomerPaymentTrans> pays = Collections.synchronizedList(new ArrayList<CustomerPaymentTrans>());
		String payTable = "pay";
		String paytransTable = "trans";
		
		String sql = "SELECT * FROM customerpaymenttrans "+ paytransTable +",customerpayment "+ payTable+" WHERE " +
				paytransTable + ".cpayid=" + payTable + ".cpayid ";
				
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof CustomerPaymentTrans){
				sql += customerPaymentTransSQL(paytransTable,(CustomerPaymentTrans)obj[i]);
			}
			if(obj[i] instanceof CustomerPayment){
				sql += CustomerPayment.customerPaymentSQL(payTable,(CustomerPayment)obj[i]);
			}
		}
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			CustomerPaymentTrans trans = new CustomerPaymentTrans();
			try{trans.setPaytransid(rs.getLong("paytransid"));}catch(NullPointerException e){}
			try{trans.setPaymentdate(rs.getString("paymentdate"));}catch(NullPointerException e){}
			try{trans.setAmountpay(rs.getBigDecimal("amountpay"));}catch(NullPointerException e){}
			try{trans.setIspaid(rs.getInt("ispaid"));}catch(NullPointerException e){}
			try{trans.setPaytransisactive(rs.getInt("paytransisactive"));}catch(NullPointerException e){}
			try{trans.setPaymentType(rs.getInt("paymentType"));}catch(NullPointerException e){}
			try{trans.setRemarks(rs.getString("payRemarks"));}catch(NullPointerException e){}
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{trans.setReceiptNo(rs.getString("receiptno"));}catch(NullPointerException e){}
			try{
				if(PaymentTransactionType.STORE.getId()==trans.getPaymentType()) {
					trans.setEnableEdit(false);
				}else {
					trans.setEnableEdit(true);
				}
			}catch(NullPointerException e){}
			
			CustomerPayment pay = new CustomerPayment();
			try{pay.setCpayid(rs.getLong("cpayid"));}catch(NullPointerException e){}
			try{pay.setAmountpaid(rs.getBigDecimal("amountpaid"));}catch(NullPointerException e){}
			try{pay.setAmountpaiddate(rs.getString("amountpaiddate"));}catch(NullPointerException e){}
			try{pay.setAmountprevpaid(rs.getBigDecimal("amountprevpaid"));}catch(NullPointerException e){}
			try{pay.setAmountpaiddate(rs.getString("amountprevpaiddate"));}catch(NullPointerException e){}
			try{pay.setAmountbalance(rs.getBigDecimal("amountbalance"));}catch(NullPointerException e){}
			try{pay.setAmountprevbalance(rs.getBigDecimal("amountprevbalance"));}catch(NullPointerException e){}
			try{pay.setPayisactive(rs.getInt("payisactive"));}catch(NullPointerException e){}
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			pay.setCustomer(cus);
			trans.setCustomerPayment(pay);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			trans.setUserDtls(user);
			
			pays.add(trans);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pays;
	}
	
	public static CustomerPaymentTrans retrieve(String paytransid){
		CustomerPaymentTrans trans = new CustomerPaymentTrans();
		String payTable = "pay";
		String paytransTable = "trans";
		String sql = "SELECT * FROM customerpaymenttrans "+ paytransTable +",customerpayment "+ payTable +" WHERE " +
				paytransTable + ".cpayid=" + payTable + ".cpayid AND " +
				 paytransTable + ".paytransid=" + paytransid;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{trans.setPaytransid(rs.getLong("paytransid"));}catch(NullPointerException e){}
			try{trans.setPaymentdate(rs.getString("paymentdate"));}catch(NullPointerException e){}
			try{trans.setAmountpay(rs.getBigDecimal("amountpay"));}catch(NullPointerException e){}
			try{trans.setIspaid(rs.getInt("ispaid"));}catch(NullPointerException e){}
			try{trans.setPaytransisactive(rs.getInt("paytransisactive"));}catch(NullPointerException e){}
			try{trans.setPaymentType(rs.getInt("paymentType"));}catch(NullPointerException e){}
			try{trans.setRemarks(rs.getString("payRemarks"));}catch(NullPointerException e){}
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{trans.setReceiptNo(rs.getString("receiptno"));}catch(NullPointerException e){}
			try{
				if(PaymentTransactionType.STORE.getId()==trans.getPaymentType()) {
					trans.setEnableEdit(false);
				}else {
					trans.setEnableEdit(true);
				}
			}catch(NullPointerException e){}
			
			CustomerPayment pay = new CustomerPayment();
			try{pay.setCpayid(rs.getLong("cpayid"));}catch(NullPointerException e){}
			try{pay.setAmountpaid(rs.getBigDecimal("amountpaid"));}catch(NullPointerException e){}
			try{pay.setAmountpaiddate(rs.getString("amountpaiddate"));}catch(NullPointerException e){}
			try{pay.setAmountprevpaid(rs.getBigDecimal("amountprevpaid"));}catch(NullPointerException e){}
			try{pay.setAmountpaiddate(rs.getString("amountprevpaiddate"));}catch(NullPointerException e){}
			try{pay.setAmountbalance(rs.getBigDecimal("amountbalance"));}catch(NullPointerException e){}
			try{pay.setAmountprevbalance(rs.getBigDecimal("amountprevbalance"));}catch(NullPointerException e){}
			try{pay.setPayisactive(rs.getInt("payisactive"));}catch(NullPointerException e){}

			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			pay.setCustomer(cus);
			trans.setCustomerPayment(pay);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			trans.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static void save(CustomerPaymentTrans cus){
		if(cus!=null){
			
			long id = CustomerPaymentTrans.getInfo(cus.getPaytransid() ==0? CustomerPaymentTrans.getLatestId()+1 : cus.getPaytransid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				CustomerPaymentTrans.insertData(cus, "1");
			}else if(id==2){
				LogU.add("update Data ");
				CustomerPaymentTrans.updateData(cus);
			}else if(id==3){
				LogU.add("added new Data ");
				CustomerPaymentTrans.insertData(cus, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getInfo(getPaytransid() ==0? getLatestId()+1 : getPaytransid());
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
	
	public static CustomerPaymentTrans insertData(CustomerPaymentTrans cus, String type){
		String sql = "INSERT INTO customerpaymenttrans ("
				+ "paytransid,"
				+ "cpayid,"
				+ "paymentdate,"
				+ "amountpay,"
				+ "ispaid,"
				+ "paytransisactive,"
				+ "userdtlsid,"
				+ "paymentType,"
				+ "payRemarks,"
				+ "receiptno)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customerpaymenttrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			cus.setPaytransid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			cus.setPaytransid(id);
			LogU.add("id: " + id);
		}
		ps.setLong(2, cus.getCustomerPayment()==null? 0 : cus.getCustomerPayment().getCpayid());
		ps.setString(3, cus.getPaymentdate());
		ps.setBigDecimal(4, cus.getAmountpay());
		ps.setInt(5, cus.getIspaid());
		ps.setInt(6, cus.getPaytransisactive());
		ps.setLong(7, cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		ps.setInt(8, cus.getPaymentType());
		ps.setString(9, cus.getRemarks());
		ps.setString(10, cus.getReceiptNo());
		
		LogU.add(cus.getCustomerPayment()==null? 0 : cus.getCustomerPayment().getCpayid());
		LogU.add(cus.getPaymentdate());
		LogU.add(cus.getAmountpay());
		LogU.add(cus.getIspaid());
		LogU.add(cus.getPaytransisactive());
		LogU.add(cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		LogU.add(cus.getPaymentType());
		LogU.add(cus.getRemarks());
		LogU.add(cus.getReceiptNo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customerpaymenttrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO customerpaymenttrans ("
				+ "paytransid,"
				+ "cpayid,"
				+ "paymentdate,"
				+ "amountpay,"
				+ "ispaid,"
				+ "paytransisactive,"
				+ "userdtlsid,"
				+ "paymentType,"
				+ "payRemarks,"
				+ "receiptno)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customerpaymenttrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setPaytransid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setPaytransid(id);
			LogU.add("id: " + id);
		}
		ps.setLong(2, getCustomerPayment()==null? 0 : getCustomerPayment().getCpayid());
		ps.setString(3, getPaymentdate());
		ps.setBigDecimal(4, getAmountpay());
		ps.setInt(5, getIspaid());
		ps.setInt(6, getPaytransisactive());
		ps.setLong(7, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(8, getPaymentType());
		ps.setString(9, getRemarks());
		ps.setString(10, getReceiptNo());
		
		LogU.add(getCustomerPayment()==null? 0 : getCustomerPayment().getCpayid());
		LogU.add(getPaymentdate());
		LogU.add(getAmountpay());
		LogU.add(getIspaid());
		LogU.add(getPaytransisactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getPaymentType());
		LogU.add(getRemarks());
		LogU.add(getReceiptNo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customerpaymenttrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static CustomerPaymentTrans updateData(CustomerPaymentTrans cus){
		String sql = "UPDATE customerpaymenttrans SET "
				+ "amountpay=?,"
				+ "ispaid=?,"
				+ "userdtlsid=?,"
				+ "paymentType=?,"
				+ "payRemarks=? " 
				+ " WHERE paytransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customerpaymenttrans");
		
		ps.setBigDecimal(1, cus.getAmountpay());
		ps.setInt(2, cus.getIspaid());
		ps.setLong(3, cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		ps.setInt(4, cus.getPaymentType());
		ps.setString(5, cus.getRemarks());
		ps.setLong(6, cus.getPaytransid());
		
		LogU.add(cus.getAmountpay());
		LogU.add(cus.getIspaid());
		LogU.add(cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==null? 0 : cus.getUserDtls().getUserdtlsid()));
		LogU.add(cus.getPaymentType());
		LogU.add(cus.getRemarks());
		LogU.add(cus.getPaytransid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customerpaymenttrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void updateData(){
		String sql = "UPDATE customerpaymenttrans SET "
				+ "amountpay=?,"
				+ "ispaid=?,"
				+ "userdtlsid=?,"
				+ "paymentType=?,"
				+ "payRemarks=? " 
				+ " WHERE paytransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customerpaymenttrans");
		
		ps.setBigDecimal(1, getAmountpay());
		ps.setInt(2, getIspaid());
		ps.setLong(3, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(4, getPaymentType());
		ps.setString(5, getRemarks());
		ps.setLong(6, getPaytransid());
		
		LogU.add(getAmountpay());
		LogU.add(getIspaid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getPaymentType());
		LogU.add(getRemarks());
		LogU.add(getPaytransid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customerpaymenttrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT paytransid FROM customerpaymenttrans  ORDER BY paytransid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("paytransid");
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
		ps = conn.prepareStatement("SELECT paytransid FROM customerpaymenttrans WHERE paytransid=?");
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
		String sql = "UPDATE customerpaymenttrans set paytransisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE paytransid=?";
		
		String[] params = new String[1];
		params[0] = getPaytransid()+"";
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
	
	public long getPaytransid() {
		return paytransid;
	}

	public void setPaytransid(long paytransid) {
		this.paytransid = paytransid;
	}

	public CustomerPayment getCustomerPayment() {
		return customerPayment;
	}

	public void setCustomerPayment(CustomerPayment customerPayment) {
		this.customerPayment = customerPayment;
	}

	public String getPaymentdate() {
		return paymentdate;
	}

	public void setPaymentdate(String paymentdate) {
		this.paymentdate = paymentdate;
	}

	public BigDecimal getAmountpay() {
		return amountpay;
	}

	public void setAmountpay(BigDecimal amountpay) {
		this.amountpay = amountpay;
	}

	public int getIspaid() {
		return ispaid;
	}

	public void setIspaid(int ispaid) {
		this.ispaid = ispaid;
	}

	public int getPaytransisactive() {
		return paytransisactive;
	}

	public void setPaytransisactive(int paytransisactive) {
		this.paytransisactive = paytransisactive;
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

	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public boolean isEnableEdit() {
		return enableEdit;
	}

	public void setEnableEdit(boolean enableEdit) {
		this.enableEdit = enableEdit;
	}

	public static void main(String[] args) {
		
		CustomerPaymentTrans trans = new CustomerPaymentTrans();
		
		trans.setPaytransid(1);
		trans.setPaymentdate(DateUtils.getCurrentDateMMDDYYYY());
		trans.setAmountpay(new BigDecimal("600"));
		trans.setIspaid(1);
		trans.setPaytransisactive(1);
		
		CustomerPayment pay = new CustomerPayment();
		pay.setCpayid(1);
		trans.setCustomerPayment(pay);
		
		UserDtls user = new UserDtls();
		user.setUserdtlsid(1l);
		trans.setUserDtls(user);
		
		trans.save(trans);
		
		
	}
	
}











