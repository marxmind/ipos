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
 * @author mark itaia
 * @since 03/09/2017
 * @version 1.0
 *
 */
public class ProductReturnSupplier {

	private long id;
	private String dateTrans;
	private double quantity;
	private double amount;
	private int isActive;
	private int status;
	private String remarks;
	private Timestamp timestamp;
	
	private Product product;
	private ProductProperties properties;
	private UOM uom;
	private UserDtls userDtls;
	private Supplier supplier;
	
	private String dateFrom;
	private String dateTo;
	private boolean between;
	
	public ProductReturnSupplier(){}
	
	public ProductReturnSupplier(
			long id,
			String dateTrans,
			double quantity,
			double amount,
			int isActive,
			int status,
			String remarks,
			Product product,
			ProductProperties properties,
			UOM uom,
			UserDtls userDtls,
			Supplier supplier
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.quantity = quantity;
		this.amount = amount;
		this.isActive = isActive;
		this.status = status;
		this.remarks = remarks;
		this.product = product;
		this.properties = properties;
		this.uom = uom;
		this.userDtls = userDtls;
		this.supplier = supplier;
	}
	
	public static String returnSQL(String tablename,ProductReturnSupplier sup){
		String sql= " AND "+ tablename +".retisactive=" + sup.getIsActive();
		if(sup!=null){
			
			if(sup.getId()!=0){
				sql += " AND "+ tablename +".prodretid=" + sup.getId();
			}
			
			if(sup.isBetween()){
				
				sql += " AND ("+ tablename +".datereturned>='" + sup.getDateFrom() +"' AND " + tablename + ".datereturned<='" + sup.getDateTo() + "') ";
				
			}else{
			
			if(sup.getDateTrans()!=null){
				sql += " AND "+ tablename +".datereturned='" + sup.getDateTrans() +"'";
			}
			
			}
			
			
			
		}
		
		return sql;
	}

	public static List<ProductReturnSupplier> retrieve(Object... obj){
		List<ProductReturnSupplier> sups = Collections.synchronizedList(new ArrayList<ProductReturnSupplier>());
		String retTable = "ret";
		String prodTable = "prod";
		String propTable = "prop";
		String uomTable = "uom";
		String userTable = "usr";
		String supTable = "sup";
		String sql = "SELECT * FROM productreturnsupplier "+ retTable + ", product  "+ prodTable + ", productproperties  " + propTable + ", uom " + uomTable +", userdtls "+ userTable +", supplier "+ supTable +" WHERE " +
				retTable + ".prodid=" + prodTable +".prodid AND " + retTable + ".propid=" + propTable +".propid AND " +  retTable + ".uomid=" + uomTable + ".uomid AND " + 
				retTable + ".userdtlsid=" + userTable + ".userdtlsid AND " + retTable + ".supid = " + supTable + ".supid ";
		
		for(int i=0;i<obj.length;i++){
			
			if(obj[i] instanceof ProductReturnSupplier){
				sql += returnSQL(retTable,(ProductReturnSupplier)obj[i]);
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
			
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
			
			if(obj[i] instanceof Supplier){
				sql += Supplier.supplierSQL(supTable,(Supplier)obj[i]);
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
			
			ProductReturnSupplier sup = new ProductReturnSupplier();
			try{sup.setId(rs.getLong("prodretid"));}catch(NullPointerException e){}
			try{sup.setDateTrans(rs.getString("datereturned"));}catch(NullPointerException e){}
			try{sup.setQuantity(rs.getDouble("returnqty"));}catch(NullPointerException e){}
			try{sup.setAmount(rs.getDouble("returnamount"));}catch(NullPointerException e){}
			try{sup.setIsActive(rs.getInt("retisactive"));}catch(NullPointerException e){}
			try{sup.setStatus(rs.getInt("retstatus"));}catch(NullPointerException e){}
			try{sup.setRemarks(rs.getString("returnremarks"));}catch(NullPointerException e){}
			try{sup.setTimestamp(rs.getTimestamp("rettimestamp"));}catch(NullPointerException e){}
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			sup.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			sup.setProperties(prop);
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			sup.setUom(uom);
			
			Supplier sp = new Supplier();
			try{sp.setSupid(rs.getLong("supid"));}catch(NullPointerException e){}
			try{sp.setSuppliername(rs.getString("suppliername"));}catch(NullPointerException e){}
			try{sp.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{sp.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{sp.setOwnername(rs.getString("ownername"));}catch(NullPointerException e){}
			try{sp.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{sp.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			sup.setSupplier(sp);
			
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
			sup.setUserDtls(user);
			
			sups.add(sup);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return sups;
	}
	
	
	public static ProductReturnSupplier retrieveProduct(long returnId){
		ProductReturnSupplier sup = new ProductReturnSupplier();
		String retTable = "ret";
		String prodTable = "prod";
		String propTable = "prop";
		String uomTable = "uom";
		String userTable = "usr";
		String supTable = "sup";
		String sql = "SELECT * FROM productreturnsupplier "+ retTable + ", product  "+ prodTable + ", productproperties  " + propTable + ", uom " + uomTable + ", userdtls "+ userTable +", supplier "+ supTable +" WHERE " +
				retTable + ".prodid=" + prodTable +".prodid AND " + retTable + ".propid=" + propTable +".propid AND " +  retTable + ".uomid=" + uomTable + ".uomid AND " + 
				retTable + ".userdtlsid=" + userTable + ".userdtlsid AND " + retTable + ".supid="+ supTable + ".supid AND " + retTable + ".prodretid="+ returnId;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{sup.setId(rs.getLong("prodretid"));}catch(NullPointerException e){}
			try{sup.setDateTrans(rs.getString("datereturned"));}catch(NullPointerException e){}
			try{sup.setQuantity(rs.getDouble("returnqty"));}catch(NullPointerException e){}
			try{sup.setAmount(rs.getDouble("returnamount"));}catch(NullPointerException e){}
			try{sup.setIsActive(rs.getInt("retisactive"));}catch(NullPointerException e){}
			try{sup.setStatus(rs.getInt("retstatus"));}catch(NullPointerException e){}
			try{sup.setRemarks(rs.getString("returnremarks"));}catch(NullPointerException e){}
			try{sup.setTimestamp(rs.getTimestamp("rettimestamp"));}catch(NullPointerException e){}
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			sup.setProduct(prod);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			sup.setProperties(prop);
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			sup.setUom(uom);
			
			Supplier sp = new Supplier();
			try{sp.setSupid(rs.getLong("supid"));}catch(NullPointerException e){}
			try{sp.setSuppliername(rs.getString("suppliername"));}catch(NullPointerException e){}
			try{sp.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{sp.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{sp.setOwnername(rs.getString("ownername"));}catch(NullPointerException e){}
			try{sp.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{sp.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			sup.setSupplier(sp);
			
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
			sup.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return sup;
	}
	
	public static ProductReturnSupplier save(ProductReturnSupplier ret){
		if(ret!=null){
			
			long id = ProductReturnSupplier.getInfo(ret.getId() ==0? ProductReturnSupplier.getLatestId()+1 : ret.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ret = ProductReturnSupplier.insertData(ret, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ret = ProductReturnSupplier.updateData(ret);
			}else if(id==3){
				LogU.add("added new Data ");
				ret = ProductReturnSupplier.insertData(ret, "3");
			}
			
		}
		return ret;
	}
	
	public void save(){
			
			long id = getInfo(getId() ==0? ProductReturnSupplier.getLatestId()+1 : getId());
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
	
	public static ProductReturnSupplier insertData(ProductReturnSupplier ret, String type){
		String sql = "INSERT INTO productreturnsupplier ("
				+ "prodretid,"
				+ "datereturned,"
				+ "returnqty,"
				+ "returnamount,"
				+ "returnremarks,"
				+ "retisactive,"
				+ "retstatus,"
				+ "prodid,"
				+ "propid,"
				+ "uomid,"
				+ "userdtlsid,"
				+ "supid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productreturnsupplier");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			ret.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			ret.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, ret.getDateTrans());
		ps.setDouble(cnt++, ret.getQuantity());
		ps.setDouble(cnt++, ret.getAmount());
		ps.setString(cnt++, ret.getRemarks());
		ps.setInt(cnt++, ret.getIsActive());
		ps.setInt(cnt++, ret.getStatus());
		ps.setLong(cnt++, ret.getProduct()==null? 0 : ret.getProduct().getProdid());
		ps.setLong(cnt++, ret.getProperties()==null? 0 : ret.getProperties().getPropid());
		ps.setInt(cnt++, ret.getUom()==null? 0 : ret.getUom().getUomid());
		ps.setLong(cnt++, ret.getUserDtls()==null? 0 : (ret.getUserDtls().getUserdtlsid()==null? 0 : ret.getUserDtls().getUserdtlsid()));
		ps.setLong(cnt++, ret.getSupplier()==null? 0 : ret.getSupplier().getSupid());
		
		LogU.add(ret.getDateTrans());
		LogU.add(ret.getQuantity());
		LogU.add(ret.getAmount());
		LogU.add(ret.getRemarks());
		LogU.add(ret.getIsActive());
		LogU.add(ret.getStatus());
		LogU.add(ret.getProduct()==null? 0 : ret.getProduct().getProdid());
		LogU.add(ret.getProperties()==null? 0 : ret.getProperties().getPropid());
		LogU.add(ret.getUom()==null? 0 : ret.getUom().getUomid());
		LogU.add(ret.getUserDtls()==null? 0 : (ret.getUserDtls().getUserdtlsid()==null? 0 : ret.getUserDtls().getUserdtlsid()));
		LogU.add(ret.getSupplier()==null? 0 : ret.getSupplier().getSupid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productreturnsupplier : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ret;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO productreturnsupplier ("
				+ "prodretid,"
				+ "datereturned,"
				+ "returnqty,"
				+ "returnamount,"
				+ "returnremarks,"
				+ "retisactive,"
				+ "retstatus,"
				+ "prodid,"
				+ "propid,"
				+ "uomid,"
				+ "userdtlsid,"
				+ "supid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productreturnsupplier");
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
		ps.setDouble(cnt++, getAmount());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getStatus());
		ps.setLong(cnt++, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(cnt++, getProperties()==null? 0 : getProperties().getPropid());
		ps.setInt(cnt++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(cnt++, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(cnt++, getSupplier()==null? 0 : getSupplier().getSupid());
		
		LogU.add(getDateTrans());
		LogU.add(getQuantity());
		LogU.add(getAmount());
		LogU.add(getRemarks());
		LogU.add(getIsActive());
		LogU.add(getStatus());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getProperties()==null? 0 : getProperties().getPropid());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getSupplier()==null? 0 : getSupplier().getSupid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productreturnsupplier : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static ProductReturnSupplier updateData(ProductReturnSupplier ret){
		String sql = "UPDATE productreturnsupplier SET "
				+ "datereturned=?,"
				+ "returnqty=?,"
				+ "returnamount=?,"
				+ "returnremarks=?,"
				+ "retstatus=?,"
				+ "prodid=?,"
				+ "propid=?,"
				+ "uomid=?,"
				+ "userdtlsid=?,"
				+ "supid=? " 
				+ " WHERE prodretid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productreturnsupplier");
		
		ps.setString(cnt++, ret.getDateTrans());
		ps.setDouble(cnt++, ret.getQuantity());
		ps.setDouble(cnt++, ret.getAmount());
		ps.setString(cnt++, ret.getRemarks());
		ps.setInt(cnt++, ret.getStatus());
		ps.setLong(cnt++, ret.getProduct()==null? 0 : ret.getProduct().getProdid());
		ps.setLong(cnt++, ret.getProperties()==null? 0 : ret.getProperties().getPropid());
		ps.setInt(cnt++, ret.getUom()==null? 0 : ret.getUom().getUomid());
		ps.setLong(cnt++, ret.getUserDtls()==null? 0 : (ret.getUserDtls().getUserdtlsid()==null? 0 : ret.getUserDtls().getUserdtlsid()));
		ps.setLong(cnt++, ret.getSupplier()==null? 0 : ret.getSupplier().getSupid());
		ps.setLong(cnt++, ret.getId());
		
		LogU.add(ret.getDateTrans());
		LogU.add(ret.getQuantity());
		LogU.add(ret.getAmount());
		LogU.add(ret.getRemarks());
		LogU.add(ret.getStatus());
		LogU.add(ret.getProduct()==null? 0 : ret.getProduct().getProdid());
		LogU.add(ret.getProperties()==null? 0 : ret.getProperties().getPropid());
		LogU.add(ret.getUom()==null? 0 : ret.getUom().getUomid());
		LogU.add(ret.getUserDtls()==null? 0 : (ret.getUserDtls().getUserdtlsid()==null? 0 : ret.getUserDtls().getUserdtlsid()));
		LogU.add(ret.getSupplier()==null? 0 : ret.getSupplier().getSupid());
		LogU.add(ret.getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productreturnsupplier : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ret;
	}
	
	public void updateData(){
		String sql = "UPDATE productreturnsupplier SET "
				+ "datereturned=?,"
				+ "returnqty=?,"
				+ "returnamount=?,"
				+ "returnremarks=?,"
				+ "retstatus=?,"
				+ "prodid=?,"
				+ "propid=?,"
				+ "uomid=?,"
				+ "userdtlsid=?,"
				+ "supid=? " 
				+ " WHERE prodretid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table productreturnsupplier");
		
		ps.setString(cnt++, getDateTrans());
		ps.setDouble(cnt++, getQuantity());
		ps.setDouble(cnt++, getAmount());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getStatus());
		ps.setLong(cnt++, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(cnt++, getProperties()==null? 0 : getProperties().getPropid());
		ps.setInt(cnt++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(cnt++, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(cnt++, getSupplier()==null? 0 : getSupplier().getSupid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getQuantity());
		LogU.add(getAmount());
		LogU.add(getRemarks());
		LogU.add(getStatus());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getProperties()==null? 0 : getProperties().getPropid());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getSupplier()==null? 0 : getSupplier().getSupid());
		LogU.add(getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to productreturnsupplier : " + s.getMessage());
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
		sql="SELECT prodretid FROM productreturnsupplier  ORDER BY prodretid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("prodretid");
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
		ps = conn.prepareStatement("SELECT prodretid FROM productreturnsupplier WHERE prodretid=?");
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
		String sql = "UPDATE productreturnsupplier set retisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE prodretid=?";
		
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public ProductProperties getProperties() {
		return properties;
	}

	public void setProperties(ProductProperties properties) {
		this.properties = properties;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public boolean isBetween() {
		return between;
	}

	public void setBetween(boolean between) {
		this.between = between;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public static void main(String[] args) {
		ProductReturnSupplier ret = new ProductReturnSupplier();
		ret.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		ret.setQuantity(100);
		ret.setAmount(200);
		ret.setIsActive(1);
		ret.setStatus(1);
		ret.setRemarks("ako");
		
		Product prod = new Product();
		prod.setProdid(1);
		ret.setProduct(prod);
		
		ProductProperties prop = new ProductProperties();
		prop.setPropid(1);
		ret.setProperties(prop);
		
		UOM uom = new UOM();
		uom.setUomid(1);
		ret.setUom(uom);
		
		Supplier sup = new Supplier();
		sup.setSupid(1);
		ret.setSupplier(sup);
		
		UserDtls user = new UserDtls();
		user.setUserdtlsid(1l);
		ret.setUserDtls(user);
		
		ret.save();
	}
}
