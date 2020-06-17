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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.http.HttpServletResponse;

import com.italia.ipos.application.Application;
import com.italia.ipos.application.ClientInfo;
import com.italia.ipos.controller.AddOnStore;
import com.italia.ipos.controller.Business;
import com.italia.ipos.controller.ChargeInvoice;
import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.CustomerPayment;
import com.italia.ipos.controller.CustomerPaymentTrans;
import com.italia.ipos.controller.CustomerPoints;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.Features;
import com.italia.ipos.controller.InputedInventoryQtyTracker;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.MoneyIO;
import com.italia.ipos.controller.Payment;
import com.italia.ipos.controller.PointsHistory;
import com.italia.ipos.controller.PointsRule;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductInventory;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.ProductRunning;
import com.italia.ipos.controller.PurchasedItem;
import com.italia.ipos.controller.QtyRunning;
import com.italia.ipos.controller.ReceiptInfo;
import com.italia.ipos.controller.StoreProduct;
import com.italia.ipos.controller.Terms;
import com.italia.ipos.controller.Transactions;
import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.controller.Xtras;
import com.italia.ipos.enm.HistoryReceiptStatus;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.MoneyStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.ProductStatus;
import com.italia.ipos.enm.ReceiptStatus;
import com.italia.ipos.enm.Status;
import com.italia.ipos.enm.UserAccess;
import com.italia.ipos.reader.DispenseReadWriteXML;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.reader.ReceiptXML;
import com.italia.ipos.reports.ReadXML;
import com.italia.ipos.reports.ReportCompiler;
import com.italia.ipos.reports.ReportTag;
import com.italia.ipos.utils.Currency;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;
import com.italia.ipos.utils.Whitelist;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author mark italia
 * @since 04/02/2017
 * @version 1.0
 */
@ManagedBean(name="cashBean")
@ViewScoped
public class CashierBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 165486676565465L;

	private String searchCode;
	private String searchProduct;
	private List<StoreProduct> prods = Collections.synchronizedList(new ArrayList<StoreProduct>());
	private String qtyToCart;
	private String qtyToCartTmp;
	private Map<Long, PurchasedItem> productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
	private List<PurchasedItem> orders = Collections.synchronizedList(new ArrayList<PurchasedItem>());
	private List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());
	private String grandTotalPrice;
	private double itemCount;
	private String receiptView;
	private String keyPress;
	private String amountin="0.00";
	private String cashiername;
	private double purchasedPrice;
	
	private BigDecimal balanceamnt;
	private BigDecimal changeamnt;
	private String customername = "UNKNOWN";
	private boolean enableKey = true;
	private String searchCustomer;
	private Customer customer;
	
	private String discountAmount;
	
	private ProductRunning productRunning;
	private Map<Long,QtyRunning> qtyrunning = Collections.synchronizedMap(new HashMap<Long,QtyRunning>());
	
	private String businessName;
	
	private List<ProductRunning> productHolds = Collections.synchronizedList(new ArrayList<ProductRunning>());
	
	private List<Transactions> transactions = Collections.synchronizedList(new ArrayList<Transactions>());
	private String dateFromHistory;
	private String dateToHistory;
	private String searchHistory;
	private Transactions transactionDataRecall;
	private String userName;
	private String password;
	
	private String validateResponse;
	
	private List<AddOnStore> xtras = Collections.synchronizedList(new ArrayList<AddOnStore>());
	private AddOnStore xtraData;
	private String xtraDescription;
	private double xtraAmount;
	private List xtraTypes;
	private int xtraTypeId;
	
	//private boolean onHoldButton;
	//private boolean historyButton;
	private boolean moveToOnHoldButton;
	//private boolean xtraProductButton;
	//private boolean returnProductButton;
	private List<StoreProduct> prodReturn = Collections.synchronizedList(new ArrayList<StoreProduct>());
	private String searchProductReturn;
	
	private String receiptViewHistory;
	private boolean lockDispense;
	private UserDtls loginUser;
	
	private boolean onLoadDemand;
	
	//private boolean lockCustomer = !Features.isEnabled(com.italia.ipos.enm.Features.CASHIER_CUSTOMER);
	
	private double totalNonTaxable;
	private double totalTaxable;
	private double totalVat;
	
	private List terms;
	private int termId;
	private Date dueDate;
	private boolean enableCharge;
	
	private double totalpoints;
	private double pointsredeem;
	private double remainingpoints;
	
	@ManagedProperty("#{productBean}")
	private ProductBean products;
	
	private boolean validatedRecalled;
	
	public void checkPoints() {
		System.out.println("checking points checkPoints");
		String[] params = new String[0];
		String sql = " AND pts.isactivepo=1 AND pts.isactivepo=1 AND cuz.customerid=" + getCustomer().getCustomerid()+"";
		List<CustomerPoints> cpts = CustomerPoints.retrieve(sql, params);
		double points = 0d;
		if(cpts!=null && cpts.size()>0) {
			points = cpts.get(0).getCurrentPoints();
		}	
		setRemainingpoints(points);
		setTotalpoints(points);
	}
	
	public void calculatePointsAmount() {
		
		String[] params = new String[0];
		String sql = " AND pts.isactivepo=1 AND pts.isactivepo=1 AND cuz.customerid=" + getCustomer().getCustomerid()+"";
		List<CustomerPoints> cpts = CustomerPoints.retrieve(sql, params);
		if(cpts!=null && cpts.size()>0) {
			double currentPoints = cpts.get(0).getCurrentPoints();
			//double pointsamount = currentPoints;//PointsRule.redeemValue(currentPoints);
			double pointsdeducted = currentPoints;//PointsRule.pointsAvail(pointsamount);
			double purchased = 0d;
			if(getGrandTotalPrice()!=null && !getGrandTotalPrice().isEmpty()) {
				
				purchased = Double.valueOf(Currency.removeComma(getGrandTotalPrice()));
				
				if(purchased>currentPoints) {
					
				}else {
					//pointsamount = purchased;
					pointsdeducted = purchased;
				}
				
				setRemainingpoints(currentPoints - pointsdeducted);
				setDiscountAmount(pointsdeducted+"");
				redeemNow();
			}
			
		}
		
	}
	
	public void redeemNow() {
		if(getCustomer()!=null) {
			String[] params = new String[0];
			String sql = " AND pts.isactivepo=1 AND pts.isactivepo=1 AND cuz.customerid=" + getCustomer().getCustomerid()+"";
			List<CustomerPoints> cpts = CustomerPoints.retrieve(sql, params);
			double purchased = 0d;
			if(cpts!=null && cpts.size()>0) {
				
				
				CustomerPoints pt = cpts.get(0);
				purchased = Double.valueOf(Currency.removeComma(getGrandTotalPrice()));
				double points = pt.getCurrentPoints();
				
				
				if(purchased>points) {
					PointsRule.redeemPoints(getCustomer(), "0", points);//deduction
				}else {
					PointsRule.redeemPoints(getCustomer(), "0", purchased);//deduction
					//pointsamount = PointsRule.redeemValue(deductionPoints);
				}
				//setDiscountAmount(Currency.formatAmount(pointsamount));
				Application.addMessage(1, "Success", "Successfully redeemed");
			}
		}
	}
	
	public void setProducts(ProductBean products){
		this.products = products;
	}
	public String getSearchProduct() {
		return searchProduct;
	}
	public void setSearchProduct(String searchProduct) {
		this.searchProduct = searchProduct;
	}
	
	public void clickItem(PurchasedItem item){
		setSearchProduct(item.getProductName());
		initProduct();
	}
	
	public void cashbutton(){
		setEnableKey(false);
	}
	
	@PostConstruct
	public void init(){
		loginUser = Login.getUserLogin().getUserDtls();
		
		String val = ReadConfig.value(Ipos.CASHIER_SELECTED_ITEM_SAVE);
		if("1".equalsIgnoreCase(val)){
			onLoadDemand = true;
		}else{
			onLoadDemand = false;
		}
		
	}
	
	public void activateAutoSave(){
		if(getOrders()!=null && getOrders().size()>0){
			String message = isOnLoadDemand()? "On " : "Off ";
			addMessage("Changing the status to "+ message + "is not allowed. Please remove first all selected product or dispense all product in order use this function.","");
			onLoadDemand = isOnLoadDemand()? false : true;
		}else{
			Ipos[] tag = new Ipos[1];
			String[] value = new String[1];
			tag[0] = Ipos.CASHIER_SELECTED_ITEM_SAVE; value[0] = isOnLoadDemand()? "1" : "0";
			Business.updateBusiness(tag, value);
			init();
			String message = isOnLoadDemand()? "activated" : "removed";
			addMessage("Auto hold has been "+message,"");
		}
	}
	
	public void updateCustomer(Customer customer){
		if(Login.getUserLogin().checkUserStatus()){
			setCustomername(customer.getFullname());
			setCustomer(customer);
			if(getProductRunning()!=null){
				ProductRunning productrun = getProductRunning();
				try{productrun.setCustomer(getCustomer());
				ProductRunning.save(productrun);}catch(Exception e){}
			}	
		}
	}
	
	public void initCustomer(){
		customers = Collections.synchronizedList(new ArrayList<Customer>());
		//Customer customer = new Customer();
		//customer.setIsactive(1);
		//customer.setFullname(Whitelist.remove(getSearchCustomer()));
		
		String sql = " AND cus.cusisactive=1 ";
		String[] params = new String[0];
		
		if(getSearchCustomer()!=null && !getSearchCustomer().isEmpty()) {
			sql += " AND ( cus.fullname like '%"+ getSearchCustomer().replace("--", "") +"%' OR cus.cuscardno like '%"+ getSearchCustomer().replace("--", "") +"%')";
		}else {
			sql += " LIMIT 10";
		}
				
		customers = Customer.retrieve(sql, params);
	}
	
	public void qtyCheck(){
		try{
		Double.valueOf(getQtyToCartTmp());
		setQtyToCart(getQtyToCartTmp());
		}catch(NullPointerException n){
		}catch(NumberFormatException e){}
	}
	
	public void initXtras(){
		xtras = Collections.synchronizedList(new ArrayList<AddOnStore>());
		ProductRunning run = new ProductRunning();
		
		if(getProductRunning()!=null){
			AddOnStore on = new AddOnStore();
			on.setIsActive(1);
			
			run.setRunid(getProductRunning().getRunid());
			run.setIsrunactive(1);
			xtras = AddOnStore.retrieve(on,run);
		}
	}
	
	public void deleteXtras(AddOnStore add){
		if(Login.checkUserStatus()){
			if(add.getStatus()==Status.NEW.getId()){
				add.delete();
				add.save();
				initXtras();
				addMessage("Product removed successfully.","");
			}else{
				addMessage("Removing of product is not allowed. Product is already been processed.","");
			}
		}
	}
	
	public void addXtras(){
		ProductRunning run = addRunningProduct();
		AddOnStore on = new AddOnStore();
		if(getXtraData()!=null){
			on = getXtraData();
		}
		
		on.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		
		
		if(getXtraTypeId()==1){
			on.setDescription(getXtraDescription());
			on.setAmount(getXtraAmount());
		}else{
			on.setAmount(-getXtraAmount());
			on.setDescription(getXtraDescription() + " less " + getXtraAmount());
		}
		
		on.setAddOnType(getXtraTypeId());
		on.setIsActive(1);
		on.setStatus(Status.NEW.getId());
		on.setProductRunning(run);
		on.save();
		
		setXtraData(null);
		setXtraAmount(0);
		setXtraDescription(null);
		setXtraTypeId(0);
		initXtras();
		initOrders();
		addMessage("Product added successfully.","");
	}
	
	public void clickItemXtra(AddOnStore addOn){
		setXtraData(addOn);
		setXtraAmount(addOn.getAmount());
		setXtraDescription(addOn.getDescription());
		setXtraTypeId(addOn.getAddOnType());
	}
	
	private ProductRunning addRunningProduct(){
		ProductRunning productrun = new ProductRunning();
		if(getProductRunning()!=null){
			productrun = getProductRunning();
			try{productrun.setCustomer(getCustomer());
			ProductRunning.save(productrun);}catch(Exception e){}
		}else{
			productrun.setRundate(DateUtils.getCurrentDateYYYYMMDD());
			productrun.setIsrunactive(1);
			productrun.setClientip(ClientInfo.getClientIP());
			productrun.setClientbrowser(ClientInfo.getBrowserName());
			productrun.setRunstatus(ProductStatus.ON_QUEUE.getId());
			productrun.setRunremarks(ProductStatus.ON_QUEUE.getName());
			//productrun.setUserDtls(Login.getUserLogin().getUserDtls());
			productrun.setUserDtls(getLoginUser());
			try{
				Customer customer = getCustomer();
				if(customer==null){
					customer = new Customer();
					customer.setCustomerid(1);
				}
				productrun.setCustomer(customer);}catch(Exception e){}
			productrun = ProductRunning.save(productrun);
			setProductRunning(productrun);
		}
		return productrun;
	}
	
	public String addToCart(StoreProduct store){
		
		//String val = ReadConfig.value(Ipos.CASHIER_SELECTED_ITEM_SAVE);
		
		
		
		//if("1".equalsIgnoreCase(val)){// save item on selection
		if(isOnLoadDemand()){
			Product prop = store.getProduct();
			ProductProperties proper = store.getProductProperties();
			store.setProductProperties(proper);
			prop.setProductProperties(proper);
		
		boolean isQty=false;
		
		if(getQtyToCart()!=null && !getQtyToCart().isEmpty()){
			isQty=true;
		}
		
		//if(Login.getUserLogin().checkUserStatus() && isQty){
			PurchasedItem item = new PurchasedItem();
			
			//save temporary transactions to this table
			ProductRunning productrun = new ProductRunning();
			productrun = addRunningProduct();
			
			
			if(getProductPurchasedData()!=null){
				
				
				long id = prop.getProdid();
				if(productPurchasedData.containsKey(id)){
					PurchasedItem itm = productPurchasedData.get(id);
					Double newQty = Double.valueOf(getQtyToCart());
					Double qty = itm.getQty();
					qty = qty + newQty;
					
					String productName = store.getProductName();
					double sellingPrice = store.getSellingPrice();
					//Double price = Double.valueOf(sellingPrice+"");
					Double totalPrice = qty * sellingPrice;
					item.setProductId(id);
					item.setProductName(productName);
					item.setUomSymbol(store.getUomSymbol());
					item.setSellingPrice(new BigDecimal(sellingPrice));
					item.setTotalPrice(totalPrice);
					item.setQty(qty);
					item.setProductProperties(store.getProductProperties());
					item.setProduct(prop);
					item.setStoreProduct(store);
					productPurchasedData.remove(id);
					productPurchasedData.put(id, item);
					
					//modify inventory
					//ProductInventory.invtoryqty(false, prop, newQty);
					
					StoreProduct.storeQuantity(false, prop, newQty);
					
					System.out.println("additional qty loading = " + qty);
					Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunning(getQtyrunning(),id, productrun, prop, qty);
					setQtyrunning(qtyrunning);
				}else{
					
					String productName = store.getProductName();
					double sellingPrice = store.getSellingPrice();
					Double newQty = Double.valueOf(getQtyToCart());
					//Double price = Double.valueOf(sellingPrice+"");
					Double totalPrice = newQty * sellingPrice;
					item.setProductId(prop.getProdid());
					item.setProductName(productName);
					item.setUomSymbol(store.getUomSymbol());
					item.setSellingPrice(new BigDecimal(sellingPrice));
					item.setTotalPrice(totalPrice);
					item.setQty(Double.valueOf(getQtyToCart()));
					item.setProductProperties(store.getProductProperties());
					item.setProduct(prop);
					item.setStoreProduct(store);
					productPurchasedData.put(prop.getProdid(), item);
					
					//modify inventory
					//ProductInventory.invtoryqty(false, prop, newQty);
					
					newQty = StoreProduct.storeQuantity(false, prop, newQty);
					System.out.println("first load qty else = " + newQty);
					Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunning(getQtyrunning(),prop.getProdid(), productrun, prop, newQty);
					setQtyrunning(qtyrunning);
				}
				
				initOrders();
				addMessage("Product added successfully.","");
				
			}else{
				
				productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
				
				String productName = store.getProductName();
				double sellingPrice = store.getSellingPrice();
				Double newQty = Double.valueOf(getQtyToCart());
				//Double price = Double.valueOf(sellingPrice+"");
				Double totalPrice = newQty * sellingPrice;
				item.setProductId(prop.getProdid());
				item.setProductName(productName);
				item.setUomSymbol(store.getUomSymbol());
				item.setSellingPrice(new BigDecimal(sellingPrice));
				item.setTotalPrice(totalPrice);
				item.setQty(Double.valueOf(getQtyToCart()));
				item.setProductProperties(store.getProductProperties());
				item.setProduct(prop);
				item.setStoreProduct(store);
				productPurchasedData.put(prop.getProdid(), item);
				
				//modify inventory
				//ProductInventory.invtoryqty(false,prop, newQty);
				
				newQty = StoreProduct.storeQuantity(false, prop, newQty);
				System.out.println("first load qty = " + newQty);
				Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunning(getQtyrunning(),prop.getProdid(), productrun, prop, newQty);
				setQtyrunning(qtyrunning);
			
				initOrders();
				addMessage("Product added successfully.","");
			}
			
			//}
		
		setQtyToCart(null);
		setQtyToCartTmp(null);
		
		}else{ //not saving item during selection
			System.out.println("not saving on demand....");
			collectItenSelected(store);
			
		}
				
		
		return "";
	}
	
	private void collectItenSelected(StoreProduct store){
		Product prop = store.getProduct();
		ProductProperties proper = store.getProductProperties();
		store.setProductProperties(proper);
		prop.setProductProperties(proper);
		PurchasedItem item = new PurchasedItem();
		long id = prop.getProdid();
		if(getProductPurchasedData()!=null){
			
			
			
			if(productPurchasedData.containsKey(id)){
				PurchasedItem itm = productPurchasedData.get(id);
				Double newQty = Double.valueOf(getQtyToCart());
				Double qty = itm.getQty();
				qty = qty + newQty;
				boolean isQtyOk = false;
				double storeQty = StoreProduct.retrieveCurrentQty(id+"");
				
				if(storeQty>=qty){
					isQtyOk = true;
				}else{
					qty = storeQty;
					isQtyOk = false;
				}
					
				if(newQty>0){
					String productName = store.getProductName();
					double sellingPrice = store.getSellingPrice();
					//Double price = Double.valueOf(sellingPrice+"");
					Double totalPrice = qty * sellingPrice;
					item.setProductId(id);
					item.setProductName(productName);
					item.setUomSymbol(store.getUomSymbol());
					item.setSellingPrice(new BigDecimal(sellingPrice));
					item.setTotalPrice(totalPrice);
					item.setQty(qty);
					item.setProductProperties(store.getProductProperties());
					item.setProduct(prop);
					item.setStoreProduct(store);
					productPurchasedData.remove(id);
					productPurchasedData.put(id, item);
					initOrders();
				}
				
				if(isQtyOk){
					addMessage("Product added successfully.","");
				}else{
					if(newQty==0){
						addMessage("This product has no quantity remaining.","");
					}else{
						addMessage("Product maximum quantity has been reached. The remaining product in store has been added.","");
					}
				}
				
			}else{
				
				String productName = store.getProductName();
				double sellingPrice = store.getSellingPrice();
				Double newQty = Double.valueOf(getQtyToCart());
				
				boolean isQtyOk = false;
				double storeQty = StoreProduct.retrieveCurrentQty(id+"");
				
				if(storeQty>=newQty){
					isQtyOk = true;
				}else{
					newQty = storeQty;
					isQtyOk = false;
				}
				
				if(newQty>0){
					Double totalPrice = newQty * sellingPrice;
					item.setQty(newQty);
					item.setProductId(prop.getProdid());
					item.setProductName(productName);
					item.setUomSymbol(store.getUomSymbol());
					item.setSellingPrice(new BigDecimal(sellingPrice));
					item.setTotalPrice(totalPrice);
					//item.setQty(Double.valueOf(getQtyToCart()));
					item.setProductProperties(store.getProductProperties());
					item.setProduct(prop);
					item.setStoreProduct(store);
					productPurchasedData.put(prop.getProdid(), item);
					initOrders();
				}
				
				if(isQtyOk){
					addMessage("Product added successfully.","");
				}else{
					if(newQty==0){
						addMessage("This product has no quantity remaining.","");
					}else{
						addMessage("Product maximum quantity has been reached. The remaining product in store has been added.","");
					}
				}
				
			}
			
			
			
		}else{
			
			productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
			
			String productName = store.getProductName();
			double sellingPrice = store.getSellingPrice();
			Double newQty = Double.valueOf(getQtyToCart());
			
			boolean isQtyOk = false;
			double storeQty = StoreProduct.retrieveCurrentQty(id+"");
			
			if(storeQty>=newQty){
				isQtyOk = true;
			}else{
				newQty = storeQty;
				isQtyOk = false;
			}
			
			if(newQty>0){
				Double totalPrice = newQty * sellingPrice;
				item.setQty(newQty);
				item.setProductId(prop.getProdid());
				item.setProductName(productName);
				item.setUomSymbol(store.getUomSymbol());
				item.setSellingPrice(new BigDecimal(sellingPrice));
				item.setTotalPrice(totalPrice);
				//item.setQty(Double.valueOf(getQtyToCart()));
				item.setProductProperties(store.getProductProperties());
				item.setProduct(prop);
				item.setStoreProduct(store);
				productPurchasedData.put(prop.getProdid(), item);		
				initOrders();
			}
			
			if(isQtyOk){
				addMessage("Product added successfully.","");
			}else{
				if(newQty==0){
					addMessage("This product has no quantity remaining.","");
				}else{
					addMessage("Product maximum quantity has been reached. The remaining product in store has been added.","");
				}
			}
		}
		
		setQtyToCart(null);
		setQtyToCartTmp(null);
	}
	
	
	/*private void invtoryqty(boolean isRevert, Product product, double qty){
		Product prd = new Product();
		prd.setProdid(product.getProdid());
		prd.setIsactiveproduct(1);
		ProductInventory inv = ProductInventory.retrieve(prd).get(0);
		double oldqty = inv.getNewqty();
		double qtynew = 0d;
		if(isRevert){
			qtynew = oldqty + qty;
		}else{
			if(qty>0){
				qtynew = oldqty - qty;
			}else{
				qtynew = oldqty + Math.abs(qty);
			}
		}
		inv.setNewqty(qtynew);
		inv.setOldqty(oldqty);
		inv.save();
	}*/
	
	/*private void addQtyRunning(long productid, ProductRunning productRunning, Product product, double qty){
		
		QtyRunning qtyrun = new QtyRunning();
		if(getQtyrunning()!=null && getQtyrunning().size()>0){
			
			if(getQtyrunning().containsKey(productid)){
				qtyrun = getQtyrunning().get(productid);
				getQtyrunning().remove(productid);
			}
		}
		
		qtyrun.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
		qtyrun.setQtyhold(qty);
		qtyrun.setIsqtyactive(1);
		qtyrun.setQtystatus(ProductStatus.ON_QUEUE.getId());
		qtyrun.setQtyremarks(ProductStatus.ON_QUEUE.getName());
		qtyrun.setProductRunning(productRunning);
		qtyrun.setProduct(product);
		qtyrun.setUserDtls(Login.getUserLogin().getUserDtls());
		qtyrun = QtyRunning.save(qtyrun);
		getQtyrunning().put(productid, qtyrun);
	}*/
	
	public void deleteRow(PurchasedItem item){
		if(Login.getUserLogin().checkUserStatus()){
			if(item.getAddOnStore()==null){
			long id = item.getProduct().getProdid();//item.getProductId();
			productPurchasedData.remove(id);
			}
			orders.remove(item);
			voidIndividual(item);
			setGrandTotalPrice("0.00");
			initOrders();
			addMessage("The product "+ item.getProductName() +" has been successfully deleted.","");
		}
	}
	
	private void voidIndividual(PurchasedItem item){
		
		if(item.getAddOnStore()==null){
		System.out.println("null in store");
		System.out.println("getQtyrunning()!=null && getQtyrunning().size()>0 >>>>>>>> " + getQtyrunning().size());
			if(getQtyrunning()!=null && getQtyrunning().size()>0){
				
				QtyRunning qtyrun = getQtyrunning().get(item.getProduct().getProdid());
				
				//return qty in inventory table
				//has been changed with below line of code. instead of return to warehouse
				// the code below only return to store
				//ProductInventory.invtoryqty(true, qtyrun.getProduct(), qtyrun.getQtyhold());
				StoreProduct.storeQuantity(true, qtyrun.getProduct(), qtyrun.getQtyhold());
				
				//modify Qtyrunning table
				qtyrun.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
				qtyrun.setQtystatus(ProductStatus.VOID.getId());
				qtyrun.setQtyremarks(ProductStatus.VOID.getName());
				//qtyrun.setUserDtls(Login.getUserLogin().getUserDtls());
				qtyrun.setUserDtls(getLoginUser());
				qtyrun.save();
				}
			
		}else{
			System.out.println("not null in store");
			AddOnStore on = item.getAddOnStore();
			on.delete();
		}
	}
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public void initOrders(){
		setTotalNonTaxable(0);
		setTotalTaxable(0);
		setTotalVat(0);
		setReceiptView(null);
		orders = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		double totalprice =0d;
		itemCount = 0;
		int count = 1;
		ReceiptInfo rp = ReceiptXML.value();
		StringBuffer str = new StringBuffer();
		
		if(rp.getPosStatus()!=null && !rp.getPosStatus().isEmpty()){
			str.append(rp.getPosStatus() +"\n");
		}
		if(rp.getOwner()!=null && !rp.getOwner().isEmpty()){
			str.append(rp.getOwner() +"\n");
		}
		if(rp.getTitle()!=null && !rp.getTitle().isEmpty()){
			str.append(rp.getTitle() +"\n");
		}
		if(rp.getAdditionalDetails()!=null && !rp.getAdditionalDetails().isEmpty()){
			str.append(rp.getAdditionalDetails() +"\n");
		}
		if(rp.getAdditionalDetails2()!=null && !rp.getAdditionalDetails2().isEmpty()){
			str.append(rp.getAdditionalDetails2() +"\n");
		}
		if(rp.getPosserial()!=null && !rp.getPosserial().isEmpty()){
			str.append(rp.getPosserial() +"\n");
		}
		if(rp.getTelephoneNumber()!=null && !rp.getTelephoneNumber().isEmpty()){
			str.append(rp.getTelephoneNumber() +"\n");
		}
		if(rp.getEmail()!=null && !rp.getEmail().isEmpty()){
			str.append(rp.getEmail() +"\n\n");
		}
		
		str.append("Cashier: " + getCashiername() +"\n");
		str.append("Date: " + DateUtils.getCurrentDateMMDDYYYYTIME() +"\n");
		str.append("Receipt No: " + DeliveryItemReceipt.generateNewReceiptNo()+"\n");
		str.append("Customer:"+ getCustomer().getFullname() +"\n");
		/*str.append("Address:_______________________\n");
		str.append("TIN:___________________________\n");
		str.append("Business Type:_________________\n");
		str.append("Signature:_____________________\n");*/
		str.append("---------------------------------\n");
		str.append("ORDERS\n");
		str.append("---------------------------------\n");
		List<String> tmpData=new ArrayList<>();
		initXtras();
		if(productPurchasedData.size()>0){
			for(PurchasedItem item : productPurchasedData.values()){
				item.setCount(count++);
				totalprice += item.getTotalPrice();
				str.append(item.getProductName()+"\n");
				str.append(item.getQty() +" " + item.getUomSymbol() + " x " + item.getSellingPrice() + "\t\t" + item.getTotalPrice() + "\n");
				orders.add(item);
				itemCount += item.getQty();
				item.setAddOnStore(null);
				totalTaxable += item.getTotalPrice();
			}
			if(getXtras().size()>0){
				for(AddOnStore on : getXtras()){
					
					BigDecimal amount = new BigDecimal(on.getAmount());
					double price = on.getAmount();
					
					PurchasedItem item = new PurchasedItem();
					
					item.setProductName(on.getDescription());
					item.setQty(1);
					item.setUomSymbol("n/a");
					item.setDatesold(on.getDateTrans());
					
					if(on.getAddOnType()==2){
						item.setNetprice(new BigDecimal("0.00"));
						item.setSellingPrice(new BigDecimal("0.00"));
						item.setTotalPrice(price);
						item.setPurchasedprice(new BigDecimal("0.00"));
					}else{
						item.setNetprice(amount);
						item.setSellingPrice(amount);
						item.setTotalPrice(price);
						totalNonTaxable += on.getAmount();
					}
					
					
					item.setCount(count++);
					totalprice += item.getTotalPrice();
					if(on.getAddOnType()==1){
						str.append(item.getProductName() + "\n");
						str.append(item.getQty() +" " +" N/A" + " x " + item.getTotalPrice() + "\t\t" + item.getTotalPrice() + "\n");
					}else{
						str.append(item.getProductName() +"\n");
						str.append(item.getQty() +" " +" N/A" + " x " +  "(" + on.getAmount() + ")\n");
					}
					orders.add(item);
					itemCount += item.getQty();
					item.setAddOnStore(on);
				}
			}
			str.append("Item Count: " + itemCount +"\n");
			setPurchasedPrice(totalprice);
			if(getDiscountAmount()==null || getDiscountAmount().isEmpty()){
				setDiscountAmount("0");
			}
			double discount = 0d;
			try{discount = Double.valueOf(getDiscountAmount().replace(",", ""));}catch(Exception e) {}
			System.out.println("processing total payable");
			double payment = totalprice - discount;
			setGrandTotalPrice(Currency.formatAmount(payment));
			System.out.println("en processing total payable");
			str.append("---------------------------------\n");
			str.append("\tDiscount:\t"+Currency.formatAmount(discount) + "\n");
			str.append("\tAmount Due:"+Currency.formatAmount(payment) + "\n");
			
			
			if(totalprice==discount) {
				setAmountin("0.00");
			}
			
			setReceiptView(str.toString());
		}else{
			
			if(getXtras().size()>0){
				for(AddOnStore on : getXtras()){
					
					BigDecimal amount = new BigDecimal(on.getAmount());
					double price = on.getAmount();
					
					
					PurchasedItem item = new PurchasedItem();
					
					item.setProductName(on.getDescription());
					item.setQty(1);
					item.setUomSymbol("n/a");
					item.setDatesold(on.getDateTrans());
					if(on.getAddOnType()==2){
						item.setNetprice(new BigDecimal("0.00"));
						item.setSellingPrice(new BigDecimal("0.00"));
						item.setTotalPrice(price);
						item.setPurchasedprice(new BigDecimal("0.00"));
					}else{
						item.setNetprice(amount);
						item.setSellingPrice(amount);
						item.setTotalPrice(price);
						totalNonTaxable += on.getAmount();
					}
					
					item.setCount(count++);
					totalprice += item.getTotalPrice();
					if(on.getAddOnType()==1){
						str.append(item.getProductName() + "   " + item.getQty() + "  " + item.getTotalPrice() + "\n");
					}else{
						str.append(item.getProductName() + "   " + item.getQty() + "  (" + on.getAmount() + ")\n");
					}
					orders.add(item);
					itemCount += item.getQty();
					item.setAddOnStore(on);
				}
				str.append("Item Count: " + itemCount +"\n");
				setPurchasedPrice(totalprice);
				if(getDiscountAmount()==null || getDiscountAmount().isEmpty()){
					setDiscountAmount("0");
				}
				double discount = 0d;
				try{discount = Double.valueOf(getDiscountAmount().replace(",", ""));}catch(Exception e) {}
				double payment = totalprice - discount;
				setGrandTotalPrice(Currency.formatAmount(payment));
				str.append("---------------------------------\n");
				str.append("\tDiscount:\t"+Currency.formatAmount(discount) + "\n");
				str.append("\tAmount Due:"+Currency.formatAmount(payment) + "\n");
				
				
				setReceiptView(str.toString());
			}
			
		}
		setSearchProduct(null);
		//enable or disable charge invoice
		System.out.println("Check amountIn: "+ getAmountin());
		if(getAmountin()==null || getAmountin().isEmpty() || "0.00".equalsIgnoreCase(getAmountin()) || "0".equalsIgnoreCase(getAmountin())) {
			setEnableCharge(true);
			System.out.println("initOrders true charge");
		}else {
			setEnableCharge(false);
			System.out.println("initOrders false charge");
		}
	}
	
	/*public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }*/
	
	public List<StoreProduct> getProds() {
		return prods;
	}
	
	
	
	public void initProduct(){
		prods = Collections.synchronizedList(new ArrayList<StoreProduct>());
		
		if(getSearchProduct()!=null && !getSearchProduct().isEmpty()){
		
			int len = getSearchProduct().length();
			if(len>=4){
				
				String sql = " AND store.qty!=0 AND purchasedprice!=0 AND sellingprice!=0 AND store.productName like '%"+ getSearchProduct().replace("--", "") +"%'";
				for(StoreProduct store : StoreProduct.retrieve(sql, new String[0])){
					ProductProperties prop = ProductProperties.properties(store.getProductProperties().getPropid()+"");
					store.setProductProperties(prop);
					prods.add(store);
				}		
				Collections.reverse(prods);
				
			}
		}
		
		
		
	}
	
	public void printAll(){
		
	}
	 
	public void search(){
		System.out.println("search is active");
		//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Search found", "Search code"));
		if(getSearchCode()!=null && !getSearchCode().isEmpty()){
			setEnableKey(false);
		}else{
			setEnableKey(true);
		}
		
	}
	
 
	public String getSearchCode() {
		return searchCode;
	}

	public void setSearchCode(String searchCode) {
		this.searchCode = searchCode;
	}

public String getQtyToCart() {
		return qtyToCart;
	}
	public void setQtyToCart(String qtyToCart) {
		this.qtyToCart = qtyToCart;
	}
public String getQtyToCartTmp() {
		return qtyToCartTmp;
	}
	public void setQtyToCartTmp(String qtyToCartTmp) {
		this.qtyToCartTmp = qtyToCartTmp;
	}
public Map<Long, PurchasedItem> getProductPurchasedData() {
		return productPurchasedData;
	}
	public void setProductPurchasedData(Map<Long, PurchasedItem> productPurchasedData) {
		this.productPurchasedData = productPurchasedData;
	}
public List<PurchasedItem> getOrders() {
		return orders;
	}
	public void setOrders(List<PurchasedItem> orders) {
		this.orders = orders;
	}
	public String getGrandTotalPrice() {
		if(grandTotalPrice==null){
			grandTotalPrice = "0.00";
		}
		return grandTotalPrice;
	}
	public void setGrandTotalPrice(String grandTotalPrice) {
		this.grandTotalPrice = grandTotalPrice;
	}
public double getItemCount() {
		return itemCount;
	}
	public void setItemCount(double itemCount) {
		this.itemCount = itemCount;
	}
	public String getReceiptView() {
		ReceiptInfo rp = ReceiptXML.value();
		if(receiptView==null){
			if(rp.getPosStatus()!=null && !rp.getPosStatus().isEmpty()){
				receiptView = rp.getPosStatus() +"\n";
			}
			if(rp.getOwner()!=null && !rp.getOwner().isEmpty()){
				receiptView += rp.getOwner() +"\n";
			}
			if(rp.getTitle()!=null && !rp.getTitle().isEmpty()){
				receiptView += rp.getTitle() +"\n";
			}
			if(rp.getAdditionalDetails()!=null && !rp.getAdditionalDetails().isEmpty()){
				receiptView += rp.getAdditionalDetails() +"\n";
			}
			if(rp.getAdditionalDetails2()!=null && !rp.getAdditionalDetails2().isEmpty()){
				receiptView += rp.getAdditionalDetails2() +"\n";
			}
			if(rp.getPosserial()!=null && !rp.getPosserial().isEmpty()){
				receiptView += rp.getPosserial() +"\n";
			}
			if(rp.getTelephoneNumber()!=null && !rp.getTelephoneNumber().isEmpty()){
				receiptView += rp.getTelephoneNumber() +"\n";
			}
			if(rp.getEmail()!=null && !rp.getEmail().isEmpty()){
				receiptView += rp.getEmail() +"\n\n";
			}
			
			receiptView += "Cashier: " + getCashiername() +"\n";
			receiptView += "Printed Date: " + DateUtils.getCurrentDateMMDDYYYYTIME() +"\n";
			receiptView += "Receipt No: " + DeliveryItemReceipt.generateNewReceiptNo() +"\n";
			receiptView += "---------------------------------------------\n";
			receiptView += "Item Count: " + getItemCount() +"\n";
		}
			return receiptView;
	}
	public void setReceiptView(String receiptView) {
		this.receiptView = receiptView;
	}
   public String getKeyPress() {
	keyPress="amountId";
	System.out.println("im press....");
	
		return keyPress;
	}
	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}
	private String refreshCashier;
	public String getRefreshCashier(){
		/*if(getSearchCode()!=null && !getSearchCode().isEmpty()){
			return "formIdPos";
		}else{
			return "searchId";
		}*/
		return "orderDataId grandTotalId toolbarCommands rptId searchId";
	}
	
	public void setRefreshCashier(String refreshCashier){
		this.refreshCashier = refreshCashier;
	}
	
	public void keyButton(){
	
		
		if(getSearchCode()!=null && !getSearchCode().isEmpty()){
			System.out.println("searching  code..... " + getSearchCode());
			searchBarcode();
		}else if(getAmountin()!=null && !getAmountin().isEmpty()){
			System.out.println(" amoun in " + getAmountin());
			
			initOrders();
			if(getOrders()!=null && getOrders().size()>0){
			
			String str = getReceiptView();
				   str += "\n\n";
				if(str!=null && !str.isEmpty()){
					str += "\t\tCash :\t" + Currency.formatAmount(getAmountin()) + "\n";
					str += "---------------------------------\n";
					Double changeamnt = Double.valueOf(Currency.removeComma(getAmountin())) - Double.valueOf(Currency.removeComma(getGrandTotalPrice()));
					if(changeamnt>0){
						str +="\tChange :\t" + Currency.formatAmount(changeamnt);
						setChangeamnt(new BigDecimal(changeamnt+""));
						setBalanceamnt(new BigDecimal("0.00"));
					}else{
						str +="\tChange :\t0.00\n";
						str +="\tBalance :\t" +  Math.abs(changeamnt) ;
						setChangeamnt(new BigDecimal("0.00"));
						setBalanceamnt(new BigDecimal(changeamnt+""));
					}
					double discount =0d;
					double taxable = 0d;
					String per = "";
					try{discount = Double.valueOf(getDiscountAmount().replace(",", ""));
					taxable = getTotalTaxable() - discount;
					setTotalTaxable(taxable);
					}catch(Exception e){}
					str += "\n---------------------------------\n";
					str +="\nNon-Taxable Sales :\t"+ Currency.formatAmount(getTotalNonTaxable()) +"\n";
					str +="Taxable Sales :\t"+Currency.formatAmount(getTotalTaxable())+"\n";
					double vatPercentage = 0.12;
					try{vatPercentage = Features.retrieve("SELECT * FROM features WHERE isActive=1 AND modulename='"+ com.italia.ipos.enm.Features.VAT.getName()+"'", new String[0]).get(0).getVat();}catch(Exception e){}
					totalVat = getTotalTaxable() / (1 + vatPercentage);
					totalVat = getTotalTaxable() - totalVat;
					per = vatPercentage+"";
					per = per.replace("0.", "");
					str +="VAT("+per+"%) :\t"+ Currency.formatAmount(totalVat) +"\n";
					str +="\n";
					setReceiptView(str);
					//taxInfo();
					posDeveloper();
					returnPolicy();
					String strNew = getReceiptView();
					strNew += " \n";
					strNew += " \n";
					strNew += " \n";
					strNew += " \n";
					strNew += " \n";
					strNew += " \n";
					setReceiptView(strNew);
					}
				addMessage("You can now dispense the items.","");
			}else{
				setAmountin(null);
				addMessage("Please select item/s first before inputing an amount.","");
			}
			
		}
		
		//enable or disable charge invoice
				if(getAmountin()==null || getAmountin().isEmpty()) {
					setEnableCharge(true);
					System.out.println("keyButton() true charge");
				}else {
					setEnableCharge(false);
					System.out.println("keyButton() false charge");
				}
		
		//clearFields();
		
	}
	
	public void voidTrans(){
		if(Login.checkUserStatus()){
			setCustomername("UNKNOWN");
			setCustomer(null);
			setAmountin(null);
			setBalanceamnt(null);
			setReceiptView(null);
			setItemCount(0);
			orders = Collections.synchronizedList(new ArrayList<PurchasedItem>());
			productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
			
			transactiondVoid();
			
			setGrandTotalPrice("0.00");
			
			addMessage("Tracsactions has been successfully canceled.","");
		}
	}
	
	private void voidAddOns(ProductRunning run){
		ProductRunning prun = new ProductRunning();
		prun.setRunid(run.getRunid());
		prun.setIsrunactive(1);
		
		AddOnStore on = new AddOnStore();
		on.setIsActive(1);
		
		for(AddOnStore st : AddOnStore.retrieve(on, prun)){
			st.delete();
		}
		
	}
	
	public void transactiondVoid(){
		
		
		if(getQtyrunning()!=null && getQtyrunning().size()>0){
			for(QtyRunning qtyrun : getQtyrunning().values()){
				
				if(qtyrun.getQtystatus()==ProductStatus.ON_QUEUE.getId()){
				//return qty in inventory table
				//this code has been changed with below code
				//ProductInventory.invtoryqty(true, qtyrun.getProduct(), qtyrun.getQtyhold());
				StoreProduct.storeQuantity(true, qtyrun.getProduct(), qtyrun.getQtyhold());
				
				//modify Qtyrunning table
				qtyrun.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
				qtyrun.setQtystatus(ProductStatus.VOID.getId());
				qtyrun.setQtyremarks(ProductStatus.VOID.getName());
				//qtyrun.setUserDtls(Login.getUserLogin().getUserDtls());
				qtyrun.setUserDtls(getLoginUser());
				qtyrun.save();
				}
				
			}
			setQtyrunning(Collections.synchronizedMap(new HashMap<Long,QtyRunning>()));
		}else{
			if(getProductRunning()!=null){
				ProductRunning run = new ProductRunning();
				run.setRunid(getProductRunning().getRunid());
				run.setIsrunactive(1);
				
				QtyRunning qty = new QtyRunning();
				qty.setIsqtyactive(1);
				
				for(QtyRunning qtyrun : QtyRunning.retrieve(qty,run)){
						if(qtyrun.getQtystatus()==ProductStatus.ON_QUEUE.getId()){
						//return qty in inventory table
						//this code has been changed with below code
						//ProductInventory.invtoryqty(true, qtyrun.getProduct(), qtyrun.getQtyhold());
						StoreProduct.storeQuantity(true, qtyrun.getProduct(), qtyrun.getQtyhold());
						
						//modify Qtyrunning table
						qtyrun.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
						qtyrun.setQtystatus(ProductStatus.VOID.getId());
						qtyrun.setQtyremarks(ProductStatus.VOID.getName());
						//qtyrun.setUserDtls(Login.getUserLogin().getUserDtls());
						qtyrun.setUserDtls(getLoginUser());
						qtyrun.save();
						}
				}
				
				
			}
		}
		
		
		if(getProductRunning()!=null){
			ProductRunning run = getProductRunning();
			run.setIsrunactive(1);
			run.setRundate(DateUtils.getCurrentDateYYYYMMDD());
			run.setRunstatus(ProductStatus.VOID.getId());
			run.setRunremarks(ProductStatus.VOID.getName());
			run.save();
			
			voidAddOns(run);
			
			setProductRunning(null);
		}
		
	}
	
	public void clearCashier(){
		
		setCustomername("UNKNOWN");
		setCustomer(null);
		setAmountin(null);
		setBalanceamnt(null);
		setReceiptView(null);
		setGrandTotalPrice(null	);
		setDiscountAmount(null);
		setTransactionDataRecall(null);
		setItemCount(0);
		setProductRunning(null);
		
		qtyrunning = Collections.synchronizedMap(new HashMap<Long,QtyRunning>());
		productHolds = Collections.synchronizedList(new ArrayList<ProductRunning>());
		transactions = Collections.synchronizedList(new ArrayList<Transactions>());
		
		if(getProductPurchasedData()!=null && getProductPurchasedData().size()>0){
			addMessage("Transaction has been moved to Payable Accounts.","");
		}
		orders = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
	}
	
	public void moveToHoldPurchased(){
		
		if(getOrders()!=null && getOrders().size()>0){
		
		//String val = ReadConfig.value(Ipos.CASHIER_SELECTED_ITEM_SAVE);
		//if("1".equalsIgnoreCase(val)){
		if(isOnLoadDemand()){	
			clearCashier();
		}else{
			holdData();
			clearCashier();
		}
		
		}else{
			addMessage("No data to be move.","");
		}
	}
	
	private void holdData(){
		
		ProductRunning productrun = addRunningProduct();
		setQtyrunning(Collections.synchronizedMap(new HashMap<Long,QtyRunning>()));
		
		QtyRunning runQty = new QtyRunning();
		runQty.setIsqtyactive(1);
		
		ProductRunning prodRun = new ProductRunning();
		prodRun.setRunid(productrun.getRunid());
		prodRun.setIsrunactive(1);
		
		for(QtyRunning items : QtyRunning.retrieve(runQty,prodRun)){
			getQtyrunning().put(items.getProduct().getProdid(), items);
		}
		
		for(PurchasedItem item : getOrders()){
			if(item.getAddOnStore()==null){
				StoreProduct.storeQuantity(false, item.getProduct(), item.getQty());
				Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunning(getQtyrunning(),item.getProduct().getProdid(), productrun, item.getProduct(), item.getQty());
				setQtyrunning(qtyrunning);
			}
		}
		
		
	}
	
	public void loadHoldItems(){
		ProductRunning run = new ProductRunning();
		run.setIsrunactive(1);
		run.setRunstatus(ProductStatus.ON_QUEUE.getId());
		
		productHolds = Collections.synchronizedList(new ArrayList<ProductRunning>());
		for(ProductRunning rn : ProductRunning.retrieve(run)){
			Customer customer = Customer.retrieve(rn.getCustomer().getCustomerid()+"");
			rn.setCustomer(customer);
			productHolds.add(rn);
		}
		
	}
	
	public void openHoldTrans(ProductRunning run){
		loadProduct(run, ProductStatus.ON_QUEUE);
	}
	
	private void loadProduct(ProductRunning run, ProductStatus status){
		QtyRunning prod = new QtyRunning();
		prod.setIsqtyactive(1);
		prod.setQtystatus(status.getId());
		
		ProductRunning prun = new ProductRunning();
		prun.setIsrunactive(1);
		prun.setRunid(run.getRunid());
		prun.setRunstatus(status.getId());
		
		setCustomer(run.getCustomer());
		setCustomername(run.getCustomer().getFullname());
		
		setProductRunning(run);
		productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
		setQtyrunning(Collections.synchronizedMap(new HashMap<Long,QtyRunning>()));
		for(QtyRunning prodHold : QtyRunning.retrieve(prod, prun)){
			ProductProperties prop = ProductProperties.properties(prodHold.getProduct().getProductProperties().getPropid()+"");
			prodHold.getProduct().setProductProperties(prop);
			
			String sql = "SELECT * FROM storeproduct WHERE prodid=? AND propid=? AND uomid=?";
			String[] params = new String[3];
			params[0] = prodHold.getProduct().getProdid()+"";
			params[1] = prop.getPropid()+"";
			params[2] = prop.getUom().getUomid()+"";
			
			StoreProduct store = StoreProduct.retrieveProduct(sql, params).get(0);
			
			store.setProductProperties(prop);
			
			PurchasedItem item = new PurchasedItem();
			String productName = store.getProductName();
			double sellingPrice = store.getSellingPrice();
			Double newQty = prodHold.getQtyhold();
			Double totalPrice = newQty * sellingPrice;
			item.setProductName(productName);
			item.setUomSymbol(store.getUomSymbol());
			item.setSellingPrice(new BigDecimal(sellingPrice));
			item.setTotalPrice(totalPrice);
			item.setQty(newQty);
			item.setProductProperties(store.getProductProperties());
			item.setProduct(prodHold.getProduct());
			item.setStoreProduct(store);
			item.setUserDtls(getLoginUser());
			productPurchasedData.put(prodHold.getProduct().getProdid(), item);
			
			getQtyrunning().put(prodHold.getProduct().getProdid(), prodHold);//added to delete data from selected order //10/06/2018
			
		}
		initOrders();
	}
	
	public void loadHistory(){
		transactions = Collections.synchronizedList(new ArrayList<Transactions>());
		Transactions tran = new Transactions();
		tran.setIsvoidtrans(1);
		String sql = "";
		int cnt =0;
		String[] params = new String[0];
		if(getSearchHistory()==null || getSearchHistory().isEmpty()){
			//tran.setBetween(true);
			//tran.setDateFrom(getDateFromHistory());
			//tran.setDateTo(getDateToHistory());
			sql = " AND ( tran.transdate>=? AND tran.transdate<=?) AND tran.isvoidtrans =1 ";
			params = new String[2];
			params[0] =  getDateFromHistory();
			params[1] =  getDateToHistory();
		}
		
		
		Customer customer = new Customer();
		customer.setIsactive(1);
		if(getSearchHistory()!=null && !getSearchHistory().isEmpty()){
			//customer.setFullname(getSearchHistory());
			if(getSearchHistory().contains("-")){
				sql += " AND tran.isvoidtrans =1 AND tran.transreceipt like '%"+ getSearchHistory() +"%'";
			}else{
				sql += " AND tran.isvoidtrans =1 AND cus.fullname like '%"+ getSearchHistory() +"%'";
			}
		}
		
		
		//transactions = Transactions.retrieve(tran,customer);
		transactions = Transactions.retrieve(sql,params);
		Collections.reverse(transactions);
	}
	
	public void showHistory(Transactions tran){
		
		clearCashier();
		
		setTransactionDataRecall(tran);
	}
	
	public void removeTransactions(){
		//if(!isValidatedRecalled()){
			setTransactionDataRecall(null);
		//}
	}
	
	private String onClickLog;
	
	
	public void validateRecall(){
		String sql = "SELECT * FROM login WHERE username=? and password=?";
		String[] params = new String[2];
		         params[0] = Whitelist.remove(getUserName());
		         params[1] = Whitelist.remove(getPassword());
		Login in = null;
		try{in = Login.retrieve(sql, params).get(0);}catch(Exception e){}
		
		if(in!=null){
			
			if(UserAccess.DEVELOPER.getId() == in.getAccessLevel().getLevel() || 
					UserAccess.OWNER.getId() == in.getAccessLevel().getLevel() || 
					UserAccess.MANAGER.getId() == in.getAccessLevel().getLevel()){
				loadRecall();
				setValidateResponse("unameId,passwordId,growl");
				setOnClickLog("PF('multiDialogAccessRight').hide();PF('multiDialogHistoryLookUp').hide();");
				setValidateResponse("formIdPos");
				addMessage("Username and Password have been validated please click close button","");
			}else{
				setValidateResponse("unameId,passwordId,growl");
				setOnClickLog("PF('multiDialogAccessRight').show();PF('multiDialogHistoryLookUp').show();");
				addMessage("You are not authorized to recall this transaction. Please contact your manager or the owner to unlock this process.","");
			}
			
		}else{
			setUserName(null);
			setPassword(null);
			setValidateResponse("unameId,passwordId,growl");
			setOnClickLog("PF('multiDialogAccessRight').show();PF('multiDialogHistoryLookUp').show();");
			addMessage("Username or Password provided is not matched. Please check your username and password.","");
		}
		
	}
	
	public void loadRecall(){
		System.out.println("recalling item...");
		if(getTransactionDataRecall()!=null){
			
			Transactions tran = new Transactions();
			tran.setIsvoidtrans(getTransactionDataRecall().getIsvoidtrans());
			tran.setTransid(getTransactionDataRecall().getTransid());
			
			ProductRunning run = new ProductRunning();
			run.setIsrunactive(1);
			run.setRunstatus(ProductStatus.DISPENSE.getId());
			///note data for transactions iomoney productrunning and qtyrunning should be modified
			try{run = ProductRunning.retrieveRecall(run,tran).get(0);}catch(Exception e){run=null;}
				
			if(run!=null){
				
				//removing paid amount
				//do not re-arrange these line of codes -
				removePaymentTrans(getTransactionDataRecall());
				recalledTransactions(); //recalling transaction data
				recalledPurchasedItemStatus(getTransactionDataRecall(), run); //recalled item status = return to ON_QUEUE
				hideProductInPurchased(); // removing product from the list
				retrieveMoneyInOutRecord(); //recording money transactions
				
				//canceling receipt
				DeliveryItemReceipt rcpt = retrievingRecallReceipt();
				rcpt.setStatus(ReceiptStatus.CANCELLED.getId());
				rcpt.setRemarks("Cancelled transactions");
				rcpt.save();
				
				loadProduct(run, ProductStatus.ON_QUEUE);
				//setValidatedRecalled(true);
				
				//deducting points
				PointsRule.addPoints(rcpt.getCustomer(), rcpt.getReceiptNo(), rcpt.getTotalAmount(), 0);
			}
			
		}
	}
	
	private void removePaymentTrans(Transactions trans){
		System.out.println("cancelling payment transactions...");
		CustomerPaymentTrans tran = new CustomerPaymentTrans();
		tran.setPaytransisactive(1);
		tran.setIspaid(1);
		tran.setReceiptNo(trans.getReceipts());
		
		try{
		tran = CustomerPaymentTrans.retrieve(tran).get(0);
		String oldRemarks = tran.getRemarks();
		tran.setPaytransisactive(0);
		tran.setIspaid(0);
		tran.setRemarks(oldRemarks + " - " + "Cancelled Transactions");
		tran.save();
		System.out.println("removing now " + tran.getReceiptNo());
		tran.delete();
		
		CustomerPayment pay = new CustomerPayment();
		pay.setPayisactive(1);
		
		Customer cus = new Customer();
		cus.setIsactive(1);
		cus.setCustomerid(trans.getCustomer().getCustomerid());
		
		try{
		pay = CustomerPayment.retrieve(pay,cus).get(0);
		double oldBal = pay.getAmountbalance().doubleValue();
		double newBal = 0d;
		double currentBal = trans.getAmountbal().doubleValue();
		System.out.println("old bal " + oldBal);
		System.out.println("current bal " + currentBal);
		if(oldBal>currentBal){
			newBal = oldBal - currentBal;
		}
		System.out.println("new bal " + newBal);
		pay.setAmountbalance(new BigDecimal(newBal));
		pay.setAmountpaid(pay.getAmountprevpaid());
		pay.setAmountpaiddate(pay.getAmountprevpaiddate());
		pay.save();
		System.out.println("recalling amount... previous paid");
		}catch(Exception e){}
		
		}catch(Exception e){}
	}
	
	/**
	 * Hiding product
	 */
	private void hideProductInPurchased(){
		if(getTransactionDataRecall()!=null){
			
			Transactions tran = new Transactions();
			tran.setIsvoidtrans(1);
			tran.setTransid(getTransactionDataRecall().getTransid());
			
			PurchasedItem pitem = new PurchasedItem();
			pitem.setIsactiveitem(1);
			
			for(PurchasedItem item : PurchasedItem.retrieve(pitem, tran)){
				item.setIsactiveitem(0);
				item.save();
			}
		}
	}
	
	/**
	 * recording recalled money transactions
	 */
	private void retrieveMoneyInOutRecord(){
		if(getTransactionDataRecall()!=null){
			String sql = "SELECT * FROM iomoney WHERE receiptno=? ORDER BY ioid DESC";
			String[] params = new String[1];
			params[0] = getTransactionDataRecall().getReceipts();
			MoneyIO io = null;
			try{io = MoneyIO.retrieve(sql, params).get(0);}catch(Exception e){}
			if(io!=null){
				io.setDescripion(io.getDescripion() + " - Cancelled Transactions. Amounting to Php " + Currency.formatAmount(io.getInAmount()));
				io.setInAmount(0);
				io.setOutAmount(0);
				//io.setUserDtls(Login.getUserLogin().getUserDtls());
				io.setUserDtls(getLoginUser());
				MoneyIO.save(io);
			}
		}
	}
	
	private DeliveryItemReceipt retrievingRecallReceipt(){
		System.out.println("Retrieving recalled receipt");
		DeliveryItemReceipt rpt = new DeliveryItemReceipt();
		rpt.setIsActive(1);
		rpt.setReceiptNo(getTransactionDataRecall().getReceipts());
		return DeliveryItemReceipt.retrieve(rpt).get(0);
	}
	
	private void recalledPurchasedItemStatus(Transactions trans, ProductRunning prodRun){
		
		prodRun.setRunstatus(ProductStatus.ON_QUEUE.getId());
		prodRun.setRunremarks(ProductStatus.ON_QUEUE.getName());
		prodRun.setTransactions(trans);
		//prodRun.setUserDtls(Login.getUserLogin().getUserDtls());
		prodRun.setUserDtls(getLoginUser());
		prodRun.save();
		
		ProductRunning pRun = new ProductRunning();
		pRun.setRunid(prodRun.getRunid());
		pRun.setIsrunactive(1);
		
		for(QtyRunning run : QtyRunning.retrieve(pRun)){
			run.setQtystatus(ProductStatus.ON_QUEUE.getId());
			run.setQtyremarks(ProductStatus.ON_QUEUE.getName());
			//run.setUserDtls(Login.getUserLogin().getUserDtls());
			run.setUserDtls(getLoginUser());
			run.save();
		}
	}
	
	private void recalledTransactions(){
		if(getTransactionDataRecall()!=null){
			Transactions tran = getTransactionDataRecall();
			tran.setAmountbal(new BigDecimal("0.00"));
			tran.setAmountpurchased(new BigDecimal("0.00"));
			tran.setAmountreceived(new BigDecimal("0.00"));
			tran.setDiscount(new BigDecimal("0.00"));
			tran.setAmountchange(new BigDecimal("0.00"));
			tran.setVatsales(new BigDecimal("0.00"));
			tran.setVatexmptsales(new BigDecimal("0.00"));
			tran.setZeroratedsales(new BigDecimal("0.00"));
			tran.setVatnet(new BigDecimal("0.00"));
			tran.setVatamnt(new BigDecimal("0.00"));
			tran.save();
		}
	}
	
	public void chargeDispense() {
		if(getAmountin()==null || getAmountin().isEmpty() || getBalanceamnt().doubleValue()>1){
			dispenseItems(true);
		}else {
			dispenseItems(false);
		}
		
	}
	
	private void dispenseItems(boolean isCharge) {
		String val = ReadConfig.value(Ipos.OUTSIDE_SAVING_ENABLE);
		String printerIsOn = ReadConfig.value(Ipos.PRINTER_ISON);
		if(getOrders()!=null && getOrders().size()>0){
		
			if(getAmountin()==null || getAmountin().isEmpty()){
				setAmountin("0.00");
			}
			
			String tmpAmnt = getAmountin().replace("?", "");
			setAmountin(tmpAmnt.replace("?", ""));
				
				//save customer info
				Customer customer = new Customer();
				customer = updateCustomer();
				
				//Assign new receipt no
				String receiptNo = DeliveryItemReceipt.generateNewReceiptNo();
				//receiptNo = DateUtils.getCurrentDateYYYYMMDD() + " " + receiptNo;
				
				if("false".equalsIgnoreCase(val)){
				
				//save customer transactions
				Transactions trans = new Transactions();
				trans = addTransactions(customer,receiptNo);
				
				//save customer purchased items
				List<PurchasedItem> items = Collections.synchronizedList(new ArrayList<PurchasedItem>());
				if(isOnLoadDemand()){
					items = addPurchasedItem(trans);
				}else{
					items = addPurchasedItemForFirstTime(trans);
				}
				
				
				if(isCharge) {
					ChargeInvoice in = new ChargeInvoice();
					in.setIsActive(1);
					in.setTerms(getTermId());
					in.setDueDate(DateUtils.convertDate(getDueDate(), "yyyy-MM-dd"));
					in.setReceiptNo(receiptNo);
					in.setBalanceAmount(getBalanceamnt().doubleValue());
					in.setTransactions(trans);
					in.setCustomer(customer);
					in.save();
				}
				
				//save receipt
				saveToReceipt(trans,receiptNo);
				
				
				//save to IOMoney
				saveMoneyIO(trans);
									
				if(isOnLoadDemand()){
					purchasedItemStatus(trans);
				}else{
					purchasedItemStatusFirstTime(trans);
				}
				
				saveAddOns(trans); // additional item to charge
				
				
				
				}else{
					//outside saving
					double[] money = new double[8];
					try{money[0] = Double.valueOf(getAmountin().replace(",",""));}catch(Exception e){money[0]=0;}
					try{money[1] = getPurchasedPrice();}catch(Exception e){money[1]=0;}
					try{money[2] = Double.valueOf(getDiscountAmount().replace(",", ""));}catch(Exception e){money[2]=0;}
					try{money[3] = getBalanceamnt().doubleValue();}catch(Exception e){money[3]=0;}
					try{money[4] = getChangeamnt().doubleValue();}catch(Exception e){money[4]=0;}
					try{money[5] = getTotalTaxable();}catch(Exception e){money[5]=0;}
					try{money[6] = getTotalNonTaxable();}catch(Exception e){money[6]=0;}
					try{money[7] = getTotalVat();}catch(Exception e){money[7]=0;}
					
					LogU.add("creating xml file>>>");
					DispenseReadWriteXML.writeDispenseProductInXML(isOnLoadDemand(), receiptNo, customer, getProductPurchasedData(), getProductRunning(), getXtras(), loginUser, money, getItemCount(), getTermId(),DateUtils.convertDate(getDueDate(), "yyyy-MM-dd"));
					LogU.add("End creatinf xml");
					String loadDBFile = ReadConfig.value(Ipos.EXECUTABLE_JAR_CASHIER);
					//commented due to migrated to openjdk11
					//try{Runtime.getRuntime().exec(" java -jar " + loadDBFile);}catch(Exception e){}
					try{
						LogU.add("Running jar file");
						Runtime.getRuntime().exec(loadDBFile.replace("\\", File.separator));
						LogU.add("Successfully run the jar file at " + loadDBFile);
						}catch(Exception e){
							LogU.add("Error Running jar file >> " + e.getMessage());
						}
				}
				
				
				
				//save copy of receipt in text format file
				ReceiptRecording.saveToFileReceipt(getReceiptView(), receiptNo);
				
				if("1".equalsIgnoreCase(printerIsOn)){//active
					cashdrawerOpen();
					printReceipt(receiptNo); //send to printer
					cuttReceiptPaper();
				}
				
				//add points if card is available
				PointsRule.addPoints(customer,receiptNo, getPurchasedPrice(),1);
				
				clearAll();
				addMessage("Product dispense successfully.","");
			
		}else{
			addMessage("Please select item/s first before dispensing an item.","");
		}
	}
	
	public void clearSearchProduct() {
		setSearchProduct(null);
	}
	
	public void clearAll() {
		setTotalNonTaxable(0);
		setTotalTaxable(0);
		setTotalVat(0);
		
		//refresh view
		setCustomername("UNKNOWN");
		setCustomer(null);
		setAmountin(null);
		setBalanceamnt(null);
		setReceiptView(null);
		setGrandTotalPrice(null	);
		setDiscountAmount(null);
		setTransactionDataRecall(null);
		setItemCount(0);
		setProductRunning(null);
		setXtraData(null);
		xtras = Collections.synchronizedList(new ArrayList<AddOnStore>());
		qtyrunning = Collections.synchronizedMap(new HashMap<Long,QtyRunning>());
		productHolds = Collections.synchronizedList(new ArrayList<ProductRunning>());
		transactions = Collections.synchronizedList(new ArrayList<Transactions>());
		orders = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
	}
	
	@Deprecated
	public void dispense(){
		
		if(getAmountin()==null || getAmountin().isEmpty() || getBalanceamnt().doubleValue()>1){
			
		}else {
			
		}
	}
	
	public void printReceipt(String receiptNo){
		
		String receiptLocation = ReadConfig.value(Ipos.RECEIPTS_LOG);
		String receiptFile = receiptLocation + receiptNo + ".txt";
		
		//PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		//DocPrintJob job = service.createPrintJob();
		/*URL url = new URL(
		   "http://www.apress.com/ApressCorporate/supplement/1/421/bcm.gif ");*/
		try{
			
		PrintService[] printServices;
		String printerName = ReadConfig.value(Ipos.PRINTER_NAME);
		
		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		printServiceAttributeSet.add(new PrinterName(printerName, null));
		printServices = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);
		
		
		DocPrintJob job = printServices[0].createPrintJob();
		
		//String FILE_NAME = "C:\\ipos\\receipts\\000000000000026.txt";
		FileInputStream textStream = new FileInputStream(receiptFile);
		
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(textStream, flavor, null);
		PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
		attrs.add(new Copies(1));
		job.print(doc, attrs);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cashdrawerOpen() {
        
        byte[] open = {27, 112, 48, 55, 121};
        String printer = ReadConfig.value(Ipos.PRINTER_NAME);
        PrintServiceAttributeSet printserviceattributeset = new HashPrintServiceAttributeSet();
        printserviceattributeset.add(new PrinterName(printer,null));
        PrintService[] printservice = PrintServiceLookup.lookupPrintServices(null, printserviceattributeset);
        if(printservice.length!=1){
            System.out.println("Printer not found");
        }
        PrintService pservice = printservice[0];
        DocPrintJob job = pservice.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(open,flavor,null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        try {
            job.print(doc, aset);
        } catch (PrintException ex) {
            System.out.println(ex.getMessage());
        }
    }
	
	public void cuttReceiptPaper() {
        
		byte[] cutter = {29, 86,49};
        String printer = ReadConfig.value(Ipos.PRINTER_NAME);
        PrintServiceAttributeSet printserviceattributeset = new HashPrintServiceAttributeSet();
        printserviceattributeset.add(new PrinterName(printer,null));
        PrintService[] printservice = PrintServiceLookup.lookupPrintServices(null, printserviceattributeset);
        if(printservice.length!=1){
            System.out.println("Printer not found");
        }
        PrintService pservice = printservice[0];
        DocPrintJob job = pservice.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(cutter,flavor,null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        try {
            job.print(doc, aset);
        } catch (PrintException ex) {
            System.out.println(ex.getMessage());
        }
    }
	
	@Deprecated
	private void trackQuantityDispense(List<PurchasedItem> items){
		for(PurchasedItem item : items){
			Product prod = new Product();
			prod.setIsactiveproduct(1);
			prod.setProdid(item.getProduct().getProdid());
			ProductInventory inv = ProductInventory.retrieve(prod).get(0);
			inv.setAddqty(-item.getQty());
			//inv.setUserDtls(Login.getUserLogin().getUserDtls());
			inv.setUserDtls(getLoginUser());
			InputedInventoryQtyTracker.saveQty(inv,"SOLD PRODUCT IN STORE");
		}
	}
	
	private void saveAddOns(Transactions trans){
		
		if(getXtras()!=null && getXtras().size()>0){
		
			System.out.println("Xtra product is not null " + getXtras().size());
		
		
			ProductRunning run = new ProductRunning();
			run.setRunid(getProductRunning().getRunid());
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
	
	private void purchasedItemStatus(Transactions trans){
		ProductRunning prodRun = getProductRunning();
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
	
	private void purchasedItemStatusFirstTime(Transactions trans){
		ProductRunning prodRun = getProductRunning();
		prodRun.setRunstatus(ProductStatus.DISPENSE.getId());
		prodRun.setRunremarks(ProductStatus.DISPENSE.getName());
		prodRun.setTransactions(trans);
		prodRun.save();
	}
	
	private void saveToReceipt(Transactions trans, String receiptNo){
		DeliveryItemReceipt rec = new DeliveryItemReceipt();
		
		
		//this is for recalled
		//product that already dispense
		//if(getTransactionDataRecall()!=null){
		//	rec = retrievingRecallReceipt();
		//	rec.setRemarks("Recalled receipts");
		//}else{
			rec.setReceiptNo(receiptNo);//generate new receipt number
			rec.setRemarks("Normal transactions");
		//}
		
		rec.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		rec.setStatus(ReceiptStatus.POSTED.getId());
		rec.setIsActive(1);
		
		
		rec.setDeliveryChargeAmount(0);
		
		
		rec.setTotalAmount(trans.getAmountpurchased().doubleValue());
		rec.setBalanceAmount(trans.getAmountbal().doubleValue());
		rec.setDiscountAmount(trans.getDiscount().doubleValue());
		rec.setQuantity(getItemCount());
		rec.setDownPayment(trans.getAmountreceived().doubleValue());
		
		//check payment
		if(trans.getAmountbal().doubleValue()==0){
			rec.setPaymentStatus(ReceiptStatus.FULL.getId());
		}else{
			rec.setPaymentStatus(ReceiptStatus.PARTIAL.getId());
		}
		
		rec.setCustomer(trans.getCustomer());
		
		//rec.setUserDtls(Login.getUserLogin().getUserDtls());
		rec.setUserDtls(getLoginUser());
		
		rec.save();
		
	}
	
	private void saveMoneyIO(Transactions trans){
		
		System.out.println("save money checking customer " + trans.getCustomer().getFullname() + " id " + trans.getCustomer().getCustomerid());
		
		if(trans.getAmountbal().doubleValue()==0){
			
			double amount = 0d;
			if(trans.getAmountreceived().doubleValue()>trans.getAmountpurchased().doubleValue()){
				amount = trans.getAmountpurchased().doubleValue();
				amount -= trans.getDiscount().doubleValue();//solved in 8.0
			}else{
				amount = trans.getAmountreceived().doubleValue();
			}
			
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			io.setDescripion("Full payment in Store. Paid by " + trans.getCustomer().getFullname());
			io.setTransType(MoneyStatus.INCOME.getId());
			io.setInAmount(amount);
			//io.setUserDtls(Login.getUserLogin().getUserDtls());
			io.setUserDtls(getLoginUser());
			io.setReceiptNo(trans.getReceipts());
			io.setCustomer(trans.getCustomer());
			
			MoneyIO.save(io);
			
			Payment.customerPayment(trans.getCustomer(), trans.getAmountbal().doubleValue(), amount, PaymentTransactionType.STORE, "Full payment. Paid by "+ trans.getCustomer().getFullname(), trans.getReceipts());
			
		}else{
			
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			io.setTransType(MoneyStatus.INCOME.getId());
			//io.setUserDtls(Login.getUserLogin().getUserDtls());
			io.setUserDtls(getLoginUser());
			
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
	
	/**
	 * 
	 * This method has been moved to Payment.java
	 */
	@Deprecated
	private void customerpaybal(Customer customer){
		Customer cus = new Customer();
		cus.setCustomerid(customer.getCustomerid());
		cus.setIsactive(1);
		CustomerPayment py = new CustomerPayment();
		py.setPayisactive(1);
		List<CustomerPayment> payments = CustomerPayment.retrieve(py,cus);
		CustomerPayment payment = new CustomerPayment();
		
		double bal = Double.valueOf(getBalanceamnt()+"");
		BigDecimal baltmp = new BigDecimal(Math.abs(bal));
		
		if(payments.size()>0){
			payment = payments.get(0);
		CustomerPaymentTrans trans = new CustomerPaymentTrans();
		trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
		trans.setAmountpay(new BigDecimal(Currency.removeComma(getAmountin())));
		trans.setIspaid(1);
		trans.setPaytransisactive(1);
		trans.setCustomerPayment(payment);
		//trans.setUserDtls(Login.getUserLogin().getUserDtls());
		trans.setUserDtls(getLoginUser());
		trans.save();
		
		//set temp data
		BigDecimal balamnt = payment.getAmountbalance();
		BigDecimal amntpaidorig = payment.getAmountpaid();
		String paiddate = payment.getAmountpaiddate();
		
		
		//BigDecimal addBalamnt =  balamnt.add(baltmp);
		BigDecimal newBalamnt =  balamnt.add(baltmp);  //addBalamnt.subtract(new BigDecimal(Currency.removeComma(getAmountin())));
		
		//Update customerpayment table
		payment.setAmountpaid(new BigDecimal(Currency.removeComma(getAmountin())));
		payment.setAmountpaiddate(DateUtils.getCurrentDateYYYYMMDD());
		
		payment.setAmountprevpaid(amntpaidorig);
		payment.setAmountprevpaiddate(paiddate);
		
		payment.setAmountbalance(newBalamnt);
		payment.setAmountprevbalance(balamnt);
		payment.save();
		}else{
			
			
			
			payment = new CustomerPayment();
			payment.setAmountpaid(new BigDecimal(Currency.removeComma(getAmountin())));
			payment.setAmountpaiddate(DateUtils.getCurrentDateYYYYMMDD());
			payment.setAmountprevpaid(new BigDecimal("0"));
			payment.setAmountprevpaiddate(DateUtils.getCurrentDateYYYYMMDD());
			payment.setAmountbalance(baltmp);
			payment.setAmountprevbalance(new BigDecimal("0"));
			payment.setCustomer(customer);
			payment.setPayisactive(1);
			//payment.setUserDtls(Login.getUserLogin().getUserDtls());
			payment.setUserDtls(getLoginUser());
			payment = CustomerPayment.save(payment);
			
			
			CustomerPaymentTrans trans = new CustomerPaymentTrans();
			trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
			trans.setAmountpay(new BigDecimal(Currency.removeComma(getAmountin())));
			trans.setIspaid(1);
			trans.setPaytransisactive(1);
			trans.setCustomerPayment(payment);
			//trans.setUserDtls(Login.getUserLogin().getUserDtls());
			trans.setUserDtls(getLoginUser());
			trans.save();
			
		}
		
	}
	
	private Customer updateCustomer(){
		Customer customer = new Customer();
		if(getCustomer()!=null){
			customer = getCustomer();
		}else{
			customer = Customer.customer("1"); // get the walk-in customer
			/*customer = Customer.addUnknownCustomer();
			customer = Customer.save(customer);*/
		}
		return customer;
	}
	
	private Transactions addTransactions(Customer customer, String receiptNo){
		Transactions trans = new Transactions();
		//if(getTransactionDataRecall()!=null){
		//	trans = getTransactionDataRecall();
		//}else{
			trans.setReceipts(receiptNo);
		//}
		
		trans.setTransdate(DateUtils.getCurrentDateYYYYMMDD());
		trans.setCustomer(customer);
		trans.setAmountpurchased(new BigDecimal(getPurchasedPrice()));
		
		trans.setAmountreceived(new BigDecimal(Currency.removeComma(getAmountin())));
		double amountIn = 0d;
		try{amountIn = Double.valueOf(getAmountin().replace(",",""));}catch(Exception e){}
		if(amountIn==0){
			trans.setAmountbal(trans.getAmountpurchased());
		}else{
			trans.setAmountbal(new BigDecimal(Math.abs(getBalanceamnt().doubleValue())));
		}
		
		trans.setAmountchange(getChangeamnt());
		
		trans.setIsvoidtrans(1);
		//trans.setUserDtls(Login.getUserLogin().getUserDtls());
		trans.setUserDtls(getLoginUser());
		
		BigDecimal amnt = new BigDecimal("0"); 
		try{amnt = new BigDecimal(getDiscountAmount().replace(",", ""));}catch(NumberFormatException num){}
		trans.setDiscount(amnt);
		
		//vat
		trans.setVatsales(new BigDecimal("0"));
		trans.setVatexmptsales(new BigDecimal("0"));
		trans.setZeroratedsales(new BigDecimal("0"));
		trans.setVatnet(new BigDecimal("0"));
		trans.setVatamnt(new BigDecimal("0"));
		
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
	
	private List<PurchasedItem> addPurchasedItemForFirstTime(Transactions trans){
		List<PurchasedItem> items = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		
		if(orders!=null && orders.size()>0){
			
			ProductRunning productrun = new ProductRunning();
			productrun = addRunningProduct();
			
			QtyRunning runQty = new QtyRunning();
			runQty.setIsqtyactive(1);
			runQty.setQtystatus(ProductStatus.ON_QUEUE.getId());
			
			ProductRunning prodRun = new ProductRunning();
			prodRun.setRunid(productrun.getRunid());
			prodRun.setIsrunactive(1);
			
			for(QtyRunning itmQty : QtyRunning.retrieve(runQty,prodRun)){
				//getQtyrunning().put(itmQty.getProduct().getProdid(), itmQty);
				
				long id = itmQty.getProduct().getProdid();
				if(getProductPurchasedData().containsKey(id)){
					
					PurchasedItem item = getProductPurchasedData().get(id);
					
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
						item.setUserDtls(getLoginUser());
						
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
						itmQty.setUserDtls(getLoginUser());
						itmQty = QtyRunning.save(itmQty);
						
						getProductPurchasedData().remove(id); //removing data that already save// this will be use for faster iteration on below codes
					}	
					
				}
				
			}
			
			//additional product not yet save in on hold
			setQtyrunning(Collections.synchronizedMap(new HashMap<Long,QtyRunning>()));
			for(PurchasedItem item : getProductPurchasedData().values()){
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
					item.setUserDtls(getLoginUser());
					
					item = PurchasedItem.save(item);
					items.add(item);
					
					
					StoreProduct.storeQuantity(false, item.getProduct(), item.getQty());
					
					Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunningDispense(getQtyrunning(),item.getProduct().getProdid(), productrun, item.getProduct(), item.getQty());
					setQtyrunning(qtyrunning);
					
					}
			}
			
			/*for(PurchasedItem item : orders){
				
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
				item.setUserDtls(Login.getUserLogin().getUserDtls());
				
				item = PurchasedItem.save(item);
				items.add(item);
				
				
				StoreProduct.storeQuantity(false, item.getProduct(), item.getQty());
				
				Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunningDispense(getQtyrunning(),item.getProduct().getProdid(), productrun, item.getProduct(), item.getQty());
				setQtyrunning(qtyrunning);
				
				}
			}*/
			
		}
		
		return items;
	}
	
	private List<PurchasedItem> addPurchasedItem(Transactions trans){
		List<PurchasedItem> items = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		
		if(orders!=null && orders.size()>0){
			
			for(PurchasedItem item : orders){
				
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
				item.setUserDtls(getLoginUser());
				
				item = PurchasedItem.save(item);
				items.add(item);
				}
			}
			
		}
		
		return items;
	}
	
	private void searchBarcode(){
		
		setQtyToCart("1");
		String sql = " AND store.prodIsActive=1 AND store.qty!=0 AND store.barcode=?";
		String[] params = new String[1];
		String barcode = Whitelist.remove(getSearchCode());
		barcode = barcode.substring(0, 12);
		System.out.println("barcode >> " + barcode);
		params[0] = barcode;
		
		StoreProduct store = null;
		try{store = StoreProduct.retrieve(sql, params).get(0);}catch(IndexOutOfBoundsException e){}
		
		if(store!=null){
			ProductProperties proper = ProductProperties.properties(store.getProductProperties().getPropid()+"");
			store.setProductProperties(proper);
			addToCart(store);
		}
		setSearchCode(null);
		
	}
	
	public void taxInfo(){
		String str = getReceiptView();
		str += "\n\n";
		str +="VATABLE SALES: \n";
		str +="VAT-EXEMT SALES: \n";
		str +="ZERO-RATED SALES: \n";
		str +="12% VAT (NET): \n";
		str +="Total Amount: \n";
		setReceiptView(str);
	}

	public void posDeveloper(){
		ReceiptInfo rp = ReceiptXML.value();
		String str = getReceiptView();
		//str += "\n";
		if(rp.getPosDistributor()!=null && !rp.getPosDistributor().isEmpty()){
			str += rp.getPosDistributor() + "\n";
		}
		if(rp.getPosDistributorAddress()!=null && !rp.getPosDistributorAddress().isEmpty()){
			str += rp.getPosDistributorAddress() + "\n";
		}
		if(rp.getPosDisTinNumber()!=null && !rp.getPosDisTinNumber().isEmpty()){
			str += rp.getPosDisTinNumber() +"\n";
		}
		if(rp.getPosCreditedNumber()!=null && !rp.getPosCreditedNumber().isEmpty()){
			str += rp.getPosCreditedNumber() + "\n";
		}
		if(rp.getPosDateRegistered()!=null && !rp.getPosDateRegistered().isEmpty()){
			str += rp.getPosDateRegistered() + "\n";
		}
		if(rp.getPosDetails1()!=null && !rp.getPosDetails1().isEmpty()){
			str += rp.getPosDetails1() + "\n";
		}
		if(rp.getPosDetails2()!=null && !rp.getPosDetails2().isEmpty()){
			str += rp.getPosDetails2() + "\n";
		}
		
		setReceiptView(str);
	}
	
	public void returnPolicy(){
		ReceiptInfo rp = ReceiptXML.value();
		String str = getReceiptView();
		
		if(rp.getPosDetails3()!=null && !rp.getPosDetails3().isEmpty()){
		str += rp.getPosDetails3()+ "\n";
		}
		if(rp.getPosDetails4()!=null && !rp.getPosDetails4().isEmpty()){
		str +=  rp.getPosDetails4() + "\n";
		}
		if(rp.getPosDetails5()!=null && !rp.getPosDetails5().isEmpty()){
		str += rp.getPosDetails5() +"\n";
		}
		setReceiptView(str);
	}
		
	public void clearFields(){
		//setSearchCode(null);
		//setAmountin(null);
		//setCustomer(null);
	}
	
	public void addDiscount(){
		initOrders();
	}
	
	public void initReturn(){
		
		prodReturn = Collections.synchronizedList(new ArrayList<StoreProduct>());
		
		if(getSearchProductReturn()!=null && !getSearchProductReturn().isEmpty()){
			
			StoreProduct store = new StoreProduct();
			store.setIsActive(1);
			
			long barcode = 0l;
			try{
				barcode = Long.valueOf(getSearchProductReturn());
			}catch(Exception e){}
			
			if(barcode!=0){
				store.setBarcode(getSearchProductReturn());
			}else{
				store.setProductName(getSearchProductReturn());
			}
			
			prodReturn = StoreProduct.retrieve(store);
			
		}
		
	}
	
	public void saveReturn(StoreProduct store){
		
		if(Login.checkUserStatus()){
			
			double newQty = store.getQuantity() + store.getQtyReturn();
			store.setQuantity(newQty);
			store.save();
			
			//record in xtra table
			Xtras xtra = new Xtras();
			xtra.setIsActive(1);
			xtra.setDescription("Return product " + store.getProductName() + " qty(" + store.getQtyReturn() + ")");
			xtra.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			xtra.setAmount(store.getSellingPrice());
			xtra.setStatus(Status.POSTED.getId());
			xtra.setCustomer(getCustomer());
			//xtra.setUserDtls(Login.getUserLogin().getUserDtls());
			xtra.setUserDtls(getLoginUser());
			xtra.setRemarks("Transaction from Cashier");
			xtra.setTransType(MoneyStatus.REFUND.getId());
			xtra.save();
			
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			io.setTransType(MoneyStatus.REFUND.getId());
			io.setDescripion("Return product "+ store.getProductName() + "("+ store.getQtyReturn() +") was returned by " + getCustomer().getFullname());
			io.setOutAmount(store.getQtyReturn() * store.getSellingPrice());
			io.setReceiptNo("0000-00-00-0000000000");
			io.save(io);
			initReturn();
			addMessage("Return item has been successfully saved.","");
			
		}
		
	}
	
	public void viewReceiptHistory(Transactions trans){
		String printerIsOn = ReadConfig.value(Ipos.PRINTER_ISON);
		setReceiptViewHistory(ReceiptRecording.viewReceipt(trans.getReceipts()));
		ReceiptRecording.saveToFileReceipt("\t****REPRINT****\n" + getReceiptViewHistory(), trans.getReceipts());
		
		if("1".equalsIgnoreCase(printerIsOn)){
			cashdrawerOpen();
			printReceipt(trans.getReceipts());
			cuttReceiptPaper();
		}
		
		ReceiptRecording.saveToFileReceipt(getReceiptViewHistory(), trans.getReceipts());
	}
	
public String getAmountin() {
	return amountin;
}
public void setAmountin(String amountin) {
	this.amountin = amountin;
}
public String getCashiername() {
	UserDtls userLog = getLoginUser();
	cashiername = userLog.getFirstname() + " " + userLog.getLastname(); 
	return cashiername;
}
public void setCashiername(String cashiername) {
	this.cashiername = cashiername;
}
public BigDecimal getBalanceamnt() {
	if(balanceamnt==null){
		balanceamnt = new BigDecimal("0.00");
	}
	return balanceamnt;
}
public void setBalanceamnt(BigDecimal balanceamnt) {
	this.balanceamnt = balanceamnt;
}
public BigDecimal getChangeamnt() {
	return changeamnt;
}
public void setChangeamnt(BigDecimal changeamnt) {
	this.changeamnt = changeamnt;
}
public String getCustomername() {
	return customername;
}
public void setCustomername(String customername) {
	this.customername = customername;
}
public boolean isEnableKey() {
	return enableKey;
}
public void setEnableKey(boolean enableKey) {
	this.enableKey = enableKey;
}
public List<Customer> getCustomers() {
	return customers;
}
public void setCustomers(List<Customer> customers) {
	this.customers = customers;
}
public String getSearchCustomer() {
	return searchCustomer;
}
public void setSearchCustomer(String searchCustomer) {
	this.searchCustomer = searchCustomer;
}
public Customer getCustomer() {
	if(customer==null){
		customer = new Customer();
		//customer.setCustomerid(1); fasdf
		customer = Customer.customer("1"); // get the walk-in customer
	}
	return customer;
}
public void setCustomer(Customer customer) {
	this.customer = customer;
}
public ProductRunning getProductRunning() {
	return productRunning;
}
public void setProductRunning(ProductRunning productRunning) {
	this.productRunning = productRunning;
}

public Map<Long, QtyRunning> getQtyrunning() {
	return qtyrunning;
}
public void setQtyrunning(Map<Long, QtyRunning> qtyrunning) {
	this.qtyrunning = qtyrunning;
}
public String getDiscountAmount() {
	if(discountAmount==null) {
		discountAmount = "0.00";
	}
	return discountAmount;
}
public void setDiscountAmount(String discountAmount) {
	this.discountAmount = discountAmount;
}
public double getPurchasedPrice() {
	return purchasedPrice;
}
public void setPurchasedPrice(double purchasedPrice) {
	this.purchasedPrice = purchasedPrice;
}
public String getBusinessName() {
	
	if(businessName==null){
		businessName = ReadConfig.value(Ipos.BUSINESS_NAME);
	}
	
	return businessName;
}
public void setBusinessName(String businessName) {
	this.businessName = businessName;
}
public List<ProductRunning> getProductHolds() {
	return productHolds;
}
public void setProductHolds(List<ProductRunning> productHolds) {
	this.productHolds = productHolds;
}
public List<Transactions> getTransactions() {
	return transactions;
}
public void setTransactions(List<Transactions> transactions) {
	this.transactions = transactions;
}
public String getDateFromHistory() {
	if(dateFromHistory==null){
		dateFromHistory = DateUtils.getCurrentDateYYYYMMDD();
	}
	return dateFromHistory;
}
public void setDateFromHistory(String dateFromHistory) {
	this.dateFromHistory = dateFromHistory;
}
public String getDateToHistory() {
	if(dateToHistory==null){
		dateToHistory = DateUtils.getCurrentDateYYYYMMDD();
	}
	return dateToHistory;
}
public void setDateToHistory(String dateToHistory) {
	this.dateToHistory = dateToHistory;
}
public String getSearchHistory() {
	return searchHistory;
}
public void setSearchHistory(String searchHistory) {
	this.searchHistory = searchHistory;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String getValidateResponse() {
	if(validateResponse==null){
		validateResponse = "growl";
	}
	return validateResponse;
}
public void setValidateResponse(String validateResponse) {
	this.validateResponse = validateResponse;
}
public Transactions getTransactionDataRecall() {
	return transactionDataRecall;
}
public void setTransactionDataRecall(Transactions transactionDataRecall) {
	this.transactionDataRecall = transactionDataRecall;
}
public String getOnClickLog() {
	if(onClickLog==null){
		onClickLog = "PF('multiDialogAccessRight').show();";
	}
	return onClickLog;
}
public void setOnClickLog(String onClickLog) {
	this.onClickLog = onClickLog;
}
public List<AddOnStore> getXtras() {
	return xtras;
}
public void setXtras(List<AddOnStore> xtras) {
	this.xtras = xtras;
}
public AddOnStore getXtraData() {
	return xtraData;
}
public void setXtraData(AddOnStore xtraData) {
	this.xtraData = xtraData;
}
public String getXtraDescription() {
	return xtraDescription;
}
public void setXtraDescription(String xtraDescription) {
	this.xtraDescription = xtraDescription;
}
public double getXtraAmount() {
	return xtraAmount;
}
public void setXtraAmount(double xtraAmount) {
	this.xtraAmount = xtraAmount;
}
public List getXtraTypes() {
	
	xtraTypes = new ArrayList<>();
	xtraTypes.add(new SelectItem(1, "Additional"));
	xtraTypes.add(new SelectItem(2, "Less"));
	
	return xtraTypes;
}
public void setXtraTypes(List xtraTypes) {
	this.xtraTypes = xtraTypes;
}
public int getXtraTypeId() {
	if(xtraTypeId==0){
		xtraTypeId = 1;
	}
	return xtraTypeId;
}
public void setXtraTypeId(int xtraTypeId) {
	this.xtraTypeId = xtraTypeId;
}
/*public boolean getOnHoldButton() {
	if(!Features.isEnabled(com.italia.ipos.enm.Features.ON_HOLD_BUTTON)){
		onHoldButton = true;
	}
	return onHoldButton;
}
public void setOnHoldButton(boolean onHoldButton) {
	this.onHoldButton = onHoldButton;
}*/
/*public boolean getHistoryButton() {
	if(!Features.isEnabled(com.italia.ipos.enm.Features.HISTORY_BUTTON)){
		historyButton = true;
	}
	return historyButton;
}
public void setHistoryButton(boolean historyButton) {
	this.historyButton = historyButton;
}*/
public boolean getMoveToOnHoldButton() {
	
	if(getOrders()!=null && getOrders().size()>0){
	
		if(!Features.isEnabled(com.italia.ipos.enm.Features.ON_HOLD_BUTTON)){
			moveToOnHoldButton = true;
		}else{
			moveToOnHoldButton = false;
		}
	
	}else{
		moveToOnHoldButton = true;
	}
	
	return moveToOnHoldButton;
}
public void setMoveToOnHoldButton(boolean moveToOnHoldButton) {
	this.moveToOnHoldButton = moveToOnHoldButton;
}
/*public boolean getXtraProductButton() {
	if(!Features.isEnabled(com.italia.ipos.enm.Features.XTRA_PRODUCT_BUTTON)){
		xtraProductButton = true;
	}
	return xtraProductButton;
}
public void setXtraProductButton(boolean xtraProductButton) {
	this.xtraProductButton = xtraProductButton;
}*/
public List<StoreProduct> getProdReturn() {
	return prodReturn;
}
public void setProdReturn(List<StoreProduct> prodReturn) {
	this.prodReturn = prodReturn;
}
public String getSearchProductReturn() {
	return searchProductReturn;
}
public void setSearchProductReturn(String searchProductReturn) {
	this.searchProductReturn = searchProductReturn;
}
/*public boolean isReturnProductButton() {
	if(!Features.isEnabled(com.italia.ipos.enm.Features.CASHIER_PRODUCT_RETURN)){
		returnProductButton = true;
	}
	return returnProductButton;
}
public void setReturnProductButton(boolean returnProductButton) {
	this.returnProductButton = returnProductButton;
}*/
public String getReceiptViewHistory() {
	return receiptViewHistory;
}
public void setReceiptViewHistory(String receiptViewHistory) {
	this.receiptViewHistory = receiptViewHistory;
}

public void printSalesInvoice(Transactions transactions){
	
	ProductRunning run = new ProductRunning();
	run.setIsrunactive(1);
	
	Transactions trans = new Transactions();
	trans.setIsvoidtrans(transactions.getIsvoidtrans());
	trans.setTransid(transactions.getTransid());
	
	run = ProductRunning.retrieveRecall(run, trans).get(0);
	
	AddOnStore addOn = new AddOnStore();
	addOn.setIsActive(1);
	
	ProductRunning runagain = new ProductRunning();
	runagain.setIsrunactive(1);
	runagain.setRunid(run.getRunid());
	
	List<com.italia.ipos.controller.Reports> reports = Collections.synchronizedList(new ArrayList<com.italia.ipos.controller.Reports>());
	
	//add on in store
	System.out.println("Add ons Print");
	for(AddOnStore an : AddOnStore.retrieve(addOn, runagain)){
		com.italia.ipos.controller.Reports rpt = new com.italia.ipos.controller.Reports();
		rpt.setF1(an.getDescription());
		rpt.setF2("N/A");
		rpt.setF3(an.getAmount()+"");
		rpt.setF4("1");
		rpt.setF5(an.getAmount()+"");
		reports.add(rpt);
		
		//System.out.println(an.getDateTrans() + " " + an.getDescription());
	}
	
	PurchasedItem item = new PurchasedItem();
	item.setIsactiveitem(1);
	
	Transactions transAgain = new Transactions();
	transAgain.setTransid(transactions.getTransid());
	transAgain.setIsvoidtrans(transactions.getIsvoidtrans());
	
	System.out.println("Regular product");
	for(PurchasedItem itm : PurchasedItem.retrieve(item, transAgain)){
		com.italia.ipos.controller.Reports rpt = new com.italia.ipos.controller.Reports();
		rpt.setF1(itm.getProductName());
		rpt.setF2(itm.getUomSymbol());
		rpt.setF3(itm.getSellingPrice()+"");
		rpt.setF4(itm.getQty()+"");
		double amount = itm.getQty() * itm.getSellingPrice().doubleValue(); 
		rpt.setF5(amount+"");
		reports.add(rpt);
		
	}
	
	printSalesInvoice(reports, transactions);
	
}

public void printChargeInvoice(Transactions transactions){
	
	List<com.italia.ipos.controller.Reports> reports = Collections.synchronizedList(new ArrayList<com.italia.ipos.controller.Reports>());
	
	PurchasedItem item = new PurchasedItem();
	item.setIsactiveitem(1);
	
	Transactions transAgain = new Transactions();
	transAgain.setTransid(transactions.getTransid());
	transAgain.setIsvoidtrans(transactions.getIsvoidtrans());
	
	System.out.println("Regular product");
	for(PurchasedItem itm : PurchasedItem.retrieve(item, transAgain)){
		com.italia.ipos.controller.Reports rpt = new com.italia.ipos.controller.Reports();
		rpt.setF1(itm.getProductName());
		rpt.setF2(itm.getUomSymbol());
		rpt.setF3(Currency.formatAmount(itm.getSellingPrice()));
		rpt.setF4(itm.getQty()+"");
		double amount = itm.getQty() * itm.getSellingPrice().doubleValue(); 
		rpt.setF5(Currency.formatAmount(amount));
		reports.add(rpt);
		
	}
	
	ProductRunning run = new ProductRunning();
	run.setIsrunactive(1);
	
	Transactions trans = new Transactions();
	trans.setIsvoidtrans(transactions.getIsvoidtrans());
	trans.setTransid(transactions.getTransid());
	
	run = ProductRunning.retrieveRecall(run, trans).get(0);
	
	AddOnStore addOn = new AddOnStore();
	addOn.setIsActive(1);
	
	ProductRunning runagain = new ProductRunning();
	runagain.setIsrunactive(1);
	runagain.setRunid(run.getRunid());
	
	
	
	//add on in store
	System.out.println("Add ons Print");
	for(AddOnStore an : AddOnStore.retrieve(addOn, runagain)){
		com.italia.ipos.controller.Reports rpt = new com.italia.ipos.controller.Reports();
		rpt.setF1(an.getDescription());
		rpt.setF2("N/A");
		rpt.setF3(Currency.formatAmount(an.getAmount()));
		rpt.setF4("1");
		rpt.setF5(Currency.formatAmount(an.getAmount()));
		reports.add(rpt);
		
		//System.out.println(an.getDateTrans() + " " + an.getDescription());
	}
	
	printChargeInvoice(reports, transactions);
	
}

private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
private static final String REPORT_PATH = ReadConfig.value(Ipos.REPORT);
private static final String REPORT_NAME_SALES = ReadXML.value(ReportTag.SALES_INVOICE);
private static final String REPORT_NAME_CHARGE = ReadXML.value(ReportTag.CHARGE_INVOICE);

private void printSalesInvoice(List<com.italia.ipos.controller.Reports> reports,Transactions transactions){
	
	ReportCompiler compiler = new ReportCompiler();
	String jrxmlFile = compiler.compileReport(REPORT_NAME_SALES, REPORT_NAME_SALES, REPORT_PATH);
	
	JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	UserDtls user = Login.getUserLogin().getUserDtls();
		HashMap param = new HashMap();
		//System.out.println("Grand total: " + transactions.getAmountpurchased());
		param.put("PARAM_RECEIPT_NO", "Receipt No: "+ transactions.getReceipts());
		param.put("PARAM_PURCHASED_DATE", "Purchased Date: "+ transactions.getTransdate());
		param.put("PARAM_BUSINESS_NAME", ReadConfig.value(Ipos.BUSINESS_NAME));
		param.put("PARAM_PREPAREDBY", "Prepared By: "+ user.getFirstname() + " " + user.getLastname());
		param.put("PARAM_DATE", "Printed: "+DateUtils.getCurrentDateMMDDYYYYTIME());
		
		param.put("PARAM_GRAND_TOTAL", Currency.formatAmount(transactions.getAmountpurchased()));
		param.put("PARAM_DISCOUNT", Currency.formatAmount(transactions.getDiscount()));
		param.put("PARAM_CASH", Currency.formatAmount(transactions.getAmountreceived()));
		param.put("PARAM_BALANCE", Currency.formatAmount(transactions.getAmountbal()));
		param.put("PARAM_CHANGE", "Php "+ Currency.formatAmount(transactions.getAmountchange()));
		
			try{
  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME_SALES +".pdf");
  		}catch(Exception e){e.printStackTrace();}
	
			try{
				System.out.println("REPORT_PATH:" + REPORT_PATH + "REPORT_NAME: " + REPORT_NAME_SALES);
	  		 File file = new File(REPORT_PATH, REPORT_NAME_SALES + ".pdf");
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
		            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME_SALES + ".pdf" + "\"");
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

private void printChargeInvoice(List<com.italia.ipos.controller.Reports> reports,Transactions transactions){
	
	
	ChargeInvoice charge = ChargeInvoice.retrieve(transactions.getTransid()+"");
	
	ReportCompiler compiler = new ReportCompiler();
	String jrxmlFile = compiler.compileReport(REPORT_NAME_CHARGE, REPORT_NAME_CHARGE, REPORT_PATH);
	
	JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	UserDtls user = Login.getUserLogin().getUserDtls();
		HashMap param = new HashMap();
		//System.out.println("Grand total: " + transactions.getAmountpurchased());
		param.put("PARAM_RECEIPT_NO", "Receipt No: "+ transactions.getReceipts());
		param.put("PARAM_PURCHASED_DATE", "Purchased Date: "+ transactions.getTransdate());
		param.put("PARAM_BUSINESS_NAME", ReadConfig.value(Ipos.BUSINESS_NAME));
		try{
		param.put("PARAM_COMPANY_ADDRESS", Skinning.property("address"));
		param.put("PARAM_COMPANY_CONTACT", Skinning.property("contactNumber"));
		param.put("PARAM_CUSTOMER", "Customer: "+transactions.getCustomer().getFullname().toUpperCase());}catch(Exception e) {}
		
		
		param.put("PARAM_PREPAREDBY", "Prepared By: "+ user.getFirstname() + " " + user.getLastname());
		param.put("PARAM_DATE", "Printed: "+DateUtils.getCurrentDateMMDDYYYYTIME());
		
		param.put("PARAM_GRAND_TOTAL", Currency.formatAmount(transactions.getAmountpurchased()));
		param.put("PARAM_DISCOUNT", Currency.formatAmount(transactions.getDiscount()));
		param.put("PARAM_CASH", Currency.formatAmount(transactions.getAmountreceived()));
		param.put("PARAM_BALANCE", Currency.formatAmount(transactions.getAmountbal()));
		param.put("PARAM_CHANGE", "Php "+ Currency.formatAmount(transactions.getAmountchange()));
		
		if(charge!=null) {
			param.put("PARAM_TERMS", "Term: "+ Terms.name(charge.getTerms()));
			param.put("PARAM_DUE", "Due: " + DateUtils.convertDateToMonthDayYear(charge.getDueDate()));
		}
		
		
		param.put("PARAM_CONDITION", Skinning.property("conditions"));
			try{
  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME_CHARGE +".pdf");
  		}catch(Exception e){e.printStackTrace();}
	
			try{
				System.out.println("REPORT_PATH:" + REPORT_PATH + "REPORT_NAME: " + REPORT_NAME_CHARGE);
	  		 File file = new File(REPORT_PATH, REPORT_NAME_CHARGE + ".pdf");
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
		            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME_CHARGE + ".pdf" + "\"");
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

public void printOrder() {
	if(getOrders()!=null && getOrders().size()>0) {
		String printerIsOn = ReadConfig.value(Ipos.PRINTER_ISON);
		//save copy of receipt in text format file
		String tmpFileName = "tmpOrderReceipt-"+DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
		ReceiptRecording.saveToFileReceipt(getReceiptView(), tmpFileName);
		  
		if("1".equalsIgnoreCase(printerIsOn)){//active
			cashdrawerOpen();
			printReceipt(tmpFileName); //send to printer
			cuttReceiptPaper();
		}
		
	}
}

public boolean isValidatedRecalled() {
	return validatedRecalled;
}
public void setValidatedRecalled(boolean validatedRecalled) {
	this.validatedRecalled = validatedRecalled;
}
public boolean getLockDispense() {
	if(getOrders()!=null && getOrders().size()>0){
		lockDispense = false;
	}else{
		lockDispense = true;
	}
	return lockDispense;
}
public void setLockDispense(boolean lockDispense) {
	this.lockDispense = lockDispense;
}
public UserDtls getLoginUser() {
	return loginUser;
}
public void setLoginUser(UserDtls loginUser) {
	this.loginUser = loginUser;
}
public boolean isOnLoadDemand() {
	return onLoadDemand;
}
public void setOnLoadDemand(boolean onLoadDemand) {
	this.onLoadDemand = onLoadDemand;
}

public double getTotalNonTaxable() {
	return totalNonTaxable;
}
public void setTotalNonTaxable(double totalNonTaxable) {
	this.totalNonTaxable = totalNonTaxable;
}
public double getTotalTaxable() {
	return totalTaxable;
}
public void setTotalTaxable(double totalTaxable) {
	this.totalTaxable = totalTaxable;
}
public double getTotalVat() {
	return totalVat;
}
public void setTotalVat(double totalVat) {
	this.totalVat = totalVat;
}
public List getTerms() {
	terms = new ArrayList<Terms>();
	for(Terms term : Terms.loadTerms()) {
		terms.add(new SelectItem(term.getId(), term.getName()));
	}
	return terms;
}
public void setTerms(List terms) {
	this.terms = terms;
}
public int getTermId() {
	if(termId==0) {
		termId = 1;
	}
	return termId;
}
public void setTermId(int termId) {
	this.termId = termId;
}
public Date getDueDate() {
	if(dueDate==null) {
		dueDate = DateUtils.getDateToday();
	}
	return dueDate;
}
public void setDueDate(Date dueDate) {
	this.dueDate = dueDate;
}
public boolean isEnableCharge() {
	return enableCharge;
}
public void setEnableCharge(boolean enableCharge) {
	this.enableCharge = enableCharge;
}
public double getTotalpoints() {
	return totalpoints;
}
public void setTotalpoints(double totalpoints) {
	this.totalpoints = totalpoints;
}
public double getPointsredeem() {
	return pointsredeem;
}
public void setPointsredeem(double pointsredeem) {
	this.pointsredeem = pointsredeem;
}
public double getRemainingpoints() {
	return remainingpoints;
}

public void setRemainingpoints(double remainingpoints) {
	this.remainingpoints = remainingpoints;
}

public static void main(String[] args) {
	
	Double amnt = new Double("-3");
	
	amnt = 20 + amnt;
	
	System.out.println("amount : " + amnt);
	
}
	
}
