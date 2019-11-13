package com.italia.ipos.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

import com.italia.ipos.controller.AddOnStore;
import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.CustomerPayment;
import com.italia.ipos.controller.CustomerPaymentTrans;
import com.italia.ipos.controller.DeliveryItem;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.DeliveryItemTrans;
import com.italia.ipos.controller.Expenses;
import com.italia.ipos.controller.Incomes;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.MoneyIO;
import com.italia.ipos.controller.Payable;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductInventory;
import com.italia.ipos.controller.ProductPricingTrans;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.ProductReturnSupplier;
import com.italia.ipos.controller.PurchasedItem;
import com.italia.ipos.controller.Receivable;
import com.italia.ipos.controller.RentedBottle;
import com.italia.ipos.controller.Reports;
import com.italia.ipos.controller.StoreProduct;
import com.italia.ipos.controller.Summary;
import com.italia.ipos.controller.Supplier;
import com.italia.ipos.controller.SupplierPayment;
import com.italia.ipos.controller.SupplierTrans;
import com.italia.ipos.controller.Xtras;
import com.italia.ipos.enm.DateFormatter;
import com.italia.ipos.enm.DeliveryStatus;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.MoneyStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.ReceiptStatus;
import com.italia.ipos.enm.ReturnStatus;
import com.italia.ipos.enm.Status;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.reports.ReadXML;
import com.italia.ipos.reports.ReportCompiler;
import com.italia.ipos.reports.ReportTag;
import com.italia.ipos.utils.Currency;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Whitelist;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author mark italia
 * @since 11/08/2016
 * @version 1.0
 */
@ManagedBean(name="accntBean")
@ViewScoped
public class AccountingBean implements Serializable {
	
	private static final long serialVersionUID = 1098801825428384363L;
	
	private List<PurchasedItem> items = Collections.synchronizedList(new ArrayList<PurchasedItem>());
	private Date incomesDateFrom;
	private Date incomesDateTo;
	private String incomesProductName;
	private String incomesPlusVatTotal;
	private String incomesMinususVatTotal;
	private String capitalTotal;
	private String salesTotal;
	private String netTotal;
	private List<Incomes> soldItems = Collections.synchronizedList(new ArrayList<Incomes>());
	
	private List<DeliveryItemReceipt> receipts = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
	private Date receiptDateFrom;
	private Date receiptDateTo;
	private String customerName;
	private String priceTotal;
	private double vatTotal;
	private double qtyTotal;
	private String purchasedTotal;
	private String balanceTotal;
	private String discountTotal;
	private String downpaymentTotal;
	private String chargeTotal;
	
	private List<CustomerPaymentTrans> cashs = Collections.synchronizedList(new ArrayList<CustomerPaymentTrans>());
	private Date cashDateFrom;
	private Date cashDateTo;
	private String cashName;
	private String cashTotal;
	
	private List<Expenses> expenses = Collections.synchronizedList(new ArrayList<Expenses>());
	private String description;
	private Date expenseDateFrom;
	private Date expenseDateTo;
	private String expensesTotal;
	
	private List<Payable> payables = Collections.synchronizedList(new ArrayList<Payable>());
	private String payDescription;
	private Date payDateFrom;
	private Date payDateTo;
	private String payTotal;
	
	private List<Receivable> receivables = Collections.synchronizedList(new ArrayList<Receivable>());
	private String recDescription;
	private String recDateFrom;
	private String recDateTo;
	private String recTotal;
	
	private List<MoneyIO> ios = Collections.synchronizedList(new ArrayList<MoneyIO>());
	private String ioDescription;
	private Date ioDateFrom;
	private Date ioDateTo;
	private String ioExpenses;
	private String ioIncome;
	private String cashOnHandAmount;
	
	private Date dateFromSummary;
	private Date dateToSummary;
	private int transTypeId;
	private List tranTypes;
	private List<Summary> summaryData = Collections.synchronizedList(new ArrayList<>());
	private double totalSummaryQty;
	private String totalSummaryItemPrice;
	private String totalSummaryCapital;
	private String totalSummarySale;
	private String totalSummaryNet;
	private int summaryFilterId;
	private List summaryFilters;
	private List<ProductInventory> warehouseProduct = Collections.synchronizedList(new ArrayList<ProductInventory>());
	private double warehouseTotalQty;
	private List<DeliveryItem> returnItems = Collections.synchronizedList(new ArrayList<DeliveryItem>());
	private String returnProductItemGrandTotal;
	private String retrunProductItemPriceGrandTotal;
	private double returnProductItemQtyGrandTotal;
	
	private List<StoreProduct> stores = Collections.synchronizedList(new ArrayList<StoreProduct>());
	
	public void onTabChangeView(TabChangeEvent event) {
        /*FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);*/
		
		if("Incomes".equalsIgnoreCase(event.getTab().getTitle())){
			startOnHand();
		}else if("Expenses".equalsIgnoreCase(event.getTab().getTitle())){
			startExpenses();
		}else if("Receivable".equalsIgnoreCase(event.getTab().getTitle())){
			startReceivable();
		}else if("Payable".equalsIgnoreCase(event.getTab().getTitle())){
			startPayable();
		}else if("Summary Report".equalsIgnoreCase(event.getTab().getTitle())){
			startSummary();
		}
    }
	
	public void onTabChange(TabChangeEvent event) {
        /*FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);*/
		if("Cash On Hand".equalsIgnoreCase(event.getTab().getTitle())){
			startOnHand();
		}else if("Cash Collection".equalsIgnoreCase(event.getTab().getTitle())){
			startCash();
		}else if("Receipts".equalsIgnoreCase(event.getTab().getTitle())){
			startReceipts();
		}else if("Sold Items".equalsIgnoreCase(event.getTab().getTitle())){
			startIncomes();
		}
		
    }
         
    public void onTabClose(TabCloseEvent event) {
        /*FacesMessage msg = new FacesMessage("Tab Closed", "Closed tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);*/
    }
	
    public void startOnHand(){
    	ios = Collections.synchronizedList(new ArrayList<MoneyIO>());
    	String sql = "SELECT * FROM iomoney WHERE iodatetrans>=? AND iodatetrans<=? ";
    	String[] params = new String[2];
    	params[0] = DateUtils.convertDate(getIoDateFrom(),DateFormatter.YYYY_MM_DD.getName());
    	params[1] = DateUtils.convertDate(getIoDateTo(),DateFormatter.YYYY_MM_DD.getName());
    	if(getIoDescription()!=null && !getIoDescription().isEmpty()){
    		sql += " AND iodescription like '%" + getIoDescription().replace("--", "") + "%'";
    	}
    	double in=0d, out=0d,cashOnHandAmount=0d;
    	for(MoneyIO io : MoneyIO.retrieve(sql, params)){
    		if(io.getInAmount()==0 && io.getOutAmount()==0){
    			
    		}else{
	    		in += io.getInAmount();
	    		out += io.getOutAmount();
	    		io.setTransactionName(MoneyStatus.typeName(io.getTransType()));
	    		ios.add(io);
    		}
    	}
    	cashOnHandAmount = in - out;
    	setIoIncome(Currency.formatAmount(in));
    	setIoExpenses(Currency.formatAmount(out));
    	setCashOnHandAmount(Currency.formatAmount(cashOnHandAmount));
    	Collections.reverse(ios);
    }
    
	@PostConstruct
	public void init(){
		/**
		 * Temporary commented this staff works fine
		 */
		//initIncomes();
		startOnHand();
		//startCash();
		/*startReceipts();
		startIncomes();*/
		//startExpenses();
		//startReceivable();
		//startPayable();
	}
	
	public void startCash(){
		
		cashs = Collections.synchronizedList(new ArrayList<CustomerPaymentTrans>());
		CustomerPaymentTrans pay = new CustomerPaymentTrans();
		pay.setIspaid(1);
		pay.setPaytransisactive(1);
		pay.setBetween(true);
		pay.setDateFrom(DateUtils.convertDate(getCashDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		pay.setDateTo(DateUtils.convertDate(getCashDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		double cashTotal = 0d;
		for(CustomerPaymentTrans caz : CustomerPaymentTrans.retrieve(pay)){
			if(caz.getAmountpay().doubleValue()>0){
				Customer cus = Customer.retrieve(caz.getCustomerPayment().getCustomer().getCustomerid()+"");
				cashTotal += caz.getAmountpay().doubleValue();
				caz.getCustomerPayment().setCustomer(cus);
				caz.setDescription(cus.getFullname());
				caz.setRemarks(PaymentTransactionType.typeName(caz.getPaymentType()));
				cashs.add(caz);
			}
		}
		
		Xtras x = new Xtras(); 
		x.setIsActive(1);
		x.setStatus(Status.POSTED.getId());
		x.setBetween(true);
		x.setDateFrom(DateUtils.convertDate(getCashDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		x.setDateTo(DateUtils.convertDate(getCashDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		
		for(Xtras xt : Xtras.retrieve(x)){
			if(MoneyStatus.OTHER_INCOME.getId()==xt.getTransType()){
				CustomerPaymentTrans caz = new CustomerPaymentTrans();
				
				if(xt.getCustomer().getCustomerid()!=0){
					Customer cus = Customer.retrieve(xt.getCustomer().getCustomerid()+"");
					CustomerPayment cy = new CustomerPayment();
					cy.setPayisactive(1);
					
					Customer c = new Customer();
					c.setCustomerid(cus.getCustomerid());
					c.setIsactive(1);
					
					cy = CustomerPayment.retrieve(cy, c).get(0);
					
					caz.setCustomerPayment(cy);
					caz.setDescription(xt.getDescription() + " - " +cy.getCustomer().getFullname());
					caz.setPaymentdate(xt.getDateTrans());
					cashTotal += xt.getAmount();
					caz.setRemarks(MoneyStatus.typeName(xt.getTransType()));
					caz.setAmountpay(new BigDecimal(xt.getAmount()));
				}else{
					caz.setDescription(xt.getDescription());
					caz.setPaymentdate(xt.getDateTrans());
					cashTotal += xt.getAmount();
					caz.setRemarks(MoneyStatus.typeName(xt.getTransType()));
					caz.setAmountpay(new BigDecimal(xt.getAmount()));
					
				}
				
				
				cashs.add(caz);
			}
		}
		
		setCashTotal(Currency.formatAmount(cashTotal+""));
		Collections.reverse(cashs);
	}
	
	public void startReceipts(){
		
		DeliveryItemReceipt rec = new DeliveryItemReceipt();
		rec.setIsActive(1);
		rec.setStatus(ReceiptStatus.POSTED.getId());
		rec.setBetween(true);
		rec.setDateFrom(DateUtils.convertDate(getReceiptDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		rec.setDateTo(DateUtils.convertDate(getReceiptDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		
		//receipts = DeliveryItemReceipt.retrieve(rec);
		double purchased=0d, balance = 0d, discount =0d, downpayment=0d, charge=0d;
		receipts = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
		for(DeliveryItemReceipt rpt : DeliveryItemReceipt.retrieve(rec)){
			purchased += rpt.getTotalAmount();
			balance += rpt.getBalanceAmount();
			discount += rpt.getDiscountAmount();
			downpayment += rpt.getDownPayment();
			charge += rpt.getDeliveryChargeAmount();
			receipts.add(rpt);
		}
		
		setPurchasedTotal(Currency.formatAmount(purchased+""));
		setBalanceTotal(Currency.formatAmount(balance+""));
		setDiscountTotal(Currency.formatAmount(discount+""));
		setChargeTotal(Currency.formatAmount(charge+""));
		setDownpaymentTotal(Currency.formatAmount(downpayment+""));
		
		Collections.reverse(receipts);
		
	}
	
	public void startIncomes(){
		soldItems = Collections.synchronizedList(new ArrayList<Incomes>());
		loadSoldProductOnly();
		//loadSoldGrocerryOnly();
		loadSoldGrocerry();
	}
	
	
	
	public void loadSoldProductOnly(){
		
		
		DeliveryItemTrans tran = new DeliveryItemTrans();
		tran.setIsActive(1);
		tran.setStatus(DeliveryStatus.POSTED_SOLD_ITEM.getId());
		tran.setBetween(true);
		tran.setDateFrom(DateUtils.convertDate(getIncomesDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		tran.setDateTo(DateUtils.convertDate(getIncomesDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		
		Map<String, DeliveryItemTrans> product = Collections.synchronizedMap(new HashMap<String, DeliveryItemTrans>());
		Map<Long, Map<String, DeliveryItemTrans>> products = Collections.synchronizedMap(new HashMap<Long,Map<String, DeliveryItemTrans>>());
		
		for(DeliveryItemTrans trn : DeliveryItemTrans.retrieve(tran)){
			
			if(products!=null && products.size()>0){
				
				long id = trn.getProduct().getProdid();
				
				if(products.containsKey(id)){
					
					if(products.get(id).containsKey(trn.getDateTrans())){
						
						double newQty = products.get(id).get(trn.getDateTrans()).getQuantity() + trn.getQuantity();
						double sellingAmnt = products.get(id).get(trn.getDateTrans()).getSellingPrice() + trn.getSellingPrice();
						products.get(id).get(trn.getDateTrans()).setQuantity(newQty);
						products.get(id).get(trn.getDateTrans()).setSellingPrice(sellingAmnt);
						
					}else{
						
						ProductPricingTrans price = ProductPricingTrans.retrievePrice(trn.getProduct().getProdid()+"");
						trn.getProduct().setProductPricingTrans(price);
						ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
						trn.getProduct().setProductProperties(prop);
						products.get(id).put(trn.getDateTrans(), trn);
						
					}
					
				}else{
					product = Collections.synchronizedMap(new HashMap<String, DeliveryItemTrans>());
					
					ProductPricingTrans price = ProductPricingTrans.retrievePrice(trn.getProduct().getProdid()+"");
					trn.getProduct().setProductPricingTrans(price);
					ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
					trn.getProduct().setProductProperties(prop);
					
					product.put(trn.getDateTrans(), trn);
					products.put(trn.getProduct().getProdid(), product);
				}
				
			}else{
				
				ProductPricingTrans price = ProductPricingTrans.retrievePrice(trn.getProduct().getProdid()+"");
				trn.getProduct().setProductPricingTrans(price);
				ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
				trn.getProduct().setProductProperties(prop);
				
				product.put(trn.getDateTrans(), trn);
				products.put(trn.getProduct().getProdid(), product);
			}
			
			
		}
		double vat=0d,wVat = 0d, noVat=0d, qty=0d;
		double price=0d,capital = 0d, sales=0d, net =0d;
		int idKey=getSoldItems().size() + 1;
		for(Long id : products.keySet() ){
			for(DeliveryItemTrans tn : products.get(id).values()){
				
				Incomes in = new Incomes();
				in.setId(idKey++);
				in.setDateTrans(tn.getDateTrans());
				in.setDescription(tn.getProduct().getProductProperties().getProductname());
				in.setTransactionType("DELIVERY");
				in.setQuantity(tn.getQuantity());
				in.setUom(tn.getProduct().getProductProperties().getUom().getSymbol());
				
				double priceTmp = tn.getProduct().getProductPricingTrans().getSellingprice().doubleValue();
				in.setPrice(Currency.formatAmount(priceTmp));
				double vatTmp = tn.getProduct().getProductPricingTrans().getTaxpercentage();
				in.setVat(vatTmp);
				
				BigDecimal amntWithOutVat = new BigDecimal("0.0"); 
				try{amntWithOutVat = tn.getProduct().getProductPricingTrans().getPurchasedprice().add(tn.getProduct().getProductPricingTrans().getNetprice());
				amntWithOutVat = amntWithOutVat.multiply(new BigDecimal(tn.getQuantity()+""));}catch(Exception e){}
				
				in.setPriceWihtOutVAT(Currency.formatAmount(amntWithOutVat));
				in.setPriceWihtVAT(Currency.formatAmount(tn.getSellingPrice()));
				double capTmp = tn.getProduct().getProductPricingTrans().getPurchasedprice().doubleValue() * tn.getQuantity();
				in.setCapitalAmount(Currency.formatAmount(capTmp));
				double saleTmp = tn.getProduct().getProductPricingTrans().getSellingprice().doubleValue() * tn.getQuantity();
				in.setSaleAmount(Currency.formatAmount(saleTmp));
				
				 
				double netAmnt = tn.getProduct().getProductPricingTrans().getNetprice().doubleValue() * tn.getQuantity();
				in.setNetAmount(Currency.formatAmount(netAmnt));
				
				soldItems.add(in);
				
				qty += tn.getQuantity();
				price += priceTmp;
				vat += vatTmp;
				wVat += tn.getSellingPrice();
				noVat += amntWithOutVat.doubleValue();
				capital += capTmp;
				sales += saleTmp;
				net += netAmnt;
			}
			
		}
		setQtyTotal(qty);
		setPriceTotal(Currency.formatAmount(price));
		setVatTotal(vat);
		setIncomesPlusVatTotal(Currency.formatAmount(wVat));
		setIncomesMinususVatTotal(Currency.formatAmount(noVat));
		setCapitalTotal(Currency.formatAmount(capital));
		setSalesTotal(Currency.formatAmount(sales));
		setNetTotal(Currency.formatAmount(net));
		
	}	
	
	public void loadSoldGrocerry() {
		
		
		
		PurchasedItem item = new PurchasedItem();
		item.setIsactiveitem(1);
		item.setIsBetweenDate(true);
		item.setDateFrom(DateUtils.convertDate(getIncomesDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		item.setDateTo(DateUtils.convertDate(getIncomesDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		
		List<PurchasedItem> plist = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		Map<String, List<PurchasedItem>> mapSold = Collections.synchronizedMap(new HashMap<String, List<PurchasedItem>>());
		
		for(PurchasedItem items : PurchasedItem.retrieve(item)){
			String date = items.getDatesold();
			
			if(mapSold!=null && mapSold.size()>0) {
				if(mapSold.containsKey(date)) {
					
					plist = mapSold.get(date);
					plist.add(items);
					mapSold.put(date, plist);
					
				}else {
					
					plist = Collections.synchronizedList(new ArrayList<PurchasedItem>());
					plist.add(items);
					mapSold.put(date, plist);
				}
			}else {
				plist.add(items);
				mapSold.put(date, plist);
			}
			
		}
		double vat=0d,wVat = 0d, noVat=0d, qty=0d;
		double price=0d,capital = 0d, sales=0d, net =0d;
		int idKey=getSoldItems().size() + 1;
		List<Incomes> listIncome = Collections.synchronizedList(new ArrayList<Incomes>());
		Map<String, List<Incomes>> incomeMap = Collections.synchronizedMap(new HashMap<String, List<Incomes>>());
		for(String date : mapSold.keySet()) {
			listIncome = Collections.synchronizedList(new ArrayList<Incomes>());
			for(PurchasedItem prod : mapSold.get(date)) {
				Incomes in = new Incomes();
				in.setId(idKey++);
				in.setDateTrans(prod.getDatesold());
				in.setDescription(prod.getProductName());
				in.setTransactionType("STORE");
				in.setQuantity(prod.getQty());
				in.setUom(prod.getUomSymbol());
				
				double priceTmp = 0d;
				double vatTmp = 0d;
				double amntWithOutVat = 0d,amntWithVat = 0d;;
				double capTmp = 0d;
				double saleTmp = 0d;
				double netAmnt = 0d;
				
				vatTmp = prod.getTaxpercentage();
				in.setVat(vatTmp);
				
				priceTmp = prod.getSellingPrice().doubleValue();
				capTmp = prod.getPurchasedprice().doubleValue() * prod.getQty();
				saleTmp = prod.getSellingPrice().doubleValue() * prod.getQty();
				netAmnt = prod.getNetprice().doubleValue() * prod.getQty();
				
				amntWithVat = saleTmp;
				amntWithOutVat = capTmp + netAmnt;
				
				in.setPrice(Currency.formatAmount(priceTmp));
				in.setPriceWihtOutVAT(Currency.formatAmount(amntWithOutVat));
				in.setPriceWihtVAT(Currency.formatAmount(amntWithVat));
				in.setCapitalAmount(Currency.formatAmount(capTmp));
				in.setSaleAmount(Currency.formatAmount(saleTmp));
				in.setNetAmount(Currency.formatAmount(netAmnt));
				
				listIncome.add(in);
				
				qty += prod.getQty();
				price += priceTmp;
				vat += vatTmp;
				wVat += amntWithVat;
				noVat += amntWithOutVat;
				capital += capTmp;
				sales += saleTmp;
				net += netAmnt;
			}
			incomeMap.put(date, listIncome);
		}
		
		
		//additional product in store
				AddOnStore on = new AddOnStore();
				on.setIsActive(1);
				on.setBetween(true);
				on.setDateFrom(DateUtils.convertDate(getIncomesDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
				on.setDateTo(DateUtils.convertDate(getIncomesDateTo(),DateFormatter.YYYY_MM_DD.getName()));
				on.setStatus(Status.POSTED.getId());
				double addOnNet = 0d;
				for(AddOnStore st : AddOnStore.retrieve(on)){
					Incomes in = new Incomes();
					in.setId(idKey++);
					in.setDateTrans(st.getDateTrans());
					in.setDescription(st.getDescription());
					in.setTransactionType("STORE ADD ONS");
					in.setQuantity(1);
					in.setUom("n/a");
					
					double priceTmp = 0d;
					try{priceTmp=st.getAmount();}catch(NullPointerException e){}
					in.setPrice(Currency.formatAmount(priceTmp));
					double vatTmp = 0;
					in.setVat(vatTmp);
					//try{prod.getPurchasedprice().add(prod.getNetprice());}catch(NullPointerException e){prod.setPurchasedprice(new BigDecimal("0")); prod.getPurchasedprice().add(new BigDecimal("0"));}
					BigDecimal amntWithOutVat = new BigDecimal("0.0"); 
					try{amntWithOutVat = new BigDecimal(st.getAmount()+"");//prod.getPurchasedprice().add(prod.getNetprice());
					amntWithOutVat = amntWithOutVat.multiply(new BigDecimal("1"));}catch(Exception e){}
					
					in.setPriceWihtOutVAT(Currency.formatAmount(amntWithOutVat));
					in.setPriceWihtVAT(Currency.formatAmount(priceTmp));
					double capTmp = st.getAmount();
					in.setCapitalAmount(Currency.formatAmount(capTmp));
					double saleTmp = priceTmp;
					in.setSaleAmount(Currency.formatAmount(saleTmp));
					
					 
					double netAmnt = st.getAmount();
					in.setNetAmount(Currency.formatAmount(netAmnt));
					
					//soldItems.add(in);
					
					qty += 1;
					price += priceTmp;
					vat += vatTmp;
					//wVat += priceTmp;
					//noVat += amntWithOutVat.doubleValue();
					//capital += capTmp;
					sales += saleTmp;
					net += netAmnt;
					addOnNet += netAmnt;
					if(incomeMap!=null && incomeMap.size()>0) {
						if(incomeMap.containsKey(st.getDateTrans())) {
							listIncome = incomeMap.get(st.getDateTrans());
							listIncome.add(in);
							incomeMap.put(st.getDateTrans(), listIncome);
						}else {
							listIncome = Collections.synchronizedList(new ArrayList<Incomes>());
							listIncome.add(in);
							incomeMap.put(st.getDateTrans(), listIncome);
						}
					}else {
						listIncome = Collections.synchronizedList(new ArrayList<Incomes>());
						listIncome.add(in);
						incomeMap.put(st.getDateTrans(), listIncome);
					}
				}
				
				
				qty = getQtyTotal() + qty;
				setQtyTotal(qty);
				
				price = Double.valueOf(getPriceTotal().replace(",", "")) + price;
				setPriceTotal(Currency.formatAmount(price));
				
				vat = getVatTotal() + vat;
				setVatTotal(vat);
				
				wVat = Double.valueOf(getIncomesPlusVatTotal().replace(",", "")) + wVat;
				setIncomesPlusVatTotal(Currency.formatAmount(wVat));
				
				noVat = Double.valueOf(getIncomesMinususVatTotal().replace(",", "")) + noVat;
				setIncomesMinususVatTotal(Currency.formatAmount(noVat));
				
				capital = Double.valueOf(getCapitalTotal().replace(",", "")) + capital;
				setCapitalTotal(Currency.formatAmount(capital));
				
				sales = Double.valueOf(getSalesTotal().replace(",", "")) + sales;
				setSalesTotal(Currency.formatAmount(sales));
				
				net = Double.valueOf(getNetTotal().replace(",", "")) + net;
				setNetTotal(Currency.formatAmount(net));
		
				//setSoldItems(Collections.synchronizedList(new ArrayList<Incomes>()));
				Map<String, List<Incomes>> sortProduct = new TreeMap<String, List<Incomes>>(incomeMap);
				for(String date : sortProduct.keySet()) {
					for(Incomes in : sortProduct.get(date)) {
						soldItems.add(in);
					}
				}
		
	}
	@Deprecated
	public void loadSoldGrocerryOnly(){
		
		PurchasedItem item = new PurchasedItem();
		item.setIsactiveitem(1);
		item.setIsBetweenDate(true);
		item.setDateFrom(DateUtils.convertDate(getIncomesDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		item.setDateTo(DateUtils.convertDate(getIncomesDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		
		Map<String, PurchasedItem> product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
		Map<Long, Map<String, PurchasedItem>> products = Collections.synchronizedMap(new HashMap<Long,Map<String, PurchasedItem>>());
		
		for(PurchasedItem items : PurchasedItem.retrieve(item)){
			
			if(products!=null && products.size()>0){
				
				long id = items.getProduct().getProdid();
				
				if(products.containsKey(id)){
					
					String date = items.getDatesold();
					
					if(products.get(id).containsKey(date)){
						
						double addQty = 0d;
						double oldQty = 0d;
						double newQty = 0d;
						addQty = items.getQty();
						oldQty = products.get(id).get(date).getQty();
						newQty = oldQty + addQty;
						products.get(id).get(date).setQty(newQty);
						
						
						BigDecimal purchase = items.getPurchasedprice();
						products.get(id).get(date).setPurchasedprice(purchase);
						BigDecimal price = items.getSellingPrice();
						products.get(id).get(date).setSellingPrice(price);
						BigDecimal net = items.getNetprice();
						products.get(id).get(date).setNetprice(net);
						
					}else{
						
						products.get(id).put(date, items);
					}
					
					
				}else{
					
					product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
					
					product.put(items.getDatesold(), items);
					products.put(items.getProduct().getProdid(), product);
				}
				
			}else{
				products = Collections.synchronizedMap(new HashMap<Long,Map<String, PurchasedItem>>());
				product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
				
				product.put(items.getDatesold(), items);
				products.put(items.getProduct().getProdid(), product);
				
			}
			
		}
		
		
		
		double vat=0d,wVat = 0d, noVat=0d, qty=0d;
		double price=0d,capital = 0d, sales=0d, net =0d;
		int idKey=getSoldItems().size() + 1;
		
		for(Long id : products.keySet()){
			
			for(PurchasedItem prod : products.get(id).values()){
				
				Incomes in = new Incomes();
				in.setId(idKey++);
				in.setDateTrans(prod.getDatesold());
				in.setDescription(prod.getProductName());
				in.setTransactionType("STORE");
				in.setQuantity(prod.getQty());
				in.setUom(prod.getUomSymbol());
				
				double priceTmp = 0d;
				try{priceTmp=prod.getSellingPrice().doubleValue();}catch(NullPointerException e){}
				in.setPrice(Currency.formatAmount(priceTmp));
				double vatTmp = prod.getTaxpercentage();
				in.setVat(vatTmp);
				try{prod.getPurchasedprice().add(prod.getNetprice());}catch(NullPointerException e){prod.setPurchasedprice(new BigDecimal("0")); prod.getPurchasedprice().add(new BigDecimal("0"));}
				BigDecimal amntWithOutVat = new BigDecimal("0.0"); 
				try{amntWithOutVat = prod.getPurchasedprice().add(prod.getNetprice());
				amntWithOutVat = amntWithOutVat.multiply(new BigDecimal(prod.getQty()+""));}catch(Exception e){prod.setNetprice(new BigDecimal("0"));}
				
				in.setPriceWihtOutVAT(Currency.formatAmount(amntWithOutVat));
				in.setPriceWihtVAT(Currency.formatAmount(priceTmp * prod.getQty()));
				double capTmp = prod.getPurchasedprice().doubleValue() * prod.getQty();
				in.setCapitalAmount(Currency.formatAmount(capTmp));
				double saleTmp = priceTmp * prod.getQty();
				in.setSaleAmount(Currency.formatAmount(saleTmp));
				
				 
				double netAmnt = prod.getNetprice().doubleValue() * prod.getQty();
				in.setNetAmount(Currency.formatAmount(netAmnt));
				
				soldItems.add(in);
				
				qty += prod.getQty();
				price += priceTmp;
				vat += vatTmp;
				wVat += priceTmp;
				noVat += amntWithOutVat.doubleValue();
				capital += capTmp;
				sales += saleTmp;
				net += netAmnt;
				
			}
			
		}
		
		//additional product in store
		AddOnStore on = new AddOnStore();
		on.setIsActive(1);
		on.setBetween(true);
		on.setDateFrom(DateUtils.convertDate(getIncomesDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		on.setDateTo(DateUtils.convertDate(getIncomesDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		on.setStatus(Status.POSTED.getId());
		for(AddOnStore st : AddOnStore.retrieve(on)){
			Incomes in = new Incomes();
			in.setId(idKey++);
			in.setDateTrans(st.getDateTrans());
			in.setDescription(st.getDescription());
			in.setTransactionType("STORE ADD ONS");
			in.setQuantity(1);
			in.setUom("n/a");
			
			double priceTmp = 0d;
			try{priceTmp=st.getAmount();}catch(NullPointerException e){}
			in.setPrice(Currency.formatAmount(priceTmp));
			double vatTmp = 0;
			in.setVat(vatTmp);
			//try{prod.getPurchasedprice().add(prod.getNetprice());}catch(NullPointerException e){prod.setPurchasedprice(new BigDecimal("0")); prod.getPurchasedprice().add(new BigDecimal("0"));}
			BigDecimal amntWithOutVat = new BigDecimal("0.0"); 
			try{amntWithOutVat = new BigDecimal(st.getAmount()+"");//prod.getPurchasedprice().add(prod.getNetprice());
			amntWithOutVat = amntWithOutVat.multiply(new BigDecimal("1"));}catch(Exception e){}
			
			in.setPriceWihtOutVAT(Currency.formatAmount(amntWithOutVat));
			in.setPriceWihtVAT(Currency.formatAmount(priceTmp));
			double capTmp = st.getAmount();
			in.setCapitalAmount(Currency.formatAmount(capTmp));
			double saleTmp = priceTmp;
			in.setSaleAmount(Currency.formatAmount(saleTmp));
			
			 
			double netAmnt = st.getAmount();
			in.setNetAmount(Currency.formatAmount(netAmnt));
			
			soldItems.add(in);
			
			qty += 1;
			price += priceTmp;
			vat += vatTmp;
			wVat += priceTmp;
			noVat += amntWithOutVat.doubleValue();
			capital += capTmp;
			sales += saleTmp;
			net += netAmnt;
		}
		
		
		qty = getQtyTotal() + qty;
		setQtyTotal(qty);
		
		price = Double.valueOf(getPriceTotal().replace(",", "")) + price;
		setPriceTotal(Currency.formatAmount(price));
		
		vat = getVatTotal() + vat;
		setVatTotal(vat);
		
		wVat = Double.valueOf(getIncomesPlusVatTotal().replace(",", "")) + wVat;
		setIncomesPlusVatTotal(Currency.formatAmount(wVat));
		
		noVat = Double.valueOf(getIncomesMinususVatTotal().replace(",", "")) + noVat;
		setIncomesMinususVatTotal(Currency.formatAmount(noVat));
		
		capital = Double.valueOf(getCapitalTotal().replace(",", "")) + capital;
		setCapitalTotal(Currency.formatAmount(capital));
		
		sales = Double.valueOf(getSalesTotal().replace(",", "")) + sales;
		setSalesTotal(Currency.formatAmount(sales));
		
		net = Double.valueOf(getNetTotal().replace(",", "")) + net;
		setNetTotal(Currency.formatAmount(net));
		
	}
	
	public void printAll(){
		
	}
	
	public void initIncomes(){
		PurchasedItem item = new PurchasedItem();
		item.setIsactiveitem(1);
		
		if(getIncomesProductName()!=null){
			item.setProductName(Whitelist.remove(getIncomesProductName()));
		}
		if(getIncomesDateFrom()!=null && getIncomesDateTo()==null){
			item.setDatesold(DateUtils.convertDate(getIncomesDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		}else if(getIncomesDateFrom()==null && getIncomesDateTo()!=null){
			item.setDatesold(DateUtils.convertDate(getIncomesDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		}else if(getIncomesDateFrom()!=null && getIncomesDateTo()!=null){
			item.setDateFrom(DateUtils.convertDate(getIncomesDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
			item.setDateTo(DateUtils.convertDate(getIncomesDateTo(),DateFormatter.YYYY_MM_DD.getName()));
			item.setIsBetweenDate(true);
		}
		items = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		/*setIncomesPlusVatTotal(new BigDecimal("0.00"));
		setIncomesMinususVatTotal(new BigDecimal("0.00"));
		setCapitalTotal(new BigDecimal("0.00"));
		setSalesTotal(new BigDecimal("0.00"));*/
		
		for(PurchasedItem itm : PurchasedItem.retrieve(item)){
			
			
			
			BigDecimal capital = itm.getPurchasedprice().multiply(new BigDecimal(itm.getQty()+""));
			BigDecimal sales = itm.getSellingPrice().multiply(new BigDecimal(itm.getQty()+""));
			
			BigDecimal incomeminusvat = itm.getNetprice().multiply(new BigDecimal(itm.getQty()+""));
			BigDecimal incomeplusvat = sales.subtract(capital); 
			
			itm.setIncomesMinusVat(incomeminusvat);
			itm.setIncomesPlusVat(incomeplusvat);
			
			itm.setCapital(capital);
			itm.setSales(sales);
			
			/*if(getIncomesMinususVatTotal()!=null){
				setIncomesMinususVatTotal(getIncomesMinususVatTotal().add(incomeminusvat));
			}else{
				setIncomesMinususVatTotal(incomeminusvat);
			}
			
			if(getIncomesPlusVatTotal()!=null){
				setIncomesPlusVatTotal(getIncomesPlusVatTotal().add(incomeplusvat));
			}else{
				setIncomesPlusVatTotal(incomeplusvat);
			}
			
			if(getCapitalTotal()!=null){
				setCapitalTotal(getCapitalTotal().add(capital));
			}else{
				setCapitalTotal(capital);
			}
			
			if(getSalesTotal()!=null){
				setSalesTotal(getSalesTotal().add(sales));
			}else{
				setSalesTotal(sales);
			}*/
			
			items.add(itm);
		}
		
		Collections.reverse(items);
	}
	
	
	public void startExpenses(){
		expenses = Collections.synchronizedList(new ArrayList<Expenses>());
		
		SupplierPayment pay = new SupplierPayment();
		pay.setIsActive(1);
		pay.setBetween(true);
		pay.setDateFrom(DateUtils.convertDate(getExpenseDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		pay.setDateTo(DateUtils.convertDate(getExpenseDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		
		double expensesTotal=0d;
		int id=1;
		for(SupplierPayment py : SupplierPayment.retrieve(pay)){
			
			Supplier sp = new Supplier();
			sp.setSupid(py.getSupplierTrans().getSupplier().getSupid());
			sp.setIsactive(1);
			if(getDescription()!=null && !getDescription().isEmpty()){
				sp.setSuppliername(getDescription());
			}
			
			List<Supplier> sups = Supplier.retrieve(sp);
			
			if(sups.size()>0){
				Supplier sup = sups.get(0);
				Expenses exp = new Expenses();
				
				exp.setId(id++);
				exp.setDateTrans(py.getPayTransDate());
				
				exp.setHtmlPage("1");
				exp.setActionName("Show Supplier");
				
				//Supplier sup = Supplier.supplier(py.getSupplierTrans().getSupplier().getSupid()+"");
				
				exp.setDescription(sup.getSuppliername());
				exp.setTransactionType("SUPPLIER");
				
				exp.setAmount(Currency.formatAmount(py.getAmount()));
				exp.setRemarks(py.getRemarks());
				expenses.add(exp);
				expensesTotal += py.getAmount();
			}
		}
		
		Xtras x = new Xtras();
		x.setIsActive(1);
		x.setStatus(Status.POSTED.getId());
		x.setBetween(true);
		x.setDateFrom(DateUtils.convertDate(getExpenseDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		x.setDateTo(DateUtils.convertDate(getExpenseDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		
		if(getDescription()!=null && !getDescription().isEmpty()){
			x.setDescription(getDescription());
		}
		
		for(Xtras xt : Xtras.retrieve(x)){
			
			Expenses exp = new Expenses();
			exp.setId(id++);
			exp.setDateTrans(xt.getDateTrans());
			exp.setAmount(Currency.formatAmount(xt.getAmount()));
			exp.setRemarks(xt.getRemarks());
			
			exp.setHtmlPage("2");
			exp.setActionName("Show Xtras");
			
			if(MoneyStatus.ALLOWANCES.getId()==xt.getTransType()){
				exp.setTransactionType(MoneyStatus.ALLOWANCES.getName());
				exp.setDescription(xt.getDescription());
				expenses.add(exp);
				expensesTotal += xt.getAmount();
			}else if(MoneyStatus.CASH_LOAN.getId()==xt.getTransType()){
				Customer customer = new Customer();
				customer = Customer.retrieve(xt.getCustomer().getCustomerid()+"");
				exp.setTransactionType(MoneyStatus.CASH_LOAN.getName());
				exp.setDescription(xt.getDescription() + " " + customer.getFullname());
				expenses.add(exp);
				expensesTotal += xt.getAmount();
			}else if(MoneyStatus.OTHER_EXP.getId()==xt.getTransType()){
				exp.setTransactionType(MoneyStatus.OTHER_EXP.getName());
				exp.setDescription(xt.getDescription());
				expenses.add(exp);
				expensesTotal += xt.getAmount();
			}else if(MoneyStatus.REFUND.getId()==xt.getTransType()){
				Customer customer = new Customer();
				customer = Customer.retrieve(xt.getCustomer().getCustomerid()+"");
				exp.setTransactionType(MoneyStatus.REFUND.getName());
				exp.setDescription(xt.getDescription() + " from " + customer.getFullname());
				expenses.add(exp);
				expensesTotal += xt.getAmount();	
			}else if(MoneyStatus.SALARY.getId()==xt.getTransType()){
				exp.setTransactionType(MoneyStatus.SALARY.getName());
				exp.setDescription(xt.getDescription());
				expenses.add(exp);
				expensesTotal += xt.getAmount();
			}
		}
		
		/*ProductReturnSupplier ret = new ProductReturnSupplier();
		ret.setIsActive(1);
		ret.setBetween(true);
		ret.setDateFrom(getExpenseDateFrom());
		ret.setDateTo(getExpenseDateTo());
		ret.setStatus(ReturnStatus.POSTED.getId());
		
		for(ProductReturnSupplier rt : ProductReturnSupplier.retrieve(ret)){
			
			Expenses exp = new Expenses();
			exp.setHtmlPage("1");
			exp.setActionName("Show Supplier");
			exp.setId(id++);
			exp.setDateTrans(rt.getDateTrans());
			exp.setAmount(Currency.formatAmount(rt.getAmount()));
			exp.setRemarks("Transaction from Supplier Return "+rt.getRemarks());
			exp.setTransactionType(MoneyStatus.RETURN_CAPITAL.getName());
			exp.setDescription("Return product "+ rt.getProperties().getProductname() +" total of " + rt.getQuantity() + " " + rt.getUom().getSymbol() + " to supplier " + rt.getSupplier().getSuppliername());
			expenses.add(exp);
			expensesTotal += rt.getAmount();
			
		}*/
		
		
		setExpensesTotal(Currency.formatAmount(expensesTotal));
		Collections.reverse(expenses);
	}
	
	public void startReceivable(){
		receivables = Collections.synchronizedList(new ArrayList<Receivable>());
		
		CustomerPayment cus = new CustomerPayment();
		cus.setPayisactive(1);
		
		Customer cz = new Customer();
		cz.setIsactive(1);
		
		if(getRecDescription()!=null && !getRecDescription().isEmpty()){
			cz.setFullname(getRecDescription().replace("--", ""));
		}
		
		double recTotal=0d;
		int id=1;
		for(CustomerPayment py : CustomerPayment.retrieve(cus,cz)){
			if(py.getAmountbalance().doubleValue()>0){
				recTotal += py.getAmountbalance().doubleValue();
				Receivable rc = new Receivable();
				rc.setId(id++);
				rc.setDescription(py.getCustomer().getFullname());
				rc.setTransactionType("Customer");
				rc.setAmount(Currency.formatAmount(py.getAmountbalance().doubleValue()));
				receivables.add(rc);
			}
		}
		/*
		for(RentedBottle bot : RentedBottle.retrieve(cz)){
			if(bot.getCurrentBalance()>0){
				recTotal += bot.getCurrentBalance();
				Receivable rc = new Receivable();
				rc.setId(id++);
				rc.setDescription(bot.getCustomer().getFullname());
				rc.setTransactionType("Rented");
				rc.setAmount(Currency.formatAmount(bot.getCurrentBalance()));
				receivables.add(rc);
			}
		}*/	
		
		setRecTotal(Currency.formatAmount(recTotal));
		Collections.reverse(receivables);
		
	}
	
	public void startPayable(){
		payables = Collections.synchronizedList(new ArrayList<Payable>());
		SupplierTrans tran = new SupplierTrans();
		tran.setIsActive(1);
		tran.setStatus(1);
		
		Supplier sup = new Supplier();
		sup.setIsactive(1);
		if(getPayDescription()!=null && !getPayDescription().isEmpty()){
			sup.setSuppliername(getPayDescription());
		}
		
		if(getPayDateFrom()!=null){
			setPayDateFrom(null);
		}
		if(getPayDateTo()!=null){
			setPayDateTo(null);
		}
		
		if(getPayDateFrom()==null && getPayDateTo()==null){
			//no date range
		}else if(getPayDateFrom()!=null && getPayDateTo()==null){
			tran.setBetweenDate(true);
			tran.setDateFrom(DateUtils.convertDate(getPayDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
			tran.setDateTo(DateUtils.convertDate(getPayDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		}else if(getPayDateFrom()==null && getPayDateTo()!=null){
			tran.setBetweenDate(true);
			tran.setDateFrom(DateUtils.convertDate(getPayDateTo(),DateFormatter.YYYY_MM_DD.getName()));
			tran.setDateTo(DateUtils.convertDate(getPayDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		}else if(getPayDateFrom()!=null && getPayDateTo()!=null){
			tran.setBetweenDate(true);
			tran.setDateFrom(DateUtils.convertDate(getPayDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
			tran.setDateTo(DateUtils.convertDate(getPayDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		}
		
		
			
		
		
		double payTotal = 0d;
		int id=1;
		for(SupplierTrans tn : SupplierTrans.retrieve(tran,sup)){
			
			Payable payable = new Payable();
			payable.setId(id++);
			payable.setDateTrans(tn.getTransDate());
			payable.setDescription(tn.getSupplier().getSuppliername());
			payable.setTransactionType("Payment");
			double amount = 0d, paidAmnt = 0d;;
			SupplierTrans tnx = new SupplierTrans();
			tnx.setIsActive(1);
			tnx.setId(tn.getId());
			
			SupplierPayment pay = new SupplierPayment();
			pay.setIsActive(1);
			
			for(SupplierPayment py : SupplierPayment.retrieve(pay,tnx)){
				paidAmnt += py.getAmount();
			}
			
			amount = tn.getPurchasedPrice() - paidAmnt;
			payable.setAmount(Currency.formatAmount(amount));
			payable.setRemarks("");
			payTotal += amount;
			payables.add(payable);
		}
		setPayTotal(Currency.formatAmount(payTotal));
		Collections.reverse(payables);
		
	}
	
	public void startSummary(){
		
		summaryData = Collections.synchronizedList(new ArrayList<>());
		
		totalSummaryQty = 0;
		totalSummaryItemPrice = Currency.formatAmount("0.0");
		totalSummaryCapital = Currency.formatAmount("0.0");
		totalSummarySale = Currency.formatAmount("0.0");
		totalSummaryNet = Currency.formatAmount("0.0");
		
		warehouseReaminingProduct();
		
		if(getTransTypeId()==1){ //delivery
			delivery();
			retrieveRemainingProductInDeliveryTruck();
			productRemainingOnDeliveryTruck();
		}else if(getTransTypeId()==2){//store
			//store();
			storeProduct();
			retrieveStoreRemainingProduct();
		}
		
	}
	
	public void retrieveStoreRemainingProduct(){
		
		stores = Collections.synchronizedList(new ArrayList<StoreProduct>());
		String sql = "SELECT * FROM storeproduct WHERE qty!=0 AND prodIsActive=1";
		String[] params = new String[0];
		
		double grandPrice=0d, qtyTotal = 0d, grandTotal=0d;
		for(StoreProduct store : StoreProduct.retrieveProduct(sql, params)){
			grandPrice += store.getSellingPrice();
			double total = store.getQuantity() * store.getSellingPrice();
			store.setTotal(total);
			grandTotal += total;
			qtyTotal += store.getQuantity();
			stores.add(store);
		}
		
		setRetrunProductItemPriceGrandTotal(Currency.formatAmount(grandPrice+""));
		setReturnProductItemQtyGrandTotal(qtyTotal);
		setReturnProductItemGrandTotal(Currency.formatAmount(grandTotal+""));
	}
	
	/**
	 * Check and load the previous return item from delivery
	 */
	public void retrieveRemainingProductInDeliveryTruck(){
		
		String sql = "SELECT * FROM deliveryitem WHERE isactiveDel=1 AND delStatus=2 ORDER BY deldate DESC LIMIT 1";
		DeliveryItem items = null;
		DeliveryItem itemDate = null;
		
		try{items = DeliveryItem.retrieve(sql, new String[0]).get(0);}catch(IndexOutOfBoundsException e){}
		
		boolean isNotExist = false;
		if(items!=null && DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(items.getDateTrans())){
			isNotExist = false;
		}else{
		sql = "SELECT * FROM deliveryitem WHERE isactiveDel=1 AND delStatus=2 AND deldate='" + DateUtils.getCurrentDateYYYYMMDD() + "' LIMIT 1";
		try{itemDate = DeliveryItem.retrieve(sql, new String[0]).get(0);
		System.out.println("check 2 retrieveRemainingProductInDeliveryTruck : " + itemDate.getDateTrans());
		}catch(IndexOutOfBoundsException e){}
		
		if(itemDate==null){
			isNotExist = true;
		}else{
			isNotExist = false;
		}
		
		}
		 
		//filter item should not much the current date
		if(items!=null && itemDate==null & isNotExist){
			
			DeliveryItem item = new DeliveryItem();
			item.setIsActive(1);
			item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
			item.setBetween(true);
			item.setDateFrom(items.getDateTrans());
			item.setDateTo(items.getDateTrans());
			
			Map<Long, DeliveryItem> product = Collections.synchronizedMap(new HashMap<Long, DeliveryItem>());
			for(DeliveryItem it : DeliveryItem.retrieve(item)){
				//filter product
				if(product!=null && product.size()>0){
					long id = it.getProduct().getProdid();
					if(product.containsKey(id)){
						double qty = product.get(id).getQuantity();
						qty = qty + it.getQuantity();
						product.get(id).setQuantity(qty);
					}else{
						ProductProperties prop = ProductProperties.properties(it.getProduct().getProductProperties().getPropid()+"");
						it.getProduct().setProductProperties(prop);
						product.put(it.getProduct().getProdid(), it);
					}
					
				}else{
					ProductProperties prop = ProductProperties.properties(it.getProduct().getProductProperties().getPropid()+"");
					it.getProduct().setProductProperties(prop);
					product.put(it.getProduct().getProdid(), it);
				}
				
				
			}
			
			if(product!=null && product.size()>0){
				
				for(DeliveryItem itm : product.values()){
						
					double qtySold = retrieveSoldItem(itm.getProduct(),DeliveryStatus.POSTED_SOLD_ITEM.getId(),items.getDateTrans(),items.getDateTrans());
					double remQty = 0d;
					remQty = itm.getQuantity() - qtySold;
					
					if(remQty>0){
						DeliveryItem newItem = new DeliveryItem();
						newItem.setIsActive(1);
						newItem.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
						newItem.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
						newItem.setSellingPrice(itm.getSellingPrice());
						newItem.setChargeAmount(0);
						newItem.setQuantity(remQty);
						newItem.setRemarks("remaining qty from date " + items.getDateTrans());
						newItem.setProduct(itm.getProduct());
						newItem.setUserDtls(Login.getUserLogin().getUserDtls());
						newItem.save();
					}
					
				}
				
			}
			
			
		}
		
		
		
		
	}
	
	private void productRemainingOnDeliveryTruck(){
		returnItems = Collections.synchronizedList(new ArrayList<DeliveryItem>());
		DeliveryItem item = new DeliveryItem();
		item.setIsActive(1);
		item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
		item.setBetween(true);
		item.setDateFrom(DateUtils.getCurrentDateYYYYMMDD());
		item.setDateTo(DateUtils.getCurrentDateYYYYMMDD());
		Map<Long, Map<String, DeliveryItem>> products = Collections.synchronizedMap(new HashMap<Long,Map<String, DeliveryItem>>());
		Map<String, DeliveryItem> product = Collections.synchronizedMap(new HashMap<String, DeliveryItem>());
		
		for(DeliveryItem it : DeliveryItem.retrieve(item)){
			
			if(products!=null && products.size()>0){
				
				long id = it.getProduct().getProdid();
				
				if(products.containsKey(id)){
					
					if(products.get(id).containsKey(it.getDateTrans())){
						
						double qty = products.get(id).get(it.getDateTrans()).getQuantity();
						qty = qty + it.getQuantity();
						products.get(id).get(it.getDateTrans()).setQuantity(qty);
						
					}else{
						
						ProductProperties prop = ProductProperties.properties(it.getProduct().getProductProperties().getPropid()+"");
						it.getProduct().setProductProperties(prop);
						products.get(id).put(it.getDateTrans(), it);
						
					}
					
				}else{
					
					product = Collections.synchronizedMap(new HashMap<String, DeliveryItem>());
					ProductProperties prop = ProductProperties.properties(it.getProduct().getProductProperties().getPropid()+"");
					it.getProduct().setProductProperties(prop);
					product.put(it.getDateTrans(), it);
					products.put(it.getProduct().getProdid(), product);
					
				}
				
				
			}else{
				ProductProperties prop = ProductProperties.properties(it.getProduct().getProductProperties().getPropid()+"");
				it.getProduct().setProductProperties(prop);
				product.put(it.getDateTrans(), it);
				products.put(it.getProduct().getProdid(), product);
			}
			
		}
		
		
		Map<Long, DeliveryItem> unSort = Collections.synchronizedMap(new HashMap<Long, DeliveryItem>());
		double grandTotal = 0d;
		double grandPrice = 0d;
		double qtyTotal = 0d;
		if(products!=null && products.size()>0){
			for(Long id : products.keySet()){
				for(DeliveryItem itm : products.get(id).values()){
						
					double qtySold = retrieveSoldItem(itm.getProduct(),DeliveryStatus.POSTED_SOLD_ITEM.getId(),itm.getDateTrans(),itm.getDateTrans());
					double remQty = 0d;
					double total = 0d;
					remQty = itm.getQuantity() - qtySold;
					
					if(remQty>0){
						total = itm.getSellingPrice() * remQty;
						grandTotal += total;
						grandPrice +=itm.getSellingPrice();
						qtyTotal +=remQty;
						itm.setQuantity(remQty);
						itm.setTotal(total);
						//returnItems.add(itm);
						unSort.put(itm.getId(), itm);
					}
					
				}
			}
			
		}
		
		Map<Long, DeliveryItem> sort = new TreeMap<Long, DeliveryItem>(unSort);
		for(DeliveryItem itm : sort.values()){
			returnItems.add(itm);
		}
		
		setRetrunProductItemPriceGrandTotal(Currency.formatAmount(grandPrice+""));
		setReturnProductItemQtyGrandTotal(qtyTotal);
		setReturnProductItemGrandTotal(Currency.formatAmount(grandTotal+""));
	}
	
	public double retrieveSoldItem(Product prod , int status, String dateFrom, String dateTo){
		double qty = 0;
		Product prd = new Product();
		prd.setProdid(prod.getProdid());
		prd.setIsactiveproduct(1);
		
		DeliveryItemTrans tran = new DeliveryItemTrans();
		tran.setIsActive(1);
		tran.setStatus(status);
		tran.setBetween(true);
		tran.setDateFrom(dateFrom);
		tran.setDateTo(dateTo);
		try{
			for(DeliveryItemTrans trn : DeliveryItemTrans.retrieve(tran,prod)){
				qty += trn.getQuantity();
			}
		}catch(Exception e){}
		
		return qty;
	}
	
	private void warehouseReaminingProduct(){
		warehouseProduct = Collections.synchronizedList(new ArrayList<ProductInventory>());
		String sql = " AND inv.newqty!=0 AND prop.isactive=1";
		String[] params = new String[0]; 
		
		for(ProductInventory inv : ProductInventory.retrieve(sql, params)){
			
			ProductProperties prop = ProductProperties.properties(inv.getProductProperties().getPropid()+"");
			inv.setProductProperties(prop);
			warehouseProduct.add(inv);
			warehouseTotalQty += inv.getNewqty();
		}
		
	}
	
	private void storeProduct(){
		PurchasedItem item = new PurchasedItem();
		item.setIsactiveitem(1);
		item.setIsBetweenDate(true);
		item.setDateFrom(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()));
		item.setDateTo(DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
		
		Map<String, PurchasedItem> product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
		Map<Long, Map<String, PurchasedItem>> products = Collections.synchronizedMap(new HashMap<Long,Map<String, PurchasedItem>>());
		double itemPrice=0d,capital = 0d, sale=0d, net =0d,qty=0d;
		
		List<Summary> summaryDetailed = Collections.synchronizedList(new ArrayList<Summary>());
		Map<String, List<Summary>> sumDataDetailed = Collections.synchronizedMap(new HashMap<String, List<Summary>>());
		
		if(getSummaryFilterId()==1){ //summary
			
		
		for(PurchasedItem items : PurchasedItem.retrieve(item)){
			long id = items.getProduct().getProdid();
			String date = items.getDatesold();
			
			if(products!=null && products.size()>0){
				
				
				
				if(products.containsKey(id)){
					
					
					
					if(products.get(id).containsKey(date)){
						
						double addQty = 0d;
						double oldQty = 0d;
						double newQty = 0d;
						addQty = items.getQty();
						oldQty = products.get(id).get(date).getQty();
						newQty = oldQty + addQty;
						products.get(id).get(date).setQty(newQty);
						
						
							double newPur = 0d;
							double newSell = 0d;
							double newNet = 0d;
							
							double purchase = items.getPurchasedprice().doubleValue();
							newPur = (purchase * items.getQty()) + products.get(id).get(date).getPurchasedprice().doubleValue();
							products.get(id).get(date).setPurchasedprice(new BigDecimal(newPur));
							
							double price = items.getSellingPrice().doubleValue();
							newSell = (price * items.getQty()) + products.get(id).get(date).getSellingPrice().doubleValue();
							products.get(id).get(date).setSellingPrice(new BigDecimal(newSell));
							
							double net1 = items.getNetprice().doubleValue();
							newNet = (net1 * items.getQty()) + products.get(id).get(date).getNetprice().doubleValue();
							products.get(id).get(date).setNetprice(new BigDecimal(newNet));
							
						
					}else{
						
						
							double newPur = 0d;
							double newSell = 0d;
							double newNet = 0d;
							
							double purchase = items.getPurchasedprice().doubleValue();
							newPur = purchase * items.getQty();
							items.setPurchasedprice(new BigDecimal(newPur));
							
							double price = items.getSellingPrice().doubleValue();
							newSell = price * items.getQty();
							items.setSellingPrice(new BigDecimal(newSell));
							
							double net1 = items.getNetprice().doubleValue();
							newNet = net1 * items.getQty();
							items.setNetprice(new BigDecimal(newNet));
							
						
						
						products.get(id).put(date, items);
					}
					
					
				}else{
					
					product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
					
					
						double newPur = 0d;
						double newSell = 0d;
						double newNet = 0d;
						
						double purchase = items.getPurchasedprice().doubleValue();
						newPur = purchase * items.getQty();
						items.setPurchasedprice(new BigDecimal(newPur));
						
						double price = items.getSellingPrice().doubleValue();
						newSell = price * items.getQty();
						items.setSellingPrice(new BigDecimal(newSell));
						
						double net1 = items.getNetprice().doubleValue();
						newNet = net1 * items.getQty();
						items.setNetprice(new BigDecimal(newNet));
					
					
					product.put(items.getDatesold(), items);
					products.put(items.getProduct().getProdid(), product);
				}
				
			}else{
				products = Collections.synchronizedMap(new HashMap<Long,Map<String, PurchasedItem>>());
				product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
				
				
					double newPur = 0d;
					double newSell = 0d;
					double newNet = 0d;
					
					double purchase = items.getPurchasedprice().doubleValue();
					newPur = purchase * items.getQty();
					items.setPurchasedprice(new BigDecimal(newPur));
					
					double price = items.getSellingPrice().doubleValue();
					newSell = price * items.getQty();
					items.setSellingPrice(new BigDecimal(newSell));
					
					double net1 = items.getNetprice().doubleValue();
					newNet = net1 * items.getQty();
					items.setNetprice(new BigDecimal(newNet));
				
				
				product.put(items.getDatesold(), items);
				products.put(items.getProduct().getProdid(), product);
				
			}
			
		}
		
		Map<Long, PurchasedItem> unSort = Collections.synchronizedMap(new HashMap<Long, PurchasedItem>());
		for(Long id : products.keySet()){
				
				
					PurchasedItem puritem = new PurchasedItem();
					double qty1 =0d;
					double newPur = 0d;
					double newSell = 0d;
					double newNet = 0d;
					
					int cnt =1;
					for(PurchasedItem prod : products.get(id).values()){
						qty1 += prod.getQty();
						
						newSell += prod.getSellingPrice().doubleValue();
						newPur += prod.getPurchasedprice().doubleValue();
						newNet += prod.getNetprice().doubleValue();
						
						if(cnt==1){
							puritem = prod;
						}
					}
					puritem.setQty(qty1);
					puritem.setPurchasedprice(new BigDecimal(newPur));
					puritem.setSellingPrice(new BigDecimal(newSell));
					puritem.setNetprice(new BigDecimal(newNet));
					unSort.put(id, puritem);
					
				
				
		}
		
		Map<Long, PurchasedItem> sort = new TreeMap<Long, PurchasedItem>(unSort);
		
		
		for(PurchasedItem proItem : sort.values()){
			Summary sum = new Summary();
			double priceTmp = 0d;
			double capTmp = 0d;
			double saleTmp = 0d;
			double netAmnt = 0d;
			
				sum.setF1(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()) + " - " + DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
				
				priceTmp = proItem.getSellingPrice().doubleValue();
				//sum.setF4(Currency.formatAmount(priceTmp));
				sum.setF4("***");
				
				sum.setF5(proItem.getQty()+"");
				
				capTmp = proItem.getPurchasedprice().doubleValue();
				sum.setF6(Currency.formatAmount(capTmp)); //base amount
				
				saleTmp = proItem.getSellingPrice().doubleValue();
				sum.setF7(Currency.formatAmount(saleTmp)); //selling amount
				
				 
				netAmnt = proItem.getNetprice().doubleValue();
				sum.setF8(Currency.formatAmount(netAmnt));
			
			sum.setF2(proItem.getProductName());
			sum.setF3(proItem.getUomSymbol());
			
			
			
			summaryData.add(sum);
			
			itemPrice += priceTmp;
			qty += proItem.getQty();
			capital += capTmp;
			sale += saleTmp;
			net += netAmnt;
			
			
		}
		
		}else {
		//detailed
			
			List<PurchasedItem> prodList = Collections.synchronizedList(new ArrayList<PurchasedItem>());
			Map<String, List<PurchasedItem>> prods = Collections.synchronizedMap(new HashMap<String, List<PurchasedItem>>());
			
			for(PurchasedItem items : PurchasedItem.retrieve(item)){
				String date = items.getDatesold();
				
				if(prods!=null && prods.size()>0) {
					
					if(prods.containsKey(date)) {
						prodList = prods.get(date);
						prodList.add(items);
						prods.put(date, prodList);
					}else {
						prodList = Collections.synchronizedList(new ArrayList<PurchasedItem>());
						prodList.add(items);
						prods.put(date, prodList);
					}
					
				}else {
					prodList.add(items);
					prods.put(date, prodList);
				}
				
			}
			Map<String, List<PurchasedItem>> sort = new TreeMap<String, List<PurchasedItem>>(prods);
			
			for(String date : sort.keySet()) {
				for(PurchasedItem items : sort.get(date)){
					Summary sum = new Summary();
					double priceTmp = 0d;
					double capTmp = 0d;
					double saleTmp = 0d;
					double netAmnt = 0d;
					
					sum.setF1(items.getDatesold());
					
					priceTmp = items.getSellingPrice().doubleValue();
					sum.setF4(Currency.formatAmount(priceTmp));
					
					sum.setF5(items.getQty()+"");
					
					capTmp = items.getPurchasedprice().doubleValue() * items.getQty();
					sum.setF6(Currency.formatAmount(capTmp)); //base amount
					
					saleTmp = items.getSellingPrice().doubleValue() * items.getQty();
					sum.setF7(Currency.formatAmount(saleTmp)); //selling amount
					
					 
					netAmnt = items.getNetprice().doubleValue() * items.getQty();
					sum.setF8(Currency.formatAmount(netAmnt));
					
					sum.setF2(items.getProductName());
					sum.setF3(items.getUomSymbol());
					
					
					
					//summaryData.add(sum);
					
					itemPrice += priceTmp;
					qty += items.getQty();
					capital += capTmp;
					sale += saleTmp;
					net += netAmnt;
					
					
					if(sumDataDetailed!=null && sumDataDetailed.size()>0) {
						if(sumDataDetailed.containsKey(items.getDatesold())) {
							summaryDetailed = sumDataDetailed.get(items.getDatesold());
							summaryDetailed.add(sum);
							sumDataDetailed.put(items.getDatesold(), summaryDetailed);
						}else {
							summaryDetailed = Collections.synchronizedList(new ArrayList<Summary>());
							summaryDetailed.add(sum);
							sumDataDetailed.put(items.getDatesold(), summaryDetailed);
						}
					}else {
						summaryDetailed.add(sum);
						sumDataDetailed.put(items.getDatesold(), summaryDetailed);
					}
					
				}
				
			}
			
			
		}
		
		
		
		AddOnStore on = new AddOnStore();
		on.setIsActive(1);
		on.setBetween(true);
		on.setDateFrom(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()));
		on.setDateTo(DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
		on.setStatus(Status.POSTED.getId());
		double addOnNet = 0d;
		for(AddOnStore st : AddOnStore.retrieve(on)){
			Summary sum = new Summary();
			
			
			sum.setF2(st.getDescription());
			sum.setF3("n/a");
			
			double priceTmp = st.getAmount();
			sum.setF4(Currency.formatAmount(priceTmp));
			if(getSummaryFilterId()==1){ //summary
				sum.setF4("***");
			}
			sum.setF5("1");
			
			double capTmp = st.getAmount();
			sum.setF6(Currency.formatAmount(capTmp)); //base amount
			
			double saleTmp = st.getAmount();
			sum.setF7(Currency.formatAmount(saleTmp)); //selling amount
			
			 
			double netAmnt = st.getAmount();
			sum.setF8(Currency.formatAmount(netAmnt));
			
			
			
			itemPrice += priceTmp;
			qty += 1;
			//capital += capTmp;
			sale += saleTmp;
			net += netAmnt;
			addOnNet += netAmnt;
			
			if(getSummaryFilterId()==1){ //summary
				sum.setF1(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()) + " - " + DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
				summaryData.add(sum);
				itemPrice = 0;
			}else if(getSummaryFilterId()==2){ //detailed
				sum.setF1(st.getDateTrans());
				
				
				if(sumDataDetailed!=null && sumDataDetailed.size()>0) {
					if(sumDataDetailed.containsKey(st.getDateTrans())) {
						summaryDetailed = sumDataDetailed.get(st.getDateTrans());
						summaryDetailed.add(sum);
						sumDataDetailed.put(st.getDateTrans(), summaryDetailed);
					}else {
						summaryDetailed = Collections.synchronizedList(new ArrayList<Summary>());
						summaryDetailed.add(sum);
						sumDataDetailed.put(st.getDateTrans(), summaryDetailed);
					}
				}else {
					summaryDetailed.add(sum);
					sumDataDetailed.put(st.getDateTrans(), summaryDetailed);
				}
				
			}
			
		}
		
		totalSummaryQty = qty;
		totalSummaryItemPrice = Currency.formatAmount(itemPrice);
		totalSummaryCapital = Currency.formatAmount(capital);
		totalSummarySale = Currency.formatAmount(sale);
		totalSummaryNet = Currency.formatAmount(net);
		
		
		if(getSummaryFilterId()==2){ //detailed
			setSummaryData(Collections.synchronizedList(new ArrayList<Summary>()));
			Map<String, List<Summary>> sort = new TreeMap<String, List<Summary>>(sumDataDetailed);
			for(String date : sort.keySet()) {
				for(Summary sum : sort.get(date)) {
					summaryData.add(sum);
				}
			}
		}
	}
	
	@Deprecated
	private void store(){
		PurchasedItem item = new PurchasedItem();
		item.setIsactiveitem(1);
		item.setIsBetweenDate(true);
		item.setDateFrom(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()));
		item.setDateTo(DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
		
		Map<String, PurchasedItem> product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
		Map<Long, Map<String, PurchasedItem>> products = Collections.synchronizedMap(new HashMap<Long,Map<String, PurchasedItem>>());
		
		for(PurchasedItem items : PurchasedItem.retrieve(item)){
			
			if(products!=null && products.size()>0){
				
				long id = items.getProduct().getProdid();
				
				if(products.containsKey(id)){
					
					String date = items.getDatesold();
					
					if(products.get(id).containsKey(date)){
						
						double addQty = 0d;
						double oldQty = 0d;
						double newQty = 0d;
						addQty = items.getQty();
						oldQty = products.get(id).get(date).getQty();
						newQty = oldQty + addQty;
						products.get(id).get(date).setQty(newQty);
						
						if(getSummaryFilterId()==1){ //summary
							BigDecimal newPur = new BigDecimal("0");
							BigDecimal newSell = new BigDecimal("0");
							BigDecimal newNet = new BigDecimal("0");
							
							BigDecimal purchase = items.getPurchasedprice();
							newPur = purchase.add(products.get(id).get(date).getPurchasedprice());
							products.get(id).get(date).setPurchasedprice(newPur);
							
							BigDecimal price = items.getSellingPrice();
							newSell = price.add(products.get(id).get(date).getSellingPrice());
							products.get(id).get(date).setSellingPrice(newSell);
							
							BigDecimal net = items.getNetprice();
							newNet = net.add(products.get(id).get(date).getNetprice());
							products.get(id).get(date).setNetprice(newNet);
						}else {
							BigDecimal purchase = items.getPurchasedprice();
							products.get(id).get(date).setPurchasedprice(purchase);
							
							BigDecimal price = items.getSellingPrice();
							products.get(id).get(date).setSellingPrice(price);
							
							BigDecimal net = items.getNetprice();
							products.get(id).get(date).setNetprice(net);
						}
						
					}else{
						
						products.get(id).put(date, items);
					}
					
					
				}else{
					
					product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
					
					product.put(items.getDatesold(), items);
					products.put(items.getProduct().getProdid(), product);
				}
				
			}else{
				products = Collections.synchronizedMap(new HashMap<Long,Map<String, PurchasedItem>>());
				product = Collections.synchronizedMap(new HashMap<String, PurchasedItem>());
				
				product.put(items.getDatesold(), items);
				products.put(items.getProduct().getProdid(), product);
				
			}
			
		}
		
		Map<Long, PurchasedItem> unSort = Collections.synchronizedMap(new HashMap<Long, PurchasedItem>());
		for(Long id : products.keySet()){
				
				if(getSummaryFilterId()==1){ //summary
					PurchasedItem puritem = new PurchasedItem();
					double qty =0d;
					int cnt =1;
					for(PurchasedItem prod : products.get(id).values()){
						qty += prod.getQty();
						if(cnt==1){
							puritem = prod;
						}
					}
					puritem.setQty(qty);
					unSort.put(id, puritem);
					
				}else if(getSummaryFilterId()==2){ //detailed
					for(PurchasedItem prod : products.get(id).values()){
						unSort.put(prod.getItemid(), prod);
					}
				}
				
		}
		
		Map<Long, PurchasedItem> sort = new TreeMap<Long, PurchasedItem>(unSort);
		double itemPrice=0d,capital = 0d, sale=0d, net =0d,qty=0d;
		
		for(PurchasedItem proItem : sort.values()){
			Summary sum = new Summary();
			double priceTmp = 0d;
			double capTmp = 0d;
			double saleTmp = 0d;
			double netAmnt = 0d;
			if(getSummaryFilterId()==1){ //summary
				sum.setF1(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()) + " - " + DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
				
				priceTmp = proItem.getSellingPrice().doubleValue();
				sum.setF4(Currency.formatAmount(priceTmp));
				
				sum.setF5(proItem.getQty()+"");
				
				capTmp = proItem.getPurchasedprice().doubleValue();
				sum.setF6(Currency.formatAmount(capTmp)); //base amount
				
				saleTmp = proItem.getSellingPrice().doubleValue();
				sum.setF7(Currency.formatAmount(saleTmp)); //selling amount
				
				 
				netAmnt = proItem.getNetprice().doubleValue();
				sum.setF8(Currency.formatAmount(netAmnt));
			}else if(getSummaryFilterId()==2){ //detailed
				sum.setF1(proItem.getDatesold());
				
				priceTmp = proItem.getSellingPrice().doubleValue();
				sum.setF4(Currency.formatAmount(priceTmp));
				
				sum.setF5(proItem.getQty()+"");
				
				capTmp = proItem.getPurchasedprice().doubleValue() * proItem.getQty();
				sum.setF6(Currency.formatAmount(capTmp)); //base amount
				
				saleTmp = proItem.getSellingPrice().doubleValue() * proItem.getQty();
				sum.setF7(Currency.formatAmount(saleTmp)); //selling amount
				
				 
				netAmnt = proItem.getNetprice().doubleValue() * proItem.getQty();
				sum.setF8(Currency.formatAmount(netAmnt));
				
			}
			sum.setF2(proItem.getProductName());
			sum.setF3(proItem.getUomSymbol());
			
			
			
			summaryData.add(sum);
			
			itemPrice += priceTmp;
			qty += proItem.getQty();
			capital += capTmp;
			sale += saleTmp;
			net += netAmnt;
		}
		
		AddOnStore on = new AddOnStore();
		on.setIsActive(1);
		on.setBetween(true);
		on.setDateFrom(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()));
		on.setDateTo(DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
		on.setStatus(Status.POSTED.getId());
		for(AddOnStore st : AddOnStore.retrieve(on)){
			Summary sum = new Summary();
			if(getSummaryFilterId()==1){ //summary
				sum.setF1(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()) + " - " + DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
			}else if(getSummaryFilterId()==2){ //detailed
				sum.setF1(st.getDateTrans());
			}
			
			sum.setF2(st.getDescription());
			sum.setF3("n/a");
			
			double priceTmp = st.getAmount();
			sum.setF4(Currency.formatAmount(priceTmp));
			
			sum.setF5("1");
			
			double capTmp = st.getAmount();
			sum.setF6(Currency.formatAmount(capTmp)); //base amount
			
			double saleTmp = st.getAmount();
			sum.setF7(Currency.formatAmount(saleTmp)); //selling amount
			
			 
			double netAmnt = st.getAmount();
			sum.setF8(Currency.formatAmount(netAmnt));
			
			summaryData.add(sum);
			
			itemPrice += priceTmp;
			qty += 1;
			capital += capTmp;
			sale += saleTmp;
			net += netAmnt;
			
		}
		
		totalSummaryQty = qty;
		totalSummaryItemPrice = Currency.formatAmount(itemPrice);
		totalSummaryCapital = Currency.formatAmount(capital);
		totalSummarySale = Currency.formatAmount(sale);
		totalSummaryNet = Currency.formatAmount(net);
		
	}
	
	private void delivery(){
		
		DeliveryItemTrans tran = new DeliveryItemTrans();
		tran.setIsActive(1);
		tran.setStatus(DeliveryStatus.POSTED_SOLD_ITEM.getId());
		tran.setBetween(true);
		tran.setDateFrom(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()));
		tran.setDateTo(DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()));
		
		Map<String, DeliveryItemTrans> product = Collections.synchronizedMap(new HashMap<String, DeliveryItemTrans>());
		Map<Long, Map<String, DeliveryItemTrans>> products = Collections.synchronizedMap(new HashMap<Long,Map<String, DeliveryItemTrans>>());
		
		for(DeliveryItemTrans trn : DeliveryItemTrans.retrieve(tran)){
			
			if(products!=null && products.size()>0){
				
				long id = trn.getProduct().getProdid();
				
				if(products.containsKey(id)){
					
					if(products.get(id).containsKey(trn.getDateTrans())){
						
						double newQty = products.get(id).get(trn.getDateTrans()).getQuantity() + trn.getQuantity();
						double sellingAmnt = products.get(id).get(trn.getDateTrans()).getSellingPrice() + trn.getSellingPrice();
						products.get(id).get(trn.getDateTrans()).setQuantity(newQty);
						products.get(id).get(trn.getDateTrans()).setSellingPrice(sellingAmnt);
						
					}else{
						
						ProductPricingTrans price = ProductPricingTrans.retrievePrice(trn.getProduct().getProdid()+"");
						trn.getProduct().setProductPricingTrans(price);
						ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
						trn.getProduct().setProductProperties(prop);
						products.get(id).put(trn.getDateTrans(), trn);
						
					}
					
				}else{
					product = Collections.synchronizedMap(new HashMap<String, DeliveryItemTrans>());
					
					ProductPricingTrans price = ProductPricingTrans.retrievePrice(trn.getProduct().getProdid()+"");
					trn.getProduct().setProductPricingTrans(price);
					ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
					trn.getProduct().setProductProperties(prop);
					
					product.put(trn.getDateTrans(), trn);
					products.put(trn.getProduct().getProdid(), product);
				}
				
			}else{
				
				ProductPricingTrans price = ProductPricingTrans.retrievePrice(trn.getProduct().getProdid()+"");
				trn.getProduct().setProductPricingTrans(price);
				ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
				trn.getProduct().setProductProperties(prop);
				
				product.put(trn.getDateTrans(), trn);
				products.put(trn.getProduct().getProdid(), product);
			}
			
			
		}
		Map<Long, DeliveryItemTrans> unSort = Collections.synchronizedMap(new HashMap<Long, DeliveryItemTrans>());
		for(Long id : products.keySet() ){
			
			if(getSummaryFilterId()==1){ //summary
				double qty=0d;
				//combine the same product
				DeliveryItemTrans prodTrans = new DeliveryItemTrans();
				int cnt =1;
				for(DeliveryItemTrans tn : products.get(id).values()){
					qty += tn.getQuantity();
					if(cnt==1){
						prodTrans = tn;
					}
				}
				prodTrans.setQuantity(qty);
				unSort.put(id, prodTrans);
				
			}else{ //detailed
				
				for(DeliveryItemTrans tn : products.get(id).values()){
					unSort.put(tn.getId(), tn);
				}
			}
			
			
		}
		double itemPrice=0d, qty = 0d, capital=0d, sale=0d, net=0d;  
		Map<Long, DeliveryItemTrans> sort = new TreeMap<Long, DeliveryItemTrans>(unSort);
		for(DeliveryItemTrans trn : sort.values()){
			Summary sum = new Summary();
			if(getSummaryFilterId()==1){ //summary
				sum.setF1(getDateFromSummary() + " - " + getDateToSummary());
			}else if(getSummaryFilterId()==2){ //detailed
				sum.setF1(trn.getDateTrans());
			}
			sum.setF2(trn.getProduct().getProductProperties().getProductname());
			sum.setF3(trn.getProduct().getProductProperties().getUom().getSymbol());
			
			double priceTmp = trn.getProduct().getProductPricingTrans().getSellingprice().doubleValue();
			sum.setF4(Currency.formatAmount(priceTmp));
			
			sum.setF5(trn.getQuantity()+"");
			
			double capTmp = trn.getProduct().getProductPricingTrans().getPurchasedprice().doubleValue() * trn.getQuantity();
			sum.setF6(Currency.formatAmount(capTmp)); //base amount
			
			double saleTmp = trn.getProduct().getProductPricingTrans().getSellingprice().doubleValue() * trn.getQuantity();
			sum.setF7(Currency.formatAmount(saleTmp)); //selling amount
			
			 
			double netAmnt = trn.getProduct().getProductPricingTrans().getNetprice().doubleValue() * trn.getQuantity();
			sum.setF8(Currency.formatAmount(netAmnt));
			
			summaryData.add(sum);
			
			itemPrice += priceTmp;
			qty += trn.getQuantity();
			capital += capTmp;
			sale += saleTmp;
			net += netAmnt;
		}
		
		totalSummaryQty = qty;
		totalSummaryItemPrice = Currency.formatAmount(itemPrice);
		totalSummaryCapital = Currency.formatAmount(capital);
		totalSummarySale = Currency.formatAmount(sale);
		totalSummaryNet = Currency.formatAmount(net);
	}
	
	public List<PurchasedItem> getItems() {
		return items;
	}

	public void setItems(List<PurchasedItem> items) {
		this.items = items;
	}

	public Date getIncomesDateFrom() {
		if(incomesDateFrom==null){
			incomesDateFrom = DateUtils.getDateToday();
		}
		return incomesDateFrom;
	}

	public void setIncomesDateFrom(Date incomesDateFrom) {
		this.incomesDateFrom = incomesDateFrom;
	}

	public Date getIncomesDateTo() {
		if(incomesDateTo==null){
			incomesDateTo= DateUtils.getDateToday();
		}
		return incomesDateTo;
	}

	public void setIncomesDateTo(Date incomesDateTo) {
		this.incomesDateTo = incomesDateTo;
	}

	public String getIncomesProductName() {
		return incomesProductName;
	}

	public void setIncomesProductName(String incomesProductName) {
		this.incomesProductName = incomesProductName;
	}
	
	public List<Incomes> getSoldItems() {
		return soldItems;
	}

	public void setSoldItems(List<Incomes> soldItems) {
		this.soldItems = soldItems;
	}

	public String getIncomesPlusVatTotal() {
		return incomesPlusVatTotal;
	}

	public void setIncomesPlusVatTotal(String incomesPlusVatTotal) {
		this.incomesPlusVatTotal = incomesPlusVatTotal;
	}

	public String getIncomesMinususVatTotal() {
		return incomesMinususVatTotal;
	}

	public void setIncomesMinususVatTotal(String incomesMinususVatTotal) {
		this.incomesMinususVatTotal = incomesMinususVatTotal;
	}

	public String getCapitalTotal() {
		return capitalTotal;
	}

	public void setCapitalTotal(String capitalTotal) {
		this.capitalTotal = capitalTotal;
	}

	public String getSalesTotal() {
		return salesTotal;
	}

	public void setSalesTotal(String salesTotal) {
		this.salesTotal = salesTotal;
	}

	public String getNetTotal() {
		return netTotal;
	}

	public void setNetTotal(String netTotal) {
		this.netTotal = netTotal;
	}

	public List<DeliveryItemReceipt> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<DeliveryItemReceipt> receipts) {
		this.receipts = receipts;
	}

	public Date getReceiptDateFrom() {
		if(receiptDateFrom==null){
			receiptDateFrom = DateUtils.getDateToday();
		}
		return receiptDateFrom;
	}

	public void setReceiptDateFrom(Date receiptDateFrom) {
		this.receiptDateFrom = receiptDateFrom;
	}

	public Date getReceiptDateTo() {
		if(receiptDateTo==null){
			receiptDateTo = DateUtils.getDateToday();
		}
		return receiptDateTo;
	}

	public void setReceiptDateTo(Date receiptDateTo) {
		this.receiptDateTo = receiptDateTo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPurchasedTotal() {
		return purchasedTotal;
	}

	public void setPurchasedTotal(String purchasedTotal) {
		this.purchasedTotal = purchasedTotal;
	}

	public String getBalanceTotal() {
		return balanceTotal;
	}

	public void setBalanceTotal(String balanceTotal) {
		this.balanceTotal = balanceTotal;
	}

	public String getDiscountTotal() {
		return discountTotal;
	}

	public void setDiscountTotal(String discountTotal) {
		this.discountTotal = discountTotal;
	}

	public String getDownpaymentTotal() {
		return downpaymentTotal;
	}

	public void setDownpaymentTotal(String downpaymentTotal) {
		this.downpaymentTotal = downpaymentTotal;
	}

	public String getChargeTotal() {
		return chargeTotal;
	}

	public void setChargeTotal(String chargeTotal) {
		this.chargeTotal = chargeTotal;
	}

	public List<CustomerPaymentTrans> getCashs() {
		return cashs;
	}

	public void setCashs(List<CustomerPaymentTrans> cashs) {
		this.cashs = cashs;
	}

	public Date getCashDateFrom() {
		if(cashDateFrom==null){
			cashDateFrom = DateUtils.getDateToday();
		}
		return cashDateFrom;
	}

	public void setCashDateFrom(Date cashDateFrom) {
		this.cashDateFrom = cashDateFrom;
	}

	public Date getCashDateTo() {
		if(cashDateTo==null){
			cashDateTo = DateUtils.getDateToday();
		}
		return cashDateTo;
	}

	public void setCashDateTo(Date cashDateTo) {
		this.cashDateTo = cashDateTo;
	}

	public String getCashName() {
		return cashName;
	}

	public void setCashName(String cashName) {
		this.cashName = cashName;
	}

	public String getCashTotal() {
		return cashTotal;
	}

	public void setCashTotal(String cashTotal) {
		this.cashTotal = cashTotal;
	}

	public String getPriceTotal() {
		return priceTotal;
	}

	public void setPriceTotal(String priceTotal) {
		this.priceTotal = priceTotal;
	}

	public double getVatTotal() {
		return vatTotal;
	}

	public void setVatTotal(double vatTotal) {
		this.vatTotal = vatTotal;
	}

	public double getQtyTotal() {
		return qtyTotal;
	}

	public void setQtyTotal(double qtyTotal) {
		this.qtyTotal = qtyTotal;
	}

	public List<Expenses> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Expenses> expenses) {
		this.expenses = expenses;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getExpenseDateFrom() {
		if(expenseDateFrom==null){
			expenseDateFrom = DateUtils.getDateToday();
		}
		return expenseDateFrom;
	}

	public void setExpenseDateFrom(Date expenseDateFrom) {
		this.expenseDateFrom = expenseDateFrom;
	}

	public Date getExpenseDateTo() {
		if(expenseDateTo==null){
			expenseDateTo = DateUtils.getDateToday();
		}
		return expenseDateTo;
	}

	public void setExpenseDateTo(Date expenseDateTo) {
		this.expenseDateTo = expenseDateTo;
	}

	public String getExpensesTotal() {
		return expensesTotal;
	}

	public void setExpensesTotal(String expensesTotal) {
		this.expensesTotal = expensesTotal;
	}

	public List<Payable> getPayables() {
		return payables;
	}

	public void setPayables(List<Payable> payables) {
		this.payables = payables;
	}

	public String getPayDescription() {
		return payDescription;
	}

	public void setPayDescription(String payDescription) {
		this.payDescription = payDescription;
	}

	public Date getPayDateFrom() {
		return payDateFrom;
	}

	public void setPayDateFrom(Date payDateFrom) {
		this.payDateFrom = payDateFrom;
	}

	public Date getPayDateTo() {
		return payDateTo;
	}

	public void setPayDateTo(Date payDateTo) {
		this.payDateTo = payDateTo;
	}

	public String getPayTotal() {
		return payTotal;
	}

	public void setPayTotal(String payTotal) {
		this.payTotal = payTotal;
	}

	public List<Receivable> getReceivables() {
		return receivables;
	}

	public void setReceivables(List<Receivable> receivables) {
		this.receivables = receivables;
	}

	public String getRecDescription() {
		return recDescription;
	}

	public void setRecDescription(String recDescription) {
		this.recDescription = recDescription;
	}

	public String getRecDateFrom() {
		return recDateFrom;
	}

	public void setRecDateFrom(String recDateFrom) {
		this.recDateFrom = recDateFrom;
	}

	public String getRecDateTo() {
		return recDateTo;
	}

	public void setRecDateTo(String recDateTo) {
		this.recDateTo = recDateTo;
	}

	public String getRecTotal() {
		return recTotal;
	}

	public void setRecTotal(String recTotal) {
		this.recTotal = recTotal;
	}

	public List<MoneyIO> getIos() {
		return ios;
	}

	public void setIos(List<MoneyIO> ios) {
		this.ios = ios;
	}

	public String getIoDescription() {
		return ioDescription;
	}

	public void setIoDescription(String ioDescription) {
		this.ioDescription = ioDescription;
	}

	public Date getIoDateFrom() {
		if(ioDateFrom==null){
			ioDateFrom = DateUtils.getDateToday();
		}
		return ioDateFrom;
	}

	public void setIoDateFrom(Date ioDateFrom) {
		this.ioDateFrom = ioDateFrom;
	}

	public Date getIoDateTo() {
		if(ioDateTo==null){
			ioDateTo = DateUtils.getDateToday();
		}
		return ioDateTo;
	}

	public void setIoDateTo(Date ioDateTo) {
		this.ioDateTo = ioDateTo;
	}

	public String getIoExpenses() {
		return ioExpenses;
	}

	public void setIoExpenses(String ioExpenses) {
		this.ioExpenses = ioExpenses;
	}

	public String getIoIncome() {
		return ioIncome;
	}

	public void setIoIncome(String ioIncome) {
		this.ioIncome = ioIncome;
	}

	public String getCashOnHandAmount() {
		return cashOnHandAmount;
	}

	public void setCashOnHandAmount(String cashOnHandAmount) {
		this.cashOnHandAmount = cashOnHandAmount;
	}

	public Date getDateFromSummary() {
		
		if(dateFromSummary==null){
			int dateToday = DateUtils.getCurrentMonth();
			int year = DateUtils.getCurrentYear();
			dateFromSummary= DateUtils.convertDateString(year + "-" + monthToday(dateToday) + "-01",DateFormatter.YYYY_MM_DD.getName());
		}
		
		return dateFromSummary;
	}

	public void setDateFromSummary(Date dateFromSummary) {
		this.dateFromSummary = dateFromSummary;
	}

	public Date getDateToSummary() {
		if(dateToSummary==null){
			/*int dateToday = DateUtils.getCurrentMonth();
			int year = DateUtils.getCurrentYear();
			dateToSummary= year + "-" + monthToday(dateToday) + "-31";*/
			dateToSummary = DateUtils.convertDateString(DateUtils.getLastDayOfTheMonth("yyyy-MM-dd", DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN),DateFormatter.YYYY_MM_DD.getName());
		}
		return dateToSummary;
	}

	public void setDateToSummary(Date dateToSummary) {
		this.dateToSummary = dateToSummary;
	}
	
	public int getTransTypeId() {
		if(transTypeId==0){
			transTypeId = 2; // store
		}
		return transTypeId;
	}

	public void setTransTypeId(int transTypeId) {
		this.transTypeId = transTypeId;
	}

	public List getTranTypes() {
		
		tranTypes = new ArrayList<>();
		tranTypes.add(new SelectItem(1, "DELIVERY"));
		tranTypes.add(new SelectItem(2, "STORE"));
		
		return tranTypes;
	}

	public void setTranTypes(List tranTypes) {
		this.tranTypes = tranTypes;
	}

	private String monthToday(int month){
		
		if(month<=9){
			return "0" + month;
		}else{
			return "" + month;
		}
		
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Ipos.REPORT);
	private static final String REPORT_NAME = ReadXML.value(ReportTag.ACCOUNTING_SUMMARY);
	
	public void printSummary(){
		List<Reports> reports = Collections.synchronizedList(new ArrayList<>());
		
		//Creating header
		Reports report = new Reports();
		report.setF1("Date");
		report.setF2("Product");
		report.setF3("UOM");
		report.setF4("Price");
		report.setF5("Qty");
		report.setF6("Sale");
		report.setF7("Net");
		reports.add(report);
		
		for(Summary rpt : getSummaryData()){
			Reports rep = new Reports();
			rep.setF1(rpt.getF1());
			rep.setF2(rpt.getF2());
			rep.setF3(rpt.getF3());
			rep.setF4(rpt.getF4());
			rep.setF5(rpt.getF5());
			rep.setF6(rpt.getF7());
			rep.setF7(rpt.getF8());
			reports.add(rep);
		}
		
		
		//Grand Total Field
		report = new Reports();
		report.setF1("Grand Total");
		report.setF5(totalSummaryQty+"");
		report.setF6("Php"+totalSummarySale);
		report.setF7("Php"+totalSummaryNet);
		reports.add(report);
		
		String sql = "SELECT * FROM iomoney WHERE (iodatetrans>=? AND iodatetrans<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName());
		params[1] = DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName());
		double income = 0d, expenses =0d, cashonhand = 0d;
		for(MoneyIO io : MoneyIO.retrieve(sql, params)){
			
			if(MoneyStatus.INCOME.getId()==io.getTransType() ||
					MoneyStatus.OTHER_INCOME.getId()==io.getTransType() ||
					MoneyStatus.ADD_CAPITAL.getId()==io.getTransType() ||
					MoneyStatus.OTC.getId()==io.getTransType() ||
					MoneyStatus.RENTED.getId()==io.getTransType() ||
					MoneyStatus.RETURN_CAPITAL.getId() ==io.getTransType()
					){
				
				income += io.getInAmount();
				
			}else if(MoneyStatus.EXPENSES.getId()==io.getTransType() ||
					MoneyStatus.ALLOWANCES.getId() ==io.getTransType() ||
					MoneyStatus.CASH_LOAN.getId() ==io.getTransType() || 
					MoneyStatus.OTHER_EXP.getId() ==io.getTransType() ||
					MoneyStatus.REFUND.getId() ==io.getTransType() ||
					MoneyStatus.SALARY.getId() ==io.getTransType()
					
					){
				
				expenses += io.getOutAmount();
				
			}
			
		}
		
		//create blank row
		report = new Reports();
		reports.add(report);
		
		//Cash Collection
		report = new Reports();
		report.setF1("Cash Collections");
		report.setF2("Php"+Currency.formatAmount(income));
		reports.add(report);
		//Expenses
		report = new Reports();
		report.setF1("Expenses");
		report.setF2("Php"+Currency.formatAmount(expenses));
		reports.add(report);
		//Expenses
		cashonhand = income - expenses;
		report = new Reports();
		report.setF1("Cash On Hand");
		report.setF2("Php"+Currency.formatAmount(cashonhand));
		reports.add(report);
		
		//Receivable
		report = new Reports();
		report.setF1("Receivable");
		reports.add(report);
		
		CustomerPayment cus = new CustomerPayment();
		cus.setPayisactive(1);
		
		Customer cz = new Customer();
		cz.setIsactive(1);
		
		if(getRecDescription()!=null && !getRecDescription().isEmpty()){
			cz.setFullname(getRecDescription().replace("--", ""));
		}
		
		double recTotal=0d;
		int id=1;
		for(CustomerPayment py : CustomerPayment.retrieve(cus,cz)){
			if(py.getAmountbalance().doubleValue()>0){
				Reports rpt = new Reports();
				recTotal += py.getAmountbalance().doubleValue();
				rpt.setF2(id++ + ") " +py.getCustomer().getFullname());
				rpt.setF4("Php"+Currency.formatAmount(py.getAmountbalance().doubleValue()));
				reports.add(rpt);
			}
		}
		
		//Receivable Grand Total
		report = new Reports();
		report.setF2("Grand Total");
		report.setF4("Php"+Currency.formatAmount(recTotal));
		reports.add(report);
		
		//Payable
		report = new Reports();
		report.setF1("Payable");
		reports.add(report);
		
		SupplierTrans tran = new SupplierTrans();
		tran.setIsActive(1);
		tran.setStatus(1);
		
		double payTotal = 0d;
		int cnt=1;
		for(SupplierTrans tn : SupplierTrans.retrieve(tran)){
			Reports rpt = new Reports();
			
			double amount = 0d, paidAmnt = 0d;;
			SupplierTrans tnx = new SupplierTrans();
			tnx.setIsActive(1);
			tnx.setId(tn.getId());
			
			SupplierPayment pay = new SupplierPayment();
			pay.setIsActive(1);
			
			for(SupplierPayment py : SupplierPayment.retrieve(pay,tnx)){
				paidAmnt += py.getAmount();
			}
			
			amount = tn.getPurchasedPrice() - paidAmnt;
			
			payTotal += amount;
			//rpt.setF1(""+cnt++);
			rpt.setF2(cnt++ + ") " + tn.getSupplier().getSuppliername());
			rpt.setF4(Currency.formatAmount(amount));
			reports.add(rpt);
		}
		
		//Payable Grand Total
		report = new Reports();
		report.setF2("Grand Total");
		report.setF4(Currency.formatAmount(payTotal));
		reports.add(report);
		
		reports = filterPerMunicipalityFromReceipts(DateUtils.convertDate(getDateFromSummary(),DateFormatter.YYYY_MM_DD.getName()), DateUtils.convertDate(getDateToSummary(),DateFormatter.YYYY_MM_DD.getName()), reports);
		
		/**
		 * Above output should be like below format
		 * Product Sold
		 * Cash Collections
		 * Expenses
		 * Cash On Hand
		 * Receivable
		 * 		<list of receivable>
		 * Payable
		 * 		<list of payable>
		 */
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
  		param.put("PARAM_BUSINESS_NAME", ReadConfig.value(Ipos.BUSINESS_NAME));
  		String preparedBy = "";
  		try{
  			preparedBy = Login.getUserLogin().getUserDtls().getFirstname() + " " + Login.getUserLogin().getUserDtls().getLastname();
  		}catch(Exception e){}
  		param.put("PARAM_PREPAREDBY", "Prepared By: "+preparedBy);
  		param.put("PARAM_DATE", "Printed:"+DateUtils.getCurrentDateMMDDYYYYTIME());
  		
  		/*param.put("PARAM_QTY", totalSummaryQty+"");
  		param.put("PARAM_TOTAL_SALE", totalSummarySale);
  		param.put("PARAM_TOTAL_NET", totalSummaryNet);*/
  		
  		//System.out.println("qty: " + totalSummaryQty + " sale " + totalSummarySale + " net " + totalSummaryNet);
  		
  		
  			try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
	  		}catch(Exception e){e.printStackTrace();}
		
  			try{
  				System.out.println("REPORT_PATH:" + REPORT_PATH + "REPORT_NAME: " + REPORT_NAME);
		  		 File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
				 FacesContext faces = FacesContext.getCurrentInstance();
				 ExternalContext context = faces.getExternalContext();
				 HttpServletResponse response = (HttpServletResponse)context.getResponse();
					
			     BufferedInputStream input = null;
			     BufferedOutputStream output = null;
			     
			     try{
			    	 
			    	 // Open file.
			            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			            int length;
			            while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }

			            // Finalize task.
			            output.flush();
			    	 
			     }finally{
			    	// Gently close streams.
			            close(output);
			            close(input);
			     }
			     
			     // Inform JSF that it doesn't need to handle response.
			        // This is very important, otherwise you will get the following exception in the logs:
			        // java.lang.IllegalStateException: Cannot forward after response has been committed.
			        faces.responseComplete();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
	}
	
	private void close(Closeable resource) {
	    if (resource != null) {
	        try {
	            resource.close();
	        } catch (IOException e) {
	            // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
	            // know that this will generally only be thrown when the client aborted the download.
	            e.printStackTrace();
	        }
	    }
	}
	
	/*
	 * return municipality total sale
	 */
	private List<Reports> filterPerMunicipalityFromReceipts(String dateFrom, String dateTo, List<Reports> reports){
		//List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
		
		Customer cus = new Customer();
		cus.setIsactive(1);
		
		DeliveryItemReceipt rcpt = new DeliveryItemReceipt();
		rcpt.setIsActive(1);
		rcpt.setBetween(true);
		rcpt.setDateFrom(dateFrom);
		rcpt.setDateTo(dateTo);
		//integer - Municipality id
		Map<Integer, DeliveryItemReceipt> municipalityMap = Collections.synchronizedMap(new HashMap<Integer, DeliveryItemReceipt>());
		//List<DeliveryItemReceipt> receiptsData = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
		for(DeliveryItemReceipt recpts : DeliveryItemReceipt.retrieve(rcpt, cus)){
			
			Customer customer = Customer.retrieve(recpts.getCustomer().getCustomerid()+"");
			recpts.setCustomer(customer);
			int id = recpts.getCustomer().getMunicipality().getId();
			//commented due to error in calculation for barangay
			//receiptsData.add(recpts);//do not move this code - this line of code will be use for barangay filtering
			
			if(municipalityMap!=null && municipalityMap.size()>0){
				
				if(municipalityMap.containsKey(id)){
					
					double newPurchasedAmnt = 0d;
					double addPurchasedAmnt = recpts.getTotalAmount();
					double oldPurchasedAmnt = municipalityMap.get(id).getTotalAmount();
					newPurchasedAmnt = oldPurchasedAmnt + addPurchasedAmnt;
					municipalityMap.get(id).setTotalAmount(newPurchasedAmnt);
					
					double newCash = 0d;
					double addCash = recpts.getDownPayment();
					double oldCash = municipalityMap.get(id).getDownPayment();
					newCash = oldCash + addCash;
					municipalityMap.get(id).setDownPayment(newCash);
					
					double newDiscount = 0d;
					double addDiscount = recpts.getDiscountAmount();
					double oldDiscount = municipalityMap.get(id).getDiscountAmount();
					newDiscount = oldDiscount + addDiscount;
					municipalityMap.get(id).setDiscountAmount(newDiscount);
					
					double newBalance = 0d;
					double addBalance = recpts.getBalanceAmount();
					double oldBalance = municipalityMap.get(id).getBalanceAmount();
					newBalance = oldBalance + addBalance;
					municipalityMap.get(id).setBalanceAmount(newBalance);
					
				}else{
					municipalityMap.put(id, recpts);
				}
				
			}else{
				municipalityMap = Collections.synchronizedMap(new HashMap<Integer, DeliveryItemReceipt>());
				municipalityMap.put(id, recpts);
			}
			
			
		}
		
		Reports rep = new Reports();
		rep.setF1("Municipality");
		reports.add(rep);
		
		rep = new Reports();
		rep.setF2("Name");
		rep.setF3("*Purchased");
		rep.setF4("*Discount");
		rep.setF5("*Paid");
		rep.setF6("*Balance");
		reports.add(rep);
		
		for(Integer id : municipalityMap.keySet()){
			
			String municipality = municipalityMap.get(id).getCustomer().getMunicipality().getName();
			double totalPurchased = municipalityMap.get(id).getTotalAmount();
			double totalDiscount = municipalityMap.get(id).getDiscountAmount();
			double totalPaid = municipalityMap.get(id).getDownPayment();
			double totalBalance = municipalityMap.get(id).getBalanceAmount();
			
			Reports rpt = new Reports();
			rpt.setF2(municipality);
			rpt.setF3("Php"+Currency.formatAmount(totalPurchased));
			rpt.setF4("Php"+Currency.formatAmount(totalDiscount));
			rpt.setF5("Php"+Currency.formatAmount(totalPaid));
			rpt.setF6("Php"+Currency.formatAmount(totalBalance));
			reports.add(rpt);
			
		}
		
		rep = new Reports();
		rep.setF2("*Please note that above data is based on receipts");
		reports.add(rep);
		
		
		//filter per barangay
		Map<Integer, DeliveryItemReceipt> barangay = Collections.synchronizedMap(new HashMap<Integer, DeliveryItemReceipt>());
		Map<Integer, Map<Integer, DeliveryItemReceipt>> barangayData = Collections.synchronizedMap(new HashMap<Integer, Map<Integer, DeliveryItemReceipt>>());
		
		//for(DeliveryItemReceipt rpt : receipts){
		for(DeliveryItemReceipt rpt : DeliveryItemReceipt.retrieve(rcpt, cus)){
			
			Customer customer = Customer.retrieve(rpt.getCustomer().getCustomerid()+"");
			rpt.setCustomer(customer);
			
			int munId = rpt.getCustomer().getMunicipality().getId(); //municipal id
			int barId = rpt.getCustomer().getBarangay().getId(); // barangay id
			
			if(barangayData!=null && barangayData.size()>0){
				
				if(barangayData.containsKey(munId)){
					
					if(barangayData.get(munId).containsKey(barId)){
						
						double newPurchasedAmnt = 0d;
						double addPurchasedAmnt = rpt.getTotalAmount();
						double oldPurchasedAmnt = barangayData.get(munId).get(barId).getTotalAmount();
						newPurchasedAmnt = oldPurchasedAmnt + addPurchasedAmnt;
						barangayData.get(munId).get(barId).setTotalAmount(newPurchasedAmnt);
						
						double newCash = 0d;
						double addCash = rpt.getDownPayment();
						double oldCash = barangayData.get(munId).get(barId).getDownPayment();
						newCash = oldCash + addCash;
						barangayData.get(munId).get(barId).setDownPayment(newCash);
						
						double newDiscount = 0d;
						double addDiscount = rpt.getDiscountAmount();
						double oldDiscount = barangayData.get(munId).get(barId).getDiscountAmount();
						newDiscount = oldDiscount + addDiscount;
						barangayData.get(munId).get(barId).setDiscountAmount(newDiscount);
						
						double newBalance = 0d;
						double addBalance = rpt.getBalanceAmount();
						double oldBalance = barangayData.get(munId).get(barId).getBalanceAmount();
						newBalance = oldBalance + addBalance;
						barangayData.get(munId).get(barId).setBalanceAmount(newBalance); 
						
						
					}else{
						
						
						barangayData.get(munId).put(barId, rpt);
						
					}
					
				}else{
					
					barangay = Collections.synchronizedMap(new HashMap<Integer, DeliveryItemReceipt>());
					barangay.put(barId, rpt);
					barangayData.put(munId, barangay);
					
				}
				
			}else{
				barangayData = Collections.synchronizedMap(new HashMap<Integer, Map<Integer, DeliveryItemReceipt>>());
				barangay = Collections.synchronizedMap(new HashMap<Integer, DeliveryItemReceipt>());
				barangay.put(barId, rpt);
				barangayData.put(munId, barangay);
			}
			
		}
		
		/*rep = new Reports();
		rep.setF2("*Please note that above data is based on receipts");
		reports.add(rep);*/
		
		rep = new Reports();
		reports.add(rep);
		
		rep = new Reports();
		rep.setF2("Transactions per Barangay");
		reports.add(rep);
		
		rep = new Reports();
		rep.setF1("Municipality");
		rep.setF2("Barangay");
		rep.setF3("*Purchased");
		rep.setF4("*Discount");
		rep.setF5("*Paid");
		rep.setF6("*Balance");
		reports.add(rep);
		
		
		
		for(Integer munId : barangayData.keySet()){
			int cnt = 1;
			double purchasedTotalAmnt = 0d;
			double discountTotal = 0d;
			double cashTotal = 0d;
			double balanceTotal = 0d;
			
			for(DeliveryItemReceipt bar : barangayData.get(munId).values()){
				if(cnt==1){
					rep = new Reports();
					rep.setF1(bar.getCustomer().getMunicipality().getName());
					reports.add(rep);
				}
				
				double purchasedAmnt = bar.getTotalAmount();
				double discount = bar.getDiscountAmount();
				double cash = bar.getDownPayment();
				double balance = bar.getBalanceAmount();
				
				rep = new Reports();
				rep.setF2(bar.getCustomer().getBarangay().getName());
				rep.setF3("Php"+Currency.formatAmount(purchasedAmnt));
				rep.setF4("Php"+Currency.formatAmount(discount));
				rep.setF5("Php"+Currency.formatAmount(cash));
				rep.setF6("Php"+Currency.formatAmount(balance));
				reports.add(rep);
				
				purchasedTotalAmnt += purchasedAmnt;
				discountTotal += discount;
				cashTotal += cash;
				balanceTotal += balance;
				
				cnt++;
			}
			rep = new Reports();
			rep.setF2("Grand Total");
			rep.setF3("Php"+Currency.formatAmount(purchasedTotalAmnt));
			rep.setF4("Php"+Currency.formatAmount(discountTotal));
			rep.setF5("Php"+Currency.formatAmount(cashTotal));
			rep.setF6("Php"+Currency.formatAmount(balanceTotal));
			reports.add(rep);
			
			
		}
		rep = new Reports();
		rep.setF2("*Please note that above data is based on receipts");
		reports.add(rep);
		
		return reports;
	}
	
	
	public List<Summary> getSummaryData() {
		return summaryData;
	}

	public void setSummaryData(List<Summary> summaryData) {
		this.summaryData = summaryData;
	}

	public double getTotalSummaryQty() {
		return totalSummaryQty;
	}

	public void setTotalSummaryQty(double totalSummaryQty) {
		this.totalSummaryQty = totalSummaryQty;
	}

	public String getTotalSummaryItemPrice() {
		return totalSummaryItemPrice;
	}

	public void setTotalSummaryItemPrice(String totalSummaryItemPrice) {
		this.totalSummaryItemPrice = totalSummaryItemPrice;
	}

	public String getTotalSummaryCapital() {
		return totalSummaryCapital;
	}

	public void setTotalSummaryCapital(String totalSummaryCapital) {
		this.totalSummaryCapital = totalSummaryCapital;
	}

	public String getTotalSummarySale() {
		return totalSummarySale;
	}

	public void setTotalSummarySale(String totalSummarySale) {
		this.totalSummarySale = totalSummarySale;
	}

	public String getTotalSummaryNet() {
		return totalSummaryNet;
	}

	public void setTotalSummaryNet(String totalSummaryNet) {
		this.totalSummaryNet = totalSummaryNet;
	}

	public int getSummaryFilterId() {
		if(summaryFilterId==0){
			summaryFilterId = 1;
		}
		return summaryFilterId;
	}

	public void setSummaryFilterId(int summaryFilterId) {
		this.summaryFilterId = summaryFilterId;
	}

	public List getSummaryFilters() {
		summaryFilters = new ArrayList<>();
		
		summaryFilters.add(new SelectItem(1, "Summary"));
		summaryFilters.add(new SelectItem(2, "Detailed"));
		
		return summaryFilters;
	}

	public void setSummaryFilters(List summaryFilters) {
		this.summaryFilters = summaryFilters;
	}

	public List<ProductInventory> getWarehouseProduct() {
		return warehouseProduct;
	}

	public void setWarehouseProduct(List<ProductInventory> warehouseProduct) {
		this.warehouseProduct = warehouseProduct;
	}

	public List<DeliveryItem> getReturnItems() {
		return returnItems;
	}

	public void setReturnItems(List<DeliveryItem> returnItems) {
		this.returnItems = returnItems;
	}

	public String getReturnProductItemGrandTotal() {
		return returnProductItemGrandTotal;
	}

	public void setReturnProductItemGrandTotal(String returnProductItemGrandTotal) {
		this.returnProductItemGrandTotal = returnProductItemGrandTotal;
	}

	public String getRetrunProductItemPriceGrandTotal() {
		return retrunProductItemPriceGrandTotal;
	}

	public void setRetrunProductItemPriceGrandTotal(String retrunProductItemPriceGrandTotal) {
		this.retrunProductItemPriceGrandTotal = retrunProductItemPriceGrandTotal;
	}

	public double getReturnProductItemQtyGrandTotal() {
		return returnProductItemQtyGrandTotal;
	}

	public void setReturnProductItemQtyGrandTotal(double returnProductItemQtyGrandTotal) {
		this.returnProductItemQtyGrandTotal = returnProductItemQtyGrandTotal;
	}

	public double getWarehouseTotalQty() {
		return warehouseTotalQty;
	}

	public void setWarehouseTotalQty(double warehouseTotalQty) {
		this.warehouseTotalQty = warehouseTotalQty;
	}

	public List<StoreProduct> getStores() {
		return stores;
	}

	public void setStores(List<StoreProduct> stores) {
		this.stores = stores;
	}
	
	
	

}
