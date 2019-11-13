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
 * @since created 10/05/2016
 * @version 1.0
 *
 */
public class IposType {
	
	private int iposid;
	private String iposcode;
	private String ipostype;
	private int isactive;
	private int bizid;
	private Timestamp timestamp;
	
	
	public IposType(){}
	
	public IposType(
			int iposid,
			String iposcode,
			String ipostype,
			int isactive,
			int bizid
			){
		this.iposid = iposid;
		this.iposcode = iposcode;
		this.ipostype = ipostype;
		this.isactive = isactive;
		this.bizid = bizid;
	}
	
	public static String iposTypeSQL(String tablename,IposType ipos){
		String sql = tablename +".isactive=" + ipos.getIsactive();
		if(ipos!=null){
			if(ipos.getIposid()!=0){
				sql += " AND "+ tablename +".iposid=" + ipos.getIposid();
			}
			if(ipos.getIposcode()!=null){
				sql += " AND "+ tablename +".iposcode like '%" + ipos.getIposcode() +"%'";
			}
			if(ipos.getIpostype()!=null){
				sql += " AND "+ tablename +".ipostype like '%" + ipos.getIpostype() +"%'";
			}
			
			
			
			
		}
		return sql;
	}
	
	public static List<IposType> retrieve(Object... obj){
		List<IposType> ipos = Collections.synchronizedList(new ArrayList<IposType>());
		String iposTable = "ipos";
		String sql = "SELECT * FROM ipostype "+ iposTable +" WHERE ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof IposType){
				sql += iposTypeSQL(iposTable,(IposType)obj[i]);
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
			IposType ipo = new IposType();
			try{ipo.setIposid(rs.getInt("iposid"));}catch(NullPointerException e){}
			try{ipo.setIposcode(rs.getString("iposcode"));}catch(NullPointerException e){}
			try{ipo.setIpostype(rs.getString("ipostype"));}catch(NullPointerException e){}
			try{ipo.setIsactive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{ipo.setBizid(rs.getInt("bizid"));}catch(NullPointerException e){}
			try{ipo.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			ipos.add(ipo);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return ipos;
	}
	
	
	public int getIposid() {
		return iposid;
	}
	public void setIposid(int iposid) {
		this.iposid = iposid;
	}
	public String getIposcode() {
		return iposcode;
	}
	public void setIposcode(String iposcode) {
		this.iposcode = iposcode;
	}
	public String getIpostype() {
		return ipostype;
	}
	public void setIpostype(String ipostype) {
		this.ipostype = ipostype;
	}
	public int getIsactive() {
		return isactive;
	}
	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	public int getBizid() {
		return bizid;
	}
	public void setBizid(int bizid) {
		this.bizid = bizid;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public static void main(String[] args) {
		
		IposType ipos = new IposType();
		ipos.setIsactive(1);
		for(IposType i : IposType.retrieve(ipos)){
			System.out.println(i.getIposcode() + " " + i.getIpostype());
		}
		
	}
	
}
