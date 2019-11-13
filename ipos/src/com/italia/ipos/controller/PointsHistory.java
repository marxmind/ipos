package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.enm.HistoryReceiptStatus;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 09/28/2018
 *
 */
public class PointsHistory {

	private long id;
	private String dateTrans;
	private double points;
	private int isActive;
	private int type;
	private Customer customer;
	private String receiptNo;
	
	private String typeName;
	
	public static List<PointsHistory> retrieve(String sqlAdd, String[] params){
		List<PointsHistory> pts = Collections.synchronizedList(new ArrayList<PointsHistory>());
		
		String tablePoints = "pts";
		String tableCustomer = "cuz";
		
		
		String sql = "SELECT * FROM pointshistory "+ tablePoints +", customer "+ tableCustomer +" WHERE  "+
				tablePoints + ".customerid=" +tableCustomer + ".customerid ";
		
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
			
			PointsHistory pt = new PointsHistory();
			try{pt.setId(rs.getLong("pohid"));}catch(NullPointerException e){}
			try{pt.setDateTrans(rs.getString("podate"));}catch(NullPointerException e){}
			try{pt.setPoints(rs.getDouble("points"));}catch(NullPointerException e){}
			try{pt.setIsActive(rs.getInt("isactivepoh"));}catch(NullPointerException e){}
			try{pt.setType(rs.getInt("pointtype"));}catch(NullPointerException e){}
			try{pt.setReceiptNo(rs.getString("receiptno"));}catch(NullPointerException e){}
			if(pt.getType()==1) {
				pt.setTypeName("Addedd");
			}else {
				pt.setTypeName("Deducted");
			}
			
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
			
			pt.setCustomer(cus);
			
			pts.add(pt);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pts;
	}
	
	public static PointsHistory retrieve(long id){
		PointsHistory pt = new PointsHistory();
		
		String tablePoints = "pts";
		String tableCustomer = "cuz";
		
		String sql = "SELECT * FROM pointshistory "+ tablePoints +", customer "+ tableCustomer +" WHERE  "+
				tablePoints + ".customerid=" +tableCustomer + ".customerid AND " + 
				tablePoints+".isactivepoh=1 AND "+tablePoints+".pohid=" +id;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{pt.setId(rs.getLong("pohid"));}catch(NullPointerException e){}
			try{pt.setDateTrans(rs.getString("podate"));}catch(NullPointerException e){}
			try{pt.setPoints(rs.getDouble("points"));}catch(NullPointerException e){}
			try{pt.setIsActive(rs.getInt("isactivepoh"));}catch(NullPointerException e){}
			try{pt.setType(rs.getInt("pointtype"));}catch(NullPointerException e){}
			try{pt.setReceiptNo(rs.getString("receiptno"));}catch(NullPointerException e){}
			if(pt.getType()==1) {
				pt.setTypeName("Addedd");
			}else {
				pt.setTypeName("Deducted");
			}
			
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
			
			pt.setCustomer(cus);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pt;
	}
	
	public static void save(PointsHistory pt){
		if(pt!=null){
			
			long id = PointsHistory.getInfo(pt.getId() ==0? PointsHistory.getLatestId()+1 : pt.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				PointsHistory.insertData(pt, "1");
			}else if(id==2){
				LogU.add("update Data ");
				PointsHistory.updateData(pt);
			}else if(id==3){
				LogU.add("added new Data ");
				PointsHistory.insertData(pt, "3");
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
	
	public static PointsHistory insertData(PointsHistory pt, String type){
		String sql = "INSERT INTO pointshistory ("
				+ "pohid,"
				+ "podate,"
				+ "points,"
				+ "isactivepoh,"
				+ "pointtype,"
				+ "receiptno,"
				+ "customerid) " 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table pointshistory");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			pt.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			pt.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, pt.getDateTrans());
		ps.setDouble(cnt++, pt.getPoints());
		ps.setInt(cnt++, pt.getIsActive());
		ps.setInt(cnt++, pt.getType());
		ps.setString(cnt++, pt.getReceiptNo());
		ps.setLong(cnt++, pt.getCustomer()==null? 0 : pt.getCustomer().getCustomerid());
		
		LogU.add(pt.getDateTrans());
		LogU.add(pt.getPoints());
		LogU.add(pt.getIsActive());
		LogU.add(pt.getType());
		LogU.add(pt.getReceiptNo());
		LogU.add(pt.getCustomer()==null? 0 : pt.getCustomer().getCustomerid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to pointshistory : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return pt;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO pointshistory ("
				+ "pohid,"
				+ "podate,"
				+ "points,"
				+ "isactivepoh,"
				+ "pointtype,"
				+ "receiptno,"
				+ "customerid) " 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table pointshistory");
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
		ps.setDouble(cnt++, getPoints());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getType());
		ps.setString(cnt++, getReceiptNo());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		
		LogU.add(getDateTrans());
		LogU.add(getPoints());
		LogU.add(getIsActive());
		LogU.add(getType());
		LogU.add(getReceiptNo());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to pointshistory : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static PointsHistory updateData(PointsHistory pt){
		String sql = "UPDATE pointshistory SET "
				+ "podate=?,"
				+ "points=?,"
				+ "pointtype=?,"
				+ "receiptno=?,"
				+ "customerid=? " 
				+ " WHERE pohid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("updating data into table pointshistory");
		
		ps.setString(cnt++, pt.getDateTrans());
		ps.setDouble(cnt++, pt.getPoints());
		ps.setInt(cnt++, pt.getType());
		ps.setString(cnt++, pt.getReceiptNo());
		ps.setLong(cnt++, pt.getCustomer()==null? 0 : pt.getCustomer().getCustomerid());
		ps.setLong(cnt++, pt.getId());
		
		LogU.add(pt.getDateTrans());
		LogU.add(pt.getPoints());
		LogU.add(pt.getIsActive());
		LogU.add(pt.getType());
		LogU.add(pt.getReceiptNo());
		LogU.add(pt.getCustomer()==null? 0 : pt.getCustomer().getCustomerid());
		LogU.add(pt.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to pointshistory : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return pt;
	}
	
	public void updateData(){
		String sql = "UPDATE pointshistory SET "
				+ "podate=?,"
				+ "points=?,"
				+ "pointtype=?,"
				+ "receiptno=?,"
				+ "customerid=? " 
				+ " WHERE pohid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("updating data into table pointshistory");
		
		ps.setString(cnt++, getDateTrans());
		ps.setDouble(cnt++, getPoints());
		ps.setInt(cnt++, getType());
		ps.setString(cnt++, getReceiptNo());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getPoints());
		LogU.add(getIsActive());
		LogU.add(getType());
		LogU.add(getReceiptNo());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to pointshistory : " + s.getMessage());
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
		sql="SELECT pohid FROM pointshistory  ORDER BY pohid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("pohid");
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
		ps = conn.prepareStatement("SELECT pohid FROM pointshistory WHERE pohid=?");
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
		String sql = "UPDATE pointshistory set isactivepoh=0 WHERE pohid=?";
		
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
	public double getPoints() {
		return points;
	}
	public void setPoints(double points) {
		this.points = points;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	
}
