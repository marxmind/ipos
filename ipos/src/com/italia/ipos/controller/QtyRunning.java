package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.enm.ProductStatus;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 10/08/2016
 * @version 1.0
 */
public class QtyRunning {

	private long qtyrunid;
	private String qtyrundate;
	private double qtyhold;
	private int qtystatus;
	private String qtyremarks;
	private int isqtyactive;
	
	private ProductRunning productRunning;
	private Product product;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	public QtyRunning(){}
	
	public QtyRunning(
			long qtyrunid,
			String qtyrundate,
			double qtyhold,
			int qtystatus,
			String qtyremarks,
			int isqtyactive,
			ProductRunning productRunning,
			Product product,
			UserDtls userDtls
			){
		this.qtyrunid = qtyrunid;
		this.qtyrundate = qtyrundate;
		this.qtyhold = qtyhold;
		this.qtystatus = qtystatus;
		this.qtyremarks = qtyremarks;
		this.isqtyactive = isqtyactive;
		this.productRunning = productRunning;
		this.product = product;
		this.userDtls = userDtls;
	}
	
	public static String productSQL(String tablename,QtyRunning prod){
		String sql= " AND "+ tablename +".isqtyactive=" + prod.getIsqtyactive();
		if(prod!=null){
			if(prod.getQtyrunid()!=0){
				sql += " AND "+ tablename +".qtyrunid=" + prod.getQtyrunid();
			}
			if(prod.getQtyrundate()!=null){
				sql += " AND "+ tablename +".qtyrundate like '%" + prod.getQtyrundate() +"%'";
			}
			if(prod.getQtyremarks()!=null){
				sql += " AND "+ tablename +".qtyremarks like '%" + prod.getQtyremarks() +"%'";
			}
			if(prod.getQtyhold()!=0){
				sql += " AND "+ tablename +".qtyhold=" + prod.getQtyhold();
			}
			if(prod.getQtystatus()!=0){
				sql += " AND "+ tablename +".qtystatus=" + prod.getQtystatus();
			}
			if(prod.getProductRunning()!=null){
				if(prod.getProductRunning().getRunid()!=0){
					sql += " AND "+ tablename +".runid=" + prod.getProductRunning().getRunid();
				}
			}
			
			if(prod.getProduct()!=null){
				if(prod.getProduct().getProdid()!=0){
					sql += " AND "+ tablename +".prodid=" + prod.getProduct().getProdid();
				}
			}
			
			if(prod.getUserDtls()!=null){
				if(prod.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + prod.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		return sql;
	}
	
	/**
	 * 
	 * @param obj[Product][UserDtls]
	 * @return list of QtyRunning
	 */
	public static List<QtyRunning> retrieve(Object ...obj){
		List<QtyRunning> runs = Collections.synchronizedList(new ArrayList<QtyRunning>());
		String runTable = "rund";
		String prodTable = "prod";
		String qtyTable = "qty";
		//String userTable = "usr";
		String sql = "SELECT * FROM  qtyrunning "+ qtyTable +", productrunning "+ runTable  +",product " + prodTable +
				" WHERE " + qtyTable + ".runid = " + runTable + ".runid AND "+ qtyTable + ".prodid=" +prodTable + ".prodid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof QtyRunning){
				sql += productSQL(qtyTable,(QtyRunning)obj[i]);
			}
			if(obj[i] instanceof ProductRunning){
				sql += ProductRunning.productSQL(runTable,(ProductRunning)obj[i]);
			}
			if(obj[i] instanceof Product){
				sql += Product.productSQL(prodTable,(Product)obj[i]);
			}
			/*if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}*/
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
			
			QtyRunning qty = new QtyRunning();
			try{qty.setQtyrunid(rs.getLong("qtyrunid"));}catch(NullPointerException e){}
			try{qty.setQtyrundate(rs.getString("qtyrundate"));}catch(NullPointerException e){}
			try{qty.setQtyhold(rs.getDouble("qtyhold"));}catch(NullPointerException e){}
			try{qty.setQtystatus(rs.getInt("qtystatus"));}catch(NullPointerException e){}
			try{qty.setQtyremarks(rs.getString("qtyremarks"));}catch(NullPointerException e){}
			try{qty.setIsqtyactive(rs.getInt("isqtyactive"));}catch(NullPointerException e){}
			try{qty.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			ProductRunning prun = new ProductRunning();
			try{prun.setRunid(rs.getLong("runid"));}catch(NullPointerException e){}
			try{prun.setRundate(rs.getString("rundate"));}catch(NullPointerException e){}
			try{prun.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{prun.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{prun.setRunstatus(rs.getInt("runstatus"));}catch(NullPointerException e){}
			try{prun.setIsrunactive(rs.getInt("isrunactive"));}catch(NullPointerException e){}
			try{prun.setRunremarks(rs.getString("runremarks"));}catch(NullPointerException e){}
			try{prun.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			qty.setProductRunning(prun);
			
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
			qty.setProduct(prod);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			qty.setUserDtls(user);
			
			runs.add(qty);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return runs;
	}
	
	/**
	 * 
	 * @param obj[Product][UserDtls]
	 * @return list of QtyRunning
	 */
	public static QtyRunning retrieve(String qtyrunid){
		QtyRunning qty = new QtyRunning();
		String runTable = "rund";
		String prodTable = "prod";
		String qtyTable = "qty";
		String sql = "SELECT * FROM  qtyrunning "+ qtyTable +", productrunning "+ runTable  +",product " + prodTable +
				" WHERE " + qtyTable + ".runid = " + runTable + ".runid AND "+ qtyTable + ".prodid=" +prodTable + ".prodid AND " +
				qtyTable + ".qtyrunid="+qtyrunid;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{qty.setQtyrunid(rs.getLong("qtyrunid"));}catch(NullPointerException e){}
			try{qty.setQtyrundate(rs.getString("qtyrundate"));}catch(NullPointerException e){}
			try{qty.setQtyhold(rs.getDouble("qtyhold"));}catch(NullPointerException e){}
			try{qty.setQtystatus(rs.getInt("qtystatus"));}catch(NullPointerException e){}
			try{qty.setQtyremarks(rs.getString("qtyremarks"));}catch(NullPointerException e){}
			try{qty.setIsqtyactive(rs.getInt("isqtyactive"));}catch(NullPointerException e){}
			try{qty.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
			ProductRunning prun = new ProductRunning();
			try{prun.setRunid(rs.getLong("runid"));}catch(NullPointerException e){}
			try{prun.setRundate(rs.getString("rundate"));}catch(NullPointerException e){}
			try{prun.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{prun.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{prun.setRunstatus(rs.getInt("runstatus"));}catch(NullPointerException e){}
			try{prun.setIsrunactive(rs.getInt("isrunactive"));}catch(NullPointerException e){}
			try{prun.setRunremarks(rs.getString("runremarks"));}catch(NullPointerException e){}
			try{prun.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			qty.setProductRunning(prun);
			
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
			qty.setProduct(prod);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			/*try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}*/
			qty.setUserDtls(user);
				
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return qty;
	}
	
	public static QtyRunning save(QtyRunning qty){
		if(qty!=null){
			
			long id = QtyRunning.getInfo(qty.getQtyrunid() ==0? QtyRunning.getLatestId()+1 : qty.getQtyrunid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				qty = QtyRunning.insertData(qty, "1");
			}else if(id==2){
				LogU.add("update Data ");
				qty = QtyRunning.updateData(qty);
			}else if(id==3){
				LogU.add("added new Data ");
				qty = QtyRunning.insertData(qty, "3");
			}
			
		}
		return qty;
	}
	
	public void save(){
			
			long id = getInfo(getQtyrunid() ==0? getLatestId()+1 : getQtyrunid());
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
	
	public static QtyRunning insertData(QtyRunning qty, String type){
		String sql = "INSERT INTO qtyrunning ("
				+ "qtyrunid,"
				+ "qtyrundate,"
				+ "qtyhold,"
				+ "qtystatus,"
				+ "qtyremarks,"
				+ "isqtyactive,"
				+ "runid,"
				+ "prodid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table qtyrunning");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			qty.setQtyrunid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			qty.setQtyrunid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, qty.getQtyrundate());
		ps.setDouble(3, qty.getQtyhold());
		ps.setInt(4, qty.getQtystatus());
		ps.setString(5, qty.getQtyremarks());
		ps.setInt(6, qty.getIsqtyactive());
		ps.setLong(7, qty.getProductRunning()==null? 0 : qty.getProductRunning().getRunid());
		ps.setLong(8, qty.getProduct()==null? 0 : qty.getProduct().getProdid());
		ps.setLong(9, qty.getUserDtls()==null? 0 : (qty.getUserDtls().getUserdtlsid()==null? 0 : qty.getUserDtls().getUserdtlsid()));
		
		LogU.add(qty.getQtyrundate());
		LogU.add(qty.getQtyhold());
		LogU.add(qty.getQtystatus());
		LogU.add(qty.getQtyremarks());
		LogU.add(qty.getIsqtyactive());
		LogU.add(qty.getProductRunning()==null? 0 : qty.getProductRunning().getRunid());
		LogU.add(qty.getProduct()==null? 0 : qty.getProduct().getProdid());
		LogU.add(qty.getUserDtls()==null? 0 : (qty.getUserDtls().getUserdtlsid()==null? 0 : qty.getUserDtls().getUserdtlsid()));
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to qtyrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return qty;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO qtyrunning ("
				+ "qtyrunid,"
				+ "qtyrundate,"
				+ "qtyhold,"
				+ "qtystatus,"
				+ "qtyremarks,"
				+ "isqtyactive,"
				+ "runid,"
				+ "prodid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table qtyrunning");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setQtyrunid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setQtyrunid(id);
			LogU.add("id: " + id);
		}
		ps.setString(2, getQtyrundate());
		ps.setDouble(3, getQtyhold());
		ps.setInt(4, getQtystatus());
		ps.setString(5, getQtyremarks());
		ps.setInt(6, getIsqtyactive());
		ps.setLong(7, getProductRunning()==null? 0 : getProductRunning().getRunid());
		ps.setLong(8, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(9, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add(getQtyrundate());
		LogU.add(getQtyhold());
		LogU.add(getQtystatus());
		LogU.add(getQtyremarks());
		LogU.add(getIsqtyactive());
		LogU.add(getProductRunning()==null? 0 : getProductRunning().getRunid());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to qtyrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static QtyRunning updateData(QtyRunning qty){
		String sql = "UPDATE qtyrunning SET "
				+ "qtyrundate=?,"
				+ "qtyhold=?,"
				+ "qtystatus=?,"
				+ "qtyremarks=?,"
				+ "isqtyactive=?,"
				+ "runid=?,"
				+ "prodid=?,"
				+ "userdtlsid=? " 
				+ " WHERE qtyrunid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table qtyrunning");
		
		ps.setString(1, qty.getQtyrundate());
		ps.setDouble(2, qty.getQtyhold());
		ps.setInt(3, qty.getQtystatus());
		ps.setString(4, qty.getQtyremarks());
		ps.setInt(5, qty.getIsqtyactive());
		ps.setLong(6, qty.getProductRunning()==null? 0 : qty.getProductRunning().getRunid());
		ps.setLong(7, qty.getProduct()==null? 0 : qty.getProduct().getProdid());
		ps.setLong(8, qty.getUserDtls()==null? 0 : (qty.getUserDtls().getUserdtlsid()==null? 0 : qty.getUserDtls().getUserdtlsid()));
		ps.setLong(9, qty.getQtyrunid());
		
		LogU.add(qty.getQtyrundate());
		LogU.add(qty.getQtyhold());
		LogU.add(qty.getQtystatus());
		LogU.add(qty.getQtyremarks());
		LogU.add(qty.getIsqtyactive());
		LogU.add(qty.getProductRunning()==null? 0 : qty.getProductRunning().getRunid());
		LogU.add(qty.getProduct()==null? 0 : qty.getProduct().getProdid());
		LogU.add(qty.getUserDtls()==null? 0 : (qty.getUserDtls().getUserdtlsid()==null? 0 : qty.getUserDtls().getUserdtlsid()));
		LogU.add(qty.getQtyrunid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to qtyrunning : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return qty;
	}
	
	public void updateData(){
		String sql = "UPDATE qtyrunning SET "
				+ "qtyrundate=?,"
				+ "qtyhold=?,"
				+ "qtystatus=?,"
				+ "qtyremarks=?,"
				+ "isqtyactive=?,"
				+ "runid=?,"
				+ "prodid=?,"
				+ "userdtlsid=? " 
				+ " WHERE qtyrunid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table qtyrunning");
		
		ps.setString(1, getQtyrundate());
		ps.setDouble(2, getQtyhold());
		ps.setInt(3, getQtystatus());
		ps.setString(4, getQtyremarks());
		ps.setInt(5, getIsqtyactive());
		ps.setLong(6, getProductRunning()==null? 0 : getProductRunning().getRunid());
		ps.setLong(7, getProduct()==null? 0 : getProduct().getProdid());
		ps.setLong(8, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(9, getQtyrunid());
		
		LogU.add(getQtyrundate());
		LogU.add(getQtyhold());
		LogU.add(getQtystatus());
		LogU.add(getQtyremarks());
		LogU.add(getIsqtyactive());
		LogU.add(getProductRunning()==null? 0 : getProductRunning().getRunid());
		LogU.add(getProduct()==null? 0 : getProduct().getProdid());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getQtyrunid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to qtyrunning : " + s.getMessage());
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
		sql="SELECT qtyrunid FROM qtyrunning  ORDER BY qtyrunid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("qtyrunid");
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
		ps = conn.prepareStatement("SELECT qtyrunid FROM qtyrunning WHERE qtyrunid=?");
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
		String sql = "UPDATE qtyrunning set isqtyactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE qtyrunid=?";
		
		String[] params = new String[1];
		params[0] = getQtyrunid()+"";
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
	
	public static Map<Long, QtyRunning> addQtyRunning(Map<Long, QtyRunning> mapQty,long productid, ProductRunning productRunning, Product product, double qty){
		
		QtyRunning qtyrun = new QtyRunning();
		if(mapQty!=null && mapQty.size()>0){
			
			if(mapQty.containsKey(productid)){
				qtyrun = mapQty.get(productid);
				mapQty.remove(productid);
			}
		}
		
		qtyrun.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
		qtyrun.setQtyhold(qty);
		qtyrun.setIsqtyactive(1);
		qtyrun.setQtystatus(ProductStatus.ON_QUEUE.getId());
		qtyrun.setQtyremarks(ProductStatus.ON_QUEUE.getName());
		qtyrun.setProductRunning(productRunning);
		qtyrun.setProduct(product);
		qtyrun.setUserDtls(Login.getUserLogin().getUserDtls());
		qtyrun = QtyRunning.save(qtyrun);
		mapQty.put(productid, qtyrun);
		
		return mapQty;
	}
	
	public static Map<Long, QtyRunning> addQtyRunningDispense(Map<Long, QtyRunning> mapQty,long productid, ProductRunning productRunning, Product product, double qty){
		
		QtyRunning qtyrun = new QtyRunning();
		if(mapQty!=null && mapQty.size()>0){
			
			if(mapQty.containsKey(productid)){
				qtyrun = mapQty.get(productid);
				mapQty.remove(productid);
			}
		}
		
		qtyrun.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
		qtyrun.setQtyhold(qty);
		qtyrun.setIsqtyactive(1);
		qtyrun.setQtystatus(ProductStatus.DISPENSE.getId());
		qtyrun.setQtyremarks(ProductStatus.DISPENSE.getName());
		qtyrun.setProductRunning(productRunning);
		qtyrun.setProduct(product);
		qtyrun.setUserDtls(Login.getUserLogin().getUserDtls());
		qtyrun = QtyRunning.save(qtyrun);
		mapQty.put(productid, qtyrun);
		
		return mapQty;
	}
	
	public long getQtyrunid() {
		return qtyrunid;
	}
	public void setQtyrunid(long qtyrunid) {
		this.qtyrunid = qtyrunid;
	}
	public String getQtyrundate() {
		return qtyrundate;
	}
	public void setQtyrundate(String qtyrundate) {
		this.qtyrundate = qtyrundate;
	}
	public double getQtyhold() {
		return qtyhold;
	}
	public void setQtyhold(double qtyhold) {
		this.qtyhold = qtyhold;
	}
	public int getQtystatus() {
		return qtystatus;
	}
	public void setQtystatus(int qtystatus) {
		this.qtystatus = qtystatus;
	}
	public String getQtyremarks() {
		return qtyremarks;
	}
	public void setQtyremarks(String qtyremarks) {
		this.qtyremarks = qtyremarks;
	}
	public int getIsqtyactive() {
		return isqtyactive;
	}
	public void setIsqtyactive(int isqtyactive) {
		this.isqtyactive = isqtyactive;
	}
	public ProductRunning getProductRunning() {
		return productRunning;
	}
	public void setProductRunning(ProductRunning productRunning) {
		this.productRunning = productRunning;
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
	
	public static void main(String[] args) {
		
		QtyRunning qty = new QtyRunning();
		qty.setQtyrunid(1);
		qty.setQtyrundate(DateUtils.getCurrentDateYYYYMMDD());
		qty.setQtyhold(10);
		qty.setQtystatus(2);
		qty.setQtyremarks("dispense");
		qty.setIsqtyactive(1);
		
		ProductRunning r = new ProductRunning();
		r.setRunid(1);
		qty.setProductRunning(r);
		
		Product product = new Product();
		product.setProdid(1);
		qty.setProduct(product);
		
		UserDtls user = new UserDtls();
		user.setUserdtlsid(1l);
		qty.setUserDtls(user);
		
		//qty.save();
		
		qty = new QtyRunning();
		qty.setQtyrunid(1);
		qty.setIsqtyactive(1);
		
		for(QtyRunning q : QtyRunning.retrieve(qty)){
			System.out.println(q.getQtyremarks());
		}
		
	}
	
}
