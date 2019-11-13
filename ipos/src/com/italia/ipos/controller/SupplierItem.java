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
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 12/24/2016
 * @version 1.0
 *
 */
public class SupplierItem {

	private long id;
	private String datePurchased;	
	private double purchasedPrice;
	private double quantity; 
	private String remarks;
	private String productExpiration;
	private int isActiveItem;
	private int status;
	private SupplierTrans supplierTrans;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private Product product;
	
	public SupplierItem(){}
	
	public SupplierItem(
			long id,
			String datePurchased,	
			double purchasedPrice,
			double quantity, 
			String remarks,
			String productExpiration,
			int isActiveItem,
			int satatus,
			ProductProperties productProperties,
			SupplierTrans supplierTrans,
			UserDtls userDtls,
			Product product
			){
		this.id = id;
		this.datePurchased = datePurchased;
		this.purchasedPrice = purchasedPrice;
		this.quantity = quantity;
		this.remarks = remarks;
		this.productExpiration = productExpiration;
		this.isActiveItem = isActiveItem;
		this.status = status;
		this.supplierTrans = supplierTrans;
		this.userDtls = userDtls;
		this.product = product;
	}
	
	public static String itemSQL(String tablename,SupplierItem sup){
		String sql= " AND "+ tablename +".isactiveItem=" + sup.getIsActiveItem();
		if(sup!=null){
			if(sup.getId()!=0){
				sql += " AND "+ tablename +".supitemid=" + sup.getId();
			}
			if(sup.getDatePurchased()!=null){
				sql += " AND "+ tablename +".datepurchased =" + sup.getDatePurchased();
			}
			if(sup.getStatus()!=0){
				sql += " AND "+ tablename +".itemstatus =" + sup.getStatus();
			}
			
		}
		return sql;
	}
	
	public static List<SupplierItem> retrieve(Object... obj){
		List<SupplierItem> items = Collections.synchronizedList(new ArrayList<SupplierItem>());
		
		String itemTable = "item";
		String tranTable = "tran";
		String prodTable = "prod";
		String userTable = "usr";
		String sql = "SELECT * FROM  suppliertrans "+ tranTable +",supplieritem " + itemTable + ", product " + prodTable +", userdtls "+ userTable +" WHERE  "+ itemTable +".suptranid="+ tranTable +".suptranid AND " + itemTable + ".prodid="+ prodTable +".prodid AND " + itemTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			
			if(obj[i] instanceof SupplierItem){
				sql += itemSQL(itemTable,(SupplierItem)obj[i]);
			}
			if(obj[i] instanceof SupplierTrans){
				sql += SupplierTrans.supplierSQL(tranTable,(SupplierTrans)obj[i]);
			}
			if(obj[i] instanceof Product){
				sql += Product.productSQL(prodTable,(Product)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
		}
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			SupplierItem item = new SupplierItem();
			try{item.setId(rs.getLong("supitemid"));}catch(NullPointerException e){}
			try{item.setDatePurchased(rs.getString("datepurchased"));}catch(NullPointerException e){}
			try{item.setPurchasedPrice(rs.getDouble("purchasedpriceitem"));}catch(NullPointerException e){}
			try{item.setQuantity(rs.getDouble("quantity"));}catch(NullPointerException e){}
			try{item.setRemarks(rs.getString("remarks"));}catch(NullPointerException e){}
			try{item.setProductExpiration(rs.getString("productexpiration"));}catch(NullPointerException e){}
			try{item.setIsActiveItem(rs.getInt("isactiveItem"));}catch(NullPointerException e){}
			try{item.setStatus(rs.getInt("itemstatus"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setProductProperties(prop);*/
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setProduct(prod);
			
			SupplierTrans tran = new SupplierTrans();
			try{tran.setId(rs.getLong("suptranid"));}catch(NullPointerException e){}
			try{tran.setTransDate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{tran.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{tran.setStatus(rs.getInt("tranStatus"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isActiveTrans"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setSupplierTrans(tran);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			item.setUserDtls(user);
			
			items.add(item);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
	}
	
	public SupplierItem item(String itemId){
		SupplierItem item = new SupplierItem();
		
		String itemTable = "item";
		String tranTable = "tran";
		String prodTable = "prod";
		String userTable = "usr";
		String sql = "SELECT * FROM  suppliertrans "+ tranTable +",supplieritem " + itemTable + ", product " + prodTable +", userdtls "+ userTable +
				" WHERE  "+ itemTable +".suptranid="+ tranTable +".suptranid AND " + itemTable + ".prodid="+ 
				prodTable +".prodid AND " + itemTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+itemTable + ".supitemid=" + itemId;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{item.setId(rs.getLong("supitemid"));}catch(NullPointerException e){}
			try{item.setDatePurchased(rs.getString("datepurchased"));}catch(NullPointerException e){}
			try{item.setPurchasedPrice(rs.getDouble("purchasedpriceitem"));}catch(NullPointerException e){}
			try{item.setQuantity(rs.getDouble("quantity"));}catch(NullPointerException e){}
			try{item.setRemarks(rs.getString("remarks"));}catch(NullPointerException e){}
			try{item.setProductExpiration(rs.getString("productexpiration"));}catch(NullPointerException e){}
			try{item.setIsActiveItem(rs.getInt("isactiveItem"));}catch(NullPointerException e){}
			try{item.setStatus(rs.getInt("itemstatus"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setProductProperties(prop);*/
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setProduct(prod);
			
			SupplierTrans tran = new SupplierTrans();
			try{tran.setId(rs.getLong("suptranid"));}catch(NullPointerException e){}
			try{tran.setTransDate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{tran.setPurchasedPrice(rs.getDouble("purchasedprice"));}catch(NullPointerException e){}
			try{tran.setStatus(rs.getInt("tranStatus"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isActiveTrans"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setSupplierTrans(tran);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			item.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return item;
	}
	
	public static void save(SupplierItem sup){
		if(sup!=null){
			
			long id = SupplierItem.getInfo(sup.getId() ==0? SupplierItem.getLatestId()+1 : sup.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				SupplierItem.insertData(sup, "1");
			}else if(id==2){
				LogU.add("update Data ");
				SupplierItem.updateData(sup);
			}else if(id==3){
				LogU.add("added new Data ");
				SupplierItem.insertData(sup, "3");
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
	
	public static SupplierItem insertData(SupplierItem sup, String type){
		String sql = "INSERT INTO supplieritem ("
				+ "supitemid,"
				+ "datepurchased,"
				+ "purchasedpriceitem,"
				+ "quantity,"
				+ "remarks,"
				+ "productexpiration,"
				+ "isactiveItem,"
				+ "prodid,"
				+ "suptranid,"
				+ "userdtlsid,"
				+ "itemstatus)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table supplieritem");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			sup.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			sup.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, sup.getDatePurchased());
		ps.setDouble(3, sup.getPurchasedPrice());
		ps.setDouble(4, sup.getQuantity());
		ps.setString(5, sup.getRemarks());
		ps.setString(6, sup.getProductExpiration());
		ps.setInt(7, sup.getIsActiveItem());
		ps.setLong(8, sup.getProduct()==null? 0 : sup.getProduct().getProdid());
		ps.setLong(9, sup.getSupplierTrans()==null? 0 : sup.getSupplierTrans().getId());
		ps.setLong(10, sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		ps.setInt(11, sup.getStatus());
		
		LogU.add(sup.getDatePurchased());
		LogU.add(sup.getPurchasedPrice());
		LogU.add(sup.getQuantity());
		LogU.add(sup.getRemarks());
		LogU.add(sup.getProductExpiration());
		LogU.add(sup.getIsActiveItem());
		LogU.add(sup.getProduct()==null? 0 : sup.getProduct().getProdid());
		LogU.add(sup.getSupplierTrans()==null? 0 : sup.getSupplierTrans().getId());
		LogU.add(sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		LogU.add(sup.getStatus());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to supplieritem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sup;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO supplieritem ("
				+ "supitemid,"
				+ "datepurchased,"
				+ "purchasedpriceitem,"
				+ "quantity,"
				+ "remarks,"
				+ "productexpiration,"
				+ "isactiveItem,"
				+ "prodid,"
				+ "suptranid,"
				+ "userdtlsid,"
				+ "itemstatus)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table supplieritem");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, getDatePurchased());
		ps.setDouble(3, getPurchasedPrice());
		ps.setDouble(4, getQuantity());
		ps.setString(5, getRemarks());
		ps.setString(6, getProductExpiration());
		ps.setInt(7, getIsActiveItem());
		ps.setLong(8, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(9, getSupplierTrans()==null? 0 : getSupplierTrans().getId());
		ps.setLong(10, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(11, getStatus());
		
		LogU.add(getDatePurchased());
		LogU.add(getPurchasedPrice());
		LogU.add(getQuantity());
		LogU.add(getRemarks());
		LogU.add(getProductExpiration());
		LogU.add(getIsActiveItem());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getSupplierTrans()==null? 0 : getSupplierTrans().getId());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getStatus());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to supplieritem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static SupplierItem updateData(SupplierItem sup){
		String sql = "UPDATE supplieritem SET "
				+ "datepurchased=?,"
				+ "purchasedpriceitem=?,"
				+ "quantity=?,"
				+ "remarks=?,"
				+ "productexpiration=?,"
				+ "prodid=?,"
				+ "suptranid=?,"
				+ "userdtlsid=?,"
				+ "itemstatus=? " 
				+ " WHERE supitemid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table supplieritem");
		
		ps.setString(1, sup.getDatePurchased());
		ps.setDouble(2, sup.getPurchasedPrice());
		ps.setDouble(3, sup.getQuantity());
		ps.setString(4, sup.getRemarks());
		ps.setString(5, sup.getProductExpiration());
		ps.setLong(6, sup.getProduct()==null? 0 : sup.getProduct().getProdid());
		ps.setLong(7, sup.getSupplierTrans()==null? 0 : sup.getSupplierTrans().getId());
		ps.setLong(8, sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		ps.setInt(9, sup.getStatus());
		ps.setLong(10, sup.getId());
		
		LogU.add(sup.getDatePurchased());
		LogU.add(sup.getPurchasedPrice());
		LogU.add(sup.getQuantity());
		LogU.add(sup.getRemarks());
		LogU.add(sup.getProductExpiration());
		LogU.add(sup.getProduct()==null? 0 : sup.getProduct().getProdid());
		LogU.add(sup.getSupplierTrans()==null? 0 : sup.getSupplierTrans().getId());
		LogU.add(sup.getUserDtls()==null? 0 : (sup.getUserDtls().getUserdtlsid()==null? 0 : sup.getUserDtls().getUserdtlsid()));
		LogU.add(sup.getStatus());
		LogU.add(sup.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to supplieritem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sup;
	}
	
	public void updateData(){
		String sql = "UPDATE supplieritem SET "
				+ "datepurchased=?,"
				+ "purchasedpriceitem=?,"
				+ "quantity=?,"
				+ "remarks=?,"
				+ "productexpiration=?,"
				+ "prodid=?,"
				+ "suptranid=?,"
				+ "userdtlsid=?,"
				+ "itemstatus=? " 
				+ " WHERE supitemid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table supplieritem");
		
		ps.setString(1, getDatePurchased());
		ps.setDouble(2, getPurchasedPrice());
		ps.setDouble(3, getQuantity());
		ps.setString(4, getRemarks());
		ps.setString(5, getProductExpiration());
		ps.setLong(6, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(7, getSupplierTrans()==null? 0 : getSupplierTrans().getId());
		ps.setLong(8, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setInt(9, getStatus());
		ps.setLong(10, getId());
		
		LogU.add(getDatePurchased());
		LogU.add(getPurchasedPrice());
		LogU.add(getQuantity());
		LogU.add(getRemarks());
		LogU.add(getProductExpiration());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getSupplierTrans()==null? 0 : getSupplierTrans().getId());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getStatus());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to supplieritem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT supitemid FROM supplieritem  ORDER BY supitemid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("supitemid");
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
		ps = conn.prepareStatement("SELECT supitemid FROM supplieritem WHERE supitemid=?");
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
		String sql = "UPDATE supplieritem set isactiveItem=0, userdtlsid=? WHERE supitemid=?";
		
		String[] params = new String[2];
		params[0] = getUserDtls().getUserdtlsid()+"";
		params[1] = getId()+"";
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
	public String getDatePurchased() {
		return datePurchased;
	}
	public void setDatePurchased(String datePurchased) {
		this.datePurchased = datePurchased;
	}
	public double getPurchasedPrice() {
		return purchasedPrice;
	}
	public void setPurchasedPrice(double purchasedPrice) {
		this.purchasedPrice = purchasedPrice;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getProductExpiration() {
		return productExpiration;
	}
	public void setProductExpiration(String productExpiration) {
		this.productExpiration = productExpiration;
	}
	public int getIsActiveItem() {
		return isActiveItem;
	}
	public void setIsActiveItem(int isActiveItem) {
		this.isActiveItem = isActiveItem;
	}
	
	public SupplierTrans getSupplierTrans() {
		return supplierTrans;
	}
	public void setSupplierTrans(SupplierTrans supplierTrans) {
		this.supplierTrans = supplierTrans;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public static void main(String[] args) {
		SupplierItem item = new SupplierItem();
		item.setId(1);
		item.setDatePurchased(DateUtils.getCurrentDateYYYYMMDD());
		item.setPurchasedPrice(200.00);
		item.setQuantity(10);
		item.setRemarks("testing again");
		item.setProductExpiration(DateUtils.getCurrentDateYYYYMMDD());
		item.setIsActiveItem(1);
		ProductProperties prop = new ProductProperties();
		prop.setPropid(1);
		//item.setProductProperties(prop);
		SupplierTrans tran = new SupplierTrans();
		tran.setId(1);
		item.setSupplierTrans(tran);
		item.setUserDtls(Login.getUserLogin().getUserDtls());
		item.save();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
