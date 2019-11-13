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
import com.italia.ipos.enm.DeliveryStatus;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since
 *
 */
public class DeliveryItem {

	private long id;
	private String dateTrans;
	private double sellingPrice;
	private double chargeAmount;
	private double quantity; 
	private String remarks;
	private int status;
	private int isActive;
	private Product product;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private boolean isBetween;
	private String dateFrom;
	private String dateTo;
	private boolean isStatusOR;
	private int statusParam1;
	private int statusParam2;
	
	private double total;
	
	public DeliveryItem(){}
	
	public DeliveryItem(
				long id,
				String dateTrans,
				double sellingPrice,
				double chargeAmount,
				double quantity, 
				String remarks,
				int status,
				int isActive,
				Product product,
				UserDtls userDtls
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.sellingPrice = sellingPrice;
		this.chargeAmount = chargeAmount;
		this.quantity = quantity;
		this.remarks = remarks;
		this.status = status;
		this.isActive = isActive;
		this.product = product;
		this.userDtls = userDtls;
	}
	
	public static String itemSQL(String tablename,DeliveryItem item){
		String sql= " AND "+ tablename +".isactiveDel=" + item.getIsActive();
		if(item!=null){
			if(item.getId()!=0){
				sql += " AND "+ tablename +".delid=" + item.getId();
			}
			if(item.isBetween()){
				sql += " AND ( "+ tablename +".deldate >='" + item.getDateFrom() + "' AND " + tablename +".deldate <='" + item.getDateTo() + "' ) ";
			}else{
				if(item.getDateTrans()!=null){
					sql += " AND "+ tablename +".deldate ='" + item.getDateTrans() + "'";
				}
			}
			
			if(item.isStatusOR()){
				
				sql += " AND ( "+ tablename +".delStatus =" + item.getStatusParam1() + " OR "+ tablename +".delStatus =" + item.getStatusParam2() + " ) ";
				
			}else{
			
				if(item.getStatus()!=0){
					sql += " AND "+ tablename +".delStatus =" + item.getStatus();
				}
			
			}
			
		}
		return sql;
	}
	
	public static List<DeliveryItem> retrieve(Object... obj){
		List<DeliveryItem> items = Collections.synchronizedList(new ArrayList<DeliveryItem>());
		
		String itemTable = "item";
		String prodTable = "prod";
		String userTable = "usr";
		String sql = "SELECT * FROM  deliveryitem " + itemTable + ", product " + prodTable +", userdtls "+ userTable +
				" WHERE  " + itemTable + ".prodid="+ prodTable +".prodid AND " + itemTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			
			if(obj[i] instanceof DeliveryItem){
				sql += itemSQL(itemTable,(DeliveryItem)obj[i]);
			}
			if(obj[i] instanceof Product){
				sql += Product.productSQL(prodTable,(Product)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
		}
		
        System.out.println("SQL Delivery Item: "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			DeliveryItem item = new DeliveryItem();
			try{item.setId(rs.getLong("delid"));}catch(NullPointerException e){}
			try{item.setDateTrans(rs.getString("deldate"));}catch(NullPointerException e){}
			try{item.setSellingPrice(rs.getDouble("delprice"));}catch(NullPointerException e){}
			try{item.setChargeAmount(rs.getDouble("delcharge"));}catch(NullPointerException e){}
			try{item.setQuantity(rs.getDouble("delquantity"));}catch(NullPointerException e){}
			try{item.setRemarks(rs.getString("delremarks"));}catch(NullPointerException e){}
			try{item.setStatus(rs.getInt("delStatus"));}catch(NullPointerException e){}
			try{item.setIsActive(rs.getInt("isactiveDel"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("deltimestamp"));}catch(NullPointerException e){}
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{ProductProperties prop = new ProductProperties();
			prop.setPropid(rs.getLong("propid"));
			prod.setProductProperties(prop);}catch(NullPointerException e){}
			item.setProduct(prod);
			
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
	
	public static DeliveryItem retrieve(String itemId){
		DeliveryItem item = new DeliveryItem();
		String itemTable = "item";
		String prodTable = "prod";
		String userTable = "usr";
		String sql = "SELECT * FROM  deliveryitem " + itemTable + ", product " + prodTable +", userdtls "+ userTable +
				" WHERE  " + itemTable + ".prodid="+ prodTable +".prodid AND " + itemTable +".userdtlsid = "+ userTable +".userdtlsid AND " +
				itemTable + ".delid=" + itemId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		System.out.println("SQL "+ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{item.setId(rs.getLong("delid"));}catch(NullPointerException e){}
			try{item.setDateTrans(rs.getString("deldate"));}catch(NullPointerException e){}
			try{item.setSellingPrice(rs.getDouble("delprice"));}catch(NullPointerException e){}
			try{item.setChargeAmount(rs.getDouble("delcharge"));}catch(NullPointerException e){}
			try{item.setQuantity(rs.getDouble("delquantity"));}catch(NullPointerException e){}
			try{item.setRemarks(rs.getString("delremarks"));}catch(NullPointerException e){}
			try{item.setStatus(rs.getInt("delStatus"));}catch(NullPointerException e){}
			try{item.setIsActive(rs.getInt("isactiveDel"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("deltimestamp"));}catch(NullPointerException e){}
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{ProductProperties prop = new ProductProperties();
			prop.setPropid(rs.getLong("propid"));
			prod.setProductProperties(prop);}catch(NullPointerException e){}
			item.setProduct(prod);
			
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
	/**
	 * Open query
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<DeliveryItem> retrieve(String sql, String[] params){
		List<DeliveryItem> items = Collections.synchronizedList(new ArrayList<DeliveryItem>());
		
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
		
		System.out.println("SQL Delivery Item : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			DeliveryItem item = new DeliveryItem();
			try{item.setId(rs.getLong("delid"));}catch(NullPointerException e){}
			try{item.setDateTrans(rs.getString("deldate"));}catch(NullPointerException e){}
			try{item.setSellingPrice(rs.getDouble("delprice"));}catch(NullPointerException e){}
			try{item.setChargeAmount(rs.getDouble("delcharge"));}catch(NullPointerException e){}
			try{item.setQuantity(rs.getDouble("delquantity"));}catch(NullPointerException e){}
			try{item.setRemarks(rs.getString("delremarks"));}catch(NullPointerException e){}
			try{item.setStatus(rs.getInt("delStatus"));}catch(NullPointerException e){}
			try{item.setIsActive(rs.getInt("isactiveDel"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("deltimestamp"));}catch(NullPointerException e){}
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			item.setProduct(prod);
			UserDtls userDtls = new UserDtls();
			try{userDtls.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			item.setUserDtls(userDtls);
			items.add(item);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
	}
	
	public static void save(DeliveryItem item){
		if(item!=null){
			
			long id = SupplierItem.getInfo(item.getId() ==0? DeliveryItem.getLatestId()+1 : item.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				DeliveryItem.insertData(item, "1");
			}else if(id==2){
				LogU.add("update Data ");
				DeliveryItem.updateData(item);
			}else if(id==3){
				LogU.add("added new Data ");
				DeliveryItem.insertData(item, "3");
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
	
	public static DeliveryItem insertData(DeliveryItem item, String type){
		String sql = "INSERT INTO deliveryitem ("
				+ "delid,"
				+ "deldate,"
				+ "delprice,"
				+ "delcharge,"
				+ "delquantity,"
				+ "delremarks,"
				+ "delStatus,"
				+ "isactiveDel,"
				+ "prodid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table deliveryitem");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			item.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			item.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, item.getDateTrans());
		ps.setDouble(3, item.getSellingPrice());
		ps.setDouble(4, item.getChargeAmount());
		ps.setDouble(5, item.getQuantity());
		ps.setString(6, item.getRemarks());
		ps.setInt(7, item.getStatus());
		ps.setInt(8, item.getIsActive());
		ps.setLong(9, item.getProduct()==null? 0 : item.getProduct().getProdid());
		ps.setLong(10, item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));		

		LogU.add(item.getDateTrans());
		LogU.add(item.getSellingPrice());
		LogU.add(item.getChargeAmount());
		LogU.add(item.getQuantity());
		LogU.add(item.getRemarks());
		LogU.add(item.getStatus());
		LogU.add(item.getIsActive());
		LogU.add(item.getProduct()==null? 0 : item.getProduct().getProdid());
		LogU.add(item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to deliveryitem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return item;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO deliveryitem ("
				+ "delid,"
				+ "deldate,"
				+ "delprice,"
				+ "delcharge,"
				+ "delquantity,"
				+ "delremarks,"
				+ "delStatus,"
				+ "isactiveDel,"
				+ "prodid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table deliveryitem");
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
		ps.setString(2, getDateTrans());
		ps.setDouble(3, getSellingPrice());
		ps.setDouble(4, getChargeAmount());
		ps.setDouble(5, getQuantity());
		ps.setString(6, getRemarks());
		ps.setInt(7, getStatus());
		ps.setInt(8, getIsActive());
		ps.setLong(9, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(10, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));		

		LogU.add(getDateTrans());
		LogU.add(getSellingPrice());
		LogU.add(getChargeAmount());
		LogU.add(getQuantity());
		LogU.add(getRemarks());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to deliveryitem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static DeliveryItem updateData(DeliveryItem item){
		String sql = "UPDATE deliveryitem SET "
				+ "deldate=?,"
				+ "delprice=?,"
				+ "delcharge=?,"
				+ "delquantity=?,"
				+ "delremarks=?,"
				+ "delStatus=?,"
				+ "prodid=?,"
				+ "userdtlsid=? " 
				+ " WHERE delid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table deliveryitem");
		
		ps.setString(1, item.getDateTrans());
		ps.setDouble(2, item.getSellingPrice());
		ps.setDouble(3, item.getChargeAmount());
		ps.setDouble(4, item.getQuantity());
		ps.setString(5, item.getRemarks());
		ps.setInt(6, item.getStatus());
		ps.setLong(7, item.getProduct()==null? 0 : item.getProduct().getProdid());
		ps.setLong(8, item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));		
		ps.setLong(9, item.getId());
		
		LogU.add(item.getDateTrans());
		LogU.add(item.getSellingPrice());
		LogU.add(item.getChargeAmount());
		LogU.add(item.getQuantity());
		LogU.add(item.getRemarks());
		LogU.add(item.getStatus());
		LogU.add(item.getProduct()==null? 0 : item.getProduct().getProdid());
		LogU.add(item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));
		LogU.add(item.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to deliveryitem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return item;
	}
	
	public void updateData(){
		String sql = "UPDATE deliveryitem SET "
				+ "deldate=?,"
				+ "delprice=?,"
				+ "delcharge=?,"
				+ "delquantity=?,"
				+ "delremarks=?,"
				+ "delStatus=?,"
				+ "prodid=?,"
				+ "userdtlsid=? " 
				+ " WHERE delid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table deliveryitem");
		
		ps.setString(1, getDateTrans());
		ps.setDouble(2, getSellingPrice());
		ps.setDouble(3, getChargeAmount());
		ps.setDouble(4, getQuantity());
		ps.setString(5, getRemarks());
		ps.setInt(6, getStatus());
		ps.setLong(7, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(8, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));		
		ps.setLong(9, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getSellingPrice());
		LogU.add(getChargeAmount());
		LogU.add(getQuantity());
		LogU.add(getRemarks());
		LogU.add(getStatus());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to deliveryitem : " + s.getMessage());
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
		sql="SELECT delid FROM deliveryitem  ORDER BY delid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("delid");
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
		ps = conn.prepareStatement("SELECT delid FROM deliveryitem WHERE delid=?");
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
		String sql = "UPDATE deliveryitem set isactiveDel=0, userdtlsid=? WHERE delid=?";
		
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

	public String getDateTrans() {
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}

	public double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public double getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(double chargeAmount) {
		this.chargeAmount = chargeAmount;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public boolean isBetween() {
		return isBetween;
	}

	public void setBetween(boolean isBetween) {
		this.isBetween = isBetween;
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

	public boolean isStatusOR() {
		return isStatusOR;
	}

	public void setStatusOR(boolean isStatusOR) {
		this.isStatusOR = isStatusOR;
	}

	public int getStatusParam1() {
		return statusParam1;
	}

	public void setStatusParam1(int statusParam1) {
		this.statusParam1 = statusParam1;
	}

	public int getStatusParam2() {
		return statusParam2;
	}

	public void setStatusParam2(int statusParam2) {
		this.statusParam2 = statusParam2;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public static void main(String[] args) {
		
		DeliveryItem item = new DeliveryItem();
		//item.setId(1);
		item.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		item.setSellingPrice(400.00);
		item.setChargeAmount(2.00);
		item.setQuantity(10);
		item.setRemarks("testing new...");
		item.setStatus(1);
		item.setIsActive(1);
		Product prod = new Product();
		prod.setProdid(1);
		item.setProduct(prod);
		UserDtls user = new UserDtls();
		user.setUserdtlsid(1l);
		item.setUserDtls(user);
		item.save();
		
	}

	
	
}










