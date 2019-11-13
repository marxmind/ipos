package com.italia.ipos.room.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.ipos.controller.Login;
import com.italia.ipos.room.controller.RoomHistory;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 04/12/2017
 * @version 1.0
 *
 */
@ManagedBean(name="historyBean", eager=true)
@ViewScoped
public class HistoryRoomBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 423476868671L;

	private String searchRoom;
	private List<RoomHistory> rooms = Collections.synchronizedList(new ArrayList<RoomHistory>());
	
	private Date calendarFrom;
	private Date calendarTo;
	
	@PostConstruct
	public void init(){
		
		rooms = Collections.synchronizedList(new ArrayList<RoomHistory>());
		
		if(Login.checkUserStatus()){
		
		String sql = "SELECT * FROM roomhistory WHERE roomIsActive=1 AND (roomDateTrans>=? AND roomDateTrans<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(getSearchRoom()!=null && !getSearchRoom().isEmpty()){
			String room = Whitelist.remove(getSearchRoom());
			sql +=" AND ( roomNo like '%"+ room +"%' OR roomName like '%"+ room +"%' OR roomCustomer like '%"+ room +"%') ";
			sql += " ORDER BY roomDateTrans";
		}else{
			sql += " ORDER BY roomDateTrans";
		}
		
		rooms = RoomHistory.retrieve(sql, params);
		Collections.reverse(rooms);
		
		}
		
	}
	
	public String getSearchRoom() {
		return searchRoom;
	}
	public void setSearchRoom(String searchRoom) {
		this.searchRoom = searchRoom;
	}
	public List<RoomHistory> getRooms() {
		return rooms;
	}
	public void setRooms(List<RoomHistory> rooms) {
		this.rooms = rooms;
	}
	
	public Date getCalendarFrom() {
		if(calendarFrom==null){
			calendarFrom = DateUtils.getDateToday();
		}
		return calendarFrom;
	}

	public void setCalendarFrom(Date calendarFrom) {
		this.calendarFrom = calendarFrom;
	}

	public Date getCalendarTo() {
		if(calendarTo==null){
			calendarTo = DateUtils.getDateToday();
		}
		return calendarTo;
	}

	public void setCalendarTo(Date calendarTo) {
		this.calendarTo = calendarTo;
	}
	
}

