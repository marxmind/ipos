package com.italia.ipos.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.italia.ipos.controller.Login;
import com.italia.ipos.enm.UserAccess;


/**
 * 
 * @author mark italia
 * @since 09/29/2016
 *@version 1.0
 */
@ManagedBean(name="menuBean")
@ViewScoped
public class MenuBean {

	private static final long serialVersionUID = 1098801825228384363L;
	
	@ManagedProperty("#{productBean}")
	private ProductBean products;
	
	private boolean accounting;
	
	private boolean clientsOverCounter;
	private boolean clientsOverCounterTabPayments;
	private boolean clientsOverCounterTabProductReceipts;
	private boolean clientsOverCounterTabRentedItems;
	private boolean clientsOverCounterTabCollectible;
	
	private boolean supplierTrans;
	
	private boolean deliveryTrans;
	private boolean deliverySupplier;
	private boolean deliveryProduct;
	private boolean deliveryProductTrans;
	private boolean deliverySold;
	private boolean deliveryReturn;
	private boolean deliveryProductRented;
	
	private boolean xtras;
	private boolean cashier;
	private boolean stats;
	private boolean storeloader;
	private boolean expireMonitor;
	private boolean tableMonitor;
	private boolean rooms;
	private boolean receiptsTrans;
	private boolean receiptReset;
	private boolean settings;
	private boolean userSetting;
	private boolean productSetting;
	private boolean supplierSetting;
	private boolean customerSetting;
	private boolean points;
	
	
	
	@PostConstruct
	public void init() {
		Login in = Login.getUserLogin();
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()) {
			accounting = true;
			clientsOverCounter = true;
			clientsOverCounterTabPayments = true;
			clientsOverCounterTabProductReceipts = true;
			clientsOverCounterTabRentedItems = true;
			clientsOverCounterTabCollectible = true;
			supplierTrans = true;
			deliveryTrans = true;
			xtras = true;
			cashier = true;
			stats = true;
			storeloader = true;
			expireMonitor = true;
			tableMonitor = true;
			rooms = true;
			receiptsTrans = true;
			receiptReset = true;
			settings = true;
			userSetting = true;
			productSetting = true;
			supplierSetting = true;
			customerSetting = true;
			deliverySupplier = true;
			deliveryProduct  = true;
			deliveryProductTrans = true;
			deliverySold = true;
			deliveryReturn = true;
			deliveryProductRented = true;
			points = true;
		}else {
			accounting = true;
			clientsOverCounter = true;
			clientsOverCounterTabPayments = true;
			clientsOverCounterTabProductReceipts = true;
			clientsOverCounterTabRentedItems = false;
			clientsOverCounterTabCollectible = false;
			supplierTrans = true;
			deliveryTrans = true;
			xtras = true;
			cashier = true;
			stats = false;
			storeloader = true;
			expireMonitor = false;
			tableMonitor = false;
			rooms = false;
			receiptsTrans = false;
			receiptReset = false;
			settings = false;
			userSetting = true;
			productSetting = true;
			supplierSetting = true;
			customerSetting = true;
			deliverySupplier = true;
			deliveryProduct  = false;
			deliveryProductTrans = false;
			deliverySold = false;
			deliveryReturn = false;
			deliveryProductRented = false;
			points = false;
		}
	}
	
	
	public void setProducts(ProductBean products){
		this.products = products;
	}
	
	public void printAll(){
		
	}
	
	public String adminpoints() {
		return "points";
	}
	
	public String pos(){
		
		//load product imagaes
		/**
		 * remove temporary
		 * slowness on loading
		 */
		//products.loadProduct();
		
		//return "pos";
		//if(Features.isEnabled(com.italia.ipos.enm.Features.GROCERRY_CASHIER)){
			return "cashier";
		//}else{
		//	return "feature";
		//}
	}
	
	public String store(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.STORE_TRANSFER)){
			return "store";
		/*}else{
			return "feature";
		}*/
	}
	
	public String adminuser(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_USER)){
			return "adminuser";
		/*}else{
			return "feature";
		}*/
	}
	
	public String adminproduct(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_PRODUCT)){
			return "adminproduct";
		/*}else{
			return "feature";
		}*/
	}
	
	public String adminsupplier(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_SUPPLIER)){
			return "adminsupplier";
		/*}else{
			return "feature";
		}*/
	}
	
	public String admincustomer(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_CLIENT)){
			return "admincustomer";
		/*}else{
			return "feature";
		}*/
	}
	
	public String adminuom(){
		return "adminuom";
	}
	
	public String invorder(){
		return "invorder";
	}
	
	public String invreturn(){
		return "invreturn";
	}
	
	public String invforecast(){
		return "invforecast";
	}
	
	public String invadjustment(){
		return "invadjustment";
	}
	
	public String incomes(){
		return "incomes";
	}
	
	public String expenses(){
		return "expenses";
	}
	
	public String suppliers(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.SUPPLIERS)){
			return "suppliers";
		/*}else{
			return "feature";
		}*/
	}
	
	public String clients(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.CLIENTS)){
			return "clients";
		/*}else{
			return "feature";
		}*/
	}
	
	public String receivable(){
		return "receivable";
	}
	
	public String payable(){
		return "payable";
	}

	public String accounting(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING)){
			return "accounting";
		/*}else{
			return "feature";
		}*/
	}
	public String inventory(){
		return "inventory";
	}
	
	public String delivery(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.DELIEVRY)){
			return "delivery";
		/*}else{
			return "feature";
		}*/
	}
	
	public String xtras(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.XTRAS)){
			return "xtras";
		/*}else{
			return "feature";
		}*/
	}
	public String monitoring(){
		//if(Features.isEnabled(com.italia.ipos.enm.Features.STATISTICS)){
			return "productdeliverymonitoring";
		/*}else{
			return "feature";
		}*/
	}
	public String delReceiptRecording(){
		return "delreceipts";
	}
	
	public String room(){
		return "roommain";
	}
	
	
	public String tableMonitoring(){
		return "tableMonitoring";
	}
	
	public String productExpiration(){
		return "productExpiration";
	}
	
	public String rptTrans(){
		return "trans";
	}
	
	public String rpt(){
		return "rpt";
	}
	
	public String features(){
		return "features";
	}

	public boolean isAccounting() {
		return accounting;
	}

	public void setAccounting(boolean accounting) {
		this.accounting = accounting;
	}

	public boolean isClientsOverCounter() {
		return clientsOverCounter;
	}

	public void setClientsOverCounter(boolean clientsOverCounter) {
		this.clientsOverCounter = clientsOverCounter;
	}

	public boolean isSupplierTrans() {
		return supplierTrans;
	}

	public void setSupplierTrans(boolean supplierTrans) {
		this.supplierTrans = supplierTrans;
	}

	public boolean isDeliveryTrans() {
		return deliveryTrans;
	}

	public void setDeliveryTrans(boolean deliveryTrans) {
		this.deliveryTrans = deliveryTrans;
	}

	public boolean isXtras() {
		return xtras;
	}

	public void setXtras(boolean xtras) {
		this.xtras = xtras;
	}

	public boolean isCashier() {
		return cashier;
	}

	public void setCashier(boolean cashier) {
		this.cashier = cashier;
	}

	public boolean isStats() {
		return stats;
	}

	public void setStats(boolean stats) {
		this.stats = stats;
	}

	public boolean isStoreloader() {
		return storeloader;
	}

	public void setStoreloader(boolean storeloader) {
		this.storeloader = storeloader;
	}

	public boolean isExpireMonitor() {
		return expireMonitor;
	}

	public void setExpireMonitor(boolean expireMonitor) {
		this.expireMonitor = expireMonitor;
	}

	public boolean isTableMonitor() {
		return tableMonitor;
	}

	public void setTableMonitor(boolean tableMonitor) {
		this.tableMonitor = tableMonitor;
	}

	public boolean isRooms() {
		return rooms;
	}

	public void setRooms(boolean rooms) {
		this.rooms = rooms;
	}

	public boolean isReceiptsTrans() {
		return receiptsTrans;
	}

	public void setReceiptsTrans(boolean receiptsTrans) {
		this.receiptsTrans = receiptsTrans;
	}

	public boolean isReceiptReset() {
		return receiptReset;
	}

	public void setReceiptReset(boolean receiptReset) {
		this.receiptReset = receiptReset;
	}

	public boolean isSettings() {
		return settings;
	}

	public void setSettings(boolean settings) {
		this.settings = settings;
	}

	public boolean isUserSetting() {
		return userSetting;
	}

	public void setUserSetting(boolean userSetting) {
		this.userSetting = userSetting;
	}

	public boolean isProductSetting() {
		return productSetting;
	}

	public void setProductSetting(boolean productSetting) {
		this.productSetting = productSetting;
	}

	public boolean isSupplierSetting() {
		return supplierSetting;
	}

	public void setSupplierSetting(boolean supplierSetting) {
		this.supplierSetting = supplierSetting;
	}

	public boolean isCustomerSetting() {
		return customerSetting;
	}

	public void setCustomerSetting(boolean customerSetting) {
		this.customerSetting = customerSetting;
	}

	public ProductBean getProducts() {
		return products;
	}


	public boolean isClientsOverCounterTabPayments() {
		return clientsOverCounterTabPayments;
	}


	public void setClientsOverCounterTabPayments(boolean clientsOverCounterTabPayments) {
		this.clientsOverCounterTabPayments = clientsOverCounterTabPayments;
	}


	public boolean isClientsOverCounterTabProductReceipts() {
		return clientsOverCounterTabProductReceipts;
	}


	public void setClientsOverCounterTabProductReceipts(boolean clientsOverCounterTabProductReceipts) {
		this.clientsOverCounterTabProductReceipts = clientsOverCounterTabProductReceipts;
	}


	public boolean isClientsOverCounterTabRentedItems() {
		return clientsOverCounterTabRentedItems;
	}


	public void setClientsOverCounterTabRentedItems(boolean clientsOverCounterTabRentedItems) {
		this.clientsOverCounterTabRentedItems = clientsOverCounterTabRentedItems;
	}


	public boolean isClientsOverCounterTabCollectible() {
		return clientsOverCounterTabCollectible;
	}


	public void setClientsOverCounterTabCollectible(boolean clientsOverCounterTabCollectible) {
		this.clientsOverCounterTabCollectible = clientsOverCounterTabCollectible;
	}


	public boolean isDeliverySupplier() {
		return deliverySupplier;
	}


	public void setDeliverySupplier(boolean deliverySupplier) {
		this.deliverySupplier = deliverySupplier;
	}


	public boolean isDeliveryProduct() {
		return deliveryProduct;
	}


	public void setDeliveryProduct(boolean deliveryProduct) {
		this.deliveryProduct = deliveryProduct;
	}


	public boolean isDeliveryProductTrans() {
		return deliveryProductTrans;
	}


	public void setDeliveryProductTrans(boolean deliveryProductTrans) {
		this.deliveryProductTrans = deliveryProductTrans;
	}


	public boolean isDeliverySold() {
		return deliverySold;
	}


	public void setDeliverySold(boolean deliverySold) {
		this.deliverySold = deliverySold;
	}


	public boolean isDeliveryReturn() {
		return deliveryReturn;
	}


	public void setDeliveryReturn(boolean deliveryReturn) {
		this.deliveryReturn = deliveryReturn;
	}


	public boolean isDeliveryProductRented() {
		return deliveryProductRented;
	}


	public void setDeliveryProductRented(boolean deliveryProductRented) {
		this.deliveryProductRented = deliveryProductRented;
	}


	public boolean isPoints() {
		return points;
	}


	public void setPoints(boolean points) {
		this.points = points;
	}
	
}
