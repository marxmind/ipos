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
 * @author mark
 * @since 09/28/2016
 * @version 1.0
 *
 */
public class ApplicationVersionController {

	private Long buildid;
	private String buildversion;
	private String buildapplieddate;
	private Timestamp timestamp;
	
	public ApplicationVersionController(){}
	
	public ApplicationVersionController(
			Long buildid,
			String buildversion,
			String buildapplieddate
			){
		this.buildid = buildid;
		this.buildversion = buildversion;
		this.buildapplieddate = buildapplieddate;
	}
	
	public static List<ApplicationVersionController> retrieve(String sql, String[] params){
		List<ApplicationVersionController> data = Collections.synchronizedList(new ArrayList<>());
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
		
		System.out.println("SQL build " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			ApplicationVersionController app = new ApplicationVersionController();
			try{app.setBuildid(rs.getLong("buildid"));}catch(NullPointerException e){}
			try{app.setBuildversion(rs.getString("buildversion"));}catch(NullPointerException e){}
			try{app.setBuildapplieddate(rs.getString("buildapplieddate"));}catch(NullPointerException e){}
			try{app.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			data.add(app);
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		return data;
	}
	
	public Long getBuildid() {
		return buildid;
	}
	public void setBuildid(Long buildid) {
		this.buildid = buildid;
	}
	public String getBuildversion() {
		return buildversion;
	}
	public void setBuildversion(String buildversion) {
		this.buildversion = buildversion;
	}
	public String getBuildapplieddate() {
		return buildapplieddate;
	}
	public void setBuildapplieddate(String buildapplieddate) {
		this.buildapplieddate = buildapplieddate;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
