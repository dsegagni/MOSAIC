package it.fsm.mosaic.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class DBUtil {
	private final static String DATASOURCE_CONTEXT = "java:comp/env/jdbc/mosaici2b2"; 

	
	
	static public Connection getI2B2Connection(){
		return getConnection(DATASOURCE_CONTEXT);
	}
	
	private static Connection getConnection(String context){
		Context initialContext;
		Connection con = null;
		try {
			initialContext = new InitialContext();
			DataSource datasource = (DataSource)initialContext.lookup(context);
			con = datasource.getConnection();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return con;
	}
	
	static public void closeI2B2Connection(Connection con){
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
