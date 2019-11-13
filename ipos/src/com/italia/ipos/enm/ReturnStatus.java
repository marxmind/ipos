package com.italia.ipos.enm;

/**
 * 
 * @author mark italia
 * @since 01/20/2017
 * @version 1.0
 *
 */
public enum ReturnStatus {

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
	
	private ReturnStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(ReturnStatus type : ReturnStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return ReturnStatus.NEW.getName();
	}
	public static int typeId(String name){
		for(ReturnStatus type : ReturnStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return ReturnStatus.NEW.getId();
	}
	
}
