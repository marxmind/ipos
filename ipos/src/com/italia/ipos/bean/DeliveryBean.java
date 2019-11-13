package com.italia.ipos.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.event.TabChangeEvent;

import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.CustomerPayment;
import com.italia.ipos.controller.CustomerPaymentTrans;
import com.italia.ipos.controller.DeliveryItem;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.DeliveryItemTrans;
import com.italia.ipos.controller.InputedInventoryQtyTracker;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.MoneyIO;
import com.italia.ipos.controller.Payment;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductInventory;
import com.italia.ipos.controller.ProductPricingTrans;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.RentedBottle;
import com.italia.ipos.controller.RentedBottleTrans;
import com.italia.ipos.controller.Supplier;
import com.italia.ipos.controller.SupplierItem;
import com.italia.ipos.controller.SupplierPayment;
import com.italia.ipos.controller.SupplierTrans;
import com.italia.ipos.controller.UOM;
import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.enm.DeliveryStatus;
import com.italia.ipos.enm.MoneyStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.ReceiptStatus;
import com.italia.ipos.enm.RentedStatus;
import com.italia.ipos.utils.Currency;
import com.italia.ipos.utils.DateUtils;
/**
 * 
 * @author mark italia
 * @since 12/26/2016
 * @version 1.0
 *
 */
@ManagedBean(name="deliveryBean", eager=true)
@ViewScoped
public class DeliveryBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 770962343376931L;
	
	private List suppliers = new ArrayList<>();
	private long supplierId;
	private List products = new ArrayList<>();
	private List productsSup = new ArrayList<>();
	private long productId;
	private double priceAmount;
	private double quantity;
	private List<SupplierItem> items = Collections.synchronizedList(new ArrayList<SupplierItem>());
	private String addFromDate;
	private String addToDate;
	private String addSearchSupplier;
	private String addSearchProdcut;
	private String dateTransacted;
	private String productExp;
	private String remarks;
	
	private Supplier addSelectedSupplier;
	private SupplierTrans addSelectedSupplierTrans;
	private SupplierItem addSelectedItem;
	private List<SupplierItem> addSelectedItemsData = Collections.synchronizedList(new ArrayList<SupplierItem>());
	private List<SupplierTrans> trans = Collections.synchronizedList(new ArrayList<SupplierTrans>());
	private String grandTotalSupplierPurchased;
	private String addSelectedSupplierDeliveryReceipt;
	
	private String addDeliveryDateTrans;
	private double addDeliverySellingPrice;
	private double addDeliveryQty;
	private String addDeliveryStatus;
	private String addDeliveryRemarks;
	private List<DeliveryItem> deliveryItems = Collections.synchronizedList(new ArrayList<DeliveryItem>());
	private String addDeliveryDateFrom;
	private String addDeliveryDateTo;
	private DeliveryItem deliveryData;
	private Product addDeliveryProductData;
	private double addDeliveryRemainingQtyinWarehouse;
	private List<DeliveryItem> selectedAddedDeliveryItems;
	private String addDeliveryGrandTotalSelling;
	private double addDeliveryTotalQty;
	
	
	private DeliveryItemReceipt receiptData;
	private Customer soldCustomer;
	private String soldDate;
	private String soldRemarks;
	private DeliveryItem soldItem;
	private String soldDateFrom;
	private String soldDateTo;
	private String soldPaymentStatus;
	private String soldStatus;
	private double soldQty;
	private double soldPrice;
	private double soldBalance;
	private double soldDiscount;
	private long soldCustomerId;
	private String searchSoldCustomer;
	private List soldCustomers;
	private List<DeliveryItemReceipt> receipts = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
	private String soldReceiptNo;
	private double soldChargeAmount;
	private double soldGrandTotal;
	private double soldDownPayment;
	private boolean isSoldItemPosted;
	private boolean customerNameLock;
	private List<DeliveryItemReceipt> selectedReceipts;
	private String soldItemGrandTotalPurchased;
	private String soldItemGrandTotalBalance;
	
	private List<DeliveryItemTrans> itemSolds = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
	private List<DeliveryItemTrans> itemSoldSelected;
	private DeliveryItemTrans soldData;
	private String soldItemDate;
	private double soldItemPrice;
	private double soldItemQty;
	private double soldItemQtyInDelivery;
	private double soldItemSellingPrice;
	private String soldSearchProduct;
	private long soldProductId;
	private List soldProducts;
	private String soldItemStatus;
	private String soldItemRemarks;
	
	
	private List<DeliveryItemTrans> soldProductItem = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
	private String soldProductItemDateFrom;
	private String soldProductItemDateTo;
	private String soldProductItemPriceGrandTotal;
	private double soldProductItemQtyGrandTotal;
	private String soldProductItemGrandTotal;
	
	private List<DeliveryItem> returnItems = Collections.synchronizedList(new ArrayList<DeliveryItem>());
	private String returnDateFrom;
	private String returnDateTo;
	private String returnProductName;
	private String returnProductItemGrandTotal;
	private String retrunProductItemPriceGrandTotal;
	private double returnProductItemQtyGrandTotal;
	
	private List<RentedBottleTrans> rentedItems = Collections.synchronizedList(new ArrayList<RentedBottleTrans>());
	private String rentDateFrom;
	private String rentDateTo;
	
	private String retDateTrans;
	private double rentQuantity;
	private double rentTotalAmount;
	private double rentPaidAmount;
	private double rentBalance;
	private double rentChargeitem;
	private String rentRemarks;
	private List rentUom;
	private int rentUomId;
	private List rentCustomer;
	private long rentCustomerId;
	private List rentProduct;
	private long rentProductId;
	private RentedBottleTrans rentedData;
	private double rentDeposit;
	private String rentDescription;
	private int uomIdTmp;
	private long prodcutIdTmp;
	private long customerIdTmp;
	private List<RentedBottleTrans> selectedRentedTrans = Collections.synchronizedList(new ArrayList<RentedBottleTrans>());
	
	private String searchName;
	private String searchProduct;
	
	public void onTabChange(TabChangeEvent event) {
        /*FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);*/
				
		if("Product From Suppliers".equalsIgnoreCase(event.getTab().getTitle())){
			loadSupplierInit();
		}else if("Product for Delivery".equalsIgnoreCase(event.getTab().getTitle())){
			loadAddedItemDelivery();
		}else if("Product Transactions".equalsIgnoreCase(event.getTab().getTitle())){
			loadSoldItemDelivery();
		}else if("Product Sold From Delivery".equalsIgnoreCase(event.getTab().getTitle())){
			loadSoldProductOnly();
		}else if("Product Return From Delivery".equalsIgnoreCase(event.getTab().getTitle())){
			loadReturnDelivery();
		}else if("Damaged Items".equalsIgnoreCase(event.getTab().getTitle())){
			
		}else if("Items Returned by Customer".equalsIgnoreCase(event.getTab().getTitle())){
			
		}else if("Items Rented by Customer".equalsIgnoreCase(event.getTab().getTitle())){
			loadRented();
		}
		
    }
	
	@PostConstruct
	public void init(){
		loadSupplierInit();
	}
	
	//==============================================Added Item============================================================
	public void newSupplierTrans(){
		clearSupplierFields();
		loadSupplier();
	}
	
	public void clearSupplierFields(){
		setSupplierId(0);
		setAddSearchSupplier(null);
		setAddSelectedSupplier(null);
		setAddSelectedSupplierDeliveryReceipt(null);
	}
	
	public void addSave(){
		boolean isPosted = false;
		if(getAddSelectedItem()!=null && getAddSelectedItem().getStatus()==2){
			isPosted = true;
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Items were already posted. Editing is no longer allowed.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		if(Login.checkUserStatus() && !isPosted){
			
			if(getAddSelectedSupplierTrans()!=null){
				SupplierItem item = new SupplierItem();
				if(getAddSelectedItem()!=null){
					item = getAddSelectedItem();
				}else{
					item.setStatus(1);
					item.setIsActiveItem(1);
				}
				item.setDatePurchased(getDateTransacted()==null? DateUtils.getCurrentDateYYYYMMDD() : getDateTransacted());
				item.setPurchasedPrice(getPriceAmount());
				item.setQuantity(getQuantity());
				item.setProductExpiration(getProductExp());
				item.setRemarks(getRemarks());
				item.setSupplierTrans(getAddSelectedSupplierTrans());
				item.setUserDtls(Login.getUserLogin().getUserDtls());
				
				Product prod = new Product();
				prod.setProdid(getProductId());
				item.setProduct(prod);
				
				item.save();
				clickItemOrder(getAddSelectedSupplierTrans());
				clearFields();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been successfully saved.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Please select supplier transaction", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void addItemPosted(){
		if(Login.checkUserStatus()){
			
			if(getAddSelectedSupplierTrans()!=null && getAddSelectedSupplierTrans().getStatus()==3){
		
			double amount = 0d;
			for(SupplierItem item : items){
				amount += item.getPurchasedPrice();
				
				item.setUserDtls(Login.getUserLogin().getUserDtls());
				item.setStatus(2);
				item.save();
				
				//adding inventory
				ProductInventory inv = new ProductInventory();
				Product prod = new Product();
				prod.setIsactiveproduct(1);
				
				prod.setProdid(item.getProduct().getProdid());
				
				List<ProductInventory> invs = ProductInventory.retrieve(prod);
				
				if(invs.size()==0){
					inv.setProduct(item.getProduct());
					inv.setNewqty(0);
					inv.setIsactive(1);
					System.out.println("NEW TO INVENTORY");
				}else{
					inv = invs.get(0);
					System.out.println("OLD TO INVENTORY");
				}
					
				double oldQuantity = 0d;
				oldQuantity = inv.getNewqty();
				Double qty = item.getQuantity() + inv.getNewqty();
				inv.setNewqty(qty);
				inv.setOldqty(oldQuantity);
				inv.setProductProperties(item.getProduct().getProductProperties());
				inv.setUserDtls(Login.getUserLogin().getUserDtls());
				inv.save();
				
				//track inputed quantity
				inv.setAddqty(item.getQuantity());
				InputedInventoryQtyTracker.saveQty(inv, "FROM SUPPLIER");
				
			}
			SupplierTrans tran = getAddSelectedSupplierTrans();
			tran.setUserDtls(Login.getUserLogin().getUserDtls());
			tran.setStatus(1);
			tran.setPurchasedPrice(amount);
			tran.save();
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "This transaction has been successfully posted. Item quantity will be added to inventory", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: This transaction was already processed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void addNewItem(){
		if(getAddSelectedSupplierTrans()!=null && getAddSelectedSupplierTrans().getStatus()==1){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "This transaction already posted. Adding of item is no longer allowed.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}else{
			clearFields();
		}
	}
	
	public void deleteAddTran(SupplierTrans tran){
		if(Login.checkUserStatus()){
			if(tran!=null && tran.getStatus()==3 && tran.getPurchasedPrice()==0){
				
				SupplierItem item = new SupplierItem();
				item.setIsActiveItem(1);
				//item.setStatus(1);
				SupplierTrans trn = new SupplierTrans();
				trn.setId(tran.getId());
				trn.setIsActive(1);
				List<SupplierItem> items =SupplierItem.retrieve(trn,item);
				if(items.size()==0){
					if(DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(tran.getTransDate())){
						tran.delete();
						clearSupplierFields();
						loadSupplierTransactions();
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deletion of transaction has been successfully processed.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Deletion of data is not allowed. This item has been processed already.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: There is/are attached item for this transaction. Please delete first the item.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Transaction has been already processed. Deletion of data is now invalid.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Login session has been expired. Please login again..", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void deleteAddItem(SupplierItem item){
		if(Login.checkUserStatus()){
			if(item.getStatus()==1){
				item.delete();
				clickItemOrder(getAddSelectedSupplierTrans());
				clearFields();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deletion of item has been successfully processed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Deletion of data is not allowed. This item has been processed already.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Login session has been expired. Please login again..", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void clearFields(){
		setAddSelectedItem(null);
		setAddSearchProdcut(null);
		setProductId(0);
		setQuantity(0.00);
		setPriceAmount(0.00);
		setRemarks(null);
		setProductExp(null);
	}
	
	public void assignSupplier(){
		if(getSupplierId()!=0){
			Supplier supplier = new Supplier();
			supplier.setIsactive(1);
			supplier.setSupid(getSupplierId());
			supplier = Supplier.retrieve(supplier).get(0);
			setAddSelectedSupplier(supplier);
		}
	}
	
	public void createSupplierTransaction(){
		if(Login.checkUserStatus()){
			
			boolean isOk = true;
			
			if(getAddSelectedSupplier()==null){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select supplier", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
			}
			if(getAddSelectedSupplierDeliveryReceipt()==null || getAddSelectedSupplierDeliveryReceipt().isEmpty()) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide receipt no", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
			}
			
			if(isOk) {
				SupplierTrans tran = new SupplierTrans();
				tran.setTransDate(getDateTransacted()==null? DateUtils.getCurrentDateYYYYMMDD() : getDateTransacted());
				tran.setIsActive(1);
				tran.setStatus(3);
				tran.setPurchasedPrice(0.00);
				tran.setUserDtls(Login.getUserLogin().getUserDtls());
				tran.setSupplier(getAddSelectedSupplier());
				tran.setDeliveryReceipt(getAddSelectedSupplierDeliveryReceipt());
				tran.save();
				String supplierName = getAddSelectedSupplier().getSuppliername();
				clearSupplierFields();
				loadSupplierTransactions();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully created new transaction for " + supplierName, "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void clickItemOrder(SupplierTrans sup){
		setAddSelectedSupplierTrans(sup);
		SupplierItem item = new SupplierItem();
		item.setIsActiveItem(1);
		//item.setStatus(1);
		SupplierTrans tran = new SupplierTrans();
		tran.setId(sup.getId());
		tran.setIsActive(1);
		items = Collections.synchronizedList(new ArrayList<SupplierItem>()); 
		for(SupplierItem i : SupplierItem.retrieve(tran,item)){
			
				ProductProperties prop = new ProductProperties();
				Product prod = new Product();
				prod = Product.retrieve(i.getProduct().getProdid()+"");
				prop = ProductProperties.properties(prod.getProductProperties().getPropid()+"");
				prod.setProductProperties(prop);
				
			
			i.setProduct(prod);
			items.add(i);
		}
		loadProductItem();
		Collections.reverse(items);
	}
	
	public void clickItemEdit(SupplierItem item){
		setAddSelectedItem(item);
		setDateTransacted(item.getDatePurchased());
		setProductId(item.getProduct().getProdid());
		setQuantity(item.getQuantity());
		setPriceAmount(item.getPurchasedPrice());
		setProductExp(item.getProductExpiration());
		setRemarks(item.getRemarks());
	}
	
	public void loadSupplierInit(){
		loadSupplier();
		//loadProduct();
		loadProductSup();
		loadSupplierTransactions();
	}
	
	public void loadSupplierTransactions(){
		SupplierTrans tran = new SupplierTrans();
		tran.setIsActive(1);
		//tran.setStatus(3);
		tran.setBetweenDate(true);
		tran.setDateFrom(getAddFromDate());
		tran.setDateTo(getAddToDate());
		trans = Collections.synchronizedList(new ArrayList<SupplierTrans>());
		double price = 0d;
		for(SupplierTrans sup : SupplierTrans.retrieve(tran)){
			price += sup.getPurchasedPrice();
			trans.add(sup);
		}
		setGrandTotalSupplierPurchased(Currency.formatAmount(price+""));
		Collections.reverse(trans);
	}
	
	private double balanceToSupplierAmnt(Supplier sup){
		
		double balance = 0d;
		SupplierTrans tran = new SupplierTrans();
		tran.setIsActive(1);
		tran.setStatus(1);
		Supplier sp = new Supplier();
		sp.setSupid(sup.getSupid());
		sp.setIsactive(1);
		for(SupplierTrans t :  SupplierTrans.retrieve(tran,sp)){
			SupplierPayment pay = new SupplierPayment();
			pay.setIsActive(1);
			SupplierTrans tr = new SupplierTrans();
			tr.setIsActive(1);
			tr.setId(t.getId());
			tr.setStatus(1);
			double amnt = 0d;
			for(SupplierPayment py : SupplierPayment.retrieve(pay,tr)){
				amnt += py.getAmount();
			}
			balance += t.getPurchasedPrice() - amnt;
		}
		
		return balance;
	}
	
	public void printAddedItem(){
		
	}
	
	public void loadSupplier(){
		suppliers = new ArrayList<>();
		Supplier supplier = new Supplier();
		supplier.setIsactive(1);
		if(getAddSearchSupplier()!=null){
			supplier.setSuppliername(getAddSearchSupplier());
		}
		for(Supplier sup : Supplier.retrieve(supplier)){
			suppliers.add(new SelectItem(sup.getSupid(), sup.getSuppliername()));
		}
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
	
	public void loadAddedItemDelivery(){
		/**
		 * automatically reinserted to database as new delivery from the remaining product from previous delivery
		 */
		retrieveRemainingProductInDeliveryTruck();
		
		DeliveryItem item = new DeliveryItem();
		item.setIsActive(1);
		//item.setStatus(DeliveryStatus.FOR_DELIVERY.getId());
		item.setStatusOR(true);
		item.setStatusParam1(DeliveryStatus.NEW_FOR_DELIVERY.getId());
		item.setStatusParam2(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
		item.setBetween(true);
		item.setDateFrom(getAddDeliveryDateFrom());
		item.setDateTo(getAddDeliveryDateTo());
		deliveryItems = Collections.synchronizedList(new ArrayList<DeliveryItem>());
		//deliveryItems = DeliveryItem.retrieve(item);
		double priceTotal = 0d, qtyTotal = 0d;
		for(DeliveryItem itm : DeliveryItem.retrieve(item)){
			Product prod = new Product();
			//prod = Product.retrieve(itm.getProduct().getProdid()+"");
			ProductProperties prop = new ProductProperties();
			prop.setPropid(itm.getProduct().getProductProperties().getPropid());
			prop.setIsactive(1);
			prop = ProductProperties.retrieve(prop).get(0);
			prod = itm.getProduct();
			prod.setProductProperties(prop);
			itm.setProduct(prod);
			
			priceTotal += itm.getSellingPrice();
			qtyTotal += itm.getQuantity();
			
			deliveryItems.add(itm);
		}
		loadProduct();
		setAddDeliveryGrandTotalSelling(Currency.formatAmount(priceTotal+""));
		setAddDeliveryTotalQty(qtyTotal);
		Collections.reverse(deliveryItems);
	}
	
	public void clickItemDelivery(DeliveryItem item){
		setDeliveryData(item);
		setAddDeliveryDateFrom(item.getDateTrans());
		setProductId(item.getProduct().getProdid());
		setAddDeliverySellingPrice(item.getSellingPrice());
		setAddDeliveryQty(item.getQuantity());
		setAddDeliveryStatus(DeliveryStatus.statusName(item.getStatus()));
		setAddDeliveryRemarks(item.getRemarks());
		
		retrieveWareHouseQty(item);
	}
	
	public void retrieveWareHouseQty(DeliveryItem item){
		Product prod = new Product();
		prod.setProdid(item.getProduct().getProdid());
		prod.setIsactiveproduct(1);
		ProductInventory inv = ProductInventory.retrieve(prod).get(0);
		
		double remQty = 0d;
		System.out.println("calculate warehouse qty...");
		remQty = inv.getNewqty() - countItemDeliveryNewQty(prod);
		System.out.println("end calculate warehouse qty..." + remQty);
		setAddDeliveryRemainingQtyinWarehouse(remQty);
		
	}
	
	public void productPrice(){
		if(getProductId()!=0){
			ProductPricingTrans prodPrice = ProductPricingTrans.retrievePrice(getProductId()+"");
			setAddDeliverySellingPrice(Double.valueOf(prodPrice.getSellingprice()+""));
			setAddDeliveryProductData(prodPrice.getProduct());
			
			Product prod = new Product();
			prod.setProdid(prodPrice.getProduct().getProdid());
			prod.setIsactiveproduct(1);
			ProductInventory inv = ProductInventory.retrieve(prod).get(0);
			
			double remQty = 0d;
			System.out.println("calculate warehouse qty...");
			remQty = inv.getNewqty() - countItemDeliveryNewQty(prod);
			System.out.println("end calculate warehouse qty..." + remQty);
			setAddDeliveryRemainingQtyinWarehouse(remQty);
		}
	}
	
	public void calculateAddDeliveryQty(){
		
		if(getAddDeliveryQty()>getAddDeliveryRemainingQtyinWarehouse()){
			setAddDeliveryQty(getAddDeliveryRemainingQtyinWarehouse());
		}
		
	}
	
	public void saveAddedDeliveryItem(){
		if(Login.checkUserStatus()){
			
			if(getAddDeliveryProductData()!=null){
				boolean isOk = true;
				DeliveryItem item = new DeliveryItem();
				if(getDeliveryData()!=null){
					item = getDeliveryData();
					
					if((!DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(item.getDateTrans())) && item.getStatus()==2){
						isOk=false;
					}
					
				}else{
					item.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
					item.setStatus(DeliveryStatus.NEW_FOR_DELIVERY.getId());
				}
				
				if(isOk){
				
				if(getAddDeliveryQty()>0){	
				item.setSellingPrice(getAddDeliverySellingPrice());
				item.setChargeAmount(0);
				item.setQuantity(getAddDeliveryQty());
				item.setRemarks(getAddDeliveryRemarks());
				item.setIsActive(1);
				item.setProduct(getAddDeliveryProductData());
				item.setUserDtls(Login.getUserLogin().getUserDtls());
				item.save();
				clearDeliveryFields();
				loadAddedItemDelivery();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been successfully saved.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Zero quantity is not allowed for saving.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Data has been processed already.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
            
			}else{
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been processed already. Editing is not allowed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
				
			}
            
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void addDeliveryItemPosted(){
		if(getSelectedAddedDeliveryItems().size()>0){
			int cnt = 0; //count posted items
			for(DeliveryItem item : getSelectedAddedDeliveryItems()){
				
				if(item.getStatus()==DeliveryStatus.NEW_FOR_DELIVERY.getId()){ //only new status will posting
					
					//adding inventory
					ProductInventory inv = new ProductInventory();
					Product prod = new Product();
					prod.setIsactiveproduct(1);
					prod.setProdid(item.getProduct().getProdid());
					
					inv = ProductInventory.retrieve(prod).get(0);
					double oldQuantity = 0d;
					oldQuantity = inv.getNewqty();
					Double qty = -item.getQuantity() + inv.getNewqty();
					inv.setNewqty(qty);
					inv.setOldqty(oldQuantity);
					inv.setProductProperties(item.getProduct().getProductProperties());
					inv.setUserDtls(Login.getUserLogin().getUserDtls());
					inv.save();
					
					//track inputed quantity
					inv.setAddqty(-item.getQuantity());
					InputedInventoryQtyTracker.saveQty(inv, "TRANSFERRED ITEM TO DELIVERY TRUCK");
					
					item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId()); //
					item.save();
					cnt++;
					loadAddedItemDelivery();
					
				}
				
				/**
				 * Temporary commented
				 * this code is working 
				 */
				/*else if(item.getStatus()==DeliveryStatus.POSTED_FOR_DELIVERY.getId()){ //recalling posting items
					
					if(isSoldItemHasData(item)){
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: There are already items were tagged for this Product. Changing status to NEW is no longer available.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
					
					//adding inventory
					ProductInventory inv = new ProductInventory();
					Product prod = new Product();
					prod.setIsactiveproduct(1);
					prod.setProdid(item.getProduct().getProdid());
					
					inv = ProductInventory.retrieve(prod).get(0);
					double oldQuantity = 0d;
					oldQuantity = inv.getNewqty();
					Double qty = item.getQuantity() + inv.getNewqty();
					inv.setNewqty(qty);
					inv.setOldqty(oldQuantity);
					inv.setUserDtls(Login.getUserLogin().getUserDtls());
					inv.save();
					
					//track inputed quantity
					inv.setAddqty(item.getQuantity());
					InputedInventoryQtyTracker.saveQty(inv, "RECALLED TRANSFERRED ITEM FROM DELIVERY TRUCK");
					
					item.setStatus(DeliveryStatus.NEW_FOR_DELIVERY.getId()); //
					item.save();
					cnt++;
					loadAddedItemDelivery();
					}
					
				}*/
				
			}
			if(cnt>0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, cnt + " data has been processed for posting.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            setSelectedAddedDeliveryItems(null);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "There are no data have been processed for posting.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Please check an item for posting.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public double countItemDeliveryNewQty(Product prod){
		
		Product prd = new Product();
		prd.setProdid(prod.getProdid());
		prd.setIsactiveproduct(1);
		
		DeliveryItem item = new DeliveryItem();
		item.setIsActive(1);
		item.setStatus(DeliveryStatus.NEW_FOR_DELIVERY.getId());
		item.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		
		double qty = 0d;
		for(DeliveryItem itm : DeliveryItem.retrieve(item,prd)){
			qty += itm.getQuantity();
		}
		
		return qty;
	}
	
	public void addNewDeliveryItem(){
		clearDeliveryFields();
	}
	
	public void deleteAddDelivery(DeliveryItem item){
		if(Login.checkUserStatus()){
			if(item.getStatus()==DeliveryStatus.NEW_FOR_DELIVERY.getId()){
				item.delete();
				loadAddedItemDelivery();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been successfully removed from the list.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Deletion of data is not allowed. This item has been processed already.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void clearDeliveryFields(){
		setAddDeliveryProductData(null);
		setDeliveryData(null);
		setAddDeliveryDateTrans(null);
		setAddDeliverySellingPrice(0);
		setAddDeliveryQty(0);
		setAddDeliveryStatus(null);
		setAddDeliveryRemarks(null);
		setAddDeliveryRemainingQtyinWarehouse(0);
	}
	
	public List getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List suppliers) {
		this.suppliers = suppliers;
	}

	public long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(long supplierId) {
		this.supplierId = supplierId;
	}
	
	public void loadProduct(){
		products = new ArrayList<>();
		if(getAddSearchProdcut()!=null && !getAddSearchProdcut().isEmpty()){
		Product prod = new Product();
		prod.setIsactiveproduct(1);
		
		ProductProperties prop = new ProductProperties();
		prop.setIsactive(1);
		
		
		prop.setProductname(getAddSearchProdcut());
		
		
		for(Product p : Product.retrieve(prod, prop)){
			
			ProductInventory inv = new ProductInventory();
			inv.setIsactive(1);
			
			prod = new Product();
			prod.setProdid(p.getProdid());
			prod.setIsactiveproduct(1);
			List<ProductInventory> invs = ProductInventory.retrieve(inv,prod);
			if(invs.size()>0){
				if(invs.get(0).getNewqty()>0){
					prop = ProductProperties.properties(p.getProductProperties().getPropid()+"");
					products.add(new SelectItem(invs.get(0).getProduct().getProdid(), prop.getProductname() + " per " + prop.getUom().getUomname()));
				}
			}
		}
		
		}// search product item should not be empty
		Collections.reverse(products);
		/*for(ProductPricingTrans trans : ProductPricingTrans.retrieve(price, prod)){
			prop = ProductProperties.properties(trans.getProduct().getProductProperties().getPropid()+"");
			products.add(new SelectItem(trans.getProduct().getProdid(), prop.getProductname() + " per " + prop.getUom().getUomname()));
		}*/
		
		
		
		/*for(Product p : Product.retrieve(prod,prop)){
			prop = ProductProperties.properties(p.getProductProperties().getPropid()+"");
			products.add(new SelectItem(p.getProdid(), prop.getProductname() + " per " + prop.getUom().getUomname()));
		}*/
		
	}
	
	public void loadProductItem(){
		productsSup = new ArrayList<>();
		
		ProductProperties prop = new ProductProperties();
		prop.setIsactive(1);
		
		Product prod = new Product();
		prod.setIsactiveproduct(1);
		for(Product p : Product.retrieve(prod,prop)){
			prop = ProductProperties.properties(p.getProductProperties().getPropid()+"");
			productsSup.add(new SelectItem(p.getProdid(), prop.getProductname() + " per " + prop.getUom().getUomname()));
		}
		
		Collections.reverse(productsSup);
	}
	
	public void loadProductSup(){
		productsSup = new ArrayList<>();
		if(getAddSearchProdcut()!=null && !getAddSearchProdcut().isEmpty()){
		ProductProperties prop = new ProductProperties();
		prop.setIsactive(1);
		
		
		prop.setProductname(getAddSearchProdcut());
		
		
		Product prod = new Product();
		prod.setIsactiveproduct(1);
		for(Product p : Product.retrieve(prod,prop)){
			prop = ProductProperties.properties(p.getProductProperties().getPropid()+"");
			productsSup.add(new SelectItem(p.getProdid(), prop.getProductname() + " per " + prop.getUom().getUomname()));
		}
		}// item search should not be empty
		Collections.reverse(productsSup);
	}
	
	///////////////////////////////////////////////////////////////////////////SOLD////////////////////////////////
	
	
	public void soldDeliveryItemPosted(){
		
		if(getReceiptData().getStatus()==ReceiptStatus.POSTED.getId()){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "The receipt for this transactios was already processed. Reposting is no longer allowed.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}else{
		
		if(getItemSoldSelected()!=null && getItemSoldSelected().size()>0){
			int cnt = 0;
			int icnt = 0;
			boolean isDateInvalid = false; 
			
			for(DeliveryItemTrans tran : getItemSoldSelected()){
				if(DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(tran.getDateTrans())){
					if(tran.getStatus()==DeliveryStatus.NEW_SOLD_ITEM.getId()){
						tran.setStatus(DeliveryStatus.POSTED_SOLD_ITEM.getId());
						tran.setUserDtls(Login.getUserLogin().getUserDtls());
						tran.save();
						cnt++;
					}else if(tran.getStatus()==DeliveryStatus.POSTED_SOLD_ITEM.getId()){
						tran.setStatus(DeliveryStatus.NEW_SOLD_ITEM.getId());
						tran.setUserDtls(Login.getUserLogin().getUserDtls());
						tran.save();
						icnt++;
					}
				}else{
					isDateInvalid = true;
				}
			}
			
			if(cnt>0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,  (cnt==0? "No item " : (cnt==1? "1 item has " : cnt +" items have ")) + " been successfully posted.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			if(icnt>0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,  (icnt==0? "No item " : (icnt==1? "1 item has " : icnt +" items have ")) + " been successfully recalled.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			if(isDateInvalid){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,  "Posting of an item is no longer available. Posting of an items are allowed only during the creation of receipt.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: No item has been selected for posting.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		}
	}
	
	public void loadSoldCustomer(){
		
		Customer cus = new Customer();
		cus.setIsactive(1);
		if(getSearchSoldCustomer()!=null && !getSearchSoldCustomer().isEmpty()){
		cus.setFullname(getSearchSoldCustomer().trim().replace("--", ""));
		
		soldCustomers = new ArrayList<>();
		for(Customer c : Customer.retrieve(cus)){
			soldCustomers.add(new SelectItem(c.getCustomerid(), c.getFullname()));
		}
		}
	}
	
	public void deleteReceipt(DeliveryItemReceipt rec){
		if(Login.checkUserStatus()){
			
			if(rec.getStatus()==ReceiptStatus.NEW.getId()){
				
				if(isSoldItemHasData(rec)){
					
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: There is/are item tag for this receipt. Deletion is not allowed. Please delete first the item.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
					
				}else{
					rec.delete();
					loadSoldItemDelivery();
					clearReceiptFields();
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been processed successfully deleted", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been processed already. Deletion is not allowed", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public boolean isSoldItemHasData(Object obj){
		
		
		if(obj instanceof DeliveryItemReceipt){
			DeliveryItemReceipt rec = (DeliveryItemReceipt)obj;
			DeliveryItemTrans tran = new DeliveryItemTrans();
			List<DeliveryItemTrans> trans;
			tran.setIsActive(1);
			tran.setDateTrans(rec.getDateTrans());
			DeliveryItemReceipt rcpt = new DeliveryItemReceipt();
			rcpt.setId(rec.getId());
			rcpt.setIsActive(1);
			rcpt.setDateTrans(rec.getDateTrans());
			trans = DeliveryItemTrans.retrieve(tran,rcpt);
			if(trans.size()>0){
				System.out.println("has a record....");
				return true;
			}
		
		}
		
		if(obj instanceof DeliveryItem){
		DeliveryItem item = (DeliveryItem)obj;
		
		
		Product prod = new Product();
		prod.setProdid(item.getProduct().getProdid());
		prod.setIsactiveproduct(1);
		
		DeliveryItem itm = new DeliveryItem();
		
		itm.setIsActive(1);
		itm.setDateTrans(item.getDateTrans());
		itm.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
		double qtyInTruck = 0d;
		for(DeliveryItem it : DeliveryItem.retrieve(itm,prod)){
			qtyInTruck += it.getQuantity();
		}
		
		itm = new DeliveryItem();
		itm.setIsActive(1);
		itm.setDateTrans(item.getDateTrans());
		DeliveryItemTrans tran = new DeliveryItemTrans();
		tran.setIsActive(1);
		tran.setDateTrans(item.getDateTrans());
		
		
		double qtySold = 0d;
		double remQty = 0d;
		for(DeliveryItemTrans tr :  DeliveryItemTrans.retrieve(tran, prod, itm)){
			qtySold += tr.getQuantity();
		}
		remQty = qtyInTruck - qtySold;
		
		if(remQty<item.getQuantity()){
			System.out.println("has a record....");
			return true;
		}
		}
		
		return false;
	}
	
	public void closeSoldItemWindow(){
		clickItemReceipt(getReceiptData());
	}
	
	public void clickItemReceipt(DeliveryItemReceipt rec){
		setReceiptData(rec);
		setSoldDate(rec.getDateTrans());
		setSoldCustomerId(rec.getCustomer().getCustomerid());
		setSoldPrice(rec.getTotalAmount());
		setSoldBalance(rec.getBalanceAmount());
		setSoldDiscount(rec.getDiscountAmount());
		setSoldPaymentStatus(ReceiptStatus.statusName(rec.getPaymentStatus()));
		setSoldQty(rec.getQuantity());
		setSoldRemarks(rec.getRemarks());
		setSoldReceiptNo(rec.getReceiptNo());
		setSoldChargeAmount(rec.getDeliveryChargeAmount());
		setSoldDownPayment(rec.getDownPayment());
		int status = rec.getStatus();
		setSoldStatus(ReceiptStatus.statusName(status));
		
		if(ReceiptStatus.NEW.getId()==status){
			if(checkSoldItemIsPosted(rec)){
				setIsSoldItemPosted(false);
			}else{
				setIsSoldItemPosted(true);
			}
		}else if(ReceiptStatus.POSTED.getId()==status){
			setIsSoldItemPosted(true);
		}
	}
	
	public boolean checkSoldItemIsPosted(DeliveryItemReceipt rec){
		
		DeliveryItemReceipt rpt = new DeliveryItemReceipt();
		rpt.setId(rec.getId());
		rpt.setIsActive(1);
		rpt.setDateTrans(rec.getDateTrans());
		
		DeliveryItemTrans trn = new DeliveryItemTrans();
		trn.setIsActive(1);
		trn.setDateTrans(rec.getDateTrans());
		boolean isNew=false;
		boolean isPost=false;
		int cnt = 0;
		double soldQtyCnt = 0d;
		double amountPosted = 0d;
		double amountPurchasedTotal = 0d;
		double amountBalance = 0d;
		double amntGrandTotal = 0d;
		DeliveryItemReceipt tmpRec = null;
		for(DeliveryItemTrans tran : DeliveryItemTrans.retrieve(trn,rpt)){
			if(DeliveryStatus.NEW_SOLD_ITEM.getId()==tran.getStatus()){
				isNew=true;
			}else if(DeliveryStatus.POSTED_SOLD_ITEM.getId()==tran.getStatus()){
				isPost=true;
				soldQtyCnt += tran.getQuantity();
				amountPosted += tran.getSellingPrice();
			}
			cnt++;
			 if(tmpRec==null){
				 tmpRec = tran.getReceipt(); //fresh from db
			 }
		}
		
		if(tmpRec==null){
			System.out.println("cut the execution ..... receipt is now");
			return false;
		}
		
		setSoldQty(soldQtyCnt); // total items
		amountPurchasedTotal = tmpRec.getTotalAmount(); //old total selling amount
		
		//calculate the latest amount balance
		amntGrandTotal = amountPosted + tmpRec.getDeliveryChargeAmount();
		setSoldGrandTotal(amntGrandTotal);
		amountBalance = amntGrandTotal - (tmpRec.getDiscountAmount() + tmpRec.getDownPayment());
		if(amountBalance==tmpRec.getBalanceAmount()){
			// no changes
		}else{
			if(amountBalance>0){
				tmpRec.setBalanceAmount(amountBalance);
				tmpRec.setPaymentStatus(ReceiptStatus.PARTIAL.getId());
				setSoldPaymentStatus(ReceiptStatus.PARTIAL.getName());
				setSoldBalance(amountBalance);
			}else{
				tmpRec.setPaymentStatus(ReceiptStatus.FULL.getId());
				setSoldPaymentStatus(ReceiptStatus.FULL.getName());
			}
		}
		/**
		 * check if need to update the total sold item amount
		 */
		if(amountPosted == amountPurchasedTotal){
			// no changes made
		}else{
			tmpRec.setTotalAmount(amountPosted);
			setSoldPrice(amountPosted);
		}
		
		//locking the drop down box for customer selection
		if(cnt>0){
			setCustomerNameLock(true);
		}else{
			setCustomerNameLock(false);
		}
		
		if(isNew && isPost){
			if(tmpRec.getStatus()==ReceiptStatus.POSTED.getId()){
				tmpRec.setStatus(ReceiptStatus.NEW.getId());
				tmpRec.setUserDtls(Login.getUserLogin().getUserDtls());
				tmpRec.save();
				System.out.println("saving isNew && isPost incomplete");
			}
			return false; //incomplete
		}else if(!isNew && isPost){
			if(tmpRec.getStatus()==ReceiptStatus.NEW.getId()){
				//tmpRec.setStatus(ReceiptStatus.POSTED.getId());
				tmpRec.setUserDtls(Login.getUserLogin().getUserDtls());
				tmpRec.save();
				System.out.println("saving !isNew && isPost complete");
			}
			return true; //complete
		}else if(isNew && !isPost){
			if(tmpRec.getStatus()==ReceiptStatus.POSTED.getId()){
				tmpRec.setStatus(ReceiptStatus.NEW.getId());
				tmpRec.setUserDtls(Login.getUserLogin().getUserDtls());
				tmpRec.save();
				System.out.println("saving isNew && !isPost incomplete");
			}
			return false; //incomplete
		}else if(!isNew && !isPost){
			if(tmpRec.getStatus()==ReceiptStatus.POSTED.getId()){
				tmpRec.setStatus(ReceiptStatus.NEW.getId());
				tmpRec.setUserDtls(Login.getUserLogin().getUserDtls());
				tmpRec.save();
				System.out.println("saving !isNew && !isPost incomplete");
			}
			return false; //incomplete
		}
		return false;
	}
	
	public void calculateBilling(){
		double amount = 0d;
		double groundTotal = (getSoldPrice() + getSoldChargeAmount()) - getSoldDiscount();
		amount = groundTotal - getSoldDownPayment();
		setSoldGrandTotal(groundTotal);
		setSoldBalance(amount);
		
	}
	
	
	public void clickReceiptDetails(DeliveryItemReceipt rec){
		clearSoldItemFields();
		setReceiptData(rec);
		loadSoldProduct();
		loadSoldItem();
	}
	
	public void loadSoldItem(){
		if(getReceiptData()!=null){
			DeliveryItemTrans tran = new DeliveryItemTrans();
			tran.setIsActive(1);
			DeliveryItemReceipt recpt = new DeliveryItemReceipt();
			recpt.setId(getReceiptData().getId());
			recpt.setIsActive(1);
			itemSolds = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
			for(DeliveryItemTrans trn :  DeliveryItemTrans.retrieve(tran,recpt)){
				
				/*Product prod = new Product();
				prod = Product.retrieve(trn.getProduct().getProdid()+"");*/
				ProductProperties prop = new ProductProperties();
				prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
				trn.getProduct().setProductProperties(prop);
				itemSolds.add(trn);
				
			}
			Collections.reverse(itemSolds);
			
		}
	}
	
	public void clickItemSold(DeliveryItemTrans trans){
		setSoldData(trans);
		setSoldDate(trans.getDateTrans());
		loadSoldProduct();
		setSoldSearchProduct(trans.getProduct().getProductProperties().getProductname());
		setSoldProductId(trans.getProduct().getProdid());
		setSoldItemPrice(trans.getSellingPrice());
		setSoldItemQty(trans.getQuantity());
		setSoldItemRemarks(trans.getRemarks());
		setSoldItemStatus(DeliveryStatus.statusName(trans.getStatus()));
		setSoldItemQtyInDelivery(remainingQtyOnTruck(trans));
		
		Product prod = new Product();
		prod.setProdid(trans.getProduct().getProdid());
		prod.setIsactiveproduct(1);
		
		ProductPricingTrans price = new ProductPricingTrans();
		price.setIsActiveprice(1);
		price = ProductPricingTrans.retrievePrice(price,prod).get(0);
		setSoldItemSellingPrice(Double.valueOf(price.getSellingprice()+""));
		
	}
	
	public double remainingQtyOnTruck(DeliveryItemTrans trans){
		double qtyInSold = 0d;
		double qtyInTruck = 0d;
		
		Product prod = new Product();
		prod.setProdid(trans.getProduct().getProdid());
		prod.setIsactiveproduct(1);
		
		DeliveryItem item = new DeliveryItem();
		//item.setId(trans.getDeliveryItem().getId());
		item.setIsActive(1);
		item.setDateTrans(trans.getDateTrans());
		item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
		System.out.println("remainingQtyOnTruck processing delivery item");
		//item = DeliveryItem.retrieve(item).get(0);
		//qtyInTruck = item.getQuantity();
		for(DeliveryItem itm : DeliveryItem.retrieve(item,prod)){
			qtyInTruck += itm.getQuantity();
		}
		System.out.println("===============CHECK QTY DELIVER "+ qtyInTruck +"==========");
		
		DeliveryItemTrans tr = new DeliveryItemTrans();
		tr.setDateTrans(trans.getDateTrans());
		tr.setIsActive(1);
		item = new DeliveryItem();
		item.setIsActive(1);
		item.setDateTrans(trans.getDateTrans());
		for(DeliveryItemTrans trn : DeliveryItemTrans.retrieve(tr,prod,item)){
			qtyInSold += trn.getQuantity();
		}
		System.out.println("done remainingQtyOnTruck processing DeliveryItemTrans item");
		System.out.println("===============CALCULATE==========");
		System.out.println("===============QTY USE "+ qtyInSold +"==========");
		System.out.println("===============QTY DELIVER "+ qtyInTruck +"==========");
		System.out.println("===============CALCULATE==========");
		qtyInSold = qtyInTruck - qtyInSold;
		
		return qtyInSold;
	}
	
	public void soldNewDeliveryReceipt(){
		clearReceiptFields();
	}
	
	public void soldNewDeliveryItem(){
		clearSoldItemFields();
	}
	
	public void loadSoldItemDelivery(){
		loadSoldCustomer();
		loadReceipts();
		
		/**
		 * set disabled by default
		 * Charge amount
		 * Discount amount
		 * Downpayment amount
		 */
		setIsSoldItemPosted(true);
		
		setCustomerNameLock(false);
	}
	
	/**
	 * 
	 * This method has been moved to Payment.java
	 */
	@Deprecated
	private void customerpaybal(Customer customer, double balance, double paidAmount, PaymentTransactionType type, String remarks){
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
			trans.save();
			
		}
		
	}
	
	public void saveSoldReceipt(){
		
		
		if(getSoldCustomerId()!=0){
			
		
		if(Login.checkUserStatus()){
			
			DeliveryItemReceipt rec = new DeliveryItemReceipt();
			boolean isOk = true;
			if(getReceiptData()!=null){
				rec = getReceiptData();
				
				if(rec.getStatus()==ReceiptStatus.POSTED.getId()){
					isOk = false;
				}
				
			}else{
				rec.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
				rec.setStatus(ReceiptStatus.NEW.getId());
				rec.setIsActive(1);
				rec.setReceiptNo(getSoldReceiptNo());
			}
			
			if(isOk){
				
				rec.setTotalAmount(getSoldPrice());
				rec.setBalanceAmount(getSoldBalance());
				rec.setDiscountAmount(getSoldDiscount());
				rec.setQuantity(getSoldQty());
				rec.setRemarks(getSoldRemarks());
				rec.setDeliveryChargeAmount(getSoldChargeAmount());
				rec.setDownPayment(getSoldDownPayment());
				
				//check payment
				if(getSoldBalance()==0){
					rec.setPaymentStatus(ReceiptStatus.FULL.getId());
				}else{
					rec.setPaymentStatus(ReceiptStatus.PARTIAL.getId());
				}
				
				Customer cus = new Customer();
				cus.setCustomerid(getSoldCustomerId());
				rec.setCustomer(cus);
				
				rec.setUserDtls(Login.getUserLogin().getUserDtls());
				
				rec.save();
				clearReceiptFields();
				loadReceipts();
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been successfully saved.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: This transaction was already processed. Editing is not allowed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Please select customer.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
	}
	
	public void postSoldReceipt(){
		if(Login.checkUserStatus()){
			
			
			//check if there is receipt selected
			if(getReceiptData()!=null){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: We cannot posting this receipt unless you save it first. Please click save button first before posting.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
			
			int cntSameDay = 0;
			int cntPastDay = 0;
			int cntPosted = 0;
			int cntItemNotPosted = 0;
			if(getSelectedReceipts()!=null && getSelectedReceipts().size()>0){
				
				for(DeliveryItemReceipt rpt : getSelectedReceipts()){
					
					if(ReceiptStatus.POSTED.getId()==rpt.getStatus()){
						cntPosted++;
					}else{
					
					if(checkIfAllPostedItems(rpt)){	//checking if all item is already posted
						
					if(DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(rpt.getDateTrans())){
						
						if(rpt.getBalanceAmount()==0){
							rpt.setPaymentStatus(ReceiptStatus.FULL.getId());
							
							//saving information to customer payment
							//customerpaybal(rpt.getCustomer(), rpt.getBalanceAmount(), rpt.getDownPayment(), PaymentTransactionType.DELIVERY, "Full payment in Delivery");
							Payment.customerPayment(rpt.getCustomer(), rpt.getBalanceAmount(), rpt.getDownPayment(), PaymentTransactionType.DELIVERY, "Full payment in Delivery",getSelectedReceipts().get(0).getReceiptNo());
							
							MoneyIO io = new MoneyIO();
							io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
							io.setDescripion("Full payment in delivery. Paid by " + getSelectedReceipts().get(0).getCustomer().getFullname());
							io.setTransType(MoneyStatus.INCOME.getId());
							io.setInAmount(rpt.getDownPayment());
							io.setUserDtls(Login.getUserLogin().getUserDtls());
							io.setReceiptNo(getSelectedReceipts().get(0).getReceiptNo());
							io.setCustomer(getSelectedReceipts().get(0).getCustomer());
							
							io.save(io);
						}else{
							rpt.setPaymentStatus(ReceiptStatus.PARTIAL.getId());
							
							MoneyIO io = new MoneyIO();
							io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
							io.setTransType(MoneyStatus.INCOME.getId());
							io.setUserDtls(Login.getUserLogin().getUserDtls());
							
							
							//saving information to customer payment
							if(rpt.getDownPayment()==0){
								//customerpaybal(rpt.getCustomer(), rpt.getBalanceAmount(), rpt.getDownPayment(),PaymentTransactionType.DELIVERY, "No downpayment in Delivery. Payable amount " + rpt.getBalanceAmount());
								Payment.customerPayment(rpt.getCustomer(), rpt.getBalanceAmount(), rpt.getDownPayment(),PaymentTransactionType.DELIVERY, "No downpayment in Delivery. Payable amount " + rpt.getBalanceAmount(),getSelectedReceipts().get(0).getReceiptNo());
								io.setDescripion("No downpayment in delivery. Payable by " + getSelectedReceipts().get(0).getCustomer().getFullname() + ". Payable amount " + rpt.getBalanceAmount());
								io.setInAmount(0.0);
								io.setReceiptNo(getSelectedReceipts().get(0).getReceiptNo());
								io.setCustomer(getSelectedReceipts().get(0).getCustomer());
								
								io.save(io);
							}else{
								//customerpaybal(rpt.getCustomer(), rpt.getBalanceAmount(), rpt.getDownPayment(),PaymentTransactionType.DELIVERY, "Partial downpayment in Delivery. Payable amount " + rpt.getBalanceAmount());
								Payment.customerPayment(rpt.getCustomer(), rpt.getBalanceAmount(), rpt.getDownPayment(),PaymentTransactionType.DELIVERY, "Partial downpayment in Delivery. Payable amount " + rpt.getBalanceAmount(),getSelectedReceipts().get(0).getReceiptNo());
								io.setDescripion("Partial downpayment in delivery. Paid by " + getSelectedReceipts().get(0).getCustomer().getFullname() + ". Payable amount " + rpt.getBalanceAmount());
								io.setInAmount(rpt.getDownPayment());
								io.setReceiptNo(getSelectedReceipts().get(0).getReceiptNo());
								io.setCustomer(getSelectedReceipts().get(0).getCustomer());
								
								io.save(io);
							}
							
						}
						
						rpt.setStatus(ReceiptStatus.POSTED.getId());
						rpt.setUserDtls(Login.getUserLogin().getUserDtls());
						rpt.save();
						
						cntSameDay++;
					}else{
						cntPastDay++;
					}
					
					}else{//end check if all item has been posted
						cntItemNotPosted++;
					}
					
					}
					
				}
				
				if(cntSameDay>0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, (cntSameDay==1? "1 receipt has " : cntSameDay + " receipts have ") + "been processed for posting", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "No receipt has been processed for posting", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
				if(cntPastDay>0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, (cntPastDay==1? "1 receipt was " : cntPastDay + " receipts were ") + "not successfully posting. This receipt is already expired.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
				if(cntPosted>0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, (cntPosted==1? "1 receipt was " : cntPosted + " receipts were ") + "not successfully posting. This receipt is already posted. Reposting is not allowed.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
				if(cntItemNotPosted>0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, (cntItemNotPosted==1? "1 receipt was " : cntItemNotPosted + " receipts were ") + "not successfully posting. The item for this receipt is not yet posted or there is no item yet selected.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Please select a receip to post.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}//end receipt selected checking
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public boolean checkIfAllPostedItems(DeliveryItemReceipt rec){
		
		DeliveryItemReceipt rpt = new DeliveryItemReceipt();
		rpt.setId(rec.getId());
		rpt.setIsActive(1);
		rpt.setDateTrans(rec.getDateTrans());
		
		DeliveryItemTrans trn = new DeliveryItemTrans();
		trn.setIsActive(1);
		trn.setDateTrans(rec.getDateTrans());
		
		boolean isNew=false;
		boolean isPost=false;
		for(DeliveryItemTrans tran : DeliveryItemTrans.retrieve(trn,rpt)){
			if(DeliveryStatus.NEW_SOLD_ITEM.getId()==tran.getStatus()){
				isNew=true;
			}else if(DeliveryStatus.POSTED_SOLD_ITEM.getId()==tran.getStatus()){
				isPost=true;
			}
		}
		
		if(isNew && isPost){
			return false;
		}else if(!isNew && isPost){
			return true;
		}else if(!isNew && !isPost){
			return false; // no item has been selected
		}else if(isNew && !isPost){
			return false;
		}
		
		return false;
	}
	
	
	public void clearReceiptFields(){
		setReceiptData(null);
		setSoldDate(null);
		setSoldCustomerId(0);
		setSoldPrice(0);
		setSoldBalance(0);
		setSoldDiscount(0);
		setSoldStatus(ReceiptStatus.NEW.getName());
		setSoldPaymentStatus(ReceiptStatus.FULL.getName());
		setSoldQty(0);
		setSoldRemarks(null);
		setSoldReceiptNo(null);
		setCustomerNameLock(false);
		setIsSoldItemPosted(true);
		setSoldGrandTotal(0);
		setSoldDownPayment(0);
		setSoldChargeAmount(0);
	}
	
	public void saveSoldDeliveryItem(){
		
			if(Login.checkUserStatus()){
				
				if(getReceiptData().getStatus()==ReceiptStatus.POSTED.getId()){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "The receipt for this transaction was already processed. Adding/Editing is no longer allowed.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
				
				
				if(getReceiptData()!=null && DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(getReceiptData().getDateTrans())){
				
				DeliveryItemTrans tran = new DeliveryItemTrans();
				boolean isOk = true; 
				if(getSoldData()!=null){
					tran = getSoldData();
					
					if(tran.getStatus()==DeliveryStatus.POSTED_SOLD_ITEM.getId()){
						isOk=false;
					}
					
				}else{
					tran.setStatus(DeliveryStatus.NEW_SOLD_ITEM.getId());
					tran.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
				}
				
				if(isOk){
					
					if(getSoldItemQty()>0){
					tran.setIsActive(1);
					tran.setQuantity(getSoldItemQty());
					tran.setSellingPrice(getSoldItemPrice());
					tran.setRemarks(getSoldItemRemarks());
					
					tran.setReceipt(getReceiptData());
					tran.setCustomer(getReceiptData().getCustomer());
					
					Product product = new Product();
					product.setProdid(getSoldProductId());
					product.setIsactiveproduct(1);
					tran.setProduct(product);
					
					DeliveryItem item = new DeliveryItem();
					item.setIsActive(1);
					item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
					item.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
					item = DeliveryItem.retrieve(item, product).get(0);
					System.out.println("Saving item id " + item.getId());
					tran.setDeliveryItem(item);
					
					tran.setUserDtls(Login.getUserLogin().getUserDtls());
					tran.save();
					loadSoldItem();
					clearSoldItemFields();
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been successfully saved.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: You can't save if the quantity is zero.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Data has been processed already. Editing is not allowed", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
				
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: This transaction is out of date adding/editing is now invalid.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
				}
				
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		
			
		
	}
	
	public void clearSoldItemFields(){
		setSoldSearchProduct(null);
		setSoldData(null);
		setSoldDate(null);
		setSoldProductId(0);
		setSoldItemPrice(0);
		setSoldItemQty(0);
		setSoldItemRemarks(null);
		setSoldItemStatus(DeliveryStatus.NEW_SOLD_ITEM.getName());
		setSoldItemQtyInDelivery(0);
	}
	
	public void deleteSoldItem(DeliveryItemTrans trans){
		if(Login.checkUserStatus()){
			
			if(trans.getStatus()==DeliveryStatus.NEW_SOLD_ITEM.getId() && DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(trans.getDateTrans())){
				
				trans.delete();
				loadSoldItem();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been successfully deleted.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            
			}else{
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: The data has been processed already. Deletion is not allowed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void loadReceipts(){
		
		DeliveryItemReceipt rec = new DeliveryItemReceipt();
		rec.setIsActive(1);
		rec.setBetween(true);
		rec.setDateFrom(getSoldDateFrom());
		rec.setDateTo(getSoldDateTo());
		
		//receipts = DeliveryItemReceipt.retrieve(rec);
		double purchased=0d, balance = 0d;
		receipts = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
		for(DeliveryItemReceipt rpt : DeliveryItemReceipt.retrieve(rec)){
			if(ReceiptStatus.CANCELLED.getId()!=rpt.getStatus()){
				purchased += rpt.getTotalAmount();
				balance += rpt.getBalanceAmount();
				receipts.add(rpt);
			}
		}
		setSoldItemGrandTotalPurchased(Currency.formatAmount(purchased+""));
		setSoldItemGrandTotalBalance(Currency.formatAmount(balance+""));
		Collections.reverse(receipts);
		
	}
	
	public void soldProductItemDetails(){
		if(getSoldProductId()!=0){
			Product prod = new Product();
			prod.setProdid(getSoldProductId());
			prod.setIsactiveproduct(1);
			
			ProductPricingTrans price = new ProductPricingTrans();
			price.setIsActiveprice(1);
			System.out.println("processing price");
			price = ProductPricingTrans.retrievePrice(price,prod).get(0);
			System.out.println("done processing price");
			setSoldItemSellingPrice(Double.valueOf(price.getSellingprice()+""));
			
			DeliveryItem item = new DeliveryItem();
			item.setIsActive(1);
			item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
			item.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			System.out.println("soldProductItemDetails processing delivery item");
			item = DeliveryItem.retrieve(item, prod).get(0);
			System.out.println("done soldProductItemDetails processing delivery item");
			
			DeliveryItemTrans trans = new DeliveryItemTrans();
			trans.setDeliveryItem(item);
			trans.setProduct(item.getProduct());
			trans.setDateTrans(item.getDateTrans());
			trans.setIsActive(1);
			
			setSoldItemQtyInDelivery(remainingQtyOnTruck(trans));
		}
	}
	
	public void loadSoldProduct(){
		DeliveryItem item = new DeliveryItem();
		item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
		item.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		item.setIsActive(1);
		int itemcnt =1; 
		soldProducts = new ArrayList<>();
		Map<Long, Product> productData = Collections.synchronizedMap(new HashMap<Long, Product>());
		for(DeliveryItem itm : DeliveryItem.retrieve(item)){
			boolean isNotExist = false;
			if(productData!=null && productData.size()>0){
				long id = itm.getProduct().getProdid();
				if(!productData.containsKey(id)){
					isNotExist = true;
					productData.put(itm.getProduct().getProdid(), itm.getProduct());
				}
			}else{
				productData.put(itm.getProduct().getProdid(), itm.getProduct());
				isNotExist = true;
			}
			
			if(isNotExist){
			Product prod = new Product();
			prod.setProdid(itm.getProduct().getProdid());
			prod.setIsactiveproduct(1);
			ProductProperties prop = new ProductProperties();
			prop.setIsactive(1);
			if(getSoldSearchProduct()!=null){
				prop.setProductname(getSoldSearchProduct());
			}
			int prdcnt=1;
			for(Product p : Product.retrieve(prod,prop)){
			//Product p = Product.retrieve(prod,prop).get(0);
				prop = ProductProperties.properties(p.getProductProperties().getPropid()+"");
				soldProducts.add(new SelectItem(p.getProdid(), prop.getProductname() + " per " + prop.getUom().getUomname()));
				System.out.println("itemcnt "+ itemcnt + " loadSoldProduct prdcnt " + prdcnt++);
			}
			itemcnt++;
			}
			
			
		}
	}
	
	public void calculateSoldItemPrice(){
		double qty = 0d;
		if(getSoldData()!=null){
			qty = getSoldItemQtyInDelivery() + getSoldData().getQuantity();
		}
		
		double price = 0d;
		if(qty==0){
		
		if(getSoldItemQty()<=getSoldItemQtyInDelivery()){
			price = getSoldItemQty() * getSoldItemSellingPrice();
			setSoldItemPrice(price);
		}else{
			price = getSoldItemQtyInDelivery() * getSoldItemSellingPrice();
			setSoldItemPrice(price);
			setSoldItemQty(getSoldItemQtyInDelivery());
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Please choose quantity lower or equal to Qunatity Remaining on Delivery Truck.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		}else{
			
			if(getSoldItemQty()<=qty){
				price = getSoldItemQty() * getSoldItemSellingPrice();
				setSoldItemPrice(price);
			}else{
				price = qty * getSoldItemSellingPrice();
				setSoldItemPrice(price);
				setSoldItemQty(qty);
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Please choose quantity lower or equal to Qunatity Remaining on Delivery Truck.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}
	}
	////////////////////////////////////////////////////////////////SOLD PRODUCT TAB
	
	public void loadSoldProductOnly(){
		soldProductItem = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
		
		Map<String, DeliveryItemTrans> product = Collections.synchronizedMap(new HashMap<String, DeliveryItemTrans>());
		
		Map<Long, Map<String, DeliveryItemTrans>> products = Collections.synchronizedMap(new HashMap<Long,Map<String, DeliveryItemTrans>>());
		
		DeliveryItemTrans tran = new DeliveryItemTrans();
		tran.setIsActive(1);
		tran.setStatus(DeliveryStatus.POSTED_SOLD_ITEM.getId());
		tran.setBetween(true);
		tran.setDateFrom(getSoldProductItemDateFrom());
		tran.setDateTo(getSoldProductItemDateTo());
		
		//filtering product
		for(DeliveryItemTrans trn : DeliveryItemTrans.retrieve(tran)){
			
			/*if(product!=null && product.size()>0){
				long id = trn.getProduct().getProdid();
				
				if(product.containsKey(id)){
					double newQty = product.get(id).getQuantity() + trn.getQuantity();
					double sellingAmnt = product.get(id).getSellingPrice() + trn.getSellingPrice();
					product.get(id).setQuantity(newQty);
					product.get(id).setSellingPrice(sellingAmnt);
				}else{
					ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
					trn.getProduct().setProductProperties(prop);
					product.put(trn.getProduct().getProdid(), trn);
				}
				
				
			}else{
				ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
				trn.getProduct().setProductProperties(prop);
				product.put(trn.getProduct().getProdid(), trn);
			}*/
			
			if(products!=null && products.size()>0){
				
				long id = trn.getProduct().getProdid();
				
				if(products.containsKey(id)){
					
					if(products.get(id).containsKey(trn.getDateTrans())){
						
						double newQty = products.get(id).get(trn.getDateTrans()).getQuantity() + trn.getQuantity();
						double sellingAmnt = products.get(id).get(trn.getDateTrans()).getSellingPrice() + trn.getSellingPrice();
						products.get(id).get(trn.getDateTrans()).setQuantity(newQty);
						products.get(id).get(trn.getDateTrans()).setSellingPrice(sellingAmnt);
						
					}else{
						
						ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
						trn.getProduct().setProductProperties(prop);
						products.get(id).put(trn.getDateTrans(), trn);
						
					}
					
				}else{
					product = Collections.synchronizedMap(new HashMap<String, DeliveryItemTrans>());
					ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
					trn.getProduct().setProductProperties(prop);
					product.put(trn.getDateTrans(), trn);
					products.put(trn.getProduct().getProdid(), product);
				}
				
			}else{
				
				ProductProperties prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
				trn.getProduct().setProductProperties(prop);
				product.put(trn.getDateTrans(), trn);
				products.put(trn.getProduct().getProdid(), product);
			}
			
			
		}
		double grandTotal = 0d;
		double grandPrice = 0d;
		double qtyTotal = 0d;
		/*for(DeliveryItemTrans tn : product.values()){
			grandTotal += tn.getSellingPrice();
			soldProductItem.add(tn);
		}*/
		
		//Map<Long, Map<String, DeliveryItemTrans>> treeMap = new TreeMap<Long, Map<String, DeliveryItemTrans>>(products);
		Map<Long, DeliveryItemTrans> unSort = Collections.synchronizedMap(new HashMap<Long, DeliveryItemTrans>());
		for(Long id : products.keySet() ){
			for(DeliveryItemTrans tn : products.get(id).values()){
				unSort.put(tn.getId(), tn);
			}
			
		}
		Map<Long, DeliveryItemTrans> sort = new TreeMap<Long, DeliveryItemTrans>(unSort);
		for(DeliveryItemTrans tn : sort.values()){
			grandTotal += tn.getSellingPrice();
			grandPrice += tn.getDeliveryItem().getSellingPrice();
			qtyTotal += tn.getQuantity();
			soldProductItem.add(tn);
		}
		
		
		
		setSoldProductItemPriceGrandTotal(Currency.formatAmount(grandPrice+""));
		setSoldProductItemQtyGrandTotal(qtyTotal);
		setSoldProductItemGrandTotal(Currency.formatAmount(grandTotal+""));
		Collections.reverse(soldProductItem);//from new to old
		
		
		
	}
	
	////////////////////////////////////////DELIVERY RETURN TAB
	
	public void loadReturnDelivery(){
		
		returnItems = Collections.synchronizedList(new ArrayList<DeliveryItem>());
		DeliveryItem item = new DeliveryItem();
		item.setIsActive(1);
		item.setStatus(DeliveryStatus.POSTED_FOR_DELIVERY.getId());
		item.setBetween(true);
		item.setDateFrom(getReturnDateFrom());
		item.setDateTo(getReturnDateTo());
		Map<Long, Map<String, DeliveryItem>> products = Collections.synchronizedMap(new HashMap<Long,Map<String, DeliveryItem>>());
		Map<String, DeliveryItem> product = Collections.synchronizedMap(new HashMap<String, DeliveryItem>());
		/*for(DeliveryItem it : DeliveryItem.retrieve(item)){
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
			
			
		}*/
		
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
		Collections.reverse(returnItems);
		
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
	
	public void loadRented(){
		rentedItems = Collections.synchronizedList(new ArrayList<RentedBottleTrans>());
		
		RentedBottleTrans tran = new RentedBottleTrans();
		tran.setIsActive(1);
		tran.setBetween(true);
		tran.setDateFrom(getRentDateFrom());
		tran.setDateTo(getRentDateTo());
		
		if(getRentDescription()!=null && !getRentDescription().isEmpty()){
			Customer cus = new Customer();
			cus.setIsactive(1);
			cus.setFullname(getRentDescription().replace("--", ""));
			rentedItems = RentedBottleTrans.retrieve(tran,cus);
		}else{
			rentedItems = RentedBottleTrans.retrieve(tran);
		}
		
		Collections.reverse(rentedItems);
		
	}
	
	public void newRented(){
		clearRentedFields();
	}
	
	public void deleteRentItem(RentedBottleTrans rent){
		if(Login.checkUserStatus()){
			
			if(RentedStatus.NEW.getId()==rent.getStatus()){
				rent.delete();
				loadRented();
				clearRentedFields();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Transaction has been successfully processed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: This transaction has been processed already. Deletion is no longer allowed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void saveRented(){
		if(Login.checkUserStatus()){
			
			boolean isNotEmpty = true;
			if(getRentCustomerId()==0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Customer is not yet selected.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
				isNotEmpty = false;
			}
			if(getRentProductId()==0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Product is not yet selected.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
				isNotEmpty = false;
			}
			if(getRentUomId()==0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: UOM is not yet selected.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
				isNotEmpty = false;
			}
			if(getRentQuantity()==0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Quantity is not yet specified.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
				isNotEmpty = false;
			}
			if(getRentChargeitem()==0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Charge Per Item is not yet specified.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
				isNotEmpty = false;
			}
			
			RentedBottleTrans tran = new RentedBottleTrans();
			boolean isOk = true; 
			if(getRentedData()!=null){
				tran = getRentedData();
				
				if(DateUtils.getCurrentDateYYYYMMDD().equalsIgnoreCase(tran.getDateTrans()) && tran.getStatus()==RentedStatus.NEW.getId()){
					
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Transaction already passed. Editing is no longer allowed.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				
				if(tran.getStatus()==RentedStatus.POSTED.getId()){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: This transaction is already Posted. Editing is no longer allowed.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
					isOk = false;
				}
				
			}else{
				tran.setDateTrans(getRetDateTrans()==null? DateUtils.getCurrentDateYYYYMMDD() :  getRetDateTrans());
			}
			
			if(isOk && isNotEmpty){
				
				tran.setQuantity(getRentQuantity());
				tran.setChargeitem(getRentChargeitem());
				tran.setTotalAmount(getRentTotalAmount());
				tran.setPaidAmount(getRentPaidAmount());
				tran.setBalance(getRentBalance());
				tran.setDeposit(getRentDeposit());
				tran.setStatus(RentedStatus.NEW.getId());
				
				Customer customer = new Customer();
				customer.setCustomerid(getRentCustomerId());
				tran.setCustomer(customer);
				
				UOM uom = new UOM();
				uom.setUomid(getRentUomId());
				tran.setUom(uom);
				
				ProductProperties prop = new ProductProperties();
				prop.setPropid(getRentProductId());
				tran.setProductProperties(prop);
				
				tran.setIsActive(1);
				tran.setUserDtls(Login.getUserLogin().getUserDtls());
				
				RentedBottle bottle = addRentedBottle(tran);
				
				if(bottle!=null){
					tran.setRentedBottle(bottle);
					tran.save();
					loadRented();
					clearRentedFields();
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Transaction has been successfully saved.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Something went wrong. Please login again.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
			
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void postRented(){
		if(Login.checkUserStatus()){
			
			if(getSelectedRentedTrans()!=null && getSelectedRentedTrans().size()>0){
				int cnt = 0;
				for(RentedBottleTrans tran : getSelectedRentedTrans()){
					
					if(RentedStatus.NEW.getId()==tran.getStatus()){
						tran.setStatus(RentedStatus.POSTED.getId());
						tran.setUserDtls(Login.getUserLogin().getUserDtls());
						tran.save();
						addRentedBottle(tran);
						cnt++;
					}
				}
				if(cnt>0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,  (cnt==1? "1 item has " : cnt + " items have ") + "been successfully posted.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Please select an item for posting.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	/**
	 * Add rented bottle if not exist
	 * @param tran
	 * @return
	 */
	public RentedBottle addRentedBottle(RentedBottleTrans tran){
		
		Customer cus = new Customer();
		cus.setCustomerid(tran.getCustomer().getCustomerid());
		cus.setIsactive(1);
		
		ProductProperties prop = new ProductProperties();
		prop.setPropid(tran.getProductProperties().getPropid());
		prop.setIsactive(1);
		
		UOM uom = new UOM();
		uom.setUomid(tran.getUom().getUomid());
		uom.setIsactive(1);
		
		RentedBottle bottle = null;
		if(tran.getRentedBottle()!=null && tran.getRentedBottle().getId()!=0){
			
			bottle = RentedBottle.retrieve(tran.getRentedBottle().getId()+"");
			
		}else{
			
			try{bottle = RentedBottle.retrieve(cus, prop, uom).get(0);}catch(IndexOutOfBoundsException e){bottle=null;}
			
		}
		
		if(bottle!=null && RentedStatus.POSTED.getId()==tran.getStatus() ){
			
			double oldBal=0d, oldQty=0d, oldPaid=0d, oldDeposit=0d;
			String oldDate = DateUtils.getCurrentDateYYYYMMDD();
			oldDate = bottle.getCurrentDateTrans();
			oldBal = bottle.getCurrentBalance();
			oldQty = bottle.getCurrentQty();
			oldPaid = bottle.getCurrentPaidAmount();
			oldDeposit = bottle.getCurrentDeposit();
			
			bottle.setCurrentDateTrans(tran.getDateTrans());
			
			double newBal = (oldBal + tran.getBalance());// - tran.getPaidAmount(); 
			double newQty = oldQty + tran.getQuantity();
			double newDeposit = oldDeposit + tran.getDeposit();
			
			bottle.setCurrentQty(newQty);
			bottle.setCurrentPaidAmount(tran.getPaidAmount());
			bottle.setCurrentBalance(newBal);
			bottle.setCurrentDeposit(newDeposit);
			
			bottle.setPrevDateTrans(oldDate);
			bottle.setPrevBalance(oldBal);
			bottle.setPrevQty(oldQty);
			bottle.setPrevPaidAmount(oldPaid);
			bottle.setPrevDeposit(oldDeposit);
			
			bottle.setUserDtls(Login.getUserLogin().getUserDtls());
			
			bottle.save();
			
			//saving information to customer payment
			if(tran.getBalance()>0){
				if(tran.getPaidAmount()==0){
					//customerpaybal(tran.getCustomer(), tran.getBalance(), tran.getPaidAmount(), PaymentTransactionType.RENTED, "Rented item. No Payment. Payable amount "+ tran.getBalance());
					Payment.customerPayment(tran.getCustomer(), tran.getBalance(), tran.getPaidAmount(), PaymentTransactionType.RENTED, "Rented item. No Payment. Payable amount "+ tran.getBalance(),"");
				}else{
					//customerpaybal(tran.getCustomer(), tran.getBalance(), tran.getPaidAmount(), PaymentTransactionType.RENTED, "Rented item. Partial Payment. Remaining payable amount " + tran.getBalance());
					Payment.customerPayment(tran.getCustomer(), tran.getBalance(), tran.getPaidAmount(), PaymentTransactionType.RENTED, "Rented item. Partial Payment. Remaining payable amount " + tran.getBalance(),"");
				}
			}else{
				//customerpaybal(tran.getCustomer(), tran.getBalance(), tran.getPaidAmount(), PaymentTransactionType.RENTED, "Rented item. Full Payment. Paid amount "+ tran.getPaidAmount());
				Payment.customerPayment(tran.getCustomer(), tran.getBalance(), tran.getPaidAmount(), PaymentTransactionType.RENTED, "Rented item. Full Payment. Paid amount "+ tran.getPaidAmount(),"");
			}
			
			//Money transactions
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			if(tran.getPaidAmount()==0){
				io.setDescripion("Rented Item. Not Paid by " + tran.getCustomer().getFullname() + ". Payable amount " + tran.getBalance());
			}else{
				if(tran.getBalance()==0){
					io.setDescripion("Rented Item.Full payment. Paid by " + tran.getCustomer().getFullname());
				}else{
					io.setDescripion("Rented Item.Partial payment. Paid by " + tran.getCustomer().getFullname() + ". Payable amount " + tran.getBalance());
				}
			}
			io.setTransType(MoneyStatus.INCOME.getId());
			io.setInAmount(tran.getPaidAmount());
			io.setUserDtls(Login.getUserLogin().getUserDtls());
			io.setReceiptNo("0000-00-00-0000000000");//DeliveryItemReceipt.generateNewReceiptNo());
			io.setCustomer(tran.getCustomer());
			
			io.save(io);
			
		}
		
		if(bottle==null){
			bottle = new RentedBottle();
			
			bottle.setCurrentDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			/*bottle.setCurrentQty(tran.getQuantity());
			bottle.setCurrentPaidAmount(tran.getPaidAmount());
			bottle.setCurrentBalance(tran.getBalance());
			bottle.setCurrentDeposit(tran.getDeposit());*/
			bottle.setCurrentQty(0);
			bottle.setCurrentPaidAmount(0);
			bottle.setCurrentBalance(0);
			bottle.setCurrentDeposit(0);
			
			bottle.setPrevDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			bottle.setPrevQty(0);
			bottle.setPrevPaidAmount(0);
			bottle.setPrevBalance(0);
			bottle.setPrevDeposit(0);
			
			bottle.setCustomer(tran.getCustomer());
			bottle.setProductProperties(tran.getProductProperties());
			bottle.setUom(tran.getUom());
			bottle.setUserDtls(Login.getUserLogin().getUserDtls());
			bottle = bottle.save(bottle);
			
		}
		
		return bottle;
	}
	
	public void clickRentedItem(RentedBottleTrans rent){
		setRentedData(rent);
		setRetDateTrans(rent.getDateTrans());
		setRentQuantity(rent.getQuantity());
		setRentTotalAmount(rent.getTotalAmount());
		setRentPaidAmount(rent.getPaidAmount());
		setRentBalance(rent.getBalance());
		setRentChargeitem(rent.getChargeitem());
		setRentRemarks(rent.getRemarks());
		setRentUomId(rent.getUom().getUomid());
		setRentCustomerId(rent.getCustomer().getCustomerid());
		setRentProductId(rent.getProductProperties().getPropid());
		setRentDeposit(rent.getDeposit());
	}
	
	public void storeRentComboData(){
		setUomIdTmp(getRentUomId());
		setProdcutIdTmp(getRentProductId());
		setCustomerIdTmp(getRentCustomerId());
	}
	
	public void computeRented(){
		
		if(getRentQuantity()<0) setRentQuantity(0);
		if(getRentChargeitem()<0) setRentChargeitem(0);
		if(getRentPaidAmount()<0) setRentPaidAmount(0);
		if(getRentDeposit()<0) setRentDeposit(0); 
		double total = getRentQuantity() * getRentChargeitem();
		double bal = total - getRentPaidAmount();
		setRentTotalAmount(total);
		setRentBalance(bal);
		setRentUomId(getUomIdTmp());
		setRentProductId(getProdcutIdTmp());
		setRentCustomerId(getCustomerIdTmp());
	}
	
	public void clearRentedFields(){
		setRentedData(null);
		setRetDateTrans(null);
		setRentQuantity(0);
		setRentTotalAmount(0);
		setRentPaidAmount(0);
		setRentBalance(0);
		setRentChargeitem(0);
		setRentRemarks(null);
		setRentUomId(0);
		setRentCustomerId(0);
		setRentProductId(0);
		setRentDeposit(0);
	}
	
	
	public List getProducts() {
		return products;
	}

	public void setProducts(List products) {
		this.products = products;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public double getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(double priceAmount) {
		this.priceAmount = priceAmount;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public List<SupplierItem> getItems() {
		return items;
	}

	public void setItems(List<SupplierItem> items) {
		this.items = items;
	}

	public String getAddFromDate() {
		if(addFromDate==null){
			addFromDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return addFromDate;
	}

	public void setAddFromDate(String addFromDate) {
		this.addFromDate = addFromDate;
	}

	public String getAddToDate() {
		if(addToDate==null){
			addToDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return addToDate;
	}

	public void setAddToDate(String addToDate) {
		this.addToDate = addToDate;
	}

	public String getAddSearchSupplier() {
		return addSearchSupplier;
	}

	public void setAddSearchSupplier(String addSearchSupplier) {
		this.addSearchSupplier = addSearchSupplier;
	}

	public String getAddSearchProdcut() {
		return addSearchProdcut;
	}

	public void setAddSearchProdcut(String addSearchProdcut) {
		this.addSearchProdcut = addSearchProdcut;
	}

	public String getDateTransacted() {
		if(dateTransacted==null){
			dateTransacted = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTransacted;
	}

	public void setDateTransacted(String dateTransacted) {
		this.dateTransacted = dateTransacted;
	}

	public Supplier getAddSelectedSupplier() {
		return addSelectedSupplier;
	}

	public void setAddSelectedSupplier(Supplier addSelectedSupplier) {
		this.addSelectedSupplier = addSelectedSupplier;
	}

	public SupplierTrans getAddSelectedSupplierTrans() {
		return addSelectedSupplierTrans;
	}

	public void setAddSelectedSupplierTrans(SupplierTrans addSelectedSupplierTrans) {
		this.addSelectedSupplierTrans = addSelectedSupplierTrans;
	}

	public List<SupplierItem> getAddSelectedItemsData() {
		return addSelectedItemsData;
	}

	public void setAddSelectedItemsData(List<SupplierItem> addSelectedItemsData) {
		this.addSelectedItemsData = addSelectedItemsData;
	}

	public String getProductExp() {
		return productExp;
	}

	public void setProductExp(String productExp) {
		this.productExp = productExp;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<SupplierTrans> getTrans() {
		return trans;
	}

	public void setTrans(List<SupplierTrans> trans) {
		this.trans = trans;
	}

	public SupplierItem getAddSelectedItem() {
		return addSelectedItem;
	}

	public void setAddSelectedItem(SupplierItem addSelectedItem) {
		this.addSelectedItem = addSelectedItem;
	}

	public String getAddDeliveryDateTrans() {
		if(addDeliveryDateTrans==null){
			addDeliveryDateTrans = DateUtils.getCurrentDateYYYYMMDD();
		}
		return addDeliveryDateTrans;
	}

	public void setAddDeliveryDateTrans(String addDeliveryDateTrans) {
		this.addDeliveryDateTrans = addDeliveryDateTrans;
	}

	public double getAddDeliverySellingPrice() {
		return addDeliverySellingPrice;
	}

	public void setAddDeliverySellingPrice(double addDeliverySellingPrice) {
		this.addDeliverySellingPrice = addDeliverySellingPrice;
	}

	public double getAddDeliveryQty() {
		return addDeliveryQty;
	}

	public void setAddDeliveryQty(double addDeliveryQty) {
		this.addDeliveryQty = addDeliveryQty;
	}

	public String getAddDeliveryStatus() {
		if(addDeliveryStatus==null){
			addDeliveryStatus = DeliveryStatus.NEW_FOR_DELIVERY.getName();
		}
		return addDeliveryStatus;
	}

	public void setAddDeliveryStatus(String addDeliveryStatus) {
		this.addDeliveryStatus = addDeliveryStatus;
	}

	public String getAddDeliveryRemarks() {
		return addDeliveryRemarks;
	}

	public void setAddDeliveryRemarks(String addDeliveryRemarks) {
		this.addDeliveryRemarks = addDeliveryRemarks;
	}

	public List<DeliveryItem> getDeliveryItems() {
		return deliveryItems;
	}

	public void setDeliveryItems(List<DeliveryItem> deliveryItems) {
		this.deliveryItems = deliveryItems;
	}

	public String getAddDeliveryDateFrom() {
		if(addDeliveryDateFrom==null){
			addDeliveryDateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return addDeliveryDateFrom;
	}

	public void setAddDeliveryDateFrom(String addDeliveryDateFrom) {
		this.addDeliveryDateFrom = addDeliveryDateFrom;
	}

	public String getAddDeliveryDateTo() {
		if(addDeliveryDateTo==null){
			addDeliveryDateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return addDeliveryDateTo;
	}

	public void setAddDeliveryDateTo(String addDeliveryDateTo) {
		this.addDeliveryDateTo = addDeliveryDateTo;
	}

	public DeliveryItem getDeliveryData() {
		return deliveryData;
	}

	public void setDeliveryData(DeliveryItem deliveryData) {
		this.deliveryData = deliveryData;
	}

	public Product getAddDeliveryProductData() {
		return addDeliveryProductData;
	}

	public void setAddDeliveryProductData(Product addDeliveryProductData) {
		this.addDeliveryProductData = addDeliveryProductData;
	}

	public double getAddDeliveryRemainingQtyinWarehouse() {
		return addDeliveryRemainingQtyinWarehouse;
	}

	public void setAddDeliveryRemainingQtyinWarehouse(double addDeliveryRemainingQtyinWarehouse) {
		this.addDeliveryRemainingQtyinWarehouse = addDeliveryRemainingQtyinWarehouse;
	}

	public List<DeliveryItem> getSelectedAddedDeliveryItems() {
		return selectedAddedDeliveryItems;
	}

	public void setSelectedAddedDeliveryItems(List<DeliveryItem> selectedAddedDeliveryItems) {
		this.selectedAddedDeliveryItems = selectedAddedDeliveryItems;
	}

	public List<DeliveryItemTrans> getItemSolds() {
		return itemSolds;
	}

	public void setItemSolds(List<DeliveryItemTrans> itemSolds) {
		this.itemSolds = itemSolds;
	}

	public List<DeliveryItemTrans> getItemSoldSelected() {
		return itemSoldSelected;
	}

	public void setItemSoldSelected(List<DeliveryItemTrans> itemSoldSelected) {
		this.itemSoldSelected = itemSoldSelected;
	}

	public Customer getSoldCustomer() {
		return soldCustomer;
	}

	public void setSoldCustomer(Customer soldCustomer) {
		this.soldCustomer = soldCustomer;
	}

	public String getSoldDate() {
		if(soldDate==null){
			soldDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return soldDate;
	}

	public void setSoldDate(String soldDate) {
		this.soldDate = soldDate;
	}

	public String getSoldRemarks() {
		return soldRemarks;
	}

	public void setSoldRemarks(String soldRemarks) {
		this.soldRemarks = soldRemarks;
	}

	public DeliveryItem getSoldItem() {
		return soldItem;
	}

	public void setSoldItem(DeliveryItem soldItem) {
		this.soldItem = soldItem;
	}

	public String getSoldDateFrom() {
		if(soldDateFrom==null){
			soldDateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return soldDateFrom;
	}

	public void setSoldDateFrom(String soldDateFrom) {
		this.soldDateFrom = soldDateFrom;
	}

	public String getSoldDateTo() {
		if(soldDateTo==null){
			soldDateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return soldDateTo;
	}

	public void setSoldDateTo(String soldDateTo) {
		this.soldDateTo = soldDateTo;
	}

	public String getSoldStatus() {
		if(soldStatus==null){
			soldStatus = ReceiptStatus.NEW.getName();
		}
		return soldStatus;
	}

	public void setSoldStatus(String soldStatus) {
		this.soldStatus = soldStatus;
	}

	public double getSoldQty() {
		return soldQty;
	}

	public void setSoldQty(double soldQty) {
		this.soldQty = soldQty;
	}

	public double getSoldPrice() {
		return soldPrice;
	}

	public void setSoldPrice(double soldPrice) {
		this.soldPrice = soldPrice;
	}

	public long getSoldCustomerId() {
		return soldCustomerId;
	}

	public void setSoldCustomerId(long soldCustomerId) {
		this.soldCustomerId = soldCustomerId;
	}

	public String getSearchSoldCustomer() {
		return searchSoldCustomer;
	}

	public void setSearchSoldCustomer(String searchSoldCustomer) {
		this.searchSoldCustomer = searchSoldCustomer;
	}

	public List getSoldCustomers() {
		return soldCustomers;
	}

	public void setSoldCustomers(List soldCustomers) {
		this.soldCustomers = soldCustomers;
	}

	public List<DeliveryItemReceipt> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<DeliveryItemReceipt> receipts) {
		this.receipts = receipts;
	}

	public String getSoldPaymentStatus() {
		if(soldPaymentStatus==null){
			soldPaymentStatus = ReceiptStatus.PARTIAL.getName();
		}
		return soldPaymentStatus;
	}

	public void setSoldPaymentStatus(String soldPaymentStatus) {
		this.soldPaymentStatus = soldPaymentStatus;
	}

	public double getSoldBalance() {
		return soldBalance;
	}

	public void setSoldBalance(double soldBalance) {
		this.soldBalance = soldBalance;
	}

	public double getSoldDiscount() {
		return soldDiscount;
	}

	public void setSoldDiscount(double soldDiscount) {
		this.soldDiscount = soldDiscount;
	}

	public DeliveryItemReceipt getReceiptData() {
		return receiptData;
	}

	public void setReceiptData(DeliveryItemReceipt receiptData) {
		this.receiptData = receiptData;
	}

	public String getSoldReceiptNo() {
		if(soldReceiptNo==null){
			soldReceiptNo = DeliveryItemReceipt.generateNewReceiptNo();
		}
		return soldReceiptNo;
	}

	public void setSoldReceiptNo(String soldReceiptNo) {
		this.soldReceiptNo = soldReceiptNo;
	}

	public String getSoldItemDate() {
		if(soldItemDate==null){
			soldItemDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return soldItemDate;
	}

	public void setSoldItemDate(String soldItemDate) {
		this.soldItemDate = soldItemDate;
	}

	public double getSoldItemPrice() {
		return soldItemPrice;
	}

	public void setSoldItemPrice(double soldItemPrice) {
		this.soldItemPrice = soldItemPrice;
	}

	public double getSoldItemQty() {
		return soldItemQty;
	}

	public void setSoldItemQty(double soldItemQty) {
		this.soldItemQty = soldItemQty;
	}

	public String getSoldSearchProduct() {
		return soldSearchProduct;
	}

	public void setSoldSearchProduct(String soldSearchProduct) {
		this.soldSearchProduct = soldSearchProduct;
	}

	public long getSoldProductId() {
		return soldProductId;
	}

	public void setSoldProductId(long soldProductId) {
		this.soldProductId = soldProductId;
	}

	public List getSoldProducts() {
		return soldProducts;
	}

	public void setSoldProducts(List soldProducts) {
		this.soldProducts = soldProducts;
	}

	public double getSoldItemQtyInDelivery() {
		return soldItemQtyInDelivery;
	}

	public void setSoldItemQtyInDelivery(double soldItemQtyInDelivery) {
		this.soldItemQtyInDelivery = soldItemQtyInDelivery;
	}

	public double getSoldItemSellingPrice() {
		return soldItemSellingPrice;
	}

	public void setSoldItemSellingPrice(double soldItemSellingPrice) {
		this.soldItemSellingPrice = soldItemSellingPrice;
	}

	public DeliveryItemTrans getSoldData() {
		return soldData;
	}

	public void setSoldData(DeliveryItemTrans soldData) {
		this.soldData = soldData;
	}

	public String getSoldItemStatus() {
		if(soldItemStatus==null){
			soldItemStatus = DeliveryStatus.NEW_SOLD_ITEM.getName();
		}
		return soldItemStatus;
	}

	public void setSoldItemStatus(String soldItemStatus) {
		this.soldItemStatus = soldItemStatus;
	}

	public String getSoldItemRemarks() {
		return soldItemRemarks;
	}

	public void setSoldItemRemarks(String soldItemRemarks) {
		this.soldItemRemarks = soldItemRemarks;
	}

	public double getSoldChargeAmount() {
		return soldChargeAmount;
	}

	public void setSoldChargeAmount(double soldChargeAmount) {
		this.soldChargeAmount = soldChargeAmount;
	}

	public double getSoldGrandTotal() {
		return soldGrandTotal;
	}

	public void setSoldGrandTotal(double soldGrandTotal) {
		this.soldGrandTotal = soldGrandTotal;
	}

	public double getSoldDownPayment() {
		return soldDownPayment;
	}

	public void setSoldDownPayment(double soldDownPayment) {
		this.soldDownPayment = soldDownPayment;
	}

	public boolean getIsSoldItemPosted() {
		return isSoldItemPosted;
	}

	public void setIsSoldItemPosted(boolean isSoldItemPosted) {
		this.isSoldItemPosted = isSoldItemPosted;
	}

	public boolean isCustomerNameLock() {
		return customerNameLock;
	}

	public void setCustomerNameLock(boolean customerNameLock) {
		this.customerNameLock = customerNameLock;
	}

	public List<DeliveryItemReceipt> getSelectedReceipts() {
		return selectedReceipts;
	}

	public void setSelectedReceipts(List<DeliveryItemReceipt> selectedReceipts) {
		this.selectedReceipts = selectedReceipts;
	}

	public List<DeliveryItem> getReturnItems() {
		return returnItems;
	}

	public void setReturnItems(List<DeliveryItem> returnItems) {
		this.returnItems = returnItems;
	}

	public String getReturnDateFrom() {
		if(returnDateFrom==null){
			returnDateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return returnDateFrom;
	}

	public void setReturnDateFrom(String returnDateFrom) {
		this.returnDateFrom = returnDateFrom;
	}

	public String getReturnDateTo() {
		if(returnDateTo==null){
			returnDateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return returnDateTo;
	}

	public void setReturnDateTo(String returnDateTo) {
		this.returnDateTo = returnDateTo;
	}

	public String getReturnProductName() {
		return returnProductName;
	}

	public void setReturnProductName(String returnProductName) {
		this.returnProductName = returnProductName;
	}

	public List<DeliveryItemTrans> getSoldProductItem() {
		return soldProductItem;
	}

	public void setSoldProductItem(List<DeliveryItemTrans> soldProductItem) {
		this.soldProductItem = soldProductItem;
	}

	public String getSoldProductItemDateFrom() {
		if(soldProductItemDateFrom==null){
			soldProductItemDateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return soldProductItemDateFrom;
	}

	public void setSoldProductItemDateFrom(String soldProductItemDateFrom) {
		this.soldProductItemDateFrom = soldProductItemDateFrom;
	}

	public String getSoldProductItemDateTo() {
		if(soldProductItemDateTo==null){
			soldProductItemDateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return soldProductItemDateTo;
	}

	public void setSoldProductItemDateTo(String soldProductItemDateTo) {
		this.soldProductItemDateTo = soldProductItemDateTo;
	}

	public String getSoldProductItemGrandTotal() {
		if(soldProductItemGrandTotal==null){
			soldProductItemGrandTotal = "0.00";
		}
		return soldProductItemGrandTotal;
	}

	public void setSoldProductItemGrandTotal(String soldProductItemGrandTotal) {
		this.soldProductItemGrandTotal = soldProductItemGrandTotal;
	}

	public String getReturnProductItemGrandTotal() {
		if(returnProductItemGrandTotal==null){
			returnProductItemGrandTotal = "0.00";
		}
		return returnProductItemGrandTotal;
	}

	public void setReturnProductItemGrandTotal(String returnProductItemGrandTotal) {
		this.returnProductItemGrandTotal = returnProductItemGrandTotal;
	}

	public String getSoldItemGrandTotalPurchased() {
		if(soldItemGrandTotalPurchased==null){
			soldItemGrandTotalPurchased = "0.00";
		}
		return soldItemGrandTotalPurchased;
	}

	public void setSoldItemGrandTotalPurchased(String soldItemGrandTotalPurchased) {
		this.soldItemGrandTotalPurchased = soldItemGrandTotalPurchased;
	}

	public String getSoldItemGrandTotalBalance() {
		if(soldItemGrandTotalBalance==null){
			soldItemGrandTotalBalance = "0.00";
		}
		return soldItemGrandTotalBalance;
	}

	public void setSoldItemGrandTotalBalance(String soldItemGrandTotalBalance) {
		this.soldItemGrandTotalBalance = soldItemGrandTotalBalance;
	}

	public String getAddDeliveryGrandTotalSelling() {
		if(addDeliveryGrandTotalSelling==null){
			addDeliveryGrandTotalSelling = "0.00";
		}
		return addDeliveryGrandTotalSelling;
	}

	public void setAddDeliveryGrandTotalSelling(String addDeliveryGrandTotalSelling) {
		this.addDeliveryGrandTotalSelling = addDeliveryGrandTotalSelling;
	}

	public double getAddDeliveryTotalQty() {
		return addDeliveryTotalQty;
	}

	public void setAddDeliveryTotalQty(double addDeliveryTotalQty) {
		this.addDeliveryTotalQty = addDeliveryTotalQty;
	}

	public String getGrandTotalSupplierPurchased() {
		if(grandTotalSupplierPurchased==null){
			grandTotalSupplierPurchased = "0.00";
		}
		return grandTotalSupplierPurchased;
	}

	public void setGrandTotalSupplierPurchased(String grandTotalSupplierPurchased) {
		this.grandTotalSupplierPurchased = grandTotalSupplierPurchased;
	}

	public List getProductsSup() {
		return productsSup;
	}

	public void setProductsSup(List productsSup) {
		this.productsSup = productsSup;
	}

	public String getSoldProductItemPriceGrandTotal() {
		return soldProductItemPriceGrandTotal;
	}

	public void setSoldProductItemPriceGrandTotal(String soldProductItemPriceGrandTotal) {
		this.soldProductItemPriceGrandTotal = soldProductItemPriceGrandTotal;
	}

	public double getSoldProductItemQtyGrandTotal() {
		return soldProductItemQtyGrandTotal;
	}

	public void setSoldProductItemQtyGrandTotal(double soldProductItemQtyGrandTotal) {
		this.soldProductItemQtyGrandTotal = soldProductItemQtyGrandTotal;
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

	public List<RentedBottleTrans> getRentedItems() {
		return rentedItems;
	}

	public void setRentedItems(List<RentedBottleTrans> rentedItems) {
		this.rentedItems = rentedItems;
	}

	public String getRentDateFrom() {
		if(rentDateFrom==null){
			rentDateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return rentDateFrom;
	}

	public void setRentDateFrom(String rentDateFrom) {
		this.rentDateFrom = rentDateFrom;
	}

	public String getRentDateTo() {
		if(rentDateTo==null){
			rentDateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return rentDateTo;
	}

	public void setRentDateTo(String rentDateTo) {
		this.rentDateTo = rentDateTo;
	}

	public String getRetDateTrans() {
		if(retDateTrans==null){
			retDateTrans = DateUtils.getCurrentDateYYYYMMDD();
		}
		return retDateTrans;
	}

	public void setRetDateTrans(String retDateTrans) {
		this.retDateTrans = retDateTrans;
	}

	public double getRentQuantity() {
		return rentQuantity;
	}

	public void setRentQuantity(double rentQuantity) {
		this.rentQuantity = rentQuantity;
	}

	public double getRentTotalAmount() {
		return rentTotalAmount;
	}

	public void setRentTotalAmount(double rentTotalAmount) {
		this.rentTotalAmount = rentTotalAmount;
	}

	public double getRentPaidAmount() {
		return rentPaidAmount;
	}

	public void setRentPaidAmount(double rentPaidAmount) {
		this.rentPaidAmount = rentPaidAmount;
	}

	public double getRentBalance() {
		return rentBalance;
	}

	public void setRentBalance(double rentBalance) {
		this.rentBalance = rentBalance;
	}

	public double getRentChargeitem() {
		return rentChargeitem;
	}

	public void setRentChargeitem(double rentChargeitem) {
		this.rentChargeitem = rentChargeitem;
	}

	public String getRentRemarks() {
		return rentRemarks;
	}

	public void setRentRemarks(String rentRemarks) {
		this.rentRemarks = rentRemarks;
	}

	public List getRentUom() {
		rentUom = new ArrayList<>();
		UOM uom  = new UOM();
		uom.setIsactive(1);
		
		for(UOM u : UOM.retrieve(uom)){
			rentUom.add(new SelectItem(u.getUomid(), u.getSymbol()));
		}
		
		
		return rentUom;
	}

	public void setRentUom(List rentUom) {
		this.rentUom = rentUom;
	}

	public int getRentUomId() {
		return rentUomId;
	}

	public void setRentUomId(int rentUomId) {
		this.rentUomId = rentUomId;
	}

	public List getRentCustomer() {
		
		rentCustomer = new ArrayList<>();
		if(getSearchName()!=null && !getSearchName().isEmpty()){
		Customer cus = new Customer();
		cus.setIsactive(1);
		
		
		cus.setFullname(getSearchName());
		
		
		for(Customer cz : Customer.retrieve(cus)){
			rentCustomer.add(new SelectItem(cz.getCustomerid(), cz.getFullname()));
		}
		} 
		return rentCustomer;
	}

	public void setRentCustomer(List rentCustomer) {
		this.rentCustomer = rentCustomer;
	}

	public long getRentCustomerId() {
		return rentCustomerId;
	}

	public void setRentCustomerId(long rentCustomerId) {
		this.rentCustomerId = rentCustomerId;
	}

	public List getRentProduct() {
		
		rentProduct = new ArrayList<>();
		if(getSearchProduct()!=null && !getSearchProduct().isEmpty()){
		ProductProperties prop = new ProductProperties();
		prop.setIsactive(1);
		
		prop.setProductname(getSearchProduct());
		
		
		for(ProductProperties prp : ProductProperties.retrieve(prop)){
			rentProduct.add(new SelectItem(prp.getPropid(), prp.getProductname()));
		}
		}
		return rentProduct;
	}

	public void setRentProduct(List rentProduct) {
		this.rentProduct = rentProduct;
	}

	public long getRentProductId() {
		return rentProductId;
	}

	public void setRentProductId(long rentProductId) {
		this.rentProductId = rentProductId;
	}

	public RentedBottleTrans getRentedData() {
		return rentedData;
	}

	public void setRentedData(RentedBottleTrans rentedData) {
		this.rentedData = rentedData;
	}

	public double getRentDeposit() {
		return rentDeposit;
	}

	public void setRentDeposit(double rentDeposit) {
		this.rentDeposit = rentDeposit;
	}

	public int getUomIdTmp() {
		return uomIdTmp;
	}

	public void setUomIdTmp(int uomIdTmp) {
		this.uomIdTmp = uomIdTmp;
	}

	public long getProdcutIdTmp() {
		return prodcutIdTmp;
	}

	public void setProdcutIdTmp(long prodcutIdTmp) {
		this.prodcutIdTmp = prodcutIdTmp;
	}

	public long getCustomerIdTmp() {
		return customerIdTmp;
	}

	public void setCustomerIdTmp(long customerIdTmp) {
		this.customerIdTmp = customerIdTmp;
	}

	public String getRentDescription() {
		return rentDescription;
	}

	public void setRentDescription(String rentDescription) {
		this.rentDescription = rentDescription;
	}

	public List<RentedBottleTrans> getSelectedRentedTrans() {
		return selectedRentedTrans;
	}

	public void setSelectedRentedTrans(List<RentedBottleTrans> selectedRentedTrans) {
		this.selectedRentedTrans = selectedRentedTrans;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getSearchProduct() {
		return searchProduct;
	}

	public void setSearchProduct(String searchProduct) {
		this.searchProduct = searchProduct;
	}

	public String getAddSelectedSupplierDeliveryReceipt() {
		return addSelectedSupplierDeliveryReceipt;
	}

	public void setAddSelectedSupplierDeliveryReceipt(String addSelectedSupplierDeliveryReceipt) {
		this.addSelectedSupplierDeliveryReceipt = addSelectedSupplierDeliveryReceipt;
	}
	
	//==============================================End Added Item============================================================

}
