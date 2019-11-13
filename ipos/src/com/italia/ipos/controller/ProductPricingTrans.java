package com.italia.ipos.controller;

import java.math.BigDecimal;
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
 * @since 10/02/2016
 *
 */
public class ProductPricingTrans {

	private long pricingid;
	private BigDecimal purchasedprice;
	private BigDecimal sellingprice;
	private BigDecimal netprice;
	private int isActiveprice;
	private ProductProperties productProperties;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private double taxpercentage;
	private Product product;
	
	public ProductPricingTrans(){}
	
	public ProductPricingTrans(
			long pricingid,
			BigDecimal purchasedprice,
			BigDecimal sellingprice,
			BigDecimal netprice,
			int isActiveprice,
			ProductProperties productProperties,
			UserDtls userDtls,
			double taxpercentage,
			Product product
			){
		this.pricingid = pricingid;
		this.purchasedprice = purchasedprice;
		this.sellingprice = sellingprice;
		this.netprice = netprice;
		this.isActiveprice = isActiveprice;
		this.productProperties = productProperties;
		this.userDtls = userDtls;
		this.taxpercentage = taxpercentage;
		this.product = product;
	}
	
	public static String productPricingSQL(String tablename,ProductPricingTrans pricing){
		String sql=" AND "+ tablename +".isActiveprice=" + pricing.getIsActiveprice();
		if(pricing!=null){
			
			if(pricing.getPricingid()!=0){
				sql += " AND "+ tablename +".pricingid=" + pricing.getPricingid();
			}
			if(pricing.getPurchasedprice()!=null){
				sql += " AND "+ tablename +".purchasedprice=" + pricing.getPurchasedprice();
			}
			if(pricing.getSellingprice()!=null){
				sql += " AND "+ tablename +".sellingprice=" + pricing.getSellingprice();
			}
			if(pricing.getNetprice()!=null){
				sql += " AND "+ tablename +".netprice=" + pricing.getNetprice();
			}
			
			if(pricing.getTaxpercentage()!=0){
				sql += " AND "+ tablename +".taxpercentage=" + pricing.getTaxpercentage();
			}
			if(pricing.getProductProperties()!=null){
				if(pricing.getProductProperties().getPropid()!=0){
					sql += " AND "+ tablename +".propid=" + pricing.getProductProperties().getPropid();
				}
			}
			if(pricing.getUserDtls()!=null){
				if(pricing.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + pricing.getUserDtls().getUserdtlsid() ;
				}
			}
			if(pricing.getProduct()!=null){
				if(pricing.getProduct().getProdid()!=0){
					sql += " AND "+ tablename +".prodid=" + pricing.getProduct().getProdid();
				}
			}
		}
		
		return sql;
	}
	
	/**
	 * 
	 * @param obj[ProductPricingTrans][Product]
	 * @return list of product properties
	 */
	public static List<ProductPricingTrans> retrievePrice(Object... obj){
		List<ProductPricingTrans> trans = Collections.synchronizedList(new ArrayList<ProductPricingTrans>());
		String productTable = "prd";
		String priceTable="price";
		String sql = "SELECT * FROM product "+ productTable +", productpricingtrans "+ priceTable +" WHERE "+ productTable +".prodid = "+ priceTable +".prodid";
		
					for(int i=0;i<obj.length; i++){
						if(obj[i] instanceof ProductPricingTrans){
							sql += productPricingSQL(priceTable,(ProductPricingTrans)obj[i]);
						}
						/*if(obj[i] instanceof ProductProperties){
							sql += ProductProperties.productSQL(productTable,(ProductProperties)obj[i]);
						}*/
						if(obj[i] instanceof Product){
							sql += Product.productSQL(productTable,(Product)obj[i]);
						}
					}
			
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL product pricing : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			ProductPricingTrans price = new ProductPricingTrans();
			try{price.setPricingid(rs.getLong("pricingid"));}catch(NullPointerException e){}
			try{price.setPurchasedprice(rs.getBigDecimal("purchasedprice"));}catch(NullPointerException e){}
			try{price.setSellingprice(rs.getBigDecimal("sellingprice"));}catch(NullPointerException e){}
			try{price.setNetprice(rs.getBigDecimal("netprice"));}catch(NullPointerException e){}
			try{price.setIsActiveprice(rs.getInt("isActiveprice"));}catch(NullPointerException e){}
			try{price.setTaxpercentage(rs.getDouble("taxpercentage"));}catch(NullPointerException e){}
			
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			price.setProductProperties(prop);*/
			/**
			 * try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			 */
			
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
			
			price.setProduct(prod);
			
			trans.add(price);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		/*if(trans.size()==0){
			for(int i=0;i<obj.length; i++){
				if(obj[i] instanceof ProductProperties){
					trans.add(createDefaultPrice((ProductProperties)obj[i]));
				}
			}
		}*/
		
		return trans;
	}
	
	/**
	 * 
	 * @param obj[ProductPricingTrans][ProductProperties]
	 * @return list of product properties
	 */
	public static ProductPricingTrans retrievePrice(String productId){
		ProductPricingTrans price = new ProductPricingTrans();
		String productTable = "prd";
		String priceTable="price";
		String sql = "SELECT * FROM product "+ productTable +", productpricingtrans "+ 
		priceTable +" WHERE "+ productTable +".prodid = "+ priceTable +".prodid AND " + productTable + ".prodid=" + productId + " AND price.isActiveprice=1 ";
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL " + sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{price.setPricingid(rs.getLong("pricingid"));}catch(NullPointerException e){}
			try{price.setPurchasedprice(rs.getBigDecimal("purchasedprice"));}catch(NullPointerException e){}
			try{price.setSellingprice(rs.getBigDecimal("sellingprice"));}catch(NullPointerException e){}
			try{price.setNetprice(rs.getBigDecimal("netprice"));}catch(NullPointerException e){}
			try{price.setIsActiveprice(rs.getInt("isActiveprice"));}catch(NullPointerException e){}
			try{price.setTaxpercentage(rs.getDouble("taxpercentage"));}catch(NullPointerException e){}
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			price.setProductProperties(prop);*/
			
			price.setProduct(prod);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		return price;
	}
	
	private static ProductPricingTrans createDefaultPrice(ProductProperties prop){
		ProductPricingTrans price = new ProductPricingTrans();
		BigDecimal priceValue =new BigDecimal("0.00");
		price.setPurchasedprice(priceValue);
		price.setSellingprice(priceValue);
		price.setNetprice(priceValue);
		price.setIsActiveprice(1);
		price.setProductProperties(prop);
		price.setUserDtls(Login.getUserLogin().getUserDtls());
		price.setTaxpercentage(0);
		
		Long id = getInfo(ProductPricingTrans.getLatestId()+1);
		if(id==1){
			price = insertData(price, "1");
		}else if(id==3){
			price = insertData(price, "3");
		}
		return price;
	}
	
	/**
	 * 
	 * @param obj[ProductProperties][UOM][ProductCategory][UserDtls]
	 * @return list of product properties
	 */
	public static List<ProductPricingTrans> retrieve(Object ...obj){
		List<ProductPricingTrans> pricings = Collections.synchronizedList(new ArrayList<ProductPricingTrans>());
		
		String priceTable = "price";
		String propertyTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM productpricingtrans "+ priceTable +","
				+ " product "+ propertyTable +", userdtls "+ userTable +" WHERE "+ priceTable +
				".prodid="+ propertyTable +".prodid AND "+ priceTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
			for(int i=0;i<obj.length;i++){
				if(obj[i] instanceof ProductPricingTrans){
					sql += productPricingSQL(priceTable,(ProductPricingTrans)obj[i]);
				}
				/*if(obj[i] instanceof ProductProperties){
					sql += ProductProperties.productSQL(propertyTable,(ProductProperties)obj[i]);
				}*/
				if(obj[i] instanceof Product){
					sql += Product.productSQL(propertyTable,(Product)obj[i]);
				}
				if(obj[i] instanceof UserDtls){
					sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
				}
			}
		
		System.out.println("SQL ProductTrans : " + sql);	
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			ProductPricingTrans price = new ProductPricingTrans();
			try{price.setPricingid(rs.getLong("pricingid"));}catch(NullPointerException e){}
			try{price.setPurchasedprice(rs.getBigDecimal("purchasedprice"));}catch(NullPointerException e){}
			try{price.setSellingprice(rs.getBigDecimal("sellingprice"));}catch(NullPointerException e){}
			try{price.setNetprice(rs.getBigDecimal("netprice"));}catch(NullPointerException e){}
			try{price.setIsActiveprice(rs.getInt("isActiveprice"));}catch(NullPointerException e){}
			try{price.setTaxpercentage(rs.getDouble("taxpercentage"));}catch(NullPointerException e){}
			
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			price.setProductProperties(prop);*/
			
			Product prod = new Product();
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			price.setProduct(prod);
			
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
			price.setUserDtls(user);
			
			pricings.add(price);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return pricings;
	}
	
	/**
	 * 
	 * @param obj[ProductProperties][UOM][ProductCategory][UserDtls]
	 * @return list of product properties
	 */
	public static ProductPricingTrans price(String productpriceid){
		ProductPricingTrans price = new ProductPricingTrans();
		String priceTable = "price";
		String propertyTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM productpricingtrans "+ priceTable +","
				+ " product "+ propertyTable +", userdtls "+ userTable +" WHERE "+ priceTable +
				".prodid="+ propertyTable +".prodid AND "+ priceTable +".userdtlsid = "+ userTable +".userdtlsid AND " + priceTable + "=" + productpriceid;
		
			
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{price.setPricingid(rs.getLong("pricingid"));}catch(NullPointerException e){}
			try{price.setPurchasedprice(rs.getBigDecimal("purchasedprice"));}catch(NullPointerException e){}
			try{price.setSellingprice(rs.getBigDecimal("sellingprice"));}catch(NullPointerException e){}
			try{price.setNetprice(rs.getBigDecimal("netprice"));}catch(NullPointerException e){}
			try{price.setIsActiveprice(rs.getInt("isActiveprice"));}catch(NullPointerException e){}
			try{price.setTaxpercentage(rs.getDouble("taxpercentage"));}catch(NullPointerException e){}
			
			/*ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			price.setProductProperties(prop);*/
			
			Product prod = new Product();
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			price.setProduct(prod);
			
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
			price.setUserDtls(user);
				
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return price;
	}
	
	public static ProductPricingTrans save(ProductPricingTrans prop){
		if(prop!=null){
			
			long id = ProductPricingTrans.getInfo(prop.getPricingid()==0? ProductPricingTrans.getLatestId()+1 : prop.getPricingid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				prop = ProductPricingTrans.insertData(prop, "1");
			}else if(id==2){
				LogU.add("update Data ");
				prop = ProductPricingTrans.updateData(prop);
			}else if(id==3){
				LogU.add("added new Data ");
				prop = ProductPricingTrans.insertData(prop, "3");
			}
			
		}
		return prop;
	}
	
	public void save(){
			
			long id = getInfo(getPricingid()==0? getLatestId()+1 : getPricingid());
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
	
	public static ProductPricingTrans insertData(ProductPricingTrans prop, String type){
		String sql = "INSERT INTO productpricingtrans ("
				+ "pricingid,"
				+ "purchasedprice,"
				+ "sellingprice,"
				+ "netprice,"
				+ "isActiveprice,"
				+ "propid,"
				+ "userdtlsid,"
				+ "taxpercentage,"
				+ "prodid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productpricingtrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			prop.setPricingid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			prop.setPricingid(id);
			LogU.add("id: " + id);
		}
		
		ps.setBigDecimal(2, prop.getPurchasedprice());
		ps.setBigDecimal(3, prop.getSellingprice());
		ps.setBigDecimal(4, prop.getNetprice());
		ps.setInt(5, prop.getIsActiveprice());
		ps.setLong(6, prop.getProductProperties()==null? 0 : prop.getProductProperties().getPropid());
		ps.setLong(7, prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		ps.setDouble(8, prop.getTaxpercentage());
		ps.setLong(9, prop.getProduct()==null? 0 : prop.getProduct().getProdid());
		
		LogU.add(prop.getPurchasedprice());
		LogU.add(prop.getSellingprice());
		LogU.add(prop.getNetprice());
		LogU.add(prop.getIsActiveprice());
		LogU.add(prop.getProductProperties()==null? 0 : prop.getProductProperties().getPropid());
		LogU.add(prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		LogU.add(prop.getTaxpercentage());
		LogU.add(prop.getProduct()==null? 0 : prop.getProduct().getProdid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productpricingtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prop;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO productpricingtrans ("
				+ "pricingid,"
				+ "purchasedprice,"
				+ "sellingprice,"
				+ "netprice,"
				+ "isActiveprice,"
				+ "propid,"
				+ "userdtlsid,"
				+ "taxpercentage,"
				+ "prodid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productpricingtrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setPricingid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setPricingid(id);
			LogU.add("id: " + id);
		}
		
		ps.setBigDecimal(2, getPurchasedprice());
		ps.setBigDecimal(3, getSellingprice());
		ps.setBigDecimal(4, getNetprice());
		ps.setInt(5, getIsActiveprice());
		ps.setLong(6, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(7, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setDouble(8, getTaxpercentage());
		ps.setLong(9, getProduct()==null? 0 : getProduct().getProdid());
		
		LogU.add(getPurchasedprice());
		LogU.add(getSellingprice());
		LogU.add(getNetprice());
		LogU.add(getIsActiveprice());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getTaxpercentage());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productpricingtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ProductPricingTrans updateData(ProductPricingTrans prop){
		String sql = "UPDATE productpricingtrans SET "
				+ "purchasedprice=?,"
				+ "sellingprice=?,"
				+ "netprice=?,"
				+ "isActiveprice=?,"
				+ "propid=?,"
				+ "userdtlsid=?,"
				+ "taxpercentage=?,"
				+ "prodid=? " 
				+ " WHERE pricingid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productpricingtrans");
		
		ps.setBigDecimal(1, prop.getPurchasedprice());
		ps.setBigDecimal(2, prop.getSellingprice());
		ps.setBigDecimal(3, prop.getNetprice());
		ps.setInt(4, prop.getIsActiveprice());
		ps.setLong(5, prop.getProductProperties()==null? 0 : prop.getProductProperties().getPropid());
		ps.setLong(6, prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		ps.setDouble(7, prop.getTaxpercentage());
		ps.setLong(8, prop.getProduct()==null? 0 : prop.getProduct().getProdid());
		ps.setLong(9, prop.getPricingid());
		
		LogU.add(prop.getPurchasedprice());
		LogU.add(prop.getSellingprice());
		LogU.add(prop.getNetprice());
		LogU.add(prop.getIsActiveprice());
		LogU.add(prop.getProductProperties()==null? 0 : prop.getProductProperties().getPropid());
		LogU.add(prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		LogU.add(prop.getTaxpercentage());
		LogU.add(prop.getProduct()==null? 0 : prop.getProduct().getProdid());
		LogU.add(prop.getPricingid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productpricingtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prop;
	}
	
	public void updateData(){
		String sql = "UPDATE productpricingtrans SET "
				+ "purchasedprice=?,"
				+ "sellingprice=?,"
				+ "netprice=?,"
				+ "isActiveprice=?,"
				+ "propid=?,"
				+ "userdtlsid=?,"
				+ "taxpercentage=?,"
				+ "prodid=? " 
				+ " WHERE pricingid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productpricingtrans");
		
		ps.setBigDecimal(1, getPurchasedprice());
		ps.setBigDecimal(2, getSellingprice());
		ps.setBigDecimal(3, getNetprice());
		ps.setInt(4, getIsActiveprice());
		ps.setLong(5, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(6, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setDouble(7, getTaxpercentage());
		ps.setLong(8, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(9, getPricingid());
		
		System.out.println("update " + getProduct().getProdid());
		
		LogU.add(getPurchasedprice());
		LogU.add(getSellingprice());
		LogU.add(getNetprice());
		LogU.add(getIsActiveprice());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getTaxpercentage());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getPricingid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productpricingtrans : " + s.getMessage());
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
		sql="SELECT pricingid FROM productpricingtrans  ORDER BY pricingid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("pricingid");
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
		ps = conn.prepareStatement("SELECT pricingid FROM productpricingtrans WHERE pricingid=?");
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
	
	public void delete(boolean retain){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "DELETE FROM productpricingtrans WHERE pricingid=?";
		
		if(retain){
			sql = "UPDATE productpricingtrans set isActiveprice=0 WHERE pricingid=?";
		}
		
		String[] params = new String[1];
		params[0] = getPricingid()+"";
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
	
	public long getPricingid() {
		return pricingid;
	}
	public void setPricingid(long pricingid) {
		this.pricingid = pricingid;
	}
	public BigDecimal getPurchasedprice() {
		return purchasedprice;
	}
	public void setPurchasedprice(BigDecimal purchasedprice) {
		this.purchasedprice = purchasedprice;
	}
	public BigDecimal getSellingprice() {
		return sellingprice;
	}
	public void setSellingprice(BigDecimal sellingprice) {
		this.sellingprice = sellingprice;
	}
	public BigDecimal getNetprice() {
		return netprice;
	}
	public void setNetprice(BigDecimal netprice) {
		this.netprice = netprice;
	}
	public int getIsActiveprice() {
		return isActiveprice;
	}
	public void setIsActiveprice(int isActiveprice) {
		this.isActiveprice = isActiveprice;
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
	
	public double getTaxpercentage() {
		return taxpercentage;
	}

	public void setTaxpercentage(double taxpercentage) {
		this.taxpercentage = taxpercentage;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public static void main(String[] args) {
		ProductPricingTrans price = new ProductPricingTrans();
		/*//price.setPricingid(59);
		price.setPurchasedprice(new BigDecimal("1002.00"));
		price.setSellingprice(new BigDecimal("220.00"));
		price.setNetprice(new BigDecimal("20.00"));
		price.setIsActiveprice(1);
		//price.setProductProperties(ProductProperties.properties("1"));
		price.setUserDtls(UserDtls.addedby("3"));
		
		price.setProduct(Product.retrieve("3"));
		
		System.out.println("price" + price.getProduct().getBarcode());
		price.save();*/
		//price.setPricingid(65);
		
		
		
		ProductPricingTrans p = ProductPricingTrans.retrievePrice("7");
		System.out.println("price " + p.getPurchasedprice());
		
	}
	
}
