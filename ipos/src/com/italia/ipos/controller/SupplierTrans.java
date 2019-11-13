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
 * @author mark
 * @since 12/24/2016
 * @version 1.0
 *
 */
public class SupplierTrans {
	
	private long id;
	private String transDate;
	private double purchasedPrice;
	private int status;
	private int isActive;
	private Supplier supplier;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private String deliveryReceipt;
	private double balance;
	private boolean isBetweenDate;
	private String dateFrom;
	private String dateTo;
	
	private List<SupplierPayment> payments = Collections.synchronizedList(new ArrayList<SupplierPayment>());
	
	public SupplierTrans(){}
	
	public SupplierTrans(
			long id,
			String transDate,
			double purchasedPrice,
			int status,
			int isActive,
			Supplier supplier,
			UserDtls userDtls,
			Timestamp timestamp
			){
		this.id = id;
		this.transDate = transDate;
		this.purchasedPrice = purchasedPrice;
		this.status = status;
		this.isActive = isActive;
		this.supplier = supplier;
		this.userDtls = userDtls;
	}
	
	public static String supplierSQL(String tablename,SupplierTrans sup){
		String sql= " AND "+ tablename +".isActiveTrans=" + sup.getIsActive();
		if(sup!=null){
			if(sup.getId()!=0){
				sql += " AND "+ tablename +".suptranid=" + sup.getId();
			}
			if(sup.getIsBetweenDate()){
					sql += " AND ("+ tablename +".transdate >='" + sup.getDateFrom() + "' AND " + tablename + ".transdate <='" + sup.getDateTo() + "') ";
			}else{
				if(sup.getTransDate()!=null){
					sql += " AND "+ tablename +".transdate ='" + sup.getTransDate() +"'";
				}
			}
			
			if(sup.getStatus()!=0){
				sql += " AND "+ tablename +".tranStatus=" + sup.getStatus();
			}
			
		}
		return sql;
	}
	
	public static List<SupplierTrans> retrieve(Object... obj){
		List<SupplierTrans> trans = Collections.synchronizedList(new ArrayList<SupplierTrans>());
		
		String supTable = "sup";
		String tranTable = "tran";
		String userTable = "usr";
		String sql = "SELECT * FROM  suppliertrans "+ tranTable +",supplier "+ supTable +", userdtls "+ userTable +" WHERE  "+ tranTable +".supid="+ supTable +".supid AND "+ tranTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof SupplierTrans){
				sql += supplierSQL(tranTable,(SupplierTrans)obj[i]);
			}
			if(obj[i] instanceof Supplier){
				sql += Supplier.supplierSQL(supTable,(Supplier)obj[i]);
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
			
			SupplierTrans tran = new SupplierTrans();
			try{tran.setId(rs.getLong("suptranid"));}catch(NullPointerException e){}
			try{tran.setTransDate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{tran.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{tran.setStatus(rs.getInt("tranStatus"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isActiveTrans"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{tran.setDeliveryReceipt(rs.getString("deliveryrcpt"));}catch(NullPointerException e){}
			
			Supplier sup = new Supplier();
			try{sup.setSupid(rs.getLong("supid"));}catch(NullPointerException e){}
			try{sup.setSuppliername(rs.getString("suppliername"));}catch(NullPointerException e){}
			try{sup.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{sup.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{sup.setOwnername(rs.getString("ownername"));}catch(NullPointerException e){}
			try{sup.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{sup.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			tran.setSupplier(sup);
			
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
			tran.setUserDtls(user);
			
			trans.add(tran);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static SupplierTrans supplier(String supplierTransId){
		SupplierTrans tran = new SupplierTrans();
		String supTable = "sup";
		String tranTable = "tran";
		String userTable = "usr";
		String sql = "SELECT * FROM  suppliertrans "+ tranTable +",supplier "+ supTable +", userdtls "+ userTable +
				" WHERE  "+ tranTable +".supid="+ supTable +".supid AND "+ tranTable +".userdtlsid = "+ userTable +".userdtlsid AND "+tranTable+".suptranid=" + supplierTransId;
		
		 System.out.println("SQL "+sql);
			
			Connection conn = null;
			ResultSet rs = null;
			PreparedStatement ps = null;
			try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				try{tran.setId(rs.getLong("suptranid"));}catch(NullPointerException e){}
				try{tran.setTransDate(rs.getString("transdate"));}catch(NullPointerException e){}
				try{tran.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
				try{tran.setStatus(rs.getInt("tranStatus"));}catch(NullPointerException e){}
				try{tran.setIsActive(rs.getInt("isActiveTrans"));}catch(NullPointerException e){}
				try{tran.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
				try{tran.setDeliveryReceipt(rs.getString("deliveryrcpt"));}catch(NullPointerException e){}
				
				Supplier sup = new Supplier();
				try{sup.setSupid(rs.getLong("supid"));}catch(NullPointerException e){}
				try{sup.setSuppliername(rs.getString("suppliername"));}catch(NullPointerException e){}
				try{sup.setAddress(rs.getString("address"));}catch(NullPointerException e){}
				try{sup.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
				try{sup.setOwnername(rs.getString("ownername"));}catch(NullPointerException e){}
				try{sup.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
				try{sup.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
				tran.setSupplier(sup);
				
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
				tran.setUserDtls(user);
				
			}
			
			rs.close();
			ps.close();
			ConnectDB.close(conn);
			}catch(Exception e){e.getMessage();}
			
		return tran;
	}
	
	public static void save(SupplierTrans sup){
		if(sup!=null){
			
			long id = SupplierTrans.getInfo(sup.getId() ==0? SupplierTrans.getLatestId()+1 : sup.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				SupplierTrans.insertData(sup, "1");
			}else if(id==2){
				LogU.add("update Data ");
				SupplierTrans.updateData(sup);
			}else if(id==3){
				LogU.add("added new Data ");
				SupplierTrans.insertData(sup, "3");
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
	
	public static SupplierTrans insertData(SupplierTrans sup, String type){
		String sql = "INSERT INTO suppliertrans ("
				+ "suptranid,"
				+ "transdate,"
				+ "purchasedprice,"
				+ "tranStatus,"
				+ "isActiveTrans,"
				+ "supid,"
				+ "userdtlsid,"
				+ "deliveryrcpt)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table suppliertrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			sup.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			sup.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, sup.getTransDate());
		ps.setDouble(cnt++, sup.getPurchasedPrice());
		ps.setInt(cnt++, sup.getStatus());
		ps.setInt(cnt++, sup.getIsActive());
		ps.setLong(cnt++, sup.getSupplier()==null? 0 : sup.getSupplier().getSupid());
		ps.setLong(cnt++, sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, sup.getDeliveryReceipt());
		
		LogU.add(sup.getTransDate());
		LogU.add(sup.getPurchasedPrice());
		LogU.add(sup.getStatus());
		LogU.add(sup.getIsActive());
		LogU.add(sup.getSupplier()==null? 0 : sup.getSupplier().getSupid());
		LogU.add(sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		LogU.add(sup.getDeliveryReceipt());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to suppliertrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sup;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO suppliertrans ("
				+ "suptranid,"
				+ "transdate,"
				+ "purchasedprice,"
				+ "tranStatus,"
				+ "isActiveTrans,"
				+ "supid,"
				+ "userdtlsid,"
				+ "deliveryrcpt)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table suppliertrans");
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
		ps.setString(cnt++, getTransDate());
		ps.setDouble(cnt++, getPurchasedPrice());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getSupplier()==null? 0 : getSupplier().getSupid());
		ps.setLong(cnt++, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, getDeliveryReceipt());
		
		LogU.add(getTransDate());
		LogU.add(getPurchasedPrice());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getSupplier()==null? 0 : getSupplier().getSupid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getDeliveryReceipt());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to suppliertrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static SupplierTrans updateData(SupplierTrans sup){
		String sql = "UPDATE suppliertrans SET "
				+ "transdate=?,"
				+ "purchasedprice=?,"
				+ "tranStatus=?,"
				+ "supid=?,"
				+ "userdtlsid=?,"
				+ "deliveryrcpt=?  " 
				+ " WHERE suptranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table suppliertrans");
		int cnt = 1;
		
		ps.setString(cnt++, sup.getTransDate());
		ps.setDouble(cnt++, sup.getPurchasedPrice());
		ps.setInt(cnt++, sup.getStatus());
		ps.setLong(cnt++, sup.getSupplier()==null? 0 : sup.getSupplier().getSupid());
		ps.setLong(cnt++, sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, sup.getDeliveryReceipt());
		ps.setLong(cnt++, sup.getId());
		
		LogU.add(sup.getTransDate());
		LogU.add(sup.getPurchasedPrice());
		LogU.add(sup.getStatus());
		LogU.add(sup.getSupplier()==null? 0 : sup.getSupplier().getSupid());
		LogU.add(sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		LogU.add(sup.getDeliveryReceipt());
		LogU.add(sup.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to suppliertrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sup;
	}
	
	public void updateData(){
		String sql = "UPDATE suppliertrans SET "
				+ "transdate=?,"
				+ "purchasedprice=?,"
				+ "tranStatus=?,"
				+ "supid=?,"
				+ "userdtlsid=?,"
				+ "deliveryrcpt=?  " 
				+ " WHERE suptranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table suppliertrans");
		int cnt = 1;
		ps.setString(cnt++, getTransDate());
		ps.setDouble(cnt++, getPurchasedPrice());
		ps.setInt(cnt++, getStatus());
		ps.setLong(cnt++, getSupplier()==null? 0 : getSupplier().getSupid());
		ps.setLong(cnt++, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, getDeliveryReceipt());
		ps.setLong(cnt++, getId());
		
		LogU.add(getTransDate());
		LogU.add(getPurchasedPrice());
		LogU.add(getStatus());
		LogU.add(getSupplier()==null? 0 : getSupplier().getSupid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getDeliveryReceipt());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to suppliertrans : " + s.getMessage());
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
		sql="SELECT suptranid FROM suppliertrans  ORDER BY suptranid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("suptranid");
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
		ps = conn.prepareStatement("SELECT suptranid FROM suppliertrans WHERE suptranid=?");
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
		String sql = "UPDATE suppliertrans set isActiveTrans=0, userdtlsid=? WHERE suptranid=?";
		
		String[] params = new String[2];
		params[0] = getUserDtls().getUserdtlsid()+"";
		params[1] = getId()+"";
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
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public double getPurchasedPrice() {
		return purchasedPrice;
	}
	public void setPurchasedPrice(double purchasedPrice) {
		this.purchasedPrice = purchasedPrice;
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
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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
	
	public static void main(String[] args) {
		/*
		SupplierTrans t = new SupplierTrans();
		t.setId(1);
		t.setTransDate(DateUtils.getCurrentDateYYYYMMDD());
		t.setPurchasedPrice(200.00);
		t.setStatus(1);
		t.setIsActive(1);
		Supplier supplier = new Supplier();
		supplier.setSupid(1);
		t.setSupplier(supplier);
		t.setUserDtls(Login.getUserLogin().getUserDtls());
		t.save();*/
		
	}

	public List<SupplierPayment> getPayments() {
		return payments;
	}

	public void setPayments(List<SupplierPayment> payments) {
		this.payments = payments;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public boolean getIsBetweenDate() {
		return isBetweenDate;
	}

	public void setBetweenDate(boolean isBetweenDate) {
		this.isBetweenDate = isBetweenDate;
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

	public String getDeliveryReceipt() {
		return deliveryReceipt;
	}

	public void setDeliveryReceipt(String deliveryReceipt) {
		this.deliveryReceipt = deliveryReceipt;
	}
	
}
