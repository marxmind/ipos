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
 * @since 12/30/2016
 * @version 1.0
 *
 */
public class DeliveryItemTrans {

	private long id;
	private String dateTrans;
	private double sellingPrice;
	private double quantity; 
	private String remarks;
	private int status;
	private int isActive;
	private DeliveryItemReceipt receipt;
	private DeliveryItem deliveryItem;
	private Customer customer;
	private Product product;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private boolean isBetween;
	private String dateFrom;
	private String dateTo;
	
	public DeliveryItemTrans(){}
	
	public DeliveryItemTrans(
			long id,
			String dateTrans,
			double sellingPrice,
			double quantity, 
			String remarks,
			int status,
			int isActive,
			DeliveryItemReceipt receipt,
			DeliveryItem deliveryItem,
			Customer customer,
			Product product,
			UserDtls userDtls
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.sellingPrice = sellingPrice;
		this.quantity = quantity;
		this.remarks = remarks;
		this.status = status;
		this.isActive = isActive;
		this.receipt = receipt;
		this.deliveryItem = deliveryItem;
		this.customer = customer;
		this.product = product;
		this.userDtls = userDtls;
	}
	
	public static String itemSQL(String tablename,DeliveryItemTrans item){
		String sql= " AND "+ tablename +".isactiveDelTran=" + item.getIsActive();
		if(item!=null){
			if(item.getId()!=0){
				sql += " AND "+ tablename +".deltranid=" + item.getId();
			}
			
			if(item.isBetween()){
					sql += " AND ( "+ tablename +".deltrandate >='" + item.getDateFrom() + "' AND "+ tablename +".deltrandate <='" + item.getDateTo() + "' )";
			}else{
			
				if(item.getDateTrans()!=null){
					sql += " AND "+ tablename +".deltrandate ='" + item.getDateTrans() +"'";
				}
			
			}
			if(item.getStatus()!=0){
				sql += " AND "+ tablename +".deltranItemPaymentStatus =" + item.getStatus();
			}
			
			
			
		}
		return sql;
	}
	
	public static List<DeliveryItemTrans> retrieve(Object... obj){
		List<DeliveryItemTrans> items = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
		
		String itemTable = "item";
		String prodTable = "prod";
		String transTable = "trans";
		String customerTable = "cus";
		String receiptTable = "rec";
		String userTable = "usr";
		String sql = "SELECT * FROM  deliveryitem " + itemTable + ", deliveryitemtrans " + transTable + ", deliveryitemreceipt " + receiptTable + ", customer " + customerTable + ", product " + prodTable +", userdtls "+ userTable +
				" WHERE  " + transTable + ".deltranrecid=" + receiptTable + ".deltranrecid AND " +transTable + ".customerid=" + customerTable + ".customerid AND " + transTable + ".prodid="+ prodTable +".prodid AND " + 
				transTable +".userdtlsid = "+ userTable +".userdtlsid AND " + transTable + ".delid = " + itemTable + ".delid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof DeliveryItemTrans){
				sql += itemSQL(transTable,(DeliveryItemTrans)obj[i]);
			}
			if(obj[i] instanceof DeliveryItem){
				sql += DeliveryItem.itemSQL(itemTable,(DeliveryItem)obj[i]);
			}
			if(obj[i] instanceof DeliveryItemReceipt){
				sql += DeliveryItemReceipt.receiptSQL(receiptTable,(DeliveryItemReceipt)obj[i]);
			}
			if(obj[i] instanceof Product){
				sql += Product.productSQL(prodTable,(Product)obj[i]);
			}
			if(obj[i] instanceof Customer){
				sql += Customer.customerSQL(customerTable,(Customer)obj[i]);
			}
			
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
		}
		
        System.out.println("DeliveryItemTrans SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			DeliveryItemTrans tran = new DeliveryItemTrans();
			try{tran.setId(rs.getLong("deltranid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("deltrandate"));}catch(NullPointerException e){}
			try{tran.setSellingPrice(rs.getDouble("deltranprice"));}catch(NullPointerException e){}
			try{tran.setQuantity(rs.getDouble("deltranquantity"));}catch(NullPointerException e){}
			try{tran.setRemarks(rs.getString("deltranremarks"));}catch(NullPointerException e){}
			try{tran.setStatus(rs.getInt("deltranItemPaymentStatus"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isactiveDelTran"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("deltrantimestamp"));}catch(NullPointerException e){}
			
			DeliveryItemReceipt rec = new DeliveryItemReceipt();
			try{rec.setId(rs.getLong("deltranrecid"));}catch(NullPointerException e){}
			try{rec.setDateTrans(rs.getString("deltranrecdate"));}catch(NullPointerException e){}
			try{rec.setReceiptNo(rs.getString("deltranreceiptno"));}catch(NullPointerException e){}
			try{rec.setTotalAmount(rs.getDouble("deltranTotalAmount"));}catch(NullPointerException e){}
			try{rec.setDeliveryChargeAmount(rs.getDouble("deltranChargeAmount"));}catch(NullPointerException e){}
			try{rec.setDiscountAmount(rs.getDouble("deltranDiscountAmount"));}catch(NullPointerException e){}
			try{rec.setBalanceAmount(rs.getDouble("deltranbalance"));}catch(NullPointerException e){}
			try{rec.setDownPayment(rs.getDouble("deltrabdownpayment"));}catch(NullPointerException e){}
			try{rec.setQuantity(rs.getDouble("deliveredquantity"));}catch(NullPointerException e){}
			try{rec.setRemarks(rs.getString("deltranrecremarks"));}catch(NullPointerException e){}
			try{rec.setPaymentStatus(rs.getInt("deltranPaymentReceiptStatus"));}catch(NullPointerException e){}
			try{rec.setStatus(rs.getInt("deltranrecptStatus"));}catch(NullPointerException e){}
			try{rec.setIsActive(rs.getInt("isactiveDelTranRec"));}catch(NullPointerException e){}
			try{rec.setTimestamp(rs.getTimestamp("deltranrectimestamp"));}catch(NullPointerException e){}
			try{Customer customer = new Customer();
			customer.setCustomerid(rs.getLong("customerid"));
			rec.setCustomer(customer);}catch(NullPointerException e){}
			
			tran.setReceipt(rec);
			
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
			tran.setDeliveryItem(item);
			
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
			tran.setProduct(prod);
			
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
			tran.setUserDtls(user);
			
			items.add(tran);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return items;
	}
	
	public static DeliveryItemTrans retrieve(String tranId){
		DeliveryItemTrans tran = new DeliveryItemTrans();
		
		String itemTable = "item";
		String prodTable = "prod";
		String transTable = "trans";
		String customerTable = "cus";
		String receiptTable = "rec";
		String userTable = "usr";
		String sql = "SELECT * FROM  deliveryitem " + itemTable + ", deliveryitemtrans " + transTable + ", deliveryitemreceipt " + receiptTable + ", customer " + customerTable + ", product " + prodTable +", userdtls "+ userTable +
				" WHERE  " + transTable + ".deltranrecid=" + receiptTable + ".deltranrecid AND " +transTable + ".customerid=" + customerTable + ".customerid AND " + transTable + ".prodid="+ prodTable +".prodid AND " + 
				transTable +".userdtlsid = "+ userTable +".userdtlsid AND " + transTable + ".delid = " + itemTable + ".delid AND " + transTable + "=" + tranId;
		
        
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL "+ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			tran.setId(rs.getLong("deltranid"));
			tran.setDateTrans(rs.getString("deltrandate"));
			tran.setSellingPrice(rs.getDouble("deltranprice"));
			tran.setQuantity(rs.getDouble("deltranquantity"));
			tran.setRemarks(rs.getString("deltranremarks"));
			tran.setStatus(rs.getInt("deltranItemPaymentStatus"));
			tran.setIsActive(rs.getInt("isactiveDelTran"));
			tran.setTimestamp(rs.getTimestamp("deltrantimestamp"));
			
			DeliveryItemReceipt rec = new DeliveryItemReceipt();
			rec.setId(rs.getLong("deltranrecid"));
			rec.setDateTrans(rs.getString("deltranrecdate"));
			rec.setReceiptNo(rs.getString("deltranreceiptno"));
			rec.setTotalAmount(rs.getDouble("deltranTotalAmount"));
			rec.setBalanceAmount(rs.getDouble("deltranbalance"));
			rec.setQuantity(rs.getDouble("deliveredquantity"));
			rec.setRemarks(rs.getString("deltranrecremarks"));
			rec.setStatus(rs.getInt("deltranPaymentReceiptStatus"));
			rec.setIsActive(rs.getInt("isactiveDelTranRec"));
			rec.setTimestamp(rs.getTimestamp("deltranrectimestamp"));
			tran.setReceipt(rec);
			
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
			tran.setDeliveryItem(item);
			
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
			tran.setProduct(prod);
			
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
			tran.setUserDtls(user);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return tran;
	}
	
	public static void save(DeliveryItemTrans item){
		if(item!=null){
			
			long id = DeliveryItemTrans.getInfo(item.getId() ==0? DeliveryItemTrans.getLatestId()+1 : item.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				DeliveryItemTrans.insertData(item, "1");
			}else if(id==2){
				LogU.add("update Data ");
				DeliveryItemTrans.updateData(item);
			}else if(id==3){
				LogU.add("added new Data ");
				DeliveryItemTrans.insertData(item, "3");
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
	
	public static DeliveryItemTrans insertData(DeliveryItemTrans item, String type){
		String sql = "INSERT INTO deliveryitemtrans ("
				+ "deltranid,"
				+ "deltrandate,"
				+ "deltranprice,"
				+ "deltranquantity,"
				+ "deltranremarks,"
				+ "deltranItemPaymentStatus,"
				+ "isactiveDelTran,"
				+ "deltranrecid,"
				+ "delid,"
				+ "customerid,"
				+ "prodid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table deliveryitemtrans");
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
		ps.setDouble(4, item.getQuantity());
		ps.setString(5, item.getRemarks());
		ps.setInt(6, item.getStatus());
		ps.setInt(7, item.getIsActive());
		ps.setLong(8, item.getReceipt()==null? 0 : item.getReceipt().getId());
		ps.setLong(9, item.getDeliveryItem()==null? 0 : item.getDeliveryItem().getId());
		ps.setLong(10, item.getCustomer()==null? 0 : item.getCustomer().getCustomerid());
		ps.setLong(11, item.getProduct()==null? 0 : item.getProduct().getProdid());
		ps.setLong(12, item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));		
		
		LogU.add(item.getDateTrans());
		LogU.add(item.getSellingPrice());
		LogU.add(item.getQuantity());
		LogU.add(item.getRemarks());
		LogU.add(item.getStatus());
		LogU.add(item.getIsActive());
		LogU.add(item.getReceipt()==null? 0 : item.getReceipt().getId());
		LogU.add(item.getDeliveryItem()==null? 0 : item.getDeliveryItem().getId());
		LogU.add(item.getCustomer()==null? 0 : item.getCustomer().getCustomerid());
		LogU.add(item.getProduct()==null? 0 : item.getProduct().getProdid());
		LogU.add(item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to deliveryitemtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return item;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO deliveryitemtrans ("
				+ "deltranid,"
				+ "deltrandate,"
				+ "deltranprice,"
				+ "deltranquantity,"
				+ "deltranremarks,"
				+ "deltranItemPaymentStatus,"
				+ "isactiveDelTran,"
				+ "deltranrecid,"
				+ "delid,"
				+ "customerid,"
				+ "prodid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table deliveryitemtrans");
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
		ps.setDouble(4, getQuantity());
		ps.setString(5, getRemarks());
		ps.setInt(6, getStatus());
		ps.setInt(7, getIsActive());
		ps.setLong(8, getReceipt()==null? 0 : getReceipt().getId());
		ps.setLong(9, getDeliveryItem()==null? 0 : getDeliveryItem().getId());
		ps.setLong(10, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(11, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(12, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));		
		
		LogU.add(getDateTrans());
		LogU.add(getSellingPrice());
		LogU.add(getQuantity());
		LogU.add(getRemarks());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getReceipt()==null? 0 : getReceipt().getId());
		LogU.add(getDeliveryItem()==null? 0 : getDeliveryItem().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to deliveryitemtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static DeliveryItemTrans updateData(DeliveryItemTrans item){
		String sql = "UPDATE deliveryitemtrans SET "
				+ "deltrandate=?,"
				+ "deltranprice=?,"
				+ "deltranquantity=?,"
				+ "deltranremarks=?,"
				+ "deltranItemPaymentStatus=?,"
				+ "deltranrecid=?,"
				+ "delid=?,"
				+ "customerid=?,"
				+ "prodid=?,"
				+ "userdtlsid=? " 
				+ " WHERE deltranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table deliveryitemtrans");
		
		ps.setString(1, item.getDateTrans());
		ps.setDouble(2, item.getSellingPrice());
		ps.setDouble(3, item.getQuantity());
		ps.setString(4, item.getRemarks());
		ps.setInt(5, item.getStatus());
		ps.setLong(6, item.getReceipt()==null? 0 : item.getReceipt().getId());
		ps.setLong(7, item.getDeliveryItem()==null? 0 : item.getDeliveryItem().getId());
		ps.setLong(8, item.getCustomer()==null? 0 : item.getCustomer().getCustomerid());
		ps.setLong(9, item.getProduct()==null? 0 : item.getProduct().getProdid());
		ps.setLong(10, item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));		
		ps.setLong(11, item.getId());
		
		LogU.add(item.getDateTrans());
		LogU.add(item.getSellingPrice());
		LogU.add(item.getQuantity());
		LogU.add(item.getRemarks());
		LogU.add(item.getStatus());
		LogU.add(item.getReceipt()==null? 0 : item.getReceipt().getId());
		LogU.add(item.getDeliveryItem()==null? 0 : item.getDeliveryItem().getId());
		LogU.add(item.getCustomer()==null? 0 : item.getCustomer().getCustomerid());
		LogU.add(item.getProduct()==null? 0 : item.getProduct().getProdid());
		LogU.add(item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));
		LogU.add(item.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to deliveryitemtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return item;
	}
	
	public void updateData(){
		String sql = "UPDATE deliveryitemtrans SET "
				+ "deltrandate=?,"
				+ "deltranprice=?,"
				+ "deltranquantity=?,"
				+ "deltranremarks=?,"
				+ "deltranItemPaymentStatus=?,"
				+ "deltranrecid=?,"
				+ "delid=?,"
				+ "customerid=?,"
				+ "prodid=?,"
				+ "userdtlsid=? " 
				+ " WHERE deltranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("updating data into table deliveryitemtrans");
		
		ps.setString(1, getDateTrans());
		ps.setDouble(2, getSellingPrice());
		ps.setDouble(3, getQuantity());
		ps.setString(4, getRemarks());
		ps.setInt(5, getStatus());
		ps.setLong(6, getReceipt()==null? 0 : getReceipt().getId());
		ps.setLong(7, getDeliveryItem()==null? 0 : getDeliveryItem().getId());
		ps.setLong(8, getCustomer()==null? 0 : getCustomer().getCustomerid());
		ps.setLong(9, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(10, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));		
		ps.setLong(11, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getSellingPrice());
		LogU.add(getQuantity());
		LogU.add(getRemarks());
		LogU.add(getStatus());
		LogU.add(getReceipt()==null? 0 : getReceipt().getId());
		LogU.add(getDeliveryItem()==null? 0 : getDeliveryItem().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getCustomerid());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to deliveryitemtrans : " + s.getMessage());
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
		sql="SELECT deltranid FROM deliveryitemtrans  ORDER BY deltranid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("deltranid");
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
		ps = conn.prepareStatement("SELECT deltranid FROM deliveryitemtrans WHERE deltranid=?");
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
		String sql = "UPDATE deliveryitemtrans set isactiveDelTran=0, userdtlsid=? WHERE deltranid=?";
		
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
	public DeliveryItemReceipt getReceipt() {
		return receipt;
	}
	public void setReceipt(DeliveryItemReceipt receipt) {
		this.receipt = receipt;
	}
	public DeliveryItem getDeliveryItem() {
		return deliveryItem;
	}
	public void setDeliveryItem(DeliveryItem deliveryItem) {
		this.deliveryItem = deliveryItem;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
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
	
	public static void main(String[] args) {
		DeliveryItemTrans tran = new DeliveryItemTrans();
		DeliveryItem item = new DeliveryItem();
		item.setId(11);
		tran.setDeliveryItem(item);
		tran.save();
	}
	
}
