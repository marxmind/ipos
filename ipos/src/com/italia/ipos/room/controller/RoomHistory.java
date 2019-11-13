package com.italia.ipos.room.controller;

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
 * @author mark italia
 * @since 04/11/2017
 * @version 1.0
 *
 */
public class RoomHistory {

	private int id;
	private String dateTrans;
	private String roomNumber;
	private String name;
	private String description;
	private String customerName;
	private String checkInDateTime;
	private String checkOutDateTime;
	private int countDays;
	private double price;
	private int active;
	private String remarks;
	
	public RoomHistory(){}
	
	public RoomHistory(
			int id,
			String dateTrans,
			String roomNumber,
			String name,
			String description,
			String customerName,
			String checkInDateTime,
			String checkOutDateTime,
			int countDays,
			double price,
			int active,
			String remarks
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.roomNumber = roomNumber;
		this.name = name;
		this.description = description;
		this.customerName = customerName;
		this.checkInDateTime = checkInDateTime;
		this.checkOutDateTime = checkOutDateTime;
		this.countDays = countDays;
		this.price = price;
		this.active = active;
		this.remarks = remarks;
	}
	
	public static String roomSQL(String tablename,RoomHistory room){
		String sql = " roomIsActive=" + room.getActive();
		if(room!=null){
			if(room.getId()!=0){
				sql += " AND "+ tablename +".hisId=" + room.getId();
			}
			if(room.getRoomNumber()!=null){
				sql += " AND "+ tablename +".roomDateTrans='" + room.getDateTrans()+"'";
			}
			if(room.getRoomNumber()!=null){
				sql += " AND "+ tablename +".roomNo='" + room.getRoomNumber()+"'";
			}
			if(room.getName()!=null){
				sql += " AND "+ tablename +".roomName like '%" + room.getName() +"%'";
			}
			if(room.getCustomerName()!=null){
				sql += " AND "+ tablename +".roomCustomer like '%" + room.getCustomerName() +"%'";
			}
			if(room.getRemarks()!=null){
				sql += " AND "+ tablename +".roomRemarks like '%" + room.getRemarks() +"%'";
			}
		}
		return sql;
	}
	
	public static List<RoomHistory> retrieve(Object... obj){
		List<RoomHistory> rooms = Collections.synchronizedList(new ArrayList<RoomHistory>());
		
		String roomTable = "room";
		String sql = "SELECT * FROM roomhistory "+ roomTable + 
				" WHERE "; 
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof RoomHistory){
				sql += roomSQL(roomTable,(RoomHistory)obj[i]);
			}
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			RoomHistory room = new RoomHistory();
			try{room.setId(rs.getInt("hisId"));}catch(NullPointerException e){}
			try{room.setDateTrans(rs.getString("roomDateTrans"));}catch(NullPointerException e){}
			try{room.setRoomNumber(rs.getString("roomNo"));}catch(NullPointerException e){}
			try{room.setName(rs.getString("roomName"));}catch(NullPointerException e){}
			try{room.setCustomerName(rs.getString("roomCustomer"));}catch(NullPointerException e){}
			try{room.setCheckInDateTime(rs.getString("roomCheckIn"));}catch(NullPointerException e){}
			try{room.setCheckOutDateTime(rs.getString("roomCheckOut"));}catch(NullPointerException e){}
			try{room.setDescription(rs.getString("roomDescription"));}catch(NullPointerException e){}
			try{room.setPrice(rs.getDouble("roomPrice"));}catch(NullPointerException e){}
			try{room.setCountDays(rs.getInt("roomDays"));}catch(NullPointerException e){}
			try{room.setActive(rs.getInt("roomIsActive"));}catch(NullPointerException e){}
			try{room.setRemarks(rs.getString("roomRemarks"));}catch(NullPointerException e){}
			rooms.add(room);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rooms;
	}
	
	public static List<RoomHistory> retrieve(String sql, String[] params){
		List<RoomHistory> rooms = Collections.synchronizedList(new ArrayList<RoomHistory>());
		
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
		
		System.out.println("SQL: " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			RoomHistory room = new RoomHistory();
			try{room.setId(rs.getInt("hisId"));}catch(NullPointerException e){}
			try{room.setDateTrans(rs.getString("roomDateTrans"));}catch(NullPointerException e){}
			try{room.setRoomNumber(rs.getString("roomNo"));}catch(NullPointerException e){}
			try{room.setName(rs.getString("roomName"));}catch(NullPointerException e){}
			try{room.setCustomerName(rs.getString("roomCustomer"));}catch(NullPointerException e){}
			try{room.setCheckInDateTime(rs.getString("roomCheckIn"));}catch(NullPointerException e){}
			try{room.setCheckOutDateTime(rs.getString("roomCheckOut"));}catch(NullPointerException e){}
			try{room.setDescription(rs.getString("roomDescription"));}catch(NullPointerException e){}
			try{room.setPrice(rs.getDouble("roomPrice"));}catch(NullPointerException e){}
			try{room.setCountDays(rs.getInt("roomDays"));}catch(NullPointerException e){}
			try{room.setActive(rs.getInt("roomIsActive"));}catch(NullPointerException e){}
			try{room.setRemarks(rs.getString("roomRemarks"));}catch(NullPointerException e){}
			rooms.add(room);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rooms;
	}
	
	public static void save(RoomHistory room){
		if(room!=null){
			
			long id = RoomHistory.getInfo(room.getId() ==0? RoomHistory.getLatestId()+1 : room.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				RoomHistory.insertData(room, "1");
			}else if(id==2){
				LogU.add("update Data ");
				RoomHistory.updateData(room);
			}else if(id==3){
				LogU.add("added new Data ");
				RoomHistory.insertData(room, "3");
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
	
	public static RoomHistory insertData(RoomHistory room, String type){
		String sql = "INSERT INTO roomhistory ("
				+ "hisId,"
				+ "roomNo,"
				+ "roomName,"
				+ "roomCustomer,"
				+ "roomCheckIn,"
				+ "roomCheckOut,"
				+ "roomDescription,"
				+ "roomPrice,"
				+ "roomDays,"
				+ "roomIsActive,"
				+ "roomDateTrans,"
				+ "roomRemarks) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table rooms");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			room.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			room.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, room.getRoomNumber());
		ps.setString(cnt++, room.getName());
		ps.setString(cnt++, room.getCustomerName());
		ps.setString(cnt++, room.getCheckInDateTime());
		ps.setString(cnt++, room.getCheckOutDateTime());
		ps.setString(cnt++, room.getDescription());
		ps.setDouble(cnt++, room.getPrice());
		ps.setInt(cnt++, room.getCountDays());
		ps.setInt(cnt++, room.getActive());
		ps.setString(cnt++, room.getDateTrans());
		ps.setString(cnt++, room.getRemarks());
		
		LogU.add(room.getRoomNumber());
		LogU.add(room.getName());
		LogU.add(room.getCustomerName());
		LogU.add(room.getCheckInDateTime());
		LogU.add(room.getCheckOutDateTime());
		LogU.add(room.getDescription());
		LogU.add(room.getPrice());
		LogU.add(room.getCountDays());
		LogU.add(room.getActive());
		LogU.add(room.getDateTrans());
		LogU.add(room.getRemarks());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to room : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return room;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO roomhistory ("
				+ "hisId,"
				+ "roomNo,"
				+ "roomName,"
				+ "roomCustomer,"
				+ "roomCheckIn,"
				+ "roomCheckOut,"
				+ "roomDescription,"
				+ "roomPrice,"
				+ "roomDays,"
				+ "roomIsActive,"
				+ "roomDateTrans,"
				+ "roomRemarks) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			int id =1;
			int cnt = 1;
			LogU.add("===========================START=========================");
			LogU.add("inserting data into table rooms");
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
			
			ps.setString(cnt++, getRoomNumber());
			ps.setString(cnt++, getName());
			ps.setString(cnt++, getCustomerName());
			ps.setString(cnt++, getCheckInDateTime());
			ps.setString(cnt++, getCheckOutDateTime());
			ps.setString(cnt++, getDescription());
			ps.setDouble(cnt++, getPrice());
			ps.setInt(cnt++, getCountDays());
			ps.setInt(cnt++, getActive());
			ps.setString(cnt++, getDateTrans());
			ps.setString(cnt++, getRemarks());
			
			LogU.add(getRoomNumber());
			LogU.add(getName());
			LogU.add(getCustomerName());
			LogU.add(getCheckInDateTime());
			LogU.add(getCheckOutDateTime());
			LogU.add(getDescription());
			LogU.add(getPrice());
			LogU.add(getCountDays());
			LogU.add(getActive());
			LogU.add(getDateTrans());
			LogU.add(getRemarks());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to room : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static RoomHistory updateData(RoomHistory room){
		String sql = "UPDATE roomhistory SET "
				+ "roomNo=?,"
				+ "roomName=?,"
				+ "roomCustomer=?,"
				+ "roomCheckIn=?,"
				+ "roomCheckOut=?,"
				+ "roomDescription=?,"
				+ "roomPrice=?,"
				+ "roomDays=?,"
				+ "roomDateTrans=?,"
				+ "roomRemarks=? " 
				+ " WHERE hisId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table rooms");
		
		
		ps.setString(cnt++, room.getRoomNumber());
		ps.setString(cnt++, room.getName());
		ps.setString(cnt++, room.getCustomerName());
		ps.setString(cnt++, room.getCheckInDateTime());
		ps.setString(cnt++, room.getCheckOutDateTime());
		ps.setString(cnt++, room.getDescription());
		ps.setDouble(cnt++, room.getPrice());
		ps.setInt(cnt++, room.getCountDays());
		ps.setString(cnt++, room.getDateTrans());
		ps.setString(cnt++, room.getRemarks());
		ps.setInt(cnt++, room.getId());
		
		LogU.add(room.getRoomNumber());
		LogU.add(room.getName());
		LogU.add(room.getCustomerName());
		LogU.add(room.getCheckInDateTime());
		LogU.add(room.getCheckOutDateTime());
		LogU.add(room.getDescription());
		LogU.add(room.getPrice());
		LogU.add(room.getCountDays());
		LogU.add(room.getDateTrans());
		LogU.add(room.getRemarks());
		LogU.add(room.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to room : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return room;
	}
	
	public void updateData(){
		String sql = "UPDATE roomhistory SET "
				+ "roomNo=?,"
				+ "roomName=?,"
				+ "roomCustomer=?,"
				+ "roomCheckIn=?,"
				+ "roomCheckOut=?,"
				+ "roomDescription=?,"
				+ "roomPrice=?,"
				+ "roomDays=?,"
				+ "roomDateTrans=?,"
				+ "roomRemarks=? " 
				+ " WHERE hisId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table rooms");
		
		
		ps.setString(cnt++, getRoomNumber());
		ps.setString(cnt++, getName());
		ps.setString(cnt++, getCustomerName());
		ps.setString(cnt++, getCheckInDateTime());
		ps.setString(cnt++, getCheckOutDateTime());
		ps.setString(cnt++, getDescription());
		ps.setDouble(cnt++, getPrice());
		ps.setInt(cnt++, getCountDays());
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getId());
		
		LogU.add(getRoomNumber());
		LogU.add(getName());
		LogU.add(getCustomerName());
		LogU.add(getCheckInDateTime());
		LogU.add(getCheckOutDateTime());
		LogU.add(getDescription());
		LogU.add(getPrice());
		LogU.add(getCountDays());
		LogU.add(getDateTrans());
		LogU.add(getRemarks());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to room : " + s.getMessage());
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
		sql="SELECT hisId FROM roomhistory  ORDER BY hisId DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("hisId");
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
		ps = conn.prepareStatement("SELECT hisId FROM roomhistory WHERE hisId=?");
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
		String sql = "UPDATE roomhistory set roomIsActive=0 WHERE hisId=?";
		
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
	public String getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCheckInDateTime() {
		return checkInDateTime;
	}
	public void setCheckInDateTime(String checkInDateTime) {
		this.checkInDateTime = checkInDateTime;
	}
	public String getCheckOutDateTime() {
		return checkOutDateTime;
	}
	public void setCheckOutDateTime(String checkOutDateTime) {
		this.checkOutDateTime = checkOutDateTime;
	}
	public int getCountDays() {
		return countDays;
	}
	public void setCountDays(int countDays) {
		this.countDays = countDays;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}

	public String getDateTrans() {
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}

