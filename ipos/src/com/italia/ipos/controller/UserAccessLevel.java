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
public class UserAccessLevel {
	
	private int useraccesslevelid;
	private int level;
	private String name;
	private Timestamp timestamp;
	
	public UserAccessLevel(){}
	
	public UserAccessLevel(
			int useraccesslevelid,
			int level,
			String name
			){
		this.useraccesslevelid = useraccesslevelid;
		this.level = level;
		this.name = name;
	}
	
	public static String accessLevelSQL(String tablename,UserAccessLevel lvl){
		String sql="";
		if(lvl!=null){
			
			if(lvl.getUseraccesslevelid()!=0){
				sql += " AND "+ tablename +".useraccesslevelid=" + lvl.getUseraccesslevelid();
			}
			if(lvl.getLevel()!=0){
				sql += " AND "+ tablename +".level=" + lvl.getLevel();
			}
			if(lvl.getName()!=null){
				sql += " AND "+ tablename +".levelname='" + lvl.getName() +"'";
			}
			
			
		}
		
		return sql;
	}
	
	public static List<UserAccessLevel> retrieve(String sql, String[] params){
		List<UserAccessLevel> levels = Collections.synchronizedList(new ArrayList<UserAccessLevel>());
			
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
			UserAccessLevel level = new UserAccessLevel();
			try{level.setUseraccesslevelid(rs.getInt("useraccesslevelid"));}catch(NullPointerException e){}
			try{level.setLevel(rs.getInt("level"));}catch(NullPointerException e){}
			try{level.setName(rs.getString("levelname"));}catch(NullPointerException e){}
			levels.add(level);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return levels;
	}
	
	public static UserAccessLevel userAccessLevel(String userAccessLevelId){
		UserAccessLevel userAccessLevel = new UserAccessLevel();
		String sql = "SELECT * FROM useraccesslevel WHERE useraccesslevelid=?";
		String[] params = new String[1];
		params[0] = userAccessLevelId;
		try{
			userAccessLevel = UserAccessLevel.retrieve(sql, params).get(0);
		}catch(Exception e){}
		
		
		return userAccessLevel;
	}
	
	public int getUseraccesslevelid() {
		return useraccesslevelid;
	}
	public void setUseraccesslevelid(int useraccesslevelid) {
		this.useraccesslevelid = useraccesslevelid;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	} 
	
}
