package com.italia.ipos.enm;

public enum ProductStatus {

	VOID(0,"VOID"),
	ON_QUEUE(1,"ON QUEUE"),
	DISPENSE(2,"DISPENSE");
	
	private ProductStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	
}
