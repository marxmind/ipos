package com.italia.ipos.enm;
/**
 * 
 * @author mark italia
 * @since 01/22/2017
 * @version 1.0
 *
 */
public enum Status {

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
	
	private Status(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(Status type : Status.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return Status.NEW.getName();
	}
	public static int typeId(String name){
		for(Status type : Status.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return Status.NEW.getId();
	}
	
}
