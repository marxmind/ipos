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
 * @author mark italia
 * @since 06/23/2017
 * @version 1.0
 *
 */

public class Receipt {
	
	private int id;
	private String dateResetted;
	private String reasonDescription;
	private String licenseNo;
	private String licenseDate;
	private int isActual;
	private int isActivated;
	private long latestOR;
	private long previousOR;
	private int isActive;
	private Timestamp timestamp;
	
	private boolean checked;
	
	public Receipt(){}
	
	public Receipt(
			int id,
			String dateResetted,
			String reasonDescription,
			String licenseNo,
			String licenseDate,
			int isActual,
			int isActivated,
			long latestOR,
			long previousOR,
			int isActive
			){
		this.id = id;
		this.dateResetted = dateResetted;
		this.reasonDescription = reasonDescription;
		this.licenseNo = licenseNo;
		this.licenseDate = licenseDate;
		this.isActual = isActual;
		this.isActivated = isActivated;
		this.latestOR = latestOR;
		this.previousOR = previousOR;
		this.isActive = isActive;
	}
	
	public static long generateLatestOR(){
		String sql = "SELECT latestOR FROM receiptmanagement WHERE isactivated=1 AND recIsActive!=0";
		long newOR = 0l;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			newOR = rs.getLong("latestOR") + 1;
			break;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		return newOR;
	}
	
	public static List<Receipt> retrieve(String sql, String[] params){
		List<Receipt> rpts = Collections.synchronizedList(new ArrayList<Receipt>());
		
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
			Receipt rpt = new Receipt();
			try{rpt.setId(rs.getInt("resId"));}catch(NullPointerException e){}
			try{rpt.setDateResetted(rs.getString("dateresetted"));}catch(NullPointerException e){}
			try{rpt.setReasonDescription(rs.getString("reasondesc"));}catch(NullPointerException e){}
			try{rpt.setLicenseNo(rs.getString("licenseno"));}catch(NullPointerException e){}
			try{rpt.setLicenseDate(rs.getString("licenseapprovedate"));}catch(NullPointerException e){}
			try{rpt.setIsActual(rs.getInt("isactual"));}catch(NullPointerException e){}
			try{rpt.setIsActivated(rs.getInt("isactivated"));}catch(NullPointerException e){}
			try{rpt.setLatestOR(rs.getLong("latestOR"));}catch(NullPointerException e){}
			try{rpt.setPreviousOR(rs.getLong("previousOR"));}catch(NullPointerException e){}
			try{rpt.setIsActive(rs.getInt("recIsActive"));}catch(NullPointerException e){}
			try{rpt.setTimestamp(rs.getTimestamp("rectimestamp"));}catch(NullPointerException e){}
			
			if(rpt.getIsActivated()==1){
				rpt.setChecked(true);
			}
			
			rpts.add(rpt);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rpts;
	}
	
	public static void save(Receipt rpt){
		if(rpt!=null){
			
			long id = Receipt.getInfo(rpt.getId() ==0? Receipt.getLatestId()+1 : rpt.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				Receipt.insertData(rpt, "1");
			}else if(id==2){
				LogU.add("update Data ");
				Receipt.updateData(rpt);
			}else if(id==3){
				LogU.add("added new Data ");
				Receipt.insertData(rpt, "3");
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
	
	public static Receipt insertData(Receipt rpt, String type){
		String sql = "INSERT INTO receiptmanagement ("
				+ "resId,"
				+ "dateresetted,"
				+ "reasondesc,"
				+ "licenseno,"
				+ "licenseapprovedate,"
				+ "isactual,"
				+ "isactivated,"
				+ "latestOR,"
				+ "previousOR,"
				+ "recIsActive) " 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table receiptmanagement");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			rpt.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			rpt.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, rpt.getDateResetted());
		ps.setString(cnt++, rpt.getReasonDescription());
		ps.setString(cnt++, rpt.getLicenseNo());
		ps.setString(cnt++, rpt.getLicenseDate());
		ps.setInt(cnt++, rpt.getIsActual());
		ps.setInt(cnt++, rpt.getIsActivated());
		ps.setLong(cnt++, rpt.getLatestOR());
		ps.setLong(cnt++, rpt.getPreviousOR());
		ps.setInt(cnt++, rpt.getIsActive());
		
		LogU.add(rpt.getDateResetted());
		LogU.add(rpt.getReasonDescription());
		LogU.add(rpt.getLicenseNo());
		LogU.add(rpt.getLicenseDate());
		LogU.add(rpt.getIsActual());
		LogU.add(rpt.getIsActivated());
		LogU.add(rpt.getLatestOR());
		LogU.add(rpt.getPreviousOR());
		LogU.add(rpt.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to receiptmanagement : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rpt;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO receiptmanagement ("
				+ "resId,"
				+ "dateresetted,"
				+ "reasondesc,"
				+ "licenseno,"
				+ "licenseapprovedate,"
				+ "isactual,"
				+ "isactivated,"
				+ "latestOR,"
				+ "previousOR,"
				+ "recIsActive) " 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1; 
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table receiptmanagement");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, getDateResetted());
		ps.setString(cnt++, getReasonDescription());
		ps.setString(cnt++, getLicenseNo());
		ps.setString(cnt++, getLicenseDate());
		ps.setInt(cnt++, getIsActual());
		ps.setInt(cnt++, getIsActivated());
		ps.setLong(cnt++, getLatestOR());
		ps.setLong(cnt++, getPreviousOR());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getDateResetted());
		LogU.add(getReasonDescription());
		LogU.add(getLicenseNo());
		LogU.add(getLicenseDate());
		LogU.add(getIsActual());
		LogU.add(getIsActivated());
		LogU.add(getLatestOR());
		LogU.add(getPreviousOR());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to receiptmanagement : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Receipt updateData(Receipt rpt){
		String sql = "UPDATE receiptmanagement SET "
				+ "dateresetted=?,"
				+ "reasondesc=?,"
				+ "licenseno=?,"
				+ "licenseapprovedate=?,"
				+ "isactual=?,"
				+ "isactivated=?,"
				+ "latestOR=?,"
				+ "previousOR=?"
				+ " WHERE resId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		 
		LogU.add("===========================START=========================");
		LogU.add("updating data into table receiptmanagement");
		int cnt = 1;
		ps.setString(cnt++, rpt.getDateResetted());
		ps.setString(cnt++, rpt.getReasonDescription());
		ps.setString(cnt++, rpt.getLicenseNo());
		ps.setString(cnt++, rpt.getLicenseDate());
		ps.setInt(cnt++, rpt.getIsActual());
		ps.setInt(cnt++, rpt.getIsActivated());
		ps.setLong(cnt++, rpt.getLatestOR());
		ps.setLong(cnt++, rpt.getPreviousOR());
		ps.setInt(cnt++, rpt.getId());
		
		LogU.add(rpt.getDateResetted());
		LogU.add(rpt.getReasonDescription());
		LogU.add(rpt.getLicenseNo());
		LogU.add(rpt.getLicenseDate());
		LogU.add(rpt.getIsActual());
		LogU.add(rpt.getIsActivated());
		LogU.add(rpt.getLatestOR());
		LogU.add(rpt.getPreviousOR());
		LogU.add(rpt.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to receiptmanagement : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rpt;
	}
	
	public void updateData(){
		String sql = "UPDATE receiptmanagement SET "
				+ "dateresetted=?,"
				+ "reasondesc=?,"
				+ "licenseno=?,"
				+ "licenseapprovedate=?,"
				+ "isactual=?,"
				+ "isactivated=?,"
				+ "latestOR=?,"
				+ "previousOR=?"
				+ " WHERE resId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		 
		LogU.add("===========================START=========================");
		LogU.add("updating data into table receiptmanagement");
		int cnt = 1;
		ps.setString(cnt++, getDateResetted());
		ps.setString(cnt++, getReasonDescription());
		ps.setString(cnt++, getLicenseNo());
		ps.setString(cnt++, getLicenseDate());
		ps.setInt(cnt++, getIsActual());
		ps.setInt(cnt++, getIsActivated());
		ps.setLong(cnt++, getLatestOR());
		ps.setLong(cnt++, getPreviousOR());
		ps.setInt(cnt++, getId());
		
		LogU.add(getDateResetted());
		LogU.add(getReasonDescription());
		LogU.add(getLicenseNo());
		LogU.add(getLicenseDate());
		LogU.add(getIsActual());
		LogU.add(getIsActivated());
		LogU.add(getLatestOR());
		LogU.add(getPreviousOR());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to receiptmanagement : " + s.getMessage());
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
		sql="SELECT resId FROM receiptmanagement  ORDER BY resId DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("resId");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static int getInfo(int id){
		boolean isNotNull=false;
		int result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		int val = getLatestId();	
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
	public static boolean isIdNoExist(int id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT resId FROM receiptmanagement WHERE resId=?");
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
		String sql = "UPDATE receiptmanagement set recIsActive=0 WHERE resId=?";
		
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
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDateResetted() {
		return dateResetted;
	}
	public void setDateResetted(String dateResetted) {
		this.dateResetted = dateResetted;
	}
	public String getReasonDescription() {
		return reasonDescription;
	}
	public void setReasonDescription(String reasonDescription) {
		this.reasonDescription = reasonDescription;
	}
	public String getLicenseNo() {
		return licenseNo;
	}
	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}
	public String getLicenseDate() {
		return licenseDate;
	}
	public void setLicenseDate(String licenseDate) {
		this.licenseDate = licenseDate;
	}
	public int getIsActual() {
		return isActual;
	}
	public void setIsActual(int isActual) {
		this.isActual = isActual;
	}
	public int getIsActivated() {
		return isActivated;
	}
	public void setIsActivated(int isActivated) {
		this.isActivated = isActivated;
	}
	public long getLatestOR() {
		return latestOR;
	}
	public void setLatestOR(long latestOR) {
		this.latestOR = latestOR;
	}
	public long getPreviousOR() {
		return previousOR;
	}
	public void setPreviousOR(long previousOR) {
		this.previousOR = previousOR;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
