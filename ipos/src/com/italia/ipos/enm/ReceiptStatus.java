package com.italia.ipos.enm;

public enum ReceiptStatus {

	NEW(1, "NEW"),
	POSTED(2,"POSTED"),
	PARTIAL(3, "PARTIAL"),
	FULL(4, "FULL"),
	CANCELLED(5,"CANCELLED");
	
	private String name;
	private int id;
	
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	private ReceiptStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static int statusId(String name){
		for(ReceiptStatus s : ReceiptStatus.values()){
			if(name.equalsIgnoreCase(s.getName())){
				return s.getId();
			}
		}
		return ReceiptStatus.PARTIAL.getId();
	}
	
	public static String statusName(int id){
		for(ReceiptStatus s : ReceiptStatus.values()){
			if(id == s.getId()){
				return s.getName();
			}
		}
		return ReceiptStatus.PARTIAL.getName();
	}
	
}
