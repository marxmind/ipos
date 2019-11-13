package com.italia.ipos.utils;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Currency {
	
	public static void main(String[] args) {
		
		String amount = "1,968.00";
		double pay = Double.valueOf(removeComma(amount));
		System.out.println(pay);
		
	}
	
	public static String removeCurrencySymbol(String value, String replaceChr){
		String[] symbols = {"Php","php","PHP","$"};
		if(value==null) return "";
		for(String symbol : symbols){
			value = value.replace(symbol, replaceChr);
		}
		
		return value;
	}
	
	public static String removeComma(String value){
		
		if(value==null) return "0";
		if(value.isEmpty()) return "0";
		try{
			value = value.replace(",", "");
			value = value.replace("$", "");
			value = value.replace("Php", "");
			value = value.replace("\\u20B1", "");	
		NumberFormat format = NumberFormat.getCurrencyInstance();
		Number number = format.parse(value);
		return number.toString();
		}catch(ParseException e) {}
			
		return value;
		
	}
	
	public static String formatAmount(BigDecimal amount){
		return formatAmount(amount+"");
	}
	
	public static String formatAmount(double amount){
		return formatAmount(amount+"");
	}
	
	public static String formatAmount(String amount){
		
		if(amount==null) return "0";
		if(amount.isEmpty()) return "0";
		try{
		amount = amount.replace(",", "");
		amount = amount.replace("$", "");
		amount = amount.replace("Php", "");
		amount = amount.replace("\\u20B1", "");
		double money = Double.valueOf(amount.replace(",", ""));
		NumberFormat format = NumberFormat.getNumberInstance();
		amount = format.format(money).replace("$", "");
		amount = amount.replace("Php", "");
		}catch(Exception e){
			
		}
		return amount;
		
		/*try{
		NumberFormat format = NumberFormat.getCurrencyInstance();
		DecimalFormatSymbols dcFormat = ((DecimalFormat) format).getDecimalFormatSymbols();
		dcFormat.setCurrencySymbol("");
		((DecimalFormat)format).setDecimalFormatSymbols(dcFormat);
		amount = format.format(amount);
		Number number = format.parse(amount);
		amount = format.format(number.toString());
		return amount;
		}catch(Exception e){
			
		}
		return amount;*/
	}
	
	public static double amountDouble(String amount){
		if(amount == null) return 0d;
		if(amount.isEmpty()) return 0d;
		double amnt = 0d;
		amount = formatAmount(amount);	
		amnt = Double.valueOf(amount);
		return amnt;
	}
}
