package com.italia.ipos.reports;

public enum ReportTag {

	PRODUCT_CODE("productbarcode"),
	ACCOUNTING_SUMMARY("accountingsummary"),
	CHECK_OUT_RECEIPT("checkout"),
	SALES_INVOICE("salesinvoice"),
	CHARGE_INVOICE("chargeinvoice"),
	VCARD("vcard");
	
	private String name;
	
	public String getName(){
		return name;
	}
	
	private ReportTag(String name){
		this.name = name;
	}
	
}
