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
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 12/30/2016
 * @version 1.0
 *
 */
public class DeliveryItemReceipt {
	
	private long id;
	private String dateTrans;
	private String receiptNo;
	private double totalAmount;
	private double deliveryChargeAmount;
	private double discountAmount;
	private double balanceAmount;
	private double downPayment;
	private double quantity; 
	private String remarks;
	private int paymentStatus;
	private int status; 
	private int isActive;
	private Customer customer;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private boolean isBetween;
	private String dateFrom;
	private String dateTo;
	
	public DeliveryItemReceipt(){}
	
	public DeliveryItemReceipt(
			long id,
			String dateTrans,
			String receiptNo,
			double totalAmount,
			double deliveryChargeAmount,
			double discountAmount,
			double balanceAmount,
			double downPayment,
			double quantity, 
			String remarks,
			int paymentStatus,
			int status,
			int isActive,
			Customer customer,
			UserDtls userDtls
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.receiptNo = receiptNo;
		this.totalAmount = totalAmount;
		this.deliveryChargeAmount = deliveryChargeAmount;
		this.discountAmount = discountAmount;
		this.balanceAmount = balanceAmount;
		this.downPayment = downPayment;
		this.quantity = quantity;
		this.remarks = remarks;
		this.paymentStatus = paymentStatus;
		this.status = status;
		this.isActive = isActive;
		this.customer = customer;
		this.userDtls = userDtls;
	}
	
	public static String generateNewReceiptNo(){
		
		String recptNo = "";
		//recptNo = Receipt.generateLatestOR()+"";
		//long id = getLatestId() + 1;
		long id = Receipt.generateLatestOR();
		String len = id + "";
		/*String _date = DateUtils.getCurrentDateYYYYMMDD();
		switch(len.length()){
		case 1 : recptNo= _date + "-000000000" + id; break;
		case 2 : recptNo= _date + "-00000000" + id; break;
		case 3 : recptNo= _date + "-0000000" + id; break;
		case 4 : recptNo= _date + "-000000" + id; break;
		case 5 : recptNo= _date + "-00000" + id; break;
		case 6 : recptNo= _date + "-0000" + id; break;
		case 7 : recptNo= _date + "-000" + id; break;
		case 8 : recptNo= _date + "-00" + id; break;
		case 9 : recptNo= _date + "-0" + id; break;
		case 10 : recptNo= _date + "-" + id; break;
		}*/
		
		switch(len.length()){
			case 1 : recptNo= "00000000000000" + id; break;
			case 2 : recptNo= "0000000000000" + id; break;
			case 3 : recptNo= "000000000000" + id; break;
			case 4 : recptNo= "00000000000" + id; break;
			case 5 : recptNo= "0000000000" + id; break;
			case 6 : recptNo= "000000000" + id; break;
			case 7 : recptNo= "00000000" + id; break;
			case 8 : recptNo= "0000000" + id; break;
			case 9 : recptNo= "000000" + id; break;
			case 10 : recptNo= "00000" + id; break;
			case 11 : recptNo= "0000" + id; break;
			case 12 : recptNo= "000" + id; break;
			case 13 : recptNo= "00" + id; break;
			case 14 : recptNo= "0" + id; break;
			case 15 : recptNo= "" + id; break;
		}
		
		return recptNo;
	}
	
	public static String receiptSQL(String tablename,DeliveryItemReceipt item){
		String sql= " AND "+ tablename +".isactiveDelTranRec=" + item.getIsActive();
		if(item!=null){
			if(item.getId()!=0){
				sql += " AND "+ tablename +".deltranrecid=" + item.getId();
			}
			
			if(item.isBetween()){
					sql += " AND ( "+ tablename +".deltranrecdate >='" + item.getDateFrom() + "' AND "+ tablename +".deltranrecdate <='" + item.getDateTo() + "' )";
			}else{
			
				if(item.getDateTrans()!=null){
					sql += " AND "+ tablename +".deltranrecdate ='" + item.getDateTrans() + "'";
				}
			
			}
			if(item.getPaymentStatus()!=0){
				sql += " AND "+ tablename +".deltranPaymentReceiptStatus =" + item.getPaymentStatus();
			}
			
			if(item.getStatus()!=0){
				sql += " AND "+ tablename +".deltranrecptStatus =" + item.getStatus();
			}
			if(item.getReceiptNo()!=null){
				sql += " AND "+ tablename +".deltranreceiptno ='" + item.getReceiptNo() + "'";
			}
			
			
		}
		return sql;
	}
	
	public static List<DeliveryItemReceipt> retrieve(Object... obj){
		List<DeliveryItemReceipt> items = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
		
		String customerTable = "cus";
		String receiptTable = "rec";
		String userTable = "usr";
		String sql = "SELECT * FROM   deliveryitemreceipt " + receiptTable + ", customer " + customerTable  +", userdtls "+ userTable +
				" WHERE  "  + receiptTable + ".customerid=" + customerTable + ".customerid AND " + receiptTable +".userdtlsid = "+ userTable +".userdtlsid";
		
		for(int i=0;i<obj.length;i++){
			
			if(obj[i] instanceof DeliveryItemReceipt){
				sql += receiptSQL(receiptTable,(DeliveryItemReceipt)obj[i]);
			}
			
			if(obj[i] instanceof Customer){
				sql += Customer.customerSQL(customerTable,(Customer)obj[i]);
			}
			
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
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
			
			DeliveryItemReceipt rec = new DeliveryItemReceipt();
			try{rec.setId(rs.getLong("deltranrecid"));}catch(NullPointerException e){}
			try{rec.setDateTrans(rs.getString("deltranrecdate"));}catch(NullPointerException e){}
			try{rec.setReceiptNo(rs.getString("deltranreceiptno"));}catch(NullPointerException e){}
			try{rec.setTotalAmount(rs.getDouble("deltranTotalAmount"));}catch(NullPointerException e){}
			try{rec.setDeliveryChargeAmount(rs.getDouble("deltranChargeAmount"));}catch(NullPointerException e){}
			try{rec.setDiscountAmount(rs.getDouble("deltranDiscountAmount"));}catch(NullPointerException e){}
			try{rec.setBalanceAmount(rs.getDouble("deltranbalance"));}catch(NullPointerException e){}
			try{rec.setDownPayment(rs.getDouble("deltrabdownpayment"));}catch(NullPointerException e){}
			try{rec.setQuantity(rs.getDouble("deliveredquantity"));}catch(NullPointerException e){}
			try{rec.setRemarks(rs.getString("deltranrecremarks"));}catch(NullPointerException e){}
			try{rec.setPaymentStatus(rs.getInt("deltranPaymentReceiptStatus"));}catch(NullPointerException e){}
			try{rec.setStatus(rs.getInt("deltranrecptStatus"));}catch(NullPointerException e){}
			try{rec.setIsActive(rs.getInt("isactiveDelTranRec"));}catch(NullPointerException e){}
			try{rec.setTimestamp(rs.getTimestamp("deltranrectimestamp"));}catch(NullPointerException e){}
			
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
			rec.setCustomer(cus);
			
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
			rec.setUserDtls(user);
			
			items.add(rec);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
	}
	
	public static DeliveryItemReceipt retrieve(String receiptId){
		DeliveryItemReceipt rec = new DeliveryItemReceipt();
		
		String customerTable = "cus";
		String receiptTable = "rec";
		String userTable = "usr";
		String sql = "SELECT * FROM   deliveryitemreceipt " + receiptTable + ", customer " + customerTable  +", userdtls "+ userTable +
				" WHERE  "  + receiptTable + ".customerid=" + customerTable + ".customerid AND " + receiptTable +".userdtlsid = "+ userTable +".userdtlsid AND " +
				receiptTable + ".deltranrecid="+receiptId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		System.out.println("SQL "+ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{rec.setId(rs.getLong("deltranrecid"));}catch(NullPointerException e){}
			try{rec.setDateTrans(rs.getString("deltranrecdate"));}catch(NullPointerException e){}
			try{rec.setReceiptNo(rs.getString("deltranreceiptno"));}catch(NullPointerException e){}
			try{rec.setTotalAmount(rs.getDouble("deltranTotalAmount"));}catch(NullPointerException e){}
			try{rec.setDeliveryChargeAmount(rs.getDouble("deltranChargeAmount"));}catch(NullPointerException e){}
			try{rec.setDiscountAmount(rs.getDouble("deltranDiscountAmount"));}catch(NullPointerException e){}
			try{rec.setBalanceAmount(rs.getDouble("deltranbalance"));}catch(NullPointerException e){}
			try{rec.setDownPayment(rs.getDouble("deltrabdownpayment"));}catch(NullPointerException e){}
			try{rec.setQuantity(rs.getDouble("deliveredquantity"));}catch(NullPointerException e){}
			try{rec.setRemarks(rs.getString("deltranrecremarks"));}catch(NullPointerException e){}
			try{rec.setPaymentStatus(rs.getInt("deltranPaymentReceiptStatus"));}catch(NullPointerException e){}
			try{rec.setStatus(rs.getInt("deltranrecptStatus"));}catch(NullPointerException e){}
			try{rec.setIsActive(rs.getInt("isactiveDelTranRec"));}catch(NullPointerException e){}
			try{rec.setTimestamp(rs.getTimestamp("deltranrectimestamp"));}catch(NullPointerException e){}
			
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
			rec.setCustomer(cus);
			
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
			rec.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rec;
	}
	
	public static void save(DeliveryItemReceipt item){
		if(item!=null){
			
			long id = DeliveryItemReceipt.getInfo(item.getId() ==0? DeliveryItemReceipt.getLatestId()+1 : item.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				DeliveryItemReceipt.insertData(item, "1");
			}else if(id==2){
				LogU.add("update Data ");
				DeliveryItemReceipt.updateData(item);
			}else if(id==3){
				LogU.add("added new Data ");
				DeliveryItemReceipt.insertData(item, "3");
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
	
	public static DeliveryItemReceipt insertData(DeliveryItemReceipt item, String type){
		String sql = "INSERT INTO deliveryitemreceipt ("
				+ "deltranrecid,"
				+ "deltranrecdate,"
				+ "deltranreceiptno,"
				+ "deltranTotalAmount,"
				+ "deltranChargeAmount,"
				+ "deltranDiscountAmount,"
				+ "deltranbalance,"
				+ "deltrabdownpayment,"
				+ "deliveredquantity,"
				+ "deltranrecremarks,"
				+ "deltranPaymentReceiptStatus,"
				+ "deltranrecptStatus,"
				+ "isactiveDelTranRec,"
				+ "customerid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table deliveryitemreceipt");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			item.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			item.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, item.getDateTrans());
		ps.setString(3, item.getReceiptNo());
		ps.setDouble(4, item.getTotalAmount());
		ps.setDouble(5, item.getDeliveryChargeAmount());
		ps.setDouble(6, item.getDiscountAmount());
		ps.setDouble(7, item.getBalanceAmount());
		ps.setDouble(8, item.getDownPayment());
		ps.setDouble(9, item.getQuantity());
		ps.setString(10, item.getRemarks());
		ps.setInt(11, item.getPaymentStatus());
		ps.setInt(12, item.getStatus());
		ps.setInt(13, item.getIsActive());
		ps.setLong(14, item.getCustomer()==null? 0 : item.getCustomer().getCustomerid());
		ps.setLong(15, item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));		
				
		LogU.add(item.getDateTrans());
		LogU.add(item.getReceiptNo());
		LogU.add(item.getTotalAmount());
		LogU.add(item.getDeliveryChargeAmount());
		LogU.add(item.getDiscountAmount());
		LogU.add(item.getBalanceAmount());
		LogU.add(item.getDownPayment());
		LogU.add(item.getQuantity());
		LogU.add(item.getRemarks());
		LogU.add(item.getPaymentStatus());
		LogU.add(item.getStatus());
		LogU.add(item.getIsActive());
		LogU.add(item.getCustomer()==null? 0 : item.getCustomer().getCustomerid());
		LogU.add(item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));	
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to deliveryitemreceipt : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return item;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO deliveryitemreceipt ("
				+ "deltranrecid,"
				+ "deltranrecdate,"
				+ "deltranreceiptno,"
				+ "deltranTotalAmount,"
				+ "deltranChargeAmount,"
				+ "deltranDiscountAmount,"
				+ "deltranbalance,"
				+ "deltrabdownpayment,"
				+ "deliveredquantity,"
				+ "deltranrecremarks,"
				+ "deltranPaymentReceiptStatus,"
				+ "deltranrecptStatus,"
				+ "isactiveDelTranRec,"
				+ "customerid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table deliveryitemreceipt");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, getDateTrans());
		ps.setString(3, getReceiptNo());
		ps.setDouble(4, getTotalAmount());
		ps.setDouble(5, getDeliveryChargeAmount());
		ps.setDouble(6, getDiscountAmount());
		ps.setDouble(7, getBalanceAmount());
		ps.setDouble(8, getDownPayment());
		ps.setDouble(9, getQuantity());
		ps.setString(10, getRemarks());
		ps.setInt(11, getPaymentStatus());
		ps.setInt(12, getStatus());
		ps.setInt(13, getIsActive());
		ps.setLong(14, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(15, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));		
				
		LogU.add(getDateTrans());
		LogU.add(getReceiptNo());
		LogU.add(getTotalAmount());
		LogU.add(getDeliveryChargeAmount());
		LogU.add(getDiscountAmount());
		LogU.add(getBalanceAmount());
		LogU.add(getDownPayment());
		LogU.add(getQuantity());
		LogU.add(getRemarks());
		LogU.add(getPaymentStatus());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));	
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to deliveryitemreceipt : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static DeliveryItemReceipt updateData(DeliveryItemReceipt item){
		String sql = "UPDATE deliveryitemreceipt SET "
				+ "deltranrecdate=?,"
				+ "deltranreceiptno=?,"
				+ "deltranTotalAmount=?,"
				+ "deltranChargeAmount=?,"
				+ "deltranDiscountAmount=?,"
				+ "deltranbalance=?,"
				+ "deltrabdownpayment=?,"
				+ "deliveredquantity=?,"
				+ "deltranrecremarks=?,"
				+ "deltranPaymentReceiptStatus=?,"
				+ "deltranrecptStatus=?,"
				+ "customerid=?,"
				+ "userdtlsid=?" 
				+ " WHERE deltranrecid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table deliveryitemreceipt");
		
		ps.setString(1, item.getDateTrans());
		ps.setString(2, item.getReceiptNo());
		ps.setDouble(3, item.getTotalAmount());
		ps.setDouble(4, item.getDeliveryChargeAmount());
		ps.setDouble(5, item.getDiscountAmount());
		ps.setDouble(6, item.getBalanceAmount());
		ps.setDouble(7, item.getDownPayment());
		ps.setDouble(8, item.getQuantity());
		ps.setString(9, item.getRemarks());
		ps.setInt(10, item.getPaymentStatus());
		ps.setInt(11, item.getStatus());
		ps.setLong(12, item.getCustomer()==null? 0 : item.getCustomer().getCustomerid());
		ps.setLong(13, item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));		
		ps.setLong(14, item.getId());
		
		LogU.add(item.getDateTrans());
		LogU.add(item.getReceiptNo());
		LogU.add(item.getTotalAmount());
		LogU.add(item.getDeliveryChargeAmount());
		LogU.add(item.getDiscountAmount());
		LogU.add(item.getBalanceAmount());
		LogU.add(item.getDownPayment());
		LogU.add(item.getQuantity());
		LogU.add(item.getRemarks());
		LogU.add(item.getPaymentStatus());
		LogU.add(item.getStatus());
		LogU.add(item.getCustomer()==null? 0 : item.getCustomer().getCustomerid());
		LogU.add(item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));	
		LogU.add(item.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to deliveryitemreceipt : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return item;
	}
	
	public void updateData(){
		String sql = "UPDATE deliveryitemreceipt SET "
				+ "deltranrecdate=?,"
				+ "deltranreceiptno=?,"
				+ "deltranTotalAmount=?,"
				+ "deltranChargeAmount=?,"
				+ "deltranDiscountAmount=?,"
				+ "deltranbalance=?,"
				+ "deltrabdownpayment=?,"
				+ "deliveredquantity=?,"
				+ "deltranrecremarks=?,"
				+ "deltranPaymentReceiptStatus=?,"
				+ "deltranrecptStatus=?,"
				+ "customerid=?,"
				+ "userdtlsid=?" 
				+ " WHERE deltranrecid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table deliveryitemreceipt");
		
		ps.setString(1, getDateTrans());
		ps.setString(2, getReceiptNo());
		ps.setDouble(3, getTotalAmount());
		ps.setDouble(4, getDeliveryChargeAmount());
		ps.setDouble(5, getDiscountAmount());
		ps.setDouble(6, getBalanceAmount());
		ps.setDouble(7, getDownPayment());
		ps.setDouble(8, getQuantity());
		ps.setString(9, getRemarks());
		ps.setInt(10, getPaymentStatus());
		ps.setInt(11, getStatus());
		ps.setLong(12, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(13, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));		
		ps.setLong(14, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getReceiptNo());
		LogU.add(getTotalAmount());
		LogU.add(getDeliveryChargeAmount());
		LogU.add(getDiscountAmount());
		LogU.add(getBalanceAmount());
		LogU.add(getDownPayment());
		LogU.add(getQuantity());
		LogU.add(getRemarks());
		LogU.add(getPaymentStatus());
		LogU.add(getStatus());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));	
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to deliveryitemreceipt : " + s.getMessage());
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
		sql="SELECT deltranrecid FROM deliveryitemreceipt  ORDER BY deltranrecid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("deltranrecid");
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
		ps = conn.prepareStatement("SELECT deltranrecid FROM deliveryitemreceipt WHERE deltranrecid=?");
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
		String sql = "UPDATE deliveryitemreceipt set isactiveDelTranRec=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE deltranrecid=?";
		
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
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getBalanceAmount() {
		return balanceAmount;
	}
	public void setBalanceAmount(double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
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
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isBetween() {
		return isBetween;
	}

	public void setBetween(boolean isBetween) {
		this.isBetween = isBetween;
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

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getDeliveryChargeAmount() {
		return deliveryChargeAmount;
	}

	public void setDeliveryChargeAmount(double deliveryChargeAmount) {
		this.deliveryChargeAmount = deliveryChargeAmount;
	}
	
	public double getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(double downPayment) {
		this.downPayment = downPayment;
	}

	public static void main(String[] args) {
		DeliveryItemReceipt r = new DeliveryItemReceipt();
		r.setRemarks("testing");
		r.save();
	}
	
}
