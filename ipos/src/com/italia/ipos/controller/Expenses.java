package com.italia.ipos.controller;

/**
 * 
 * @author mark italia
 * @since 01/13/2017
 * @version 1.0
 *
 */
public class Expenses {

	private int id;
	private String dateTrans;
	private String description;
	private String transactionType;
	private String amount;
	private String remarks;
	private String htmlPage;
	private String actionName;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getHtmlPage() {
		return htmlPage;
	}
	public void setHtmlPage(String htmlPage) {
		this.htmlPage = htmlPage;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
}
