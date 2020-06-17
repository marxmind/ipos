package com.italia.ipos.bean;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

import com.italia.ipos.barcode.Reports;
import com.italia.ipos.controller.InputedInventoryQtyTracker;
import com.italia.ipos.controller.IposType;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductBrand;
import com.italia.ipos.controller.ProductCategory;
import com.italia.ipos.controller.ProductGroup;
import com.italia.ipos.controller.ProductInventory;
import com.italia.ipos.controller.ProductPricingTrans;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.ProductRunning;
import com.italia.ipos.controller.QtyRunning;
import com.italia.ipos.controller.StoreProduct;
import com.italia.ipos.controller.UOM;
import com.italia.ipos.enm.IStore;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.ProductStatus;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.reports.ReadXML;
import com.italia.ipos.reports.ReportCompiler;
import com.italia.ipos.reports.ReportTag;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;
import com.italia.ipos.utils.Whitelist;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author mark italia
 * @since 10/02/2016
 *@version 1.0
 */
@ManagedBean(name="aprodBean", eager=true)
@SessionScoped
public class AdminProductBean implements Serializable{

	private static final long serialVersionUID = 1094801425228384663L;
	
	/**
	 * UOM Properties
	 */
	private List<UOM> uoms = Collections.synchronizedList(new ArrayList<UOM>());
	private String uomname = "Input Here";
	private String symbol = "Input Here";
	private UOM uomdata;
	private String searchUOM;
	
	/**
	 * Product Category Properties
	 */
	private List<ProductCategory> cats = Collections.synchronizedList(new ArrayList<ProductCategory>());
	private String productCatDesc = "Input Here";
	private ProductCategory prodcatData;
	private String searchCategory;
	
	/**
	 * Product Properties
	 */
	private List<ProductProperties> props = Collections.synchronizedList(new ArrayList<ProductProperties>());
	private ProductProperties productPropData;
	private String docFileExt;
	private String documentPath;
	private InputStream docImage;
	private String productPropertyDesc = "Input Here";
	private String propUomId;
	private List propUoms = new ArrayList<>();
	private String propCatId;
	private List propCats = new ArrayList<>();
	private String propGroupId;
	private List propGroups = new ArrayList<>();
	private String propBrandId;
	private List propBrands = new ArrayList<>();
	private String productCode;
	private List propCodes = new ArrayList<>();
	private String propCodeId;
	private boolean lockBusinessSelection;
	private String searchProperties;
	
	/**
	 * Product Pricing Properties
	 */
	private List<Product> pricingstans = Collections.synchronizedList(new ArrayList<Product>());
	private ProductPricingTrans pricingData;
	private String purchasePrice;
	private String sellingPrice;
	private String netPrice;
	private String purchasePricetmp;
	private String sellingPricetmp;
	private String netPricetmp;
	private String searchProductpricing;
	private String errormsgprice;
	private boolean isvalidamount;
	private double taxpercentagetmp;
	private double taxpercentage;
	
	
	/**
	 * Product Group Properties
	 */
	private List<ProductGroup> groups = Collections.synchronizedList(new ArrayList<ProductGroup>());
	private ProductGroup productGroupData;
	private String productGroupDesc = "Input Here";
	private String searchGroup;
	/**
	 * Product Brand
	 */
	private List<ProductBrand> brands = Collections.synchronizedList(new ArrayList<ProductBrand>());
	private ProductBrand productBrandData;
	private String productBrandCode;
	private String productBrandName = "Input Here";
	private String searchBrand;
	/**
	 * Product quantity
	 */
	private List<Product> invs = Collections.synchronizedList(new ArrayList<Product>());
	private ProductInventory inventoryData;
	private double inventoryNewQty;
	private double inventoryOldQty;
	private String errorqtymsg;
	private String searchProdQty;
	private List<Product> quantityData;
	
	private String searchProduct;
	private List<Product> prods = Collections.synchronizedList(new ArrayList<Product>());
	
	
	/**
	 * 
	 * Product with barcode
	 */
	private String searchProductCode;
	private List<Product> bars = Collections.synchronizedList(new ArrayList<Product>());
	private Product productbar;
	private String barcode;
	private String datecoded;
	private String searchProductItem;
	private List<ProductProperties> prodItems = Collections.synchronizedList(new ArrayList<ProductProperties>());
	private ProductProperties selectedItem;
	private String expirationdate;
	private boolean lockBarcode;
	
	private String msgId;
	
	//private List<QtyRunning> productRunning = Collections.synchronizedList(new ArrayList<QtyRunning>());
	
	public void onTabChange(TabChangeEvent event) {
        
		/*if("Dispense in Store".equalsIgnoreCase(event.getTab().getTitle())){
			initFailedDispenseProductInStore();*/
		if("Quantity Adjustment".equalsIgnoreCase(event.getTab().getTitle())){
			initProductQty();
		}else if("Price Setup".equalsIgnoreCase(event.getTab().getTitle())){
			initPricing();
		}else if("Barcode".equalsIgnoreCase(event.getTab().getTitle())){
			initBarcode();
			
			int cnt = countForNewBarcode(); 
			if(cnt>0){
			
			FacesMessage msg = new FacesMessage("Barcode", (cnt==1? "1 item is " : cnt +" items are ") + "subject for barcode creation. Please click New button.");
	        FacesContext.getCurrentInstance().addMessage(null, msg);
	        
			}
	        
			setMsgId("msgbar");
			
		}else if("Properties".equalsIgnoreCase(event.getTab().getTitle())){
			initProperty();
		}else if("Category".equalsIgnoreCase(event.getTab().getTitle())){
			initCategory();
		}else if("Unit Of Measurement".equalsIgnoreCase(event.getTab().getTitle())){
			initUOM();
		}else if("Group Type".equalsIgnoreCase(event.getTab().getTitle())){
			initProdGroup();
		}else if("Brand".equalsIgnoreCase(event.getTab().getTitle())){
			initBrand();
		}
		
    }
	
	/*public void addToInventoryProductFailed(){
		if(productRunning!=null && productRunning.size()>0){
				
				QtyRunning xqty = new QtyRunning();
				xqty.setIsqtyactive(1);
				xqty.setQtystatus(ProductStatus.ON_QUEUE.getId());
				int cnt =0;
				for(QtyRunning rn : QtyRunning.retrieve(xqty)){
					rn.setQtystatus(ProductStatus.VOID.getId());
					rn.setQtyremarks(ProductStatus.VOID.getName());
					
					ProductInventory.invtoryqty(true, rn.getProduct(), rn.getQtyhold());
					
					rn.save();
					cnt++;
				}
				initFailedDispenseProductInStore();
				
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully recalled "+ (cnt==1? cnt +" item" : cnt + " items") +" quantity.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}*/
	
	/*public void initFailedDispenseProductInStore(){
		
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
			
	}*/
	
	public int countForNewBarcode(){
		System.out.println("start countForNewBarcode");
		String sql = " AND prd.isactive=1 AND prd.timestamp>=? AND prd.timestamp<=? ORDER BY prd.timestamp DESC";
		String[] params = new String[2];
		params[0] = DateUtils.getCurrentDateYYYYMMDD() + " 00:00:00";
		params[1] = DateUtils.getCurrentDateYYYYMMDD() + " 23::59:00";
		int cnt =0;
		for(ProductProperties prop : ProductProperties.retrieve(sql, params)){
			System.out.println("collecting product...");
			ProductProperties prp = new ProductProperties();
			prp.setIsactive(1);
			prp.setPropid(prop.getPropid());
			
			Product prd = new Product();
			prd.setIsactiveproduct(1);
			Product prod = null;
			try{prod = Product.retrieve(prd,prp).get(0);}catch(IndexOutOfBoundsException e){}
			
			if(prod==null){
			cnt +=1;
			System.out.println("counting... " + cnt);
			}
			
		}
		System.out.println("d countForNewBarcode");
		return cnt;
	}
	
	public List<Product> getProds() {
		return prods;
	}
	
	public String getSearchProduct() {
		return searchProduct;
	}

	public void setSearchProduct(String searchProduct) {
		this.searchProduct = searchProduct;
	}
	
	@ManagedProperty("#{productBean}")
	private ProductBean products;
	
	public void setProducts(ProductBean products){
		this.products = products;
	}
	
	public boolean isProductAlreadyInWareHouse(Object obj){
		
		if(obj instanceof ProductProperties){
			ProductProperties prop = (ProductProperties)obj;
			ProductProperties prp = new ProductProperties();
			prp.setPropid(prop.getPropid());
			prp.setIsactive(1);
			
			Product prod = new Product();
			prod.setIsactiveproduct(1);
			Product prd = null;
			
			try{prd = Product.retrieve(prod,prp).get(0);}catch(IndexOutOfBoundsException e){}
			if(prd!=null){
				return true;
			}		
		}
		
		if(obj instanceof Product){
			Product prod = (Product)obj;
			Product prd = new Product();
			prd.setProdid(prod.getProdid());
			prd.setIsactiveproduct(1);
			ProductInventory inv = null;
			ProductInventory invs = new ProductInventory();
			invs.setIsactive(1);
			try{inv = ProductInventory.retrieve(invs,prd).get(0);}catch(IndexOutOfBoundsException e){}
			if(inv!=null){
				return true;
			}
		}
		
		if(obj instanceof ProductBrand){
			ProductBrand brand = (ProductBrand)obj;
			ProductBrand brd = new ProductBrand();
			brd.setProdbrandid(brand.getProdbrandid());
			brd.setIsactive(1);
			
			ProductProperties prop = new ProductProperties();
			prop.setIsactive(1);
			
			ProductProperties prp = null;
			try{prp = ProductProperties.retrieve(prop,brd).get(0);}catch(IndexOutOfBoundsException e){}
			if(prp!=null){
				return true;
			}
			
		}
		
		if(obj instanceof ProductGroup){
			ProductGroup group = (ProductGroup)obj;
			ProductGroup grp = new ProductGroup();
			grp.setProdgroupid(group.getProdgroupid());
			grp.setIsactive(1);
			
			
			ProductProperties prop = new ProductProperties();
			prop.setIsactive(1);
			
			ProductProperties prp = null;
			try{prp = ProductProperties.retrieve(prop,grp).get(0);}catch(IndexOutOfBoundsException e){}
			if(prp!=null){
				return true;
			}
			
		}
		
		if(obj instanceof UOM){
			UOM uom = (UOM)obj;
			UOM om = new UOM();
			om.setUomid(uom.getUomid());
			om.setIsactive(1);
			
			
			ProductProperties prop = new ProductProperties();
			prop.setIsactive(1);
			
			ProductProperties prp = null;
			try{prp = ProductProperties.retrieve(prop,om).get(0);}catch(IndexOutOfBoundsException e){}
			if(prp!=null){
				return true;
			}
			
		}
		
		if(obj instanceof ProductCategory){
			ProductCategory cat = (ProductCategory)obj;
			ProductCategory ct = new ProductCategory();
			ct.setProdcatid(cat.getProdcatid());
			ct.setIsactive(1);
			
			ProductProperties prop = new ProductProperties();
			prop.setIsactive(1);
			
			ProductProperties prp = null;
			try{prp = ProductProperties.retrieve(prop,ct).get(0);}catch(IndexOutOfBoundsException e){}
			if(prp!=null){
				return true;
			}
			
		}
		
		return false;
	}
	
	public void barcodeChecker(){
		if(isBarcodeExist()){
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: This barcode " + getBarcode().replace("--", "") + " is already in use.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            String barcode = getBarcode().substring(0, 10);
            System.out.println("bacode >>>> " + barcode);
            setBarcode(barcode);
		}
	}
	
	public boolean isBarcodeExist(){
		
		if(!getBarcode().isEmpty()){
			String code = getBarcode();
			int len = code.length();
			if(len==12){
				String sql = "SELECT * FROM product WHERE barcode=?";
				String[] params = new String[1];
				params[0] = code.replace("--", "");
				List<Product> prods = Product.retrieve(sql, params);
				if(prods.size()>0){
		            return true;
				}
			}
		}
		
		return false;
	}
	
	public void savebar(){
		if(Login.checkUserStatus()){
			
			boolean isOk = true;
			Product product = new Product();
			if(getProductbar()!=null){
				product = getProductbar();
			}else{
				
				if(getSelectedItem()==null){
					isOk = false;
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No product selected yet.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
				if(isBarcodeExist()){
					isOk = false;
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "This barcode " + getBarcode().replace("--", "") + " is already in use.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
				product.setDatecoded(DateUtils.getCurrentDateYYYYMMDD());
			}
			
			if(getBarcode()!=null && !getBarcode().isEmpty()){
				/*int len = getBarcode().length();
				if(len!=12){
					isOk = false;
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Barcode must be 12 digits", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}*/
			}else{
				isOk = false;
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No barcode has been specefied", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			if(isOk){
			ProductProperties prop = getSelectedItem();
			product.setProductExpiration(getExpirationdate());
			product.setBarcode(getBarcode());
			product.setProductProperties(prop);
			product.setUserDtls(Login.getUserLogin().getUserDtls());
			product.setIsactiveproduct(1);
			product.save();
			updateStoreBarcode(prop, getBarcode());
			initBarcode();
			//clearBarcodeFields();
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data has been successfully saved.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            
			}
			
            
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	private void updateStoreBarcode(ProductProperties prop, String barcode){
		
		ProductProperties prod = new ProductProperties();
		prod.setPropid(prop.getPropid());
		prod.setIsactive(1);
		
		StoreProduct store = new StoreProduct();
		store.setIsActive(1);
		List<StoreProduct> stores = StoreProduct.retrieve(store,prod);
		if(stores.size()>0){
			stores.get(0).setBarcode(barcode);
			stores.get(0).save();
			System.out.println("barcode successfully changed...");
		}else{
			System.out.println("No retrieve data...");
		}
	}
	
	public void newProductBarcode(){
		setLockBarcode(false);
		clearBarcodeFields();
		initProductItem();
	}
	
	public void initProductItem(){
		
		prodItems = Collections.synchronizedList(new ArrayList<ProductProperties>());
		if(getSearchProductItem()!=null && !getSearchProductItem().isEmpty()){
			ProductProperties prp = new ProductProperties();
			prp.setIsactive(1);
			prp.setProductname(Whitelist.remove(getSearchProductItem()));
			
			if(getProductbar()!=null){
				prodItems = ProductProperties.retrieve(prp);
			}else{
			
				for(ProductProperties prop : ProductProperties.retrieve(prp)){
					
					
					
					ProductProperties pp = new ProductProperties();
					pp.setIsactive(1);
					pp.setPropid(prop.getPropid());
					
					Product prd = new Product();
					prd.setIsactiveproduct(1);
					Product prod = null;
					
					try{prod = Product.retrieve(prd,pp).get(0);}catch(IndexOutOfBoundsException e){}
					
					if(prod==null){
						prodItems.add(prop);
					}
					
				}
			
			}
			
		}else{
			String sql = " AND prd.isactive=1 AND prd.timestamp>=? AND prd.timestamp<=? ORDER BY prd.timestamp DESC";
			String[] params = new String[2];
			params[0] = DateUtils.getCurrentDateYYYYMMDD() + " 00:00:00";
			params[1] = DateUtils.getCurrentDateYYYYMMDD() + " 23::59:00";
			for(ProductProperties prop : ProductProperties.retrieve(sql, params)){
				
				ProductProperties prp = new ProductProperties();
				prp.setIsactive(1);
				prp.setPropid(prop.getPropid());
				
				Product prd = new Product();
				prd.setIsactiveproduct(1);
				Product prod = null;
				
				 try{prod = Product.retrieve(prd,prp).get(0);}catch(IndexOutOfBoundsException e){}
				if(prod==null){
					UOM uom = UOM.uom(prop.getUom().getUomid()+"");
					prop.setUom(uom);
					prodItems.add(prop);
				}
			}
		}
	}
	
	public void initBarcode(){
		
		/*Product product = new Product();
		product.setIsactiveproduct(1);
		
		ProductProperties prop = new ProductProperties();
		prop.setIsactive(1);
		if(getSearchProductCode()!=null && !getSearchProductCode().isEmpty()){
			prop.setProductname(getSearchProductCode());
		}*/
		
		List<Product> products = Collections.synchronizedList(new ArrayList<Product>());
		String sqlProd = "";
		String[] paramsProd = new String[0];
		boolean isOk = false;
		if(getSearchProductCode()!=null && !getSearchProductCode().isEmpty()){
			
			int len = getSearchProductCode().length();
			if(len>=4){
				bars = Collections.synchronizedList(new ArrayList<Product>());
				sqlProd = " AND prod.isactiveproduct=1 AND prop.isactive=1  AND prop.productname like '%"+ getSearchProductCode().replace("--", "") +"%'";
				isOk = true;
			}else{
				isOk = false;
			}
		}else{
			bars = Collections.synchronizedList(new ArrayList<Product>());
			sqlProd = " AND prod.isactiveproduct=1 AND prop.isactive=1 order by prod.prodid desc limit 10";
			isOk = true;
		}
		
		products = Product.retrieve(sqlProd, paramsProd);
		
		if(products!=null && products.size()>0 && isOk){
			for(Product prd : products){
				prd.setProductProperties(ProductProperties.properties(prd.getProductProperties().getPropid()+"")); //retrieve product properties in order to load UOM - subject for enhancement
				bars.add(prd);
			}
			//Collections.reverse(bars);
		}
		
	}
	
	public void initProduct(){
		prods = Collections.synchronizedList(new ArrayList<Product>());
		if(getSearchProduct()!=null){
			Product prod = new Product();
			prod.setIsactiveproduct(1);
			
			ProductProperties prop = new ProductProperties();
			prop.setProductname(getSearchProduct());
			prop.setIsactive(1);
			System.out.println("start searching product....");
			prods = products.productList(prod,prop);
		}
		
		
		
		Collections.reverse(prods);
		
	}
	
	@PostConstruct
	public void init(){
		//initFailedDispenseProductInStore();
		initProductQty();
		/*initPricing();
		initProperty();
		initCategory();
		initUOM();
		initProdGroup();
		initBrand();*/
		//loadPropertyComboList(); //load list item in combo box of Property Profile
	}
	
	public void initProductQty(){
		
		//if(getSearchProdQty()!=null){
		
			/*Product prod = new Product();
			prod.setIsactiveproduct(1);
			ProductProperties prop = new ProductProperties();
			prop.setProductname(getSearchProdQty());*/
			boolean isOk = false;
			List<Product> products = Collections.synchronizedList(new ArrayList<Product>());
			String sqlProd = "";
			String[] paramsProd = new String[0];
			
			if(getSearchProdQty()!=null && !getSearchProdQty().isEmpty()){
				int len = getSearchProdQty().length();
				if(len>=4){
					invs = Collections.synchronizedList(new ArrayList<Product>());
					sqlProd = " AND prod.isactiveproduct=1 AND prop.isactive=1 AND prop.productname like '%"+ getSearchProdQty().replace("--", "") +"%'";
					isOk = true;
				}else{
					isOk=false;
				}
			}else{
				invs = Collections.synchronizedList(new ArrayList<Product>());
				sqlProd = "  AND prod.isactiveproduct=1 AND prop.isactive=1 order by prod.prodid desc limit 10";
				isOk = true;
			}
			
			products = Product.retrieve(sqlProd, paramsProd);
			
			if(products!=null && products.size()>0 && isOk){
				
			for(Product pr : products){
				
				Product prd = new Product();
				prd.setProdid(pr.getProdid());
				prd.setIsactiveproduct(1);
				
				ProductInventory iv = new ProductInventory();
				iv.setIsactive(1);
				
				
				List<ProductInventory> inv = ProductInventory.retrieve(iv,prd);
				if(inv.size()==0){
					pr.setProductInventory(createDefaultInvQty(pr));
				}else{
					pr.setProductInventory(inv.get(0));
					
					//start this will only use for update 5.4.0 and will remove in future update
					String sql = " AND prop.propid=? AND prop.isactive=1 AND prd.prodid=? AND prd.isactiveproduct=1 ";
					String[] params = new String[2];
					params[0] = pr.getProductProperties().getPropid()+"";
					params[1] = pr.getProdid()+"";
					
					List<ProductInventory> saveInv = ProductInventory.retrieve(sql, params);
					if(saveInv.size()==0){
						System.out.println("start inserting data to propid = " + pr.getProductProperties().getPropid());
						ProductInventory invSave = inv.get(0);
						invSave.setProductProperties(pr.getProductProperties());
						invSave.save();
					}
					//end this will only use for update 5.4.0 and will remove in future update
					
				}
				
				pr.setProductProperties(ProductProperties.properties(pr.getProductProperties().getPropid()+""));
				invs.add(pr);
			}
			//Collections.reverse(invs);
	   }	
			
		//}
		/*ProductProperties prod = new ProductProperties();
		prod.setIsactive(1);
		if(getSearchProdQty()!=null){
			prod.setProductname(getSearchProdQty());
		}
		
		for(ProductProperties prop : ProductProperties.retrieve(prod)){
			ProductInventory inv = ProductInventory.retrieve(prop.getPropid()+"");
			if(inv.getUserDtls()!=null){
				prop.setProductInventory(inv);
			}else{
				prop.setProductInventory(createDefaultInvQty(prop));
			}
			invs.add(prop);
		}*/
		
	}
	
	public void checkInputQty(ProductInventory inv){
		try{
			//setInventoryData(inv);
			System.out.println(" inputted " + inv.getAddqty() + " Current " + inv.getNewqty() + " old " + inv.getOldqty());
			setErrorqtymsg("");
		}catch(NullPointerException e){
		}catch(NumberFormatException e){
			setErrorqtymsg("Invalid quantity.");
		}
	}
	
	private ProductInventory createDefaultInvQty(Product prop){
		ProductInventory inv = new ProductInventory(); //.retrieve(prop.getPropid()+"");
		//inv.setInvid(0);
		//inv.setAddqty(new Double("0.00"));
		inv.setNewqty(new Double("0.00"));
		inv.setOldqty(new Double("0.00"));
		inv.setIsactive(1);
		inv.setProductProperties(prop.getProductProperties());
		inv.setProduct(prop);
		inv.setUserDtls(Login.getUserLogin().getUserDtls());
		
		long id = ProductInventory.getInfo(ProductInventory.getLatestId()+1);
		inv = ProductInventory.insertData(inv, ""+id);
		return inv;
	}
	
	public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        System.out.println("Old Value   "+ event.getOldValue()); 
        System.out.println("New Value   "+ event.getNewValue());
	} 
	
	public void updateQty(){
		if(Login.checkUserStatus()){
			
			if(getQuantityData()!=null && getQuantityData().size()>0){
				
				int cnt =0;
				
				for(Product inv : getQuantityData()){
					addQty(inv.getProductInventory());
					cnt++;
				}
				initProductQty();
				setQuantityData(null);
				if(cnt>0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, (cnt==1? "1 product has" : cnt + " products have" ) + " been updated.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "No product quantiy has been changed.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
	            
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No selected product for update.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}
	}
	
	public void addQty(ProductInventory prop){
		//if(Login.checkUserStatus()){
			
			ProductInventory inv = new ProductInventory();
			if(prop.getInvid()!=0){
				inv.setInvid(prop.getInvid());
				try{
					if(prop.getAddqty()!=null){
						Double qty = prop.getAddqty() + prop.getNewqty(); 
						inv.setNewqty(qty);
						inv.setOldqty(prop.getNewqty());
					}
				}catch(NullPointerException n){
				}catch(NumberFormatException num){}
			}else{
				try{
					if(prop.getAddqty()!=null){
						inv.setNewqty(prop.getAddqty());
						inv.setOldqty(prop.getAddqty());
					}
				}catch(NullPointerException n){
				}catch(NumberFormatException num){}	
			}
			
				inv.setIsactive(1);
				/*ProductProperties p = new ProductProperties();
				p.setPropid(prop.getProductProperties().getPropid());*/
				Product p = new Product();
				p.setProdid(prop.getProduct().getProdid());
				//inv.setProductProperties(p);
				inv.setProduct(p);
				inv.setUserDtls(Login.getUserLogin().getUserDtls());
				inv.save();
				//initProductQty();
				
				//track inputed quantity
				InputedInventoryQtyTracker.saveQty(prop,"MANUAL ADJUSTMENT");
				//FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Adjustment for quantity has been successfully updated.", "");
	            //FacesContext.getCurrentInstance().addMessage(null, msg);
				
		/*}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}*/
	}
	
	public void searchProductPrice(){
		initPricing();
	}
	
	public void checkInputSellingPrice(){
		if(getPurchasePricetmp()!=null){
			Double taxableamount = 0d;
			Double purchase = Double.valueOf(getPurchasePricetmp());
			Double selling = Double.valueOf(getSellingPricetmp());
			Double netprice = 0d;
			
			taxableamount = purchase * getTaxpercentagetmp();
			
			if(selling<purchase){
				
				if(taxableamount!=0){
					selling = purchase + taxableamount;
				}
				setSellingPricetmp(selling+"");
				setSellingPrice(getSellingPricetmp());
				netprice = selling - (purchase + taxableamount);
				setNetPrice(netprice+"");
				setNetPricetmp(netprice+"");
				System.out.println(" checkInputSellingPrice < " + netprice);
			}else{
				netprice = selling - (purchase + taxableamount);
				setNetPrice(netprice+"");
				setNetPricetmp(netprice+"");
				setSellingPrice(selling+"");
				System.out.println(" checkInputSellingPrice > " + netprice);
			}
			
		}
		if(getPurchasePricetmp()==null &&
				getTaxpercentagetmp()!=0
				){
			setErrormsgprice("Provide Purchase Price");
			setIsvalidamount(true);
		}else if(getPurchasePricetmp()==null &&
				getTaxpercentagetmp()!=0
				){
			setErrormsgprice("Provide Tax percentage");
			setIsvalidamount(true);
		}
	}
	
	public void checkInputAmount(){
		
		System.out.println("CheckInputAmount " + getSellingPricetmp());
		
		try{
			
			
			Double sellprice = 0d;
			Double taxableamount = 0d;
			Double purchaseAmnt = Double.valueOf(getPurchasePricetmp());
			Double netprice = 0d;
			
			if(getSellingPricetmp()==null && getPurchasePricetmp()!=null){
				taxableamount = purchaseAmnt * getTaxpercentagetmp();
				sellprice = purchaseAmnt + taxableamount;
				if(sellprice!=0){
					setSellingPricetmp(sellprice+"");
				}else{
					setSellingPricetmp(getPurchasePricetmp());
				}
				setTaxpercentage(getTaxpercentagetmp());
				netprice = sellprice - (purchaseAmnt + taxableamount);
			}else if(getSellingPricetmp()!=null && getTaxpercentagetmp()!=0){
				taxableamount = purchaseAmnt * getTaxpercentagetmp();
				sellprice = purchaseAmnt + taxableamount;
				if(sellprice!=0){
					setSellingPricetmp(sellprice+"");
				}else{
					setSellingPricetmp(getPurchasePricetmp());
				}
				setTaxpercentage(getTaxpercentagetmp());
				netprice = sellprice - (purchaseAmnt + taxableamount);
			}else{
				setSellingPricetmp(getPurchasePricetmp());
			}
			
			
			setPurchasePrice(getPurchasePricetmp());
			setSellingPrice(getSellingPricetmp());
			setTaxpercentage(getTaxpercentagetmp());
			setNetPrice(netprice+"");
			
			setErrormsgprice("");
			setIsvalidamount(false);
		}catch(NullPointerException e){
		}catch(NumberFormatException num){
			setErrormsgprice("Invalid amount.");
			setIsvalidamount(true);
		}
		
		if((getPurchasePricetmp()==null || getPurchasePricetmp().isEmpty()) ||
				(getSellingPricetmp()==null || getSellingPricetmp().isEmpty()) ||
					(getTaxpercentagetmp()==0)
				){
			setErrormsgprice("");
			setIsvalidamount(false);
		}else if(getPurchasePricetmp()!=null && getSellingPricetmp()!=null){
			setErrormsgprice("");
			setIsvalidamount(false);
		}
		
		if(getPurchasePricetmp()!=null &&
				getSellingPricetmp()==null
				){
			setErrormsgprice("Provide Selling Price");
			setIsvalidamount(true);
		}else if(getPurchasePricetmp()==null &&
				getSellingPricetmp()!=null
				){
			setErrormsgprice("Provide Purchase Price");
			setIsvalidamount(true);
		}else if(getTaxpercentagetmp()<0){
			setErrormsgprice("Provide VAT(%)");
			setIsvalidamount(true);
		}
		
	}
	
	public void initPricing(){
		
		//if(getSearchProductpricing()!=null){
		/*Product prod = new Product();
		prod.setIsactiveproduct(1);
		ProductProperties prop = new ProductProperties();
		
		prop.setProductname(Whitelist.remove(getSearchProductpricing()));*/
		
		List<Product> products = Collections.synchronizedList(new ArrayList<Product>());
		String sqlProd = "";
		String[] paramsProd = new String[0];
		boolean isOk = false;
		if(getSearchProductpricing()!=null && !getSearchProductpricing().isEmpty()){
			
			int len = getSearchProductpricing().length();
			if(len>=4){
				pricingstans = Collections.synchronizedList(new ArrayList<Product>());
				sqlProd = " AND prod.isactiveproduct=1 AND prop.isactive=1 AND prop.productname like '%"+ getSearchProductpricing().replace("--", "") +"%'";
				isOk = true;
			}else{
				isOk = false;
			}
		}else{
			pricingstans = Collections.synchronizedList(new ArrayList<Product>());
			sqlProd = " AND prod.isactiveproduct=1 AND prop.isactive=1 order by prod.prodid desc limit 10";
			isOk = true;
		}
		
		products = Product.retrieve(sqlProd, paramsProd);
		
		if(products!=null && products.size()>0 && isOk){
			for(Product prd : products){
				
				//Search product in pricing if present
				Product trans = new Product();
				trans.setProdid(prd.getProdid());
				trans.setIsactiveproduct(1);
				ProductPricingTrans price = new ProductPricingTrans();
				price.setIsActiveprice(1);
				
				List<ProductPricingTrans> pricelist = ProductPricingTrans.retrievePrice(price,trans);
				if(pricelist.size()==0){
					pricelist.add(createDefaultPrice(prd));
				}
				
				prd.setProductProperties(ProductProperties.properties(prd.getProductProperties().getPropid()+""));
				
				prd.setPricetrans(pricelist);
				pricingstans.add(prd); 
			}
			//Collections.reverse(pricingstans);
		}
	  //}
		/*ProductProperties prp = new ProductProperties();
		prp.setIsactive(1);
		if(getSearchProductpricing()!=null){
			prp.setProductname(getSearchProductpricing());
		}
		
		for(ProductProperties prop : ProductProperties.retrieve(prp)){
			
			ProductProperties trans = new ProductProperties();
			trans.setPropid(prop.getPropid());
			ProductPricingTrans price = new ProductPricingTrans();
			price.setIsActiveprice(1);
			List<ProductPricingTrans> pricelist = ProductPricingTrans.retrievePrice(price,trans);
			if(pricelist.size()==0){
				pricelist.add(createDefaultPrice(prop));
			}
			prop.setPricetrans(pricelist);
			
			pricingstans.add(prop);
		}*/
		
	}
	
	private ProductPricingTrans createDefaultPrice(Product prop){
		ProductPricingTrans price = new ProductPricingTrans();
		BigDecimal priceValue =new BigDecimal("0.00");
		price.setPurchasedprice(priceValue);
		price.setSellingprice(priceValue);
		price.setNetprice(priceValue);
		price.setIsActiveprice(1);
		//price.setProductProperties(prop.getProductProperties());
		price.setProduct(prop);
		price.setUserDtls(Login.getUserLogin().getUserDtls());
		return price;
	}
	
	public void initProdGroup(){
		groups = Collections.synchronizedList(new ArrayList<ProductGroup>());
		ProductGroup grp = new ProductGroup();
		grp.setIsactive(1);
		if(getSearchGroup()!=null){
			grp.setProductgroupname(getSearchGroup());
		}
		groups = ProductGroup.retrieve(grp);
		Collections.reverse(groups);
	}
	
	public void initBrand(){
		brands = Collections.synchronizedList(new ArrayList<ProductBrand>());
		ProductBrand brand = new ProductBrand();
		brand.setIsactive(1);
		if(getSearchBrand()!=null){
			brand.setProductbrandname(getSearchBrand());
		}
		brands = ProductBrand.retrieve(brand);
		Collections.reverse(brands);
	}
	
	
	
	public void initProperty(){
		
		String sql = "";
		String[] params = new String[0]; 
		boolean isOk = false;
		if(getSearchProperties()!=null && !getSearchProperties().isEmpty()){
			int len = getSearchProperties().length();
			if(len>=4){
				props = Collections.synchronizedList(new ArrayList<ProductProperties>());
				sql = " AND prd.isactive=1 AND prd.productname like '%"+ getSearchProperties().replace("--", "") +"%'";
				isOk = true;
			}else{
				isOk = false;
			}
		}else{
			props = Collections.synchronizedList(new ArrayList<ProductProperties>());
			sql = " AND prd.isactive=1 order by prd.propid desc limit 10";
			isOk = true;
		}
		if(isOk){
			props = ProductProperties.retrieve(sql, params);
			//Collections.reverse(props);
		}
		
		
	}
	
	public void loadPropertyComboList(){
		setPropUomId("");
		propUoms = new ArrayList<>();
		for(UOM uom :  UOM.retrieve(new UOM())){
			propUoms.add(new SelectItem(uom.getUomid()+"",uom.getUomname()));
		}
		
		setPropCatId("");
		propCats = new ArrayList<>();
		for(ProductCategory prop :  ProductCategory.retrieve(new ProductProperties())){
			propCats.add(new SelectItem(prop.getProdcatid()+"",prop.getCatname()));
		}
		
		setPropGroupId("");
		propGroups = new ArrayList<>();
		for(ProductGroup grp : ProductGroup.retrieve(new ProductGroup())){
			propGroups.add(new SelectItem(grp.getProdgroupid()+"",grp.getProductgroupname()));
		}
		
		setPropBrandId("");
		propBrands = new ArrayList<>();
		for(ProductBrand brnd : ProductBrand.retrieve(new ProductBrand())){
			propBrands.add(new SelectItem(brnd.getProdbrandid()+"",brnd.getProductbrandname()));
		}
		
		setProductCode("");
		propCodes = new ArrayList<>();
		IposType ipos = new IposType();
		ipos.setIsactive(1);
		int i=1;
		for(IposType store : IposType.retrieve(ipos)){
			propCodes.add(new SelectItem(store.getIposcode()+"",store.getIpostype()));
		}
		//setDefault business type
		setProductPropertyDesc("");
		setPropCodeId("");
		generateProdCode();
	}
	
	public void generateProdCode(){
		IStore type = IStore.name(getPropCodeId());
		String code = ProductProperties.productCode(type);
		setProductCode(code);
	}
	
	public void initCategory(){
		cats = Collections.synchronizedList(new ArrayList<ProductCategory>());
		ProductCategory cat = new ProductCategory();
		cat.setIsactive(1);
		if(getSearchCategory()!=null){
			cat.setCatname(getSearchCategory());
		}
		cats = ProductCategory.retrieve(cat);
		Collections.reverse(cats);
	}
	public void initUOM(){
		uoms = Collections.synchronizedList(new ArrayList<UOM>());
		UOM uom = new UOM();
		uom.setIsactive(1);
		if(getSearchUOM()!=null){
			uom.setUomname(getSearchUOM());
		}
		uoms = UOM.retrieve(uom);
		Collections.reverse(uoms);
	}
	public void save(){
		System.out.println("save");
	}
	
	public String addPrice(Product prop){
		//try{System.out.println("Check addprice properties id " + prop.getPropid());}catch(Exception e){e.getMessage();}
		try{System.out.println("Purchase : " + getPurchasePrice());}catch(Exception e){e.getMessage();}
		try{System.out.println("Selling : " + getSellingPrice());}catch(Exception e){e.getMessage();}
		try{System.out.println("Net : " + getNetPrice());}catch(Exception e){e.getMessage();}
		if(Login.checkUserStatus()){
			ProductPricingTrans trans = new ProductPricingTrans();
			trans.setIsActiveprice(1);
			//trans.setProductProperties(prop);
			trans.setProduct(prop);
			trans.setTaxpercentage(getTaxpercentage());
			trans.setPurchasedprice(new BigDecimal(getPurchasePrice()));
			trans.setSellingprice(new BigDecimal(getSellingPrice()));
			trans.setNetprice(new BigDecimal(getNetPrice()));
			trans.setUserDtls(Login.getUserLogin().getUserDtls());
			trans = ProductPricingTrans.save(trans);
			
			try{
			prop.setProductPricingTrans(trans);
			trans.setProduct(prop);
			saveCopyForStoreProduct(trans);
			}catch(Exception e){}
			
			clearFields();
			initPricing();
		}
		
		return "";
	}
	
	public String savegbrand(){
		System.out.println("save");
		
		if(Login.checkUserStatus()){
			
			
			if("Input Here".equalsIgnoreCase(getProductBrandName())) return "Empty description";
			
			ProductBrand brand = new ProductBrand();
			if(getProductBrandData()!=null){
				brand = getProductBrandData();
			}
			brand.setUserDtls(Login.getUserLogin().getUserDtls());
			brand.setIsactive(1);
			brand.setProductbrandcode(getProductBrandCode());
			brand.setProductbrandname(getProductBrandName());
			brand.save();
			clearFields();
			initBrand();
		}
		
		return "";
	}
	
	public String savegroup(){
		System.out.println("save");
		
		if(Login.checkUserStatus()){
			if("Input Here".equalsIgnoreCase(getProductGroupDesc())) return "Empty Description";
			
			ProductGroup group = new ProductGroup();
			if(getProductGroupData()!=null){
				group = getProductGroupData();
			}
			group.setUserDtls(Login.getUserLogin().getUserDtls());
			group.setIsactive(1);
			group.setProductgroupname(getProductGroupDesc());
			group.save();
			clearFields();
			initProdGroup();
		}
		
		return "";
	}
	
	public void priceDeactivate(ProductPricingTrans trans){
		if(Login.checkUserStatus()){
			LogU.add("Deactivate Price id " + trans.getPricingid());
			trans.delete(true);
			initPricing();
		}
	}
	
	public String saveProp(){
		System.out.println("save");
		
		if(Login.checkUserStatus()){
			boolean isOk = true;
			if("Input Here".equalsIgnoreCase(getProductPropertyDesc())){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Empty Description.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
	            System.out.println("Checking 1");
			}
			
			if("".equalsIgnoreCase(getProductPropertyDesc())){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Empty Description.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
	            System.out.println("Checking 2");
			}
			
			if(getPropUomId().isEmpty()){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Empty UOM.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
	            System.out.println("Checking 3");
			}	
			if(getPropCatId().isEmpty()){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Empty Category.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
	            System.out.println("Checking 4");
			}
			
			if(getPropGroupId().isEmpty()){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Empty Group.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
	            System.out.println("Checking 5");
			}
			
			if(getPropBrandId().isEmpty()){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Empty Brand.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
	            System.out.println("Checking 6");
			}
			boolean isExist = false;
			ProductProperties prop = new ProductProperties();
			if(getProductPropData()!=null){
				prop = getProductPropData();
				isExist = true;
				System.out.println("Checking 7");
			}
			
			if(!isExist){
				System.out.println("Checking 8");
				String sql="SELECT * FROM productproperties WHERE productname=?";
				String[] params = new String[1];
				params[0] = getProductPropertyDesc();
				ProductProperties prp = null;
				try{prp = ProductProperties.retrieve(sql, params).get(0);}catch(IndexOutOfBoundsException e){}
				if(prp!=null){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: The name " + getProductPropertyDesc() + " is existed. Please choose another name.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
		            System.out.println("Checking 9");
				}
			}
			
			if(isOk){
				System.out.println("Checking 10");
			prop.setUserDtls(Login.getUserLogin().getUserDtls());
			prop.setProductname(getProductPropertyDesc());
			String fileName = "";
			
			if(getDocFileExt()!=null){
				fileName=getProductCode() + "." + getDocFileExt();
			}else{
				fileName=getProductCode() + ".jpg";
			}
			
			writeImgToFile(getDocImage(), fileName);
			prop.setImagepath(ReadConfig.value(Ipos.APP_IMG_FILE) + fileName);//getDocumentPath());
			prop.setIsactive(1);
			prop.setProductCategory(ProductCategory.prodcategory(getPropCatId()));
			prop.setUom(UOM.uom(getPropUomId()));
			prop.setProductGroup(ProductGroup.productGroup(getPropGroupId()));
			prop.setProductBrand(ProductBrand.productBrand(getPropBrandId()));
			prop.setProductcode(getProductCode());
			prop = ProductProperties.save(prop);
			Product product = saveBarcode(prop);
			saveCopyForStoreProduct(product);
			clearFields();
			initProperty();
			clickItem(prop);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data successfully saved.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
		return "";
	}
	
	private void saveCopyForStoreProduct(Object obj){
		System.out.println("Checking 14 copystore");
		if(obj instanceof Product){
			Product product = (Product)obj;
			//String sql = "SELECT * FROM storeproduct WHERE barcode=?";
			String sql = "SELECT * FROM storeproduct WHERE prodid=? AND propid=?";
			String[] params = new String[2];
			params[0] = product.getProdid()+"";
			params[1] = product.getProductProperties().getPropid()+"";
			StoreProduct storeProd = null;
		try{storeProd = StoreProduct.retrieveProduct(sql, params).get(0);}catch(IndexOutOfBoundsException io){}
		
		if(storeProd!=null){
			storeProd.setProductName(product.getProductProperties().getProductname());
			storeProd.setUomSymbol(product.getProductProperties().getUom().getSymbol());
			storeProd.save();
		}else{
			storeProd = new StoreProduct();
			storeProd.setBarcode(product.getBarcode());
			storeProd.setProductName(product.getProductProperties().getProductname());
			storeProd.setUomSymbol(product.getProductProperties().getUom().getSymbol());
			storeProd.setIsActive(1);
			storeProd.setProduct(product);
			storeProd.setProductProperties(product.getProductProperties());
			storeProd.setUom(product.getProductProperties().getUom());
			storeProd.save();
		}
		
		
		}else if(obj instanceof ProductPricingTrans){
			
			ProductPricingTrans price = (ProductPricingTrans)obj;
			
			StoreProduct storeProd = null;
			
			String sql = "SELECT * FROM storeproduct WHERE barcode=?";
			String[] params = new String[1];
			params[0] = price.getProduct().getBarcode();
			
			try{storeProd = StoreProduct.retrieveProduct(sql, params).get(0);}catch(IndexOutOfBoundsException io){}
			if(storeProd!=null){
				try{storeProd.setPurchasedPrice(price.getPurchasedprice().doubleValue());}catch(Exception e){storeProd.setPurchasedPrice(0);}
				try{storeProd.setSellingPrice(price.getSellingprice().doubleValue());}catch(Exception e){storeProd.setSellingPrice(0);}
				try{storeProd.setNetPrice(price.getNetprice().doubleValue());}catch(Exception e){storeProd.setNetPrice(0);}
				storeProd.save();
			}
			
		}
	}
	
	private Product saveBarcode(ProductProperties prop){
		System.out.println("Checking 11 barcode");
		ProductProperties prp = new ProductProperties();
		prp.setPropid(prop.getPropid());
		prp.setIsactive(1);
		
		Product prd = new Product();
		prd.setIsactiveproduct(1);
		
		Product prod = null;
		
		try{prod = Product.retrieve(prd, prp).get(0);}catch(IndexOutOfBoundsException e){}
		
		if(prod==null){
			System.out.println("Checking 12 barcode");
			Product product = new Product();
			product.setDatecoded(DateUtils.getCurrentDateYYYYMMDD());
			product.setProductExpiration(null);
			product.setBarcode(barcodeCreation());
			product.setProductProperties(prop);
			product.setUserDtls(Login.getUserLogin().getUserDtls());
			product.setIsactiveproduct(1);
			product = Product.save(product);
			product.setProductProperties(prop);
			return product;
		}else{
			System.out.println("Checking 13 barcode");
			prod.setProductProperties(prop);
			return prod;
		}
	}
	
	private String barcodeCreation(){
		String barcode = "100000000001";
		String sql = " AND prod.isactiveproduct=1 ORDER BY prod.prodid DESC limit 1";
		String[] params = new String[0];
		Product product = null;
		try{product = Product.retrieve(sql, params).get(0);}catch(IndexOutOfBoundsException e){}
		
		if(product!=null){
			String bar = "10000000000";
			try{product.getBarcode().substring(1,12);}catch(Exception e){}
			int code = 0;
			try{code = Integer.valueOf(bar) + 1;}catch(NumberFormatException e){}
			int len = String.valueOf(code).length();
			switch(len){
			case 1: barcode="10000000000" + code; break;
			case 2: barcode="1000000000" + code; break;
			case 3: barcode="100000000" + code; break;
			case 4: barcode="10000000" + code; break;
			case 5: barcode="1000000" + code; break;
			case 6: barcode="100000" + code; break;
			case 7: barcode="10000" + code; break;
			case 8: barcode="1000" + code; break;
			case 9: barcode="100" + code; break;
			case 10: barcode="10" + code; break;
			case 11: barcode="1" + code; break;
			case 12: barcode="" + code; break;
			}
			
			
		}
		
		
		return barcode;
	}
	
	public String savecat(){
		System.out.println("save");
		if(Login.checkUserStatus()){
			
			if("Input Here".equalsIgnoreCase(getProductCatDesc())) return "Empty Description";
			
			ProductCategory prod = new ProductCategory();
			if(getProdcatData()!=null){
				prod = getProdcatData();
			}
			prod.setCatname(getProductCatDesc());
			prod.setUserDtls(Login.getUserLogin().getUserDtls());
			prod.setIsactive(1);
			prod.save();
			clearFields();
			initCategory();
			
		}
		return "";
	}
	
	public String saveuom(){
		System.out.println("save");
		if(Login.checkUserStatus()){
			
			if("Input Here".equalsIgnoreCase(getUomname())) return "Empty Description";
			if("Input Here".equalsIgnoreCase(getUomname())) return "Empty Symbol";
			UOM uom = new UOM();
			if(getUomdata()!=null){
				uom = getUomdata();
			}
			
			uom.setUserDtls(Login.getUserLogin().getUserDtls());
			uom.setUomname(getUomname());
			uom.setSymbol(getSymbol());
			uom.setIsactive(1);
			uom.save();
			clearFields();
			initUOM();
			
		}
		return "";
	}
	
	public void clearFields(){
		System.out.println("Clear Fields");
		
		//Product Category
		setProdcatData(null);
		setProductCatDesc("Input Here");
		
		//Product UOM
		setUomdata(null);
		setUomname("Input Here");
		setSymbol("Input Here");
		
		//Product Property
		setProductPropData(null);
		setProductPropertyDesc("Input Here");
		setPropCatId(null);
		setPropGroupId(null);
		setPropBrandId(null);
		setPropUomId(null);
		setDocumentPath(null);
		setPropCodeId("GP");
		setProductCode(null);
		setLockBusinessSelection(false);
		
		//Product Group
		setProductGroupData(null);
		setProductGroupDesc("Input Here");
		
		//Product Brand
		setProductBrandData(null);
		setProductBrandCode(null);
		setProductBrandName("Input Here");
		
		//Product Pricing
		setPurchasePrice(null);
		setSellingPrice(null);
		setNetPrice(null);
		setPurchasePricetmp(null);
		setSellingPricetmp(null);
		setNetPricetmp(null);
		setTaxpercentage(0);
		setTaxpercentagetmp(0);
		
		
	}
	
	public void clearBarcodeFields(){
		setSearchProductCode(null);
		setSearchProductItem(null);
		setProductbar(null);
		setBarcode(null);
		setDatecoded(null);
		setSelectedItem(null);
		setExpirationdate(null);
	}
	
	
	public void imgUploadListener(FileUploadEvent event) {
		System.out.println("docUploadListener....");
        try {
        	InputStream input = event.getFile().getInputStream();
        if(input!=null){
        	//save the stream in setter for saving purposes later
        	setDocFileExt(FilenameUtils.getExtension(event.getFile().getFileName()));
        	setDocumentPath(event.getFile().getFileName());
        	setDocImage(input);
        } else {
            throw new IOException("FAILED TO CONVERT DOCUMENT");
        }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
	}
	
	private void writeImgToFile(InputStream image, String filename){
		try{
			
		String imageLocation = ReadConfig.value(Ipos.APP_IMG_FILE); 	
			//check log directory
	        File logdirectory = new File(imageLocation);
	        if(!logdirectory.isDirectory()){
	        	logdirectory.mkdir();
	        }	
			
		System.out.println("writing... writeDocToFile : " + filename);
		File fileImg = new File(imageLocation +  filename);
		System.out.println("writing pdf images.....");
		//ImageIO.write(image, "jpg", fileImg);
		Path file = fileImg.toPath();
		
		if(image==null){
			String defaultImg = ReadConfig.value(Ipos.APP_IMG_FILE) + "noimageproduct.jpg";
			File newfile = new File(defaultImg);
				try{
				Files.copy(newfile.toPath(), (new File(imageLocation + filename)).toPath(),
				        StandardCopyOption.REPLACE_EXISTING);
				}catch(IOException e){}
		}else{
		
			Files.copy(image, file, StandardCopyOption.REPLACE_EXISTING);
		
		
		}
		
		}catch(IOException e){}
	}
	
	public void print(Object obj){
		System.out.println("Print");
	}
	
	public void printAll(){
		System.out.println("Print All");
	}
	
	public void close(){
		System.out.println("close");
		clearFields();
	}
	
	public void clickItem(Object obj){
		System.out.println("clickItem");
		if(obj instanceof UOM){
			UOM uom = (UOM)obj;
			setUomdata(uom);
			setUomname(uom.getUomname());
			setSymbol(uom.getSymbol());
		}else if(obj instanceof ProductCategory){
			ProductCategory prodc = (ProductCategory)obj;
			setProdcatData(prodc);
			setProductCatDesc(prodc.getCatname());
		}else if(obj instanceof ProductProperties){
			
			loadPropertyComboList();
			
			ProductProperties prop = (ProductProperties)obj;
			setProductPropData(prop);
			setPropCodeId(null);
			setLockBusinessSelection(true);
			setProductCode(prop.getProductcode());
			setProductPropertyDesc(prop.getProductname());
			setPropCatId(prop.getProductCategory().getProdcatid()+"");
			setPropGroupId(prop.getProductGroup().getProdgroupid()+"");
			setPropBrandId(prop.getProductBrand().getProdbrandid()+"");
			setPropUomId(prop.getUom().getUomid()+"");
			setDocumentPath(prop.getImagepath());
			
		}else if(obj instanceof ProductGroup){
			ProductGroup group = (ProductGroup)obj;
			setProductGroupData(group);
			setProductGroupDesc(group.getProductgroupname());
		}else if(obj instanceof ProductBrand){
			ProductBrand brand = (ProductBrand)obj;
			setProductBrandData(brand);
			setProductBrandCode(brand.getProductbrandcode());
			setProductBrandName(brand.getProductbrandname());
		}else if(obj instanceof Product){
			Product product = (Product)obj;
			setProductbar(product);
			ProductProperties prop = ProductProperties.properties(product.getProductProperties().getPropid()+"");
			setSelectedItem(prop);
			System.out.println("click " + product.getDatecoded() + " " + product.getBarcode());
			setDatecoded(product.getDatecoded());
			setBarcode(product.getBarcode());
			setExpirationdate(product.getProductExpiration());
			setSearchProductItem(product.getProductProperties().getProductname());
			setLockBarcode(true);
			initProductItem();
		}
		
	}
	
	
	public void deleteRow(Object obj){
		System.out.println("deleteRow");
			if(Login.checkUserStatus()){
			
				if(obj instanceof ProductProperties){
					ProductProperties prop = (ProductProperties)obj;
					
					if(!isProductAlreadyInWareHouse(prop)){
						LogU.add("Deactivate product properties id " + prop.getPropid());
						prop.delete(true);
						initProperty();
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Product successfully deleted.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Item deletion is not allowed. Item already in use.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
					
				}else if(obj instanceof ProductCategory){
					ProductCategory prod = (ProductCategory)obj;
					if(!isProductAlreadyInWareHouse(prod)){
						LogU.add("Deactivate product category id " + prod.getProdcatid());
						prod.delete();
						initCategory();
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Category successfully deleted.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Item deletion is not allowed. Item already in use.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}else if(obj instanceof UOM){
					UOM uom = (UOM)obj;
					if(!isProductAlreadyInWareHouse(uom)){
						LogU.add("Deactivate uom id " + uom.getUomid());
						uom.delete();
						initUOM();
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "UOM successfully deleted.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Item deletion is not allowed. Item already in use.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}else if(obj instanceof ProductGroup){
					ProductGroup grp = (ProductGroup)obj;
					if(!isProductAlreadyInWareHouse(grp)){
						LogU.add("Deactivate Group id " + grp.getProdgroupid());
						grp.delete();
						initProdGroup();
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Group successfully deleted.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Item deletion is not allowed. Item already in use.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}else if(obj instanceof ProductBrand){
					ProductBrand brand = (ProductBrand)obj;
					if(!isProductAlreadyInWareHouse(brand)){
						LogU.add("Deactivate brand id " + brand.getProdbrandid());
						brand.delete();
						initBrand();
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Brand successfully deleted.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Item deletion is not allowed. Item already in use.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}else if(obj instanceof Product ){
					Product prod = (Product)obj;
					if(!isProductAlreadyInWareHouse(prod)){
						LogU.add("Deactivate product id " + prod.getProdid());
						prod.delete();
						initBarcode();
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Product successfully deleted.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}else{
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Item deletion is not allowed. Item already in use.", "");
			            FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: Your login session has been expired. Please login again.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		
	}

	public List<ProductCategory> getCats() {
		return cats;
	}

	public void setCats(List<ProductCategory> cats) {
		this.cats = cats;
	}

	public List<ProductProperties> getProps() {
		return props;
	}

	public void setProps(List<ProductProperties> props) {
		this.props = props;
	}

	public List<UOM> getUoms() {
		return uoms;
	}

	public void setUoms(List<UOM> uoms) {
		this.uoms = uoms;
	}

	public String getUomname() {
		return uomname;
	}

	public void setUomname(String uomname) {
		this.uomname = uomname;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public UOM getUomdata() {
		return uomdata;
	}

	public void setUomdata(UOM uomdata) {
		this.uomdata = uomdata;
	}

	public String getProductCatDesc() {
		return productCatDesc;
	}

	public void setProductCatDesc(String productCatDesc) {
		this.productCatDesc = productCatDesc;
	}

	public ProductCategory getProdcatData() {
		return prodcatData;
	}

	public void setProdcatData(ProductCategory prodcatData) {
		this.prodcatData = prodcatData;
	}

	public String getDocFileExt() {
		return docFileExt;
	}

	public void setDocFileExt(String docFileExt) {
		this.docFileExt = docFileExt;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public InputStream getDocImage() {
		return docImage;
	}

	public void setDocImage(InputStream docImage) {
		this.docImage = docImage;
	}

	public String getProductPropertyDesc() {
		return productPropertyDesc;
	}

	public void setProductPropertyDesc(String productPropertyDesc) {
		this.productPropertyDesc = productPropertyDesc;
	}

	public String getPropUomId() {
		return propUomId;
	}

	public void setPropUomId(String propUomId) {
		this.propUomId = propUomId;
	}

	public List getPropUoms() {
		return propUoms;
	}

	public void setPropUoms(List propUoms) {
		this.propUoms = propUoms;
	}

	public String getPropCatId() {
		return propCatId;
	}

	public void setPropCatId(String propCatId) {
		this.propCatId = propCatId;
	}

	public List getPropCats() {
		return propCats;
	}

	public void setPropCats(List propCats) {
		this.propCats = propCats;
	}

	public ProductProperties getProductPropData() {
		return productPropData;
	}

	public void setProductPropData(ProductProperties productPropData) {
		this.productPropData = productPropData;
	}

	public ProductPricingTrans getPricingData() {
		return pricingData;
	}

	public void setPricingData(ProductPricingTrans pricingData) {
		this.pricingData = pricingData;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public String getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(String netPrice) {
		this.netPrice = netPrice;
	}

	public List<Product> getPricingstans() {
		return pricingstans;
	}

	public void setPricingstans(List<Product> pricingstans) {
		this.pricingstans = pricingstans;
	}

	public List<ProductGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ProductGroup> groups) {
		this.groups = groups;
	}

	public ProductGroup getProductGroupData() {
		return productGroupData;
	}

	public void setProductGroupData(ProductGroup productGroupData) {
		this.productGroupData = productGroupData;
	}

	public String getProductGroupDesc() {
		return productGroupDesc;
	}

	public void setProductGroupDesc(String productGroupDesc) {
		this.productGroupDesc = productGroupDesc;
	}

	public List<ProductBrand> getBrands() {
		return brands;
	}

	public void setBrands(List<ProductBrand> brands) {
		this.brands = brands;
	}

	public ProductBrand getProductBrandData() {
		return productBrandData;
	}

	public void setProductBrandData(ProductBrand productBrandData) {
		this.productBrandData = productBrandData;
	}

	public String getProductBrandCode() {
		if(productBrandCode==null){
			productBrandCode = ProductBrand.brandCode();
		}
		return productBrandCode;
	}

	public void setProductBrandCode(String productBrandCode) {
		this.productBrandCode = productBrandCode;
	}

	public String getProductBrandName() {
		return productBrandName;
	}

	public void setProductBrandName(String productBrandName) {
		this.productBrandName = productBrandName;
	}

	public String getPropGroupId() {
		return propGroupId;
	}

	public void setPropGroupId(String propGroupId) {
		this.propGroupId = propGroupId;
	}

	public List getPropGroups() {
		return propGroups;
	}

	public void setPropGroups(List propGroups) {
		this.propGroups = propGroups;
	}

	public String getPropBrandId() {
		return propBrandId;
	}

	public void setPropBrandId(String propBrandId) {
		this.propBrandId = propBrandId;
	}

	public List getPropBrands() {
		return propBrands;
	}

	public void setPropBrands(List propBrands) {
		this.propBrands = propBrands;
	}

	public String getSearchProductpricing() {
		return searchProductpricing;
	}

	public void setSearchProductpricing(String searchProductpricing) {
		this.searchProductpricing = searchProductpricing;
	}

	public String getPurchasePricetmp() {
		return purchasePricetmp;
	}

	public void setPurchasePricetmp(String purchasePricetmp) {
		this.purchasePricetmp = purchasePricetmp;
	}

	public String getSellingPricetmp() {
		return sellingPricetmp;
	}

	public void setSellingPricetmp(String sellingPricetmp) {
		this.sellingPricetmp = sellingPricetmp;
	}

	public String getNetPricetmp() {
		return netPricetmp;
	}

	public void setNetPricetmp(String netPricetmp) {
		this.netPricetmp = netPricetmp;
	}

	public String getErrormsgprice() {
		return errormsgprice;
	}

	public void setErrormsgprice(String errormsgprice) {
		this.errormsgprice = errormsgprice;
	}

	public boolean isIsvalidamount() {
		return isvalidamount;
	}

	public void setIsvalidamount(boolean isvalidamount) {
		this.isvalidamount = isvalidamount;
	}

	public List<Product> getInvs() {
		return invs;
	}

	public void setInvs(List<Product> invs) {
		this.invs = invs;
	}

	public ProductInventory getInventoryData() {
		return inventoryData;
	}

	public void setInventoryData(ProductInventory inventoryData) {
		this.inventoryData = inventoryData;
	}

	public double getInventoryNewQty() {
		return inventoryNewQty;
	}

	public void setInventoryNewQty(double inventoryNewQty) {
		this.inventoryNewQty = inventoryNewQty;
	}

	public double getInventoryOldQty() {
		return inventoryOldQty;
	}

	public void setInventoryOldQty(double inventoryOldQty) {
		this.inventoryOldQty = inventoryOldQty;
	}

	public String getErrorqtymsg() {
		return errorqtymsg;
	}

	public void setErrorqtymsg(String errorqtymsg) {
		this.errorqtymsg = errorqtymsg;
	}

	public List getPropCodes() {
		return propCodes;
	}

	public void setPropCodes(List propCodes) {
		this.propCodes = propCodes;
	}

	public String getPropCodeId() {
		return propCodeId;
	}

	public void setPropCodeId(String propCodeId) {
		this.propCodeId = propCodeId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public boolean isLockBusinessSelection() {
		return lockBusinessSelection;
	}

	public void setLockBusinessSelection(boolean lockBusinessSelection) {
		this.lockBusinessSelection = lockBusinessSelection;
	}

	public String getSearchProdQty() {
		return searchProdQty;
	}

	public void setSearchProdQty(String searchProdQty) {
		this.searchProdQty = searchProdQty;
	}

	public String getSearchProperties() {
		return searchProperties;
	}

	public void setSearchProperties(String searchProperties) {
		this.searchProperties = searchProperties;
	}

	public String getSearchCategory() {
		return searchCategory;
	}

	public void setSearchCategory(String searchCategory) {
		this.searchCategory = searchCategory;
	}

	public String getSearchUOM() {
		return searchUOM;
	}

	public void setSearchUOM(String searchUOM) {
		this.searchUOM = searchUOM;
	}

	public String getSearchGroup() {
		return searchGroup;
	}

	public void setSearchGroup(String searchGroup) {
		this.searchGroup = searchGroup;
	}

	public String getSearchBrand() {
		return searchBrand;
	}

	public void setSearchBrand(String searchBrand) {
		this.searchBrand = searchBrand;
	}

	public double getTaxpercentagetmp() {
		return taxpercentagetmp;
	}

	public void setTaxpercentagetmp(double taxpercentagetmp) {
		this.taxpercentagetmp = taxpercentagetmp;
	}

	public double getTaxpercentage() {
		return taxpercentage;
	}

	public void setTaxpercentage(double taxpercentage) {
		this.taxpercentage = taxpercentage;
	}

	public String getSearchProductCode() {
		return searchProductCode;
	}

	public void setSearchProductCode(String searchProductCode) {
		this.searchProductCode = searchProductCode;
	}

	public List<Product> getBars() {
		return bars;
	}

	public void setBars(List<Product> bars) {
		this.bars = bars;
	}

	public Product getProductbar() {
		return productbar;
	}

	public void setProductbar(Product productbar) {
		this.productbar = productbar;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getDatecoded() {
		if(datecoded==null){
			datecoded = DateUtils.getCurrentDateYYYYMMDD();
		}
		return datecoded;
	}

	public void setDatecoded(String datecoded) {
		this.datecoded = datecoded;
	}

	public String getSearchProductItem() {
		return searchProductItem;
	}

	public void setSearchProductItem(String searchProductItem) {
		this.searchProductItem = searchProductItem;
	}

	public List<ProductProperties> getProdItems() {
		return prodItems;
	}

	public void setProdItems(List<ProductProperties> prodItems) {
		this.prodItems = prodItems;
	}

	public ProductProperties getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ProductProperties selectedItem) {
		this.selectedItem = selectedItem;
	}

	public String getExpirationdate() {
		return expirationdate;
	}

	public void setExpirationdate(String expirationdate) {
		this.expirationdate = expirationdate;
	}

	public boolean isLockBarcode() {
		return lockBarcode;
	}

	public void setLockBarcode(boolean lockBarcode) {
		this.lockBarcode = lockBarcode;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		if(msgId==null){
			msgId = "msg";
		}
		this.msgId = msgId;
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Ipos.REPORT);
	private static final String REPORT_NAME = ReadXML.value(ReportTag.PRODUCT_CODE);
	
	public void printBarcode(){
		
		List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
		for(Product prod : getBars()){
			Reports rpt = new Reports();
			rpt.setF1(prod.getDatecoded());
			rpt.setF2(prod.getProductProperties().getProductname());
			rpt.setF3(prod.getProductProperties().getUom().getSymbol());
			rpt.setF4(prod.getBarcode());
			
			reports.add(rpt);
		}
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
  		
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
	
	public void printAllProductBarcode(){
		
		List<Product> products = Collections.synchronizedList(new ArrayList<Product>());
		String sqlProd = "";
		String[] paramsProd = new String[0];
		
		
		List<Product> bars = Collections.synchronizedList(new ArrayList<Product>());
		sqlProd = " AND prod.isactiveproduct=1 AND prop.isactive=1 order by prop.propid";
		
		products = Product.retrieve(sqlProd, paramsProd);
		
		if(products!=null && products.size()>0){
			for(Product prd : products){
				prd.setProductProperties(ProductProperties.properties(prd.getProductProperties().getPropid()+"")); //retrieve product properties in order to load UOM - subject for enhancement
				bars.add(prd);
			}
			
		}
		
		List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
		for(Product prod : bars){
			Reports rpt = new Reports();
			rpt.setF1(prod.getDatecoded());
			rpt.setF2(prod.getProductProperties().getProductname());
			rpt.setF3(prod.getProductProperties().getUom().getSymbol());
			rpt.setF4(prod.getBarcode());
			
			reports.add(rpt);
		}
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
  		
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

	/*public List<QtyRunning> getProductRunning() {
		return productRunning;
	}

	public void setProductRunning(List<QtyRunning> productRunning) {
		this.productRunning = productRunning;
	}*/

	public List<Product> getQuantityData() {
		return quantityData;
	}

	public void setQuantityData(List<Product> quantityData) {
		this.quantityData = quantityData;
	}
	
}
