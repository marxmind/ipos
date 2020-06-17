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
 * @since 02/13/2017
 * @version 1.0
 *
 */
public class Features {

	private int id;
	private String moduleName;
	private int isActive;
	private String monthExpiration;
	private String activationCode;
	private Timestamp timestamp;
	private double vat;
	
	private boolean checked;
	
	public Features(){}
	
	public Features(
			int id,
			String moduleName,
			int isActive,
			String monthExpiration,
			String activationCode
			){
		this.id = id;
		this.moduleName = moduleName;
		this.isActive = isActive;
		this.monthExpiration = monthExpiration;
		this.activationCode = activationCode;
	}
	
	public static List<Features> retrieve(String sql, String[] params){
		List<Features> fets = new ArrayList<Features>();
		
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
			Features fet = new Features();
			fet.setId(rs.getInt("fid"));
			fet.setModuleName(rs.getString("modulename"));
			fet.setIsActive(rs.getInt("isActive"));
			fet.setMonthExpiration(rs.getString("monthexp"));
			fet.setActivationCode(rs.getString("activationcode"));
			try{fet.setVat(rs.getDouble("vat"));}catch(Exception e){}
			fets.add(fet);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return fets;
	}

	public static boolean isEnabled(com.italia.ipos.enm.Features fets){
		//Features fet = new Features();
		
		String sql = "SELECT * FROM features WHERE modulename=? AND isActive=1";
		String[] params = new String[1];
		params[0] = fets.getName();
		
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
			
			return true;
			/*fet.setId(rs.getInt("fid"));
			fet.setModuleName(rs.getString("modulename"));
			fet.setIsActive(rs.getInt("isActive"));
			fet.setMonthExpiration(rs.getString("monthexp"));
			fet.setActivationCode(rs.getString("activationcode"));*/
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static void save(com.italia.ipos.enm.Features feat, boolean isEnabled){
		String sql ="";
		if(isEnabled){
			sql = "UPDATE features set isActive=1 WHERE modulename='" + feat.getName() +"'";
		}else{
			sql = "UPDATE features set isActive=0 WHERE modulename='" + feat.getName() +"'";
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try{
			
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			conn.close();
			
		}catch(Exception e){}
		
	}
	
	public static void saveData(Features features, boolean isEnabled){
		String sql ="";
		if(isEnabled){
			sql = "UPDATE features set isActive=1 WHERE modulename='" + features.getModuleName() +"'";
		}else{
			sql = "UPDATE features set isActive=0 WHERE modulename='" + features.getModuleName() +"'";
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try{
			
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			conn.close();
			
		}catch(Exception e){}
		
	}
	
	public static void saveVAT(Features features,double value){
		String sql ="";
		
			sql = "UPDATE features set vat="+value+" WHERE modulename='" + features.getModuleName() +"'";
		
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try{
			
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			System.out.println("Update vat " + ps.toString());
			ps.executeUpdate();
			ps.close();
			conn.close();
			
		}catch(Exception e){}
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
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

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public double getVat() {
		return vat;
	}

	public void setVat(double vat) {
		this.vat = vat;
	}
	
}
