package com.italia.ipos.bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.italia.ipos.application.ClientInfo;
import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.CustomerPayment;
import com.italia.ipos.controller.CustomerPaymentTrans;
import com.italia.ipos.controller.DeliveryItem;
import com.italia.ipos.controller.DeliveryItemTrans;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.Payment;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductInventory;
import com.italia.ipos.controller.ProductPricingTrans;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.ProductRunning;
import com.italia.ipos.controller.PurchasedItem;
import com.italia.ipos.controller.QtyRunning;
import com.italia.ipos.controller.Transactions;
import com.italia.ipos.enm.DeliveryStatus;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.ProductStatus;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.utils.Currency;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 02/27/2017
 * @version 1.0
 */
@ManagedBean(name="receiptBean")
@ViewScoped
public class ReceiptRecording implements Serializable{
	
private static final long serialVersionUID = 1094809025228384363L;
	
	private String searchCode;
	private String keyPress;
	private String searchProduct;
	private List<DeliveryItem> items = Collections.synchronizedList(new ArrayList<DeliveryItem>());
	private double selectedQty;
	private Map<Long, DeliveryItem> selectedProduct = Collections.synchronizedMap(new HashMap<Long, DeliveryItem>());
	
	
	public static void main(String[] args) {
		
		StringBuffer str = new StringBuffer();
		str.append("mark italia\n");
		str.append("sunrise\n");
		
		for(String s : str.toString().split("\n")){
			System.out.println("print : " + s);
		}
		
	}
	
	public static void saveToFileReceipt(String receiptInfo, String receiptNumber){
		
		//check log directory
		String receiptLocation = ReadConfig.value(Ipos.RECEIPTS_LOG);
		String receiptFile = receiptLocation + receiptNumber + ".txt";
        File logdirectory = new File(receiptLocation);
        if(!logdirectory.isDirectory()){
        	logdirectory.mkdir();
        }
		
        //BufferedWriter writer = null;
        PrintWriter writer = null; 
        try{
			File file = new File(receiptFile);
			writer = new PrintWriter(new FileWriter(file));
			for(String str : receiptInfo.toString().split("\n")){
				writer.println(str);
			}
			
			writer.flush();
			writer.close();
			
		}catch(IOException e){
			
		}finally{
			
		}
        
	}
	
	public static String viewReceipt(String receiptNumber){
		
		String receiptLocation = ReadConfig.value(Ipos.RECEIPTS_LOG);
		String receiptFile = receiptLocation + receiptNumber + ".txt";
		try{
		BufferedReader br = new BufferedReader(new FileReader(receiptFile));
		
		 	String line = null;
	        // Read from the original file and write to the new
		 	StringBuffer str = new StringBuffer();
		 	//str.append("\t****REPRINT****\n");
	        while ((line = br.readLine()) != null) {
	        	str.append(line + "\n");
	        }
	        br.close();
	        return str.toString();
		}catch(Exception e){}
		
		return null;
	}
	
	public void loadProduct(){
		
		ProductProperties prop = new ProductProperties();
		prop.setIsactive(1);
		prop.setProductname(getSearchProduct().replace("--", ""));
		
		Product prod = new Product();
		prod.setIsactiveproduct(1);
		
		items = Collections.synchronizedList(new ArrayList<DeliveryItem>());
		for(Product prd : Product.retrieve(prod, prop)){
			
			Product pd = new Product();
			pd.setIsactiveproduct(1);
			pd.setProdid(prd.getProdid());
			
			DeliveryItem item = new DeliveryItem();
			item.setIsActive(1);
			item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
			item.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			List<DeliveryItem> delItem = DeliveryItem.retrieve(item,pd);
			double qty = 0d;
			if(delItem.size()>0){
				qty = productActualQtyRemaining(prd);
				if(qty>0){
					prd.setProductProperties(ProductProperties.properties(prd.getProductProperties().getPropid()+""));
					delItem.get(0).setProduct(prd);
					delItem.get(0).setQuantity(qty);
					items.add(delItem.get(0));
				}
			}
		}
		
		
	}
	
	private double productActualQtyRemaining(Product prod){
		
		DeliveryItem item = new DeliveryItem();
		item.setIsActive(1);
		item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
		item.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		
		Product prd = new Product();
		prd.setIsactiveproduct(1);
		prd.setProdid(prod.getProdid());
		double delQty = 0d;
		double soldQty = 0d;
		double newQty = 0d;
		for(DeliveryItem itm : DeliveryItem.retrieve(item,prd)){
			delQty += itm.getQuantity();
		}
		
		DeliveryItemTrans trans = new DeliveryItemTrans();
		//trans.setDeliveryItem(item);
		trans.setProduct(item.getProduct());
		trans.setDateTrans(item.getDateTrans());
		trans.setIsActive(1);
		for(DeliveryItemTrans tran : DeliveryItemTrans.retrieve(trans)){
			soldQty += tran.getQuantity();
		}
		
		newQty = delQty - soldQty;
		
		return newQty;
	}
	
	public void search(){
		
	}

	public void keyButton(){
		
	}
	
	 public String getKeyPress() {
		keyPress="amountId";
		System.out.println("im press....");
		return keyPress;
	}
	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}
	
	public String getSearchCode() {
		return searchCode;
	}

	public void setSearchCode(String searchCode) {
		this.searchCode = searchCode;
	}

	public List<DeliveryItem> getItems() {
		return items;
	}

	public void setItems(List<DeliveryItem> items) {
		this.items = items;
	}



	public String getSearchProduct() {
		return searchProduct;
	}



	public void setSearchProduct(String searchProduct) {
		this.searchProduct = searchProduct;
	}

	public Map<Long, DeliveryItem> getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(Map<Long, DeliveryItem> selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public double getSelectedQty() {
		return selectedQty;
	}

	public void setSelectedQty(double selectedQty) {
		this.selectedQty = selectedQty;
	}
}

