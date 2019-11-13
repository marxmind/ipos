package com.italia.ipos.bean;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.ipos.application.ClientInfo;
import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.CustomerPayment;
import com.italia.ipos.controller.CustomerPaymentTrans;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.InputedInventoryQtyTracker;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.MoneyIO;
import com.italia.ipos.controller.Payment;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductInventory;
import com.italia.ipos.controller.ProductPricingTrans;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.ProductRunning;
import com.italia.ipos.controller.PurchasedItem;
import com.italia.ipos.controller.QtyRunning;
import com.italia.ipos.controller.Transactions;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.MoneyStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.ProductStatus;
import com.italia.ipos.enm.ReceiptStatus;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.utils.Currency;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;
import com.italia.ipos.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 09/29/2016
 * @version 1.0
 */
@ManagedBean(name="posBean")
@ViewScoped
public class PosBean implements Serializable {

	private static final long serialVersionUID = 1094801825228384363L;
	
	private String searchCode;
	private String searchProduct;
	private List<Product> prods = Collections.synchronizedList(new ArrayList<Product>());
	private String qtyToCart;
	private String qtyToCartTmp;
	private Map<Long, PurchasedItem> productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
	private List<PurchasedItem> orders = Collections.synchronizedList(new ArrayList<PurchasedItem>());
	private List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());
	private String grandTotalPrice;
	private double itemCount;
	private String receiptView;
	private String keyPress;
	private String amountin;
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
	
	@ManagedProperty("#{productBean}")
	private ProductBean products;
	
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
	
	public void updateCustomer(Customer customer){
		if(Login.getUserLogin().checkUserStatus()){
			setCustomername(customer.getFullname());
			setCustomer(customer);
		}
	}
	
	public void initCustomer(){
		customers = Collections.synchronizedList(new ArrayList<Customer>());
		Customer customer = new Customer();
		customer.setIsactive(1);
		customer.setFullname(Whitelist.remove(getSearchCustomer()));
		customers = Customer.retrieve(customer);
	}
	
	public void qtyCheck(){
		try{
		Double.valueOf(getQtyToCartTmp());
		setQtyToCart(getQtyToCartTmp());
		}catch(NullPointerException n){
		}catch(NumberFormatException e){}
	}
	
	public String addToCart(Product prop){
		//System.out.println("Property Id " + prop.getPropid() + " name " + prop.getProductname() + " qty " + getQtyToCart());
		boolean isQty=false;
		
		if(getQtyToCart()!=null && !getQtyToCart().isEmpty()){
			isQty=true;
		}
		
		if(Login.getUserLogin().checkUserStatus() && isQty){
			PurchasedItem item = new PurchasedItem();
			
			//save temporary transactions to this table
			ProductRunning productrun = new ProductRunning();
			if(getProductRunning()!=null){
				productrun = getProductRunning();
			}else{
				productrun.setRundate(DateUtils.getCurrentDateYYYYMMDD());
				productrun.setIsrunactive(1);
				productrun.setClientip(ClientInfo.getClientIP());
				productrun.setClientbrowser(ClientInfo.getBrowserName());
				productrun.setRunstatus(ProductStatus.ON_QUEUE.getId());
				productrun.setRunremarks(ProductStatus.ON_QUEUE.getName());
				productrun.setUserDtls(Login.getUserLogin().getUserDtls());
				productrun = ProductRunning.save(productrun);
				setProductRunning(productrun);
			}
			
			/*QtyRunning qtyrun = new QtyRunning();
			if(getQtyrunning()!=null && getQtyrunning().size()>0){
				qtyrun = getQtyrunning().get(id);
			}*/
			
			if(getProductPurchasedData()!=null){
				
				
				long id = prop.getProdid();
				if(productPurchasedData.containsKey(id)){
					PurchasedItem itm = productPurchasedData.get(id);
					Double newQty = Double.valueOf(getQtyToCart());
					Double qty = itm.getQty();
					qty = qty + newQty;
					
					String productName = prop.getProductProperties().getProductname();
					BigDecimal sellingPrice = prop.getProductPricingTrans().getSellingprice();
					Double price = Double.valueOf(sellingPrice+"");
					Double totalPrice = qty * price;
					item.setProductId(id);
					item.setProductName(productName);
					item.setUomSymbol(prop.getProductProperties().getUom().getSymbol());
					item.setSellingPrice(sellingPrice);
					item.setTotalPrice(totalPrice);
					item.setQty(qty);
					item.setProductProperties(prop.getProductProperties());
					item.setProduct(prop);
					productPurchasedData.remove(id);
					productPurchasedData.put(id, item);
					
					//modify inventory
					ProductInventory.invtoryqty(false, prop, newQty);
					
					System.out.println("dito meron id : " + id + " product run " + productrun.getRunid() + " product " + prop.getProdid() + " qty " + qty);
					//addQtyRunning(id, productrun, prop, qty);
					Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunning(getQtyrunning(),id, productrun, prop, qty);
					setQtyrunning(qtyrunning);
				}else{
					
					String productName = prop.getProductProperties().getProductname();
					BigDecimal sellingPrice = prop.getProductPricingTrans().getSellingprice();
					Double newQty = Double.valueOf(getQtyToCart());
					Double price = Double.valueOf(sellingPrice+"");
					Double totalPrice = newQty * price;
					item.setProductId(prop.getProdid());
					item.setProductName(productName);
					item.setUomSymbol(prop.getProductProperties().getUom().getSymbol());
					item.setSellingPrice(sellingPrice);
					item.setTotalPrice(totalPrice);
					item.setQty(Double.valueOf(getQtyToCart()));
					item.setProductProperties(prop.getProductProperties());
					item.setProduct(prop);
					productPurchasedData.put(prop.getProdid(), item);
					
					//modify inventory
					ProductInventory.invtoryqty(false, prop, newQty);
					
					System.out.println("else meron id : " + id + " product run " + productrun.getRunid() + " product " + prop.getProdid() + " qty " + newQty);
					//addQtyRunning(prop.getProdid(), productrun, prop, newQty);
					Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunning(getQtyrunning(),prop.getProdid(), productrun, prop, newQty);
					setQtyrunning(qtyrunning);
				}
				
				
			}else{
				
				productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
				
				String productName = prop.getProductProperties().getProductname();
				BigDecimal sellingPrice = prop.getProductPricingTrans().getSellingprice();
				Double newQty = Double.valueOf(getQtyToCart());
				Double price = Double.valueOf(sellingPrice+"");
				Double totalPrice = newQty * price;
				item.setProductId(prop.getProdid());
				item.setProductName(productName);
				item.setUomSymbol(prop.getProductProperties().getUom().getSymbol());
				item.setSellingPrice(sellingPrice);
				item.setTotalPrice(totalPrice);
				item.setQty(Double.valueOf(getQtyToCart()));
				item.setProductProperties(prop.getProductProperties());
				item.setProduct(prop);
				productPurchasedData.put(prop.getProdid(), item);
				
				//modify inventory
				ProductInventory.invtoryqty(false,prop, newQty);
				
				System.out.println("dito else wala id : " + prop.getProdid() + " product run " + productrun.getRunid() + " product " + prop.getProdid() + " qty " + newQty);
				//addQtyRunning(prop.getProdid(), productrun, prop, newQty);
				Map<Long, QtyRunning> qtyrunning = QtyRunning.addQtyRunning(getQtyrunning(),prop.getProdid(), productrun, prop, newQty);
				setQtyrunning(qtyrunning);
			}
			initOrders();
			addMessage("Product added successfully.","");
			}
		
		setQtyToCart(null);
		setQtyToCartTmp(null);
		
		return "";
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
			long id = item.getProduct().getProdid();//item.getProductId();
			productPurchasedData.remove(id);
			orders.remove(item);
			voidIndividual(item);
			setGrandTotalPrice("0.00");
			initOrders();
			addMessage("The product "+ item.getProductName() +" has been successfully deleted.","");
		}
	}
	
	private void voidIndividual(PurchasedItem item){
		
		QtyRunning qtyrun = getQtyrunning().get(item.getProduct().getProdid());
		
		//return qty in inventory table
		ProductInventory.invtoryqty(true, qtyrun.getProduct(), qtyrun.getQtyhold());
		
		//modify Qtyrunning table
		qtyrun.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
		qtyrun.setQtystatus(ProductStatus.VOID.getId());
		qtyrun.setQtyremarks(ProductStatus.VOID.getName());
		qtyrun.setUserDtls(Login.getUserLogin().getUserDtls());
		qtyrun.save();
	}
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public void initOrders(){
		setReceiptView(null);
		orders = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		double totalprice =0;
		itemCount = 0;
		int count = 1;
		StringBuffer str = new StringBuffer();
		str.append("Receipt No: " + DeliveryItemReceipt.generateNewReceiptNo()+"\n");
		List<String> tmpData=new ArrayList<>();
		if(productPurchasedData.size()>0){
			for(PurchasedItem item : productPurchasedData.values()){
				item.setCount(count++);
				totalprice += item.getTotalPrice();
				str.append(item.getProductName() + "   " + item.getQty() + "  " + item.getTotalPrice() + "\n");
				orders.add(item);
				itemCount += item.getQty();
			}
			setPurchasedPrice(totalprice);
			if(getDiscountAmount()==null || getDiscountAmount().isEmpty()){
				setDiscountAmount("0");
			}
			double discount = Double.valueOf(getDiscountAmount().replace(",", ""));
			double payment = totalprice - discount;
			setGrandTotalPrice(Currency.formatAmount(payment));
			str.append("---------------------------------------------\n");
			str.append("\tDiscount:\t"+Currency.formatAmount(discount) + "\n");
			str.append("\tAmount Due:\t"+Currency.formatAmount(payment) + "\n");
			
			
			setReceiptView(str.toString());
		}
		
	}
	
	/*public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }*/
	
	public List<Product> getProds() {
		return prods;
	}
	
	
	
	public void initProduct(){
		prods = Collections.synchronizedList(new ArrayList<Product>());
		
		/*ProductProperties prod = new ProductProperties();
		prod.setIsactive(1);
		if(getSearchProduct()!=null){
			prod.setProductname(getSearchProduct());
		}
		
		prods = products.productList(prod);*/
		
		if(getSearchProduct()!=null && !getSearchProduct().isEmpty()){
			
			/**
			 * this code was changed with below codes
			 */
			/*Product prod = new Product();
			prod.setIsactiveproduct(1);
			
			ProductProperties prop = new ProductProperties();
			prop.setProductname(getSearchProduct());
			prop.setIsactive(1);
			
			
			prods = products.productList(prod,prop);*/
			int len = getSearchProduct().length();
			if(len>=4){
				String sql = " AND inv.newqty!=0 AND prop.isactive=1 AND prop.productname like '%"+ getSearchProduct().replace("--", "") +"%'";
				String[] params = new String[0]; 
				prods = products.loadProduct(sql, params);
				Collections.reverse(prods);
			}
		}
		
		
		
	}
	
	public void printAll(){
		
	}
	 
	public void search(){
		System.out.println("search is active");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Search found", "Search code"));
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
		if(receiptView==null){
			receiptView = "Receipt No: " + DeliveryItemReceipt.generateNewReceiptNo();
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
	
	public void keyButton(){
		
		
		if(getSearchCode()!=null && !getSearchCode().isEmpty()){
			System.out.println("searching  code..... " + getSearchCode());
			searchBarcode();
		}else if(getAmountin()!=null && !getAmountin().isEmpty()){
			System.out.println(" amoun in " + getAmountin());
			
			if(getOrders()!=null && getOrders().size()>0){
			
			String str = getReceiptView();
			
				if(str!=null && !str.isEmpty()){
					str += "\t\tCash :\t" + getAmountin() + "\n";
					str += "---------------------------------------------\n";
					Double changeamnt = Double.valueOf(Currency.removeComma(getAmountin())) - Double.valueOf(Currency.removeComma(getGrandTotalPrice()));
					if(changeamnt>0){
						str +="\t\tChange :\t" + changeamnt;
						setChangeamnt(new BigDecimal(changeamnt+""));
						setBalanceamnt(new BigDecimal("0.00"));
					}else{
						str +="\t\tChange :\t0.00\n";
						str +="\t\tBalance :\t" +  Math.abs(changeamnt) ;
						setChangeamnt(new BigDecimal("0.00"));
						setBalanceamnt(new BigDecimal(changeamnt+""));
					}
					setReceiptView(str);
					taxInfo();
					posDeveloper();
					returnPolicy();
					}
			}else{
				setAmountin(null);
				addMessage("Please select item/s first before inputing an amount.","");
			}
			
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
	
	public void transactiondVoid(){
		if(getProductRunning()!=null){
			ProductRunning run = getProductRunning();
			run.setIsrunactive(1);
			run.setRundate(DateUtils.getCurrentDateYYYYMMDD());
			run.setRunstatus(ProductStatus.VOID.getId());
			run.setRunremarks(ProductStatus.VOID.getName());
			run.save();
			setProductRunning(null);
		}
		
		if(getQtyrunning()!=null && getQtyrunning().size()>0){
			for(QtyRunning qtyrun : getQtyrunning().values()){
				
				//return qty in inventory table
				ProductInventory.invtoryqty(true, qtyrun.getProduct(), qtyrun.getQtyhold());
				
				//modify Qtyrunning table
				qtyrun.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
				qtyrun.setQtystatus(ProductStatus.VOID.getId());
				qtyrun.setQtyremarks(ProductStatus.VOID.getName());
				qtyrun.setUserDtls(Login.getUserLogin().getUserDtls());
				qtyrun.save();
			}
			setQtyrunning(Collections.synchronizedMap(new HashMap<Long,QtyRunning>()));
		}
		
	}
	
	public void dispense(){
		if(Login.checkUserStatus()){
			
			if(getOrders()!=null && getOrders().size()>0){
			
				if(getAmountin()!=null && !getAmountin().isEmpty()){
					
					//save customer info
					Customer customer = new Customer();
					customer = updateCustomer();
					
					//save customer transactions
					Transactions trans = new Transactions();
					trans = addTransactions(customer);
					 
					//save customer purchased items
					List<PurchasedItem> items = addPurchasedItem(trans);
					
					
					//save receipt
					saveToReceipt(trans);
					
					//save to IOMoney
					saveMoneyIO(trans);
					
					//update product item purchased status
					purchasedItemStatus(trans);
					
					
					//track dispense quantity
					if(items.size()>0){
						trackQuantityDispense(items);
					}
					//record customer amount purchased
					//if(getBalanceamnt().doubleValue()!=0){
						//Payment.customerPayment(customer, getBalanceamnt().doubleValue(), Double.valueOf(getAmountin().replace(",", "")), PaymentTransactionType.GROCERY, "Partial payment. Paid by "+ customer.getFullname() + ". Payable amount " + getBalanceamnt());
					//}else{
						//Payment.customerPayment(customer, getBalanceamnt().doubleValue(), Double.valueOf(getAmountin().replace(",", "")), PaymentTransactionType.GROCERY, "Full payment. Paid by "+ customer.getFullname());
					//}
					
					//refresh view
					setCustomername("UNKNOWN");
					setCustomer(null);
					setAmountin(null);
					setBalanceamnt(null);
					setReceiptView(null);
					setGrandTotalPrice(null	);
					setDiscountAmount(null);
					setItemCount(0);
					orders = Collections.synchronizedList(new ArrayList<PurchasedItem>());
					productPurchasedData = Collections.synchronizedMap(new HashMap<Long,PurchasedItem>());
					addMessage("Product dispense successfully.","");
				}else{
					addMessage("Please input amount.","");
				}
			}else{
				addMessage("Please select item/s first before dispensing an item.","");
			}
		}
	}
	
	private void trackQuantityDispense(List<PurchasedItem> items){
		for(PurchasedItem item : items){
			Product prod = new Product();
			prod.setIsactiveproduct(1);
			prod.setProdid(item.getProduct().getProdid());
			ProductInventory inv = ProductInventory.retrieve(prod).get(0);
			inv.setAddqty(-item.getQty());
			inv.setUserDtls(Login.getUserLogin().getUserDtls());
			InputedInventoryQtyTracker.saveQty(inv,"SOLD PRODUCT IN STORE");
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
	
	private void saveToReceipt(Transactions trans){
		DeliveryItemReceipt rec = new DeliveryItemReceipt();
		rec.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		rec.setStatus(ReceiptStatus.POSTED.getId());
		rec.setIsActive(1);
		rec.setReceiptNo(DeliveryItemReceipt.generateNewReceiptNo());//generate new receipt number
		
		rec.setDeliveryChargeAmount(0);
		rec.setRemarks("no remarks");
		
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
		
		rec.setUserDtls(Login.getUserLogin().getUserDtls());
		
		rec.save();
		
	}
	
	private void saveMoneyIO(Transactions trans){
		
		if(trans.getAmountbal().doubleValue()==0){
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			io.setDescripion("Full payment in Store. Paid by " + trans.getCustomer().getFullname());
			io.setTransType(MoneyStatus.INCOME.getId());
			io.setInAmount(trans.getAmountreceived().doubleValue());
			io.setUserDtls(Login.getUserLogin().getUserDtls());
			io.setCustomer(trans.getCustomer());
			io.setReceiptNo(trans.getReceipts());
			MoneyIO.save(io);
			
			Payment.customerPayment(trans.getCustomer(), trans.getAmountbal().doubleValue(), Double.valueOf(getAmountin().replace(",", "")), PaymentTransactionType.STORE, "Full payment. Paid by "+ trans.getCustomer().getFullname(), trans.getReceipts());
			
		}else{
			
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			io.setTransType(MoneyStatus.INCOME.getId());
			io.setUserDtls(Login.getUserLogin().getUserDtls());
			
			//saving information to customer payment
			if(trans.getAmountreceived().doubleValue()==0){
				Payment.customerPayment(trans.getCustomer(),  trans.getAmountbal().doubleValue(), trans.getAmountreceived().doubleValue(),PaymentTransactionType.STORE, "No downpayment in Store. Payable amount " + trans.getAmountbal().doubleValue(),trans.getReceipts());
				io.setDescripion("No downpayment in delivery. Payable by " + trans.getCustomer().getFullname() + ". Payable amount " + trans.getAmountbal().doubleValue());
				io.setInAmount(0.00);
				io.setReceiptNo(trans.getReceipts());
				io.setCustomer(trans.getCustomer());
				MoneyIO.save(io);
			}else{
				Payment.customerPayment(trans.getCustomer(), trans.getAmountbal().doubleValue(), trans.getAmountreceived().doubleValue(),PaymentTransactionType.STORE, "Partial downpayment in Store. Payable amount " + trans.getAmountbal().doubleValue(),trans.getReceipts());
				io.setDescripion("Partial downpayment in delivery. Paid by " + trans.getCustomer().getFullname() + ". Payable amount " + trans.getAmountbal().doubleValue());
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
		trans.setUserDtls(Login.getUserLogin().getUserDtls());
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
			payment.setUserDtls(Login.getUserLogin().getUserDtls());
			payment = CustomerPayment.save(payment);
			
			
			CustomerPaymentTrans trans = new CustomerPaymentTrans();
			trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
			trans.setAmountpay(new BigDecimal(Currency.removeComma(getAmountin())));
			trans.setIspaid(1);
			trans.setPaytransisactive(1);
			trans.setCustomerPayment(payment);
			trans.setUserDtls(Login.getUserLogin().getUserDtls());
			trans.save();
			
		}
		
	}
	
	private Customer updateCustomer(){
		Customer customer = new Customer();
		if(getCustomer()!=null){
			customer = getCustomer();
		}else{	
			customer = Customer.addUnknownCustomer();
			customer = Customer.save(customer);
		}
		return customer;
	}
	
	private Transactions addTransactions(Customer customer){
		Transactions trans = new Transactions();
		trans.setTransdate(DateUtils.getCurrentDateYYYYMMDD());
		trans.setCustomer(customer);
		trans.setAmountpurchased(new BigDecimal(getPurchasedPrice()));
		trans.setAmountreceived(new BigDecimal(Currency.removeComma(getAmountin())));
		trans.setAmountchange(getChangeamnt());
		trans.setAmountbal(new BigDecimal(Math.abs(getBalanceamnt().doubleValue())));
		trans.setIsvoidtrans(1);
		trans.setUserDtls(Login.getUserLogin().getUserDtls());
		
		trans.setReceipts(DeliveryItemReceipt.generateNewReceiptNo());
		
		BigDecimal amnt = new BigDecimal("0"); 
		try{amnt = new BigDecimal(getDiscountAmount().replace(",", ""));}catch(NumberFormatException num){}
		trans.setDiscount(amnt);
		
		//vat
		trans.setVatsales(amnt);
		trans.setVatexmptsales(amnt);
		trans.setZeroratedsales(amnt);
		trans.setVatnet(amnt);
		trans.setVatamnt(amnt);
		
		trans = Transactions.save(trans);
		return trans;
	}
	
	private List<PurchasedItem> addPurchasedItem(Transactions trans){
		List<PurchasedItem> items = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		
		if(orders.size()>0){
			
			for(PurchasedItem item : orders){
				
				ProductProperties prod = item.getProductProperties();
				item.setDatesold(DateUtils.getCurrentDateYYYYMMDD());
				item.setProductName(prod.getProductname());
				item.setUomSymbol(prod.getUom().getSymbol());
				item.setProductbrand(prod.getProductBrand().getProductbrandname());
				//qty already defined in orders
				
				//this data to be use for sales and income
				ProductPricingTrans price = ProductPricingTrans.retrievePrice(item.getProduct().getProdid()+"");
				item.setPurchasedprice(price.getPurchasedprice());
				item.setSellingPrice(price.getSellingprice());
				item.setNetprice(price.getNetprice());
				item.setTaxpercentage(price.getTaxpercentage());
				
				item.setIsactiveitem(1);
				//Product already defined in orders
				item.setTransactions(trans);
				item.setUserDtls(Login.getUserLogin().getUserDtls());
				
				item = PurchasedItem.save(item);
				items.add(item);
			}
			
		}
		
		return items;
	}
	
	private void searchBarcode(){
		Product prod = new Product();
		prod.setBarcode(Whitelist.remove(getSearchCode()));
		prod.setIsactiveproduct(1);
		
		ProductProperties prop = new ProductProperties();
		prop.setIsactive(1);
		setQtyToCart("1");
		
		/**
		 * 
		 */
		//addToCart(products.productList(prod,prop).get(0));
		System.out.println("Barcode : " + getSearchCode());
		String sql = " AND inv.newqty!=0 AND inv.isactive=1  AND prd.barcode=? AND prd.isactiveproduct=1";
		String[] params = new String[1];
		params[0] = Whitelist.remove(getSearchCode());
		addToCart(products.loadProduct(sql, params).get(0));
		
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
		String str = getReceiptView();
		str += "\n\n";
		str +="POS Developer\n";
		str +="Poblacion Lake Sebu, So. Cot. 9512\n";
		str +="TIN 273-982-834-000\n";
		str +="THIS INVOICE/RECEIPT SHALL BE VALID FOR\n";
		str +="(5) YEARS FROM THE DATE OF THE PERMIT TO USE.\n";
		str += "\n\n";
		str +="THIS SERVES AS YOUR OFFICIAL RECEIPT\n";
		str +="KEEP THIS FOR FUTURE USE\n";
		setReceiptView(str);
	}
	
	public void returnPolicy(){
		String str = getReceiptView();
		str += "\n\n";
		
		str +="Defective returns will be evaluated in accordance with DTI regulations.\n";
		str += "\n\n";
		str += "Returns without receipt will require verification and charged with a 10% fee.\n";
		str += "\n\n";
		str += "THANK YOU FOR YOUR SHOPPING\n";
		
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
	
public String getAmountin() {
	return amountin;
}
public void setAmountin(String amountin) {
	this.amountin = amountin;
}
public String getCashiername() {
	cashiername = Login.getUserLogin().getUserDtls().getFirstname() + " " + Login.getUserLogin().getUserDtls().getLastname(); 
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
public static void main(String[] args) {
	
	Double amnt = new Double("-3");
	
	amnt = 20 + amnt;
	
	System.out.println("amount : " + amnt);
	
}
	
}
