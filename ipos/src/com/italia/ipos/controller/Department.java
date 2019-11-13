package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.ipos.database.ConnectDB;
/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */
public class Department {

	private int depid;
	private String departmentName;
	private UserDtls userDtls;
	private Business company;
	private Timestamp timestamp;
	
	public Department(){}
	
	public Department(
			int depid,
			String departmentName,
			UserDtls userDtls,
			Business company
			){
		this.depid = depid;
		this.departmentName = departmentName;
		this.userDtls = userDtls;
		this.company = company;
	}
	
	public static String departmentSQL(String tablename,Department dep){
		String sql="";
		if(dep!=null){
			
			if(dep.getDepid()!=0){
				sql += " AND "+ tablename +".departmentid=" + dep.getDepid();
			}
			if(dep.getDepartmentName()!=null){
				sql += " AND "+ tablename +".departmentname='" + dep.getDepartmentName()+"'";
			}
			
		}
		
		return sql;
	}
	
	public static List<Department> retrieve(String sql, String[] params){
		List<Department> deps = Collections.synchronizedList(new ArrayList<Department>());
		
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
			Department dep = new Department();
			try{dep.setDepid(rs.getInt("departmentid"));}catch(NullPointerException e){}
			try{dep.setDepartmentName(rs.getString("departmentname"));}catch(NullPointerException e){}
			deps.add(dep);
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return deps;
	}
	
	public static Department department(String departmentid){
		Department dep = new Department();
		String sql = "SELECT * FROM department WHERE departmentid=?";
		String[] params = new String[1];
		params[0] = departmentid;
		try{
			dep = Department.retrieve(sql, params).get(0);
		}catch(Exception e){}
		return dep;
	}
	
	public int getDepid() {
		return depid;
	}
	public void setDepid(int depid) {
		this.depid = depid;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Business getCompany() {
		return company;
	}
	public void setCompany(Business company) {
		this.company = company;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
