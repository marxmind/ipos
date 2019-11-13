package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.TabChangeEvent;

import com.italia.ipos.controller.Features;
import com.italia.ipos.controller.Login;
import com.italia.ipos.enm.UserAccess;

/**
 * 
 * @author mark italia
 * @since 05/25/2017
 * @version 1.0
 *
 */
@ManagedBean(name="featuresBean")
@SessionScoped
public class FeaturesBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 18780765747848478L;

	private boolean store;
	private boolean delivery;
	private boolean accounting;
	private boolean xtras;
	private boolean xtraProduct;
	private boolean storeTransfer;
	private boolean adminUser;
	private boolean adminClient;
	private boolean adminSupplier;
	private boolean adminProduct;
	private boolean clients;
	private boolean suppliers;
	private boolean productStat;
	private boolean onHold;
	private boolean recallHistory;
	private boolean accIncomes;
	private boolean accExpenses;
	private boolean accReceivable;
	private boolean accPayable;
	private boolean accSummary;
	private boolean accSoldItems;
	private boolean clientRented;
	private boolean clientCollectible;
	
	private boolean deliveryTruck;
	private boolean deliveryTrans;
	private boolean deliverySold;
	private boolean deliveryReturn;
	private boolean deliveryRented;
	
	private boolean roomMonitoring;
	private boolean tableMonitoring;
	
	private boolean productExpiration;
	private boolean receiptTrans;
	private boolean receiptSetting;
	
	private boolean featuresSettings;
	
	private boolean basic;
	private boolean custom;
	
	private boolean autohold;
	private boolean cashierCustomer;
	private boolean cashierProductReturn;
	
	private double vatValue;
	private boolean points;
	
	private List<Features> basics = Collections.synchronizedList(new ArrayList<Features>());
	private List<Features> customs = Collections.synchronizedList(new ArrayList<Features>());
	
	public void activate(String value){
		boolean isEnabled = false;
		boolean isSelected = false;
		String fet = "";
		if(com.italia.ipos.enm.Features.BASIC_FEATURES.getName().equalsIgnoreCase(value)){
			Features.save(com.italia.ipos.enm.Features.BASIC_FEATURES, basic);
			
			if(basic){
				isSelected = true;
				isEnabled = basic;
				fet = com.italia.ipos.enm.Features.BASIC_FEATURES.getName();
				//enable basic features
				basic(true);
				//disable other features
				otherFeatures(false);
			}else{
				isSelected = false;
				isEnabled = false;
				fet = "Error";
			}
		}else if(com.italia.ipos.enm.Features.CUSTOMIZE_FEATURES.getName().equalsIgnoreCase(value)){
			Features.save(com.italia.ipos.enm.Features.CUSTOMIZE_FEATURES, custom);
			isSelected = true;
			isEnabled = custom;
			fet = com.italia.ipos.enm.Features.CUSTOMIZE_FEATURES.getName();
		}			
		
		
		if(isSelected && isEnabled){
			addMessage(fet + " has been activated.","");
		}else{
			addMessage(fet + " has been deactivated.","");
		}
	}
	
	public void updateVat(){
		Features fet = new Features();
		fet.setModuleName(com.italia.ipos.enm.Features.VAT.getName());
		Features.saveVAT(fet, getVatValue());
		addMessage("VAT has been updated", "");
	}
	
	private void basic(boolean isEnabled){
		/**
		 * cashier
		 */
		Features.save(com.italia.ipos.enm.Features.GROCERRY_CASHIER, isEnabled);
		/**
		 * Administrative
		 */
		Features.save(com.italia.ipos.enm.Features.ADMIN_PRODUCT, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ADMIN_USER, isEnabled);
		/**
		 * Store transfering of product
		 */
		Features.save(com.italia.ipos.enm.Features.STORE_TRANSFER, isEnabled);
		
	}
	
	private void otherFeatures(boolean isEnabled){
		/**
		 * Accounting
		 */
		Features.save(com.italia.ipos.enm.Features.ACCOUNTING, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ACCOUNTING_INCOMES, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ACCOUNTING_EXPENSES, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ACCOUNTING_RECEIVABLE, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ACCOUNTING_PAYABLE, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ACCOUNTING_SUMMARY, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ACCOUNTING_SOLD_ITEMS, isEnabled);
		
		
		/**
		 * Administrative
		 */
		Features.save(com.italia.ipos.enm.Features.ADMIN_CLIENT, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ADMIN_SUPPLIER, isEnabled);
		
		/**
		 * Clients
		 */
		Features.save(com.italia.ipos.enm.Features.CLIENTS, isEnabled);
		Features.save(com.italia.ipos.enm.Features.CLIENT_RENTED_ITEMS, isEnabled);
		Features.save(com.italia.ipos.enm.Features.CLIENT_COLLECTIBLE, isEnabled);
		
		/**
		 * Suppliers
		 */
		Features.save(com.italia.ipos.enm.Features.SUPPLIERS, isEnabled);
		
		/**
		 * Delivery
		 */
		Features.save(com.italia.ipos.enm.Features.DELIEVRY, isEnabled);
		Features.save(com.italia.ipos.enm.Features.DELIVERY_TRUCK, isEnabled);
		Features.save(com.italia.ipos.enm.Features.DELIVERY_TRANSACTION, isEnabled);
		Features.save(com.italia.ipos.enm.Features.DELIVERY_SOLD, isEnabled);
		Features.save(com.italia.ipos.enm.Features.DELIVERY_RETURN, isEnabled);
		Features.save(com.italia.ipos.enm.Features.DELIVERY_RENTED_ITEMS, isEnabled);
		
		/**
		 * Extra type of transactions
		 */
		Features.save(com.italia.ipos.enm.Features.XTRAS, isEnabled);
		
		/**
		 * Cashier
		 */
		Features.save(com.italia.ipos.enm.Features.XTRA_PRODUCT_BUTTON, isEnabled);
		Features.save(com.italia.ipos.enm.Features.ON_HOLD_BUTTON, isEnabled);
		Features.save(com.italia.ipos.enm.Features.HISTORY_BUTTON, isEnabled);
		Features.save(com.italia.ipos.enm.Features.CASHIER_PRODUCT_RETURN, isEnabled);
		Features.save(com.italia.ipos.enm.Features.AUTO_HOLD, isEnabled);
		Features.save(com.italia.ipos.enm.Features.CASHIER_CUSTOMER, isEnabled);
		
		/**
		 * Statistics
		 */
		Features.save(com.italia.ipos.enm.Features.STATISTICS, isEnabled);
		
		/**
		 * Product Expiration
		 */
		Features.save(com.italia.ipos.enm.Features.PRODUCT_EXPIRATION_MONITORING, isEnabled);
		
		/**
		 * Table Monitoring
		 */
		Features.save(com.italia.ipos.enm.Features.TABLE_MONITORING, isEnabled);
		
		/**
		 * Room Monitoring
		 */
		Features.save(com.italia.ipos.enm.Features.ROOM_MONITORING, isEnabled);
		
		Features.save(com.italia.ipos.enm.Features.PRODUCT_GRAPH_LINE, isEnabled);
		Features.save(com.italia.ipos.enm.Features.PRODUCT_GRAPH_PIE, isEnabled);
		
		/**
		 * Customer points
		 * 
		 */
		Features.save(com.italia.ipos.enm.Features.POINTS, isEnabled);
	}
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	@PostConstruct
	public void init(){
		Login in = Login.getUserLogin();
		
		System.out.println("features initialization......");
		if(UserAccess.OWNER.getId() == in.getAccessLevel().getLevel() || 
				UserAccess.MANAGER.getId() == in.getAccessLevel().getLevel()){
			
		store = !Features.isEnabled(com.italia.ipos.enm.Features.GROCERRY_CASHIER);
		delivery = !Features.isEnabled(com.italia.ipos.enm.Features.DELIEVRY);
		accounting = !Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING);
		xtras = !Features.isEnabled(com.italia.ipos.enm.Features.XTRAS);
		xtraProduct = !Features.isEnabled(com.italia.ipos.enm.Features.XTRA_PRODUCT_BUTTON);
		storeTransfer = !Features.isEnabled(com.italia.ipos.enm.Features.STORE_TRANSFER);
		adminUser = !Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_USER);
		adminClient = !Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_CLIENT);
		adminSupplier = !Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_SUPPLIER);
		adminProduct = !Features.isEnabled(com.italia.ipos.enm.Features.ADMIN_PRODUCT);
		clients = !Features.isEnabled(com.italia.ipos.enm.Features.CLIENTS);
		suppliers = !Features.isEnabled(com.italia.ipos.enm.Features.SUPPLIERS);
		productStat = !Features.isEnabled(com.italia.ipos.enm.Features.STATISTICS);
		onHold = !Features.isEnabled(com.italia.ipos.enm.Features.ON_HOLD_BUTTON);
		recallHistory = !Features.isEnabled(com.italia.ipos.enm.Features.HISTORY_BUTTON);
		accIncomes = !Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING_INCOMES);
		accExpenses = !Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING_EXPENSES);
		accReceivable = !Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING_RECEIVABLE);
		accPayable = !Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING_PAYABLE);
		accSummary = !Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING_SUMMARY);
		accSoldItems = !Features.isEnabled(com.italia.ipos.enm.Features.ACCOUNTING_SOLD_ITEMS);
		clientRented = !Features.isEnabled(com.italia.ipos.enm.Features.CLIENT_RENTED_ITEMS);
		clientCollectible = !Features.isEnabled(com.italia.ipos.enm.Features.CLIENT_COLLECTIBLE);
		deliveryTruck = !Features.isEnabled(com.italia.ipos.enm.Features.DELIVERY_TRUCK);
		deliveryTrans = !Features.isEnabled(com.italia.ipos.enm.Features.DELIVERY_TRANSACTION);
		deliverySold = !Features.isEnabled(com.italia.ipos.enm.Features.DELIVERY_SOLD);
		deliveryReturn = !Features.isEnabled(com.italia.ipos.enm.Features.DELIVERY_RETURN);
		deliveryRented = !Features.isEnabled(com.italia.ipos.enm.Features.DELIVERY_RENTED_ITEMS);
		roomMonitoring = !Features.isEnabled(com.italia.ipos.enm.Features.ROOM_MONITORING);
		tableMonitoring = !Features.isEnabled(com.italia.ipos.enm.Features.TABLE_MONITORING);
		productExpiration = !Features.isEnabled(com.italia.ipos.enm.Features.PRODUCT_EXPIRATION_MONITORING);
		autohold = !Features.isEnabled(com.italia.ipos.enm.Features.AUTO_HOLD);
		cashierProductReturn = !Features.isEnabled(com.italia.ipos.enm.Features.CASHIER_PRODUCT_RETURN);
		cashierCustomer = !Features.isEnabled(com.italia.ipos.enm.Features.CASHIER_CUSTOMER);
		//vatValue = Features.retrieve("SELECT * FROM features WHERE isActive=1 AND modulename='"+ com.italia.ipos.enm.Features.VAT.getName()+"'", new String[0]).get(0).getVat();
		receiptTrans = false;
		receiptSetting = true;
		featuresSettings = true;
		points = !Features.isEnabled(com.italia.ipos.enm.Features.POINTS);
		
				
		}else if(UserAccess.DEVELOPER.getId() == in.getAccessLevel().getLevel()){
			
			store = false;
			delivery = false;
			accounting = false;
			xtras = false;
			xtraProduct = false;
			cashierProductReturn = false;
			storeTransfer = false;
			adminUser = false;
			adminClient = false;
			adminSupplier = false;
			adminProduct = false;
			clients = false;
			suppliers = false;
			productStat = false;
			onHold = false;
			recallHistory = false;
			accIncomes = false;
			accExpenses = false;
			accReceivable = false;
			accPayable = false;
			accSummary = false;
			accSoldItems = false;
			clientRented = false;
			clientCollectible = false;
			deliveryTruck = false;
			deliveryTrans = false;
			deliverySold = false;
			deliveryReturn = false;
			deliveryRented = false;
			roomMonitoring = false;
			tableMonitoring = false;
			productExpiration = false;
			receiptTrans = false;
			receiptSetting = false;
			featuresSettings = false;
			autohold = false;
			cashierCustomer = false;
			
			receiptSetting = false;
			featuresSettings = false;
			points = false;
			
			basic = Features.isEnabled(com.italia.ipos.enm.Features.BASIC_FEATURES);
			custom = Features.isEnabled(com.italia.ipos.enm.Features.CUSTOMIZE_FEATURES);
			vatValue = Features.retrieve("SELECT * FROM features WHERE isActive=1 AND modulename='"+ com.italia.ipos.enm.Features.VAT.getName()+"'", new String[0]).get(0).getVat();
			
			loadBasic();
			
		}else{
			store = true;
			delivery = true;
			accounting = true;
			xtras = true;
			xtraProduct = !Features.isEnabled(com.italia.ipos.enm.Features.XTRA_PRODUCT_BUTTON);
			storeTransfer = true;
			adminUser = true;
			adminClient = true;
			adminSupplier = true;
			adminProduct = true;
			clients = true;
			suppliers = true;
			productStat = true;
			onHold = !Features.isEnabled(com.italia.ipos.enm.Features.ON_HOLD_BUTTON);
			recallHistory = !Features.isEnabled(com.italia.ipos.enm.Features.HISTORY_BUTTON);
			accIncomes = true;
			accExpenses = true;
			accReceivable = true;
			accPayable = true;
			accSummary = true;
			accSoldItems = true;
			clientRented = true;
			clientCollectible = true;
			deliveryTruck = true;
			deliveryTrans = true;
			deliverySold = true;
			deliveryReturn = true;
			deliveryRented = true;
			roomMonitoring = true;
			tableMonitoring = true;
			productExpiration = true;
			receiptTrans = true;
			receiptSetting = true;
			featuresSettings = true;
			autohold = !Features.isEnabled(com.italia.ipos.enm.Features.AUTO_HOLD);
			cashierCustomer = !Features.isEnabled(com.italia.ipos.enm.Features.CASHIER_CUSTOMER);
			cashierProductReturn = !Features.isEnabled(com.italia.ipos.enm.Features.CASHIER_PRODUCT_RETURN);
			//vatValue = Features.retrieve("SELECT * FROM features WHERE isActive=1 AND modulename='"+ com.italia.ipos.enm.Features.VAT.getName()+"'", new String[0]).get(0).getVat();
		}
		
	}
	
	public void onTabChange(TabChangeEvent event) {
        
		if("BASIC".equalsIgnoreCase(event.getTab().getTitle())){
			loadBasic();
		}else if("CUSTOM".equalsIgnoreCase(event.getTab().getTitle())){
			loadCustom();
		}
		
    }
	
	private void loadBasic(){
		basics = Collections.synchronizedList(new ArrayList<Features>());
		String sql = "SELECT * FROM features WHERE isActive=1 AND (modulename!=? AND modulename!=? AND modulename!=?)";
		String[] params = new String[3];
		params[0] =  com.italia.ipos.enm.Features.BASIC_FEATURES.getName();
		params[1] =  com.italia.ipos.enm.Features.CUSTOMIZE_FEATURES.getName();
		params[2] =  com.italia.ipos.enm.Features.VAT.getName();
		basics = Features.retrieve(sql, params);
	}
	
	private void loadCustom(){
		customs = Collections.synchronizedList(new ArrayList<Features>());
		String sql = "SELECT * FROM features WHERE isActive=0 AND modulename!=? AND modulename!=? AND modulename!=? AND modulename!=? AND modulename!=? AND modulename!=?";
		String[] params = new String[6];
		params[0] =  com.italia.ipos.enm.Features.CUSTOMIZE_FEATURES.getName();
		params[1] =  com.italia.ipos.enm.Features.STORE_TRANSFER.getName();
		params[2] =  com.italia.ipos.enm.Features.ADMIN_USER.getName();
		params[3] =  com.italia.ipos.enm.Features.ADMIN_PRODUCT.getName();
		params[4] =  com.italia.ipos.enm.Features.GROCERRY_CASHIER.getName();
		params[5] =  com.italia.ipos.enm.Features.BASIC_FEATURES.getName();
		customs = Features.retrieve(sql, params);
	}
	
	public void changeActivation(Features features){
		boolean isactivate = features.isChecked();
		if(isactivate){
			Features.saveData(features, isactivate);
			addMessage(features.getModuleName() + " has been activated", "");
		}else{
			Features.saveData(features, isactivate);
			addMessage(features.getModuleName() + " has been deactivated", "");
		}
	}
	
	public boolean getStore() {
		return store;
	}
	public void setStore(boolean store) {
		this.store = store;
	}
	public boolean getDelivery() {
		return delivery;
	}
	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}
	public boolean getAccounting() {
		return accounting;
	}
	public void setAccounting(boolean accounting) {
		this.accounting = accounting;
	}
	public boolean getXtras() {
		return xtras;
	}
	public void setXtras(boolean xtras) {
		this.xtras = xtras;
	}
	public boolean getXtraProduct() {
		return xtraProduct;
	}
	public void setXtraProduct(boolean xtraProduct) {
		this.xtraProduct = xtraProduct;
	}
	public boolean getStoreTransfer() {
		return storeTransfer;
	}
	public void setStoreTransfer(boolean storeTransfer) {
		this.storeTransfer = storeTransfer;
	}
	public boolean getAdminUser() {
		return adminUser;
	}
	public void setAdminUser(boolean adminUser) {
		this.adminUser = adminUser;
	}
	public boolean getAdminClient() {
		return adminClient;
	}
	public void setAdminClient(boolean adminClient) {
		this.adminClient = adminClient;
	}
	public boolean getAdminSupplier() {
		return adminSupplier;
	}
	public void setAdminSupplier(boolean adminSupplier) {
		this.adminSupplier = adminSupplier;
	}
	public boolean getAdminProduct() {
		return adminProduct;
	}
	public void setAdminProduct(boolean adminProduct) {
		this.adminProduct = adminProduct;
	}
	public boolean getClients() {
		return clients;
	}
	public void setClients(boolean clients) {
		this.clients = clients;
	}
	public boolean getSuppliers() {
		return suppliers;
	}
	public void setSuppliers(boolean suppliers) {
		this.suppliers = suppliers;
	}
	public boolean getProductStat() {
		return productStat;
	}
	public void setProductStat(boolean productStat) {
		this.productStat = productStat;
	}
	public boolean getOnHold() {
		return onHold;
	}
	public void setOnHold(boolean onHold) {
		this.onHold = onHold;
	}
	public boolean getRecallHistory() {
		return recallHistory;
	}
	public void setRecallHistory(boolean recallHistory) {
		this.recallHistory = recallHistory;
	}
	public boolean getAccIncomes() {
		return accIncomes;
	}
	public void setAccIncomes(boolean accIncomes) {
		this.accIncomes = accIncomes;
	}
	public boolean getAccExpenses() {
		return accExpenses;
	}
	public void setAccExpenses(boolean accExpenses) {
		this.accExpenses = accExpenses;
	}
	public boolean getAccReceivable() {
		return accReceivable;
	}
	public void setAccReceivable(boolean accReceivable) {
		this.accReceivable = accReceivable;
	}
	public boolean getAccPayable() {
		return accPayable;
	}
	public void setAccPayable(boolean accPayable) {
		this.accPayable = accPayable;
	}
	public boolean getAccSummary() {
		return accSummary;
	}
	public void setAccSummary(boolean accSummary) {
		this.accSummary = accSummary;
	}
	public boolean getAccSoldItems() {
		return accSoldItems;
	}
	public void setAccSoldItems(boolean accSoldItems) {
		this.accSoldItems = accSoldItems;
	}
	public boolean getClientRented() {
		return clientRented;
	}
	public void setClientRented(boolean clientRented) {
		this.clientRented = clientRented;
	}
	public boolean getClientCollectible() {
		return clientCollectible;
	}
	public void setClientCollectible(boolean clientCollectible) {
		this.clientCollectible = clientCollectible;
	}
	public boolean getDeliveryTruck() {
		return deliveryTruck;
	}
	public void setDeliveryTruck(boolean deliveryTruck) {
		this.deliveryTruck = deliveryTruck;
	}
	public boolean getDeliveryTrans() {
		return deliveryTrans;
	}
	public void setDeliveryTrans(boolean deliveryTrans) {
		this.deliveryTrans = deliveryTrans;
	}
	public boolean getDeliverySold() {
		return deliverySold;
	}
	public void setDeliverySold(boolean deliverySold) {
		this.deliverySold = deliverySold;
	}
	public boolean getDeliveryReturn() {
		return deliveryReturn;
	}
	public void setDeliveryReturn(boolean deliveryReturn) {
		this.deliveryReturn = deliveryReturn;
	}
	public boolean getDeliveryRented() {
		return deliveryRented;
	}
	public void setDeliveryRented(boolean deliveryRented) {
		this.deliveryRented = deliveryRented;
	}
	public boolean getRoomMonitoring() {
		return roomMonitoring;
	}
	public void setRoomMonitoring(boolean roomMonitoring) {
		this.roomMonitoring = roomMonitoring;
	}
	public boolean getTableMonitoring() {
		return tableMonitoring;
	}
	public void setTableMonitoring(boolean tableMonitoring) {
		this.tableMonitoring = tableMonitoring;
	}
	public boolean getProductExpiration() {
		return productExpiration;
	}
	public void setProductExpiration(boolean productExpiration) {
		this.productExpiration = productExpiration;
	}

	public boolean getReceiptTrans() {
		return receiptTrans;
	}

	public void setReceiptTrans(boolean receiptTrans) {
		this.receiptTrans = receiptTrans;
	}

	public boolean getReceiptSetting() {
		return receiptSetting;
	}

	public void setReceiptSetting(boolean receiptSetting) {
		this.receiptSetting = receiptSetting;
	}

	public boolean getFeaturesSettings() {
		return featuresSettings;
	}

	public void setFeaturesSettings(boolean featuresSettings) {
		this.featuresSettings = featuresSettings;
	}

	public boolean getBasic() {
		return basic;
	}

	public void setBasic(boolean basic) {
		this.basic = basic;
	}

	public boolean getCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public boolean getAutohold() {
		return autohold;
	}

	public void setAutohold(boolean autohold) {
		this.autohold = autohold;
	}

	public boolean getCashierCustomer() {
		return cashierCustomer;
	}

	public void setCashierCustomer(boolean cashierCustomer) {
		this.cashierCustomer = cashierCustomer;
	}

	public boolean getCashierProductReturn() {
		return cashierProductReturn;
	}

	public void setCashierProductReturn(boolean cashierProductReturn) {
		this.cashierProductReturn = cashierProductReturn;
	}

	public List<Features> getBasics() {
		return basics;
	}

	public void setBasics(List<Features> basics) {
		this.basics = basics;
	}

	public List<Features> getCustoms() {
		return customs;
	}

	public void setCustoms(List<Features> customs) {
		this.customs = customs;
	}

	public double getVatValue() {
		return vatValue;
	}

	public void setVatValue(double vatValue) {
		this.vatValue = vatValue;
	}

	public boolean isPoints() {
		return points;
	}

	public void setPoints(boolean points) {
		this.points = points;
	}

	
	
}
