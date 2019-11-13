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
 * @since 01/19/2017
 * @version 1.0
 *
 */
public class ReturnRentedItems {

	private long id;
	private String dateTrans;
	private double quantity;
	private int status;
	private int isActive;
	private int itemCondition;
	private double charges;
	private String remarks;
	private Timestamp timestamp;
	private Customer customer;
	private UOM uom;
	private ProductProperties productProperties;
	private UserDtls userDtls;
	
	private boolean between;
	private String dateFrom;
	private String dateTo;
	
	public ReturnRentedItems(){}
	
	public ReturnRentedItems(
			long id,
			String dateTrans,
			double quantity,
			int status,
			int isActive,
			Customer customer,
			UOM uom,
			ProductProperties productProperties,
			UserDtls userDtls
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.quantity = quantity;
		this.status = status;
		this.isActive = isActive;
		this.customer = customer;
		this.uom = uom;
		this.productProperties = productProperties;
		this.userDtls = userDtls;
	}
	
	public static String returnSQL(String tablename,ReturnRentedItems ret){
		String sql = " AND " + tablename + ".retIsActive=" + ret.getIsActive();
		if(ret!=null){
			if(ret.getId()!=0){
				sql += " AND "+ tablename +".retitmid=" + ret.getId();
			}
			if(ret.getItemCondition()!=0){
				sql += " AND "+ tablename +".retitemcondition=" + ret.getItemCondition();
			}
			if(ret.getStatus()!=0){
				sql += " AND "+ tablename +".retStatus=" + ret.getStatus();
			}
			if(ret.isBetween()){
				
				sql += " ( AND "+ tablename +".retDateTrans>='" + ret.getDateFrom() +"' AND " + tablename +".retDateTrans<='" + ret.getDateTo() + "')";
								
			}else{
				if(ret.getDateTrans()!=null){
					sql += " AND "+ tablename +".retDateTrans='" + ret.getDateTrans() +"'";
				}
			}
		}	
			
		return sql;
	}
	
	public static List<ReturnRentedItems> retrieve(Object...obj){
		List<ReturnRentedItems> items = Collections.synchronizedList(new ArrayList<ReturnRentedItems>());
		
		String retTable = "ret";
		String cusTable = "cus";
		String uomTable = "uom";
		String propTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM returnrenteditems " + retTable + 
				", customer " + cusTable +
				", uom " + uomTable + 
				", productproperties " + propTable + 
				", userdtls " + userTable +
				" WHERE " + retTable + ".uomid=" + uomTable + ".uomid " + 
				" AND " + retTable + ".propid=" + propTable+ ".propid " + 
				" AND " + retTable + ".customerid=" + cusTable + ".customerid " + 
				" AND " + retTable + ".userdtlsid=" + userTable + ".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof ReturnRentedItems){
				sql += returnSQL(retTable,(ReturnRentedItems)obj[i]);
			}
			
			if(obj[i] instanceof UOM){
				sql += UOM.uomSQL(uomTable,(UOM)obj[i]);
			}
			
			if(obj[i] instanceof ProductProperties){
				sql += ProductProperties.productSQL(propTable,(ProductProperties)obj[i]);
			}
			
			if(obj[i] instanceof Customer){
				sql += Customer.customerSQL(cusTable,(Customer)obj[i]);
			}
			
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
		}
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("Return Items SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			ReturnRentedItems item = new ReturnRentedItems();
			try{item.setId(rs.getLong("retitmid"));}catch(NullPointerException e){}
			try{item.setDateTrans(rs.getString("retDateTrans"));}catch(NullPointerException e){}
			try{item.setQuantity(rs.getDouble("retQuantity"));}catch(NullPointerException e){}
			try{item.setStatus(rs.getInt("retStatus"));}catch(NullPointerException e){}
			try{item.setIsActive(rs.getInt("retIsActive"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("retimestamp"));}catch(NullPointerException e){}
			try{item.setItemCondition(rs.getInt("retitemcondition"));}catch(NullPointerException e){}
			try{item.setCharges(rs.getDouble("retcharges"));}catch(NullPointerException e){}
			try{item.setRemarks(rs.getString("retRemarks"));}catch(NullPointerException e){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			item.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setProductProperties(prop);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setCustomer(cus);
			
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
			item.setUserDtls(user);
			
			items.add(item);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
		
	}	
	
	public static List<ReturnRentedItems> retrieve(String sql, String[] params){
		List<ReturnRentedItems> items = Collections.synchronizedList(new ArrayList<ReturnRentedItems>());
		
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
			
			ReturnRentedItems item = new ReturnRentedItems();
			try{item.setId(rs.getLong("retitmid"));}catch(NullPointerException e){}
			try{item.setDateTrans(rs.getString("retDateTrans"));}catch(NullPointerException e){}
			try{item.setQuantity(rs.getDouble("retQuantity"));}catch(NullPointerException e){}
			try{item.setStatus(rs.getInt("retStatus"));}catch(NullPointerException e){}
			try{item.setIsActive(rs.getInt("retIsActive"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("retimestamp"));}catch(NullPointerException e){}
			try{item.setItemCondition(rs.getInt("retitemcondition"));}catch(NullPointerException e){}
			try{item.setCharges(rs.getDouble("retcharges"));}catch(NullPointerException e){}
			try{item.setRemarks(rs.getString("retRemarks"));}catch(NullPointerException e){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			item.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			item.setProductProperties(prop);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			item.setCustomer(cus);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			item.setUserDtls(user);
			
			items.add(item);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
		
	}	
	
	public static ReturnRentedItems retrieve(String returnId){
		ReturnRentedItems item = new ReturnRentedItems();
		
		String retTable = "ret";
		String cusTable = "cus";
		String uomTable = "uom";
		String propTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM returnrenteditems " + retTable + 
				", customer " + cusTable +
				", uom " + uomTable + 
				", productproperties " + propTable + 
				", userdtls " + userTable +
				" WHERE " + retTable + ".uomid=" + uomTable + ".uomid " + 
				" AND " + retTable + ".propid=" + propTable+ ".propid " + 
				" AND " + retTable + ".customerid=" + cusTable + ".customerid " + 
				" AND " + retTable + ".userdtlsid=" + userTable + ".userdtlsid AND " + retTable + ".retitmid=" + returnId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("Return Items SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{item.setId(rs.getLong("retitmid"));}catch(NullPointerException e){}
			try{item.setDateTrans(rs.getString("retDateTrans"));}catch(NullPointerException e){}
			try{item.setQuantity(rs.getDouble("retQuantity"));}catch(NullPointerException e){}
			try{item.setStatus(rs.getInt("retStatus"));}catch(NullPointerException e){}
			try{item.setIsActive(rs.getInt("retIsActive"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("retimestamp"));}catch(NullPointerException e){}
			try{item.setItemCondition(rs.getInt("retitemcondition"));}catch(NullPointerException e){}
			try{item.setCharges(rs.getDouble("retcharges"));}catch(NullPointerException e){}
			try{item.setRemarks(rs.getString("retRemarks"));}catch(NullPointerException e){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			item.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setProductProperties(prop);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setCustomer(cus);
			
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
			item.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return item;
		
	}	
	
	public static ReturnRentedItems save(ReturnRentedItems ret){
		if(ret!=null){
			
			long id = ReturnRentedItems.getInfo(ret.getId() ==0? ReturnRentedItems.getLatestId()+1 : ret.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ret = ReturnRentedItems.insertData(ret, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ret = ReturnRentedItems.updateData(ret);
			}else if(id==3){
				LogU.add("added new Data ");
				ret = ReturnRentedItems.insertData(ret, "3");
			}
			
		}
		return ret;
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
	
	public static ReturnRentedItems insertData(ReturnRentedItems ret, String type){
		String sql = "INSERT INTO returnrenteditems ("
				+ "retitmid,"
				+ "retQuantity,"
				+ "retDateTrans,"
				+ "retStatus,"
				+ "retIsActive,"
				+ "customerid,"
				+ "uomid,"
				+ "userdtlsid,"
				+ "propid,"
				+ "retitemcondition,"
				+ "retcharges,"
				+ "retRemarks)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table returnrenteditems");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(i++, id);
			ret.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(i++, id);
			ret.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setDouble(i++, ret.getQuantity());
		ps.setString(i++, ret.getDateTrans());
		ps.setInt(i++, ret.getStatus());
		ps.setInt(i++, ret.getIsActive());
		ps.setLong(i++, ret.getCustomer()==null? 0 : ret.getCustomer().getCustomerid());
		ps.setInt(i++, ret.getUom()==null? 0 : ret.getUom().getUomid());
		ps.setLong(i++, ret.getUserDtls()==null? 0l : (ret.getUserDtls().getUserdtlsid()==null? 0l : ret.getUserDtls().getUserdtlsid()));
		ps.setLong(i++, ret.getProductProperties()==null? 0 : ret.getProductProperties().getPropid());
		ps.setInt(i++, ret.getItemCondition());
		ps.setDouble(i++, ret.getCharges());
		ps.setString(i++, ret.getRemarks());
		
		LogU.add(ret.getQuantity());
		LogU.add(ret.getDateTrans());
		LogU.add(ret.getStatus());
		LogU.add(ret.getIsActive());
		LogU.add(ret.getCustomer()==null? 0 : ret.getCustomer().getCustomerid());
		LogU.add(ret.getUom()==null? 0 : ret.getUom().getUomid());
		LogU.add(ret.getUserDtls()==null? 0l : (ret.getUserDtls().getUserdtlsid()==null? 0l : ret.getUserDtls().getUserdtlsid()));
		LogU.add(ret.getProductProperties()==null? 0 : ret.getProductProperties().getPropid());
		LogU.add(ret.getItemCondition());
		LogU.add(ret.getCharges());
		LogU.add(ret.getRemarks());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to returnrenteditems : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ret;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO returnrenteditems ("
				+ "retitmid,"
				+ "retQuantity,"
				+ "retDateTrans,"
				+ "retStatus,"
				+ "retIsActive,"
				+ "customerid,"
				+ "uomid,"
				+ "userdtlsid,"
				+ "propid,"
				+ "retitemcondition,"
				+ "retcharges,"
				+ "retRemarks)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			long id =1;
			int i=1;
			LogU.add("===========================START=========================");
			LogU.add("inserting data into table returnrenteditems");
			if("1".equalsIgnoreCase(type)){
				ps.setLong(i++, id);
				setId(id);
				LogU.add("id: 1");
			}else if("3".equalsIgnoreCase(type)){
				id=getLatestId()+1;
				ps.setLong(i++, id);
				setId(id);
				LogU.add("id: " + id);
			}
			
			ps.setDouble(i++, getQuantity());
			ps.setString(i++, getDateTrans());
			ps.setInt(i++, getStatus());
			ps.setInt(i++, getIsActive());
			ps.setLong(i++, getCustomer()==null? 0 : getCustomer().getCustomerid());
			ps.setInt(i++, getUom()==null? 0 : getUom().getUomid());
			ps.setLong(i++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
			ps.setLong(i++, getProductProperties()==null? 0 : getProductProperties().getPropid());
			ps.setInt(i++, getItemCondition());
			ps.setDouble(i++, getCharges());
			ps.setString(i++, getRemarks());
			
			LogU.add(getQuantity());
			LogU.add(getDateTrans());
			LogU.add(getStatus());
			LogU.add(getIsActive());
			LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
			LogU.add(getUom()==null? 0 : getUom().getUomid());
			LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
			LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
			LogU.add(getItemCondition());
			LogU.add(getCharges());
			LogU.add(getRemarks());
			
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to returnrenteditems : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static ReturnRentedItems updateData(ReturnRentedItems ret){
		String sql = "UPDATE returnrenteditems SET "
				+ "retQuantity=?,"
				+ "retDateTrans=?,"
				+ "retStatus=?,"
				+ "customerid=?,"
				+ "uomid=?,"
				+ "userdtlsid=?,"
				+ "propid=?,"
				+ "retitemcondition=?,"
				+ "retcharges=?,"
				+ "retRemarks=? " 
				+ " WHERE retitmid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table returnrenteditems");
		
		ps.setDouble(i++, ret.getQuantity());
		ps.setString(i++, ret.getDateTrans());
		ps.setInt(i++, ret.getStatus());
		ps.setLong(i++, ret.getCustomer()==null? 0 : ret.getCustomer().getCustomerid());
		ps.setInt(i++, ret.getUom()==null? 0 : ret.getUom().getUomid());
		ps.setLong(i++, ret.getUserDtls()==null? 0l : (ret.getUserDtls().getUserdtlsid()==null? 0l : ret.getUserDtls().getUserdtlsid()));
		ps.setLong(i++, ret.getProductProperties()==null? 0 : ret.getProductProperties().getPropid());
		ps.setInt(i++, ret.getItemCondition());
		ps.setDouble(i++, ret.getCharges());
		ps.setString(i++, ret.getRemarks());
		ps.setLong(i++, ret.getId());
		
		LogU.add(ret.getQuantity());
		LogU.add(ret.getDateTrans());
		LogU.add(ret.getStatus());
		LogU.add(ret.getCustomer()==null? 0 : ret.getCustomer().getCustomerid());
		LogU.add(ret.getUom()==null? 0 : ret.getUom().getUomid());
		LogU.add(ret.getUserDtls()==null? 0l : (ret.getUserDtls().getUserdtlsid()==null? 0l : ret.getUserDtls().getUserdtlsid()));
		LogU.add(ret.getProductProperties()==null? 0 : ret.getProductProperties().getPropid());
		LogU.add(ret.getItemCondition());
		LogU.add(ret.getCharges());
		LogU.add(ret.getRemarks());
		LogU.add(ret.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to returnrenteditems : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return ret;
	}
	
	public void updateData(){
		String sql = "UPDATE returnrenteditems SET "
				+ "retQuantity=?,"
				+ "retDateTrans=?,"
				+ "retStatus=?,"
				+ "customerid=?,"
				+ "uomid=?,"
				+ "userdtlsid=?,"
				+ "propid=?,"
				+ "retitemcondition=?,"
				+ "retcharges=?,"
				+ "retRemarks=? " 
				+ " WHERE retitmid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table returnrenteditems");
		
		ps.setDouble(i++, getQuantity());
		ps.setString(i++, getDateTrans());
		ps.setInt(i++, getStatus());
		ps.setLong(i++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setInt(i++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(i++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setLong(i++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setInt(i++, getItemCondition());
		ps.setDouble(i++, getCharges());
		ps.setString(i++, getRemarks());
		ps.setLong(i++, getId());
		
		LogU.add(getQuantity());
		LogU.add(getDateTrans());
		LogU.add(getStatus());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getItemCondition());
		LogU.add(getCharges());
		LogU.add(getRemarks());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to returnrenteditems : " + s.getMessage());
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
		sql="SELECT retitmid FROM returnrenteditems  ORDER BY retitmid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("retitmid");
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
		ps = conn.prepareStatement("SELECT retitmid FROM returnrenteditems WHERE retitmid=?");
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
		String sql = "UPDATE returnrenteditems set retIsActive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE retitmid=?";
		
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
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public UOM getUom() {
		return uom;
	}
	public void setUom(UOM uom) {
		this.uom = uom;
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

	public int getItemCondition() {
		return itemCondition;
	}

	public void setItemCondition(int itemCondition) {
		this.itemCondition = itemCondition;
	}

	public double getCharges() {
		return charges;
	}

	public void setCharges(double charges) {
		this.charges = charges;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
