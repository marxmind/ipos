package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.ipos.bean.SessionBean;
import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 10/04/2016
 * @version 1.0
 */
public class ProductInventory {

	private long invid;
	private double newqty;
	private double oldqty;
	private int isactive;
	private ProductProperties productProperties;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private Product product;
	
	private Double addqty; //temporary data storing
	
	public ProductInventory(){}
	
	public ProductInventory(
			long invid,
			Double newqty,
			Double oldqty,
			int isactive,
			ProductProperties productProperties,
			UserDtls userDtls,
			Product product
			){
		this.invid = invid;
		this.newqty = newqty;
		this.oldqty = oldqty;
		this.isactive = isactive;
		this.productProperties = productProperties;
		this.userDtls = userDtls;
		this.product = product;
	}
	
	public static String productInventorySQL(String tablename,ProductInventory inv){
		String sql= " AND "+ tablename +".isactive=" + inv.getIsactive();
		if(inv!=null){
			if(inv.getInvid()!=0){
				sql += " AND "+ tablename +".invid=" + inv.getInvid();
			}
			if(inv.getNewqty()!=0){
				sql += " AND "+ tablename +".newqty=" + inv.getNewqty();
			}
			if(inv.getOldqty()!=0){
				sql += " AND "+ tablename +".oldqty=" + inv.getOldqty();
			}
			
			if(inv.getProductProperties()!=null){
				sql += " AND "+ tablename +".propid=" + inv.getProductProperties().getPropid();
			}
			if(inv.getProduct()!=null){
				sql += " AND "+ tablename +".prodid=" + inv.getProduct().getProdid();
			}
			if(inv.getUserDtls()!=null){
				if(inv.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + inv.getUserDtls().getUserdtlsid() ;
				}
			}
		}
		return sql;
	}
	
	public static List<ProductInventory> retrieve(String sql, String[] params){
		List<ProductInventory> invs = Collections.synchronizedList(new ArrayList<ProductInventory>());
		String invtTable = "inv";
		String productTable = "prd";
		String userTable = "usr";
		String propTable = "prop";
		String sqlAdd = "SELECT * FROM productinventory "+ invtTable +
				                       ", product "+ productTable +
				                       ", userdtls "+ userTable +
				                       ", productproperties " + propTable +
				                       " WHERE "+ invtTable +".prodid= "+ productTable + ".prodid AND "+ 
				                       invtTable + ".userdtlsid = "+ userTable + ".userdtlsid AND " +
				                       invtTable + ".propid = " + propTable + ".propid ";
		
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
		
		System.out.println("Inventory SQL : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			ProductInventory inv = new ProductInventory();
			try{inv.setInvid(rs.getInt("invid"));}catch(NullPointerException e){}
			//inv.setAddqty(new Double("0.00"));
			try{inv.setNewqty(rs.getDouble("newqty"));}catch(NullPointerException e){}
			try{inv.setOldqty(rs.getDouble("oldqty"));}catch(NullPointerException e){}
			try{inv.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			inv.setProductProperties(prop);
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			inv.setProduct(prod);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			inv.setUserDtls(user);
			invs.add(inv);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		return invs;
	}
	
	/**
	 * 
	 * @param obj[ProductProperties][ProductInventory][UserDtls]
	 * @return list of product properties
	 */
	public static List<ProductInventory> retrieve(Object ...obj){
		List<ProductInventory> invs = Collections.synchronizedList(new ArrayList<ProductInventory>());
		String invtTable = "inv";
		String productTable = "prd";
		String userTable = "usr";
		String sql = "SELECT * FROM productinventory "+ invtTable +", product "+ productTable +", userdtls "+ userTable +" WHERE "+ invtTable +".prodid= "+ productTable +".prodid AND "+ invtTable +".userdtlsid = "+ userTable +".userdtlsid";
			for(int i=0;i<obj.length;i++){
				if(obj[i] instanceof ProductInventory){
					sql += productInventorySQL(invtTable, (ProductInventory)obj[i]);
				}
				/*if(obj[i] instanceof ProductProperties){
					sql += ProductProperties.productSQL(productTable,(ProductProperties)obj[i]);
				}*/
				
				if(obj[i] instanceof Product){
					sql += Product.productSQL(productTable,(Product)obj[i]);
				}
				
				if(obj[i] instanceof UserDtls){
					sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
				}
				
			}
			
		System.out.println("SQL PRODUCT INV " + sql);	
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			ProductInventory inv = new ProductInventory();
			try{inv.setInvid(rs.getInt("invid"));}catch(NullPointerException e){}
			//inv.setAddqty(new Double("0.00"));
			try{inv.setNewqty(rs.getDouble("newqty"));}catch(NullPointerException e){}
			try{inv.setOldqty(rs.getDouble("oldqty"));}catch(NullPointerException e){}
			try{inv.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			inv.setProductProperties(prop);*/
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			prod.setProductProperties(prop);*/
			inv.setProduct(prod);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			inv.setUserDtls(user);
			invs.add(inv);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return invs;
	}
	
	/**
	 * 
	 * @param obj[ProductProperties][ProductInventory][UserDtls]
	 * @return list of product properties
	 */
	public static ProductInventory retrieve(String productInventoryId){
		ProductInventory inv = new ProductInventory();
		String invtTable = "inv";
		String productTable = "prd";
		String userTable = "usr";
		String sql = "SELECT * FROM productinventory "+ invtTable +", product "+ productTable +
				", userdtls "+ userTable +" WHERE "+ invtTable +".prodid= "+ productTable +".prodid AND "+ 
				invtTable +".userdtlsid = "+ userTable +".userdtlsid AND " + invtTable + ".invid=" + productInventoryId;
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{inv.setInvid(rs.getInt("invid"));}catch(NullPointerException e){}
			//inv.setAddqty(new Double("0.00"));
			try{inv.setNewqty(rs.getDouble("newqty"));}catch(NullPointerException e){}
			try{inv.setOldqty(rs.getDouble("oldqty"));}catch(NullPointerException e){}
			try{inv.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			inv.setProductProperties(prop);*/
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			prod.setProductProperties(prop);*/
			inv.setProduct(prod);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			inv.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return inv;
	}
	
	public static void save(ProductInventory prop){
		
		if(prop!=null){
			
			long id = ProductInventory.getInfo(prop.getInvid()==0? ProductInventory.getLatestId()+1 : prop.getInvid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ProductInventory.insertData(prop, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ProductInventory.updateData(prop);
			}else if(id==3){
				LogU.add("added new Data ");
				ProductInventory.insertData(prop, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getInfo(getInvid()==0? getLatestId()+1 : getInvid());
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
	
	public static ProductInventory insertData(ProductInventory prop, String type){
		String sql = "INSERT INTO productinventory ("
				+ "invid,"
				+ "newqty,"
				+ "oldqty,"
				+ "isactive,"
				+ "propid,"
				+ "userdtlsid,"
				+ "prodid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productinventory");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			prop.setInvid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			prop.setInvid(id);
			LogU.add("id: " + id);
		}
		
		ps.setDouble(2, prop.getNewqty());
		ps.setDouble(3, prop.getOldqty());
		ps.setInt(4, prop.getIsactive());
		ps.setLong(5, prop.getProductProperties()==null? 0 : prop.getProductProperties().getPropid());
		ps.setLong(6, prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		ps.setLong(7, prop.getProduct()==null? 0 : prop.getProduct().getProdid());
				
		LogU.add(prop.getNewqty());
		LogU.add(prop.getOldqty());
		LogU.add(prop.getIsactive());
		LogU.add(prop.getProductProperties()==null? 0 : prop.getProductProperties().getPropid());
		LogU.add(prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		LogU.add(prop.getProduct()==null? 0 : prop.getProduct().getProdid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productinventory : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prop;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO productinventory ("
				+ "invid,"
				+ "newqty,"
				+ "oldqty,"
				+ "isactive,"
				+ "propid,"
				+ "userdtlsid,"
				+ "prodid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productinventory");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setInvid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setInvid(id);
			LogU.add("id: " + id);
		}
		
		ps.setDouble(2, getNewqty());
		ps.setDouble(3, getOldqty());
		ps.setInt(4, getIsactive());
		ps.setLong(5, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(6, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(7, getProduct()==null? 0 : getProduct().getProdid());
				
		LogU.add(getNewqty());
		LogU.add(getOldqty());
		LogU.add(getIsactive());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productinventory : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static ProductInventory updateData(ProductInventory prop){
		String sql = "UPDATE productinventory SET "
				+ "newqty=?,"
				+ "oldqty=?,"
				+ "isactive=?,"
				+ "propid=?,"
				+ "userdtlsid=?,"
				+ "prodid=? " 
				+ " WHERE invid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productinventory");
		
		ps.setDouble(1, prop.getNewqty());
		ps.setDouble(2, prop.getOldqty());
		ps.setInt(3, prop.getIsactive());
		ps.setLong(4, prop.getProductProperties()==null? 0 : prop.getProductProperties().getPropid());
		ps.setLong(5, prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		ps.setLong(6, prop.getProduct()==null? 0 : prop.getProduct().getProdid());
		ps.setLong(7, prop.getInvid());
				
		LogU.add(prop.getNewqty());
		LogU.add(prop.getOldqty());
		LogU.add(prop.getIsactive());
		LogU.add(prop.getProductProperties()==null? 0 : prop.getProductProperties().getPropid());
		LogU.add(prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		LogU.add(prop.getProduct()==null? 0 : prop.getProduct().getProdid());
		LogU.add(prop.getInvid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productinventory : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prop;
	}
	
	public void updateData(){
		String sql = "UPDATE productinventory SET "
				+ "newqty=?,"
				+ "oldqty=?,"
				+ "isactive=?,"
				+ "propid=?,"
				+ "userdtlsid=?,"
				+ "prodid=? " 
				+ " WHERE invid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productinventory");
		
		ps.setDouble(1, getNewqty());
		ps.setDouble(2, getOldqty());
		ps.setInt(3, getIsactive());
		ps.setLong(4, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(5, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(6, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(7, getInvid());
				
		LogU.add(getNewqty());
		LogU.add(getOldqty());
		LogU.add(getIsactive());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getInvid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productinventory : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT invid FROM productinventory  ORDER BY invid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("invid");
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
		ps = conn.prepareStatement("SELECT invid FROM productinventory WHERE invid=?");
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
		String sql = "UPDATE productinventory set isactive=0 WHERE invid=?";
		
		
		String[] params = new String[1];
		params[0] = getInvid()+"";
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
	
	public static void invtoryqty(boolean isRevert, Product product, double qty){
		Product prd = new Product();
		prd.setProdid(product.getProdid());
		prd.setIsactiveproduct(1);
		ProductInventory inv = ProductInventory.retrieve(prd).get(0);
		double oldqty = inv.getNewqty();
		double qtynew = 0d;
		if(isRevert){
			qtynew = oldqty + qty;
		}else{
			if(qty>0){
				qtynew = oldqty - qty;
			}else{
				qtynew = oldqty + Math.abs(qty);
			}
		}
		inv.setProductProperties(product.getProductProperties());
		inv.setNewqty(qtynew);
		inv.setOldqty(oldqty);
		inv.save();
	}
	
	public static void invtoryqty(boolean isRevert, Object obj){
		
		Product prd = new Product();
		
		if(obj instanceof StoreProductTrans){
		
			StoreProductTrans tran = (StoreProductTrans)obj;	
			
			prd.setProdid(tran.getProduct().getProdid());
			prd.setIsactiveproduct(1);
			ProductInventory inv = ProductInventory.retrieve(prd).get(0);
			double oldqty = inv.getNewqty();
			double qtynew = 0d;
			if(isRevert){
				qtynew = oldqty + tran.getQuantity();
			}else{
				if(tran.getQuantity()>0){
					qtynew = oldqty - tran.getQuantity();
				}else{
					qtynew = oldqty + Math.abs(tran.getQuantity());
				}
			}
			inv.setProductProperties(tran.getProductProperties());
			inv.setNewqty(qtynew);
			inv.setOldqty(oldqty);
			inv.save();
		
		}else if(obj instanceof StoreReturnWarehouse){
			
			StoreReturnWarehouse tran = (StoreReturnWarehouse)obj;	
			
			prd.setProdid(tran.getProduct().getProdid());
			prd.setIsactiveproduct(1);
			ProductInventory inv = ProductInventory.retrieve(prd).get(0);
			double oldqty = inv.getNewqty();
			double qtynew = 0d;
			if(isRevert){
				qtynew = oldqty + tran.getQuantity();
			}else{
				if(tran.getQuantity()>0){
					qtynew = oldqty - tran.getQuantity();
				}else{
					qtynew = oldqty + Math.abs(tran.getQuantity());
				}
			}
			inv.setProductProperties(tran.getProductProperties());
			inv.setNewqty(qtynew);
			inv.setOldqty(oldqty);
			inv.save();
		}
		
	}
	
	public long getInvid() {
		return invid;
	}
	public void setInvid(long invid) {
		this.invid = invid;
	}
	public double getNewqty() {
		return newqty;
	}
	public void setNewqty(double newqty) {
		this.newqty = newqty;
	}
	public double getOldqty() {
		return oldqty;
	}
	public void setOldqty(double oldqty) {
		this.oldqty = oldqty;
	}
	public int getIsactive() {
		return isactive;
	}
	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	public ProductProperties getProductProperties() {
		return productProperties;
	}
	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
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
	
	public Double getAddqty() {
		return addqty;
	}

	public void setAddqty(Double addqty) {
		this.addqty = addqty;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public static void main(String[] args) {
		ProductInventory inv = new ProductInventory();
		inv.setInvid(33);
		inv.setIsactive(1);
		/*inv.setNewqty(new Double("800"));
		inv.setOldqty(new Double("900"));
		inv.setIsactive(1);
		//inv.setProductProperties(ProductProperties.properties("1"));
		inv.setProduct(Product.retrieve("2"));
		inv.setUserDtls(UserDtls.addedby("1"));
		inv.save();*/
		
		for(ProductInventory i : ProductInventory.retrieve(inv)){
			System.out.println("qty " + i.getNewqty());
		}
	}
	
}
