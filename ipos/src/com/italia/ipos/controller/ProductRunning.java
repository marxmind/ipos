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
 * @since 10/08/2016
 * @version 1.0
 */
public class ProductRunning {

	private long runid;
	private String rundate;
	private String clientip;
	private String clientbrowser;
	private int runstatus;
	private int isrunactive;
	private String runremarks;
	private Customer customer;
	private Transactions transactions;
	private Product product;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private List<QtyRunning> productHolds;
	
	public ProductRunning(){}
	
	public ProductRunning(
			long runid,
			String rundate,
			String clientip,
			String clientbrowser,
			int runstatus,
			int isrunactive,
			String runremarks,
			Transactions transactions,
			Product product,
			UserDtls userDtls,
			Customer customer
			){
		this.runid = runid;
		this.rundate = rundate;
		this.clientip = clientip;
		this.clientbrowser = clientbrowser;
		this.runstatus = runstatus;
		this.isrunactive = isrunactive;
		this.runremarks = runremarks;
		this.transactions = transactions;
		this.product = product;
		this.userDtls = userDtls;
		this.customer = customer;
	}
	
	public static String productSQL(String tablename,ProductRunning prod){
		String sql= " AND "+ tablename +".isrunactive=" + prod.getIsrunactive();
		if(prod!=null){
			if(prod.getRunid()!=0){
				sql += " AND "+ tablename +".runid=" + prod.getRunid();
			}
			if(prod.getRundate()!=null){
				sql += " AND "+ tablename +".rundate like '%" + prod.getRundate() +"%'";
			}
			if(prod.getClientip()!=null){
				sql += " AND "+ tablename +".clientip like '%" + prod.getClientip() +"%'";
			}
			if(prod.getClientbrowser()!=null){
				sql += " AND "+ tablename +".clientbrowser like '%" + prod.getClientbrowser() +"%'";
			}
			if(prod.getRunremarks()!=null){
				sql += " AND "+ tablename +".runremarks like '%" + prod.getRunremarks() +"%'";
			}
			if(prod.getRunstatus()!=0){
				sql += " AND "+ tablename +".runstatus=" + prod.getRunstatus();
			}
			
			if(prod.getTransactions()!=null){
				if(prod.getTransactions().getTransid()!=0){
					sql += " AND "+ tablename +".transid=" + prod.getTransactions().getTransid();
				}
			}
			if(prod.getProduct()!=null){
				if(prod.getProduct().getProdid()!=0){
					sql += " AND "+ tablename +".prodid=" + prod.getProduct().getProdid();
				}
			}
			
			if(prod.getUserDtls()!=null){
				if(prod.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + prod.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		return sql;
	}
	
	/**
	 * 
	 * @param obj[Transactions][UserDtls]
	 * @return list of productrunning
	 */
	public static List<ProductRunning> retrieve(Object ...obj){
		List<ProductRunning> runs = Collections.synchronizedList(new ArrayList<ProductRunning>());
		String runTable = "rund";
		//String transTable = "tran";
		//String userTable = "usr";
		String sql = "SELECT * FROM  productrunning "+ runTable   +" WHERE " + runTable + ".runid!=0";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof ProductRunning){
				sql += productSQL(runTable,(ProductRunning)obj[i]);
			}
			/*if(obj[i] instanceof Transactions){
				sql += Transactions.transSQL(transTable,(Transactions)obj[i]);
			}*/
			/*if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}*/
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
			
			ProductRunning prun = new ProductRunning();
			try{prun.setRunid(rs.getLong("runid"));}catch(NullPointerException e){}
			try{prun.setRundate(rs.getString("rundate"));}catch(NullPointerException e){}
			try{prun.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{prun.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{prun.setRunstatus(rs.getInt("runstatus"));}catch(NullPointerException e){}
			try{prun.setIsrunactive(rs.getInt("isrunactive"));}catch(NullPointerException e){}
			try{prun.setRunremarks(rs.getString("runremarks"));}catch(NullPointerException e){}
			try{prun.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			Transactions trans = new Transactions();
			trans.setTransid(rs.getLong("transid"));
			prun.setTransactions(trans);
			/*Transactions trans = new Transactions();
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
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prun.setTransactions(trans);*/
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			/*try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}*/
			prun.setUserDtls(user);
			
			Customer customer = new Customer();
			try{customer.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			prun.setCustomer(customer);
			
			
			
			runs.add(prun);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return runs;
	}
	
	/**
	 * this method only use for recall
	 * @param obj
	 * @return
	 */
	public static List<ProductRunning> retrieveRecall(Object ...obj){
		List<ProductRunning> runs = Collections.synchronizedList(new ArrayList<ProductRunning>());
		String runTable = "rund";
		String transTable = "tran";
		String cusTable = "cus";
		String userTable = "usr";
		String sql = "SELECT * FROM  productrunning "+ runTable   +  ", transactions " + transTable + ", customer " + cusTable  
				+ ", userdtls " + userTable + " WHERE " + runTable + ".transid=" + transTable + ".transid  AND " + 
				runTable + ".userdtlsid=" + userTable + ".userdtlsid AND " + runTable + ".customerid="+cusTable + ".customerid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof ProductRunning){
				sql += productSQL(runTable,(ProductRunning)obj[i]);
			}
			if(obj[i] instanceof Transactions){
				sql += Transactions.transSQL(transTable,(Transactions)obj[i]);
			}
			if(obj[i] instanceof Customer){
				sql += Customer.customerSQL(cusTable,(Customer)obj[i]);
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
		
		System.out.println("SQL PRoduct recall "+ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			ProductRunning prun = new ProductRunning();
			try{prun.setRunid(rs.getLong("runid"));}catch(NullPointerException e){}
			try{prun.setRundate(rs.getString("rundate"));}catch(NullPointerException e){}
			try{prun.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{prun.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{prun.setRunstatus(rs.getInt("runstatus"));}catch(NullPointerException e){}
			try{prun.setIsrunactive(rs.getInt("isrunactive"));}catch(NullPointerException e){}
			try{prun.setRunremarks(rs.getString("runremarks"));}catch(NullPointerException e){}
			try{prun.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
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
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prun.setTransactions(trans);
			
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
			prun.setCustomer(cus);
			
			UserDtls user = new UserDtls();
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			prun.setUserDtls(user);
			
			
			runs.add(prun);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return runs;
	}
	
	/**
	 * 
	 * @param obj[Transactions][UserDtls]
	 * @return list of productrunning
	 */
	public static ProductRunning retrieve(String runId){
		ProductRunning prun = new ProductRunning();
		String runTable = "rund";
		//String transTable = "tran";
		String userTable = "usr";
		String sql = "SELECT * FROM  productrunning "+ runTable  + 
				", userdtls "+ userTable +" WHERE " +  
				runTable +".userdtlsid = "+ userTable +".userdtlsid AND " + runTable + ".runid="+runId;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{prun.setRunid(rs.getLong("runid"));}catch(NullPointerException e){}
			try{prun.setRundate(rs.getString("rundate"));}catch(NullPointerException e){}
			try{prun.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{prun.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{prun.setRunstatus(rs.getInt("runstatus"));}catch(NullPointerException e){}
			try{prun.setIsrunactive(rs.getInt("isrunactive"));}catch(NullPointerException e){}
			try{prun.setRunremarks(rs.getString("runremarks"));}catch(NullPointerException e){}
			try{prun.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			Transactions trans = new Transactions();
			trans.setTransid(rs.getLong("transid"));
			prun.setTransactions(trans);
			/*Transactions trans = new Transactions();
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
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prun.setTransactions(trans);*/
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			/*try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}*/
			prun.setUserDtls(user);
			
			Customer customer = new Customer();
			try{customer.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			prun.setCustomer(customer);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return prun;
	}
	
	public static ProductRunning save(ProductRunning run){
		if(run!=null){
			
			long id = ProductRunning.getInfo(run.getRunid() ==0? ProductRunning.getLatestId()+1 : run.getRunid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				run = ProductRunning.insertData(run, "1");
			}else if(id==2){
				LogU.add("update Data ");
				run = ProductRunning.updateData(run);
			}else if(id==3){
				LogU.add("added new Data ");
				run = ProductRunning.insertData(run, "3");
			}
			
		}
		return run;
	}
	
	public void save(){
			
			long id = getInfo(getRunid() ==0? getLatestId()+1 : getRunid());
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
	
	public static ProductRunning insertData(ProductRunning run, String type){
		String sql = "INSERT INTO productrunning ("
				+ "runid,"
				+ "rundate,"
				+ "clientip,"
				+ "clientbrowser,"
				+ "runstatus,"
				+ "isrunactive,"
				+ "runremarks,"
				+ "transid,"
				+ "prodid,"
				+ "userdtlsid,"
				+ "customerid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
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
			run.setRunid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			run.setRunid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, run.getRundate());
		ps.setString(3, run.getClientip());
		ps.setString(4, run.getClientbrowser());
		ps.setInt(5, run.getRunstatus());
		ps.setInt(6, run.getIsrunactive());
		ps.setString(7, run.getRunremarks());
		ps.setLong(8, run.getTransactions()==null? 0 : run.getTransactions().getTransid());
		ps.setLong(9, run.getProduct()==null? 0 : run.getProduct().getProdid());
		ps.setLong(10, run.getUserDtls()==null? 0 : (run.getUserDtls().getUserdtlsid()==null? 0 : run.getUserDtls().getUserdtlsid()));
		ps.setLong(11, run.getCustomer()==null? 0 : (run.getCustomer().getCustomerid()==0? 0 : run.getCustomer().getCustomerid()));
		
		LogU.add(run.getRundate());
		LogU.add(run.getClientip());
		LogU.add(run.getClientbrowser());
		LogU.add(run.getRunstatus());
		LogU.add(run.getIsrunactive());
		LogU.add(run.getRunremarks());
		LogU.add(run.getTransactions()==null? 0 : run.getTransactions().getTransid());
		LogU.add(run.getProduct()==null? 0 : run.getProduct().getProdid());
		LogU.add(run.getUserDtls()==null? 0 : (run.getUserDtls().getUserdtlsid()==null? 0 : run.getUserDtls().getUserdtlsid()));
		LogU.add(run.getCustomer()==null? 0 : (run.getCustomer().getCustomerid()==0? 0 : run.getCustomer().getCustomerid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return run;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO productrunning ("
				+ "runid,"
				+ "rundate,"
				+ "clientip,"
				+ "clientbrowser,"
				+ "runstatus,"
				+ "isrunactive,"
				+ "runremarks,"
				+ "transid,"
				+ "prodid,"
				+ "userdtlsid,"
				+ "customerid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
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
			setRunid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setRunid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, getRundate());
		ps.setString(3, getClientip());
		ps.setString(4, getClientbrowser());
		ps.setInt(5, getRunstatus());
		ps.setInt(6, getIsrunactive());
		ps.setString(7, getRunremarks());
		ps.setLong(8, getTransactions()==null? 0 : getTransactions().getTransid());
		ps.setLong(9, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(10, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(11, getCustomer()==null? 0 : (getCustomer().getCustomerid()==0? 0 : getCustomer().getCustomerid()));
		
		LogU.add(getRundate());
		LogU.add(getClientip());
		LogU.add(getClientbrowser());
		LogU.add(getRunstatus());
		LogU.add(getIsrunactive());
		LogU.add(getRunremarks());
		LogU.add(getTransactions()==null? 0 : getTransactions().getTransid());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getCustomer()==null? 0 : (getCustomer().getCustomerid()==0? 0 : getCustomer().getCustomerid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static ProductRunning updateData(ProductRunning run){
		String sql = "UPDATE productrunning SET "
				+ "rundate=?,"
				+ "clientip=?,"
				+ "clientbrowser=?,"
				+ "runstatus=?,"
				+ "isrunactive=?,"
				+ "runremarks=?,"
				+ "transid=?,"
				+ "prodid=?,"
				+ "userdtlsid=?,"
				+ "customerid=? " 
				+ " WHERE runid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table transactions");
		
		ps.setString(1, run.getRundate());
		ps.setString(2, run.getClientip());
		ps.setString(3, run.getClientbrowser());
		ps.setInt(4, run.getRunstatus());
		ps.setInt(5, run.getIsrunactive());
		ps.setString(6, run.getRunremarks());
		ps.setLong(7, run.getTransactions()==null? 0 : run.getTransactions().getTransid());
		ps.setLong(8, run.getProduct()==null? 0 : run.getProduct().getProdid());
		ps.setLong(9, run.getUserDtls()==null? 0 : (run.getUserDtls().getUserdtlsid()==null? 0 : run.getUserDtls().getUserdtlsid()));
		ps.setLong(10, run.getCustomer()==null? 0 : (run.getCustomer().getCustomerid()==0? 0 : run.getCustomer().getCustomerid()));
		ps.setLong(11, run.getRunid());
		
		LogU.add(run.getRundate());
		LogU.add(run.getClientip());
		LogU.add(run.getClientbrowser());
		LogU.add(run.getRunstatus());
		LogU.add(run.getIsrunactive());
		LogU.add(run.getRunremarks());
		LogU.add(run.getTransactions()==null? 0 : run.getTransactions().getTransid());
		LogU.add(run.getProduct()==null? 0 : run.getProduct().getProdid());
		LogU.add(run.getUserDtls()==null? 0 : (run.getUserDtls().getUserdtlsid()==null? 0 : run.getUserDtls().getUserdtlsid()));
		LogU.add(run.getCustomer()==null? 0 : (run.getCustomer().getCustomerid()==0? 0 : run.getCustomer().getCustomerid()));
		LogU.add(run.getRunid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return run;
	}
	
	public void updateData(){
		String sql = "UPDATE productrunning SET "
				+ "rundate=?,"
				+ "clientip=?,"
				+ "clientbrowser=?,"
				+ "runstatus=?,"
				+ "isrunactive=?,"
				+ "runremarks=?,"
				+ "transid=?,"
				+ "prodid=?,"
				+ "userdtlsid=?,"
				+ "customerid=? " 
				+ " WHERE runid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table transactions");
		
		ps.setString(1, getRundate());
		ps.setString(2, getClientip());
		ps.setString(3, getClientbrowser());
		ps.setInt(4, getRunstatus());
		ps.setInt(5, getIsrunactive());
		ps.setString(6, getRunremarks());
		ps.setLong(7, getTransactions()==null? 0 : getTransactions().getTransid());
		ps.setLong(8, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(9, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(10, getCustomer()==null? 0 : (getCustomer().getCustomerid()==0? 0 : getCustomer().getCustomerid()));
		ps.setLong(11, getRunid());
		
		LogU.add(getRundate());
		LogU.add(getClientip());
		LogU.add(getClientbrowser());
		LogU.add(getRunstatus());
		LogU.add(getIsrunactive());
		LogU.add(getRunremarks());
		LogU.add(getTransactions()==null? 0 : getTransactions().getTransid());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getCustomer()==null? 0 : (getCustomer().getCustomerid()==0? 0 : getCustomer().getCustomerid()));
		LogU.add(getRunid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productrunning : " + s.getMessage());
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
		sql="SELECT runid FROM productrunning  ORDER BY runid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("runid");
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
		ps = conn.prepareStatement("SELECT runid FROM productrunning WHERE runid=?");
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
		String sql = "UPDATE productrunning set isrunactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE runid=?";
		
		String[] params = new String[1];
		params[0] = getRunid()+"";
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
	
	public long getRunid() {
		return runid;
	}
	public void setRunid(long runid) {
		this.runid = runid;
	}
	public String getRundate() {
		return rundate;
	}
	public void setRundate(String rundate) {
		this.rundate = rundate;
	}
	public String getClientip() {
		return clientip;
	}
	public void setClientip(String clientip) {
		this.clientip = clientip;
	}
	public String getClientbrowser() {
		return clientbrowser;
	}
	public void setClientbrowser(String clientbrowser) {
		this.clientbrowser = clientbrowser;
	}
	public int getRunstatus() {
		return runstatus;
	}
	public void setRunstatus(int runstatus) {
		this.runstatus = runstatus;
	}
	public int getIsrunactive() {
		return isrunactive;
	}
	public void setIsrunactive(int isrunactive) {
		this.isrunactive = isrunactive;
	}
	public String getRunremarks() {
		return runremarks;
	}
	public void setRunremarks(String runremarks) {
		this.runremarks = runremarks;
	}
	public Transactions getTransactions() {
		return transactions;
	}
	public void setTransactions(Transactions transactions) {
		this.transactions = transactions;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
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
	
	public List<QtyRunning> getProductHolds() {
		return productHolds;
	}

	public void setProductHolds(List<QtyRunning> productHolds) {
		this.productHolds = productHolds;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public static void main(String[] args) {
		
		ProductRunning prun = new ProductRunning();
		prun.setRunid(1);
		prun.setRundate(DateUtils.getCurrentDateYYYYMMDD());
		prun.setClientip("1.0.0.0.0.0");
		prun.setClientbrowser("Chrome");
		prun.setRunstatus(1);
		prun.setIsrunactive(1);
		prun.setRunremarks("testing again");
		
		Transactions t = new Transactions();
		t.setTransid(1);
		prun.setTransactions(t);
		
		Product product = new Product();
		product.setProdid(1);
		prun.setProduct(product);
		
		UserDtls user = new UserDtls();
		user.setUserdtlsid(1l);
		prun.setUserDtls(user);
		
		//prun.save();
		prun = new ProductRunning();
		prun.setRunid(1);
		prun.setIsrunactive(1);
		
		for(ProductRunning r : ProductRunning.retrieve(prun)){
			System.out.println(r.getClientbrowser());
		}
		
	}
	
}












