package com.italia.ipos.enm;

/**
 * 
 * @author mark italia
 * @since 01/14/2017
 * @version 1.0
 *
 */
public enum PaymentTransactionType {

	
	OTC(1, "OVER THE COUNTER"),
	DELIVERY(2,"DELIVERY"),
	RENTED(3,"RENTED"),
	SUPPLIER(4,"SUPPLIER"),
	RETURN(5,"RETURN ITEM"),
	CASH_LOAN(6, "CASH LOAN"),
	STORE(7, "STORE");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private PaymentTransactionType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(PaymentTransactionType type : PaymentTransactionType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return PaymentTransactionType.OTC.getName();
	}
	public static int typeId(String name){
		for(PaymentTransactionType type : PaymentTransactionType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return PaymentTransactionType.OTC.getId();
	}
	
}
