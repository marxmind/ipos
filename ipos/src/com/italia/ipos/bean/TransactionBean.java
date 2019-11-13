package com.italia.ipos.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.Transactions;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Numbers;

/**
 * 
 * @author mark italia
 * @since 06/24/2017
 * @version 1.0
 *
 */
@ManagedBean(name="transBean", eager=true)
@ViewScoped
public class TransactionBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 67895685648541L;
	
	private String dateFrom;
	private String dateTo;
	private String receiptSearch;
	private List<Transactions> trans = Collections.synchronizedList(new ArrayList<Transactions>());
	
	private double totalPurchased;
	private double totalDiscount;
	private double totalTaxable;
	private double totalNonTaxable;
	private double totalVat;
	
	
	@PostConstruct
	public void init(){
		trans = Collections.synchronizedList(new ArrayList<Transactions>());
		String sql = " AND (tran.transdate>=? AND tran.transdate<=?) ";
		String[] params = new String[2];
		params[0] = getDateFrom();
		params[1] = getDateTo();
		
		if(getReceiptSearch()!=null && !getReceiptSearch().isEmpty()){
			if(getReceiptSearch().contains("-")){
				sql += " AND tran.transreceipt like '%" + getReceiptSearch()+"%'";
			}else{
				sql += " AND cus.fullname like '%" + getReceiptSearch()+"%'";
			}
		}
		totalPurchased = 0;
		totalDiscount = 0;
		totalTaxable = 0;
		totalNonTaxable = 0;
		totalVat = 0;
		//trans = Transactions.retrieve(sql, params);
		for(Transactions tran : Transactions.retrieve(sql, params)){
			if(tran.getAmountpurchased().doubleValue()>0){
			totalPurchased += tran.getAmountpurchased().doubleValue();
			}
			totalDiscount += tran.getDiscount().doubleValue();
			totalTaxable += tran.getVatsales().doubleValue();
			totalNonTaxable += tran.getVatnet().doubleValue();
			totalVat += tran.getVatamnt().doubleValue();
			trans.add(tran);
		}
		if(trans!=null && trans.size()>0){
			Transactions tran = new Transactions();
			Customer cus = new Customer();
			cus.setCustomerid(0);
			cus.setFullname("GRAND TOTAL");
			//tran.setTransdate("Grand Total:");
			tran.setCustomer(cus);
			tran.setAmountpurchased(new BigDecimal(Numbers.formatDouble(totalPurchased)+""));
			tran.setDiscount(new BigDecimal(Numbers.formatDouble(totalDiscount)+""));
			tran.setVatsales(new BigDecimal(Numbers.formatDouble(totalTaxable)+""));
			tran.setVatnet(new BigDecimal(Numbers.formatDouble(totalNonTaxable)+""));
			tran.setVatamnt(new BigDecimal(Numbers.formatDouble(totalVat)+""));
			trans.add(tran);
		}
		
		
	}
	
	
	public String getDateFrom() {
		if(dateFrom==null){
			dateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		if(dateTo==null){
			dateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
	public String getReceiptSearch() {
		return receiptSearch;
	}
	public void setReceiptSearch(String receiptSearch) {
		this.receiptSearch = receiptSearch;
	}
	public List<Transactions> getTrans() {
		return trans;
	}
	public void setTrans(List<Transactions> trans) {
		this.trans = trans;
	}


	public double getTotalPurchased() {
		return totalPurchased;
	}


	public void setTotalPurchased(double totalPurchased) {
		this.totalPurchased = totalPurchased;
	}


	public double getTotalDiscount() {
		return totalDiscount;
	}


	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}


	public double getTotalTaxable() {
		return totalTaxable;
	}


	public void setTotalTaxable(double totalTaxable) {
		this.totalTaxable = totalTaxable;
	}


	public double getTotalNonTaxable() {
		return totalNonTaxable;
	}


	public void setTotalNonTaxable(double totalNonTaxable) {
		this.totalNonTaxable = totalNonTaxable;
	}


	public double getTotalVat() {
		return totalVat;
	}


	public void setTotalVat(double totalVat) {
		this.totalVat = totalVat;
	}

}
