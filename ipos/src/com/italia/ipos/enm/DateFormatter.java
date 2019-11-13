package com.italia.ipos.enm;

public enum DateFormatter {

	YYYY_MM_DD(1, "yyyy-MM-dd"),
	MM_DD_YYYY(2,"MM-dd-yyyy");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private DateFormatter(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(DateFormatter type : DateFormatter.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return DateFormatter.YYYY_MM_DD.getName();
	}
	public static int typeId(String name){
		for(DateFormatter type : DateFormatter.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return DateFormatter.YYYY_MM_DD.getId();
	}
	
}
