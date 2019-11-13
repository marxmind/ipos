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
 * @since 09/27/2016
 * @version 1.0
 *
 */
public class UserDtls {

	private Long userdtlsid;
	private String regdate;
	private String firstname;
	private String middlename;
	private String lastname;
	private String address;
	private String contactno;
	private int age;
	private int gender;
	private Login login;
	private Job job;
	private Department department;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private int isActive;
	
	public UserDtls(){}
	
	public UserDtls(
			Long userdtlsid,
			String regdate,
			String firstname,
			String middlename,
			String lastname,
			String address,
			String contactno,
			int age,
			int gender,
			Login login,
			Job job,
			Department department,
			UserDtls userDtls,
			int isActive
			){
		this.userdtlsid = userdtlsid;
		this.regdate = regdate;
		this.firstname = firstname;
		this.middlename = middlename;
		this.lastname = lastname;
		this.address = address;
		this.contactno = contactno;
		this.age = age;
		this.gender = gender;
		this.login = login;
		this.job = job;
		this.department = department;
		this.userDtls = userDtls;
		this.isActive = isActive;
	}
	
	public static String userSQL(String tablename,UserDtls user){
		String sql= " AND "+ tablename +".isactive=" + user.getIsActive();
		if(user!=null){
			
			if(user.getUserdtlsid()!=null){
				sql += " AND "+ tablename +".userdtlsid=" + user.getUserdtlsid();
			}
			if(user.getRegdate()!=null){
				sql += " AND "+ tablename +".regdate like '%" + user.getRegdate()+"%'";
			}
			if(user.getFirstname()!=null){
				sql += " AND "+ tablename +".firstname like '%" + user.getFirstname()+"%'";
			}
			if(user.getMiddlename()!=null){
				sql += " AND "+ tablename +".middlename like '%" + user.getMiddlename()+"%'";
			}
			if(user.getLastname()!=null){
				sql += " AND "+ tablename +".lastname like '%" + user.getLastname()+"%'";
			}
			if(user.getAddress()!=null){
				sql += " AND "+ tablename +".address like '%" + user.getAddress()+"%'";
			}
			if(user.getContactno()!=null){
				sql += " AND "+ tablename +".contactno like'%" + user.getContactno()+"%'";
			}
			if(user.getAge()!=0){
				sql += " AND "+ tablename +".age=" + user.getAge();
			}
			if(user.getGender()!=0){
				sql += " AND "+ tablename +".gender=" + user.getGender();
			}
			
		}
		
		return sql;
	}
	
	/**
	 * 
	 * @param obj[UserDtls]
	 * @return list of users
	 */
	public static List<UserDtls> retrieve(Object ...obj){
		List<UserDtls> users = Collections.synchronizedList(new ArrayList<UserDtls>());
		
		String userTable = "usr";
		String loginTable = "login";
		String jobtitleTable = "job";
		String departmentTable = "dep";
		String sql = "SELECT * FROM userdtls "+userTable+", "
				+ "login "+ loginTable +", jobtitle "+ jobtitleTable +", "
						+ "department "+ departmentTable +" WHERE "+ userTable +".logid="+ 
				loginTable +".logid AND "+ userTable +".jobtitleid = "+ jobtitleTable +".jobtitleid AND "+ 
						userTable +".departmentid = "+ departmentTable +".departmentid ";
		
			for(int i=0; i<obj.length; i++){
				if(obj[i] instanceof UserDtls){
					sql += userSQL(userTable,(UserDtls)obj[i]);
				}
				if(obj[i] instanceof Login){
					sql += Login.loginSQL(loginTable,(Login)obj[i]);
				}
				if(obj[i] instanceof Job){
					sql += Job.jobSQL(jobtitleTable,(Job)obj[i]);
				}
				if(obj[i] instanceof Department){
					sql += Department.departmentSQL(departmentTable,(Department)obj[i]);
				}
			}
		
		System.out.println("SQL : " + sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
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
			
			Login in = new Login();
			try{in.setLogid(rs.getLong("logid"));}catch(NullPointerException e){}
			try{in.setUsername(rs.getString("username"));}catch(NullPointerException e){}
			try{in.setPassword(rs.getString("password"));}catch(NullPointerException e){}
			try{in.setLastlogin(rs.getString("lastlogin"));}catch(NullPointerException e){}
			try{in.setAccessLevel(UserAccessLevel.userAccessLevel(rs.getString("useraccesslevelid")));}catch(NullPointerException e){}
			try{in.setIsOnline(rs.getInt("isOnline"));}catch(NullPointerException e){}
			try{in.setLogintime(rs.getString("logintime"));}catch(NullPointerException e){}
			try{in.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{in.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{in.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			in.setUserDtls(UserDtls.addedby(rs.getString("userdtlsid")));
			try{user.setLogin(in);}catch(NullPointerException e){}
			
			Job job = new Job();
			try{job.setJobid(rs.getInt("jobtitleid"));}catch(NullPointerException e){}
			try{job.setJobname(rs.getString("jobname"));}catch(NullPointerException e){}
			try{user.setJob(job);}catch(NullPointerException e){}
			
			Department dep = new Department();
			try{dep.setDepid(rs.getInt("departmentid"));}catch(NullPointerException e){}
			try{dep.setDepartmentName(rs.getString("departmentname"));}catch(NullPointerException e){}
			try{user.setDepartment(dep);}catch(NullPointerException e){}
			
			try{user.setUserDtls(UserDtls.addedby(rs.getString("addedby")));}catch(NullPointerException e){}
			
			
			users.add(user);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return users;
	}
	
	public static List<UserDtls> retrieve(String sql, String[] params){
		List<UserDtls> users = Collections.synchronizedList(new ArrayList<UserDtls>());
		
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
			try{user.setLogin(Login.login(rs.getString("logid")));}catch(NullPointerException e){}
			try{user.setJob(Job.job(rs.getString("jobtitleid")));}catch(NullPointerException e){}
			try{user.setDepartment(Department.department(rs.getString("departmentid")));}catch(NullPointerException e){}
			try{user.setUserDtls(UserDtls.addedby(rs.getString("addedby")));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			users.add(user);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return users;
	}
	
	public static UserDtls addedby(String userdtlsid){
		UserDtls user = new UserDtls();
		String sql = "SELECT * FROM userdtls WHERE userdtlsid=?";
		String[] params = new String[1];
		params[0] = userdtlsid;
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
			}
			
			
		}catch(Exception e){}
		return user;
	}
	
	@Deprecated
	public static UserDtls user(String userdtlsid){
		UserDtls user = new UserDtls();
		String sql = "SELECT * FROM userdtls WHERE userdtlsid=?";
		String[] params = new String[1];
		params[0] = userdtlsid;
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
				try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
				try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
				try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
				try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
				try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
				try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
				try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
				try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
				try{user.setLogin(Login.login(rs.getString("logid")));}catch(NullPointerException e){}
				try{user.setJob(Job.job(rs.getString("jobtitleid")));}catch(NullPointerException e){}
				try{user.setDepartment(Department.department(rs.getString("departmentid")));}catch(NullPointerException e){}
				try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
				try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			}
			
			
		}catch(Exception e){}
		return user;
	}
	
	public static void save(UserDtls user){
		if(user!=null){
			
			long id = UserDtls.getInfo(user.getUserdtlsid() ==null? UserDtls.getLatestId()+1 : user.getUserdtlsid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				UserDtls.insertData(user, "1");
			}else if(id==2){
				LogU.add("update Data ");
				UserDtls.updateData(user);
			}else if(id==3){
				LogU.add("added new Data ");
				UserDtls.insertData(user, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = UserDtls.getInfo(getUserdtlsid() ==null? getLatestId()+1 : getUserdtlsid());
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
	
	public static UserDtls insertData(UserDtls user, String type){
		String sql = "INSERT INTO userdtls ("
				+ "userdtlsid,"
				+ "firstname,"
				+ "middlename,"
				+ "lastname,"
				+ "address,"
				+ "contactno,"
				+ "age,"
				+ "gender,"
				+ "logid,"
				+ "jobtitleid,"
				+ "departmentid,"
				+ "addedby,"
				+ "isactive,"
				+ "regdate)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table userdtls");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			user.setUserdtlsid(Long.valueOf(id));
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			user.setUserdtlsid(Long.valueOf(id));
			LogU.add("id: " + id);
		}
		
		ps.setString(2, user.getFirstname());
		ps.setString(3, user.getMiddlename());
		ps.setString(4, user.getLastname());
		ps.setString(5, user.getAddress());
		ps.setString(6, user.getContactno());
		ps.setInt(7, user.getAge());
		ps.setInt(8, user.getGender());
		ps.setLong(9, user.getLogin()==null? 0l : (user.getLogin().getLogid()==null? 0l : user.getLogin().getLogid()));
		ps.setInt(10, user.getJob()==null? 0 : (user.getJob().getJobid()==0? 0 : user.getJob().getJobid()));
		ps.setInt(11, user.getDepartment()==null? 0 : (user.getDepartment().getDepid()==0? 0 : user.getDepartment().getDepid()));
		ps.setLong(12, user.getUserDtls()==null? 0l : (user.getUserDtls().getUserdtlsid()==null? 0l : user.getUserDtls().getUserdtlsid()));
		ps.setInt(13, user.getIsActive());
		ps.setString(14, user.getRegdate());
		
		LogU.add( user.getFirstname());
		LogU.add( user.getMiddlename());
		LogU.add(user.getLastname());
		LogU.add(user.getAddress());
		LogU.add(user.getContactno());
		LogU.add(user.getAge());
		LogU.add(user.getGender());
		LogU.add(user.getLogin()==null? 0l : (user.getLogin().getLogid()==null? 0l : user.getLogin().getLogid()));
		LogU.add(user.getJob()==null? 0 : (user.getJob().getJobid()==0? 0 : user.getJob().getJobid()));
		LogU.add(user.getDepartment()==null? 0 : (user.getDepartment().getDepid()==0? 0 : user.getDepartment().getDepid()));
		LogU.add(user.getUserDtls()==null? 0l : (user.getUserDtls().getUserdtlsid()==null? 0l : user.getUserDtls().getUserdtlsid()));
		LogU.add(user.getIsActive());
		LogU.add(user.getRegdate());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to userdtls : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return user;
	}

	public void insertData(String type){
		String sql = "INSERT INTO userdtls ("
				+ "userdtlsid,"
				+ "firstname,"
				+ "middlename,"
				+ "lastname,"
				+ "address,"
				+ "contactno,"
				+ "age,"
				+ "gender,"
				+ "logid,"
				+ "jobtitleid,"
				+ "departmentid,"
				+ "addedby,"
				+ "isactive,"
				+ "regdate)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table userdtls");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setUserdtlsid(Long.valueOf(id));
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setUserdtlsid(Long.valueOf(id));
			LogU.add("id: " + id);
		}
		
		ps.setString(2, getFirstname());
		ps.setString(3, getMiddlename());
		ps.setString(4, getLastname());
		ps.setString(5, getAddress());
		ps.setString(6, getContactno());
		ps.setInt(7, getAge());
		ps.setInt(8, getGender());
		ps.setLong(9, getLogin()==null? 0l : (getLogin().getLogid()==null? 0l : getLogin().getLogid()));
		ps.setInt(10, getJob()==null? 0 : (getJob().getJobid()==0? 0 : getJob().getJobid()));
		ps.setInt(11, getDepartment()==null? 0 : (getDepartment().getDepid()==0? 0 : getDepartment().getDepid()));
		ps.setLong(12, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setInt(13, getIsActive());
		ps.setString(14, getRegdate());
		
		LogU.add( getFirstname());
		LogU.add( getMiddlename());
		LogU.add(getLastname());
		LogU.add(getAddress());
		LogU.add(getContactno());
		LogU.add(getAge());
		LogU.add(getGender());
		LogU.add(getLogin()==null? 0l : (getLogin().getLogid()==null? 0l : getLogin().getLogid()));
		LogU.add(getJob()==null? 0 : (getJob().getJobid()==0? 0 : getJob().getJobid()));
		LogU.add(getDepartment()==null? 0 : (getDepartment().getDepid()==0? 0 : getDepartment().getDepid()));
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getIsActive());
		LogU.add(getRegdate());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to userdtls : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static UserDtls updateData(UserDtls user){
		String sql = "UPDATE userdtls SET "
				+ "firstname=?,"
				+ "middlename=?,"
				+ "lastname=?,"
				+ "address=?,"
				+ "contactno=?,"
				+ "age=?,"
				+ "gender=?,"
				+ "logid=?,"
				+ "jobtitleid=?,"
				+ "departmentid=?,"
				+ "addedby=?,"
				+ "isactive=?" 
				+ " WHERE userdtlsid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("update data into table userdtls");
		
		ps.setString(1, user.getFirstname());
		ps.setString(2, user.getMiddlename());
		ps.setString(3, user.getLastname());
		ps.setString(4, user.getAddress());
		ps.setString(5, user.getContactno());
		ps.setInt(6, user.getAge());
		ps.setInt(7, user.getGender());
		ps.setLong(8, user.getLogin()==null? 0l : (user.getLogin().getLogid()==null? 0l : user.getLogin().getLogid()));
		ps.setInt(9, user.getJob()==null? 0 : (user.getJob().getJobid()==0? 0 : user.getJob().getJobid()));
		ps.setInt(10, user.getDepartment()==null? 0 : (user.getDepartment().getDepid()==0? 0 : user.getDepartment().getDepid()));
		ps.setLong(11, user.getUserDtls()==null? 0l : (user.getUserDtls().getUserdtlsid()==null? 0l : user.getUserDtls().getUserdtlsid()));
		ps.setInt(12, user.getIsActive());
		ps.setLong(13, user.getUserdtlsid());
		
		LogU.add(user.getFirstname());
		LogU.add(user.getMiddlename());
		LogU.add(user.getLastname());
		LogU.add(user.getAddress());
		LogU.add(user.getContactno());
		LogU.add(user.getAge());
		LogU.add(user.getGender());
		LogU.add(user.getLogin()==null? 0l : (user.getLogin().getLogid()==null? 0l : user.getLogin().getLogid()));
		LogU.add(user.getJob()==null? 0 : (user.getJob().getJobid()==0? 0 : user.getJob().getJobid()));
		LogU.add(user.getDepartment()==null? 0 : (user.getDepartment().getDepid()==0? 0 : user.getDepartment().getDepid()));
		LogU.add(user.getUserDtls()==null? 0l : (user.getUserDtls().getUserdtlsid()==null? 0l : user.getUserDtls().getUserdtlsid()));
		LogU.add(user.getIsActive());
		LogU.add(user.getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to userdtls : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return user;
	}
	
	public void updateData(){
		String sql = "UPDATE userdtls SET "
				+ "firstname=?,"
				+ "middlename=?,"
				+ "lastname=?,"
				+ "address=?,"
				+ "contactno=?,"
				+ "age=?,"
				+ "gender=?,"
				+ "logid=?,"
				+ "jobtitleid=?,"
				+ "departmentid=?,"
				+ "addedby=?,"
				+ "isactive=?" 
				+ " WHERE userdtlsid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("update data into table userdtls");
		
		ps.setString(1, getFirstname());
		ps.setString(2, getMiddlename());
		ps.setString(3, getLastname());
		ps.setString(4, getAddress());
		ps.setString(5, getContactno());
		ps.setInt(6, getAge());
		ps.setInt(7, getGender());
		ps.setLong(8, getLogin()==null? 0l : (getLogin().getLogid()==null? 0l : getLogin().getLogid()));
		ps.setInt(9, getJob()==null? 0 : (getJob().getJobid()==0? 0 : getJob().getJobid()));
		ps.setInt(10, getDepartment()==null? 0 : (getDepartment().getDepid()==0? 0 : getDepartment().getDepid()));
		ps.setLong(11, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		ps.setInt(12, getIsActive());
		ps.setLong(13, getUserdtlsid());
		
		LogU.add(getFirstname());
		LogU.add(getMiddlename());
		LogU.add(getLastname());
		LogU.add(getAddress());
		LogU.add(getContactno());
		LogU.add(getAge());
		LogU.add(getGender());
		LogU.add(getLogin()==null? 0l : (getLogin().getLogid()==null? 0l : getLogin().getLogid()));
		LogU.add(getJob()==null? 0 : (getJob().getJobid()==0? 0 : getJob().getJobid()));
		LogU.add(getDepartment()==null? 0 : (getDepartment().getDepid()==0? 0 : getDepartment().getDepid()));
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==null? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getIsActive());
		LogU.add(getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to userdtls : " + s.getMessage());
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
		sql="SELECT userdtlsid FROM userdtls  ORDER BY userdtlsid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("userdtlsid");
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
		ps = conn.prepareStatement("SELECT userdtlsid FROM userdtls WHERE userdtlsid=?");
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
		String sql = "UPDATE userdtls set isactive=0 WHERE userdtlsid=?";
		
		if(!retain){
			sql = "DELETE FROM userdtls WHERE userdtlsid=?";
		}
		
		String[] params = new String[1];
		params[0] = getUserdtlsid()+"";
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
	
	public Long getUserdtlsid() {
		return userdtlsid;
	}
	public void setUserdtlsid(Long userdtlsid) {
		this.userdtlsid = userdtlsid;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContactno() {
		return contactno;
	}
	public void setContactno(String contactno) {
		this.contactno = contactno;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public Login getLogin() {
		return login;
	}
	public void setLogin(Login login) {
		this.login = login;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
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

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public static void main(String[] args) {
		
		UserDtls u = new UserDtls();
		//u.setUserdtlsid(1l);
		u.setIsActive(1);
		Department dep = new Department();
		//dep.setDepartmentName("Accounting");
		for(UserDtls us : u.retrieve(u,dep)){
			System.out.println(us.getFirstname() + " Job " +
		us.getJob().getJobname() + " Department " +
		us.getDepartment().getDepartmentName() + " Access Level " + us.getLogin().getAccessLevel().getName() + 
		" added by " + us.getUserDtls().getFirstname());
		}
		
		/*UserDtls u = new UserDtls();
		u.setUserdtlsid(1L);
		u.setFirstname("Mark");
		u.setMiddlename("Rivera");
		u.setLastname("Italia");
		u.setIsActive(1);
		u.setAddress("lake sebu");
		u.setContactno("1234567890");
		u.setAge(30);
		u.setGender(1);
		u.setLogin(Login.login(1));
		u.setJob(Job.job(1));
		u.setDepartment(Department.department(1));
		u.setUserDtls(UserDtls.user(1));
		u.save(u);*/
		//System.out.println(user(1).getFirstname());
		
		
	}
}
