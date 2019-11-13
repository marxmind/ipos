package com.italia.ipos.enm;
/**
 * 
 * @author mark italia
 * @since 01/17/2017
 * @version 1.0
 *
 */
public enum RentedStatus {

	NEW(1, "NEW"),
	POSTED(2,"POSTED");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private RentedStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(RentedStatus type : RentedStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return RentedStatus.NEW.getName();
	}
	public static int typeId(String name){
		for(RentedStatus type : RentedStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return RentedStatus.NEW.getId();
	}
	
}

