package com.italia.ipos.security;

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
 * @since 01/24/2017
 * @version 1.0
 *
 */
public class ActivationCode {

	private long id;
	private String moduleName;
	private String codeName;
	private String monthExpiration;
	private String activationCode;
	private Timestamp timestamp;
	
	public static List<ActivationCode> retrieve(String sql, String params[]){
		List<ActivationCode> lis = Collections.synchronizedList(new ArrayList<ActivationCode>());
		
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
		
		System.out.println("SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			ActivationCode li = new ActivationCode();
			li.setId(rs.getLong("cid"));
			li.setModuleName(rs.getString("modulename"));
			li.setCodeName(rs.getString("codename"));
			li.setMonthExpiration(rs.getString("monthexp"));
			li.setActivationCode(rs.getString("activationcode"));
			li.setTimestamp(rs.getTimestamp("timestamp"));
			lis.add(li);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return lis;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public String getMonthExpiration() {
		return monthExpiration;
	}
	public void setMonthExpiration(String monthExpiration) {
		this.monthExpiration = monthExpiration;
	}
	public String getActivationCode() {
		return activationCode;
	}
	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
