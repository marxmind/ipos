package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 03/26/2017
 *
 */

public class StoreProduct {
	
	private long id;
	private String barcode;
	private String productName;
	private String uomSymbol;
	private double quantity;
	private double purchasedPrice;
	private double sellingPrice;
	private double netPrice;
	private int isActive;
	private Timestamp timestamp;
	
	private Product product;
	private ProductProperties productProperties;
	private UOM uom;
	
	private double total;
	private double qtyReturn;
	
	public StoreProduct(){}
	
	public StoreProduct(
			long id,
			String barcode,
			String productName,
			String uomSymbol,
			double quantity,
			double purchasedPrice,
			double sellingPrice,
			double netPrice,
			int isActive,
			Product product,
			ProductProperties productProperties,
			UOM uom
			){
		this.id = id;
		this.barcode = barcode;
		this.productName = productName;
		this.uomSymbol = uomSymbol;
		this.quantity = quantity;
		this.purchasedPrice = purchasedPrice;
		this.sellingPrice = sellingPrice;
		this.netPrice = netPrice;
		this.isActive = isActive;
		this.product = product;
		this.productProperties = productProperties;
		this.uom = uom;
	}
	
	public static StoreProduct scanBarcode(String barcode){
		StoreProduct store = null;
		
		String sql = "SELECT * FROM storeproduct WHERE prodIsActive=1 AND barcode=?";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(0, barcode);
		
		/*
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		*/
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			store = new StoreProduct();
			try{store.setId(rs.getLong("storeid"));}catch(NullPointerException e){}
			try{store.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{store.setProductName(rs.getString("productName"));}catch(NullPointerException e){}
			try{store.setUomSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{store.setQuantity(rs.getDouble("qty"));}catch(NullPointerException e){}
			try{store.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{store.setSellingPrice(rs.getDouble("sellingprice"));}catch(NullPointerException e){}
			try{store.setNetPrice(rs.getDouble("netprice"));}catch(NullPointerException e){}
			try{store.setIsActive(rs.getInt("prodIsActive"));}catch(NullPointerException e){}
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			store.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			store.setProductProperties(prop);
						
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			store.setUom(uom);
			
		}
		
		}catch(SQLException e) {
			e.getMessage();
		}
		
		return store;
	}
	
	public static double storeQuantity(boolean isRevert, Product product, double qty){
		String sql = "SELECT * FROM storeproduct WHERE prodIsActive=1 AND prodid=?";
		String[] params = new String[1];
		params[0] = product.getProdid()+"";
		double newQty = 0d;
		StoreProduct store = null;
		try{store = StoreProduct.retrieveProduct(sql, params).get(0);}catch(IndexOutOfBoundsException e){}
		if(store!=null){
			
			if(isRevert){
				newQty = store.getQuantity() + qty;
				store.setQuantity(newQty);
				store.save();
				return qty;
			}else{
				if(store.getQuantity() > qty){
					newQty = store.getQuantity() - qty;
					store.setQuantity(newQty);
					store.save();
					return qty;
				}else if(store.getQuantity() == qty){
					store.setQuantity(0);
					store.save();
					return qty;
				}else if(store.getQuantity() < qty){
					newQty = store.getQuantity();
					store.setQuantity(0);
					store.save();
					return newQty;
				}
			}
		}
		return 0;
	}
	
	public static double storeQuantityRecallReplace(double oldQty, double userNewQuantity, Product product){
		String sql = "SELECT * FROM storeproduct WHERE prodIsActive=1 AND prodid=?";
		String[] params = new String[1];
		params[0] = product.getProdid()+"";
		double newQty = 0d;
		StoreProduct store = null;
		try{store = StoreProduct.retrieveProduct(sql, params).get(0);}catch(IndexOutOfBoundsException e){}
		if(store!=null){
			
				newQty = store.getQuantity() + oldQty;
				
				if(newQty>userNewQuantity){
					newQty = newQty - userNewQuantity;
					store.setQuantity(newQty);
					store.save();
					return userNewQuantity;
				}else if(newQty==userNewQuantity){
					store.setQuantity(0);
					store.save();
					return 0;
				}else if(newQty<userNewQuantity){
					store.setQuantity(newQty);
					store.save();
					return newQty;
				}
			
		}
		return 0;
	}
	
	public static String productSQL(String tablename,StoreProduct product){
		String sql= " AND "+ tablename +".prodIsActive=" + product.getIsActive();
		if(product!=null){
			if(product.getId()!=0){
				sql += " AND "+ tablename +".storeid=" + product.getId();
			}
			
			if(product.getProductName()!=null){
				sql += " AND "+ tablename +".productName like '%" + product.getProductName()+"%'";
			}
			
			if(product.getBarcode()!=null){
				sql += " AND "+ tablename +".barcode=" + product.getBarcode();
			}
			
			if(product.getQuantity()!=0){
				
				if(product.getQuantity()==1){
					sql += " AND "+ tablename +".qty=0";
				}else if(product.getQuantity()==2){
					sql += " AND "+ tablename +".qty!=0";
				}
				
			}
			
		}
		
		return sql;
	}
	
	public static List<StoreProduct> retrieve(Object... obj){
		//List<StoreProduct> products = Collections.synchronizedList(new ArrayList<StoreProduct>());
		List<StoreProduct> products = new ArrayList<StoreProduct>();
		String storeTable = "store";
		String prodTable = "prod";
		String propTable = "prop";
		String uomTable = "uom";
		String sql = "SELECT * FROM storeproduct "+ storeTable +", product "+ prodTable +" ,productproperties "+ propTable + ", uom " + uomTable + 
				" WHERE " + storeTable + ".prodid=" + prodTable + ".prodid AND " + storeTable + ".propid=" + propTable + ".propid AND " + storeTable + ".uomid=" + uomTable + ".uomid "; 
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof StoreProduct){
				sql += productSQL(storeTable,(StoreProduct)obj[i]);
			}
			if(obj[i] instanceof Product){
				sql += Product.productSQL(prodTable,(Product)obj[i]);
			}
			if(obj[i] instanceof ProductProperties){
				sql += ProductProperties.productSQL(propTable,(ProductProperties)obj[i]);
			}
			if(obj[i] instanceof UOM){
				sql += UOM.uomSQL(uomTable,(UOM)obj[i]);
			}
		}
		
        System.out.println("SQL Product: "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			StoreProduct store = new StoreProduct();
			try{store.setId(rs.getLong("storeid"));}catch(NullPointerException e){}
			try{store.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{store.setProductName(rs.getString("productName"));}catch(NullPointerException e){}
			try{store.setUomSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{store.setQuantity(rs.getDouble("qty"));}catch(NullPointerException e){}
			try{store.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{store.setSellingPrice(rs.getDouble("sellingprice"));}catch(NullPointerException e){}
			try{store.setNetPrice(rs.getDouble("netprice"));}catch(NullPointerException e){}
			try{store.setIsActive(rs.getInt("prodIsActive"));}catch(NullPointerException e){}
						
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			store.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			//ProductBrand brand = new ProductBrand();
			//brand.setProdbrandid(rs.getLong(""));
			
			store.setProductProperties(prop);
						
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			store.setUom(uom);
			
			products.add(store);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return products;
	}
	
	public static List<StoreProduct> retrieve(String sql, String[] params){
		
		//List<StoreProduct> products = Collections.synchronizedList(new ArrayList<StoreProduct>());
		List<StoreProduct> products = new ArrayList<StoreProduct>();
		
		String storeTable = "store";
		String prodTable = "prod";
		String propTable = "prop";
		String uomTable = "uom";
		String sqlAdd = "SELECT * FROM storeproduct "+ storeTable +", product "+ prodTable +" ,productproperties "+ propTable + ", uom " + uomTable + 
				" WHERE " + storeTable + ".prodid=" + prodTable + ".prodid AND " + storeTable + ".propid=" + propTable + ".propid AND " + storeTable + ".uomid=" + uomTable + ".uomid ";
		
		sql = sqlAdd + sql;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		System.out.println("SQL STORE " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			StoreProduct store = new StoreProduct();
			try{store.setId(rs.getLong("storeid"));}catch(NullPointerException e){}
			try{store.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{store.setProductName(rs.getString("productName"));}catch(NullPointerException e){}
			try{store.setUomSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{store.setQuantity(rs.getDouble("qty"));}catch(NullPointerException e){}
			try{store.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{store.setSellingPrice(rs.getDouble("sellingprice"));}catch(NullPointerException e){}
			try{store.setNetPrice(rs.getDouble("netprice"));}catch(NullPointerException e){}
			try{store.setIsActive(rs.getInt("prodIsActive"));}catch(NullPointerException e){}
						
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			store.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			store.setProductProperties(prop);
						
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			store.setUom(uom);
			
			products.add(store);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return products;
	}
	
	public static List<StoreProduct> retrieveProduct(String sql, String[] params){
		
		//List<StoreProduct> products = Collections.synchronizedList(new ArrayList<StoreProduct>());
		List<StoreProduct> products = new ArrayList<StoreProduct>();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		System.out.println("SQL store " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			StoreProduct store = new StoreProduct();
			try{store.setId(rs.getLong("storeid"));}catch(NullPointerException e){}
			try{store.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{store.setProductName(rs.getString("productName"));}catch(NullPointerException e){}
			try{store.setUomSymbol(rs.getString("uomsymbol"));}catch(NullPointerException e){}
			try{store.setQuantity(rs.getDouble("qty"));}catch(NullPointerException e){}
			try{store.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{store.setSellingPrice(rs.getDouble("sellingprice"));}catch(NullPointerException e){}
			try{store.setNetPrice(rs.getDouble("netprice"));}catch(NullPointerException e){}
			try{store.setIsActive(rs.getInt("prodIsActive"));}catch(NullPointerException e){}
						
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			store.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			store.setProductProperties(prop);
						
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			store.setUom(uom);
			
			products.add(store);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return products;
	}
	
	public static double retrieveCurrentQty(String productId){
		
		String sql = "SELECT * FROM storeproduct WHERE prodIsActive=1 AND prodid=?";
		String[] params = new String[1];
		params[0] = productId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			return rs.getDouble("qty");
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return 0;
	}
	
	public static void save(StoreProduct prod){
		if(prod!=null){
			
			long id = StoreProduct.getInfo(prod.getId() ==0? StoreProduct.getLatestId()+1 : prod.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				StoreProduct.insertData(prod, "1");
			}else if(id==2){
				LogU.add("update Data ");
				StoreProduct.updateData(prod);
			}else if(id==3){
				LogU.add("added new Data ");
				StoreProduct.insertData(prod, "3");
			}
			
		}
	}
	
	public void save(){
		
			long id = getInfo(getId() ==0? getLatestId()+1 : getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				insertData("1");
			}else if(id==2){
				LogU.add("update Data ");
				updateData();
			}else if(id==3){
				LogU.add("added new Data ");
				insertData("3");
			}
			
	}
	
	public static StoreProduct insertData(StoreProduct prod, String type){
		String sql = "INSERT INTO storeproduct ("
				+ "storeid,"
				+ "barcode,"
				+ "productName,"
				+ "uomsymbol,"
				+ "qty,"
				+ "purchasedprice,"
				+ "sellingprice,"
				+ "netprice,"
				+ "prodIsActive,"
				+ "prodid,"
				+ "propid,"
				+ "uomid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table storeproduct");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			prod.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			prod.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, prod.getBarcode());
		ps.setString(cnt++, prod.getProductName());
		ps.setString(cnt++, prod.getUomSymbol());
		ps.setDouble(cnt++, prod.getQuantity());
		ps.setDouble(cnt++, prod.getPurchasedPrice());
		ps.setDouble(cnt++, prod.getSellingPrice());
		ps.setDouble(cnt++, prod.getNetPrice());
		ps.setInt(cnt++, prod.getIsActive());
		ps.setLong(cnt++, prod.getProduct()==null? 0 : prod.getProduct().getProdid());
		ps.setLong(cnt++, prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		ps.setInt(cnt++, prod.getUom()==null? 0 : prod.getUom().getUomid());
		
		LogU.add(prod.getBarcode());
		LogU.add(prod.getProductName());
		LogU.add(prod.getUomSymbol());
		LogU.add(prod.getQuantity());
		LogU.add(prod.getPurchasedPrice());
		LogU.add(prod.getSellingPrice());
		LogU.add(prod.getNetPrice());
		LogU.add(prod.getIsActive());
		LogU.add(prod.getProduct()==null? 0 : prod.getProduct().getProdid());
		LogU.add(prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		LogU.add(prod.getUom()==null? 0 : prod.getUom().getUomid());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to storeproduct : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO storeproduct ("
				+ "storeid,"
				+ "barcode,"
				+ "productName,"
				+ "uomsymbol,"
				+ "qty,"
				+ "purchasedprice,"
				+ "sellingprice,"
				+ "netprice,"
				+ "prodIsActive,"
				+ "prodid,"
				+ "propid,"
				+ "uomid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table storeproduct");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, getBarcode());
		ps.setString(cnt++, getProductName());
		ps.setString(cnt++, getUomSymbol());
		ps.setDouble(cnt++, getQuantity());
		ps.setDouble(cnt++, getPurchasedPrice());
		ps.setDouble(cnt++, getSellingPrice());
		ps.setDouble(cnt++, getNetPrice());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(cnt++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setInt(cnt++, getUom()==null? 0 : getUom().getUomid());
		
		LogU.add(getBarcode());
		LogU.add(getProductName());
		LogU.add(getUomSymbol());
		LogU.add(getQuantity());
		LogU.add(getPurchasedPrice());
		LogU.add(getSellingPrice());
		LogU.add(getNetPrice());
		LogU.add(getIsActive());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to storeproduct : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static StoreProduct updateData(StoreProduct prod){
		String sql = "UPDATE storeproduct SET "
				+ "barcode=?,"
				+ "productName=?,"
				+ "uomsymbol=?,"
				+ "qty=?,"
				+ "purchasedprice=?,"
				+ "sellingprice=?,"
				+ "netprice=?,"
				+ "prodid=?,"
				+ "propid=?,"
				+ "uomid=? " 
				+ " WHERE storeid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table storeproduct");
		
		ps.setString(cnt++, prod.getBarcode());
		ps.setString(cnt++, prod.getProductName());
		ps.setString(cnt++, prod.getUomSymbol());
		ps.setDouble(cnt++, prod.getQuantity());
		ps.setDouble(cnt++, prod.getPurchasedPrice());
		ps.setDouble(cnt++, prod.getSellingPrice());
		ps.setDouble(cnt++, prod.getNetPrice());
		ps.setLong(cnt++, prod.getProduct()==null? 0 : prod.getProduct().getProdid());
		ps.setLong(cnt++, prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		ps.setInt(cnt++, prod.getUom()==null? 0 : prod.getUom().getUomid());
		ps.setLong(cnt++, prod.getId());
		
		LogU.add(prod.getBarcode());
		LogU.add(prod.getProductName());
		LogU.add(prod.getUomSymbol());
		LogU.add(prod.getQuantity());
		LogU.add(prod.getPurchasedPrice());
		LogU.add(prod.getSellingPrice());
		LogU.add(prod.getNetPrice());
		LogU.add(prod.getProduct()==null? 0 : prod.getProduct().getProdid());
		LogU.add(prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		LogU.add(prod.getUom()==null? 0 : prod.getUom().getUomid());
		LogU.add(prod.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to storeproduct : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void updateData(){
		String sql = "UPDATE storeproduct SET "
				+ "barcode=?,"
				+ "productName=?,"
				+ "uomsymbol=?,"
				+ "qty=?,"
				+ "purchasedprice=?,"
				+ "sellingprice=?,"
				+ "netprice=?,"
				+ "prodid=?,"
				+ "propid=?,"
				+ "uomid=? " 
				+ " WHERE storeid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table storeproduct");
		
		ps.setString(cnt++, getBarcode());
		ps.setString(cnt++, getProductName());
		ps.setString(cnt++, getUomSymbol());
		ps.setDouble(cnt++, getQuantity());
		ps.setDouble(cnt++, getPurchasedPrice());
		ps.setDouble(cnt++, getSellingPrice());
		ps.setDouble(cnt++, getNetPrice());
		ps.setLong(cnt++, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(cnt++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setInt(cnt++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getBarcode());
		LogU.add(getProductName());
		LogU.add(getUomSymbol());
		LogU.add(getQuantity());
		LogU.add(getPurchasedPrice());
		LogU.add(getSellingPrice());
		LogU.add(getNetPrice());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to storeproduct : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT storeid FROM storeproduct  ORDER BY storeid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("storeid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT storeid FROM storeproduct WHERE storeid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE storeproduct set prodIsActive=0 WHERE storeid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getUomSymbol() {
		return uomSymbol;
	}
	public void setUomSymbol(String uomSymbol) {
		this.uomSymbol = uomSymbol;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getPurchasedPrice() {
		return purchasedPrice;
	}
	public void setPurchasedPrice(double purchasedPrice) {
		this.purchasedPrice = purchasedPrice;
	}
	public double getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	public double getNetPrice() {
		return netPrice;
	}
	public void setNetPrice(double netPrice) {
		this.netPrice = netPrice;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public ProductProperties getProductProperties() {
		return productProperties;
	}
	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}
	public UOM getUom() {
		return uom;
	}
	public void setUom(UOM uom) {
		this.uom = uom;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getQtyReturn() {
		return qtyReturn;
	}

	public void setQtyReturn(double qtyReturn) {
		this.qtyReturn = qtyReturn;
	}

	public static void main(String[] args) {
		
		StoreProduct s = new StoreProduct();
		s.setBarcode("11111111111");
		s.setProductName("first thing first");
		s.setUomSymbol("pcs");
		s.setQuantity(1);
		s.setPurchasedPrice(123);
		s.setSellingPrice(300);
		s.setNetPrice(100);
		s.setIsActive(1);
		
		Product prod = new Product();
		prod.setProdid(1);
		s.setProduct(prod);
		
		ProductProperties prop = new ProductProperties();
		prop.setPropid(1);
		s.setProductProperties(prop);
					
		UOM uom = new UOM();
		uom.setUomid(1);
		s.setUom(uom);
		
		s.save();
		
	}
	
}
