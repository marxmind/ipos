package com.italia.ipos.room.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.Login;
import com.italia.ipos.room.controller.Rooms;
import com.italia.ipos.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 04/12/2017
 * @version 1.0
 *
 */

@ManagedBean(name="roomBean", eager=true)
@ViewScoped
public class RoomMaintenanceBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 678654463354561L;

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
	
	private String searchRoom;
	
	private List<Rooms> rooms = Collections.synchronizedList(new ArrayList<Rooms>());
	private Rooms roomSelectedData;
	
	@PostConstruct
	public void init(){
		
		if(Login.checkUserStatus()){
		
		rooms = Collections.synchronizedList(new ArrayList<Rooms>());
		String sql = "SELECT * FROM rooms WHERE roomIsActive=1 ";
		String[] params = new String[0];
		if(getSearchRoom()!=null && !getSearchRoom().isEmpty()){
			String room = Whitelist.remove(getSearchRoom());
			sql +=" AND ( roomNo like '%"+ room +"%' OR roomName like '%"+ room +"%' ) ";
		}
		sql += " ORDER BY roomNo"; 
		rooms = Rooms.retrieve(sql, params);
		
		}
		
	}
	
	public void clearFields(){
		setRoomSelectedData(null);
		setRoomNumber(null);
		setName(null);
		setDescription(null);
		setPrice(0.00);
	}
	
	public void saveRoom(){
		if(Login.checkUserStatus()){
			
			Rooms room = new Rooms();
			if(getRoomSelectedData()!=null){
				room = getRoomSelectedData();
			}
			room.setRoomNumber(getRoomNumber());
			room.setName(getName());
			room.setDescription(getDescription());
			room.setPrice(getPrice());
			room.setActive(1);
			room.setAvailable(0);
			room.save();
			
			clearFields();
			init();
			
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Room information has been successfully saved.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}else{
			/*FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage("Successful",  "Your message: " + message) );*/
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void clickRoom(Rooms room){
		setRoomSelectedData(room);
		setRoomNumber(room.getRoomNumber());
		setName(room.getName());
		setDescription(room.getDescription());
		setPrice(room.getPrice());
	}
	
	public void deleteRoom(Rooms room){
		room.delete();
		init();
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully deleted", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
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

	public List<Rooms> getRooms() {
		return rooms;
	}

	public void setRooms(List<Rooms> rooms) {
		this.rooms = rooms;
	}

	public String getSearchRoom() {
		return searchRoom;
	}

	public void setSearchRoom(String searchRoom) {
		this.searchRoom = searchRoom;
	}

	public Rooms getRoomSelectedData() {
		return roomSelectedData;
	}

	public void setRoomSelectedData(Rooms roomSelectedData) {
		this.roomSelectedData = roomSelectedData;
	}
	
}

