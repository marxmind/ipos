package com.italia.sheedz.pos;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.italia.ipos.application.ClientInfo;
import com.italia.ipos.controller.AddOnStore;
import com.italia.ipos.controller.Barangay;
import com.italia.ipos.controller.ChargeInvoice;
import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.MoneyIO;
import com.italia.ipos.controller.Municipality;
import com.italia.ipos.controller.Payment;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.ProductRunning;
import com.italia.ipos.controller.Province;
import com.italia.ipos.controller.PurchasedItem;
import com.italia.ipos.controller.QtyRunning;
import com.italia.ipos.controller.Receipt;
import com.italia.ipos.controller.StoreProduct;
import com.italia.ipos.controller.Transactions;
import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.enm.HistoryReceiptStatus;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.MoneyStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.ProductStatus;
import com.italia.ipos.enm.ReceiptStatus;
import com.italia.ipos.enm.Status;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;



public class DispenseXML {

	public static String APP_DISPINESE_FOLDER = ReadConfig.value(Ipos.DISPENSE_PRODUCT_FOLDER);
	
	public static void main(String[] args) {
		
		LogU.add("initialize xml fetching.....");
		System.out.println("initialize.....");
		
		File folder = new File(APP_DISPINESE_FOLDER);
		File[] listOfFiles = folder.listFiles();
		
		for(File file : folder.listFiles()){
			if(file.isFile()){
				int start = file.getName().lastIndexOf(".");
				String ext = file.getName().substring(start, start+4);
				if(".xml".equalsIgnoreCase(ext)){
					System.out.println("Extracting " + file.getName());
					DispenseXML.loadXMLDB(file);
					System.out.println("deleting " + file.delete());
				}
			}
		}
		
		
		//DispenseXML.readXML("2017-06-21-0000000912.xml");
		LogU.add("completed xml fetching.....");
		System.out.println("completed");
	}
	
	private static void loadXMLDB(File fileName){
		
		boolean isAutoHold = false;
		Customer customer = new Customer();
		List<PurchasedItem> purchased = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		ProductRunning prodRunning = new ProductRunning();
		List<QtyRunning> qtyRunning = Collections.synchronizedList(new ArrayList<QtyRunning>());
		List<AddOnStore> xtraProduct = Collections.synchronizedList(new ArrayList<AddOnStore>());
		UserDtls userlogin = new UserDtls();
		double itemCount = 0;
		double[] money = new double[8];
		String locationXMLFile = APP_DISPINESE_FOLDER + fileName.getName();
		int termId = 1;
		String dueDate = DateUtils.getCurrentDateYYYYMMDD();		
		
		try{
			File xmlFile = new File(locationXMLFile);
			//String result="";
			if(xmlFile.exists()){
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(xmlFile);
			
			/////////////normalize
			doc.getDocumentElement().normalize();
			//System.out.println("Reading conf......");
			
			NodeList ls = doc.getElementsByTagName("purchased");
			
			int size=ls.getLength();
			for(int i=0; i<size; i++){
			Node n = ls.item(i);
			
			
			if(n.getNodeType() == Node.ELEMENT_NODE){
			Element e = (Element)n;
				isAutoHold = e.getElementsByTagName("isautohold").item(0).getTextContent().equalsIgnoreCase("false")? false : true;
				money[0] = Double.valueOf(e.getElementsByTagName("cash").item(0).getTextContent());
				money[1] = Double.valueOf(e.getElementsByTagName("totalprice").item(0).getTextContent());
				money[2] = Double.valueOf(e.getElementsByTagName("discount").item(0).getTextContent());
				money[3] = Double.valueOf(e.getElementsByTagName("balance").item(0).getTextContent());
				money[4] = Double.valueOf(e.getElementsByTagName("change").item(0).getTextContent());
				money[5] = Double.valueOf(e.getElementsByTagName("totaltaxable").item(0).getTextContent());
				money[6] = Double.valueOf(e.getElementsByTagName("totalnontaxable").item(0).getTextContent());
				money[7] = Double.valueOf(e.getElementsByTagName("vat").item(0).getTextContent());
				
				itemCount = Double.valueOf(e.getElementsByTagName("itemcount").item(0).getTextContent());
				
				/**
				 * User Login
				 */
				NodeList luser = e.getElementsByTagName("userlogin");
				for(int lu=0; lu<luser.getLength(); lu++){
					Node ln = luser.item(lu);
					if(ln.getNodeType() == Node.ELEMENT_NODE){
						Element le = (Element)ln;
						long id = Long.valueOf(le.getElementsByTagName("userdtlsid").item(0).getTextContent());
						userlogin.setUserdtlsid(id);
						userlogin.setFirstname(le.getElementsByTagName("firstname").item(0).getTextContent());
						userlogin.setMiddlename(le.getElementsByTagName("middlename").item(0).getTextContent());
						userlogin.setLastname(le.getElementsByTagName("lastname").item(0).getTextContent());
						userlogin.setAddress(le.getElementsByTagName("address").item(0).getTextContent());
					}
				}
				
				
				/**
				 * Customer
				 */
				NodeList lcustomer = e.getElementsByTagName("customer");
				for(int c=0; c<size; c++){
					Node cn = lcustomer.item(c);
					
					if(cn.getNodeType() == Node.ELEMENT_NODE){
						Element ce = (Element)cn;
						long id = Long.valueOf(ce.getElementsByTagName("customerid").item(0).getTextContent());
						customer.setCustomerid(id);
						customer.setFirstname(ce.getElementsByTagName("cusfirstname").item(0).getTextContent());
						customer.setMiddlename(ce.getElementsByTagName("cusmiddlename").item(0).getTextContent());
						customer.setLastname(ce.getElementsByTagName("cuslastname").item(0).getTextContent());
						customer.setGender(ce.getElementsByTagName("cusgender").item(0).getTextContent());
						int age = Integer.valueOf(ce.getElementsByTagName("cusage").item(0).getTextContent());
						customer.setAge(age);
						customer.setAddress(ce.getElementsByTagName("cusaddress").item(0).getTextContent());
						customer.setContactno(ce.getElementsByTagName("cuscontactno").item(0).getTextContent());
						customer.setDateregistered(ce.getElementsByTagName("cusdateregistered").item(0).getTextContent());
						customer.setCardno(ce.getElementsByTagName("cuscardno").item(0).getTextContent());
						int isActive = Integer.valueOf(ce.getElementsByTagName("cusisactive").item(0).getTextContent());
						customer.setIsactive(isActive);
						UserDtls userDtls = new UserDtls();
						long userId = Long.valueOf(ce.getElementsByTagName("userdtlsid").item(0).getTextContent());
						userDtls.setUserdtlsid(userId);
						customer.setUserDtls(userDtls);
						customer.setFullname(ce.getElementsByTagName("fullname").item(0).getTextContent());
						Barangay barangay = new Barangay();
						int bId = Integer.valueOf(ce.getElementsByTagName("bgid").item(0).getTextContent());
						barangay.setId(bId);
						customer.setBarangay(barangay);
						Municipality municipality = new Municipality();
						int mId = Integer.valueOf(ce.getElementsByTagName("munid").item(0).getTextContent());
						municipality.setId(mId);
						customer.setMunicipality(municipality);
						Province province = new Province();
						int pid = Integer.valueOf(ce.getElementsByTagName("provid").item(0).getTextContent());
						province.setId(pid);
						customer.setProvince(province);
					}
					
				}
				
				/**
				 * Products
				 */
				NodeList lproducts = e.getElementsByTagName("productselected");
				for(int p=0; p<lproducts.getLength(); p++){
					Node pn = lproducts.item(p);
					
					if(pn.getNodeType() == Node.ELEMENT_NODE){
						Element pe = (Element)pn;
						
						NodeList litems = pe.getElementsByTagName("purchaseditem");
						for(int pi=0; pi<litems.getLength(); pi++){
							Node ppn = litems.item(pi);
							if(ppn.getNodeType() == Node.ELEMENT_NODE){
								Element ppe = (Element)ppn;
								
								PurchasedItem pItem = new PurchasedItem();
								long pid = Long.valueOf(ppe.getElementsByTagName("itemid").item(0).getTextContent());
								System.out.println("Product.... item id " + pid);
								if(pid!=0){
									pItem.setItemid(pid);
								}
								pItem.setDatesold(ppe.getElementsByTagName("datesold").item(0).getTextContent());
								
								Product product = Product.retrieve(ppe.getElementsByTagName("prodid").item(0).getTextContent());
								ProductProperties properties = ProductProperties.properties(product.getProductProperties().getPropid()+"");
								String sql = " AND store.prodid=? AND store.propid=? AND store.qty!=0 AND store.prodIsActive=1 ";
								String[] params = new String[2];
								params[0] = product.getProdid()+"";
								params[1] = properties.getPropid()+"";
								StoreProduct store = StoreProduct.retrieve(sql,params).get(0);
								pItem.setStoreProduct(store);
								
								pItem.setProductName(ppe.getElementsByTagName("productname").item(0).getTextContent());
								pItem.setProductbrand(properties.getProductBrand().getProductbrandname());
								pItem.setProduct(product);
								pItem.setProductProperties(properties);
								double qty = Double.valueOf(ppe.getElementsByTagName("qty").item(0).getTextContent());
								pItem.setQty(qty);
								pItem.setUomSymbol(ppe.getElementsByTagName("uomSymbol").item(0).getTextContent());
								
								try{pItem.setPurchasedprice(new BigDecimal(ppe.getElementsByTagName("purchasedprice").item(0).getTextContent()));}catch(Exception ex){pItem.setPurchasedprice(new BigDecimal("0.00"));}
								try{pItem.setSellingPrice(new BigDecimal(ppe.getElementsByTagName("sellingPrice").item(0).getTextContent()));}catch(Exception ex){pItem.setSellingPrice(new BigDecimal("0.00"));}
								try{pItem.setNetprice(new BigDecimal(ppe.getElementsByTagName("netprice").item(0).getTextContent()));}catch(Exception ex){pItem.setNetprice(new BigDecimal("0.00"));}
								
								pItem.setTaxpercentage(0);
								pItem.setIsactiveitem(1);
								//UserDtls userDtls = new UserDtls();
								//long uId = Long.valueOf(ppe.getElementsByTagName("userdtlsid").item(0).getTextContent());
								//userDtls.setUserdtlsid(uId);
								pItem.setUserDtls(userlogin);
								
								purchased.add(pItem);
							}
						}
						
					}
				}	
			
				
				/**
				 * Product Running
				 */
				NodeList lrunning = e.getElementsByTagName("productrunning");
				for(int run=0; run<lrunning.getLength(); run++){
					Node runn = lrunning.item(run);
					
					if(runn.getNodeType() == Node.ELEMENT_NODE){
						Element rune = (Element)runn;
						
						long pId = Long.valueOf(rune.getElementsByTagName("runid").item(0).getTextContent());
						if(pId!=0){
							prodRunning.setRunid(pId);
						}
						prodRunning.setRundate(rune.getElementsByTagName("rundate").item(0).getTextContent());
						prodRunning.setClientip(rune.getElementsByTagName("clientip").item(0).getTextContent());
						prodRunning.setClientbrowser(rune.getElementsByTagName("clientbrowser").item(0).getTextContent());
						prodRunning.setIsrunactive(1);
						prodRunning.setRunstatus(2);
						prodRunning.setRunremarks(rune.getElementsByTagName("runremarks").item(0).getTextContent());
						
						//UserDtls userDtls = new UserDtls();
						//long uId = Long.valueOf(rune.getElementsByTagName("userdtlsid").item(0).getTextContent());
						//userDtls.setUserdtlsid(uId);
						prodRunning.setUserDtls(userlogin);
						
						prodRunning.setCustomer(customer);
						
					}	
				}
				
				/**
				 * Hold Items
				 */
				NodeList lholds = e.getElementsByTagName("holditems");
				for(int hold=0; hold<lholds.getLength(); hold++){
					Node holdn = lholds.item(hold);
					
					if(holdn.getNodeType() == Node.ELEMENT_NODE){
						Element holde = (Element)holdn;
						
						NodeList lholditems = holde.getElementsByTagName("qtyrunning");
						for(int holdagain=0; holdagain<lholditems.getLength(); holdagain++){
							Node holdagainn = lholditems.item(holdagain);
							
							if(holdagainn.getNodeType() == Node.ELEMENT_NODE){
								Element holdagaine = (Element)holdagainn;
								
								QtyRunning run = new QtyRunning();
								
								long runId = Long.valueOf(holdagaine.getElementsByTagName("qtyrunid").item(0).getTextContent());
								
								if(runId!=0){
									run.setQtyrunid(runId);
								}
								run.setQtyrundate(holdagaine.getElementsByTagName("qtyrundate").item(0).getTextContent());
								double holdQty = Double.valueOf(holdagaine.getElementsByTagName("qtyhold").item(0).getTextContent());
								run.setQtyhold(holdQty);
								run.setQtystatus(2);
								run.setIsqtyactive(1);
								run.setQtyremarks(holdagaine.getElementsByTagName("qtyremarks").item(0).getTextContent());
								Product product = new Product();
								long prodid = Long.valueOf(holdagaine.getElementsByTagName("prodid").item(0).getTextContent());
								product.setProdid(prodid);
								run.setProduct(product);
								
								//UserDtls userDtls = new UserDtls();
								//long uId = Long.valueOf(holdagaine.getElementsByTagName("userdtlsid").item(0).getTextContent());
								//userDtls.setUserdtlsid(uId);
								run.setUserDtls(userlogin);
								
								qtyRunning.add(run);
							}
							
						}
						
						
						
					}
				}
				
				/**
				 * Xtra Product
				 */
				NodeList lxtras = e.getElementsByTagName("xtraproduct");
				for(int xtra=0; xtra<lxtras.getLength(); xtra++){
					Node xtran = lxtras.item(xtra);
					
					if(xtran.getNodeType() == Node.ELEMENT_NODE){
						Element xtrane = (Element)xtran;
						
						NodeList lstore = xtrane.getElementsByTagName("addonsinstore");
						for(int xt=0; xt<lstore.getLength(); xt++){
							Node xtn = lstore.item(xt);
							
							if(xtn.getNodeType() == Node.ELEMENT_NODE){
								Element xte = (Element)xtn;
								
								AddOnStore addStore = new AddOnStore();
								long id = Long.valueOf(xte.getElementsByTagName("addid").item(0).getTextContent());
								addStore.setId(id);
								addStore.setDateTrans(xte.getElementsByTagName("addDate").item(0).getTextContent());
								addStore.setDescription(xte.getElementsByTagName("addDesc").item(0).getTextContent());
								addStore.setReceiptNo(xte.getElementsByTagName("addreceiptno").item(0).getTextContent());
								double amount = Double.valueOf(xte.getElementsByTagName("addAmount").item(0).getTextContent());
								addStore.setAmount(amount);
								int type = Integer.valueOf(xte.getElementsByTagName("addtype").item(0).getTextContent());
								addStore.setAddOnType(type);
								addStore.setIsActive(1);
								addStore.setStatus(2);
								
								xtraProduct.add(addStore);
							}	
							
						}
					}	
					
				}	
				
				/**
				 *  Charge Invoice
				 */
				NodeList charge = e.getElementsByTagName("chargeinvoice");
				for(int xt=0; xt<charge.getLength(); xt++){
					Node xtn = charge.item(xt);
					
					if(xtn.getNodeType() == Node.ELEMENT_NODE){
						Element xte = (Element)xtn;
						termId = Integer.valueOf(xte.getElementsByTagName("termid").item(0).getTextContent());
						dueDate = xte.getElementsByTagName("duedate").item(0).getTextContent();
					}	
				}
				
				
			}
			
			}
			
			
			if(isAutoHold){
				DispenseXML.prepareToLoadDB(customer, purchased, prodRunning, qtyRunning, xtraProduct,money,userlogin, itemCount, termId, dueDate);
			}else{
				DispenseXML.prepareToLoadDBFirstTime(customer, purchased, prodRunning, qtyRunning, xtraProduct, money,userlogin, itemCount, termId, dueDate);
			}
			
			System.out.println("completed reading conf......");
			
			}else{
			System.out.println("File is not exist...");
			}
			
			}catch(Exception e){
				e.printStackTrace();
			}
			
		
	}
	
	private static boolean saveNewReceiptOR(String receiptNo){
		try{
		String sql = "SELECT * FROM receiptmanagement WHERE isactivated=1 AND recIsActive!=0";
		Receipt rpt = Receipt.retrieve(sql, new String[0]).get(0);
		long newOR = Long.valueOf(receiptNo);
		long prevOR = rpt.getLatestOR();
		rpt.setLatestOR(newOR);
		rpt.setPreviousOR(prevOR);
		rpt.save();
		return true;
		}catch(Exception e){return false;}
	}
	
	private static void prepareToLoadDB( 
			Customer customer,
			List<PurchasedItem> purchased, 
			ProductRunning prodRunning, 
			List<QtyRunning> qtyRunning, 
			List<AddOnStore> xtraProduct,
			double[] money, UserDtls user, double itemCount, int termId, String dueDate){
		
		String receiptNo = DeliveryItemReceipt.generateNewReceiptNo();
		//receiptNo = DateUtils.getCurrentDateYYYYMMDD() + "-" + receiptNo;
		if(saveNewReceiptOR(receiptNo)){
			//receiptNo = DateUtils.getCurrentDateYYYYMMDD() + " " + receiptNo;
			Transactions trans = new Transactions();
			trans = addTransactions(customer,receiptNo,money, user);
			prodRunning.setTransactions(trans);
			
			addPurchasedItem(trans,purchased,user);
			
			saveToReceipt(itemCount, trans, receiptNo, user);
		
			saveMoneyIO(trans, user);
			
			purchasedItemStatus(trans, prodRunning);
			
			saveAddOns(trans, prodRunning, xtraProduct);
			
			if(trans.getAmountbal().doubleValue()>=1) {
				saveChargeInvoice(trans, customer, receiptNo, termId, dueDate);
			}
		}
	}
	
	private static void prepareToLoadDBFirstTime( 
										Customer customer,
										List<PurchasedItem> purchased, 
										ProductRunning prodRunning, 
										List<QtyRunning> qtyRunning, 
										List<AddOnStore> xtraProduct,
										double[] money, UserDtls user, double itemCount, int termId, String dueDate){
		
		String receiptNo = DeliveryItemReceipt.generateNewReceiptNo();
		//receiptNo = DateUtils.getCurrentDateYYYYMMDD() + "-" + receiptNo;
		if(saveNewReceiptOR(receiptNo)){
				//receiptNo = DateUtils.getCurrentDateYYYYMMDD() + " " + receiptNo;
				Transactions trans = new Transactions();
				trans = addTransactions(customer,receiptNo,money, user);
				prodRunning.setTransactions(trans);
				
				addPurchasedItemForFirstTime(customer, trans, prodRunning, purchased, user);
				
				saveToReceipt(itemCount, trans, receiptNo, user);
				
				saveMoneyIO(trans, user);
				
				purchasedItemStatusFirstTime(trans, prodRunning);
				
				saveAddOns(trans, prodRunning, xtraProduct);
				
				if(trans.getAmountbal().doubleValue()>=1) {
					saveChargeInvoice(trans, customer, receiptNo, termId, dueDate);
				}
		}		
	}
	
	
	private static Transactions addTransactions(Customer customer, String receiptNo, double[] money, UserDtls user){
		Transactions trans = new Transactions();
		
		trans.setReceipts(receiptNo);
		
		
			/**
			 * Money[0] cash
			 * Money[1] totalpurchased
			 * Money[2] discount
			 * Money[3] balance
			 * Money[4] change
			 * Money[5] totaltaxable
			 * Money[6] totalNontaxable
			 * Money[7] vat	
			 */	
			
		trans.setTransdate(DateUtils.getCurrentDateYYYYMMDD());
		trans.setCustomer(customer);
		trans.setAmountpurchased(new BigDecimal(money[1]));
		
		trans.setAmountreceived(new BigDecimal(money[0]));
		double amountIn = 0d;
		try{amountIn = money[0];}catch(Exception e){}
		if(amountIn==0){
			trans.setAmountbal(trans.getAmountpurchased());
		}else{
			trans.setAmountbal(new BigDecimal(Math.abs(money[3])));
		}
		
		trans.setAmountchange(new BigDecimal(money[4]));
		
		trans.setIsvoidtrans(1);
		//trans.setUserDtls(Login.getUserLogin().getUserDtls());
		trans.setUserDtls(user);
		
		BigDecimal amnt = new BigDecimal("0"); 
		try{amnt = new BigDecimal(money[2]);}catch(NumberFormatException num){}
		trans.setDiscount(amnt);
		
		//vat
		trans.setVatsales(new BigDecimal(money[5])); //total taxable
		trans.setVatexmptsales(new BigDecimal("0"));
		trans.setZeroratedsales(new BigDecimal("0"));
		trans.setVatnet(new BigDecimal(money[6])); //total non taxable
		trans.setVatamnt(new BigDecimal(money[7])); //vat
		
		if(trans.getAmountbal().doubleValue()>0 && trans.getAmountreceived().doubleValue()==0) {
			trans.setPaymentType(HistoryReceiptStatus.UNPAID.getId());
		}else if(trans.getAmountbal().doubleValue()==0) {
			trans.setPaymentType(HistoryReceiptStatus.FULLPAID.getId());
		}else if(trans.getAmountreceived().doubleValue()>0 && trans.getAmountbal().doubleValue()>0) {
			trans.setPaymentType(HistoryReceiptStatus.PARTIALPAID.getId());
		}
		
		trans = Transactions.save(trans);
		return trans;
	}
	
	private static List<PurchasedItem> addPurchasedItem(Transactions trans,List<PurchasedItem> purchased, UserDtls user){
		List<PurchasedItem> items = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		
		if(purchased!=null && purchased.size()>0){
			
			for(PurchasedItem item : purchased){
				
				if(item.getAddOnStore()==null){
				ProductProperties prod = item.getProductProperties();
				item.setDatesold(DateUtils.getCurrentDateYYYYMMDD());
				item.setProductName(prod.getProductname());
				item.setUomSymbol(prod.getUom().getSymbol());
				item.setProductbrand(prod.getProductBrand().getProductbrandname());
				//qty already defined in orders
				
				//this data to be use for sales and income
				/*ProductPricingTrans price = ProductPricingTrans.retrievePrice(item.getProduct().getProdid()+"");
				item.setPurchasedprice(price.getPurchasedprice());
				item.setSellingPrice(price.getSellingprice());
				item.setNetprice(price.getNetprice());
				item.setTaxpercentage(price.getTaxpercentage());*/
				
				item.setPurchasedprice(new BigDecimal(item.getStoreProduct().getPurchasedPrice()));
				item.setSellingPrice(new BigDecimal(item.getStoreProduct().getSellingPrice()));
				item.setNetprice(new BigDecimal(item.getStoreProduct().getNetPrice()));
				item.setTaxpercentage(0);
				
				item.setIsactiveitem(1);
				//Product already defined in orders
				item.setTransactions(trans);
				//item.setUserDtls(Login.getUserLogin().getUserDtls());
				item.setUserDtls(user);
				
				item = PurchasedItem.save(item);
				items.add(item);
				}
			}
			
		}
		
		return items;
	}
	
	private static void saveChargeInvoice(Transactions trans, Customer customer, String recieptNo, int termId, String dueDate) {
		ChargeInvoice in = new ChargeInvoice();
		in.setIsActive(1);
		in.setTerms(termId);
		in.setDueDate(dueDate);
		in.setReceiptNo(recieptNo);
		in.setBalanceAmount(trans.getAmountbal().doubleValue());
		in.setTransactions(trans);
		in.setCustomer(customer);
		in.save();
	}
	
	private static void saveToReceipt(double itemCount, Transactions trans, String receiptNo, UserDtls user){
		DeliveryItemReceipt rec = new DeliveryItemReceipt();
		
		rec.setReceiptNo(receiptNo);
		rec.setRemarks("Normal transactions");
		
		
		rec.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		rec.setStatus(ReceiptStatus.POSTED.getId());
		rec.setIsActive(1);
		
		rec.setDeliveryChargeAmount(0);
		
		
		rec.setTotalAmount(trans.getAmountpurchased().doubleValue());
		rec.setBalanceAmount(trans.getAmountbal().doubleValue());
		rec.setDiscountAmount(trans.getDiscount().doubleValue());
		rec.setQuantity(itemCount);
		rec.setDownPayment(trans.getAmountreceived().doubleValue());
		
		//check payment
		if(trans.getAmountbal().doubleValue()==0){
			rec.setPaymentStatus(ReceiptStatus.FULL.getId());
		}else{
			rec.setPaymentStatus(ReceiptStatus.PARTIAL.getId());
		}
		
		rec.setCustomer(trans.getCustomer());
		
		//rec.setUserDtls(Login.getUserLogin().getUserDtls());
		rec.setUserDtls(user);
		
		rec.save();
		
	}
	
private static void saveMoneyIO(Transactions trans, UserDtls user){
		
		System.out.println("save money checking customer " + trans.getCustomer().getFullname() + " id " + trans.getCustomer().getCustomerid());
		
		if(trans.getAmountbal().doubleValue()==0){
			
			double amount = 0d;
			if(trans.getAmountreceived().doubleValue()>trans.getAmountpurchased().doubleValue()){
				amount = trans.getAmountpurchased().doubleValue();
				amount -= trans.getDiscount().doubleValue();
			}else{
				amount = trans.getAmountreceived().doubleValue();
			}
			
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			io.setDescripion("Full payment in Store. Paid by " + trans.getCustomer().getFullname());
			io.setTransType(MoneyStatus.INCOME.getId());
			io.setInAmount(amount);
			//io.setUserDtls(Login.getUserLogin().getUserDtls());
			io.setUserDtls(user);
			io.setReceiptNo(trans.getReceipts());
			io.setCustomer(trans.getCustomer());
			
			MoneyIO.save(io);
			
			Payment.customerPayment(trans.getCustomer(), trans.getAmountbal().doubleValue(), amount, PaymentTransactionType.STORE, "Full payment. Paid by "+ trans.getCustomer().getFullname(), trans.getReceipts());
			
		}else{
			
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			io.setTransType(MoneyStatus.INCOME.getId());
			//io.setUserDtls(Login.getUserLogin().getUserDtls());
			io.setUserDtls(user);
			
			//saving information to customer payment
			if(trans.getAmountreceived().doubleValue()==0){
				Payment.customerPayment(trans.getCustomer(),  trans.getAmountbal().doubleValue(), trans.getAmountreceived().doubleValue(),PaymentTransactionType.STORE, "No downpayment in Store. Payable amount " + trans.getAmountbal().doubleValue(),trans.getReceipts());
				
				io.setDescripion("No downpayment in store. Payable by " + trans.getCustomer().getFullname() + ". Payable amount " + trans.getAmountbal().doubleValue());
				io.setInAmount(0.00);
				io.setReceiptNo(trans.getReceipts());
				io.setCustomer(trans.getCustomer());
				
				MoneyIO.save(io);
			}else{
				Payment.customerPayment(trans.getCustomer(), trans.getAmountbal().doubleValue(), trans.getAmountreceived().doubleValue(),PaymentTransactionType.STORE, "Partial downpayment in Store. Payable amount " + trans.getAmountbal().doubleValue(),trans.getReceipts());
				io.setDescripion("Partial downpayment in store. Paid by " + trans.getCustomer().getFullname() + ". Payable amount " + trans.getAmountbal().doubleValue());
				io.setInAmount(trans.getAmountreceived().doubleValue());
				io.setReceiptNo(trans.getReceipts());
				io.setCustomer(trans.getCustomer());
				
				MoneyIO.save(io);
			}
			
			
		}
		
	}

private static void purchasedItemStatus(Transactions trans, ProductRunning prodRun){
	//ProductRunning prodRun = getProductRunning();
	prodRun.setRunstatus(ProductStatus.DISPENSE.getId());
	prodRun.setRunremarks(ProductStatus.DISPENSE.getName());
	prodRun.setTransactions(trans);
	prodRun.save();
	
	ProductRunning pRun = new ProductRunning();
	pRun.setRunid(prodRun.getRunid());
	pRun.setIsrunactive(1);
	
	for(QtyRunning run : QtyRunning.retrieve(pRun)){
		run.setQtystatus(ProductStatus.DISPENSE.getId());
		run.setQtyremarks(ProductStatus.DISPENSE.getName());
		run.save();
	}
}

private static void saveAddOns(Transactions trans, ProductRunning prodRun, List<AddOnStore> xtras){
	
	if(xtras!=null && xtras.size()>0){
	
		ProductRunning run = new ProductRunning();
		run.setRunid(prodRun.getRunid());
		run.setIsrunactive(1);
		List<AddOnStore> ons = AddOnStore.retrieve(run);
		for(AddOnStore on : ons){
			on.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			on.setReceiptNo(trans.getReceipts());
			on.setStatus(Status.POSTED.getId());
			on.save();
		}
	
	}
}

private static ProductRunning addRunningProduct(Customer customer, ProductRunning running, UserDtls user){
	ProductRunning productrun = new ProductRunning();
	if(running!=null){
		productrun = running;
		try{productrun.setCustomer(customer);
		ProductRunning.save(productrun);}catch(Exception e){}
	}else{
		productrun.setRundate(DateUtils.getCurrentDateYYYYMMDD());
		productrun.setIsrunactive(1);
		productrun.setClientip(ClientInfo.getClientIP());
		productrun.setClientbrowser(ClientInfo.getBrowserName());
		productrun.setRunstatus(ProductStatus.ON_QUEUE.getId());
		productrun.setRunremarks(ProductStatus.ON_QUEUE.getName());
		//productrun.setUserDtls(Login.getUserLogin().getUserDtls());
		productrun.setUserDtls(user);
		try{
			Customer cus = customer;
			if(customer==null){
				cus = new Customer();
				cus.setCustomerid(1);
			}
			productrun.setCustomer(customer);}catch(Exception e){}
		productrun = ProductRunning.save(productrun);
		
	}
	return productrun;
}

private static List<PurchasedItem> addPurchasedItemForFirstTime(Customer customer, Transactions trans,ProductRunning prodRunning, List<PurchasedItem> orders, UserDtls user){
	List<PurchasedItem> items = Collections.synchronizedList(new ArrayList<PurchasedItem>());
	
	if(orders!=null && orders.size()>0){
		
		ProductRunning productrun = new ProductRunning();
		productrun = addRunningProduct(customer, prodRunning, user);
		
		QtyRunning runQty = new QtyRunning();
		runQty.setIsqtyactive(1);
		runQty.setQtystatus(ProductStatus.ON_QUEUE.getId());
		
		ProductRunning prodRun = new ProductRunning();
		prodRun.setRunid(productrun.getRunid());
		prodRun.setIsrunactive(1);
		
		Map<Long, PurchasedItem> purchasedData = Collections.synchronizedMap(new HashMap<Long, PurchasedItem>());
		for(PurchasedItem item : orders){
			purchasedData.put(item.getProduct().getProdid(), item);
		}
		
		for(QtyRunning itmQty : QtyRunning.retrieve(runQty,prodRun)){
			//getQtyrunning().put(itmQty.getProduct().getProdid(), itmQty);
			
			long id = itmQty.getProduct().getProdid();
			if(purchasedData.containsKey(id)){
				
				PurchasedItem item = purchasedData.get(id);
				
				if(item.getAddOnStore()==null){
					
					ProductProperties prod = item.getProductProperties();
					item.setDatesold(DateUtils.getCurrentDateYYYYMMDD());
					item.setProductName(prod.getProductname());
					item.setUomSymbol(prod.getUom().getSymbol());
					item.setProductbrand(prod.getProductBrand().getProductbrandname());
					
					item.setPurchasedprice(new BigDecimal(item.getStoreProduct().getPurchasedPrice()));
					item.setSellingPrice(new BigDecimal(item.getStoreProduct().getSellingPrice()));
					item.setNetprice(new BigDecimal(item.getStoreProduct().getNetPrice()));
					item.setTaxpercentage(0);
					
					item.setIsactiveitem(1);
					item.setTransactions(trans);
					//item.setUserDtls(Login.getUserLogin().getUserDtls());
					item.setUserDtls(user);
					
					item = PurchasedItem.save(item);
					items.add(item);
					
					//record qty removed in store
					//StoreProduct.storeQuantity(true, item.getProduct(), itmQty.getQtyhold());
					//StoreProduct.storeQuantity(false, item.getProduct(), item.getQty());
					StoreProduct.storeQuantityRecallReplace(itmQty.getQtyhold(), item.getQty(), item.getProduct());
					
					//change the status of hold data to dispense
					itmQty.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
					itmQty.setQtyhold(item.getQty());
					itmQty.setIsqtyactive(1);
					itmQty.setQtystatus(ProductStatus.DISPENSE.getId());
					itmQty.setQtyremarks(ProductStatus.DISPENSE.getName());
					itmQty.setProductRunning(productrun);
					itmQty.setProduct(item.getProduct());
					//itmQty.setUserDtls(Login.getUserLogin().getUserDtls());
					itmQty.setUserDtls(user);
					itmQty = QtyRunning.save(itmQty);
					
					purchasedData.remove(id); //removing data that already save// this will be use for faster iteration on below codes
				}	
				
			}
			
		}
		
		//additional product not yet save in on hold
		Map<Long,QtyRunning> qtyRunning =  Collections.synchronizedMap(new HashMap<Long,QtyRunning>());
		for(PurchasedItem item : purchasedData.values()){
			if(item.getAddOnStore()==null){
				
				ProductProperties prod = item.getProductProperties();
				item.setDatesold(DateUtils.getCurrentDateYYYYMMDD());
				item.setProductName(prod.getProductname());
				item.setUomSymbol(prod.getUom().getSymbol());
				item.setProductbrand(prod.getProductBrand().getProductbrandname());
				
				item.setPurchasedprice(new BigDecimal(item.getStoreProduct().getPurchasedPrice()));
				item.setSellingPrice(new BigDecimal(item.getStoreProduct().getSellingPrice()));
				item.setNetprice(new BigDecimal(item.getStoreProduct().getNetPrice()));
				item.setTaxpercentage(0);
				
				item.setIsactiveitem(1);
				item.setTransactions(trans);
				//item.setUserDtls(Login.getUserLogin().getUserDtls());
				item.setUserDtls(user);
				
				item = PurchasedItem.save(item);
				items.add(item);
				
				
				StoreProduct.storeQuantity(false, item.getProduct(), item.getQty());
				
				Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunningDispense(qtyRunning,item.getProduct().getProdid(), productrun, item.getProduct(), item.getQty());
				
				
				}
		}
		
	}
	
	return items;
}

private static void purchasedItemStatusFirstTime(Transactions trans, ProductRunning productRunning){
	ProductRunning prodRun = productRunning;
	prodRun.setRunstatus(ProductStatus.DISPENSE.getId());
	prodRun.setRunremarks(ProductStatus.DISPENSE.getName());
	prodRun.setTransactions(trans);
	prodRun.save();
}

	
}





















