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

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 09/29/2016
 * @version 1.0
 */
public class PurchasedItem {
	
	private long itemid;
	private String datesold;
	private String productName;
	private String productbrand;
	private String uomSymbol;
	private BigDecimal purchasedprice;
	private BigDecimal netprice;
	private double taxpercentage;
	private BigDecimal sellingPrice;
	private double qty;
	private int isactiveitem;
	private UserDtls userDtls;
	private Product product;
	private Transactions transactions;
	private Timestamp timestamp;
	
	private ProductProperties productProperties;
	private double totalPrice;
	private long productId;
	private int count;
	private boolean isBetweenDate;
	private String dateFrom;
	private String dateTo;
	private BigDecimal incomesPlusVat;
	private BigDecimal incomesMinusVat;
	private BigDecimal capital;
	private BigDecimal sales;
	
	private StoreProduct storeProduct;
	
	private AddOnStore addOnStore;
	
	public PurchasedItem(){}
	
	public PurchasedItem(
			long itemid,
			String datesold,
			String productbrand,
			String uomSymbol,
			double qty,
			BigDecimal sellingPrice,
			BigDecimal purchasedprice,
			BigDecimal netprice,
			double taxpercentage,
			int isactiveitem,
			Product product,
			Transactions transactions, 
			UserDtls userDtls,
			String dateFrom,
			String dateTo,
			boolean isBetweenDate
			){
		this.itemid = itemid;
		this.datesold = datesold;
		this.productbrand = productbrand;
		this.uomSymbol = uomSymbol;
		this.qty = qty;
		this.sellingPrice = sellingPrice;
		this.purchasedprice = purchasedprice;
		this.netprice = netprice;
		this.taxpercentage = taxpercentage;
		this.isactiveitem = isactiveitem;
		this.product = product;
		this.transactions = transactions;
		this.userDtls = userDtls;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.isBetweenDate = isBetweenDate;
	}
	
	public static String purchasedSQL(String tablename,PurchasedItem prop){
		String sql= " AND "+ tablename +".isactiveitem=" + prop.getIsactiveitem();
		if(prop!=null){
			
			if(prop.isIsBetweenDate()){
				sql += " AND ("+ tablename +".datesold>='" +prop.getDateFrom() +"' AND "+ tablename +".datesold<='" +prop.getDateTo()+"' ) ";
			}else{
			
				if(prop.getDatesold()!=null){
					sql += " AND "+ tablename +".datesold='" + prop.getDatesold() +"'";
				}
			
			}
			
			if(prop.getItemid()!=0){
				sql += " AND "+ tablename +".itemid=" + prop.getItemid();
			}
			
			
			
			if(prop.getProductName()!=null){
				sql += " AND "+ tablename +".productname like '%" + prop.getProductName() +"%'";
			}
			if(prop.getProductbrand()!=null){
				sql += " AND "+ tablename +".productbrand like '%" + prop.getProductbrand() +"%'";
			}
			if(prop.getUomSymbol()!=null){
				sql += " AND "+ tablename +".uomSymbol like '%" + prop.getUomSymbol() +"%'";
			}
			if(prop.getQty()!=0){
				sql += " AND "+ tablename +".qty=" + prop.getQty();
			}
			
			if(prop.getTaxpercentage()!=0){
				sql += " AND "+ tablename +".taxpercentage=" + prop.getTaxpercentage();
			}
			
			if(prop.getPurchasedprice()!=null){
				sql += " AND "+ tablename +".purchasedprice=" + prop.getPurchasedprice();
			}
			
			if(prop.getSellingPrice()!=null){
				sql += " AND "+ tablename +".sellingPrice=" + prop.getSellingPrice();
			}
			
			if(prop.getNetprice()!=null){
				sql += " AND "+ tablename +".netprice=" + prop.getNetprice();
			}
			
			if(prop.getProduct()!=null){
				if(prop.getProduct().getProdid()!=0){
					sql += " AND "+ tablename +".prodid=" + prop.getProduct().getProdid()	;
				}
			}
			
			if(prop.getUserDtls()!=null){
				if(prop.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + prop.getUserDtls().getUserdtlsid() ;
				}
			}
			if(prop.getTransactions()!=null){
				if(prop.getTransactions().getTransid()!=0){
					sql += " AND "+ tablename +".transid=" + prop.getTransactions().getTransid();
				}
			}
		}
		return sql;
	}

	/**
	 * 
	 * @param obj[Product][UserDtls]
	 * @return list of product properties
	 */
	public static List<PurchasedItem> retrieve(Object ...obj){
		List<PurchasedItem> prods = Collections.synchronizedList(new ArrayList<PurchasedItem>());
		String prodTable = "prod";
		String transTable = "trans";
		String itemTable = "item";
		String userTable = "usr";
		String sql = "SELECT * FROM purchaseditem " + itemTable + ",product "+ prodTable  + ", transactions " + transTable  +
				", userdtls "+ userTable +" WHERE " + itemTable + ".prodid = " + prodTable + ".prodid AND "+ itemTable + ".transid=" + transTable + ".transid AND " + 
				prodTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof PurchasedItem){
				sql += purchasedSQL(itemTable,(PurchasedItem)obj[i]);
			}
			if(obj[i] instanceof Transactions){
				sql += Transactions.transSQL(transTable,(Transactions)obj[i]);
			}
			if(obj[i] instanceof Product){
				sql += Product.productSQL(prodTable,(Product)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
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
			
			PurchasedItem item = new PurchasedItem();
			try{item.setItemid(rs.getLong("itemid"));}catch(NullPointerException e){}
			try{item.setDatesold(rs.getString("datesold"));}catch(NullPointerException e){}
			try{item.setProductName(rs.getString("productname"));}catch(NullPointerException e){}
			try{item.setProductbrand(rs.getString("productbrand"));}catch(NullPointerException e){}
			try{item.setUomSymbol(rs.getString("uomSymbol"));}catch(NullPointerException e){}
			try{item.setQty(rs.getDouble("qty"));}catch(NullPointerException e){}
			try{item.setPurchasedprice(rs.getBigDecimal("purchasedprice"));}catch(NullPointerException e){}
			try{item.setSellingPrice(rs.getBigDecimal("sellingPrice"));}catch(NullPointerException e){}
			try{item.setNetprice(rs.getBigDecimal("netprice"));}catch(NullPointerException e){}
			try{item.setTaxpercentage(rs.getDouble("taxpercentage"));}catch(NullPointerException e){}
			try{item.setIsactiveitem(rs.getInt("isactiveitem"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setProduct(prod);
			
			
			Transactions trans = new Transactions();
			try{trans.setTransid(rs.getLong("transid"));}catch(NullPointerException e){}
			trans.setTransdate(rs.getString("transdate"));
			try{trans.setAmountpurchased(rs.getBigDecimal("amountpurchased"));}catch(NullPointerException e){}
			try{trans.setAmountreceived(rs.getBigDecimal("amountreceived"));}catch(NullPointerException e){}
			try{trans.setAmountchange(rs.getBigDecimal("amountchange"));}catch(NullPointerException e){}
			try{trans.setAmountbal(rs.getBigDecimal("amountbal"));}catch(NullPointerException e){}
			try{trans.setDiscount(rs.getBigDecimal("discount"));}catch(NullPointerException e){}
			try{trans.setVatsales(rs.getBigDecimal("vatsales"));}catch(NullPointerException e){}
			try{trans.setVatexmptsales(rs.getBigDecimal("vatexmptsales"));}catch(NullPointerException e){}
			try{trans.setZeroratedsales(rs.getBigDecimal("zeroratedsales"));}catch(NullPointerException e){}
			try{trans.setVatnet(rs.getBigDecimal("vatnet"));}catch(NullPointerException e){}
			try{trans.setVatamnt(rs.getBigDecimal("vatamnt"));}catch(NullPointerException e){}
			try{trans.setIsvoidtrans(rs.getInt("isvoidtrans"));}catch(NullPointerException e){}
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			Customer customer = new Customer();
			try{customer.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			trans.setCustomer(customer);
			
			item.setTransactions(trans);
			
			
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
			
			prods.add(item);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return prods;
	}
	
	/**
	 * 
	 * @param obj[Product][UserDtls]
	 * @return list of product properties
	 */
	public static PurchasedItem retrieve(String itemid){
		PurchasedItem item = new PurchasedItem();
		String prodTable = "prod";
		String itemTable = "item";
		String userTable = "usr";
		String sql = "SELECT * FROM purchaseditem " + itemTable + ",product "+ prodTable  +
				", userdtls "+ userTable +" WHERE " + itemTable + ".prodid = " + prodTable + ".prodid AND "+ 
				prodTable +".userdtlsid = "+ userTable +".userdtlsid AND "+ itemTable + ".itemid=" + itemid;
		
		
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{item.setItemid(rs.getLong("itemid"));}catch(NullPointerException e){}
			try{item.setDatesold(rs.getString("datesold"));}catch(NullPointerException e){}
			try{item.setProductName(rs.getString("productname"));}catch(NullPointerException e){}
			try{item.setProductbrand(rs.getString("productbrand"));}catch(NullPointerException e){}
			try{item.setUomSymbol(rs.getString("uomSymbol"));}catch(NullPointerException e){}
			try{item.setQty(rs.getDouble("qty"));}catch(NullPointerException e){}
			try{item.setPurchasedprice(rs.getBigDecimal("purchasedprice"));}catch(NullPointerException e){}
			try{item.setSellingPrice(rs.getBigDecimal("sellingPrice"));}catch(NullPointerException e){}
			try{item.setNetprice(rs.getBigDecimal("netprice"));}catch(NullPointerException e){}
			try{item.setTaxpercentage(rs.getDouble("taxpercentage"));}catch(NullPointerException e){}
			try{item.setIsactiveitem(rs.getInt("isactiveitem"));}catch(NullPointerException e){}
			try{item.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			Product prod = new Product();
			try{prod.setProdid(rs.getLong("prodid"));}catch(NullPointerException e){}
			try{prod.setDatecoded(rs.getString("datecoded"));}catch(NullPointerException e){}
			try{prod.setBarcode(rs.getString("barcode"));}catch(NullPointerException e){}
			try{prod.setProductExpiration(rs.getString("productExpiration"));}catch(NullPointerException e){}
			try{prod.setIsactiveproduct(rs.getInt("isactiveproduct"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			item.setProduct(prod);
			
			Transactions trans = new Transactions();
			try{trans.setTransid(rs.getLong("transid"));}catch(NullPointerException e){}
			try{trans.setTransdate(rs.getString("transdate"));}catch(NullPointerException e){}
			try{trans.setAmountpurchased(rs.getBigDecimal("amountpurchased"));}catch(NullPointerException e){}
			try{trans.setAmountreceived(rs.getBigDecimal("amountreceived"));}catch(NullPointerException e){}
			try{trans.setAmountchange(rs.getBigDecimal("amountchange"));}catch(NullPointerException e){}
			try{trans.setAmountbal(rs.getBigDecimal("amountbal"));}catch(NullPointerException e){}
			try{trans.setDiscount(rs.getBigDecimal("discount"));}catch(NullPointerException e){}
			try{trans.setVatsales(rs.getBigDecimal("vatsales"));}catch(NullPointerException e){}
			try{trans.setVatexmptsales(rs.getBigDecimal("vatexmptsales"));}catch(NullPointerException e){}
			try{trans.setZeroratedsales(rs.getBigDecimal("zeroratedsales"));}catch(NullPointerException e){}
			try{trans.setVatnet(rs.getBigDecimal("vatnet"));}catch(NullPointerException e){}
			try{trans.setVatamnt(rs.getBigDecimal("vatamnt"));}catch(NullPointerException e){}
			try{trans.setIsvoidtrans(rs.getInt("isvoidtrans"));}catch(NullPointerException e){}
			try{trans.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			Customer customer = new Customer();
			try{customer.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			trans.setCustomer(customer);
			
			item.setTransactions(trans);
			
			
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
	
	public static PurchasedItem save(PurchasedItem item){
		if(item!=null){
			
			long id = PurchasedItem.getInfo(item.getItemid() ==0? PurchasedItem.getLatestId()+1 : item.getItemid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				item = PurchasedItem.insertData(item, "1");
			}else if(id==2){
				LogU.add("update Data ");
				item = PurchasedItem.updateData(item);
			}else if(id==3){
				LogU.add("added new Data ");
				item = PurchasedItem.insertData(item, "3");
			}
			
		}
		return item;
	}
	
	public void save(){
		
			long id = getInfo(getItemid() ==0? getLatestId()+1 : getItemid());
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
	
	public static PurchasedItem insertData(PurchasedItem item, String type){
		String sql = "INSERT INTO purchaseditem ("
				+ "itemid,"
				+ "datesold,"
				+ "productname,"
				+ "productbrand,"
				+ "uomSymbol,"
				+ "qty,"
				+ "purchasedprice,"
				+ "sellingPrice,"
				+ "netprice,"
				+ "taxpercentage,"
				+ "isactiveitem,"
				+ "prodid,"
				+ "transid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table purchaseditem");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			item.setItemid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			item.setItemid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, item.getDatesold());
		ps.setString(3, item.getProductName());
		ps.setString(4, item.getProductbrand());
		ps.setString(5, item.getUomSymbol());
		ps.setDouble(6, item.getQty());
		ps.setBigDecimal(7, item.getPurchasedprice());
		ps.setBigDecimal(8, item.getSellingPrice());
		ps.setBigDecimal(9, item.getNetprice());
		ps.setDouble(10, item.getTaxpercentage());
		ps.setInt(11, item.getIsactiveitem());
		ps.setLong(12, item.getProduct()==null? 0 : item.getProduct().getProdid());
		ps.setLong(13, item.getTransactions()==null? 0 : item.getTransactions().getTransid());
		ps.setLong(14, item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));
		
		LogU.add(item.getDatesold());
		LogU.add(item.getProductName());
		LogU.add(item.getProductbrand());
		LogU.add(item.getUomSymbol());
		LogU.add(item.getQty());
		LogU.add(item.getPurchasedprice());
		LogU.add(item.getSellingPrice());
		LogU.add(item.getNetprice());
		LogU.add(item.getTaxpercentage());
		LogU.add(item.getIsactiveitem());
		LogU.add(item.getProduct()==null? 0 : item.getProduct().getProdid());
		LogU.add(item.getTransactions()==null? 0 : item.getTransactions().getTransid());
		LogU.add(item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to purchaseditem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return item;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO purchaseditem ("
				+ "itemid,"
				+ "datesold,"
				+ "productname,"
				+ "productbrand,"
				+ "uomSymbol,"
				+ "qty,"
				+ "purchasedprice,"
				+ "sellingPrice,"
				+ "netprice,"
				+ "taxpercentage,"
				+ "isactiveitem,"
				+ "prodid,"
				+ "transid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table purchaseditem");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setItemid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setItemid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, getDatesold());
		ps.setString(3, getProductName());
		ps.setString(4, getProductbrand());
		ps.setString(5, getUomSymbol());
		ps.setDouble(6, getQty());
		ps.setBigDecimal(7, getPurchasedprice());
		ps.setBigDecimal(8, getSellingPrice());
		ps.setBigDecimal(9, getNetprice());
		ps.setDouble(10, getTaxpercentage());
		ps.setInt(11, getIsactiveitem());
		ps.setLong(12, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(13, getTransactions()==null? 0 : getTransactions().getTransid());
		ps.setLong(14, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add(getDatesold());
		LogU.add(getProductName());
		LogU.add(getProductbrand());
		LogU.add(getUomSymbol());
		LogU.add(getQty());
		LogU.add(getPurchasedprice());
		LogU.add(getSellingPrice());
		LogU.add(getNetprice());
		LogU.add(getTaxpercentage());
		LogU.add(getIsactiveitem());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getTransactions()==null? 0 : getTransactions().getTransid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to purchaseditem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static PurchasedItem updateData(PurchasedItem item){
		String sql = "UPDATE purchaseditem SET "
				+ "datesold=?,"
				+ "productname=?,"
				+ "productbrand=?,"
				+ "uomSymbol=?,"
				+ "qty=?,"
				+ "purchasedprice=?,"
				+ "sellingPrice=?,"
				+ "netprice=?,"
				+ "taxpercentage=?,"
				+ "isactiveitem=?,"
				+ "userdtlsid=? " 
				+ " WHERE itemid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table purchaseditem");
		
		ps.setString(1, item.getDatesold());
		ps.setString(2, item.getProductName());
		ps.setString(3, item.getProductbrand());
		ps.setString(4, item.getUomSymbol());
		ps.setDouble(5, item.getQty());
		ps.setBigDecimal(6, item.getPurchasedprice());
		ps.setBigDecimal(7, item.getSellingPrice());
		ps.setBigDecimal(8, item.getNetprice());
		ps.setDouble(9, item.getTaxpercentage());
		ps.setInt(10, item.getIsactiveitem());
		ps.setLong(11, item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));
		ps.setLong(12, item.getItemid());
		
		LogU.add(item.getDatesold());
		LogU.add(item.getProductName());
		LogU.add(item.getProductbrand());
		LogU.add(item.getUomSymbol());
		LogU.add(item.getQty());
		LogU.add(item.getPurchasedprice());
		LogU.add(item.getSellingPrice());
		LogU.add(item.getNetprice());
		LogU.add(item.getTaxpercentage());
		LogU.add(item.getIsactiveitem());
		LogU.add(item.getUserDtls()==null? 0 : (item.getUserDtls().getUserdtlsid()==null? 0 : item.getUserDtls().getUserdtlsid()));
		LogU.add(item.getItemid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to purchaseditem : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return item;
	}
	
	public void updateData(){
		String sql = "UPDATE purchaseditem SET "
				+ "datesold=?,"
				+ "productname=?,"
				+ "productbrand=?,"
				+ "uomSymbol=?,"
				+ "qty=?,"
				+ "purchasedprice=?,"
				+ "sellingPrice=?,"
				+ "netprice=?,"
				+ "taxpercentage=?,"
				+ "isactiveitem=?,"
				+ "userdtlsid=? " 
				+ " WHERE itemid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table purchaseditem");
		
		ps.setString(1, getDatesold());
		ps.setString(2, getProductName());
		ps.setString(3, getProductbrand());
		ps.setString(4, getUomSymbol());
		ps.setDouble(5, getQty());
		ps.setBigDecimal(6, getPurchasedprice());
		ps.setBigDecimal(7, getSellingPrice());
		ps.setBigDecimal(8, getNetprice());
		ps.setDouble(9, getTaxpercentage());
		ps.setInt(10, getIsactiveitem());
		ps.setLong(11, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(12, getItemid());
		
		LogU.add(getDatesold());
		LogU.add(getProductName());
		LogU.add(getProductbrand());
		LogU.add(getUomSymbol());
		LogU.add(getQty());
		LogU.add(getPurchasedprice());
		LogU.add(getSellingPrice());
		LogU.add(getNetprice());
		LogU.add(getTaxpercentage());
		LogU.add(getIsactiveitem());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getItemid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to purchaseditem : " + s.getMessage());
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
		sql="SELECT itemid FROM purchaseditem  ORDER BY itemid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("itemid");
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
		ps = conn.prepareStatement("SELECT itemid FROM purchaseditem WHERE itemid=?");
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
		String sql = "UPDATE purchaseditem set isactiveitem=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE itemid=?";
		
		String[] params = new String[1];
		params[0] = getItemid()+"";
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
		}catch(SQLException s){
			s.printStackTrace();
		}
		
	}
	
	public ProductProperties getProductProperties() {
		return productProperties;
	}
	public void setProductProperties(ProductProperties productProperties) {
		this.productProperties = productProperties;
	}
	public double getQty() {
		return qty;
	}
	public void setQty(double qty) {
		this.qty = qty;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getUomSymbol() {
		return uomSymbol;
	}
	public void setUomSymbol(String uomSymbol) {
		this.uomSymbol = uomSymbol;
	}
	@Deprecated
	public long getProductId() {
		return productId;
	}
	@Deprecated
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public long getItemid() {
		return itemid;
	}
	public void setItemid(long itemid) {
		this.itemid = itemid;
	}
	public String getDatesold() {
		return datesold;
	}
	public void setDatesold(String datesold) {
		this.datesold = datesold;
	}
	public String getProductbrand() {
		return productbrand;
	}
	public void setProductbrand(String productbrand) {
		this.productbrand = productbrand;
	}
	public int getIsactiveitem() {
		return isactiveitem;
	}
	public void setIsactiveitem(int isactiveitem) {
		this.isactiveitem = isactiveitem;
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

	public Transactions getTransactions() {
		return transactions;
	}

	public void setTransactions(Transactions transactions) {
		this.transactions = transactions;
	}

	public BigDecimal getPurchasedprice() {
		return purchasedprice;
	}

	public void setPurchasedprice(BigDecimal purchasedprice) {
		this.purchasedprice = purchasedprice;
	}

	public BigDecimal getNetprice() {
		return netprice;
	}

	public void setNetprice(BigDecimal netprice) {
		this.netprice = netprice;
	}

	public double getTaxpercentage() {
		return taxpercentage;
	}

	public void setTaxpercentage(double taxpercentage) {
		this.taxpercentage = taxpercentage;
	}
	
	public boolean isIsBetweenDate() {
		return isBetweenDate;
	}

	public void setIsBetweenDate(boolean isBetweenDate) {
		this.isBetweenDate = isBetweenDate;
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

	public BigDecimal getIncomesPlusVat() {
		return incomesPlusVat;
	}

	public void setIncomesPlusVat(BigDecimal incomesPlusVat) {
		this.incomesPlusVat = incomesPlusVat;
	}

	public BigDecimal getIncomesMinusVat() {
		return incomesMinusVat;
	}

	public void setIncomesMinusVat(BigDecimal incomesMinusVat) {
		this.incomesMinusVat = incomesMinusVat;
	}

	public BigDecimal getCapital() {
		return capital;
	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public BigDecimal getSales() {
		return sales;
	}

	public void setSales(BigDecimal sales) {
		this.sales = sales;
	}

	public StoreProduct getStoreProduct() {
		return storeProduct;
	}

	public void setStoreProduct(StoreProduct storeProduct) {
		this.storeProduct = storeProduct;
	}

	public AddOnStore getAddOnStore() {
		return addOnStore;
	}

	public void setAddOnStore(AddOnStore addOnStore) {
		this.addOnStore = addOnStore;
	}

	public static void main(String[] args) {
		
		PurchasedItem item = new PurchasedItem();
		
		try{item.setItemid(1);}catch(NullPointerException e){}
		try{item.setDatesold(DateUtils.getCurrentDateYYYYMMDD());}catch(NullPointerException e){}
		try{item.setProductName("Test 1");}catch(NullPointerException e){}
		try{item.setProductbrand("Test Brand");}catch(NullPointerException e){}
		try{item.setUomSymbol("pcs");}catch(NullPointerException e){}
		try{item.setQty(1);}catch(NullPointerException e){}
		try{item.setPurchasedprice(new BigDecimal("100"));}catch(NullPointerException e){}
		try{item.setSellingPrice(new BigDecimal("100"));}catch(NullPointerException e){}
		try{item.setNetprice(new BigDecimal("100"));}catch(NullPointerException e){}
		try{item.setTaxpercentage(0.12);}catch(NullPointerException e){}
		try{item.setIsactiveitem(1);}catch(NullPointerException e){}
		
		Transactions trans = new Transactions();
		trans.setTransid(1);
		item.setTransactions(trans);
		
		UserDtls user = new UserDtls();
		user.setUserdtlsid(1l);
		item.setUserDtls(user);
		
		Product prod = new Product();
		prod.setProdid(1);
		item.setProduct(prod);
		
		//item.save();
		
		item = new PurchasedItem();
		item.setItemid(1);
		item.setIsactiveitem(1);
		
		for(PurchasedItem i : PurchasedItem.retrieve(item)){
			System.out.println(i.getProductName());
		}
		
		
	}
	
}
