package com.italia.ipos.enm;

public enum DeliveryStatus {

	NEW_FOR_DELIVERY(1, "NEW"),
	POSTED_FOR_DELIVERY(2, "POSTED"),
	NEW_SOLD_ITEM(3, "NEW"),
	POSTED_SOLD_ITEM(4, "POSTED"),
	NEW_DAMAGED_ITEM(5, "NEW"),
	POSTED_DAMAGED_ITEM(6, "POSTED"),
	NEW_RETURN_DELIVERY_ITEM(7, "NEW"),
	POSTED_RETURN_DELIVERY_ITEM(8, "POSTED"),
	NEW_RETURN_CUSTOMER_ITEM(9, "NEW"),
	POSTED_RETURN_CUSTOMER_ITEM(10, "POSTED"),
	NEW_RENTED_ITEM(11, "NEW"),
	POSTED_RENTED_ITEM(12, "POSTED");
	
	private int id;
	private String name;
	
	private DeliveryStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static int statusId(String name){
		for(DeliveryStatus s : DeliveryStatus.values()){
			if(name.equalsIgnoreCase(s.getName())){
				return s.getId();
			}
		}
		return 1;
	}
	
	public static String statusName(int id){
		
		for(DeliveryStatus s : DeliveryStatus.values()){
			if(id==s.getId()){
				return s.getName();
			}
		}
		
		/*switch(id){
		case 1 : return DeliveryStatus.FOR_DELIVERY.getName();
		case 2 : return DeliveryStatus.SOLD_ITEM.getName();
		case 3 : return DeliveryStatus.DAMAGED_ITEM.getName();
		case 4 : return DeliveryStatus.RETURN_DELIVERY_ITEM.getName();
		case 5 : return DeliveryStatus.RETURN_CUSTOMER_ITEM.getName();
		case 6 : return DeliveryStatus.RENTED_ITEM.getName();
		}*/
		return "";
	}
	
	public int getId() {
		return id;
	}	public String getName() {
		return name;
	}	
	
}
