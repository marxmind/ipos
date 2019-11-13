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
 * @since 01/16/2017
 * @version 1.0
 *
 */
public class RentedBottle {

	private long id;
	private String currentDateTrans;
	private double currentQty;
	private double currentPaidAmount;
	private String prevDateTrans;
	private double prevQty;
	private double prevPaidAmount;
	private Customer customer;
	private UserDtls userDtls;
	private UOM uom;
	private ProductProperties productProperties;
	private Timestamp timestamp;
	private double currentBalance;
	private double prevBalance;
	private double currentDeposit;
	private double prevDeposit;
	
	private boolean between;
	private String dateFrom;
	private String dateTo;
	private String remarks;
	
	public RentedBottle(){}
	
	public RentedBottle(
			long id,
			String currentDateTrans,
			double currentQty,
			double currentPaidAmount,
			String prevDateTrans,
			double prevQty,
			double prevPaidAmount,
			Customer customer,
			UserDtls userDtls,
			UOM uom,
			ProductProperties productProperties,
			double currentBalance,
			double prevBalance,
			double currentDeposit,
			double prevDeposit
			){
		this.id = id;
		this.currentDateTrans = currentDateTrans;
		this.currentQty = currentQty;
		this.currentPaidAmount = currentPaidAmount;
		this.prevDateTrans = prevDateTrans;
		this.prevQty = prevQty;
		this.prevPaidAmount = prevPaidAmount;
		this.customer = customer;
		this.uom = uom;
		this.productProperties = productProperties;
		this.userDtls = userDtls;
		this.currentBalance = currentBalance;
		this.prevBalance = prevBalance;
		this.currentDeposit = currentDeposit;
		this.prevDeposit = prevDeposit;
	}
	
	public static String rentSQL(String tablename,RentedBottle rent){
		String sql= "";
		if(rent!=null){
			if(rent.getId()!=0){
				sql += " AND "+ tablename +".botid=" + rent.getId();
			}
			if(rent.isBetween()){
				
				sql += " ( AND "+ tablename +".currentDateTrans>='" + rent.getDateFrom() +"' AND " + tablename +".currentDateTrans<='" + rent.getDateTo() + "')";
								
			}else{
				if(rent.getCurrentDateTrans()!=null){
					sql += " AND "+ tablename +".currentDateTrans='" + rent.getCurrentDateTrans() +"'";
				}
			}
		}	
			
		return sql;
	}
	
	public static List<RentedBottle> retrieve(Object...obj){
		List<RentedBottle> rents = Collections.synchronizedList(new ArrayList<RentedBottle>());
		
		String rentTable = "rent";
		String cusTable = "cus";
		String uomTable = "uom";
		String propTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM rentedbottle " + rentTable + 
				", customer " + cusTable +
				", uom " + uomTable + 
				", productproperties " + propTable + 
				", userdtls " + userTable +
				" WHERE " + rentTable + ".uomid=" + uomTable + ".uomid " + 
				" AND " + rentTable + ".propid=" + propTable+ ".propid " + 
				" AND " + rentTable + ".customerid=" + cusTable + ".customerid " + 
				" AND " + rentTable + ".userdtlsid=" + userTable + ".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof RentedBottle){
				sql += rentSQL(rentTable,(RentedBottle)obj[i]);
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
		
		System.out.println("Rented Bottle SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			RentedBottle rent = new RentedBottle();
			try{rent.setId(rs.getLong("botid"));}catch(NullPointerException n){}
			try{rent.setCurrentDateTrans(rs.getString("currentDateTrans"));}catch(NullPointerException n){}
			try{rent.setCurrentQty(rs.getDouble("currentQuantity"));}catch(NullPointerException n){}
			try{rent.setCurrentPaidAmount(rs.getDouble("currentPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setPrevDateTrans(rs.getString("prevDateTrans"));}catch(NullPointerException n){}
			try{rent.setPrevQty(rs.getDouble("prevQuantity"));}catch(NullPointerException n){}
			try{rent.setPrevPaidAmount(rs.getDouble("prevPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setTimestamp(rs.getTimestamp("rentimestamp"));}catch(NullPointerException n){}
			try{rent.setCurrentBalance(rs.getDouble("currentBalance"));}catch(NullPointerException n){}
			try{rent.setPrevBalance(rs.getDouble("prevBalance"));}catch(NullPointerException n){}
			try{rent.setCurrentDeposit(rs.getDouble("currentDeposit"));}catch(NullPointerException n){}
			try{rent.setPrevDeposit(rs.getDouble("prevDeposit"));}catch(NullPointerException n){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			rent.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			rent.setProductProperties(prop);
			
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
			rent.setCustomer(cus);
			
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
			rent.setUserDtls(user);
			
			rents.add(rent);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rents;
		
	}	
	
	public static List<RentedBottle> retrieve(String sql, String[] params){
		List<RentedBottle> rents = Collections.synchronizedList(new ArrayList<RentedBottle>());
		
		String rentTable = "rent";
		String cusTable = "cus";
		String uomTable = "uom";
		String propTable = "prop";
		String userTable = "usr";
		String sqlAdd = "SELECT * FROM rentedbottle " + rentTable + 
				", customer " + cusTable +
				", uom " + uomTable + 
				", productproperties " + propTable + 
				", userdtls " + userTable +
				" WHERE " + rentTable + ".uomid=" + uomTable + ".uomid " + 
				" AND " + rentTable + ".propid=" + propTable+ ".propid " + 
				" AND " + rentTable + ".customerid=" + cusTable + ".customerid " + 
				" AND " + rentTable + ".userdtlsid=" + userTable + ".userdtlsid ";
		
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
			
			/*RentedBottle rent = new RentedBottle();
			try{rent.setId(rs.getLong("botid"));}catch(NullPointerException n){}
			try{rent.setCurrentDateTrans(rs.getString("currentDateTrans"));}catch(NullPointerException n){}
			try{rent.setCurrentQty(rs.getDouble("currentQuantity"));}catch(NullPointerException n){}
			try{rent.setCurrentPaidAmount(rs.getDouble("currentPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setPrevDateTrans(rs.getString("prevDateTrans"));}catch(NullPointerException n){}
			try{rent.setPrevQty(rs.getDouble("prevQuantity"));}catch(NullPointerException n){}
			try{rent.setPrevPaidAmount(rs.getDouble("prevPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setTimestamp(rs.getTimestamp("rentimestamp"));}catch(NullPointerException n){}
			try{rent.setCurrentBalance(rs.getDouble("currentBalance"));}catch(NullPointerException n){}
			try{rent.setPrevBalance(rs.getDouble("prevBalance"));}catch(NullPointerException n){}
			try{rent.setCurrentDeposit(rs.getDouble("currentDeposit"));}catch(NullPointerException n){}
			try{rent.setPrevDeposit(rs.getDouble("prevDeposit"));}catch(NullPointerException n){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException n){}
			rent.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			rent.setProductProperties(prop);
			
			Customer customer = new Customer();
			try{customer.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException n){}
			try{customer.setFullname(rs.getString("fullname"));}catch(Exception e){}
			rent.setCustomer(customer);
			
			UserDtls userDtls = new UserDtls();
			try{userDtls.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException n){}
			rent.setUserDtls(userDtls);
			rents.add(rent);*/
			
			RentedBottle rent = new RentedBottle();
			try{rent.setId(rs.getLong("botid"));}catch(NullPointerException n){}
			try{rent.setCurrentDateTrans(rs.getString("currentDateTrans"));}catch(NullPointerException n){}
			try{rent.setCurrentQty(rs.getDouble("currentQuantity"));}catch(NullPointerException n){}
			try{rent.setCurrentPaidAmount(rs.getDouble("currentPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setPrevDateTrans(rs.getString("prevDateTrans"));}catch(NullPointerException n){}
			try{rent.setPrevQty(rs.getDouble("prevQuantity"));}catch(NullPointerException n){}
			try{rent.setPrevPaidAmount(rs.getDouble("prevPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setTimestamp(rs.getTimestamp("rentimestamp"));}catch(NullPointerException n){}
			try{rent.setCurrentBalance(rs.getDouble("currentBalance"));}catch(NullPointerException n){}
			try{rent.setPrevBalance(rs.getDouble("prevBalance"));}catch(NullPointerException n){}
			try{rent.setCurrentDeposit(rs.getDouble("currentDeposit"));}catch(NullPointerException n){}
			try{rent.setPrevDeposit(rs.getDouble("prevDeposit"));}catch(NullPointerException n){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			rent.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			rent.setProductProperties(prop);
			
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
			rent.setCustomer(cus);
			
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
			rent.setUserDtls(user);
			
			rents.add(rent);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rents;
		
	}
	
	public static RentedBottle retrieve(String rentedId){
		RentedBottle rent = new RentedBottle();
		String rentTable = "rent";
		String cusTable = "cus";
		String uomTable = "uom";
		String propTable = "prop";
		String userTable = "usr";
		String sql = "SELECT * FROM rentedbottle " + rentTable + 
				", customer " + cusTable +
				", uom " + uomTable + 
				", productproperties " + propTable + 
				", userdtls " + userTable +
				" WHERE " + rentTable + ".uomid=" + uomTable + ".uomid " + 
				" AND " + rentTable + ".propid=" + propTable+ ".propid " + 
				" AND " + rentTable + ".customerid=" + cusTable + ".customerid " + 
				" AND " + rentTable + ".userdtlsid=" + userTable + ".userdtlsid  AND " + rentTable + ".botid=" + rentedId ;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("rented Bottle search id " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{rent.setId(rs.getLong("botid"));}catch(NullPointerException n){}
			try{rent.setCurrentDateTrans(rs.getString("currentDateTrans"));}catch(NullPointerException n){}
			try{rent.setCurrentQty(rs.getDouble("currentQuantity"));}catch(NullPointerException n){}
			try{rent.setCurrentPaidAmount(rs.getDouble("currentPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setPrevDateTrans(rs.getString("prevDateTrans"));}catch(NullPointerException n){}
			try{rent.setPrevQty(rs.getDouble("prevQuantity"));}catch(NullPointerException n){}
			try{rent.setPrevPaidAmount(rs.getDouble("prevPaidAmnt"));}catch(NullPointerException n){}
			try{rent.setTimestamp(rs.getTimestamp("rentimestamp"));}catch(NullPointerException n){}
			try{rent.setCurrentBalance(rs.getDouble("currentBalance"));}catch(NullPointerException n){}
			try{rent.setPrevBalance(rs.getDouble("prevBalance"));}catch(NullPointerException n){}
			try{rent.setCurrentDeposit(rs.getDouble("currentDeposit"));}catch(NullPointerException n){}
			try{rent.setPrevDeposit(rs.getDouble("prevDeposit"));}catch(NullPointerException n){}
			
			UOM uom = new UOM();
			try{uom.setUomid(rs.getInt("uomid"));}catch(NullPointerException e){}
			try{uom.setUomname(rs.getString("uomname"));}catch(NullPointerException e){}
			try{uom.setSymbol(rs.getString("symbol"));}catch(NullPointerException e){}
			try{uom.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{uom.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			rent.setUom(uom);
			
			ProductProperties prop = new ProductProperties();
			try{prop.setPropid(rs.getLong("propid"));}catch(NullPointerException e){}
			try{prop.setProductcode(rs.getString("productcode"));}catch(NullPointerException e){}
			try{prop.setProductname(rs.getString("productname"));}catch(NullPointerException e){}
			try{prop.setImagepath(rs.getString("imagepath"));}catch(NullPointerException e){}
			try{prop.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prop.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			rent.setProductProperties(prop);
			
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
			rent.setCustomer(cus);
			
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
			rent.setUserDtls(user);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rent;
	}	
	
	
	public static RentedBottle save(RentedBottle rent){
		if(rent!=null){
			
			long id = RentedBottle.getInfo(rent.getId() ==0? RentedBottle.getLatestId()+1 : rent.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				rent = RentedBottle.insertData(rent, "1");
			}else if(id==2){
				LogU.add("update Data ");
				rent = RentedBottle.updateData(rent);
			}else if(id==3){
				LogU.add("added new Data ");
				rent = RentedBottle.insertData(rent, "3");
			}
			
		}
		return rent;
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
	
	public static RentedBottle insertData(RentedBottle rent, String type){
		String sql = "INSERT INTO rentedbottle ("
				+ "botid,"
				+ "currentQuantity,"
				+ "currentDateTrans,"
				+ "currentPaidAmnt,"
				+ "prevQuantity,"
				+ "prevDateTrans,"
				+ "prevPaidAmnt,"
				+ "uomid,"
				+ "propid,"
				+ "customerid,"
				+ "userdtlsid,"
				+ "currentBalance,"
				+ "currentDeposit,"
				+ "prevBalance,"
				+ "prevDeposit)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table rentedbottle");
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
		
		ps.setDouble(i++, rent.getCurrentQty());
		ps.setString(i++, rent.getCurrentDateTrans());
		ps.setDouble(i++, rent.getCurrentPaidAmount());
		ps.setDouble(i++, rent.getPrevQty());
		ps.setString(i++, rent.getPrevDateTrans());
		ps.setDouble(i++, rent.getPrevPaidAmount());
		ps.setInt(i++, rent.getUom()==null? 0 : rent.getUom().getUomid());
		ps.setLong(i++, rent.getProductProperties()==null? 0 : rent.getProductProperties().getPropid());
		ps.setLong(i++, rent.getCustomer()==null? 0 : rent.getCustomer().getCustomerid());
		ps.setLong(i++, rent.getUserDtls()==null? 0l : (rent.getUserDtls().getUserdtlsid()==null? 0l : rent.getUserDtls().getUserdtlsid()));
		ps.setDouble(i++, rent.getCurrentBalance());
		ps.setDouble(i++, rent.getCurrentDeposit());
		ps.setDouble(i++, rent.getPrevBalance());
		ps.setDouble(i++, rent.getPrevDeposit());
		
		LogU.add(rent.getCurrentQty());
		LogU.add(rent.getCurrentDateTrans());
		LogU.add(rent.getCurrentPaidAmount());
		LogU.add(rent.getPrevQty());
		LogU.add(rent.getPrevDateTrans());
		LogU.add(rent.getPrevPaidAmount());
		LogU.add(rent.getUom()==null? 0 : rent.getUom().getUomid());
		LogU.add(rent.getProductProperties()==null? 0 : rent.getProductProperties().getPropid());
		LogU.add(rent.getCustomer()==null? 0 : rent.getCustomer().getCustomerid());
		LogU.add(rent.getUserDtls()==null? 0l : (rent.getUserDtls().getUserdtlsid()==null? 0l : rent.getUserDtls().getUserdtlsid()));
		LogU.add(rent.getCurrentBalance());
		LogU.add(rent.getCurrentDeposit());
		LogU.add(rent.getPrevBalance());
		LogU.add(rent.getPrevDeposit());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to rentedbottle : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rent;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO rentedbottle ("
				+ "botid,"
				+ "currentQuantity,"
				+ "currentDateTrans,"
				+ "currentPaidAmnt,"
				+ "prevQuantity,"
				+ "prevDateTrans,"
				+ "prevPaidAmnt,"
				+ "uomid,"
				+ "propid,"
				+ "customerid,"
				+ "userdtlsid,"
				+ "currentBalance,"
				+ "currentDeposit,"
				+ "prevBalance,"
				+ "prevDeposit)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table rentedbottle");
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
		
		ps.setDouble(i++, getCurrentQty());
		ps.setString(i++, getCurrentDateTrans());
		ps.setDouble(i++, getCurrentPaidAmount());
		ps.setDouble(i++, getPrevQty());
		ps.setString(i++, getPrevDateTrans());
		ps.setDouble(i++, getPrevPaidAmount());
		ps.setInt(i++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(i++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(i++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(i++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setDouble(i++, getCurrentBalance());
		ps.setDouble(i++, getCurrentDeposit());
		ps.setDouble(i++, getPrevBalance());
		ps.setDouble(i++, getPrevDeposit());
		
		LogU.add(getCurrentQty());
		LogU.add(getCurrentDateTrans());
		LogU.add(getCurrentPaidAmount());
		LogU.add(getPrevQty());
		LogU.add(getPrevDateTrans());
		LogU.add(getPrevPaidAmount());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getCurrentBalance());
		LogU.add(getCurrentDeposit());
		LogU.add(getPrevBalance());
		LogU.add(getPrevDeposit());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to rentedbottle : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static RentedBottle updateData(RentedBottle rent){
		String sql = "UPDATE rentedbottle SET "
				+ "currentQuantity=?,"
				+ "currentDateTrans=?,"
				+ "currentPaidAmnt=?,"
				+ "prevQuantity=?,"
				+ "prevDateTrans=?,"
				+ "prevPaidAmnt=?,"
				+ "uomid=?,"
				+ "propid=?,"
				+ "customerid=?,"
				+ "userdtlsid=?," 
				+ "currentBalance=?,"
				+ "currentDeposit=?,"
				+ "prevBalance=?,"
				+ "prevDeposit=?"
				+ " WHERE botid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table rentedbottle");
		
		ps.setDouble(i++, rent.getCurrentQty());
		ps.setString(i++, rent.getCurrentDateTrans());
		ps.setDouble(i++, rent.getCurrentPaidAmount());
		ps.setDouble(i++, rent.getPrevQty());
		ps.setString(i++, rent.getPrevDateTrans());
		ps.setDouble(i++, rent.getPrevPaidAmount());
		ps.setInt(i++, rent.getUom()==null? 0 : rent.getUom().getUomid());
		ps.setLong(i++, rent.getProductProperties()==null? 0 : rent.getProductProperties().getPropid());
		ps.setLong(i++, rent.getCustomer()==null? 0 : rent.getCustomer().getCustomerid());
		ps.setLong(i++, rent.getUserDtls()==null? 0l : (rent.getUserDtls().getUserdtlsid()==null? 0l : rent.getUserDtls().getUserdtlsid()));
		ps.setDouble(i++, rent.getCurrentBalance());
		ps.setDouble(i++, rent.getCurrentDeposit());
		ps.setDouble(i++, rent.getPrevBalance());
		ps.setDouble(i++, rent.getPrevDeposit());
		ps.setLong(i++, rent.getId());
		
		LogU.add(rent.getCurrentQty());
		LogU.add(rent.getCurrentDateTrans());
		LogU.add(rent.getCurrentPaidAmount());
		LogU.add(rent.getPrevQty());
		LogU.add(rent.getPrevDateTrans());
		LogU.add(rent.getPrevPaidAmount());
		LogU.add(rent.getUom()==null? 0 : rent.getUom().getUomid());
		LogU.add(rent.getProductProperties()==null? 0 : rent.getProductProperties().getPropid());
		LogU.add(rent.getCustomer()==null? 0 : rent.getCustomer().getCustomerid());
		LogU.add(rent.getUserDtls()==null? 0l : (rent.getUserDtls().getUserdtlsid()==null? 0l : rent.getUserDtls().getUserdtlsid()));
		LogU.add(rent.getCurrentBalance());
		LogU.add(rent.getCurrentDeposit());
		LogU.add(rent.getPrevBalance());
		LogU.add(rent.getPrevDeposit());
		LogU.add(rent.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to rentedbottle : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rent;
	}
	
	public void updateData(){
		String sql = "UPDATE rentedbottle SET "
				+ "currentQuantity=?,"
				+ "currentDateTrans=?,"
				+ "currentPaidAmnt=?,"
				+ "prevQuantity=?,"
				+ "prevDateTrans=?,"
				+ "prevPaidAmnt=?,"
				+ "uomid=?,"
				+ "propid=?,"
				+ "customerid=?,"
				+ "userdtlsid=?," 
				+ "currentBalance=?,"
				+ "currentDeposit=?,"
				+ "prevBalance=?,"
				+ "prevDeposit=?"
				+ " WHERE botid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int i=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table rentedbottle");
		
		ps.setDouble(i++, getCurrentQty());
		ps.setString(i++, getCurrentDateTrans());
		ps.setDouble(i++, getCurrentPaidAmount());
		ps.setDouble(i++, getPrevQty());
		ps.setString(i++, getPrevDateTrans());
		ps.setDouble(i++, getPrevPaidAmount());
		ps.setInt(i++, getUom()==null? 0 : getUom().getUomid());
		ps.setLong(i++, getProductProperties()==null? 0 : getProductProperties().getPropid());
		ps.setLong(i++, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(i++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setDouble(i++, getCurrentBalance());
		ps.setDouble(i++, getCurrentDeposit());
		ps.setDouble(i++, getPrevBalance());
		ps.setDouble(i++, getPrevDeposit());
		ps.setLong(i++, getId());
		
		LogU.add(getCurrentQty());
		LogU.add(getCurrentDateTrans());
		LogU.add(getCurrentPaidAmount());
		LogU.add(getPrevQty());
		LogU.add(getPrevDateTrans());
		LogU.add(getPrevPaidAmount());
		LogU.add(getUom()==null? 0 : getUom().getUomid());
		LogU.add(getProductProperties()==null? 0 : getProductProperties().getPropid());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getCurrentBalance());
		LogU.add(getCurrentDeposit());
		LogU.add(getPrevBalance());
		LogU.add(getPrevDeposit());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to rentedbottle : " + s.getMessage());
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
		sql="SELECT botid FROM rentedbottle  ORDER BY botid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("botid");
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
		ps = conn.prepareStatement("SELECT botid FROM rentedbottle WHERE botid=?");
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
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCurrentDateTrans() {
		return currentDateTrans;
	}
	public void setCurrentDateTrans(String currentDateTrans) {
		this.currentDateTrans = currentDateTrans;
	}
	public double getCurrentQty() {
		return currentQty;
	}
	public void setCurrentQty(double currentQty) {
		this.currentQty = currentQty;
	}
	public double getCurrentPaidAmount() {
		return currentPaidAmount;
	}
	public void setCurrentPaidAmount(double currentPaidAmount) {
		this.currentPaidAmount = currentPaidAmount;
	}
	public String getPrevDateTrans() {
		return prevDateTrans;
	}
	public void setPrevDateTrans(String prevDateTrans) {
		this.prevDateTrans = prevDateTrans;
	}
	public double getPrevQty() {
		return prevQty;
	}
	public void setPrevQty(double prevQty) {
		this.prevQty = prevQty;
	}
	public double getPrevPaidAmount() {
		return prevPaidAmount;
	}
	public void setPrevPaidAmount(double prevPaidAmount) {
		this.prevPaidAmount = prevPaidAmount;
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
	public UOM getUom() {
		return uom;
	}
	public void setUom(UOM uom) {
		this.uom = uom;
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

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public ProductProperties getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public double getPrevBalance() {
		return prevBalance;
	}

	public void setPrevBalance(double prevBalance) {
		this.prevBalance = prevBalance;
	}

	public double getCurrentDeposit() {
		return currentDeposit;
	}

	public void setCurrentDeposit(double currentDeposit) {
		this.currentDeposit = currentDeposit;
	}

	public double getPrevDeposit() {
		return prevDeposit;
	}

	public void setPrevDeposit(double prevDeposit) {
		this.prevDeposit = prevDeposit;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
