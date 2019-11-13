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

import com.italia.ipos.controller.InputedInventoryQtyTracker;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductInventory;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.QtyRunning;
import com.italia.ipos.controller.StoreProduct;
import com.italia.ipos.controller.StoreProductTrans;
import com.italia.ipos.controller.StoreReturnWarehouse;
import com.italia.ipos.enm.ProductStatus;
import com.italia.ipos.enm.Status;
import com.italia.ipos.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 03/26/2017
 *
 */
@ManagedBean(name="storeBean")
@ViewScoped
public class StoreBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1695546546745446L;

	private List<QtyRunning> productRunning = Collections.synchronizedList(new ArrayList<QtyRunning>());
	private String searchStoreProduct;
	private List<StoreProduct> storeProducts = Collections.synchronizedList(new ArrayList<StoreProduct>());
	private List<StoreProductTrans> storeTrans = Collections.synchronizedList(new ArrayList<StoreProductTrans>());
	
	private List<StoreProductTrans> selectedPost = Collections.synchronizedList(new ArrayList<StoreProductTrans>());
	private StoreProductTrans selectedTran;
	private Map<Long, Product> storeTransData = Collections.synchronizedMap(new HashMap<Long, Product>());
	
	private String dateTrans;
	private String status;
	private String searchProduct;
	private double quantity;
	private List products;
	private long productId;
	private String uomSymbol;
	private double inventoryQty;
	
	
	private List<StoreReturnWarehouse> selectedRetunPost = Collections.synchronizedList(new ArrayList<StoreReturnWarehouse>());
	private List<StoreReturnWarehouse> retrunTrans = Collections.synchronizedList(new ArrayList<StoreReturnWarehouse>());
	private StoreReturnWarehouse selectedTranReturn;
	private String dateTransReturn;
	private String statusReturn;
	private String searchProductReturn;
	private double quantityReturn;
	private List productsReturn;
	private long productIdReturn;
	private String uomSymbolReturn;
	private double storeQtyReturn;
	private String searchReturnStoreProduct;
	private Map<Long, StoreProduct> storeTransDataReturn = Collections.synchronizedMap(new HashMap<Long, StoreProduct>());
	
	private boolean nonZero;
	private boolean loadAll;
	
	public void onTabChange(TabChangeEvent event) {
        
		if("Hold Transaction".equalsIgnoreCase(event.getTab().getTitle())){
			initFailedDispenseProductInStore();
		}else if("Store Product".equalsIgnoreCase(event.getTab().getTitle())){
			loadStore();
		}else if("Product Loading".equalsIgnoreCase(event.getTab().getTitle())){
			loadLoading();
		}else if("Return Loading".equalsIgnoreCase(event.getTab().getTitle())){
			returnLoading();
		}
		
    }
	
	public void loadLoading(){
		storeTrans = Collections.synchronizedList(new ArrayList<StoreProductTrans>());
		
		String sql = " AND (tran.datetrans>=? AND tran.datetrans<=?)";
		String[] params = new String[2];
		
		if(getSearchStoreProduct()!=null && !getSearchStoreProduct().isEmpty()){
			sql +=" AND prop.productname like '%"+ getSearchStoreProduct().replace("--", "") +"%'";
		}
		params[0] = DateUtils.getCurrentDateYYYYMMDD();
		params[1] = DateUtils.getCurrentDateYYYYMMDD();
		
		storeTrans = StoreProductTrans.retrieve(sql, params);
				
		Collections.reverse(storeTrans);
		
		loadProduct();
	}
	
	private void loadProduct() {
		products = new ArrayList<>();
		storeTransData = Collections.synchronizedMap(new HashMap<Long, Product>());
		
		//if(getSearchProduct()!=null && !getSearchProduct().isEmpty()){
			
			//String sql = " AND inv.newqty!=0 AND prop.isactive=1 AND prop.productname like '%"+ getSearchProduct().replace("--", "") +"%'";
			String sql = " AND inv.newqty!=0 AND prop.isactive=1";
			String[] params = new String[0];
			
			for(ProductInventory inv : ProductInventory.retrieve(sql, params)){
				Product prod = new Product();
				prod = inv.getProduct();
				prod.setProductProperties(ProductProperties.properties(inv.getProductProperties().getPropid()+""));
				prod.setProductInventory(inv);
				products.add(new SelectItem(prod.getProdid(), prod.getProductProperties().getProductname()));
				storeTransData.put(prod.getProdid(), prod);
			}	
	}
	
	public void loadStore(){
		storeProducts = Collections.synchronizedList(new ArrayList<StoreProduct>());
		
		String sql = "SELECT * FROM storeproduct WHERE prodIsActive=1 ";
		
		if(isNonZero()){
			sql += " AND qty!=0 ";
		}
		
		if(getSearchStoreProduct()!=null && !getSearchStoreProduct().isEmpty()){
			sql +=" AND productName like '%"+ getSearchStoreProduct().replace("--", "") +"%'";
		}else{
			if(!isLoadAll()){
				sql +=" LIMIT 10";
			}
		}
		storeProducts = StoreProduct.retrieveProduct(sql, new String[0]);
		Collections.reverse(storeProducts);
	}
	
	public void saveTrans(){
		if(Login.checkUserStatus()){
			
			boolean isOk = true;
			StoreProductTrans tran = new StoreProductTrans();
			if(getSelectedTran()!=null){
				tran = getSelectedTran();
				if(Status.POSTED.getId()==tran.getStatus()){
					isOk = false;
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Transaction is already poseted. Saving of data is not allowed", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
				
				double qty = tran.getQuantity() + getInventoryQty();
				if(getQuantity()> qty){
					isOk = false;
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Inputed quantity is greater than remaining quantity in warehouse", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
				
			}else{
			
				if(getQuantity()> getInventoryQty()){
					isOk = false;
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Inputed quantity is greater than remaining quantity in warehouse", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
			
			if(isOk){
				Product prod = getStoreTransData().get(getProductId());
				StoreProduct store = storeProductData(prod);
				if(store!=null){
				tran.setStore(store);
				tran.setProduct(prod);
				tran.setProductProperties(prod.getProductProperties());
				tran.setUom(prod.getProductProperties().getUom());
				tran.setIsActive(1);
				tran.setUserDtls(Login.getUserLogin().getUserDtls());
				tran.setQuantity(getQuantity());
				tran.setDateTrans(getDateTrans());
				tran.setStatus(Status.NEW.getId());
				tran.save();
				clearLoading();
				loadLoading();
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Saving of information has been successfully saved.", "");
		        FacesContext.getCurrentInstance().addMessage(null, message);
				}else{
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error in saving. Initial product in store is not yet specify.", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
		}
	}
	
	private StoreProduct storeProductData(Product product){
		
		StoreProduct store = null;
		
		try{
		String sql = "SELECT * FROM storeproduct WHERE prodIsActive=1 AND prodid=?";
		String[] params = new String[1];
		params[0] = product.getProdid()+"";
		try{store = StoreProduct.retrieveProduct(sql, params).get(0);}catch(IndexOutOfBoundsException ie){}
		}catch(Exception e){}
		
		return store;
	}
	
	private double calculateNewStatusQty(Product prod){
		
		String sql = "SELECT * FROM transferstoretrans WHERE tranisactive=1 AND transtatus=1 AND prodid=?";
		String[] params = new String[1];
		params[0] = prod.getProdid()+"";
		double qty = 0d;
		for(StoreProductTrans store : StoreProductTrans.retrieveProduct(sql, params)){
			qty += store.getQuantity();
		}
		
		return qty;
	}
	
	private double calculateNewStatusReturnQty(Product prod){
		
		String sql = "SELECT * FROM storetowarehousetrans WHERE tranisactive=1 AND transtatus=1 AND prodid=?";
		String[] params = new String[1];
		params[0] = prod.getProdid()+"";
		double qty = 0d;
		for(StoreReturnWarehouse store : StoreReturnWarehouse.retrieveProduct(sql, params)){
			qty += store.getQuantity();
		}
		
		return qty;
	}
	
	public void postedLoading(){
		
		if(getSelectedPost()!=null){
			
			
			if(getSelectedPost().size()>0){
				
				int cnt = 0;
				for(StoreProductTrans tran : getSelectedPost()){
					if(Status.NEW.getId()==tran.getStatus()){
						cnt++;
						//modify inventory
						ProductInventory.invtoryqty(false, tran);
						tran.setStatus(Status.POSTED.getId());
						tran.setIsActive(1);
						tran.setUserDtls(Login.getUserLogin().getUserDtls());
						tran.save();
						
						//truck product qty
						productActions(true, tran);
						
						StoreProduct storeProd = null;
						String sql = "SELECT * FROM storeproduct WHERE barcode=?";
						String[] params = new String[1];
						params[0] = tran.getProduct().getBarcode();
						
						try{storeProd = StoreProduct.retrieveProduct(sql, params).get(0);}catch(IndexOutOfBoundsException io){}
						if(storeProd!=null){
							
							double newQty = 0d;
							newQty = storeProd.getQuantity() + tran.getQuantity();
							storeProd.setQuantity(newQty);
							storeProd.save();
							
						}
						
					}
				}
			
				
				if(cnt>0){
					clearLoading();
					loadLoading();
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, (cnt==1? "1 product has" : cnt+" products have") + " been posted.", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}else{
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No product has been posted", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}else{
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a product first.", "");
		        FacesContext.getCurrentInstance().addMessage(null, message);
			}
			
		}else{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a product first.", "");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
		
	}
	
	private void productActions(boolean isPullOut, Object obj){
		
		Product prod = new Product();
		prod.setIsactiveproduct(1);
		
		if(isPullOut){
			StoreProductTrans tran = (StoreProductTrans) obj;
			prod.setProdid(tran.getProduct().getProdid());
			ProductInventory inv = ProductInventory.retrieve(prod).get(0);
			inv.setAddqty(-tran.getQuantity());
			inv.setUserDtls(Login.getUserLogin().getUserDtls());
			InputedInventoryQtyTracker.saveQty(inv,"PRODUCT TRANSFER TO STORE");
		}else{
			StoreReturnWarehouse tran = (StoreReturnWarehouse) obj;
			ProductInventory inv = ProductInventory.retrieve(prod).get(0);
			inv.setAddqty(tran.getQuantity());
			inv.setUserDtls(Login.getUserLogin().getUserDtls());
			InputedInventoryQtyTracker.saveQty(inv,"PRODUCT TRANSFER TO WAREHOUSE");
		}
	}
	
	public void clearLoading(){
		selectedPost = Collections.synchronizedList(new ArrayList<StoreProductTrans>());
		storeTransData = Collections.synchronizedMap(new HashMap<Long, Product>());
		setInventoryQty(0);
		setSearchProduct(null);
		setQuantity(0);
		setDateTrans(null);
		setProductId(0);
		setUomSymbol(null);
		setStatus(Status.NEW.getName());
		setSelectedTran(null);
	}
	
	public void clickItem(StoreProductTrans trans){
		setQuantity(trans.getQuantity());
		setDateTrans(trans.getDateTrans());
		setProductId(trans.getProduct().getProdid());
		setUomSymbol(trans.getUom().getSymbol());
		setStatus(Status.typeName(trans.getStatus()));
		setSelectedTran(trans);
		
			
			String sql = " AND prd.prodid=?";
			String[] params = new String[1];
			params[0] = trans.getProduct().getProdid()+"";
			ProductInventory inv = null;
			try{inv = ProductInventory.retrieve(sql, params).get(0);}catch(IndexOutOfBoundsException e){}
			if(inv!=null){
				Product product = trans.getProduct();
				/*product.setProductProperties(ProductProperties.properties(inv.getProductProperties().getPropid()+""));
				product.setProductInventory(inv);*/
				getStoreTransData().put(product.getProdid(), product);
				products = new ArrayList<>();
				products.add(new SelectItem(inv.getProduct().getProdid(), inv.getProductProperties().getProductname()));
				setSearchProduct(inv.getProductProperties().getProductname());
				double reaminingQty = inv.getNewqty() - calculateNewStatusQty(inv.getProduct());
				setInventoryQty(reaminingQty);
				//productQty();
			}
	}
	
	public void productQty(){
		if(getStoreTransData()!=null && getStoreTransData().size()>0){
			
			if(getStoreTransData().containsKey(getProductId())){
				
				double qty = 0d;
				qty = getStoreTransData().get(getProductId()).getProductInventory().getNewqty();
				String uomSymbol = getStoreTransData().get(getProductId()).getProductProperties().getUom().getSymbol();
				setUomSymbol(uomSymbol);
				
				double reaminingQty = qty - calculateNewStatusQty(getStoreTransData().get(getProductId()));
				setQuantity(reaminingQty);
				setInventoryQty(reaminingQty);
			}
			
		}
	}
	
	public void productStoreQty(){
		if(getStoreTransDataReturn()!=null && getStoreTransDataReturn().size()>0){
			if(getStoreTransDataReturn().containsKey(getProductIdReturn())){
				
				double qty = 0d;
				qty = getStoreTransDataReturn().get(getProductIdReturn()).getQuantity();
				
				String uomSymbol = getStoreTransDataReturn().get(getProductIdReturn()).getUomSymbol();
				setUomSymbolReturn(uomSymbol);
				
				double reaminingQty = qty - calculateNewStatusReturnQty(getStoreTransDataReturn().get(getProductIdReturn()).getProduct());
				setQuantityReturn(reaminingQty);
				setStoreQtyReturn(reaminingQty);
			}
		}
	}
	
	public void returnLoading(){
		retrunTrans = Collections.synchronizedList(new ArrayList<StoreReturnWarehouse>());
		
		String sql = " AND (tran.datetrans>=? AND tran.datetrans<=?)";
		String[] params = new String[2];
		
		if(getSearchReturnStoreProduct()!=null && !getSearchReturnStoreProduct().isEmpty()){
			sql +=" AND prop.productname like '%"+ getSearchReturnStoreProduct().replace("--", "") +"%'";
		}
		params[0] = DateUtils.getCurrentDateYYYYMMDD();
		params[1] = DateUtils.getCurrentDateYYYYMMDD();
		
		retrunTrans = StoreReturnWarehouse.retrieve(sql, params);
		
		Collections.reverse(retrunTrans);
	}
	
	public void saveReturn(){
		if(Login.checkUserStatus()){
			
			boolean isOk = true;
			StoreReturnWarehouse tran = new StoreReturnWarehouse();
			if(getSelectedTranReturn()!=null){
				tran = getSelectedTranReturn();
				if(Status.POSTED.getId()==tran.getStatus()){
					isOk = false;
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Transaction is already poseted. Saving of data is not allowed", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
				
				double qty = tran.getQuantity() + getStoreQtyReturn();
				if(getQuantityReturn()> qty){
					isOk = false;
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Inputed quantity is greater than remaining quantity in warehouse", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
				
			}else{
			
				if(getQuantityReturn()> getStoreQtyReturn()){
					isOk = false;
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Inputed quantity is greater than remaining quantity in warehouse", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
			
			if(isOk){
				Product prod = getStoreTransDataReturn().get(getProductIdReturn()).getProduct();
				StoreProduct store = getStoreTransDataReturn().get(getProductIdReturn());
				if(store!=null){
				tran.setStore(store);
				tran.setProduct(prod);
				tran.setProductProperties(getStoreTransDataReturn().get(getProductIdReturn()).getProductProperties());
				tran.setUom(getStoreTransDataReturn().get(getProductIdReturn()).getUom());
				tran.setIsActive(1);
				tran.setUserDtls(Login.getUserLogin().getUserDtls());
				tran.setQuantity(getQuantityReturn());
				tran.setDateTrans(getDateTransReturn());
				tran.setStatus(Status.NEW.getId());
				tran.save();
				clearReturn();
				returnLoading();
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Saving of information has been successfully saved.", "");
		        FacesContext.getCurrentInstance().addMessage(null, message);
				}else{
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error in saving. Initial product in store is not yet specify.", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}
		}
	}
	
	public void postedReturn(){
		
		if(getSelectedRetunPost()!=null){
			
			
			if(getSelectedRetunPost().size()>0){
				
				int cnt = 0;
				for(StoreReturnWarehouse tran : getSelectedRetunPost()){
					if(Status.NEW.getId()==tran.getStatus()){
						
						double qty = StoreProduct.storeQuantity(false, tran.getProduct(), tran.getQuantity());
						tran.setQuantity(qty); //get the actual quantity to return to warehouse//do not move this code
						
						//modify inventory
						ProductInventory.invtoryqty(true, tran);
						
						//truck product qty
						productActions(false, tran);
						
						tran.setStatus(Status.POSTED.getId());
						tran.setUserDtls(Login.getUserLogin().getUserDtls());
						tran.save();
						cnt++;
					}
				}
			
				
				if(cnt>0){
					clearReturn();
					returnLoading();
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, (cnt==1? "1 product has" : cnt+" products have") + " been posted.", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}else{
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No product has been posted", "");
			        FacesContext.getCurrentInstance().addMessage(null, message);
				}
			}else{
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a product first.", "");
		        FacesContext.getCurrentInstance().addMessage(null, message);
			}
			
		}else{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a product first.", "");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	public void clearReturn(){
		selectedRetunPost = Collections.synchronizedList(new ArrayList<StoreReturnWarehouse>());
		storeTransDataReturn = Collections.synchronizedMap(new HashMap<Long, StoreProduct>());
		setStoreQtyReturn(0);
		setSearchProductReturn(null);
		setQuantityReturn(0);
		setDateTransReturn(null);
		setProductIdReturn(0);
		setUomSymbolReturn(null);
		setStatusReturn(Status.NEW.getName());
		setSelectedTranReturn(null);
	}
	
	public void clickItemReturn(StoreReturnWarehouse trans){
		
		setQuantityReturn(trans.getQuantity());
		setDateTransReturn(trans.getDateTrans());
		setProductIdReturn(trans.getProduct().getProdid());
		setUomSymbolReturn(trans.getUom().getSymbol());
		setStatusReturn(Status.typeName(trans.getStatus()));
		setSelectedTranReturn(trans);
		
			
		String sql = " AND store.prodIsActive=1 AND store.qty!=0 AND store.prodid=?";
		String[] params = new String[1];
		params[0] = trans.getProduct().getProdid()+"";
		double qty = 0d;
		StoreProduct store = null;
		try{store = StoreProduct.retrieve(sql, params).get(0);}catch(IndexOutOfBoundsException ie){}
		
		if(store!=null){
			getStoreTransDataReturn().put(store.getProduct().getProdid(), store);
			productsReturn = new ArrayList<>();
			productsReturn.add(new SelectItem(store.getProduct().getProdid(), store.getProductName()));
			setSearchProductReturn(store.getProductName());
			double reaminingQty = store.getQuantity() - calculateNewStatusReturnQty(store.getProduct());
			setStoreQtyReturn(reaminingQty);
		}
			
		
		
	}
	
	@PostConstruct
	public void init(){
		initFailedDispenseProductInStore();
	}
	
	public void initFailedDispenseProductInStore(){
		
		productRunning = Collections.synchronizedList(new ArrayList<QtyRunning>());
		
		QtyRunning xqty = new QtyRunning();
		xqty.setIsqtyactive(1);
		xqty.setQtystatus(ProductStatus.ON_QUEUE.getId());
		
		for(QtyRunning rn : QtyRunning.retrieve(xqty)){
			ProductProperties prop = new ProductProperties();
			prop = ProductProperties.properties(rn.getProduct().getProductProperties().getPropid()+"");
			rn.getProduct().setProductProperties(prop);
			
			productRunning.add(rn);
		}
		
	}
	
	public void addToInventoryProductFailed(){
		if(productRunning!=null && productRunning.size()>0){
				
				QtyRunning xqty = new QtyRunning();
				xqty.setIsqtyactive(1);
				xqty.setQtystatus(ProductStatus.ON_QUEUE.getId());
				int cnt =0;
				for(QtyRunning rn : QtyRunning.retrieve(xqty)){
					rn.setQtystatus(ProductStatus.VOID.getId());
					rn.setQtyremarks(ProductStatus.VOID.getName());
					
					//ProductInventory.invtoryqty(true, rn.getProduct(), rn.getQtyhold());
					StoreProduct.storeQuantity(true, rn.getProduct(), rn.getQtyhold());
					
					rn.save();
					cnt++;
				}
				initFailedDispenseProductInStore();
				
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully recalled "+ (cnt==1? cnt +" item" : cnt + " items") +" quantity.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public List<QtyRunning> getProductRunning() {
		return productRunning;
	}

	public void setProductRunning(List<QtyRunning> productRunning) {
		this.productRunning = productRunning;
	}

	public List<StoreProduct> getStoreProducts() {
		return storeProducts;
	}

	public void setStoreProducts(List<StoreProduct> storeProducts) {
		this.storeProducts = storeProducts;
	}

	public String getSearchStoreProduct() {
		return searchStoreProduct;
	}

	public void setSearchStoreProduct(String searchStoreProduct) {
		this.searchStoreProduct = searchStoreProduct;
	}

	public List<StoreProductTrans> getStoreTrans() {
		return storeTrans;
	}

	public void setStoreTrans(List<StoreProductTrans> storeTrans) {
		this.storeTrans = storeTrans;
	}

	public List<StoreProductTrans> getSelectedPost() {
		return selectedPost;
	}

	public void setSelectedPost(List<StoreProductTrans> selectedPost) {
		this.selectedPost = selectedPost;
	}

	public String getDateTrans() {
		if(dateTrans==null){
			dateTrans = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}

	public String getStatus() {
		if(status==null){
			status = Status.NEW.getName();
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSearchProduct() {
		return searchProduct;
	}

	public void setSearchProduct(String searchProduct) {
		this.searchProduct = searchProduct;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public List getProducts() {
		return products;
	}

	
	public void nonZeroMessage(){
		String summary = isNonZero() ? "Filtered with non-zero quantity" : "Filtered with zero and non-zero quantity";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, "");
        FacesContext.getCurrentInstance().addMessage(null, message);
        
        loadStore();
        
	}
	
	public void loadAllMessage(){
		String summary = isLoadAll() ? "Load All Produt" : "Load only latest added product";
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, "");
        FacesContext.getCurrentInstance().addMessage(null, message);
        
        loadStore();
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

	public String getUomSymbol() {
		return uomSymbol;
	}

	public void setUomSymbol(String uomSymbol) {
		this.uomSymbol = uomSymbol;
	}

	public double getInventoryQty() {
		return inventoryQty;
	}

	public void setInventoryQty(double inventoryQty) {
		this.inventoryQty = inventoryQty;
	}

	public Map<Long, Product> getStoreTransData() {
		return storeTransData;
	}

	public void setStoreTransData(Map<Long, Product> storeTransData) {
		this.storeTransData = storeTransData;
	}

	public StoreProductTrans getSelectedTran() {
		return selectedTran;
	}

	public void setSelectedTran(StoreProductTrans selectedTran) {
		this.selectedTran = selectedTran;
	}

	public List<StoreReturnWarehouse> getRetrunTrans() {
		return retrunTrans;
	}

	public void setRetrunTrans(List<StoreReturnWarehouse> retrunTrans) {
		this.retrunTrans = retrunTrans;
	}

	public List<StoreReturnWarehouse> getSelectedRetunPost() {
		return selectedRetunPost;
	}

	public void setSelectedRetunPost(List<StoreReturnWarehouse> selectedRetunPost) {
		this.selectedRetunPost = selectedRetunPost;
	}

	public String getDateTransReturn() {
		if(dateTransReturn==null){
			dateTransReturn = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTransReturn;
	}

	public void setDateTransReturn(String dateTransReturn) {
		this.dateTransReturn = dateTransReturn;
	}

	public String getStatusReturn() {
		return statusReturn;
	}

	public void setStatusReturn(String statusReturn) {
		this.statusReturn = statusReturn;
	}

	public String getSearchProductReturn() {
		return searchProductReturn;
	}

	public void setSearchProductReturn(String searchProductReturn) {
		this.searchProductReturn = searchProductReturn;
	}

	public double getQuantityReturn() {
		return quantityReturn;
	}

	public void setQuantityReturn(double quantityReturn) {
		this.quantityReturn = quantityReturn;
	}

	public List getProductsReturn() {
		
		productsReturn = new ArrayList<>();
		storeTransDataReturn = Collections.synchronizedMap(new HashMap<Long, StoreProduct>());
		
		String sql = " AND store.prodIsActive AND store.qty!=0 ";
		if(getSearchProductReturn()!=null && !getSearchProductReturn().isEmpty()){
			sql += " AND store.productName like '%"+ getSearchProductReturn().replace("--", "") +"%'";
		}
		for(StoreProduct store : StoreProduct.retrieve(sql, new String[0])){
			productsReturn.add(new SelectItem(store.getProduct().getProdid(), store.getProductName()));
			storeTransDataReturn.put(store.getProduct().getProdid(), store);
		}
		
		return productsReturn;
	}

	public void setProductsReturn(List productsReturn) {
		this.productsReturn = productsReturn;
	}

	public long getProductIdReturn() {
		return productIdReturn;
	}

	public void setProductIdReturn(long productIdReturn) {
		this.productIdReturn = productIdReturn;
	}

	public String getUomSymbolReturn() {
		return uomSymbolReturn;
	}

	public void setUomSymbolReturn(String uomSymbolReturn) {
		this.uomSymbolReturn = uomSymbolReturn;
	}

	public double getStoreQtyReturn() {
		return storeQtyReturn;
	}

	public void setStoreQtyReturn(double storeQtyReturn) {
		this.storeQtyReturn = storeQtyReturn;
	}

	public Map<Long, StoreProduct> getStoreTransDataReturn() {
		return storeTransDataReturn;
	}

	public void setStoreTransDataReturn(Map<Long, StoreProduct> storeTransDataReturn) {
		this.storeTransDataReturn = storeTransDataReturn;
	}

	public StoreReturnWarehouse getSelectedTranReturn() {
		return selectedTranReturn;
	}

	public void setSelectedTranReturn(StoreReturnWarehouse selectedTranReturn) {
		this.selectedTranReturn = selectedTranReturn;
	}

	public String getSearchReturnStoreProduct() {
		return searchReturnStoreProduct;
	}

	public void setSearchReturnStoreProduct(String searchReturnStoreProduct) {
		this.searchReturnStoreProduct = searchReturnStoreProduct;
	}

	public boolean isNonZero() {
		return nonZero;
	}

	public void setNonZero(boolean nonZero) {
		this.nonZero = nonZero;
	}

	public boolean isLoadAll() {
		return loadAll;
	}

	public void setLoadAll(boolean loadAll) {
		this.loadAll = loadAll;
	}
}
