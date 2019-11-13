package com.italia.ipos.enm;

/**
 * 
 * @author mark italia
 * @since 01/20/2017
 * @version 1.0
 *
 */
public enum ReturnCondition {

	GOOD(1, "GOOD CONDITION"),
	DAMAGED(2,"DAMAGED ITEM");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private ReturnCondition(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(ReturnCondition type : ReturnCondition.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return ReturnCondition.GOOD.getName();
	}
	public static int typeId(String name){
		for(ReturnCondition type : ReturnCondition.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return ReturnCondition.GOOD.getId();
	}
	
}
