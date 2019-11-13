package com.italia.ipos.enm;

/**
 * 
 * @author mark italia
 * @since 06/11/2017
 * @version 1.0
 *
 */
public enum HistoryReceiptStatus {

	UNPAID(1, "UNPAID"),
	PARTIALPAID(2,"PARTIAL PAID"),
	FULLPAID(3,"FULL PAID"),
	VOID(4,"VOID");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private HistoryReceiptStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(HistoryReceiptStatus type : HistoryReceiptStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return HistoryReceiptStatus.UNPAID.getName();
	}
	public static int typeId(String name){
		for(HistoryReceiptStatus type : HistoryReceiptStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return HistoryReceiptStatus.UNPAID.getId();
	}
	
}
