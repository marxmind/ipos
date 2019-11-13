package com.italia.ipos.controller;

import java.math.BigDecimal;
import java.util.List;

import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 01/23/2017
 * @version 1.0
 *
 */
public class Payment {

	public static void customerPayment(Customer customer, double balance, double paidAmount, PaymentTransactionType type, String remarks, String receiptNo){
		Customer cus = new Customer();
		cus.setCustomerid(customer.getCustomerid());
		cus.setIsactive(1);
		CustomerPayment py = new CustomerPayment();
		py.setPayisactive(1);
		List<CustomerPayment> payments = CustomerPayment.retrieve(py,cus);
		CustomerPayment payment = new CustomerPayment();
		
		//double bal = Double.valueOf(getBalanceamnt()+"");
		BigDecimal baltmp = new BigDecimal(Math.abs(balance));
		
		/**
		 * check if there are history for this customer
		 */
		if(payments.size()>0){
			payment = payments.get(0);
			
		CustomerPaymentTrans trans = new CustomerPaymentTrans();
		trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
		trans.setAmountpay(new BigDecimal(paidAmount+""));
		trans.setIspaid(1);
		trans.setPaytransisactive(1);
		trans.setCustomerPayment(payment);
		trans.setUserDtls(Login.getUserLogin().getUserDtls());
		trans.setPaymentType(type.getId());
		trans.setRemarks(remarks);
		if(receiptNo!=null && !receiptNo.isEmpty()){
			trans.setReceiptNo(receiptNo);
		}
		trans.save();
		
		//set temp data
		BigDecimal balamnt = payment.getAmountbalance();
		BigDecimal amntpaidorig = payment.getAmountpaid();
		String paiddate = payment.getAmountpaiddate();
		
		
		//BigDecimal addBalamnt =  balamnt.add(baltmp);
		BigDecimal newBalamnt =  balamnt.add(baltmp);  //addBalamnt.subtract(new BigDecimal(Currency.removeComma(getAmountin())));
		
		//Update customerpayment table
		payment.setAmountpaid(new BigDecimal(paidAmount+""));
		payment.setAmountpaiddate(DateUtils.getCurrentDateYYYYMMDD());
		
		payment.setAmountprevpaid(amntpaidorig);
		payment.setAmountprevpaiddate(paiddate);
		
		payment.setAmountbalance(newBalamnt);
		payment.setAmountprevbalance(balamnt);
		payment.save();
		
		}else{ 
		
			/**
			 * Customer's first data 
			 */
			
			payment = new CustomerPayment();
			payment.setAmountpaid(new BigDecimal(paidAmount+""));
			payment.setAmountpaiddate(DateUtils.getCurrentDateYYYYMMDD());
			payment.setAmountprevpaid(new BigDecimal("0"));
			payment.setAmountprevpaiddate(DateUtils.getCurrentDateYYYYMMDD());
			payment.setAmountbalance(baltmp);
			payment.setAmountprevbalance(new BigDecimal("0"));
			payment.setCustomer(customer);
			payment.setPayisactive(1);
			payment.setUserDtls(Login.getUserLogin().getUserDtls());
			payment = CustomerPayment.save(payment);
			
			
			CustomerPaymentTrans trans = new CustomerPaymentTrans();
			trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
			trans.setAmountpay(new BigDecimal(paidAmount+""));
			trans.setIspaid(1);
			trans.setPaytransisactive(1);
			trans.setCustomerPayment(payment);
			trans.setUserDtls(Login.getUserLogin().getUserDtls());
			trans.setPaymentType(type.getId());
			trans.setRemarks(remarks);
			if(receiptNo!=null && !receiptNo.isEmpty()){
				trans.setReceiptNo(receiptNo);
			}
			trans.save();
			
		}
		
	}
	
}
