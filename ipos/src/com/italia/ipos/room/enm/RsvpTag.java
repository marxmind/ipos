package com.italia.ipos.room.enm;

/**
 * 
 * @author mark italia
 * @since created 09/27/2016
 * @version 1.0
 *
 */
public enum RsvpTag {
	ID("id"),
	RSVP("rsvp"),
	DESC("description"),
	START_DATE("startDate"),
	END_DATE("endDate");
	
	private String name;
	
	
	private RsvpTag(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
}
