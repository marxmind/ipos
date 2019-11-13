package com.italia.ipos.room.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.ipos.controller.Customer;
import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.Currency;
import com.italia.ipos.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 04/11/2017
 * @version 1.0
 *
 */
public class Rooms {

	private int id;
	private String roomNumber;
	private String name;
	private String description;
	private String customerName;
	private String checkInDateTime;
	private String checkOutDateTime;
	private int countDays;
	private int available;
	private double price;
	private int active;
	private Customer customer;
	private String availableName;
	private double addPrice;
	private String addOnsDetails;
	
	private String amountRendered;
	
	public Rooms(){}
	
	public Rooms(
			int id,
			String roomNumber,
			String name,
			String description,
			String customerName,
			String checkInDateTime,
			String checkOutDateTime,
			int countDays,
			int available,
			double price,
			int active,
			Customer customer,
			double addPrice,
			String addOnsDetails
			){
		this.id = id;
		this.roomNumber = roomNumber;
		this.name = name;
		this.description = description;
		this.customerName = customerName;
		this.checkInDateTime = checkInDateTime;
		this.checkOutDateTime = checkOutDateTime;
		this.countDays = countDays;
		this.available = available;
		this.price = price;
		this.active = active;
		this.customer = customer;
	}
	
	public static String roomSQL(String tablename,Rooms room){
		String sql = " roomIsActive=" + room.getActive();
		if(room!=null){
			if(room.getId()!=0){
				sql += " AND "+ tablename +".roomId=" + room.getId();
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
		}
		return sql;
	}
	
	public static List<Rooms> retrieve(Object... obj){
		List<Rooms> rooms = Collections.synchronizedList(new ArrayList<Rooms>());
		
		String roomTable = "room";
		String sql = "SELECT * FROM rooms "+ roomTable + 
				" WHERE "; 
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof Rooms){
				sql += roomSQL(roomTable,(Rooms)obj[i]);
			}
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Rooms room = new Rooms();
			room.setId(rs.getInt("roomId"));
			room.setRoomNumber(rs.getString("roomNo"));
			room.setName(rs.getString("roomName"));
			room.setCustomerName(rs.getString("roomCustomer"));
			room.setCheckInDateTime(rs.getString("roomCheckIn"));
			room.setCheckOutDateTime(rs.getString("roomCheckOut"));
			room.setDescription(rs.getString("roomDescription"));
			room.setPrice(rs.getDouble("roomPrice"));
			room.setCountDays(rs.getInt("roomDays"));
			room.setAvailable(rs.getInt("roomIsAvailable"));
			room.setActive(rs.getInt("roomIsActive"));
			room.setAddOnsDetails(rs.getString("addOns"));
			room.setAddPrice(rs.getDouble("addPrice"));
			
			Customer customer = new Customer();
			customer.setCustomerid(rs.getLong("customerid"));
			room.setCustomer(customer);
			
			room.setAvailableName(room.getAvailable()==1? "OCCUPIED" : "NOT OCCUPIED");
			
			rooms.add(room);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rooms;
	}
	
	public static List<Rooms> retrieve(String sql, String[] params){
		List<Rooms> rooms = Collections.synchronizedList(new ArrayList<Rooms>());
		
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
		
		System.out.println("SQL : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Rooms room = new Rooms();
			room.setId(rs.getInt("roomId"));
			room.setRoomNumber(rs.getString("roomNo"));
			room.setName(rs.getString("roomName"));
			room.setCustomerName(rs.getString("roomCustomer"));
			room.setCheckInDateTime(rs.getString("roomCheckIn"));
			room.setCheckOutDateTime(rs.getString("roomCheckOut"));
			room.setDescription(rs.getString("roomDescription"));
			room.setPrice(rs.getDouble("roomPrice"));
			room.setCountDays(rs.getInt("roomDays"));
			room.setAvailable(rs.getInt("roomIsAvailable"));
			room.setActive(rs.getInt("roomIsActive"));
			room.setAddOnsDetails(rs.getString("addOns"));
			room.setAddPrice(rs.getDouble("addPrice"));
			
			Customer customer = new Customer();
			customer.setCustomerid(rs.getLong("customerid"));
			room.setCustomer(customer);
			
			room.setAvailableName(room.getAvailable()==1? "OCCUPIED" : "NOT OCCUPIED");
			
			double amount = room.getCountDays() * room.getPrice();
			amount += room.getAddPrice();
			room.setAmountRendered(amount);
			
			rooms.add(room);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rooms;
	}
	
	public static void save(Rooms room){
		if(room!=null){
			
			long id = Rooms.getInfo(room.getId() ==0? Rooms.getLatestId()+1 : room.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				Rooms.insertData(room, "1");
			}else if(id==2){
				LogU.add("update Data ");
				Rooms.updateData(room);
			}else if(id==3){
				LogU.add("added new Data ");
				Rooms.insertData(room, "3");
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
	
	public static Rooms insertData(Rooms room, String type){
		String sql = "INSERT INTO rooms ("
				+ "roomId,"
				+ "roomNo,"
				+ "roomName,"
				+ "roomCustomer,"
				+ "roomCheckIn,"
				+ "roomCheckOut,"
				+ "roomDescription,"
				+ "roomPrice,"
				+ "roomDays,"
				+ "roomIsAvailable,"
				+ "roomIsActive,"
				+ "customerid,"
				+ "addPrice,"
				+ "addOns) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
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
		ps.setInt(cnt++, room.getAvailable());
		ps.setInt(cnt++, room.getActive());
		ps.setLong(cnt++, room.getCustomer()==null? 0 : room.getCustomer().getCustomerid());
		ps.setDouble(cnt++, room.getAddPrice());
		ps.setString(cnt++, room.getAddOnsDetails());
		
		LogU.add(room.getRoomNumber());
		LogU.add(room.getName());
		LogU.add(room.getCustomerName());
		LogU.add(room.getCheckInDateTime());
		LogU.add(room.getCheckOutDateTime());
		LogU.add(room.getDescription());
		LogU.add(room.getPrice());
		LogU.add(room.getCountDays());
		LogU.add(room.getAvailable());
		LogU.add(room.getActive());
		LogU.add(room.getCustomer()==null? 0 : room.getCustomer().getCustomerid());
		LogU.add(room.getAddPrice());
		LogU.add(room.getAddOnsDetails());
		
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
		String sql = "INSERT INTO rooms ("
				+ "roomId,"
				+ "roomNo,"
				+ "roomName,"
				+ "roomCustomer,"
				+ "roomCheckIn,"
				+ "roomCheckOut,"
				+ "roomDescription,"
				+ "roomPrice,"
				+ "roomDays,"
				+ "roomIsAvailable,"
				+ "roomIsActive,"
				+ "customerid,"
				+ "addPrice,"
				+ "addOns) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
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
			ps.setInt(cnt++, getAvailable());
			ps.setInt(cnt++, getActive());
			ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
			ps.setDouble(cnt++, getAddPrice());
			ps.setString(cnt++, getAddOnsDetails());
			
			LogU.add(getRoomNumber());
			LogU.add(getName());
			LogU.add(getCustomerName());
			LogU.add(getCheckInDateTime());
			LogU.add(getCheckOutDateTime());
			LogU.add(getDescription());
			LogU.add(getPrice());
			LogU.add(getCountDays());
			LogU.add(getAvailable());
			LogU.add(getActive());
			LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
			LogU.add(getAddPrice());
			LogU.add(getAddOnsDetails());
			
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
	
	public static Rooms updateData(Rooms room){
		String sql = "UPDATE rooms SET "
				+ "roomNo=?,"
				+ "roomName=?,"
				+ "roomCustomer=?,"
				+ "roomCheckIn=?,"
				+ "roomCheckOut=?,"
				+ "roomDescription=?,"
				+ "roomPrice=?,"
				+ "roomDays=?,"
				+ "roomIsAvailable=?, "
				+ "customerid=?,"
				+ "addPrice=?,"
				+ "addOns=? " 
				+ " WHERE roomId=?";
		
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
		ps.setInt(cnt++, room.getAvailable());
		ps.setLong(cnt++, room.getCustomer()==null? 0 : room.getCustomer().getCustomerid());
		ps.setDouble(cnt++, room.getAddPrice());
		ps.setString(cnt++, room.getAddOnsDetails());
		ps.setInt(cnt++, room.getId());
		
		LogU.add(room.getRoomNumber());
		LogU.add(room.getName());
		LogU.add(room.getCustomerName());
		LogU.add(room.getCheckInDateTime());
		LogU.add(room.getCheckOutDateTime());
		LogU.add(room.getDescription());
		LogU.add(room.getPrice());
		LogU.add(room.getCountDays());
		LogU.add(room.getAvailable());
		LogU.add(room.getCustomer()==null? 0 : room.getCustomer().getCustomerid());
		LogU.add(room.getAddPrice());
		LogU.add(room.getAddOnsDetails());
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
		String sql = "UPDATE rooms SET "
				+ "roomNo=?,"
				+ "roomName=?,"
				+ "roomCustomer=?,"
				+ "roomCheckIn=?,"
				+ "roomCheckOut=?,"
				+ "roomDescription=?,"
				+ "roomPrice=?,"
				+ "roomDays=?,"
				+ "roomIsAvailable=?, "
				+ "customerid=?,"
				+ "addPrice=?,"
				+ "addOns=? " 
				+ " WHERE roomId=?";
		
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
		ps.setInt(cnt++, getAvailable());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setDouble(cnt++, getAddPrice());
		ps.setString(cnt++, getAddOnsDetails());
		ps.setInt(cnt++, getId());
		
		LogU.add(getRoomNumber());
		LogU.add(getName());
		LogU.add(getCustomerName());
		LogU.add(getCheckInDateTime());
		LogU.add(getCheckOutDateTime());
		LogU.add(getDescription());
		LogU.add(getPrice());
		LogU.add(getCountDays());
		LogU.add(getAvailable());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getAddPrice());
		LogU.add(getAddOnsDetails());
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
		sql="SELECT roomId FROM rooms  ORDER BY roomId DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("roomId");
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
		ps = conn.prepareStatement("SELECT roomId FROM rooms WHERE roomId=?");
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
		String sql = "UPDATE rooms set roomIsActive=0 WHERE roomId=?";
		
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
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
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
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getAvailableName() {
		return availableName;
	}

	public void setAvailableName(String availableName) {
		this.availableName = availableName;
	}

	public String getAmountRendered() {
		if(amountRendered==null){
			amountRendered = Currency.formatAmount("0.00");
		}else{
			amountRendered = amountRendered.replace(",", "");
			amountRendered = Currency.formatAmount(amountRendered);
		}
		return amountRendered;
	}

	public void setAmountRendered(String amountRendered) {
		this.amountRendered = amountRendered;
	}
	
	public void setAmountRendered(double amountRendered) {
		setAmountRendered(amountRendered+"");
	}

	public double getAddPrice() {
		return addPrice;
	}

	public void setAddPrice(double addPrice) {
		this.addPrice = addPrice;
	}

	public String getAddOnsDetails() {
		return addOnsDetails;
	}

	public void setAddOnsDetails(String addOnsDetails) {
		this.addOnsDetails = addOnsDetails;
	}

	public static void main(String[] args) {
		
		Rooms room = new Rooms();
		room.setRoomNumber("004");
		room.setName("Married House");
		room.setDescription("Good for 2 person");
		room.setPrice(1500.00);
		room.setAvailable(1);
		room.setActive(1);
		room.save();
	}
	
	
}











