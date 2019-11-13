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
public class Job {

	private int jobid;
	private String jobname;
	private Timestamp timestamp;
	
	public Job(){}
	
	public Job(
			int jobid,
			String jobname
			){
		this.jobid = jobid;
		this.jobname = jobname;
	}
	
	public static String jobSQL(String tablename,Job job){
		String sql="";
		if(job!=null){
			
			if(job.getJobid()!=0){
				sql += " AND "+ tablename +".jobtitleid=" + job.getJobid();
			}
			if(job.getJobname()!=null){
				sql += " AND "+ tablename +".jobname='" + job.getJobname()+"'";
			}
			
		}
		
		return sql;
	}
	
	public static List<Job> retrieve(String sql, String[] params){
		List<Job> jobs = Collections.synchronizedList(new ArrayList<Job>());
		
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
			Job job = new Job();
			try{job.setJobid(rs.getInt("jobtitleid"));}catch(NullPointerException e){}
			try{job.setJobname(rs.getString("jobname"));}catch(NullPointerException e){}
			jobs.add(job);
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return jobs;
	}
	
	public static Job job(String jobid){
		Job job = new Job();
		String sql = "SELECT * FROM jobtitle WHERE jobtitleid=?";
		String[] params = new String[1];
		params[0] = jobid;
		try{
			job = Job.retrieve(sql, params).get(0);
		}catch(Exception e){}
		return job;
	}
	
	public int getJobid() {
		return jobid;
	}
	public void setJobid(int jobid) {
		this.jobid = jobid;
	}
	public String getJobname() {
		return jobname;
	}
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
