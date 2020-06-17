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

import org.primefaces.model.StreamedContent;

import com.italia.ipos.bean.SessionBean;
import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.enm.IStore;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 10/02/2016
 * @version 1.0
 *
 */
public class ProductProperties {

	private long propid;
	private String productcode;
	private String productname;
	private String imagepath;
	private int isactive;
	private UOM uom;
	private ProductCategory productCategory;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private ProductGroup productGroup;
	private ProductBrand productBrand;
	
	private ProductPricingTrans productPricingTrans;
	private List<ProductPricingTrans> pricetrans = Collections.synchronizedList(new ArrayList<ProductPricingTrans>());
	private ProductInventory productInventory;
	private StreamedContent productImage;
	
	public ProductProperties(){}
	
	public ProductProperties(
			long propid,
			String productcode,
			String productname,
			String imagepath,
			int isacative,
			UOM uom,
			ProductCategory productCategory,
			UserDtls userDtls,
			ProductGroup productGroup,
			ProductBrand productBrand
			){
		this.propid = propid;
		this.productcode = productcode;
		this.productname = productname;
		this.isactive = isacative;
		this.imagepath = imagepath;
		this.uom = uom;
		this.productCategory = productCategory;
		this.userDtls = userDtls;
		this.productGroup = productGroup;
		this.productBrand = productBrand;
	}
	
	
	public static String productCode(IStore type){
		String storeCode = "GP";
		storeCode = IStore.storeCode(type);
		long id = getLatestId()+1;
		storeCode = "PRODUCT-"+ storeCode;
		String code = storeCode +"-000000000"+id;
		char[] count = String.valueOf(id).toCharArray();
		int size = count.length;
		if(size==1){
			code =  storeCode +"-000000000"+id;
		}else if(size==2){
			code =  storeCode +"-00000000"+id;
		}else if(size==3){
			code =  storeCode +"-0000000"+id;
		}else if(size==4){
			code =  storeCode +"-000000"+id;
		}else if(size==5){
			code =  storeCode +"-00000"+id;
		}else if(size==6){
			code =  storeCode +"-0000"+id;
		}else if(size==7){
			code =  storeCode +"-000"+id;
		}else if(size==8){
			code =  storeCode +"-00"+id;
		}else if(size==9){
			code =  storeCode +"-0"+id;
		}else if(size==10){
			code =  storeCode +"-"+id;
		}
		
		return code;
	}
	
	public static String productSQL(String tablename,ProductProperties prop){
		String sql="";
		if(prop!=null){
			if(prop.getPropid()!=0){
				sql += " AND "+ tablename +".propid=" + prop.getPropid();
			}
			if(prop.getProductcode()!=null){
				sql += " AND "+ tablename +".productcode like '%" + prop.getProductcode() +"%'";
			}
			if(prop.getProductname()!=null){
				sql += " AND "+ tablename +".productname like '%" + prop.getProductname() +"%'";
			}
			if(prop.getImagepath()!=null){
				sql += " AND "+ tablename +".imagepath like '%" + prop.getImagepath() +"%'";
			}
			if(prop.getIsactive()!=0){
				sql += " AND "+ tablename +".isactive=" + prop.getIsactive();
			}
			if(prop.getUserDtls()!=null){
				if(prop.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + prop.getUserDtls().getUserdtlsid() ;
				}
			}
			if(prop.getProductGroup()!=null){
				if(prop.getProductGroup().getProdgroupid()!=0){
					sql += " AND "+ tablename +".prodgroupid=" + prop.getProductGroup().getProdgroupid();
				}
			}
			if(prop.getProductBrand()!=null){
				if(prop.getProductBrand().getProdbrandid()!=0){
					sql += " AND "+ tablename +".prodbrandid=" + prop.getProductBrand().getProdbrandid();
				}
			}
			if(prop.getUom()!=null){
				if(prop.getUom().getUomid()!=0){
					sql += " AND "+ tablename +".uomid=" + prop.getProductBrand().getProdbrandid();
				}
			}
			
		}
		return sql;
	}
	
	
	/**
	 * 
	 * @param obj[ProductProperties][UOM][ProductCategory][UserDtls]
	 * @return list of product properties
	 */
	public static List<ProductProperties> retrieve(Object ...obj){
		//List<ProductProperties> props = Collections.synchronizedList(new ArrayList<ProductProperties>());
		List<ProductProperties> props = new ArrayList<ProductProperties>();
		String productTable = "prd";
		String uomTable = "om";
		String userTable = "usr";
		String catTable = "ct";
		String pgroupTable = "grp";
		String pbrandTable = "brand";
		String sql = "SELECT * FROM productproperties "+ productTable +", "
				+ "productcat "+ catTable +", uom "+ uomTable +", userdtls "+ userTable +
				",productgroup "+ pgroupTable +", productbrand "+ pbrandTable +
				" WHERE "+ productTable +".prodcatid="+ catTable +".prodcatid AND "
				+ productTable +".uomid="+ uomTable +".uomid AND "+ productTable +".userdtlsid="+ userTable +".userdtlsid AND "+ productTable+".prodgroupid="+pgroupTable + ".prodgroupid AND " + productTable+".prodbrandid="+pbrandTable+".prodbrandid ";
		
			for(int i=0;i<obj.length;i++){
				if(obj[i] instanceof ProductProperties){
					sql += productSQL(productTable,(ProductProperties)obj[i]);
				}
				if(obj[i] instanceof UOM){
					sql += UOM.uomSQL(uomTable,(UOM)obj[i]);
				}
				if(obj[i] instanceof ProductCategory){
					sql += ProductCategory.prodCategorySQL(catTable,(ProductCategory)obj[i]);
				}
				if(obj[i] instanceof UserDtls){
					sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
				}
				if(obj[i] instanceof ProductGroup){
					sql += ProductGroup.productGroupSQL(pgroupTable,(ProductGroup)obj[i]);
				}
				if(obj[i] instanceof ProductBrand){
					sql += ProductBrand.productBrandSQL(pbrandTable,(ProductBrand)obj[i]);
				}
			}
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL Properties: " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			//try{uom.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prop.setUom(uom);
			
			ProductCategory cat = new ProductCategory();
			try{cat.setProdcatid(rs.getInt("prodcatid"));}catch(NullPointerException e){}
			try{cat.setCatname(rs.getString("catname"));}catch(NullPointerException e){}
			//try{cat.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));}catch(NullPointerException e){}
			prop.setProductCategory(cat);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			prop.setUserDtls(user);
			
			ProductGroup group = new ProductGroup();
			try{group.setProdgroupid(rs.getLong("prodgroupid"));}catch(NullPointerException e){}
			try{group.setProductgroupname(rs.getString("productgroupname"));}catch(NullPointerException e){}
			try{group.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			prop.setProductGroup(group);
			
			ProductBrand brand = new ProductBrand();
			try{brand.setProdbrandid(rs.getLong("prodbrandid"));}catch(NullPointerException e){}
			try{brand.setProductbrandcode(rs.getString("productbrandcode"));}catch(NullPointerException e){}
			try{brand.setProductbrandname(rs.getString("productbrandname"));}catch(NullPointerException e){}
			try{brand.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			prop.setProductBrand(brand);
			
			props.add(prop);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return props;
	}
	
	public static ProductProperties properties(String productPropertiesId){
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		ProductProperties prop = new ProductProperties();
		String sql = "SELECT * FROM productproperties prd, productcat ct, uom om, userdtls usr,productgroup grp, productbrand brand "
				+ "WHERE prd.prodcatid=ct.prodcatid AND prd.uomid=om.uomid AND "
				+ "prd.userdtlsid=usr.userdtlsid AND prd.prodgroupid=grp.prodgroupid AND prd.prodbrandid=brand.prodbrandid  AND prd.propid=?";
		String[] params = new String[1];
		params[0] = productPropertiesId;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		System.out.println("Properties SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prop.setUom(uom);
			
			ProductCategory cat = new ProductCategory();
			try{cat.setProdcatid(rs.getInt("prodcatid"));}catch(NullPointerException e){}
			try{cat.setCatname(rs.getString("catname"));}catch(NullPointerException e){}
			prop.setProductCategory(cat);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			
			ProductGroup group = new ProductGroup();
			try{group.setProdgroupid(rs.getLong("prodgroupid"));}catch(NullPointerException e){}
			try{group.setProductgroupname(rs.getString("productgroupname"));}catch(NullPointerException e){}
			try{group.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			prop.setProductGroup(group);
			
			ProductBrand brand = new ProductBrand();
			try{brand.setProdbrandid(rs.getLong("prodbrandid"));}catch(NullPointerException e){}
			try{brand.setProductbrandcode(rs.getString("productbrandcode"));}catch(NullPointerException e){}
			try{brand.setProductbrandname(rs.getString("productbrandname"));}catch(NullPointerException e){}
			try{brand.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			prop.setProductBrand(brand);
			
			prop.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return prop;
	}
	
	public static List<ProductProperties> retrieve(String sql, String[] params){
		//List<ProductProperties> props = Collections.synchronizedList(new ArrayList<ProductProperties>());
		List<ProductProperties> props = new ArrayList<ProductProperties>();
		String productTable = "prd";
		String uomTable = "om";
		String userTable = "usr";
		String catTable = "ct";
		String pgroupTable = "grp";
		String pbrandTable = "brand";
		String sqlAdd = "SELECT * FROM productproperties "+ productTable +", "
				+ "productcat "+ catTable +", uom "+ uomTable +", userdtls "+ userTable +
				",productgroup "+ pgroupTable +", productbrand "+ pbrandTable +
				" WHERE "+ productTable +".prodcatid="+ catTable +".prodcatid AND "
				+ productTable +".uomid="+ uomTable +".uomid AND "+ productTable +".userdtlsid="+ userTable +".userdtlsid AND "+ productTable+".prodgroupid="+pgroupTable + ".prodgroupid AND " + productTable+".prodbrandid="+pbrandTable+".prodbrandid ";
		
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
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			//try{uom.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			prop.setUom(uom);
			
			ProductCategory cat = new ProductCategory();
			try{cat.setProdcatid(rs.getInt("prodcatid"));}catch(NullPointerException e){}
			try{cat.setCatname(rs.getString("catname"));}catch(NullPointerException e){}
			//try{cat.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));}catch(NullPointerException e){}
			prop.setProductCategory(cat);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			prop.setUserDtls(user);
			
			ProductGroup group = new ProductGroup();
			try{group.setProdgroupid(rs.getLong("prodgroupid"));}catch(NullPointerException e){}
			try{group.setProductgroupname(rs.getString("productgroupname"));}catch(NullPointerException e){}
			try{group.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			prop.setProductGroup(group);
			
			ProductBrand brand = new ProductBrand();
			try{brand.setProdbrandid(rs.getLong("prodbrandid"));}catch(NullPointerException e){}
			try{brand.setProductbrandcode(rs.getString("productbrandcode"));}catch(NullPointerException e){}
			try{brand.setProductbrandname(rs.getString("productbrandname"));}catch(NullPointerException e){}
			try{brand.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			prop.setProductBrand(brand);
			
			props.add(prop);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return props;
	}
	
	public static ProductProperties save(ProductProperties prop){
		if(prop!=null){
			
			long id = ProductProperties.getInfo(prop.getPropid()==0? ProductProperties.getLatestId()+1 : prop.getPropid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				prop = ProductProperties.insertData(prop, "1");
			}else if(id==2){
				LogU.add("update Data ");
				prop = ProductProperties.updateData(prop);
			}else if(id==3){
				LogU.add("added new Data ");
				prop = ProductProperties.insertData(prop, "3");
			}
			
		}
		return prop;
	}
	
	public void save(){
		
			long id = ProductProperties.getInfo(getPropid()==0? ProductProperties.getLatestId()+1 : getPropid());
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
	
	public static ProductProperties insertData(ProductProperties prop, String type){
		String sql = "INSERT INTO productproperties ("
				+ "propid,"
				+ "productcode,"
				+ "productname,"
				+ "imagepath,"
				+ "isactive,"
				+ "uomid,"
				+ "prodcatid,"
				+ "userdtlsid,"
				+ "prodgroupid,"
				+ "prodbrandid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productproperties");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			prop.setPropid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			prop.setPropid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, prop.getProductcode());
		ps.setString(3, prop.getProductname());
		ps.setString(4, prop.getImagepath());
		ps.setInt(5, prop.getIsactive());
		ps.setInt(6, prop.getUom()==null? 0 : prop.getUom().getUomid());
		ps.setInt(7, prop.getProductCategory()==null? 0 : prop.getProductCategory().getProdcatid());
		ps.setLong(8, prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		ps.setLong(9, prop.getProductGroup()==null? 0 : prop.getProductGroup().getProdgroupid());
		ps.setLong(10, prop.getProductBrand()==null? 0 : prop.getProductBrand().getProdbrandid());
				
		LogU.add(prop.getProductcode());
		LogU.add(prop.getProductname());
		LogU.add(prop.getImagepath());
		LogU.add(prop.getIsactive());
		LogU.add(prop.getUom()==null? 0 : prop.getUom().getUomid());
		LogU.add(prop.getProductCategory()==null? 0 : prop.getProductCategory().getProdcatid());
		LogU.add(prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		LogU.add(prop.getProductGroup()==null? 0 : prop.getProductGroup().getProdgroupid());
		LogU.add(prop.getProductBrand()==null? 0 : prop.getProductBrand().getProdbrandid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productproperties : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prop;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO productproperties ("
				+ "propid,"
				+ "productcode,"
				+ "productname,"
				+ "imagepath,"
				+ "isactive,"
				+ "uomid,"
				+ "prodcatid,"
				+ "userdtlsid,"
				+ "prodgroupid,"
				+ "prodbrandid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productproperties");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setPropid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setPropid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, getProductcode());
		ps.setString(3, getProductname());
		ps.setString(4, getImagepath());
		ps.setInt(5, getIsactive());
		ps.setInt(6, getUom()==null? 0 : getUom().getUomid());
		ps.setInt(7, getProductCategory()==null? 0 : getProductCategory().getProdcatid());
		ps.setLong(8, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(9, getProductGroup()==null? 0 : getProductGroup().getProdgroupid());
		ps.setLong(10, getProductBrand()==null? 0 : getProductBrand().getProdbrandid());
				
		LogU.add(getProductcode());
		LogU.add(getProductname());
		LogU.add(getImagepath());
		LogU.add(getIsactive());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getProductCategory()==null? 0 : getProductCategory().getProdcatid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getProductGroup()==null? 0 : getProductGroup().getProdgroupid());
		LogU.add(getProductBrand()==null? 0 : getProductBrand().getProdbrandid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productproperties : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ProductProperties updateData(ProductProperties prop){
		String sql = "UPDATE productproperties SET "
				+ "productcode=?,"
				+ "productname=?,"
				+ "imagepath=?,"
				+ "isactive=?,"
				+ "uomid=?,"
				+ "prodcatid=?,"
				+ "userdtlsid=?,"
				+ "prodgroupid=?,"
				+ "prodbrandid=? " 
				+ " WHERE propid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productproperties");
		
		ps.setString(1, prop.getProductcode());
		ps.setString(2, prop.getProductname());
		ps.setString(3, prop.getImagepath());
		ps.setInt(4, prop.getIsactive());
		ps.setInt(5, prop.getUom()==null? 0 : prop.getUom().getUomid());
		ps.setInt(6, prop.getProductCategory()==null? 0 : prop.getProductCategory().getProdcatid());
		ps.setLong(7, prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		ps.setLong(8, prop.getProductGroup()==null? 0 : prop.getProductGroup().getProdgroupid());
		ps.setLong(9, prop.getProductBrand()==null? 0 : prop.getProductBrand().getProdbrandid());
		ps.setLong(10, prop.getPropid());
		
		LogU.add(prop.getProductcode());
		LogU.add(prop.getProductname());
		LogU.add(prop.getImagepath());
		LogU.add(prop.getIsactive());
		LogU.add(prop.getUom()==null? 0 : prop.getUom().getUomid());
		LogU.add(prop.getProductCategory()==null? 0 : prop.getProductCategory().getProdcatid());
		LogU.add(prop.getUserDtls()==null? 0 : (prop.getUserDtls().getUserdtlsid()==null? 0 : prop.getUserDtls().getUserdtlsid()));
		LogU.add(prop.getProductGroup()==null? 0 : prop.getProductGroup().getProdgroupid());
		LogU.add(prop.getProductBrand()==null? 0 : prop.getProductBrand().getProdbrandid());
		LogU.add(prop.getPropid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productproperties : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prop;
	}
	
	public void updateData(){
		String sql = "UPDATE productproperties SET "
				+ "productcode=?,"
				+ "productname=?,"
				+ "imagepath=?,"
				+ "isactive=?,"
				+ "uomid=?,"
				+ "prodcatid=?,"
				+ "userdtlsid=?,"
				+ "prodgroupid=?,"
				+ "prodbrandid=? " 
				+ " WHERE propid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productproperties");
		
		ps.setString(1, getProductcode());
		ps.setString(2, getProductname());
		ps.setString(3, getImagepath());
		ps.setInt(4, getIsactive());
		ps.setInt(5, getUom()==null? 0 : getUom().getUomid());
		ps.setInt(6, getProductCategory()==null? 0 : getProductCategory().getProdcatid());
		ps.setLong(7, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(8, getProductGroup()==null? 0 : getProductGroup().getProdgroupid());
		ps.setLong(9, getProductBrand()==null? 0 : getProductBrand().getProdbrandid());
		ps.setLong(10, getPropid());
		
		LogU.add(getProductcode());
		LogU.add(getProductname());
		LogU.add(getImagepath());
		LogU.add(getIsactive());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getProductCategory()==null? 0 : getProductCategory().getProdcatid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getProductGroup()==null? 0 : getProductGroup().getProdgroupid());
		LogU.add(getProductBrand()==null? 0 : getProductBrand().getProdbrandid());
		LogU.add(getPropid());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productproperties : " + s.getMessage());
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
		sql="SELECT propid FROM productproperties  ORDER BY propid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("propid");
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
		ps = conn.prepareStatement("SELECT propid FROM productproperties WHERE propid=?");
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
		String sql = "DELETE FROM productproperties WHERE propid=?";
		
		if(retain){
			sql = "UPDATE productproperties set isactive=0 WHERE propid=?";
		}
		
		String[] params = new String[1];
		params[0] = getPropid()+"";
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
	
	public long getPropid() {
		return propid;
	}
	public void setPropid(long propid) {
		this.propid = propid;
	}
	public String getProductname() {
		return productname;
	}
	public void setProductname(String productname) {
		this.productname = productname;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	public UOM getUom() {
		return uom;
	}
	public void setUom(UOM uom) {
		this.uom = uom;
	}
	public ProductCategory getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
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

	public int getIsactive() {
		return isactive;
	}

	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	
	public ProductPricingTrans getProductPricingTrans() {
		return productPricingTrans;
	}

	public void ProductProperties(ProductPricingTrans productPricingTrans) {
		this.productPricingTrans = productPricingTrans;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public ProductBrand getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(ProductBrand productBrand) {
		this.productBrand = productBrand;
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

	public void setProductPricingTrans(ProductPricingTrans productPricingTrans) {
		this.productPricingTrans = productPricingTrans;
	}

	public String getProductcode() {
		return productcode;
	}

	public void setProductcode(String productcode) {
		this.productcode = productcode;
	}

	public StreamedContent getProductImage() {
		return productImage;
	}

	public void setProductImage(StreamedContent productImage) {
		this.productImage = productImage;
	}

	public static void main(String[] args) {
		
		/*ProductProperties p = new ProductProperties();
				p.setPropid(9);
				p.setProductname("Coca Colas 1500ml");
				p.setImagepath("testing");
				p.setIsactive(1);
				p.setProductCategory(ProductCategory.prodcategory("1"));
				p.setUom(UOM.uom("3"));
				p.setUserDtls(UserDtls.addedby("1"));
				p.setProductGroup(ProductGroup.productGroup("1"));
				p.setProductBrand(ProductBrand.productBrand("1"));
				p.save();
				
				p = new ProductProperties();
				p.setPropid(9);
				p.setIsactive(1);
				
				UOM u = new UOM();
				//u.setUomid(3);
				UserDtls user = new UserDtls();
				user.setUserdtlsid(1L);
				for(ProductProperties s : p.retrieve(u,p,user)){
					System.out.println(s.getProductname());
				}*/
		System.out.println("CODE : " + ProductProperties.productCode(IStore.RESTOBAR));
				
	}
	
}
