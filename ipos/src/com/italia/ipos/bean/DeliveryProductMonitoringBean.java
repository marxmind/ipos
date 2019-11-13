package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import com.italia.ipos.controller.DeliveryItemTrans;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.PurchasedItem;
import com.italia.ipos.enm.DeliveryStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 02/13/2017
 * @version 1.0
 *
 */
@ManagedBean(name="delprodmonBean", eager=true)
@SessionScoped
public class DeliveryProductMonitoringBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 16587473342L;
	private List range;
	private int rangeId;
	private String productName;
	private String dateFrom;
	private String dateTo;
	
	private List graphType;
	private int graphId;
	
	private List<DeliveryItemTrans> products = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
	
	private Map<String, Map<Long, DeliveryItemTrans>> transactionData = Collections.synchronizedMap(new HashMap<String,Map<Long, DeliveryItemTrans>>());
	private Map<Long, Product> productData = Collections.synchronizedMap(new HashMap<Long, Product>());
	
	private int monthId;
	private List months;
	
	private int yearIdFrom;
	private List yearFrom;
	private int yearIdTo;
	private List yearTo;
	
	private int typeId;
	private List types;
	
	@PostConstruct
	public void init(){
		
		filter();
		
		if(1==getTypeId()){
			delivery();
		}else if(2==getTypeId()){
			store();
		}
		
	}
	
	private void store(){
		
		products = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
		
		PurchasedItem item = new PurchasedItem();
		item.setIsactiveitem(1);
		item.setDateFrom(getDateFrom());
		item.setDateTo(getDateTo());
		item.setIsBetweenDate(true);
		
		List<DeliveryItemTrans> transData = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
		for(PurchasedItem it : PurchasedItem.retrieve(item)){
			
			DeliveryItemTrans tran = new DeliveryItemTrans();
			tran.setId(it.getItemid());
			tran.setDateTrans(it.getDatesold());
			
			Product product = Product.retrieve(it.getProduct().getProdid()+"");
			tran.setProduct(product);
			tran.setQuantity(it.getQty());
			transData.add(tran);
		}
		
		Map<Long, Product> prods = Collections.synchronizedMap(new HashMap<Long, Product>());
		
		for(DeliveryItemTrans del : transData){
			
			Product prod = del.getProduct();
			if(prods!=null && prods.size()>0){
				long id = del.getProduct().getProdid();
				if(!prods.containsKey(id)){
					prod = Product.retrieve(id+""); //retrieving product more details
					prods.put(del.getProduct().getProdid(), prod);
				}else{
					prod = prods.get(id);
					prods.put(del.getProduct().getProdid(), prod);
				}
				
			}else{
				prod = Product.retrieve(del.getProduct().getProdid()+"");//retrieving product more details
				prods.put(del.getProduct().getProdid(), prod);
								
			}
			del.setProduct(prod);
			products.add(del);
			
		}
		
		
		Map<String, Map<Long, DeliveryItemTrans>> perDate = Collections.synchronizedMap(new HashMap<String,Map<Long, DeliveryItemTrans>>());
		Map<Long, DeliveryItemTrans> items = Collections.synchronizedMap(new HashMap<Long, DeliveryItemTrans>());
		
		for(DeliveryItemTrans dels : products){
			
			long id = dels.getProduct().getProdid();
			
			String date = "";
			
			if(getRangeId()==1){ //day
				date = dels.getDateTrans();
			}else if(getRangeId()==2){//month
				date = dels.getDateTrans().split("-")[1];
			}else if(getRangeId()==3){//year
				date = dels.getDateTrans().split("-")[0];
			}
			
			if(perDate!=null && perDate.size()>0){
				
				if(perDate.containsKey(date)){
					
					//same date and same product
					if(perDate.get(date).containsKey(id)){
						double additionalQty = dels.getQuantity();
						additionalQty += perDate.get(date).get(id).getQuantity(); // old qty
						perDate.get(date).get(id).setQuantity(additionalQty);
					}else{
						perDate.get(date).put(id, dels);
					}
					
				}else{
					items = Collections.synchronizedMap(new HashMap<Long, DeliveryItemTrans>());
					items.put(id, dels);
					perDate.put(date, items);
				}
				
			}else{
				items = Collections.synchronizedMap(new HashMap<Long, DeliveryItemTrans>());
				items.put(id, dels);
				perDate.put(date, items);
			}
			
			
		}
		
		Map<String, Map<Long, DeliveryItemTrans>> treeSort = new TreeMap<String, Map<Long,DeliveryItemTrans>>(perDate);
		
		setTransactionData(treeSort);
		setProductData(prods);
		
		List<DeliveryItemTrans> productsFiltered = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
		for(String date : getTransactionData().keySet()){
			for(DeliveryItemTrans trn : getTransactionData().get(date).values()){
				productsFiltered.add(trn);
			}
		}
		
		products = productsFiltered;
		
	}
	
	private void delivery(){
		
		products = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
		
		DeliveryItemTrans tran = new DeliveryItemTrans();
		tran.setIsActive(1);
		tran.setStatus(DeliveryStatus.POSTED_SOLD_ITEM.getId());
		tran.setBetween(true);
		tran.setDateFrom(getDateFrom());
		tran.setDateTo(getDateTo());
		Map<Long, Product> prods = Collections.synchronizedMap(new HashMap<Long, Product>());
		
		for(DeliveryItemTrans del : DeliveryItemTrans.retrieve(tran)){
			
			Product prod = del.getProduct();
			if(prods!=null && prods.size()>0){
				long id = del.getProduct().getProdid();
				if(!prods.containsKey(id)){
					prod = Product.retrieve(id+""); //retrieving product more details
					prods.put(del.getProduct().getProdid(), prod);
				}else{
					prod = prods.get(id);
					prods.put(del.getProduct().getProdid(), prod);
				}
				
			}else{
				prod = Product.retrieve(del.getProduct().getProdid()+"");//retrieving product more details
				prods.put(del.getProduct().getProdid(), prod);
								
			}
			del.setProduct(prod);
			products.add(del);
			
		}
		
		//Date<String> - product<Long> - tran<DeliveryTrans>
		Map<String, Map<Long, DeliveryItemTrans>> perDate = Collections.synchronizedMap(new HashMap<String,Map<Long, DeliveryItemTrans>>());
		Map<Long, DeliveryItemTrans> items = Collections.synchronizedMap(new HashMap<Long, DeliveryItemTrans>());
		
		for(DeliveryItemTrans dels : products){
			
			long id = dels.getProduct().getProdid();
			
			String date = "";
			
			if(getRangeId()==1){ //day
				date = dels.getDateTrans();
			}else if(getRangeId()==2){//month
				date = dels.getDateTrans().split("-")[1];
			}else if(getRangeId()==3){//year
				date = dels.getDateTrans().split("-")[0];
			}
			
			if(perDate!=null && perDate.size()>0){
				
				if(perDate.containsKey(date)){
					
					//same date and same product
					if(perDate.get(date).containsKey(id)){
						double additionalQty = dels.getQuantity();
						additionalQty += perDate.get(date).get(id).getQuantity(); // old qty
						perDate.get(date).get(id).setQuantity(additionalQty);
					}else{
						perDate.get(date).put(id, dels);
					}
					
				}else{
					items = Collections.synchronizedMap(new HashMap<Long, DeliveryItemTrans>());
					items.put(id, dels);
					perDate.put(date, items);
				}
				
			}else{
				items = Collections.synchronizedMap(new HashMap<Long, DeliveryItemTrans>());
				items.put(id, dels);
				perDate.put(date, items);
			}
			
			
		}
		
		Map<String, Map<Long, DeliveryItemTrans>> treeSort = new TreeMap<String, Map<Long,DeliveryItemTrans>>(perDate);
		
		setTransactionData(treeSort);
		setProductData(prods);
		
		List<DeliveryItemTrans> productsFiltered = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
		for(String date : getTransactionData().keySet()){
			for(DeliveryItemTrans trn : getTransactionData().get(date).values()){
				productsFiltered.add(trn);
			}
		}
		
		products = productsFiltered;
	}
	
	private void filter(){
		
		
		if(getRangeId()==1){
		
		if(getMonthId()<=9){
			setDateFrom(getYearIdFrom() + "-0"+ getMonthId() +"-01"); setDateTo(getYearIdTo() + "-0" + getMonthId() + "-31");
		}else{
			setDateFrom(getYearIdFrom() + "-"+ getMonthId() +"-01"); setDateTo(getYearIdTo() + "-" + getMonthId() + "-31");
		}
		
		}else if(getRangeId()==2){
			setDateFrom(getYearIdFrom() + "-01-01"); setDateTo(getYearIdTo() + "-12-31");
		
		}else{
			setDateFrom(getYearIdFrom() + "-01"+"-01"); setDateTo(getYearIdTo() + "-12-31");
		}
		
		
	}
	
	public void updateDate(){
		
	}
	
	public List getRange() {
		
		range = new ArrayList<>();
		
		range.add(new SelectItem(1, "DAY"));
		range.add(new SelectItem(2, "MONTH"));
		range.add(new SelectItem(3, "YEAR"));
		
		
		return range;
	}

	public void setRange(List range) {
		this.range = range;
	}

	public int getRangeId() {
		if(rangeId==0){
			rangeId=1;
		}
		return rangeId;
	}

	public void setRangeId(int rangeId) {
		this.rangeId = rangeId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	public List getGraphType() {
		
		graphType = new ArrayList<>();
		graphType.add(new SelectItem(1, "Line Graph"));
		graphType.add(new SelectItem(2, "Pie Graph"));
		
		return graphType;
	}

	public void setGraphType(List graphType) {
		this.graphType = graphType;
	}

	public int getGraphId() {
		return graphId;
	}

	public void setGraphId(int graphId) {
		this.graphId = graphId;
	}

	public List<DeliveryItemTrans> getProducts() {
		return products;
	}

	public void setProducts(List<DeliveryItemTrans> products) {
		this.products = products;
	}


	public Map<String, Map<Long, DeliveryItemTrans>> getTransactionData() {
		return transactionData;
	}


	public void setTransactionData(Map<String, Map<Long, DeliveryItemTrans>> transactionData) {
		this.transactionData = transactionData;
	}


	public Map<Long, Product> getProductData() {
		return productData;
	}


	public void setProductData(Map<Long, Product> productData) {
		this.productData = productData;
	}

	public int getMonthId() {
		
		if(monthId==0){
			monthId = DateUtils.getCurrentMonth();
		}
		
		return monthId;
	}

	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}

	public List getMonths() {
		
		months = new ArrayList<>();
		int cnt = 1;
		months.add(new SelectItem(cnt++, "January"));
		months.add(new SelectItem(cnt++, "February"));
		months.add(new SelectItem(cnt++, "March"));
		months.add(new SelectItem(cnt++, "April"));
		months.add(new SelectItem(cnt++, "May"));
		months.add(new SelectItem(cnt++, "June"));
		months.add(new SelectItem(cnt++, "July"));
		months.add(new SelectItem(cnt++, "August"));
		months.add(new SelectItem(cnt++, "September"));
		months.add(new SelectItem(cnt++, "October"));
		months.add(new SelectItem(cnt++, "November"));
		months.add(new SelectItem(cnt++, "December"));
		
		return months;
	}

	public void setMonths(List months) {
		this.months = months;
	}

	public int getYearIdFrom() {
		
		if(yearIdFrom==0){
			yearIdFrom = DateUtils.getCurrentYear();
		}
		
		return yearIdFrom;
	}

	public void setYearIdFrom(int yearIdFrom) {
		this.yearIdFrom = yearIdFrom;
	}

	public List getYearFrom() {
		
		yearFrom = new ArrayList<>();
		for(int year = 2017; year<=DateUtils.getCurrentYear(); year++){
			yearFrom.add(new SelectItem(year, year+""));
		}
		
		return yearFrom;
	}

	public void setYearFrom(List yearFrom) {
		this.yearFrom = yearFrom;
	}

	public int getYearIdTo() {
		
		if(yearIdTo==0){
			yearIdTo = DateUtils.getCurrentYear();
		}
		
		return yearIdTo;
	}

	public void setYearIdTo(int yearIdTo) {
		this.yearIdTo = yearIdTo;
	}

	public List getYearTo() {
		
		yearTo = new ArrayList<>();
		for(int year = 2017; year<=DateUtils.getCurrentYear(); year++){
			yearTo.add(new SelectItem(year, year+""));
		}
		
		return yearTo;
	}

	public int getTypeId() {
		if(typeId==0){
			typeId = 1;
		}
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public List getTypes() {
		
		types = new ArrayList<>();
		types.add(new SelectItem(1, "Delivery"));
		types.add(new SelectItem(2, "Store"));
		return types;
	}

	public void setTypes(List types) {
		this.types = types;
	}

	public void setYearTo(List yearTo) {
		this.yearTo = yearTo;
	}


	
	
}
