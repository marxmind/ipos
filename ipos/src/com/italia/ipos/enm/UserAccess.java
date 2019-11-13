package com.italia.ipos.enm;

public enum UserAccess {

	DEVELOPER(1, "Developer"),
	OWNER(2, "Owner"),
	MANAGER(3, "Manager"),
	CLERK(4, "Clerk"),
	Cashier(5, "Cashier"),
	SALES_AGENT(6, "Sales Agent");
	
	private int id;
	private String name;
	
	private UserAccess(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static int statusId(String name){
		for(UserAccess s : UserAccess.values()){
			if(name.equalsIgnoreCase(s.getName())){
				return s.getId();
			}
		}
		return 1;
	}
	
	public static String statusName(int id){
		
		for(UserAccess s : UserAccess.values()){
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
