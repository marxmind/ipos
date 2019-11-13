package com.italia.ipos.database;

import com.italia.ipos.enm.Ipos;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.security.SecureChar;
import java.sql.Connection;
import java.sql.DriverManager;
/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */
public class ConnectDB {

	public static Connection getConnection(){
		Connection conn = null;
		
		try{
			String driver = ReadConfig.value(Ipos.DB_DRIVER);
				   driver = SecureChar.decode(driver);
			Class.forName(driver);
			String db_url = ReadConfig.value(Ipos.DB_URL);
				   db_url = SecureChar.decode(db_url);
			String port = ReadConfig.value(Ipos.DB_PORT);
			       port = SecureChar.decode(port);
			String url = db_url + ":" + port + "/" +ReadConfig.value(Ipos.DB_NAME)+ "?" + ReadConfig.value(Ipos.DB_SSL);
			String u_name = ReadConfig.value(Ipos.USER_NAME);
				   u_name = SecureChar.decode(u_name);
				   u_name = u_name.replaceAll("mark", "");
				   u_name = u_name.replaceAll("rivera", "");
				   u_name = u_name.replaceAll("italia", "");
			String pword = ReadConfig.value(Ipos.USER_PASS);
				   pword =  SecureChar.decode(pword);
				   pword = pword.replaceAll("mark", "");
				   pword = pword.replaceAll("rivera", "");
				   pword = pword.replaceAll("italia", "");
			conn = DriverManager.getConnection(url, u_name, pword);
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void close(Connection conn){
		try{
			if(conn!=null){
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Connection c = getConnection();
		System.out.println("Successfully connected" + c.toString());
	}
	
}
