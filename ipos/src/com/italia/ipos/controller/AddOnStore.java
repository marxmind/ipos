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
 * @since 05/23/2017
 * @version 1.0
 *
 */

public class AddOnStore {

	private long id;
	private String dateTrans;
	private String receiptNo;
	private String description;
	private double amount;
	private int addOnType; 
	private int isActive;
	private int status;
	private Timestamp timestamp;
	private ProductRunning productRunning;
	
	private boolean between;
	private String dateFrom;
	private String dateTo;
	
	public AddOnStore(){}
	
	public AddOnStore(
			long id,
			String dateTrans,
			String receiptNo,
			String description,
			double amount,
			int addOnType, 
			int isActive,
			int status,
			ProductRunning productRunning
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.receiptNo = receiptNo;
		this.description = description;
		this.amount = amount;
		this.addOnType = addOnType;
		this.isActive = isActive;
		this.status = status;
		this.productRunning = productRunning;
	}
	
	public static String addOnsSQL(String tablename,AddOnStore add){
		String sql= " AND "+ tablename +".addisactive=" + add.getIsActive();
		if(add!=null){
			if(add.getId()!=0){
				sql += " AND "+ tablename +".addid=" + add.getId();
			}
			
			if(add.isBetween()){
			
				sql += " AND ("+ tablename +".addDate>= '" + add.getDateFrom()+"' AND " + tablename +".addDate<= '" + add.getDateTo()+"') ";
				
			}else{	
				if(add.getDateTrans()!=null){
					sql += " AND "+ tablename +".addDate= '" + add.getDateTrans()+"'";
				}
			}
			
			if(add.getReceiptNo()!=null){
				sql += " AND "+ tablename +".addreceiptno= '" + add.getReceiptNo()+"'";
			}
			
			if(add.getAddOnType()!=0){
				sql += " AND "+ tablename +".addtype= " + add.getAddOnType();
			}
			
			if(add.getStatus()!=0){
				sql += " AND "+ tablename +".addstatus= " + add.getStatus();
			}
			
		}
		
		return sql;
	}
	
	public static List<AddOnStore> retrieve(Object... obj){
		List<AddOnStore> adds = Collections.synchronizedList(new ArrayList<AddOnStore>());
		
		String addOnTable = "addOn";
		String runTable = "prod";
		String sql = "SELECT * FROM addonsinstore "+ addOnTable +", productrunning "+ runTable+ 
				" WHERE " + addOnTable + ".runid=" + runTable + ".runid "; 
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof AddOnStore){
				sql += addOnsSQL(addOnTable,(AddOnStore)obj[i]);
			}
			if(obj[i] instanceof ProductRunning){
				sql += ProductRunning.productSQL(runTable,(ProductRunning)obj[i]);
			}
		}
		
        System.out.println("SQL Product: "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			AddOnStore add = new AddOnStore();
			try{add.setId(rs.getLong("addid"));}catch(NullPointerException e){}
			try{add.setDateTrans(rs.getString("addDate"));}catch(NullPointerException e){}
			try{add.setReceiptNo(rs.getString("addreceiptno"));}catch(NullPointerException e){}
			try{add.setDescription(rs.getString("addDesc"));}catch(NullPointerException e){}
			try{add.setAmount(rs.getDouble("addAmount"));}catch(NullPointerException e){}
			try{add.setAddOnType(rs.getInt("addtype"));}catch(NullPointerException e){}
			try{add.setIsActive(rs.getInt("addisactive"));}catch(NullPointerException e){}
			try{add.setStatus(rs.getInt("addstatus"));}catch(NullPointerException e){}
			try{add.setTimestamp(rs.getTimestamp("addtimestamp"));}catch(NullPointerException e){}
			
			ProductRunning prun = new ProductRunning();
			try{prun.setRunid(rs.getLong("runid"));}catch(NullPointerException e){}
			try{prun.setRundate(rs.getString("rundate"));}catch(NullPointerException e){}
			try{prun.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{prun.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{prun.setRunstatus(rs.getInt("runstatus"));}catch(NullPointerException e){}
			try{prun.setIsrunactive(rs.getInt("isrunactive"));}catch(NullPointerException e){}
			try{prun.setRunremarks(rs.getString("runremarks"));}catch(NullPointerException e){}
			try{prun.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			add.setProductRunning(prun);
			
			adds.add(add);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return adds;
	}
	
	public static List<AddOnStore> retrieve(String sql, String[] params){
		List<AddOnStore> adds = Collections.synchronizedList(new ArrayList<AddOnStore>());
		
		String addOnTable = "addOn";
		String runTable = "prod";
		String sqlAdd = "SELECT * FROM addonsinstore "+ addOnTable +", productrunning "+ runTable+ 
				" WHERE " + addOnTable + ".runid=" + runTable + ".runid "; 
		
		sql = sqlAdd + sql;
		
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
		
		System.out.println("SQL ADD " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			AddOnStore add = new AddOnStore();
			try{add.setId(rs.getLong("addid"));}catch(NullPointerException e){}
			try{add.setDateTrans(rs.getString("addDate"));}catch(NullPointerException e){}
			try{add.setReceiptNo(rs.getString("addreceiptno"));}catch(NullPointerException e){}
			try{add.setDescription(rs.getString("addDesc"));}catch(NullPointerException e){}
			try{add.setAmount(rs.getDouble("addAmount"));}catch(NullPointerException e){}
			try{add.setAddOnType(rs.getInt("addtype"));}catch(NullPointerException e){}
			try{add.setIsActive(rs.getInt("addisactive"));}catch(NullPointerException e){}
			try{add.setStatus(rs.getInt("addstatus"));}catch(NullPointerException e){}
			try{add.setTimestamp(rs.getTimestamp("addtimestamp"));}catch(NullPointerException e){}
			
			ProductRunning prun = new ProductRunning();
			try{prun.setRunid(rs.getLong("runid"));}catch(NullPointerException e){}
			try{prun.setRundate(rs.getString("rundate"));}catch(NullPointerException e){}
			try{prun.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{prun.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{prun.setRunstatus(rs.getInt("runstatus"));}catch(NullPointerException e){}
			try{prun.setIsrunactive(rs.getInt("isrunactive"));}catch(NullPointerException e){}
			try{prun.setRunremarks(rs.getString("runremarks"));}catch(NullPointerException e){}
			try{prun.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			add.setProductRunning(prun);
			
			adds.add(add);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return adds;
	}
	
	public static void save(AddOnStore prod){
		if(prod!=null){
			
			long id = StoreProduct.getInfo(prod.getId() ==0? AddOnStore.getLatestId()+1 : prod.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				AddOnStore.insertData(prod, "1");
			}else if(id==2){
				LogU.add("update Data ");
				AddOnStore.updateData(prod);
			}else if(id==3){
				LogU.add("added new Data ");
				AddOnStore.insertData(prod, "3");
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
	
	public static AddOnStore insertData(AddOnStore prod, String type){
		String sql = "INSERT INTO addonsinstore ("
				+ "addid,"
				+ "addDate,"
				+ "addreceiptno,"
				+ "addDesc,"
				+ "addAmount,"
				+ "addtype,"
				+ "addisactive,"
				+ "addstatus,"
				+ "runid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table addonsinstore");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			prod.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			prod.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, prod.getDateTrans());
		ps.setString(cnt++, prod.getReceiptNo());
		ps.setString(cnt++, prod.getDescription());
		ps.setDouble(cnt++, prod.getAmount());
		ps.setInt(cnt++, prod.getAddOnType());
		ps.setInt(cnt++, prod.getIsActive());
		ps.setInt(cnt++, prod.getStatus());
		ps.setLong(cnt++, prod.getProductRunning()==null? 0 : prod.getProductRunning().getRunid());
		
		LogU.add(prod.getDateTrans());
		LogU.add(prod.getReceiptNo());
		LogU.add(prod.getDescription());
		LogU.add(prod.getAmount());
		LogU.add(prod.getAddOnType());
		LogU.add(prod.getIsActive());
		LogU.add(prod.getStatus());
		LogU.add(prod.getProductRunning()==null? 0 : prod.getProductRunning().getRunid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to addonsinstore : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO addonsinstore ("
				+ "addid,"
				+ "addDate,"
				+ "addreceiptno,"
				+ "addDesc,"
				+ "addAmount,"
				+ "addtype,"
				+ "addisactive,"
				+ "addstatus,"
				+ "runid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table addonsinstore");
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
		ps.setString(cnt++, getReceiptNo());
		ps.setString(cnt++, getDescription());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getAddOnType());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getStatus());
		ps.setLong(cnt++, getProductRunning()==null? 0 : getProductRunning().getRunid());
		
		LogU.add(getDateTrans());
		LogU.add(getReceiptNo());
		LogU.add(getDescription());
		LogU.add(getAmount());
		LogU.add(getAddOnType());
		LogU.add(getIsActive());
		LogU.add(getStatus());
		LogU.add(getProductRunning()==null? 0 : getProductRunning().getRunid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to addonsinstore : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static AddOnStore updateData(AddOnStore prod){
		String sql = "UPDATE addonsinstore SET "
				+ "addDate=?,"
				+ "addreceiptno=?,"
				+ "addDesc=?,"
				+ "addAmount=?,"
				+ "addtype=?,"
				+ "addstatus=?,"
				+ "runid=? " 
				+ " WHERE addid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table addonsinstore");
		
		ps.setString(cnt++, prod.getDateTrans());
		ps.setString(cnt++, prod.getReceiptNo());
		ps.setString(cnt++, prod.getDescription());
		ps.setDouble(cnt++, prod.getAmount());
		ps.setInt(cnt++, prod.getAddOnType());
		ps.setInt(cnt++, prod.getStatus());
		ps.setLong(cnt++, prod.getProductRunning()==null? 0 : prod.getProductRunning().getRunid());
		ps.setLong(cnt++, prod.getId());
		
		LogU.add(prod.getDateTrans());
		LogU.add(prod.getReceiptNo());
		LogU.add(prod.getDescription());
		LogU.add(prod.getAmount());
		LogU.add(prod.getAddOnType());
		LogU.add(prod.getStatus());
		LogU.add(prod.getProductRunning()==null? 0 : prod.getProductRunning().getRunid());
		LogU.add(prod.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to addonsinstore : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void updateData(){
		String sql = "UPDATE addonsinstore SET "
				+ "addDate=?,"
				+ "addreceiptno=?,"
				+ "addDesc=?,"
				+ "addAmount=?,"
				+ "addtype=?,"
				+ "addstatus=?,"
				+ "runid=? " 
				+ " WHERE addid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table addonsinstore");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getReceiptNo());
		ps.setString(cnt++, getDescription());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getAddOnType());
		ps.setInt(cnt++, getStatus());
		ps.setLong(cnt++, getProductRunning()==null? 0 : getProductRunning().getRunid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getReceiptNo());
		LogU.add(getDescription());
		LogU.add(getAmount());
		LogU.add(getAddOnType());
		LogU.add(getStatus());
		LogU.add(getProductRunning()==null? 0 : getProductRunning().getRunid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to addonsinstore : " + s.getMessage());
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
		sql="SELECT addid FROM addonsinstore  ORDER BY addid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("addid");
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
		ps = conn.prepareStatement("SELECT addid FROM addonsinstore WHERE addid=?");
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
		String sql = "UPDATE addonsinstore set addisactive=0 WHERE addid=?";
		
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getAddOnType() {
		return addOnType;
	}
	public void setAddOnType(int addOnType) {
		this.addOnType = addOnType;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public ProductRunning getProductRunning() {
		return productRunning;
	}
	public void setProductRunning(ProductRunning productRunning) {
		this.productRunning = productRunning;
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

	public static void main(String[] args) {
		
		AddOnStore on = new AddOnStore();
		on.setId(1);
		on.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		on.setReceiptNo(DateUtils.getCurrentDateYYYYMMDD()+"1000000001");
		on.setDescription("testing again");
		on.setAmount(100);
		on.setAddOnType(1);
		on.setIsActive(1);
		on.setStatus(1);
		
		ProductRunning run = new ProductRunning();
		run.setRunid(1);
		on.setProductRunning(run);
		
		on.save();
		
	}
	
}
