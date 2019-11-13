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

public class StoreProductTrans {
	
	private long id;
	private String dateTrans;
	private double quantity;
	private int status;
	private int isActive;
	private Timestamp timestamp;
	
	private StoreProduct store;
	private Product product;
	private ProductProperties productProperties;
	private UOM uom;
	private UserDtls userDtls;
	
	private boolean between;
	private String dateFrom;
	private String dateTo;
	
	public StoreProductTrans(){}
	
	public StoreProductTrans(
			long id,
			String dateTrans,
			double quantity,
			int status,
			int isActive,
			StoreProduct store,
			Product product,
			ProductProperties productProperties,
			UOM uom,
			UserDtls userDtls
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.quantity = quantity;
		this.status = status;
		this.isActive = isActive;
		this.store = store;
		this.product = product;
		this.productProperties = productProperties;
		this.uom = uom;
		this.userDtls = userDtls;
	}
	
	public static String productSQL(String tablename,StoreProductTrans product){
		String sql= " AND "+ tablename +".tranisactive=" + product.getIsActive();
		if(product!=null){
			if(product.getId()!=0){
				sql += " AND "+ tablename +".transid=" + product.getId();
			}
			
			if(product.isBetween()){
				
				sql += " AND ("+ tablename +".datetrans>='" + product.getDateFrom()+"' AND " + tablename + ".datetrans<='" + product.getDateTo() + "')";
				
			}else{
			
				if(product.getDateTrans()!=null){
					sql += " AND "+ tablename +".datetrans='" + product.getDateTrans()+"'";
				}
			
			}
			
			if(product.getStatus()!=0){
				sql += " AND "+ tablename +".transtatus=" + product.getStatus();
			}
			
			if(product.getQuantity()!=0){
				
				if(product.getQuantity()==1){
					sql += " AND "+ tablename +".qtytrans=0";
				}else if(product.getQuantity()==2){
					sql += " AND "+ tablename +".qtytrans!=0";
				}
				
			}
			
		}
		
		return sql;
	}
	
	public static List<StoreProductTrans> retrieve(Object... obj){
		List<StoreProductTrans> products = Collections.synchronizedList(new ArrayList<StoreProductTrans>());
		String tranTable = "tran";
		String storeTable = "store";
		String prodTable = "prod";
		String propTable = "prop";
		String uomTable = "uom";
		String userTable = "usr";
		String sql = "SELECT * FROM transferstoretrans "+ tranTable +",  storeproduct "+ storeTable +", product "+ prodTable +" ,productproperties "+ propTable + ", uom " + uomTable + ", userdtls " + userTable +
				" WHERE " + tranTable + ".storeid=" + storeTable + ".storeid AND " + tranTable + ".prodid=" + prodTable + ".prodid AND " + 
				tranTable + ".propid=" + propTable + ".propid AND " + tranTable + ".uomid=" + uomTable + ".uomid AND " + tranTable + ".userdtlsid=" + userTable + ".userdtlsid "; 
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof StoreProductTrans){
				sql += productSQL(tranTable,(StoreProductTrans)obj[i]);
			}
			if(obj[i] instanceof StoreProduct){
				sql += StoreProduct.productSQL(storeTable,(StoreProduct)obj[i]);
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
			
			StoreProductTrans tran = new StoreProductTrans();
			tran.setId(rs.getLong("transid"));
			tran.setDateTrans(rs.getString("datetrans"));
			tran.setQuantity(rs.getDouble("qtytrans"));
			tran.setStatus(rs.getInt("transtatus"));
			tran.setIsActive(rs.getInt("tranisactive"));
			tran.setTimestamp(rs.getTimestamp("trantimestamp"));
			
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
			tran.setStore(store);
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			tran.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			tran.setProductProperties(prop);
						
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			tran.setUom(uom);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			tran.setUserDtls(user);
			
			products.add(tran);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return products;
	}
	
	public static List<StoreProductTrans> retrieve(String sql, String[] params){
		
		List<StoreProductTrans> products = Collections.synchronizedList(new ArrayList<StoreProductTrans>());
		
		String tranTable = "tran";
		String storeTable = "store";
		String prodTable = "prod";
		String propTable = "prop";
		String uomTable = "uom";
		String userTable = "usr";
		String sqlAdd = "SELECT * FROM transferstoretrans "+ tranTable +",  storeproduct "+ storeTable +", product "+ prodTable +" ,productproperties "+ propTable + ", uom " + uomTable + ", userdtls " + userTable +
				" WHERE " + tranTable + ".storeid=" + storeTable + ".storeid AND " + tranTable + ".prodid=" + prodTable + ".prodid AND " + 
				tranTable + ".propid=" + propTable + ".propid AND " + tranTable + ".uomid=" + uomTable + ".uomid AND " + tranTable + ".userdtlsid=" + userTable + ".userdtlsid ";
		
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
		
		System.out.println("store Trans SQL: " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			StoreProductTrans tran = new StoreProductTrans();
			tran.setId(rs.getLong("transid"));
			tran.setDateTrans(rs.getString("datetrans"));
			tran.setQuantity(rs.getDouble("qtytrans"));
			tran.setStatus(rs.getInt("transtatus"));
			tran.setIsActive(rs.getInt("tranisactive"));
			tran.setTimestamp(rs.getTimestamp("trantimestamp"));
			
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
			tran.setStore(store);
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			tran.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			tran.setProductProperties(prop);
						
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			tran.setUom(uom);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			tran.setUserDtls(user);
			
			products.add(tran);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return products;
	}
	
	public static List<StoreProductTrans> retrieveProduct(String sql, String[] params){
		
		List<StoreProductTrans> products = Collections.synchronizedList(new ArrayList<StoreProductTrans>());
		
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
			
			StoreProductTrans tran = new StoreProductTrans();
			tran.setId(rs.getLong("transid"));
			tran.setDateTrans(rs.getString("datetrans"));
			tran.setQuantity(rs.getDouble("qtytrans"));
			tran.setStatus(rs.getInt("transtatus"));
			tran.setIsActive(rs.getInt("tranisactive"));
			tran.setTimestamp(rs.getTimestamp("trantimestamp"));
			
			StoreProduct store = new StoreProduct();
			try{store.setId(rs.getLong("storeid"));}catch(NullPointerException e){}
			tran.setStore(store);
						
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			tran.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			tran.setProductProperties(prop);
						
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			tran.setUom(uom);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			tran.setUserDtls(user);
			
			products.add(tran);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return products;
	}
	
	public static void save(StoreProductTrans prod){
		if(prod!=null){
			
			long id = StoreProductTrans.getInfo(prod.getId() ==0? StoreProductTrans.getLatestId()+1 : prod.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				StoreProductTrans.insertData(prod, "1");
			}else if(id==2){
				LogU.add("update Data ");
				StoreProductTrans.updateData(prod);
			}else if(id==3){
				LogU.add("added new Data ");
				StoreProductTrans.insertData(prod, "3");
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
	
	public static StoreProductTrans insertData(StoreProductTrans prod, String type){
		String sql = "INSERT INTO transferstoretrans ("
				+ "transid,"
				+ "datetrans,"
				+ "qtytrans,"
				+ "transtatus,"
				+ "tranisactive,"
				+ "storeid,"
				+ "prodid,"
				+ "propid,"
				+ "uomid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transferstoretrans");
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
		ps.setString(cnt++, prod.getDateTrans());
		ps.setDouble(cnt++, prod.getQuantity());
		ps.setInt(cnt++, prod.getStatus());
		ps.setInt(cnt++, prod.getIsActive());
		ps.setLong(cnt++, prod.getStore()==null? 0 : prod.getStore().getId());
		ps.setLong(cnt++, prod.getProduct()==null? 0 : prod.getProduct().getProdid());
		ps.setLong(cnt++, prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		ps.setInt(cnt++, prod.getUom()==null? 0 : prod.getUom().getUomid());
		ps.setLong(cnt++, prod.getUserDtls()==null? 0 : prod.getUserDtls().getUserdtlsid());
		
		LogU.add(prod.getDateTrans());
		LogU.add(prod.getQuantity());
		LogU.add(prod.getStatus());
		LogU.add(prod.getIsActive());
		LogU.add(prod.getStore()==null? 0 : prod.getStore().getId());
		LogU.add(prod.getProduct()==null? 0 : prod.getProduct().getProdid());
		LogU.add(prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		LogU.add(prod.getUom()==null? 0 : prod.getUom().getUomid());
		LogU.add(prod.getUserDtls()==null? 0 : prod.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transferstoretrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO transferstoretrans ("
				+ "transid,"
				+ "datetrans,"
				+ "qtytrans,"
				+ "transtatus,"
				+ "tranisactive,"
				+ "storeid,"
				+ "prodid,"
				+ "propid,"
				+ "uomid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transferstoretrans");
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
		ps.setString(cnt++, getDateTrans());
		ps.setDouble(cnt++, getQuantity());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getStore()==null? 0 : getStore().getId());
		ps.setLong(cnt++, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(cnt++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setInt(cnt++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getQuantity());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getStore()==null? 0 : getStore().getId());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transferstoretrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static StoreProductTrans updateData(StoreProductTrans prod){
		String sql = "UPDATE transferstoretrans SET "
				+ "datetrans=?,"
				+ "qtytrans=?,"
				+ "transtatus=?,"
				+ "storeid=?,"
				+ "prodid=?,"
				+ "propid=?,"
				+ "uomid=?,"
				+ "userdtlsid=?" 
				+ " WHERE transid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transferstoretrans");
		
		ps.setString(cnt++, prod.getDateTrans());
		ps.setDouble(cnt++, prod.getQuantity());
		ps.setInt(cnt++, prod.getStatus());
		ps.setLong(cnt++, prod.getStore()==null? 0 : prod.getStore().getId());
		ps.setLong(cnt++, prod.getProduct()==null? 0 : prod.getProduct().getProdid());
		ps.setLong(cnt++, prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		ps.setInt(cnt++, prod.getUom()==null? 0 : prod.getUom().getUomid());
		ps.setLong(cnt++, prod.getUserDtls()==null? 0 : prod.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, prod.getId());
		
		LogU.add(prod.getDateTrans());
		LogU.add(prod.getQuantity());
		LogU.add(prod.getStatus());
		LogU.add(prod.getStore()==null? 0 : prod.getStore().getId());
		LogU.add(prod.getProduct()==null? 0 : prod.getProduct().getProdid());
		LogU.add(prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		LogU.add(prod.getUom()==null? 0 : prod.getUom().getUomid());
		LogU.add(prod.getUserDtls()==null? 0 : prod.getUserDtls().getUserdtlsid());
		LogU.add(prod.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transferstoretrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void updateData(){
		String sql = "UPDATE transferstoretrans SET "
				+ "datetrans=?,"
				+ "qtytrans=?,"
				+ "transtatus=?,"
				+ "storeid=?,"
				+ "prodid=?,"
				+ "propid=?,"
				+ "uomid=?,"
				+ "userdtlsid=?" 
				+ " WHERE transid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table transferstoretrans");
		
		ps.setString(cnt++, getDateTrans());
		ps.setDouble(cnt++, getQuantity());
		ps.setInt(cnt++, getStatus());
		ps.setLong(cnt++, getStore()==null? 0 : getStore().getId());
		ps.setLong(cnt++, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(cnt++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setInt(cnt++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getQuantity());
		LogU.add(getStatus());
		LogU.add(getStore()==null? 0 : getStore().getId());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to transferstoretrans : " + s.getMessage());
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
		sql="SELECT transid FROM transferstoretrans  ORDER BY transid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("transid");
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
		ps = conn.prepareStatement("SELECT transid FROM transferstoretrans WHERE transid=?");
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
		String sql = "UPDATE transferstoretrans set tranisactive=0 WHERE transid=?";
		
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

	public String getDateTrans() {
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public StoreProduct getStore() {
		return store;
	}

	public void setStore(StoreProduct store) {
		this.store = store;
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
	
	public UserDtls getUserDtls() {
		return userDtls;
	}

	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}

	public boolean isBetween() {
		return between;
	}

	public void setBetween(boolean between) {
		this.between = between;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
	
}
