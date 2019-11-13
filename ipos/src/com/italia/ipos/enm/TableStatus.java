package com.italia.ipos.enm;

public enum TableStatus {

	AVAILABLE(1, "AVAILABLE"),
	OCCUPIED(2, "OCCUPIED");
	
	private int id;
	private String name;
	
	private TableStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static int statusId(String name){
		for(TableStatus s : TableStatus.values()){
			if(name.equalsIgnoreCase(s.getName())){
				return s.getId();
			}
		}
		return 1;
	}
	
	public static String statusName(int id){
		
		for(TableStatus s : TableStatus.values()){
			if(id==s.getId()){
				return s.getName();
			}
		}
		return "";
	}
	
	public int getId() {
		return id;
	}	public String getName() {
		return name;
	}	
	
}
