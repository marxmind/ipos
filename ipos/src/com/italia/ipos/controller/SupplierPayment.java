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
 * @since 12/24/2016
 * @version 1.0
 *
 */
public class SupplierPayment {
	private long id;
	private String payTransDate;
	private double amount;
	private String remarks;
	private String orNumber;
	private int isActive;
	private SupplierTrans supplierTrans;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private boolean between;
	private String dateFrom;
	private String dateTo;
	
	public SupplierPayment(){}
	
	public SupplierPayment(
			long id,
			String payTransDate,
			double amount,
			String remarks,
			int isActive,
			SupplierTrans supplierTrans,
			UserDtls userDtls
			){
		this.id = id;
		this.payTransDate = payTransDate;
		this.amount = amount;
		this.remarks = remarks;
		this.isActive = isActive;
		this.supplierTrans = supplierTrans;
		this.userDtls = userDtls;
	}
	
	public static String paymentSQL(String tablename,SupplierPayment sup){
		String sql= " AND "+ tablename +".isActivePay=" + sup.getIsActive();
		if(sup!=null){
			if(sup.getId()!=0){
				sql += " AND "+ tablename +".payid=" + sup.getId();
			}
			if(sup.isBetween()){
				sql += " AND ("+ tablename +".paytransdate >='" + sup.getDateFrom() + "' AND "+ tablename +".paytransdate <='" + sup.getDateTo() +"')";
			}else{
				
				if(sup.getPayTransDate()!=null){
					sql += " AND "+ tablename +".paytransdate ='" + sup.getPayTransDate() + "'";
				}
			
			}
		}
		return sql;
	}
	
	public static List<SupplierPayment> retrieve(Object... obj){
		List<SupplierPayment> pays = Collections.synchronizedList(new ArrayList<SupplierPayment>());
		
		String payTable = "pay";
		String tranTable = "tran";
		String userTable = "usr";
		String sql = "SELECT * FROM  suppliertrans "+ tranTable +",suppliertranspayment "+ payTable +", userdtls "+ userTable +" WHERE  "+ payTable +".suptranid="+ tranTable +".suptranid AND "+ payTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			
			if(obj[i] instanceof SupplierPayment){
				sql += paymentSQL(payTable,(SupplierPayment)obj[i]);
			}
			if(obj[i] instanceof SupplierTrans){
				sql += SupplierTrans.supplierSQL(tranTable,(SupplierTrans)obj[i]);
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
			
			SupplierPayment pay = new SupplierPayment();
			try{pay.setId(rs.getLong("payid"));}catch(NullPointerException e){}
			try{pay.setPayTransDate(rs.getString("paytransdate"));}catch(NullPointerException e){}
			try{pay.setAmount(rs.getDouble("payamount"));}catch(NullPointerException e){}
			try{pay.setRemarks(rs.getString("remarks"));}catch(NullPointerException e){}
			try{pay.setIsActive(rs.getInt("isActivePay"));}catch(NullPointerException e){}
			try{pay.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{pay.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e){}
			
			SupplierTrans tran = new SupplierTrans();
			try{tran.setId(rs.getLong("suptranid"));}catch(NullPointerException e){}
			try{tran.setTransDate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{tran.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{tran.setStatus(rs.getInt("tranStatus"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isActiveTrans"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			Supplier sup = new Supplier();
			try{sup.setSupid(rs.getLong("supid"));}catch(NullPointerException e){}
			tran.setSupplier(sup);
			
			pay.setSupplierTrans(tran);
			
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
			pay.setUserDtls(user);
			
			pays.add(pay);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pays;
	}
	
	public SupplierPayment payment(String paymentId){
		SupplierPayment pay = new SupplierPayment();
		String payTable = "pay";
		String tranTable = "tran";
		String userTable = "usr";
		String sql = "SELECT * FROM  suppliertrans "+ tranTable +",suppliertranspayment "+ payTable +", userdtls "+ userTable +
				" WHERE  "+ payTable +".suptranid="+ tranTable +".suptranid AND "+ payTable +".userdtlsid = "+ userTable +".userdtlsid AND "+ payTable+ ".payid=" + paymentId;
		
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{pay.setId(rs.getLong("payid"));}catch(NullPointerException e){}
			try{pay.setPayTransDate(rs.getString("paytransdate"));}catch(NullPointerException e){}
			try{pay.setAmount(rs.getDouble("payamount"));}catch(NullPointerException e){}
			try{pay.setRemarks(rs.getString("remarks"));}catch(NullPointerException e){}
			try{pay.setIsActive(rs.getInt("isActivePay"));}catch(NullPointerException e){}
			try{pay.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{pay.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e){}
			
			SupplierTrans tran = new SupplierTrans();
			try{tran.setId(rs.getLong("suptranid"));}catch(NullPointerException e){}
			try{tran.setTransDate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{tran.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{tran.setStatus(rs.getInt("tranStatus"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isActiveTrans"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			Supplier sup = new Supplier();
			try{sup.setSupid(rs.getLong("supid"));}catch(NullPointerException e){}
			tran.setSupplier(sup);
			
			pay.setSupplierTrans(tran);
			
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
			pay.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pay;
	}
	
	public static void save(SupplierPayment sup){
		if(sup!=null){
			
			long id = SupplierPayment.getInfo(sup.getId() ==0? SupplierPayment.getLatestId()+1 : sup.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				SupplierPayment.insertData(sup, "1");
			}else if(id==2){
				LogU.add("update Data ");
				SupplierPayment.updateData(sup);
			}else if(id==3){
				LogU.add("added new Data ");
				SupplierPayment.insertData(sup, "3");
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
	
	public static SupplierPayment insertData(SupplierPayment sup, String type){
		String sql = "INSERT INTO suppliertranspayment ("
				+ "payid,"
				+ "paytransdate,"
				+ "payamount,"
				+ "remarks,"
				+ "isActivePay,"
				+ "suptranid,"
				+ "userdtlsid,"
				+ "ornumber)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table suppliertranspayment");
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
		ps.setString(cnt++, sup.getPayTransDate());
		ps.setDouble(cnt++, sup.getAmount());
		ps.setString(cnt++, sup.getRemarks());
		ps.setInt(cnt++, sup.getIsActive());
		ps.setLong(cnt++, sup.getSupplierTrans()==null? 0 : sup.getSupplierTrans().getId());
		ps.setLong(cnt++, sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, sup.getOrNumber());
		
		LogU.add(sup.getPayTransDate());
		LogU.add(sup.getAmount());
		LogU.add(sup.getRemarks());
		LogU.add(sup.getIsActive());
		LogU.add(sup.getSupplierTrans()==null? 0 : sup.getSupplierTrans().getId());
		LogU.add(sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		LogU.add(sup.getOrNumber());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to suppliertranspayment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sup;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO suppliertranspayment ("
				+ "payid,"
				+ "paytransdate,"
				+ "payamount,"
				+ "remarks,"
				+ "isActivePay,"
				+ "suptranid,"
				+ "userdtlsid,"
				+ "ornumber)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table suppliertranspayment");
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
		ps.setString(cnt++, getPayTransDate());
		ps.setDouble(cnt++, getAmount());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getSupplierTrans()==null? 0 : getSupplierTrans().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, getOrNumber());
		
		LogU.add(getPayTransDate());
		LogU.add(getAmount());
		LogU.add(getRemarks());
		LogU.add(getIsActive());
		LogU.add(getSupplierTrans()==null? 0 : getSupplierTrans().getId());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getOrNumber());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to suppliertranspayment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static SupplierPayment updateData(SupplierPayment sup){
		String sql = "UPDATE suppliertranspayment SET "
				+ "paytransdate=?,"
				+ "payamount=?,"
				+ "remarks=?,"
				+ "suptranid=?,"
				+ "userdtlsid=?,"
				+ "ornumber=? " 
				+ " WHERE payid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("upadting data into table suppliertranspayment");
		int cnt= 1;
		
		ps.setString(cnt++, sup.getPayTransDate());
		ps.setDouble(cnt++, sup.getAmount());
		ps.setString(cnt++, sup.getRemarks());
		ps.setLong(cnt++, sup.getSupplierTrans()==null? 0 : sup.getSupplierTrans().getId());
		ps.setLong(cnt++, sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, sup.getOrNumber());
		ps.setLong(cnt++, sup.getId());
		
		LogU.add(sup.getPayTransDate());
		LogU.add(sup.getAmount());
		LogU.add(sup.getRemarks());
		LogU.add(sup.getSupplierTrans()==null? 0 : sup.getSupplierTrans().getId());
		LogU.add(sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		LogU.add(sup.getOrNumber());
		LogU.add(sup.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to suppliertranspayment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sup;
	}
	
	public void updateData(){
		String sql = "UPDATE suppliertranspayment SET "
				+ "paytransdate=?,"
				+ "payamount=?,"
				+ "remarks=?,"
				+ "suptranid=?,"
				+ "userdtlsid=?,"
				+ "ornumber=? " 
				+ " WHERE payid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("upadting data into table suppliertranspayment");
		int cnt = 1;
		
		ps.setString(cnt++, getPayTransDate());
		ps.setDouble(cnt++, getAmount());
		ps.setString(cnt++, getRemarks());
		ps.setLong(cnt++, getSupplierTrans()==null? 0 : getSupplierTrans().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, getOrNumber());
		ps.setLong(cnt++, getId());
		
		LogU.add(getPayTransDate());
		LogU.add(getAmount());
		LogU.add(getRemarks());
		LogU.add(getSupplierTrans()==null? 0 : getSupplierTrans().getId());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getOrNumber());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to suppliertranspayment : " + s.getMessage());
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
		sql="SELECT payid FROM suppliertranspayment  ORDER BY payid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("payid");
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
		ps = conn.prepareStatement("SELECT payid FROM suppliertranspayment WHERE payid=?");
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
		String sql = "UPDATE suppliertranspayment set isActivePay=0, userdtlsid=? WHERE payid=?";
		
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
	public String getPayTransDate() {
		return payTransDate;
	}
	public void setPayTransDate(String payTransDate) {
		this.payTransDate = payTransDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public SupplierTrans getSupplierTrans() {
		return supplierTrans;
	}
	public void setSupplierTrans(SupplierTrans supplierTrans) {
		this.supplierTrans = supplierTrans;
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

	public String getOrNumber() {
		return orNumber;
	}

	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

	public static void main(String[] args) {
		
		SupplierPayment pay = new SupplierPayment();
		pay.setId(1);
		pay.setPayTransDate(DateUtils.getCurrentDateYYYYMMDD());
		pay.setAmount(200.00);
		pay.setRemarks("testing again");
		pay.setIsActive(1);
		SupplierTrans tran = new SupplierTrans();
		tran.setId(1);
		pay.setSupplierTrans(tran);
		pay.setUserDtls(Login.getUserLogin().getUserDtls());
		pay.save();
		
	}
}
