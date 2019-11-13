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

import com.italia.ipos.application.ClientInfo;
import com.italia.ipos.bean.SessionBean;
import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.session.IBean;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;



/**
 * 
 * @author mark italia
 * @since 09/27/2016
 * @version 1.0
 *
 */
public class Login {
	
	private Long logid;
	private String username;
	private String password;
	private String lastlogin;
	private UserAccessLevel accessLevel;
	private int isOnline;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private String logintime;
	private String clientip;
	private String clientbrowser;
	private int isActive;
	
	public Login(){}
	
	public Login(
			Long logid,
			String username,
			String password,
			String lastlogin,
			UserAccessLevel accessLevel,
			int isOnline,
			UserDtls userDtls,
			String logintime,
			String clientip,
			String clientbrowser,
			int isActive
			){
		this.logid = logid;
		this.username = username;
		this.password = password;
		this.lastlogin = lastlogin;
		this.accessLevel = accessLevel;
		this.isOnline = isOnline;
		this.userDtls = userDtls;
		this.logintime = logintime;
		this.clientip = clientip;
		this.clientbrowser = clientbrowser;
		this.isActive = isActive;
	}
	
	public static String loginSQL(String tablename,Login in){
		String sql="";
		if(in!=null){
			
			if(in.getLogid()!=null){
				sql += " AND "+ tablename +".logid=" + in.getLogid();
			}
			if(in.getUsername()!=null){
				sql += " AND "+ tablename +".username='" + in.getUsername() +"'";
			}
			if(in.getPassword()!=null){
				sql += " AND "+ tablename +".password='" + in.getPassword()+"'";
			}
			if(in.getLastlogin()!=null){
				sql += " AND "+ tablename +".lastlogin='" + in.getLastlogin()+"'";
			}
			if(in.getIsOnline()!=0){
				sql += " AND "+ tablename +".isOnline=" + in.getIsOnline();
			}
			if(in.getLogintime()!=null){
				sql += " AND "+ tablename +".logintime='" + in.getLogintime()+"'";
			}
			if(in.getIsActive()!=0){
				sql += " AND "+ tablename +".isactive=" + in.getIsActive();
			}
			if(in.getClientip()!=null){
				sql += " AND "+ tablename +".clientip='" + in.getClientip()+"'";
			}
			if(in.getClientbrowser()!=null){
				sql += " AND "+ tablename +".clientbrowser='" + in.getClientbrowser()+"'";
			}
			
			if(in.getAccessLevel()!=null){
				if(in.getAccessLevel().getUseraccesslevelid()!=0){
					sql += " AND "+ tablename +".accessLevel=" + in.getAccessLevel().getUseraccesslevelid();
				}
			}
			
			if(in.getUserDtls()!=null){
				if(in.getUserDtls().getUserdtlsid()!=null){
					sql += " AND "+ tablename +".userdtlsid=" + in.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		
		return sql;
	}
	
	public static List<Login> retrieve(Object... obj){
		List<Login> ins = Collections.synchronizedList(new ArrayList<Login>());
		String loginTable = "log";
		String accessLvlTable = "lvl";
		String userTable = "usr";
		String sql = "SELECT * FROM login "+ loginTable +" , useraccesslevel "+ 
		accessLvlTable +", userdtls "+ userTable +" WHERE "+ loginTable +
		".useraccesslevelid = "+ accessLvlTable +".useraccesslevelid AND "+ loginTable +".userdtlsid="+ userTable +".userdtlsid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof ProductProperties){
				sql += loginSQL(loginTable,(Login)obj[i]);
			}
			if(obj[i] instanceof ProductCategory){
				sql += UserAccessLevel.accessLevelSQL(accessLvlTable,(UserAccessLevel)obj[i]);
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
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Login in = new Login();
			try{in.setLogid(rs.getLong("logid"));}catch(NullPointerException e){}
			try{in.setUsername(rs.getString("username"));}catch(NullPointerException e){}
			try{in.setPassword(rs.getString("password"));}catch(NullPointerException e){}
			try{in.setLastlogin(rs.getString("lastlogin"));}catch(NullPointerException e){}
			try{in.setIsOnline(rs.getInt("isOnline"));}catch(NullPointerException e){}
			try{in.setLogintime(rs.getString("logintime"));}catch(NullPointerException e){}
			try{in.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{in.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{in.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
			
			UserAccessLevel level = new UserAccessLevel();
			try{level.setUseraccesslevelid(rs.getInt("useraccesslevelid"));}catch(NullPointerException e){}
			try{level.setLevel(rs.getInt("level"));}catch(NullPointerException e){}
			try{level.setName(rs.getString("levelname"));}catch(NullPointerException e){}
			try{in.setAccessLevel(level);}catch(NullPointerException e){}
			
			
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
			try{in.setUserDtls(user);}catch(NullPointerException e){}
			
			ins.add(in);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return ins;
	}
	
	public static List<Login> retrieve(String sql, String[] params){
		List<Login> ins = Collections.synchronizedList(new ArrayList<Login>());
		
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
			
			Login in = new Login();
			try{in.setLogid(rs.getLong("logid"));}catch(NullPointerException e){}
			try{in.setUsername(rs.getString("username"));}catch(NullPointerException e){}
			try{in.setPassword(rs.getString("password"));}catch(NullPointerException e){}
			try{in.setLastlogin(rs.getString("lastlogin"));}catch(NullPointerException e){}
			try{in.setAccessLevel(UserAccessLevel.userAccessLevel(rs.getString("useraccesslevelid")));}catch(NullPointerException e){}
			try{in.setIsOnline(rs.getInt("isOnline"));}catch(NullPointerException e){}
			try{UserDtls user = new UserDtls();
			user.setUserdtlsid(rs.getLong("userdtlsid"));
			in.setUserDtls(user);}catch(NullPointerException e){}
			try{in.setLogintime(rs.getString("logintime"));}catch(NullPointerException e){}
			try{in.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{in.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{in.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			ins.add(in);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return ins;
	}
	
	public static Login login(String logid){
		Login in = new Login();
		/*String sql = "SELECT * FROM login WHERE logid=?";
		String[] params = new String[1];
		params[0] = logid;
		try{
			in = Login.retrieve(sql, params).get(0);
		}catch(Exception e){}*/
		in.setLogid(Long.valueOf(logid));
		in = Login.retrieve(in).get(0);
		
		return in;
	}
	
	public static boolean validate(String sql, String[] params){
		
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
			
			if(rs.next()){
				return true;
			}
			rs.close();
			ps.close();
			ConnectDB.close(conn);
			}catch(Exception e){}
		return false;
	}
	
	public static void save(Login in){
		if(in!=null){
			
			long id = Login.getInfo(in.getLogid()==null? Login.getLatestId()+1 : in.getLogid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				Login.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				Login.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				Login.insertData(in, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getInfo(getLogid()==null? getLatestId()+1 : getLogid());
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
	
	public static Login insertData(Login in, String type){
		String sql = "INSERT INTO login ("
				+ "logid,"
				+ "username,"
				+ "password,"
				+ "lastlogin,"
				+ "useraccesslevelid,"
				+ "isOnline,"
				+ "userdtlsid,"
				+ "logintime,"
				+ "clientip,"
				+ "clientbrowser,"
				+ "isactive)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table login");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			in.setLogid(Long.valueOf(id));
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			in.setLogid(Long.valueOf(id));
			LogU.add("logid: " + id);
		}
		
		ps.setString(2, in.getUsername());
		ps.setString(3, in.getPassword());
		ps.setString(4, in.getLastlogin());
		ps.setInt(5, in.getAccessLevel().getUseraccesslevelid());
		ps.setInt(6, in.getIsOnline());
		ps.setLong(7, in.getUserDtls().getUserdtlsid());
		ps.setString(8, in.getLogintime());
		ps.setString(9, in.getClientip());
		ps.setString(10, in.getClientbrowser());
		ps.setInt(11, in.getIsActive());
		
		LogU.add(in.getUsername());
		LogU.add(in.getPassword());
		LogU.add(in.getLastlogin());
		LogU.add(in.getAccessLevel().getUseraccesslevelid());
		LogU.add(in.getIsOnline());
		LogU.add(in.getUserDtls().getUserdtlsid());
		LogU.add(in.getLogintime());
		LogU.add(in.getClientip());
		LogU.add(in.getClientbrowser());
		LogU.add(in.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to login : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO login ("
				+ "logid,"
				+ "username,"
				+ "password,"
				+ "lastlogin,"
				+ "useraccesslevelid,"
				+ "isOnline,"
				+ "userdtlsid,"
				+ "logintime,"
				+ "clientip,"
				+ "clientbrowser,"
				+ "isactive)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table login");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setLogid(Long.valueOf(id));
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setLogid(Long.valueOf(id));
			LogU.add("logid: " + id);
		}
		
		ps.setString(2, getUsername());
		ps.setString(3, getPassword());
		ps.setString(4, getLastlogin());
		ps.setInt(5, getAccessLevel().getUseraccesslevelid());
		ps.setInt(6, getIsOnline());
		ps.setLong(7, getUserDtls().getUserdtlsid());
		ps.setString(8, getLogintime());
		ps.setString(9, getClientip());
		ps.setString(10, getClientbrowser());
		ps.setInt(11, getIsActive());
		
		LogU.add(getUsername());
		LogU.add(getPassword());
		LogU.add(getLastlogin());
		LogU.add(getAccessLevel().getUseraccesslevelid());
		LogU.add(getIsOnline());
		LogU.add(getUserDtls().getUserdtlsid());
		LogU.add(getLogintime());
		LogU.add(getClientip());
		LogU.add(getClientbrowser());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to login : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static Login updateData(Login in){
		String sql = "UPDATE login SET "
				+ "username=?,"
				+ "password=?,"
				+ "lastlogin=?,"
				+ "useraccesslevelid=?,"
				+ "isOnline=?,"
				+ "userdtlsid=?,"
				+ "logintime=?,"
				+ "clientip=?,"
				+ "clientbrowser=?,"
				+ "isactive=?" 
				+ " WHERE logid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("update data into table login");
		
		ps.setString(1, in.getUsername());
		ps.setString(2, in.getPassword());
		ps.setString(3, in.getLastlogin());
		ps.setInt(4, in.getAccessLevel().getUseraccesslevelid());
		ps.setInt(5, in.getIsOnline());
		ps.setLong(6, in.getUserDtls().getUserdtlsid());
		ps.setString(7, in.getLogintime());
		ps.setString(8, in.getClientip());
		ps.setString(9, in.getClientbrowser());
		ps.setInt(10, in.getIsActive());
		ps.setLong(11, in.getLogid());
		 
		LogU.add(in.getUsername());
		LogU.add(in.getPassword());
		LogU.add(in.getLastlogin());
		LogU.add(in.getAccessLevel().getUseraccesslevelid());
		LogU.add(in.getIsOnline());
		LogU.add(in.getUserDtls().getUserdtlsid());
		LogU.add(in.getLogintime());
		LogU.add(in.getClientip());
		LogU.add(in.getClientbrowser());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to login : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public void updateData(){
		String sql = "UPDATE login SET "
				+ "username=?,"
				+ "password=?,"
				+ "lastlogin=?,"
				+ "useraccesslevelid=?,"
				+ "isOnline=?,"
				+ "userdtlsid=?,"
				+ "logintime=?,"
				+ "clientip=?,"
				+ "clientbrowser=?,"
				+ "isactive=?" 
				+ " WHERE logid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("update data into table login");
		
		ps.setString(1,getUsername());
		ps.setString(2,getPassword());
		ps.setString(3,getLastlogin());
		ps.setInt(4,getAccessLevel().getUseraccesslevelid());
		ps.setInt(5,getIsOnline());
		ps.setLong(6,getUserDtls().getUserdtlsid());
		ps.setString(7,getLogintime());
		ps.setString(8,getClientip());
		ps.setString(9,getClientbrowser());
		ps.setInt(10, getIsActive());
		ps.setLong(11, getLogid());
		
		LogU.add(getUsername());
		LogU.add(getPassword());
		LogU.add(getLastlogin());
		LogU.add(getAccessLevel().getUseraccesslevelid());
		LogU.add(getIsOnline());
		LogU.add(getUserDtls().getUserdtlsid());
		LogU.add(getLogintime());
		LogU.add(getClientip());
		LogU.add(getClientbrowser());
		LogU.add(getLogid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to login : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static boolean checkUserStatus(){
		
		Login in = getUserLogin();
		LogU.add("Checking user status...");
		//check browser, user ip address and online status
		if(ClientInfo.getBrowserName().trim().equalsIgnoreCase(in.getClientbrowser().trim())
				&& ClientInfo.getClientIP().trim().equalsIgnoreCase(in.getClientip().trim())
					&& in.getIsOnline()==1
				){
			System.out.println("user is valid");
			return true;
			
		}else{
			IBean.removeBean();// force the user to log out in the system
			LogU.add("user is invalid. System invalidating the process...");
		}
		
		return false;
	}
	
	public static Login getUserLogin(){
		String username = "error";
		String userid = "error";
		Login in = new Login();
		try{
			HttpSession session = SessionBean.getSession();
			username = session.getAttribute("username").toString();
			userid = session.getAttribute("userid").toString();
			
			String sql = "SELECT * FROM login WHERE logid=? AND username=?";
			String[] params = new String[2];
			params[0] = userid;
			params[1] = username;
			in = Login.retrieve(sql, params).get(0);
			
			in.setUserDtls(UserDtls.user(in.getUserDtls().getUserdtlsid()+""));
			
		}catch(Exception e){}
		return in;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT logid FROM login  ORDER BY logid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("logid");
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
		ps = conn.prepareStatement("SELECT logid FROM login WHERE logid=?");
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
		String sql = "UPDATE login set isactive=0 WHERE logid=?";
		
		if(!retain){
			sql = "DELETE FROM login WHERE logid=?";
		}
		
		String[] params = new String[1];
		params[0] = getLogid()+"";
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
	
	public Long getLogid() {
		return logid;
	}
	public void setLogid(Long logid) {
		this.logid = logid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLastlogin() {
		return lastlogin;
	}
	public void setLastlogin(String lastlogin) {
		this.lastlogin = lastlogin;
	}
	public UserAccessLevel getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(UserAccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}
	public int getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
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

	public String getLogintime() {
		return logintime;
	}

	public void setLogintime(String logintime) {
		this.logintime = logintime;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

	public String getClientbrowser() {
		return clientbrowser;
	}

	public void setClientbrowser(String clientbrowser) {
		this.clientbrowser = clientbrowser;
	}
	
	public static void main(String[] args) {
//		/System.out.println(Login.login(1).getUsername());
		/*Login in = new Login();
		in.setLogid(2l);
		in.setUsername("babez");
		in.setPassword("babez");
		in.setLastlogin(DateUtils.getCurrentDateMMDDYYYYTIME());
		UserAccessLevel accessLevel = new UserAccessLevel();
		accessLevel.setUseraccesslevelid(1);
		accessLevel.setLevel(1);
		accessLevel.setName("Programmer");
		in.setAccessLevel(accessLevel);
		in.setIsOnline(1);
		UserDtls userDtls = new UserDtls();
		userDtls.setUserdtlsid(2l);
		in.setUserDtls(userDtls);
		in.setLogintime(DateUtils.getCurrentDateMMDDYYYYTIME());
		in.setClientip("UNKNOWN");
		in.setClientbrowser("UNKNOWN");
		in.save(in);*/
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
}
