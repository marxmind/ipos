package com.italia.ipos.controller;

/**
 * 
 * @author mark italia
 * @since 01/12/2017
 * @version 1.0
 *
 */
public class Incomes {
	
	private int id;
	private String dateTrans;
	private String description;
	private String transactionType;
	private String price;
	private double vat;
	private double quantity;
	private String uom;
	private String priceWihtVAT;
	private String priceWihtOutVAT;
	private String capitalAmount;
	private String saleAmount;
	private String netAmount;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDateTrans() {
		return dateTrans;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getPriceWihtVAT() {
		return priceWihtVAT;
	}
	public void setPriceWihtVAT(String priceWihtVAT) {
		this.priceWihtVAT = priceWihtVAT;
	}
	public String getPriceWihtOutVAT() {
		return priceWihtOutVAT;
	}
	public void setPriceWihtOutVAT(String priceWihtOutVAT) {
		this.priceWihtOutVAT = priceWihtOutVAT;
	}
	public String getCapitalAmount() {
		return capitalAmount;
	}
	public void setCapitalAmount(String capitalAmount) {
		this.capitalAmount = capitalAmount;
	}
	public String getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(String saleAmount) {
		this.saleAmount = saleAmount;
	}
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public double getVat() {
		return vat;
	}
	public void setVat(double vat) {
		this.vat = vat;
	}
	
}
