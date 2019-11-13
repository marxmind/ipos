package com.italia.ipos.reader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.italia.ipos.application.ClientInfo;
import com.italia.ipos.controller.AddOnStore;
import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.ProductRunning;
import com.italia.ipos.controller.PurchasedItem;
import com.italia.ipos.controller.QtyRunning;
import com.italia.ipos.controller.Transactions;
import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 06-20/2017
 * @version 1.0
 *
 */
public class DispenseReadWriteXML {

	private static final String DISPENSE_DIRECTORY = ReadConfig.value(Ipos.DISPENSE_PRODUCT_FOLDER);
	
	public static void writeDispenseProductInXML(boolean isAutoHold,
											String receiptNo,
											Customer customer, 
											Map<Long, PurchasedItem> productPurchasedData, 
											ProductRunning productRun, 
											List<AddOnStore> addOnStore,
											UserDtls user, double[] money, double itemCount, int termId, String dueDate){
		//Map<Long, PurchasedItem> productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());	
		String dispenseXMLFileName = DISPENSE_DIRECTORY + receiptNo + "-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + ".xml";
		
		//create directory if not existing
		File logdirectory = new File(DISPENSE_DIRECTORY);
        if(!logdirectory.isDirectory()){
        	logdirectory.mkdir();
        }
        
        PrintWriter writer = null; 
        try{
			File file = new File(dispenseXMLFileName);
			writer = new PrintWriter(new FileWriter(file));
			String quote = "\"";
			String newLine = "";
			String tab = "\t";
			String val = "<?xml version="+quote+"1.0"+quote+"?>";
			writer.println(val+newLine);
			writer.println("<purchased>"+newLine);
			writer.println(tab+"<isautohold>" + isAutoHold + "</isautohold>");
			writer.println(tab+"<cash>" + money[0] + "</cash>");
			writer.println(tab+"<totalprice>" + money[1] + "</totalprice>");
			writer.println(tab+"<discount>" + money[2] + "</discount>");
			writer.println(tab+"<balance>" + money[3] + "</balance>");
			writer.println(tab+"<change>" + money[4] + "</change>");
			
			writer.println(tab+"<totaltaxable>" + money[5] + "</totaltaxable>");
			writer.println(tab+"<totalnontaxable>" + money[6] + "</totalnontaxable>");
			writer.println(tab+"<vat>" + money[7] + "</vat>");
			
			writer.println(tab+"<itemcount>" + itemCount + "</itemcount>");
			
			writer.println(tab+"<userlogin>");
				writer.println(tab+tab+"<userdtlsid>"+ user.getUserdtlsid() +"</userdtlsid>");
				writer.println(tab+tab+"<firstname>"+ user.getFirstname() +"</firstname>");
				writer.println(tab+tab+"<middlename>"+ user.getMiddlename() +"</middlename>");
				writer.println(tab+tab+"<lastname>"+ user.getLastname() +"</lastname>");
				writer.println(tab+tab+"<address>"+ user.getAddress() +"</address>");
			writer.println(tab+"</userlogin>");
			/**
			 * Customer
			 */
			writer.println(tab+"<customer>"+newLine);
			writer.println(tab+tab+"<customerid>"+ customer.getCustomerid() +"</customerid>" +newLine);
			writer.println(tab+tab+"<cusfirstname>"+ customer.getFirstname() +"</cusfirstname>" + newLine);
			writer.println(tab+tab+"<cusmiddlename>"+ customer.getMiddlename() +"</cusmiddlename>" + newLine);
			writer.println(tab+tab+"<cuslastname>"+ customer.getLastname() +"</cuslastname>"+ newLine);
			writer.println(tab+tab+"<cusgender>"+ customer.getGender() +"</cusgender>" + newLine);
			writer.println(tab+tab+"<cusage>"+ customer.getAge() +"</cusage>"+ newLine);
			writer.println(tab+tab+"<cusaddress>"+ customer.getAddress() +"</cusaddress>" + newLine);
			writer.println(tab+tab+"<cuscontactno>"+ customer.getContactno() +"</cuscontactno>" + newLine);
			writer.println(tab+tab+"<cusdateregistered>"+ customer.getDateregistered() +"</cusdateregistered>"+ newLine);
			writer.println(tab+tab+"<cuscardno>"+ customer.getCardno() +"</cuscardno>" + newLine);
			writer.println(tab+tab+"<cusisactive>"+ customer.getIsactive() +"</cusisactive>"+ newLine);
			try{writer.println(tab+tab+"<userdtlsid>"+ customer.getUserDtls().getUserdtlsid() +"</userdtlsid>"+ newLine);}catch(Exception e){writer.println(tab+tab+"<userdtlsid>1</userdtlsid>"+ newLine);}
			writer.println(tab+tab+"<fullname>"+ customer.getFullname() +"</fullname>" + newLine);
			try{writer.println(tab+tab+"<bgid>"+ customer.getBarangay().getId() +"</bgid>" + newLine);}catch(Exception e){writer.println(tab+tab+"<bgid>1</bgid>" + newLine);}
			try{writer.println(tab+tab+"<munid>"+ customer.getMunicipality().getId() +"</munid>" + newLine);}catch(Exception e){writer.println(tab+tab+"<munid>1</munid>" + newLine);}
			try{writer.println(tab+tab+"<provid>"+ customer.getProvince().getId() +"</provid>" + newLine);}catch(Exception e){writer.println(tab+tab+"<provid>1</provid>" + newLine);}
			writer.println(tab+"</customer>"+newLine);
			
			/**
			 * Purchased Products
			 */
			if(productPurchasedData!=null && productPurchasedData.size()>0){
			writer.println(tab+"<productselected>");
			for(PurchasedItem item : productPurchasedData.values()){
			if(item.getAddOnStore()==null){
			writer.println(tab+tab+"<purchaseditem>"); 
				writer.println(tab+tab+tab+"<itemid>"+ item.getItemid() +"</itemid>");
				writer.println(tab+tab+tab+"<datesold>"+ DateUtils.getCurrentDateYYYYMMDD() +"</datesold>");
				writer.println(tab+tab+tab+"<productname>"+ item.getProductName() +"</productname>");
				writer.println(tab+tab+tab+"<productbrand>"+ item.getProductProperties().getProductBrand().getProductbrandname() +"</productbrand>");
				writer.println(tab+tab+tab+"<uomSymbol>"+ item.getUomSymbol() +"</uomSymbol>");
				writer.println(tab+tab+tab+"<qty>"+ item.getQty() +"</qty>");
				writer.println(tab+tab+tab+"<purchasedprice>"+ item.getStoreProduct().getPurchasedPrice() +"</purchasedprice>");
				writer.println(tab+tab+tab+"<sellingPrice>"+ item.getStoreProduct().getSellingPrice() +"</sellingPrice>");
				writer.println(tab+tab+tab+"<netprice>"+ item.getStoreProduct().getNetPrice() +"</netprice>");
				writer.println(tab+tab+tab+"<taxpercentage>"+ item.getTaxpercentage() +"</taxpercentage>");
				writer.println(tab+tab+tab+"<isactiveitem>"+ item.getIsactiveitem() +"</isactiveitem>");
				writer.println(tab+tab+tab+"<prodid>"+ item.getProduct().getProdid() +"</prodid>");
				writer.println(tab+tab+tab+"<userdtlsid>"+ user.getUserdtlsid() +"</userdtlsid>");
				writer.println(tab+tab+tab+"<transid>0</transid>");
			writer.println(tab+tab+"</purchaseditem>");
			}
			}
			writer.println(tab+"</productselected>");
			}
			/**
			 * Product Running
			 */
			if(productRun!=null){
				writer.println(tab+"<productrunning>");
				writer.println(tab+tab+"<runid>"+ productRun.getRunid() +"</runid>");
				writer.println(tab+tab+"<rundate>"+ productRun.getRundate() +"</rundate>");
				writer.println(tab+tab+"<clientip>"+ productRun.getClientip() +"</clientip>");
				writer.println(tab+tab+"<clientbrowser>"+ productRun.getClientbrowser() +"</clientbrowser>");
				writer.println(tab+tab+"<runstatus>2</runstatus>");
				writer.println(tab+tab+"<isrunactive>1</isrunactive>");
				writer.println(tab+tab+"<runremarks>DISPENSE</runremarks>");
				writer.println(tab+tab+"<transid>0</transid>");
				writer.println(tab+tab+"<prodid>0</prodid>");
				writer.println(tab+tab+"<userdtlsid>"+ user.getUserdtlsid() +"</userdtlsid>");
				writer.println(tab+tab+"<customerid>"+ customer.getCustomerid() +"</customerid>");
				writer.println(tab+"</productrunning>");
			}else{
				writer.println(tab+"<productrunning>");
				writer.println(tab+tab+"<runid>0</runid>");
				writer.println(tab+tab+"<rundate>"+ DateUtils.getCurrentDateYYYYMMDD() +"</rundate>");
				writer.println(tab+tab+"<clientip>"+ ClientInfo.getClientIP()  +"</clientip>");
				writer.println(tab+tab+"<clientbrowser>"+ ClientInfo.getBrowserName() +"</clientbrowser>");
				writer.println(tab+tab+"<runstatus>2</runstatus>");
				writer.println(tab+tab+"<isrunactive>1</isrunactive>");
				writer.println(tab+tab+"<runremarks>DISPENSE</runremarks>");
				writer.println(tab+tab+"<transid>0</transid>");
				writer.println(tab+tab+"<prodid>0</prodid>");
				writer.println(tab+tab+"<userdtlsid>"+ user.getUserdtlsid() +"</userdtlsid>");
				writer.println(tab+tab+"<customerid>"+ customer.getCustomerid() +"</customerid>");
				writer.println(tab+"</productrunning>");
			}
			
			List<QtyRunning> qtyRunning = Collections.synchronizedList(new ArrayList<QtyRunning>());
			if(productRun!=null){
				ProductRunning runs = new ProductRunning();
				runs.setIsrunactive(1);
				runs.setRunid(productRun.getRunid());
				
				QtyRunning qtyRun = new QtyRunning();
				qtyRun.setIsqtyactive(1);
				
				qtyRunning = QtyRunning.retrieve(qtyRun,runs);
			}
			/**
			 * QtyRunning
			 */
			if(qtyRunning!=null && qtyRunning.size()>0){
			writer.println(tab+"<holditems>");
			for(QtyRunning run : qtyRunning){
				long id = run.getProduct().getProdid();
				if(productPurchasedData.containsKey(id)){
				writer.println(tab+tab+"<qtyrunning>");
					writer.println(tab+tab+tab+"<qtyrunid>"+ run.getQtyrunid() +"</qtyrunid>");
					writer.println(tab+tab+tab+"<qtyrundate>"+ run.getQtyrundate() +"</qtyrundate>");
					writer.println(tab+tab+tab+"<qtyhold>"+ productPurchasedData.get(id).getQty() +"</qtyhold>");
					writer.println(tab+tab+tab+"<qtystatus>"+ run.getQtystatus() +"</qtystatus>");
					writer.println(tab+tab+tab+"<qtyremarks>"+ run.getQtyremarks() +"</qtyremarks>");
					writer.println(tab+tab+tab+"<isqtyactive>"+ run.getIsqtyactive() +"</isqtyactive>");
					writer.println(tab+tab+tab+"<runid>"+ run.getProductRunning().getRunid() +"</runid>");
					writer.println(tab+tab+tab+"<prodid>"+ run.getProduct().getProdid() +"</prodid>");
					writer.println(tab+tab+tab+"<userdtlsid>"+ user.getUserdtlsid() +"</userdtlsid>");
				writer.println(tab+tab+"</qtyrunning>");
				productPurchasedData.remove(run.getProduct().getProdid());
				}
			}
			
			if(productPurchasedData!=null && productPurchasedData.size()>0){
				for(PurchasedItem item : productPurchasedData.values()){
					if(item.getAddOnStore()==null){
						writer.println(tab+tab+"<qtyrunning>");
						writer.println(tab+tab+tab+"<qtyrunid>0</qtyrunid>");
						writer.println(tab+tab+tab+"<qtyrundate>"+ DateUtils.getCurrentDateYYYYMMDD() +"</qtyrundate>");
						writer.println(tab+tab+tab+"<qtyhold>"+ item.getQty() +"</qtyhold>");
						writer.println(tab+tab+tab+"<qtystatus>2</qtystatus>");
						writer.println(tab+tab+tab+"<qtyremarks>DISPENSE</qtyremarks>");
						writer.println(tab+tab+tab+"<isqtyactive>1</isqtyactive>");
						writer.println(tab+tab+tab+"<runid>0</runid>");
						writer.println(tab+tab+tab+"<prodid>"+ item.getProduct().getProdid() +"</prodid>");
						writer.println(tab+tab+tab+"<userdtlsid>"+ user.getUserdtlsid() +"</userdtlsid>");
					writer.println(tab+tab+"</qtyrunning>");
					}
				}
			}
			
			writer.println(tab+"</holditems>");
			}else{
				writer.println(tab+"<holditems>");
				
				for(PurchasedItem item : productPurchasedData.values()){
					if(item.getAddOnStore()==null){
						writer.println(tab+tab+"<qtyrunning>");
						writer.println(tab+tab+tab+"<qtyrunid>0</qtyrunid>");
						writer.println(tab+tab+tab+"<qtyrundate>"+ DateUtils.getCurrentDateYYYYMMDD() +"</qtyrundate>");
						writer.println(tab+tab+tab+"<qtyhold>"+ item.getQty() +"</qtyhold>");
						writer.println(tab+tab+tab+"<qtystatus>2</qtystatus>");
						writer.println(tab+tab+tab+"<qtyremarks>DISPENSE</qtyremarks>");
						writer.println(tab+tab+tab+"<isqtyactive>1</isqtyactive>");
						writer.println(tab+tab+tab+"<runid>0</runid>");
						writer.println(tab+tab+tab+"<prodid>"+ item.getProduct().getProdid() +"</prodid>");
						writer.println(tab+tab+tab+"<userdtlsid>"+ user.getUserdtlsid() +"</userdtlsid>");
					writer.println(tab+tab+"</qtyrunning>");
					}
				}
				
				writer.println(tab+"</holditems>");
			}
			
			/**
			 * Add on Product
			 */
			if(addOnStore!=null && addOnStore.size()>0){
				writer.println(tab+"<xtraproduct>");
				for(AddOnStore store : addOnStore){
				writer.println(tab+tab+"<addonsinstore>");
					writer.println(tab+tab+tab+"<addid>"+ store.getId() +"</addid>");
					writer.println(tab+tab+tab+"<addDate>"+ store.getDateTrans() +"</addDate>");
					writer.println(tab+tab+tab+"<addreceiptno>"+ receiptNo +"</addreceiptno>");
					writer.println(tab+tab+tab+"<addDesc>"+ store.getDescription() +"</addDesc>");
					writer.println(tab+tab+tab+"<addAmount>"+ store.getAmount() +"</addAmount>");
					writer.println(tab+tab+tab+"<addtype>"+ store.getAddOnType() +"</addtype>");
					writer.println(tab+tab+tab+"<addisactive>1</addisactive>");
					writer.println(tab+tab+tab+"<addstatus>2</addstatus>");
					writer.println(tab+tab+tab+"<runid>"+ store.getProductRunning().getRunid() +"</runid>");
				writer.println(tab+tab+"</addonsinstore>");
				}
				writer.println(tab+"</xtraproduct>");
			}
			
			writer.println(tab+"<chargeinvoice>");
			writer.println(tab+tab+"<termid>"+termId+"</termid>");
			writer.println(tab+tab+"<duedate>"+dueDate+"</duedate>");
			writer.println(tab+"</chargeinvoice>");
			
			writer.println("</purchased>");
			writer.flush();
			writer.close();
			
		}catch(IOException e){
			
		}finally{
			
		}
        	
		
		
	}
	
	public static void main(String[] args) {
		
		
		
	}
}
