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

import com.italia.ipos.bean.SessionBean;
import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 10/03/2016
 * @version 1.0
 */
public class ProductBrand {

	private long prodbrandid;
	private String productbrandcode;	
	private String productbrandname;
	private int isactive;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	public ProductBrand(){}
	
	public ProductBrand(
			long prodbrandid,
			String productbrandcode,	
			String productbrandname,
			int isactive,
			UserDtls userDtls
			){
		this.prodbrandid = prodbrandid;
		this.productbrandcode = productbrandcode;
		this.productbrandname = productbrandname;
		this.isactive = isactive;
		this.userDtls = userDtls;
	}
	
	public static String brandCode(){
		String code = "BRAND-000000";
		long id = getLatestId()+1;
		char[] count = String.valueOf(id).toCharArray();
		int size = count.length;
		if(size==1){
			code = "BRAND-00000"+id;
		}else if(size==2){
			code = "BRAND-0000"+id;
		}else if(size==3){
			code = "BRAND-000"+id;
		}else if(size==4){
			code = "BRAND-00"+id;
		}else if(size==5){
			code = "BRAND-0"+id;
		}else if(size==6){
			code = "BRAND-"+id;
		}
		
		return code;
	}
	
	public static String productBrandSQL(String tablename,ProductBrand prod){
		String sql="";
		if(prod!=null){
			if(prod.getProdbrandid()!=0){
				sql += " AND "+ tablename +".prodbrandid=" + prod.getProdbrandid();
			}
			if(prod.getProductbrandcode()!=null){
				sql += " AND "+ tablename +".productbrandcode like '%" + prod.getProductbrandcode() +"%'";
			}
			if(prod.getProductbrandname()!=null){
				sql += " AND "+ tablename +".productbrandname like '%" + prod.getProductbrandname() +"%'";
			}
			if(prod.getIsactive()!=0){
				sql += " AND "+ tablename +".isactive=" + prod.getIsactive();
			}
			if(prod.getUserDtls()!=null){
				if(prod.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + prod.getUserDtls().getUserdtlsid() ;
				}
			}
		}
		return sql;
	}
	
	public static List<ProductBrand> retrieve(Object... obj){
		List<ProductBrand> prods = Collections.synchronizedList(new ArrayList<ProductBrand>());
		String pgrpTable = "pbrand";
		String userTable = "usr";
		String sql = "SELECT * FROM productbrand "+ pgrpTable +", userdtls "+ userTable +" WHERE "+ pgrpTable +".userdtlsid = "+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof ProductBrand){
				sql += productBrandSQL(pgrpTable,(ProductBrand)obj[i]);
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
			ProductBrand prod = new ProductBrand();
			try{prod.setProdbrandid(rs.getLong("prodbrandid"));}catch(NullPointerException e){}
			try{prod.setProductbrandcode(rs.getString("productbrandcode"));}catch(NullPointerException e){}
			try{prod.setProductbrandname(rs.getString("productbrandname"));}catch(NullPointerException e){}
			try{prod.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
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
	
	public static ProductBrand productBrand(String productBrandId){
		ProductBrand prod = new ProductBrand();
		String pgrpTable = "pbrand";
		String userTable = "usr";
		String sql = "SELECT * FROM productbrand "+ pgrpTable +", userdtls "+ userTable +
				" WHERE "+ pgrpTable +".userdtlsid = "+ userTable +".userdtlsid AND " + pgrpTable +".prodbrandid=" + productBrandId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{prod.setProdbrandid(rs.getLong("prodbrandid"));}catch(NullPointerException e){}
			try{prod.setProductbrandcode(rs.getString("productbrandcode"));}catch(NullPointerException e){}
			try{prod.setProductbrandname(rs.getString("productbrandname"));}catch(NullPointerException e){}
			try{prod.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{prod.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			
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
	
	public static void save(ProductBrand prod){
		if(prod!=null){
			
			long id = ProductBrand.getInfo(prod.getProdbrandid() ==0? ProductBrand.getLatestId()+1 : prod.getProdbrandid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				ProductBrand.insertData(prod, "1");
			}else if(id==2){
				LogU.add("update Data ");
				ProductBrand.updateData(prod);
			}else if(id==3){
				LogU.add("added new Data ");
				ProductBrand.insertData(prod, "3");
			}
			
		}
	}
	
	public void save(){
		
			
			long id = getInfo(getProdbrandid() ==0? getLatestId()+1 : getProdbrandid());
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
	
	public static ProductBrand insertData(ProductBrand prod, String type){
		String sql = "INSERT INTO productbrand ("
				+ "prodbrandid,"
				+ "productbrandcode,"
				+ "productbrandname,"
				+ "isactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productbrand");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			prod.setProdbrandid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			prod.setProdbrandid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, prod.getProductbrandcode());
		ps.setString(3, prod.getProductbrandname());
		ps.setInt(4, prod.getIsactive());
		ps.setLong(5, prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		
		LogU.add( prod.getProductbrandcode());
		LogU.add( prod.getProductbrandname());
		LogU.add( prod.getIsactive());
		LogU.add( prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productbrand : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO productbrand ("
				+ "prodbrandid,"
				+ "productbrandcode,"
				+ "productbrandname,"
				+ "isactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productbrand");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setProdbrandid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setProdbrandid(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(2, getProductbrandcode());
		ps.setString(3, getProductbrandname());
		ps.setInt(4, getIsactive());
		ps.setLong(5, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		
		LogU.add(getProductbrandcode());
		LogU.add(getProductbrandname());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productbrand : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ProductBrand updateData(ProductBrand prod){
		String sql = "UPDATE productbrand SET "
				+ "productbrandcode=?,"
				+ "productbrandname=?,"
				+ "isactive=?,"
				+ "userdtlsid=? " 
				+ " WHERE prodbrandid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productbrand");
				
		ps.setString(1, prod.getProductbrandcode());
		ps.setString(2, prod.getProductbrandname());
		ps.setInt(3, prod.getIsactive());
		ps.setLong(4, prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		ps.setLong(5, prod.getProdbrandid());
		
		LogU.add( prod.getProductbrandcode());
		LogU.add( prod.getProductbrandname());
		LogU.add( prod.getIsactive());
		LogU.add( prod.getUserDtls()==null? 0 : (prod.getUserDtls().getUserdtlsid()==null? 0 : prod.getUserDtls().getUserdtlsid()));
		LogU.add( prod.getProdbrandid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productbrand : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return prod;
	}
	
	public void updateData(){
		String sql = "UPDATE productbrand SET "
				+ "productbrandcode=?,"
				+ "productbrandname=?,"
				+ "isactive=?,"
				+ "userdtlsid=? " 
				+ " WHERE prodbrandid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table productbrand");
				
		ps.setString(1, getProductbrandcode());
		ps.setString(2, getProductbrandname());
		ps.setInt(3, getIsactive());
		ps.setLong(4, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		ps.setLong(5, getProdbrandid());
		
		LogU.add( getProductbrandcode());
		LogU.add( getProductbrandname());
		LogU.add( getIsactive());
		LogU.add( getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==null? 0 : getUserDtls().getUserdtlsid()));
		LogU.add( getProdbrandid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to productbrand : " + s.getMessage());
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
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT prodbrandid FROM productbrand  ORDER BY prodbrandid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("prodbrandid");
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
		ps = conn.prepareStatement("SELECT prodbrandid FROM productbrand WHERE prodbrandid=?");
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
		String sql = "UPDATE productbrand set isactive=0 WHERE prodbrandid=?";
		
		String[] params = new String[1];
		params[0] = getProdbrandid()+"";
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
	
	public long getProdbrandid() {
		return prodbrandid;
	}
	public void setProdbrandid(long prodbrandid) {
		this.prodbrandid = prodbrandid;
	}
	public String getProductbrandcode() {
		return productbrandcode;
	}
	public void setProductbrandcode(String productbrandcode) {
		this.productbrandcode = productbrandcode;
	}
	public String getProductbrandname() {
		return productbrandname;
	}
	public void setProductbrandname(String productbrandname) {
		this.productbrandname = productbrandname;
	}
	public int getIsactive() {
		return isactive;
	}
	public void setIsactive(int isactive) {
		this.isactive = isactive;
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
		/*ProductBrand prod = new ProductBrand();
		prod.setProductbrandcode("BRAND-000001");
		prod.setProductbrandname("COCA COLA COMPANY INC");
		//prod.setIsactive(1);
		//prod.save();
		for(ProductBrand p : ProductBrand.retrieve(prod)){
			System.out.println(p.getProductbrandname());
		}*/
		
		System.out.println(ProductBrand.brandCode());
	}
	
}
