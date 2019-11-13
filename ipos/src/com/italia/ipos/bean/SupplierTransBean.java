package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.event.TabChangeEvent;

import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.InputedInventoryQtyTracker;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.MoneyIO;
import com.italia.ipos.controller.Payment;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductInventory;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.ProductReturnSupplier;
import com.italia.ipos.controller.Supplier;
import com.italia.ipos.controller.SupplierItem;
import com.italia.ipos.controller.SupplierPayment;
import com.italia.ipos.controller.SupplierTrans;
import com.italia.ipos.controller.UOM;
import com.italia.ipos.enm.MoneyStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.ReturnStatus;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Whitelist;
/**
 * 
 * @author mark italia
 * @since 12/24/2016
 * @version 1.0
 *
 */
@ManagedBean(name="supTranBean", eager=true)
@ViewScoped
public class SupplierTransBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 547658797645865861L;
	
	private Supplier supplier;
	private List<Supplier> sups = Collections.synchronizedList(new ArrayList<Supplier>());
	private String searchSupplier;
	private List<SupplierTrans> trans = Collections.synchronizedList(new ArrayList<SupplierTrans>());
	private SupplierTrans tran;
	private List<SupplierItem> items = Collections.synchronizedList(new ArrayList<SupplierItem>());
	private String searchItem;
	private SupplierPayment payment;
	private double amount;
	private String orNumber;
	private String remarks;
	
	private String dateFrom;
	private String dateTo;
	private List status;
	private int statusId;
	
	private List<ProductReturnSupplier> returns = Collections.synchronizedList(new ArrayList<ProductReturnSupplier>());
	private List<ProductReturnSupplier> returnDataPosting =  Collections.synchronizedList(new ArrayList<ProductReturnSupplier>());
	private Map<Long, ProductInventory> inventoryData = Collections.synchronizedMap(new HashMap<Long, ProductInventory>());
	private ProductReturnSupplier returnData;
	private String dateReturn;
	private String returnStatus;
	
	private List suppliers;
	private long supplierId;
	private List products;
	private long productId;
	private List uoms;
	private int uomid;
	
	private String researchSupplier;
	private String reserachProduct;
	private String amountReturn;
	private String qtyReturn;
	private String remarksReturn;
	private String returnDateFrom;
	private String returnDateTo;
	
	public void onTabChange(TabChangeEvent event) {
        
		if("Received From Suppliers".equalsIgnoreCase(event.getTab().getTitle())){
			initReceived();
		}else if("Returned To Suppliers".equalsIgnoreCase(event.getTab().getTitle())){
			initReturned();
			loadReturnListBox();
		}
		
    }
	
	@PostConstruct
	public void init(){
		initReceived();
	}
	
	public void initReturned(){
		returns = Collections.synchronizedList(new ArrayList<ProductReturnSupplier>());
		
		ProductReturnSupplier ret = new ProductReturnSupplier();
		ret.setIsActive(1);
		ret.setBetween(true);
		ret.setDateFrom(getReturnDateFrom());
		ret.setDateTo(getReturnDateTo());
		
		System.out.println("return dateFrom " + getReturnDateFrom());
		System.out.println("return dateTo " + getReturnDateTo());
		
		Supplier sup = new Supplier();
		sup.setIsactive(1);
		
		if(getSearchSupplier()!=null){
			sup.setSuppliername(Whitelist.remove(getSearchSupplier()));
		}
		
		for(ProductReturnSupplier rt : ProductReturnSupplier.retrieve(ret, sup)){
			returns.add(rt);
		}
		Collections.reverse(returns);
		
	}
	
	public void initReceived(){
		sups = Collections.synchronizedList(new ArrayList<Supplier>());
		Supplier sup = new Supplier();
		sup.setIsactive(1);
		
		if(getSearchSupplier()!=null){
			sup.setSuppliername(Whitelist.remove(getSearchSupplier()));
		}
		double bal = 0d;
		for(Supplier s : Supplier.retrieve(sup)){
			try{bal = balanceToSupplierAmnt(s);}catch(Exception e){}
			System.out.println("Supplier " + s.getOwnername() + " bal: " + bal);
			if(bal!=0){
				s.setBalance(bal);
				sups.add(s);
			}
		}
		
		Collections.reverse(sups);
	}
	
	public void saveReturn(){
		String val = "";
		boolean isNotValid = false;
		int cnt = 0;
		if(getSupplierId()==0){isNotValid=true; val="Supplier "; cnt++;}
		if(getProductId()==0){isNotValid=true; val+="Product "; cnt++;}
		if(getUomid()==0){isNotValid=true; val+="UOM "; cnt++;}
		if(getQtyReturn()==null || getQtyReturn().isEmpty()){isNotValid=true; val+="Quantiy "; cnt++;}
		
		if(isNotValid){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide information for the following: " +  (cnt==1? val : val.replace(" ", ", ")), "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}else{
		
		if(Login.checkUserStatus()){
			
			ProductReturnSupplier ret = new ProductReturnSupplier();
			boolean isNew = true;
			if(getReturnData()!=null){
				ret = getReturnData();
				
				if(ReturnStatus.NEW.getId()== ret.getStatus()){
					isNew = true;
				}else{
					isNew = false;
				}
			}
			
			if(isNew){
			ret.setDateTrans(getDateReturn());
			ret.setStatus(ReturnStatus.NEW.getId());
			ret.setQuantity(Double.valueOf(getQtyReturn().replace(",", "")));
			if(getAmountReturn()==null || getAmountReturn().isEmpty()){setAmountReturn("0");}
			ret.setAmount(Double.valueOf(getAmountReturn().replace(",", "")));
			ret.setIsActive(1);
			ret.setRemarks(getRemarksReturn());
			
			Product prod = new Product();
			prod = Product.retrieve(getProductId()+"");
			ret.setProduct(prod);
			
			ProductProperties prop = prod.getProductProperties();
			ret.setProperties(prop);
			
			UOM uom = new UOM();
			uom.setUomid(getUomid());
			ret.setUom(uom);
			
			Supplier sup = new Supplier();
			sup.setSupid(getSupplierId());
			ret.setSupplier(sup);
			
			ret.setUserDtls(Login.getUserLogin().getUserDtls());
			
			ret.save();
			clearReturnFields();
			initReturned();
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Posted data cannot be changed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Something went wrong. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		}
	}
	
	public void postedReturn(){
		if(Login.checkUserStatus()){
			
			if(getReturnDataPosting()!=null && getReturnDataPosting().size()>0){
				
				int cnt = 0;
				
				for(ProductReturnSupplier ret : getReturnDataPosting()){
					if(ReturnStatus.NEW.getId()==ret.getStatus()){
						
						//record inventory
						boolean isOk = recordingInventory(ret);
						
						if(isOk){
							ret.setStatus(ReturnStatus.POSTED.getId());
							ret.setUserDtls(Login.getUserLogin().getUserDtls());
							ret.save();
							//record to iomoney table
							moneySaveTransactions(ret);
							cnt++;
						}
					}
				}
				
				if(cnt>0){
					clearReturnFields();
					initReturned();
					
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, (cnt==1? "1 item has " : cnt+" items have " ) + "been posted.", "");
			        FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: No data has been posted.", "");
			        FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
				
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: No data has been selected for posting.", "");
		        FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Something went wrong. Please login again.", "");
	        FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void deleteReturn(ProductReturnSupplier ret){
		if(Login.checkUserStatus()){	
		
			if(ReturnStatus.NEW.getId()==ret.getStatus()){
				
				ret.delete();
				initReturned();
				clearReturnFields();
				
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully deleted.", "");
		        FacesContext.getCurrentInstance().addMessage(null, msg);
				
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Posted item cannot be deleted.", "");
		        FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Something went wrong. Please login again.", "");
	        FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	private boolean recordingInventory(ProductReturnSupplier ret){
		boolean isOk = false;
		//adding inventory
		ProductInventory inv = new ProductInventory();
		Product prod = new Product();
		prod.setIsactiveproduct(1);
		prod.setProdid(ret.getProduct().getProdid());
		
		inv = ProductInventory.retrieve(prod).get(0);
		if(ret.getQuantity()<=inv.getNewqty()){
			double oldQuantity = 0d;
			oldQuantity = inv.getNewqty();
			Double qty = -ret.getQuantity() + inv.getNewqty();
			inv.setNewqty(qty);
			inv.setOldqty(oldQuantity);
			inv.setUserDtls(Login.getUserLogin().getUserDtls());
			inv.setProductProperties(ret.getProperties());
			inv.save();
			
			isOk=true;
			
			//track inputed quantity
			inv.setAddqty(-ret.getQuantity());
			InputedInventoryQtyTracker.saveQty(inv, "QUANTITY RETURN TO SUPPLIER");
		}
		
		
		
		return isOk;
	}
	
	public void clearReturnFields(){
		setReturnData(null);
		setReturnDataPosting(null);
		setDateReturn(null);
		setReturnStatus(null);
		setSupplierId(0);
		setProductId(0);
		setUomid(0);
		setQtyReturn(null);
		setAmountReturn(null);
		setRemarksReturn(null);
	}
	
	public void clickItemReturn(ProductReturnSupplier ret){
		setReturnData(ret);
		setDateReturn(ret.getDateTrans());
		setReturnStatus(ReturnStatus.typeName(ret.getStatus()));
		setSupplierId(ret.getSupplier().getSupid());
		setProductId(ret.getProduct().getProdid());
		setUomid(ret.getUom().getUomid());
		setQtyReturn(ret.getQuantity()+"");
		setAmountReturn(ret.getAmount()+"");
		setRemarksReturn(ret.getRemarks());
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
			balance = balance + (t.getPurchasedPrice() - amnt);
		}
		
		return balance;
	}
	
	public void printAll(){
		
	}
	
	public void clickItem(Supplier sup){
		
		//setSearchSupplier(sup.getSuppliername());
		init();
		
		setSupplier(sup);
		items = Collections.synchronizedList(new ArrayList<SupplierItem>());
		trans = Collections.synchronizedList(new ArrayList<SupplierTrans>());
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
			List<SupplierPayment> payments = Collections.synchronizedList(new ArrayList<SupplierPayment>());
			for(SupplierPayment py : SupplierPayment.retrieve(pay,tr)){
				amnt += py.getAmount();
				System.out.println("payment: " + py.getSupplierTrans().getId());
				payments.add(py);
			}
			t.setPayments(payments);
			amnt = t.getPurchasedPrice() - amnt;
			t.setBalance(amnt);
			trans.add(t);
		}
		Collections.reverse(trans);
	}
	
	public void clickItemOrder(SupplierTrans sup){
		setTran(sup);
		SupplierItem item = new SupplierItem();
		item.setIsActiveItem(1);
		item.setStatus(2);
		SupplierTrans tran = new SupplierTrans();
		tran.setId(sup.getId());
		tran.setIsActive(1);
		items = Collections.synchronizedList(new ArrayList<SupplierItem>()); 
		for(SupplierItem i : SupplierItem.retrieve(tran,item)){
			
			/*ProductProperties prop = new ProductProperties();
			prop.setIsactive(1);
			prop.setPropid(i.getProductProperties().getPropid());
			prop =  ProductProperties.retrieve(prop).get(0);
			System.out.println("Product " + prop.getProductname());
			i.setProductProperties(prop);
			items.add(i);*/
			
			ProductProperties prop = new ProductProperties();
			Product prod = new Product();
			prod = Product.retrieve(i.getProduct().getProdid()+"");
			prop = ProductProperties.properties(prod.getProductProperties().getPropid()+"");
			prod.setProductProperties(prop);
			
		
		i.setProduct(prod);
		items.add(i);
			
		}
		Collections.reverse(items);
	}
	
	public void addPay(SupplierTrans trans){
		SupplierPayment pay = new SupplierPayment();
		pay.setPayTransDate(DateUtils.getCurrentDateYYYYMMDD());
		pay.setSupplierTrans(trans);
		pay.setIsActive(1);
		pay.setUserDtls(Login.getUserLogin().getUserDtls());
		setPayment(pay);
		setAmount(0);
		setRemarks("N/A");
		
	}
	public void savePay(){
		if(Login.getUserLogin().checkUserStatus() && getPayment()!=null){
			boolean isSuccess = false;
			SupplierPayment pay = getPayment();
			pay.setAmount(getAmount());
			pay.setRemarks(getRemarks()==null? "added payment" : getRemarks().equalsIgnoreCase("")? "added Payment" : getRemarks());
			pay.setOrNumber(getOrNumber());
			isSuccess = updateTransactionStatus(pay,"add");
			if(isSuccess){
				pay.save();
				moneySaveTransactions(pay);
				setAmount(0);
				setRemarks(null);
				setPayment(null);
				setOrNumber(null);
				clickItem(getSupplier());
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Additional payment has been successfully added", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Please provide allowed amount", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Please provide required data", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	private void moneySaveTransactions(Object obj ){
		MoneyIO io = new MoneyIO();
		io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		Supplier sup = null;
		if(obj instanceof SupplierPayment){
			SupplierPayment pay = (SupplierPayment) obj;
			try{sup = Supplier.supplier(pay.getSupplierTrans().getSupplier().getSupid()+"");}catch(Exception e){}
			io.setTransType(MoneyStatus.EXPENSES.getId());
			io.setOutAmount(getAmount());
			
			if(sup!=null){
				io.setDescripion("Product paid to Supplier " + sup.getSuppliername() + "ref. No:" + pay.getOrNumber());
			}else{
				io.setDescripion("Product paid to Supplier");
			}
			
		}else if(obj instanceof ProductReturnSupplier){
			ProductReturnSupplier ret = (ProductReturnSupplier) obj;
			sup = ret.getSupplier();
			io.setTransType(MoneyStatus.RETURN_CAPITAL.getId());
			//io.setOutAmount(ret.getAmount());
			io.setInAmount(ret.getAmount());
			
			
			if(sup!=null){
				io.setDescripion("Product return to Supplier - Amount received from supplier " + sup.getSuppliername());
			}else{
				io.setDescripion("Product return to Supplier - Amount received from supplier");
			}
		}
		
		
		
		
		io.setReceiptNo("1111-00-00-0000000000");
		io.setUserDtls(Login.getUserLogin().getUserDtls());
		Customer customer = new Customer();
		customer.setCustomerid(0);
		io.setCustomer(customer);
		io.save(io);
	}
	
	private boolean updateTransactionStatus(SupplierPayment pay, String type){
		SupplierTrans tran = pay.getSupplierTrans();
		System.out.println("transaction Balance: "+tran.getBalance());
		if(tran.getStatus()==2){
			return false;
		}
		double balance = 0;
		if("add".equalsIgnoreCase(type)){
			balance = tran.getBalance() - pay.getAmount();
		}else if("delete".equalsIgnoreCase(type)){
			balance = tran.getBalance() + pay.getAmount();
		}
		if(balance==0){
			tran.setStatus(2);
			tran.setUserDtls(Login.getUserLogin().getUserDtls());
			tran.save();
			return true;
		}else if(balance>0){
			tran.setStatus(1);
			tran.setUserDtls(Login.getUserLogin().getUserDtls());
			tran.save();
			return true;
		}
		return false;
	}
	
	public void deletePay(SupplierPayment pay){
		if(Login.getUserLogin().checkUserStatus()){
			boolean isSuccess = false;
			SupplierTrans tran = new SupplierTrans();
			tran.setId(pay.getSupplierTrans().getId());
			tran.setIsActive(1);
			tran = SupplierTrans.retrieve(tran).get(0);
			pay.setSupplierTrans(tran);
			isSuccess = updateTransactionStatus(pay,"delete");
			if(isSuccess){
				pay.delete();
				clickItem(getSupplier());
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Added payment has been successfully deleted", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Something went wrong", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Something went wrong", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void findTransaction(){
		items = Collections.synchronizedList(new ArrayList<SupplierItem>());
		trans = Collections.synchronizedList(new ArrayList<SupplierTrans>());
		
		if(getDateFrom()==null){
			setDateFrom(DateUtils.getCurrentDateYYYYMMDD());
		}
		if(getDateTo()==null){
			setDateTo(getDateFrom());
		}
		
		SupplierTrans tran = new SupplierTrans();
		tran.setIsActive(1);
		tran.setBetweenDate(true);
		tran.setDateFrom(getDateFrom());
		tran.setDateTo(getDateTo());
		tran.setStatus(getStatusId());
		Supplier sp = new Supplier();
		sp.setSupid(getSupplier().getSupid());
		sp.setIsactive(1);
		for(SupplierTrans t :  SupplierTrans.retrieve(tran,sp)){
			SupplierPayment pay = new SupplierPayment();
			pay.setIsActive(1);
			SupplierTrans tr = new SupplierTrans();
			tr.setIsActive(1);
			tr.setId(t.getId());
			tr.setStatus(getStatusId());
			tr.setBetweenDate(true);
			tr.setDateFrom(getDateFrom());
			tr.setDateTo(getDateTo());
			double amnt = 0d;
			List<SupplierPayment> payments = Collections.synchronizedList(new ArrayList<SupplierPayment>());
			for(SupplierPayment py : SupplierPayment.retrieve(pay,tr)){
				amnt += py.getAmount();
				payments.add(py);
			}
			t.setPayments(payments);
			amnt = t.getPurchasedPrice() - amnt;
			t.setBalance(amnt);
			trans.add(t);
		}
		Collections.reverse(trans);
	}
	
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<Supplier> getSups() {
		return sups;
	}

	public void setSups(List<Supplier> sups) {
		this.sups = sups;
	}

	public String getSearchSupplier() {
		return searchSupplier;
	}

	public void setSearchSupplier(String searchSupplier) {
		this.searchSupplier = searchSupplier;
	}

	public List<SupplierTrans> getTrans() {
		return trans;
	}

	public void setTrans(List<SupplierTrans> trans) {
		this.trans = trans;
	}

	public SupplierTrans getTran() {
		return tran;
	}

	public void setTran(SupplierTrans tran) {
		this.tran = tran;
	}

	public List<SupplierItem> getItems() {
		return items;
	}

	public void setItems(List<SupplierItem> items) {
		this.items = items;
	}

	public String getSearchItem() {
		return searchItem;
	}

	public void setSearchItem(String searchItem) {
		this.searchItem = searchItem;
	}

	public SupplierPayment getPayment() {
		return payment;
	}

	public void setPayment(SupplierPayment payment) {
		this.payment = payment;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDateFrom() {
		if(dateFrom==null){
			dateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		if(dateTo==null){
			dateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public List getStatus() {
		status = new ArrayList<>();
		status.add(new SelectItem(1,"Partial"));
		status.add(new SelectItem(2,"Full Paid"));
		return status;
	}

	public void setStatus(List status) {
		this.status = status;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public List<ProductReturnSupplier> getReturns() {
		return returns;
	}

	public void setReturns(List<ProductReturnSupplier> returns) {
		this.returns = returns;
	}

	public String getDateReturn() {
		if(dateReturn==null){
			dateReturn = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateReturn;
	}

	public void setDateReturn(String dateReturn) {
		this.dateReturn = dateReturn;
	}

	public String getReturnStatus() {
		if(returnStatus==null){
			returnStatus = ReturnStatus.NEW.getName();
		}
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List getSuppliers() {
		
		suppliers = new ArrayList<>();
		
		Supplier sup = new Supplier();
		sup.setIsactive(1);
		
		/*if(getResearchSupplier()!=null && !getResearchSupplier().isEmpty()){
			sup.setSuppliername(getResearchSupplier());
		}*/
		
		for(Supplier s : Supplier.retrieve(sup)){
			suppliers.add(new SelectItem(s.getSupid(), s.getSuppliername()));
		}
		
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
	
	public void loadReturnListBox(){
		
		
		/**
		 * Load producst
		 */
		products = new ArrayList<>();
		inventoryData = Collections.synchronizedMap(new HashMap<Long, ProductInventory>()); 
		Product prod = new Product();
		prod.setIsactiveproduct(1);
		
		ProductProperties prop = new ProductProperties();
		prop.setIsactive(1);
		
		/*if(getReserachProduct()!=null && !getReserachProduct().isEmpty()){
			prop.setProductname(getReserachProduct());
		}*/
		
		for(Product p : Product.retrieve(prod, prop)){
			
			ProductInventory inv = new ProductInventory();
			inv.setIsactive(1);
			prop = ProductProperties.properties(p.getProductProperties().getPropid()+"");
			p.setProductProperties(prop);
			prod = new Product();
			prod.setProdid(p.getProdid());
			prod.setIsactiveproduct(1);
			List<ProductInventory> invs = ProductInventory.retrieve(inv,prod);
			if(invs.size()>0){
				if(invs.get(0).getNewqty()>0){
					products.add(new SelectItem(p.getProdid(), p.getProductProperties().getProductname() + " per " + p.getProductProperties().getUom().getSymbol()));
					inventoryData.put(p.getProdid(), invs.get(0));
				}
			}
		}
		
	}
	
	public void updateQty(){
		ProductInventory inv = getInventoryData().get(getProductId());
		setQtyReturn(inv.getNewqty()+"");
	}
	public void checkQtyReturn(){
		ProductInventory inv = getInventoryData().get(getProductId());
		double qty = Double.valueOf(getQtyReturn().replace(",", ""));
		System.out.println("QTY : " + qty);
		if(qty>inv.getNewqty()){
			setQtyReturn(inv.getNewqty()+"");
			System.out.println("GREATER QTY : " + qty);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Please provide less than or equal to remaining quantity", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}else{
			System.out.println("LESS QTY : " + qty);
			setQtyReturn(qty+"");
		}
	}
	
	public List getUoms() {
		
		uoms = new ArrayList<>();
		UOM uom = new UOM();
		uom.setIsactive(1);
		
		if(getProductId()>0){
			Product prod = new Product(); 
			prod = Product.retrieve(getProductId()+"");
			
			if(prod!=null && prod.getProductProperties()!=null){
			ProductProperties prop = new ProductProperties();
			prop.setIsactive(1);
			prop.setPropid(prod.getProductProperties().getPropid());
			
				for(ProductProperties p : ProductProperties.retrieve(prop, uom)){
					
					uoms.add(new SelectItem(p.getUom().getUomid(), p.getUom().getSymbol()));
				}
			}
		}
		/*for(UOM u : UOM.retrieve(uom)){
			uoms.add(new SelectItem(u.getUomid(), u.getSymbol()));
		}*/
		
		return uoms;
	}

	public void setUoms(List uoms) {
		this.uoms = uoms;
	}

	public int getUomid() {
		return uomid;
	}

	public void setUomid(int uomid) {
		this.uomid = uomid;
	}

	public String getResearchSupplier() {
		return researchSupplier;
	}

	public void setResearchSupplier(String researchSupplier) {
		this.researchSupplier = researchSupplier;
	}

	public String getReserachProduct() {
		return reserachProduct;
	}

	public void setReserachProduct(String reserachProduct) {
		this.reserachProduct = reserachProduct;
	}

	public String getAmountReturn() {
		return amountReturn;
	}

	public void setAmountReturn(String amountReturn) {
		this.amountReturn = amountReturn;
	}

	public String getQtyReturn() {
		return qtyReturn;
	}

	public void setQtyReturn(String qtyReturn) {
		this.qtyReturn = qtyReturn;
	}

	public String getRemarksReturn() {
		return remarksReturn;
	}

	public void setRemarksReturn(String remarksReturn) {
		this.remarksReturn = remarksReturn;
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

	public ProductReturnSupplier getReturnData() {
		return returnData;
	}

	public void setReturnData(ProductReturnSupplier returnData) {
		this.returnData = returnData;
	}

	public List<ProductReturnSupplier> getReturnDataPosting() {
		return returnDataPosting;
	}

	public void setReturnDataPosting(List<ProductReturnSupplier> returnDataPosting) {
		this.returnDataPosting = returnDataPosting;
	}

	public Map<Long, ProductInventory> getInventoryData() {
		return inventoryData;
	}

	public void setInventoryData(Map<Long, ProductInventory> inventoryData) {
		this.inventoryData = inventoryData;
	}

	public String getOrNumber() {
		return orNumber;
	}

	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

}
