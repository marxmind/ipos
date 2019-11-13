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
import com.italia.ipos.enm.HistoryReceiptStatus;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 11/06/2016
 * @version 1.0
 */
public class Transactions {

	private long transid;
	private String transdate;
	private BigDecimal amountpurchased;
	private BigDecimal amountreceived;
	private BigDecimal amountchange;
	private BigDecimal amountbal;
	private BigDecimal discount;
	private BigDecimal vatsales;
	private BigDecimal vatexmptsales;
	private BigDecimal zeroratedsales;
	private BigDecimal vatnet;
	private BigDecimal vatamnt;
	private int isvoidtrans;
	private Customer customer;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private String receipts;
	private int paymentType;
	
	private boolean between;
	private String dateFrom;
	private String dateTo;
	private String paymentTypeName;
	
	private boolean cancelledTrans;
	private String status;
	
	public Transactions(){}
	
	public Transactions(
			long transid,
			String transdate,
			BigDecimal amountpurchased,
			BigDecimal amountreceived,
			BigDecimal amountchange,
			BigDecimal amountbal,
			BigDecimal discount,
			BigDecimal vatsales,
			BigDecimal vatexmptsales,
			BigDecimal zeroratedsales,
			BigDecimal vatnet,
			BigDecimal vatamnt,
			int isvoidtrans,
			Customer customer,
			UserDtls userDtls,
			String receipts
			){
				this.transid = transid;
				this.transdate = transdate;
				this.amountpurchased = amountpurchased;
				this.amountpurchased = amountpurchased;
				this.amountchange = amountchange;
				this.amountbal = amountbal;
				this.discount = discount;
				this.vatsales = vatsales;
				this.vatexmptsales = vatexmptsales;
				this.zeroratedsales = zeroratedsales;
				this.vatnet = vatnet; 
				this.vatamnt = vatamnt;
				this.isvoidtrans = isvoidtrans;
				this.customer = customer;
				this.userDtls = userDtls;
				this.receipts = receipts;
	}
	
	/*public static String generateNewReceiptNo(){
		
		String recptNo = "";
		
		long id = getLatestId() + 1;
		String len = id + "";
		String _date = DateUtils.getCurrentDateYYYYMMDD();
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
		}
		
		return recptNo;
	}*/
	
	/**
	 * 
	 * @param tablename
	 * @param trans
	 * @return
	 * @deprecated this is no longer use as search
	 */
	public static String transSQL(String tablename,Transactions trans){
		String sql= " AND "+ tablename +".isvoidtrans=" + trans.getIsvoidtrans();
		if(trans!=null){
			if(trans.getTransid()!=0){
				sql += " AND "+ tablename +".transid=" + trans.getTransid();
			}
			
			if(trans.isBetween()){
				
					sql += " AND ( "+ tablename +".transdate>='" + trans.getDateFrom() +"' AND " + tablename +".transdate<='" + trans.getDateTo() +"' )";
				
			}else{
				if(trans.getTransdate()!=null){
					sql += " AND "+ tablename +".transdate='" + trans.getTransdate() +"'";
				}	
			}
			
			if(trans.getAmountpurchased()!=null){
				sql += " AND "+ tablename +".amountpurchased=" + trans.getAmountpurchased();
			}
			if(trans.getAmountreceived()!=null){
				sql += " AND "+ tablename +".amountreceived=" + trans.getAmountreceived();
			}
			if(trans.getAmountchange()!=null){
				sql += " AND "+ tablename +".amountchange=" + trans.getAmountchange();
			}
			if(trans.getAmountbal()!=null){
				sql += " AND "+ tablename +".amountbal=" + trans.getAmountbal();
			}
			if(trans.getDiscount()!=null){
				sql += " AND "+ tablename +".discount=" + trans.getDiscount();
			}
			if(trans.getVatsales()!=null){
				sql += " AND "+ tablename +".vatsales=" + trans.getVatsales();
			}
			if(trans.getVatexmptsales()!=null){
				sql += " AND "+ tablename +".vatexmptsales=" + trans.getVatexmptsales();
			}
			if(trans.getZeroratedsales()!=null){
				sql += " AND "+ tablename +".zeroratedsales=" + trans.getZeroratedsales();
			}
			if(trans.getVatnet()!=null){
				sql += " AND "+ tablename +".vatnet=" + trans.getVatnet();
			}
			if(trans.getVatamnt()!=null){
				sql += " AND "+ tablename +".vatamnt=" + trans.getVatamnt();
			}
			if(trans.getReceipts()!=null){
				sql += " AND "+ tablename +".transreceipt='" + trans.getReceipts()+"'";
			}
			if(trans.getCustomer()!=null){
				if(trans.getCustomer().getCustomerid()!=0){
					sql += " AND "+ tablename +".customerid=" + trans.getCustomer().getCustomerid();
				}
			}
			
			if(trans.getUserDtls()!=null){
				if(trans.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + trans.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		return sql;
	}
	
	/**
	 * 
	 * @param obj[Customer][UserDtls]
	 * @return list of transactions
	 * @deprecated Please use public static List<Transactions> retrieve(String sql, String[] params)
	 */
	public static List<Transactions> retrieve(Object ...obj){
		List<Transactions> transx = Collections.synchronizedList(new ArrayList<Transactions>());
		String transTable = "tran";
		String cusTable = "cus";
		String userTable = "usr";
		String sql = "SELECT * FROM transactions " + transTable + ",customer "+ cusTable  +
				", userdtls "+ userTable +" WHERE " + transTable + ".customerid = " + cusTable + ".customerid AND "+ 
				transTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof Transactions){
				sql += transSQL(transTable,(Transactions)obj[i]);
			}
			if(obj[i] instanceof Customer){
				sql += Customer.customerSQL(cusTable,(Customer)obj[i]);
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
			
			
			Transactions trans = new Transactions();
			try{trans.setTransid(rs.getLong("transid"));}catch(NullPointerException e){}
			try{trans.setTransdate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{trans.setAmountpurchased(rs.getBigDecimal("amountpurchased"));}catch(NullPointerException e){}
			try{trans.setAmountreceived(rs.getBigDecimal("amountreceived"));}catch(NullPointerException e){}
			try{trans.setAmountchange(rs.getBigDecimal("amountchange"));}catch(NullPointerException e){}
			try{trans.setAmountbal(rs.getBigDecimal("amountbal"));}catch(NullPointerException e){}
			try{trans.setDiscount(rs.getBigDecimal("discount"));}catch(NullPointerException e){}
			try{trans.setVatsales(rs.getBigDecimal("vatsales"));}catch(NullPointerException e){}
			try{trans.setVatexmptsales(rs.getBigDecimal("vatexmptsales"));}catch(NullPointerException e){}
			try{trans.setZeroratedsales(rs.getBigDecimal("zeroratedsales"));}catch(NullPointerException e){}
			try{trans.setVatnet(rs.getBigDecimal("vatnet"));}catch(NullPointerException e){}
			try{trans.setVatamnt(rs.getBigDecimal("vatamnt"));}catch(NullPointerException e){}
			try{trans.setIsvoidtrans(rs.getInt("isvoidtrans"));}catch(NullPointerException e){}
			try{trans.setReceipts(rs.getString("transreceipt"));}catch(NullPointerException e){}
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{trans.setPaymentType(rs.getInt("paytype"));}catch(NullPointerException e){}
			try{trans.setPaymentTypeName(HistoryReceiptStatus.typeName(rs.getInt("paytype")));}catch(NullPointerException e){}
			
			try{
			if(trans.getAmountpurchased().doubleValue()==0){
				trans.setCancelledTrans(true);
				trans.setStatus("CANCELLED");
				trans.setPaymentTypeName(HistoryReceiptStatus.VOID.getName());
			}
			}catch(Exception e){}
			
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
			trans.setCustomer(cus);
			
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
			trans.setUserDtls(user);
			
			transx.add(trans);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return transx;
	}
	
	/**
	 * 
	 * @param obj[Customer][UserDtls]
	 * @return list of transactions
	 */
	public static List<Transactions> retrieve(String sql, String[] params){
		List<Transactions> transx = Collections.synchronizedList(new ArrayList<Transactions>());
		String transTable = "tran";
		String cusTable = "cus";
		String userTable = "usr";
		String sqlAdd = "SELECT * FROM transactions " + transTable + ",customer "+ cusTable  +
				", userdtls "+ userTable +" WHERE " + transTable + ".customerid = " + cusTable + ".customerid AND "+ 
				transTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		sql = sqlAdd + sql;
		/*for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof Transactions){
				sql += transSQL(transTable,(Transactions)obj[i]);
			}
			if(obj[i] instanceof Customer){
				sql += Customer.customerSQL(cusTable,(Customer)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
		}*/
		
		/*Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);*/
        
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
		
		System.out.println("SQL "+ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			Transactions trans = new Transactions();
			try{trans.setTransid(rs.getLong("transid"));}catch(NullPointerException e){}
			try{trans.setTransdate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{trans.setAmountpurchased(rs.getBigDecimal("amountpurchased"));}catch(NullPointerException e){}
			try{trans.setAmountreceived(rs.getBigDecimal("amountreceived"));}catch(NullPointerException e){}
			try{trans.setAmountchange(rs.getBigDecimal("amountchange"));}catch(NullPointerException e){}
			try{trans.setAmountbal(rs.getBigDecimal("amountbal"));}catch(NullPointerException e){}
			try{trans.setDiscount(rs.getBigDecimal("discount"));}catch(NullPointerException e){}
			try{trans.setVatsales(rs.getBigDecimal("vatsales"));}catch(NullPointerException e){}
			try{trans.setVatexmptsales(rs.getBigDecimal("vatexmptsales"));}catch(NullPointerException e){}
			try{trans.setZeroratedsales(rs.getBigDecimal("zeroratedsales"));}catch(NullPointerException e){}
			try{trans.setVatnet(rs.getBigDecimal("vatnet"));}catch(NullPointerException e){}
			try{trans.setVatamnt(rs.getBigDecimal("vatamnt"));}catch(NullPointerException e){}
			try{trans.setIsvoidtrans(rs.getInt("isvoidtrans"));}catch(NullPointerException e){}
			try{trans.setReceipts(rs.getString("transreceipt"));}catch(NullPointerException e){}
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{trans.setPaymentType(rs.getInt("paytype"));}catch(NullPointerException e){}
			try{trans.setPaymentTypeName(HistoryReceiptStatus.typeName(rs.getInt("paytype")));}catch(NullPointerException e){}
				try{
				if(trans.getAmountpurchased().doubleValue()==0){
					trans.setCancelledTrans(true);
					trans.setStatus("CANCELLED");
					trans.setPaymentTypeName(HistoryReceiptStatus.VOID.getName());
				}
				}catch(Exception e){}
			
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
			trans.setCustomer(cus);
			
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
			trans.setUserDtls(user);
			
			transx.add(trans);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return transx;
	}
	
	/**
	 * 
	 * @param obj[Customer][UserDtls]
	 * @return list of transactions
	 */
	public static Transactions retrieve(String transid){
		Transactions trans = new Transactions();
		String transTable = "tran";
		String cusTable = "cus";
		String userTable = "usr";
		String sql = "SELECT * FROM transactions " + transTable + ",customer "+ cusTable  +
				", userdtls "+ userTable +" WHERE " + transTable + ".customerid = " + cusTable + ".customerid AND "+ 
				transTable +".userdtlsid = "+ userTable +".userdtlsid AND " + transTable + ".transid=" + transid;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{trans.setTransid(rs.getLong("transid"));}catch(NullPointerException e){}
			try{trans.setTransdate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{trans.setAmountpurchased(rs.getBigDecimal("amountpurchased"));}catch(NullPointerException e){}
			try{trans.setAmountreceived(rs.getBigDecimal("amountreceived"));}catch(NullPointerException e){}
			try{trans.setAmountchange(rs.getBigDecimal("amountchange"));}catch(NullPointerException e){}
			try{trans.setAmountbal(rs.getBigDecimal("amountbal"));}catch(NullPointerException e){}
			try{trans.setDiscount(rs.getBigDecimal("discount"));}catch(NullPointerException e){}
			try{trans.setVatsales(rs.getBigDecimal("vatsales"));}catch(NullPointerException e){}
			try{trans.setVatexmptsales(rs.getBigDecimal("vatexmptsales"));}catch(NullPointerException e){}
			try{trans.setZeroratedsales(rs.getBigDecimal("zeroratedsales"));}catch(NullPointerException e){}
			try{trans.setVatnet(rs.getBigDecimal("vatnet"));}catch(NullPointerException e){}
			try{trans.setVatamnt(rs.getBigDecimal("vatamnt"));}catch(NullPointerException e){}
			try{trans.setIsvoidtrans(rs.getInt("isvoidtrans"));}catch(NullPointerException e){}
			try{trans.setReceipts(rs.getString("transreceipt"));}catch(NullPointerException e){}
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{trans.setPaymentType(rs.getInt("paytype"));}catch(NullPointerException e){}
			try{trans.setPaymentTypeName(HistoryReceiptStatus.typeName(rs.getInt("paytype")));}catch(NullPointerException e){}
				try{
				if(trans.getAmountpurchased().doubleValue()==0){
					trans.setCancelledTrans(true);
					trans.setStatus("CANCELLED");
					trans.setPaymentTypeName(HistoryReceiptStatus.VOID.getName());
				}
				}catch(Exception e){}
			
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
			trans.setCustomer(cus);
			
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
			trans.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static Transactions save(Transactions trans){
		if(trans!=null){
			
			long id = Transactions.getInfo(trans.getTransid() ==0? Transactions.getLatestId()+1 : trans.getTransid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				trans = Transactions.insertData(trans, "1");
			}else if(id==2){
				LogU.add("update Data ");
				trans = Transactions.updateData(trans);
			}else if(id==3){
				LogU.add("added new Data ");
				trans = Transactions.insertData(trans, "3");
			}
			
		}
		return trans;
	}
	
	public void save(){
			
			long id = getInfo(getTransid() ==0? getLatestId()+1 : getTransid());
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
	
	public static Transactions insertData(Transactions trans, String type){
		String sql = "INSERT INTO transactions ("
				+ "transid,"
				+ "customerid,"
				+ "transdate,"
				+ "amountpurchased,"
				+ "amountreceived,"
				+ "amountchange,"
				+ "amountbal,"
				+ "discount,"
				+ "vatsales,"
				+ "vatexmptsales,"
				+ "zeroratedsales,"
				+ "vatnet,"
				+ "vatamnt,"
				+ "isvoidtrans,"
				+ "userdtlsid,"
				+ "transreceipt,"
				+ "paytype)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transactions");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			trans.setTransid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			trans.setTransid(id);
			LogU.add("id: " + id);
		}
		ps.setLong(2, trans.getCustomer()==null? 0 : trans.getCustomer().getCustomerid());
		ps.setString(3, trans.getTransdate());
		ps.setBigDecimal(4, trans.getAmountpurchased());
		ps.setBigDecimal(5, trans.getAmountreceived());
		ps.setBigDecimal(6, trans.getAmountchange());
		ps.setBigDecimal(7, trans.getAmountbal());
		ps.setBigDecimal(8, trans.getDiscount());
		ps.setBigDecimal(9, trans.getVatsales());
		ps.setBigDecimal(10, trans.getVatexmptsales());
		ps.setBigDecimal(11, trans.getZeroratedsales());
		ps.setBigDecimal(12, trans.getVatnet());
		ps.setBigDecimal(13, trans.getVatamnt());
		ps.setInt(14, trans.getIsvoidtrans());
		ps.setLong(15, trans.getUserDtls()==null? 0 : (trans.getUserDtls().getUserdtlsid()==null? 0 : trans.getUserDtls().getUserdtlsid()));
		ps.setString(16, trans.getReceipts());
		ps.setInt(17, trans.getPaymentType());
		
		LogU.add(trans.getCustomer()==null? 0 : trans.getCustomer().getCustomerid());
		LogU.add(trans.getTransdate());
		LogU.add(trans.getAmountpurchased());
		LogU.add(trans.getAmountreceived());
		LogU.add(trans.getAmountchange());
		LogU.add(trans.getAmountbal());
		LogU.add(trans.getDiscount());
		LogU.add(trans.getVatsales());
		LogU.add(trans.getVatexmptsales());
		LogU.add(trans.getZeroratedsales());
		LogU.add(trans.getVatnet());
		LogU.add(trans.getVatamnt());
		LogU.add(trans.getIsvoidtrans());
		LogU.add(trans.getUserDtls()==null? 0 : (trans.getUserDtls().getUserdtlsid()==null? 0 : trans.getUserDtls().getUserdtlsid()));
		LogU.add(trans.getReceipts());
		LogU.add(trans.getPaymentType());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transactions : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return trans;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO transactions ("
				+ "transid,"
				+ "customerid,"
				+ "transdate,"
				+ "amountpurchased,"
				+ "amountreceived,"
				+ "amountchange,"
				+ "amountbal,"
				+ "discount,"
				+ "vatsales,"
				+ "vatexmptsales,"
				+ "zeroratedsales,"
				+ "vatnet,"
				+ "vatamnt,"
				+ "isvoidtrans,"
				+ "userdtlsid,"
				+ "transreceipt,"
				+ "paytype)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transactions");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setTransid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setTransid(id);
			LogU.add("id: " + id);
		}
		ps.setLong(2, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setString(3, getTransdate());
		ps.setBigDecimal(4, getAmountpurchased());
		ps.setBigDecimal(5, getAmountreceived());
		ps.setBigDecimal(6, getAmountchange());
		ps.setBigDecimal(7, getAmountbal());
		ps.setBigDecimal(8, getDiscount());
		ps.setBigDecimal(9, getVatsales());
		ps.setBigDecimal(10, getVatexmptsales());
		ps.setBigDecimal(11, getZeroratedsales());
		ps.setBigDecimal(12, getVatnet());
		ps.setBigDecimal(13, getVatamnt());
		ps.setInt(14, getIsvoidtrans());
		ps.setLong(15, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(16, getReceipts());
		ps.setInt(17, getPaymentType());
		
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getTransdate());
		LogU.add(getAmountpurchased());
		LogU.add(getAmountreceived());
		LogU.add(getAmountchange());
		LogU.add(getAmountbal());
		LogU.add(getDiscount());
		LogU.add(getVatsales());
		LogU.add(getVatexmptsales());
		LogU.add(getZeroratedsales());
		LogU.add(getVatnet());
		LogU.add(getVatamnt());
		LogU.add(getIsvoidtrans());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getReceipts());
		LogU.add(getPaymentType());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transactions : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static Transactions updateData(Transactions trans){
		String sql = "UPDATE transactions SET "
				+ "customerid=?,"
				+ "transdate=?,"
				+ "amountpurchased=?,"
				+ "amountreceived=?,"
				+ "amountchange=?,"
				+ "amountbal=?,"
				+ "discount=?,"
				+ "vatsales=?,"
				+ "vatexmptsales=?,"
				+ "zeroratedsales=?,"
				+ "vatnet=?,"
				+ "vatamnt=?,"
				+ "userdtlsid=?,"
				+ "paytype=? " 
				+ " WHERE transid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table transactions");
		
		ps.setLong(1, trans.getCustomer()==null? 0 : trans.getCustomer().getCustomerid());
		ps.setString(2, trans.getTransdate());
		ps.setBigDecimal(3, trans.getAmountpurchased());
		ps.setBigDecimal(4, trans.getAmountreceived());
		ps.setBigDecimal(5, trans.getAmountchange());
		ps.setBigDecimal(6, trans.getAmountbal());
		ps.setBigDecimal(7, trans.getDiscount());
		ps.setBigDecimal(8, trans.getVatsales());
		ps.setBigDecimal(9, trans.getVatexmptsales());
		ps.setBigDecimal(10, trans.getZeroratedsales());
		ps.setBigDecimal(11, trans.getVatnet());
		ps.setBigDecimal(12, trans.getVatamnt());
		ps.setLong(13, trans.getUserDtls()==null? 0 : (trans.getUserDtls().getUserdtlsid()==null? 0 : trans.getUserDtls().getUserdtlsid()));
		ps.setInt(14, trans.getPaymentType());
		ps.setLong(15, trans.getTransid());
		
		LogU.add(trans.getCustomer()==null? 0 : trans.getCustomer().getCustomerid());
		LogU.add(trans.getTransdate());
		LogU.add(trans.getAmountpurchased());
		LogU.add(trans.getAmountreceived());
		LogU.add(trans.getAmountchange());
		LogU.add(trans.getAmountbal());
		LogU.add(trans.getDiscount());
		LogU.add(trans.getVatsales());
		LogU.add(trans.getVatexmptsales());
		LogU.add(trans.getZeroratedsales());
		LogU.add(trans.getVatnet());
		LogU.add(trans.getVatamnt());
		LogU.add(trans.getUserDtls()==null? 0 : (trans.getUserDtls().getUserdtlsid()==null? 0 : trans.getUserDtls().getUserdtlsid()));
		LogU.add(trans.getPaymentType());
		LogU.add(trans.getTransid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to transactions : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return trans;
	}
	
	public void updateData(){
		String sql = "UPDATE transactions SET "
				+ "customerid=?,"
				+ "transdate=?,"
				+ "amountpurchased=?,"
				+ "amountreceived=?,"
				+ "amountchange=?,"
				+ "amountbal=?,"
				+ "discount=?,"
				+ "vatsales=?,"
				+ "vatexmptsales=?,"
				+ "zeroratedsales=?,"
				+ "vatnet=?,"
				+ "vatamnt=?,"
				+ "userdtlsid=?,"
				+ "paytype=? " 
				+ " WHERE transid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table transactions");
		
		ps.setLong(1, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setString(2, getTransdate());
		ps.setBigDecimal(3, getAmountpurchased());
		ps.setBigDecimal(4, getAmountreceived());
		ps.setBigDecimal(5, getAmountchange());
		ps.setBigDecimal(6, getAmountbal());
		ps.setBigDecimal(7, getDiscount());
		ps.setBigDecimal(8, getVatsales());
		ps.setBigDecimal(9, getVatexmptsales());
		ps.setBigDecimal(10, getZeroratedsales());
		ps.setBigDecimal(11, getVatnet());
		ps.setBigDecimal(12, getVatamnt());
		ps.setLong(13, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(14, getPaymentType());
		ps.setLong(15, getTransid());
		
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getTransdate());
		LogU.add(getAmountpurchased());
		LogU.add(getAmountreceived());
		LogU.add(getAmountchange());
		LogU.add(getAmountbal());
		LogU.add(getDiscount());
		LogU.add(getVatsales());
		LogU.add(getVatexmptsales());
		LogU.add(getZeroratedsales());
		LogU.add(getVatnet());
		LogU.add(getVatamnt());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getPaymentType());
		LogU.add(getTransid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to transactions : " + s.getMessage());
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
		sql="SELECT transid FROM transactions  ORDER BY transid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("transid");
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
		ps = conn.prepareStatement("SELECT transid FROM transactions WHERE transid=?");
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
		String sql = "UPDATE transactions set isvoidtrans=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE transid=?";
		
		String[] params = new String[1];
		params[0] = getTransid()+"";
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
	
	public long getTransid() {
		return transid;
	}
	public void setTransid(long transid) {
		this.transid = transid;
	}
	public String getTransdate() {
		return transdate;
	}
	public void setTransdate(String transdate) {
		this.transdate = transdate;
	}
	public BigDecimal getAmountpurchased() {
		return amountpurchased;
	}
	public void setAmountpurchased(BigDecimal amountpurchased) {
		this.amountpurchased = amountpurchased;
	}
	public BigDecimal getAmountreceived() {
		return amountreceived;
	}
	public void setAmountreceived(BigDecimal amountreceived) {
		this.amountreceived = amountreceived;
	}
	public BigDecimal getAmountchange() {
		return amountchange;
	}
	public void setAmountchange(BigDecimal amountchange) {
		this.amountchange = amountchange;
	}
	public BigDecimal getAmountbal() {
		return amountbal;
	}
	public void setAmountbal(BigDecimal amountbal) {
		this.amountbal = amountbal;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public BigDecimal getVatsales() {
		return vatsales;
	}
	public void setVatsales(BigDecimal vatsales) {
		this.vatsales = vatsales;
	}
	public BigDecimal getVatexmptsales() {
		return vatexmptsales;
	}
	public void setVatexmptsales(BigDecimal vatexmptsales) {
		this.vatexmptsales = vatexmptsales;
	}
	public BigDecimal getZeroratedsales() {
		return zeroratedsales;
	}
	public void setZeroratedsales(BigDecimal zeroratedsales) {
		this.zeroratedsales = zeroratedsales;
	}
	public BigDecimal getVatnet() {
		return vatnet;
	}
	public void setVatnet(BigDecimal vatnet) {
		this.vatnet = vatnet;
	}
	public BigDecimal getVatamnt() {
		return vatamnt;
	}
	public void setVatamnt(BigDecimal vatamnt) {
		this.vatamnt = vatamnt;
	}
	public int getIsvoidtrans() {
		return isvoidtrans;
	}
	public void setIsvoidtrans(int isvoidtrans) {
		this.isvoidtrans = isvoidtrans;
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
	
	public String getReceipts() {
		return receipts;
	}

	public void setReceipts(String receipts) {
		this.receipts = receipts;
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

	public boolean getCancelledTrans() {
		return cancelledTrans;
	}

	public void setCancelledTrans(boolean cancelledTrans) {
		this.cancelledTrans = cancelledTrans;
	}

	public String getStatus() {
		if(status==null){
			status = "VALID";
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public static void main(String[] args) {
		
		
		Transactions trans = new Transactions();
		try{trans.setTransid(1);}catch(NullPointerException e){}
		try{trans.setTransdate(DateUtils.getCurrentDateYYYYMMDD());}catch(NullPointerException e){}
		try{trans.setAmountpurchased(new BigDecimal("200"));}catch(NullPointerException e){}
		try{trans.setAmountreceived(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setAmountchange(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setAmountbal(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setDiscount(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setVatsales(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setVatexmptsales(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setZeroratedsales(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setVatnet(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setVatamnt(new BigDecimal("100"));}catch(NullPointerException e){}
		try{trans.setIsvoidtrans(1);}catch(NullPointerException e){}
		
		Customer customer = new Customer();
		customer.setCustomerid(1);
		trans.setCustomer(customer);
		
		UserDtls userDtls = new UserDtls();
		userDtls.setUserdtlsid(1l);
		trans.setUserDtls(userDtls);
		
		//trans.save();
		
		trans = new Transactions();
		trans.setIsvoidtrans(1);
		
		for(Transactions t : Transactions.retrieve(trans)){
			System.out.println(t.getTransdate());
		}
	}
	
}
