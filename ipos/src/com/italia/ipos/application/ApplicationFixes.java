package com.italia.ipos.application;

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
 * @since 09/28/2016
 * @version 1.0
 */
public class ApplicationFixes {

	private Long fixid;
	private String fixdesc;
	private ApplicationVersionController versionController;
	private Timestamp timestamp;
	
	public ApplicationFixes(){}
	
	public ApplicationFixes(
			Long fixid,
			String fixdesc,
			ApplicationVersionController versionController
			){
		this.fixid = fixid;
		this.fixdesc = fixdesc;
		this.versionController = versionController;
	}
	
	public static List<ApplicationFixes> retrieve(String sql, String[] params){
		List<ApplicationFixes> data = Collections.synchronizedList(new ArrayList<>());
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
			ApplicationFixes app = new ApplicationFixes();
			try{app.setFixid(rs.getLong("fixid"));}catch(NullPointerException e){}
			try{app.setFixdesc(rs.getString("fixdesc"));}catch(NullPointerException e){}
			try{
				ApplicationVersionController versionController = new ApplicationVersionController();
				versionController.setBuildid(rs.getLong("buildid"));
				app.setVersionController(versionController);}catch(NullPointerException e){}
			try{app.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			data.add(app);
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		return data;
	}
	
	public Long getFixid() {
		return fixid;
	}
	public void setFixid(Long fixid) {
		this.fixid = fixid;
	}
	public String getFixdesc() {
		return fixdesc;
	}
	public void setFixdesc(String fixdesc) {
		this.fixdesc = fixdesc;
	}
	public ApplicationVersionController getVersionController() {
		return versionController;
	}
	public void setVersionController(ApplicationVersionController versionController) {
		this.versionController = versionController;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
