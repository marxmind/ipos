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
 * @since 11/06/2016
 * @version 1.0
 *
 */
public class Product {

	private long prodid;
	private String datecoded;
	private String barcode;
	private String productExpiration;
	private int isactiveproduct;
	private UserDtls userDtls;
	private ProductProperties productProperties;
	private Timestamp timestamp;
	
	private ProductPricingTrans productPricingTrans;
	private List<ProductPricingTrans> pricetrans = Collections.synchronizedList(new ArrayList<ProductPricingTrans>());
	private ProductInventory productInventory;
	
	private String remainingProductDays;
	
	public Product(){}
	
	public Product(
			long prodid,
			String datecoded,
			String barcode,
			String productExpiration,
			int isactiveproduct,
			UserDtls userDtls,
			ProductProperties productProperties
			){
		this.prodid = prodid;
		this.datecoded = datecoded;
		this.barcode = barcode;
		this.productExpiration = productExpiration;
		this.isactiveproduct = isactiveproduct;
		this.userDtls = userDtls;
		this.productProperties = productProperties;
	}
	
	public static String productSQL(String tablename,Product product){
		String sql= " AND "+ tablename +".isactiveproduct=" + product.getIsactiveproduct();
		if(product!=null){
			if(product.getProdid()!=0){
				sql += " AND "+ tablename +".prodid=" + product.getProdid();
			}
			
			if(product.getDatecoded()!=null){
				sql += " AND "+ tablename +".datecoded like '%" + product.getDatecoded()+"%'";
			}
			if(product.getBarcode()!=null){
				sql += " AND "+ tablename +".barcode=" + product.getBarcode();
			}
			if(product.getProductExpiration()!=null){
				sql += " AND "+ tablename +".productExpiration like '%" + product.getProductExpiration()+"%'";
			}
			if(product.getProductProperties()!=null){
				if(product.getProductProperties().getPropid()!=0){
					sql += " AND "+ tablename +".propid=" +product.getProductProperties().getPropid();
				}
			}
			
			if(product.getUserDtls()!=null){
				if(product.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + product.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		
		return sql;
	}
	
	
	
	public static List<Product> retrieve(Object... obj){
		List<Product> prods = Collections.synchronizedList(new ArrayList<Product>());
		String prodTable = "prod";
		String propTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM product "+ prodTable +" ,productproperties "+ 
				propTable +", userdtls "+ userTable +" WHERE " + prodTable + ".propid = " + propTable + ".propid AND "+ 
				prodTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof Product){
				sql += productSQL(prodTable,(Product)obj[i]);
			}
			if(obj[i] instanceof ProductProperties){
				sql += ProductProperties.productSQL(propTable,(ProductProperties)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
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
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prod.setProductProperties(prop);
			
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
			prod.setUserDtls(user);
			
			prods.add(prod);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return prods;
	}
	
	public static Product retrieve(String productId){
		Product prod = new Product();
		String prodTable = "prod";
		String propTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM product "+ prodTable +" ,productproperties "+ 
				propTable +", userdtls "+ userTable +" WHERE " + prodTable + ".propid = " + propTable + ".propid AND "+ 
				prodTable +".userdtlsid = "+ userTable +".userdtlsid AND " + prodTable + ".prodid="+productId;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prod.setProductProperties(prop);
			
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
			prod.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return prod;
	}
	
	public static List<Product> retrieve(String sql, String[] params){
		List<Product> prods = Collections.synchronizedList(new ArrayList<Product>());
		
		String prodTable = "prod";
		String propTable = "prop";
		String userTable = "usr";
		String sqlAdd = "SELECT * FROM product "+ prodTable +" ,productproperties "+ 
				propTable +", userdtls "+ userTable +" WHERE " + prodTable + ".propid = " + propTable + ".propid AND "+ 
				prodTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
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
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prod.setProductProperties(prop);
			
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
			prod.setUserDtls(user);
			
			prods.add(prod);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return prods;
	}
	
	public static Product save(Product prod){
		if(prod!=null){
			
			long id = Product.getInfo(prod.getProdid() ==0? Product.getLatestId()+1 : prod.getProdid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				prod = Product.insertData(prod, "1");
			}else if(id==2){
				LogU.add("update Data ");
				prod = Product.updateData(prod);
			}else if(id==3){
				LogU.add("added new Data ");
				prod = Product.insertData(prod, "3");
			}
			
		}
		return prod;
	}
	
	public void save(){
		
			long id = getInfo(getProdid() ==0? getLatestId()+1 : getProdid());
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
	
	public static Product insertData(Product prod, String type){
		String sql = "INSERT INTO product ("
				+ "prodid,"
				+ "datecoded,"
				+ "barcode,"
				+ "productExpiration,"
				+ "isactiveproduct,"
				+ "propid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table product");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			prod.setProdid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			prod.setProdid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, prod.getDatecoded());
		ps.setString(3, prod.getBarcode());
		ps.setString(4, prod.getProductExpiration());
		ps.setInt(5, prod.getIsactiveproduct());
		ps.setLong(6, prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		ps.setLong(7, prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		
		LogU.add(prod.getDatecoded());
		LogU.add(prod.getBarcode());
		LogU.add(prod.getProductExpiration());
		LogU.add(prod.getIsactiveproduct());
		LogU.add(prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		LogU.add(prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to product : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO product ("
				+ "prodid,"
				+ "datecoded,"
				+ "barcode,"
				+ "productExpiration,"
				+ "isactiveproduct,"
				+ "propid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table product");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setProdid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setProdid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, getDatecoded());
		ps.setString(3, getBarcode());
		ps.setString(4, getProductExpiration());
		ps.setInt(5, getIsactiveproduct());
		ps.setLong(6, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(7, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add(getDatecoded());
		LogU.add(getBarcode());
		LogU.add(getProductExpiration());
		LogU.add(getIsactiveproduct());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to product : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static Product updateData(Product prod){
		String sql = "UPDATE product SET "
				+ "datecoded=?,"
				+ "barcode=?,"
				+ "productExpiration=?,"
				+ "isactiveproduct=?,"
				+ "propid=?,"
				+ "userdtlsid=?" 
				+ " WHERE prodid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table product");
		
		ps.setString(1, prod.getDatecoded());
		ps.setString(2, prod.getBarcode());
		ps.setString(3, prod.getProductExpiration());
		ps.setInt(4, prod.getIsactiveproduct());
		ps.setLong(5, prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		ps.setLong(6, prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		ps.setLong(7, prod.getProdid());
		
		LogU.add(prod.getDatecoded());
		LogU.add(prod.getBarcode());
		LogU.add(prod.getProductExpiration());
		LogU.add(prod.getIsactiveproduct());
		LogU.add(prod.getProductProperties()==null? 0 : prod.getProductProperties().getPropid());
		LogU.add(prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		LogU.add(prod.getProdid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to product : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void updateData(){
		String sql = "UPDATE product SET "
				+ "datecoded=?,"
				+ "barcode=?,"
				+ "productExpiration=?,"
				+ "isactiveproduct=?,"
				+ "propid=?,"
				+ "userdtlsid=?" 
				+ " WHERE prodid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table product");
		
		ps.setString(1, getDatecoded());
		ps.setString(2, getBarcode());
		ps.setString(3, getProductExpiration());
		ps.setInt(4, getIsactiveproduct());
		ps.setLong(5, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(6, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(7, getProdid());
		
		LogU.add(getDatecoded());
		LogU.add(getBarcode());
		LogU.add(getProductExpiration());
		LogU.add(getIsactiveproduct());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getProdid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to product : " + s.getMessage());
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
		sql="SELECT prodid FROM product  ORDER BY prodid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("prodid");
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
		ps = conn.prepareStatement("SELECT prodid FROM product WHERE prodid=?");
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
		String sql = "UPDATE product set isactiveproduct=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE prodid=?";
		
		String[] params = new String[1];
		params[0] = getProdid()+"";
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
	
	public long getProdid() {
		return prodid;
	}

	public void setProdid(long prodid) {
		this.prodid = prodid;
	}

	public String getDatecoded() {
		return datecoded;
	}

	public void setDatecoded(String datecoded) {
		this.datecoded = datecoded;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getProductExpiration() {
		return productExpiration;
	}

	public void setProductExpiration(String productExpiration) {
		this.productExpiration = productExpiration;
	}

	public int getIsactiveproduct() {
		return isactiveproduct;
	}

	public void setIsactiveproduct(int isactiveproduct) {
		this.isactiveproduct = isactiveproduct;
	}

	public UserDtls getUserDtls() {
		return userDtls;
	}

	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public List<ProductPricingTrans> getPricetrans() {
		return pricetrans;
	}

	public void setPricetrans(List<ProductPricingTrans> pricetrans) {
		this.pricetrans = pricetrans;
	}

	public ProductInventory getProductInventory() {
		return productInventory;
	}

	public void setProductInventory(ProductInventory productInventory) {
		this.productInventory = productInventory;
	}

	public ProductPricingTrans getProductPricingTrans() {
		return productPricingTrans;
	}

	public void setProductPricingTrans(ProductPricingTrans productPricingTrans) {
		this.productPricingTrans = productPricingTrans;
	}

	public String getRemainingProductDays() {
		return remainingProductDays;
	}

	public void setRemainingProductDays(String remainingProductDays) {
		this.remainingProductDays = remainingProductDays;
	}

	public static void main(String[] args) {
		
		Product p = new Product();
		p.setProdid(1);
		p.setDatecoded(DateUtils.getCurrentDateMMDDYYYY());
		p.setBarcode("0000000000");
		p.setProductExpiration(DateUtils.getCurrentDateMMDDYYYY());
		p.setIsactiveproduct(1);
		ProductProperties r = new ProductProperties();
		r.setProductname("XXX 100ml");
		UserDtls u = new UserDtls();
		u.setUserdtlsid(1l);
		p.setUserDtls(u);
		
		ProductProperties pr = new ProductProperties();
		pr.setPropid(1);
		p.setProductProperties(pr);
		
		//p.save();
		
		p = new Product();
		p.setIsactiveproduct(1);
		for(Product o : Product.retrieve(p,r)){
			System.out.println("product name : " + o.getProductProperties().getProductname());
		}
		
		
		
	}
	
}








