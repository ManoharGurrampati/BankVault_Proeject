package com.bank.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	private static final String CONFIG_FILE = "db.properties";
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			throw new RuntimeException("MySQL JDBC Driver not found", e);
		}
	}
	
	public static Connection getConnection() throws SQLException{
		try{
			Properties p = loadProperties();
			
			String URL = p.getProperty("db.url");
			
			String userName = p.getProperty("db.username");
			
			String passWord = p.getProperty("db.password");
			
			return  DriverManager.getConnection(URL,userName,passWord);
			
		}catch(IOException e) {
			throw new SQLException("Failed to load database configuration", e);
		}
	}
	
	private static Properties loadProperties() throws IOException{
		
		Properties properties = new Properties();
		
		try(InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)){
			
			properties.load(input);
			
			return properties;
		}
		
		
	}
	
}
