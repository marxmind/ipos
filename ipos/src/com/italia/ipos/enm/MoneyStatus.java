package com.italia.ipos.enm;

public enum MoneyStatus {
	
	//please be careful when adding field here
	//affected class AccountingBean
	//like startExpenses method
	INCOME(1, "INCOME"),
	EXPENSES(2,"EXPENSES"),
	ALLOWANCES(3, "ALLOWANCES"),
	CASH_LOAN(4, "CASH LOAN"),
	OTHER_INCOME(5, "OTHER INCOME"),
	ADD_CAPITAL(6, "ADDITIONAL CAPITAL"),
	OTHER_EXP(7, "OTHER EXPENSES"),
	OTC(8, "OVER THE COUNTER"),
	RENTED(9, "RENTED ITEM"),
	REFUND(10, "REFUND"),
	SALARY(11, "SALARY"),
	RETURN_CAPITAL(12, "RETURN CAPITAL");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private MoneyStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(MoneyStatus type : MoneyStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return MoneyStatus.INCOME.getName();
	}
	public static int typeId(String name){
		for(MoneyStatus type : MoneyStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return MoneyStatus.INCOME.getId();
	}
	
}
