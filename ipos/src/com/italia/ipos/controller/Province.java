package com.italia.ipos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.italia.ipos.database.ConnectDB;

/**
 * 
 * @author mark italia
 * @since 04/09/2017
 * @version 1.0
 *
 */
public class Province {

	private int id;
	private String name;
	private int isActive;
	
	public Province(){}
	public Province(
			int id,
			String name,
			int isActive
			){
		this.id = id;
		this.name = name;
		this.isActive = isActive;
	}
	
	public static String provinceSQL(String tablename,Province bg){
		String sql= " AND "+ tablename +".provisactive=" + bg.getIsActive();
		if(bg!=null){
			
			sql += " AND "+ tablename +".provid=" + bg.getId();
			
			if(bg.getName()!=null){
				
				sql += " AND "+ tablename +".provname like '%"+ bg.getName() +"%'";
				
			}
			
		}
		
		return sql;
	}	
	
	public static List<Province> retrieve(String sql, String[] params){
		List<Province> bars = new ArrayList<>();
		
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
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			bars.add(prov);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return bars;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	
}


