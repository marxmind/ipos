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
 * @since 01/17/2017
 * @version 1.0
 *
 */
public class RentedBottleTrans {

	private long id;
	private String dateTrans;
	private double quantity;
	private double totalAmount;
	private double paidAmount;
	private double balance;
	private double chargeitem;
	private String remarks;
	private UOM uom;
	private Customer customer;
	private ProductProperties productProperties;
	private UserDtls userDtls;
	private RentedBottle rentedBottle;
	private Timestamp timestamp;
	private int isActive;
	private int status;
	private double deposit;
	
	private boolean between;
	private String dateFrom;
	private String dateTo;
	
	public RentedBottleTrans(){}
	
	public RentedBottleTrans(
			long id,
			String dateTrans,
			double quantity,
			double totalAmount,
			double paidAmount,
			double balance,
			double chargeitem,
			String remarks,
			int isActive,
			int status,
			UOM uom,
			Customer customer,
			ProductProperties productProperties,
			UserDtls userDtls,
			RentedBottle rentedBottle,
			double deposit
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.quantity = quantity;
		this.totalAmount = totalAmount;
		this.paidAmount = paidAmount;
		this.balance = balance;
		this.chargeitem = chargeitem;
		this.remarks = remarks;
		this.isActive = isActive;
		this.status = status;
		this.uom = uom;
		this.customer = customer;
		this.productProperties = productProperties;
		this.userDtls = userDtls;
		this.rentedBottle = rentedBottle;
		this.deposit = deposit;
	}
	
	public static String rentSQL(String tablename,RentedBottleTrans rent){
		String sql= " AND  " + tablename + ".bottranisActive=" + rent.getIsActive();
		if(rent!=null){
			if(rent.getId()!=0){
				sql += " AND "+ tablename +".bottranid=" + rent.getId();
			}
			if(rent.isBetween()){
				
				sql += " AND ("+ tablename +".bottranDateTrans>='" + rent.getDateFrom() +"' AND " + tablename +".bottranDateTrans<='" + rent.getDateTo() + "')";
								
			}else{
				if(rent.getDateTrans()!=null){
					sql += " AND "+ tablename +".bottranDateTrans='" + rent.getDateTrans() +"'";
				}
			}
			
			if(rent.getStatus()!=0){
				sql += " AND " + tablename + ".botstatus=" + rent.getStatus();
			}
		}	
			
		return sql;
	}
	
	public static List<RentedBottleTrans> retrieve(Object...obj){
		List<RentedBottleTrans> trans = Collections.synchronizedList(new ArrayList<RentedBottleTrans>());
		
		String tranTable = "tran";
		String rentTable = "rent";
		String cusTable = "cus";
		String uomTable = "uom";
		String propTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM rentedbottletrans " + tranTable +
				",rentedbottle " + rentTable + 
				", customer " + cusTable +
				", uom " + uomTable + 
				", productproperties " + propTable + 
				", userdtls " + userTable +
				" WHERE " +  tranTable + ".botid=" + rentTable + ".botid " + 
				" AND " + tranTable + ".uomid=" + uomTable + ".uomid " + 
				" AND " + tranTable + ".propid=" + propTable + ".propid " + 
				" AND " + tranTable + ".customerid=" + cusTable + ".customerid " + 
				" AND " + tranTable + ".userdtlsid=" + userTable + ".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof RentedBottleTrans){
				sql += rentSQL(tranTable,(RentedBottleTrans)obj[i]);
			}
			
			if(obj[i] instanceof RentedBottle){
				sql += RentedBottle.rentSQL(rentTable,(RentedBottle)obj[i]);
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
		
		System.out.println("Rented trans SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			RentedBottleTrans tran = new RentedBottleTrans();
			try{tran.setId(rs.getLong("bottranid"));}catch(NullPointerException n){}
			try{tran.setDateTrans(rs.getString("bottranDateTrans"));}catch(NullPointerException n){}
			try{tran.setQuantity(rs.getDouble("bottranquantity"));}catch(NullPointerException n){}
			try{tran.setTotalAmount(rs.getDouble("bottranTotalAmnt"));}catch(NullPointerException n){}
			try{tran.setPaidAmount(rs.getDouble("bottranPaidAmnt"));}catch(NullPointerException n){}
			try{tran.setChargeitem(rs.getDouble("botchargeitem"));}catch(NullPointerException n){}
			try{tran.setBalance(rs.getDouble("bottranBalance"));}catch(NullPointerException n){}
			try{tran.setRemarks(rs.getString("botremarks"));}catch(NullPointerException n){}
			try{tran.setIsActive(rs.getInt("bottranisActive"));}catch(NullPointerException n){}
			try{tran.setStatus(rs.getInt("botstatus"));}catch(NullPointerException n){}
			try{tran.setTimestamp(rs.getTimestamp("rentranstimestamp"));}catch(NullPointerException n){}
			try{tran.setDeposit(rs.getDouble("bottDeposit"));}catch(NullPointerException n){}
			
			RentedBottle rent = new RentedBottle();
			try{rent.setId(rs.getLong("botid"));}catch(NullPointerException n){}
			try{rent.setCurrentDateTrans(rs.getString("currentDateTrans"));}catch(NullPointerException n){}
			try{rent.setCurrentQty(rs.getDouble("currentQuantity"));}catch(NullPointerException n){}
			try{rent.setCurrentPaidAmount(rs.getDouble("currentPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setPrevDateTrans(rs.getString("prevDateTrans"));}catch(NullPointerException n){}
			try{rent.setPrevQty(rs.getDouble("prevQuantity"));}catch(NullPointerException n){}
			try{rent.setPrevPaidAmount(rs.getDouble("prevPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setTimestamp(rs.getTimestamp("rentimestamp"));}catch(NullPointerException n){}
			tran.setRentedBottle(rent);
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			tran.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			tran.setProductProperties(prop);
			
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
			tran.setCustomer(cus);
			
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
			
			trans.add(tran);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
		
	}	
	
	public static RentedBottleTrans retrieve(String rentId){
		RentedBottleTrans tran = new RentedBottleTrans();
		String tranTable = "tran";
		String rentTable = "rent";
		String cusTable = "cus";
		String uomTable = "uom";
		String propTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM rentedbottletrans " + tranTable +
				",rentedbottle " + rentTable + 
				", customer " + cusTable +
				", uom " + uomTable + 
				", productproperties " + propTable +
				", userdtls " + userTable +
				" WHERE " +  tranTable + ".botid=" + rentTable + ".botid " + 
				" AND " + tranTable + ".uomid=" + uomTable + ".uomid " + 
				" AND " + tranTable + ".propid=" + propTable+ ".propid " + 
				" AND " + tranTable + ".customerid=" + cusTable + ".customerid " + 
				" AND " + tranTable + ".userdtlsid=" + userTable + ".userdtlsid AND " + tranTable + ".bottranid=" + rentId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{tran.setId(rs.getLong("bottranid"));}catch(NullPointerException n){}
			try{tran.setDateTrans(rs.getString("bottranDateTrans"));}catch(NullPointerException n){}
			try{tran.setQuantity(rs.getDouble("bottranquantity"));}catch(NullPointerException n){}
			try{tran.setTotalAmount(rs.getDouble("bottranTotalAmnt"));}catch(NullPointerException n){}
			try{tran.setPaidAmount(rs.getDouble("bottranPaidAmnt"));}catch(NullPointerException n){}
			try{tran.setChargeitem(rs.getDouble("botchargeitem"));}catch(NullPointerException n){}
			try{tran.setBalance(rs.getDouble("bottranBalance"));}catch(NullPointerException n){}
			try{tran.setRemarks(rs.getString("botremarks"));}catch(NullPointerException n){}
			try{tran.setIsActive(rs.getInt("bottranisActive"));}catch(NullPointerException n){}
			try{tran.setStatus(rs.getInt("botstatus"));}catch(NullPointerException n){}
			try{tran.setTimestamp(rs.getTimestamp("rentranstimestamp"));}catch(NullPointerException n){}
			try{tran.setDeposit(rs.getDouble("bottDeposit"));}catch(NullPointerException n){}
			
			RentedBottle rent = new RentedBottle();
			try{rent.setId(rs.getLong("botid"));}catch(NullPointerException n){}
			try{rent.setCurrentDateTrans(rs.getString("currentDateTrans"));}catch(NullPointerException n){}
			try{rent.setCurrentQty(rs.getDouble("currentQuantity"));}catch(NullPointerException n){}
			try{rent.setCurrentPaidAmount(rs.getDouble("currentPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setPrevDateTrans(rs.getString("prevDateTrans"));}catch(NullPointerException n){}
			try{rent.setPrevQty(rs.getDouble("prevQuantity"));}catch(NullPointerException n){}
			try{rent.setPrevPaidAmount(rs.getDouble("prevPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setTimestamp(rs.getTimestamp("rentimestamp"));}catch(NullPointerException n){}
			tran.setRentedBottle(rent);
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			tran.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			tran.setProductProperties(prop);
			
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
			tran.setCustomer(cus);
			
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
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return tran;
		
	}	
	
	public static List<RentedBottleTrans> retrieve(String sql, String[] params){
		List<RentedBottleTrans> trans = Collections.synchronizedList(new ArrayList<RentedBottleTrans>());
		
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
			
			RentedBottleTrans tran = new RentedBottleTrans();
			try{tran.setId(rs.getLong("bottranid"));}catch(NullPointerException n){}
			try{tran.setDateTrans(rs.getString("bottranDateTrans"));}catch(NullPointerException n){}
			try{tran.setQuantity(rs.getDouble("bottranquantity"));}catch(NullPointerException n){}
			try{tran.setTotalAmount(rs.getDouble("bottranTotalAmnt"));}catch(NullPointerException n){}
			try{tran.setPaidAmount(rs.getDouble("bottranPaidAmnt"));}catch(NullPointerException n){}
			try{tran.setChargeitem(rs.getDouble("botchargeitem"));}catch(NullPointerException n){}
			try{tran.setBalance(rs.getDouble("bottranBalance"));}catch(NullPointerException n){}
			try{tran.setRemarks(rs.getString("botremarks"));}catch(NullPointerException n){}
			try{tran.setIsActive(rs.getInt("bottranisActive"));}catch(NullPointerException n){}
			try{tran.setStatus(rs.getInt("botstatus"));}catch(NullPointerException n){}
			try{tran.setTimestamp(rs.getTimestamp("rentranstimestamp"));}catch(NullPointerException n){}
			try{tran.setDeposit(rs.getDouble("bottDeposit"));}catch(NullPointerException n){}
			
			RentedBottle rent = new RentedBottle();
			try{rent.setId(rs.getLong("botid"));}catch(NullPointerException n){}
			tran.setRentedBottle(rent);
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException n){}
			tran.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			rent.setProductProperties(prop);
			
			Customer customer = new Customer();
			try{customer.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException n){}
			tran.setCustomer(customer);
			
			UserDtls userDtls = new UserDtls();
			try{userDtls.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException n){}
			tran.setUserDtls(userDtls);
			trans.add(tran);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
		
	}
	
	public static void save(RentedBottleTrans rent){
		if(rent!=null){
			
			long id = RentedBottleTrans.getInfo(rent.getId() ==0? RentedBottleTrans.getLatestId()+1 : rent.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				RentedBottleTrans.insertData(rent, "1");
			}else if(id==2){
				LogU.add("update Data ");
				RentedBottleTrans.updateData(rent);
			}else if(id==3){
				LogU.add("added new Data ");
				RentedBottleTrans.insertData(rent, "3");
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
	
	public static RentedBottleTrans insertData(RentedBottleTrans rent, String type){
		String sql = "INSERT INTO rentedbottletrans ("
				+ "bottranid,"
				+ "bottranquantity,"
				+ "bottranDateTrans,"
				+ "bottranTotalAmnt,"
				+ "bottranPaidAmnt,"
				+ "bottranBalance,"
				+ "botchargeitem,"
				+ "botremarks,"
				+ "bottranisActive,"
				+ "botstatus,"
				+ "botid,"
				+ "uomid,"
				+ "customerid,"
				+ "propid,"
				+ "userdtlsid,"
				+ "bottDeposit)" 
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		long id =1;
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table rentedbottletrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(i++, id);
			rent.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(i++, id);
			rent.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setDouble(i++, rent.getQuantity());
		ps.setString(i++, rent.getDateTrans());
		ps.setDouble(i++, rent.getTotalAmount());
		ps.setDouble(i++, rent.getPaidAmount());
		ps.setDouble(i++, rent.getBalance());
		ps.setDouble(i++, rent.getChargeitem());
		ps.setString(i++, rent.getRemarks());
		ps.setInt(i++, rent.getIsActive());
		ps.setInt(i++, rent.getStatus());
		ps.setLong(i++, rent.getRentedBottle()==null? 0 : rent.getRentedBottle().getId());
		ps.setInt(i++, rent.getUom()==null? 0 : rent.getUom().getUomid());
		ps.setLong(i++, rent.getCustomer()==null? 0 : rent.getCustomer().getCustomerid());
		ps.setLong(i++, rent.getProductProperties()==null? 0 : rent.getProductProperties().getPropid());
		ps.setLong(i++, rent.getUserDtls()==null? 0l : (rent.getUserDtls().getUserdtlsid()==null? 0l : rent.getUserDtls().getUserdtlsid()));
		ps.setDouble(i++, rent.getDeposit());
		System.out.println("Saving... " + ps.toString());
		LogU.add(rent.getQuantity());
		LogU.add(rent.getDateTrans());
		LogU.add(rent.getTotalAmount());
		LogU.add(rent.getPaidAmount());
		LogU.add(rent.getBalance());
		LogU.add(rent.getChargeitem());
		LogU.add(rent.getRemarks());
		LogU.add(rent.getIsActive());
		LogU.add(rent.getStatus());
		LogU.add(rent.getRentedBottle()==null? 0 : rent.getRentedBottle().getId());
		LogU.add(rent.getUom()==null? 0 : rent.getUom().getUomid());
		LogU.add(rent.getCustomer()==null? 0 : rent.getCustomer().getCustomerid());
		LogU.add(rent.getProductProperties()==null? 0 : rent.getProductProperties().getPropid());
		LogU.add(rent.getUserDtls()==null? 0l : (rent.getUserDtls().getUserdtlsid()==null? 0l : rent.getUserDtls().getUserdtlsid()));
		LogU.add(rent.getDeposit());
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to rentedbottletrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rent;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO rentedbottletrans ("
				+ "bottranid,"
				+ "bottranquantity,"
				+ "bottranDateTrans,"
				+ "bottranTotalAmnt,"
				+ "bottranPaidAmnt,"
				+ "bottranBalance,"
				+ "botchargeitem,"
				+ "botremarks,"
				+ "bottranisActive,"
				+ "botstatus,"
				+ "botid,"
				+ "uomid,"
				+ "customerid,"
				+ "propid,"
				+ "userdtlsid,"
				+ "bottDeposit)" 
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		long id =1;
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table rentedbottletrans");
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
		ps.setDouble(i++, getTotalAmount());
		ps.setDouble(i++, getPaidAmount());
		ps.setDouble(i++, getBalance());
		ps.setDouble(i++, getChargeitem());
		ps.setString(i++, getRemarks());
		ps.setInt(i++, getIsActive());
		ps.setInt(i++,getStatus());
		ps.setLong(i++, getRentedBottle()==null? 0 : getRentedBottle().getId());
		ps.setInt(i++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(i++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(i++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(i++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setDouble(i++, getDeposit());
		System.out.println("Saving... " + ps.toString());
		LogU.add(getQuantity());
		LogU.add(getDateTrans());
		LogU.add(getTotalAmount());
		LogU.add(getPaidAmount());
		LogU.add(getBalance());
		LogU.add(getChargeitem());
		LogU.add(getRemarks());
		LogU.add(getIsActive());
		LogU.add(getStatus());
		LogU.add(getRentedBottle()==null? 0 : getRentedBottle().getId());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getDeposit());
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to rentedbottletrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static RentedBottleTrans updateData(RentedBottleTrans rent){
		String sql = "UPDATE rentedbottletrans SET "
				+ "bottranquantity=?,"
				+ "bottranDateTrans=?,"
				+ "bottranTotalAmnt=?,"
				+ "bottranPaidAmnt=?,"
				+ "bottranBalance=?,"
				+ "botchargeitem=?,"
				+ "botremarks=?,"
				+ "botstatus=?,"
				+ "uomid=?,"
				+ "customerid=?,"
				+ "propid=?,"
				+ "userdtlsid=?,"
				+ "bottDeposit=?" 
				+ " WHERE bottranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table rentedbottletrans");
		
		
		ps.setDouble(i++, rent.getQuantity());
		ps.setString(i++, rent.getDateTrans());
		ps.setDouble(i++, rent.getTotalAmount());
		ps.setDouble(i++, rent.getPaidAmount());
		ps.setDouble(i++, rent.getBalance());
		ps.setDouble(i++, rent.getChargeitem());
		ps.setString(i++, rent.getRemarks());
		ps.setInt(i++, rent.getStatus());
		ps.setInt(i++, rent.getUom()==null? 0 : rent.getUom().getUomid());
		ps.setLong(i++, rent.getCustomer()==null? 0 : rent.getCustomer().getCustomerid());
		ps.setLong(i++, rent.getProductProperties()==null? 0 : rent.getProductProperties().getPropid());
		ps.setLong(i++, rent.getUserDtls()==null? 0l : (rent.getUserDtls().getUserdtlsid()==null? 0l : rent.getUserDtls().getUserdtlsid()));
		ps.setDouble(i++, rent.getDeposit());
		ps.setLong(i++, rent.getId());
		
		LogU.add(rent.getQuantity());
		LogU.add(rent.getDateTrans());
		LogU.add(rent.getTotalAmount());
		LogU.add(rent.getPaidAmount());
		LogU.add(rent.getBalance());
		LogU.add(rent.getChargeitem());
		LogU.add(rent.getRemarks());
		LogU.add(rent.getStatus());
		LogU.add(rent.getUom()==null? 0 : rent.getUom().getUomid());
		LogU.add(rent.getCustomer()==null? 0 : rent.getCustomer().getCustomerid());
		LogU.add(rent.getProductProperties()==null? 0 : rent.getProductProperties().getPropid());
		LogU.add(rent.getUserDtls()==null? 0l : (rent.getUserDtls().getUserdtlsid()==null? 0l : rent.getUserDtls().getUserdtlsid()));
		LogU.add(rent.getDeposit());
		LogU.add(rent.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to rentedbottletrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rent;
	}
	
	public void updateData(){
		String sql = "UPDATE rentedbottletrans SET "
				+ "bottranquantity=?,"
				+ "bottranDateTrans=?,"
				+ "bottranTotalAmnt=?,"
				+ "bottranPaidAmnt=?,"
				+ "bottranBalance=?,"
				+ "botchargeitem=?,"
				+ "botremarks=?,"
				+ "botstatus=?,"
				+ "uomid=?,"
				+ "customerid=?,"
				+ "propid=?,"
				+ "userdtlsid=?,"
				+ "bottDeposit=?" 
				+ " WHERE bottranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table rentedbottletrans");
		
		
		ps.setDouble(i++, getQuantity());
		ps.setString(i++, getDateTrans());
		ps.setDouble(i++, getTotalAmount());
		ps.setDouble(i++, getPaidAmount());
		ps.setDouble(i++, getBalance());
		ps.setDouble(i++, getChargeitem());
		ps.setString(i++, getRemarks());
		ps.setInt(i++, getStatus());
		ps.setInt(i++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(i++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(i++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(i++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setDouble(i++, getDeposit());
		ps.setLong(i++, getId());
		
		LogU.add(getQuantity());
		LogU.add(getDateTrans());
		LogU.add(getTotalAmount());
		LogU.add(getPaidAmount());
		LogU.add(getBalance());
		LogU.add(getChargeitem());
		LogU.add(getRemarks());
		LogU.add(getStatus());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getDeposit());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to rentedbottletrans : " + s.getMessage());
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
		sql="SELECT bottranid FROM rentedbottletrans  ORDER BY bottranid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("bottranid");
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
		ps = conn.prepareStatement("SELECT bottranid FROM rentedbottletrans WHERE bottranid=?");
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
		String sql = "UPDATE rentedbottletrans set bottranisActive=0 WHERE bottranid=?";
		
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
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getChargeitem() {
		return chargeitem;
	}
	public void setChargeitem(double chargeitem) {
		this.chargeitem = chargeitem;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public UOM getUom() {
		return uom;
	}
	public void setUom(UOM uom) {
		this.uom = uom;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public RentedBottle getRentedBottle() {
		return rentedBottle;
	}
	public void setRentedBottle(RentedBottle rentedBottle) {
		this.rentedBottle = rentedBottle;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
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

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}
	
	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public static void main(String[] args) {
		RentedBottleTrans tran = new RentedBottleTrans();
		tran.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		UserDtls user = new UserDtls();
		Customer cus = new Customer();
		cus.setCustomerid(1);
		tran.setCustomer(cus);
		user.setUserdtlsid(1l);
		tran.setUserDtls(user);
		tran.setIsActive(1);
		tran.save(tran);
	}
}
